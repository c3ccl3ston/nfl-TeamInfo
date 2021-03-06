package chris.eccleston.footballinfo.activities;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Chris on 8/18/2015.
 */
public class BaseActivity extends AppCompatActivity {


    public static final String PREFS_NAME = "PREFERENCES";
    public static final int SORT_BY_NAME = 0;
    public static final int SORT_BY_NUMBER = 1;
    public static final int SORT_BY_POSITION = 2;
    public static final int SORT_BY_LOCATION = 0;
    public static final int SORT_BY_TEAMNAME = 1;
    public static final int SORT_BY_RECORD = 2;
    public static final int SORT_BY_DIVISION = 3;
    public static Context mContext;
    public static int TEAMS_SORT_ORDER = 0;
    public static int SORT_ORDER = -1;
    public final String TEAM_ID = "teamID";

    /**
     * Use this method to colorize toolbar icons to the desired target color
     *
     * @param toolbarView       toolbar view being colored
     * @param toolbarIconsColor the target color of toolbar icons
     */
    public static void colorizeToolbar(Toolbar toolbarView, int toolbarIconsColor) {
        final PorterDuffColorFilter colorFilter
                = new PorterDuffColorFilter(toolbarIconsColor, PorterDuff.Mode.SRC_IN);

        for (int i = 0; i < toolbarView.getChildCount(); i++) {
            final View v = toolbarView.getChildAt(i);

            doColorizing(v, colorFilter, toolbarIconsColor);
        }

        //Step 3: Changing the color of title and subtitle.
        toolbarView.setTitleTextColor(toolbarIconsColor);
        toolbarView.setSubtitleTextColor(toolbarIconsColor);
    }

    public static void doColorizing(View v, final ColorFilter colorFilter, int toolbarIconsColor) {
        if (v instanceof ImageButton) {
            ((ImageButton) v).getDrawable().setAlpha(255);
            ((ImageButton) v).getDrawable().setColorFilter(colorFilter);
        }

        if (v instanceof ImageView) {
            ((ImageView) v).getDrawable().setAlpha(255);
            ((ImageView) v).getDrawable().setColorFilter(colorFilter);
        }

        if (v instanceof AutoCompleteTextView) {
            ((AutoCompleteTextView) v).setTextColor(toolbarIconsColor);
        }

        if (v instanceof TextView) {
            ((TextView) v).setTextColor(toolbarIconsColor);
        }

        if (v instanceof EditText) {
            ((EditText) v).setTextColor(toolbarIconsColor);
        }

        if (v instanceof ViewGroup) {
            for (int lli = 0; lli < ((ViewGroup) v).getChildCount(); lli++) {
                doColorizing(((ViewGroup) v).getChildAt(lli), colorFilter, toolbarIconsColor);
            }
        }

        if (v instanceof ActionMenuView) {
            for (int j = 0; j < ((ActionMenuView) v).getChildCount(); j++) {

                //Step 2: Changing the color of any ActionMenuViews - icons that
                //are not back button, nor text, nor overflow menu icon.
                final View innerView = ((ActionMenuView) v).getChildAt(j);

                if (innerView instanceof ActionMenuItemView) {
                    int drawablesCount = ((ActionMenuItemView) innerView).getCompoundDrawables().length;
                    for (int k = 0; k < drawablesCount; k++) {
                        if (((ActionMenuItemView) innerView).getCompoundDrawables()[k] != null) {
                            final int finalK = k;

                            //Important to set the color filter in seperate thread,
                            //by adding it to the message queue
                            //Won't work otherwise.
                            //Works fine for my case but needs more testing

//                            ((ActionMenuItemView) innerView).getCompoundDrawables()[finalK].setColorFilter(colorFilter);

                            innerView.post(new Runnable() {
                                @Override
                                public void run() {
                                    ((ActionMenuItemView) innerView).getCompoundDrawables()[finalK].setColorFilter(colorFilter);
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    public void setToolbarColors(Toolbar toolbar, int toolbarColor, int toolbarColorDark, int toolbarIconColor) {
        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        window.setStatusBarColor(toolbarColorDark);
        window.setNavigationBarColor(toolbarColor);
        setItemColor(toolbar, toolbarIconColor);
        toolbar.setBackgroundColor(toolbarColor);
    }

    public void setItemColor(Toolbar toolbar, int color) {
        colorizeToolbar(toolbar, color);
    }
}
