package com.jabub;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static com.jabub.Utils.extractVersion;
import static java.lang.Math.max;
import static java.util.Arrays.stream;

public class SemanticVersionsComparator implements Comparator<Path> {

    @Override
    public int compare(Path o1, Path o2) {
        String version1 = extractVersion(o1);
        String version2 = extractVersion(o2);

        List<Integer> version1Components = stream(version1.split("\\."))
                .map(Integer::parseInt)
                .toList();
        List<Integer> version2Components = stream(version2.split("\\."))
                .map(Integer::parseInt)
                .toList();

        int maxLength = max(version1Components.size(), version2Components.size());

        for (int i = 0; i < maxLength; i++) {
            int v1Component = i < version1Components.size() ? version1Components.get(i) : 0;
            int v2Component = i < version2Components.size() ? version2Components.get(i) : 0;

            if (v1Component > v2Component) {
                return 1;
            } else if (v1Component < v2Component) {
                return -1;
            }
        }
        return 0;
    }

    boolean isHigher(Path script, String version) {
        return this.compare(script, Path.of(version + "_")) > 0;
    }
}

