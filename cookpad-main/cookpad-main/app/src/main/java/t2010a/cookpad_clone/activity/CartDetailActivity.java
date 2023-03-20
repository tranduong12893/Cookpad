package t2010a.cookpad_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import t2010a.cookpad_clone.repository.Repository;

public class CartDetailActivity extends AppCompatActivity implements View.OnClickListener {
    //    private TextView tvCartCount;
    private TextView TotalPrice;
    private BigDecimal tongTien;
    private RecyclerView rvCartDetail;
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private Button btnToCheckout, btnDeleteCart;
    private LinearLayout layoutNotification_2;

    private List<CartItem> cartItemList = new ArrayList<>();
    private Repository repository = Repository.getInstance();
    private CartDetailAdapter adapter;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_detail);
        initView();
        configRv();
        configToolBar();

    }


    private void initView() {
        rvCartDetail = findViewById(R.id.rvCartDetail);
//        tvCartCount = findViewById(R.id.tvCartCount);
        TotalPrice = findViewById(R.id.TotalPrice);
        toolbar = findViewById(R.id.toolbar);
        appBar = findViewById(R.id.appBar);
        btnToCheckout = findViewById(R.id.btnToCheckout);
        layoutNotification_2 = findViewById(R.id.layoutNotification_2);

    }

    private void configRv() {
        cartItemList = initData();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        adapter = new CartDetailAdapter(this, cartItemList);
        rvCartDetail.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        rvCartDetail.setAdapter(adapter);
        rvCartDetail.setLayoutManager(layoutManager);
    }

    private void checkToUnit() {
        if (cartItemList.size() == 0) {
            layoutNotification_2.setVisibility(View.VISIBLE);
            btnToCheckout.setVisibility(View.GONE);
        }

    }

    private List<CartItem> initData() {
        //retrofit get data
        repository.getService().findAllCart("Bearer " + LocalDataManager.getAccessToken()).enqueue(new Callback<List<CartItem>>() {
            @Override
            public void onResponse(Call<List<CartItem>> call, Response<List<CartItem>> response) {
                cartItemList = response.body();
                adapter.reloadData(cartItemList);
                checkToUnit();
                tinhTongTien();
                btnToCheckout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CartDetailActivity.this, CheckoutActivity.class);
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<CartItem>> call, Throwable t) {
                Log.d("TAG", "onFailure: ");
            }
        });
        return cartItemList;
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

    private void tinhTongTien() {
        tongTien = BigDecimal.valueOf(0);
        for (int i = 0; i < cartItemList.size(); i++) {
            tongTien = tongTien.add(cartItemList.get(i).getProduct().getPrice().multiply(BigDecimal.valueOf(cartItemList.get(i).getQuantity())));
        }
        TotalPrice.setText(decimalFormat.format(tongTien) + " VNĐ");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnToCheckout:
                startActivity(new Intent(this, CheckoutActivity.class));
                break;
        }
    }
}