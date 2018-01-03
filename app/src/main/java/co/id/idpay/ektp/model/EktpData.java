package co.id.idpay.ektp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created      : Rahman on 8/25/2017.
 * Project      : EKTP.
 * ================================
 * Package      : com.esimtek.model.
 * Copyright    : idpay.com 2017.
 */
public class EktpData {
    @SerializedName("uid")
    @Expose
    private int uId;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("issued_by")
    @Expose
    private String issuedBy;
    @SerializedName("nik")
    @Expose
    private String nik;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("tmp_lahir")
    @Expose
    private String tmpLahir;
    @SerializedName("tgl_lahir")
    @Expose
    private String tglLahir;
    @SerializedName("jns_kel")
    @Expose
    private String jnsKel;
    @SerializedName("gol_darah")
    @Expose
    private String golDarah;
    @SerializedName("alamat")
    @Expose
    private String addrs;
    @SerializedName("rt")
    @Expose
    private String rt;
    @SerializedName("rw")
    @Expose
    private String rw;
    @SerializedName("prov")
    @Expose
    private String prov;
    @SerializedName("kab")
    @Expose
    private String kab;
    @SerializedName("kel")
    @Expose
    private String kel;
    @SerializedName("kec")
    @Expose
    private String kec;
    @SerializedName("agama")
    @Expose
    private String religion;
    @SerializedName("status")
    @Expose
    private String marriage;
    @SerializedName("pekerjaan")
    @Expose
    private String occupation;
    @SerializedName("nationality")
    @Expose
    private String nationality;
    @SerializedName("val_until")
    @Expose
    private String valUntil;
    @SerializedName("foto")
    @Expose
    private String photoGraph;
    @SerializedName("bio_metric")
    @Expose
    private String bioMetric;
    @SerializedName("signature")
    @Expose
    private String signature;
    @SerializedName("status_send")
    @Expose
    private String statusSend;
    @SerializedName("statusektp")
    @Expose
    private String ektpVerify;

    public EktpData() {

    }


    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTmpLahir() {
        return tmpLahir;
    }

    public void setTmpLahir(String tmpLahir) {
        this.tmpLahir = tmpLahir;
    }

    public String getTglLahir() {
        return tglLahir;
    }

    public void setTglLahir(String tglLahir) {
        this.tglLahir = tglLahir;
    }

    public String getJnsKel() {
        return jnsKel;
    }

    public void setJnsKel(String jnsKel) {
        this.jnsKel = jnsKel;
    }

    public String getGolDarah() {
        return golDarah;
    }

    public void setGolDarah(String golDarah) {
        this.golDarah = golDarah;
    }

    public String getAddrs() {
        return addrs;
    }

    public void setAddrs(String addrs) {
        this.addrs = addrs;
    }

    public String getRt() {
        return rt;
    }

    public void setRt(String rt) {
        this.rt = rt;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getProv() {
        return prov;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public String getKab() {
        return kab;
    }

    public void setKab(String kab) {
        this.kab = kab;
    }

    public String getKel() {
        return kel;
    }

    public void setKel(String kel) {
        this.kel = kel;
    }

    public String getKec() {
        return kec;
    }

    public void setKec(String kec) {
        this.kec = kec;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getValUntil() {
        return valUntil;
    }

    public void setValUntil(String valUntil) {
        this.valUntil = valUntil;
    }

    public String getPhotoGraph() {
        return photoGraph;
    }

    public void setPhotoGraph(String photoGraph) {
        this.photoGraph = photoGraph;
    }

    public String getBioMetric() {
        return bioMetric;
    }

    public void setBioMetric(String bioMetric) {
        this.bioMetric = bioMetric;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getStatusSend() {
        return statusSend;
    }

    public void setStatusSend(String statusSend) {
        this.statusSend = statusSend;
    }

    public String getEktpVerify() {
        return ektpVerify;
    }

    public void setEktpVerify(String ektpVerify) {
        this.ektpVerify = ektpVerify;
    }


}
