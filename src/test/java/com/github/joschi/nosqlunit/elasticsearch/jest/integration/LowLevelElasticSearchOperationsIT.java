package com.github.joschi.nosqlunit.elasticsearch.jest.integration;

import org.apache.http.HttpHost;
import org.junit.Test;

import java.io.IOException;

import static com.github.joschi.nosqlunit.elasticsearch.jest.LowLevelElasticSearchOperations.assertThatConnectionToElasticsearchIsPossible;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LowLevelElasticSearchOperationsIT extends BaseIT {
    private final HttpHost serverAddress = getServer();

    @Test
    public void successful_connection_should_not_throw() throws IOException {
        assertThatConnectionToElasticsearchIsPossible(serverAddress);
    }

    @Test
    public void unsuccessful_connection_should_throw_AssertionError() throws IOException {
        try {
            assertThatConnectionToElasticsearchIsPossible(new HttpHost("localhost", 0, "http"), 1);
        } catch (AssertionError e) {
            assertThat(e.getMessage(), is("Couldn't connect to Elasticsearch at [http://localhost:0]"));
        }
    }
}
