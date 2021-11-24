package com.selada.seladasegar.presentation.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.selada.seladasegar.BuildConfig;
import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiAjax;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.request.Billing;
import com.selada.seladasegar.models.request.RequestRegister;
import com.selada.seladasegar.models.request.Shipping;
import com.selada.seladasegar.models.response.orders.CouponLines;
import com.selada.seladasegar.models.response.orders.FeeLine;
import com.selada.seladasegar.models.response.orders.LineItem;
import com.selada.seladasegar.models.response.orders.OrderResponse;
import com.selada.seladasegar.models.response.orders.ShippingLine;
import com.selada.seladasegar.models.response.register.ResponseRegister;
import com.selada.seladasegar.presentation.maps.MapsActivity;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.PlacesAutoCompleteAdapter;
import com.selada.seladasegar.services.MyFirebaseMessagingService;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.Loading;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class AccountFragment extends Fragment {

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;
    private int RC_SIGN_IN = 9001;
    private int LAUNCH_SECOND_ACTIVITY = 9002;
    private ResponseRegister responseRegister;
    private String provinceSelected;
    private ViewGroup viewGroup;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private List<OrderResponse> orderResponseList, orderResponseListAll;
    private boolean isLoginFromCheckout;
    private String latlong;

    @BindView(R.id.etFirstName)
    EditText etFirstName;
    @BindView(R.id.etLastName)
    EditText etLastName;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.etEmail)
    EditText etEmail;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etAlamatLengkap)
    EditText etAlamatLengkap;

//    @BindView(R.id.etCityShipping)
//    EditText etCityShipping;
//    @BindView(R.id.etPostcodeShipping)
//    EditText etPostcodeShipping;
//    @BindView(R.id.tvCountry)
//    TextView tvCountry;
//    @BindView(R.id.spinnerProvinceShipping)
//    com.jaredrummler.materialspinner.MaterialSpinner spinnerProvinceShipping;
//    @BindView(R.id.places_recycler_view)
//    RecyclerView places_recycler_view;

    @BindView(R.id.cvLogin)
    CardView cvLogin;
    @BindView(R.id.layoutUbahAkun)
    LinearLayout layoutUbahAkun;
    @BindView(R.id.framePesanan)
    FrameLayout framePesanan;

    @BindView(R.id.rvRiwayat)
    RecyclerView rvRiwayat;
    @BindView(R.id.rvRiwayatSemua)
    RecyclerView rvRiwayatSemua;
    @BindView(R.id.tvNoData)
    TextView tvNoData;
    @BindView(R.id.tvNoDataSemua)
    TextView tvNoDataSemua;
    @BindView(R.id.tvVersion)
    TextView tvVersion;
    @BindView(R.id.tvIdPelanggan)
    TextView tvIdPelanggan;

    @BindView(R.id.framePesananOnHold)
    FrameLayout framePesananOnHold;
    @BindView(R.id.frameSemuaPesanan)
    FrameLayout frameSemuaPesanan;
    @BindView(R.id.shimmer_view_container_feed_onhold)
    ShimmerFrameLayout shimmer_view_container_feed_onhold;
    @BindView(R.id.shimmer_view_container_feed_semua)
    ShimmerFrameLayout shimmer_view_container_feed_semua;
    @BindView(R.id.navOnHold)
    FrameLayout navOnHold;
    @BindView(R.id.navSemua)
    FrameLayout navSemua;
    @BindView(R.id.tvOnHold)
    TextView tvOnHold;
    @BindView(R.id.tvSemua)
    TextView tvSemua;

    @OnClick(R.id.cvSignIn)
    void onClickcvSignIn() {
        Intent intent = new Intent(getActivity(), SignInSignUpActivity.class);
        intent.putExtra(Constant.INTENT_CLASS_NAME, Constant.LOGIN_CLASS);
        startActivity(intent);
    }

    @OnClick(R.id.cvSignUp)
    void onClickcvSignUp() {
        Intent intent = new Intent(getActivity(), SignInSignUpActivity.class);
        intent.putExtra(Constant.INTENT_CLASS_NAME, Constant.REGISTER_CLASS);
        startActivity(intent);
    }

    @OnClick(R.id.tvKeluar)
    void onClickKeluar() {
        signOut();
    }

    @OnClick(R.id.btnTandaiLokasi)
    void onClickbtnTandaiLokasi() {
        Intent i = new Intent(getActivity(), MapsActivity.class);
        startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
    }

