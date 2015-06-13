package de.scriptomania;

/**
 * Created by leadsinger_mac_mini on 16/05/15.
 */
public class PushConfiguration {

    private String senderID = "525825801218";
    private String variantID = "256fde38-fa71-4fe7-8c42-bd2148186004";
    private String secret = "d4b3f8ce-c1a3-442c-a22f-e27cebb64a36";
    private String serverURL = "https://aerogear-scriptomania.rhcloud.com/ag-push/";
    private String alias = "https://aerogear-scriptomania.rhcloud.com/ag-push/";

    public PushConfiguration() {

    }

    public PushConfiguration(String serverURL, String senderID, String variantID, String secret, String alias) {
        this.serverURL = serverURL;
        this.senderID = senderID;
        this.variantID = variantID;
        this.secret = secret;
        this.alias = alias;
    }

    public void initializeFromSettings() {
        // TODO
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