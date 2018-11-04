package de.ryanthara.ja.rycon.util;

import de.ryanthara.ja.rycon.core.converter.Separator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testing the StringUtils class...")
class StringUtilsTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    private final String emptyMessage = "§§";
    private final String wrongMessage = "singular message§plural message";

    private final String whiteSpaceMessage = " §§ ";
    private final String singularPluralMessage = "singular message§§plural message";

    @Test
    void fillWithSpacesFromBeginning() {
        assertThrows(NullPointerException.class, () -> StringUtils.fillWithSpacesFromBeginning(null, 0));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.fillWithSpacesFromBeginning("", 0));

        assertEquals("           RyCON", StringUtils.fillWithSpacesFromBeginning("RyCON", 16));
        assertEquals("Ry", StringUtils.fillWithSpacesFromBeginning("RyCON", 2));
    }

    @Test
    void fillWithZerosFromBeginning() {
        assertThrows(NullPointerException.class, () -> StringUtils.fillWithZerosFromBeginning(null, 0));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.fillWithZerosFromBeginning("", 0));

        assertEquals("00000000000RyCON", StringUtils.fillWithZerosFromBeginning("RyCON", 16));
    }

    @Test
    void getPluralMessage() {
        assertThrows(NullPointerException.class, () -> StringUtils.getPluralMessage(null));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.getPluralMessage(emptyMessage));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.getPluralMessage(wrongMessage));

        assertEquals(Separator.WHITESPACE.getSign(), StringUtils.getPluralMessage(whiteSpaceMessage));
        assertEquals("plural message", StringUtils.getPluralMessage(singularPluralMessage));
    }

    @Test
    void getSingularMessage() {
        assertThrows(NullPointerException.class, () -> StringUtils.getSingularMessage(null));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.getSingularMessage(emptyMessage));
        assertThrows(IllegalArgumentException.class, () -> StringUtils.getSingularMessage(wrongMessage));

        assertEquals(Separator.WHITESPACE.getSign(), StringUtils.getPluralMessage(whiteSpaceMessage));
        assertEquals("singular message", StringUtils.getSingularMessage(singularPluralMessage));
    }

}