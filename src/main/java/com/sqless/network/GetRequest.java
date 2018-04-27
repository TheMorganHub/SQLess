package com.sqless.network;

import us.monoid.json.JSONObject;

public class GetRequest extends RestRequest {

    public GetRequest(String url) {
        super(url);
    }

    public GetRequest(String url, boolean newThread) {
        super(url, newThread);
    }

    @Override
    public void exec() {
        Runnable runnable = () -> {
            try {
                JSONObject json = rest.json(url).object();
                executePostExec(json);
            } catch (Exception e) {
                onFailure(e.getMessage());
            }
        };
        if (newThread) {
            Thread networkThread = new Thread(runnable);
            networkThread.start();
        } else {
            runnable.run();
        }
    }

}
