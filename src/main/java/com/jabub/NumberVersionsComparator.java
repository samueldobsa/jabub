package com.jabub;

import java.nio.file.Path;
import java.util.Comparator;

import static com.jabub.Utils.extractVersion;
import static java.lang.Double.parseDouble;

public class NumberVersionsComparator implements Comparator<Path> {

    @Override
    public int compare(Path o1, Path o2) {
        double version1 = parseDouble(extractVersion(o1));
        double version2 = parseDouble(extractVersion(o2));

        return Double.compare(version1, version2);
    }

    boolean isHigher(Path script, String version) {
        return this.compare(script, Path.of(version + "_")) > 0;
    }
}
