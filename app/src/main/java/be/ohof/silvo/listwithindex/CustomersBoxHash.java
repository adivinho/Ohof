package be.ohof.silvo.listwithindex;

import android.util.Log;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by silvo on 11/22/17.
 */

public class CustomersBoxHash {

    static LinkedHashMap<String, List<String>> expandableListDetail = new LinkedHashMap<String, List<String>>();

    static {
        Log.e("CustomersBoxHash => ","init");
        expandableListDetail.clear();
    }


    public static void setCustomersList(Customers cs){
        List<String> customerDetails = new ArrayList<String>();
        customerDetails.add(cs.getDescription());
        customerDetails.add("VAT: "+cs.getVAT());
        customerDetails.add("Ext: "+cs.getExtension());
        customerDetails.add("Rack: "+cs.getRack());
        expandableListDetail.put(cs.getName(), customerDetails);
    }

    public LinkedHashMap<String, List<String>> getCustomersList() {
        return this.expandableListDetail;
    }

    public static void clearCustomersList(){
        expandableListDetail.clear();
    }

    public static String[] getCustomersNames() throws MalformedURLException, JSONException {
        Log.e("CustomersBoxHash","getCustomersNames => start");
        Integer i = 0;

        String[] customersNames = new String[expandableListDetail.size()];
        for (Map.Entry<String, List<String>> emp : expandableListDetail.entrySet()) {
//            System.out.println(emp.getKey());
            customersNames[i]=emp.getKey();
            i++;
        }

        Log.e("getCustomersNames => ","stop");
        return customersNames;
    }

    public static String getCustomerExt(Map.Entry<String, List<String>> emp) throws MalformedURLException, JSONException {
        Log.e("getCustomerExt => ","start");
        Integer i = 0;

        String customerExt;
        for (Map.Entry<String, List<String>> temp : expandableListDetail.entrySet()) {
            if (temp.getKey().equalsIgnoreCase(emp.getKey()))
            return temp.getValue().get(2);
        }
        Log.e("getCustomerExt => ","stop");
        return "101";       // No extension was found - provided Secretary extension
    }

//    public String toString() {
//        return String.format("Size: %s, Keys: %s", this.expandableListDetail.size(), this.expandableListDetail.keySet());
//    }

    public int getSize() {
        return this.expandableListDetail.size();
    }

    public static LinkedHashMap<String, List<String>> getData() {
        return expandableListDetail;
    }
}
