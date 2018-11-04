package de.ryanthara.ja.rycon.ui.widgets.convert.read;

import junit.framework.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Testing the ZeissReader class...")
class ZeissReaderTest {

    @Test
    void getCsv() {
        ZeissReader reader = new ZeissReader(null);

        List<String[]> when = reader.getCsv();

        Assert.assertEquals(new ArrayList<>(List.of()), when);
    }

    @Test
    void getLines() {
    }

    @Test
    void readFile() {
    }
}