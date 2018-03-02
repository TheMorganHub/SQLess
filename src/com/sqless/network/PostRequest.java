package com.sqless.network;

import java.awt.EventQueue;
import us.monoid.web.FormContent;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public class PostRequest extends RestRequest {

    private FormContent form;

    public PostRequest(String url, String formData) {
        super(url);
        this.form = Resty.form(formData);
    }

    @Override
    public void exec() {
        Thread networkThread = new Thread(() -> {
            try {
                JSONResource json = rest.json(url, form);
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
