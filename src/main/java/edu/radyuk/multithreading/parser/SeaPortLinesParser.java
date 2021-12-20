package edu.radyuk.multithreading.parser;

import edu.radyuk.multithreading.entity.SeaPortParameters;
import edu.radyuk.multithreading.entity.Ship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeaPortLinesParser {
    private static final String DELIMITER = "\\s+";
    private static final String REGEXP_FOR_SHIP_FILE_LINE = "SHIP\\s+(LOAD|UNLOAD)";

    public Map<SeaPortParameters, Integer> receiveSeaPortParameters(List<String> fileLines) {
        Integer pierNumber = Integer.parseInt(fileLines.get(0).split(DELIMITER)[1]);
        Integer capacity = Integer.parseInt(fileLines.get(1).split(DELIMITER)[1]);
        Integer containersNumber = Integer.parseInt(fileLines.get(2).split(DELIMITER)[1]);
        Map<SeaPortParameters, Integer> seaPortParameters = new HashMap<>();
        seaPortParameters.put(SeaPortParameters.PIER_NUM, pierNumber);
        seaPortParameters.put(SeaPortParameters.CAPACITY, capacity);
        seaPortParameters.put(SeaPortParameters.CONTAINERS, containersNumber);
        return seaPortParameters;
    }

    public List<Ship> receiveShips(List<String> fileLines) {
        List<Ship> ships = new ArrayList<>();
        fileLines.stream()
                .filter(s -> s.matches(REGEXP_FOR_SHIP_FILE_LINE))
                .forEach(s -> {
                    String taskTypeValue = s.split(DELIMITER)[1];
                    Ship.TaskType taskType = Ship.TaskType.valueOf(taskTypeValue);
                    ships.add(new Ship(taskType));
                });
        return ships;
    }
}
