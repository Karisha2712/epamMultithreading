package edu.radyuk.multithreading.validator;

import java.util.List;

public interface ShipsFileValidator {
    boolean isFileValid(String filePath);

    boolean areFileLinesValid(List<String> fileLines);
}
