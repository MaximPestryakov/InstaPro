package by.pestryakov.instapro;

import android.graphics.Bitmap;

class Post {
	Image image;
	User user;
	boolean hasLiked;
	int likes;
	String date;
	String caption;

	public Post(String imageId, String imageUrl, Bitmap image, String userId,
			String userName, String fullName, String avatarUrl, Bitmap avatar,
			boolean hasLiked, int likes, String date, String caption) {
		this.image = new Image(imageId, imageUrl, image);
		this.user = new User(userId, userName, fullName, new Image(userId,
				avatarUrl, avatar));
		this.hasLiked = hasLiked;
		this.likes = likes;
		this.date = date;
		this.caption = caption;
	}
}

class User {
	String id;
	String name;
	String fullName;
	Image image;

	public User(String id, String name, String fullName, Image image) {
		this.id = id;
		this.name = name;
		this.fullName = fullName;
		this.image = image;
	}
}

class Image {
	String id;
	String url;
	Bitmap bitmap;

	public Image(String id, String url, Bitmap bitmap) {
		this.id = id;
		this.url = url;
		this.bitmap = bitmap;
	}
}

class Account {

	User user;
	String token;

	public Account(String userId, String userName, String fullName,
			String avatarUrl, Bitmap avatar, String token) {
		this.user = new User(userId, userName, fullName, new Image(userId,
				avatarUrl, avatar));
		this.token = token;
	}
}