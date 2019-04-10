package be.ohof.silvo.listwithindex;

        import java.lang.reflect.InvocationTargetException;
        import java.lang.reflect.Method;
        import java.net.MalformedURLException;
        import java.util.ArrayList;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.concurrent.ExecutionException;

        import android.annotation.SuppressLint;
        import android.app.ActionBar;
        import android.content.SharedPreferences;
        import android.os.Bundle;
        import android.app.Activity;
        import android.os.Handler;
        import android.util.Log;
        import android.view.Menu;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.Window;
        import android.view.WindowManager;
        import android.widget.ExpandableListView;
        import android.widget.LinearLayout;
        import android.widget.ListView;
        import android.widget.TextView;

        import org.json.JSONException;
        import java.lang.Character;

public class MainActivity extends Activity implements OnClickListener {

    ListView simpleListView;
    Map<String, Integer> mapIndex;

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle, expandableListRack;
    LinkedHashMap<String, List<String>> expandableListDetail;
    CustomersBoxHash cb = new CustomersBoxHash();

    private int lastExpandedPosition = -1;
    private Handler handlerListCollapse = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title and set full screen till API 21 (Android 5.0.0)
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        final SharedPreferences.Editor editor = pref.edit();

        cb.clearCustomersList();

        final String login = getResources().getString(R.string.login);
        final String passwd = getResources().getString(R.string.passwd);

//        try {
//            new FetchRESTData(login, passwd, editor, pref).execute(cb).get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }


    //    setContentView(R.layout.launch_screen);

    //    simpleListView = findViewById(R.id.simpleListView);
        setContentView(R.layout.activity_main); // ----------

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = CustomersBoxHash.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListRack = CustomersBoxHash.getRacks();
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListRack, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() { // ---- Disable expanding
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                return true; // This way the expander cannot be collapsed
            }
        });
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() { // ---- Set auto-closing action
            @Override
            public void onGroupExpand(final int groupPosition) {
                if (lastExpandedPosition != -1 && groupPosition != lastExpandedPosition) {
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
                handlerListCollapse.removeCallbacksAndMessages(null);

                handlerListCollapse.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        expandableListView.collapseGroup(groupPosition);
                        lastExpandedPosition = -1;
                    }
                }, 10000);
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                handlerListCollapse.removeCallbacksAndMessages(null);
                lastExpandedPosition = -1;
            }
        });

        try {
            getIndexList(cb.getCustomersNames());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        displayIndex();

//         Create the Handler object (on the main thread by default)
        final Handler handlerUpdateDB = new Handler();
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.e(getClass().getSimpleName(), "Called on main thread ...");
                handlerUpdateDB.postDelayed(this, 7200000); // 600000 msec -> 10 min

                cb.clearCustomersList();
                try {
                    new FetchRESTData(login,passwd,editor, pref).execute(cb).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                expandableListDetail = CustomersBoxHash.getData();
                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListRack = CustomersBoxHash.getRacks();
                expandableListAdapter.SetNewData(expandableListTitle, expandableListRack, expandableListDetail);
                expandableListView.setAdapter(expandableListAdapter);

                try {
                    getIndexList(cb.getCustomersNames());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        handlerUpdateDB.post(runnableCode);
    }

    private void getIndexList(String[] names) {
        if (mapIndex != null)
            mapIndex.clear();
        else
            mapIndex = new LinkedHashMap<String, Integer>();
        mapIndex = initABClist();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String index = name.substring(0, 1);
            if (Character.isDigit(index.charAt(0))) {
                index = "#";
            }
            if (Character.isLowerCase(index.charAt(0))){
                index = ""+Character.toUpperCase(index.charAt(0));
            }
            if (mapIndex.get(index) == null)
                mapIndex.put(index, i);
        }
    }

    private Map<String, Integer> initABClist() {
        Map<String, Integer> tmpMapIndex = new LinkedHashMap<String, Integer>();
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            tmpMapIndex.put(""+alphabet,null);
        }
        return tmpMapIndex;
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
        indexLayout.postInvalidate();
    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;

        expandableListView.collapseGroup(lastExpandedPosition);
        lastExpandedPosition = -1;

        if (mapIndex.get(selectedIndex.getText()) != null)
            expandableListView.setSelection(mapIndex.get(selectedIndex.getText()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        System.out.println("Status bar has been expanded");
        try
        {
            if(!hasFocus)            {
                System.out.println("Going to collapse it");
                @SuppressLint("WrongConstant") final Object service  = getSystemService("statusbar");
                Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
                final Method collapse = statusbarManager.getMethod("collapsePanels");
                Method expand = statusbarManager.getMethod("expandNotificationsPanel");

                expand.invoke(service);
                collapse.setAccessible(true);
                for(int j = 0; j<25; j++){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                collapse.invoke(service);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }, j*150);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        Thread.currentThread().getState();
    }

    private class StableArrayAdapter {
    }
}