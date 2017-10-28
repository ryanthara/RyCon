package de.ryanthara.ja.rycon.data;

import org.junit.jupiter.api.Test;

import java.util.prefs.BackingStoreException;

import static org.junit.jupiter.api.Assertions.*;

class PreferenceHandlerTest {

    private final PreferenceHandler preferenceHandler = new PreferenceHandler();

    @Test
    void checkUserPrefPathExist() {
        final String dirBase = preferenceHandler.getUserPreference(PreferenceKeys.DIR_BASE);
        assertEquals(dirBase, PreferenceHandler.checkUserPrefPathExist(dirBase));

        assertTrue(System.getenv().get("HOME") == PreferenceHandler.checkUserPrefPathExist(null));
        assertEquals(".", PreferenceHandler.checkUserPrefPathExist("."));
        assertTrue(System.getenv().get("HOME") == PreferenceHandler.checkUserPrefPathExist("./?öT2-"));
    }

    @Test
    void getKeys() {
        try {
            final int countStoredPreferenceKeys = preferenceHandler.getKeys().length;

            assertTrue(countStoredPreferenceKeys > 0, "Key length is: " + countStoredPreferenceKeys);
        } catch (BackingStoreException e) {
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
            e.printStackTrace();
        }
    }

} // end of PreferenceHandlerTest