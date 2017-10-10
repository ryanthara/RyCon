package de.ryanthara.ja.rycon.data;

import de.ryanthara.ja.rycon.Main;
import org.junit.jupiter.api.Test;

import java.util.prefs.BackingStoreException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class PreferenceHandlerTest {
    @Test
    void checkUserPrefPathExist() {
        fail("not implemented yet");
    }

    @Test
    void getKeys() {
        fail("not implemented yet");
    }

    @Test
    void getUserPreference() {
        fail("not implemented yet");
    }

    @Test
    void isDefaultSettingsGenerated() {
        fail("not implemented yet");
    }

    @Test
    void setDefaultSettingsGenerated() {
        fail("not implemented yet");
    }

    @Test
    void preferenceChange() {
        fail("not implemented yet");
    }

    @Test
    void setUserPreference() {
        fail("not implemented yet");
    }

    @Test
    void testStoredPreferencesLength() {
        try {
            final int countStoredPreferenceKeys = Main.pref.getKeys().length;
            final int countDefaultPreferenceKeys = DefaultKeys.values().length;

            System.out.println(countDefaultPreferenceKeys);
            System.out.println(countStoredPreferenceKeys);

            assertEquals(countStoredPreferenceKeys, countDefaultPreferenceKeys);
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

}