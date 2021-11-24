package com.selada.seladasegar.presentation.checkout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.Carts;
import com.selada.seladasegar.models.response.orders.Billing;
import com.selada.seladasegar.models.response.orders.FeeLine;
import com.selada.seladasegar.models.response.orders.LineItem;
import com.selada.seladasegar.models.response.orders.OrderResponse;
import com.selada.seladasegar.models.response.orders.Shipping;
import com.selada.seladasegar.services.DBManager;
import com.selada.seladasegar.services.SQLDatabaseHelper;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.MethodUtil;
import com.selada.seladasegar.util.PreferenceManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AfterCheckoutActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvItemCheckout)
    RecyclerView rvItemCheckout;
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.tvOrderId)
    TextView tvOrderId;
    @BindView(R.id.tvOrderDate)
    TextView tvOrderDate;
    @BindView(R.id.tvOrderEmail)
    TextView tvOrderEmail;
    @BindView(R.id.tvSubTotal)
    TextView tvSubTotal;
    @BindView(R.id.tvOngkir)
    TextView tvOngkir;
    @BindView(R.id.tvKodeUnik)
    TextView tvKodeUnik;
    @BindView(R.id.tvMetodeBayar)
    TextView tvMetodeBayar;
    @BindView(R.id.tvPaymentMethod)
    TextView tvPaymentMethod;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.tvOrderTotal)
    TextView tvOrderTotal;
    @BindView(R.id.tv_fee)
    TextView tv_fee;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvNote)
    TextView tvNote;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvPaymentMethodDesc)
    TextView tvPaymentMethodDesc;
    @BindView(R.id.contentDataTransfer)
    TextView contentDataTransfer;
    @BindView(R.id.tvStatusOrder)
    TextView tvStatusOrder;
    @BindView(R.id.tvTitlePromo)
    TextView tvTitlePromo;
    @BindView(R.id.tvKodePromo)
    TextView tvKodePromo;

    @BindView(R.id.layout_koperasi)
    LinearLayout layout_koperasi;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.frameKonfirmasiPesanan)
    FrameLayout frameKonfirmasiPesanan;
    @BindView(R.id.layoutTransfer)
    LinearLayout layoutTransfer;
    @BindView(R.id.layoutQris)
    LinearLayout layoutQris;
    @BindView(R.id.layoutStatus)
    LinearLayout layoutStatus;
    @BindView(R.id.scrollView)
    androidx.core.widget.NestedScrollView nestedScrollView;
    @BindView(R.id.btnKembali)
    FrameLayout btnKembali;

    @BindView(R.id.frameHeader)
    FrameLayout frameHeader;
    @BindView(R.id.tvTitleRincianBank)
    TextView tvTitleRincianBank;
    @BindView(R.id.tvPelajariQris)
    TextView tvPelajariQris;

    @BindView(R.id.shimmer_view_container)
    ShimmerFrameLayout mShimmerViewContainer;
    @BindView(R.id.shimmer_view_container_1)
    ShimmerFrameLayout mShimmerViewContainer_1;
    @BindView(R.id.shimmer_view_container_2)
    ShimmerFrameLayout mShimmerViewContainer_2;
    @BindView(R.id.shimmer_view_container_3)
    ShimmerFrameLayout mShimmerViewContainer_3;
    @BindView(R.id.shimmer_view_container_4)
    ShimmerFrameLayout mShimmerViewContainer_4;
    @BindView(R.id.shimmer_view_container_5)
    ShimmerFrameLayout mShimmerViewContainer_5;

    @OnClick(R.id.frameDownloadImage)
    void onClickframeDownloadImage(){
        downloadImage();
    }

    @OnClick(R.id.tvPelajariQris)
    void onClickPelajarQris(){
        Dialog dialog = MethodUtil.getDialogCart(R.layout.dialog_pelajari_qris, context);
        ImageView imgClose = dialog.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(view -> dialog.dismiss());
    }

    @OnClick(R.id.btnKembali)
    void onClickBtnKembali(){
        onBackPressed();
    }

