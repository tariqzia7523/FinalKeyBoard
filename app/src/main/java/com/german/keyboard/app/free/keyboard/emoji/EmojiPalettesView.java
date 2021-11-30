package com.german.keyboard.app.free.keyboard.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.german.keyboard.app.free.keyboard.Key;
import com.german.keyboard.app.free.keyboard.KeyboardActionListener;
import com.german.keyboard.app.free.keyboard.KeyboardLayoutSet;
import com.german.keyboard.app.free.keyboard.internal.KeyDrawParams;
import com.german.keyboard.app.free.keyboard.internal.KeyVisualAttributes;
import com.german.keyboard.app.free.keyboard.internal.KeyboardIconsSet;
import com.german.keyboard.app.free.latin.AudioAndHapticFeedbackManager;
import com.german.keyboard.app.free.R;
import com.german.keyboard.app.free.latin.RichInputMethodSubtype;
import com.german.keyboard.app.free.latin.common.Constants;
import com.german.keyboard.app.free.latin.utils.ResourceUtils;

import androidx.viewpager2.widget.ViewPager2;

import static com.german.keyboard.app.free.latin.common.Constants.NOT_A_COORDINATE;

public final class EmojiPalettesView extends LinearLayout
        implements OnTabChangeListener, View.OnClickListener, View.OnTouchListener,
        EmojiPageKeyboardView.OnKeyEventListener {
    private final int mFunctionalKeyBackgroundId;
    private final int mSpacebarBackgroundId;
    private final boolean mCategoryIndicatorEnabled;
    private final int mCategoryIndicatorDrawableResId;
    private final int mCategoryIndicatorBackgroundResId;
    private final int mCategoryPageIndicatorColor;
    private final int mCategoryPageIndicatorBackground;
    private EmojiPalettesAdapter mEmojiPalettesAdapter;
    private final EmojiLayoutParams mEmojiLayoutParams;
    private final DeleteKeyOnTouchListener mDeleteKeyOnTouchListener;

    private ImageButton mDeleteKey;
    private TextView mAlphabetKeyLeft;
    private View mSpacebar;
    // TODO: Remove this workaround.
    private View mSpacebarIcon;
    private TabHost mTabHost;
    private ViewPager2 mEmojiPager;
    private int mCurrentPagerPosition = 0;
    private EmojiCategoryPageIndicatorView mEmojiCategoryPageIndicatorView;

    private KeyboardActionListener mKeyboardActionListener = KeyboardActionListener.EMPTY_LISTENER;

    private final EmojiCategory mEmojiCategory;

    public EmojiPalettesView(final Context context, final AttributeSet attrs) {
        this(context, attrs, R.attr.emojiPalettesViewStyle);
    }

    public EmojiPalettesView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray keyboardViewAttr = context.obtainStyledAttributes(attrs,
                R.styleable.KeyboardView, defStyle, R.style.KeyboardView);
        final int keyBackgroundId = keyboardViewAttr.getResourceId(
                R.styleable.KeyboardView_keyBackground, 0);
        mFunctionalKeyBackgroundId = keyboardViewAttr.getResourceId(
                R.styleable.KeyboardView_functionalKeyBackground, keyBackgroundId);
        mSpacebarBackgroundId = keyboardViewAttr.getResourceId(
                R.styleable.KeyboardView_spacebarBackground, keyBackgroundId);
        keyboardViewAttr.recycle();
        final KeyboardLayoutSet.Builder builder = new KeyboardLayoutSet.Builder(
                context, null /* editorInfo */);
        final Resources res = context.getResources();
        mEmojiLayoutParams = new EmojiLayoutParams(res);
        builder.setSubtype(RichInputMethodSubtype.getEmojiSubtype());
        builder.setKeyboardGeometry(ResourceUtils.getDefaultKeyboardWidth(res),
                mEmojiLayoutParams.mEmojiKeyboardHeight);
        final KeyboardLayoutSet layoutSet = builder.build();
        final TypedArray emojiPalettesViewAttr = context.obtainStyledAttributes(attrs,
                R.styleable.EmojiPalettesView, defStyle, R.style.EmojiPalettesView);
        mEmojiCategory = new EmojiCategory(PreferenceManager.getDefaultSharedPreferences(context),
                res, layoutSet, emojiPalettesViewAttr);
        mCategoryIndicatorEnabled = emojiPalettesViewAttr.getBoolean(
                R.styleable.EmojiPalettesView_categoryIndicatorEnabled, false);
        mCategoryIndicatorDrawableResId = emojiPalettesViewAttr.getResourceId(
                R.styleable.EmojiPalettesView_categoryIndicatorDrawable, 0);
        mCategoryIndicatorBackgroundResId = emojiPalettesViewAttr.getResourceId(
                R.styleable.EmojiPalettesView_categoryIndicatorBackground, 0);
        mCategoryPageIndicatorColor = emojiPalettesViewAttr.getColor(
                R.styleable.EmojiPalettesView_categoryPageIndicatorColor, 0);
        mCategoryPageIndicatorBackground = emojiPalettesViewAttr.getColor(
                R.styleable.EmojiPalettesView_categoryPageIndicatorBackground, 0);
        emojiPalettesViewAttr.recycle();
        mDeleteKeyOnTouchListener = new DeleteKeyOnTouchListener();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final Resources res = getContext().getResources();
        final int width = ResourceUtils.getDefaultKeyboardWidth(res)
                + getPaddingLeft() + getPaddingRight();
        final int height = ResourceUtils.getDefaultKeyboardHeight(res)
                + res.getDimensionPixelSize(R.dimen.config_suggestions_strip_height)
                + getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(width, height);
    }

    private void addTab(final TabHost host, final int categoryId) {
        final String tabId = EmojiCategory.getCategoryName(categoryId, 0 /* categoryPageId */);
        final TabHost.TabSpec tspec = host.newTabSpec(tabId);
        tspec.setContent(R.id.emoji_keyboard_dummy);
        final ImageView iconView = (ImageView)LayoutInflater.from(getContext()).inflate(
                R.layout.emoji_keyboard_tab_icon, null);
        // TODO: Replace background color with its own setting rather than using the
        //       category page indicator background as a workaround.
        iconView.setBackgroundColor(mCategoryPageIndicatorBackground);
        iconView.setImageResource(mEmojiCategory.getCategoryTabIcon(categoryId));
        iconView.setContentDescription(mEmojiCategory.getAccessibilityDescription(categoryId));
        tspec.setIndicator(iconView);
        host.addTab(tspec);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTabHost = findViewById(R.id.emoji_category_tabhost);
        mTabHost.setup();
        for (final EmojiCategory.CategoryProperties properties
                : mEmojiCategory.getShownCategories()) {
            addTab(mTabHost, properties.mCategoryId);
        }
        mTabHost.setOnTabChangedListener(this);
        final TabWidget tabWidget = mTabHost.getTabWidget();
        tabWidget.setStripEnabled(mCategoryIndicatorEnabled);
        if (mCategoryIndicatorEnabled) {
            // On TabWidget's strip, what looks like an indicator is actually a background.
            // And what looks like a background are actually left and right drawables.
            tabWidget.setBackgroundResource(mCategoryIndicatorDrawableResId);
            tabWidget.setLeftStripDrawable(mCategoryIndicatorBackgroundResId);
            tabWidget.setRightStripDrawable(mCategoryIndicatorBackgroundResId);
        }

        mEmojiPalettesAdapter = new EmojiPalettesAdapter(mEmojiCategory, this);

        mEmojiPager = findViewById(R.id.emoji_keyboard_pager);
        mEmojiPager.setAdapter(mEmojiPalettesAdapter);

        mEmojiPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mEmojiPalettesAdapter.onPageScrolled();
                final Pair<Integer, Integer> newPos =
                        mEmojiCategory.getCategoryIdAndPageIdFromPagePosition(position);
                final int newCategoryId = newPos.first;
                final int newCategorySize = mEmojiCategory.getCategoryPageSize(newCategoryId);
                final int currentCategoryId = mEmojiCategory.getCurrentCategoryId();
                final int currentCategoryPageId = mEmojiCategory.getCurrentCategoryPageId();
                final int currentCategorySize = mEmojiCategory.getCurrentCategoryPageSize();
                if (newCategoryId == currentCategoryId) {
                    mEmojiCategoryPageIndicatorView.setCategoryPageId(
                            newCategorySize, newPos.second, positionOffset);
                } else if (newCategoryId > currentCategoryId) {
                    mEmojiCategoryPageIndicatorView.setCategoryPageId(
                            currentCategorySize, currentCategoryPageId, positionOffset);
                } else if (newCategoryId < currentCategoryId) {
                    mEmojiCategoryPageIndicatorView.setCategoryPageId(
                            currentCategorySize, currentCategoryPageId, positionOffset - 1);
                }
            }

            @Override
            public void onPageSelected(int position) {
                final Pair<Integer, Integer> newPos =
                        mEmojiCategory.getCategoryIdAndPageIdFromPagePosition(position);
                setCurrentCategoryId(newPos.first /* categoryId */, false /* force */);
                mEmojiCategory.setCurrentCategoryPageId(newPos.second /* categoryPageId */);
                updateEmojiCategoryPageIdView();
                mCurrentPagerPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mEmojiPager.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT);
        mEmojiPager.setPersistentDrawingCache(PERSISTENT_NO_CACHE);
        mEmojiLayoutParams.setPagerProperties(mEmojiPager);

        mEmojiCategoryPageIndicatorView =
                findViewById(R.id.emoji_category_page_id_view);
        mEmojiCategoryPageIndicatorView.setColors(
                mCategoryPageIndicatorColor, mCategoryPageIndicatorBackground);
        mEmojiLayoutParams.setCategoryPageIdViewProperties(mEmojiCategoryPageIndicatorView);

        setCurrentCategoryId(mEmojiCategory.getCurrentCategoryId(), true /* force */);

        final LinearLayout actionBar = findViewById(R.id.emoji_action_bar);
        mEmojiLayoutParams.setActionBarProperties(actionBar);

        // deleteKey depends only on OnTouchListener.
        mDeleteKey = findViewById(R.id.emoji_keyboard_delete);
        mDeleteKey.setBackgroundResource(mFunctionalKeyBackgroundId);
        mDeleteKey.setTag(Constants.CODE_DELETE);
        mDeleteKey.setOnTouchListener(mDeleteKeyOnTouchListener);

        mAlphabetKeyLeft = findViewById(R.id.emoji_keyboard_alphabet_left);
        mAlphabetKeyLeft.setBackgroundResource(mFunctionalKeyBackgroundId);
        mAlphabetKeyLeft.setTag(Constants.CODE_ALPHA_FROM_EMOJI);
        mAlphabetKeyLeft.setOnTouchListener(this);
        mAlphabetKeyLeft.setOnClickListener(this);
        mSpacebar = findViewById(R.id.emoji_keyboard_space);
        mSpacebar.setBackgroundResource(mSpacebarBackgroundId);
        mSpacebar.setTag(Constants.CODE_SPACE);
        mSpacebar.setOnTouchListener(this);
        mSpacebar.setOnClickListener(this);
        mEmojiLayoutParams.setKeyProperties(mSpacebar);
        mSpacebarIcon = findViewById(R.id.emoji_keyboard_space_icon);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        // TODO: Remove this override method once the issue has been addressed.
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onTabChanged(final String tabId) {
        AudioAndHapticFeedbackManager.getInstance().performHapticAndAudioFeedback(
                Constants.CODE_UNSPECIFIED, this);
        final int categoryId = mEmojiCategory.getCategoryId(tabId);
        setCurrentCategoryId(categoryId, false /* force */);
        updateEmojiCategoryPageIdView();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getActionMasked() != MotionEvent.ACTION_DOWN) {
            return false;
        }
        final Object tag = v.getTag();
        if (!(tag instanceof Integer)) {
            return false;
        }
        final int code = (Integer) tag;
        mKeyboardActionListener.onPressKey(
                code, 0, true );
        return false;
    }

    @Override
    public void onClick(View v) {
        final Object tag = v.getTag();
        if (!(tag instanceof Integer)) {
            return;
        }
        final int code = (Integer) tag;
        mKeyboardActionListener.onCodeInput(code, NOT_A_COORDINATE, NOT_A_COORDINATE,
                false );
        mKeyboardActionListener.onReleaseKey(code, false );
    }

    @Override
    public void onPressKey(final Key key) {
        final int code = key.getCode();
        mKeyboardActionListener.onPressKey(code, 0 , true );
    }

    @Override
    public void onReleaseKey(final Key key) {
        mEmojiPalettesAdapter.addRecentKey(key);
        mEmojiCategory.saveLastTypedCategoryPage();
        final int code = key.getCode();
        if (code == Constants.CODE_OUTPUT_TEXT) {
            mKeyboardActionListener.onTextInput(key.getOutputText());
        } else {
            mKeyboardActionListener.onCodeInput(code, NOT_A_COORDINATE, NOT_A_COORDINATE,
                    false /* isKeyRepeat */);
        }
        mKeyboardActionListener.onReleaseKey(code, false /* withSliding */);
    }

    public void setHardwareAcceleratedDrawingEnabled(final boolean enabled) {
        if (!enabled) return;
        // TODO: Should use LAYER_TYPE_SOFTWARE when hardware acceleration is off?
        setLayerType(LAYER_TYPE_HARDWARE, null);
    }

    private static void setupAlphabetKey(final TextView alphabetKey, final String label,
                                         final KeyDrawParams params) {
        alphabetKey.setText(label);
        alphabetKey.setTextColor(params.mFunctionalTextColor);
        alphabetKey.setTextSize(TypedValue.COMPLEX_UNIT_PX, params.mLabelSize);
        alphabetKey.setTypeface(params.mTypeface);
    }

    public void startEmojiPalettes(final String switchToAlphaLabel,
                                   final KeyVisualAttributes keyVisualAttr,
                                   final KeyboardIconsSet iconSet) {
        final int deleteIconResId = iconSet.getIconResourceId(KeyboardIconsSet.NAME_DELETE_KEY);
        if (deleteIconResId != 0) {
            mDeleteKey.setImageResource(deleteIconResId);
        }
        final int spacebarResId = iconSet.getIconResourceId(KeyboardIconsSet.NAME_SPACE_KEY);
        if (spacebarResId != 0) {
            // TODO: Remove this workaround to place the spacebar icon.
            mSpacebarIcon.setBackgroundResource(spacebarResId);
        }
        final KeyDrawParams params = new KeyDrawParams();
        params.updateParams(mEmojiLayoutParams.getActionBarHeight(), keyVisualAttr);
        setupAlphabetKey(mAlphabetKeyLeft, switchToAlphaLabel, params);
        mEmojiPager.setAdapter(mEmojiPalettesAdapter);
        mEmojiPager.setCurrentItem(mCurrentPagerPosition);
    }

    public void stopEmojiPalettes() {
        mEmojiPalettesAdapter.releaseCurrentKey(true /* withKeyRegistering */);
        mEmojiPalettesAdapter.flushPendingRecentKeys();
        mEmojiPager.setAdapter(null);
    }

    public void setKeyboardActionListener(final KeyboardActionListener listener) {
        mKeyboardActionListener = listener;
        mDeleteKeyOnTouchListener.setKeyboardActionListener(listener);
    }

    private void updateEmojiCategoryPageIdView() {
        if (mEmojiCategoryPageIndicatorView == null) {
            return;
        }
        mEmojiCategoryPageIndicatorView.setCategoryPageId(
                mEmojiCategory.getCurrentCategoryPageSize(),
                mEmojiCategory.getCurrentCategoryPageId(), 0.0f /* offset */);
    }

    private void setCurrentCategoryId(final int categoryId, final boolean force) {
        final int oldCategoryId = mEmojiCategory.getCurrentCategoryId();
        if (oldCategoryId == categoryId && !force) {
            return;
        }

        if (oldCategoryId == EmojiCategory.ID_RECENTS) {
            mEmojiPalettesAdapter.flushPendingRecentKeys();
        }

        mEmojiCategory.setCurrentCategoryId(categoryId);
        final int newTabId = mEmojiCategory.getTabIdFromCategoryId(categoryId);
        final int newCategoryPageId = mEmojiCategory.getPageIdFromCategoryId(categoryId);
        if (force || mEmojiCategory.getCategoryIdAndPageIdFromPagePosition(
                mEmojiPager.getCurrentItem()).first != categoryId) {
            mEmojiPager.setCurrentItem(newCategoryPageId, false /* smoothScroll */);
        }
        if (force || mTabHost.getCurrentTab() != newTabId) {
            mTabHost.setCurrentTab(newTabId);
        }
    }

    private static class DeleteKeyOnTouchListener implements OnTouchListener {
        private KeyboardActionListener mKeyboardActionListener =
                KeyboardActionListener.EMPTY_LISTENER;

        public void setKeyboardActionListener(final KeyboardActionListener listener) {
            mKeyboardActionListener = listener;
        }

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    onTouchDown(v);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    final float x = event.getX();
                    final float y = event.getY();
                    if (x < 0.0f || v.getWidth() < x || y < 0.0f || v.getHeight() < y) {
                        onTouchCanceled(v);
                    }
                    return true;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    onTouchUp(v);
                    return true;
            }
            return false;
        }

        private void onTouchDown(final View v) {
            mKeyboardActionListener.onPressKey(Constants.CODE_DELETE,
                    0 , true );
            v.setPressed(true);
        }

        private void onTouchUp(final View v) {
            mKeyboardActionListener.onCodeInput(Constants.CODE_DELETE,
                    NOT_A_COORDINATE, NOT_A_COORDINATE, false);
            mKeyboardActionListener.onReleaseKey(Constants.CODE_DELETE, false );
            v.setPressed(false);
        }

        private void onTouchCanceled(final View v) {
            v.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}