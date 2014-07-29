package biz.paluch.logging.gelf.jul;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * @author <a href="mailto:mark.paluch@1und1.de">Mark Paluch</a>
 * @since 17.07.14 12:12
 */
public class DummyFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        return false;
    }
}
