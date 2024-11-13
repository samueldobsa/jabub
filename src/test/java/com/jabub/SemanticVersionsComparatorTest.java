package com.jabub;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SemanticVersionsComparatorTest {

    @Test
    void compare() {

        Path file1 = Path.of("v1.2.3_script");
        Path file2 = Path.of("v1.2.4__script");
        Path file3 = Path.of("v1.24.3__script");
        Path file4 = Path.of("v2.2.3_script");
        Path file5 = Path.of("v12.2.3123_script");
        Path file6 = Path.of("v12.2.3124_script");

        List<Path> files = new ArrayList<>();
        files.add(file5);
        files.add(file2);
        files.add(file3);
        files.add(file4);
        files.add(file1);
        files.add(file6);

        files.sort(new SemanticVersionsComparator());

        assertEquals(file1, files.getFirst());
        assertEquals(file2, files.get(1));
        assertEquals(file3, files.get(2));
        assertEquals(file4, files.get(3));
        assertEquals(file5, files.get(4));
        assertEquals(file6, files.get(5));
    }
}