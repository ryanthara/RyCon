package de.ryanthara.ja.rycon.check;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PathCheckTest {

    private final String currentString = ".";
    private final String emptyString = "";
    private final String nullString = null;

    private final Path currentPath = Paths.get(currentString);
    private final Path emptyPath = Paths.get(emptyString);
    private final Path nullPath = null;

    @Test
    void directoryExistsPath() {
        assertTrue(PathCheck.directoryExists(currentPath));
        assertTrue(PathCheck.directoryExists(emptyPath));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.directoryExists(nullPath));
    }

    @Test
    void directoryExistsString() {
        assertTrue(PathCheck.directoryExists(currentString));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.directoryExists(emptyString));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.directoryExists(nullString));
    }

    @Test
    void fileExistsPath() {
        assertFalse(PathCheck.fileExists(currentPath));
        assertFalse(PathCheck.fileExists(emptyPath));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.fileExists(nullPath));
    }

    @Test
    void fileExistsString() {
        assertFalse(PathCheck.fileExists(currentString));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.fileExists(emptyString));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.fileExists(nullString));
    }

    @Test
    void getValidFiles() {
        String[] exampleString = new String[]{"*.*", "*.txt", "*.java"};
        String[] nullString = null;

        Path[] currentPaths = new Path[]{Paths.get(".")};
        Path[] emptyPaths = new Path[0];
        Path[] nullPaths = null;

        assertEquals(0, PathCheck.getValidFiles(currentPaths, exampleString).length);
        assertEquals(0, PathCheck.getValidFiles(currentPaths, nullString).length);

        assertEquals(0, PathCheck.getValidFiles(emptyPaths, exampleString).length);
        assertEquals(0, PathCheck.getValidFiles(emptyPaths, nullString).length);

        assertThrows(IllegalArgumentException.class, () -> PathCheck.getValidFiles(nullPaths, exampleString));
    }

    @Test
    void isValid() {
        assertTrue(PathCheck.isValid(currentPath));
        assertTrue(PathCheck.isValid(emptyPath));
        assertThrows(IllegalArgumentException.class, () -> PathCheck.fileExists(nullPath));
    }

} // end of PathCheckTest