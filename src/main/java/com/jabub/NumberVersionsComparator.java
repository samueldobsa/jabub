package com.jabub;

import java.nio.file.Path;
import java.util.Comparator;

import static java.lang.Double.parseDouble;

public class NumberVersionsComparator implements Comparator<Path> {

    @Override
    public int compare(Path o1, Path o2) {
        String number1 = o1.getFileName().toString().substring(0, o1.getFileName().toString().indexOf("_"));
        String number2 = o2.getFileName().toString().substring(0, o2.getFileName().toString().indexOf("_"));

        return Double.compare(parseDouble(number1), parseDouble(number2));

    }

    static boolean isHigher(Path script, String lastRunVersion) {
        String scriptVersion = script.getFileName().toString().substring(0, script.getFileName().toString().indexOf("_"));

        return Double.compare(parseDouble(lastRunVersion), parseDouble(scriptVersion)) > 1;
    }
}
