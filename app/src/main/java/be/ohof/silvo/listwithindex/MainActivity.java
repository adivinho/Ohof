package be.ohof.silvo.listwithindex;

        import java.net.MalformedURLException;
        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.concurrent.ExecutionException;

        import android.content.res.Resources;
        import android.os.Bundle;
        import android.app.Activity;
        import android.os.Handler;
        import android.util.Log;
        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.AdapterView;
        import android.widget.ArrayAdapter;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;
        import android.widget.Toast;

        import org.json.JSONException;
        import java.lang.Character;

public class MainActivity extends Activity implements OnClickListener {

    Map<String, Integer> mapIndex;
    ListView namesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title and set full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        final Toast toast = new Toast(getApplicationContext());

        final CustomersBox cb = new CustomersBox();
        cb.clearCustomersList();
        Log.e("MainActivity",cb.toString());
        final String login = getResources().getString(R.string.login);
        final String passwd = getResources().getString(R.string.passwd);
        try {
            new FetchRESTData(login, passwd).execute(cb).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String[] fruits = getResources().getStringArray(R.array.fruits_array);
        Arrays.asList(fruits);

        namesList = (ListView) findViewById(R.id.list_customers);

        try {
            ArrayAdapter<String> itemsAdapter =
                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cb.getCustomersNames());
            namesList.setAdapter(itemsAdapter);
//            namesList.setAdapter(new ArrayAdapter<String>(this,
//                    android.R.layout.simple_list_item_1, cb.getCustomersNames()));
            Log.d("itemsAdapter(count)", String.valueOf(itemsAdapter.getCount()));
//            ArrayAdapter<String> itemsAdapter2 =
//                    new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fruits);
//            namesList.setAdapter(itemsAdapter2);
//            itemsAdapter.add("123");
//            namesList.removeAllViews();
//            Log.e("itemsAdapter(count)", String.valueOf(itemsAdapter.getCount()));
            itemsAdapter.notifyDataSetChanged();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            getIndexList(cb.getCustomersNames());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        namesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                String vat = cb.getVat((String) ((TextView) view).getText());
                String ext = cb.getExt((String) ((TextView) view).getText());
                showAToast(toast, ext+"\nVAT: "+vat);
            }
        });

        displayIndex();

        // Create the Handler object (on the main thread by default)
        final Handler handler = new Handler();
        // Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                // Do something here on the main thread
                Log.e("Handlers", "Called on main thread");
                handler.postDelayed(this, 360000); // 360 sec -> 6 min
                cb.clearCustomersList();
                try {
                    new FetchRESTData(login,passwd).execute(cb).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        // Run the above code block on the main thread after 360 seconds
        handler.post(runnableCode);
    }

    private void getIndexList(String[] names) {
        mapIndex = new LinkedHashMap<String, Integer>();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String index = name.substring(0, 1);
            if (Character.isDigit(index.charAt(0))) {
                index = "#";
            }
            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private void displayIndex() {
        LinearLayout indexLayout = (LinearLayout) findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }
    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        namesList.setSelection(mapIndex.get(selectedIndex.getText()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showAToast (Toast t, String st){ //"Toast toast" is declared in the class
        try{
            Log.e("showAToast => ","try");
            t.getView().isShown();     // true if visible
            t.setText(st);
            t.setDuration(Toast.LENGTH_SHORT);
        } catch (Exception e) {         // invisible if exception
            t = Toast.makeText(getApplicationContext(), st, Toast.LENGTH_LONG);
            Log.e("showAToast => ","catch");
        }
        t.show();
    }
}