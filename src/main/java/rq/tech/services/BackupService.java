package rq.tech.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class BackupService {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    public String createBackup() throws IOException, InterruptedException {
        String dbName = extractDbName(dbUrl);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupFileName = "backup_" + dbName + "_" + timestamp + ".sql";

        Path backupDir = Paths.get("backups");
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        Path backupPath = backupDir.resolve(backupFileName);

        // Command for mysqldump
        // Note: This requires mysqldump to be installed on the system
        ProcessBuilder pb = new ProcessBuilder(
                "mysqldump",
                "-u" + dbUsername,
                "-p" + dbPassword,
                dbName,
                "-r", backupPath.toString()
        );

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return backupPath.toString();
        } else {
            throw new IOException("Backup failed with exit code: " + exitCode);
        }
    }

    private String extractDbName(String url) {
        // e.g., jdbc:mysql://localhost:3306/rqtech
        return url.substring(url.lastIndexOf("/") + 1).split("\\?")[0];
    }
}
