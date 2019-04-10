package be.ohof.silvo.listwithindex;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.LinkedHashMap;
import java.util.List;

public class CustomSimpleListAdapter extends ArrayAdapter {
    private Context context;
    private List<String> simpleListTitle;
    private LinkedHashMap<String, List<String>> simpleleListDetail;


    public CustomSimpleListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }
}
