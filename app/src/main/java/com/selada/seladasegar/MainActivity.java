package com.selada.seladasegar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.selada.seladasegar.api.ApiAjax;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.api.ApiInterface;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.presentation.account.AccountFragment;
import com.selada.seladasegar.presentation.checkout.AfterCheckoutActivity;
import com.selada.seladasegar.presentation.checkout.CheckoutActivity;
import com.selada.seladasegar.presentation.coupons.DetailCouponActivity;
import com.selada.seladasegar.presentation.favourite.FavouriteFragment;
import com.selada.seladasegar.presentation.favourite.ProdukFavoritFragment;
import com.selada.seladasegar.presentation.home.HeaderAdapter;
import com.selada.seladasegar.presentation.home.HomeCartAdapter;
import com.selada.seladasegar.presentation.home.HomeFeedAdapter;
import com.selada.seladasegar.presentation.home.HomeFragment;
import com.selada.seladasegar.services.CustomViewPager;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.ExternalStoragePermissions;
import com.selada.seladasegar.services.GlideImageLoadingService;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.services.ViewPagerAdapter;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ss.com.bannerslider.Slider;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context context;
    private ActionBarDrawerToggle toggle;
    private List<Carts> cartsList;
    private DBManager dbManager;
    boolean doubleBackToExitPressedOnce = false;
    boolean isNeedTransition = true;
    boolean mToolBarNavigationListenerIsRegistered = false;
    private HomeFeedAdapter homeFeedAdapter;
    private HeaderAdapter headerAdapter;
    private HomeCartAdapter homeCartAdapter;
    private AppUpdateManager appUpdateManager;
    public boolean isHideToolbarAccount = true;
    private static final int MY_REQUEST_CODE = 100;
    private static final int UPDATE_FROM_PLAY_STORE_REQUEST = 111;
    public String linkData = "";
    private String host = "";
    public boolean isIntentFromLink = false;
    private String code = "";

    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RecyclerView rvBottomSheetCategory;

    @BindView(R.id.screen_area)
    public FrameLayout screen_area;
    @BindView(R.id.pager)
    public CustomViewPager viewPager;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    FrameLayout navigationView;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.imgNavHome)
    ImageView imgNavHome;
    @BindView(R.id.imgNavFav)
    ImageView imgNavFav;
    @BindView(R.id.imgNavKeranjang)
    ImageView imgNavPesanan;
    @BindView(R.id.imgNavAccount)
    ImageView imgNavAccount;
    @BindView(R.id.tvNavHome)
    TextView tvNavHome;
    @BindView(R.id.tvNavFav)
    TextView tvNavFav;
    @BindView(R.id.tvNavKeranjang)
    TextView tvNavPesanan;
    @BindView(R.id.tvNavAccount)
    TextView tvNavAccount;
    @BindView(R.id.etSearchProduct)
    EditText etSearchProduct;
    @BindView(R.id.cartTotal)
    TextSwitcher floatCartTotal;
    @BindView(R.id.tvBadgeCount)
    TextView tvBadgeCount;
    @BindView(R.id.cart_total)
    TextSwitcher cart_total;
    @BindView(R.id.tvNoData)
    TextView tvNoData;

    @BindView(R.id.tvMenuUtama)
    TextView tvMenuUtama;
    @BindView(R.id.tvKategori)
    TextView tvKategori;
    @BindView(R.id.tvAreaPengiriman)
    TextView tvAreaPengiriman;
    @BindView(R.id.ic_menu_utama)
    ImageView ic_menu_utama;
    @BindView(R.id.ic_kategori)
    ImageView ic_kategori;

    @BindView(R.id.layoutDetailCart)
    LinearLayout layoutDetailCart;
    @BindView(R.id.layoutCart)
    public RelativeLayout layoutCart;
    @BindView(R.id.btnCheckout)
    FrameLayout btnCheckout;
    @BindView(R.id.btnClose)
    FrameLayout btnClose;

    @BindView(R.id.layoutToolbarAccount)
    public LinearLayout layoutToolbarAccount;
    @BindView(R.id.toolbarTitleInfoAkun)
    public TextView toolbarTitleInfoAkun;
    @BindView(R.id.toolbarTitlePesanan)
    public TextView toolbarTitlePesanan;

    @BindView(R.id.rvCartDetail)
    RecyclerView rvCartDetail;

    @BindView(android.R.id.content)
    ViewGroup v;

    @BindView(R.id.btnPilihCabang)
    public LinearLayout btnPilihCabang;

    @BindView(R.id.layoutCabang)
    LinearLayout layoutCabang;
    @BindView(R.id.logo_koperasi)
    ImageView logo_koperasi;

    @BindView(R.id.layout_koperasi)
    FrameLayout layout_koperasi;

    @OnClick(R.id.toolbar_whatsapp_icon)
    void onClickToolbarChat() {
        directToWhatsapp();
    }

    @BindView(R.id.frameInfoAkun)
    public FrameLayout frameInfoAkun;

    @BindView(R.id.framePesanan)
    public FrameLayout framePesanan;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.cvKupon)
    CardView cvKupon;

    @OnClick(R.id.btnPilihCabang)
    void onClickPilihCabang() {
        Dialog dialog = MethodUtil.getDialogCart(R.layout.dialog_pilih_cabang, context);
        if (PreferenceManager.getFirstOpen()) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        CardView cvBandung = dialog.findViewById(R.id.cvBandung);
        cvBandung.setOnClickListener(this);
        CardView cvBelitung = dialog.findViewById(R.id.cvBelitung);
        cvBelitung.setOnClickListener(this);
        CardView cvKarawang = dialog.findViewById(R.id.cvKarawang);
        cvKarawang.setOnClickListener(this);
        CardView cvCilegon = dialog.findViewById(R.id.cvCilegon);
        cvCilegon.setOnClickListener(this);
        CardView cvBekasi = dialog.findViewById(R.id.cvBekasi);
        cvBekasi.setOnClickListener(this);
        CardView cvTangerang = dialog.findViewById(R.id.cvTangerang);
        cvTangerang.setOnClickListener(this);
        CardView cvCirebon = dialog.findViewById(R.id.cvCirebon);
        cvCirebon.setOnClickListener(this);
    }

    @OnClick(R.id.layoutCart)
    void onClickLayoutCart() {
//        MethodUtil.toggleTransitionExplode(v, layoutCart, false);
        layoutCart.setVisibility(View.GONE);
        initCartComponent();
        notifyCartAdapter();
        layoutDetailCart.setVisibility(View.VISIBLE);
//        MethodUtil.toggleTransitionExplode(v, layoutDetailCart, true);
    }

    @OnClick(R.id.btnKupon)
    void onClickbtnKupon(){
        Intent intent = new Intent(context, DetailCouponActivity.class);
        intent.putExtra("isFromMain", true);
        intent.putExtra("code", code);
        Objects.requireNonNull(context).startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
    }

    @OnClick(R.id.btnDeleteCoupon)
    void onClickbtnDeleteCoupon(){
        PreferenceManager.setCodeCoupon("");
        cvKupon.setVisibility(View.GONE);
    }

    @OnClick(R.id.btnCheckout)
    void onClickBtnCheckout() {
        Intent intent = new Intent(context, CheckoutActivity.class);
        Objects.requireNonNull(context).startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
    }

    @OnClick(R.id.btnClose)
    public void onClickBtnClose() {
        layoutDetailCart.setVisibility(View.GONE);
        layoutCart.setVisibility(View.VISIBLE);
        checkCart(false);
//        MethodUtil.toggleTransitionSlideBottom(v, layoutCart, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
        context = this;
        viewPager.setPagingEnabled(false);
        viewPager.setVisibility(View.VISIBLE);
        screen_area.setVisibility(View.GONE);
        setIntentFromLink();
        setupViewPager(viewPager);
//        initAutoCompletePlaces();
        initPrefrences();

        Slider.init(new GlideImageLoadingService(this));
        BigImageViewer.initialize(GlideImageLoader.with(this));
        cartsList = new ArrayList<>();
        dbManager = new DBManager(this);
        dbManager.open();
        dbManager.fetch();
        dbManager.fetchFavourite();

        floatCartTotal.setInAnimation(context, android.R.anim.slide_in_left);
        floatCartTotal.setOutAnimation(context, android.R.anim.slide_out_right);

        if (isIntentFromLink) {
            setCabangAutomatically();
        } else {
            if (PreferenceManager.getFirstOpen()) {
                onClickPilihCabang();
//                setBekasiManual();
            }
        }

        if (PreferenceManager.getIsKoperasi()){
            btnPilihCabang.setEnabled(false);
            layoutCabang.setVisibility(View.GONE);
            layout_koperasi.setVisibility(View.VISIBLE);
        }

        checkCart(false);
        setOnClickListenerMenu();
        setDefaultFragment();
        setBottomNavigationView(getResources().getString(R.string.Home));
        setPermission();
        checkCouponUsed();
        setIntentFromDetailCoupon();
        setIntentFromAccount();
        setIntentFromCheckout();
        setIntentToCheckout();
        setIntentFromNotification();

        layoutToolbarAccount.setVisibility(View.GONE);
        layoutDetailCart.setVisibility(View.GONE);
//        checkVersionUpdate();
    }

    public Context getContext(){
        return context;
    }

    private void setCabangAutomatically() {
        switch (host) {
            case "belitung.seladasegar.com":
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BELITUNG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BELITUNG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BELITUNG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA BELITUNG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BANDUNG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
            case "karawang.seladasegar.com":
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_KARAWANG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_KARAWANG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_KARAWANG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA KARAWANG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_KARAWANG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
            case "cilegon.seladasegar.com":
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_CILEGON);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_CILEGON);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_CILEGON);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA CILEGON");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_CILEGON);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
            case "bekasi.seladasegar.com":
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BEKASI);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BEKASI);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BEKASI);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA BEKASI");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BEKASI);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
            case "tangerang.seladasegar.com":
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_TANGERANG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_TANGERANG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_TANGERANG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA TANGERANG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_TANGERANG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
            case "cirebon.seladasegar.com":
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_CIREBON);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_CIREBON);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_CIREBON);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA CIREBON");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_CIREBON);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
            default:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BANDUNG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BANDUNG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BANDUNG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA BANDUNG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BANDUNG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                break;
        }
    }

    private void checkVersionUpdate(){
        String currentVersion = BuildConfig.VERSION_NAME;
        String appPackageName = getPackageName();
        String playStoreVersion = "";
        try {
            playStoreVersion = new MyTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (!Objects.equals(playStoreVersion, currentVersion)) {
            try {
                new AlertDialog.Builder(this)
                        .setTitle("Pemberitahuan")
                        .setMessage("Pembaruan tersedia, silahkan perbarui aplikasi anda")
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(false)
                        .setPositiveButton("Perbarui",
                                (dialog, id) -> {
                                    try {
                                        startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)), UPDATE_FROM_PLAY_STORE_REQUEST);
                                    } catch (Exception e) {
                                        try {
                                            startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), UPDATE_FROM_PLAY_STORE_REQUEST);
                                        } catch (Exception ignored) {
                                            ignored.printStackTrace();
                                        }
                                    }
                                    dialog.dismiss();
                                }).show();
                //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                try {
                    startActivityForResult(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)), UPDATE_FROM_PLAY_STORE_REQUEST);
                } catch (Exception e) {
                }
            }
        }
    }

    private void setIntentFromLink() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null){
            try {
                List<String> params = data.getPathSegments();
                System.out.println(data.getHost() + " - " + new Gson().toJson(params));

                if (params.size() > 0){
                    isIntentFromLink = true;
                    linkData = params.get(1);
                    host = data.getHost();

                    Log.d("Link Data", host + " = " + linkData);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String doc = null;
            try {
                doc = Jsoup.connect("https://play.google.com/store/apps/details?id=com.selada.seladasegar&hl=en&gl=US")
                        .timeout(30000)
                        .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                        .referrer("http://www.google.com")
                        .get()
                        .select(".hAyfc .htlgb")
                        .get(7)
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert doc != null;
//            Elements data = doc.select("#fcxH9b > div.WpDbMd > c-wiz > div > div.ZfcPIb > div > main > c-wiz:nth-child(4) > div.W4P4ne > div.JHTxhe.IQ1z0d > div > div:nth-child(4) > span > div > span");
//            playStoreVersion = "0";
//            if (data.size() > 0) {
//                System.out.println("full text : " + data.get(0).text());
//                Pattern pattern = Pattern.compile("(.*)\\s+\\((\\d+)\\)");
//                Matcher matcher = pattern.matcher(data.get(0).text());
//                if (matcher.find()) {
//                    System.out.println("version name : " + matcher.group(1));
//                    System.out.println("version code : " + matcher.group(2));
//                    playStoreVersion = matcher.group(1);
//                }
//            }
            return doc;
        }


        @Override
        protected void onPostExecute(String result) {
            //if you had a ui element, you could display the title

        }
    }

    private void setIntentFromNotification() {
        String orderId = getIntent().getStringExtra(Constant.INTENT_ORDER_ID);
        if (orderId != null) {
            Intent intent = new Intent(this, AfterCheckoutActivity.class);
            intent.putExtra(Constant.INTENT_ORDER_ID, orderId);
            startActivity(intent);
        }
    }

    private void setIntentToCheckout() {
        if (getIntent() != null) {
            boolean isGoToCheckout = getIntent().getBooleanExtra("isGoToCheckout", false);
            if (isGoToCheckout) {
                Intent intent = new Intent(this, CheckoutActivity.class);
                startActivity(intent);
            }
        }
    }

    private void initAutoCompletePlaces() {
        String apiKey = getString(R.string.api_key);
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        PlacesClient placesClient = Places.createClient(this);
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        // Construct a request object, passing the place ID and fields array.
//        String[] address = getAutoCompletedSupportFragment();
//        Log.d("address", address[0]);
//        FetchPlaceRequest request = FetchPlaceRequest.builder(address[0], placeFields)
        FetchPlaceRequest request = FetchPlaceRequest.builder("address[0]", placeFields)
                .build();

        placesClient.fetchPlace(request).addOnSuccessListener(runnable -> {
            Place place = runnable.getPlace();
            Log.i("TAG", "Place found: " + place.getName());
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
    }

    public void checkCouponUsed(){
        if (!PreferenceManager.getCodeCoupon().equals("")){
            code = PreferenceManager.getCodeCoupon();
            cvKupon.setVisibility(View.VISIBLE);
        }
    }

    public String getCode(){
        return code.equals("")?"":code;
    }

    private void setIntentFromDetailCoupon(){
        if (getIntent()!=null){
            boolean isFromDetail = getIntent().getBooleanExtra("isFromDetail", false);
            if(isFromDetail){
                onClickLayoutCart();
            }
        }
    }

    private void setIntentFromAccount() {
        if (getIntent() != null) {
            boolean isFromAccount = getIntent().getBooleanExtra("isFromAccount", false);
            if (isFromAccount) {
                onClickAccountHome();
            }
        }
    }

    private void setIntentFromCheckout() {
        if (getIntent() != null) {
            boolean isLoginFromCheckout = getIntent().getBooleanExtra("isLoginFromCheckout", false);
            if (isLoginFromCheckout) {
                viewPager.setVisibility(View.GONE);
                screen_area.setVisibility(View.VISIBLE);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isLoginFromCheckout", true);
                callFragment(new AccountFragment(), bundle, isNeedTransition);
//                viewPager.setCurrentItem(1);
                setToolbarComponent("Akun");
                setBottomNavigationView(getResources().getString(R.string.Akun));
                layoutCart.setVisibility(View.GONE);
                layoutDetailCart.setVisibility(View.GONE);
            }
        }
    }

    public void initCartComponent() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        rvCartDetail.setLayoutManager(layoutManager);
        updateFloatingCart(false);
    }

    private void directToWhatsapp() {
        String number = PreferenceManager.getPhoneSeladaSegar();
        String msg = "Hallo Selada Segar";
        String url = "https://api.whatsapp.com/send?phone=" + number;
        try {
            url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void checkCart(boolean isFromCart) {
        Cursor cursor = dbManager.readAllData();
        cartsList.clear();
        int gPrice = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    Carts newCarts = new Carts();
                    newCarts.itemId = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_ID));
                    newCarts.itemName = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_NAME));
                    newCarts.itemImage = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_IMAGE));
                    newCarts.itemPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_PRICE));
                    newCarts.itemQty = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                    newCarts.itemStok = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_STOK));
                    newCarts.itemTotalPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_TOTAL_PRICE));
                    if (!newCarts.itemQty.equals("0")) cartsList.add(newCarts);
                } while (cursor.moveToNext());

                for (int i = 0; i < cartsList.size(); i++) {
                    Carts newCarts = cartsList.get(i);
                    gPrice += Integer.parseInt(newCarts.itemTotalPrice);
                }

                floatCartTotal.setText(MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                cart_total.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                floatCartTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));

                homeCartAdapter = new HomeCartAdapter(cartsList, context, MainActivity.this, cart_total, floatCartTotal);
                rvCartDetail.setHasFixedSize(true);
                rvCartDetail.setAdapter(homeCartAdapter);
                cart_total.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                floatCartTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));

                btnCheckout.setEnabled(true);
                btnCheckout.setBackgroundColor(context.getResources().getColor(R.color.light_green));
                tvNoData.setVisibility(View.GONE);

                if (!isFromCart){
                    handleCart(false);
                }
            } else {
                btnCheckout.setEnabled(false);
                btnCheckout.setBackgroundColor(context.getResources().getColor(R.color.gray_text));
//                MethodUtil.toggleTransitionSlideBottom(v, layoutCart, false);

                layoutCart.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            String TAG = "SQLDatabase";
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateFloatingCart(boolean isOnlyShowFloat) {
        Cursor cursor = dbManager.readAllData();
        cartsList.clear();
        int gPrice = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    Carts newCarts = new Carts();
                    newCarts.itemId = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_ID));
                    newCarts.itemName = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_NAME));
                    newCarts.itemImage = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_IMAGE));
                    newCarts.itemPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_PRICE));
                    newCarts.itemQty = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                    newCarts.itemStok = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_STOK));
                    newCarts.itemTotalPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_TOTAL_PRICE));

                    if (!newCarts.itemQty.equals("0")) cartsList.add(newCarts);
                } while (cursor.moveToNext());

                for (int i = 0; i < cartsList.size(); i++) {
                    Carts newCarts = cartsList.get(i);
                    gPrice += Integer.parseInt(newCarts.itemTotalPrice);
                }

                floatCartTotal.setText(MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                cart_total.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                floatCartTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));

                if (!isOnlyShowFloat) {
                    homeCartAdapter = new HomeCartAdapter(cartsList, context, MainActivity.this, cart_total, floatCartTotal);
                    rvCartDetail.setHasFixedSize(true);
                    rvCartDetail.setAdapter(homeCartAdapter);
                    cart_total.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                    floatCartTotal.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(gPrice)));
                }

                btnCheckout.setEnabled(true);
                btnCheckout.setBackgroundColor(context.getResources().getColor(R.color.light_green));
                tvNoData.setVisibility(View.GONE);
            } else {
                btnCheckout.setEnabled(false);
                btnCheckout.setBackgroundColor(context.getResources().getColor(R.color.gray_text));
//                MethodUtil.toggleTransitionSlideBottom(v, layoutCart, false);
                layoutCart.setVisibility(View.GONE);
                tvNoData.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            String TAG = "SQLDatabase";
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateFloatingCart() {
        Cursor cursor = dbManager.readAllData();
        int total = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    Carts newCarts = new Carts();
                    newCarts.itemId = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_ID));
                    newCarts.itemName = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_NAME));
                    newCarts.itemImage = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_IMAGE));
                    newCarts.itemPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_PRICE));
                    newCarts.itemQty = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_QTY));
                    newCarts.itemTotalPrice = cursor.getString(cursor.getColumnIndex(SQLDatabaseHelper.ITEM_TOTAL_PRICE));
                    total += Integer.parseInt(newCarts.itemTotalPrice);
                } while (cursor.moveToNext());
            }

            cart_total.setText("Rp " + MethodUtil.toCurrencyFormat(String.valueOf(total)));

            try {
                floatCartTotal.setText(MethodUtil.toCurrencyFormat(String.valueOf(total)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("SQLDatabase", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onResume() {
        updateFloatingCart(true);
        super.onResume();
    }

    @Override
    protected void onPause() {
        updateFloatingCart(true);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateFloatingCart(true);
//        checkInAppUpdates();
    }

    private void setPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

            } else {
                ExternalStoragePermissions.verifyStoragePermissions(this);
            }
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Dexter.withActivity(this)
                    .withPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            // check if all permissions are granted
                            if (report.areAllPermissionsGranted()) {
                                // do you work now
                            }

                            // check for permanent denial of any permission
                            if (report.isAnyPermissionPermanentlyDenied()) {
                                // permission is denied permenantly, navigate user to app settings
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                        }

                    })
                    .onSameThread()
                    .check();
        }
    }

    private void setOnClickListenerMenu() {
        tvAreaPengiriman.setText(PreferenceManager.getNamaCabang());

        LinearLayout llFavProduct = findViewById(R.id.ll_fav_product);
        LinearLayout llLogReg = findViewById(R.id.ll_log_reg);
        LinearLayout llLacakPesanan = findViewById(R.id.ll_lacak_pesanan);
        LinearLayout llKontakKami = findViewById(R.id.ll_kontak_kami);
        LinearLayout nav_sub_menu_utama = findViewById(R.id.nav_sub_menu_utama);
        LinearLayout nav_sub_kategori = findViewById(R.id.nav_sub_kategori);
        LinearLayout llMenuUtama = findViewById(R.id.llMenuUtama);
        LinearLayout llKategori = findViewById(R.id.llKategori);
        LinearLayout llHome = findViewById(R.id.ll_home);
        LinearLayout llBelanjaSekarang = findViewById(R.id.ll_belanja_sekarang);
        LinearLayout llDaftarProduk = findViewById(R.id.ll_daftar_produk);
        LinearLayout llNavLacakPesanan = findViewById(R.id.ll_nav_lacak_pesanan);
        LinearLayout ll_nav_sayur = findViewById(R.id.ll_nav_sayur);
        LinearLayout ll_nav_buah = findViewById(R.id.ll_nav_buah);
        LinearLayout ll_nav_hidroponik = findViewById(R.id.ll_nav_hidroponik);
        LinearLayout ll_nav_bumbu = findViewById(R.id.ll_nav_bumbu);
        FrameLayout ll_facebook = findViewById(R.id.nav_facebook);
        FrameLayout ll_instagram = findViewById(R.id.nav_instagram);
        FrameLayout ll_whatsapp = findViewById(R.id.nav_whatsapp);

        llFavProduct.setOnClickListener(view -> {
            callFragment(new FavouriteFragment(), null, !isNeedTransition);
            setToolbarComponent("Produk Favorit Anda");
            setBottomNavigationView(getResources().getString(R.string.Favorit));
        });

        llLogReg.setOnClickListener(view -> {
        });

        llLacakPesanan.setOnClickListener(view -> {
        });

        llKontakKami.setOnClickListener(view -> {
            callFragment(new ProdukFavoritFragment(), null, !isNeedTransition);
            setToolbarComponent("Kontak Kami");
            setBottomNavigationView(getResources().getString(R.string.Default));
        });

        nav_sub_menu_utama.setOnClickListener(view -> {
            ic_menu_utama.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_grid_on_green_24));
            tvMenuUtama.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_green));
            ic_kategori.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_view_list_24));
            tvKategori.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
            llMenuUtama.setVisibility(View.VISIBLE);
            llKategori.setVisibility(View.GONE);
