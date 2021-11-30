package com.german.keyboard.app.free.changes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.german.keyboard.app.free.inputmethod.changes.ThemeActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;
import com.module.ads.AddInitilizer;
import com.module.ads.MySharedPref;
import com.module.ads.OnAdsClosedCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.settings.SettingsActivity;
import com.german.keyboard.app.free.latin.utils.UncachedInputMethodManagerUtils;

public class Main_Activity extends LocalizationActivity {

    PrefManager prefManager;
    boolean isInputDeviceEnabled;
    private final int MY_REQUEST_CODE = 999;
    private AppUpdateManager appUpdateManager;
    Animation animation;
    View alertView;
    public static final Integer RecordAudioRequestCode = 1;
    int count = 0;
    boolean isPurchesed = false;
    Advance3DDrawerLayout drawerLayout;
    NavigationView navView;
    private InputMethodManager mImm;
    public static final String PREF_KEY_FIRST_START = "com.german.keyboard.app.free.changes.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;
    AddInitilizer addInitilizer;
    public static final String EXTRA_ENTRY_KEY = "entry";
    public static final String EXTRA_ENTRY_VALUE_APP_ICON = "app_icon";

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS= 7;

    Boolean testActivityAddsFlag = false;

    public void furtherCall()
    {
        if (count == 1) {
            enableKeyboard_Func();
        } else if (count == 2) {
            setKeyboard_Func();
        } else if (count == 3) {
           startActivity(new Intent(Main_Activity.this, ThemeActivity.class));
        } else if (count == 4) {
            changeLanguage();
        }else if (count == 5) {
            startActivity(new Intent(Main_Activity.this,TestActivity.class));
        }
    }


    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAndroidVersion();

        addInitilizer = new AddInitilizer(getApplicationContext(), this, new OnAdsClosedCallBack() {
            @Override
            public void onCallBack() {
                furtherCall();
            }
        });
        addInitilizer.loadBanner(findViewById(R.id.banner_container));
        addInitilizer.adInitializer(null,null,null);


