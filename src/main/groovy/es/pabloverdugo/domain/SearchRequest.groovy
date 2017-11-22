package es.pabloverdugo.domain

class SearchRequest implements Serializable {

    String key
    String value

    String stringRequest() {
        key + ':' + value
    }

}
