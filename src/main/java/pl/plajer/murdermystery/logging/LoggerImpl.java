package pl.plajer.murdermystery.logging;

import java.util.logging.Logger;

public class LoggerImpl implements ILogger {

    private final Logger logger;

    public LoggerImpl(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(String message) {
        this.logger.warning(message);
    }

    @Override
    public void severe(String message) {
        this.logger.severe(message);
    }

}
