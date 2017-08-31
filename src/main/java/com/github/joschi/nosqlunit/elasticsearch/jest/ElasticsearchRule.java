package com.github.joschi.nosqlunit.elasticsearch.jest;

import com.lordofthejars.nosqlunit.core.AbstractNoSqlTestRule;
import com.lordofthejars.nosqlunit.core.DatabaseOperation;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestHighLevelClient;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Set;

public class ElasticsearchRule extends AbstractNoSqlTestRule {

    private static final String EXTENSION = "json";

    private DatabaseOperation<? extends RestHighLevelClient> databaseOperation;

    public static Builder newElasticsearchRule() {
        return new Builder();
    }

    public static class Builder {

        private ElasticsearchConfiguration elasticsearchConfiguration;
        private Object target;

        private Builder() {
        }

        /**
         * Configure the {@link ElasticsearchRule} with the given configuration.
         *
         * @param elasticsearchConfiguration The configuration for the {@link ElasticsearchRule}
         */
        public Builder configure(ElasticsearchConfiguration elasticsearchConfiguration) {
            this.elasticsearchConfiguration = elasticsearchConfiguration;
            return this;
        }

        public Builder unitInstance(Object target) {
            this.target = target;
            return this;
        }

        /**
         * Build an {@link ElasticsearchRule} with the default configuration (connecting to {@literal http://localhost:9200}).
         *
         * @return An {@link ElasticsearchRule} using the default configuration
         */
        public ElasticsearchRule remoteElasticsearch() {
            return new ElasticsearchRule(ElasticsearchConfiguration.remoteElasticsearch().build());
        }

        /**
         * Build an {@link ElasticsearchRule} with the default configuration (connecting to the given Elasticsearch node).
         *
         * @return An {@link ElasticsearchRule} using the default configuration
         */
        public ElasticsearchRule remoteElasticsearch(HttpHost server) {
            return remoteElasticsearch(Collections.singleton(server));
        }

        /**
         * Build an {@link ElasticsearchRule} with the default configuration (connecting to the given Elasticsearch nodes).
         *
         * @return An {@link ElasticsearchRule} using the default configuration
         */
        public ElasticsearchRule remoteElasticsearch(Set<HttpHost> servers) {
            return new ElasticsearchRule(ElasticsearchConfiguration.remoteElasticsearch(servers).build());
        }

        /**
         * Build an {@link ElasticsearchRule} with the given configuration.
         *
         * @return An {@link ElasticsearchRule} using the given configuration
         * @see #configure(ElasticsearchConfiguration)
         */
        public ElasticsearchRule build() {

            if (this.elasticsearchConfiguration == null) {
                throw new IllegalArgumentException("Configuration object should be provided.");
            }

            return new ElasticsearchRule(elasticsearchConfiguration, target);
        }

    }

    public ElasticsearchRule(ElasticsearchConfiguration elasticsearchConfiguration) {
        super(elasticsearchConfiguration.getConnectionIdentifier());
        this.databaseOperation = new ElasticsearchOperation(
                elasticsearchConfiguration.getClient(),
                elasticsearchConfiguration.isCreateIndices(),
                elasticsearchConfiguration.isDeleteAllIndices(),
                elasticsearchConfiguration.getIndexSettings(),
                elasticsearchConfiguration.getIndexTemplates()
        );
    }

    /*With JUnit 10 is impossible to get target from a Rule, it seems that future versions will support it. For now constructor is apporach is the only way.*/
    public ElasticsearchRule(ElasticsearchConfiguration elasticsearchConfiguration, Object target) {
        this(elasticsearchConfiguration);
        setTarget(target);
    }

    @Override
    public DatabaseOperation getDatabaseOperation() {
        return this.databaseOperation;
    }

    @Override
    public String getWorkingExtension() {
        return EXTENSION;
    }

    @Override
    public void close() {
        try {
            this.databaseOperation.connectionManager().close();
        } catch (IOException e) {
            throw new UncheckedIOException("Error while closing Elasticsearch client", e);
        }
    }
}
