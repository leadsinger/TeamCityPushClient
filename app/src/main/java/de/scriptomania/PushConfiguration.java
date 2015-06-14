package de.scriptomania;

import android.text.Editable;

/**
 * Created by leadsinger_mac_mini on 16/05/15.
 */
public class PushConfiguration {

    private String senderID = "";
    private String variantID = "";
    private String secret = "";
    private String serverURL = "";
    private String alias = "";

    public PushConfiguration() {

    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getVariantID() {
        return variantID;
    }

    public void setVariantID(String variantID) {
        this.variantID = variantID;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getServerURL() {
        return serverURL;
    }

    public void setServerURL(String serverURL) {
        this.serverURL = serverURL;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}