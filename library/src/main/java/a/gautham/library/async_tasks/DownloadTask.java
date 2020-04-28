package a.gautham.library.async_tasks;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import a.gautham.library.DownloadDialog;
import a.gautham.library.R;
import a.gautham.library.models.Update;

public class DownloadTask extends AsyncTask<String, Integer, String> {

    private WeakReference<Context> contextRef;
    private String app_name;
    private PowerManager.WakeLock mWakeLock;
    private DownloadDialog downloadDialog;

    public DownloadTask(Context context, Update update) {
        this.contextRef = new WeakReference<>(context);
        this.app_name = update.getAsset_name();
        downloadDialog = new DownloadDialog(contextRef.get(), this);
        downloadDialog.setDownloadName(update.getAsset_name().replace(".apk",""));
        downloadDialog.setDownloadSize(String.format(Locale.US,"%.2f MB",update.getAsses_sizeMb()));

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        PowerManager pm = (PowerManager) contextRef.get().getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
        downloadDialog.show();
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;

        try {
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(contextRef.get().getExternalFilesDir(null).getAbsolutePath() + "/"+app_name);

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);

            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);

        downloadDialog.setProgressPercent(progress[0] + "%");
        downloadDialog.setProgressBaPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {

        mWakeLock.release();
        downloadDialog.hide();
        if (result != null){
            Toast.makeText(contextRef.get(), R.string.app_updater_download_error+result, Toast.LENGTH_LONG).show();
        }
        else{

            Toast.makeText(contextRef.get(),R.string.app_updater_update_downloaded, Toast.LENGTH_SHORT).show();

            File file = new File(contextRef.get().getExternalFilesDir(null).getAbsolutePath()
                    + "/" + app_name);
            Uri data = FileProvider.getUriForFile(contextRef.get(), contextRef.get().getPackageName() +".provider",file);

            Intent installAPK = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            installAPK.setDataAndType(data,"application/vnd.android.package-archive");
            installAPK.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            contextRef.get().startActivity(installAPK);
        }
    }
}