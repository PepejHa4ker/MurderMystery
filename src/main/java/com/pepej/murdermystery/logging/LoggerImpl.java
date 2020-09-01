package com.pepej.murdermystery.logging;

import java.util.logging.Logger;

public class LoggerImpl implements ILogger {

    private final Logger logger;

    public LoggerImpl(final Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(final String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(final String message) {
        this.logger.warning(message);
    }

    @Override
    public void severe(final String message) {
        this.logger.severe(message);
    }

}
