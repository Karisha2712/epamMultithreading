package edu.radyuk.multithreading.validator;

import java.util.List;

public interface SeaPortFileValidator {
    boolean isFileValid(String filePath);

    boolean areFileLinesValid(List<String> fileLines);

    boolean areShipFileLinesValid(List<String> fileLines);
}
