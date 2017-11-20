package be.ohof.silvo.listwithindex;

import android.util.Log;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by silvo on 9/25/17.
 */

public class CustomersBox {

    static List <Customers> customersList = new ArrayList<>();

    static {
        Log.e("CustomersBox => ","init");
        customersList.clear();
    }

    public static void setCustomersList(Customers cs){
        customersList.add(cs);
    }

    public static void clearCustomersList(){
        customersList.clear();
    }
    public static String[] getCustomersNames() throws MalformedURLException, JSONException {
        Log.e("getCustomersNames => ","start");
        Integer i = 0;

        String[] customersNames = new String[customersList.size()];

        for (Customers emp : customersList) {
            customersNames[i]= (emp.getName());
            i++;
        }

        Log.e("getCustomersNames => ","stop");
        return customersNames;
    }

    public String getVat(String name){
        for (Customers emp : customersList) {
            if (name.equals(emp.getName())) {
                return emp.getVAT();
            }
        }
        return "in progress";
    }

    public String getExt(String name){
        for (Customers emp : customersList) {
            if (name.equals(emp.getName())) {
                return emp.getExtension();
            }
        }
        return "-";
    }
}
