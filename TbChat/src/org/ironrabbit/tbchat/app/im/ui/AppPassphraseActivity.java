package org.ironrabbit.tbchat.app.im.ui;

import org.ironrabbit.tbchat.R;
import org.ironrabbit.tbchat.app.lang.BhoButton;
import org.ironrabbit.tbchat.app.lang.BhoEditText;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AppPassphraseActivity extends Activity {

    private Dialog dl;

    private void gotCredentials(String usr, String pwd) {

    }

    private void foo() {
        dl = new Dialog(this);
        dl.setTitle("Information Prompt");

        dl.setContentView(R.layout.auth_view);
        BhoEditText inputBox1 = (BhoEditText) dl.findViewById(R.id.user);
        inputBox1.setText("");
        BhoEditText inputBox2 = (BhoEditText) dl.findViewById(R.id.pwd);
        inputBox2.setText("");

        BhoButton bOk = (BhoButton) dl.findViewById(R.id.ok);
        bOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                BhoEditText inputBox1 = (BhoEditText) dl.findViewById(R.id.user);
                String usr = inputBox1.getText().toString();
                inputBox1.setText("");

                BhoEditText inputBox2 = (BhoEditText) dl.findViewById(R.id.pwd);
                String pwd = inputBox2.getText().toString();
                inputBox2.setText("");

                dl.dismiss();

                gotCredentials(usr, pwd);
            }
        });
    }

    private void showPasswordDialog() {
        dl = new Dialog(this);
        dl.setTitle("Enter Password Please");

        dl.setContentView(R.layout.password_prompt);
        BhoEditText inputBox1 = (BhoEditText) dl.findViewById(R.id.pwd);
        inputBox1.setText("");

        BhoButton bOk = (BhoButton) dl.findViewById(R.id.ok);
        bOk.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                BhoEditText inputBox2 = (BhoEditText) dl.findViewById(R.id.pwd);
                String pwd = inputBox2.getText().toString();
                inputBox2.setText("");

                dl.dismiss();

                gotCredentials(null, pwd);
            }
        });
    }

    private int contentIdx = -1;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onStart() {

        super.onStart();

        setContentView(R.layout.passphrase_view);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

}
