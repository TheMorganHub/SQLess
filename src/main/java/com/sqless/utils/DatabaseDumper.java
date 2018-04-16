package com.sqless.utils;

import com.sqless.file.FileManager;
import com.sqless.sql.connection.SQLConnectionManager;
import java.io.File;

public class DatabaseDumper {

    private File mysqldumpFile;

    public DatabaseDumper(File mysqldumpFile) {
        this.mysqldumpFile = mysqldumpFile;
    }

    public void start() {
        createCommand();
    }

    private void createCommand() {
        String filePath = mysqldumpFile.getPath().replace(".exe", "");
        SQLConnectionManager conManager = SQLConnectionManager.getInstance();
        FileManager.getInstance().saveFileAs("sql", chosenPath -> {
            String command = filePath + " -h " + conManager.getHostName() + " -u " + conManager.getUsername() + " -p" + conManager.getPassword() + " --routines "
                    + SQLUtils.getConnectedDBName() + " > " + chosenPath;
            //TODO Runtime.getRuntime().exec
        });
    }

}
