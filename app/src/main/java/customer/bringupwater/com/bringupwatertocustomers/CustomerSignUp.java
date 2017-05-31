package customer.bringupwater.com.bringupwatertocustomers;

import android.content.ClipData;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class CustomerSignUp extends AppCompatActivity {

   Button reg,signin;
  
    EditText fname,laname,email,phone,password,cpassword;
    String Fname,LName,Email,Phone,Password,CPassword;
   // public  String BaseUrl="http://www.bringupwater.com";
    public String BaseUrl="http://192.168.1.7/Water_Management";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_customer_sign_up);

        reg = (Button) findViewById(R.id.button2);
       // signin=(Button) findViewById(R.id.button4);
        fname=(EditText)findViewById(R.id.editText);
        laname=(EditText)findViewById(R.id.editText2);
        email = (EditText) findViewById(R.id.editText5);
        phone=(EditText) findViewById(R.id.editText6);
        password = (EditText) findViewById(R.id.editText7);
        cpassword=(EditText) findViewById(R.id.editText8);
        reg.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Fname=fname.getText().toString();
                LName=laname.getText().toString();
                Email=email.getText().toString();
                Phone=phone.getText().toString();
                Password=md5(password.getText().toString());
                CPassword=md5(cpassword.getText().toString());
                SendPostRequest(Fname,LName,Email,Phone,Password,CPassword);


            }
        });

      /*  signin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                startActivity(new Intent(CustomerSignUp.this,CustomerSignIn.class));
            }
        }); */


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

    public void SendPostRequest(String FName,String LName,String Email,String Phone,String Password,String CPassword){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {

            protected void onPreExecute() {
            }

            protected String doInBackground(String... params) {

                try {

                    URL url = new URL(BaseUrl+"/view/RegisterCustomer.php");

                    JSONObject postDataParams = new JSONObject();
                    postDataParams.put("fname",params[0]);
                    postDataParams.put("lname",params[1]);
                    postDataParams.put("email", params[2]);
                    postDataParams.put("phone",params[3]);
                    postDataParams.put("password", params[4]);
                    postDataParams.put("cpassword",params[5]);

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
                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();
            }


        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(Fname,LName,Email,Phone,Password,CPassword);

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
            startActivity(new Intent(CustomerSignUp.this,CustomerSignIn.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
