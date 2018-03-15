package com.sqless.sql.objects;

public interface SQLRenameable {

    String getRenameStatement(String newName);

    void rename(String newName);
}
