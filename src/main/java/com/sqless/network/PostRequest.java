package com.sqless.network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import us.monoid.json.JSONException;
import us.monoid.json.JSONObject;
import us.monoid.web.FormData;
import us.monoid.web.Resty;
import us.monoid.web.mime.MultipartContent;

public class PostRequest extends RestRequest {

    private MultipartContent form;

    public PostRequest(String url, FormData... data) {
        super(url);
        List<FormData> formData = new ArrayList<>(Arrays.asList(data));
        formData.add(Resty.data("source", "DESKTOP"));
        form = Resty.form(formData.toArray(new FormData[formData.size()]));
    }

    public PostRequest(String url, boolean newThread, FormData... data) {
        this(url, data);
        super.newThread = newThread;
    }

    @Override
    public void exec() {
        Runnable runnable = () -> {
            try {
                JSONObject json = rest.json(url, form).object();
                executePostExec(json);
            } catch (Exception e) {
                String message = e.getMessage();
                String parsedError = message != null && !message.isEmpty() && message.contains("{") && message.contains("}") ? message.substring(message.indexOf('{')) : null;
                if (parsedError != null) {
                    try {
                        JSONObject obj = new JSONObject(parsedError);
                        onFailure(obj.getString("err"));
                    } catch (JSONException jsonEx) {
                        onFailure("Hubo un error al procesar la petición contra la dirección: " + url);
                    }
                } else {
                    onFailure("Hubo un error al procesar la petición contra la dirección " + url);
                }
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
