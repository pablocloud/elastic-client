package es.pabloverdugo.elastic

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import es.pabloverdugo.domain.SearchRequest
import org.junit.Test

class ElasticClientTest {

    private final String host = '127.0.0.1'
    private final Integer port = 9200
    private final String indexName = 'flightsearch'
    private final String aggregation = '{"aggs" : {"latestSearchDate" : { "max" : { "field" : "queryDate" } } } }'

    private final static ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true)

    ElasticClient elasticClient = new ElasticClient<>(host, port, indexName)

    @Test
    void clusterDescriptionTest() {
        println mapper.writeValueAsString(elasticClient.getClusterDescription())
    }

    @Test
    void getIndexesNames() {
        println mapper.writeValueAsString(elasticClient.getIndexes())
    }

    @Test
    void getIndexesWithInfo() {
        println mapper.writeValueAsString(elasticClient.getIndexesWithInfo())
    }

    @Test
    void getAllDocuments() {
        println mapper.writeValueAsString(elasticClient.getAllDocumentsOnIndex())
    }

    @Test
    void getDocumentsWithSearch() {
        println mapper.writeValueAsString(elasticClient.search(new SearchRequest(key: 'destination', value: 'PMI')))
    }

    @Test
    void aggregationTest() {
        println mapper.writeValueAsString(elasticClient.getDocumentWithAggregation(aggregation).get('aggregations'))
    }

    @Test
    void aggregationTestWithSearch() {
        println mapper.writeValueAsString(elasticClient
                .getDocumentWithAggregationAndSearch(new SearchRequest(key: 'destination', value: 'PMI'), aggregation)
                .get('aggregations'))
    }

}
