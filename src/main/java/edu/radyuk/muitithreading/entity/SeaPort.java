package edu.radyuk.muitithreading.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SeaPort {
    private static final Logger logger = LogManager.getLogger();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicBoolean created = new AtomicBoolean(false);
    private static final int STORAGE_CAPACITY = 1000;
    private static final int PIER_NUMBER = 5;
    private static SeaPort instance;
    private ReentrantLock storageLock = new ReentrantLock(true);
    private ReentrantLock pierLock = new ReentrantLock(true);
    private Condition pierCondition = pierLock.newCondition();
    private Condition loadAvailable = storageLock.newCondition();
    private Deque<Pier> freePiers;
    private Deque<Pier> busyPiers;
    private int currentContainerNumber;

    private SeaPort() {
        freePiers = new ArrayDeque<>();
        busyPiers = new ArrayDeque<>();
        for (int i = 0; i < PIER_NUMBER; i++) {
            freePiers.addLast(new Pier());
        }
    }

    public static SeaPort getInstance() {
        if (!created.get()) {
            try {
                lock.lock();
                if (instance == null) {
                    instance = new SeaPort();
                    created.set(true);
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    public Pier obtainPier() {
        try {
            pierLock.lock();
            try {
                while (freePiers.isEmpty()) {
                    pierCondition.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while pier obtaining");
                Thread.currentThread().interrupt();
            }
            Pier pier = freePiers.removeLast();
            busyPiers.addLast(pier);
            return pier;
        } finally {
            pierLock.unlock();
        }
    }

    public void releasePier(Pier pier) {
        try {
            pierLock.lock();
            busyPiers.remove(pier);
            freePiers.addLast(pier);
            pierCondition.signal();
        } finally {
            pierLock.unlock();
        }
    }

    public void loadStorage() {
        try {
            storageLock.lock();
            try {
                while (currentContainerNumber == STORAGE_CAPACITY) {
                    loadAvailable.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while storage loading");
                Thread.currentThread().interrupt();
            }
            currentContainerNumber++;
        } finally {
            storageLock.unlock();
        }
    }

    public void unloadStorage() {
        try {
            storageLock.lock();
            try {
                while (currentContainerNumber == 0) {
                    loadAvailable.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while storage loading");
                Thread.currentThread().interrupt();
            }
            currentContainerNumber--;
        } finally {
            storageLock.unlock();
        }
    }
}
