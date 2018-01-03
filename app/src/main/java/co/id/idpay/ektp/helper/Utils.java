package co.id.idpay.ektp.helper;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created      : Rahman on 9/10/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.helper.
 * Copyright    : idpay.com 2017.
 */
public class Utils {

    static final String LOG_TAG = "Utils";

    static int
    hexCharToInt(char c) {
        if (c >= '0' && c <= '9') return (c - '0');
        if (c >= 'A' && c <= 'F') return (c - 'A' + 10);
        if (c >= 'a' && c <= 'f') return (c - 'a' + 10);

        return 0;
    }

    /**
     * Converts a hex String to a byte array.
     *
     * @param s A string of hexadecimal characters, must be an even number of
     *          chars long
     * @return byte array representation
     * @throws RuntimeException on invalid format
     */
    public static byte[]
    hexStringToBytes(String s) {
        byte[] ret;

        if (s == null) return null;

        int sz = s.length();

        ret = new byte[sz / 2];

        for (int i = 0; i < sz; i += 2) {
            ret[i / 2] = (byte) ((hexCharToInt(s.charAt(i)) << 4)
                    | hexCharToInt(s.charAt(i + 1)));
        }

        return ret;
    }


    /**
     * Converts a byte array into a String of hexadecimal characters.
     *
     * @param bytes an array of bytes
     * @return hex string representation of bytes array
     */
    public static String
    bytesToHexString(byte[] bytes) {
        if (bytes == null) return null;

        StringBuilder ret = new StringBuilder(2 * bytes.length);

        for (int i = 0; i < bytes.length; i++) {
            int b;

            b = 0x0f & (bytes[i] >> 4);

            ret.append("0123456789ABCDEF".charAt(b));

            b = 0x0f & bytes[i];

            ret.append("0123456789ABCDEF".charAt(b));
        }

        return ret.toString();
    }

    /**
     * Converts a byte array into a int in big endian format.
     *
     * @param bytes  an array of bytes
     * @param offset the offset in byte array
     * @return int
     */
    public static int bytesToInt(byte[] bytes, int offset) {
        int value;
        value = (int) ((bytes[offset + 3] & 0xFF)
                | ((bytes[offset + 2] & 0xFF) << 8)
                | ((bytes[offset + 1] & 0xFF) << 16)
                | ((bytes[offset] & 0xFF) << 24));
        return value;
    }

    public static Bitmap getSignBitmap(byte[] signCompressedBytes, int outSignWidth, int outSignHeight) {
        try {
            //Signature Decompression BitToByteMapping
            byte[] signDecompressedBytes = new byte[outSignWidth * outSignHeight * 2];
            for (int h = 0; h < outSignHeight; h++) {
                for (int w = 0; w < (outSignWidth / 8); w++) {
                    byte num = signCompressedBytes[h * (outSignWidth / 8) + w];
                    int ByteIndex = (h * outSignWidth + w * 8);
                    String str = Integer.toBinaryString(num & 0xFF);
                    str = ("0000000000".substring(str.length(), 8) + str);
                    for (byte s = 0; s < 8; s++) {
                        if (str.charAt(s) == '1') {
                            signDecompressedBytes[2 * (ByteIndex + s)] = (byte) 0xff;
                            signDecompressedBytes[2 * (ByteIndex + s) + 1] = (byte) 0xff;
                        } else {
                            signDecompressedBytes[2 * (ByteIndex + s)] = (byte) 0;
                            signDecompressedBytes[2 * (ByteIndex + s) + 1] = (byte) 0;
                        }
                    }
                }
            }

            Bitmap signBmp = Bitmap.createBitmap(outSignWidth, outSignHeight, Bitmap.Config.RGB_565);
            ByteBuffer bf = ByteBuffer.wrap(signDecompressedBytes);
            signBmp.copyPixelsFromBuffer(bf);

            return signBmp;
        } catch (Exception e) {
            return null;
        }
    }

    public static void delayms(int millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (InterruptedException e) {
        }
    }

    public static void delay(int second) {
        try {
            Thread.sleep(second * 1000);
        } catch (InterruptedException e) {
        }
    }

//    public static copyFileFromSdCardToData(String fromFile, String toFile)

