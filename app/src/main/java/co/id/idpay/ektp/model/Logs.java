package co.id.idpay.ektp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created      : Rahman on 9/22/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.model.
 * Copyright    : idpay.com 2017.
 */
public class Logs {
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("create_date")
    @Expose
    String create;
    @SerializedName("issued_by")
    @Expose
    String issued;
    @SerializedName("access_log")
    @Expose
    String access;
    @SerializedName("send_log")
    @Expose
    String send;
    @SerializedName("status_log")
    @Expose
    String status;
    @SerializedName("reason_log")
    @Expose
    String reason;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
