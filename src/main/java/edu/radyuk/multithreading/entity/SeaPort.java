package edu.radyuk.multithreading.entity;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SeaPort {
    private static final Logger logger = LogManager.getLogger();
    private static final String FILE_PATH = "files/seaport.txt";
    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicBoolean created = new AtomicBoolean(false);
    private static SeaPort instance;
    private final ReentrantLock storageLock = new ReentrantLock(true);
    private final ReentrantLock pierLock = new ReentrantLock(true);
    private final Condition pierCondition = pierLock.newCondition();
    private final Condition loadAvailable = storageLock.newCondition();
    private final Condition unloadAvailable = storageLock.newCondition();
    private final Deque<Pier> freePiers;
    private final Deque<Pier> busyPiers;
    private final int storageCapacity;
    private final int pierNumber;
    private int currentContainerNumber;

    private SeaPort() {
        InputStream propertyFileStream = getClass().getClassLoader().getResourceAsStream(FILE_PATH);
        Properties properties = new Properties();
        try {
            properties.load(propertyFileStream);
        } catch (IOException e) {
            logger.log(Level.ERROR, "Input stream is invalid");
        }
        String capacity = properties.getProperty(SeaPortParameter.CAPACITY.toString());
        String pierNum = properties.getProperty(SeaPortParameter.PIER_NUM.toString());
        String containerNumber = properties.getProperty(SeaPortParameter.CONTAINERS.toString());
        storageCapacity = Integer.parseInt(capacity);
        this.pierNumber = Integer.parseInt(pierNum);
        currentContainerNumber = Integer.parseInt(containerNumber);
        freePiers = new ArrayDeque<>();
        busyPiers = new ArrayDeque<>();
        for (int i = 0; i < this.pierNumber; i++) {
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

    public int getPierNumber() {
        return pierNumber;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public int getCurrentContainerNumber() {
        return currentContainerNumber;
    }

    public Pier obtainPier() {
        try {
            pierLock.lock();
            try {
                while (freePiers.isEmpty()) {
                    pierCondition.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while pier obtaining ", e);
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
                while (currentContainerNumber == storageCapacity) {
                    loadAvailable.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while storage loading ", e);
                Thread.currentThread().interrupt();
            }
            currentContainerNumber++;
            unloadAvailable.signal();
        } finally {
            storageLock.unlock();
        }
    }

    public void unloadStorage() {
        try {
            storageLock.lock();
            try {
                while (currentContainerNumber == 0) {
                    unloadAvailable.await();
                }
            } catch (InterruptedException e) {
                logger.log(Level.ERROR, "Error while storage loading ", e);
                Thread.currentThread().interrupt();
            }
            currentContainerNumber--;
            loadAvailable.signal();
        } finally {
            storageLock.unlock();
        }
    }
}