        mImm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        drawerLayout = findViewById(R.id.drawer_layout);
        navView = findViewById(R.id.nav_view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        }


        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.MY_PREF),MODE_PRIVATE);
        isPurchesed = sharedPreferences.getBoolean(getString(R.string.is_purchsed),false);

        ProgressDialog progressDialog = new ProgressDialog(Main_Activity.this);
        progressDialog.setMessage(getString(R.string.loading_ad));

        if (getSupportActionBar() != null) getSupportActionBar().hide();



        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        drawerLayout.setViewScale(GravityCompat.START, 0.96f);
        drawerLayout.setRadius(GravityCompat.START, 20f);
        drawerLayout.setViewElevation(GravityCompat.START, 8f);
        drawerLayout.setViewRotation(GravityCompat.START, 15f);
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.nav_test_keyboard:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 5;
                    testActivityAddsFlag = !testActivityAddsFlag;
                    if(testActivityAddsFlag) {
                        if(!addInitilizer.showInterstailAdd())
                            startActivity(new Intent(Main_Activity.this,TestActivity.class));
                    }else{
                        startActivity(new Intent(Main_Activity.this,TestActivity.class));
                    }


                    break;
                case R.id.nav_enable_keyboard:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 1;
                    if(!addInitilizer.showInterstailAdd())
                        furtherCall();
                    break;
                case R.id.nav_select_keyboard:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 2;
                    if (ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        checkPermission();
                    }else {
                        if(!addInitilizer.showInterstailAdd())
                            furtherCall();
                    }
                    break;
                case R.id.nav_themes:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 3;
                    if(!addInitilizer.showInterstailAdd())
                        furtherCall();
                    break;
                case R.id.nav_language:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 4;
                        furtherCall();
                    break;
                case R.id.nav_rate:
                    rateus();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_feedback:
                    feedback();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_more_apps:
                    moreApps();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_app_policy:
                    startActivity(new Intent(Main_Activity.this, PrivacyPolicy.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_share:
                    share();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_change_language:
                    invokeSubtypeEnablerOfThisIme();
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
            }
            return true;
        });

        findViewById(R.id.drawerBtn).setOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        findViewById(R.id.testBtn).setOnClickListener(view -> {
            count = 5;
            testActivityAddsFlag = !testActivityAddsFlag;
            if(testActivityAddsFlag) {
                if(!addInitilizer.showInterstailAdd())
                    startActivity(new Intent(Main_Activity.this,TestActivity.class));
            }else{
                startActivity(new Intent(Main_Activity.this,TestActivity.class));
            }
        });

        findViewById(R.id.setting).setOnClickListener(view -> startActivity(new Intent(Main_Activity.this, SettingsActivity.class)));
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(Main_Activity.this, R.style.my_dialog));
        alertView = inflater.inflate(R.layout.exit, findViewById(android.R.id.content), false);

        CheckAppUpdate();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);
        prefManager = new PrefManager(this);

        findViewById(R.id.btn_changeTheme).setOnClickListener(v -> {
            count = 3;
            if(!addInitilizer.showInterstailAdd())
                furtherCall();
        });

        findViewById(R.id.btn_SetKeyboard).setOnClickListener(v -> {
            count = 2;

            if (ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }else
            if(!addInitilizer.showInterstailAdd())
                furtherCall();
        });
        findViewById(R.id.btn_setting).setOnClickListener(view -> {
            Log.e("***cgange", "clicked");
            count = 4;
                furtherCall();

        });


        if(new MySharedPref(this).isPurshed()){
            findViewById(R.id.premium).setVisibility(View.GONE);
        }else{
            findViewById(R.id.premium).setOnClickListener(view -> startActivity(new Intent(Main_Activity.this, Premium.class)));
        }

    }

    void invokeSubtypeEnablerOfThisIme() {
        final InputMethodInfo imi =
                UncachedInputMethodManagerUtils.getInputMethodInfoOf(getPackageName(), mImm);
        if (imi == null) {
            return;
        }
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, imi.getId());
        startActivity(intent);
    }
    private void moreApps() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=Multi+Themes+Keyboard+%26+Emoji+Keyboard")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void feedback() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","multithemeskeyboard@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
        }
    }

    public void enableKeyboard_Func() {
        if (isInputDeviceEnabled) {
            View parentLayout = findViewById(android.R.id.content);
            Snackbar.make(parentLayout, getString(R.string.select_the_keyboard), Snackbar.LENGTH_SHORT).show();
        } else {

            final InputMethodInfo imi =
                    UncachedInputMethodManagerUtils.getInputMethodInfoOf(getPackageName(), mImm);
            if (imi == null) {
                return;
            }
            Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableIntent);
            prefManager.enabledKeyboard(true);
            check_Keyboard();
        }
    }

    public void setKeyboard_Func() {
        if (isInputDeviceEnabled) {
            InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
        } else {
            InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
        }
    }


    public void changeLanguage() {
        Locale locale = getCurrentLanguage();
        String language = locale.getLanguage();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (language.endsWith("de")) {
            com.german.keyboard.app.free.latin.settings.Settings.setSelectedLaguage(pref,"en");
            setLanguage("en");

        } else {
            com.german.keyboard.app.free.latin.settings.Settings.setSelectedLaguage(pref,"de");
            setLanguage("de");

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);
        if (alertView.getParent() != null) {
            ((ViewGroup) alertView.getParent()).removeView(alertView);
        }
        builder.setView(alertView);

        TextView btn_positive = alertView.findViewById(R.id.rateBtn);
        TextView btn_negative = alertView.findViewById(R.id.exitBtn);



        final AlertDialog dialog = builder.create();

        btn_positive.setOnClickListener(v -> rateus());

        btn_negative.setOnClickListener(view -> {
            finishAffinity();

        });

        addInitilizer.setnativeAddOnView(alertView.findViewById(R.id.alertNativeAd));
        dialog.show();
    }

    public void share() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
            String shareMessage = getString(R.string.recomendation);
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + getPackageName() + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void rateus() {
        Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
        }
    }

    public void check_Keyboard() {

        String packageLocal = getPackageName();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        List<InputMethodInfo> list = inputMethodManager.getEnabledInputMethodList();

        for (InputMethodInfo inputMethod : list) {
            String packageName = inputMethod.getPackageName();
            if (packageName.equals(packageLocal)) {
                isInputDeviceEnabled = true;
                prefManager.enabledKeyboard(true);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        Log.d("in fragment on request", "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("in fragment on request", "CAMERA & WRITE_EXTERNAL_STORAGE READ_EXTERNAL_STORAGE permission granted");
                    } else {
                        Log.d("in fragment on request", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            showDialogOK("Camera and Storage Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }
        }

        if (permissions.length == 0) {
            return;
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        } else if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    //denied
                    Log.e("denied", permission);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.title_permission_required_dialog)
                            .setMessage(getString(R.string.you_must_allow_permission) +
                                    getString(R.string.otherwise_application_will_not_work) +
                                    getString(R.string.go_to_settings_and_allow) +
                                    getString(R.string.if_you_cancel_application_cloase))
                            .setPositiveButton(R.string.go_to_settings, (dialog, which) -> {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            })
                            .setNegativeButton(R.string.cancel_dialog, (dialog, which) -> finish())
                            .setCancelable(false)
                            .create()
                            .show();


                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        //allowed
                        Log.e("allowed", permission);
                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getString(R.string.title_permission_required_dialog))
                        .setMessage(getString(R.string.you_force_fully_denied_permissions) +
                                getString(R.string.open_settings_and_allow_themall))
                        .setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", getPackageName(), null));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        })
                        .setNegativeButton(getString(R.string.cancel_dialog), (dialog, which) -> {
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        }
    }

    private void CheckAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this);
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            Main_Activity.this,
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void recreate() {
        finish();
        //findViewById(R.id.btn_EnableKeyBoard).clearAnimation();
        findViewById(R.id.btn_SetKeyboard).clearAnimation();
        startActivity(new Intent(Main_Activity.this, Main_Activity.class));
    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermissions();

        } else {
            // code for lollipop and pre-lollipop devices
        }

    }


    private boolean checkAndRequestPermissions() {
        int camera = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int wtite = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (wtite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (read != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }



    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, false)
                        .apply();
            } else {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(PREF_KEY_FIRST_START, true)
                        .apply();
                finish();
            }
        }else if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {

                View parentLayout = findViewById(android.R.id.content);
                Snackbar snackbar = Snackbar
                        .make(parentLayout, R.string.install_failed, Snackbar.LENGTH_LONG);
                snackbar.show();
            }
        }
    }
}
