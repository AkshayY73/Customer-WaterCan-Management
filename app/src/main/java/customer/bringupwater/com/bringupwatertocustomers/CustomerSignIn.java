package customer.bringupwater.com.bringupwatertocustomers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;

public class CustomerSignIn extends AppCompatActivity  {
    //protected static final SharedPreferences settings = null;
    Button b1,b2;
    EditText email, password;
    String Email, Password;
    private WebView mWebView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private boolean isUserBackButtonPressed = false;
   // public  String BaseUrl="http://www.bringupwater.com";
    public String BaseUrl="http://192.168.1.7/Water_Management";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        b1 = (Button) findViewById(R.id.button);
        email = (EditText) findViewById(R.id.editText3);
        password = (EditText) findViewById(R.id.editText4);
        b2=(Button) findViewById(R.id.button3);
      /*  if(SaveSharedPreference.getUserName(CustomerSignIn.this).length() != 0)
        {
            startActivity(new Intent(CustomerSignIn.this,CustomerHome.class));
        }
        else
        {
            // Stay at the current activity.
        } */

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            Toast.makeText(this, "GPS is Enabled in your devide", Toast.LENGTH_SHORT).show();
        }
        else{
            showGPSDisabledAlertToUser();
        }

        b1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String p=md5(password.getText().toString());
                SendPostRequest(email.getText().toString(),p);

            }
        });


        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(CustomerSignIn.this,CustomerSignUp.class));
            }
        });


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }); */
    }

    private void showGPSDisabledAlertToUser(){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        android.support.v7.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public static final String md5(final String toEncrypt) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(toEncrypt.getBytes());
            final byte[] bytes = digest.digest();
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                sb.append(String.format("%02X", bytes[i]));
            }
            return sb.toString().toLowerCase();
        } catch (Exception exc) {
            return ""; // Impossibru!
        }
    }


    public void SignUpView()
    {
        setContentView(R.layout.content_customer_sign_up);
    }

    public void SendPostRequest(final String Email, String Password){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            protected void onPreExecute() {
            }

            protected String doInBackground(String... params) {

                try {

                    URL url = new URL(BaseUrl+"/view/checkAndroidLogin.php");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("email", params[0]);
                    postDataParams.put("password", params[1]);

                    Log.e("params", postDataParams.toString());


                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(25000);
                    conn.setConnectTimeout(25000);
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(getPostDataString(postDataParams));

                    writer.flush();
                    writer.close();
                    os.close();

                    int responseCode = conn.getResponseCode();

                    if (responseCode == HttpsURLConnection.HTTP_OK) {

                        BufferedReader in = new BufferedReader(new
                                InputStreamReader(
                                conn.getInputStream()));

                        StringBuffer sb = new StringBuffer("");
                        String line = "";

                        while ((line = in.readLine()) != null) {

                            sb.append(line);
                            break;
                        }

                        in.close();
                        return sb.toString();

                    } else {
                        return new String("false : " + responseCode);
                    }
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }

            }

            @Override
            protected void onPostExecute(String result) {

                String res[]=result.split(",");
         //       Log.e("array",res[0]+","+res[1]+","+res[2]+","+res[3]+","+res[4]+","+res[5]);
                Toast.makeText(getApplicationContext(), res[0],
                        Toast.LENGTH_LONG).show();
                 if(res[0].equals("login success"))
                 {
                     //SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                  //   SharedPreferences.Editor editor=settings.edit();
                    // editor.putString("username",Email);
                     //startActivity(new Intent(CustomerSignIn.this,CustomerHome.class));

                    /* SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                     editor.putString("email", res[3]);
                     editor.putString("id", res[1]);
                     editor.putString("firstname",res[2]);
                     editor.commit(); */

                     Intent intent = new Intent(getApplicationContext(), CustomerHome.class);
                     intent.putExtra("Email",res[3]);
                     intent.putExtra("id",res[1]);

                     intent.putExtra("first",res[2]);
                     intent.putExtra("fname",res[5]);
                     intent.putExtra("lname",res[4]);
                     startActivity(intent);
                 }
            }


        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
      //  String a=md5(password.getText().toString());
        sendPostReqAsyncTask.execute(Email, Password);

    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    public void onBackPressed() {
        if(!isUserBackButtonPressed){
            Toast.makeText(this,"Press Back Again to Exit", Toast.LENGTH_LONG).show();
            isUserBackButtonPressed = true;
        }
        else{
            if (Build.VERSION.SDK_INT >= 19) {
                finishAffinity();
                System.exit(0);
            }
            else
            {
                finish();
            }
        }

        new CountDownTimer(3000,1000){

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isUserBackButtonPressed = false;
            }
        }.start();

    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
