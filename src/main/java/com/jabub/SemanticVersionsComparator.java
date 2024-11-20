package com.jabub;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SemanticVersionsComparator implements Comparator<Path> {

    @Override
    public int compare(Path o1, Path o2) {
        String version1 = o1.getFileName().toString().substring(1, o1.getFileName().toString().indexOf("_"));
        String version2 = o2.getFileName().toString().substring(1, o2.getFileName().toString().indexOf("_"));

        List<Integer> version1Components = Arrays.stream(version1.split("\\."))
                .map(Integer::parseInt)
                .toList();
        List<Integer> version2Components = Arrays.stream(version2.split("\\."))
                .map(Integer::parseInt)
                .toList();

        int maxLength = Math.max(version1Components.size(), version2Components.size());

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
}
