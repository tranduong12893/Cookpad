package t2010a.cookpad_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import t2010a.cookpad_clone.R;
import t2010a.cookpad_clone.adapter.CartDetailAdapter;
import t2010a.cookpad_clone.local_data.LocalDataManager;
import t2010a.cookpad_clone.model.shop.CartItem;
import t2010a.cookpad_clone.model.shop.Order;
import t2010a.cookpad_clone.repository.Repository;

public class CheckoutActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private CardView cvProduct;
    private BigDecimal tongTien;
    private TextView tvTotal, tvTotalPrice, tvVAT, tvName, tvAddressUser, tvPhoneUser;
    private Button btnToPay;
    private RecyclerView rvCartDetail;
    private Spinner spinnerPay;
    private List<CartItem> cartItemList = new ArrayList<>();
    private List<String> listPay;
    private Repository repository = Repository.getInstance();
    private CartDetailAdapter adapter;
    private String PayType;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initView();
        configRv();
        userData();
        dataSpinner();
        configToolBar();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        appBar = findViewById(R.id.appBar);
        rvCartDetail = findViewById(R.id.rvCartDetail);
        tvName = findViewById(R.id.tvName);
        tvAddressUser = findViewById(R.id.tvAddressUser);
        tvPhoneUser = findViewById(R.id.tvPhoneUser);
        tvTotal = findViewById(R.id.tvTotal);
        tvVAT = findViewById(R.id.tvVAT);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        btnToPay = findViewById(R.id.btnToPay);
        btnToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOrder();
            }
        });
        spinnerPay = findViewById(R.id.spinnerPay);
        cvProduct = findViewById(R.id.cvProduct);
    }

    private void configRv() {
        cartItemList = initData();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new CartDetailAdapter(this, cartItemList);
        rvCartDetail.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvCartDetail.setAdapter(adapter);
        rvCartDetail.setLayoutManager(layoutManager);
    }

    private void tinhTongTien() {
        tongTien = BigDecimal.valueOf(0);
        for (int i = 0; i < cartItemList.size(); i++) {
            tongTien = tongTien.add(cartItemList.get(i).getProduct().getPrice().multiply(BigDecimal.valueOf(cartItemList.get(i).getQuantity())));
        }
        tvTotal.setText(decimalFormat.format(tongTien) + " VNĐ");
        tvVAT.setText(decimalFormat.format(tongTien.multiply(BigDecimal.valueOf(0.08))) + " VNĐ");
        tvTotalPrice.setText(decimalFormat.format(tongTien.add(tongTien.multiply(BigDecimal.valueOf(0.08)))) + " VNĐ");
    }

    private void configToolBar() {
        setSupportActionBar(toolbar);
        appBar.setOutlineProvider(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void dataSpinner() {
        PayType = "Tiền mặt";
        listPay = new ArrayList<>();
        listPay.add("Tiền mặt");
        listPay.add("MOMO");
        listPay.add("Chuyển khoản");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listPay);
        spinnerPay.setAdapter(adapter);
        spinnerPay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                PayType = listPay.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private List<CartItem> initData() {
        //retrofit get data
        repository.getService().findAllCart("Bearer " + LocalDataManager.getAccessToken()).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                cartItemList = response.body();
                adapter.reloadData(cartItemList);
                tinhTongTien();
            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.d("TAG", "onFailure: ");
            }
        });
        return cartItemList;
    }
    private void userData(){
        tvName.setText(LocalDataManager.getUserDetail().getFullName());
        tvAddressUser.setText(LocalDataManager.getUserDetail().getAddress());
        tvPhoneUser.setText(LocalDataManager.getUserDetail().getPhone());
    }

    private void onOrder() {

        repository.getService().addToOrder(LocalDataManager.getAccessToken(), cartItemList.get(0).getId().getShoppingCartId()).enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {

            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {

            }
        });
        switch (PayType) {
            case "Tiền mặt":
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                startActivity(intent);
                break;
            case "MOMO":
                Intent intent1 = new Intent(CheckoutActivity.this, MOMO.class);
                startActivity(intent1);
                break;
            case "Chuyển khoản":
                Intent intent2 = new Intent(CheckoutActivity.this, Tranfer.class);
                startActivity(intent2);
                break;
            default:
                break;

        }
    }
}