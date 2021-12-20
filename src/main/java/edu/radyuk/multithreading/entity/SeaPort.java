package edu.radyuk.multithreading.entity;

import edu.radyuk.multithreading._main.Main;
import edu.radyuk.multithreading.exception.SeaPortException;
import edu.radyuk.multithreading.parser.SeaPortLinesParser;
import edu.radyuk.multithreading.reader.SeaPortFileReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SeaPort {
    private static final Logger logger = LogManager.getLogger();
    private static final String FILE_PATH = "files/seaport.txt";
    private static final ReentrantLock lock = new ReentrantLock();
    private static final AtomicBoolean created = new AtomicBoolean(false);
    private static int STORAGE_CAPACITY;
    private static SeaPort instance;
    private final ReentrantLock storageLock = new ReentrantLock(true);
    private final ReentrantLock pierLock = new ReentrantLock(true);
    private final Condition pierCondition = pierLock.newCondition();
    private final Condition loadAvailable = storageLock.newCondition();
    private final Condition unloadAvailable = storageLock.newCondition();
    private final Deque<Pier> freePiers;
    private final Deque<Pier> busyPiers;
    private int currentContainerNumber;

    private SeaPort() throws SeaPortException {
        URL fileUrl = Main.class.getClassLoader().getResource(FILE_PATH);
        File file = new File(fileUrl.getFile());
        String filePath = file.getAbsolutePath();
        SeaPortFileReader reader = new SeaPortFileReader();
        SeaPortLinesParser parser = new SeaPortLinesParser();
        int pierNumber;
        List<String> fileLines = reader.readSeaPortFile(filePath);
        Map<SeaPortParameters, Integer> seaPortParameters = parser.receiveSeaPortParameters(fileLines);
        STORAGE_CAPACITY = seaPortParameters.get(SeaPortParameters.CAPACITY);
        pierNumber = seaPortParameters.get(SeaPortParameters.PIER_NUM);
        currentContainerNumber = seaPortParameters.get(SeaPortParameters.CONTAINERS);
        freePiers = new ArrayDeque<>();
        busyPiers = new ArrayDeque<>();
        for (int i = 0; i < pierNumber; i++) {
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
            } catch (SeaPortException e) {
                e.printStackTrace();
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
                while (currentContainerNumber == STORAGE_CAPACITY) {
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
