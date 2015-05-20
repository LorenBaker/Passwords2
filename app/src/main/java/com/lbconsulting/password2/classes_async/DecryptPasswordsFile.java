package com.lbconsulting.password2.classes_async;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lbconsulting.password2.classes.CryptLib;
import com.lbconsulting.password2.classes.MyLog;
import com.lbconsulting.password2.classes.MySettings;
import com.lbconsulting.password2.classes.clsLabPasswords;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Loren on 5/20/2015.
 */
public class DecryptPasswordsFile extends AsyncTask<Void, Long, clsLabPasswords> {

    private String mEncryptedContents;
    private FileDecryptionFinishedListener mCallback;

    public interface FileDecryptionFinishedListener {
        void fileDecryptionFinished(clsLabPasswords passwordsData);
    }

    public DecryptPasswordsFile(String encryptedContents) {
        mEncryptedContents = encryptedContents;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MyLog.i("DecryptPasswordsFile", "onPreExecute");
        // TODO: Stop watching for file changes ??
    }

    @Override
    protected clsLabPasswords doInBackground(Void... params) {
        MyLog.i("DecryptPasswordsFile", "doInBackground");

        clsLabPasswords passwordsData = new clsLabPasswords();
        if (mEncryptedContents.isEmpty()) {
            return passwordsData;
        }

        String decryptedContents = "";
        String key = MySettings.getAppPasswordKey();
        if (key.isEmpty()) {
            return passwordsData;
        }

        try {
            CryptLib mCrypt = new CryptLib();
            String iv = mEncryptedContents.substring(0, 16);
            String encryptedContentsWithoutIv = mEncryptedContents.substring(16);

            decryptedContents = mCrypt.decrypt(encryptedContentsWithoutIv, key, iv);
            MyLog.i("DecryptPasswordsFile", "doInBackground: Decrypted file length = " + decryptedContents.length() + " bytes.");

        } catch (InvalidKeyException e) {
            MyLog.e("MainActivity", "readData: InvalidKeyException");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            MyLog.e("MainActivity", "readData: NoSuchPaddingException");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            MyLog.e("MainActivity", "readData: BadPaddingException");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            MyLog.e("MainActivity", "readData: NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            MyLog.e("MainActivity", "readData: IllegalBlockSizeException");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            MyLog.e("MainActivity", "readData: UnsupportedEncodingException");
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            MyLog.e("MainActivity", "readData: InvalidAlgorithmParameterException");
            e.printStackTrace();
        }

        if (decryptedContents.length() > 0) {
            // parse JSON file string
            decryptedContents = decryptedContents.trim();
            MyLog.i("DecryptPasswordsFile", "doInBackground: trimmed decryptedContents length = " + decryptedContents.length());

            Gson gson = new Gson();
            try {
                passwordsData = gson.fromJson(decryptedContents, clsLabPasswords.class);
            } catch (JsonSyntaxException e) {
                MyLog.e("DecryptPasswordsFile", "doInBackground: JsonSyntaxException: " + e.getMessage());
                e.printStackTrace();
            }

/*            if (passwordsData != null) {
                int lastUserID = -1;
                for (clsUsers user : passwordsData.getUsers()) {
                    if (user.getUserID() > lastUserID) {
                        lastUserID = user.getUserID();
                    }
                }
                if(lastUserID>0) {
                    MySettings.setLastUserID(lastUserID);
                }

                int lastPasswordItemID = -1;
                for (clsItem item : passwordsData.getPasswordItems()) {
                    if (item.getItemID() > lastPasswordItemID) {
                        lastPasswordItemID = item.getItemID();
                    }
                }

                if(lastPasswordItemID>0) {
                    MySettings.setLastPasswordItemID(lastUserID);
                }
                mLastPasswordItemID = lastPasswordItemID;
                validateActiveUser();


            } else {
                MyLog.d("MainActivity", "readData PASSWORDS DATA NULL!");
            }*/

        } else {
            MyLog.e("DecryptPasswordsFile", "doInBackground: Unable to parse JSON file string. Decrypted file length = 0 bytes!");
        }
        return passwordsData;
    }

    @Override
    protected void onPostExecute(clsLabPasswords data) {
        super.onPostExecute(data);
        MyLog.i("DecryptPasswordsFile", "onPostExecute");
        // TODO: Resume watching for file changes ??
    }

}
