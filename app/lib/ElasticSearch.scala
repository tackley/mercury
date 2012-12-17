package lib

import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.action.search.SearchRequestBuilder
import org.elasticsearch.client.Client
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.transport._
import org.elasticsearch.common.settings._
import org.elasticsearch.common.unit._

import org.elasticsearch.index.query.QueryBuilders._
import play.api.Logger


object ElasticSearch {
  val dev = true

  val (clusterName, hostname) = if (dev) ("ophan-DEV", "localhost")
    else ("ophan-PROD", "ec2-54-247-16-54.eu-west-1.compute.amazonaws.com")

  val client = new TransportClient(ImmutableSettings.settingsBuilder().put("cluster.name", clusterName))
    .addTransportAddress(new InetSocketTransportAddress(hostname, 9300))

  Logger.info("Cluster status is " + client.admin.cluster.prepareHealth().execute().actionGet().status)





}
