package com.groupe2cs.generator.service;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    public static String capitalize(String value) {
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public static String getPackage(String fullPath) {
        String normalized = fullPath.replace("\\", "/");

        int index = normalized.indexOf("/src/main/java/");
        if (index >= 0) {
            String packagePath = normalized.substring(index + "/src/main/java/".length());
            return packagePath.replace("/", ".");
        }

        return normalized.replace("/", ".");
    }

    public static String getTestPackage(String fullPath) {

         String relativePath = getPackage(fullPath);

         String testPath = relativePath.replace("main", "test");
         testPath = getPackage(testPath);

        return testPath;
    }
    public static String getTestDir(String fullPath) {

        return fullPath.replace("main", "test");
    }

    public static Object lowerFirst(String name) {
        return Character.toLowerCase(name.charAt(0)) + name.substring(1);
    }
}
