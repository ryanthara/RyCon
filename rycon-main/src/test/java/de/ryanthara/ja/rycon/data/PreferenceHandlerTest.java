package de.ryanthara.ja.rycon.data;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.prefs.BackingStoreException;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceHandlerTest {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceHandlerTest.class.getName());

    private final PreferenceHandler preferenceHandler = new PreferenceHandler();

    @Test
    void checkUserPrefPathExist() {
        final String dirBase = preferenceHandler.getUserPreference(PreferenceKeys.DIR_BASE);
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
        assertEquals("RyCON", preferenceHandler.getUserPreference(PreferenceKeys.GENERATOR));
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
            final int countDefaultPreferenceKeys = DefaultKeys.values().length;
            final int countStoredPreferenceKeys = preferenceHandler.getKeys().length;

            assertEquals(countStoredPreferenceKeys, countDefaultPreferenceKeys);
        } catch (BackingStoreException e) {
            logger.error("Can not get the length for the default preference keys and stored user preference keys.", e.getCause());
        }
    }

} // end of PreferenceHandlerTest