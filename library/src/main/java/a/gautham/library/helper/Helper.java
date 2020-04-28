package a.gautham.library.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Helper {

    public static boolean isGitHubValid(String gitUsername, String gitRepoName) {

        if (gitUsername == null || gitRepoName == null){
            return false;
        }else
            return !gitUsername.isEmpty() && !gitRepoName.isEmpty() && gitUsername.length() != 0 && gitRepoName.length() != 0;
    }

    public static boolean isUpdateAvailable(double currentAppversion, double latestAppversion){

        return currentAppversion < latestAppversion;
    }

    public static Boolean isNetworkAvailable(Context context) {
        Boolean res = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                res = networkInfo.isConnected();
            }
        }

        return res;
    }

    public static String getAppVersion(Context context){

        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(),0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-","");
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

}
