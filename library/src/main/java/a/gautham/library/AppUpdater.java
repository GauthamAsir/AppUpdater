package a.gautham.library;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;

import a.gautham.library.async_tasks.DownloadTask;
import a.gautham.library.async_tasks.UtilsAsync;
import a.gautham.library.helper.Display;
import a.gautham.library.models.Update;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppUpdater {

    private Context context;
    private String dialogTitle, dialogMessage, dialogBtnPositiveText, dialogBtnNegativeText, gitUsername, gitRepoName;
    private String notificationTitle, notificationContent;
    private boolean dialogCancelable;
    private int dialogAlertStyle, dialog_icon;
    private UpdateListener updateListener;
    private UtilsAsync utilsAsync;
    private static final String TAG = "AppUpdater: ";
    private DownloadTask downloadTask;
    private Display display;

    public AppUpdater(Context context) {
        this.context = context;

        //NOTIFICATION
        this.notificationTitle = context.getString(R.string.app_updater_new_update_available);
        this.notificationContent = context.getString(R.string.app_updater_update_msg_content);

        // DIALOG
        this.dialogTitle = context.getString(R.string.app_updater_new_update_available);
        this.dialogBtnPositiveText = context.getString(R.string.app_updater_update);
        this.dialogBtnNegativeText = context.getString(R.string.app_updater_cancel);
        this.dialogMessage = "";
        this.dialogCancelable = false;
        this.dialog_icon = R.drawable.ic_system_update;

        this.updateListener = new UpdateListener() {
            @Override
            public void onSuccess(Update update, boolean isUpdateAvailable) {
                if (isUpdateAvailable){

                    if (display!=null){

                        switch (display){
                            case DIALOG:
                                showDialog(update);
                                break;
                            case SNACKBAR:
                                showSnackBar(update);
                                break;
                            case NOTIFICATION:
                                showNotification(context);
                                break;
                        }

                    }else {
                        Log.e(TAG,context.getString(R.string.app_updater_null_disply_msg));
                    }
                }
            }

            @Override
            public void onFailed(String error) {
                Log.e(TAG,error);
            }
        };

    }

    public AppUpdater withListener(UpdateListener updateListener){
        this.updateListener = updateListener;
        return this;
    }

    public AppUpdater setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
        return this;
    }

    public AppUpdater setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
        return this;
    }

    public AppUpdater setDialogBtnPositive(String dialogBtnPositive) {
        this.dialogBtnPositiveText = dialogBtnPositive;
        return this;
    }

    public AppUpdater setDialogBtnNegative(String dialogBtnNegative) {
        this.dialogBtnNegativeText = dialogBtnNegative;
        return this;
    }

    public AppUpdater setDialogCancelable(boolean dialogCancelable) {
        this.dialogCancelable = dialogCancelable;
        return this;
    }

    public AppUpdater setDialogAlertStyle(int dialogAlertStyle) {
        this.dialogAlertStyle = dialogAlertStyle;
        return this;
    }

    public AppUpdater setDialog_icon(int dialog_icon) {
        this.dialog_icon = dialog_icon;
        return this;
    }

    public AppUpdater setUpGithub(String gitUsername, String gitRepoName){
        this.gitUsername = gitUsername;
        this.gitRepoName = gitRepoName;
        return this;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public void start(){

        utilsAsync = new UtilsAsync(context, gitUsername, gitRepoName, updateListener);
        utilsAsync.execute();

    }

    private void showNotification(Context context) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            initNotificationChannel(context, notificationManager);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,
                context.getString(R.string.app_updater_channel_id));

        Intent intent = new Intent(context,UpdateActivity.class);
        intent.putExtra("username",gitUsername);
        intent.putExtra("repoName",gitRepoName);
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setContentIntent(pendingIntent)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent))
                .setSmallIcon(R.drawable.ic_system_update)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(1, builder.build());

    }

    private static void initNotificationChannel(Context context, NotificationManager notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    context.getString(R.string.app_updater_channel_id),
                    context.getString(R.string.app_updater_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    private void showSnackBar(Update update1) {

        Activity activity = (Activity) context;

        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content),
                context.getString(R.string.app_updater_new_update_available), Snackbar.LENGTH_LONG);
        snackbar.setAction(context.getResources().getString(R.string.app_updater_update), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadTask = new DownloadTask(context,update1);
                downloadTask.execute(update1.getDownloadUrl());

            }
        });
        snackbar.show();

    }

    private void showDialog(Update update1) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context, dialogAlertStyle)
                .setTitle(dialogTitle)
                .setCancelable(dialogCancelable)
                .setIcon(dialog_icon);

        if (dialogMessage.isEmpty()){
            builder.setMessage(String.format("New Update %s is Available. By Downloading the latest " +
                    "update, you will get the latest features, " +
                    "improvements and bug fixes. Do you want to Update App?",update1.getLatest_version()));
        }else {
            builder.setMessage(dialogMessage);
        }

        builder.setPositiveButton(dialogBtnPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadTask = new DownloadTask(context, update1);
                downloadTask.execute(update1.getDownloadUrl());
            }
        });
        builder.setNegativeButton(dialogBtnNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    public void stop(){
        if (utilsAsync != null && utilsAsync.isCancelled()){
            utilsAsync.cancel(true);
        }
    }

}
