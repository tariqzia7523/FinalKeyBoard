package io.github.otobikb.inputmethod.intro;

import android.os.Bundle;

import io.github.otobikb.inputmethod.intro.app.IntroActivity;
import io.github.otobikb.inputmethod.intro.slide.SimpleSlide;
import io.github.otobikb.inputmethod.latin.R;

public class FinnishActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setButtonBackVisible(true);
        setButtonNextVisible(true);
        setButtonCtaVisible(true);
        setButtonCtaTintMode(BUTTON_CTA_TINT_MODE_TEXT);

        addSlide(new SimpleSlide.Builder()
                //.title(R.string.title_material_metaphor)
                //.description(R.string.description_material_metaphor)
                //.image(R.drawable.ic_add_circle_white_24dp)
                .layout(R.layout.first_tutorial)
                .background(R.color.transparent)
                .backgroundDark(R.color.background_app_tutorial)
                .build());

        addSlide(new SimpleSlide.Builder()
//                .title(R.string.title_material_bold)
//                .description(R.string.description_material_bold)
//                .image(R.drawable.ic_add_circle_white_24dp)
//                .background(R.color.color_material_bold)
//                .layout(R.layout.second_tutorial)
//                .backgroundDark(R.color.color_dark_material_bold)
                .layout(R.layout.second_tutorial)
                .background(R.color.transparent)
                .backgroundDark(R.color.background_app_tutorial)

                .build());

        addSlide(new SimpleSlide.Builder()
//                .title(R.string.title_material_motion)
//                .description(R.string.description_material_motion)
//                .image(R.drawable.ic_add_circle_white_24dp)
//                .background(R.color.color_material_motion)
//                .layout(R.layout.third_tutorial)
//                .backgroundDark(R.color.color_dark_material_motion)
                .layout(R.layout.third_tutorial)
                .background(R.color.transparent)
                .backgroundDark(R.color.background_app_tutorial)
                .build());
    }

}
