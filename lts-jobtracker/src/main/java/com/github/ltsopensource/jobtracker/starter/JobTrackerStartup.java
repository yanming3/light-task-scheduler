package com.github.ltsopensource.jobtracker.starter;

import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.jobtracker.JobTracker;
import com.github.ltsopensource.jobtracker.support.policy.OldDataDeletePolicy;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @author Robert HG (254963746@qq.com) on 9/1/15.
 */
public class JobTrackerStartup {
    private final static String SYS_HOME = "application.root";

    public static void main(String[] args) {
        initEnvironment();

        String home = System.getProperty(SYS_HOME);

        System.out.println("home is "+home);
        if (home == null || home.length() == 0) {
            System.err.println("application.root为空,请在conf/wrapper.conf文件进行设置:wrapper.java.additional.n=-Dapplication.root=程序根路径!");
            System.exit(2);
        }
        try {
            JobTrackerCfg cfg = JobTrackerCfgLoader.load(home);
            final JobTracker jobTracker = new JobTracker();
            jobTracker.setRegistryAddress(cfg.getRegistryAddress());
            jobTracker.setListenPort(cfg.getListenPort());
            jobTracker.setClusterName(cfg.getClusterName());
            if (StringUtils.isNotEmpty(cfg.getBindIp())) {
                jobTracker.setBindIp(cfg.getBindIp());
            }

            jobTracker.setOldDataHandler(new OldDataDeletePolicy());

            for (Map.Entry<String, String> config : cfg.getConfigs().entrySet()) {
                jobTracker.addConfig(config.getKey(), config.getValue());
            }
            // 启动节点
            jobTracker.start();

            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    jobTracker.stop();
                }
            }));

        } catch (CfgException e) {
            System.err.println("JobTracker Startup Error: " + e.getMessage());
            e.printStackTrace();
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