//    @OnClick(R.id.tvSalin)
//    void onClickSalin(){
//        String accountNumber = tvAccountNumber.getText().toString();
//        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData clip = ClipData.newPlainText("Text Copied", accountNumber);
//        Objects.requireNonNull(clipboard).setPrimaryClip(clip);
//
//        Toast.makeText(getApplicationContext(), "Nomor rekening telah disalin", Toast.LENGTH_SHORT).show();
//    }

    @OnClick(R.id.frameKonfirmasiPesanan)
    void onClickframeKonfirmasiPesanan() {
        directToWhatsapp();
    }

    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack() {
        onBackPressed();
    }

    private Context context;
    private boolean isFromRiwayat = false;
    private DBManager dbManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_checkout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar_title.setText("Detail Pesanan");
        context = this;
        dbManager = new DBManager(this);
        dbManager.open();
        dbManager.fetch();
        dbManager.fetchHistory();
        new PreferenceManager(this);
        initComponent();
    }

    @SuppressLint("SetTextI18n")
    private void initComponent() {
        rvItemCheckout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (getIntent() != null) {
            String orderId = getIntent().getStringExtra(Constant.INTENT_ORDER_ID);
            isFromRiwayat = getIntent().getBooleanExtra(Constant.INTENT_IS_FROM_RIWAYAT, false);

            if (!isFromRiwayat) {
                btnKembali.setVisibility(View.VISIBLE);
                getDetailOrder(orderId);
            } else {
                setDetailOrderFromRiwayat();
            }
        }

        if (PreferenceManager.getIsKoperasi()){
            layout_koperasi.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setDetailOrderFromRiwayat() {
        rvItemCheckout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (getIntent() != null) {
            String orderId = getIntent().getStringExtra(Constant.INTENT_ORDER_ID);
            String orderEmail = getIntent().getStringExtra(Constant.INTENT_ORDER_EMAIL);
            String orderDate =  getIntent().getStringExtra(Constant.INTENT_ORDER_DATE);
            assert orderDate != null;
            String[] date = orderDate.split("T");
            LineItem[] shippingItem = new Gson().fromJson(getIntent().getStringExtra(Constant.INTENT_ORDER_LINE_ITEMS), LineItem[].class);
            Shipping shippingAddress = new Gson().fromJson(getIntent().getStringExtra(Constant.INTENT_ORDER_SHIPPING), Shipping.class);
            Billing billing = new Gson().fromJson(getIntent().getStringExtra(Constant.INTENT_ORDER_BILLING), Billing.class);
            FeeLine[] feeLineList = new Gson().fromJson(getIntent().getStringExtra(Constant.INTENT_ORDER_FEE_LINE), FeeLine[].class);
            String orderTotal = getIntent().getStringExtra(Constant.INTENT_ORDER_TOTAL);
//            String orderUniqueCode = feeLineList != null ? feeLineList[0].getTotal() : "0";
            String orderPaymentMethod = getIntent().getStringExtra(Constant.INTENT_ORDER_PAYMENT_METHOD);
            String orderCustomerNote = getIntent().getStringExtra(Constant.INTENT_ORDER_CUSTOMER_NOTE);
            String fullname = shippingAddress.getFirstName() + " " + shippingAddress.getLastName();
            String address = shippingAddress.getAddress1();
            String phone = billing.getPhone();
            String status = getIntent().getStringExtra(Constant.INTENT_ORDER_STATUS);
            String couponTitle = getIntent().getStringExtra(Constant.INTENT_COUPON_TITLE);
            String couponAmount = getIntent().getStringExtra(Constant.INTENT_COUPON_AMOUNT);

            Log.d("couponAmount", Objects.requireNonNull(couponAmount));

            List<Carts> cartsList = new ArrayList<>();
            for (LineItem lineItem: shippingItem) {
                int quantity = lineItem.getQuantity();
                int subTotal = Integer.parseInt(lineItem.getSubtotal());
                Integer price = subTotal/quantity;
                Carts carts = new Carts();
                carts.itemId = String.valueOf(lineItem.getId());
                carts.itemName = lineItem.getName();
                carts.itemPrice = String.valueOf(price);
                carts.itemQty = String.valueOf(lineItem.getQuantity());
                carts.itemTotalPrice = lineItem.getSubtotal();
                cartsList.add(carts);
            }

            List<FeeLine> lineList = new ArrayList<>();
            for (FeeLine line: feeLineList){
                FeeLine feeLine = new FeeLine();
                feeLine.setId(line.getId());
                feeLine.setAmount(line.getAmount());
                feeLine.setTotal(line.getTotal());
                feeLine.setName(line.getName());
                lineList.add(feeLine);
            }

            tvOrderId.setText(orderId);
            tvOrderDate.setText(date[0]);
            tvOrderEmail.setText(orderEmail);

            //Detail Pesanan
            CheckoutAdapter checkoutAdapter = new CheckoutAdapter(cartsList, context, AfterCheckoutActivity.this);
            rvItemCheckout.setAdapter(checkoutAdapter);
            rvItemCheckout.scheduleLayoutAnimation();

            int subTotal = 0;
            for (Carts carts: cartsList) {
                subTotal += Integer.parseInt(carts.itemTotalPrice);
            }

            if (PreferenceManager.getIsKoperasi()){
                String amount = lineList.size()>0?lineList.get(0).getAmount():"0";
                tv_fee.setText("Rp "+MethodUtil.toCurrencyFormat(amount));
            }

            tvSubTotal.setText("Rp "+ MethodUtil.toCurrencyFormat(String.valueOf(subTotal)));
            String ongkosKirim = subTotal >= 30000? "Gratis Ongkir": MethodUtil.toCurrencyFormat("10000");
            tvOngkir.setText(ongkosKirim);
            tvTitlePromo.setText(couponTitle);
            tvKodePromo.setText(couponAmount);
            tvMetodeBayar.setText(orderPaymentMethod);
            tvPaymentMethod.setText(orderPaymentMethod);
            tvTotal.setText("Rp " + MethodUtil.toCurrencyFormat(orderTotal));
            tvOrderTotal.setText("Rp "+MethodUtil.toCurrencyFormat(orderTotal));

            //Detail Pengiriman
            tvName.setText(fullname);
            tvAddress.setText(address);
            tvNote.setText(orderCustomerNote);
            tvPhone.setText(phone);
            tvStatusOrder.setText(status);
            layoutStatus.setVisibility(View.VISIBLE);

            switch (status){
                case Constant.ON_HOLD_STATUS:
                    frameHeader.setVisibility(View.VISIBLE);
                    layoutTransfer.setVisibility(View.VISIBLE);
                    tvTitleRincianBank.setVisibility(View.VISIBLE);
                    break;
                case Constant.PROCCESSING_STATUS:
                    frameHeader.setVisibility(View.VISIBLE);
                    layoutTransfer.setVisibility(View.GONE);
                    tvTitleRincianBank.setVisibility(View.GONE);
                default:
                    frameHeader.setVisibility(View.GONE);
                    layoutTransfer.setVisibility(View.GONE);
                    tvTitleRincianBank.setVisibility(View.GONE);
            }

            if (Objects.requireNonNull(orderPaymentMethod).contains("COD")) {
                tvPaymentMethodDesc.setVisibility(View.VISIBLE);
            } else if(Objects.requireNonNull(orderPaymentMethod).contains("Transfer Bank")) {
                layoutTransfer.setVisibility(View.VISIBLE);
                contentDataTransfer.setText(Html.fromHtml(Constant.CONTENT_BANK));
                webView.loadDataWithBaseURL(null, getString(R.string.content_bank), "text/html", "utf-8", null);
            } else {
                tvTitleRincianBank.setText("QRIS");
                layoutTransfer.setVisibility(View.GONE);
                layoutQris.setVisibility(View.VISIBLE);
                tvPelajariQris.setText("Pelajari tentang QRIS " + Html.fromHtml("<u>disini</u>"));
            }
        }


        //setLoadingFalse
        setLoadingStatus(false);
    }

    @SuppressLint("SetTextI18n")
    private void initComponentFromServer(OrderResponse orderResponse) {
        rvItemCheckout.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        if (getIntent() != null) {
            String orderId = String.valueOf(orderResponse.getId());
            String orderEmail = orderResponse.getBilling().getEmail();
            String orderDate = orderResponse.getDateCreated();
            assert orderDate != null;
            String[] date = orderDate.split("T");
            List<LineItem> shippingItem = orderResponse.getLineItems();
            Shipping shippingAddress = orderResponse.getShipping();
            Billing billing = orderResponse.getBilling();
            List<FeeLine> feeLineList = orderResponse.getFeeLines();
            String orderTotal = orderResponse.getTotal();
//            String orderUniqueCode = feeLineList != null ? feeLineList.get(0).getTotal() : "0";
            String orderPaymentMethod = orderResponse.getPaymentMethodTitle();
            String orderCustomerNote = orderResponse.getCustomerNote();
            String fullname = shippingAddress.getFirstName() + " " + shippingAddress.getLastName();
            String address = shippingAddress.getAddress1();
            String phone = billing.getPhone();
            String status = orderResponse.getStatus();
            String couponTitle = "Promo";
            String couponAmount =  "-";
            if (orderResponse.getCoupon_lines()!=null){
                if (orderResponse.getCoupon_lines().size()>0){
                    couponTitle = "Promo "+orderResponse.getCoupon_lines().get(0).getCode().toUpperCase();
                    couponAmount = "-Rp "+ MethodUtil.toCurrencyFormat(orderResponse.getCoupon_lines().get(0).getDiscount());
                }
            }

            List<Carts> cartsList = new ArrayList<>();
            for (LineItem lineItem: shippingItem) {
                int quantity = lineItem.getQuantity();
                int subTotal = Integer.parseInt(lineItem.getSubtotal());
                Integer price = subTotal/quantity;
                Carts carts = new Carts();
                carts.itemId = String.valueOf(lineItem.getId());
                carts.itemName = lineItem.getName();
                carts.itemPrice = String.valueOf(price);
                carts.itemQty = String.valueOf(lineItem.getQuantity());
                carts.itemTotalPrice = lineItem.getSubtotal();
                cartsList.add(carts);
            }

            tvOrderId.setText(orderId);
            tvOrderDate.setText(date[0]);
            tvOrderEmail.setText(orderEmail);

            //Detail Pesanan
            CheckoutAdapter checkoutAdapter = new CheckoutAdapter(cartsList, context, AfterCheckoutActivity.this);
            rvItemCheckout.setAdapter(checkoutAdapter);
            rvItemCheckout.scheduleLayoutAnimation();

            int subTotal = 0;
            for (Carts carts: cartsList) {
                subTotal += Integer.parseInt(carts.itemTotalPrice);
            }

            if (PreferenceManager.getIsKoperasi()){
                String amount = feeLineList.size()>0?feeLineList.get(0).getAmount():"0";
                tv_fee.setText("Rp "+MethodUtil.toCurrencyFormat(amount));
            }

            tvSubTotal.setText("Rp "+ MethodUtil.toCurrencyFormat(String.valueOf(subTotal)));
            String ongkosKirim = subTotal > 0? "Gratis Ongkir": MethodUtil.toCurrencyFormat("10000");
            tvOngkir.setText(ongkosKirim);
            tvTitlePromo.setText(couponTitle);
            tvKodePromo.setText(couponAmount);
            tvMetodeBayar.setText(orderPaymentMethod);
            tvPaymentMethod.setText(orderPaymentMethod);
            tvOrderTotal.setText("Rp "+MethodUtil.toCurrencyFormat(orderTotal));
            tvTotal.setText("Rp " + MethodUtil.toCurrencyFormat(orderTotal));

            //Detail Pengiriman
            tvName.setText(fullname);
            tvAddress.setText(address);
            tvNote.setText(orderCustomerNote);
            tvPhone.setText(phone);
            tvStatusOrder.setText(status);
            layoutStatus.setVisibility(View.VISIBLE);

            switch (status){
                case Constant.ON_HOLD_STATUS:
                    frameHeader.setVisibility(View.VISIBLE);
                    layoutTransfer.setVisibility(View.VISIBLE);
                    tvTitleRincianBank.setVisibility(View.VISIBLE);
                    break;
                case Constant.PROCCESSING_STATUS:
                    frameHeader.setVisibility(View.VISIBLE);
                    layoutTransfer.setVisibility(View.GONE);
                    tvTitleRincianBank.setVisibility(View.GONE);
                default:
                    frameHeader.setVisibility(View.GONE);
                    layoutTransfer.setVisibility(View.GONE);
                    tvTitleRincianBank.setVisibility(View.GONE);
            }

            if (Objects.requireNonNull(orderPaymentMethod).contains("COD")) {
                tvPaymentMethodDesc.setVisibility(View.VISIBLE);
            } else if(Objects.requireNonNull(orderPaymentMethod).contains("Transfer Bank")) {
                layoutTransfer.setVisibility(View.VISIBLE);
                contentDataTransfer.setText(Html.fromHtml(Constant.CONTENT_BANK));
                webView.loadDataWithBaseURL(null, getString(R.string.content_bank), "text/html", "utf-8", null);
            } else {
                tvTitleRincianBank.setText("QRIS");
                layoutTransfer.setVisibility(View.GONE);
                layoutQris.setVisibility(View.VISIBLE);
                tvPelajariQris.setText(Html.fromHtml("Pelajari tentang QRIS <u>disini</u>"));
            }
        }
    }

    private void getDetailOrder(String orderId) {
        nestedScrollView.setEnabled(false);
        setLoadingStatus(true);
        ApiCore.apiInterface().getDetailOrder(orderId, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                setLoadingStatus(false);
                nestedScrollView.setEnabled(true);
                try {
                    OrderResponse orderResponse = response.body();
                    initComponentFromServer(orderResponse);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                setLoadingStatus(false);
                t.printStackTrace();
            }
        });
    }

    private void setLoadingStatus(boolean isLoading){
        if (isLoading) {
            mShimmerViewContainer.setVisibility(View.VISIBLE);
            mShimmerViewContainer.startShimmer();
            mShimmerViewContainer_1.setVisibility(View.VISIBLE);
            mShimmerViewContainer_1.startShimmer();
            mShimmerViewContainer_2.setVisibility(View.VISIBLE);
            mShimmerViewContainer_2.startShimmer();
            mShimmerViewContainer_3.setVisibility(View.VISIBLE);
            mShimmerViewContainer_3.startShimmer();
            mShimmerViewContainer_4.setVisibility(View.VISIBLE);
            mShimmerViewContainer_4.startShimmer();
            mShimmerViewContainer_5.setVisibility(View.VISIBLE);
            mShimmerViewContainer_5.startShimmer();
            tvStatusOrder.setVisibility(View.GONE);
            tvOrderId.setVisibility(View.GONE);
            tvOrderDate.setVisibility(View.GONE);
            tvOrderEmail.setVisibility(View.GONE);
            tvOrderTotal.setVisibility(View.GONE);
            tvPaymentMethod.setVisibility(View.GONE);
        } else {
            mShimmerViewContainer.stopShimmer();
            mShimmerViewContainer.setVisibility(View.GONE);
            mShimmerViewContainer_1.stopShimmer();
            mShimmerViewContainer_1.setVisibility(View.GONE);
            mShimmerViewContainer_2.stopShimmer();
            mShimmerViewContainer_2.setVisibility(View.GONE);
            mShimmerViewContainer_3.stopShimmer();
            mShimmerViewContainer_3.setVisibility(View.GONE);
            mShimmerViewContainer_4.stopShimmer();
            mShimmerViewContainer_4.setVisibility(View.GONE);
            mShimmerViewContainer_5.stopShimmer();
            mShimmerViewContainer_5.setVisibility(View.GONE);
            tvStatusOrder.setVisibility(View.VISIBLE);
            tvOrderId.setVisibility(View.VISIBLE);
            tvOrderDate.setVisibility(View.VISIBLE);
            tvOrderEmail.setVisibility(View.VISIBLE);
            tvOrderTotal.setVisibility(View.VISIBLE);
            tvPaymentMethod.setVisibility(View.VISIBLE);
        }
    }

    private void downloadImage() {
        Toast.makeText(context, "Mengunduh gambar", Toast.LENGTH_SHORT).show();
        String filename = "qris_selada.jpg";
        String DIR_NAME = "SeladaSegar";
        String downloadUrlOfImage = "https://www.seladasegar.com/QR-SELADASEGAR.jpg";
        File direct =
                new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .getAbsolutePath() + "/" + DIR_NAME + "/");


        if (!direct.exists()) {
            direct.mkdir();
            Log.d("DownloadManager", "dir created for first time");
        }

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(downloadUrlOfImage);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("image/jpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                        File.separator + DIR_NAME + File.separator + filename);

        dm.enqueue(request);
    }

    private void directToWhatsapp(){
        String number = PreferenceManager.getPhoneSeladaSegar();
        String msg = "Hallo Selada Segar \nsaya ingin konfirmasi pesanan dengan detail berikut: "+ "\n";
        String orderNo = "Nomor Order: #" +tvOrderId.getText().toString()+ "\n";
        String phone = "Nomor Telepon: "+ tvPhone.getText().toString() + "\n";
        String total = "Total Belanja: "+tvTotal.getText().toString() + "\n";
        String metode = tvMetodeBayar.getText().toString();
        String url = "https://api.whatsapp.com/send?phone="+number;
        try {
            url = "https://api.whatsapp.com/send?phone="+number+"&text=" + URLEncoder.encode(msg+orderNo+phone+total+metode, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if (isFromRiwayat) {
            super.onBackPressed();
        } else {
            dbManager.deleteAllRows(SQLDatabaseHelper.TABLE_NAME);
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}