package es.pabloverdugo.rest

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.async.Callback
import com.mashape.unirest.http.exceptions.UnirestException

import java.util.logging.Logger

class RestClient extends Unirest {

    private final static Logger logger = Logger.getLogger('RestClient')
    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    def static getSyncNoMapper(String url) {
        get(url).asString().body
    }

    def static headSync(String url) {
        if (head(url).asString().body == '200 - OK') {
            true
        } else {
            false
        }
    }

    def static postSync(String url, def t, Class responseClass) {
        def jsonBody = mapper.writeValueAsString(t)
        def response = post(url)
                .header('Content-Type', 'application/json')
                .body(jsonBody).asString().body
        logger.info(response)
        response
    }

    def static postSyncNoMapper(String url, def t) {
        def response = post(url)
                .header('Content-Type', 'application/json')
                .body(t).asString().body
        logger.info(response)
        response
    }

    def static postAsync(String url, def t, Class responseClass) {
        def jsonBody = mapper.writeValueAsString(t)
        post(url)
                .header('Content-Type', 'application/json')
                .body(jsonBody).asStringAsync(new Callback<String>() {

            @Override
            void completed(HttpResponse<String> httpResponse) {
                logger.info(httpResponse.body)
            }

            @Override
            void failed(UnirestException e) {
                logger.warning(e.getMessage())
            }

            @Override
            void cancelled() {
                logger.warning('Failed rest request')
            }

        })
    }

    def static putSync(String url, def t, Class responseClass) {
        def jsonBody = mapper.writeValueAsString(t)
        def response = put(url)
                .header('Content-Type', 'application/json')
                .body(jsonBody).asString().body
        def value = mapper.readValue(response, responseClass)
        logger.info(response)
        value
    }

    def putAsync(String url, def t, Class responseClass) {
        def jsonBody = mapper.writeValueAsString(t)
        put(url)
                .header('Content-Type', 'application/json')
                .body(jsonBody).asStringAsync(new Callback<String>() {

            @Override
            void completed(HttpResponse<String> httpResponse) {
                logger.info(httpResponse.body)
                result = mapper.readValue(httpResponse.body, responseClass)
            }

            @Override
            void failed(UnirestException e) {
                logger.warning(e.getMessage())
            }

            @Override
            void cancelled() {
                logger.warning('Failed rest request')
            }

        })
    }

    def static deleteSync(String url) {
        delete(url).asString().body
    }

    def static deleteAsync(String url) {
        delete(url).asStringAsync(new Callback<String>() {

            @Override
            void completed(HttpResponse<String> httpResponse) {
                logger.info(httpResponse.body)
            }

            @Override
            void failed(UnirestException e) {
                logger.warning(e.getMessage())
            }

            @Override
            void cancelled() {
                logger.warning('Failed rest request')
            }
        })

    }

    def getResult() {
        result
    }

}