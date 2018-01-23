package data.input;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class WikiHttpApiLoaderTest {

    // response for API call: https://de.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext&format=xml&titles=Pizza
    private static String STATIC_PIZZA_RESPONSE = "\n" +
            "<?xml version=\"1.0\"?><api batchcomplete=\"\"><warnings><extracts xml:space=\"preserve\">&quot;exlimit&quot; was too large for a whole article extracts request, lowered to 1.</extracts></warnings><query><pages><page _idx=\"10959\" pageid=\"10959\" ns=\"0\" title=\"Pizza\"><extract xml:space=\"preserve\">Pizza (Aussprache deutsch [ˈpɪt͡sa], italienisch [ˈpitːsa], Plural die Pizzas oder die Pizzen) ist ein vor dem Backen würzig belegtes Fladenbrot aus einfachem Hefeteig aus der italienischen Küche. Die heutige, international verbreitete Variante mit Tomatensauce und Käse als Basis stammt vermutlich aus Neapel.\n" +
            "2017 wurde die neapolitanische Kunst des Pizzabackens („Pizzaiuolo“) von der UNESCO in die repräsentative Liste des immateriellen Kulturerbes der Menschheit aufgenommen.\n" +
            "\n" +
            "\n" +
            "== Etymologie ==\n" +
            "Die Etymologie des Wortes Pizza ist nicht abschließend geklärt. Folgende Spuren wurden verfolgt:\n" +
            "\n" +
            " Das Wort stammt vielleicht vom langobardischen pizzo oder bizzo (nicht belegt), was dem deutschen Bissen entspricht, vgl. auch Imbiss.\n" +
            " Das Wort stammt evtl. aus dem jüngeren orientalisch-semitischen Raum (z. B. dem Arabischen) und ist von dort ins Griechische als  Pita eingewandert und von dorther ins Italienische entlehnt worden.\n" +
            " Das Wort ist noch älter und steht in Zusammenhang mit dem hebräischen Wort פַת pat (Stück Brot; bibl. noch Brocken) oder gar mittelägypt. bjt (Fladen).\n" +
            " Ihm liegt das italienische pitta (lateinisch picta) zugrunde, diesem vermutlich wiederum das griechische pēktos (πηκτός; fest, geronnen).\n" +
            " Das Wort stammt von einem italienischen Verb, z. B.\n" +
            " von pestare, „zerstampfen“, wie auch bei Pesto, vgl. lateinisch pista „gestampft, gestoßen“, pistor „Bäcker, Müller“.\n" +
            " von pinza, einem Dialektwort, das immer noch in einigen italienischen Mundarten vorkommt und sich von lateinisch pinsere (zerstoßen) ableitet. Im Mittellateinischen wurde das Verb in der Bedeutung „backen“ gebraucht.\n" +
            " vom neapolitanischen piceà bzw. pizzà für „zupfen“ abgeleitet, vergleiche kalabrisch pitta und mittellateinisch pecia („Stück“, „Teil“, „Fetzen“).Ob das Wort aus einer westeuropäischen Sprache ins Griechische als pita für Fladen entlehnt wurde oder umgekehrt, lässt sich nicht beantworten.\n" +
            "\n" +
            "\n" +
            "== Geschichte ==\n" +
            "\n" +
            "\n" +
            "=== 18. Jahrhundert ===\n" +
            "\n" +
            "Die nur mit Olivenöl beträufelte, mit Tomatenscheiben und Oregano oder Basilikum belegte Pizza ist seit etwa der Mitte des 18. Jahrhunderts nachgewiesen, als die Tomate in Süditalien populär wurde. Der Name ist vermutlich älter – die apulische Pizza pugliese oder die kalabresische Pitta inchiusa zum Beispiel enthalten neben Hefeteig nur seit alters bekannte Zutaten wie Olivenöl, Zwiebeln, Salz oder Schweineschmalz. Der ligurischen Focaccia ähnliche Fladenbrote sind seit der Antike verbreitet. Da Pizza bei sehr hoher Temperatur gebacken werden sollte, was in den wenigsten Haushalten möglich war, wurde sie anfangs vorbereitet und ungebacken zum örtlichen Bäcker gebracht, bis ein eigener Handwerkszweig der Pizzamacher, der Pizzaiolo entstand, der den Teig selbst herstellte und belegte.\n" +
            "Eine Pizza, die heutigen Vorstellungen entspricht, soll erstmals am 11. Juni 1889 in Neapel vom Pizzaiolo Raffaele Esposito von der Pizzeria Brandi hergestellt worden sein, der beauftragt worden sein soll, König Umberto I. und seiner Frau Margherita eine Pizza zu servieren. Diese soll er patriotisch mit Zutaten in den italienischen Nationalfarben belegt haben: grünem Basilikum, weißem Mozzarella und roten Tomaten. Diese Geschichte ist jedoch mittlerweile von Historikern widerlegt worden. Die Königin ließ sich bereits vorher von jeweils anderen Pizzabäckern Pizza in den Palast bringen. Im Jahr 1880 erschien hierüber ein Zeitungsartikel auch in der Washington Post; aus einer Liste mit 35 verschiedenen Pizzabelägen wählte sie acht Sorten aus, die dann für sie gebacken wurden. Bei diesem Pizzabäcker handelte es sich nicht um Esposito. Er war lediglich der einzige, der die Empfangsbestätigung des Hofes aufbewahrt hatte.\n" +
            "\n" +
            "\n" +
            "=== Gegenwart ===\n" +
            "\n" +
            "Durch italienische Auswanderer verbreitete sich die Pizza gegen Ende des 19. Jahrhunderts auch in den USA. Im Oktober 1937 wurde in Frankfurt am Main erstmals eine Pizza auf dem damaligen Festhallengelände im Rahmen der 7. Internationalen Kochkunst-Ausstellung bei der Messe Frankfurt zubereitet. Nach dem Zweiten Weltkrieg wurde Pizza auch in Europa außerhalb Italiens bekannter. Die erste Pizzeria in Deutschland wurde von Nicolino di Camillo im März 1952 in Würzburg eröffnet. Neben Spaghetti ist die Pizza heute das bekannteste italienische Nationalgericht, sie wird weltweit angeboten.\n" +
            "\n" +
            "\t\t\n" +
            "\n" +
            "\n" +
            "== Zubereitung ==\n" +
            "Zur Zubereitung wird zuerst ein einfacher Hefeteig aus Mehl, Wasser, wenig Hefe, Salz und eventuell etwas Olivenöl hergestellt, gründlich durchgeknetet und nach einer Gehzeit von mindestens einer Stunde bei Zimmertemperatur (bzw. über Nacht im oberen Fach des Kühlschranks) ausgerollt oder mit den bemehlten Händen dünn ausgezogen. Geübte Pizzabäcker ziehen den Teig über den Handrücken und weiten ihn durch Kreisenlassen in der Luft.\n" +
            "\n" +
            "Dann wird der Teig mit den Zutaten je nach Rezept nicht zu üppig belegt, üblicherweise zuerst mit Tomatenscheiben oder häufiger mit passierten Dosentomaten oder Salsa pizzaiola (einer vorher gekochten, sämigen Tomatensauce, die mit Oregano, Basilikum, Knoblauch und anderem kräftig gewürzt ist). Es folgen der Käse (z. B. Mozzarella, Parmesan oder Pecorino) und die übrigen Zutaten, zum Abschluss etwas Olivenöl.\n" +
            "Schließlich wird die Pizza bei einer möglichst hohen Temperatur von 400 bis 500 °C für wenige Minuten kurz gebacken. Dies geschieht in einer möglichst niedrigen Kammer. Ein Stapeln in Einschüben oder separat schaltbare Unter- und Oberhitze ist daher nicht üblich. Der traditionelle Kuppelofen ist gemauert und die Hitze wird über ein Feuer direkt im Backraum erzeugt. Moderne Pizzaöfen werden mit Gas oder Strom beheizt.\n" +
            "In Haushaltsbacköfen sind meist nur Temperaturen bis 250 °C möglich, wodurch sich die Backzeit verlängert und kein optimales Ergebnis erzielt wird. Durch die Verwendung eines vorgeheizten, meist aus Schamotte bestehenden Pizzasteins anstelle eines Backblechs lassen sich bessere Resultate erzielen, weil dieser die Hitze gleichmäßiger hält und Schwitzwasserbildung verhindert. Ein ähnlicher Effekt lässt sich jedoch auch erreichen, indem man die auf der Unterseite ausreichend bemehlte Pizza direkt auf ein bereits im Ofen vorgeheiztes Backblech gibt und im unteren Ofenbereich oder auf der untersten Schiene bei (Ober- und) Unterhitze backt. Dies verhindert das Festkleben des Teigbodens. Hierbei sind ggf. in der jeweiligen Gebrauchsanweisung angegebene Temperatureinschränkungen von Blechen (manche nur bis 220 °C verwendbar und nicht für Vorheizen ohne Backgut geeignet) und Backpapier (meist nur bis 220 °C) zu beachten.\n" +
            "\n" +
            "\n" +
            "== Pizza Napoletana ==\n" +
            "\n" +
            "Als Reaktion auf die Verbreitung von Fast-Food-Pizza und Tiefkühlpizza wurde 1984 in Neapel mit Unterstützung der Region Kampanien die Associazione Verace Pizza Napoletana gegründet, die sich die Wahrung der Tradition der Pizza napoletana zur Aufgabe gestellt hat. Ihre Mitglieder, Pizzerien auf der ganzen Welt, dürfen ihre Pizza als Verace Pizza Napoletana (echte neapolitanische Pizza) bezeichnen. Die traditionelle Herstellungsweise und die Verwendung der korrekten Zutaten wird regelmäßig kontrolliert.Am 9. Februar 2005 wurde die Pizza Napoletana als Warenzeichen innerhalb der Europäischen Union eingetragen und die zugelassenen Zutaten festgelegt. Die Herstellung einer verace pizza napoletana artigianale (echten handgemachten neapolitanischen Pizza) als specialità tradizionale garantita (STG, garantiert traditionelle Spezialität) wird in der italienischen Norm UNI 10791:98 und der EU Verordnung 97/2010 festgeschrieben.Seit 5. Februar 2010 ist die traditionelle Zusammensetzung oder das traditionelle Herstellungsverfahren des Produktes als garantiert traditionelle Spezialität (g.t.S., engl. TSG) geschützt. Die Pizza Napoletana besteht danach aus folgenden Grundstoffen: Weichweizenmehl, Bierhefe, natürliches Trinkwasser, geschälte Tomaten und/oder kleine Frischtomaten (pomodorini), Meersalz oder Kochsalz, natives Olivenöl extra; weitere Zutaten, die bei ihrer Zubereitung verwendet werden können, sind Knoblauch und Oregano, frisches Basilikum und Mozzarella di Bufala Campana g.U. oder die Mozzarella g.t.S. Das Backen erfolgt ausschließlich in Holzöfen, in denen eine für die Zubereitung wesentliche Backtemperatur von 485 °C erreicht wird. Die Garzeit darf 60 bis 90 Sekunden nicht überschreiten.\n" +
            "Sie wird in zwei Varianten hergestellt:\n" +
            " Pizza Marinara, mit Tomaten, Knoblauch, Olivenöl und Oregano\n" +
            " Pizza Margherita, mit Tomaten, Mozzarella oder Fior di latte, Olivenöl und Basilikum.\n" +
            "\n" +
            "\n" +
            "== Pizza-Varianten ==\n" +
            "Außer der Pizza Napoletana gibt es in Italien noch weitere Pizzatypen mit regionaler Tradition. Dazu gehört die Pizza Romana, eine sehr dünne und knusprige Pizza, die meist auf dem Blech gebacken wird. Die Pizza Genovese dagegen ist eine dickere Pizza, die eher an die urtypische Form des Fladenbrotes erinnert und eine Weiterentwicklung der Focaccia ist. In ganz Italien verbreitet ist die Pizza al taglio (Pizza am Stück, bei uns eher bekannt als „Meterpizza“). Diese wird häufig nicht nur in Pizzerien, sondern auch beim Bäcker angeboten. Weit verbreitet in Italien ist auch die Pizza bianca (weiße Pizza); jegliche Pizza-Variation, die ohne Tomatensoße zubereitet wird.\n" +
            "\n" +
            "Klassische Varianten der neapolitanischen Pizza, die nicht unbedingt Tomaten und Käse enthalten müssen, sind:\n" +
            "\n" +
            " Pizza aglio e olio, mit Knoblauch, Olivenöl und Oregano\n" +
            " Pizza con cozze, mit Miesmuscheln, Knoblauch, Olivenöl und Petersilie\n" +
            " Pizza alle vongole, mit Venusmuscheln, Tomaten, Knoblauch, Olivenöl, Petersilie und Oregano\n" +
            " Margherita bianca, eine Margherita ohne Tomaten\n" +
            " Pizza Napoli, mit Tomaten, Mozzarella, Sardellen, Olivenöl und Oregano (auch zusätzlich mit Kapern und schwarzen Oliven)\n" +
            " Pizza Regina, mit Tomaten, Mozzarella, Champignons, Kochschinken und Oregano (auch zusätzlich mit schwarzen Oliven)Die Beläge können je nach Rezept variieren. Die neapolitanische Pizza ist dünn, mit etwas dickerem Rand und wird bei beinahe 500 °C in etwa zwei Minuten gebacken. Ohne Besteck kann sie „a libro“ (als Buch), also doppelt, zu einem Dreieck gefaltet, verzehrt werden.\n" +
            "Der Pizza nahe verwandt ist die Calzone (wörtlich: ‚Hose‘), bei der der Teigfladen vor dem Backen über dem Belag zusammengeklappt wird. Die traditionelle, üppige Füllung besteht aus Ricotta, rohem Schinken, Pilzen, Mozzarella, Parmesan und Oregano. Ursprünglich wurde die Calzone nicht im Ofen, sondern in einer Pfanne in Schmalz oder Öl gebacken, wie es auch heute noch als Pizza fritta in Neapel üblich ist.\n" +
            "Neben diesen Belagvarianten gibt es mittlerweile eine unüberschaubare Anzahl von weiteren, wie Pizza Salami, Funghi, Prosciutto, Tonno usw., die nach den Zutaten benannt wurden, mit denen sie belegt sind. Die Pizza quattro stagioni (Vier Jahreszeiten) ist in Vierteln beliebig verschieden belegt, Pizza Diavolo (Teufelspizza) mit Peperoni oder scharfer Wurst gewürzt, Pizza quattro formaggi mit vier Sorten Käse (zum Beispiel Mozzarella, Parmesan, Gorgonzola und Pecorino) oder Pizza frutti di mare mit Meeresfrüchten. Die Pizza Hawaii mit Kochschinken und Ananas ist möglicherweise US-amerikanischen Ursprungs.\n" +
            "In den USA sind zwei Typen weit verbreitet, „Chicago-style“ und „New York-style“ Pizza. Während die New Yorker Variante mit ihrem sehr dünnen Boden der italienischen Variante ähnelt, steht die Variante aus Chicago Kopf: Der Teig bildet eine Schüsselform, wird mit Mozzarellascheiben ausgelegt und mit weiteren Zutaten gefüllt. Zum Schluss wird das ganze von oben mit zerkleinerten Tomaten bestrichen und mit Parmesan und Oregano bestreut.\n" +
            "In Deutschland ist eine weitere Variante als „American Pizza“ populär, die sich vor allem durch einen dicken, luftigen Boden auszeichnet und u. a. durch die Restaurantkette Pizza Hut bekannt ist.\n" +
            "\n" +
            "\n" +
            "== Tiefkühlpizza ==\n" +
            "Vorgebackene und tiefgekühlte Pizza gehört zu den meistverkauften Fertiggerichten. In den 1960er Jahren in den Vereinigten Staaten entwickelt, gelangte sie über Italien nach Europa. 1966 wurde eine „Mini-Pizza“ samt dazugehörigem Aufbackofen vom italienischen Speiseeis-Hersteller Motta auf Messen in Frankfurt und München vorgestellt. Lieferbar war sie in Kartons à 17 Stück zu je 0,75 DM (inflationsbereinigt heute 1,45 Euro).\n" +
            "Tiefkühlpizza in großen Mengen stellte seit 1968 der Backwarenproduzent Romano Freddi aus Mantua her. Er entwickelte die Grundlagen zum Formen des Teigs, des Belegens und des Vorbackens für die industrielle Großproduktion. Neben der italienischen Firma Esselunga belieferte er auch Dr. Oetker, die Firma, die als erste die Tiefkühlpizza auf den deutschen Markt brachte. Ebenfalls 1968 folgten die ersten Hersteller in der Schweiz, 1970 die ersten in Deutschland, darunter auch Wagner, heute europaweit einer der Marktführer von Tiefkühlpizza mit einem Anteil von über 30 % in Deutschland sowie 26 % in Europa. Von Wagner wurde auch 1976 erstmals die tiefgekühlte „Steinofenpizza“ auf den Markt gebracht. Größter Hersteller in Europa ist die Freiberger Lebensmittel GmbH, die 1976 aus der „Pizza-Versandbäckerei“ entstand, einem kleinen Berliner Betrieb, den Ernst Freiberger, Sohn eines Eiscremeherstellers (Efa-Eiskrem), gegründet hatte.\n" +
            "\n" +
            "Der Verkauf von Tiefkühlpizza stieg in Deutschland rasch an: 1973 wurden 2.800 Tonnen hergestellt, zwei Jahre später 3.200. Der 1975 von der Gesellschaft für Konsumforschung für 1980 vorhergesagte Verbrauch von 8.400 Tonnen wurde mit über 23.000 Tonnen weit übertroffen, erreichte 2000 rund 160.000 Tonnen und 2007 fast 253.000 Tonnen – das entspricht rund 768 Millionen Stück. Im Folgejahr nahm der Umsatz von Tiefkühlpizza im Einzelhandel leicht ab und betrug rund 245.000 Tonnen. Die absatzstärksten Sorten des Herstellers Dr. Oetker waren 2017 nach Eigenangaben Salami, Speciale (Salami, Schinken, Champignons), Thunfisch, Hawaii (Schinken, Ananas) und Margherita. Dr. Oetker hat diese Sorten über Jahrzehnte vor allem in zwei Produktlinien vermarktet, die in den meisten Supermärkten omnipräsent sind: \n" +
            "Die &quot;Ristorante&quot;- Linie, die angeblich wie in einem italienischen Restaurant schmeckt, und &quot;die Ofenfrische&quot;, deren Teig nicht vorgebacken ist, sondern erst im Backofen des Endverbrauchers aufbäckt.\n" +
            "Die Herstellung von Tiefkühlpizza weicht in der Reihenfolge von der traditionellen Zubereitung ab. Die ausgestanzten Teigfladen werden zuerst mit Tomatensauce bestrichen und vorgebacken, dann nach dem Abkühlen mit den weiteren Zutaten belegt und schließlich schockgefroren. Der Teig enthält neben Weizenmehl und Hefe auch modifizierte Stärke und zusätzliche Triebmittel wie Natriumhydrogencarbonat, was das Backen ohne vorheriges langsames Auftauen ermöglicht. Sowohl beim Vorbacken als auch beim Fertigbacken im Haushaltsbackofen liegen die Temperaturen weit unter denen eines Pizzaofens. Von einigen Herstellern wird mittlerweile auch Tiefkühlpizza mit ungebackenem Boden angeboten.\n" +
            "\n" +
            "\n" +
            "== Literatur ==\n" +
            " Paul Trummer: Pizza globale. Ein Lieblingsessen erklärt die Weltwirtschaft. Econ, Berlin 2010, 336 S., ISBN 978-3-430-20100-1.\n" +
            " Dieter Richter: Die Pizza als Weltkulturerbe? Interview mit Antonio Pace, Präsident der Vereinigung „Verace Pizza Napoletana“. In: VOYAGE, Jahrbuch für Reise- und Tourismusforschung 2002, S. 89–95.\n" +
            "\n" +
            "\n" +
            "== Weblinks ==\n" +
            "\n" +
            " Geschichte der italienischen und amerikanischen Pizza, goccus.com\n" +
            " Associazione Verace Pizza Napoletana – Vereinigung für die originale neapolitanische Pizza (ital., engl.)\n" +
            "\n" +
            "\n" +
            "== Einzelnachweise ==</extract></page></pages></query></api>";

    @Test
    public void singletonTest() {
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();
        Assert.assertNotNull(loader);
    }

    @Test
    public void addApiResponseAsContentToArticleTest() {
        String title = "Pizza";
        Language lang = Language.DE;
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();
        try {
            WikiArticle article = loader.createArticleFromApiResponse(title, lang, STATIC_PIZZA_RESPONSE);
            Assert.assertNotNull(article);
            Assert.assertEquals(article.getTitle(), title);
            Assert.assertEquals(article.getLanguage(), lang);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO more detailed testing..
    }

    @Test
    public void loadArticleTest() throws IOException {
        String title = "Pizza";
        Language lang = Language.DE;
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();
        WikiArticle article = loader.loadArticle(title, lang);
        Assert.assertNotNull(article);
        Assert.assertEquals(article.getTitle(), title);
        Assert.assertEquals(article.getLanguage(), lang);
    }

    //TODO more testing! (if we need.. think of time)
}