//    @OnClick(R.id.signOut)
//    void onClicksignOut() {
//        signOut();
//    }

    @OnClick(R.id.cvGoogleLogin)
    void onClickGoogleLogin() {
        signIn();
    }

    @OnClick(R.id.btnSimpan)
    void onClickSimpan() {
        proccessSimpanAkun();
    }

    @OnClick(R.id.navOnHold)
    void onClickPesananOnHold() {
        setNavigation(R.id.navOnHold);
    }

    @OnClick(R.id.navSemua)
    void onClickSemuaPesanan() {
        setNavigation(R.id.navSemua);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            PreferenceManager.setFirebaseUser(true);
            responseRegister = PreferenceManager.getLoginData();
            orderResponseList = new ArrayList<>();
            orderResponseListAll = new ArrayList<>();
            cvLogin.setVisibility(View.GONE);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            rvRiwayat.setLayoutManager(layoutManager);
            rvRiwayatSemua.setLayoutManager(layoutManager1);
            tvVersion.setText("Versi " + BuildConfig.VERSION_NAME);
            onClickFrameInfoAkun();
            populateDataPesanan();
//            initComponent();
            if (!((MainActivity) Objects.requireNonNull(getActivity())).isHideToolbarAccount) {
                ((MainActivity) Objects.requireNonNull(getActivity())).layoutToolbarAccount.setVisibility(View.VISIBLE);
            }
            ((MainActivity) Objects.requireNonNull(getActivity())).frameInfoAkun.setOnClickListener(view -> onClickFrameInfoAkun());
            ((MainActivity) Objects.requireNonNull(getActivity())).toolbarTitlePesanan.setOnClickListener(view -> onClickToolbarPesanan());
        } else {
            if (PreferenceManager.isFirebaseUser()) {
                responseRegister = PreferenceManager.getLoginData();
                orderResponseList = new ArrayList<>();
                orderResponseListAll = new ArrayList<>();
                cvLogin.setVisibility(View.GONE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                rvRiwayat.setLayoutManager(layoutManager);
                rvRiwayatSemua.setLayoutManager(layoutManager1);
                tvVersion.setText("Versi " + BuildConfig.VERSION_NAME);
                onClickFrameInfoAkun();
                populateDataPesanan();
//            initComponent();
                if (!((MainActivity) Objects.requireNonNull(getActivity())).isHideToolbarAccount) {
                    ((MainActivity) Objects.requireNonNull(getActivity())).layoutToolbarAccount.setVisibility(View.VISIBLE);
                }
                ((MainActivity) Objects.requireNonNull(getActivity())).frameInfoAkun.setOnClickListener(view -> onClickFrameInfoAkun());
                ((MainActivity) Objects.requireNonNull(getActivity())).toolbarTitlePesanan.setOnClickListener(view -> onClickToolbarPesanan());
            } else {
                PreferenceManager.setFirebaseUser(false);
                framePesanan.setVisibility(View.GONE);
                layoutUbahAkun.setVisibility(View.GONE);
                cvLogin.setVisibility(View.VISIBLE);
                ((MainActivity) requireActivity()).layoutToolbarAccount.setVisibility(View.GONE);
            }
        }
        String currentUserJson = new Gson().toJson(currentUser);
        Log.d("CURRENT USER JSON", currentUserJson);
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = container;
        return inflater.inflate(R.layout.fragment_account, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        createRequest();
        mAuth = FirebaseAuth.getInstance();
        responseRegister = PreferenceManager.getLoginData();
        populateDataInformasiAkun();
        getIntentFromCheckout();
    }

    private void getIntentFromCheckout() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            isLoginFromCheckout = bundle.getBoolean("isLoginFromCheckout", false);
        }
    }

    private void initComponent() {
//        Places.initialize(Objects.requireNonNull(getActivity()), getResources().getString(R.string.google_maps_key));
//        etAlamatShipping.addTextChangedListener(filterTextWatcher);
//        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(getActivity());
//        places_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
//        mAutoCompleteAdapter.setClickListener(place -> etAlamatShipping.setText(place.getAddress()));
//        places_recycler_view.setAdapter(mAutoCompleteAdapter);
//        mAutoCompleteAdapter.notifyDataSetChanged();
    }

    private void setNavigation(int id) {
        switch (id) {
            case R.id.navOnHold:
                framePesananOnHold.setVisibility(View.VISIBLE);
                frameSemuaPesanan.setVisibility(View.GONE);
//                MethodUtil.toggleTransitionSlideStart(viewGroup, framePesananOnHold, true);
//                MethodUtil.toggleTransitionSlideStart(viewGroup, frameSemuaPesanan, false);
                navOnHold.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.shado));
                navSemua.setBackgroundColor(getContext().getResources().getColor(R.color.white));
                tvOnHold.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.light_green));
                tvSemua.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.NormalColorText));
                break;
            case R.id.navSemua:
                frameSemuaPesanan.setVisibility(View.VISIBLE);
                framePesananOnHold.setVisibility(View.GONE);
