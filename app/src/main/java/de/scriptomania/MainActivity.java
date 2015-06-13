package de.scriptomania;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jboss.aerogear.android.core.Callback;
import org.jboss.aerogear.android.unifiedpush.MessageHandler;
import org.jboss.aerogear.android.unifiedpush.PushRegistrar;
import org.jboss.aerogear.android.unifiedpush.RegistrarManager;
import org.jboss.aerogear.android.unifiedpush.gcm.AeroGearGCMPushConfiguration;

import java.net.URI;
import java.net.URISyntaxException;


public class MainActivity extends Activity implements MessageHandler {

    private PushConfiguration aerogearPushConfiguration;
    private static final String REGISTRAR_ID = "teamCityPushService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aerogearPushConfiguration = new PushConfiguration();
        try {
            RegistrarManager.config(REGISTRAR_ID, AeroGearGCMPushConfiguration.class)
                    .setPushServerURI(new URI(aerogearPushConfiguration.getServerURL()))
                    .addSenderId(aerogearPushConfiguration.getSenderID())
                    .setVariantID(aerogearPushConfiguration.getVariantID())
                    .setSecret(aerogearPushConfiguration.getSecret())
                    .setAlias("")
                    .asRegistrar();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        PushRegistrar registrar = RegistrarManager.getRegistrar(REGISTRAR_ID);
        registrar.register(getApplicationContext(), new Callback<Void>() {

            @Override
            public void onSuccess(Void ignore) {
                // Seems your device was registered in Unified Push Server
                Toast.makeText(getApplicationContext(), "Successfull registered", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getApplicationContext(), "Failed to register", Toast.LENGTH_SHORT).show();
                Log.e("TeamCityPushClient", exception.getMessage(), exception);
            }

        });
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
        Toast.makeText(this, bundle.getString("alert"), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError() {
        // ignore
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}