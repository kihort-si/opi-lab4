package org.viacheslav.utils;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.viacheslav.beans.PointCounter;
import org.viacheslav.beans.ShapeArea;

@WebListener
public class MBeanContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        MBeanRegistry.registerBean(new PointCounter(), "pointCounter");
        MBeanRegistry.registerBean(new ShapeArea(), "shapeArea");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        MBeanRegistry.unregisterBean(PointCounter.class);
        MBeanRegistry.unregisterBean(ShapeArea.class);
    }
}
