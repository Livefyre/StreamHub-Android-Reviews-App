package livefyre.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.livefyre.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;

import livefyre.AppSingleton;
import livefyre.ImagesCache.DownloadImageTask;
import livefyre.ImagesCache.ImagesCache;
import livefyre.LFSAppConstants;
import livefyre.LFSConfig;
import livefyre.LFUtils;
import livefyre.LivefyreApplication;
import livefyre.activities.Edit;
import livefyre.activities.Reply;
import livefyre.models.ContentBean;
import livefyre.models.Vote;
import livefyre.parsers.ContentParser;
import livefyre.streamhub.LFSActions;
import livefyre.streamhub.LFSConstants;
import livefyre.streamhub.LFSFlag;
import livefyre.streamhub.WriteClient;

public class ReviewInDetailAdapter extends BaseAdapter {
	private static final int VIEW_COUNT = 3;
	private static final int DELETED = -1;
	private static final int PARENT = 0;
	private static final int CHILD = 1;
	ImagesCache cache = ImagesCache.getInstance();
	OnClickListener notificationHandler;

	private ProgressDialog dialog;
	String mainReviewId;
	int helpfulFlag = 0;
	private LayoutInflater inflater;
	Context context;
	Activity activity;
	private LivefyreApplication application;

	private List<ContentBean> detailContent;

	public ReviewInDetailAdapter(Activity activity, Context context,
			List<ContentBean> detailContent,
			OnClickListener notificationHandler, String mainReviewId) {
		this.mainReviewId = mainReviewId;
		this.notificationHandler = notificationHandler;
		Log.d("Listener", "" + notificationHandler);
		this.detailContent = detailContent;
		this.inflater = LayoutInflater.from(context);
		this.context = context;
		this.activity = activity;
		application = AppSingleton.getInstance().getApplication();

	}

	public void updateReviewInDetailAdapter(List<ContentBean> ContentCollection) {
		this.detailContent = ContentCollection;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return detailContent.size();
	}

	@Override
	public Object getItem(int position) {
		return detailContent.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		return detailContent.get(position).getContentType().getValue();
	}

	@Override
	public int getViewTypeCount() {
		return VIEW_COUNT;
	}

	private class ViewHolder {
		// main
		Button backtoReviewActivityFromInDetailView;
		TextView titleForReviewInDetail;
		ImageView reviewImageInDetail;
		RatingBar ratingBarInDetailView;
		ImageView mainReviewerImage;
		TextView mainReviewerDisplayName;
		TextView mainReviewDate;
		TextView mainReviewBody;
		TextView isParenrMod;
		TextView parentReplyTv;
		TextView parentHelpfulTv;

		ImageView parentMoreOptions;

		ImageView parentHelpfulImg;

		ImageView parentsHelpfulImg, parentReplyImg;

		TextView detailReplyNotificationTV;// notif Tv

		Button parentNotifBtn;// Notif B

		RelativeLayout parentNotifRV;

		// child
		RelativeLayout childMain;
		ImageView childReviewerImage;
		TextView childReviewerDisplayName;
		TextView childReviewedDate;
		TextView childReviewHelpful;
		TextView childReviewBody;
		TextView isChildMod;
		ImageView childHelpfulImg;
		ImageView childReplyImg;

		ImageView childMoreOptions;

		// deleted
		LinearLayout deletedCell;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null)
			convertView = getConvertView(getItemViewType(position));

		ViewHolder holder = (ViewHolder) convertView.getTag();
		updateView(position, holder);

		convertView.setId(position);

