package com.sqless.network;

import java.awt.EventQueue;
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
                EventQueue.invokeLater(() -> {
                    try {
                        onSuccess(json);
                    } catch (Exception e) {
                        onFailure(e.getMessage());
                    }
                });
            } catch (Exception e) {
                onFailure(e.getMessage());
            }
        });
        networkThread.start();
    }
    
}
