package app;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

public class ElasticSearchTest {
    // todo: maybe outsource to config file
    // config
    private static final String CLUSTER_NAME =  "docker-cluster";
    private static final int TRANSPORT_TCP_PORT =  9201;
    private static final String[] CLUSTER_SERVERS =  {"127.0.0.1"};

    private static final String TEST_INDEX_NAME = "test_index";
    private static final String TEST_INDEX_ALIAS = "test_index_alias";

    private TransportClient client = null;

    @Before
    public void init() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("client.transport.sniff", false)
                .put(ClusterName.CLUSTER_NAME_SETTING.getKey(), CLUSTER_NAME)
                .build()
                ;

        this.client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(CLUSTER_SERVERS[0]), TRANSPORT_TCP_PORT))
        ;
    }

    @After
    public void closeConnection() {
        this.client.close();
    }

    @Test
    @Ignore
    public void generateAndDeleteTestIndexTest() throws ExecutionException, InterruptedException {

        // create index
        CreateIndexResponse response = this.client.admin().indices().prepareCreate(TEST_INDEX_NAME)
                .addAlias(new Alias(TEST_INDEX_ALIAS))
                .get()
        ;
        Assert.assertTrue(response.isAcknowledged());

        // delete new index
        DeleteIndexResponse deleteResponse = client.admin().indices().delete(new DeleteIndexRequest(TEST_INDEX_NAME)).actionGet();
        Assert.assertTrue(deleteResponse.isAcknowledged());
    }

    @Test
    @Ignore
    public void getAllIndexesTest() throws ExecutionException, InterruptedException {

        ActionFuture<GetIndexResponse> getIndexResponseActionFuture = this.client.admin().indices().getIndex(new GetIndexRequest());

        for(String elem : getIndexResponseActionFuture.get().getIndices()){
            System.out.println(elem);
        }
    }

    @Test
    @Ignore
    public void checkClusterHealthTest(){
        ClusterHealthResponse response = this.client.admin()
                .cluster()
                .health(
                        Requests
                                .clusterHealthRequest()
                                .waitForGreenStatus()
                                .timeout(TimeValue.timeValueSeconds(5))
                )
                .actionGet();

        Assert.assertEquals(response.getStatus().value(), (byte)1);
    }

}
