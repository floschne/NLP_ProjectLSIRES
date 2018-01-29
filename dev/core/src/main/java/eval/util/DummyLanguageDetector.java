package eval.util;

import data.input.Language;
import query_generation.Query;

import java.util.Random;

public class DummyLanguageDetector {

    private Random random;

    public DummyLanguageDetector(int seed) {
        random = new Random(seed);
    }

    public DummyLanguageDetector() {
        random = new Random();
    }

    public Language detectLanguageOfQuery(Query query) {
        switch (random.nextInt(3)) {
            case 0:
                return Language.DE;
            case 1:
                return Language.EN;
            case 2:
                return Language.ES;
            default:
                return null;
        }
    }

}
