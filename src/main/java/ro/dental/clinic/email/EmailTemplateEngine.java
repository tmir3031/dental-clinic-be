package ro.dental.clinic.email;

/**
 * Engine used for email template processing
 */
public interface EmailTemplateEngine {

    /**
     * Implementation of the current engine processor
     *
     * @param templateText the content of the template to be processed
     * @param payload      the object maintaining the variable context to be merged
     *                     in the current template
     * @return the processed template
     * @throws IllegalArgumentException if {@code templateText} is null
     * @throws IllegalStateException    if template processing fails
     */
    String processTemplate(String templateText, Object payload);
}
