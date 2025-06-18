package com.freeuni.quiz;

import com.freeuni.quiz.util.DBUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

@WebListener
public class ContextListener implements ServletContextListener {

    private DataSource dataSource;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        dataSource = DBUtil.createDataSource();
        ServletContext context = sce.getServletContext();
        context.setAttribute("dataSource", dataSource);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            if (dataSource instanceof AutoCloseable) {
                ((AutoCloseable) dataSource).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
