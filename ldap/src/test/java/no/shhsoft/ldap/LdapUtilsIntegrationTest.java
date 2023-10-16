package no.shhsoft.ldap;

import no.shhsoft.utils.ExceptionUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.zapodot.junit.ldap.EmbeddedLdapRule;

import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.ldap.LdapContext;

import static org.junit.Assert.*;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class LdapUtilsIntegrationTest {

    @ClassRule
    public static final EmbeddedLdapRule LDAP_RULE = EmbeddedLdapUtils.createEmbeddedLdapRule();

    private void removeNonStuff(final LdapContext ldap) {
        try {
            LdapUtils.remove(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN);
        } catch (final UncheckedNamingException e) {
            assertTrue(ExceptionUtils.containsCause(e, NameNotFoundException.class));
        }
        try {
            LdapUtils.remove(ldap, EmbeddedLdapUtils.NON_EXISTING_SUBTREE);
        } catch (final UncheckedNamingException e) {
            assertTrue(ExceptionUtils.containsCause(e, NameNotFoundException.class));
        }
        assertNull(LdapUtils.findByRdn(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN));
    }

    private String getSingleValue(final Attributes attributes, final String name) {
        try {
            final Attribute attribute = attributes.get(name);
            if (attribute != null) {
                return attribute.get().toString();
            }
        } catch (final NamingException e) {
            fail("Error fetching attribute value: " + e.getMessage());
        }
        return null;
    }

    @Test
    public void testStore() {
        final LdapContext ldap = EmbeddedLdapUtils.getLdapContext(LDAP_RULE);
        removeNonStuff(ldap);
        try {
            Attributes attributes = LdapUtils.findByRdn(ldap, EmbeddedLdapUtils.EXISTING_RDN);
            attributes.remove("cn");
            attributes.put("cn", EmbeddedLdapUtils.NON_EXISTING_ID);
            attributes.remove("sn");
            attributes.put("sn", "sn");
            attributes.remove("description");
            attributes.put("description", "description");
            LdapUtils.store(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN, attributes, true);

            attributes = LdapUtils.findByRdn(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN);
            assertEquals("sn", getSingleValue(attributes, "sn"));
            assertEquals("description", getSingleValue(attributes, "description"));
            attributes.remove("description");
            attributes.put("description", "description2");
            LdapUtils.store(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN, attributes, true);

            attributes = LdapUtils.findByRdn(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN);
            assertEquals("sn", getSingleValue(attributes, "sn"));
            assertEquals("description2", getSingleValue(attributes, "description"));
            attributes.remove("description");
            attributes.put("description", "description2");
            LdapUtils.store(ldap, EmbeddedLdapUtils.NON_EXISTING_RDN, attributes, true);
        } finally {
            removeNonStuff(ldap);
        }
    }

}
