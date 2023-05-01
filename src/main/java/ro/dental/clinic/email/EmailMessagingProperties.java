package ro.dental.clinic.email;


import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * Contains the parameters of the thread pool used by the email sending mechanism
 */
@ConfigurationProperties(prefix = "ro.dental.clinic.email-sending")
public record EmailSendingProperties(
        ThreadPool threadPool) {

@ConstructorBinding
public EmailSendingProperties(ThreadPool threadPool) {
        this.threadPool = Optional.ofNullable(threadPool).orElseGet(ThreadPool::new);
        }

/**
 * Thread pool parameters
 */
public record ThreadPool(
        int corePoolSize,
        int maxPoolSize,
        int keepAliveTime,
        int maxWaitingThreads) {

@ConstructorBinding
public ThreadPool {
        }

public ThreadPool() {
        this(2, 10, 60, 100);
        }
        }
        }