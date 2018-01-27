package be.ohof.silvo.listwithindex;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    SharedPreferences.Editor editor;
    SharedPreferences pref;


    public FetchRESTData(String login, String passwd, SharedPreferences.Editor editor, SharedPreferences pref){
        this.login = login;
        this.passwd = passwd;
        this.editor = editor;
        this.pref = pref;
    }

    protected void onPreExecute() {
        //display progress dialog.
    }

    @Override
    protected Void doInBackground(CustomersBoxHash... params) {
        trustEveryone();

        // ==== getting customers list
        StringBuilder sb = new StringBuilder(pushRestRequestCustomers());

        if (sb.length() > 0) {
            System.out.println("Reply size is "+sb.length());
            writeToFile("ohofCustomers.txt", sb);
        }
        else {
            Log.e("====>","Looks like Internet problems. Kept up previous customers data");
            try {
                sb = readFromFile("ohofCustomers.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        JSONParser parser = new JSONParser();
        Iterator<JSONObject> iteratorCustomers = null;

        try {
            Object obj = parser.parse(sb.toString());
            JSONObject jsonObject = (JSONObject) obj;

            JSONArray customer_list = (JSONArray) jsonObject.get("customer_list");
            iteratorCustomers = customer_list.iterator();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ===== getting extensions list
        sb = new StringBuilder(pushRestRequestExtensions());

        if (sb.length() > 0) {
            System.out.println("Reply size is "+sb.length());
            editor.putString("extensions", sb.toString());
            editor.commit();
            writeToFile("ohofExtensions.txt", sb);
        }
        else {
            Log.e("====>","Looks like Internet problems. Kept up previous extensions data");
            try {
                sb = readFromFile("ohofExtensions.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
//                System.out.println("===> "+arCust.get("name")+" "+arCust.get("blocked")+" "+arCust.get("i_customer_type"));
                sucks = false;
                while (iteratorExtensions.hasNext() & !(sucks)) {
                    JSONObject arExt = iteratorExtensions.next();
                    if (arExt.get("name").equals(arCust.get("name"))) {
//                            System.out.println(arExt.get("name") + "\t Ext: " + arExt.get("id"));
                        if (arCust.get("tax_id") != null) tax = arCust.get("tax_id").toString();
                        else tax = "in progress";

                        if (arCust.get("blocked").equals("N") & arCust.get("i_customer_type").equals("1"))
                            params[0].setCustomersList(new Customers(arCust.get("name").toString(), arCust.get("note").toString().replaceAll("&", "and"), tax, arExt.get("id").toString()));
                        sucks = true;
                    }
                }
                if (!(sucks)) {
                    if (arCust.get("tax_id") != null) tax = arCust.get("tax_id").toString();
                    else tax = "in progress";
                    if (arCust.get("blocked").equals("N") & arCust.get("i_customer_type").equals("1"))
                        params[0].setCustomersList(new Customers(arCust.get("name").toString(), arCust.get("note").toString().replaceAll("&", "and"), tax, "Call to Office Hof desk"));
                }
                iteratorExtensions = extensions_list.iterator();
            }
            } catch (Exception e) {
                e.printStackTrace();
            }

        return null;
    }


    public StringBuilder pushRestRequestCustomers() {
        StringBuilder sb = new StringBuilder();
        try {
            Log.e("FetchRESTData", "connecting to pbs (Customers) ...");

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
//                throw new RuntimeException("\n\nFailed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));

            String output;

            try {
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.disconnect();

            return sb;

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
        return sb;
    }

    private StringBuilder pushRestRequestExtensions() {
        StringBuilder sb = new StringBuilder();
        try {
            Log.e("FetchRESTData", "connecting to pbs (Extensions) ...");
            URL url = new URL("https://pbs.allrelay.com:8442/rest/Customer/get_extensions_list/%7B%22login%22:%22"+login+"%22,%22password%22:%22"+passwd+"%22%7D/%7B%22i_customer%22:%2289906%22%7D");
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.getOutputStream();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("\n\nFailed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            String output;

            try {
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            conn.disconnect();

            return sb;

        } catch (MalformedURLException e) {
            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();
        }
        return sb;
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

    private Boolean writeToFile(String filename, StringBuilder data) {
        try {

            if (Environment.getExternalStorageDirectory().canWrite()){
                File lfile = new File(Environment.getExternalStorageDirectory(), filename);
                FileWriter lfilewriter = new FileWriter(lfile);
                BufferedWriter lout = new BufferedWriter(lfilewriter);
                // If file does not exists, then create it
                if (!lfile.exists()) {
                    lfile.createNewFile();
                }
                lout.write(data.toString());
                lout.close();
            }
            else
                Log.e("Error ","can't write");

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private StringBuilder readFromFile(String filename) throws IOException {
        StringBuilder sb = new StringBuilder();
        File f = new File(Environment.getExternalStorageDirectory(),filename);
        FileReader fr = new FileReader(f);
        BufferedReader br  = new BufferedReader(fr);

        String output = null;

        try {
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        br.close();
        return sb;
    }
}