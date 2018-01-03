package co.id.idpay.ektp.helper;



/**
 * Created      : Rahman on 8/25/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.helper.
 * Copyright    : idpay.com 2017.
 */
public class Constants {

    public final static String BASE_URL = "https://abaka.adira.co.id";

    public static final String update = "https://raw.githubusercontent.com/cybertank378/update/master/update.json";

    //Psam Profile
    public final static String psamProfile = "psamProfile";
    public final static String sdPsamProfile = "/mnt/sdcard/psamProfile";
    public static final String key = "kopikoP1!";
    public static final String mIv = "1234123412341234";
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;



    public static int limitData = 0;


    // SQL Lite Database Constans
    // SQL LITE DB Name
    public static String EKTP_DB = "EktpDB.db";
    // SQL LITE Table Name
    public static final String TBL_EKTP = "tbl_ektp";
    public static final String TBL_USER = "tbl_user";
    public static final String TBL_LOG = "tbl_log";
    // SQL LITE Field Name
    // Global Fields id
    public static final String ID = "id";
    //Field EKTP
    public static final String NIK = "nik";
    public static final String NAMA = "nama";
    public static final String TMP_LAHIR = "tmp_lahir";
    public static final String TGL_LAHIR = "tgl_lahir";
    public static final String JNS_KELAMIN = "jns_kel";
    public static final String GOL_DARAH =  "gol_darah";
    public static final String ALAMAT =  "alamat";
    public static final String RT = "rt";
    public static final String RW = "rw";
    public static final String PROV = "prov";
    public static final String KAB =  "kab";
    public static final String KEL = "kel";
    public static final String KEC = "kec";
    public static final String AGAMA = "agama";
    public static final String STATUS = "status";
    public static final String PEKERJAAN = "pekerjaan";
    public static final String NATIONALITY = "nationality";
    public static final String VALID_UNTIL = "val_until";
    public static final String FOTO = "foto";
    public static final String SIGNATURE = "signature";
    public static final String BIOMETRIC = "bio_metric";
    public static final String CREATE_DATE =  "create_date";
    public static final String ISSUED_BY = "issued_by";
    public static final String EKTP_VERIFIED = "verify";
    // Field User Table
    public static final String UID =  "uid";
    public static final String EMAIL =  "email";
    public static final String USERNAME =  "uname";
    public static final String PASSWORD = "pass";
    public static final String FULLNAME = "fullname";
    public static final String ACCESS_DATE= "access_date";
    public static final String MACADDR = "macaddr";

    // Field Log Table
    public static final String ACCESSLOG =  "access_log";
    public static final String SENDLOG = "send_log";
    public static final String STATLOG = "status_log";
    public static final String REASON= "reason_log";
    public static final String SENDDATE= "send_date";
    public static final String USR_STATUS = "status";




}
