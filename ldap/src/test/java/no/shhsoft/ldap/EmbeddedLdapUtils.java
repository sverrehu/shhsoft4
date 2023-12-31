package no.shhsoft.ldap;

import org.zapodot.junit.ldap.EmbeddedLdapRule;
import org.zapodot.junit.ldap.EmbeddedLdapRuleBuilder;

import javax.naming.ldap.LdapContext;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class EmbeddedLdapUtils {

    static final boolean USE_LDAPS = false;
    static final String LDAP_BASE_DN = "dc=example,dc=com";
    static final String LDAP_ADMIN_DN = "cn=admin," + LDAP_BASE_DN;
    static final char[] LDAP_ADMIN_PASSWORD = "admin".toCharArray();
    static final String NON_EXISTING_SUBTREE = "ou=non-existing";
    static final String NON_EXISTING_ID = "non-existing";
    static final String NON_EXISTING_RDN = "cn=" + NON_EXISTING_ID + "," + NON_EXISTING_SUBTREE;
    static final String EXISTING_USERNAME = "sverrehu";
    static final String EXISTING_RDN = "cn=" + EXISTING_USERNAME + ",ou=People";
    static final char[] EXISTING_USER_PASSWORD = "secret".toCharArray();
    static final String USERNAME_TO_DN_FORMAT = "cn=%s,ou=People," + LDAP_BASE_DN;

    private EmbeddedLdapUtils() {
    }

    static EmbeddedLdapRule createEmbeddedLdapRule() {
        return EmbeddedLdapRuleBuilder.newInstance().usingDomainDsn(LDAP_BASE_DN).importingLdifs("ldap/bootstrap.ldif").build();
    }

    static LdapContext getLdapContext(final EmbeddedLdapRule ldapRule) {
        return getLdapConnectionSpec(ldapRule).getUnpooledContext();
    }

    static LdapConnectionSpec getLdapConnectionSpec(final EmbeddedLdapRule ldapRule) {
        return new LdapConnectionSpec("localhost", ldapRule.embeddedServerPort(), USE_LDAPS, LDAP_BASE_DN);
    }

}
