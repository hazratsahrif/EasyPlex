package com.siflusso.ui.payment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.siflusso.util.Constants.ARG_PAYMENT;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.siflusso.BuildConfig;
import com.siflusso.R;
import com.siflusso.data.model.plans.Plan;
import com.siflusso.data.remote.ErrorHandling;
import com.siflusso.databinding.PaymentPaypalBinding;
import com.siflusso.ui.manager.SettingsManager;
import com.siflusso.ui.splash.SplashActivity;
import com.siflusso.ui.viewmodels.LoginViewModel;
import com.siflusso.util.DialogHelper;
import com.siflusso.util.Tools;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.PaymentButtonIntent;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.OrderRequest;
import com.paypal.checkout.order.PurchaseUnit;
import com.paypal.checkout.paymentbutton.PaymentButton;

import java.util.ArrayList;

import javax.inject.Inject;
import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * HoneyStream - Android Movie Portal App
 * @package     HoneyStream - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class PaymentPaypal extends AppCompatActivity {


    PaymentPaypalBinding binding;


    @Inject
    SettingsManager settingsManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel loginViewModel;

    private Plan plan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);


        binding = DataBindingUtil.setContentView(this, R.layout.payment_paypal);


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);



        Intent intent = getIntent();
        plan = intent.getParcelableExtra(ARG_PAYMENT);

        onInitPaypal();

        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        onLoadPaypal();





        binding.paypalMethod.performClick();

    }


    private void onInitPaypal() {

        if (settingsManager.getSettings().getPaypalClientId() !=null &&

                !settingsManager.getSettings().getPaypalClientId().isEmpty() &&
                settingsManager.getSettings().getPaypalCurrency() !=null &&
                !settingsManager.getSettings().getPaypalCurrency().isEmpty()) {

            CheckoutConfig config = new CheckoutConfig(getApplication(),settingsManager.getSettings().getPaypalClientId(),
                    Environment.SANDBOX,CurrencyCode.valueOf(settingsManager.getSettings().getPaypalCurrency()),UserAction.PAY_NOW,
                    PaymentButtonIntent.CAPTURE,new SettingsConfig(true,false),String.format("%s://paypalpay", BuildConfig.APPLICATION_ID));
            PayPalCheckout.setConfig(config);
        }
    }


    private void onLoadPaypal() {



        binding.paypalMethod.setup(
                new CreateOrder() {
                    @Override
                    public void create(@NonNull CreateOrderActions createOrderActions) {
                        ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();

                        purchaseUnits.add(
                                new PurchaseUnit.Builder()
                                        .amount(
                                                new Amount.Builder()
                                                        .currencyCode(CurrencyCode.valueOf(settingsManager.getSettings().getPaypalCurrency()))
                                                        .value(plan.getPrice())
                                                        .build()
                                        ).description(plan.getDescription())
                                        .build()
                        );

                        OrderRequest order = new OrderRequest(
                                OrderIntent.CAPTURE,
                                new AppContext.Builder()
                                        .userAction(UserAction.PAY_NOW)
                                        .build(),
                                purchaseUnits
                        );

                        createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
                    }
                },
                approval -> approval.getOrderActions().capture(result -> {
                    Timber.i("CaptureOrderResult: %s", result);


                    loginViewModel.setSubscription(String.valueOf(plan.getId()), "1", plan.getName(), plan.getPackDuration(), "paypal").observe(PaymentPaypal.this, login -> {

                        if (login.status == ErrorHandling.Status.SUCCESS) {

                            binding.loader.setVisibility(View.GONE);


                            final Dialog dialog = new Dialog(this);
                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_success_payment);
                            dialog.setCancelable(false);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                            lp.copyFrom(dialog.getWindow().getAttributes());

                            lp.gravity = Gravity.BOTTOM;
                            lp.width = MATCH_PARENT;
                            lp.height = MATCH_PARENT;


                            dialog.findViewById(R.id.btn_start_watching).setOnClickListener(v -> {

                                Intent intent = new Intent(PaymentPaypal.this, SplashActivity.class);
                                startActivity(intent);
                                finish();


                            });


                            dialog.show();
                            dialog.getWindow().setAttributes(lp);
                            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> {
                                Intent intent = new Intent(PaymentPaypal.this, SplashActivity.class);
                                startActivity(intent);
                                finish();
                            });
                            dialog.show();
                            dialog.getWindow().setAttributes(lp);


                        } else {

                            binding.loader.setVisibility(View.GONE);
                            DialogHelper.erroPayment(PaymentPaypal.this);


                        }

                    });
                })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}