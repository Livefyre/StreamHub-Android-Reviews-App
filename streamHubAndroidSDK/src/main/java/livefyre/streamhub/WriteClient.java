package livefyre.streamhub;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author kvanainc1
 * 
 */
public class WriteClient {

	static String[] actions = { "edit", // 0
			"approve", // 1
			"unapprove", // 2
			"hide", // 3
			"delete", // 4
			"bozo", // 5
			"ignore-flags", // 6
			"add-note", // 7
			"like", // 8
			"unlike", // 9
			"flag", // 10
			"mention", // 11
			"share", // 12
			"vote" // 13
	};
	static String[] flags = { "spam", // 0
			"offensive", // 1
			"disagree", // 2
			"off-topic" // 3
	};

	/**
	 * Post content to a Livefyre collection.
	 * 
	 * @param networkId
	 *            The collection's network as identified by domain, i.e.
	 *            livefyre.com.
	 * @param collectionId
	 *            The Id of the collection.
	 * @param parentId
	 *            The id of the content to which this content is a reply. If not
	 *            necessary (that is, this is a top level post, then set to
	 *            empty string ("").
	 * @param token
	 *            The token of the logged in user.
	 * @param body
	 *            A string version of the HTML body
	 * @param handler
	 *            Response handler
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 */
	public static void postContent(String networkId, String collectionId,
			String parentId, String userToken,
			HashMap<String, Object> perameters, JsonHttpResponseHandler handler)
			throws MalformedURLException {
		// add body parameters
		RequestParams bodyParams = new RequestParams();

		// if (perameters.containsKey(LFSConstants.LFSPostType)) {

		// if (perameters.get(LFSConstants.LFSPostType) ==
		// LFSConstants.LFSPostTypeRating) {

		bodyParams.put(LFSConstants.LFSPostBodyKey,
				(String) perameters.get(LFSConstants.LFSPostBodyKey));

		// //Get Title
		if (perameters.containsKey("attachments")) {
			bodyParams.put("attachments",
					(String) perameters.get("attachments"));
		}

		if (perameters.containsKey(LFSConstants.LFSPostTitleKey)) {
			bodyParams.put(LFSConstants.LFSPostTitleKey,
					(String) perameters.get(LFSConstants.LFSPostTitleKey));
		}

		// // Get Rating

		if (perameters.containsKey(LFSConstants.LFSPostTypeReview)) {
			JSONObject rating = new JSONObject();
			try {
				rating.put("default",
						perameters.get(LFSConstants.LFSPostTypeReview));
				// String rateJson=JSONObject.quote(rating.toString());
				bodyParams.put("rating", rating.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// // Get Parent id if available.

		if (parentId != null && parentId.length() != 0) {
			bodyParams.put("parent_id", parentId);
		}

		if (perameters.containsKey(LFSConstants.LFSPostUserTokenKey))
			bodyParams.put("lftoken",
					(String) perameters.get(LFSConstants.LFSPostUserTokenKey));
		else {

			android.os.Process.killProcess(android.os.Process.myPid());
		}

		Log.d("", "" + bodyParams);
		HttpClient.client.post(
				generateWriteURL(networkId, collectionId, userToken, perameters
						.get(LFSConstants.LFSPostType).toString()), bodyParams,
				handler);

	}

	/**
	 * 
	 * @param collectionId
	 * @param contentId
	 * @param token
	 * @param action
	 * @param perameters
	 * @param handler
	 */

	public static void flagContent(String collectionId, String contentId,
			String token, LFSFlag action, RequestParams perameters,
			JsonHttpResponseHandler handler) {
		// String url =
		// "http://quill.client-solutions.fyre.co/api/v3.0/message/"
		// + contentId
		// + "/flag/"
		// + flags[action.value()]
		// + "/"
		// + (new Uri.Builder().appendQueryParameter("lftoken", token)
		// .appendQueryParameter("collection_id", collectionId));

		 String url = (new Uri.Builder().scheme(Config.scheme)
				.authority(Config.quillDomain + "." + Config.networkId)
				.appendPath("api").appendPath("v3.0").appendPath("message").appendPath(""))+
				contentId+(new Uri.Builder().appendPath("").appendPath("flag")
				.appendPath(flags[action.value()])
				.appendQueryParameter("lftoken", token)
				.appendQueryParameter("collection_id", collectionId));

		Log.d("Action SDK call", "" + url);
		Log.d("Action SDK call", "" + perameters);
		HttpClient.client.post(url, perameters, handler);
	}

	/**
	 * 
	 * @param networkId
	 * @param collectionId
	 * @param userToken
	 * @param endpoint
	 * @return
	 * @throws MalformedURLException
	 */

	public static String generateWriteURL(String networkId,
			String collectionId, String userToken, String endpoint)
			throws MalformedURLException {
		final Builder uriBuilder = new Uri.Builder().scheme(Config.scheme)
				.authority(Config.quillDomain + "." + Config.networkId)
				.appendPath("api").appendPath("v3.0").appendPath("collection")
				.appendPath(collectionId).appendPath("post");
		if (LFSConstants.LFSPostTypeReview == endpoint)
			uriBuilder.appendPath(endpoint).appendPath("");
		else
			uriBuilder.appendPath("");
		Log.d("Write URL", "" + uriBuilder.toString());
		return uriBuilder.toString();
	}

	/**
	 * 
	 * @param action
	 * @param contentId
	 * @param collectionId
	 * @param userToken
	 * @param perameters
	 * @param handler
	 * @throws MalformedURLException
	 */

	public static void featureMessage(String action, String contentId,
			String collectionId, String userToken,
			HashMap<String, Object> perameters, JsonHttpResponseHandler handler)
			throws MalformedURLException {
		RequestParams bodyParams = new RequestParams();
		bodyParams.put("lftoken", userToken);

		// String url =
		// "http://quill.client-solutions.fyre.co/api/v3.0/collection/"
		// + collectionId
		// + "/"
		// + action
		// + "/"
		// + contentId
		// + "/"
		// + (new Uri.Builder().appendQueryParameter("lftoken", userToken));

		final Builder uriBuilder = new Uri.Builder().scheme(Config.scheme)
				.authority(Config.quillDomain + "." + Config.networkId)
				.appendPath("api").appendPath("v3.0").appendPath("collection")
				.appendPath(collectionId).appendPath(action)
				.appendPath(contentId).appendPath("")
				.appendQueryParameter("lftoken", userToken)
				.appendQueryParameter("collection_id", collectionId);

		Log.d("SDK", "" + uriBuilder);
		HttpClient.client.post(uriBuilder.toString(), bodyParams, handler);
	}

	/**
	 * 
	 * @param collectionId
	 * @param contentId
	 * @param token
	 * @param action
	 * @param perameters
	 * @param handler
	 */

	public static void postAction(String collectionId, String contentId,
			String token, LFSActions action, RequestParams perameters,
			JsonHttpResponseHandler handler) {
		// Build the URL

		String url = new Uri.Builder().scheme(Config.scheme)
				.authority(Config.quillDomain + "." + Config.networkId)
				.appendPath("api").appendPath("v3.0").appendPath("message").appendPath("")+contentId+
				(new Uri.Builder().appendPath(actions[action.value()]).appendPath("")
				.appendQueryParameter("lftoken", token)
				.appendQueryParameter("collection_id", collectionId));

		//
		// String url = "http://quill.labs.fyre.co/api/v3.0/message/"
		// + contentId
		// + "/"
		// + actions[action.value()]
		// + "/"
		// + (new Uri.Builder().appendQueryParameter("lftoken", token)
		// .appendQueryParameter("collection_id", collectionId));

		Log.d("Action SDK call", "" + url);
		Log.d("Action SDK call", "" + perameters);
		HttpClient.client.post(url, perameters, handler);
	}

	/**
	 * 
	 * @param authorId
	 *            Author of Post
	 * @param token
	 *            Livefyre Token
	 * @param perameters
	 *            Perameters includes network,
	 * @param handler
	 */

	public static void flagAuthor(String authorId, String token,
			RequestParams perameters, JsonHttpResponseHandler handler) {
		// Build the URL

//		String url = "http://quill.client-solutions.fyre.co/api/v3.0/author/"
//				+ authorId + "/ban/"
//				+ (new Uri.Builder().appendQueryParameter("lftoken", token));

		
		
		final Builder uriBuilder = new Uri.Builder().scheme(Config.scheme)
				.authority(Config.quillDomain + "." + Config.networkId)
				.appendPath("api").appendPath("v3.0").appendPath("author");
		String url=uriBuilder+""+authorId+(new Uri.Builder().appendPath("ban").appendPath("").appendQueryParameter("lftoken", token));
				
				
		
		
		
		
		
		Log.d("Action SDK call", "" + url);
		Log.d("Action SDK call", "" + perameters);
		HttpClient.client.post(url, perameters, handler);
	}

}
