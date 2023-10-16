package no.shhsoft.security;

import no.shhsoft.utils.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HashedPasswordUtilsTest {

    private static final char[] UTF_PASSWORD = "\u30de\u30e4".toCharArray();
    private static final char[] NON_UTF_PASSWORD = "\u00de\u00e4".toCharArray();

    @Test
    public void shouldBeWithinTheLengthSetAsideForSomeDatabasesUsingThisShit() {
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword("foo".toCharArray());
        assertTrue(hashedPassword.length() <= 160);
    }

    @Test
    public void shouldCreateStuffWithThreePartsAndAllLengthsOk() {
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword("foo".toCharArray());
        assertEquals(HashedPasswordUtils.HASHED_PASSWORD_STRING_LENGTH, hashedPassword.length());
        final String[] parts = StringUtils.split(hashedPassword, HashedPasswordUtils.SEPARATORS, true, false);
        assertEquals(3, parts.length);
        assertEquals(HashedPasswordUtils.PREFIX, parts[0]);
        assertEquals(40, parts[2].length()); /* SHA-1 to hex at the moment. */
    }

    @Test
    public void shouldVerifyPassword() {
        final char[] password = "foobar".toCharArray();
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword(password);
        assertTrue(HashedPasswordUtils.isMatch(password, hashedPassword));
    }

    @Test
    public void shouldVerifyPasswordUtf8() {
        final char[] password = UTF_PASSWORD;
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword(password);
        assertTrue(HashedPasswordUtils.isMatch(password, hashedPassword));
    }

    @Test
    public void shouldNotVerifyPassword() {
        final char[] password = "foobar".toCharArray();
        final char[] wrongPassword = "fobar".toCharArray();
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword(password);
        assertFalse(HashedPasswordUtils.isMatch(wrongPassword, hashedPassword));
    }

    @Test
    public void shouldNotVerify8BitOnly() {
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword(UTF_PASSWORD);
        assertFalse(HashedPasswordUtils.isMatch(NON_UTF_PASSWORD, hashedPassword));
    }

    @Test
    public void shouldNotVerify8BitOnlyOtherWayAround() {
        final String hashedPassword = HashedPasswordUtils.generateHashedPassword(NON_UTF_PASSWORD);
        assertFalse(HashedPasswordUtils.isMatch(UTF_PASSWORD, hashedPassword));
    }

}
