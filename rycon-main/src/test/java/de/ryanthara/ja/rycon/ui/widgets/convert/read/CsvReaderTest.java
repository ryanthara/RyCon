package de.ryanthara.ja.rycon.ui.widgets.convert.read;

import junit.framework.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Testing the CsvReader class...")
class CsvReaderTest {

    @Test
    void getCsv() {

    }

    @Test
    void getLines() {
        CsvReader reader = new CsvReader(null, new ReadParameter(false, false, false));

        List<String> when = reader.getLines();

        Assert.assertEquals(new ArrayList<>(List.of()), when);
    }

    @Test
    void readFile() {
        CsvReader reader = new CsvReader(null, new ReadParameter(false, false, false));

        Executable when = () -> reader.readFile(null);

        Assertions.assertThrows(NullPointerException.class, when);
    }
}