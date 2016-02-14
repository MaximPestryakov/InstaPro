package by.pestryakov.instapro;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

class GetBitmap extends AsyncTask<Void, Void, Bitmap> {

	private final Image image;
	private final ImageView imageView;
	private final boolean isCircle;

	public GetBitmap(Image image, ImageView imageView, boolean isCircle) {
		this.imageView = new WeakReference<ImageView>(imageView).get();
		this.image = new WeakReference<Image>(image).get();
		this.isCircle = isCircle;

	}

	static Bitmap getCircleBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(0xff000000);
		canvas.drawCircle(width / 2, height / 2, width / 2, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		Rect rect = new Rect(0, 0, width, height);
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		Bitmap bitmap = null;
		try {
			URL url = new URL(image.url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			if (isCircle)
				bitmap = getCircleBitmap(BitmapFactory.decodeStream(input));
			else
				bitmap = BitmapFactory.decodeStream(input);
			if (bitmap != null) {
				image.bitmap = bitmap;
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				ContentValues cv = new ContentValues();
				cv.put("image_id", image.id);
				cv.put("bitmap", stream.toByteArray());
				MyDB.db.insert("images", null, cv);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
	}
}

class Request extends AsyncTask<Void, Void, Void> {

	private final String INSTAGRAM = "https://api.instagram.com/v1/";
	private String url;
	private String token;
	private int type;
	private ImageView image;
	private String userId;

	public Request(String url, String token, View view, String userId) {
		this.url = url + token;
		this.token = token;
		this.type = 3;
		this.image = (ImageView) view;
		this.userId = userId;
	}

	public Request(String url, String token, int type) {
		this.url = url + token;
		this.token = token;
		this.type = type;
	}

	@Override
	protected Void doInBackground(Void... params) {
		String response = getRequest();
		switch (type) {
		case 0:
			Posts.response = response;
			Posts.h.sendEmptyMessage(1);
			break;
		case 1:
			Auth.insertInDB(response, token);
			break;
		case 2:
			Posts.response = response;
			Posts.getJSON();
			break;
		case 3:
			try {
				String avatarUrl = new JSONObject(response).getJSONObject(
						"data").getString("profile_picture");
				new GetBitmap(new Image(userId, avatarUrl, null), image, true)
						.execute();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		}
		return null;
	}

	private String getRequest() {
		String result = null;
		try {
			HttpEntity entity = new DefaultHttpClient().execute(
					new HttpGet(INSTAGRAM + url)).getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}