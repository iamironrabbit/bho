/*
 * Copyright (C) 2008 Esmertec AG. Copyright (C) 2008 The Android Open Source
 * Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ironrabbit.tbchat.app.im.app;

import java.util.Locale;

import org.ironrabbit.tbchat.IOtrKeyManager;
import org.ironrabbit.tbchat.app.im.IImConnection;
import org.ironrabbit.tbchat.R;
import org.ironrabbit.tbchat.app.im.plugin.BrandingResourceIDs;
import org.ironrabbit.tbchat.app.im.provider.Imps;
import org.ironrabbit.tbchat.app.im.service.ImServiceConstants;
import org.ironrabbit.tbchat.app.lang.BhoButton;
import org.ironrabbit.tbchat.app.lang.BhoCheckBox;
import org.ironrabbit.tbchat.app.lang.BhoEditText;
import org.ironrabbit.tbchat.app.lang.BhoTextView;
import org.ironrabbit.tbchat.app.lang.BhoToast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import android.widget.CompoundButton;
import android.widget.Toast;

import android.widget.TextView.OnEditorActionListener;

import android.widget.CompoundButton.OnCheckedChangeListener;

public class AccountActivity extends Activity {
    public static final String TAG = "AccountActivity";
    private static final String ACCOUNT_URI_KEY = "accountUri";

    private long mProviderId;
    private long mAccountId;

    static final int REQUEST_SIGN_IN = RESULT_FIRST_USER + 1;

    private static final String[] ACCOUNT_PROJECTION = { Imps.Account._ID, Imps.Account.PROVIDER,
                                                        Imps.Account.USERNAME,
                                                        Imps.Account.PASSWORD,
                                                        Imps.Account.KEEP_SIGNED_IN,
                                                        Imps.Account.LAST_LOGIN_STATE };

    private static final int ACCOUNT_PROVIDER_COLUMN = 1;
    private static final int ACCOUNT_USERNAME_COLUMN = 2;
    private static final int ACCOUNT_PASSWORD_COLUMN = 3;
    private static final int ACCOUNT_KEEP_SIGNED_IN_COLUMN = 4;
    private static final int ACCOUNT_LAST_LOGIN_STATE = 5;

    Uri mAccountUri;

    BhoEditText mEditUserAccount;
    BhoEditText mEditPass;
    BhoCheckBox mRememberPass;
    BhoCheckBox mUseTor;
    BhoButton mBtnSignIn;
    BhoButton mBtnAdvanced;
    BhoTextView mTxtFingerprint;

    boolean isEdit = false;
    boolean isSignedIn = false;

    String mUserName;
    String mDomain;
    int mPort;

    private String mOriginalUserAccount = "";

    private final static int DEFAULT_PORT = 5222;

    IOtrKeyManager otrKeyManager;

    // String mToAddress;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.account_activity);
        mEditUserAccount = (BhoEditText) findViewById(R.id.edtName);

        mEditUserAccount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                checkUserChanged();
            }
        });

        mEditPass = (BhoEditText) findViewById(R.id.edtPass);
        mRememberPass = (BhoCheckBox) findViewById(R.id.rememberPassword);
        //       mKeepSignIn = (CheckBox)findViewById(R.id.keepSignIn);
        mUseTor = (BhoCheckBox) findViewById(R.id.useTor);
        mUseTor.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                updateUseTor(isChecked);
            }
        });

        mBtnSignIn = (BhoButton) findViewById(R.id.btnSignIn);

        mBtnAdvanced = (BhoButton) findViewById(R.id.btnAdvanced);

        mRememberPass.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateWidgetState();
            }
        });

        mApp = ImApp.getApplication(this);
        Intent i = getIntent();
        String action = i.getAction();

        if (i.hasExtra("isSignedIn"))
            isSignedIn = i.getBooleanExtra("isSignedIn", false);

        //    mToAddress = i.getStringExtra(ImApp.EXTRA_INTENT_SEND_TO_USER);
        final ProviderDef provider;

        ContentResolver cr = getContentResolver();
        Uri uri = i.getData();
        // check if there is account information and direct accordingly
        if (Intent.ACTION_INSERT_OR_EDIT.equals(action)) {
            if ((uri == null) || !Imps.Account.CONTENT_ITEM_TYPE.equals(cr.getType(uri))) {
                action = Intent.ACTION_INSERT;
            } else {
                action = Intent.ACTION_EDIT;
            }
        }

        if (Intent.ACTION_INSERT.equals(action)) {
            mOriginalUserAccount = "";
            // TODO once we implement multiple IM protocols
            mProviderId = ContentUris.parseId(i.getData());
            provider = mApp.getProvider(mProviderId);
            setTitle(getResources().getString(R.string.add_account, provider.mFullName));
        } else if (Intent.ACTION_EDIT.equals(action)) {
            if ((uri == null) || !Imps.Account.CONTENT_ITEM_TYPE.equals(cr.getType(uri))) {
                Log.w(ImApp.LOG_TAG, "<AccountActivity>Bad data");
                return;
            }

            isEdit = true;

            Cursor cursor = cr.query(uri, ACCOUNT_PROJECTION, null, null, null);

            if (cursor == null) {
                finish();
                return;
            }

            if (!cursor.moveToFirst()) {
                cursor.close();
                finish();
                return;
            }

            setTitle(R.string.sign_in);

            mAccountId = cursor.getLong(cursor.getColumnIndexOrThrow(Imps.Account._ID));

            mProviderId = cursor.getLong(ACCOUNT_PROVIDER_COLUMN);
            provider = mApp.getProvider(mProviderId);

            ContentResolver contentResolver = getContentResolver();
            Imps.ProviderSettings.QueryMap settings = new Imps.ProviderSettings.QueryMap(
                    contentResolver, mProviderId, false, null);

            mOriginalUserAccount = cursor.getString(ACCOUNT_USERNAME_COLUMN) + "@"
                                   + settings.getDomain();
            mEditUserAccount.setText(mOriginalUserAccount);
            mEditPass.setText(cursor.getString(ACCOUNT_PASSWORD_COLUMN));

            mRememberPass.setChecked(!cursor.isNull(ACCOUNT_PASSWORD_COLUMN));

            mUseTor.setChecked(settings.getUseTor());

            getOTRKeyInfo();

            settings.close();
            cursor.close();
        } else {
            Log.w(ImApp.LOG_TAG, "<AccountActivity> unknown intent action " + action);
            finish();
            return;
        }

        if (isSignedIn) {
            mBtnSignIn.setText(getString(R.string.menu_sign_out));
            mBtnSignIn.setBackgroundResource(R.drawable.btn_red);
        }

        final BrandingResources brandingRes = mApp.getBrandingResource(mProviderId);
        /*
        mKeepSignIn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                CheckBox keepSignIn = (CheckBox) v;
                if ( keepSignIn.isChecked() ) {
                    String msg = brandingRes.getString(BrandingResourceIDs.STRING_TOAST_CHECK_AUTO_SIGN_IN);
                    Toast.makeText(AccountActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        });
        */

        mRememberPass.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                BhoCheckBox mRememberPass = (BhoCheckBox) v;

                if (mRememberPass.isChecked()) {
                    String msg = brandingRes
                            .getString(BrandingResourceIDs.STRING_TOAST_CHECK_SAVE_PASSWORD);
                    new BhoToast(AccountActivity.this, msg, Toast.LENGTH_LONG);
                }
            }
        });

        mEditUserAccount.addTextChangedListener(mTextWatcher);
        mEditPass.addTextChangedListener(mTextWatcher);

        mBtnAdvanced.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                showAdvanced();
            }
        });

        mBtnSignIn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                checkUserChanged();

                final String pass = mEditPass.getText().toString();
                final boolean rememberPass = mRememberPass.isChecked();

                ContentResolver cr = getContentResolver();

                if (!parseAccount(mEditUserAccount.getText().toString())) {
                    mEditUserAccount.selectAll();
                    mEditUserAccount.requestFocus();
                    return;
                }

                long accountId = ImApp.insertOrUpdateAccount(cr, mProviderId, mUserName,
                        rememberPass ? pass : null);

                mAccountUri = ContentUris.withAppendedId(Imps.Account.CONTENT_URI, accountId);

                //if remember pass is true, set the "keep signed in" property to true

                if (isSignedIn) {
                    signOut();
                    isSignedIn = false;
                } else {
                    ContentValues values = new ContentValues();
                    values.put(Imps.Account.KEEP_SIGNED_IN, rememberPass ? 1 : 0);
                    getContentResolver().update(mAccountUri, values, null, null);

                    if (!mOriginalUserAccount.equals(mUserName + '@' + mDomain)
                        && shouldShowTermOfUse(brandingRes)) {
                        confirmTermsOfUse(brandingRes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                signIn(rememberPass, pass);
                            }
                        });
                    } else {
                        signIn(rememberPass, pass);
                    }

                    isSignedIn = true;
                }

                updateWidgetState();
            }

            void signIn(boolean rememberPass, String pass) {

                Intent intent = new Intent(AccountActivity.this, SigningInActivity.class);
                intent.setData(mAccountUri);

                if (!rememberPass) {
                    intent.putExtra(ImApp.EXTRA_INTENT_PASSWORD, pass);
                }

                /*
                if (mToAddress != null) {
                    intent.putExtra(ImApp.EXTRA_INTENT_SEND_TO_USER, mToAddress);
                }*/

                startActivityForResult(intent, REQUEST_SIGN_IN);
            }
        });

        /*
        // Make link for signing up.
        String publicXmppServices = "http://xmpp.org/services/";
        	
        String text = brandingRes.getString(BrandingResourceIDs.STRING_LABEL_SIGN_UP);
        SpannableStringBuilder builder = new SpannableStringBuilder(text);
        builder.setSpan(new URLSpan(publicXmppServices), 0, builder.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView signUp = (TextView)findViewById(R.id.signUp);
        signUp.setText(builder);
        signUp.setMovementMethod(LinkMovementMethod.getInstance());
         */
        // repurposing R.id.signUp for short term kludge for account settings message

        updateWidgetState();

    }

    private void updateUseTor(boolean useTor) {
        checkUserChanged();

        final Imps.ProviderSettings.QueryMap settings = new Imps.ProviderSettings.QueryMap(
                getContentResolver(), mProviderId, false /* don't keep updated */, null /* no handler */);

        // if using Tor, disable DNS SRV to reduce anonymity leaks
        settings.setDoDnsSrv(!useTor);

        String server = "";

        if (useTor) {
            server = settings.getDomain();
            String domain = settings.getDomain().toLowerCase();

            // a little bit of custom handling here
            if (domain.equals("gmail.com")) {
                server = "talk.l.google.com";
            } else if (domain.equals("jabber.ccc.de")) {
                server = "okj7xc6j2szr2y75.onion";
            } else if (domain.equals("jabber.org")) {
                server = "hermes.jabber.org";
            } else if (domain.equals("chat.facebook.com")) {
                server = "chat.facebook.com";
            }

        }

        settings.setServer(server);

        settings.setUseTor(useTor);
        settings.close();
    }

    private void getOTRKeyInfo() {

        if (mApp != null && mApp.getRemoteImService() != null) {
            try {
                otrKeyManager = mApp.getRemoteImService().getOtrKeyManager(mOriginalUserAccount);

                if (otrKeyManager == null) {
                    mTxtFingerprint = ((BhoTextView) findViewById(R.id.txtFingerprint));

                    String localFingerprint = otrKeyManager.getLocalFingerprint();
                    if (localFingerprint != null) {
                        ((BhoTextView) findViewById(R.id.lblFingerprint)).setVisibility(View.VISIBLE);
                        mTxtFingerprint.setText(processFingerprint(localFingerprint));
                    } else {
                        ((BhoTextView) findViewById(R.id.lblFingerprint)).setVisibility(View.GONE);
                        mTxtFingerprint.setText("");
                    }
                } else {
                    new BhoToast(this, "OTR is not initialized yet", Toast.LENGTH_SHORT);
                }

            } catch (Exception e) {
                Log.e(ImApp.LOG_TAG, "error on create", e);

            }
        }

    }

    private void checkUserChanged() {
        String username = mEditUserAccount.getText().toString().trim();

        if ((!username.equals(mOriginalUserAccount)) && parseAccount(username)) {
            //Log.i(TAG, "Username changed: " + mOriginalUserAccount + " != " + username);
            settingsForDomain(mDomain, mPort);
            mOriginalUserAccount = username;
        }
    }

    boolean parseAccount(String userField) {
        boolean isGood = true;
        String[] splitAt = userField.trim().split("@");
        mUserName = splitAt[0];
        mDomain = null;
        mPort = 5222;

        if (splitAt.length > 1) {
            mDomain = splitAt[1].toLowerCase();
            String[] splitColon = mDomain.split(":");
            mDomain = splitColon[0];
            if (splitColon.length > 1) {
                try {
                    mPort = Integer.parseInt(splitColon[1]);
                } catch (NumberFormatException e) {
                    // TODO move these strings to strings.xml
                    isGood = false;
                    new BhoToast(
                            AccountActivity.this,
                            "The port value '" + splitColon[1]
                                    + "' after the : could not be parsed as a number!",
                            Toast.LENGTH_LONG);
                }
            }
        }

        if (mDomain == null) {
            isGood = false;
            //Toast.makeText(AccountActivity.this, 
            //	R.string.account_wizard_no_domain_warning,
            //	Toast.LENGTH_LONG).show();
        } else if (mDomain.indexOf(".") == -1) {
            isGood = false;
            //	Toast.makeText(AccountActivity.this, 
            //		R.string.account_wizard_no_root_domain_warning,
            //	Toast.LENGTH_LONG).show();
        }

        return isGood;
    }

    void settingsForDomain(String domain, int port) {
        final Imps.ProviderSettings.QueryMap settings = new Imps.ProviderSettings.QueryMap(
                getContentResolver(), mProviderId, false /* don't keep updated */, null /* no handler */);

        if (domain.equals("gmail.com")) {
            // Google only supports a certain configuration for XMPP:
            // http://code.google.com/apis/talk/open_communications.html
            settings.setDoDnsSrv(true);
            settings.setServer("");
            settings.setDomain(domain);
            settings.setPort(DEFAULT_PORT);
            settings.setRequireTls(true);
            settings.setTlsCertVerify(true);
            settings.setAllowPlainAuth(false);

        } else if (domain.equals("jabber.org")) {
            settings.setDoDnsSrv(true);
            settings.setDomain(domain);
            settings.setPort(DEFAULT_PORT);
            settings.setServer("");
            settings.setRequireTls(true);
            settings.setTlsCertVerify(true);
            settings.setAllowPlainAuth(false);

        } else if (domain.equals("facebook.com")) {
            settings.setDoDnsSrv(false);
            settings.setDomain("chat.facebook.com");
            settings.setPort(DEFAULT_PORT);
            settings.setServer("chat.facebook.com");
            settings.setRequireTls(true); //facebook TLS now seems to be on
            settings.setTlsCertVerify(false); //but cert verify can still be funky - off by default
            settings.setAllowPlainAuth(false);
        } else {

            settings.setDoDnsSrv(true);
            settings.setDomain(domain);
            settings.setPort(port);
            settings.setServer("");
            settings.setRequireTls(true);
            settings.setTlsCertVerify(true);
            settings.setAllowPlainAuth(false);

        }

        settings.close();
    }

    void confirmTermsOfUse(BrandingResources res, DialogInterface.OnClickListener accept) {
        SpannableString message = new SpannableString(
                res.getString(BrandingResourceIDs.STRING_TOU_MESSAGE));
        Linkify.addLinks(message, Linkify.ALL);

        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(res.getString(BrandingResourceIDs.STRING_TOU_TITLE)).setMessage(message)
                .setPositiveButton(res.getString(BrandingResourceIDs.STRING_TOU_DECLINE), null)
                .setNegativeButton(res.getString(BrandingResourceIDs.STRING_TOU_ACCEPT), accept)
                .show();
    }

    boolean shouldShowTermOfUse(BrandingResources res) {
        return !TextUtils.isEmpty(res.getString(BrandingResourceIDs.STRING_TOU_MESSAGE));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mAccountUri = savedInstanceState.getParcelable(ACCOUNT_URI_KEY);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ACCOUNT_URI_KEY, mAccountUri);
    }

    void signOutUsingActivity() {
        final Imps.ProviderSettings.QueryMap settings = new Imps.ProviderSettings.QueryMap(
                getContentResolver(), mProviderId, false /* don't keep updated */, null /* no handler */);

        Intent intent = new Intent(AccountActivity.this, SignoutActivity.class);
        intent.setData(mAccountUri);

        settings.close();

        startActivity(intent);
    }

    private Handler mHandler = new Handler();
    private ImApp mApp = null;

    void signOut() {
        //if you are signing out, then we will deactive "auto" sign in
        ContentValues values = new ContentValues();
        values.put(Imps.Account.KEEP_SIGNED_IN, false ? 1 : 0);
        getContentResolver().update(mAccountUri, values, null, null);

        mApp = ImApp.getApplication(AccountActivity.this);

        mApp.callWhenServiceConnected(mHandler, new Runnable() {
            public void run() {

                signOut(mProviderId, mAccountId);
            }
        });

    }

    void signOut(long providerId, long accountId) {

        try {

            IImConnection conn = mApp.getConnection(providerId);
            if (conn != null) {
                conn.logout();
            } else {
                // Normally, we can always get the connection when user chose to
                // sign out. However, if the application crash unexpectedly, the
                // status will never be updated. Clear the status in this case
                // to make it recoverable from the crash.
                ContentValues values = new ContentValues(2);
                values.put(Imps.AccountStatus.PRESENCE_STATUS, Imps.Presence.OFFLINE);
                values.put(Imps.AccountStatus.CONNECTION_STATUS, Imps.ConnectionStatus.OFFLINE);
                String where = Imps.AccountStatus.ACCOUNT + "=?";
                getContentResolver().update(Imps.AccountStatus.CONTENT_URI, values, where,
                        new String[] { Long.toString(accountId) });
            }
        } catch (RemoteException ex) {
            Log.e(ImApp.LOG_TAG, "signout: caught ", ex);
        } finally {

            new BhoToast(this,
                    getString(R.string.signed_out_prompt, this.mEditUserAccount.getText()),
                    Toast.LENGTH_SHORT);
            isSignedIn = false;

            mBtnSignIn.setText(getString(R.string.sign_in));
            mBtnSignIn.setBackgroundResource(R.drawable.btn_green);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (requestCode == REQUEST_SIGN_IN) {
            if (resultCode == RESULT_OK) {

                finish();
            } else {
                // sign in failed, let's show the screen!
                /*
                //n8fr8: 2011/09/02: removed password reset b/c it was annoying in cases where problem was network
                mEditPass.setText("");
                ContentValues values = new ContentValues();
                values.put(Imps.Account.PASSWORD, (String) null);
                getContentResolver().update(mAccountUri, values, null, null);
                */
            }
        }
    }

    /*
    void updateKeepSignedIn(boolean keepSignIn) {
        ContentValues values = new ContentValues();
        values.put(Imps.Account.KEEP_SIGNED_IN, keepSignIn ? 1 : 0);
        getContentResolver().update(mAccountUri, values, null, null);
    }*/

    void updateWidgetState() {
        boolean goodUsername = mEditUserAccount.getText().length() > 0;
        boolean goodPassword = mEditPass.getText().length() > 0;
        boolean hasNameAndPassword = goodUsername && goodPassword;

        mEditPass.setEnabled(goodUsername);
        mEditPass.setFocusable(goodUsername);
        mEditPass.setFocusableInTouchMode(goodUsername);

        // enable keep sign in only when remember password is checked.
        boolean rememberPass = mRememberPass.isChecked();
        if (rememberPass && !hasNameAndPassword) {
            mRememberPass.setChecked(false);
            rememberPass = false;
        }
        mRememberPass.setEnabled(hasNameAndPassword);
        mRememberPass.setFocusable(hasNameAndPassword);

        /*
        if (!rememberPass) {
            mKeepSignIn.setChecked(false);
        }
        mKeepSignIn.setEnabled(rememberPass);
        mKeepSignIn.setFocusable(rememberPass);
        */

        mEditUserAccount.setEnabled(!isSignedIn);
        mEditPass.setEnabled(!isSignedIn);
        mBtnAdvanced.setEnabled(!isSignedIn);
        mUseTor.setEnabled(!isSignedIn);

        if (!isSignedIn) {
            mBtnSignIn.setEnabled(hasNameAndPassword);
            mBtnSignIn.setFocusable(hasNameAndPassword);
        }
    }

    private final TextWatcher mTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int before, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int after) {
            updateWidgetState();

        }

        public void afterTextChanged(Editable s) {

        }
    };

    private void showAdvanced() {

        checkUserChanged();

        Intent intent = new Intent(this, AccountSettingsActivity.class);
        intent.putExtra(ImServiceConstants.EXTRA_INTENT_PROVIDER_ID, mProviderId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.account_settings_menu, menu);

        if (isEdit) {
            //add delete menu option

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.menu_gen_key:
            otrGenKey();
            return true;

        case R.id.menu_locale:
            showLocaleDialog();
            return true;

            /*
            case R.id.menu_account_settings:
            Intent intent = new Intent(this, AccountSettingsActivity.class);
            //Intent intent = new Intent(this, SettingActivity.class);
            intent.putExtra(ImServiceConstants.EXTRA_INTENT_PROVIDER_ID, mProviderId);
            startActivity(intent);
            return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    ProgressDialog pbarDialog;

    private void otrGenKey() {

        pbarDialog = new ProgressDialog(this);

        pbarDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pbarDialog.setMessage(getString(R.string.otr_gen_key));
        pbarDialog.show();

        KeyGenThread kgt = new KeyGenThread();
        kgt.start();

    }

    private class KeyGenThread extends Thread {

        public KeyGenThread() {

        }

        @Override
        public void run() {

            try {
                if (otrKeyManager != null) {
                    otrKeyManager.generateLocalKeyPair();

                } else {
                    new BhoToast(AccountActivity.this, "OTR is not initialized yet",
                            Toast.LENGTH_SHORT);
                }
            } catch (Exception e) {
                Log.e("OTR", "could not gen local key pair", e);
            } finally {
                handler.sendEmptyMessage(0);
            }

        }

        private Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {

                pbarDialog.dismiss();

                try {
                    if (otrKeyManager != null) {
                        String lFingerprint = otrKeyManager.getLocalFingerprint();
                        mTxtFingerprint.setText(processFingerprint(lFingerprint));
                    }

                } catch (Exception e) {
                    Log.e("OTR", "could not gen local key pair", e);
                }

            }
        };
    }

    private String processFingerprint(String fingerprint) {
        StringBuffer out = new StringBuffer();

        for (int n = 0; n < fingerprint.length(); n++) {
            for (int i = n; n < i + 4; n++) {
                out.append(fingerprint.charAt(n));
            }

            out.append(' ');
        }

        return out.toString();
    }

    private void showLocaleDialog() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getResources().getString(R.string.KEY_PREF_LANGUAGE_TITLE));

        ad.setItems(getResources().getStringArray(R.array.languages),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Locale locale = new Locale(getResources().getStringArray(
                                R.array.languages_values)[which]);
                        ImApp.setNewLocale(AccountActivity.this.getBaseContext(), locale);

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

                    }
                });

        AlertDialog alert = ad.create();
        alert.show();
    }

}
