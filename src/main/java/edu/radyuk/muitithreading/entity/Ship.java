package edu.radyuk.muitithreading.entity;

import edu.radyuk.muitithreading.util.ShipIdGenerator;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Ship implements Runnable {
    private static final Logger logger = LogManager.getLogger();
    private final int shipId;
    private final int capacity;
    private final TaskType taskType;
    private int containersNumber;
    private TaskState taskState;

    public Ship(int capacity, int containersNumber, TaskType taskType) {
        this.capacity = capacity;
        this.containersNumber = containersNumber;
        this.shipId = ShipIdGenerator.generateId();
        this.taskType = taskType;
        this.taskState = TaskState.BEFORE_START;
    }

    public enum TaskType {
        LOAD, UNLOAD
    }

    public enum TaskState {
        BEFORE_START, IN_PROGRESS, COMPLETE
    }

    @Override
    public void run() {
        this.taskState = TaskState.IN_PROGRESS;
        logger.log(Level.INFO, "");
    }

    public int getShipId() {
        return shipId;
    }

    public int getCapacity() {
        return capacity;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getContainersNumber() {
        return containersNumber;
    }

    public void setContainersNumber(int containersNumber) {
        this.containersNumber = containersNumber;
    }

    public TaskState getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskState taskState) {
        this.taskState = taskState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ship ship = (Ship) o;
        return capacity == ship.capacity;
    }

    @Override
    public int hashCode() {
        return capacity;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Ship{");
        sb.append("shipId=").append(shipId);
        sb.append(", capacity=").append(capacity);
        sb.append(", taskType=").append(taskType);
        sb.append(", containersNumber=").append(containersNumber);
        sb.append(", taskState=").append(taskState);
        sb.append('}');
        return sb.toString();
    }
}
