package co.id.idpay.ektp.helper;

import java.util.ArrayList;

/**
 * Created      : Rahman on 12/28/2017.
 * Project      : AdiraEktp.
 * ================================
 * Package      : co.id.idpay.ektp.helper.
 * Copyright    : idpay.com 2017.
 */
public interface PermissionResultCallback {
    void PermissionGranted(int request_code);
    void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
    void PermissionDenied(int request_code);
    void NeverAskAgain(int request_code);
}