    public static byte[] readDataFile(Context context, String filename) {
        try {
            FileInputStream fin = context.openFileInput(filename);

            int length = fin.available();
            byte[] bytes = new byte[length];
            fin.read(bytes);
            fin.close();

            return bytes;
        } catch (Exception e) {
            return null;
        }
    }


    public static boolean writeDataFile(Context context, String filename, byte[] bytes) {
        try {
            FileOutputStream fout = context.openFileOutput(filename, context.MODE_PRIVATE);
            fout.write(bytes);
            fout.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    //读SD中的文件
    public static byte[] readSdcardFile(String fileName) throws IOException {
        try {
            FileInputStream fin = new FileInputStream(fileName);

            int length = fin.available();

            byte[] buffer = new byte[length];
            fin.read(buffer);
            fin.close();
            return buffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //写数据到SD中的文件
    public static void writeSdcardFile(String fileName, String write_str) throws IOException {
        try {

            FileOutputStream fout = new FileOutputStream(fileName);
            byte[] bytes = write_str.getBytes();

            fout.write(bytes);
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int copyFileFromSdcardToData(Context context, String fromFile, String toFile) {
        try {
            FileInputStream fin = new FileInputStream(fromFile);
            if (fin == null) {
                Log.e("copyfile", "faile open " + fromFile);
                return -1;

            }

            FileOutputStream fout = context.openFileOutput(toFile, context.MODE_PRIVATE);
            if (fout == null) {
                fin.close();
                Log.e("copyfile", "faile open " + fromFile);
                return -2;
            }

            int readLen = 0;
            int byteOffset = 0;
            byte[] buffer = new byte[1024];
            while ((readLen = fin.read(buffer)) > 0) {
                fout.write(buffer, byteOffset, readLen);
                byteOffset += readLen;
            }

            fin.close();
            fout.close();
            return 0;
        } catch (Exception e) {
            Log.e("copyfile", "exception" + e.toString());
        }
        return -3;
    }

    private static String getDirPath(String filePath) {
        int index = filePath.lastIndexOf("/");
        if (index > 0)
            return filePath.substring(0, index);
        return null;
    }

    public static int copyFileFromDataToSdcard(File fromFile, File toFile) {
        try {
            FileInputStream fin = new FileInputStream(fromFile);
            if (fin == null) {
                Log.e("copyfile", "open file fail:" + fromFile);
                return -1;
            }

            String toDirPath = getDirPath(toFile.getAbsolutePath());
            if (null != toDirPath) {
                File toDir = new File(toDirPath);
                if (!toDir.exists()) {
                    toDir.mkdirs();
                }
            }

            if (!toFile.exists()) {
                toFile.createNewFile();
            }

            FileOutputStream fout = new FileOutputStream(toFile);
            if (fout == null) {
                Log.e("copyfile", "open file fail" + toFile);
                fin.close();
                return -2;
            }

            int readLen = 0;
            byte[] buffer = new byte[1024];
            while ((readLen = fin.read(buffer)) > 0) {
                fout.write(buffer, 0, readLen);
            }

            fin.close();
            fout.close();
            return 0;
        } catch (Exception e) {
            Log.e("copyfile", "exception" + e.toString());
//            e.printStackTrace();
        }
        return -3;
    }


    public static int copyFileFromDataToSdcard(String fromFile, String toFile) {
        return copyFileFromDataToSdcard(new File(fromFile), new File(toFile));
    }

    public static String getRandomKey(int maxKeyLength) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < maxKeyLength; i++) {
            long Temp = (int) Math.round(Math.random() * 9);
            s.append(Temp);
        }
        return s.toString();
    }

    public static String getFingerPosString(int index) {
        String[] fingerpos_str = new String[]{
                "Jari Tidak Diketahui",
                "Ibu Jari Kanan",
                "Jari Telunjuk Kanan",
                "Jari Tengah Kanan",
                "Jari Manis Kanan",
                "Jari Kelingking Kanan",
                "Ibu Jari Kiri",
                "Jari Telunjuk Kiri",
                "Jari Tengah Kiri",
                "Jari Manis Kiri",
                "Jari Kelingking Kiri",
                "Jari Tidak Diketahui Kanan",
                "Jari Tidak Diketahui Kiri",};

        if (index < 0 || index >= fingerpos_str.length) {
            return "";
        }
        return fingerpos_str[index];
    }

    public static String getFingerPosTips(String firstFingerIndex, String secondFingerIndex) {
        return getFingerPosTipsWithPrefix(firstFingerIndex, secondFingerIndex, "Please scan your finger...");
    }

    public static String getFingerPosTipsWithPrefix(String firstFingerIndex, String secondFingerIndex, String prefix) {
        int indexFirst = 11, indexSecond = 12;

        try {
            indexFirst = Integer.parseInt(firstFingerIndex);
        } catch (NumberFormatException e) {
            indexFirst = 11;
        }

        try {
            indexSecond = Integer.parseInt(secondFingerIndex);
        } catch (NumberFormatException e) {
            indexSecond = 12;
        }

        return prefix + "\n\n"
                + Utils.getFingerPosString(indexFirst)
                + "\nAtau\n"
                + Utils.getFingerPosString(indexSecond);
    }

    public static String splitStringByLength(String str, int stagelen) {
        String retStr = "";
        for (int start = 0; start < str.length(); start += stagelen) {
            int end = start + stagelen;
            if (end >= str.length()) end = str.length();

            retStr += str.substring(start, end) + " ";
        }
        return retStr;
    }

    public static void deleteFile(String filename) {
        try {
            File file = new File(filename);
            file.delete();
        } catch (Exception e) {
        }
    }


    public static void deleteAllFilesInSdCard() {
        try {
            File dir = new File("/mnt/sdcard");

            if (dir.exists()) {
                File files[] = dir.listFiles();
                for (File tmpFile : files) {
                    if (tmpFile.isFile()) {
                        tmpFile.delete();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public Bitmap getBitmap(byte[] mImage) {
        // Decode to get the bitmap
        if (mImage != null)
            return getBitmapFromRaw(mImage, 252, 324);
        return null;
    }

    public static Bitmap getBitmapFromRaw(byte[] Src, int width, int height) {
        byte [] Bits = new byte[Src.length*4];
        Log.e("FingerPrint", "image length = " + Src.length);
        for(int i = 0 ; i < Src.length; i++) {
            Bits[i*4] = Bits[i*4+1] = Bits[i*4+2] = (byte)Src[i];
            Bits[i*4+3] = -1;
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(Bits));

        return bitmap;
    }

    public static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    public static boolean isConnectionFast(int type, int subType, Context context){
        if(type == ConnectivityManager.TYPE_WIFI){

            return true;

        } else if(type == ConnectivityManager.TYPE_MOBILE){

            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps

                // Above API level 7, make sure to set android:targetSdkVersion to appropriate level to use these

                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        } else {
            return false;
        }

    }

    public static boolean isConnectedFast(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(),info.getSubtype(), context));
    }


    public static String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }


    public static String appVersion(Activity mActivity) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0);
        String version = pInfo.versionName;
        return version;
    }


    public static String encrypted(String dataStr) {
        String result = "";
        try {
            CryptLib _crypt = new CryptLib();
            String key = CryptLib.SHA256(Constants.key, 32); //32 bytes = 256 bit
            String iv = Constants.mIv; //16 bytes = 128 bit
            result = _crypt.encrypt(dataStr, key, iv); //encrypt
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("DEBUG", "encrypted: " + e);
        }
        return result;
    }


    public static String decrypted(String dataStr) {
        String result = "";
        try {
            CryptLib _crypt = new CryptLib();
            String key = CryptLib.SHA256(Constants.key, 32); //32 bytes = 256 bit
            String iv = Constants.mIv; //16 bytes = 128 bit
            result = _crypt.decrypt(dataStr, key, iv); //decrypt
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.d("DEBUG", "encrypted: " + e);
        }
        return result;
    }


    public static Bitmap getBitmapFp(byte[] mImage) {
        // Decode to get the bitmap
        if (mImage != null)
            return getBitmapFromRaw(mImage, 252, 324);
        return null;
    }








}
