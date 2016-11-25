package com.github.ltsopensource.jobtracker.starter;

import com.github.ltsopensource.core.commons.utils.StringUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Robert HG (254963746@qq.com) on 9/1/15.
 */
public class JobTrackerCfgLoader {


    public static JobTrackerCfg load(String home) throws CfgException {
        Properties conf = new Properties();
        String cfgPath = home + "/conf/jobtracker.cfg";

        File file = new File(cfgPath);
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new CfgException("can not find " + cfgPath);
        }
        try {
            conf.load(is);
        } catch (IOException e) {
            throw new CfgException("Read " + cfgPath + " error.", e);
        }

        JobTrackerCfg cfg = new JobTrackerCfg();
        String registryAddress = conf.getProperty("registryAddress");
        if (StringUtils.isEmpty(registryAddress)) {
            throw new CfgException("registryAddress can not be null.");
        }
        cfg.setRegistryAddress(registryAddress);

        String clusterName = conf.getProperty("clusterName");
        if (StringUtils.isEmpty(clusterName)) {
            throw new CfgException("clusterName can not be null.");
        }
        cfg.setClusterName(clusterName);

        String bindIp = conf.getProperty("bindIp");
        if (StringUtils.isNotEmpty(clusterName)) {
            cfg.setBindIp(bindIp);
        }

        String listenPort = conf.getProperty("listenPort");
        if (StringUtils.isEmpty(listenPort) || !StringUtils.isInteger(listenPort)) {
            throw new CfgException("listenPort can not be null.");
        }
        cfg.setListenPort(Integer.parseInt(listenPort));

        Map<String, String> configs = new HashMap<String, String>();
        for (Map.Entry<Object, Object> entry : conf.entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith("configs.")) {
                String value = entry.getValue() == null ? null : entry.getValue().toString();
                configs.put(key.replace("configs.", ""), value);
            }
        }

        cfg.setConfigs(configs);

        return cfg;
    }


}
