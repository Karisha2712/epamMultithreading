package edu.radyuk.multithreading.validator.impl;

import edu.radyuk.multithreading.validator.ShipsFileValidator;

import java.io.File;
import java.util.List;

public class ShipsFileValidatorImpl implements ShipsFileValidator {
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
        return fileLines.stream().allMatch(s -> s.matches(REGEXP_FOR_SHIP_FILE_LINE));
    }
}
