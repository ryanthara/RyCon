package de.ryanthara.ja.rycon.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testing the PreferenceHandler class...")
class PreferenceHandlerIT {

    private static Logger logger;
    private static PreferenceHandler preferenceHandler;

    @BeforeAll
    static void initPreferenceHandler() {
        logger = LoggerFactory.getLogger(PreferenceHandlerIT.class.getName());
        preferenceHandler = new PreferenceHandler();
    }

    @Test
    void checkUserPrefPathExist() {
        final String dirBase = preferenceHandler.getUserPreference(PreferenceKey.DIR_BASE);
        assertEquals(dirBase, PreferenceHandler.checkUserPrefPathExist(dirBase));

        assertEquals(System.getenv().get("HOME"), PreferenceHandler.checkUserPrefPathExist(null));
        assertEquals(".", PreferenceHandler.checkUserPrefPathExist("."));
        assertEquals(System.getenv().get("HOME"), PreferenceHandler.checkUserPrefPathExist("./?öT2-"));
    }

    @Test
    void getKeys() {
        try {
            final int countStoredPreferenceKeys = preferenceHandler.getKeys().length;

            assertTrue(countStoredPreferenceKeys > 0, "Key length is: " + countStoredPreferenceKeys);
        } catch (BackingStoreException e) {
            logger.error("Can not get the length for stored user preference keys.", e.getCause());
            e.printStackTrace();
        }
    }

    @Test
    void getUserPreference() {
        assertEquals("RyCON", preferenceHandler.getUserPreference(PreferenceKey.GENERATOR));
        assertNotEquals("", preferenceHandler.getUserPreference(0));
    }

    @Test
    void isDefaultSettingsGenerated() {
        assertFalse(preferenceHandler.isDefaultSettingsGenerated());
    }

    @Test
    void setDefaultSettingsGenerated() {
        assertFalse(preferenceHandler.isDefaultSettingsGenerated());

        preferenceHandler.setDefaultSettingsGenerated(true);

        assertTrue(preferenceHandler.isDefaultSettingsGenerated());
    }

    @Test
    void testStoredPreferencesLength() {
        try {
            final int countDefaultPreferenceKeys = DefaultKey.getNumberOf();
            final int countStoredPreferenceKeys = preferenceHandler.getKeys().length;

            assertEquals(countStoredPreferenceKeys, countDefaultPreferenceKeys);
        } catch (BackingStoreException e) {
            logger.error("Can not get the length for the default preference keys and stored user preference keys.", e.getCause());
        }
    }

    private void showDifferentKeys() {
        if (DefaultKey.getNumberOf() != PreferenceKey.getNumberOf()) {
            List<String> defaultKeyList = new ArrayList<>(DefaultKey.getNumberOf());

            for (DefaultKey key : DefaultKey.values()) {
                defaultKeyList.add(key.name());
            }

            List<String> preferenceKeyList = new ArrayList<>(DefaultKey.getNumberOf());

            for (PreferenceKey key : PreferenceKey.values()) {
                preferenceKeyList.add(key.name());
            }

            if (defaultKeyList.size() > preferenceKeyList.size()) {
                defaultKeyList.removeAll(preferenceKeyList);

                System.err.println("The DefaultKey enumeration contains additionally this key(s):");

                defaultKeyList.forEach(System.out::println);
            } else {
                preferenceKeyList.removeAll(defaultKeyList);

                System.err.println("The PreferenceKey enumeration contains additionally this key(s):");

                preferenceKeyList.forEach(System.out::println);
            }
        }
    }

    /**
     * Ensure that there is a default key for every preference key.
     */
    @Test
    @DisplayName("Number of DefaultKey in enumeration equals number of PreferenceKey in enumeration")
    void compareKeyLengthInEnumerations() {
        showDifferentKeys();
        Assertions.assertEquals(DefaultKey.getNumberOf(), PreferenceKey.getNumberOf());
    }

}