		return convertView;
	}

	private View getConvertView(int viewType) {
		ViewHolder holder = new ViewHolder();
		View view = null;

		switch (viewType) {
		case PARENT:

			view = inflater.inflate(R.layout.parent, null);

			holder.parentNotifBtn = (Button) view
					.findViewById(R.id.parentNotifBtn);

			holder.titleForReviewInDetail = (TextView) view
					.findViewById(R.id.titleForReviewInDetail);

			holder.ratingBarInDetailView = (RatingBar) view
					.findViewById(R.id.ratingBarInDetailView);

			holder.mainReviewerImage = (ImageView) view
					.findViewById(R.id.mainReviewerImage);
			holder.mainReviewerDisplayName = (TextView) view
					.findViewById(R.id.mainReviewerDisplayName);
			holder.mainReviewDate = (TextView) view
					.findViewById(R.id.mainReviewDate);
			holder.mainReviewBody = (TextView) view
					.findViewById(R.id.mainReviewBody);
			holder.isParenrMod = (TextView) view.findViewById(R.id.isParenrMod);

			holder.parentHelpfulImg = (ImageView) view
					.findViewById(R.id.parentsHelpfulImg);

			holder.parentHelpfulTv = (TextView) view
					.findViewById(R.id.parentsHelpfulTv);

			holder.parentReplyTv = (TextView) view
					.findViewById(R.id.parentReplyTv);

			holder.parentMoreOptions = (ImageView) view
					.findViewById(R.id.parentMoreOptions);

			holder.detailReplyNotificationTV = (TextView) view
					.findViewById(R.id.detailReplyNotificationTV);

			holder.parentsHelpfulImg = (ImageView) view
					.findViewById(R.id.parentsHelpfulImg);
			holder.parentReplyImg = (ImageView) view
					.findViewById(R.id.parentReplyImg);
			holder.parentNotifRV = (RelativeLayout) view
					.findViewById(R.id.parentNotifRV);
			break;
		case CHILD:
			view = inflater.inflate(R.layout.child, null);
			holder.childMain = (RelativeLayout) view
					.findViewById(R.id.childMain);
			holder.childReviewerImage = (ImageView) view
					.findViewById(R.id.childReviewerImage);
			holder.childReviewerDisplayName = (TextView) view
					.findViewById(R.id.childReviewerDisplayName);
			holder.childReviewedDate = (TextView) view
					.findViewById(R.id.childReviewedDate);
			holder.childReviewHelpful = (TextView) view
					.findViewById(R.id.childReviewHelpful);
			holder.childReviewBody = (TextView) view
					.findViewById(R.id.childReviewBody);
			holder.isChildMod = (TextView) view.findViewById(R.id.isChildMod);

			holder.childHelpfulImg = (ImageView) view
					.findViewById(R.id.childHelpfulImg);

			holder.childReplyImg = (ImageView) view
					.findViewById(R.id.childReplyImg);

			holder.childMoreOptions = (ImageView) view
					.findViewById(R.id.childMoreOptions);

			break;
		case DELETED:
			view = inflater.inflate(R.layout.deleted_review_cell, null);
			holder.deletedCell = (LinearLayout) view
					.findViewById(R.id.deletedCell);

			break;

		}
		view.setTag(holder);
		return view;
	}

	@SuppressLint("ResourceAsColor")
	private void updateView(int position, final ViewHolder holder) {

		int viewType = detailContent.get(position).getContentType().getValue();
		final ContentBean content = (ContentBean) detailContent.get(position);
		switch (viewType) {
		case PARENT:

			if (content.getNewReplyCount() > 0) {
				holder.parentNotifRV.setVisibility(View.VISIBLE);
				if (content.getNewReplyCount() != 1)
					holder.detailReplyNotificationTV.setText(content
							.getNewReplyCount() + " New Replies");
				else
					holder.detailReplyNotificationTV.setText(content
							.getNewReplyCount() + " New Reply");
			} else {
				holder.parentNotifRV.setVisibility(View.GONE);
			}

			if (content.getIsModerator() != null) {
				if (content.getIsModerator().equals("true")) {
					holder.isParenrMod.setText("Moderator");
					holder.isParenrMod
							.setTextColor(Color.parseColor("#0F98EC"));
					holder.isParenrMod.setVisibility(View.VISIBLE);
					holder.mainReviewerDisplayName.setMaxWidth(330);
				} else {
					holder.isParenrMod.setVisibility(View.GONE);
				}
			} else {
				holder.isParenrMod.setVisibility(View.GONE);
			}

			if (content.getIsFeatured() != null) {
				if (content.getIsFeatured()) {
					holder.isParenrMod.setText("Feature");
					holder.isParenrMod
							.setTextColor(Color.parseColor("#FEB33B"));
					holder.isParenrMod.setVisibility(View.VISIBLE);
					holder.mainReviewerDisplayName.setMaxWidth(380);

				}
			}

			if (content.getVote() != null) {// know helpful value and set color
				if (content.getVote().size() > 0) {
					helpfulFlag = 0;
					helpfulFlag = knowHelpfulValue(
							application
									.getDataFromSharedPreferences(LFSAppConstants.ID),
							content.getVote());

					if (helpfulFlag == 1) {
						holder.parentHelpfulImg
								.setImageResource(R.drawable.helpful);
						holder.parentHelpfulTv.setTextColor(Color
								.parseColor("#ff0000"));
						holder.parentHelpfulTv.setText("HELPFUL");
					} else if (helpfulFlag == 2) {
						holder.parentHelpfulImg
								.setImageResource(R.drawable.unhelpful);
						holder.parentHelpfulTv.setTextColor(Color
								.parseColor("#848484"));
						holder.parentHelpfulTv.setText("UNHELPFUL");
					} else {
						holder.parentHelpfulImg
								.setImageResource(R.drawable.help_initial);
						holder.parentHelpfulTv.setTextColor(Color
								.parseColor("#0F98EC"));
						holder.parentHelpfulTv.setText("HELPFUL?");
					}
				} else {
					holder.parentHelpfulImg
							.setImageResource(R.drawable.help_initial);
					holder.parentHelpfulTv.setTextColor(Color
							.parseColor("#0F98EC"));
				}

			} else {
				holder.parentHelpfulImg
						.setImageResource(R.drawable.help_initial);
				holder.parentHelpfulTv
						.setTextColor(Color.parseColor("#0F98EC"));
			}

			holder.titleForReviewInDetail.setText(content.getTitle());

			holder.ratingBarInDetailView.setRating(Float.parseFloat(content
					.getRating()) / 20);


			Bitmap bitmapP = cache.getImageFromWarehouse(content.getAuthor()
					.getAvatar());

			if (bitmapP != null) {
				holder.mainReviewerImage.setImageBitmap(bitmapP);
			} else {
				holder.mainReviewerImage.setImageBitmap(null);
				DownloadImageTask imgTask = new DownloadImageTask(this);
				imgTask.execute(content.getAuthor().getAvatar());

			}
			DownloadImageTask.getRoundedShape(holder.mainReviewerImage);

			holder.mainReviewerDisplayName.setText(content.getAuthor()
					.getDisplayName());

			holder.mainReviewDate.setText(LFUtils.getFormatedDate(
                    content.getCreatedAt(), LFSAppConstants.DETAIL));

			holder.mainReviewBody.setText(LFUtils.trimTrailingWhitespace(Html
					.fromHtml(content.getBodyHtml())),
					TextView.BufferType.SPANNABLE);

			holder.parentNotifBtn.setOnClickListener(notificationHandler);

		
			holder.parentReplyImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent replyView = new Intent(context, Reply.class);
					replyView.putExtra("id", content.getId());
					replyView.putExtra("isEdit", false);
					activity.startActivityForResult(replyView, 1);
					activity.overridePendingTransition(R.anim.right_in,
							R.anim.left_out);
				}
			});
			holder.parentReplyTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent replyView = new Intent(activity, Reply.class);
					replyView.putExtra("id", content.getId());
					replyView.putExtra("isEdit", false);
					activity.startActivityForResult(replyView, 1);
					activity.overridePendingTransition(R.anim.right_in,
							R.anim.left_out);
				}
			});

			//
			holder.parentsHelpfulImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					helpfulDialog(
							knowHelpfulValue(
									application
											.getDataFromSharedPreferences(LFSAppConstants.ID),
									content.getVote()), content.getId());
				}
			});
			holder.parentHelpfulTv.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					helpfulDialog(
							knowHelpfulValue(
									application
											.getDataFromSharedPreferences(LFSAppConstants.ID),
									content.getVote()), content.getId());
				}
			});
			holder.parentMoreOptions.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (content.getIsFeatured() != null) {
						if (content.getIsFeatured()) {
							moreDialog(content.getId(), true, true);
						} else {
							moreDialog(content.getId(), false, true);
						}
					} else {
						moreDialog(content.getId(), false, true);
					}

				}
			});

			break;
		case CHILD:

			float density = context.getResources().getDisplayMetrics().density;
			int px = (int) (40 * density);

			switch (content.getDepth()) {
			case 1:
				holder.childMain.setPadding(px * 0, 16, 16, 16);
				break;
			case 2:
				holder.childMain.setPadding(px * 1, 16, 16, 16);
				break;
			case 3:
				holder.childMain.setPadding(px * 2, 16, 16, 16);
				break;
			default:
				holder.childMain.setPadding(px * 3, 16, 16, 16);
				break;

			}

			if (content.getIsModerator() != null) {
				if (content.getIsModerator().equals("true")) {
					holder.isChildMod.setText("Moderator");
					holder.isChildMod.setVisibility(View.VISIBLE);
					holder.isChildMod.setTextColor(Color.parseColor("#0F98EC"));
					holder.childReviewerDisplayName.setMaxWidth(330);
				} else {
					holder.isChildMod.setVisibility(View.GONE);
				}
			} else {
				holder.isChildMod.setVisibility(View.GONE);
			}

			if (content.getIsFeatured() != null) {
				if (content.getIsFeatured()) {
					holder.isChildMod.setText("Feature");

					holder.isChildMod.setTextColor(Color.parseColor("#FEB33B"));
					holder.isChildMod.setVisibility(View.VISIBLE);
					holder.childReviewerDisplayName.setMaxWidth(380);

				}
			}
			holder.childReviewerDisplayName.setText(content.getAuthor()
					.getDisplayName());
			Bitmap bitmapC = cache.getImageFromWarehouse(content.getAuthor()
					.getAvatar());

			if (bitmapC != null) {
				holder.childReviewerImage.setImageBitmap(bitmapC);
			} else {
				holder.childReviewerImage.setImageBitmap(null);
				DownloadImageTask imgTask = new DownloadImageTask(this);
				imgTask.execute(content.getAuthor().getAvatar());

			}

			DownloadImageTask.getRoundedShape(holder.childReviewerImage);
			holder.childReviewedDate.setText(LFUtils.getFormatedDate(
					content.getCreatedAt(), LFSAppConstants.SHART));

			if (content.getVote() != null) {
				if (content.getVote().size() > 0) {
					holder.childReviewHelpful.setText(foundHelpfull(content
							.getVote()));

				} else
					holder.childReviewHelpful.setVisibility(View.GONE);
			} else
				holder.childReviewHelpful.setVisibility(View.GONE);

			if (content.getVote() != null) {// know helpful value and set color
				if (content.getVote().size() > 0) {
					helpfulFlag = knowHelpfulValue(
							application
									.getDataFromSharedPreferences(LFSAppConstants.ID),
							content.getVote());

					if (helpfulFlag == 1)
						holder.childHelpfulImg
								.setImageResource(R.drawable.helpful);
					else if (helpfulFlag == 2)
						holder.childHelpfulImg
								.setImageResource(R.drawable.unhelpful);
					else
						holder.childHelpfulImg
								.setImageResource(R.drawable.help_initial);
				} else {
					holder.childHelpfulImg
							.setImageResource(R.drawable.help_initial);
				}

			} else {
				holder.childHelpfulImg
						.setImageResource(R.drawable.help_initial);
			}

			holder.childReviewBody.setText(LFUtils.trimTrailingWhitespace(Html
					.fromHtml(content.getBodyHtml())),
					TextView.BufferType.SPANNABLE);
			holder.childReplyImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent replyView = new Intent(activity, Reply.class);
					replyView.putExtra("id", content.getId());
					replyView.putExtra("isEdit", false);
					activity.startActivityForResult(replyView, 1);
					activity.overridePendingTransition(R.anim.right_in,
							R.anim.left_out);
				}
			});

			holder.childHelpfulImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					helpfulDialog(
							knowHelpfulValue(
									application
											.getDataFromSharedPreferences(LFSAppConstants.ID),
									content.getVote()), content.getId());
				}
			});
			holder.childMoreOptions.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (content.getIsFeatured() != null) {
						if (content.getIsFeatured()) {
							moreDialog(content.getId(), true, false);
						} else {
							moreDialog(content.getId(), false, false);
						}
					} else {
						moreDialog(content.getId(), false, false);
					}
				}
			});
			break;
		case DELETED:
			if (content.getId().equals(mainReviewId)) {
				showToast("This review is no longer visible.");
				activity.finish();
			}

			float densityDtl = context.getResources().getDisplayMetrics().density;
			int pxDtl = (int) (40 * densityDtl);

			switch (content.getDepth()) {
			case 1:
				holder.deletedCell.setPadding(pxDtl * 0, 16, 16, 16);
				break;
			case 2:
				holder.deletedCell.setPadding(pxDtl * 1, 16, 16, 16);
				break;
			case 3:
				holder.deletedCell.setPadding(pxDtl * 2, 16, 16, 16);
				break;
			default:
				holder.deletedCell.setPadding(pxDtl * 3, 16, 16, 16);
				break;

			}

			break;
		}
	}

	String foundHelpfull(List<Vote> v) {
		int count = 0;
		for (int i = 0; i < v.size(); i++) {

			if (v.get(i).getValue().equals("1"))// for Count
				count++;

		}
		return count + " of " + v.size() + " found helpful";
	}

	int knowHelpfulValue(String authorId, List<Vote> v) {

		int helpfulValue = 0;
		if (v != null)
			for (int i = 0; i < v.size(); i++) {
				if (v.get(i).getAuthor().equals(authorId)) { // helpful or not
																// helpful

					if (v.get(i).getValue().equals("1"))
						helpfulValue = 1;
					else
						helpfulValue = 2;
					break;
				}
			}

		return helpfulValue;
	}

	private void helpfulDialog(final int HFVal, final String id) {
		final Dialog dialog = new Dialog(activity,
				android.R.style.Theme_Translucent_NoTitleBar);

		dialog.setTitle("");
		dialog.setContentView(R.layout.helpfull_dialog);
		dialog.setCancelable(true);

		LinearLayout emptyDialogSpace = (LinearLayout) dialog
				.findViewById(R.id.emptyDialogSpace);
		emptyDialogSpace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				dialog.dismiss();

			}
		});

		LinearLayout helpful = (LinearLayout) dialog.findViewById(R.id.helpful);
		helpful.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				if (HFVal == 1) {
					RequestParams perameters = new RequestParams();
					perameters.put("value", "0");
					perameters.put(LFSConstants.LFSPostUserTokenKey,
							LFSConfig.USER_TOKEN);
					perameters.put("message_id", id);

					WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
							LFSConfig.USER_TOKEN, LFSActions.VOTE, perameters,
							new helpfulCallback());
					dialog.dismiss();
				} else {
					RequestParams perameters = new RequestParams();
					perameters.put("value", "1");
					perameters.put(LFSConstants.LFSPostUserTokenKey,
							LFSConfig.USER_TOKEN);
					perameters.put("message_id", id);

					WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
							LFSConfig.USER_TOKEN, LFSActions.VOTE, perameters,
							new helpfulCallback());
					dialog.dismiss();

				}

			}
		});

		LinearLayout notHelpful = (LinearLayout) dialog
				.findViewById(R.id.notHelpful);
		notHelpful.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				if (HFVal == 2) {
					RequestParams perameters = new RequestParams();
					perameters.put("value", "0");
					perameters.put(LFSConstants.LFSPostUserTokenKey,
							LFSConfig.USER_TOKEN);
					perameters.put("message_id", id);

					WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
							LFSConfig.USER_TOKEN, LFSActions.VOTE, perameters,
							new helpfulCallback());
					dialog.dismiss();
				} else {
					RequestParams perameters = new RequestParams();
					perameters.put("value", "2");
					perameters.put(LFSConstants.LFSPostUserTokenKey,
							LFSConfig.USER_TOKEN);
					perameters.put("message_id", id);

					WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
							LFSConfig.USER_TOKEN, LFSActions.VOTE, perameters,
							new helpfulCallback());
					dialog.dismiss();

				}

			}
		});
		dialog.show();

	}

	private void moreDialog(final String id, final Boolean isFeatured,
			final Boolean isChild) {
		ContentBean mBean = ContentParser.ContentCollection.get(id);

		final Dialog dialog = new Dialog(activity,
				android.R.style.Theme_Translucent_NoTitleBar);
		dialog.setTitle("Hari");
		dialog.setContentView(R.layout.more);
		dialog.setCancelable(true);
		if (isFeatured) {
			((TextView) dialog.findViewById(R.id.alreadyFeatured))
					.setText("Unfeature");
		} else {
			((TextView) dialog.findViewById(R.id.alreadyFeatured))
					.setText("Feature");
		}
		LinearLayout emptyDialogSpace = (LinearLayout) dialog
				.findViewById(R.id.emptyDialogSpace);
		emptyDialogSpace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});

		LinearLayout edit = (LinearLayout) dialog.findViewById(R.id.edit);
		LinearLayout feature = (LinearLayout) dialog.findViewById(R.id.feature);
		LinearLayout flag = (LinearLayout) dialog.findViewById(R.id.flag);
		LinearLayout bozo = (LinearLayout) dialog.findViewById(R.id.bozo);
		LinearLayout banUser = (LinearLayout) dialog.findViewById(R.id.banUser);
		LinearLayout delete = (LinearLayout) dialog.findViewById(R.id.delete);

		View moreLine = dialog.findViewById(R.id.moreLine);
		if ("yes".equals(application
				.getDataFromSharedPreferences(LFSAppConstants.ISMOD))
				&& mBean.getIsModerator().equals("true")) {
			edit.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);
			feature.setVisibility(View.VISIBLE);
			moreLine.setVisibility(View.VISIBLE);

		} else if ("yes".equals(application
				.getDataFromSharedPreferences(LFSAppConstants.ISMOD))) {
			edit.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);
			moreLine.setVisibility(View.VISIBLE);
			feature.setVisibility(View.VISIBLE);
			flag.setVisibility(View.VISIBLE);
			bozo.setVisibility(View.VISIBLE);
			banUser.setVisibility(View.VISIBLE);
			moreLine.setVisibility(View.VISIBLE);

		} else if (mBean.getAuthorId().equals(
				application.getDataFromSharedPreferences(LFSAppConstants.ID))) {
			edit.setVisibility(View.VISIBLE);
			delete.setVisibility(View.VISIBLE);
		} else {
			flag.setVisibility(View.VISIBLE);
		}
		if (mBean.getIsFeatured()) {
			flag.setVisibility(View.GONE);
		}

		edit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isChild) {
					Intent editView = new Intent(context, Edit.class);
					editView.putExtra("id", id);
					editView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(editView);
					activity.overridePendingTransition(R.anim.right_in,
							R.anim.left_out);
				} else {
					Intent replyView = new Intent(context, Reply.class);
					replyView.putExtra("id", id);
					replyView.putExtra("isEdit", true);
					replyView.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(replyView);
					activity.overridePendingTransition(R.anim.right_in,
							R.anim.left_out);
				}

				dialog.dismiss();

			}
		});

		feature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				if (isFeatured) {
					try {
						WriteClient.featureMessage("unfeature", id,
								LFSConfig.COLLECTION_ID, LFSConfig.USER_TOKEN,
								null, new helpfulCallback());// same as helpful
																// call back
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}

				} else {

					try {
						WriteClient.featureMessage("feature", id,
								LFSConfig.COLLECTION_ID, LFSConfig.USER_TOKEN,
								null, new helpfulCallback());// same as helpful
																// call back
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}

				dialog.dismiss();

			}
		});

		flag.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				flagDialog(id);
				dialog.dismiss();
			}
		});

		bozo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				RequestParams perameters = new RequestParams();
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("message_id", id);

				WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
						LFSConfig.USER_TOKEN, LFSActions.BOZO, perameters,
						new actionCallback());
				dialog.dismiss();
			}
		});

		banUser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				RequestParams perameters = new RequestParams();
				perameters.put("network", LFSConfig.NETWORK_ID);
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("retroactive", "0");
				WriteClient.flagAuthor(ContentParser.ContentCollection.get(id)
						.getAuthorId(), LFSConfig.USER_TOKEN, perameters,
						new actionCallback());

				dialog.dismiss();

			}
		});

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showProgress();
				RequestParams perameters = new RequestParams();
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("message_id", id);

				WriteClient.postAction(LFSConfig.COLLECTION_ID, id,
						LFSConfig.USER_TOKEN, LFSActions.DELETE, perameters,
						new actionCallback());

				dialog.dismiss();

			}
		});
		if (edit.getVisibility() == View.GONE
				&& feature.getVisibility() == View.GONE
				&& delete.getVisibility() == View.GONE
				&& banUser.getVisibility() == View.GONE
				&& bozo.getVisibility() == View.GONE
				&& flag.getVisibility() == View.GONE) {

		} else {
			dialog.show();
		}
	}

	private void flagDialog(final String id) {
		final Dialog dialog = new Dialog(activity,
				android.R.style.Theme_Translucent_NoTitleBar);

		dialog.setTitle("");
		dialog.setContentView(R.layout.flag);
		dialog.setCancelable(true);
		LinearLayout emptyDialogSpace = (LinearLayout) dialog
				.findViewById(R.id.emptyDialogSpace);
		emptyDialogSpace.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();

			}
		});
		TextView spam = (TextView) dialog.findViewById(R.id.spam);
		spam.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RequestParams perameters = new RequestParams();
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("message_id", id);

				WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
						LFSConfig.USER_TOKEN, LFSFlag.SPAM, perameters,
						new flagCallback());
				dialog.dismiss();

			}
		});

		TextView offensive = (TextView) dialog.findViewById(R.id.offensive);
		offensive.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				RequestParams perameters = new RequestParams();
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("message_id", id);

				WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
						LFSConfig.USER_TOKEN, LFSFlag.OFFENSIVE, perameters,
						new flagCallback());
				dialog.dismiss();

			}
		});

		TextView offtopic = (TextView) dialog.findViewById(R.id.offtopic);
		offtopic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				RequestParams perameters = new RequestParams();
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("message_id", id);

				WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
						LFSConfig.USER_TOKEN, LFSFlag.OFF_TOPIC, perameters,
						new flagCallback());
				dialog.dismiss();

			}
		});

		TextView disagree = (TextView) dialog.findViewById(R.id.disagree);
		disagree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				RequestParams perameters = new RequestParams();
				perameters.put(LFSConstants.LFSPostUserTokenKey,
						LFSConfig.USER_TOKEN);
				perameters.put("message_id", id);

				WriteClient.flagContent(LFSConfig.COLLECTION_ID, id,
						LFSConfig.USER_TOKEN, LFSFlag.DISAGREE, perameters,
						new flagCallback());
				dialog.dismiss();

			}
		});

		dialog.show();
	}

	private class actionCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject responce) {
			Log.d("action ClientCall", "success" + responce);
			if (!responce.isNull("data")) {
				try {
					JSONObject data = responce.getJSONObject("data");
					if (!data.isNull("messageId")) {
						if (data.getString("messageId").equals(mainReviewId)) {
							if (dialog.isShowing())
								dismissProgress();
						}
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			if (dialog.isShowing())
				dismissProgress();
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();
			Log.d("action ClientCall", error + "");
			showToast("Something went wrong.");
		}

	}

	private class helpfulCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject data) {
			dismissProgress();
		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();
			showToast("Something went wrong.");

		}

	}

	private class flagCallback extends JsonHttpResponseHandler {

		public void onSuccess(JSONObject data) {
			showToast("Content flagged successfully");

		}

		@Override
		public void onFailure(Throwable error, String content) {
			super.onFailure(error, content);
			dismissProgress();
			showToast("Something went wrong.");

		}

	}

	protected void showProgress() {
		dialog = new ProgressDialog(activity);
		dialog.setMessage("Please wait." + "\n"
				+ "Your request is being processed..");

		dialog.setCancelable(false);
		dialog.show();
	}

	protected void dismissProgress() {
		try {
			dialog.dismiss();
		} catch (Exception e) {

		}
	}

	public void showToast(String toastText) {
		Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
	}

	protected Boolean isNetworkAvailable() {
		ConnectivityManager cn = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true) {
			return true;
		} else {
			return false;
		}
	}

}
