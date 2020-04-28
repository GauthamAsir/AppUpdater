package a.gautham.library;

import a.gautham.library.models.Update;

public interface UpdateListener{

    void onSuccess(Update update, boolean isUpdateAvailable);

    void onFailed(String error);
}