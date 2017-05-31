package customer.bringupwater.com.bringupwatertocustomers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CustomerHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private WebView mWebView;
    private  boolean isUserBackButtonPressed=false;
    public ProgressBar pbar;
    private View navHeader;
    private TextView custname;
    private TextView custemail;
    public  String first;
    private String Email,uid,fname,lname;
   // public  String BaseUrl="http://www.bringupwater.com";
    public String BaseUrl="http://192.168.1.7/Water_Management";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);






        Bundle bundle = getIntent().getExtras();

         Email = bundle.getString("Email");
         uid=bundle.getString("id");
        first=bundle.getString("first");
       fname=bundle.getString("fname");
        lname=bundle.getString("lname");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeader = navigationView.getHeaderView(0);

        custname=(TextView) navHeader.findViewById(R.id.cname);
        custemail=(TextView) navHeader.findViewById(R.id.cemail);
        custname.setText(fname+" "+lname);
        custemail.setText(Email);

        mWebView = (WebView) findViewById(R.id.webView);
       // pbar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView.setWebViewClient(new WebViewClient());

        mWebView.addJavascriptInterface(new CustomerHome.WebJavaScriptInterface(this), "cust");


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        mWebView.getSettings().setAppCacheEnabled(true);

        if (Build.VERSION.SDK_INT >= 19) {
            mWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT >= 11) {
            mWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        else
        {

        }

        final Activity activity=CustomerHome.this;


        if (first.equals("")) {
              mWebView.loadUrl(BaseUrl+"/view/customer/Location/index.php?email=" + Email + "&id=" + uid);
          } else {
              mWebView.loadUrl(BaseUrl+"/view/customer/Location/index.php?first&email=" + Email + "&id=" + uid);
          }


        mWebView.setWebChromeClient(new WebChromeClient(){

            ProgressDialog progressDialog =  new ProgressDialog(CustomerHome.this);

            @Override
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    // in standard case YourActivity.this

                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                  //  pbar.setVisibility(View.VISIBLE);
                }

                if (progress == 100) {

                    progressDialog.dismiss();
                  //  pbar.setVisibility(View.INVISIBLE);

                    // }

                }
            }
        });
        
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(!isUserBackButtonPressed){
            Toast.makeText(this,"Press Back Again to Exit", Toast.LENGTH_LONG).show();
            isUserBackButtonPressed = true;

        }else {

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
        getMenuInflater().inflate(R.menu.customer_home, menu);
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
            //startActivity(new Intent(CustomerHome.this,CustomerSignIn.class));
            mWebView.loadUrl(BaseUrl+"/view/customer/customer_signIn.php?logout");

            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("username");
        }

        else if(id==R.id.action_edit)
        {
            mWebView.loadUrl(BaseUrl+"/view/customer/editProfile.php");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
             mWebView.loadUrl(BaseUrl+"/view/customer/index.php");
        } else if (id == R.id.nav_chose) {
          /*  new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Exit from Home Page")
                    .setMessage("Are you Sure...! You will be Logged Out ")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                          //  mWebView.loadUrl("http://192.168.1.7/Water_Management/view/customer/customer_signIn.php?logout");
                            Intent intent = new Intent(getApplicationContext(), CustomerSignIn.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("EXIT", true);
                            startActivity(intent);
                        }

                    })
                    .setNegativeButton("No", null)
                    .show(); */
            if (first.equals("")) {
                mWebView.loadUrl(BaseUrl+"/view/customer/Location/index.php?email=" + Email + "&id=" + uid);
            } else {
                mWebView.loadUrl(BaseUrl+"/view/customer/Location/index.php?first&email=" + Email + "&id=" + uid);
            }
        } else if (id == R.id.nav_orders) {
            mWebView.loadUrl(BaseUrl+"/view/customer/customerOrderDetails.php");
        }

        else if (id == R.id.nav_pay) {
         //  mWebView.loadUrl("http://192.168.1.7/Water_Management/view/customer/customerOrderDetails.php");
        } else if (id == R.id.nav_due) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    final class WebJavaScriptInterface {

        WebJavaScriptInterface(Context c) {

        }

        @JavascriptInterface
        public void clickOnLogin() {

            Toast.makeText(getApplicationContext(),
                    "Sorry, No Service Available Here..",
                    Toast.LENGTH_SHORT).show();

        }
        @JavascriptInterface
        public void clickOnLogout() {



            startActivity(new Intent(CustomerHome.this,CustomerSignIn.class));

        }
    }
}