//            MethodUtil.toggleTransitionSlideStart(v, llMenuUtama, true);
//            MethodUtil.toggleTransitionSlideStart(v, llKategori, false);
        });

        nav_sub_kategori.setOnClickListener(view -> {
            ic_menu_utama.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_grid_on_24));
            tvMenuUtama.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
            ic_kategori.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_view_list_green_24));
            tvKategori.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_green));
            llKategori.setVisibility(View.VISIBLE);
            llMenuUtama.setVisibility(View.GONE);
//            MethodUtil.toggleTransitionSlideEnd(v, llKategori, true);
//            MethodUtil.toggleTransitionSlideEnd(v, llMenuUtama, false);
        });

        llHome.setOnClickListener(view -> {
            callFragment(new HomeFragment(), null, !isNeedTransition);
            setToolbarComponent("Home");
            setBottomNavigationView(getResources().getString(R.string.Home));
        });


        llBelanjaSekarang.setOnClickListener(view -> {

        });

        llDaftarProduk.setOnClickListener(view -> {

        });

        llNavLacakPesanan.setOnClickListener(view -> {
        });

        ll_nav_sayur.setOnClickListener(view -> {

        });

        ll_nav_bumbu.setOnClickListener(view -> {

        });

        ll_nav_hidroponik.setOnClickListener(view -> {

        });

        ll_nav_buah.setOnClickListener(view -> {

        });

        ll_facebook.setOnClickListener(view -> {

        });

        ll_instagram.setOnClickListener(view -> {

        });

        ll_whatsapp.setOnClickListener(view -> {

        });

    }

    private void setToolbarComponent(String title) {
        ImageView toolbaricon = findViewById(R.id.toolbarIcon);
        ImageView toolbar_search_icon = findViewById(R.id.toolbar_search_icon);
        ImageView toolbar_whatsapp_icon = findViewById(R.id.toolbar_whatsapp_icon);

        if (!title.equals("Home")) {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
            toolbar.setTitle(title);
            toolbaricon.setVisibility(View.GONE);
            toolbar_search_icon.setVisibility(View.GONE);
            toolbar_whatsapp_icon.setVisibility(View.GONE);
        } else {
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            toolbar.setTitle("");
            toolbaricon.setVisibility(View.VISIBLE);
            toolbar_search_icon.setVisibility(View.GONE);
            toolbar_whatsapp_icon.setVisibility(View.VISIBLE);
        }
    }

    public void callFragment(@NonNull Fragment fragment, Bundle bundle, boolean isNeedTransitions) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (bundle != null)
            fragment.setArguments(bundle);

        if (isNeedTransitions) {
            fragmentTransaction.setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.slide_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.fade_out  // popExit
            );
        }

        fragmentTransaction.replace(R.id.screen_area, fragment);
        fragmentTransaction.addToBackStack("back_stack");
        fragmentTransaction.commit();

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
//        setBottomNavigationView("");
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            showBackMenu(false);
        }
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                PreferenceManager.clearHomeData();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        PreferenceManager.clearHomeData();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;

        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_forum_diskusi) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_berita_pengumuman) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_direktori_anggota) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_artikel) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_kalender_kegiatan) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_setting) {
            fragment = new HomeFragment();
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.screen_area, fragment);
            fragmentTransaction.addToBackStack("Home Fragment");

            fragmentTransaction.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferenceManager.clearHomeData();
    }

    //    public String[] getAutoCompletedSupportFragment(){
