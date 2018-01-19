package client;

import data.input.WikiArticle;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticsearchClient implements ElasticsearchClientInterface{

    // default config
    private static final String CLUSTER_NAME =  "docker-cluster";
    private static final int TRANSPORT_TCP_PORT =  9201;
    private static final String TYPE_NAME =  "article";
    private static final List<String> CLUSTER_SERVERS =  Collections.unmodifiableList(Collections.singletonList("127.0.0.1"));

    private TransportClient client = null;

    /**
     * Generates Elasticsearch client with default connection
     *
     * @throws UnknownHostException UnknownHostException
     */
    public ElasticsearchClient() throws UnknownHostException {
        this.init(CLUSTER_SERVERS, TRANSPORT_TCP_PORT, CLUSTER_NAME);
    }

    /**
     * @param serverAddresses List of elasticsearch server addresses (e.g. '127.0.0.1')
     * @param transportTCPPort The port for the elasticsearch transport protocol. Attention! Not the HTTP port!
     * @param clusterName Name of the elasticsearch cluster
     *
     * @throws UnknownHostException UnknownHostException
     */
    public ElasticsearchClient(List<String> serverAddresses, int transportTCPPort, String clusterName) throws UnknownHostException {
        this.init(serverAddresses, transportTCPPort, clusterName);
    }

    private void init(List<String> serverAddresses, int transportTCPPort, String clusterName) throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("client.transport.sniff", false)
                .put(ClusterName.CLUSTER_NAME_SETTING.getKey(), clusterName)
                .build()
                ;

        this.client = new PreBuiltTransportClient(settings);

        for (String serverAddress : serverAddresses) {
            this.client.addTransportAddress(new TransportAddress(InetAddress.getByName(serverAddress), transportTCPPort));
        }
    }

    public void closeConnection() {
        this.client.close();
    }

    @Override
    public List<String> findArticleTitlesByLanguageCodeAndQuery(String languageCode, String query) {
        return null; // todo
    }

    @Override
    public void indexArticle(WikiArticle wikiArticle) throws IOException {
        client.prepareIndex(wikiArticle.getLanguage().toString(), TYPE_NAME, wikiArticle.getArticleId())
                .setSource(jsonBuilder()
                        .startObject()
                        .field("title", wikiArticle.getTitle())
                        .field("content", wikiArticle.getContent())
                        .endObject()
                )
                .get();
    }

    @Override
    public void indexArticles(List<WikiArticle> wikiArticles) {
    }
}
