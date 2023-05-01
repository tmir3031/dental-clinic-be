package ro.dental.clinic.email;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.context.properties.bind.ConstructorBinding;

import com.google.common.collect.ImmutableList;

/**
 * Class representing an email template
 */
public record EmailTemplate(ImmutableList<EmailSectionTemplate> outcomeSections) {

@ConstructorBinding
public EmailTemplate(List<EmailSectionTemplate> outcomeSections) {
        this(Optional.ofNullable(outcomeSections).map(ImmutableList::copyOf).orElse(null));
        }
        }
