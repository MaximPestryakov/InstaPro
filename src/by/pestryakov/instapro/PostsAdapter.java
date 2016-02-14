package by.pestryakov.instapro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PostsAdapter extends BaseAdapter {

	Context context;
	LayoutInflater inflater;
	int lastPosition = 1;

	public PostsAdapter(Context context) {
		this.context = context;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return Posts.list.size();
	}

	@Override
	public Post getItem(int position) {
		return Posts.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.post_item, parent, false);

		if (position + 2 == getCount())
			Posts.continueFeed();

		Post post = getItem(position);
		ImageView image = (ImageView) view.findViewById(R.id.image);
		if (post.image.bitmap == null) {
			GetBitmap task = new GetBitmap(post.image, image, false);
			task.execute();
		} else
			image.setImageBitmap(post.image.bitmap);
		image = (ImageView) view.findViewById(R.id.avatar);
		if (post.user.image.bitmap == null) {
			GetBitmap task = new GetBitmap(post.user.image, image, true);
			task.execute();
		} else
			image.setImageBitmap(post.user.image.bitmap);
		if (position > lastPosition) {
			view.startAnimation(AnimationUtils
					.loadAnimation(context, R.anim.up));
			lastPosition = position;
		}
		((TextView) view.findViewById(R.id.username)).setText(post.user.name);
		((TextView) view.findViewById(R.id.date)).setText(post.date);
		TextView captionView = (TextView) view.findViewById(R.id.caption);
		if (post.caption == null)
			((ViewGroup) captionView.getParent()).removeView(captionView);
		else
			captionView.setText(post.caption);

		if (post.hasLiked)
			((ImageView) view.findViewById(R.id.like))
					.setImageDrawable(Accounts.context.getResources()
							.getDrawable(R.drawable.liked));
		return view;
	}
}