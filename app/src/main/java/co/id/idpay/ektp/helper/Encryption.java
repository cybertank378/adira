package co.id.idpay.ektp.helper;

import java.security.MessageDigest;

/**
 * Created      : Rahman on 8/30/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.helper.
 * Copyright    : idpay.com 2017.
 */
public class Encryption {
    private static final String TAG = Encryption.class.getSimpleName();

    public static String sha1(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(s.getBytes());
        byte[] bytes = md.digest();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String tmp = Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1);
            buffer.append(tmp);
        }
        return buffer.toString();
    }

}
