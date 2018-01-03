package co.id.idpay.ektp.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.id.idpay.ektp.R;
import co.id.idpay.ektp.adapter.EktpAdapter;
import co.id.idpay.ektp.db.DbHelper;
import co.id.idpay.ektp.helper.Constants;
import co.id.idpay.ektp.helper.NetworkChecker;
import co.id.idpay.ektp.helper.Utils;
import co.id.idpay.ektp.model.EktpData;
import co.id.idpay.ektp.model.ServerRequest;
import co.id.idpay.ektp.services.ApiInterface;
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
public class CardActivity extends AppCompatActivity{
    public static final String TAG = CardActivity.class.getSimpleName();
    @BindView(R.id.listView_KTP)
    ListView mListView;
    @BindView(R.id.imageView_head)
    ImageView mImgUHead;
    @BindView(R.id.img_signature)
    ImageView mImgSignature;
    @BindView(R.id.imgFp)
    ImageView mImgFingerPrint;
    private Intent mIntent;
    private String[] ektpData,ektpFailData, ektpDataUi,ektpFailDataUi;
    private String[] titleSet = new String[]{"NIK", "Nama", "Tempat/Tgl Lahir", "Jenis Kelamin", "Gol.Darah", "Alamat", "    RT/RW", "    Kel/Desa", "    Kecamatan", "    Kota/Kab", "    Provinsi", "Agama", "Status Perkawinan", "Pekerjaan", "Kewarganegaraan", "Berlaku Hingga"};
    private EktpAdapter mAdapter = null;
    private Bitmap bmEktp, bmSignature, bmFinger;
    private String ektpStr;
    private Retrofit retrofit;
    private String fotoFinger;
    private String fotoSign;
    private String fotoEktp;
    private String ektpNik, ektpNama, ektpTmpLahir, ektpTglLahir, ektpJnsKelamin, ektpGolDarah, ektpAlamat, ektpRT, ektpRW, ektpKel, ektpKec, ektpKab, ektpProv, ektpAgama, ektpStatus, ektpPekerjaan, ektpKewarganegaraan, ektpValidDate, ektpFoto,ektpSignature,ektpBiometric,ektpIssued, ektpCreate,ektpVerify;
    private DbHelper dbHelper;
    private EktpData mEktps;
    private ArrayList<EktpData> mEktpList;
    private String userId;
    private String message;
    private Handler mHandler;
    private int contextAddr;
    private int ektpUid;
    private ProgressDialog mSenderProcessDialog;
    private boolean exit;
    private String sendTime;
    private int statusFp;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        ButterKnife.bind(this);
        initLoadData();
        initializeRetrofit();

        dbHelper = new DbHelper(this);

        Log.d(TAG, "onCreate: size db " + dbHelper.getEktpData().size());
        mSenderProcessDialog = createSendProgressDialog();

