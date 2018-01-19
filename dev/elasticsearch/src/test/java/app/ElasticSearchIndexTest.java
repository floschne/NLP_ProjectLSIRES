package app;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterName;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class ElasticSearchIndexTest {

    // todo: maybe outsource to config file
    // config
    private static final String CLUSTER_NAME =  "docker-cluster";
    private static final int TRANSPORT_TCP_PORT =  9201;
    private static final String[] CLUSTER_SERVERS =  {"127.0.0.1"};

    private static final String TEST_INDEX_NAME = "de";
    private static final String TEST_TYPE_NAME = "article";

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
    public void testIndexing() throws IOException {

        String content = "Rotherbaum (niederdeutsch: Rothenboom) ist ein Stadtteil im Bezirk Eimsbüttel der Freien und Hansestadt Hamburg. Oft bezeichnet Rotherbaum auch ein größeres Quartier um die Rothenbaumchaussee.\n" +
                "\n"+
                "Geografie[Bearbeiten | Quelltext bearbeiten]\n" +
                "Geografische Lage[Bearbeiten | Quelltext bearbeiten]\n" +
                "Rotherbaum ist umgeben von der Außenalster und dem Stadtteil St. Georg im Osten, Harvestehude im Norden (Grenze ist die Hallerstraße), den Stadtteilen Eimsbüttel im Nordwesten (Grenze ist die Straße Beim Schlump) und Sternschanze im Südwesten. Im Süden liegen St. Pauli, im Südosten die Neustadt, nahe dem Bahnhof Hamburg-Dammtor. Hier bildet die Bahnstrecke der Hamburg-Altonaer Verbindungsbahn, die über die Lombardsbrücke führt, die Grenze.\n" +
                "\n" +
                "Namensherkunft[Bearbeiten | Quelltext bearbeiten]\n" +
                "Der Name Rotherbaum wurde vom Rothen Baum abgeleitet, einem vorgelagerten Wachtposten an der Rothenbaumchaussee in der Zeit der Stadtbefestigung. Der Posten war am Ausgangspunkt der Chaussee nach Eppendorf, an einem Übergang über einen Bach namens Hundebek gelegen und soll einen roten Schlagbaum besessen haben. Bemerkenswert ist, dass der Ortsname trotz der Schreibweise in einem Wort und mit historischem th häufig in gebeugter Form vorkommt („am Rothenbaum“ etc.).\n" +
                "\n" +
                "Neben dem genau umgrenzten Stadtteil wird mit Rotherbaum bisweilen auch ein größeres Quartier rund um die Rothenbaumchaussee bezeichnet, als es die Stadtteilgrenzen umfassen. Das Tennisstadion oder das NDR-Funkhaus liegen im Stadtteil Harvestehude, gleichzeitig jedoch (auf das Stadtviertel bezogen) „am Rothenbaum“.\n" +
                "\n" +
                "Geschichte[Bearbeiten | Quelltext bearbeiten]\n" +
                "\n" +
                "Der Rothenbaum um 1790 (rechts oben), deutlich zu erkennen auch der Bach Hundebek\n" +
                "Das ehemalige Dammtor war früher der Übergangspunkt von Hamburg zu den nordwestlich angrenzenden Ortschaften. Vor dem Stadttor lagen im späten 18. Jahrhundert Landhäuser und Gartengrundstücke. 1813/14, nach den Befreiungskriegen am Ende der Franzosenzeit, wurden diese zerstört. Der Schiffsmakler John Fontenay, nach dem das gesamte Gelände zwischen Alster, Badestraße und Mittelweg sowie die Straßen Fontenay und Fontenay-Allee benannt sind, kaufte um 1816 vor dem Dammtor in großem Umfang Ländereien.\n" +
                "\n" +
                "Nachdem die Torsperre 1860 aufgehoben worden war, wurde das Gebiet zunehmend besiedelt. Wohlhabende Bürger bebauten vor allem das Gebiet östlich der Rothenbaumchaussee mit Villen und Stadthäusern. Im westlichen Teil, wo das Grindelviertel liegt, entstanden alsbald auch Etagenhäuser. 1871 wurde Rotherbaum zum Vorort Hamburgs, 1894 dessen Stadtteil.\n" +
                "\n" +
                "Die Gründung der Universität im Jahr 1919 und die Konzentration der öffentlichen Verwaltung (Fernsprechknotenamt 1912, Museum für Völkerkunde 1912, Norddeutscher Rundfunk 1924, später die Gesundheitsbehörde, Oberfinanzdirektion, Bundesvermögensverwaltung, Standortverwaltung der Bundeswehr) und auch die nach und nach angesiedelten Konsulate waren prägend für die Entwicklung des Stadtteils Rotherbaum.\n" +
                "\n" +
                "Rund 15 Prozent der Einwohner in Harvestehude und Rotherbaum waren Mitte der 1920er Jahre Juden.[1] Es gab bis zur nationalsozialistischen Verfolgung mehrere Synagogen, u.a. die Bornplatzsynagoge. An das Schicksal der jüdischen Bevölkerung Rotherbaums erinnert u. a. der Platz der jüdischen Deportierten neben dem Hauptgebäude der Universität. Siehe hierzu: Artikel Bezirk Eimsbüttel.\n" +
                "\n" +
                "In der Nachkriegszeit fanden in Rotherbaum auch die Curiohaus-Prozesse eines britischen Militärgerichts statt, die sich gegen SS-Täter richteten.";

        // todo: set better id
        IndexResponse response = client.prepareIndex(TEST_INDEX_NAME, TEST_TYPE_NAME, "1")
                .setSource(jsonBuilder()
                        .startObject()
                            .field("title", "Hamburg-Rotherbaum")
                            .field("content", content)
                        .endObject()
                )
                .get();

    }

}
