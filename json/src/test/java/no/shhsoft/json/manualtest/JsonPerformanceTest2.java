package no.shhsoft.json.manualtest;

import no.shhsoft.json.JsonParser;
import no.shhsoft.json.impl.parser.JsonParserImpl;
import no.shhsoft.json.model.JsonContainer;
import no.shhsoft.utils.IoUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonPerformanceTest2 {

    private JsonPerformanceTest2() {
    }

    public static void main(final String[] args) {
        final String json = new String(IoUtils.readResource("twitter-public-timeline.json"));
        long t = System.currentTimeMillis();
        for (int q = 500; q >= 0; q--) {
            final JsonParser decoder = new JsonParserImpl();
            @SuppressWarnings("unused")
            final JsonContainer container = decoder.parse(json);
        }
        t = System.currentTimeMillis() - t;
        System.out.println("Done in " + t + " ms");
    }

}
