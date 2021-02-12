package com.dmz.airpodscasesimulator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.List;

import static com.android.billingclient.api.BillingClient.BillingResponse.ITEM_ALREADY_OWNED;
import static com.android.billingclient.api.BillingClient.BillingResponse.OK;
import static com.android.billingclient.api.BillingClient.BillingResponse.USER_CANCELED;

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
    public ViewFlipper imgsPro;
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
    AdView mAdView = null;
    InterstitialAd mInterstitialAd = null;
    Boolean mAdFree = false;
    int i;
    int currentimg;
    int currentimgPro;
    Boolean pro;

    ConstraintLayout constraintLayoutCommon;
    ConstraintLayout constraintLayoutPro;

    ImageButton mBuyButton;
    Button button;

    /*GdprHelper gdprHelper;
    private final int DIALOG_DATE = 1;
    int myYear = 2000;
    int myMonth = 01;
    int myDay = 01;*/

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
        sp = getSharedPreferences("data", Activity.MODE_PRIVATE);
        /*if (sp.getBoolean("newuser1", true)) {
            showDialog(DIALOG_DATE);
        }*/

        constraintLayoutCommon = findViewById(R.id.common);
        constraintLayoutPro = findViewById(R.id.pro);

        Bundle extras = new Bundle();
        //extras.putString("npa", sp.getString("bingle_age", "0"));
        MobileAds.initialize(this, "ca-app-pub-3254112346644116~7938885729");
        mAdView = findViewById(R.id.adView);
        adRequest = new AdRequest.Builder().build();//.addTestDevice("3B79C89427C65674A4AB312FF9F08222")
        mInterstitialAd = new InterstitialAd(MainActivity.this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3254112346644116/4389752444");
        mAdView.loadAd(adRequest);
        mInterstitialAd.loadAd(adRequest);

        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {


            @Override
            public void onBillingSetupFinished(int i) {
                if (i == OK) {
                    List<Purchase> purchaseDataList = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
                    if (purchaseDataList.size() != 0) {
                        sp.edit().putBoolean("adsdis", true).commit();
                        mAdFree = true;
                    }
                    if (!mAdFree) {
                        //Toast.makeText(MainActivity.this, "ad true", Toast.LENGTH_LONG).show();
                    } else {
                        mAdView.setVisibility(View.GONE);
                        //Toast.makeText(MainActivity.this, "ad false", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.i("TAG", "disconnected");
            }
        });

        mBuyButton = findViewById(R.id.buyButton);
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donate();
            }
        });

        mAdFree = sp.getBoolean("adsdis", false);
        imgs = findViewById(R.id.imageSwitcher);
        imgsPro = findViewById(R.id.imageSwitcher2);

        button = findViewById(R.id.corky);
        tv = findViewById(R.id.textView);

        Example.uri = 1;
        button.setOnClickListener(mCorkyListener);
        pattern = new long[]{200, 50};
        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mPlayer = MediaPlayer.create(this, R.raw.acl4_7);
        mPlayer1 = MediaPlayer.create(this, R.raw.opaudio);

        pro = sp.getBoolean("pro", true);

        switchPro(pro);
        mCheckedItems[0] = sp.getBoolean("sound", true);
        mCheckedItems[1] = sp.getBoolean("vibr", true);
        tv.setText(Example.c + "");

    }


    /*DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear;
            myDay = dayOfMonth;

            sp.edit().putBoolean("newuser1", false).putInt("age", getAge(myYear, myMonth, myDay)).commit();
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
    };*/

    public void donate() {
        showDialog(IDD_CHECK_CATS2);
    }

    @Override
    public void onPurchasesUpdated(int billingResult, List<Purchase> purchases) {


        if (billingResult == OK && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult == USER_CANCELED) {
            Log.d(TAG, "User Canceled" + billingResult);
        } else if (billingResult == ITEM_ALREADY_OWNED) {
            sp.edit().putBoolean("adsdis", true).apply();
            mAdFree = sp.getBoolean("adsdis", false);
            List<Purchase> purchaseDataList = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP).getPurchasesList();
            for (Purchase purchaseData : purchaseDataList) {
                if (purchaseData.getSku().equals(lastSku)) {
                    mBillingClient.consumeAsync(purchaseData.getPurchaseToken(), new ConsumeResponseListener() {
                        @Override
                        public void onConsumeResponse(int i, String s) {
                            purchase(lastSku);
                        }
                    });
                }
            }
        } else {
            Log.d(TAG, "Other code" + billingResult);
        }
    }


    private void handlePurchase(Purchase purchase) {
        if (purchase.getSku().equals(ITEM_SKU_ADREMOVAL) || purchase.getSku().equals(ITEM_SKU_ADREMOVAL2) || purchase.getSku().equals(ITEM_SKU_ADREMOVAL3) || purchase.getSku().equals(ITEM_SKU_ADREMOVAL4) || purchase.getSku().equals(ITEM_SKU_ADREMOVAL5)) {
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

        if (pro){
            if (sp.getInt("scorepro", 0) % 10 == 0) if (mAdFree != null) if (!mAdFree) {
                mInterstitialAd.show();
                mInterstitialAd.loadAd(adRequest);
            }

            if (action == MotionEvent.ACTION_DOWN) {
                Example.sdown = event.getY();
                if (currentimgPro == 0) i = 0;
                else i = 9;
            }

            if (action == MotionEvent.ACTION_MOVE) {
                if (Example.sdown - event.getY() > 20 && i < 9 && currentimgPro != 9) {
                    imgsPro.showNext();

                    currentimgPro = i + 1;
                    if (currentimgPro == 9) {
                        Example.c += 1;
                        if (mCheckedItems[0]) mPlayer1.start();
                        if (mCheckedItems[1]) v.vibrate(20);
                    }
                    i += 1;
                    Example.sdown = event.getY();
                }


                if (Example.sdown - event.getY() < -20 && i > 0 && currentimgPro != 0) {
                    imgsPro.showPrevious();
                    currentimgPro = i - 1;
                    if (currentimgPro == 0) {
                        if (mCheckedItems[0]) mPlayer.start();
                        if (mCheckedItems[1]) v.vibrate(25);
                    }
                    i -= 1;
                    Example.sdown = event.getY();
                }
            }

            if (action == MotionEvent.ACTION_UP) {
                if (currentimgPro != 9) {
                    if (i >= 4) {
                        while (currentimgPro != 9) {
                            imgsPro.showNext();
                            currentimgPro += 1;
                        }
                        currentimgPro = 9;
                        if (Example.uri == 1) {
                            Example.c += 1;
                            if (mCheckedItems[0]) mPlayer1.start();
                            if (mCheckedItems[1]) v.vibrate(20);
                            Example.uri = 0;
                        }
                    } else {
                        while (currentimgPro != 0) {
                            imgsPro.showPrevious();
                            currentimgPro -= 1;
                        }
                        currentimgPro = 0;
                        if (Example.uri == 0) {
                            if (mCheckedItems[0]) mPlayer.start();
                            if (mCheckedItems[1]) v.vibrate(25);
                            Example.uri = 1;
                        }

                    }
                }
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("scorepro", Example.c);
            editor.apply();
        }
        else
        {
            if (sp.getInt("score", 0) % 10 == 0) if (mAdFree != null) if (!mAdFree) {
                mInterstitialAd.show();
                mInterstitialAd.loadAd(adRequest);
            }

            if (action == MotionEvent.ACTION_DOWN) {
                Example.sdown = event.getY();
                if (currentimg == 0) i = 0;
                else i = 6;
            }

            if (action == MotionEvent.ACTION_MOVE) {
                if (Example.sdown - event.getY() > 25 && i < 6 && currentimg != 6) {
                    imgs.showNext();
                    currentimg = i + 1;
                    if (currentimg == 6) {
                        Example.c += 1;
                        if (mCheckedItems[0]) mPlayer1.start();
                        if (mCheckedItems[1]) v.vibrate(20);
                    }
                    i += 1;
                    Example.sdown = event.getY();
                }


                if (Example.sdown - event.getY() < -25 && i > 0 && currentimg != 0) {
                    imgs.showPrevious();
                    currentimg = i - 1;
                    if (currentimg == 0) {
                        if (mCheckedItems[0]) mPlayer.start();
                        if (mCheckedItems[1]) v.vibrate(25);
                    }
                    i -= 1;
                    Example.sdown = event.getY();
                }
            }

            if (action == MotionEvent.ACTION_UP) {
                if (currentimg != 6) {
                    if (i >= 3) {
                        while (currentimg != 6) {
                            imgs.showNext();
                            currentimg += 1;
                        }
                        currentimg = 6;
                        if (Example.uri == 1) {
                            Example.c += 1;
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
            }
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt("score", Example.c);
            editor.apply();
        }

        tv.setText(Example.c + "");
        return false;
    }

    private View.OnClickListener mCorkyListener = new View.OnClickListener() {
        public void onClick(View v) {
            showDialog(IDD_CHECK_CATS);

        }
    };

    void switchPro(Boolean isPro) {
        if (isPro) {
            Example.c = sp.getInt("scorepro", 0);
            tv.setText(Example.c + "");
            constraintLayoutPro.setVisibility(View.VISIBLE);
            constraintLayoutCommon.setVisibility(View.GONE);


        } else {
            Example.c = sp.getInt("score", 0);
            tv.setText(Example.c + "");
            constraintLayoutCommon.setVisibility(View.VISIBLE);
            constraintLayoutPro.setVisibility(View.GONE);


        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder builder;

        switch (id) {
            case IDD_CHECK_CATS:

                final String[] checkName = {"Sound", "Vibration"};
                builder = new AlertDialog.Builder(this);
                /*Switch switchval=new Switch(MainActivity.this);
                RelativeLayout relative=new RelativeLayout(MainActivity.this);
                relative.addView(switchval);*/
                View view1 = getLayoutInflater().inflate(R.layout.btn_share, null);
                builder.setCustomTitle(view1);
                Switch switch1 = (Switch) view1.findViewById(R.id.switch1);
                switch1.setActivated(sp.getBoolean("pro", true));
                switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("pro", b);
                        pro = b;
                        switchPro(pro);
                    }
                });

                builder//.setTitle("Settings").setCancelable(true)

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
                                            mInterstitialAd.loadAd(adRequest);
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
                        editor.putInt("scorepro", Example.c);
                        editor.apply();
                    }
                });
                return builder.create();


            /*case DIALOG_DATE:
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
                return builder.create();*/
            //}
            //return super.onCreateDialog(id);
            /*case 100:
                DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
                tpd.setTitle("Please, choose date, month and year of your birth. We don't use this information.");
                tpd.setCancelable(false);
                tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showDialog(DIALOG_DATE);
                    }
                });
                return tpd;*/
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
            public void onSkuDetailsResponse(int i, List<SkuDetails> list) {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder().setSkuDetails(list.get(0)).build();
                int response = mBillingClient.launchBillingFlow(MainActivity.this, flowParams);
            }
        });
    }

    /*private int getAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        Integer ageInt = new Integer(age);

        return ageInt;
    }*/
}
