package org.gneisenau.youtube.utils;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		org.slf4j.Logger logger = LoggerFactory.getLogger(Bootstrap.class);
		Level l = Level.WARNING;
		if (logger.isDebugEnabled()) {
			l = Level.FINER;
		} else if (logger.isTraceEnabled()) {
			l = Level.ALL;
		} else if (logger.isInfoEnabled()) {
			l = Level.FINE;
		} else if (logger.isWarnEnabled()) {
			l = Level.WARNING;
		} else if (logger.isErrorEnabled()) {
			l = Level.SEVERE;
		}
		Logger.getLogger("global").setLevel(Level.FINEST);
	}
}