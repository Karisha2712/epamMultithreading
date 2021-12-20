package edu.radyuk.multithreading.validator.impl;

import edu.radyuk.multithreading.validator.SeaPortFileValidator;

import java.io.File;
import java.util.List;

public class SeaPortFileValidatorImpl implements SeaPortFileValidator {
    private static final String REGEXP_FOR_FIRST_FILE_LINE = "PIER_NUM\\s+\\d+";
    private static final String REGEXP_FOR_SECOND_FILE_LINE = "CAPACITY\\s+\\d+";
    private static final String REGEXP_FOR_THIRD_FILE_LINE = "CONTAINERS\\s+\\d+";
    private static final String REGEXP_FOR_SHIP_FILE_LINE = "SHIP\\s+(LOAD|UNLOAD)";

    @Override
    public boolean isFileValid(String filePath) {
        boolean validFile = false;
        if (filePath != null) {
            File file = new File(filePath);
            validFile = file.exists() && (file.length() != 0);
        }
        return validFile;
    }

    @Override
    public boolean areFileLinesValid(List<String> fileLines) {
        return ((fileLines.size() == 3)
                && fileLines.get(0).matches(REGEXP_FOR_FIRST_FILE_LINE)
                && fileLines.get(1).matches(REGEXP_FOR_SECOND_FILE_LINE)
                && fileLines.get(2).matches(REGEXP_FOR_THIRD_FILE_LINE));
    }

    @Override
    public boolean areShipFileLinesValid(List<String> fileLines) {
        return fileLines.stream().allMatch(s -> s.matches(REGEXP_FOR_SHIP_FILE_LINE));
    }
}
