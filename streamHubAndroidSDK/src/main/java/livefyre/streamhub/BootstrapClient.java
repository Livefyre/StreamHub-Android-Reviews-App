package livefyre.streamhub;

import android.net.Uri;
import android.net.Uri.Builder;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;

/**
 * @author zjj
 */
public class BootstrapClient {
    /**
     * Performs a network request on a different thread and delivers a message to the callback.
     * A JSON object with the results will be bound to the message.
     *
     * @param networkId The collection's network as identified by domain, i.e. livefyre.com.
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @param handler   Response handler
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static void getInit(String networkId,
                               String siteId,
                               String articleId,
                               AsyncHttpResponseHandler handler)
            throws UnsupportedEncodingException
    {
        final String initEndpoint = generateInitEndpoint(networkId, siteId, articleId);
        Log.d("SDK","Before call"+initEndpoint);
        HttpClient.client.get(initEndpoint, handler);                                                                                        
        Log.d("SDK","After call");
    }

    /**
     * Generates an init endpoint with the specified parameters.
     *
     * @param networkId The collection's network as identified by domain, i.e. livefyre.com.
     * @param siteId    The Id of the article's site.
     * @param articleId The Id of the collection's article.
     * @return The init endpoint with the specified parameters.
     * @throws UnsupportedEncodingException
     * @throws MalformedURLException
     */
    public static String generateInitEndpoint(String networkId,
                                              String siteId,
                                              String articleId)
            throws UnsupportedEncodingException
    {
        // Casting
        final String article64 = Helpers.generateBase64String(articleId);

        // Build the URL
        final Builder uriBuilder = new Uri.Builder()
                .scheme(Config.scheme)
                .authority(Config.bootstrapDomain + "." + Config.networkId)
                .appendPath("bs3")
                .appendPath(networkId)
                .appendPath(siteId)
                .appendPath(article64)
                .appendPath("init");

        return uriBuilder.toString();
    }
}
