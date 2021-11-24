package com.selada.seladasegar.presentation.account;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.selada.seladasegar.MainActivity;
import com.selada.seladasegar.R;
import com.selada.seladasegar.api.ApiAjax;
import com.selada.seladasegar.api.ApiCore;
import com.selada.seladasegar.models.request.Billing;
import com.selada.seladasegar.models.request.RequestRegister;
import com.selada.seladasegar.models.request.Shipping;
import com.selada.seladasegar.models.response.register.ResponseRegister;
import com.selada.seladasegar.services.MyFirebaseMessagingService;
import com.selada.seladasegar.util.Constant;
import com.selada.seladasegar.util.PreferenceManager;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInSignUpActivity extends AppCompatActivity {

    @BindView(R.id.etEmailLogin)
    EditText etEmailLogin;
    @BindView(R.id.etPhoneLogin)
    EditText etPhoneLogin;

    @BindView(R.id.etEmailRegister)
    EditText etEmailRegister;
    @BindView(R.id.etPhoneRegister)
    EditText etPhoneRegister;
    @BindView(R.id.etFirstNameRegister)
    EditText etFirstNameRegister;
    @BindView(R.id.etLastNameRegister)
    EditText etLastNameRegister;

    @BindView(R.id.layout_register)
    LinearLayout layout_register;
    @BindView(R.id.layout_login)
    LinearLayout layout_login;

    private ProgressDialog dialog;
    private Context context;

    @OnClick(R.id.btnSimpanRegister)
    void onClickbtnSimpanRegister(){
        if (etEmailRegister.getText().toString().equals("") || etPhoneRegister.getText().toString().equals("") ||
                etFirstNameRegister.getText().toString().equals("") || etLastNameRegister.getText().toString().equals("")) {
            if (etEmailRegister.getText().toString().equals("")) {
                etEmailRegister.setError("Email harus diisi");
            } else if (etPhoneRegister.getText().toString().equals("")) {
                etPhoneRegister.setError("No Telepon harus diisi");
            } else if (etFirstNameRegister.getText().toString().equals("")){
                etFirstNameRegister.setError("Nama depan harus diisi");
            } else if (etLastNameRegister.getText().toString().equals("")){
                etLastNameRegister.setError("Nama belakang harus diisi");
            }
        } else {
            populateData();
        }
    }

    @OnClick(R.id.btnLogin)
    void onClickbtnLogin(){
        if (etEmailLogin.getText().toString().equals("") || etPhoneLogin.getText().toString().equals("")) {
            if (etEmailLogin.getText().toString().equals("")) {
                etEmailLogin.setError("Email harus diisi");
            } else if (etPhoneLogin.getText().toString().equals("")) {
                etPhoneLogin.setError("No Telepon harus diisi");
            }
        } else {
            findAccount(etEmailLogin.getText().toString());
        }
    }

    @OnClick(R.id.toolbar_back)
    void onClickToolbarBack(){
        onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);
        ButterKnife.bind(this);
        context = this;
        initComponent();
    }

    private void initComponent() {
        if (getIntent()!=null){
            String nameClass = getIntent().getStringExtra(Constant.INTENT_CLASS_NAME) != null ? getIntent().getStringExtra(Constant.INTENT_CLASS_NAME) : "";
            nameClass = nameClass != null ? nameClass : "";
            switch (nameClass) {
                case Constant.REGISTER_CLASS:
                    layout_register.setVisibility(View.VISIBLE);
                    layout_login.setVisibility(View.GONE);
                    break;
                case Constant.LOGIN_CLASS:
                    layout_login.setVisibility(View.VISIBLE);
                    layout_register.setVisibility(View.GONE);
                    if (PreferenceManager.getLoginData()!=null){
                        etEmailLogin.setText(PreferenceManager.getLoginData().getEmail());
                        etPhoneRegister.setText(PreferenceManager.getLoginData().getBilling().getPhone());
                    }
                    break;
            }
        }
    }

    private void populateData() {
        Billing billing = new Billing();
        billing.setFirstName(etFirstNameRegister.getText().toString());
        billing.setLastName(etLastNameRegister.getText().toString());
        billing.setEmail(etEmailRegister.getText().toString());
        billing.setPhone(etPhoneRegister.getText().toString());

        Shipping shipping = new Shipping();
        shipping.setFirstName(etFirstNameRegister.getText().toString());
        shipping.setLastName(etLastNameRegister.getText().toString());

        RequestRegister requestRegister = new RequestRegister();
        requestRegister.setFirst_name(etFirstNameRegister.getText().toString());
        requestRegister.setLast_name(etLastNameRegister.getText().toString());
        requestRegister.setEmail(etEmailRegister.getText().toString());
        requestRegister.setBilling(billing);
        requestRegister.setShipping(shipping);

        doRegister(requestRegister);
    }

    private void findAccount(String email) {
        dialog = ProgressDialog.show(this, "",
                "Memproses login anda...", true);
        ApiCore.apiInterface().findAccount(email, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<List<ResponseRegister>>() {
            @Override
            public void onResponse(Call<List<ResponseRegister>> call, Response<List<ResponseRegister>> response) {
                try {
                    dialog.dismiss();
                    String fcmDeviceToken = MyFirebaseMessagingService.getToken(context);
                    List<ResponseRegister> res = response.body();
                    assert res != null;
                    if (res.size() > 0) {
                        String id = String.valueOf(Objects.requireNonNull(res).get(0).getId());
                        if (res.get(0).getBilling().getPhone().contains(etPhoneLogin.getText().toString().substring(3))) {
                            if (PreferenceManager.getLoginData() == null){
                                sendDeviceId(id, fcmDeviceToken, true);
                            } else {
                                dialog = ProgressDialog.show(context, "",
                                        "Mohon tunggu...", true);
                                Toast.makeText(context, "Login anda berhasil", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, MainActivity.class);
                                intent.putExtra("isFromAccount", true);
                                startActivity(intent);
                            }

                            PreferenceManager.setLoginData(res.get(0));
                            PreferenceManager.setFirebaseUser(true);
                        } else {
                            Toast.makeText(context, "No Telepon anda tidak terdaftar", Toast.LENGTH_SHORT).show();
                        }
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

    private void doRegister(RequestRegister requestRegister) {
        dialog = ProgressDialog.show(this, "",
                "Sedang memproses...", true);
        ApiCore.apiInterface().doRegister(requestRegister, PreferenceManager.getConsumerKey(), PreferenceManager.getConsumerSecret()).enqueue(new Callback<ResponseRegister>() {
            @Override
            public void onResponse(Call<ResponseRegister> call, Response<ResponseRegister> response) {
                try {
                    if (response.isSuccessful()) {
                        String fcmDeviceToken = MyFirebaseMessagingService.getToken(context);
                        Log.d("fcmDeviceToken", fcmDeviceToken);
                        ResponseRegister register = response.body();
                        if (register != null) {
                            PreferenceManager.setLoginData(register);
                            sendDeviceId(String.valueOf(register.getId()), fcmDeviceToken, false);
                        }
                    } else {
                        Toast.makeText(context, "Email sudah terdaftar", Toast.LENGTH_SHORT).show();
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

    private void sendDeviceId(String userId, String deviceId, boolean isFromLogin) {
        dialog = ProgressDialog.show(this, "",
                "Mohon tunggu...", true);
        ApiAjax.apiInterface().sendDeviceId(userId, deviceId, Constant.ACTION_SEND_DEVICE).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        dialog = ProgressDialog.show(context, "",
                                "Mohon tunggu...", true);

                        if (isFromLogin) {
                            Toast.makeText(context, "Login anda berhasil", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("isFromAccount", true);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "Pendaftaran berhasil", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignInSignUpActivity.this, SignInSignUpActivity.class);
                            intent.putExtra(Constant.INTENT_CLASS_NAME, Constant.LOGIN_CLASS);
                            startActivity(intent);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Periksa koneksi internet anda", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}