//                MethodUtil.toggleTransitionSlideEnd(viewGroup, frameSemuaPesanan, true);
//                MethodUtil.toggleTransitionSlideEnd(viewGroup, framePesananOnHold, false);
                navOnHold.setBackgroundColor(Objects.requireNonNull(getContext()).getResources().getColor(R.color.white));
                navSemua.setBackgroundColor(getContext().getResources().getColor(R.color.shado));
                tvOnHold.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.NormalColorText));
                tvSemua.setTextColor(ContextCompat.getColor(Objects.requireNonNull(getActivity()), R.color.light_green));
                break;
        }
    }

    private void onClickFrameInfoAkun() {
        ((MainActivity) Objects.requireNonNull(getActivity())).toolbarTitleInfoAkun.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_green));
        ((MainActivity) Objects.requireNonNull(getActivity())).toolbarTitlePesanan.setTextColor(ContextCompat.getColor(getActivity(), R.color.NormalColorText));
        layoutUbahAkun.setVisibility(View.VISIBLE);
        framePesanan.setVisibility(View.GONE);
//        MethodUtil.toggleTransitionSlideStart(viewGroup, layoutUbahAkun, true);
//        MethodUtil.toggleTransitionSlideStart(viewGroup, framePesanan, false);
    }

    private void onClickToolbarPesanan() {
        ((MainActivity) Objects.requireNonNull(getActivity())).toolbarTitleInfoAkun.setTextColor(ContextCompat.getColor(getActivity(), R.color.NormalColorText));
        ((MainActivity) Objects.requireNonNull(getActivity())).toolbarTitlePesanan.setTextColor(ContextCompat.getColor(getActivity(), R.color.light_green));
        framePesanan.setVisibility(View.VISIBLE);
        layoutUbahAkun.setVisibility(View.GONE);
//        MethodUtil.toggleTransitionSlideEnd(viewGroup, framePesanan, true);
//        MethodUtil.toggleTransitionSlideEnd(viewGroup, layoutUbahAkun, false);
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
//            if (!s.toString().equals("")) {
//                mAutoCompleteAdapter.getFilter().filter(s.toString());
//                if (places_recycler_view.getVisibility() == View.GONE) {places_recycler_view.setVisibility(View.VISIBLE);}
//            } else {
//                if (places_recycler_view.getVisibility() == View.VISIBLE) {places_recycler_view.setVisibility(View.GONE);}
//            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private void proccessSimpanAkun() {
        Shipping shipping = new Shipping();
        shipping.setFirstName(etFirstName.getText().toString());
        shipping.setLastName(etLastName.getText().toString());
        shipping.setAddress1(etAlamatLengkap.getText().toString());
        shipping.setAddress2(latlong);
//        shipping.setCity(etCityShipping.getText().toString());
//        shipping.setCountry(tvCountry.getText().toString());
//        shipping.setPostcode(etPostcodeShipping.getText().toString());
        shipping.setState(provinceSelected);

        Billing billing = new Billing();
        billing.setPhone(etPhone.getText().toString());
        billing.setAddress2(latlong);

        RequestRegister requestUpdateRegister = new RequestRegister();
        requestUpdateRegister.setFirst_name(etFirstName.getText().toString());
        requestUpdateRegister.setLast_name(etLastName.getText().toString());
        requestUpdateRegister.setEmail(etEmail.getText().toString());
        requestUpdateRegister.setUsername(etUsername.getText().toString());
        requestUpdateRegister.setShipping(shipping);
        requestUpdateRegister.setBilling(billing);

        doUpdateCustomer(requestUpdateRegister);
    }

    private void doUpdateCustomer(RequestRegister requestRegister) {
        Loading.show(getContext());
        ApiCore.apiInterface().updateCustomer(String.valueOf(responseRegister.getId()), Constant.METHOD_PUT, requestRegister, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                Loading.hide(getContext());
                try {
                    if (response.isSuccessful()) {
                        ResponseRegister responseRegister = response.body();
                        PreferenceManager.setLoginData(responseRegister);
                        Toast.makeText(getContext(), "Berhasil menyimpan data", Toast.LENGTH_SHORT).show();
                    } else {
                        assert response.errorBody() != null;
                        String msgError = MethodUtil.getResponseError(response.errorBody().string());
                        Toast.makeText(getContext(), msgError, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                Loading.hide(getContext());
                t.printStackTrace();
            }
        });
    }

    private void setSpinText(MaterialSpinner spin, String text) {
        for (int i = 0; i < spin.getItems().size(); i++) {
            if (spin.getItems().get(i).toString().contains(text)) {
                spin.setSelectedIndex(i);
            }
        }
    }

    private void populateDataPesanan() {
        setLoading(true);
        ApiCore.apiInterface().getOrders(PreferenceManager.getLoginData().getId(), PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                setLoading(false);
                try {
                    List<OrderResponse> res = response.body();
                    for (OrderResponse orderResponse : Objects.requireNonNull(res)) {
                        int id = Objects.requireNonNull(orderResponse).getId();
                        List<LineItem> shippingItem = orderResponse.getLineItems();
                        com.selada.seladasegar.models.response.orders.Billing billing = orderResponse.getBilling();
                        List<ShippingLine> shippingLineList = orderResponse.getShippingLines();
                        List<FeeLine> feeLineList = orderResponse.getFeeLines();
                        List<CouponLines> couponLinesList = orderResponse.getCoupon_lines();
                        String createDate = orderResponse.getDateCreated();
                        String total = orderResponse.getTotal();
                        String custNote = orderResponse.getCustomerNote();
                        String paymentMethod = orderResponse.getPaymentMethodTitle();
                        String status = orderResponse.getStatus();

                        if (status.equals(Constant.COMPLETED_STATUS) ||
                                status.equals(Constant.ON_HOLD_STATUS) ||
                                status.equals(Constant.PROCCESSING_STATUS)) {
                            OrderResponse orderRes = new OrderResponse();
                            orderRes.setId(id);
                            orderRes.setBilling(billing);
                            orderRes.setDateCreated(createDate);
                            orderRes.setTotal(total);
                            orderRes.setCustomerNote(custNote);
                            orderRes.setLineItems(shippingItem);
                            orderRes.setFeeLines(feeLineList);
                            orderRes.setShippingLines(shippingLineList);
                            orderRes.setPaymentMethodTitle(paymentMethod);
                            orderRes.setStatus(status);
                            orderRes.setCoupon_lines(couponLinesList);
                            orderResponseListAll.add(orderRes);
                        }

                        if (!status.equals(Constant.COMPLETED_STATUS)) {
                            OrderResponse orderResp = new OrderResponse();
                            orderResp.setId(id);
                            orderResp.setBilling(billing);
                            orderResp.setDateCreated(createDate);
                            orderResp.setTotal(total);
                            orderResp.setCustomerNote(custNote);
                            orderResp.setLineItems(shippingItem);
                            orderResp.setFeeLines(feeLineList);
                            orderResp.setShippingLines(shippingLineList);
                            orderResp.setPaymentMethodTitle(paymentMethod);
                            orderResp.setStatus(status);
                            orderResp.setCoupon_lines(couponLinesList);
                            orderResponseList.add(orderResp);
                        }
                    }

                    RiwayatAdapter riwayatAdapter = new RiwayatAdapter(orderResponseList, getContext(), getActivity());
                    rvRiwayat.setAdapter(riwayatAdapter);
                    rvRiwayat.scheduleLayoutAnimation();

                    RiwayatAdapter riwayatAdapter1 = new RiwayatAdapter(orderResponseListAll, getContext(), getActivity());
                    rvRiwayatSemua.setAdapter(riwayatAdapter1);
                    rvRiwayatSemua.scheduleLayoutAnimation();

                    if (orderResponseList.size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }

                    if (orderResponseListAll.size() > 0) {
                        tvNoDataSemua.setVisibility(View.GONE);
                    } else {
                        tvNoDataSemua.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if (isLoading) {
            shimmer_view_container_feed_onhold.setVisibility(View.VISIBLE);
            shimmer_view_container_feed_onhold.startShimmer();
            shimmer_view_container_feed_semua.startShimmer();
        } else {
            shimmer_view_container_feed_onhold.stopShimmer();
            shimmer_view_container_feed_onhold.setVisibility(View.GONE);
            shimmer_view_container_feed_semua.stopShimmer();
            shimmer_view_container_feed_semua.setVisibility(View.GONE);
        }
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void populateDataInformasiAkun() {
        if (responseRegister != null) {
            String kodeCabang = "000";
            switch (PreferenceManager.getNamaCabang()) {
                case "KOTA BANDUNG":
                    kodeCabang = "001";
                    break;
                case "KOTA BELITUNG":
                    kodeCabang = "002";
                    break;
                case "KOTA KARAWANG":
                    kodeCabang = "003";
                    break;
                case "KOTA BEKASI":
                    kodeCabang = "004";
                    break;
                case "KOTA TANGERANG":
                    kodeCabang = "005";
                    break;
                case "KOTA CILEGON":
                    kodeCabang = "006";
                    break;
                case "KOTA CIREBON":
                    kodeCabang = "007";
                    break;
            }
            tvIdPelanggan.setText(kodeCabang + String.format("%05d", responseRegister.getId()));
            etFirstName.setText(responseRegister.getFirstName());
            etLastName.setText(responseRegister.getLastName());
            etEmail.setText(responseRegister.getEmail());
            etUsername.setText(responseRegister.getUsername());
            etPhone.setText(responseRegister.getBilling().getPhone());
            etAlamatLengkap.setText(responseRegister.getShipping().getAddress1());
//            etCityShipping.setText(responseRegister.getShipping().getCity());
//            if (etCityShipping.getText().toString().equals("null")) etCityShipping.setText("");
//            etPostcodeShipping.setText(responseRegister.getShipping().getPostcode());
//            if (etPostcodeShipping.getText().toString().equals("null"))
//                etPostcodeShipping.setText("");
//            List<String> stringList = new ArrayList<>(Arrays.asList(Constant.LIST_OF_PROVINCE));
//            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, stringList);
//            adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
//            spinnerProvinceShipping.setItems(stringList);
//            spinnerProvinceShipping.setOnItemSelectedListener((view, position, id, item) -> provinceSelected = item.toString());
//            setSpinText(spinnerProvinceShipping, responseRegister.getShipping().getState());
        }
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Objects.requireNonNull(getActivity()), gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                ((MainActivity) Objects.requireNonNull(getActivity())).layoutCart.setVisibility(View.GONE);
                Bundle bundle = data.getBundleExtra(Constant.INTENT_BUNDLE);
                assert bundle != null;
                String city = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_CITY));
                String address = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_ADDRESS));
                String state = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_STATE));
                String country = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_COUNTRY));
                String postalCode = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_POSTAL_CODE));
                String knownName = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_KNOWN_NAME));
                latlong = Objects.requireNonNull(bundle.getString(Constant.BUNDLE_LATLONG));
                etAlamatLengkap.setText(address);
