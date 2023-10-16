package no.shhsoft.resourcetrav;

import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Not thread safe.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ResourceTraverser {

    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private Resource currentResource;

    private static String getPath(final File file) {
        return file.getPath().replace('\\', '/').replace("%20", " ");
    }

    private static String getPath(final URL url) {
        String slashed = url.getPath().replace('\\', '/');
        if ("\\".equals(System.getProperty("file.separator")) && slashed.length() > 3
            && slashed.charAt(0) == '/' && slashed.charAt(2) == ':') {
            slashed = slashed.substring(1);
        }
        return slashed;
    }

    private boolean traverseJarBasedResource(final ResourceHandler resourceHandler,
                                             final URL directoryResource, final String directory) {
        String path = getPath(directoryResource);
        final int idx = path.indexOf('!');
        if (idx >= 0) {
            path = path.substring(0, idx);
        }
        if (path.startsWith("file:")) {
            path = path.substring("file:".length());
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(path);
            final JarInputStream in = new JarInputStream(new BufferedInputStream(inputStream, 32768));
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                final String entryName = entry.getName();
                final String fixedEntryName = entryName.replace('\\', '/');
                if (fixedEntryName.startsWith(directory)) {
                    final URL url = new URL("jar:file:" + path + "!/" + entryName);
                    final Resource resource = new Resource(this, Resource.Type.JAR, url, fixedEntryName);
                    if (!resourceHandler.handle(resource)) {
                        return false;
                    }
                }
                in.closeEntry();
            }
            return true;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        } finally {
            IoUtils.closeSilently(inputStream);
        }
    }

    private boolean recurseFileBasedResource(final ResourceHandler resourceHandler, final File dir,
                                             final String prefixToStrip) {
        final String[] directoryEntries = dir.list();
        if (directoryEntries == null) {
            return true;
        }
        for (final String directoryEntry : directoryEntries) {
            final File fileOrDirectory = new File(dir, directoryEntry);
            if (fileOrDirectory.isDirectory()) {
                if (!recurseFileBasedResource(resourceHandler, fileOrDirectory, prefixToStrip)) {
                    return false;
                }
            } else {
                final String path = getPath(fileOrDirectory);
                if (!path.startsWith(prefixToStrip)) {
                    throw new RuntimeException("Expected `" + path + "' to start with `"
                                               + prefixToStrip + "', but it didn't");
                }
                final String name = path.substring(prefixToStrip.length());
                final URL url;
                try {
                    url = new URL("file:" + path);
                } catch (final MalformedURLException e) {
                    throw new UncheckedIoException(e);
                }
                final Resource resource = new Resource(this, Resource.Type.FILE, url, name);
                if (!resourceHandler.handle(resource)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean traverseFileBasedResource(final ResourceHandler resourceHandler,
                                              final URL directoryResource,
                                              final String searchDirectory) {
        final String path = getPath(directoryResource);
        if (!path.endsWith(searchDirectory)) {
            throw new RuntimeException("Expected `" + path + "' to end with `"
                                       + searchDirectory + "', but it didn't");
        }
        final String prefixToStrip = path.substring(0, path.length() - searchDirectory.length());
        final File dir = new File(path);
        if (!dir.isDirectory()) {
            return true;
        }
        return recurseFileBasedResource(resourceHandler, dir, prefixToStrip);
    }

    private boolean traverse(final ResourceHandler resourceHandler, final URL directoryResource,
                             final String directory) {
        final String protocol = directoryResource.getProtocol();
        if ("file".equals(protocol)) {
            return traverseFileBasedResource(resourceHandler, directoryResource, directory);
        }
        if ("jar".equals(protocol) || "zip".equals(protocol)) {
            return traverseJarBasedResource(resourceHandler, directoryResource, directory);
        }
        throw new RuntimeException("Unsupported resource protocol: `" + protocol
                                   + "' in `" + directoryResource.toString() + "'");
    }

    byte[] getContent(final Resource resource) {
        if (resource != currentResource) {
            throw new RuntimeException("Content can only be retreived for the current resource.");
        }
        // TODO Auto-generated method stub
        throw new RuntimeException("Not implemented");
    }

    public void traverse(final ResourceHandler resourceHandler, final String... directories) {
        for (String directory : directories) {
            directory = directory.replace('\\', '/');
            while (directory.length() > 0 && directory.charAt(0) == '/') {
                directory = directory.substring(1);
            }
            while (directory.length() > 0 && directory.charAt(directory.length() - 1) == '/') {
                directory = directory.substring(0, directory.length() - 2);
            }
            directory += "/";
            /* directory now has no leading slash, and exactly one trailing slash. */
            try {
                final Enumeration<URL> directoryResources = classLoader.getResources(directory);
                while (directoryResources.hasMoreElements()) {
                    final URL directoryResource = directoryResources.nextElement();
                    if (!traverse(resourceHandler, directoryResource, directory)) {
                        break;
                    }
                }
            } catch (final IOException e) {
                throw new UncheckedIoException(e);
            }
        }
    }

}
