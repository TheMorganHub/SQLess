package com.sqless.network;

import us.monoid.web.FormContent;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public class PostRequest extends RestRequest {

    private FormContent form;

    public PostRequest(String url, String... formData) {
        super(url);
        for (int i = 0; i < formData.length; i++) {
            formData[i] = encodeSpecialChars(formData[i]);
        }
        this.form = Resty.form(formData.length > 1 ? String.join("&", formData) : formData[0]);
    }

    public PostRequest(String url, boolean newThread, String... formData) {
        this(url, formData);
        super.newThread = newThread;
    }

    @Override
    public void exec() {
        Runnable runnable = () -> {
            try {
                JSONResource json = rest.json(url, form);
                executePostExec(json);
            } catch (Exception e) {
                onFailure(e.getMessage());
                e.printStackTrace();
            }
        };
        if (newThread) {
            Thread networkThread = new Thread(runnable);
            networkThread.start();
        } else {
            runnable.run();
        }
    }
    
    private String encodeSpecialChars(String data) {
        return data.replaceAll("\\&", "%26");
    }

}
