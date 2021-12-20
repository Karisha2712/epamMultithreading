package edu.radyuk.multithreading._main;

import edu.radyuk.multithreading.entity.Ship;
import edu.radyuk.multithreading.exception.SeaPortException;
import edu.radyuk.multithreading.parser.SeaPortLinesParser;
import edu.radyuk.multithreading.reader.SeaPortFileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final Logger logger = LogManager.getLogger();
    private static final String FILE_PATH = "files/ships.txt";

    public static void main(String[] args) {
        URL fileUrl = Main.class.getClassLoader().getResource(FILE_PATH);
        File file = new File(fileUrl.getFile());
        String filePath = file.getAbsolutePath();
        SeaPortFileReader reader = new SeaPortFileReader();
        SeaPortLinesParser parser = new SeaPortLinesParser();
        try {
            List<Ship> ships;
            List<String> fileLines = reader.readShipsFile(filePath);
            ships = parser.receiveShips(fileLines);
            ExecutorService service = Executors.newFixedThreadPool(ships.size());
            ships.forEach(service::execute);
            service.shutdown();
        } catch (SeaPortException e) {
            logger.log(Level.ERROR, e);
        }
    }
}