        if (Utils.getNetworkInfo(getBaseContext()) !=null) {
            sendServer();
        } else {
            reloadEktp();
        }


    }

    private void initLoadData() {
        getIntentData();
        mAdapter = new EktpAdapter(this, titleSet, ektpDataUi);
        mListView.setAdapter(mAdapter);
    }


    private void getIntentData() {
        Bundle b = this.getIntent().getExtras();
        ektpData = b.getStringArray("ektpData");
        ektpDataUi = b.getStringArray("ektpDataStr");
        userId = b.getString("uid");
        Log.d(TAG, "getIntentData: " + Utils.decrypted(userId));
        ektpVerify = b.getString("status_ektp");
        Log.d(TAG, "getIntentData: " + ektpVerify);

        if (getIntent().hasExtra("imgEktp") && getIntent().hasExtra("imgSign")
                && getIntent().hasExtra("fingerPrint") && getIntent().hasExtra("statusSend")
                && getIntent().hasExtra("status_ektp")) {
            fotoEktp = Base64.encodeToString(getIntent().getByteArrayExtra("imgEktp"), Base64.NO_WRAP);
            bmEktp = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("imgEktp"), 0, getIntent().getByteArrayExtra("imgEktp").length);
            mImgUHead.setImageBitmap(bmEktp);

            fotoSign = Base64.encodeToString(getIntent().getByteArrayExtra("imgSign"), Base64.NO_WRAP);
            bmSignature = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("imgSign"), 0, getIntent().getByteArrayExtra("imgSign").length);
            mImgSignature.setImageBitmap(bmSignature);
            //statusFp = b.getInt("statusSend");
            fotoFinger = Base64.encodeToString(getIntent().getByteArrayExtra("fingerPrint"), Base64.NO_WRAP);
            //Log.d(TAG, "getIntentData: Foto FINGER: "+ fotoFinger);
            bmFinger = BitmapFactory.decodeByteArray(getIntent().getByteArrayExtra("fingerPrint"),0,getIntent().getByteArrayExtra("fingerPrint").length);
            mImgFingerPrint.setImageBitmap(bmFinger);


        }



    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }



    private ProgressDialog createSendProgressDialog() {

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setTitle("Sending e-Ktp...");
        dialog.setMessage("Prepare sending e-Ktp demografi ,Please wait ...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // TODO:  cancel the thread if thread is already running
                new Thread() {
                    @Override
                    public void run() {
                        if (Utils.getNetworkInfo(getBaseContext()) != null) {
                            sendServer();
                            dialog.cancel();
                        } else {
                            storeToDb();
                            dialog.cancel();
                        }


                    }
                }.start();
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                storeToDb();
                reloadEktp();
                dialog.cancel();
            }
        });
        return dialog;

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



    private void storeToDb() {
        getIntentData();

        for (int i = 0; i < ektpData.length; i++) {
            ektpNik = ektpData[0];
            ektpNama = ektpData[1];
            ektpTmpLahir = ektpData[2];
            ektpTglLahir = ektpData[3];
            ektpJnsKelamin = ektpData[4];
            ektpGolDarah = ektpData[5];
            ektpAlamat = ektpData[6];
            ektpRT = ektpData[7];
            ektpRW = ektpData[8];
            ektpKel = ektpData[9];
            ektpKec = ektpData[10];
            ektpKab = ektpData[11];
            ektpProv = ektpData[12];
            ektpAgama = ektpData[13];
            ektpStatus = ektpData[14];
            ektpPekerjaan = ektpData[15];
            ektpKewarganegaraan = ektpData[16];
            ektpValidDate = ektpData[17];
        }

        mEktps = dbHelper.getEktp();
        Log.d(TAG, "run: " + mEktps);
        if (mEktps == null || dbHelper.getEktpData().size() > 0){
            mEktps = new EktpData();
            mEktps.setNik(Utils.encrypted(ektpNik));
            mEktps.setNama(Utils.encrypted(ektpNama));
            mEktps.setTmpLahir(Utils.encrypted(ektpTmpLahir));
            mEktps.setTglLahir(Utils.encrypted(ektpTglLahir));
            mEktps.setJnsKel(Utils.encrypted(ektpJnsKelamin));
            mEktps.setGolDarah(Utils.encrypted(ektpGolDarah));
            mEktps.setAddrs(Utils.encrypted(ektpAlamat));
            mEktps.setRt(Utils.encrypted(ektpRT));
            mEktps.setRw(Utils.encrypted(ektpRW));
            mEktps.setKel(Utils.encrypted(ektpKel));
            mEktps.setKec(Utils.encrypted(ektpKec));
            mEktps.setKab(Utils.encrypted(ektpKab));
            mEktps.setProv(Utils.encrypted(ektpProv));
            mEktps.setReligion(Utils.encrypted(ektpAgama));
            mEktps.setMarriage(Utils.encrypted(ektpStatus));
            mEktps.setOccupation(Utils.encrypted(ektpPekerjaan));
            mEktps.setNationality(Utils.encrypted(ektpKewarganegaraan));
            mEktps.setValUntil(Utils.encrypted(ektpValidDate));
            mEktps.setPhotoGraph(Utils.encrypted(fotoEktp));
            mEktps.setSignature(Utils.encrypted(fotoSign));
            mEktps.setBioMetric(Utils.encrypted(fotoFinger));
            mEktps.setCreateDate(Utils.encrypted(String.valueOf(System.currentTimeMillis())));
            mEktps.setIssuedBy(userId);
            if (statusFp != 0){
                mEktps.setStatusSend(Utils.encrypted("Success"));
            } else {
                Log.d(TAG, "status: " + System.currentTimeMillis() + " Seconds");
                mEktps.setStatusSend(Utils.encrypted("Finger Print not Match"));
            }
            mEktps.setEktpVerify(Utils.encrypted(ektpVerify));

            dbHelper.addEktp(mEktps);
            dbHelper.close();
            Log.d(TAG, "storeToDb: " + System.currentTimeMillis() + " Seconds");
        }

    }


    public void sendServer() {

        sendTime = String.valueOf(System.currentTimeMillis());
        for (int i = 0; i < ektpData.length; i++) {
            ektpNik = ektpData[0];
            ektpNama = ektpData[1];
            ektpTmpLahir = ektpData[2];
            ektpTglLahir = ektpData[3];
            ektpJnsKelamin = ektpData[4];
            ektpGolDarah = ektpData[5];
            ektpAlamat = ektpData[6];
            ektpRT = ektpData[7];
            ektpRW = ektpData[8];
            ektpKel = ektpData[9];
            ektpKec = ektpData[10];
            ektpKab = ektpData[11];
            ektpProv = ektpData[12];
            ektpAgama = ektpData[13];
            ektpStatus = ektpData[14];
            ektpPekerjaan = ektpData[15];
            ektpKewarganegaraan = ektpData[16];
            ektpValidDate = ektpData[17];
        }



        HashMap<String, String> params = new HashMap<>();
        params.put("nik", Utils.encrypted(ektpNik));
        Log.d(TAG, "sendServer: "+ Utils.encrypted(ektpNik));
        params.put("nama_lkp", Utils.encrypted(ektpNama));
        params.put("tmpt_lhr", Utils.encrypted(ektpTmpLahir));
        params.put("tgl_lhr", Utils.encrypted(ektpTglLahir));
        params.put("jenis_klmin", Utils.encrypted(ektpJnsKelamin));
        params.put("gol_darah", Utils.encrypted(ektpGolDarah));
        params.put("alamat", Utils.encrypted(ektpAlamat));
        params.put("no_rt", Utils.encrypted(ektpRT));
        params.put("no_rw", Utils.encrypted(ektpRW));
        params.put("kel_name", Utils.encrypted(ektpKel));
        params.put("kec_name", Utils.encrypted(ektpKec));
        params.put("kab_name", Utils.encrypted(ektpKab));
        params.put("prop_name", Utils.encrypted(ektpProv));
        params.put("agama", Utils.encrypted(ektpAgama));
        params.put("status_kawin", Utils.encrypted(ektpStatus));
        params.put("jenis_pkrjn", Utils.encrypted(ektpPekerjaan));
        params.put("kewarganegaraan", Utils.encrypted(ektpKewarganegaraan));
        params.put("masa_berlaku", Utils.encrypted(ektpValidDate));
        params.put("biometric", Utils.encrypted(fotoFinger));
        Log.d(TAG, "sendServer: "+ Utils.encrypted(fotoFinger));
        params.put("foto", Utils.encrypted(fotoEktp));
        Log.d(TAG, "sendServer: "+ Utils.encrypted(fotoEktp));
        params.put("ttd", Utils.encrypted(fotoSign));
        Log.d(TAG, "sendServer: "+ Utils.encrypted(fotoSign));
        if (statusFp != 0){
            Log.d(TAG, "status: " + System.currentTimeMillis() + " Seconds");
            params.put("status_send", Utils.encrypted("Success"));
            params.put("status",Utils.encrypted(String.valueOf(1)));


        } else {
            params.put("status_send", Utils.encrypted("Finger Print not Match"));
            params.put("status",Utils.encrypted(String.valueOf(0)));
        }

        params.put("created_date", Utils.encrypted(String.valueOf(System.currentTimeMillis())));
        params.put("created_by", Utils.encrypted(userId));
        params.put("ektp_status",Utils.encrypted(ektpVerify));
        postEktp(params);

    }


    private void postEktp(HashMap<String, String> params) {
        ApiInterface apiService = retrofit.create(ApiInterface.class);
        ServerRequest request = new ServerRequest();
        Call<ResponseBody> result = apiService.ektpSend(params); // okhttp
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

                            Log.d(TAG, "onResponse Success  " + (System.currentTimeMillis()/1000)%60 + " Seconds");
                            Toast.makeText(getBaseContext(), "Sync demography information - check", Toast.LENGTH_LONG).show();
                        } else {
                            runOnUiThread(updateEktpData);
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    storeToDb();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onResponse fail save Save DB : " + (System.currentTimeMillis()/1000)%60 + " Second");
                runOnUiThread(updateEktpData);


            }
        });
    }


    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }
    }


    public void reloadEktp(){
        Thread thread = new Thread(null, loadEktp);
        thread.start();

    }



    private Runnable loadEktp = new Runnable() {

        @Override
        public void run() {
            try {
                mEktpList = dbHelper.getEktpData();
                Log.d("DEBUG", "DATA WARIS COUNT=" + String.valueOf(mEktpList.size()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            runOnUiThread(updateEktpData);
        }

    };

    private Runnable updateEktpData = new Runnable() {

        @Override
        public void run() {
            storeToDb();
        }

    };
}
