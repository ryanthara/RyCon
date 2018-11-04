package de.ryanthara.ja.rycon.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Testing the NumberFormatter class...")
class NumberFormatterTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    @Test
    void nullShouldThrowNullPointerException() {
        assertThrows(NullPointerException.class, () -> NumberFormatter.fillDecimalPlaces(null, 0));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3.141592"})
    void negativeDecimalLengthShouldThrowNullPointerException(String pi) {
        assertThrows(IllegalArgumentException.class, () -> NumberFormatter.fillDecimalPlaces(pi, -1));
    }

    @ParameterizedTest
    @ValueSource(strings = {"3.141592"})
    void fillDecimalPlace(String pi) {
        assertEquals("-∞", NumberFormatter.fillDecimalPlaces("DD", 4));
        assertEquals("3.", NumberFormatter.fillDecimalPlaces(pi, 0));
        assertEquals("3.14159200", NumberFormatter.fillDecimalPlaces(pi, 8));
    }

}