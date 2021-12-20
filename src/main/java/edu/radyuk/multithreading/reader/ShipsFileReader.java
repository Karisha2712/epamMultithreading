package edu.radyuk.multithreading.reader;

import edu.radyuk.multithreading.exception.SeaPortException;
import edu.radyuk.multithreading.validator.ShipsFileValidator;
import edu.radyuk.multithreading.validator.impl.ShipsFileValidatorImpl;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ShipsFileReader {
    private static final Logger logger = LogManager.getLogger();
    private final ShipsFileValidator seaPortFileValidator = new ShipsFileValidatorImpl();

    public List<String> readShipsFile(String filePath) throws SeaPortException {
        if (!seaPortFileValidator.isFileValid(filePath)) {
            throw new SeaPortException("Invalid file path");
        }
        Path path = Paths.get(filePath);
        try {
            List<String> fileLines = Files.readAllLines(path);
            if (!seaPortFileValidator.areFileLinesValid(fileLines)) {
                throw new SeaPortException("Invalid file lines");
            }
            logger.log(Level.INFO, "File lines read successfully");
            return fileLines;
        } catch (IOException e) {
            throw new SeaPortException("Can't read file: " + filePath, e);
        }
    }
}
