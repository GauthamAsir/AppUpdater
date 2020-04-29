# AppUpdater

## How to include to your project
Add the repository to your project **build.gradle**:
```Gradle
repositories {
    maven {
        url "https://jitpack.io"
    }
}
```

And add the library to your module **build.gradle**:
```Gradle
dependencies {
    implementation 'com.github.GauthamAsir:AppUpdater:1.1'
}
```

## Instructions
Library compares the App version name & Github Tag Name.
It has native downloader which downloads the apk from github releases and
It installs the apk after download is completed.
### Note
Github Tag Name should not contain any alphabets, it can be decimal (Eg: 1.1)
Make sure you enter proper Github Username and Repo

## Usage

## Displaying a dialog, Snackbar notification or activity

### Dialog
```Java
AppUpdater appUpdater = new AppUpdater(this);
appUpdater.setDisplay(Display.DIALOG);
appUpdater.setUpGithub("username", "reponame");
appUpdater.start();
```

### For SnackBar
```Java
AppUpdater appUpdater = new AppUpdater(this);
appUpdater.setDisplay(Display.SNACKBAR);
appUpdater.setUpGithub("username", "reponame");
appUpdater.start();
```

### For Notification
```Java
AppUpdater appUpdater = new AppUpdater(this);
appUpdater.setDisplay(Display.NOTIFICATION);
appUpdater.setUpGithub("username", "reponame");
appUpdater.start();
```
On Click of Notification it navigates to the Update Activity.

### Activity 
It handles the changelogs also, which is the release description from Github
```Java
Intent updateIntent = new Intent(getApplicationContext(),UpdateActivity.class);
updateIntent.putExtra("username","GauthamAsir");
updateIntent.putExtra("repoName","AppUpdater");
startActivity(updateIntent);
```

## Customizations

### Customizing Dialog

###### Dialog Style
```Java
appUpdater.setDialogStyle(R.style.dialogAlertStyle);
```
```Java
<style name="dialogAlertStyle" parent="ThemeOverlay.MaterialComponents.MaterialAlertDialog">
    <item name="colorPrimary">@color/colorAccent</item>
    <item name="colorSecondary">@color/colorAccent</item>
</style>
```
###### Dialog Buttons Text
```Java
appUpdater.setDialogBtnPositive("Update");
appUpdater.setDialogBtnNegative("Dismiss");
```
###### Dialog Icon
```Java
appUpdater.setDialog_icon(R.drawable.ic_system_update_black_24dp);
```
###### Dialog Title
```Java
appUpdater.setDialogTitle("new title");
```
###### Dialog Message
```Java
appUpdater.setDialogMessage("message");
```
###### Dialog Set Cancelable
By default its false
```Java
appUpdater.setDialogCancelable(true);
```
