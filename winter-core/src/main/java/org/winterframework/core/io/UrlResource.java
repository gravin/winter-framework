package org.winterframework.core.io;

import org.winterframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

public class UrlResource implements Resource {

    /**
     * Original URI, if available; used for URI and File access.
     */
    private final URI uri;

    /**
     * Original URL, used for actual access.
     */
    private final URL url;

    /**
     * Cleaned URL (with normalized path), used for comparisons.
     */
    private final URL cleanedUrl;


    /**
     * Create a new UrlResource based on the given URL object.
     * @param url a URL
     */
    public UrlResource(URL url) {
        Assert.notNull(url, "URL must not be null");
        this.url = url;
        this.cleanedUrl = this.url;
        this.uri = null;
    }


    /**
     * This implementation opens an InputStream for the given URL.
     * It sets the "UseCaches" flag to {@code false},
     * mainly to avoid jar file locking on Windows.
     * @see java.net.URL#openConnection()
     * @see java.net.URLConnection#setUseCaches(boolean)
     * @see java.net.URLConnection#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        URLConnection con = this.url.openConnection();
        try {
            return con.getInputStream();
        }
        catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw ex;
        }
    }

    @Override
    public String getFilename() {
        return new File(this.url.getFile()).getName();
    }
}
