package ro.dental.clinic.email;

import java.util.concurrent.*;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration used to create email messaging related beans
 */
@Slf4j
@EnableAsync
@Configuration
@EnableConfigurationProperties(EmailSendingProperties.class)
public class EmailMessagingConfiguration {

    @Bean
    public EmailTemplatesService templatesService(EmailTemplatingProperties emailTemplatingProperties) {
        return new EmailTemplatesService(emailTemplatingProperties);
    }

    @Bean
    public EmailMessagingService emailMessagingService(EmailMessagingProperties emailMessagingProperties,
                                                       EmailTemplatesService emailTemplatesService,
                                                       JavaMailSender javaMailSender) {
        return new EmailMessagingService(emailMessagingProperties, emailTemplatesService, javaMailSender);
    }

    @Bean
    public ThreadFactory emailSendingThreadFactory() {
        return new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat("email-sending-%s")
                .setUncaughtExceptionHandler((t, e) -> log.error(e.toString()))
                .build();
    }

    @Bean
    public BlockingQueue<Runnable> emailSendingWorkQueue(EmailSendingProperties configProperties) {
        return new LinkedBlockingDeque<>(configProperties.threadPool().maxWaitingThreads());
    }

    @SuppressWarnings("PMD.DoNotUseThreads")
    @Bean
    public ExecutorService emailSendingExecutorService(EmailSendingProperties configProperties) {
        var threadPoolProps = configProperties.threadPool();
        return new ThreadPoolExecutor(
                threadPoolProps.corePoolSize(),
                threadPoolProps.maxPoolSize(),
                threadPoolProps.keepAliveTime(),
                TimeUnit.MINUTES,
                emailSendingWorkQueue(configProperties),
                emailSendingThreadFactory());
    }
}