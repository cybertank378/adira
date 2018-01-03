package co.id.idpay.ektp.model;

import  co.id.idpay.ektp.helper.Utils;

/**
 * Created      : Rahman on 12/13/2017.
 * Project      : AdiraEktp.
 * ================================
 * Package      : esim.ektp.model.
 * Copyright    : idpay.com 2017.
 */
public class IndonesianIdentityCard {


    public String mPhotographData = "";

    public String mKtpDataArray[] = new String[0];

    public String mSignature = "";

    public String mRightFinger = "";

    public String mLeftFinger = "";

    public byte[] getPhotograph() {
        return Utils.hexStringToBytes(mPhotographData);
    }
    public byte[] getSignatureBytes() {
        return Utils.hexStringToBytes(mSignature);
    }

    private String readKtpDataArray(int index) {
        String str = "";
        try {
            str = mKtpDataArray[index];
        } catch (ArrayIndexOutOfBoundsException e) {
            /* Ignore, invalid data */
        }
        return str;
    }

    public String[] getKtpDataArray() {
        return mKtpDataArray;
    }

    public String getID() {
        return readKtpDataArray(0);
    }

    public String getAddress() {
        return readKtpDataArray(1);
    }

    public String getNeighbourhood() {
        /* Rukun Tetangga = Neighbourhood */
        return readKtpDataArray(2);
    }

    public String getCommunityAssociation() {
        /*  Rukun Warga = Community Association */
        return readKtpDataArray(3);
    }

    public String getPlaceOfBirth() {
        return readKtpDataArray(4);
    }

    public String getDistrict() {
        return readKtpDataArray(5);
    }

    public String getVillage() {
        return readKtpDataArray(6);
    }

    public String getCity() {
        return readKtpDataArray(7);
    }

    public String getGender() {
        return readKtpDataArray(8);
    }

    public String getBloodType() {
        return readKtpDataArray(9);
    }

    public String getReligion() {
        return readKtpDataArray(10);
    }

    public String getMarriageStatus() {
        return readKtpDataArray(11);
    }

    public String getOccupation() {
        return readKtpDataArray(12);
    }

    public String getName() {
        return readKtpDataArray(13);
    }

    public String getDateOfBirth() {
        return readKtpDataArray(14);
    }

    public String getProvince() {
        return readKtpDataArray(15);
    }

    public String getDateOfExpiry() {
        return readKtpDataArray(16);
    }


    public String getFingerPositionFirst() {
        return readKtpDataArray(17);
    }


    public String getFingerPositionSecond() {
        return readKtpDataArray(18);
    }


    public String getNationality() {
        return readKtpDataArray(19);
    }

    public byte[] getSignature() {
        return Utils.hexStringToBytes(mSignature);
    }

    public byte[] getRightFinger() {
        return Utils.hexStringToBytes(mRightFinger);
    }

    public byte[] getLeftFinger() {
        return Utils.hexStringToBytes(mLeftFinger);
    }
}
