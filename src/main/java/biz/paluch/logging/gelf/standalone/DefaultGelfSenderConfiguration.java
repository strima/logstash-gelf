package biz.paluch.logging.gelf.standalone;

import biz.paluch.logging.gelf.intern.ErrorReporter;
import biz.paluch.logging.gelf.intern.GelfSenderConfiguration;

/**
 * Default Gelf sender configuration for standalone use.
 * 
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 21.07.14 17:34
 */
public class DefaultGelfSenderConfiguration implements GelfSenderConfiguration {

    private ErrorReporter errorReporter;
    private String host;
    private int port;
    private String password;

    public DefaultGelfSenderConfiguration() {
        errorReporter = new Slf4jErrorReporter();
    }

    public DefaultGelfSenderConfiguration(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public ErrorReporter getErrorReporter() {
        return errorReporter;
    }

    public void setErrorReporter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
