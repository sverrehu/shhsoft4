package no.shhsoft.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FileNameUtilsTest {

    @Test
    public void shouldRemoveSpacesCorrectly() {
        Assert.assertEquals("HedmarkOgOppland", FileNameUtils.toFileNameWithoutPath("Hedmark og Oppland"));
    }

    @Test
    public void shouldRemoveSpacesCorrectlyForNorwegianCharacters() {
        Assert.assertEquals("AeAa", FileNameUtils.toFileNameWithoutPath("\u00e6 \u00e5"));
    }

    @Test
    public void shouldStripTrailingPathSeparators() {
        Assert.assertNull(FileNameUtils.stripTrailingPathSeparators(null));
        Assert.assertEquals("/", FileNameUtils.stripTrailingPathSeparators("/"));
        Assert.assertEquals("/", FileNameUtils.stripTrailingPathSeparators("//"));
        Assert.assertEquals(".", FileNameUtils.stripTrailingPathSeparators("./"));
        Assert.assertEquals("/foo", FileNameUtils.stripTrailingPathSeparators("/foo///"));
    }

    @Test
    public void shouldGetDirName() {
        Assert.assertNull(FileNameUtils.getDirName(null));
        Assert.assertEquals("", FileNameUtils.getDirName(""));
        Assert.assertEquals("/", FileNameUtils.getDirName("/"));
        Assert.assertEquals("/", FileNameUtils.getDirName("//"));
        Assert.assertEquals("\\", FileNameUtils.getDirName("\\"));
        Assert.assertEquals(".", FileNameUtils.getDirName("./foo"));
        Assert.assertEquals("foo", FileNameUtils.getDirName("foo/bar"));
        Assert.assertEquals(".//foo", FileNameUtils.getDirName(".//foo//bar//"));
    }

    @Test
    public void shouldGetBaseName() {
        Assert.assertNull(FileNameUtils.getBaseName(null));
        Assert.assertEquals("", FileNameUtils.getBaseName(""));
        Assert.assertEquals("/", FileNameUtils.getBaseName("/"));
        Assert.assertEquals("/", FileNameUtils.getBaseName("//"));
        Assert.assertEquals(".", FileNameUtils.getBaseName("./"));
        Assert.assertEquals("foo", FileNameUtils.getBaseName("foo//"));
        Assert.assertEquals("bar", FileNameUtils.getBaseName("foo/bar"));
        Assert.assertEquals("bar", FileNameUtils.getBaseName("foo/bar/"));
    }

    @Test
    public void shouldGetExtension() {
        Assert.assertNull(FileNameUtils.getExtension(null));
        Assert.assertNull(FileNameUtils.getExtension("foo"));
        Assert.assertNull(FileNameUtils.getExtension("foo.bar/gazonk"));
        Assert.assertEquals("bar", FileNameUtils.getExtension("foo.bar"));
        Assert.assertEquals("bar", FileNameUtils.getExtension("foo.bar/"));
        Assert.assertEquals("bar", FileNameUtils.getExtension("foo..bar"));
        Assert.assertEquals("bar", FileNameUtils.getExtension(".bar"));
        Assert.assertEquals("bar", FileNameUtils.getExtension("/xyzzy.foobar/foo.bar"));
    }

    @Test
    public void shouldAddOrReplaceExtension() {
        Assert.assertNull(FileNameUtils.addOrReplaceExtension(null, "foo"));
        Assert.assertEquals("foo.bar", FileNameUtils.addOrReplaceExtension("foo", "bar"));
        Assert.assertEquals("foo", FileNameUtils.addOrReplaceExtension("foo.bar", null));
        Assert.assertEquals("foo", FileNameUtils.addOrReplaceExtension("foo.bar", ""));
        Assert.assertEquals("foo.bar", FileNameUtils.addOrReplaceExtension("foo", ".bar"));
        Assert.assertEquals("foo.bar", FileNameUtils.addOrReplaceExtension("foo.gazonk", "bar"));
        Assert.assertEquals("foo..bar", FileNameUtils.addOrReplaceExtension("foo..gazonk", "bar"));
        Assert.assertEquals("foo.bar", FileNameUtils.addOrReplaceExtension("foo.gazonk", ".bar"));
        Assert.assertEquals("/xyzzy/foo.bar", FileNameUtils.addOrReplaceExtension("/xyzzy/foo", ".bar"));
        Assert.assertEquals("/xyzzy/foo.bar", FileNameUtils.addOrReplaceExtension("/xyzzy/foo//", ".bar"));
        Assert.assertEquals("/xyzzy.gazonk/foo.bar", FileNameUtils.addOrReplaceExtension("/xyzzy.gazonk/foo", ".bar"));
    }

}
