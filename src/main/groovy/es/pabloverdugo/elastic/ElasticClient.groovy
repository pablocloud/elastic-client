package es.pabloverdugo.elastic

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import es.pabloverdugo.domain.*
import es.pabloverdugo.rest.RestClient
import groovy.json.JsonSlurper

class ElasticClient {

    private final static RestClient restClient = new RestClient<>()
    private final static String DOC = 'doc'
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    String host
    Integer port
    String indexName
    String url = 'http://' + host + ':' + port + '/'

    ElasticClient(String host, Integer port, String indexName) {
        this.host = host
        this.port = port
        this.indexName = indexName
    }

    Cluster getClusterDescription() {
        String json = restClient.getSyncNoMapper(url)
        mapper.readValue(json, Cluster)
    }

    /**
     * Retriveves the indexes names only
     * @return List
     */
    List<Index> getIndexes() {
        String result = restClient.getSyncNoMapper(url + '_cat/indices')
        List<Index> list = new ArrayList<>()
        result.eachLine {
            list.add(new Index(name: it.split(' ')[2]))
        }
        list
    }

    Index getIndexInfo(Index index) {
        String result = restClient.getSyncNoMapper(url + index.name)
        Map json = new JsonSlurper().parseText(result) as Map
        Map root = json.get(index.name) as Map
        Map mappings = root.get('mappings') as Map
        Map settings = (root.get('settings') as Map).get('index') as Map
        index.mappings = mappings.get('doc') as Map
        index.settings = new IndexSettings(
                creationDate: new Date(settings.get('creation_date') as Long),
                numberOfShards: settings.get('number_of_shards') as Integer,
                numberOfReplicas: settings.get('number_of_replicas') as Integer,
                uuid: settings.get('uuid'),
                providedName: settings.get('provided_name')
        )
        index
    }

    List<Index> getIndexesWithInfo() {
        List<Index> indexes = getIndexes()
        indexes.each {
            getIndexInfo(it)
        }
        indexes
    }

    Map getAllDocumentsOnIndex() {
        String result = restClient.getSyncNoMapper(url + indexName + '/_search')
        new JsonSlurper().parseText(result) as Map
    }

    Map search(SearchRequest request) {
        String result = restClient.getSyncNoMapper(url + indexName + '/_search?q=' + request.stringRequest())
        new JsonSlurper().parseText(result) as Map
    }

    Map getDocumentById(String id) {
        String result = restClient.getSyncNoMapper(url + indexName + '/' + id)
        new JsonSlurper().parseText(result) as Map
    }

    Map getDocumentWithAggregation(String aggregationRequest) {
        String result = restClient.postSyncNoMapper(url + indexName + '/_search', aggregationRequest)
        new JsonSlurper().parseText(result) as Map
    }

    Map getDocumentWithAggregationAndSearch(SearchRequest request, String aggregationRequest) {
        String result = restClient.postSyncNoMapper(url + indexName + '/_search?q=' + request.stringRequest(), aggregationRequest)
        new JsonSlurper().parseText(result) as Map
    }

    Boolean checkIfIdExists(String id) {
        restClient.headSync(url + DOC + '/' + id)
    }

    def insertIntoIndexSync(def content) {
        restClient.postSync(url + indexName + '/' + DOC, content)
    }

    def insertIntoIndexAsync(def content) {
        restClient.postAsync(url + indexName + '/' + DOC, content)
    }

    def updateIndexSync(def content, String id) {
        restClient.putSync(url + DOC + '/' + id, content)
    }

    def updateIndexAsync(def content, String id) {
        restClient.putAsync(url + DOC + '/' + id, content)
    }

    Boolean deleteDocumentByIdSync(String id) {
        restClient.deleteSync(url + indexName + '/' + id)
    }

    Boolean deleteDocumentByIdAsync(String id) {
        restClient.deleteAsync(url + indexName + '/' + id)
    }

    def reindexSync(String destination) {
        restClient.postSync(url + '_reindex', createReindexRequest(destination))
    }

    def reindexAsync(String destination) {
        restClient.postAsync(url + '_reindex', createReindexRequest(destination))
    }

    private ReindexRequest createReindexRequest(String destination) {
        ReindexRequest reindexRequest = new ReindexRequest()
        reindexRequest.source = new HashMap()
        reindexRequest.dest = new HashMap()
        reindexRequest.source.put('index', indexName)
        reindexRequest.dest.put('index', destination)
        reindexRequest
    }

}
