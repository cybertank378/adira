package co.id.idpay.ektp.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.id.idpay.ektp.R;
import co.id.idpay.ektp.db.DbHelper;
import co.id.idpay.ektp.helper.Constants;
import co.id.idpay.ektp.helper.Encryption;
import co.id.idpay.ektp.helper.PrefManager;
import co.id.idpay.ektp.helper.Utils;
import co.id.idpay.ektp.model.ServerRequest;
import co.id.idpay.ektp.model.User;
import co.id.idpay.ektp.services.ApiInterface;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created      : Rahman on 12/24/2017.
 * Project      : AdiraEktp.
 * ================================
 * Package      : co.id.idpay.ektp.activity.
 * Copyright    : idpay.com 2017.
 */
public class LoginActivity extends AppCompatActivity{
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
    public static final String TAG = LoginActivity.class.getSimpleName();
    private static String CONFIG_FILE;
    private static String PCID;
    private static PrefManager mPref;
    @BindView(R.id.input_username)
    EditText mUsername;
    @BindView(R.id.input_password)
    EditText mPassword;
    @BindView(R.id.btn_login)
    Button mLoginBtn;
    private ApiInterface mApiInterface;
    private DbHelper dbHelper;
    private User mUser;
    private List<User> mUserList;
    private String username, password;
    private String message;
    private Retrofit retrofit;
    private Retrofit idPayRetro;
    private boolean valid;
    private String dialogMsg, dialogTitle;
    private ProgressDialog nDialog;
    private IntentFilter intentFilter;
    private static final int RequestPermissionCode = 1007;
    private SharedPreferences limitPreferences;
    private List<User> mUserExist;
    private MDToast mToast;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        ButterKnife.bind(this);
        dbHelper = new DbHelper(this);
        mUserList = new ArrayList<>();
        mUserExist = new ArrayList<>();
        dummyData();
        initClickListener();
        initializeRetrofit();



