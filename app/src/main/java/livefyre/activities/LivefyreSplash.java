package livefyre.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.livefyre.R;


public class LivefyreSplash extends Activity {
	 
    private static int SPLASH_TIME_OUT = 2000;
     
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livefyre_splash);
  
        new Handler().postDelayed(new Runnable() {
            public void run() {
 
                Intent i = new Intent(LivefyreSplash.this, ReviewsActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
     
}
