package com.lyve.service;

import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by mmadhusoodan on 2/27/15.
 */
public class AbsractServicesBaseClass {
    private static Logger log = Logger.getLogger(DelhpiService.class);

    protected static CloseableHttpClient httpclient;
    protected static String DATEFORMAT = "MM-dd-yyyy HH:mm:ss";
    protected static String token = "MSwxLFJEajcxcGw4bW5Fdm9hdFp4K25keUE9PSxVYXBvWVF4N2pFUFFVYWlJL01aUnlkREpGbWlCRm9hTmtTdGU1YlFSNEs4QWE3bnEwY3Q1Qnh4WFREZ2l1QXFqZS85TVF0bVI2QW1VNXc3c2JBVitTTkJYb1pGclMzNGZxV25IV2pzc1BkRDBQb3dsQXJDaDdPalM1VXB2SDlxbkVDQzdOWnZZUWx4SUREYlNFcTNaRXh3TzlqNUVzQU9Zb2RXcjVVcWZqUVU9";

    //todo get token from Token service
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

    //Methods
    public static String getHumanReadableDateFromEpoch(long epochSec) {
        Date date = new Date(epochSec);
        SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT, Locale.getDefault());

        return format.format(date);
    }

    public static String getDeviceLastSeen(long epochSec) {
        try {
            DateFormat sdf = new SimpleDateFormat(AbsractServicesBaseClass.DATEFORMAT);

            String date1 = AbsractServicesBaseClass.getHumanReadableDateFromEpoch(epochSec);
            String date2 = AbsractServicesBaseClass.getCurrentDateAndTime();

            Date dateObj1 = sdf.parse(date1);
            Date dateObj2 = sdf.parse(date2);

            long lastSeenDiff = dateObj2.getTime() - dateObj1.getTime();

            double diffminutes = (double) (lastSeenDiff / (60 * 1000));
            double diffhours = (double) (lastSeenDiff / (60 * 60 * 1000));
            double diffDays = Math.ceil(diffhours / (24));

            double diffMonth = (diffDays / (30));

            if (diffminutes < 60) {
                return getLastSeenAsString(diffminutes) + " minutes ago";
            }
            if (diffminutes >= 60 && diffhours < 24) {
                return getLastSeenAsString(diffhours) + " hours ago";
            }
            if (diffhours > 24 && diffDays <= 30) {
                return getLastSeenAsString(diffDays) + " days ago";
            }
            if (diffDays > 30) {
                return getLastSeenAsString(diffMonth);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String getLastSeenAsString(double d) {

        return Long.toString((long) Math.ceil(d));

    }

    public static String getCurrentDateAndTime() {
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
        return dateFormat.format(date).toString();
    }

}
