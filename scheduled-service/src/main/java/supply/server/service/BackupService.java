package supply.server.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;

@Slf4j
public class BackupService {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/supplydb";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "your_password";
    private static final String BACKUP_DIR = "/path/to/backup/directory/";
    private static final String BACKUP_FILE = BACKUP_DIR + "backup-" + System.currentTimeMillis() + ".sql";

    @Scheduled(cron = "0 0 * * * ?")
    public void performBackup() {
        String command = String.format(
                "pg_dump -U %s -h localhost -p 5432 -F c -b -v -f %s %s",
                DB_USER, BACKUP_FILE, "supplydb");

        try {
            Process process = new ProcessBuilder("bash", "-c", String.format("PGPASSWORD=%s %s", DB_PASSWORD, command))
                    .directory(new File(BACKUP_DIR))
                    .start();

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new InterruptedException("Database backup failed");
            }
        } catch (IOException | InterruptedException e) {
            log.error("Error during backup: {}", e.getMessage());
        }
    }
}
