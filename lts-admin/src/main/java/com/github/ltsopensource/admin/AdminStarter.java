package com.github.ltsopensource.admin;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.core.StandardServer;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;

/**
 * Created by allan on 16/11/23.
 */
public class AdminStarter {
    private static String CONTEXT_PATH = "/lts-admin";
    private final static int DEFAULT_PORT = 8081;
    private final static String SYS_HOME = "application.root";

    public static void main(String[] args) {
        initEnvironment();

        String home = System.getProperty(SYS_HOME);

        System.out.println("home path is " + home);
        if (home == null || home.length() == 0) {
            System.err.println("application.root为空,请在conf/wrapper.conf文件进行设置:wrapper.java.additional.n=-Dapplication.root=程序根路径!");
            System.exit(2);
        }
        String CATALINA_HOME = home;
        String WEB_APP_PATH = home + File.separator + "/webapp";

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(DEFAULT_PORT);
        tomcat.setBaseDir(CATALINA_HOME); //Embeded tomcat存放路径
        tomcat.getHost().setAppBase(WEB_APP_PATH); //应用存放路径
        try {
            StandardServer server = (StandardServer) tomcat.getServer();
            AprLifecycleListener listener = new AprLifecycleListener();
            server.addLifecycleListener(listener);
            Context context = tomcat.addWebapp(CONTEXT_PATH, WEB_APP_PATH);
            tomcat.start();
            System.out.println("Tomcat started at port:" + DEFAULT_PORT);


            tomcat.getServer().await();
        } catch (Exception e) {
            System.err.println("encounter error,close tomcat server");
            e.printStackTrace();
            try {
                tomcat.stop();
            } catch (LifecycleException e1) {
                System.err.println("error happened when closing tomcat server");
                e1.printStackTrace();
            }

        }

    }

    private static void initEnvironment() {
        String home = System.getProperty(SYS_HOME);
        if (home != null) {
            if (home.endsWith(File.pathSeparator)) {
                home = home.substring(0, home.length() - 1);
                System.setProperty(SYS_HOME, home);
            }
        }

        // 为空，默认尝试设置为当前目录或上级目录
        if (home == null) {
            try {
                String path = new File("..").getCanonicalPath().replaceAll("\\\\", "/");
                File conf = new File(path + "/conf");
                if (conf.exists() && conf.isDirectory()) {
                    home = path;
                } else {
                    path = new File(".").getCanonicalPath().replaceAll("\\\\", "/");
                    conf = new File(path + "/conf");
                    if (conf.exists() && conf.isDirectory()) {
                        home = path;
                    }
                }

                if (home != null) {
                    System.setProperty(SYS_HOME, home);
                }
            } catch (IOException e) {
                // 如出错，则忽略。
            }
        }
    }
}
