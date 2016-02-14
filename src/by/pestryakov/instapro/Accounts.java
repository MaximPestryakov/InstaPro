package by.pestryakov.instapro;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class Accounts extends ActionBarActivity {

	Cursor c;
	static Handler h;
	static Context context;
	static ArrayList<Account> list = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accounts);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		context = this;
		MyDB.init(context);
		updateList();
		h = new Handler() {
			public void handleMessage(Message msg) {
				updateList();
			}
		};

	}

	void updateList() {
		c = MyDB.db.query("accounts", null, null, null, null, null, null);
		list = new ArrayList<Account>();
		while (c.moveToNext()) {
			String userId = c.getString(c.getColumnIndex("user_id"));
			String token = c.getString(c.getColumnIndex("token"));
			String userName = c.getString(c.getColumnIndex("username"));
			String fullName = c.getString(c.getColumnIndex("full_name"));

			Bitmap avatar = null;
			Cursor c = MyDB.db.query("images", null, "image_id='" + userId
					+ "'", null, null, null, null);
			if (c.moveToFirst()) {
				byte[] bytes = c.getBlob(c.getColumnIndex("bitmap"));
				avatar = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			}

			list.add(new Account(userId, userName, fullName, null, avatar,
					token));
		}
		if (list != null) {
			AccountsAdapter adapter = new AccountsAdapter(this, list);
			ListView listView = (ListView) findViewById(R.id.accounts);
			listView.setAdapter(adapter);
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Posts.token = ((TextView) view.findViewById(R.id.token))
							.getText().toString();
					startActivity(new Intent(context, Posts.class));
				}
			});
		}
	}

	public void addAccount(View view) {
		startActivity(new Intent(this, Auth.class));
	}
}