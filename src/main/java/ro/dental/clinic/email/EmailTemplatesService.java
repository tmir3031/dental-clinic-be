package ro.dental.clinic.email;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Service used for email templates processing
 */
public class EmailTemplatesService {

    private final EmailTemplatingProperties emailTemplatingProperties;
    private final EmailTemplateEngine emailTemplateEngine;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public EmailTemplatesService(EmailTemplatingProperties emailTemplatingProperties) {
        this.emailTemplatingProperties = emailTemplatingProperties;
        this.emailTemplateEngine = new HandlebarsEmailTemplateEngine();
    }

    /**
     * Method used for processing the fields of a template
     *
     * @param templateId the template identifier
     * @param payload    the object maintaining the variable context to be merged
     *                   in the template identified by {@code templateId}
     * @return the processed result as an association between the name of the template field
     * and the processed text of the field
     */
    public Map<String, String> processTemplate(final String templateId, final Object payload) {
        var outcomeSections = Optional.ofNullable(this.emailTemplatingProperties.getTemplates())
                .map(templates -> templates.get(templateId))
                .map(EmailTemplate::outcomeSections)
                .orElseGet(() -> ImmutableList.<EmailSectionTemplate>builder().build());

        return getEmailSections(payload, outcomeSections);
    }

    private Map<String, String> getEmailSections(Object payload,
                                                 List<EmailSectionTemplate> outcomeSections) {
        final Map<String, String> processedSections = Maps.newHashMapWithExpectedSize(outcomeSections.size());
        for (EmailSectionTemplate outcomeSection : outcomeSections) {
            var fieldText = emailTemplateEngine.processTemplate(outcomeSection.template(), payload);
            processedSections.put(outcomeSection.sectionName(), fieldText);
        }
        return processedSections;
    }
}
