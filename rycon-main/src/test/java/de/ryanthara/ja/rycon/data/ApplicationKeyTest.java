package de.ryanthara.ja.rycon.data;

import junit.framework.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

@DisplayName("Testing the ApplicationKey class...")
class ApplicationKeyTest {

    @Test
    void getURI() throws URISyntaxException {
        Assert.assertNotSame(Optional.empty(), ApplicationKey.JAVA_WEBSITE.getURI());
        Assert.assertEquals(Optional.of(new URI("https://java.com/")), ApplicationKey.JAVA_WEBSITE.getURI());
    }

    @Test
    void getValue() {
        Assert.assertNotNull(ApplicationKey.RyCON_WEBSITE.getValue());
    }
}