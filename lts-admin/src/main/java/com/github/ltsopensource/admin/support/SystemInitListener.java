package com.github.ltsopensource.admin.support;

import com.github.ltsopensource.core.commons.utils.PlatformUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.compiler.AbstractCompiler;
import com.github.ltsopensource.core.constant.ExtConfig;
import com.github.ltsopensource.core.json.JSONFactory;
import com.github.ltsopensource.monitor.MonitorAgentStartup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.net.URI;

/**
 * @author Robert HG (254963746@qq.com) on 9/2/15.
 */
public class SystemInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {


        String confPath = servletContextEvent.getServletContext().getInitParameter("lts.admin.config.path");
        if (StringUtils.isEmpty(confPath)) {
            confPath = System.getProperty("application.root") + "/conf";
        }
        System.out.println("lts.admin.config.path is " + confPath);
        AppConfigurer.load(confPath);

        String compiler = AppConfigurer.getProperty("configs." + ExtConfig.COMPILER);
        if (StringUtils.isNotEmpty(compiler)) {
            AbstractCompiler.setCompiler(compiler);
        }

        String jsonAdapter = AppConfigurer.getProperty("configs." + ExtConfig.LTS_JSON);
        if (StringUtils.isNotEmpty(jsonAdapter)) {
            JSONFactory.setJSONAdapter(jsonAdapter);
        }

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        URI log4j = new File(confPath + "/log4j2.xml").toURI();
        context.setConfigLocation(log4j);

        System.out.println("finished to configure log4j:" + log4j.toString());

        boolean monitorAgentEnable = Boolean.valueOf(AppConfigurer.getProperty("lts.monitorAgent.enable", "true"));
        if (monitorAgentEnable) {
            String ltsMonitorCfgPath = confPath;
            if (StringUtils.isEmpty(ltsMonitorCfgPath)) {
                ltsMonitorCfgPath = this.getClass().getResource("/").getPath();
                if (PlatformUtils.isWindows()) {
                    // 替换window下空格问题
                    ltsMonitorCfgPath = ltsMonitorCfgPath.replaceAll("%20", " ");
                }
            }
            MonitorAgentStartup.start(ltsMonitorCfgPath);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        MonitorAgentStartup.stop();
    }
}
