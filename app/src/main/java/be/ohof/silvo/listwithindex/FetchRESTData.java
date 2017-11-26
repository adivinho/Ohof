package be.ohof.silvo.listwithindex;

import android.os.AsyncTask;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

/**
 * Created by silvo on 10/10/17.
 */

public class FetchRESTData extends AsyncTask<CustomersBoxHash,Void,Void> {

    String login;
    String passwd;

    public FetchRESTData(String login, String passwd){
        this.login = login;
        this.passwd = passwd;
    }
    protected void onPreExecute() {
        //display progress dialog.
    }

    @Override
    protected Void doInBackground(CustomersBoxHash... params) {
        trustEveryone();

        try {
            Log.e("FetchRESTData", "connecting to pbs ...");

            // ====== Getting customers list

            URL url = new URL("https://pbs.allrelay.com:8442/rest/Customer/get_customer_list/%7B%22login%22:%22"+login+"%22,%22password%22:%22"+passwd+"%22%7D");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("\n\nFailed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            StringBuilder sb = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            JSONParser parser = new JSONParser();
            Iterator<JSONObject> iteratorCustomers = null;

            try {
                Object obj = parser.parse(sb.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONArray customer_list = (JSONArray) jsonObject.get("customer_list");
                iteratorCustomers = customer_list.iterator();
//                while (iteratorCustomers.hasNext()) {
//                    JSONObject ar = iteratorCustomers.next();
//                    if (ar.get("tax_id") != null) tax = ar.get("tax_id").toString();
//                    else tax = "in progress";
//                    params[0].setCustomersList(new Customers(ar.get("name").toString(), ar.get("note").toString(), tax, "101".toString()));
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            conn.disconnect();


            // ====== Getting extensions

            url = new URL("https://pbs.allrelay.com:8442/rest/Customer/get_extensions_list/%7B%22login%22:%22"+login+"%22,%22password%22:%22"+passwd+"%22%7D/%7B%22i_customer%22:%2289906%22%7D");
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream();

            responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("\n\nFailed : HTTP error code : " + conn.getResponseCode());
            }

            br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            sb = new StringBuilder();

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            parser = new JSONParser();

            try {
                Object obj = parser.parse(sb.toString());
                JSONObject jsonObject = (JSONObject) obj;

                JSONArray extensions_list = (JSONArray) jsonObject.get("extensions_list");

                Boolean sucks = false;
                String tax = null;
                Iterator<JSONObject> iteratorExtensions = extensions_list.iterator();
                while(iteratorCustomers.hasNext()) {
                    JSONObject arCust = iteratorCustomers.next();
                    System.out.print("===> "+arCust.get("name")+" ");
                    sucks = false;
                    while (iteratorExtensions.hasNext() & !(sucks)) {
                        JSONObject arExt = iteratorExtensions.next();
                        if (arExt.get("name").equals(arCust.get("name"))) {
                            System.out.println(arExt.get("name") + "\t Ext: " + arExt.get("id"));
                            if (arCust.get("tax_id") != null) tax = arCust.get("tax_id").toString();
                            else tax = "in progress";
                            params[0].setCustomersList(new Customers(arCust.get("name").toString(), arCust.get("note").toString(), tax, arExt.get("id").toString()));
                            sucks = true;
                        }
                    }
                    if (!(sucks)) {
                        if (arCust.get("tax_id") != null) tax = arCust.get("tax_id").toString();
                        else tax = "in progress";
                        params[0].setCustomersList(new Customers(arCust.get("name").toString(), arCust.get("note").toString(), tax, "101"));
                    }
                    iteratorExtensions = extensions_list.iterator();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(Void result) {
        // dismiss progress dialog and update ui
    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

}