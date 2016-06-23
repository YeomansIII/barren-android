package io.yeomans.barren;

/**
 * Created by jason on 6/20/16.
 */

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import io.yeomans.barren.models.Device;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class BarrenFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";
    SharedPreferences preferences;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        preferences = getSharedPreferences(MainActivity.MAIN_PREFERENCES, 0);
        preferences.edit().putString(MainActivity.PREF_FCM_ID, refreshedToken).apply();
        if (preferences.getBoolean(MainActivity.PREF_REGISTERED, false)) {
            sendRegistrationToServer(refreshedToken);
        }
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p/>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        String url;
        if (BuildConfig.DEBUG) {
            url = MainActivity.LOCAL_URL;
        } else {
            url = MainActivity.PROD_URL;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        String id = preferences.getString(MainActivity.PREF_DEVICE_ID, null);
        if (id != null) {
            apiService.updateDevice(id, new Device(token));
        }
    }
}