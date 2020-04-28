package a.gautham.library.async_tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.lang.ref.WeakReference;

import a.gautham.library.R;
import a.gautham.library.UpdateListener;
import a.gautham.library.helper.Helper;
import a.gautham.library.helper.ServiceGenerator;
import a.gautham.library.models.AssestsModel;
import a.gautham.library.models.GitRelease;
import a.gautham.library.models.Update;
import a.gautham.library.service.GithubService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilsAsync extends AsyncTask {

    private WeakReference<Context> contextRef;
    private String gitUsername, gitRepoName;
    private UpdateListener updateListener;
    private static final String TAG = "UtilsAsync: ";

    public UtilsAsync(Context context, String gitUsername, String gitRepoName, UpdateListener updateListener) {
        this.contextRef = new WeakReference<>(context);
        this.gitUsername = gitUsername;
        this.gitRepoName = gitRepoName;
        this.updateListener = updateListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        Context context = contextRef.get();
        if (context == null || updateListener == null){
            cancel(true);
        }else if (Helper.isNetworkAvailable(context)){
            if (!Helper.isGitHubValid(gitUsername, gitRepoName)){
                updateListener.onFailed(context.getString(R.string.app_updater_git_empty));
                cancel(true);
            }
        }else {
            updateListener.onFailed(context.getString(R.string.app_updater_no_internet));
            cancel(true);
        }

    }

    @Override
    protected Object doInBackground(Object[] objects) {

        try {
            Call<GitRelease> callAsync;
            GithubService githubService = ServiceGenerator.build().create(GithubService.class);
            callAsync = githubService.getReleases(gitUsername, gitRepoName);

            callAsync.enqueue(new Callback<GitRelease>() {
                @Override
                public void onResponse(Call<GitRelease> call, Response<GitRelease> response) {

                    if (response.code() == 200){

                        GitRelease releases = response.body();
                        AssestsModel assestsModel = releases.getAssestsModels().get(0);

                        double size = assestsModel.getSize();
                        double fileSizeInKB = size / 1024;
                        double fileSizeInMB = fileSizeInKB / 1024;
                        double fileSizeInGb = fileSizeInMB / 1024;

                        Update update = new Update(assestsModel.getDownload_url(),
                                assestsModel.getAssest_name(),
                                releases.getRelease_title(),
                                releases.getRelease_description(),
                                releases.getRelease_tag_name(),
                                assestsModel.getDownload_count(),
                                assestsModel.getSize(), fileSizeInKB, fileSizeInMB, fileSizeInGb);

                        double currentAppversion = Double.parseDouble(Helper.getAppVersion(contextRef.get()));
                        double latestAppversion = Double.parseDouble(releases.getRelease_tag_name());

                        updateListener.onSuccess(update,Helper.isUpdateAvailable(currentAppversion, latestAppversion));

                    }else {
                        updateListener.onFailed("Failed: " + response.message());
                        cancel(true);
                    }

                }

                @Override
                public void onFailure(Call<GitRelease> call, Throwable t) {
                    Log.e(TAG + "Error: ",t.getMessage());
                    updateListener.onFailed("Error: "+ t.getMessage());
                    cancel(true);
                }
            });

        }catch (Exception e){
            Log.e(TAG + "Failed: ",e.getMessage());
            updateListener.onFailed("Failed: "+ e.getMessage());
            cancel(true);
        }

        return null;
    }
}