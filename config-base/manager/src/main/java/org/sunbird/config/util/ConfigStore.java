package org.sunbird.config.util;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;
import org.sunbird.cassandra.store.CassandraStoreImpl;
import org.sunbird.common.exception.ServerException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConfigStore {

    private static Map<String, Object> configStore = new HashMap<>();
    private static CassandraStoreImpl auditStore = new CassandraStoreImpl();

    static {
        auditStore.initialise(Constants.CASSANDRA_KEYSPACE, Constants.CASSANDRA_AUDIT_TABLE, null, true);
    }

    //TODO Refactor in-memory storage interaction

    /**
     * Returns the value of the provided key from the Hashmap
     *
     * @param key Key of the configuration to be retrieved
     * @return Object
     */
    private static Object getConfig(String key) {
        return configStore.get(key);
    }

    /**
     * Stores the provided configuration data (as key, value) in the Hashmap
     *
     * @param key   Key of the configuration
     * @param value Value of the configuration
     */
    private static void setConfig(String key, Object value) {
        configStore.put(key, value);
    }

    /**
     * Returns the value of the provided key from the Hashmap
     *
     * @param key Key of the configuration to be retrieved
     * @return Boolean
     */
    public static Boolean isConfigKeyExists(String key) {
        return configStore.containsKey(key);
    }

    /**
     * Clears the current config data
     */
    private static void clearConfig() {
        configStore.clear();
    }

    public static Boolean refresh(String configPath) {
        try {
            //Get the config data from cloud store
            String configData = CloudStore.getObjectData(configPath);

            //Parse the received config using typesafe config utility
            Config parsedConfigData = ConfigFactory.parseString(configData);
            Set parsedConfigDataList = parsedConfigData.entrySet();

            //If there is some data provided
            if (parsedConfigDataList.size() > 0) {
                //Save the path as Audit log to be used on service restart
                Long currentEpoch = Instant.now().toEpochMilli();

                Map<String, Object> cloudStoreConfig = CloudStore.getConfig();
                cloudStoreConfig.put(Constants.CASSANDRA_AUDIT_COLUMN_PATH, configPath);
                cloudStoreConfig.put(Constants.CASSANDRA_AUDIT_COLUMN_DATE, currentEpoch);
                cloudStoreConfig.put(Constants.CASSANDRA_AUDIT_COLUMN_KEY, Constants.CASSANDRA_CURRENT_ID_VALUE);
                auditStore.upsertRecord(cloudStoreConfig);

                //Clear the previous data
                clearConfig();

                // Iterate over flat config and Store
                for (Map.Entry<String, ConfigValue> entry : parsedConfigData.entrySet()) {
                    String key = entry.getKey();
                    Object val = entry.getValue().unwrapped();
                    setConfig(key, val);
                }
            }
        } catch (Exception e) {
            throw new ServerException("ERR_REFRESH_CONFIG_DATA", "Error while refreshing config data.");
        }
        return true;
    }

    public static Object read(String configKey) {
        return getConfig(configKey);
    }
}