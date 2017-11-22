package es.pabloverdugo.domain

import com.fasterxml.jackson.annotation.JsonProperty

class ClusterVersion implements Serializable {

    String number

    @JsonProperty(value = 'build_hash')
    String buildHash

    @JsonProperty(value = 'build_date')
    Date buildDate

    @JsonProperty(value = 'build_snapshot')
    Boolean buildSnapshot

    @JsonProperty(value = 'lucene_version')
    String luceneVersion

}
