package a.gautham.library;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import a.gautham.library.async_tasks.DownloadTask;

public class DownloadDialog extends AlertDialog {

    private AlertDialog alertDialog;
    private TextView percent_pg, download_name, download_size;
    private ProgressBar progressBar;

    public DownloadDialog(@NonNull Context context, DownloadTask downloadTask) {
        super(context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.download_layout, null);
        builder.setView(view);

        progressBar = view.findViewById(R.id.progressBar);
        download_name = view.findViewById(R.id.download_name);
        download_size = view.findViewById(R.id.download_size);
        percent_pg = view.findViewById(R.id.percent_pg);
        builder.setTitle(R.string.app_updater_downloading);
        builder.setIcon(R.drawable.ic_system_update);

        progressBar.setMax(100);
        progressBar.setIndeterminate(false);

        builder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTask.cancel(true);
                dialog.dismiss();
            }
        });

        alertDialog = builder.create();

    }

    public void setDownloadName(String downloadName) {
        download_name.setText(downloadName);
    }

    public void setDownloadSize(String downloadSize) {
        download_size.setText(downloadSize);
    }

    public void setProgressPercent(String progressPercent) {
        percent_pg.setText(progressPercent);
    }

    public void setProgressBaPercent(int progressBaPercent) {
        progressBar.setProgress(progressBaPercent);
    }

    public void show(){
        alertDialog.show();
    }

    public void hide(){
        alertDialog.dismiss();
    }
}
