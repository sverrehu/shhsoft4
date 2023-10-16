package no.shhsoft.json.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JsonUtilsTest {

    @Test
    public void shouldGenerateUTCTimestamp() {
        final String timestamp = JsonUtils.toTimestamp(new Date());
        Assert.assertTrue("Timestamp " + timestamp + " does not end in Z.", timestamp.endsWith("Z"));
    }

    @Test
    public void shouldEncodeAndDecodeToTheSame() {
        final Date now = new Date();
        final Date then = JsonUtils.fromTimestamp(JsonUtils.toTimestamp(now));
        Assert.assertEquals(now.getTime(), then.getTime());
    }

}
