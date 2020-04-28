package a.gautham.library;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import a.gautham.library.async_tasks.DownloadTask;
import a.gautham.library.async_tasks.UtilsAsync;
import a.gautham.library.models.Update;

public class UpdateActivity extends AppCompatActivity {

    private ImageView icon_update, update_refresh;
    private TextView header1, header2, changelogs, changelogs_txt;
    private static final String TAG = "UpdateActivity: ";
    private DownloadTask downloadTask;
    private Button update_bt;
    private UtilsAsync utilsAsync;
    private UpdateListener updateListener;
    private String gitUsername, gitRepoName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        init();

        if (getIntent()!=null){

            gitUsername = getIntent().getStringExtra("username");
            gitRepoName = getIntent().getStringExtra("repoName");

            updateListener = new UpdateListener() {
                @Override
                public void onSuccess(Update update, boolean isUpdateAvailable) {

                    if (isUpdateAvailable){

                        update_bt.setVisibility(View.VISIBLE);
                        header2.setText(R.string.app_updater_new_update_available);

                        changelogs.setVisibility(View.VISIBLE);
                        changelogs_txt.setVisibility(View.VISIBLE);
                        changelogs.setText(update.getRelease_description());

                        update_bt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                downloadTask = new DownloadTask(UpdateActivity.this,update);
                                downloadTask.execute(update.getDownloadUrl());
                            }
                        });

                    }else {
                        header2.setText(R.string.app_updater_no_update_available);
                        update_bt.setVisibility(View.GONE);
                        changelogs.setVisibility(View.GONE);
                        changelogs_txt.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onFailed(String error) {
                    Log.e(TAG,error);
                }
            };

            start();

            update_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start();
                }
            });

        }else {
            Log.e(TAG,getString(R.string.app_updater_git_empty));
        }

    }

    public void start(){
        utilsAsync = new UtilsAsync(getApplicationContext(), gitUsername, gitRepoName, updateListener);
        utilsAsync.execute();
    }

    public void init() {

        icon_update = findViewById(R.id.icon_update);
        update_refresh = findViewById(R.id.update_refresh);
        header1 = findViewById(R.id.header1);
        header2 = findViewById(R.id.header2);
        update_bt = findViewById(R.id.update_bt);
        changelogs = findViewById(R.id.changelogs);
        changelogs_txt = findViewById(R.id.changelogs_txt);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (utilsAsync != null && utilsAsync.isCancelled()){
            utilsAsync.cancel(true);
        }
    }
}
