package a.gautham.appupdate;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import a.gautham.library.AppUpdater;
import a.gautham.library.Display;

public class MainActivity extends AppCompatActivity {

    private AppUpdater appUpdater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdater = new AppUpdater(this);
        appUpdater.setDisplay(Display.DIALOG);
        appUpdater.setUpGithub("GauthamAsir","WhatsApp_Status_Saver");
        appUpdater.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        appUpdater.stop();
    }
}
