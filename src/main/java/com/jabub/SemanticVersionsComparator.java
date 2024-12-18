package com.jabub;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static com.jabub.Utils.extractVersion;
import static java.lang.Math.max;
import static java.util.Arrays.deepToString;
import static java.util.Arrays.stream;

public class SemanticVersionsComparator implements Comparator<Path> {

    private static final List<String> PRE_RELEASE_ORDER = List.of("alpha", "beta", "gamma", "rc");

    @Override
    public int compare(Path o1, Path o2) {
        String version1 = extractVersion(o1);
        String version2 = extractVersion(o2);

        return compereVersions(version1, version2);
    }

    boolean isHigher (Path script, String version) {
        return this.compare(script, Path.of(version + "_")) > 0;
    }

    private int compereVersions(String version1, String version2) {
        String[] v1Parts = version1.split("\\.");
        String[] v2Parts = version2.split("\\.");

        int[] v1Main = parseMainParts(v1Parts[0]);
        int[] v2Main = parseMainParts(v2Parts[0]);

        for (int i = 0; i < 3; i++){
            int result = Integer.compare(v1Main[i], v2Main[i]);
            if (result != 0){
                return result;
            }
        }

        return comparePreRelease(
                v1Parts.length > 1 ? v1Parts[1].toLowerCase() : null,
                v2Parts.length > 1 ? v2Parts[1].toLowerCase() : null
        );
    }

    private int[] parseMainParts(String mainPart) {
        String[] parts = mainPart.split("\\.");
        int[] parsed = new int[3];
        for(int i = 0; i < parts.length && i < 3; i++){
            parsed[i] = Integer.parseInt(parts[i]);
        }
        return parsed;
    }

    private int comparePreRelease(String pre1, String pre2) {
        if (pre1 == null && pre2 == null) {
            return 0;
        }
        if (pre1 == null) {
            return 1;
        }
        if (pre2 == null) {
            return -1;
        }

        int index1 = PRE_RELEASE_ORDER.indexOf(pre1);
        int index2 = PRE_RELEASE_ORDER.indexOf(pre2);

        if (index1 == -1 && index2 == -1) {
            return pre1.compareTo(pre2);
        }
        if (index1 == -1){
            return 1;
        }
        if (index2 == -1){
            return -1;
        }
        return Integer.compare(index1, index2);
    }



//    @Override
//    public int compare(Path o1, Path o2) {
//        String version1 = extractVersion(o1);
//        String version2 = extractVersion(o2);
//
//        List<Integer> version1Components = stream(version1.split("\\."))
//                .map(Integer::parseInt)
//                .toList();
//        List<Integer> version2Components = stream(version2.split("\\."))
//                .map(Integer::parseInt)
//                .toList();
//
//        int maxLength = max(version1Components.size(), version2Components.size());
//
//        for (int i = 0; i < maxLength; i++) {
//            int v1Component = i < version1Components.size() ? version1Components.get(i) : 0;
//            int v2Component = i < version2Components.size() ? version2Components.get(i) : 0;
//
//            if (v1Component > v2Component) {
//                return 1;
//            } else if (v1Component < v2Component) {
//                return -1;
//            }
//        }
//        return 0;
//    }
//
//    boolean isHigher(Path script, String version) {
//        return this.compare(script, Path.of(version + "_")) > 0;
//    }
}




