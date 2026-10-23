package rq.tech.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileSystems;

@Service
public class StorageService {

    private static final long WARNING_THRESHOLD_BYTES = 100 * 1024 * 1024; // 100 MB
    private static final long BLOCK_THRESHOLD_BYTES = 10 * 1024 * 1024; // 10 MB

    public String getStorageStatus() {
        File file = new File(".");
        long freeSpace = file.getFreeSpace();

        if (freeSpace < BLOCK_THRESHOLD_BYTES) {
            return "BLOCK_THRESHOLD_REACHED";
        } else if (freeSpace < WARNING_THRESHOLD_BYTES) {
            return "WARNING_THRESHOLD_REACHED";
        } else {
            return "STORAGE_OK";
        }
    }

    public long getFreeSpaceInMB() {
        return new File(".").getFreeSpace() / (1024 * 1024);
    }
}
