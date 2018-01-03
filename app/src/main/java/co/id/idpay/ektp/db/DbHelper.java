package co.id.idpay.ektp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.id.idpay.ektp.helper.Constants;
import co.id.idpay.ektp.helper.Utils;
import co.id.idpay.ektp.model.EktpData;
import co.id.idpay.ektp.model.IndonesianIdentityCard;
import co.id.idpay.ektp.model.Logs;
import co.id.idpay.ektp.model.User;


import static co.id.idpay.ektp.helper.Constants.UID;

/**
 * Created      : Rahman on 12/13/2017.
 * Project      : AdiraEktp.
 * ================================
 * Package      : esim.ektp.db.
 * Copyright    : idpay.com 2017.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mDB;
    // Database Version
    private static final int DATABASE_VERSION = 57;
    // Database Name
    private static final String DATABASE_NAME = Constants.EKTP_DB;
    private SQLiteDatabase db;
    private Context mContext;
    private Cursor cursor;

    public static synchronized DbHelper getInstance(Context context){
        if (mDB==null){
            mDB= new DbHelper(context.getApplicationContext());
        }
        return mDB;
    }


    public DbHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create ektp table
        String CREATE_EKTP_TABLE = "CREATE TABLE " + Constants.TBL_EKTP + "("
                + Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.NIK + " TEXT,"
                + Constants.NAMA + " TEXT,"
                + Constants.TGL_LAHIR + " TEXT,"
                + Constants.TMP_LAHIR + " TEXT,"
                + Constants.JNS_KELAMIN + " TEXT,"
                + Constants.GOL_DARAH + " TEXT,"
                + Constants.ALAMAT + " TEXT,"
                + Constants.RT + " TEXT,"
                + Constants.RW + " TEXT,"
                + Constants.KEL + " TEXT,"
                + Constants.KEC + " TEXT,"
                + Constants.KAB + " TEXT,"
                + Constants.PROV + " TEXT,"
                + Constants.AGAMA + " TEXT,"
                + Constants.STATUS + " TEXT,"
                + Constants.PEKERJAAN + " TEXT,"
                + Constants.NATIONALITY + " TEXT,"
                + Constants.VALID_UNTIL + " TEXT,"
                + Constants.FOTO + " TEXT,"
                + Constants.SIGNATURE + " TEXT,"
                + Constants.BIOMETRIC + " TEXT,"
                + Constants.ISSUED_BY + " TEXT,"
                + Constants.CREATE_DATE + " TEXT,"
                + Constants.EKTP_VERIFIED + " TEXT,"
                + Constants.SENDDATE + " TEXT" + ")";
        db.execSQL(CREATE_EKTP_TABLE);

        String CREATE_USER_TABLE = "CREATE TABLE " + Constants.TBL_USER + "("
                + Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.UID + " TEXT,"
                + Constants.EMAIL + " TEXT,"
                + Constants.USERNAME + " TEXT,"
                + Constants.PASSWORD + " TEXT,"
                + Constants.FULLNAME + " TEXT,"
                + Constants.CREATE_DATE + " TEXT,"
                + Constants.ACCESS_DATE + " TEXT,"
                + Constants.MACADDR + " TEXT,"
                + Constants.USR_STATUS + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

        String CREATE_LOG_TABLE = "CREATE TABLE " + Constants.TBL_LOG + "("
                + Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Constants.CREATE_DATE + " TEXT,"
                + Constants.ISSUED_BY + " TEXT,"
                + Constants.ACCESSLOG + " TEXT,"
                + Constants.SENDLOG + " TEXT,"
                + Constants.STATLOG + " TEXT,"
                + Constants.REASON + " TEXT" + ")";
        db.execSQL(CREATE_LOG_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older ektp table if existed
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TBL_EKTP);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TBL_USER);
        db.execSQL("DROP TABLE IF EXISTS " + Constants.TBL_LOG);
        // create fresh ektp table
        this.onCreate(db);
    }

    public void addEktp(EktpData ektpData) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put( Constants.NIK, ektpData.getNik());
        val.put( Constants.NAMA, ektpData.getNama());
        val.put( Constants.TGL_LAHIR, ektpData.getTglLahir());
        val.put( Constants.TMP_LAHIR, ektpData.getTmpLahir());
        val.put( Constants.JNS_KELAMIN, ektpData.getJnsKel());
        val.put( Constants.GOL_DARAH, ektpData.getGolDarah());
        val.put( Constants.ALAMAT, ektpData.getAddrs());
        val.put( Constants.RT, ektpData.getRt());
        val.put( Constants.RW, ektpData.getRw());
        val.put( Constants.KEL, ektpData.getKel());
        val.put( Constants.KEC, ektpData.getKec());
        val.put( Constants.KAB, ektpData.getKab());
        val.put( Constants.PROV, ektpData.getProv());
        val.put( Constants.AGAMA, ektpData.getReligion());
        val.put( Constants.STATUS, ektpData.getMarriage());
        val.put( Constants.PEKERJAAN, ektpData.getOccupation());
        val.put( Constants.NATIONALITY, ektpData.getNationality());
        val.put( Constants.VALID_UNTIL, ektpData.getValUntil());
        val.put( Constants.FOTO, ektpData.getPhotoGraph());
        val.put( Constants.SIGNATURE, ektpData.getSignature());
        val.put( Constants.BIOMETRIC, ektpData.getBioMetric());
        val.put( Constants.ISSUED_BY, ektpData.getIssuedBy());
        val.put( Constants.CREATE_DATE, ektpData.getCreateDate());
        val.put( Constants.SENDDATE, ektpData.getStatusSend());
        val.put( Constants.EKTP_VERIFIED, ektpData.getEktpVerify());
        db.insert(Constants.TBL_EKTP, null, val);
        db.close();
    }

    // Deleting single ektp
    public void deleteEktp(EktpData mEktpData) throws SQLException {
        db = this.getWritableDatabase();

        // 2. delete
        db.delete(Constants.TBL_EKTP, UID + " = ?",
                new String[]{String.valueOf(mEktpData.getuId())});

        // 3. close
        db.close();

        Log.d("deleteEktp", mEktpData.toString());

    }

    public ArrayList<EktpData> getEktpData() throws SQLException{




        ArrayList<EktpData> ektpDatas = new ArrayList<EktpData>();
        String selectQuery = "SELECT * FROM " + Constants.TBL_EKTP;

        SQLiteDatabase db = this.getReadableDatabase();
        cursor = null;
        cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        try{
            if (cursor.moveToFirst()){
                do{
                    EktpData ektp = new EktpData();
                    ektp.setuId(cursor.getInt(0));
                    ektp.setNik(cursor.getString(1));
                    ektp.setNama(cursor.getString(2));
                    ektp.setTglLahir(cursor.getString(3));
                    ektp.setTmpLahir(cursor.getString(4));
                    ektp.setJnsKel(cursor.getString(5));
                    ektp.setGolDarah(cursor.getString(6));
                    ektp.setAddrs(cursor.getString(7));
                    ektp.setRt(cursor.getString(8));
                    ektp.setRw(cursor.getString(9));
                    ektp.setKel(cursor.getString(10));
                    ektp.setKec(cursor.getString(11));
                    ektp.setKab(cursor.getString(12));
                    ektp.setProv(cursor.getString(13));
                    ektp.setReligion(cursor.getString(14));
                    ektp.setMarriage(cursor.getString(15));
                    ektp.setOccupation(cursor.getString(16));
                    ektp.setNationality(cursor.getString(17));
                    ektp.setValUntil(cursor.getString(18));
                    ektp.setPhotoGraph(cursor.getString(19));
                    ektp.setSignature(cursor.getString(20));
                    ektp.setBioMetric(cursor.getString(21));
                    ektp.setIssuedBy(cursor.getString(22));
                    ektp.setCreateDate(cursor.getString(23));
                    ektp.setStatusSend(cursor.getString(24));
                    ektp.setEktpVerify(cursor.getString(25));


                    ektpDatas.add(ektp);
                } while(cursor.moveToNext());

            }
        }catch (Exception ee){ee.printStackTrace();}
        finally {
            cursor.close();
            db.close();
        }
        return ektpDatas;
    }


    public EktpData getEktp() throws SQLException{

        String selectQuery = "SELECT  * FROM " + Constants.TBL_EKTP + " LIMIT 1";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor != null){
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                EktpData ektps = new EktpData();
                ektps.setuId(cursor.getInt(0));
                ektps.setNik(cursor.getString(1));
                ektps.setNama(cursor.getString(2));
                ektps.setTmpLahir(cursor.getString(3));
                ektps.setTglLahir(cursor.getString(4));
                ektps.setJnsKel(cursor.getString(5));
                ektps.setGolDarah(cursor.getString(6));
                ektps.setAddrs(cursor.getString(7));
                ektps.setRt(cursor.getString(8));
                ektps.setRw(cursor.getString(9));
                ektps.setKel(cursor.getString(10));
                ektps.setKec(cursor.getString(11));
                ektps.setKab(cursor.getString(12));
                ektps.setProv(cursor.getString(13));
                ektps.setReligion(cursor.getString(14));
                ektps.setMarriage(cursor.getString(15));
                ektps.setOccupation(cursor.getString(16));
                ektps.setNationality(cursor.getString(17));
                ektps.setValUntil(cursor.getString(18));
                ektps.setPhotoGraph(cursor.getString(19));
                ektps.setSignature(cursor.getString(20));
                ektps.setBioMetric(cursor.getString(21));
                ektps.setIssuedBy(cursor.getString(22));
                ektps.setCreateDate(cursor.getString(23));
                ektps.setStatusSend(cursor.getString(24));
                ektps.setEktpVerify(cursor.getString(25));
                return ektps;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        //db.close();
        //return user
        return null;
    }








    public void addUser(User users) throws SQLException{
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put( Constants.UID, users.getUid());
        val.put( Constants.EMAIL, users.getEmail());
        val.put( Constants.USERNAME, users.getUsername());
        val.put( Constants.PASSWORD, users.getPassword());
        val.put( Constants.FULLNAME, users.getFullname());
        val.put( Constants.CREATE_DATE, users.getCreateDate());
        val.put( Constants.ACCESS_DATE, users.getAccessDate() );
        val.put( Constants.MACADDR, users.getMacaddr());
        val.put( Constants.USR_STATUS, users.getStatus());
        db.insert(Constants.TBL_USER, null, val);
        db.close();
    }



    public int updateDbUser(User user) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put( UID, user.getUid());
        val.put( Constants.ACCESS_DATE, user.getAccessDate());
        val.put( Constants.USR_STATUS, user.getStatus());

        db.close();
        // updating row
        return db.update(Constants.TBL_USER, val, UID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    public int updateUser(User user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put( UID, user.getId());
        val.put( Constants.ACCESS_DATE, user.getAccessDate());
        val.put( Constants.USR_STATUS, user.getStatus());
        //db.close();

        // updating row
        return db.update(Constants.TBL_USER, val, Constants.ID + " = ?",
                new String[] { String.valueOf(user.getId()) });
    }

    public User getUser() throws SQLException {

        String selectQuery = "SELECT  * FROM " + Constants.TBL_USER + " LIMIT 1";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor != null){
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                User user = new User();
                user.setId(cursor.getInt(0));
                user.setUid(cursor.getString(1));
                user.setUsername(cursor.getString(2));
                user.setPassword(cursor.getString(3));
                return user;
            }
        } else {

            db.close();
        }
        return null;
    }

    /**
     * Getting user data from database
     * */
    public List<User> getUserList() throws SQLException{

        List<User> usrList = new ArrayList<User>();
        Cursor cursor = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            String selectQuery = "SELECT "+Constants.ID+","+Constants.UID+","+Constants.EMAIL+","+Constants.USERNAME+","+Constants.PASSWORD+" FROM " + Constants.TBL_USER;
            cursor = db.rawQuery(selectQuery, null);
            do{
                User mUser = new User();
                mUser.setId(cursor.getInt(0));
                mUser.setUid(cursor.getString(1));
                mUser.setEmail(cursor.getString(2));
                mUser.setUsername(cursor.getString(3));
                mUser.setPassword(cursor.getString(4));
                //tambah Data Kedalam list

                usrList.add(mUser);
            } while(cursor.moveToNext());
            cursor.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(cursor!=null) cursor.close();
            db.close();
        }
        return usrList;


    }

    public boolean checkUser(String username) throws SQLException {

        // array of columns to fetch
        String[] columns = {
                Constants.ID
        };
        SQLiteDatabase db = this.getReadableDatabase();

        // selection criteria
        String selection = Constants.USERNAME + " = ?";

        // selection argument
        String[] selectionArgs = {username};

        // query user table with condition
        /**
         * Here query function is used to fetch records from user table this function works like we use sql query.
         * SQL query equivalent to this query function is
         * SELECT user_id FROM user WHERE user_email = 'jack@androidtutorialshub.com';
         */
        Cursor cursor = db.query(Constants.TBL_USER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorCount > 0) {
            return true;
        }

        return false;
    }

    /**
     * This method to check user exist or not
     *
     * @param username
     * @param password
     * @return true/false
     */
    public boolean checkUser(String username, String password) throws SQLException {

        SQLiteDatabase db = this.getWritableDatabase();
        boolean exist = false;
        Cursor cursor = null;
        try {
            String selectString = "SELECT * FROM " + Constants.TBL_USER + " WHERE " + Constants.USERNAME + "= ? AND " + Constants.PASSWORD + "= ?";

            cursor = db.rawQuery(selectString, new String[]{username, password});
            if (cursor.getCount() > 0) {
                exist = true;
            } else {
                exist = false;
            }

        } catch (Exception e) {
            Log.d("DEBUG", "checkUser: " + e);
        } finally {

            cursor.close();
            db.close();


        }
        return exist;
    }



    public void addLog(Logs logs) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put( Constants.CREATE_DATE, logs.getCreate());
        val.put( Constants.ISSUED_BY, logs.getIssued());
        val.put( Constants.ACCESS_DATE, logs.getAccess());
        val.put( Constants.SENDLOG, logs.getSend());
        val.put( Constants.STATLOG, logs.getStatus());
        val.put( Constants.REASON, logs.getReason());
        db.insert(Constants.TBL_LOG, null, val);
        db.close();
    }

    // Deleting single ektp
    public void deleteLog(Logs logs) throws SQLException {
        db = this.getWritableDatabase();

        // 2. delete
        db.delete(Constants.TBL_LOG, Constants.ID + " = ?",
                new String[]{String.valueOf(logs.getId())});

        // 3. close
        //db.close();

        Log.d("deleteEktp", logs.toString());

    }

    public ArrayList<Logs> getLogsList() throws SQLException {
        ArrayList<Logs> logsData = new ArrayList<Logs>();
        String selectQuery = "SELECT * FROM " + Constants.TBL_LOG;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        if (cursor.moveToFirst()){
            do{
                Logs logs = new Logs();
                logs.setId(cursor.getInt(0));
                logs.setCreate(cursor.getString(1));
                logs.setIssued(cursor.getString(2));
                logs.setAccess(cursor.getString(3));
                logs.setSend(cursor.getString(4));
                logs.setStatus(cursor.getString(5));
                logs.setReason(cursor.getString(6));


                logsData.add(logs);
            } while(cursor.moveToNext());

        }
        cursor.close();
        //db.close();
        //return ektp
        return logsData;
    }


    public Logs getLogs() throws SQLException {

        String selectQuery = "SELECT  * FROM " + Constants.TBL_LOG + " LIMIT 1";

        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor != null){
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                Logs logs = new Logs();
                logs.setId(cursor.getInt(0));
                logs.setCreate(cursor.getString(1));
                logs.setIssued(cursor.getString(2));
                logs.setAccess(cursor.getString(3));
                logs.setSend(cursor.getString(4));
                logs.setStatus(cursor.getString(5));
                logs.setReason(cursor.getString(6));

                return logs;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        //db.close();
        //return user
        return null;
    }


    public EktpData getFirstList() throws SQLException {

        String selectQuery = "SELECT * FROM " + Constants.TBL_EKTP + " WHERE id = 1";
        db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        if (cursor != null){
            if(cursor.getCount() == 1){
                cursor.moveToFirst();
                EktpData ektpDt = new EktpData();
                ektpDt.setuId(cursor.getInt(0));
                ektpDt.setNik(cursor.getString(1));
                ektpDt.setNama(cursor.getString(2));
                ektpDt.setTmpLahir(cursor.getString(3));
                ektpDt.setTglLahir(cursor.getString(4));
                ektpDt.setJnsKel(cursor.getString(5));
                ektpDt.setGolDarah(cursor.getString(6));
                ektpDt.setAddrs(cursor.getString(7));
                ektpDt.setRt(cursor.getString(8));
                ektpDt.setRw(cursor.getString(9));
                ektpDt.setKel(cursor.getString(10));
                ektpDt.setKec(cursor.getString(11));
                ektpDt.setKab(cursor.getString(12));
                ektpDt.setProv(cursor.getString(13));
                ektpDt.setReligion(cursor.getString(14));
                ektpDt.setMarriage(cursor.getString(15));
                ektpDt.setOccupation(cursor.getString(16));
                ektpDt.setNationality(cursor.getString(17));
                ektpDt.setValUntil(cursor.getString(18));
                ektpDt.setPhotoGraph(cursor.getString(19));
                ektpDt.setSignature(cursor.getString(20));
                ektpDt.setBioMetric(cursor.getString(21));
                ektpDt.setIssuedBy(cursor.getString(22));
                ektpDt.setCreateDate(cursor.getString(23));
                ektpDt.setStatusSend(cursor.getString(24));
                ektpDt.setEktpVerify(cursor.getString(25));
                return ektpDt;
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        //db.close();
        //return user
        return null;
    }


    public void resetLogs() throws SQLException {
        db = this.getWritableDatabase();
        //Delete All Rows
        db.delete(Constants.TBL_LOG, null, null);
        db.close();
    }

    public void resetEktp() throws SQLException {
        db = this.getWritableDatabase();
        //Delete All Rows
        db.delete(Constants.TBL_EKTP, null, null);
        db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + Constants.TBL_EKTP + "'");
        db.close();
    }
}
