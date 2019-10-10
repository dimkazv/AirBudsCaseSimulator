package com.dmz.airpodscasesimulator;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.MotionEventCompat;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.DebugGeography;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {

    private static final String TAG = "InAppBilling";
    static final String ITEM_SKU_ADREMOVAL = "disable_ads";
    static final String ITEM_SKU_ADREMOVAL2 = "disable_ads2";
    static final String ITEM_SKU_ADREMOVAL3 = "disable_ads3";
    static final String ITEM_SKU_ADREMOVAL4 = "disable_ads4";
    static final String ITEM_SKU_ADREMOVAL5 = "disable_ads5";
    String lastSku;

    private BillingClient mBillingClient;

    public ViewFlipper imgs;
    public TextView tv;
    public MediaPlayer mPlayer;
    public MediaPlayer mPlayer1;
    public Vibrator v;
    public long[] pattern;
    SharedPreferences sp;
    final boolean[] mCheckedItems = {true, true};
    private final int IDD_CHECK_CATS = 3;
    private final int IDD_CHECK_CATS2 = 4;
    AdRequest adRequest;
    AdView mAdView;
    InterstitialAd mInterstitialAd;
    Boolean mAdFree;
    int i;
    int currentimg;

    GdprHelper gdprHelper;
    private final int DIALOG_DATE = 1;
    int myYear = 2019;
    int myMonth = 01;
    int myDay = 01;

    static BooVariable donate;

    public static class Example {
        static float sdown;
        static int uri = 0;
        static int c;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ConsentInformation.getInstance(this).addTestDevice("3BEA0312B8906056A1FAD6120336C65E");
        ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        sp = getSharedPreferences("data", Activity.MODE_PRIVATE);
        if (sp.getBoolean("newuser", true)) {
            showDialog(DIALOG_DATE);
        }


        Bundle extras = new Bundle();
        extras.putString("npa", sp.getString("bingle_age", "1"));
        MobileAds.initialize(MainActivity.this, "ca-app-pub-3254112346644116~7938885729");
        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3254112346644116/4389752444");

        mBillingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {

            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<Purchase> purchaseDataList = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                    if (purchaseDataList.size() != 0) {
                        sp.edit().putBoolean("adsdis", true).commit();
                        mAdFree = true;
                    }
                    if (!mAdFree) {
                        mAdView.loadAd(adRequest);
                        mInterstitialAd.loadAd(adRequest);
                    } else {
                        mAdView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i("TAG", "disconnected");
            }
        });

        ImageButton mBuyButton = findViewById(R.id.buyButton);
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donate();
            }
        });

        mAdFree = sp.getBoolean("adsdis", false);
        imgs = findViewById(R.id.imageSwitcher);

        Button button = findViewById(R.id.corky);
        tv = findViewById(R.id.textView);

        Example.c = sp.getInt("score", 0);
        mCheckedItems[0] = sp.getBoolean("sound", true);
        mCheckedItems[1] = sp.getBoolean("vibr", true);
        tv.setText(Example.c + "");

        Example.uri = 1;
        button.setOnClickListener(mCorkyListener);
        pattern = new long[]{200, 50};
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mPlayer = MediaPlayer.create(this, R.raw.acl4_7);
        mPlayer1 = MediaPlayer.create(this, R.raw.opaudio);

    }


    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;

            sp.edit().putBoolean("newuser", false).putInt("age", getAge(myYear, myMonth, myDay)).commit();
            if (sp.getInt("age", 0) < 13) {
                sp.edit().putString("bingle_age", "1");
                FirebaseAnalytics.getInstance(MainActivity.this).setAnalyticsCollectionEnabled(false);
                ConsentInformation.getInstance(MainActivity.this).setTagForUnderAgeOfConsent(true);
            } else {
                sp.edit().putString("bingle_age", "0");
                FirebaseAnalytics.getInstance(MainActivity.this).setAnalyticsCollectionEnabled(true);
                ConsentInformation.getInstance(MainActivity.this).setTagForUnderAgeOfConsent(false);
                gdprHelper = new GdprHelper(MainActivity.this);
                gdprHelper.initialise();
                donate = new BooVariable();
                donate.setListener(new BooVariable.ChangeListener() {
                    @Override
                    public void onChange() {
                        donate();
                    }
                });
            }
        }
    };

    public void donate() {
        showDialog(IDD_CHECK_CATS2);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {


        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "User Canceled" + billingResult.getResponseCode());
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            sp.edit().putBoolean("adsdis", true).apply();
            mAdFree = sp.getBoolean("adsdis", false);
            List<Purchase> purchaseDataList = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
            for (Purchase purchaseData : purchaseDataList) {
                if (purchaseData.getSku().equals(lastSku)) {
                    ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseData.getPurchaseToken()).build();
                    mBillingClient.consumeAsync(consumeParams, new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                            purchase(lastSku);
                        }
                    });
                }
            }
        } else {
            Log.d(TAG, "Other code" + billingResult.getResponseCode());
        }
    }


    private void handlePurchase(Purchase purchase) {
        if (purchase.getSku().equals(ITEM_SKU_ADREMOVAL) || purchase.getSku().equals(ITEM_SKU_ADREMOVAL2) || purchase.getSku().equals(ITEM_SKU_ADREMOVAL3)) {
            sp.edit().putBoolean("adsdis", true).apply();
            mAdFree = sp.getBoolean("adsdis", false);
            MainActivity.this.recreate();
            Toast toast = Toast.makeText(getApplicationContext(), "Thank you!", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        if (action == MotionEvent.ACTION_DOWN) {
            Example.sdown = event.getY();
            if (currentimg == 0) i = 0;
            else i = 6;
        }

        if (action == MotionEvent.ACTION_MOVE) {
            if (Example.sdown - event.getY() > 25 && i < 6 && currentimg != 6) {
                imgs.showNext();
                currentimg = i + 1;
                i += 1;
                Example.sdown = event.getY();
            }
            if (Example.sdown - event.getY() < -25 && i > 0 && currentimg != 0) {
                imgs.showPrevious();
                currentimg = i - 1;
                i -= 1;
                Example.sdown = event.getY();
            }
        }

        if (action == MotionEvent.ACTION_UP) {
            if (i >= 3) {
                while (currentimg != 6) {
                    imgs.showNext();
                    currentimg += 1;
                }
                currentimg = 6;
                if (Example.uri == 1) {
                    Example.c += 1;
                    tv.setText(Example.c + "");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("score", Example.c);
                    editor.apply();
                    if (mCheckedItems[0]) mPlayer1.start();
                    if (mCheckedItems[1]) v.vibrate(20);
                    Example.uri = 0;
                }

            } else {
                while (currentimg != 0) {
                    imgs.showPrevious();
                    currentimg -= 1;
                }
                currentimg = 0;
                if (Example.uri == 0) {
                    if (mCheckedItems[0]) mPlayer.start();
                    if (mCheckedItems[1]) v.vibrate(25);
                    Example.uri = 1;
                }

            }
        }
        return false;
    }

    private View.OnClickListener mCorkyListener = new View.OnClickListener() {
        public void onClick(View v) {
            showDialog(IDD_CHECK_CATS);

        }
    };

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;

        switch (id) {
            case IDD_CHECK_CATS:

                final String[] checkName = {"Sound", "Vibration"};
                builder = new AlertDialog.Builder(this);
                builder.setTitle("Settings").setCancelable(true)

                        .setMultiChoiceItems(checkName, mCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                mCheckedItems[which] = isChecked;
                            }
                        })

                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("score", Example.c);
                                editor.putBoolean("sound", mCheckedItems[0]);
                                editor.putBoolean("vibr", mCheckedItems[1]);
                                editor.apply();
                            }
                        })

                        .setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (mAdFree != null) if (!mAdFree) {
                                    {
                                        try {
                                            mInterstitialAd.show();
                                        } catch (IllegalStateException ignored) {

                                        }
                                    }
                                    mInterstitialAd.setAdListener(new AdListener() {
                                        @Override
                                        public void onAdClosed() {
                                            // Load the next interstitial.
                                            finish();
                                            System.exit(0);
                                        }

                                    });
                                }
                                System.exit(0);
                            }
                        }).setNeutralButton("Reset progress", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Example.c = 0;
                        tv.setText(Example.c + "");
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("score", Example.c);
                        editor.apply();
                    }
                });
                return builder.create();


            case DIALOG_DATE:
                //if (id == DIALOG_DATE) {
                builder = new AlertDialog.Builder(this);
                builder.setCancelable(false);
                builder.setTitle("Please, choose date, month and year of your birth. We don't use this information.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog(100);
                    }
                });
                builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
                return builder.create();
            //}
            //return super.onCreateDialog(id);
            case 100:
                DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
                tpd.setTitle("Please, choose date, month and year of your birth. We don't use this information.");
                tpd.setCancelable(false);
                tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog(DIALOG_DATE);
                    }
                });
                return tpd;
            case IDD_CHECK_CATS2:
                LayoutInflater li = LayoutInflater.from(this);
                final View view = li.inflate(R.layout.admin_order_change_view, null);
                builder = new AlertDialog.Builder(this);
                builder.setView(view);
                final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.radioGroup);
                final AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.show();
                alertDialog.getWindow().setLayout(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
                Button buttoncall = view.findViewById(R.id.buttoncall);
                buttoncall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();

                        switch (checkedRadioButtonId) {
                            case R.id.radioButton: //
                                purchase(ITEM_SKU_ADREMOVAL);
                                alertDialog.cancel();
                                break;
                            case R.id.radioButton2: // 2
                                purchase(ITEM_SKU_ADREMOVAL2);
                                alertDialog.cancel();
                                break;
                            case R.id.radioButton3: // 2
                                purchase(ITEM_SKU_ADREMOVAL3);
                                alertDialog.cancel();
                                break;
                            case R.id.radioButton4: // 2
                                purchase(ITEM_SKU_ADREMOVAL4);
                                alertDialog.cancel();
                                break;
                            case R.id.radioButton5: // 2
                                purchase(ITEM_SKU_ADREMOVAL5);
                                alertDialog.cancel();
                                break;
                        }
                    }
                });
            default:
                return null;


        }
    }

    public void purchase(String SKU) {
        lastSku = SKU;
        List<String> skuList = new ArrayList<>();
        skuList.add(SKU);

        SkuDetailsParams skuDetailsParams = SkuDetailsParams.newBuilder().setSkusList(skuList).setType(BillingClient.SkuType.INAPP).build();
        mBillingClient.querySkuDetailsAsync(skuDetailsParams, new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(0)).build();
                BillingResult responseCode = mBillingClient.launchBillingFlow(MainActivity.this, flowParams);
            }
        });
    }

    private int getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);

        return ageInt;
    }
}
