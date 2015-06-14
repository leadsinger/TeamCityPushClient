package de.scriptomania;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends Activity implements MessageHandler {

    private PushConfiguration aerogearPushConfiguration;
    private static final int NOTIFICATION_ID = 53197;
    private static final String REGISTRAR_ID = "teamCityPushService";
    private static final String SETTING_KEY_RECEIVING_STATE = "isReceiving";
    private static final String SETTING_KEY_PUSH_CONFIG = "PushConfiguration";
    private ToggleButton toggleButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setTextOn("Start service");
        toggleButton.setTextOff("Stop service");
        aerogearPushConfiguration = initializeConfigAndUI();
    }

    private void registerForPushNotifications() {
        boolean error = setPushConfigurationFromTextFields(aerogearPushConfiguration);
        if (error) {
            return;
        }
        try {
            RegistrarManager.config(REGISTRAR_ID, AeroGearGCMPushConfiguration.class)
                    .setPushServerURI(new URI(aerogearPushConfiguration.getServerURL()))
                    .addSenderId(aerogearPushConfiguration.getSenderID())
                    .setVariantID(aerogearPushConfiguration.getVariantID())
                    .setSecret(aerogearPushConfiguration.getSecret())
                    .setAlias(aerogearPushConfiguration.getAlias())
                    .asRegistrar();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        PushRegistrar registrar = RegistrarManager.getRegistrar(REGISTRAR_ID);
        if (registrar == null) return;
        registrar.register(getApplicationContext(), new Callback<Void>() {

            @Override
            public void onSuccess(Void ignore) {

                // save config
                final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = preferences.edit();
                preferencesEditor.putBoolean(SETTING_KEY_RECEIVING_STATE, true);
                preferencesEditor.commit();

                Gson gson = new Gson();
                preferencesEditor.putString(SETTING_KEY_PUSH_CONFIG, gson.toJson(aerogearPushConfiguration));
                preferencesEditor.commit();

                setToggleButton(true);
                Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), "Failed to register", Toast.LENGTH_SHORT).show();
                Log.e("TeamCityPushClient", exception.getMessage(), exception);
            }

        });
    }

    private void unregisterForPushNotifications() {
        PushRegistrar registrar = RegistrarManager.getRegistrar(REGISTRAR_ID);
        if (registrar == null) return;
        registrar.unregister(getApplicationContext(), new Callback<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor preferencesEditor = preferences.edit();
                preferencesEditor.putBoolean(SETTING_KEY_RECEIVING_STATE, false);
                preferencesEditor.commit();
                setToggleButton(false);
                Toast.makeText(getApplicationContext(), "Successfully unregistered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), "Failed to unregister", Toast.LENGTH_SHORT).show();
                Log.e("TeamCityPushClient", exception.getMessage(), exception);
            }
        });
    }

    private PushConfiguration initializeConfigAndUI() {
        final SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        setToggleButton(preferences.getBoolean(SETTING_KEY_RECEIVING_STATE, false));
        toggleButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // save state and set button text
                boolean isReceiving = preferences.getBoolean(SETTING_KEY_RECEIVING_STATE, false);
                if (isReceiving) {
                    unregisterForPushNotifications();
                } else {
                    registerForPushNotifications();
                }
            }

        });

        if (preferences.contains(SETTING_KEY_PUSH_CONFIG)) {
            String jsonConfig = preferences.getString(SETTING_KEY_PUSH_CONFIG,"");
            Gson gson = new Gson();
            PushConfiguration pushConfiguration = gson.fromJson(jsonConfig, PushConfiguration.class);
            initializeTextFields(pushConfiguration);
            return pushConfiguration;
        }
        return new PushConfiguration();
    }

    private void initializeTextFields(PushConfiguration pushConfiguration) {
        EditText txtServerURL = (EditText) findViewById(R.id.serverURL);
        EditText txtSenderID = (EditText) findViewById(R.id.senderID);
        EditText txtVariantID = (EditText) findViewById(R.id.variantID);
        EditText txtSecret = (EditText) findViewById(R.id.secret);
        EditText txtAlias = (EditText) findViewById(R.id.alias);
        txtServerURL.setText(pushConfiguration.getServerURL());
        txtSenderID.setText(pushConfiguration.getSenderID());
        txtVariantID.setText(pushConfiguration.getVariantID());
        txtSecret.setText(pushConfiguration.getSecret());
        txtAlias.setText(pushConfiguration.getAlias());
    }

    private boolean setPushConfigurationFromTextFields(PushConfiguration pushConfiguration) {
        EditText txtServerURL = (EditText) findViewById(R.id.serverURL);
        EditText txtSenderID = (EditText) findViewById(R.id.senderID);
        EditText txtVariantID = (EditText) findViewById(R.id.variantID);
        EditText txtSecret = (EditText) findViewById(R.id.secret);
        EditText txtAlias = (EditText) findViewById(R.id.alias);

        if (txtServerURL.getText().toString().length() == 0 || txtSenderID.getText().toString().length() == 0
                || txtVariantID.getText().toString().length() == 0 || txtSecret.getText().toString().length() == 0
                || txtAlias.getText().toString().length() == 0) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            pushConfiguration.setServerURL(txtServerURL.getText().toString());
            pushConfiguration.setSenderID(txtSenderID.getText().toString());
            pushConfiguration.setVariantID(txtVariantID.getText().toString());
            pushConfiguration.setSecret(txtSecret.getText().toString());
            pushConfiguration.setAlias(txtAlias.getText().toString());
        }
        return true;
    }

    private void setToggleButton(boolean toggleState) {
        if (toggleState) {
            toggleButton.setText("Stop service");
        } else {
            toggleButton.setText("Start service");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        RegistrarManager.registerMainThreadHandler(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        RegistrarManager.unregisterMainThreadHandler(this);
    }

    @Override
    public void onDeleteMessage(Context context, Bundle bundle) {
        // ignore
    }

    @Override
    public void onMessage(Context context, Bundle bundle) {
        String fullName = bundle.getString("BUILD_FULL_NAME_KEY");
        String status = bundle.getString("BUILD_STATUS_KEY");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setContentTitle("TeamCity Build Notification")
                .setContentText(fullName + " - " + status)
                .setSmallIcon(R.mipmap.ic_action_email);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onError() {
        // ignore
    }

}