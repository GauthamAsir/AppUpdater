package a.gautham.appupdate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import a.gautham.library.AppUpdater;
import a.gautham.library.Display;
import a.gautham.library.models.Update;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.setDisplay(Display.DIALOG);
        appUpdater.setUpGithub("GauthamAsir","WhatsApp_Status_Saver");
        appUpdater.start();

    }
}
