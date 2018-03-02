package com.sqless.network;

import com.sqless.utils.UIUtils;
import us.monoid.web.JSONResource;
import us.monoid.web.Resty;

public abstract class RestRequest {
    
    protected Resty rest;
    protected String url;
    
    public RestRequest(String url) {
        this.url = url;
        rest = new Resty();
    }

    public void onSuccess(JSONResource json) throws Exception {    
    }

    public void onFailure(String message) {
        UIUtils.showErrorMessage("Request error", message, null);
    }
    
    public abstract void exec();

}
