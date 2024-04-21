package apps.ijp.helpinghandsv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class TransportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transport);
        Intent intentget = getIntent();
        String did = intentget.getStringExtra("did");
        Log.e("did",did);
        String ipadd = getResources().getString(R.string.ipadd);
        WebView myWebView = (WebView) findViewById(R.id.transmap);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl("http://"+ipadd+"/HHAPI/transmap.php?did="+did);
    }

}
