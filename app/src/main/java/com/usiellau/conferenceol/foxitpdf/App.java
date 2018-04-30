/**
 * Copyright (C) 2003-2018, Foxit Software Inc..
 * All Rights Reserved.
 * <p>
 * http://www.foxitsoftware.com
 * <p>
 * The following code is copyrighted and is the proprietary of Foxit Software Inc.. It is not allowed to
 * distribute any parts of Foxit Mobile PDF SDK to third party or public without permission unless an agreement
 * is signed between Foxit Software Inc. and customers to explicitly grant customers permissions.
 * Review legal.txt for additional license and legal information.
 */
package com.usiellau.conferenceol.foxitpdf;

import android.content.Context;
import android.os.Environment;

import com.foxit.sdk.common.Library;
import com.foxit.sdk.common.PDFError;
import com.foxit.sdk.common.PDFException;
import com.foxit.uiextensions.home.local.LocalModule;
import com.foxit.uiextensions.utils.UIToast;

import java.io.File;

public class App {
    static {
        System.loadLibrary("rdk");
    }

    private static String sn = "SpmcJA/cdlsOygn0dw/MZf5sp1AspuPfBnbUpq9MiF7/Rv7dzB2NGQ==";
    private static String key = "ezKXjl8mvB539NsXJVWofHeLK+gK+j5gE0unILIFELfv9k1yE3BbZ6nL9k/S9EkP/+46Grtg3TsTxqdnpoo2jqDpEX1OQ/zTP/7gl1Dm7TsEkex+WEiQeUe7XD/e2kQTln465+kU+GZfdbYdrweaXpgcY66Lcx5uS5eF2QMJ28i6TJjaN9/8DdRTINuFwbiRdBsiHeWYIVI7HibLof5vFtWBKxyu8Mdy+oNy7UF5EBr/QuZdlx8ForTZrkRwOFln3fhEkajlObr7lBcztAEph06J6XyKr1+DYicQnDyB5yFn+TZCoRAENDK7BnzauHc118ATxa6GVXlwXxIy6ka2ujJjs5q30HnA7k9oqgOfPmNN9sQiTNSzhwl6UQXSea2S8IYDJpRf3mnqomI9UwOxJq6UCKqT5fJpXFIXfSaWO/v9xUFKuIrZvHANQm7ltt3FFu/V0Kfz79WcEg07wpQ3FZ+Ihqw60EGfm9asO67bht044q73NAojjgEyyvBo9JjcTbAf6OMAtJptfmg+zazsXZKjxgkR+twZhV0c4304MqYwMbTQkmK3uyauBHZvviw6gIGE9gepL2LSlL+2BYZ04Zdhw3+Z3otUmVdBhcJ415K/pvRddoyrpvKDz8FF80/JDZ0134JkJS3BSK7f1vpyR56pmTMUB/MM8/0iVrbgpEGeA7lwoNT9O1FljXp20CsJLy5ca7pzRL1eN8DNEndmhdAsqfTt9eO1r7Ua2eKjom+5QT3dpK0x9rZ/KrgWowB8eoA9JUjyAyYqwFVJwpBmRFhFr0KLVHdzZG92Qpk7WMudIzJ+MlJn+O4QCMYz+q5jB6Ir3kwInb3vfgFyTMQ6BdZjUok6czOS8P6uZXqX3mS+WQCtfgrTl2kjWi3djQbAJKeN/TU5WDvTVNwc92eGzAJhE+zxWMNoBQWuIDy46PuP7SIyDB8l/Vf8eFS3LXUFOgMwT4+aw0pswLsSQ95gVgx2CKIjCo4ucFs1d/cA5KZ2+HZbuXbVr0KKZ8lZW2SqQdUkAkCT70fGNoRSxPh/cWyQpgxEyoR0CQC1PoKsmIVavNeB59osZ+uJDnhSgM+TUvTuckaOpBkxc07XRrRlKpRWtzub97tPAfJ5+YrYtv4CUk28KlF0QtGMQeqwV7J64LaKPCX5N5bo8tgkV8anhDaJuM9KSbVhrnjg0vmGT5T4JZZx1p+pjAUuDQLxwatK2+tPAw==";

    private Context mContext;
    private int errCode = PDFError.NO_ERROR.getCode();
    private static App INSTANCE = new App();
    public static App instance() {
        return INSTANCE;
    }

    private App(){
        try {
            Library.init(sn, key);
        } catch (PDFException e) {
            errCode = e.getLastError();
        }
    }

    public boolean checkLicense(){
        switch (PDFError.valueOf(errCode)) {
            case NO_ERROR:
                break ;
            case LICENSE_INVALID:
                UIToast.getInstance(mContext).show("The License is invalid!");
                return false;
            default:
                UIToast.getInstance(mContext).show("Failed to initialize the library!");
                return false;
        }
        return true;
    }

    public void setApplicationContext(Context context) {
        mContext = context;
    }

    public Context getApplicationContext() {
        return mContext;
    }

    LocalModule mLocalModule = null;
    public LocalModule getLocalModule() {
        if (mLocalModule == null) {
            mLocalModule = new LocalModule(mContext);
            mLocalModule.loadModule();

            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String curPath = Environment.getExternalStorageDirectory().getPath() + File.separator +"FoxitSDK";
                File file = new File(curPath);
                if (!file.exists())
                    file.mkdirs();
                File sampleFile = new File(curPath + File.separator +  "Sample.pdf");
                if (!sampleFile.exists()) {
                    mLocalModule.copyFileFromAssertsToTargetFile(sampleFile);
                }

                File guideFile = new File(curPath + File.separator + "complete_pdf_viewer_guide_android.pdf");
                if (!guideFile.exists()) {
                    mLocalModule.copyFileFromAssertsToTargetFile(guideFile);
                }
            }

        }
        return mLocalModule;
    }

    public void onDestroy() {
        if (mLocalModule != null) {
            mLocalModule.unloadModule();
            mLocalModule = null;
        }
    }
}
