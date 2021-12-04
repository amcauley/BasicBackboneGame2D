package basicbackbonegame2d;

import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.LogRecord;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.SimpleFormatter;

public class Log {
    static Logger logger;
    static Handler handler;

    // Something bad happened. Basic functionality is impacted.
    static final int ERROR = 0;
    // Something bad happened, but we may be able to recover without much impact.
    static final int WARNING = 1;
    // Something happened that isn't a common occurrence. This could be in response
    // to some event or class initialization.
    static final int INFO = 2;
    // Getting into fine details and recurring events.
    static final int DEBUG = 3;
    // Very fine details. These are probably only useful for debugging obscure
    // issues.
    static final int TRACE = 4;

    // Convert the internal levels into something the logger library understands.
    static Level toLibraryLevel(int level) {
        switch (level) {
            case ERROR:
                return Level.SEVERE;
            case WARNING:
                return Level.WARNING;
            case INFO:
                return Level.INFO;
            case DEBUG:
                return Level.FINE;
            case TRACE:
                return Level.FINER;
        }
        // Shouldn't reach this part.
        return Level.OFF;
    }

    static String toInternalName(Level level) {
        if (level == Level.SEVERE) {
            return "ERROR";
        } else if (level == Level.WARNING) {
            return "WARN";
        } else if (level == Level.INFO) {
            return "INFO";
        } else if (level == Level.FINE) {
            return "DEBUG";
        } else if (level == Level.FINER) {
            return "TRACE";
        }

        // Shouldn't reach this part.
        return "?";

    }

    static void init() {
        if (logger == null) {
            logger = Logger.getAnonymousLogger();
        }

        if (handler == null) {
            // Remove any old/default handlers.
            for (Handler h : logger.getHandlers()) {
                logger.removeHandler(h);
            }

            // Don't duplicate messages with the root logger.
            logger.setUseParentHandlers(false);

            handler = new ConsoleHandler();

            handler.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord l) {
                    return String.format(
                            "[%1$tF %1$tT] [%2$-5s] %3$s %n",
                            new Date(l.getMillis()),
                            toInternalName(l.getLevel()),
                            l.getMessage());
                }
            });

            logger.addHandler(handler);
        }
    }

    public static void setLevel(int l) {
        init();
        Level ll = toLibraryLevel(l);
        logger.setLevel(ll);
        handler.setLevel(ll);
    }

    static void logLevel(Level l, String s) {
        init();
        logger.log(l, s);
    }

    public static void error(String s) {
        logLevel(Level.SEVERE, s);
    }

    public static void warning(String s) {
        logLevel(Level.WARNING, s);
    }

    public static void info(String s) {
        logLevel(Level.INFO, s);
    }

    public static void debug(String s) {
        logLevel(Level.FINE, s);
    }

    public static void trace(String s) {
        logLevel(Level.FINER, s);
    }
}
