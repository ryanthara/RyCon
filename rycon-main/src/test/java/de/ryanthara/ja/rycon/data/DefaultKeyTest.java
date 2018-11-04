package de.ryanthara.ja.rycon.data;

import junit.framework.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testing the DefaultKey class...")
class DefaultKeyTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    @Test
    void getNumberOf() {
        Assert.assertTrue(DefaultKey.getNumberOf() > 0);
    }

    @Test
    void getValue() {
        Assert.assertEquals("DefaultKey{value='RyCON'}", DefaultKey.GENERATOR.toString());
    }

}
