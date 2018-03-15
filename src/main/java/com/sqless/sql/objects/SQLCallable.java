package com.sqless.sql.objects;

public interface SQLCallable {

    String getCallStatement();
    
    String prepareParameters();
}