//                etPostcodeShipping.setText(postalCode);
//                etCityShipping.setText(city);
//                tvCountry.setText(country);
//                provinceSelected = state;
                System.out.println("LATLONG: " + latlong + " - " + city + " " + state + " " + country + " " + postalCode + " " + knownName);
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(Objects.requireNonNull(account).getIdToken());
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(Objects.requireNonNull(getContext()));
                        if (signInAccount != null) {
                            populateData(Objects.requireNonNull(user));
                        }
                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    private void populateData(FirebaseUser user) {
        String[] name = Objects.requireNonNull(user.getDisplayName()).split(" ");
        String[] username = Objects.requireNonNull(user.getEmail()).split("@");

        Billing billing = new Billing();
        billing.setFirstName(name[0]);
        billing.setLastName(name[1]);
        billing.setEmail(user.getEmail());
        billing.setPhone(user.getPhoneNumber());

        Shipping shipping = new Shipping();
        shipping.setFirstName(name[0]);
        shipping.setLastName(name[1]);

        RequestRegister requestRegister = new RequestRegister();
        requestRegister.setEmail(user.getEmail());
        requestRegister.setFirst_name(name[0]);
        requestRegister.setLast_name(name[1]);
        requestRegister.setUsername(username[0]);
        requestRegister.setBilling(billing);
        requestRegister.setShipping(shipping);

        doRegister(requestRegister);
    }

    private void doRegister(RequestRegister requestRegister) {
        dialog = ProgressDialog.show(getActivity(), "",
                "Memproses Login Anda...", true);
        ApiCore.apiInterface().doRegister(requestRegister, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                try {
                    if (response.isSuccessful()) {
                        String fcmDeviceToken = MyFirebaseMessagingService.getToken(Objects.requireNonNull(getContext()));
                        Log.d("fcmDeviceToken", fcmDeviceToken);
                        ResponseRegister register = response.body();
                        if (register != null) {
                            PreferenceManager.setLoginData(register);
                            sendDeviceId(String.valueOf(register.getId()), fcmDeviceToken);
                        }
                    } else {
                        findAccount(requestRegister.getEmail());
                        String msg = MethodUtil.getErrorBody(Objects.requireNonNull(response.errorBody()).toString());
                        if (msg.contains("account is already")) {
                            findAccount(requestRegister.getEmail());
                        }
                    }
                } catch (Exception e) {
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseRegister> call, Throwable t) {
                dialog.dismiss();
                t.printStackTrace();
            }
        });
    }

    private void findAccount(String email) {
        ApiCore.apiInterface().findAccount(email, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<ResponseRegister>>() {
            @Override
            public void onResponse(Call<List<ResponseRegister>> call, Response<List<ResponseRegister>> response) {
                try {
                    dialog.dismiss();
                    String fcmDeviceToken = MyFirebaseMessagingService.getToken(Objects.requireNonNull(getContext()));
                    List<ResponseRegister> res = response.body();
                    assert res != null;
                    if (res.size() > 0) {
                        PreferenceManager.setLoginData(res.get(0));
                        String id = String.valueOf(Objects.requireNonNull(res).get(0).getId());
                        sendDeviceId(id, fcmDeviceToken);
                        Log.d("findAccount", "ON");
                    }
                } catch (Exception e) {
                    Log.d("findAccount", "Exception");
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ResponseRegister>> call, Throwable t) {
                Log.d("findAccount", "Failed");
                t.printStackTrace();
                dialog.dismiss();
            }
        });
    }

    private void sendDeviceId(String userId, String deviceId) {
        dialog = ProgressDialog.show(getActivity(), "",
                "Mohon tunggu...", true);
        ApiAjax.apiInterface().sendDeviceId(userId, deviceId, Constant.ACTION_SEND_DEVICE).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        dialog = ProgressDialog.show(getActivity(), "",
                                "Mohon tunggu...", true);
                        Toast.makeText(getActivity(), "Login anda berhasil", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        if (isLoginFromCheckout) {
                            intent.putExtra("isGoToCheckout", true);
                        } else {
                            intent.putExtra("isFromAccount", true);
                        }
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void signOut() {
        MethodUtil.getLoadingBar(getContext(), "Keluar...");
        FirebaseAuth.getInstance().signOut();
        PreferenceManager.logOut();
        DBManager dbManager = new DBManager(getContext());
        dbManager.open();
        dbManager.fetch();
        dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("isFromAccount", true);
        startActivity(intent);
        Toast.makeText(getActivity(), "Anda telah keluar", Toast.LENGTH_SHORT).show();
        //Intent to out screen
    }
}
