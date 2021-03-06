package org.ironrabbit.tbchat.app.im.ui;

import org.ironrabbit.tbchat.R;
import org.ironrabbit.tbchat.app.im.app.AccountActivity;
import org.ironrabbit.tbchat.app.im.provider.Imps;
import org.ironrabbit.tbchat.app.lang.BhoButton;
import org.ironrabbit.tbchat.app.lang.BhoTextView;

import android.app.Activity;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends Activity implements OnClickListener {
    //WizardHelper wizard = null;
    // TODO get mProviderId for real
    long mProviderId = 1;

    private int title[] = { R.string.about_welcome_title, R.string.about_otr_title,
                           R.string.about_security_title };

    private int msg[] = { R.string.about_welcome, R.string.about_otr, R.string.about_security,
                         R.string.setup_passphrase };

    private View.OnClickListener listener[][] = { { null, new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            nextContent();

        }
    } },

    { new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            prevContent();

        }
    }, new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            nextContent();

        }
    } },

    { new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            prevContent();

        }
    }, new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            finish();
            Intent intent = new Intent(getBaseContext(), AccountActivity.class);
            intent.setAction(Intent.ACTION_INSERT_OR_EDIT);
            intent.setData(ContentUris.withAppendedId(Imps.Provider.CONTENT_URI, mProviderId));
            // clear the back stack of the wizard
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    } },

    };

    private int contentIdx = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {

        super.onStart();

        if (contentIdx == -1) {
            setContentView(R.layout.scrollingtext_buttons_view);

            nextContent();
        }
    }

    private void prevContent() {
        contentIdx--;
        showContent(contentIdx);
    }

    private void nextContent() {
        contentIdx++;
        showContent(contentIdx);
    }

    private void showContent(int contentIdx) {

        String buttons[][] = {

        { null, getString(R.string.btn_next) },
                              { getString(R.string.btn_back), getString(R.string.btn_next) },
                              { getString(R.string.btn_back), getString(R.string.btn_next) },
                              { getString(R.string.btn_back), getString(R.string.btn_next) },

        };

        BhoTextView txtTitle = ((BhoTextView) findViewById(R.id.WizardTextTitle));
        txtTitle.setText(getString(title[contentIdx]));

        BhoTextView txtBody = ((BhoTextView) findViewById(R.id.WizardTextBody));
        txtBody.setText(getString(msg[contentIdx]));

        BhoButton btn1 = ((BhoButton) findViewById(R.id.btnWizard1));
        if (buttons[contentIdx][0] != null) {
            btn1.setText(buttons[contentIdx][0]);
            btn1.setOnClickListener(listener[contentIdx][0]);
            btn1.setVisibility(Button.VISIBLE);

        } else {
            btn1.setVisibility(Button.INVISIBLE);
        }

        BhoButton btn2 = ((BhoButton) findViewById(R.id.btnWizard2));
        if (buttons[contentIdx][1] != null) {
            btn2.setText(buttons[contentIdx][1]);
            btn2.setOnClickListener(listener[contentIdx][1]);
            btn2.setVisibility(Button.VISIBLE);

        } else {
            btn2.setVisibility(Button.INVISIBLE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(DialogInterface arg0, int arg1) {

    }

}
