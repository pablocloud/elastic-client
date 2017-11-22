package es.pabloverdugo.domain

import com.fasterxml.jackson.annotation.JsonProperty

class Cluster implements Serializable {

    String name

    @JsonProperty(value = 'cluster_name')
    String clusterName

    @JsonProperty(value = 'cluster_uuid')
    String clusterUuid

    ClusterVersion version

    String tagline

}
