package be.ohof.silvo.listwithindex;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.net.URL;
import java.util.regex.Pattern;

public class InitActivity extends Activity  {
    Button bt_start;
    CheckBox checkBox_type;
    ImageButton bt_clear, bt_exit;
    EditText api_server, api_login, api_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        //Add all your codes here
        bt_start        = findViewById(R.id.buttonStart);
        bt_clear        = findViewById(R.id.buttonClear);
        bt_exit         = findViewById(R.id.buttonExit);
        api_server      = findViewById(R.id.server);
        api_login       = findViewById(R.id.login);
        api_password    = findViewById(R.id.password);
        checkBox_type   = findViewById(R.id.checkBoxType);

        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkURL(api_server.getText().toString().trim())) {
                    Toast.makeText(InitActivity.this, "Server name isn't valid", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(InitActivity.this, "Loading data ...", Toast.LENGTH_SHORT).show();
                    InitActivity.this.finish();
                    Intent activity2Intent = new Intent(getApplicationContext(), MainActivity.class);
                    activity2Intent.putExtra("api_address", api_server.getText().toString());
                    if(checkBox_type.isChecked()) {
                        activity2Intent.putExtra("api_port", "8442");
                    }
                    else {
                        activity2Intent.putExtra("api_port", "443");
                    }
                    activity2Intent.putExtra("login", api_login.getText().toString());
                    activity2Intent.putExtra("passwd", api_password.getText().toString());
                    startActivity(activity2Intent);
                }
            }
        });

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                api_server.getText().clear();
                api_login.getText().clear();
                api_password.getText().clear();
            }
        });

        bt_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Happy coding :)

    }

    public static boolean checkURL(CharSequence input) {
        if (TextUtils.isEmpty(input)) {
            return false;
        }
        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean isURL = URL_PATTERN.matcher(input).matches();
        if (!isURL) {
            String urlString = input + "";
            if (URLUtil.isNetworkUrl(urlString)) {
                try {
                    new URL(urlString);
                    isURL = true;
                } catch (Exception e) {
                }
            }
        }
        return isURL;
    }
}