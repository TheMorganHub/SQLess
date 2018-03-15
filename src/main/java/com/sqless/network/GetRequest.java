package com.sqless.network;

import us.monoid.web.JSONResource;

public class GetRequest extends RestRequest {

    public GetRequest(String url) {
        super(url);
    }

    @Override
    public void exec() {
        Thread networkThread = new Thread(() -> {
            try {
                JSONResource json = rest.json(url);
                executePostExec(json);
            } catch (Exception e) {
                onFailure(e.getMessage());
            }
        });
        networkThread.start();
    }
    
}
