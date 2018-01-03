package co.id.idpay.ektp.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created      : Rahman on 8/31/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.model.
 * Copyright    : idpay.com 2017.
 */
public class Result {
    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private User user;

    public Result(Boolean error, String message, User user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
