package com.lyve.service;

import com.lyve.service.object.AgentObject;
import com.lyve.service.object.MeshObject;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mmadhusoodan on 2/27/15.
 */
public class AbsractServicesBaseClass {

    protected static CloseableHttpClient httpclient;

    protected static SSLContext buildSSLContext()
            throws NoSuchAlgorithmException, KeyManagementException,
            KeyStoreException {
        SSLContext sslcontext = SSLContexts.custom()
                .setSecureRandom(new SecureRandom())
                .loadTrustMaterial(null, new TrustStrategy() {

                    public boolean isTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                        return true;
                    }
                })
                .build();
        return sslcontext;
    }

    protected AgentObject agentObject = AgentObject.getInstance();

    protected MeshObject meshObject = MeshObject.getInstance();

    //Methods

    public static String GetHumanReadableDate(long epochSec, String dateFormatStr) {
        Date date = new Date(epochSec);
        SimpleDateFormat format = new SimpleDateFormat(dateFormatStr, Locale.getDefault());

        return format.format(date);
    }

}
