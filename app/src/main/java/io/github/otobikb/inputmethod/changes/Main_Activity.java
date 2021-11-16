package io.github.otobikb.inputmethod.changes;

import static android.content.ContentValues.TAG;

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
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.infideap.drawerbehavior.Advance3DDrawerLayout;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

import io.github.otobikb.inputmethod.intro.FinnishActivity;
import io.github.otobikb.inputmethod.latin.R;
import io.github.otobikb.inputmethod.latin.setup.SetupActivity;
import io.github.otobikb.inputmethod.latin.setup.SetupWizardActivity;
import io.github.otobikb.inputmethod.latin.utils.UncachedInputMethodManagerUtils;

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
    public static final String PREF_KEY_FIRST_START = "io.github.otobikb.inputmethod.changes.PREF_KEY_FIRST_START";
    public static final int REQUEST_CODE_INTRO = 1;



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
        } else if (count == 5) {
            startActivity(new Intent(Main_Activity.this, HowtoUse.class));
        }
    }


    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean firstStart = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_KEY_FIRST_START, true);

        if (firstStart) {
            Intent intent = new Intent(this, FinnishActivity.class);
            startActivityForResult(intent, REQUEST_CODE_INTRO);
        }

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
                    startActivity(new Intent(Main_Activity.this,TestActivity.class));
                    break;
                case R.id.nav_enable_keyboard:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 1;
                    furtherCall();
                    break;
                case R.id.nav_select_keyboard:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 2;
                    if (ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        checkPermission();
                    }else {
                        furtherCall();
                    }
                    break;
                case R.id.nav_themes:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    count = 3;
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
                    //startActivity(new Intent(Main_Activity.this, PrivacyPolicy.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                case R.id.nav_share:
                    share();
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
        findViewById(R.id.testBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main_Activity.this, TestActivity.class));
            }
        });

        findViewById(R.id.setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Main_Activity.this, SetupActivity.class));
            }
        });
        LayoutInflater inflater = LayoutInflater.from(new ContextThemeWrapper(Main_Activity.this, R.style.my_dialog));
        alertView = inflater.inflate(R.layout.exit, findViewById(android.R.id.content), false);


        // verifyPermissions();
        CheckAppUpdate();
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim);
        prefManager = new PrefManager(this);

        // check if our keyboard is enabled as input method
        findViewById(R.id.btn_EnableKeyBoard).startAnimation(animation);
        findViewById(R.id.btn_changeTheme).setOnClickListener(v -> {
            count = 3;
            furtherCall();
        });

        findViewById(R.id.btn_EnableKeyBoard).setOnClickListener(v -> {
            count = 1;
            furtherCall();
        });

        findViewById(R.id.btn_SetKeyboard).setOnClickListener(v -> {
            count = 2;

            if (ContextCompat.checkSelfPermission(Main_Activity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }else
                furtherCall();
        });
        findViewById(R.id.btn_setting).setOnClickListener(view -> {
            Log.e("***cgange", "clicked");


            count = 4;
            furtherCall();

        });

        findViewById(R.id.how_to_use).setOnClickListener(view -> {
            count = 5;
            furtherCall();

        });

        findViewById(R.id.premium).setOnClickListener(view -> startActivity(new Intent(Main_Activity.this, Premium.class)));
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
            final Intent intent = new Intent();
            intent.setAction(Settings.ACTION_INPUT_METHOD_SUBTYPE_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(Settings.EXTRA_INPUT_METHOD_ID, imi.getId());
            startActivity(intent);
            check_Keyboard();

//
//            Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
//            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(enableIntent);
//            prefManager.enabledKeyboard(true);
//            check_Keyboard();
        }
    }

    
    
    
    public void setKeyboard_Func() {
        //main work
        if (isInputDeviceEnabled) {
            InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
            imeManager.showInputMethodPicker();
            findViewById(R.id.btn_SetKeyboard).clearAnimation();
        } else {
            new AlertDialog.Builder(Main_Activity.this)
                    .setTitle(R.string.title_enable_keyboard)
                    .setMessage(R.string.please_first_enable_keyboard)
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(R.string.enable_dialog, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent enableIntent = new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS);
                            enableIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(enableIntent);
                            prefManager.enabledKeyboard(true);
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(R.string.cancel_dialog, (dialog, which) -> dialog.dismiss())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void changeLanguage() {
        Locale locale = getCurrentLanguage();
        String language = locale.getLanguage();
        if (language.endsWith("my"))
            setLanguage("en");
        else
            setLanguage("my");
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
            //e.toString();
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
        if (isInputDeviceEnabled) {
            findViewById(R.id.btn_SetKeyboard).startAnimation(animation);
            findViewById(R.id.btn_EnableKeyBoard).clearAnimation();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
        }
        if (!allPermissionsGranted) {
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
        }  //act according to the request code used while requesting the permission(s).

    }

    private void CheckAppUpdate() {
        appUpdateManager = (AppUpdateManager) AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    // For a flexible update, use AppUpdateType.FLEXIBLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                            AppUpdateType.IMMEDIATE,
                            // The current activity making the update request.
                            Main_Activity.this,
                            // Include a request code to later monitor this update request.
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void recreate() {
        finish();
        findViewById(R.id.btn_EnableKeyBoard).clearAnimation();
        findViewById(R.id.btn_SetKeyboard).clearAnimation();
        startActivity(new Intent(Main_Activity.this, Main_Activity.class));
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
