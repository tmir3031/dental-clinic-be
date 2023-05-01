package ro.dental.clinic.email;

import java.io.IOException;
import java.time.LocalDate;

import com.github.jknack.handlebars.EscapingStrategy;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.cache.GuavaTemplateCache;
import com.github.jknack.handlebars.helper.StringHelpers;
import com.github.jknack.handlebars.io.TemplateSource;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * Class representing the Handlebars template engine
 */
public class HandlebarsEmailTemplateEngine implements EmailTemplateEngine {

    private static final Cache<TemplateSource, Template> PRECOMPILED_TEMPLATES = CacheBuilder.newBuilder().build();

    private final Handlebars handlebars;

    /**
     * Constructs a new instance of Handlebars template engine with date formatting support
     */
    public HandlebarsEmailTemplateEngine() {
        handlebars = new Handlebars()
                .registerHelper("dateFormat", (context, options) -> {
                    var ctx = context;
                    if (ctx instanceof String) {
                        ctx = LocalDate.parse((String) context);
                    }
                    return StringHelpers.dateFormat.apply(ctx, options);
                })
                .with(new GuavaTemplateCache(PRECOMPILED_TEMPLATES))
                .with(EscapingStrategy.NOOP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String processTemplate(final String templateText, final Object payload) {
        Preconditions.checkArgument(templateText != null, "templateText could not be null");
        final String fieldText;
        try {
            var template = handlebars.compileInline(templateText);
            fieldText = template.apply(payload);
        } catch (IOException exception) {
            var errorMsg = String.format("Failed processing template %s", templateText);
            throw new IllegalStateException(errorMsg, exception);
        }
        return fieldText;
    }
}
