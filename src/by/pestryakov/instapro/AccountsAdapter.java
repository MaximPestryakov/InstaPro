package by.pestryakov.instapro;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AccountsAdapter extends BaseAdapter {

	Context context;
	ArrayList<Account> list;
	LayoutInflater inflater;

	public AccountsAdapter(Context context, ArrayList<Account> list) {
		this.context = context;
		this.list = list;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Account getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = inflater.inflate(R.layout.accounts_item, parent, false);

		Account account = getItem(position);

		if (account.user.image.bitmap == null)
			new Request("users/self?access_token=", account.token,
					view.findViewById(R.id.avatar), account.user.id).execute();
		else
			((ImageView) view.findViewById(R.id.avatar))
					.setImageBitmap(account.user.image.bitmap);
		((TextView) view.findViewById(R.id.username))
				.setText(account.user.name);
		((TextView) view.findViewById(R.id.full_name))
				.setText(account.user.fullName);
		((TextView) view.findViewById(R.id.token)).setText(account.token);
		return view;
	}
}