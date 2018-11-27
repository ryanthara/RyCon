package de.ryanthara.ja.rycon.data;

import junit.framework.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Testing PreferenceKey enumeration...")
class PreferencesKeyTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    @Test
    void getNumberOf() {
        Assert.assertTrue(PreferenceKey.getNumberOf() > 0);
    }

    @Test
    void name() {
        Assert.assertEquals("GENERATOR", PreferenceKey.GENERATOR.name());
    }

}