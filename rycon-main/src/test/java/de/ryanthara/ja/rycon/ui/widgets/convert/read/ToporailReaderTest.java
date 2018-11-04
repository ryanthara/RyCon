package de.ryanthara.ja.rycon.ui.widgets.convert.read;

import de.ryanthara.ja.rycon.nio.FileFormat;
import junit.framework.Assert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@DisplayName("Testing the ToporailReader class...")
class ToporailReaderTest {

    @Test
    void getCsv() {
        ToporailReader reader = new ToporailReader(null, FileFormat.MEP);

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