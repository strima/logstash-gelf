package biz.paluch.logging.gelf.intern;

/**
 * Configuration for a Gelf Sender.
 */
public interface GelfSenderConfiguration {

    /**
     * 
     * @return the host part (can be any arbitrary string which is supported by the GelfSender)
     */
    String getHost();

    /**
     * 
     * @return port number (optional, 0 if not provided)
     */
    int getPort();

    /**
     * 
     * @return password string (optional, null if not provided)
     */
    String getPassword();

    /**
     * 
     * @return the ErrorReporter to report any errors
     */
    ErrorReporter getErrorReporter();
}
