package com.german.keyboard.app.free.latin.setup;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.changes.Main_Activity;
import com.german.keyboard.app.free.latin.utils.LeakGuardHandlerWrapper;
import com.german.keyboard.app.free.latin.utils.UncachedInputMethodManagerUtils;
import com.module.ads.AddInitilizer;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public final class SetupWizardActivity extends Activity implements View.OnClickListener {
    static final String TAG = SetupWizardActivity.class.getSimpleName();

    private static final boolean FORCE_TO_SHOW_WELCOME_SCREEN = false;
    private InputMethodManager mImm;
    private View mSetupWizard;
    private View mWelcomeScreen;
    private View mSetupScreen;
    private View mActionStart;
    private SetupStepGroup mSetupStepGroup;
    private static final String STATE_STEP = "step";
    private int mStepNumber;
    private boolean mNeedsToAdjustStepNumberToSystemState;
    private static final int STEP_WELCOME = 0;
    private static final int STEP_1 = 1;
    private static final int STEP_2 = 2;
    private static final int STEP_3 = 3;
    private static final int STEP_LAUNCHING_IME_SETTINGS = 4;
    private static final int STEP_BACK_FROM_IME_SETTINGS = 5;
    private static int nextStep;
    SharedPreferences sharedPreferences;
    //private TextView mActionFinish;



    private SettingsPoolingHandler mHandler;

    private static final class SettingsPoolingHandler
            extends LeakGuardHandlerWrapper<SetupWizardActivity> {
        private static final int MSG_POLLING_IME_SETTINGS = 0;
        private static final long IME_SETTINGS_POLLING_INTERVAL = 200;

        private final InputMethodManager mImmInHandler;

        public SettingsPoolingHandler(@Nonnull final SetupWizardActivity ownerInstance,
                                      final InputMethodManager imm) {
            super(ownerInstance);
            mImmInHandler = imm;
        }

        @Override
        public void handleMessage(final Message msg) {
            final SetupWizardActivity setupWizardActivity = getOwnerInstance();
            if (setupWizardActivity == null) {
                return;
            }
            switch (msg.what) {
                case MSG_POLLING_IME_SETTINGS:
                    if (UncachedInputMethodManagerUtils.isThisImeEnabled(setupWizardActivity, mImmInHandler)) {
                        setupWizardActivity.invokeSetupWizardOfThisIme();
                        return;
                    }
                    startPollingImeSettings();
                    break;
            }
        }

        public void startPollingImeSettings() {
            sendMessageDelayed(obtainMessage(MSG_POLLING_IME_SETTINGS),
                    IME_SETTINGS_POLLING_INTERVAL);
        }

        public void cancelPollingImeSettings() {
            removeMessages(MSG_POLLING_IME_SETTINGS);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        super.onCreate(savedInstanceState);

        mImm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        mHandler = new SettingsPoolingHandler(this, mImm);

        setContentView(R.layout.setup_wizard);

        new AddInitilizer(getApplicationContext(),this,null)
                .adInitializer(findViewById(R.id.nativeTemplateView),findViewById(R.id.temp_add_text),findViewById(R.id.add_container));
        mSetupWizard = findViewById(R.id.setup_wizard);

        if (savedInstanceState == null) {
            mStepNumber = determineSetupStepNumberFromLauncher();
        } else {
            mStepNumber = savedInstanceState.getInt(STATE_STEP);
        }

        final String applicationName = getResources().getString(getApplicationInfo().labelRes);
        mWelcomeScreen = findViewById(R.id.setup_welcome_screen);


        mSetupScreen = findViewById(R.id.setup_steps_screen);

        final SetupStepIndicatorView indicatorView =
                findViewById(R.id.step_indicator);
        mSetupStepGroup = new SetupStepGroup(indicatorView);

        final SetupStep step1 = new SetupStep(STEP_1, applicationName,
                findViewById(R.id.setup_step1),
                R.string.setup_step1_title,
                R.string.setup_step1_finished_instruction,
                R.string.setup_step1_action, R.drawable.tutorial111, R.drawable.background_btn);
        final SettingsPoolingHandler handler = mHandler;
        step1.setAction(new Runnable() {
            @Override
            public void run() {
                invokeLanguageAndInputSettings();
                handler.startPollingImeSettings();
            }
        });
        mSetupStepGroup.addStep(step1);

        final SetupStep step2 = new SetupStep(STEP_2, applicationName,
                findViewById(R.id.setup_step2),
                R.string.setup_step2_title,
                0,
                R.string.setup_step2_action, R.drawable.tutorial_222, R.drawable.choose_background);
        step2.setAction(new Runnable() {
            @Override
            public void run() {
                invokeInputMethodPicker();
            }
        });
        mSetupStepGroup.addStep(step2);

        final SetupStep step3 = new SetupStep(STEP_3, applicationName,
                findViewById(R.id.setup_step3),
                R.string.setup_step3_title,
                0,
                R.string.setup_step3_action, R.drawable.tutorial333, R.drawable.change_language_selector);
        step3.setAction(new Runnable() {
            @Override
            public void run() {
                invokeSubtypeEnablerOfThisIme();
            }
        });
        mSetupStepGroup.addStep(step3);

        /*mActionFinish = findViewById(R.id.setup_finish);
        mActionFinish.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_setup_finish),
                null, null, null);
        mActionFinish.setOnClickListener(this);*/


        mActionStart = findViewById(R.id.setup_start_label);
        mActionStart.setOnClickListener(this);

        mActionStart.setVisibility(View.GONE);

        sharedPreferences = getSharedPreferences("Welcome_prefs", MODE_PRIVATE);


 /*       if (UncachedInputMethodManagerUtils.isThisImeEnabled(SetupWizardActivity.this, mImm)) {
            mStepNumber = STEP_2;
        }else if(){}
            mStepNumber = STEP_1;
        }*/

        if (!UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm)) {
            mStepNumber = STEP_1;
        }
        else if (!UncachedInputMethodManagerUtils.isThisImeCurrent(this, mImm)) {
            mStepNumber = STEP_2;
        }

        else{
            mStepNumber = STEP_3;
//            final InputMethodInfo imi = UncachedInputMethodManagerUtils.getInputMethodInfoOf(getPackageName(), mImm);
//            if (imi == null) {
//                startActivity(new Intent(SetupWizardActivity.this, Main_Activity.class));
//                finish();
//            }
        }
//            mStepNumber = STEP_3;



//        int lastStepFromSharedPref;
//        final int currentStep = determineSetupStepNumber();
//
//        if (currentStep == STEP_2) {
//            nextStep = STEP_1;
//        } else {
//            nextStep = mStepNumber;
//        }
//        if (mStepNumber != nextStep) {
//            mStepNumber = nextStep;
////            updateSetupStepView();
//        }

        updateSetupStepView();

        /*
        mStepNumber = STEP_1;
        updateSetupStepView();
//        mSetupWizard.setVisibility(View.GONE);
//        mWelcomeScreen.setVisibility(View.GONE);
//        mSetupScreen.setVisibility(View.VISIBLE);

        final boolean isStepActionAlreadyDone = mStepNumber < determineSetupStepNumber();
        mSetupStepGroup.enableStep(mStepNumber, isStepActionAlreadyDone);*/
    }

    @Override
    public void onClick(final View v) {
       /* if (v == mActionFinish) {
            finish();
            return;
        }*/
        final int currentStep = determineSetupStepNumber();

        if (v == mActionStart) {
            nextStep = STEP_1;
        } else if (currentStep == STEP_2) {
            nextStep = STEP_1;
        } else {
            nextStep = mStepNumber;
        }
        if (mStepNumber != nextStep) {
            mStepNumber = nextStep;
            updateSetupStepView();
        }


    }

    public void invokeSetupWizardOfThisIme() {
        final Intent intent = new Intent();
        intent.setClass(this, SetupWizardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                | Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        mNeedsToAdjustStepNumberToSystemState = true;
    }

    private void invokeSettingsOfThisIme() {
        final Intent intent = new Intent();
        intent.setClass(this, Main_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.putExtra(Main_Activity.EXTRA_ENTRY_KEY,
                Main_Activity.EXTRA_ENTRY_VALUE_APP_ICON);
        startActivity(intent);
    }

    void invokeLanguageAndInputSettings() {
        final Intent intent = new Intent();
        intent.setAction(Settings.ACTION_INPUT_METHOD_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivityForResult(intent, 100);
        mNeedsToAdjustStepNumberToSystemState = true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            //mStepNumber = STEP_2;
//            Toast.makeText(this, "step 2", Toast.LENGTH_SHORT).show();
//            sharedPreferences.getString("Welcome_prefs", "");
//            int lastStepFromSharedPref

        }else if(requestCode == 101){
            startActivity(new Intent(SetupWizardActivity.this, Main_Activity.class));
            final InputMethodInfo imi =
                    UncachedInputMethodManagerUtils.getInputMethodInfoOf(getPackageName(), mImm);
            if (imi == null) {
                Toast.makeText(this, "lang`uage", Toast.LENGTH_SHORT).show();
                return;
            }

        }
    }

    void invokeInputMethodPicker() {
        mImm.showInputMethodPicker();
        mNeedsToAdjustStepNumberToSystemState = true;
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
        startActivityForResult(intent, 101);


    }



    private int determineSetupStepNumberFromLauncher() {
        final int stepNumber = determineSetupStepNumber();
        if (stepNumber == STEP_1) {
            return STEP_WELCOME;
        }
        if (stepNumber == STEP_3) {
            return STEP_LAUNCHING_IME_SETTINGS;
        }
        return stepNumber;
    }

    private int determineSetupStepNumber() {
        mHandler.cancelPollingImeSettings();
        if (FORCE_TO_SHOW_WELCOME_SCREEN) {
            return STEP_1;
        }
        if (!UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm)) {
            return STEP_1;
        }
        if (!UncachedInputMethodManagerUtils.isThisImeCurrent(this, mImm)) {
            return STEP_2;
        }
        return STEP_3;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_STEP, mStepNumber);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mStepNumber = savedInstanceState.getInt(STATE_STEP);
    }

    private static boolean isInSetupSteps(final int stepNumber) {
        return stepNumber >= STEP_1 && stepNumber <= STEP_3;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (isInSetupSteps(mStepNumber)) {
            mStepNumber = determineSetupStepNumber();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (mStepNumber == STEP_LAUNCHING_IME_SETTINGS) {
//            mSetupWizard.setVisibility(View.INVISIBLE);
//            invokeSettingsOfThisIme();
//            mStepNumber = STEP_BACK_FROM_IME_SETTINGS;
//            return;
//        }
//        if (mStepNumber == STEP_BACK_FROM_IME_SETTINGS) {
//            finish();
//            return;
//        }

        if (!UncachedInputMethodManagerUtils.isThisImeEnabled(this, mImm)) {
            mStepNumber = STEP_1;
        }
        else if (!UncachedInputMethodManagerUtils.isThisImeCurrent(this, mImm)) {
            mStepNumber = STEP_2;

        }
        else {
            mStepNumber = STEP_3;
            invokeSettingsOfThisIme();
//            final InputMethodInfo imi = UncachedInputMethodManagerUtils.getInputMethodInfoOf(getPackageName(), mImm);
//            if (imi == null) {
//                startActivity(new Intent(SetupWizardActivity.this, Main_Activity.class));
//                finish();
//            }
        }
//            mStepNumber = STEP_3;
//
//        else
//            mStepNumber = STEP_LAUNCHING_IME_SETTINGS;

        updateSetupStepView();
    }

//    @Override
//    public void onBackPressed() {
//        if (mStepNumber == STEP_1) {
//            mStepNumber = STEP_WELCOME;
//            updateSetupStepView();
//            return;
//        }
//        super.onBackPressed();
//    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && mNeedsToAdjustStepNumberToSystemState) {
            mNeedsToAdjustStepNumberToSystemState = false;
            mStepNumber = determineSetupStepNumber();
            updateSetupStepView();
        }
    }

    private void updateSetupStepView() {
        mSetupWizard.setVisibility(View.VISIBLE);
        final boolean welcomeScreen = (mStepNumber == STEP_WELCOME);
        mWelcomeScreen.setVisibility(welcomeScreen ? View.VISIBLE : View.GONE);
        mSetupScreen.setVisibility(welcomeScreen ? View.GONE : View.VISIBLE);
        final boolean isStepActionAlreadyDone = mStepNumber < determineSetupStepNumber();
        mSetupStepGroup.enableStep(mStepNumber, isStepActionAlreadyDone);
    }

    static final class SetupStep implements View.OnClickListener {
        public final int mStepNo;
        private final View mStepView;
        private final String mFinishedInstruction;
        private final TextView mActionLabel;
        private Runnable mAction;

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public SetupStep(final int stepNo, final String applicationName,
                         final View stepView, final int title,
                         final int finishedInstruction, final int actionLabel, final int image, final int image1) {
            mStepNo = stepNo;
            mStepView = stepView;
            final Resources res = stepView.getResources();

            final TextView titleView = mStepView.findViewById(R.id.setup_step_title);
            titleView.setText(res.getString(title, applicationName));

            final ImageView imageView = mStepView.findViewById(R.id.imageView);
            imageView.setImageResource(image);

            final ImageView img = mStepView.findViewById(R.id.img);
            img.setImageResource(image1);


            mFinishedInstruction = (finishedInstruction == 0) ? null
                    : res.getString(finishedInstruction, applicationName);

            mActionLabel = mStepView.findViewById(R.id.setup_step_action_label);
            mActionLabel.setText(res.getString(actionLabel));

        }

        public void setEnabled(final boolean enabled, final boolean isStepActionAlreadyDone) {
            mStepView.setVisibility(enabled ? View.VISIBLE : View.GONE);
//            final TextView instructionView = mStepView.findViewById(
//                    R.id.setup_step_instruction);
//            instructionView.setText(isStepActionAlreadyDone ? mFinishedInstruction : null);
            mActionLabel.setVisibility(isStepActionAlreadyDone ? View.GONE : View.VISIBLE);
        }

        public void setAction(final Runnable action) {
            mActionLabel.setOnClickListener(this);
            mAction = action;
        }

        @Override
        public void onClick(final View v) {
            if (v == mActionLabel && mAction != null) {
                mAction.run();
                return;
            }
        }
    }

    static final class SetupStepGroup {
        private final SetupStepIndicatorView mIndicatorView;
        private final ArrayList<SetupStep> mGroup = new ArrayList<>();

        public SetupStepGroup(final SetupStepIndicatorView indicatorView) {
            mIndicatorView = indicatorView;
        }

        public void addStep(final SetupStep step) {
            mGroup.add(step);
        }

        public void enableStep(final int enableStepNo, final boolean isStepActionAlreadyDone) {
            for (final SetupStep step : mGroup) {
                step.setEnabled(step.mStepNo == enableStepNo, isStepActionAlreadyDone);
            }
            mIndicatorView.setIndicatorPosition(enableStepNo - STEP_1, mGroup.size());
        }
    }
}
