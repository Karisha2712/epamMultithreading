package edu.radyuk.muitithreading._main;

import edu.radyuk.muitithreading.entity.Ship;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        List<Ship> ships = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Ship ship;
            if (i % 2 == 0) {
                ship = new Ship(Ship.TaskType.LOAD);
            } else {
                ship = new Ship(Ship.TaskType.UNLOAD);
            }
            ships.add(ship);
        }
        ExecutorService service = Executors.newFixedThreadPool(ships.size());
        ships.forEach(service::execute);
        service.shutdown();
    }
}
