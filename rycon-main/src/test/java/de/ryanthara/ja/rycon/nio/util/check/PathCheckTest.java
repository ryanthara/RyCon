package de.ryanthara.ja.rycon.nio.util.check;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PathCheckTest {
    /*
     * Consider a tests consist of three core parts: given, when and then.
     */

    private static final String currentString = ".";
    private static final String emptyString = "";
    private final String nullString = null;

    private final Path currentPath = Paths.get(currentString);
    private final Path emptyPath = Paths.get(emptyString);
    private final Path nullPath = null;

    @Test
    void directoryExistsPath() {
        assertTrue(PathCheck.directoryExists(currentPath));
        assertTrue(PathCheck.directoryExists(emptyPath));
        assertThrows(NullPointerException.class, () -> PathCheck.directoryExists(nullPath));
    }

    @Test
    void directoryExistsString() {
        assertTrue(PathCheck.directoryExists(currentString));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.directoryExists(emptyString));
        assertThrows(NullPointerException.class, () -> PathCheck.directoryExists(nullString));
    }

    @Test
    void fileExistsPath() {
        assertFalse(PathCheck.fileExists(currentPath));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.fileExists(emptyPath));
        assertThrows(NullPointerException.class, () -> PathCheck.fileExists(nullPath));
    }

    @Test
    void fileExistsString() {
        assertFalse(PathCheck.fileExists(currentString));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.fileExists(emptyString));
        assertThrows(NullPointerException.class, () -> PathCheck.fileExists(nullString));
    }

    @Test
    void getValidFiles() {
        String[] exampleString = new String[]{"*.*", "*.txt", "*.java"};

        Path[] currentPaths = new Path[]{Paths.get(".")};
        Path[] emptyPaths = new Path[0];

        assertEquals(0, PathCheck.getValidFiles(currentPaths, exampleString).length);
        assertEquals(0, PathCheck.getValidFiles(currentPaths, null).length);

        assertEquals(0, PathCheck.getValidFiles(emptyPaths, exampleString).length);
        assertEquals(0, PathCheck.getValidFiles(emptyPaths, null).length);

        assertThrows(NullPointerException.class, () -> PathCheck.getValidFiles(null, exampleString));
    }

    @Test
    void isValid() {
        assertTrue(PathCheck.isValid(currentPath));
        assertTrue(PathCheck.isValid(emptyPath));
        assertThrows(NullPointerException.class, () -> PathCheck.fileExists(nullPath));
    }

}
