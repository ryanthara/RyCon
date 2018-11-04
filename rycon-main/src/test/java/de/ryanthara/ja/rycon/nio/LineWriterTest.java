package de.ryanthara.ja.rycon.nio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@DisplayName("Testing the LineWriter class...")
class LineWriterTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    @Test
    void getCountWrittenLines() {
        // Given
        // When
        // Then
    }

    @Test
    void writeFile() {
        // Given
        LineWriter nullLineWriter = new LineWriter(null);

        // When
        Executable when = () -> nullLineWriter.writeFile(null);

        // Then
        Assertions.assertThrows(NullPointerException.class, when);
    }

}