//        // Initialize the AutocompleteSupportFragment.
//        final String[] address = {""};
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//        assert autocompleteFragment != null;
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i("AutoCompleted", "Place: " + place.getName() + ", " + place.getId());
//                address[0] = place.getId();
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i("AutoCompleted", "An error occurred: " + status);
//            }
//        });
//        return address;
//    }

    private void setDefaultFragment() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.screen_area, fragment);
        fragmentTransaction.commit();

//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
    }

    public void showBackMenu(boolean enable) {
        if (enable) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
            if (!mToolBarNavigationListenerIsRegistered) {
                toggle.setToolbarNavigationClickListener(v -> onBackPressed());
                mToolBarNavigationListenerIsRegistered = true;
            }
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
            mToolBarNavigationListenerIsRegistered = false;
        }
    }

    private void setBottomNavigationView(String navName) {
        switch (navName) {
            case "Home":
                imgNavHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_home_green_24));
                tvNavHome.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_green));
                imgNavFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_favorite_border_grey_24));
                tvNavFav.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavPesanan.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_shopping_cart_24));
                tvNavPesanan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_person_24));
                tvNavAccount.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                btnPilihCabang.setVisibility(View.VISIBLE);
                logo_koperasi.setVisibility(View.VISIBLE);
                layoutToolbarAccount.setVisibility(View.GONE);
                isHideToolbarAccount = true;
                break;
            case "Favorit":
                imgNavHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_home_24));
                tvNavHome.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_favorite_border_green_24));
                tvNavFav.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_green));
                imgNavPesanan.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_shopping_cart_24));
                tvNavPesanan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_person_24));
                tvNavAccount.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                btnPilihCabang.setVisibility(View.GONE);

                break;
            case "Pesanan":
                imgNavHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_home_24));
                tvNavHome.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_favorite_border_grey_24));
                tvNavFav.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavPesanan.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_shopping_cart_green_24));
                tvNavPesanan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_green));
                imgNavAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_person_24));
                tvNavAccount.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                btnPilihCabang.setVisibility(View.GONE);
                layoutToolbarAccount.setVisibility(View.GONE);
                break;
            case "Akun":
                imgNavHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_home_24));
                tvNavHome.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_favorite_border_grey_24));
                tvNavFav.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavPesanan.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_shopping_cart_24));
                tvNavPesanan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_person_24_green));
                tvNavAccount.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_green));
                btnPilihCabang.setVisibility(View.GONE);
                logo_koperasi.setVisibility(View.GONE);
                layoutToolbarAccount.setVisibility(View.VISIBLE);
                isHideToolbarAccount = false;
                break;
            default:
                imgNavHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_home_24));
                tvNavHome.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavFav.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_favorite_border_grey_24));
                tvNavFav.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavPesanan.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_shopping_cart_24));
                tvNavPesanan.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                imgNavAccount.setImageDrawable(getResources().getDrawable(R.drawable.ic_outline_person_24));
                tvNavAccount.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.NormalColorText));
                btnPilihCabang.setVisibility(View.GONE);
                break;
        }
    }

    private void initPrefrences() {
        new PreferenceManager(this);
        new ApiCore(this);
        new ApiAjax(this);
    }

    @OnClick(R.id.navHome)
    void onClickNavHome() {
//        callFragment(new HomeFragment(), null, isNeedTransition);
        viewPager.setVisibility(View.VISIBLE);
        screen_area.setVisibility(View.GONE);
        viewPager.setCurrentItem(0);
        setToolbarComponent(getResources().getString(R.string.Home));
        setBottomNavigationView(getResources().getString(R.string.Home));
        updateFloatingCart(true);
    }

    @OnClick(R.id.navFav)
    void onClickFavHome() {
        callFragment(new FavouriteFragment(), null, isNeedTransition);
        setToolbarComponent(getResources().getString(R.string.Favorit));
        setBottomNavigationView(getResources().getString(R.string.Favorit));
    }

    @OnClick(R.id.navKeranjang)
    void onClickPesananHome() {
        if (layoutDetailCart.getVisibility() == View.GONE) {
            initCartComponent();
            notifyCartAdapter();
//            MethodUtil.toggleTransitionSlideBottom(v, layoutDetailCart, true);
            layoutDetailCart.setVisibility(View.VISIBLE);
            layoutCart.setVisibility(View.GONE);
        } else {
            layoutCart.setVisibility(View.GONE);
            layoutDetailCart.setVisibility(View.GONE);
//            MethodUtil.toggleTransitionSlideBottom(v, layoutDetailCart, false);
        }
    }

    public void handleCart(boolean isAddItem) {
//        MethodUtil.toggleTransitionSlideBottom(v, layoutDetailCart, false);
        layoutDetailCart.setVisibility(View.GONE);
//        if (isAddItem) MethodUtil.toggleTransitionSlideBottom(v, layoutCart, true);
        if (isAddItem) layoutCart.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.navAccount)
    void onClickAccountHome() {
//        Intent intent = new Intent(this, AccountActivity.class);
//        startActivity(intent);
//        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//        callFragment(new AccountFragment(), null, isNeedTransition);
        viewPager.setCurrentItem(1);
        setToolbarComponent("Akun");
        setBottomNavigationView(getResources().getString(R.string.Akun));
        layoutCart.setVisibility(View.GONE);
        layoutDetailCart.setVisibility(View.GONE);
    }

    @BindView(R.id.toolbar_search_icon)
    public ImageView toolbarSearch;

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        HomeFragment homeFragment = new HomeFragment();
        AccountFragment accountFragment = new AccountFragment();

        adapter.addFragment(homeFragment);
        adapter.addFragment(accountFragment);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cvBandung:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BANDUNG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BANDUNG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BANDUNG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA BANDUNG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BANDUNG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
            case R.id.cvBelitung:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BELITUNG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BELITUNG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BELITUNG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA BELITUNG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BANDUNG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
            case R.id.cvKarawang:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_KARAWANG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_KARAWANG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_KARAWANG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA KARAWANG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_KARAWANG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
            case R.id.cvCilegon:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_CILEGON);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_CILEGON);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_CILEGON);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA CILEGON");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_CILEGON);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
            case R.id.cvBekasi:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BEKASI);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BEKASI);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BEKASI);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA BEKASI");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BEKASI);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
            case R.id.cvTangerang:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_TANGERANG);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_TANGERANG);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_TANGERANG);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA TANGERANG");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_TANGERANG);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
            case R.id.cvCirebon:
                PreferenceManager.setFirstOpenFalse();
                PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_CIREBON);
                PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_CIREBON);
                PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_CIREBON);
                PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
                PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
                PreferenceManager.setNamaCabang("KOTA CIREBON");
                PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_CIREBON);
                PreferenceManager.clearHomeData();
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
                dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
                FirebaseAuth.getInstance().signOut();
                doRestart();
                break;
        }
    }

    private void setBekasiManual(){
        PreferenceManager.setIsKoperasi(true);
        PreferenceManager.setFirstOpenFalse();
        PreferenceManager.setBaseUrl(ApiInterface.BASE_URL_SELADA_SEGAR_BEKASI);
        PreferenceManager.setBaseUrlCreateDevice(ApiInterface.BASE_URL_CREATE_DEVICE_BEKASI);
        PreferenceManager.setBaseUrlSlider(ApiInterface.BASE_URL_SLIDER_SELADA_SEGAR_BEKASI);
        PreferenceManager.setConsumerKey(ApiInterface.consumer_key_seladasegarbandung);
        PreferenceManager.setConsumerSecret(ApiInterface.consumer_secret_seladasegarbandung);
        PreferenceManager.setNamaCabang("KOTA BEKASI");
        PreferenceManager.setPhoneSeladaSegar(Constant.PHONE_SELADA_SEGAR_BEKASI);
        PreferenceManager.clearHomeData();
        dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
        dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_HISTORY);
        FirebaseAuth.getInstance().signOut();
        doRestart();
    }

    public void doRestart() {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        startActivity(intent);
    }

    public void setFeedAdapter(HomeFeedAdapter homeFeed, HeaderAdapter adapter) {
        headerAdapter = adapter;
        try {
            homeFeedAdapter = homeFeed;
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void notifyFeedAdapter() {
        headerAdapter.notifyDataSetChanged();
        headerAdapter.syncItemCart();
//        homeFeedAdapter.notifyDataSetChanged();
    }

    public void hideCheckoutButton() {
        layoutCart.setVisibility(View.GONE);
    }

    public void notifyCartAdapter() {
        if (homeCartAdapter != null) {
            homeCartAdapter.notifyDataSetChanged();
        }
    }

    private void checkInAppUpdates() {
        appUpdateManager = AppUpdateManagerFactory.create(context);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//                    && appUpdateInfo.updatePriority() >= HIGH_PRIORITY_UPDATE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });

//        InstallStateUpdatedListener listener = state -> {
//            if (state.installStatus() == InstallStatus.DOWNLOADING) {
//                long bytesDownloaded = state.bytesDownloaded();
//                long totalBytesToDownload = state.totalBytesToDownload();
//                // Implement progress bar.
//                int bytesDownloadedint = (int) bytesDownloaded;
//                int totalint = (int) totalBytesToDownload;
//                progressBar.setVisibility(View.VISIBLE);
//                while(bytesDownloadedint<totalint){
//                    try{
//                            bytesDownloadedint = (int) state.bytesDownloaded();
//                            Thread.sleep(1000);
//                            long pos=1000L * bytesDownloadedint/totalint;
//                            Log.d("thread pos", pos+"");
//                            progressBar.setProgress((int) pos);
//                    }
//                    catch (Exception e) {
//                    }
//                }
//            }
//        };
//
//        appUpdateManager.registerListener(listener);
//        appUpdateManager.unregisterListener(listener);
    }

    /* Displays the snackbar notification and call to actsdqdsdasdasdn. */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar =
                Snackbar.make(
                        findViewById(R.id.layout),
                        "Update aplikasi berhasil.",
                        Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("MULAI ULANG", view -> appUpdateManager.completeUpdate());
        snackbar.setActionTextColor(
                getResources().getColor(R.color.colorAccent));
        snackbar.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                System.out.println("Update flow failed! Result code: " + resultCode);
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
}