        mPref = new PrefManager(this);
        if (!readPsamConfigSuccess(this)){
            showReadPsamConfigDialog();
        }
        mUsername.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
        mPassword.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.black));
        // Checking for user session
        // if user is already logged in, take him to main activity
        if (mPref.isLoggedIn()) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            startActivity(intent);

            finish();

        }

        //Log.d(TAG, "onCreate: " + getMacAddr());

        Log.d(TAG, "onCreate: " + loadPref());


    }


    private int loadPref(){
        limitPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Constants.limitData = limitPreferences.getInt("key_upload_quality", 3);



        return Constants.limitData;
    }





    private void initializeRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }


    public static boolean readPsamConfigSuccess(Context context) {
        try {
            FileInputStream fin = context.openFileInput(Constants.psamProfile);
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            fin.close();
            String[] dataArray = new String[0];
            dataArray = new String(bytes).replaceAll("\r", "").split("\n");
            if (dataArray.length != 2) {
                return false;
            }
            PCID = dataArray[0];
            CONFIG_FILE = dataArray[1];
            if (PCID.length() != 32 || CONFIG_FILE.length() != 64) {
                Log.d(TAG, "readPsamConfigSuccess: " + CONFIG_FILE);
                return false;
            }
            mPref.setPcid(PCID);
            mPref.setConf(CONFIG_FILE);

            return true;
        } catch (Exception e) {
            return false;
        }
    }



    private void showReadPsamConfigDialog() {
        MaterialDialog.Builder mDialog = new MaterialDialog.Builder(this);
        mDialog.title("Reading " + Constants.psamProfile + " Failed");
        mDialog.content("Reading \"" + Constants.psamProfile + "\" failed. \n\nPlease put " + Constants.psamProfile + " in SD card root directory and import it.");
        mDialog.iconRes(R.drawable.ic_warning);
        mDialog.positiveText("Import");
        mDialog.canceledOnTouchOutside(false);
        mDialog.negativeText("Exit");
        mDialog.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (Utils.copyFileFromSdcardToData(getApplicationContext(), Constants.sdPsamProfile, Constants.psamProfile) != 0) {
                    Log.d(TAG, "Import PSAM profile failed. \n\nPlease check if file exist in SD card.");
                    finish();
                } else if (readPsamConfigSuccess(getApplicationContext())) {
                    Log.d(TAG, "Apply the PSAM profile succeed.");
                    Utils.deleteFile(Constants.sdPsamProfile);
                    Utils.deleteAllFilesInSdCard();
                    dialog.dismiss();
                } else {
                    Log.d(TAG, "Apply the PSAM profile failed.\n\nPlease check the file format.");
                    finish();
                }
            }
        });
        mDialog.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        mDialog.cancelable(false);
        mDialog.show();
    }




    private void initClickListener() {
        mLoginBtn.setOnClickListener(LoginClickListener());
    }

    private View.OnClickListener LoginClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //userExist();

                initLogin();
            }
        };
    }

    private void initLogin() {

        openDialog();
        Log.d(TAG, "Start Login: " + (System.currentTimeMillis()/1000)%60 + " Second");

        mUserList = dbHelper.getUserList();


        if (Utils.getNetworkInfo(this) !=null){
            dialogMsg = "Checking network availability...";
            runOnUiThread(updateDialog);
            //loginByServer();
            if (mUserList.size() < loadPref() ){
                dialogMsg = "Checking new access protocol user credential.";
                runOnUiThread(updateDialog);
                loginByServer();
            } else if (mUserList.size() != loadPref() && Utils.getNetworkInfo(this) != null && userExist()) {
                dialogMsg = "Checking local user credential ...";
                runOnUiThread(updateDialog);
                nDialog.dismiss();
                loginByDb();

            } else if (mUserList.size() == loadPref() || userExist()) {
                loginByDb();
                dialogMsg = "Checking local user credential ...";
                nDialog.dismiss();
                mToast.makeText(getApplicationContext(), "Max limit user on this device is reached", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
            }else if (!validate()) {
                onLoginFailed();
                Log.d(TAG, "Fail Validation Login: " + (System.currentTimeMillis()/1000)%60 + " Seconds");
                dialogMsg = "Fail to get user credential for this apps.";
                runOnUiThread(updateDialog);
                nDialog.dismiss();
            }
        } else {
            dialogMsg = "No network access available\nChecking new access protocol user credential.";
            runOnUiThread(updateDialog);
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    nDialog.dismiss();
                    loginByDb();
                }
            }, 300);


        }

    }

    private void openDialog() {
        nDialog = new ProgressDialog(this);
        nDialog.setTitle("Authentication");
        nDialog.setMessage("Please wait\nWe checking your credential access");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(false);
        nDialog.show();
    }

    private void loginByDb(){
       // openDialog();

        dbHelper = new DbHelper(this);
        username = mUsername.getText().toString();
        String encUsr = Utils.encrypted(username);
        password = mPassword.getText().toString();
        String encPass = null;
        try {
            encPass = Utils.encrypted(Encryption.sha1(password));
            if (dbHelper.checkUser(encUsr, encPass)) {
                Log.d(TAG, "loginByDb:" + dbHelper.checkUser(encUsr, encPass) );
                //nDialog.dismiss();
                mUser = dbHelper.getUser();
                Intent uPanel = new Intent(getBaseContext(), MainActivity.class);
                uPanel.putExtra("userid", mUser.getUid());
                uPanel.putExtra("accessTime", String.valueOf(System.currentTimeMillis()));
                startActivity(uPanel);
                finish();

            } else {
                loginByServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }





    }

    private boolean userExist() {
        username = mUsername.getText().toString();
        String encUsername = Utils.encrypted(username);
        dbHelper.checkUser(encUsername);
        return true;
    }


    private void loginByServer() {

        username = mUsername.getText().toString();
        password = mPassword.getText().toString();
        if (Utils.isConnectedFast(getBaseContext())){
            message = "Please wait\nWe checking your credential access";
            HashMap<String, String> params = new HashMap<>();
            params.put("username", Utils.encrypted(username));
            params.put("pass", Utils.encrypted(password));
            params.put("macaddr", Utils.encrypted(getMacAddr()));
            Log.d(TAG, "loginByServer: " + Utils.encrypted(getMacAddr()));
            postLogin(params);
        } else {
            nDialog.dismiss();
            mToast.makeText(getApplicationContext(), "No network available", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
            loginByDb();
        }

    }



    private void postLogin(HashMap<String, String> params) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        ServerRequest request = new ServerRequest();
        Call<ResponseBody> result = apiService.login(params); // okhttp
        result.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String resp;
                try {
                    resp = response.body().string();
                    JSONObject jObj = new JSONObject(resp);
                    JSONObject errMsg = jObj.getJSONObject("error");
                    int  statObj = errMsg.getInt("status");

                    if (resp != null) {
                        message = "Checking user access authentications";
                        if (statObj == 1){
                            JSONArray jArray = jObj.getJSONArray("users");
                            for(int i=0; i<jArray.length(); i++){
                                JSONObject jObject = jArray.getJSONObject(i);
                                mUser = dbHelper.getUser();
                                if (mUser != null ||  mUserList.size() <= loadPref() ){
                                    mUser = new User();
                                    mUser.setUid(Utils.encrypted(jObject.getString("uid")));
                                    mUser.setEmail(Utils.encrypted(jObject.getString("email")));
                                    mUser.setUsername(Utils.encrypted(jObject.getString("username")));
                                    mUser.setPassword(Utils.encrypted(jObject.getString("pass")));
                                    mUser.setFullname(Utils.encrypted(jObject.getString("fullname")));
                                    mUser.setCreateDate(Utils.encrypted(jObject.getString("created_date")));
                                    mUser.setAccessDate(Utils.encrypted(jObject.getString("access_date")));
                                    mUser.setStatus(Utils.encrypted("1"));
                                    dbHelper.addUser(mUser);
                                    dbHelper.close();

                                } else if (mUserExist.contains(jObject.getString("email"))||mUserList.size() == loadPref()) {

                                    loginByDb();
                                    dbHelper.close();
                                } else{
                                    setErrorMessage();
                                    mToast.makeText(getApplicationContext(), "message", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                                }

                                dbHelper.close();
                                nDialog.dismiss();
                                Log.d(TAG, "Success Login SERVER: " + (System.currentTimeMillis()/1000)%60);
                                sendIntent();

                            }
                        } else if (statObj == -1 || Utils.getNetworkInfo(getBaseContext()) == null) {
                            nDialog.dismiss();
                            Log.d(TAG, "Fail Login SERVER: " + (System.currentTimeMillis()/1000)%60);

                            setErrorMessage();
                            mToast.makeText(getApplicationContext(), "message", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                        }
                    }
                } catch (IOException | JSONException e) {
                    mToast.makeText(getApplicationContext(), "No network available", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "No network available : " + (System.currentTimeMillis()/1000)%60);
                nDialog.dismiss();
                runOnUiThread(updateLoginProsess);
                mToast.makeText(getApplicationContext(), "No network available", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                loginByDb();

            }


        });


    }

    private boolean sendIntent() {
        mUser = dbHelper.getUser();
        if (Utils.getNetworkInfo(getBaseContext()) !=null){
            Intent uPanel = new Intent(getBaseContext(), MainActivity.class);
            uPanel.putExtra("userid",  mUser.getUid());
            uPanel.putExtra("accessTime", String.valueOf(System.currentTimeMillis()));
            startActivity(uPanel);
            finish();
        }

        return true;
    }

    private void setErrorMessage() {
        mUsername.setText("");
        mUsername.setError("Please input your valid username access");
        mPassword.setText("");
        mUsername.setError("Please input your valid password access");
    }

    private void onLoginFailed() {
        message = "Login failed\nPlease check is your input are valid credential";
    }

    private boolean validate() {
        valid = true;
        username = mUsername.getText().toString();
        password = mPassword.getText().toString();
        if (username.isEmpty()) {
            mUsername.setError("Username input is not valid");
            requestFocus(mUsername);
            valid = false;
        } else {
            mUsername.setError(null);
        }

        if (password.isEmpty()) {
            mPassword.setError("User credential is not valid");
            requestFocus(mPassword);
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void dummyData() {
        mUsername.setText("rahman");
        mPassword.setText("Masabod0");
    }


    private Runnable updateDialog = new Runnable() {
        @Override
        public void run() {
            //Log.v(TAG, strCharacters);
            nDialog.setMessage(dialogMsg);
            nDialog.setCancelable(isRestricted());
        }
    };

    private Runnable updateLoginProsess = new Runnable() {
        @Override
        public void run() {
            //Log.v(TAG, strCharacters);
            loginByDb();
        }
    };






    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    // res1.append(Integer.toHexString(b & 0xFF) + ":");
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return "";
    }
}
