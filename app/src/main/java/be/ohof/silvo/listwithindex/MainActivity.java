package be.ohof.silvo.listwithindex;

        import java.net.MalformedURLException;
        import java.util.ArrayList;
        import java.util.LinkedHashMap;
        import java.util.List;
        import java.util.Map;
        import java.util.concurrent.ExecutionException;

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
        import android.widget.TextView;

        import org.json.JSONException;
        import java.lang.Character;

public class MainActivity extends Activity implements OnClickListener {

    Map<String, Integer> mapIndex;

    ExpandableListView expandableListView;
    CustomExpandableListAdapter expandableListAdapter;
    List<String> expandableListTitle;
    LinkedHashMap<String, List<String>> expandableListDetail;

    private int lastExpandedPosition = -1;
    private Handler handlerListCollapse = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove title and set full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        final CustomersBoxHash cb = new CustomersBoxHash();
        cb.clearCustomersList();

        final String login = getResources().getString(R.string.login);
        final String passwd = getResources().getString(R.string.passwd);
        try {
            new FetchRESTData(login, passwd).execute(cb).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        expandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        expandableListDetail = CustomersBoxHash.getData();
        expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
        expandableListAdapter = new CustomExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

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
        // Define the code block to be executed
        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                Log.e(getClass().getSimpleName(), "Called on main thread ...");
                handlerUpdateDB.postDelayed(this, 600000); // 600000 msec -> 10 min
                cb.clearCustomersList();
                try {
                    new FetchRESTData(login,passwd).execute(cb).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                expandableListDetail = CustomersBoxHash.getData();
                expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());
                expandableListAdapter.SetNewData(expandableListTitle, expandableListDetail);
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
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}