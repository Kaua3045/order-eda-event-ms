package com.kaua.order.infrastructure.configurations.properties.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(KafkaProperties.class);

    private String bootstrapServers;
    private int poolTimeout;
    private boolean autoCreateTopics;
    private boolean autoCommit;

    @Override
    public void afterPropertiesSet() {
        log.debug(toString());
    }

    @Override
    public String toString() {
        return "KafkaProperties{" +
                "bootstrapServers='" + bootstrapServers + '\'' +
                ", poolTimeout=" + poolTimeout +
                ", autoCreateTopics=" + autoCreateTopics +
                ", autoCommit=" + autoCommit +
                '}';
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public int getPoolTimeout() {
        return poolTimeout;
    }

    public void setPoolTimeout(int poolTimeout) {
        this.poolTimeout = poolTimeout;
    }

    public boolean isAutoCreateTopics() {
        return autoCreateTopics;
    }

    public void setAutoCreateTopics(boolean autoCreateTopics) {
        this.autoCreateTopics = autoCreateTopics;
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }
}
