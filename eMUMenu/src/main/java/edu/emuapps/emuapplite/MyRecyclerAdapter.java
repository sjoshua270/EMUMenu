package edu.emuapps.emuapplite;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
	Context context;
	List<Card> cards;

	public static class ViewHolder extends RecyclerView.ViewHolder {
		public TextView title;
		public TextView subTitle;
		public View divider;
		public NonScrollListView content;

		public ViewHolder(View v) {
			super(v);
			title = (TextView) v.findViewById(R.id.title);
			subTitle = (TextView) v.findViewById(R.id.subtitle);
			divider = v.findViewById(R.id.divider);
			content = (NonScrollListView) v.findViewById(R.id.content);
		}
	}

	public MyRecyclerAdapter(Context context, List<Card> cards) {
		this.context = context;
		this.cards = cards;
	}

	@Override
	public MyRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
		View cardView = LayoutInflater.from(parent.getContext())
		                              .inflate(R.layout.card, parent, false);

		return new ViewHolder(cardView);
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		Card card = cards.get(position);
		viewHolder.title.setText(card.getTitle());
		if (!card.getSubTitle().equals("")) viewHolder.subTitle.setText(card.getSubTitle());
		else viewHolder.subTitle.setVisibility(View.GONE);
		viewHolder.divider.setBackgroundColor(context.getResources().getColor(R.color.light_blue_100));
		viewHolder.content.setAdapter(new CardAdapter(context, card.getData()));
	}

	@Override
	public int getItemCount() {
		return cards.size();
	}
}
