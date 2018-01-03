package co.id.idpay.ektp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.valdesekamdem.library.mdtoast.MDToast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import butterknife.BindAnim;
import butterknife.BindView;
import butterknife.ButterKnife;
import co.id.idpay.ektp.R;
import co.id.idpay.ektp.adapter.EktpAdapter;
import co.id.idpay.ektp.db.DbHelper;
import co.id.idpay.ektp.helper.Constants;
import co.id.idpay.ektp.helper.PrefManager;
import co.id.idpay.ektp.helper.TimeWatch;
import co.id.idpay.ektp.helper.Utils;
import co.id.idpay.ektp.model.EktpData;
import co.id.idpay.ektp.model.IndonesianIdentityCard;
import co.id.idpay.ektp.model.Logs;
import co.id.idpay.ektp.model.ServerRequest;
import co.id.idpay.ektp.model.User;
import co.id.idpay.ektp.services.ApiInterface;
import esim.ektplibrary.EktpSdk;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
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
public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public static long s, e, totalTime;
    public static boolean TRACETIME = true;
    private static boolean init = false;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.imageView_RFID)
    ImageView mImgRFID;
    @BindView(R.id.imageView_FP)
    ImageView mImgFP;
    /*@BindView(R.id.textView_loginInfo)
    TextView mLogInfo;*/
    @BindView(R.id.reader_layout)
    RelativeLayout mReaderLayout;
    @BindView(R.id.finger_layout)
    RelativeLayout mFingerLayout;
    @BindView(R.id.textView_fmdFailedNum)
    TextView mFailNum;
    @BindView(R.id.sendBtn)
    Button mSendBtn;
    @BindAnim(R.anim.view_show)
    Animation animShow;
    @BindAnim(R.anim.view_hide)
    Animation animHide;
    int ret = -1;
    private PrefManager mPref;
    private byte[] fmd = null;
    private int fmdFailedNum = 0;
    private Thread mThread;
    private List<Integer> imageIdList;
    private EktpAdapter mAdapter;
    private IndonesianIdentityCard icc;
    private DbHelper db;
    private Intent mIntent;
    private String[] ektpDataStr, ektpDataUi, ektpFailDataUi, ektpFailDataStr;
    private Bitmap photoBitmap, signatureBitmap, mFingerBitmap, mScaleFinger, mNoImg, mScaleNoImg;
    private ByteArrayOutputStream ektpImg, signatureImg, fingerPrintImg, noImg;
    private Thread thread;
    private String dialogMsg, dialogTitle;
    private ProgressDialog pDialog;
    private Handler mHandler;
    private Bundle mBundle;
    private String userId;
    private int matchType, matchScore;
    private TimeWatch watch;
    private ProgressDialog mReaderProcessDialog, mFingerPrintProcessDialog;
    private Timer timer;
    private long millis;
    private DialogInterface dialog;
    private User mUser;
    private ArrayList<User> mUserList;
    private String mFullName;
    private Retrofit retrofit;
    private String message;
    private Logs mLogs;
    private DbHelper dbHelper;
    private int logStatusData;
    private String logReasonData, readTime, sendTime, createTime, fpFailTime, fpFailCount;
    private String ektpNik, ektpNama, ektpTmpLahir, ektpTglLahir, ektpJnsKelamin, ektpGolDarah, ektpAlamat, ektpRT, ektpRW, ektpKel, ektpKec, ektpKab, ektpProv, ektpAgama, ektpStatus, ektpPekerjaan, ektpKewarganegaraan, ektpValidDate, ektpFoto, ektpSignature, ektpBiometric, ektpIssued, ektpCreate, ektpSend, ektpVerify;
    private int ektpUid;
    private Boolean networkStat = false;
    private Runnable runnable;
    private Handler handler;
    private int durationSeconds;
    private long timeinMillis;
    private CountDownTimer countDownTimer;
    private EktpData mEktps;
    private ArrayList<EktpData> mFristList;
    private int timeDiff;
    private String errorMsg;
    private List<String> mListStr = null;
    private boolean psamStatus = false;
    private String[] mPermission;
    private String mUserId;
    private MDToast mToast;
    private long startTime, timeStart, timeEnd;
    private ProgressDialog createCaptureFpDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        requirePermission();
        dbHelper = new DbHelper(this);
        icc = new IndonesianIdentityCard();
        mReaderProcessDialog = createReadingDialog();
        mFingerPrintProcessDialog = createCaptureFpDialog("");
        fmd = null;
        fmdFailedNum = 0;

        mPref = new PrefManager(this);
        requirePermission();
        if (LoginActivity.readPsamConfigSuccess(this)) {
            Log.d(TAG, "prepareEktpReader: read psam config success!\n");
            EktpSdk.EktpEstablishContext(this.getApplicationContext());
            EktpSdk.EktpSetAttrib(mPref.getPcid(), mPref.getConf());

        } else {
            Log.d(TAG, "prepareEktpReader: read psam config fail!\n");
        }

        if (mPref.isLoggedIn()) {
            logout();
        }


        initializeRetrofit();
        icc = new IndonesianIdentityCard();
        setSupportActionBar(mToolbar);

        clearRfidLayout();
        clearFpLayout();

        if (mPref.isLoggedIn()) {
            logout();
        }

        getIntentDataUser();
        initClickListener();
        initIntentData();
        initNetworkChanges();

        if (dbHelper.getEktpData().size() != 0) {
            String dbTime = Utils.decrypted(dbHelper.getFirstList().getCreateDate());
            Log.d(TAG, "onCreate: " + dbTime);

            timeinMillis = Long.parseLong(dbTime);

            Date mDate = new Date(timeinMillis);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String mTime = formatter.format(mDate);
            Log.d(TAG, "onCreate: " + TimeWatch.getDifferentBetweenTwoDates(mTime));
            timeDiff = Integer.parseInt(TimeWatch.getDifferentBetweenTwoDates(mTime));
            if (timeDiff >= 300 && Utils.getNetworkInfo(this) != null) {
                Log.d(TAG, "onCreate: " + mTime);

                sendLocalDbData();

            }


        }

        loadPref();
    }


    private String getIntentDataUser() {
        Intent i = getIntent();
        mUserId = Utils.decrypted(i.getStringExtra("userid"));
        Log.d(TAG, "getIntentDataUser: " + mUserId);
        return mUserId;
    }


    private void loadPref() {
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d(TAG, "loadPref: " + mySharedPreferences.getString("key_upload_quality", ""));

    }


    private void initNetworkChanges() {
        if (Utils.getNetworkInfo(getBaseContext()) != null) {
            runnable = () -> {
                mSendBtn.setVisibility(View.VISIBLE);
                mSendBtn.startAnimation(animShow);
                if (dbHelper.getEktpData().size() > 0) {
                    mSendBtn.setVisibility(View.VISIBLE);
                    mSendBtn.startAnimation(animShow);
                } else {
                    mSendBtn.setVisibility(View.GONE);
                    mSendBtn.startAnimation(animHide);
                }

            };
        }


    }


    private void sendLocalDbData() {
        Log.d(TAG, "Start send Db Data To Server: " + (System.currentTimeMillis() / 1000) % 60 + " Seconds");

        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Please wait!");
        pDialog.setMessage("We found " + dbHelper.getEktpData().size() + " Local Data Storage.\nSending " + dbHelper.getEktpData().size() + " local data storage to server");
        pDialog.setCancelable(false);
        pDialog.show();
        for (int i = 0; i < dbHelper.getEktpData().size(); i++) {
            ektpUid = dbHelper.getEktpData().get(i).getuId();
            ektpNik = dbHelper.getEktpData().get(i).getNik();
            ektpNama = dbHelper.getEktpData().get(i).getNama();
            ektpTmpLahir = dbHelper.getEktpData().get(i).getTmpLahir();
            ektpTglLahir = dbHelper.getEktpData().get(i).getTglLahir();
            ektpJnsKelamin = dbHelper.getEktpData().get(i).getJnsKel();
            ektpGolDarah = dbHelper.getEktpData().get(i).getGolDarah();
            ektpAlamat = dbHelper.getEktpData().get(i).getAddrs();
            ektpRT = dbHelper.getEktpData().get(i).getRt();
            ektpRW = dbHelper.getEktpData().get(i).getRw();
            ektpKel = dbHelper.getEktpData().get(i).getKel();
            ektpKec = dbHelper.getEktpData().get(i).getKec();
            ektpKab = dbHelper.getEktpData().get(i).getKab();
            ektpProv = dbHelper.getEktpData().get(i).getProv();
            ektpAgama = dbHelper.getEktpData().get(i).getReligion();
            ektpStatus = dbHelper.getEktpData().get(i).getMarriage();
            ektpPekerjaan = dbHelper.getEktpData().get(i).getOccupation();
            ektpKewarganegaraan = dbHelper.getEktpData().get(i).getNationality();
            ektpValidDate = dbHelper.getEktpData().get(i).getValUntil();
            ektpFoto = dbHelper.getEktpData().get(i).getPhotoGraph();
            ektpSignature = dbHelper.getEktpData().get(i).getSignature();
            ektpBiometric = dbHelper.getEktpData().get(i).getBioMetric();
            ektpIssued = dbHelper.getEktpData().get(i).getIssuedBy();
            ektpCreate = dbHelper.getEktpData().get(i).getCreateDate();
            String oldData = Utils.decrypted(dbHelper.getEktpData().get(i).getStatusSend());
            if (timeDiff >= 86400) {

                ektpSend = Utils.encrypted(oldData + "\nLate Post data by automatic send");
                Log.d(TAG, "sendLocalDbData1: " + ektpSend);
            } else {
                ektpSend = Utils.encrypted(oldData + "\nLate Post data by manual send");
                Log.d(TAG, "sendLocalDbData2: " + ektpSend);
            }

            ektpVerify = dbHelper.getEktpData().get(i).getEktpVerify();


            HashMap<String, String> params = new HashMap<>();
            params.put("nik", ektpNik);
            params.put("nama_lkp", ektpNama);
            params.put("tmpt_lhr", ektpTmpLahir);
            params.put("tgl_lhr", ektpTglLahir);
            params.put("jenis_klmin", ektpJnsKelamin);
            params.put("gol_darah", ektpGolDarah);
            params.put("alamat", ektpAlamat);
            params.put("no_rt", ektpRT);
            params.put("no_rw", ektpRW);
            params.put("kel_name", ektpKel);
            params.put("kec_name", ektpKec);
            params.put("kab_name", ektpKab);
            params.put("prop_name", ektpProv);
            params.put("agama", ektpAgama);
            params.put("status_kawin", ektpStatus);
            params.put("jenis_pkrjn", ektpPekerjaan);
            params.put("kewarganegaraan", ektpKewarganegaraan);
            params.put("masa_berlaku", ektpValidDate);
            params.put("biometric", ektpBiometric);
            params.put("foto", ektpFoto);
            params.put("ttd", ektpSignature);
            params.put("created_by", ektpIssued);
            params.put("created_date", ektpCreate);
            params.put("status_send", ektpSend);
            params.put("status", ektpVerify);

            postDbEktp(params);
        }

    }

    private void postDbEktp(HashMap<String, String> params) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        ServerRequest request = new ServerRequest();
        Call<ResponseBody> result = apiService.ektpSend(params); // okhttp
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String resp;
                try {
                    resp = response.body().string();
                    Log.d(TAG, "onResponse: " + resp);
                    JSONObject jObj = new JSONObject(resp);
                    JSONObject errMsg = jObj.getJSONObject("error");
                    int statObj = errMsg.getInt("status");
                    if (resp != null) {
                        pDialog.setTitle("Prepare Sync demography");
                        if (statObj != 0) {
                            mToast.makeText(getApplicationContext(), "Send data failed,\nPlease check your internet connection!!", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                            pDialog.dismiss();
                            mSendBtn.setVisibility(View.VISIBLE);
                            mSendBtn.startAnimation(animShow);

                        } else {
                            Log.d(TAG, "onResponse Db Success  " + (System.currentTimeMillis() / 1000) % 60 + " Seconds");
                            pDialog.dismiss();
                            mToast.makeText(getApplicationContext(), "Sync Db demography information - check", mToast.LENGTH_SHORT, mToast.TYPE_SUCCESS).show();
                            dbHelper.resetEktp();
                            mSendBtn.setVisibility(View.GONE);
                            mSendBtn.startAnimation(animHide);
                        }


                    }

                } catch (Exception e) {
                    mToast.makeText(getApplicationContext(), "Send data failed,\nPlease check your internet connection!!", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                    pDialog.dismiss();
                    mSendBtn.setVisibility(View.VISIBLE);
                    mSendBtn.startAnimation(animShow);
                    Log.d(TAG, "onResponse &&&& : " + e.getMessage());
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onResponse fail save Save DB : " + (System.currentTimeMillis() / 1000) % 60 + " Second");
                pDialog.dismiss();
                mToast.makeText(getApplicationContext(), "Send data failed,\nPlease check your internet connection!!", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                mSendBtn.setVisibility(View.VISIBLE);
                mSendBtn.startAnimation(animShow);

            }
        });
    }


    private void logout() {
        mPref.clearSession();
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        finish();
    }

    private void initClickListener() {
        mReaderLayout.setOnClickListener(rfidClickListener());
        mFingerLayout.setOnClickListener(fpClickListener());
        mSendBtn.setOnClickListener(sendDbClickListener());
    }


    private View.OnClickListener rfidClickListener() {
        return v -> {
            Log.d(TAG, "OnClick: " + System.currentTimeMillis());

            //fmdFailedNum = 0;
            if (icc == null){
                mReaderProcessDialog.cancel();
                mToast.makeText(getBaseContext(), "Please check your ektp is available ", mToast.LENGTH_SHORT, mToast.TYPE_ERROR).show();
                return;
            }
                fmdFailedNum = 0;
                clearRfidLayout();
                clearFpLayout();
                mReaderProcessDialog.show();


        };
    }


    private View.OnClickListener fpClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                icc = new IndonesianIdentityCard();
                if (icc == null) {
                    shakeRfidLayout();
                    mToast.makeText(getBaseContext(), "Please read the ID card firstly.", mToast.LENGTH_SHORT, mToast.TYPE_ERROR).show();
                    return;
                }

                clearFpLayout();
                fmd = null;
                String message = Utils.getFingerPosTips(icc.getFingerPositionFirst(), icc.getFingerPositionSecond());
                mFingerPrintProcessDialog.setMessage(message);
                mFingerPrintProcessDialog.show();


            }
        };
    }

    private View.OnClickListener sendDbClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.getNetworkInfo(getBaseContext()) != null) {
                    sendLocalDbData();
                } else {
                    mToast.makeText(getBaseContext(), "Please check your network connection ", mToast.LENGTH_SHORT, mToast.TYPE_ERROR).show();
                }


            }
        };
    }

    private ProgressDialog createReadingDialog() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Reading ...");
        dialog.setMessage("Reading Card ,Please wait ...");
        dialog.setProgress(0);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                new Thread() {
                    @Override
                    public void run() {

                        Utils.delay(5);
                        getEktpStrData();
                        mReaderProcessDialog.cancel();
                    }
                }.start();

            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

                if (icc == null) {
                    setReadCardFailed();
                    mToast.makeText(getApplicationContext(), "Read Card Failed or Canceled", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                } else {
                    setReadCardPass();
                    mToast.makeText(getApplicationContext(), "Read Card Success", mToast.LENGTH_SHORT, mToast.TYPE_SUCCESS).show();
                    Log.d(TAG, "onCancel: " + icc.mLeftFinger);
                    mReaderProcessDialog.dismiss();
                    fmd = null;
                    String message = Utils.getFingerPosTips(icc.mLeftFinger, icc.mRightFinger);
                    mFingerPrintProcessDialog.setMessage(message);
                    mFingerPrintProcessDialog.show();
                }
            }
        });
        return dialog;
    }


    private ProgressDialog createCaptureFpDialog(String message) {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Capture ...");
        if (icc != null) {
            dialog.setMessage(message);
        }
        dialog.setProgress(100);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Log.i("FingerPrint", "capture dialog show");
                clearFpLayout();
                new Thread() {
                    @Override
                    public void run() {
                        fmd = null;//capture the first finger
                        fmd = EktpSdk.EktpCaptureFingerPrint();
                        mFingerPrintProcessDialog.cancel();
                    }
                }.start();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (fmd == null) {
                    return;

                }
                matchType = 0;

                if (EktpSdk.EktpMatchFingerPrint(fmd, Utils.hexStringToBytes(icc.mLeftFinger))) {
                    matchType = 1;
                } else if (EktpSdk.EktpMatchFingerPrint(fmd, Utils.hexStringToBytes(icc.mRightFinger))) {
                    matchType = 2;
                }

                Log.d(TAG, "Finger Captured --> " + Utils.bytesToHexString(fmd));
                Log.d(TAG, "Fingerprint Match result type is: " + matchType);

                if (matchType != 0) {
                    mToast.makeText(getApplicationContext(), "Finger Print Match Passed", mToast.LENGTH_SHORT, mToast.TYPE_SUCCESS).show();
                    setFingerPrintMatchPass();
                    timeEnd = System.currentTimeMillis();
                    Log.d(TAG, "onCancel: " + timeEnd+ " seconds");

                    startShowEktpInfo();
                    Log.d(TAG, "onCancel: Read KTP-el Finish");


                } else {
                    timeEnd = System.currentTimeMillis();
                    Log.d(TAG, "onCancel Fail: " + timeEnd + " seconds");
                    mToast.makeText(getApplicationContext(), "Finger Print Match Fail.", mToast.LENGTH_SHORT, mToast.TYPE_WARNING).show();
                    setFingerPrintMatchFailed();

                }
            }
        });

        return dialog;
    }


    private void startShowEktpInfo() {
        sendTime = String.valueOf(System.currentTimeMillis());
        initIntentData();
        photoBitmap = BitmapFactory.decodeByteArray(icc.getPhotograph(), 0, icc.getPhotograph().length);
        signatureBitmap = Utils.getSignBitmap(icc.getSignatureBytes(), 168, 44);
        mFingerBitmap = getBitmap(fmd);


        ektpImg = new ByteArrayOutputStream();
        signatureImg = new ByteArrayOutputStream();
        fingerPrintImg = new ByteArrayOutputStream();
        noImg = new ByteArrayOutputStream();

        photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, ektpImg);
        signatureBitmap.compress(Bitmap.CompressFormat.PNG, 100, signatureImg);
        mScaleFinger = Bitmap.createScaledBitmap(getBitmap(fmd), 70, 88, true);
        mScaleFinger.compress(Bitmap.CompressFormat.PNG, 100, fingerPrintImg);
        mNoImg = BitmapFactory.decodeResource(getResources(), R.drawable.no_image);
        mScaleNoImg = Bitmap.createScaledBitmap(mNoImg, 70, 88, true);
        mScaleNoImg.compress(Bitmap.CompressFormat.PNG, 100, noImg);


        mBundle = new Bundle();
        mBundle.putString("uid", userId);

        mBundle.putStringArray("ektpData", ektpDataStr);
        mBundle.putStringArray("ektpDataStr", ektpDataUi);
        mBundle.putByteArray("imgEktp", ektpImg.toByteArray());
        mBundle.putByteArray("imgSign", signatureImg.toByteArray());
        Log.d(TAG, "startShowEktpInfo: " + matchType);

        if (matchType == 0) {
            mBundle.putByteArray("fingerPrint", noImg.toByteArray());
            mBundle.putInt("statusSend", 0);
            ektpVerify = "Not Verified";
            mBundle.putString("status_ektp", ektpVerify);

        } else {
            mBundle.putByteArray("fingerPrint", fingerPrintImg.toByteArray());
            mBundle.putInt("statusSend", 1);
            ektpVerify = "Verified";
            mBundle.putString("status_ektp", ektpVerify);
        }


        mIntent = new Intent(getBaseContext(), CardActivity.class);
        mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
        finish();
    }


    private void clearFpLayout() {
        mImgFP.setImageResource(R.drawable.ic_fingerprint_white);
    }

    private void setFingerPrintMatchPass() {
        mImgFP.setImageResource(R.drawable.ic_fingerprint_green);
        mFailNum.setVisibility(View.INVISIBLE);
        readTime = String.valueOf(System.currentTimeMillis());
        sendTime = String.valueOf(System.currentTimeMillis());
        createTime = String.valueOf(System.currentTimeMillis());
        sendLogData();
    }

    private void setFingerPrintMatchFailed() {
        fmdFailedNum++;
        if (fmdFailedNum == 2) {
            startShowEktpInfo();
        }
        mFailNum.setText(String.valueOf(fmdFailedNum));
        mFailNum.setVisibility(View.VISIBLE);
        fpFailTime = String.valueOf(System.currentTimeMillis());
        mImgFP.setImageResource(R.drawable.ic_fingerprint_red);
        readTime = String.valueOf(System.currentTimeMillis());
        sendTime = String.valueOf(System.currentTimeMillis());
        createTime = String.valueOf(System.currentTimeMillis());
        sendLogData();



    }

    private void clearRfidLayout() {
        //fmdFailedNum = 0;
        mFailNum.setVisibility(View.INVISIBLE);
        mImgRFID.setImageResource(R.drawable.ic_rfid_white);
    }

    private void setReadCardPass() {
        mImgRFID.setImageResource(R.drawable.ic_rfid_green);
        readTime = String.valueOf(System.currentTimeMillis());
        sendTime = String.valueOf(System.currentTimeMillis());
        createTime = String.valueOf(System.currentTimeMillis());
        sendLogData();
    }

    private void setReadCardFailed() {
        readTime = String.valueOf(System.currentTimeMillis());
        sendTime = String.valueOf(System.currentTimeMillis());
        createTime = String.valueOf(System.currentTimeMillis());
        mImgRFID.setImageResource(R.drawable.ic_rfid_red);
        sendLogData();
    }

    private void shakeRfidLayout() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        mReaderLayout.clearAnimation();
        mReaderLayout.startAnimation(anim);
    }

    private void initIntentData() {
        Intent i = getIntent();
        userId = i.getStringExtra("userid");
        Log.d(TAG, "initIntentData: " + userId);

    }


    private void requirePermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_PERMISSION = 101;

            String[] mPermission =
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
            //验证是否许可权限
            for (String str : mPermission) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    requestPermissions(mPermission, REQUEST_CODE_PERMISSION);
                    return;
                }
            }
        }
    }

    private void getEktpStrData() {

        icc = new IndonesianIdentityCard();
        mListStr = EktpSdk.EktpReadCardStepOne(getApplicationContext());
        if (mListStr.size() == 3 && mListStr.get(1).length() > 0 && mListStr.get(2).length() > 0) {
            icc.mPhotographData = mListStr.get(1);
            icc.mKtpDataArray = getDemographicStrings(mListStr.get(2));
        } else {
            Log.d(TAG, "getEktpData: Get demographic fail");
        }



        mListStr = EktpSdk.EktpReadCardStepTwo(getApplicationContext());
        if (mListStr.size() == 4 && mListStr.get(1).length() > 0 && mListStr.get(2).length() > 0 && mListStr.get(3).length() > 0) {
            icc.mLeftFinger = mListStr.get(1);
            icc.mRightFinger = mListStr.get(2);
            icc.mSignature = mListStr.get(3);
        } else {
            Log.d(TAG, "getEktpData: Get demographic fail");
        }

        if (icc.mKtpDataArray != null) {
            ektpDataStr = new String[]{icc.getID(), Utils.capitalize(icc.getName()), Utils.capitalize(icc.getPlaceOfBirth()), icc.getDateOfBirth(), Utils.capitalize(icc.getGender()), icc.getBloodType(), Utils.capitalize(icc.getAddress()), icc.getNeighbourhood(), icc.getCommunityAssociation(), Utils.capitalize(icc.getVillage()), Utils.capitalize(icc.getDistrict()), Utils.capitalize(icc.getCity()), Utils.capitalize(icc.getProvince()), Utils.capitalize(icc.getReligion()), Utils.capitalize(icc.getMarriageStatus()), Utils.capitalize(icc.getOccupation()), icc.getNationality(), "Seumur Hidup"};
            ektpDataUi = new String[]{icc.getID(), Utils.capitalize(icc.getName()), Utils.capitalize(icc.getPlaceOfBirth()) + ", " + icc.getDateOfBirth(), Utils.capitalize(icc.getGender()), icc.getBloodType(), Utils.capitalize(icc.getAddress()), icc.getNeighbourhood() + "/" + icc.getCommunityAssociation(), Utils.capitalize(icc.getVillage()), Utils.capitalize(icc.getDistrict()), Utils.capitalize(icc.getCity()), Utils.capitalize(icc.getProvince()), Utils.capitalize(icc.getReligion()), Utils.capitalize(icc.getMarriageStatus()), Utils.capitalize(icc.getOccupation()), icc.getNationality(), "Seumur Hidup"};

        }


    }


    public String[] getDemographicStrings(String demographicStr) {

        if (demographicStr != null) {
            String[] demographicArray = new String[21];
            int index = 0;
            byte[] data = Utils.hexStringToBytes(demographicStr);
            for (int i = 0; i < data.length; i++) {
                if (data[i] == 34) {
                    StringBuilder builder = new StringBuilder();
                    for (int j = i + 1; j < data.length; j++) {
                        if (data[j] == 34) {
                            i = j;
                            break;
                        }
                        builder.append((char) data[j]);
                    }
                    demographicArray[index++] = builder.toString();
                    if (index >= 21)
                        break;
                }
            }
            return demographicArray;
        }

        return null;
    }


    private void sendLogData() {

        if (fingerPrintImg == null) {
            logReasonData = "Fingerprint not match";
            logStatusData = 40;
        }
        if (fmdFailedNum > 0) {
            logReasonData = "Failed try to match in " + fmdFailedNum + " times";
            logStatusData = 42;
        } else if (icc == null) {
            logReasonData = "Cannot Read Ektp Data";
            logStatusData = 50;
        } else {
            logReasonData = "Succeed Read Ektp Data";
            logStatusData = 20;

        }


        HashMap<String, String> params = new HashMap<>();
        params.put("created_date", Utils.encrypted(createTime));
        params.put("created_by", userId);
        params.put("access_log", Utils.encrypted(readTime));
        params.put("status_log", Utils.encrypted(String.valueOf(logStatusData)));
        params.put("reason_log", Utils.encrypted(logReasonData));
        postLog(params);

    }


    private void initializeRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private void postLog(HashMap<String, String> params) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        ServerRequest request = new ServerRequest();
        Call<ResponseBody> result = apiService.logSend(params); // okhttp
        result.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String resp;
                try {
                    resp = response.body().string();
                    JSONObject jObj = new JSONObject(resp);
                    JSONObject errMsg = jObj.getJSONObject("error");
                    int statObj = errMsg.getInt("status");
                    if (resp != null) {
                        message = "Prepare Sync demography";
                        if (statObj == 1) {

                            Log.d(TAG, "onResponse Success  " + (System.currentTimeMillis() / 1000) % 60 + " Seconds");

                            //Toast.makeText(getBaseContext(), "Sync local DB demography information - check", Toast.LENGTH_LONG).show();

                        }

                        if (statObj == 0) {
                            pDialog.cancel();
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onResponse fail save Save DB : " + (System.currentTimeMillis() / 1000) % 60 + " Second");


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            // launch settings activity
            startActivity(new Intent(getBaseContext(), SettingsActivity.class));
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

private class LogOutTimerTask extends TimerTask {

    @Override
    public void run() {
        mPref.clearSession();

        //redirect user to login screen
        mIntent = new Intent(getBaseContext(), LoginActivity.class);
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mIntent);
        finish();
    }

}


    public void showDialog() {
        String strMsg = getResources().getString(R.string.infoDialog);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage(strMsg);
        pDialog.show();
    }

    public void closeDialog() {
        pDialog.dismiss();
    }


    public Bitmap getBitmap(byte[] mImage) {
        // Decode to get the bitmap
        if (mImage != null)
            return getBitmapFromRaw(mImage, 252, 324);
        return null;
    }

    public Bitmap getBitmapFromRaw(byte[] Src, int width, int height) {
        byte[] Bits = new byte[Src.length * 4];
        Log.e("FingerPrint", "image length = " + Src.length);
        for (int i = 0; i < Src.length; i++) {
            Bits[i * 4] = Bits[i * 4 + 1] = Bits[i * 4 + 2] = (byte) Src[i];
            Bits[i * 4 + 3] = -1;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));

        return bitmap;
    }


    /*private Runnable updateUIScanFailed = new Runnable() {

        @Override
        public void run() {

            fmdFailedNum ++;
            Log.d(TAG, "setFingerPrintMatchFailed: " + fmdFailedNum++);
            if (fmdFailedNum == 3) {
                Log.d(TAG, "setFingerPrintMatchFailed: " + fmdFailedNum);
                startShowEktpInfo();
            }
            mFailNum.setText(String.valueOf(fmdFailedNum));
            mFailNum.setVisibility(View.VISIBLE);
            fpFailTime = String.valueOf(System.currentTimeMillis());
            mImgFP.setImageResource(R.drawable.ic_fingerprint_red);
            readTime = String.valueOf(System.currentTimeMillis());
            sendTime = String.valueOf(System.currentTimeMillis());
            createTime = String.valueOf(System.currentTimeMillis());
            sendLogData();

        }

    };

    private Runnable updateUIScanSuccess = new Runnable() {

        @Override
        public void run() {

            startShowEktpInfo();
            mImgFP.setImageResource(R.drawable.ic_fingerprint_green);
            mFailNum.setVisibility(View.INVISIBLE);
            readTime = String.valueOf(System.currentTimeMillis());
            sendTime = String.valueOf(System.currentTimeMillis());
            createTime = String.valueOf(System.currentTimeMillis());
            sendLogData();
        }

    };*/


}
