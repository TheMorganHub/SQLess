package com.sqless.network;

import us.monoid.web.FormContent;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public class PostRequest extends RestRequest {

    private FormContent form;

    public PostRequest(String url, String... formData) {
        super(url);
        this.form = Resty.form(formData.length > 1 ? String.join("&", formData) : formData[0]);
    }

    @Override
    public void exec() {
        Thread networkThread = new Thread(() -> {
            try {
                JSONResource json = rest.json(url, form);
                executePostExec(json);
            } catch (Exception e) {
                onFailure(e.getMessage());
                e.printStackTrace();
            }
        });
        networkThread.start();
    }

}
