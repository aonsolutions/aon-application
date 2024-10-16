package net.aonsolutions.aon.api.servlet;


import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class LogCleanerServlet implements ServletContextListener {
//	 private static final String LOG_FILE_PATH = "C:/temp/bankservlet.log";
//    private static final long CLEANUP_INTERVAL = 7;
//
//    private ScheduledExecutorService scheduler;
//
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//        scheduler = Executors.newScheduledThreadPool(1);
//        scheduler.scheduleAtFixedRate(() -> {
//            File logFile = new File(LOG_FILE_PATH);
//            if (logFile.exists() && logFile.isFile()) {
//                logFile.delete();
//            }
//        }, CLEANUP_INTERVAL, CLEANUP_INTERVAL, TimeUnit.DAYS);
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//        if (scheduler != null && !scheduler.isShutdown()) {
//            scheduler.shutdown();
//        }
//    }
}
