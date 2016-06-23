package io.yeomans.barren;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.yeomans.barren.models.Device;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    final static String TAG = "MainActivity";

    final static String MAIN_PREFERENCES = "main";
    final static String PREF_REGISTERED = "registered";
    final static String PREF_DEVICE_ID = "device_id";
    final static String PREF_NAME = "name";
    final static String PREF_OWNER = "owner";
    final static String PREF_FCM_ID = "fcm_id";

    final static String LOCAL_URL = "http://pc.home.yeomans.space:9024/api/v1/";
    final static String PROD_URL = "http://yeomans.space:9024/api/v1/";

    @BindView(R.id.mainDeviceName) TextInputLayout mainDeviceName;
    @BindView(R.id.mainDeviceOwner) TextInputLayout mainDeviceOwner;
    @BindView(R.id.mainRegisterButton) Button mainRegisterButton;
    @BindView(R.id.mainCheckmarkImage) ImageView mainCheckmarkImage;
    @BindView(R.id.mainErrorImage) ImageView mainErrorImage;
    @BindView(R.id.mainProgressCircle) ProgressBar mainProgressCircle;

    ApiService apiService;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        String url;
        if (BuildConfig.DEBUG) {
            url = LOCAL_URL;
        } else {
            url = PROD_URL;
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        preferences = getSharedPreferences(MAIN_PREFERENCES, 0);

        if (preferences.getBoolean(PREF_REGISTERED, false)) {
            //mainCheckmarkImage.setVisibility(View.VISIBLE);
            mainDeviceName.getEditText().setText(preferences.getString(PREF_NAME, ""));
            mainDeviceOwner.getEditText().setText(preferences.getString(PREF_OWNER, ""));
            mainRegisterButton.setText("Update Registration");
        }
    }

    @OnClick(R.id.mainRegisterButton)
    public void register() {
        Log.d(TAG, "Register clicked");
        mainErrorImage.setVisibility(View.GONE);
        mainProgressCircle.setVisibility(View.VISIBLE);
        mainRegisterButton.setEnabled(false);
        if (preferences.getBoolean(PREF_REGISTERED, false)) {
            update();
        } else {
            Log.d(TAG, "Registering");
            String fcmId = preferences.getString(PREF_FCM_ID, null);
            if (fcmId != null) {
                Log.d(TAG, "Registering 2");
                final String name = mainDeviceName.getEditText().getText().toString(), owner = mainDeviceOwner.getEditText().getText().toString();
                Device device = new Device(name, owner, fcmId);
                Call<Device> call = apiService.createDevice(device);
                call.enqueue(new Callback<Device>() {
                    @Override
                    public void onResponse(Call<Device> call, Response<Device> response) {
                        if (response.isSuccessful()) {
                            mainProgressCircle.setVisibility(View.GONE);
                            mainCheckmarkImage.setVisibility(View.VISIBLE);
                            preferences.edit().putString(PREF_NAME, name).putString(PREF_OWNER, owner).putString(PREF_DEVICE_ID, response.body().getId()).putBoolean(PREF_REGISTERED, true).apply();
                        } else {
                            mainProgressCircle.setVisibility(View.GONE);
                            mainErrorImage.setVisibility(View.VISIBLE);
                            mainRegisterButton.setEnabled(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Device> call, Throwable t) {
                        Snackbar snack = Snackbar.make(findViewById(R.id.mainRelativeLayout), "Network error", Snackbar.LENGTH_SHORT);
                        View snackView = snack.getView();
                        snackView.setBackgroundColor(Color.RED);
                        snack.show();
                        mainProgressCircle.setVisibility(View.GONE);
                        mainErrorImage.setVisibility(View.VISIBLE);
                        mainRegisterButton.setEnabled(true);
                    }
                });
            } else {
                Snackbar snack = Snackbar.make(findViewById(R.id.mainRelativeLayout), "FCM ID was null", Snackbar.LENGTH_SHORT);
                View snackView = snack.getView();
                snackView.setBackgroundColor(Color.RED);
                snack.show();
                mainProgressCircle.setVisibility(View.GONE);
                mainErrorImage.setVisibility(View.VISIBLE);
                mainRegisterButton.setEnabled(true);
            }
        }
    }

    public void update() {
        String deviceId = preferences.getString(PREF_DEVICE_ID, null);
        if (deviceId != null) {
            final String name = mainDeviceName.getEditText().getText().toString(), owner = mainDeviceOwner.getEditText().getText().toString();
            Device device = new Device(name, owner);
            Call<Device> call = apiService.updateDevice(deviceId, device);
            call.enqueue(new Callback<Device>() {
                @Override
                public void onResponse(Call<Device> call, Response<Device> response) {
                    mainProgressCircle.setVisibility(View.GONE);
                    mainCheckmarkImage.setVisibility(View.VISIBLE);
                    preferences.edit().putString(PREF_NAME, name).putString(PREF_OWNER, owner).apply();
                }

                @Override
                public void onFailure(Call<Device> call, Throwable t) {
                    Snackbar snack = Snackbar.make(findViewById(R.id.mainRelativeLayout), "Network error", Snackbar.LENGTH_SHORT);
                    View snackView = snack.getView();
                    snackView.setBackgroundColor(Color.RED);
                    snack.show();
                    mainProgressCircle.setVisibility(View.GONE);
                    mainErrorImage.setVisibility(View.VISIBLE);
                    mainRegisterButton.setEnabled(true);
                }
            });
        } else {
            Snackbar snack = Snackbar.make(findViewById(R.id.mainRelativeLayout), "Device ID was null", Snackbar.LENGTH_SHORT);
            View snackView = snack.getView();
            snackView.setBackgroundColor(Color.RED);
            snack.show();
            mainProgressCircle.setVisibility(View.GONE);
            mainErrorImage.setVisibility(View.VISIBLE);
            mainRegisterButton.setEnabled(true);
        }
    }
}
