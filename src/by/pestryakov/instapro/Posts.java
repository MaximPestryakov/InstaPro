package by.pestryakov.instapro;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Posts extends ActionBarActivity implements OnRefreshListener {

	static Context context;
	ListView listView;
	static Cursor c;
	static String token = null;
	static Handler h;
	static String response;
	SwipeRefreshLayout mSwipeRefreshLayout;
	static String nextURL;
	static ArrayList<Post> list;
	static PostsAdapter adapter = null;
	ActionBarDrawerToggle toggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posts);

		context = getApplicationContext();
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer);
		toggle = new ActionBarDrawerToggle(this, drawer, R.string.open,
				R.string.close);
		toggle.setDrawerIndicatorEnabled(true);
		drawer.setDrawerListener(toggle);
		String[] navMenu = getResources().getStringArray(R.array.navMenu);
		ListView navDrawer = (ListView) findViewById(R.id.navDrawer);
		navDrawer.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, navMenu));

		mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
		mSwipeRefreshLayout.setOnRefreshListener(this);
		mSwipeRefreshLayout.setColorScheme(17170450);

		c = MyDB.db.query("accounts", null, null, null, null, null, null);

		h = new Handler() {
			public void handleMessage(Message msg) {
				int index = msg.what;
				switch (index) {
				case 0:
					updateFeed();
					break;
				case 1:
					updateList();
					break;
				}
			}
		};
		updateFeed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (toggle.onOptionsItemSelected(item))
			return true;
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		toggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		toggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onRefresh() {
		mSwipeRefreshLayout.setRefreshing(true);
		updateFeed();
		mSwipeRefreshLayout.setRefreshing(false);
	}

	void updateFeed() {
		list = new ArrayList<Post>();
		new Request("users/self/feed?count=20&access_token=", token, 0)
				.execute();
	}

	static void continueFeed() {
		new Request(nextURL, token, 2).execute();
	}

	static void getJSON() {
		Cursor c;
		try {
			JSONObject r = new JSONObject(response);
			nextURL = r.getJSONObject("pagination").getString("next_url")
					.substring(29);
			JSONArray data = r.getJSONArray("data");
			for (int i = 0; i < data.length(); ++i) {
				JSONObject o = data.getJSONObject(i);
				JSONObject u = o.getJSONObject("user");
				String imageId = o.getString("id");
				String userId = u.getString("id");
				String userName = u.getString("username");
				String fullName = u.getString("full_name");
				String avatarUrl = u.getString("profile_picture");
				String imageUrl = o.getJSONObject("images")
						.getJSONObject("standard_resolution").getString("url");
				boolean hasLiked = o.getBoolean("user_has_liked");
				int likes = o.getJSONObject("likes").getInt("count");
				long time = Long.valueOf(o.getString("created_time")) * 1000L;
				SimpleDateFormat dateFormat = new SimpleDateFormat(
						"dd.MM.yyyy HH:mm");
				dateFormat.setTimeZone(TimeZone.getDefault());
				String date = dateFormat.format(new Date(time));
				String caption = null;
				try {
					JSONObject captionObj = o.getJSONObject("caption");
					caption = captionObj.getString("text");
				} catch (Exception e) {
				}

				Bitmap image = null;
				c = MyDB.db.query("images", null, "image_id='" + imageId + "'",
						null, null, null, null);
				if (c.moveToFirst()) {
					byte[] bytes = c.getBlob(c.getColumnIndex("bitmap"));
					image = BitmapFactory.decodeByteArray(bytes, 0,
							bytes.length);
				}

				Bitmap avatar = null;
				c = MyDB.db.query("images", null, "image_id='" + userId + "'",
						null, null, null, null);
				if (c.moveToFirst()) {
					byte[] bytes = c.getBlob(c.getColumnIndex("bitmap"));
					avatar = BitmapFactory.decodeByteArray(bytes, 0,
							bytes.length);
				}
				list.add(new Post(imageId, imageUrl, image, userId, userName,
						fullName, avatarUrl, avatar, hasLiked, likes, date,
						caption));
				if (adapter != null)
					adapter.notifyDataSetChanged();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void updateList() {
		getJSON();
		adapter = new PostsAdapter(this);
		listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);
	}

	public void onViewClick(View view) {
		switch (view.getId()) {
		case R.id.favorite:
			break;
		case R.id.like:
			break;
		}
	}
}