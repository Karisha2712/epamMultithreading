package edu.radyuk.multithreading.parser;

import edu.radyuk.multithreading.entity.Ship;

import java.util.ArrayList;
import java.util.List;

public class ShipFileLinesParser {
    private static final String DELIMITER = "\\s+";

    public List<Ship> receiveShips(List<String> fileLines) {
        List<Ship> ships = new ArrayList<>();
        fileLines.forEach(s -> {
            String taskTypeValue = s.split(DELIMITER)[1];
            Ship.TaskType taskType = Ship.TaskType.valueOf(taskTypeValue);
            ships.add(new Ship(taskType));
        });
        return ships;
    }
}
