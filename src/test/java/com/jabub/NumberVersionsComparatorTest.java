package com.jabub;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NumberVersionsComparatorTest {

    @Test
    void compare() {
        Path file1 = Path.of("1_script");
        Path file2 = Path.of("1.1_script");
        Path file3 = Path.of("2_script");
        Path file4 = Path.of("30_script");
        Path file5 = Path.of("400.3332_script");

        List<Path> files = new ArrayList<>();
        files.add(file5);
        files.add(file2);
        files.add(file3);
        files.add(file4);
        files.add(file1);

        files.sort(new NumberVersionsComparator());

        assertEquals(file1, files.getFirst());
        assertEquals(file2, files.get(1));
        assertEquals(file3, files.get(2));
        assertEquals(file4, files.get(3));
        assertEquals(file5, files.get(4));
    }
}