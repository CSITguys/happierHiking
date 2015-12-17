package com.csitguys.happierhiking;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.StringTokenizer;


/**
 * A login screen that offers login via email/password. Requirement #1
 */
public class LoginActivity extends AppCompatActivity {


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private boolean mCreateNewAccount = false;
    private boolean mDuplicateUserName = false;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmView;
    private EditText mUserNameView;
    private TextView mCreateNewAccountLabel;
    private Button mSubmitButton;
    private Button mReturnButton;
    private View mProgressView;
    private View mLoginFormView;

    private User mUser;

    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //set the view to the login activity
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mUserNameView = (AutoCompleteTextView) findViewById(R.id.userName);
        mPasswordConfirmView = (EditText) findViewById(R.id.passwordConfirm);
        mReturnButton = (Button) findViewById(R.id.return_button);
        mSubmitButton = (Button) findViewById(R.id.email_sign_in_button);
        mPasswordView = (EditText) findViewById(R.id.password);
        mCreateNewAccountLabel = (TextView) findViewById(R.id.AccountLabel);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                //EditorInfo.IME_NULL is the enter button on the keypad
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        mReturnButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to sign in screen
                mCreateNewAccount = false;
                mPasswordConfirmView.setVisibility(View.GONE);
                mUserNameView.setVisibility(View.GONE);
                mReturnButton.setVisibility(View.GONE);

                mSubmitButton.setText(R.string.action_sign_in_short);
                mCreateNewAccountLabel.setVisibility(View.GONE);

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        sharedpreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        if(sharedpreferences.getBoolean(getString(R.string.saved_user_state_key), false)){
            //user has a login stored
            //so try that one
            showProgress(true);
            //get data from shared prefs
            String email = sharedpreferences.getString(getString(R.string.saved_user_email),null);
            String password = decodePW(sharedpreferences.getString(getString(R.string.saved_user_pw), null));
            Log.e("Hapy hiker shared pref", password);
            mAuthTask = new UserLoginTask(email, password, false, null);
            mAuthTask.execute((Void) null);
        }
    }



    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mPasswordConfirmView.setError(null);
        mUserNameView.setError(null);


        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String passwordConfirm = mPasswordConfirmView.getText().toString();
        String userName = mUserNameView.getText().toString();

        boolean cancel = false;
        View focusView = null;
        //if you are creating a new account and username is empty
        if(mCreateNewAccount && TextUtils.isEmpty(userName)){
            mUserNameView.setError(getString(R.string.error_field_required));
            focusView = mUserNameView;
            cancel = true;
        }

        // Check for a valid password
        //is first password empty?
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel =true;
        }
        //checks the length of the password
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        //if creating new account and the passwords are not the same
        else if(mCreateNewAccount && !password.equals(passwordConfirm)){
            mPasswordView.setError(getString(R.string.error_password_mismatch));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password, mCreateNewAccount, userName);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Needs to block the use of '/' and ':'
        if (email.contains("/:")) return false;
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Needs to block the use of '/' and ':'
        if(password.contains("/:")) return false;
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //not really da good solution but makes it so the password is not in plain text in the shared preferences
    //only an issue on rooted devices, otherwise this is unaccessible anyways
    private String hashPW(String input) throws Exception{
        String output;
        byte[] ascii = input.getBytes("US-ASCII");
        output = Arrays.toString(ascii);
        Log.e("hashpw debug", Arrays.toString(ascii));
        return output;
    }
    private String decodePW(String input){
        input = input.substring(1,input.length());
        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(input, " ");
        while (st.hasMoreTokens()){
            String token = st.nextToken();
            char c = (char) Integer.parseInt(token.substring(0, token.length()-1));
            sb.append(Character.toString(c));
        }
        return sb.toString();
    }



    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final boolean mAccountCreation;
        private final String mUserName;

        UserLoginTask(String email, String password, boolean accountCreation, String userName) {
            mEmail = email;
            mPassword = password;
            mAccountCreation = accountCreation;
            mUserName = userName;

        }

        @Override
        protected Boolean doInBackground(Void... params) {

            User user, userOutput = null;
            user = new User();
            user.emailAddress = mEmail;
            user.password = mPassword;
            if(mAccountCreation) {
                user.userName = mUserName;
                //Do post of the User to create account
                try {
                    if(UserServletConnection.putUser(user)) {
                        //save user info
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(getString(R.string.saved_user_state_key), true);
                        editor.putString(getString(R.string.saved_user_email), user.emailAddress);
                        editor.putString(getString(R.string.saved_user_pw), hashPW(user.password));
                        editor.putString(getString(R.string.saved_user_name), user.userName);
                        editor.commit();

                        Log.e("happy hiker debug log", "put user is unique");
                        return true;
                    } else {
                        mDuplicateUserName = true;
                        Log.e("happy hiker debug log", "put user is duplicate");
                        return false;
                    }
                } catch (Exception e){
                    Log.e("happy hiker debug log", "Failed to update SERVER: ", e);
                    return false;
                }


            }else {
                try {
                    userOutput = UserServletConnection.getUser(user);
                } catch (Exception e) {
                    Log.e("happy hiker debug", e.toString());
                }
                //account does not exist
                if (userOutput == null) {
                    //account doesn't exist so change to new account views
                    mCreateNewAccount = true;
                    return false;
                } else {
                    //account exists
                    if (userOutput.password.equals("good")) {
                        try{
                        //if preferences are not saved then we will need to save them
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(getString(R.string.saved_user_state_key), true);
                        editor.putString(getString(R.string.saved_user_email), user.emailAddress);
                        editor.putString(getString(R.string.saved_user_pw), hashPW(user.password));
                        editor.putString(getString(R.string.saved_user_name), user.userName);
                        editor.commit();
                        }catch (Exception e){
                            Log.e("happyhiker debug", "Authenticated as " + userOutput.userName);
                        }
                    }else{
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putBoolean(getString(R.string.saved_user_state_key), false);
                        editor.commit();
                        Log.e("happyhiker debug", "bad pw is: " + userOutput.password);
                        return false;
                    }
                }
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                if(mCreateNewAccount){
                    //make registration views visible
                    mPasswordConfirmView.setVisibility(View.VISIBLE);
                    mUserNameView.setVisibility(View.VISIBLE);
                    mReturnButton.setVisibility(View.VISIBLE);
                    mSubmitButton.setText(R.string.action_register);
                    mCreateNewAccountLabel.setVisibility(View.VISIBLE);

                    if(mDuplicateUserName) {
                        mDuplicateUserName = false;
                        mUserNameView.setError(getString(R.string.error_duplicate_username));
                        mUserNameView.requestFocus();
                        Log.e("duplicate names", "duplicate names");
                        mUserNameView.setText("");
                    }

                } else {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

