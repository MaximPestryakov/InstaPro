package by.pestryakov.instapro;

import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Auth extends Activity {

	final static String CLIENT_ID = "af7a78ad139b4789bdf44dfe14795ae3";
	final static String CLIENT_SECRET = "61388ea88d934f669fc4d55824767de2";
	final static String REDIRECT_URI = "http://instapro.esy.es/";
	static Handler h;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.auth);

		h = new Handler() {
			public void handleMessage(Message msg) {
				finish();
			}
		};
		android.webkit.CookieManager.getInstance().removeAllCookie();
		WebView webView = (WebView) findViewById(R.id.webview);
		webView.setVerticalScrollBarEnabled(false);
		webView.setHorizontalScrollBarEnabled(false);
		webView.setWebViewClient(new WebViewClient());
		webView.getSettings().setJavaScriptEnabled(true);
		webView.addJavascriptInterface(new WebAppInterface(this), "Android");
		webView.loadUrl("https://instagram.com/oauth/authorize/?client_id="
				+ CLIENT_ID + "&redirect_uri=" + REDIRECT_URI
				+ "&response_type=token");
	}

	static void insertInDB(String response, String token) {
		try {
			JSONObject o = new JSONObject(response).getJSONObject("data");
			String username = o.getString("username");
			String fullName = o.getString("full_name");
			String userId = o.getString("id");
			ContentValues cv = new ContentValues();
			cv.put("token", token);
			cv.put("user_id", userId);
			cv.put("username", username);
			cv.put("full_name", fullName);
			MyDB.db.insert("accounts", null, cv);
			Accounts.h.sendEmptyMessage(0);
			Auth.h.sendEmptyMessage(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class WebAppInterface {
	Context mContext;

	WebAppInterface(Context c) {
		mContext = c;
	}

	@JavascriptInterface
	public void accessToken(String access_token) {
		if (access_token != null)
			new Request("users/self?access_token=", access_token, 1).execute();
	}
}