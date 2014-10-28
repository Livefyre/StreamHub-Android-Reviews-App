package livefyre.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.livefyre.R;

import java.util.List;

import livefyre.ImagesCache.DownloadImageTask;
import livefyre.ImagesCache.ImagesCache;
import livefyre.LFSAppConstants;
import livefyre.LFUtils;
import livefyre.models.ContentBean;
import livefyre.models.Vote;


@SuppressLint("SimpleDateFormat")
public class ReviewListAdapter extends BaseAdapter {

	private List<ContentBean> ContentCollection;
	private LayoutInflater inflater;
	Context context;
	ImagesCache cache = ImagesCache.getInstance();

	public ReviewListAdapter(Context context,
			List<ContentBean> ContentCollection) {
		this.ContentCollection = ContentCollection;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
	}

	public void updateContentResult(List<ContentBean> ContentCollection) {
		this.ContentCollection = ContentCollection;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return ContentCollection.size();
	}

	@Override
	public Object getItem(int position) {
		return ContentCollection.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private class ViewHolder {
		ImageView reviewerImage;
		RatingBar reviewRatingBar;
		TextView reviewerid;
		TextView isMod;
		TextView reviewedDate;
		TextView reviewTitle;
		TextView reviewBody;
		TextView helpful;
		TextView replies;
		ImageView reviewImage;
		LinearLayout reviewCount;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			// convertView = getConvertView(getItemViewType(position));
			convertView = getConvertView();

		ViewHolder holder = (ViewHolder) convertView.getTag();
		updateView(position, holder);

		convertView.setId(position);

		return convertView;
	}

	@SuppressLint("InflateParams")
	private View getConvertView() {
		ViewHolder holder = new ViewHolder();
		View view = null;

		view = inflater.inflate(R.layout.review_cell, null);
		holder.reviewCount = (LinearLayout) view.findViewById(R.id.reviewCount);
		holder.reviewerid = (TextView) view.findViewById(R.id.reviewerid);
		holder.isMod = (TextView) view.findViewById(R.id.isMod);
		holder.reviewedDate = (TextView) view.findViewById(R.id.reviewedDate);
		holder.reviewTitle = (TextView) view.findViewById(R.id.reviewTitle);

		holder.reviewBody = (TextView) view.findViewById(R.id.reviewBody);

		holder.reviewerImage = (ImageView) view
				.findViewById(R.id.reviewerImage);

		holder.reviewImage = (ImageView) view.findViewById(R.id.reviewImage);

		holder.reviewRatingBar = (RatingBar) view
				.findViewById(R.id.reviewRatingBar);

		holder.helpful = (TextView) view.findViewById(R.id.helpful);
		holder.replies = (TextView) view.findViewById(R.id.replies);
		view.setTag(holder);
		return view;
	}

	private void updateView(int position, final ViewHolder holder) {

		try {
			final ContentBean content = (ContentBean) ContentCollection
					.get(position);
			holder.reviewerid.setText(content.getAuthor().getDisplayName());
			holder.reviewCount.setVisibility(View.VISIBLE);

			if (content.getVote() != null) {

				if (content.getVisibilityCount() == 0
						&& content.getVote().size() == 0) {

					holder.reviewCount.setVisibility(View.GONE);

				}
			} else if (content.getVisibilityCount() == 0) {
				holder.reviewCount.setVisibility(View.GONE);

			}

			if (content.getIsModerator() != null) {
				if (content.getIsModerator().equals("true")) {
					holder.isMod.setText("Moderator");
					holder.isMod.setVisibility(View.VISIBLE);
					holder.isMod.setTextColor(Color.parseColor("#0F98EC"));
					holder.reviewerid.setMaxWidth(330);
				} else {
					holder.isMod.setVisibility(View.GONE);
				}
			} else {
				holder.isMod.setVisibility(View.GONE);
			}

			if (content.getIsFeatured() != null) {
				if (content.getIsFeatured()) {
					holder.isMod.setText("Feature");
					holder.isMod.setTextColor(Color.parseColor("#FEB33B"));
					holder.isMod.setVisibility(View.VISIBLE);
					holder.reviewerid.setMaxWidth(330);

				}
			}

			holder.reviewedDate.setText(LFUtils.getFormatedDate(
                    content.getCreatedAt(), LFSAppConstants.SHART));
			holder.reviewTitle.setText(content.getTitle());

			holder.reviewBody.setText(LFUtils.trimTrailingWhitespace(Html
					.fromHtml(content.getBodyHtml())),
					TextView.BufferType.SPANNABLE);
			holder.reviewRatingBar.setRating(Float.parseFloat(content
					.getRating()) / 20);

			if (content.getAuthor().getAvatar().length() > 0) {
				Bitmap bm = cache.getImageFromWarehouse(content.getAuthor()
						.getAvatar());

				if (bm != null) {
					holder.reviewerImage.setImageBitmap(bm);
				} else {
					holder.reviewerImage.setImageBitmap(null);
					DownloadImageTask imgTask = new DownloadImageTask(this);
					imgTask.execute(content.getAuthor().getAvatar());

				}
			} else {
				holder.reviewerImage
						.setImageResource(R.drawable.profile_default);
			}
			DownloadImageTask.getRoundedShape(holder.reviewerImage);

			if (content.getOembedUrl() != null) {
				if (content.getOembedUrl().length() > 0) {

					holder.reviewImage.setVisibility(View.VISIBLE);

					Bitmap bitmap = cache.getImageFromWarehouse(content
							.getOembedUrl());

					if (bitmap != null) {
						holder.reviewImage.setImageBitmap(bitmap);
					} else {
						holder.reviewImage.setImageBitmap(null);
						DownloadImageTask imgTask = new DownloadImageTask(this);
						// `Adapter` call first Constructor.
						imgTask.execute(content.getOembedUrl());
					}

				} else {
					holder.reviewImage.setVisibility(View.GONE);
				}
			} else {
				holder.reviewImage.setVisibility(View.GONE);
			}

			if (content.getVote() != null) {
				if (content.getVote().size() > 0)
					holder.helpful.setText(foundHelpfull(content.getVote()));
				else
					holder.helpful.setVisibility(View.GONE);
			} else
				holder.helpful.setVisibility(View.GONE);

			if (content.getVisibilityCount() > 0) {
				holder.replies.setVisibility(View.VISIBLE);
				if (content.getVisibilityCount() == 1)
					holder.replies.setText(content.getVisibilityCount()
							+ " Reply");
				else
					holder.replies.setText(content.getVisibilityCount()
							+ " Replies");
			} else
				holder.replies.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	String foundHelpfull(List<Vote> v) {
		int count = 0;
		for (int i = 0; i < v.size(); i++) {
			if (v.get(i).getValue().equals("1"))
				count++;
		}
		return count + " of " + v.size() + " found helpful";
	}

}
