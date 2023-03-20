package t2010a.cookpad_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;

import java.text.DecimalFormat;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import t2010a.cookpad_clone.R;
import t2010a.cookpad_clone.local_data.LocalDataManager;
import t2010a.cookpad_clone.model.shop.Product;
import t2010a.cookpad_clone.repository.Repository;

public class ProductDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivThumbnail, ivMinus, ivAdd;
    private TextView tvName;
    private TextView tvPrice;
    private TextView tvDetail;
    private TextView textCartItemCount;
    private TextView tvItemCount;
    private LinearLayout btnAddToCart;
    private Toolbar toolbar;
    private AppBarLayout appBar;

    private int mCartItemCount = 10;
    private Product product;
    private Repository repository = Repository.getInstance();
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initView();
    }

    private void initView() {
        ivThumbnail = findViewById(R.id.ivThumbnail);
        tvName = findViewById(R.id.tvName);
        tvPrice = findViewById(R.id.tvPrice);
        tvDetail = findViewById(R.id.tvDetail);
        toolbar = findViewById(R.id.toolbar);
        appBar = findViewById(R.id.appBar);
        ivMinus = findViewById(R.id.ivMinus);
        ivAdd = findViewById(R.id.ivAdd);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        tvItemCount = findViewById(R.id.tvItemCount);
        ivAdd.setOnClickListener(this);
        ivMinus.setOnClickListener(this);

        product = (Product) getIntent().getSerializableExtra("PRODUCT");

        Glide.with(this).load(product.getThumbnails()).into(ivThumbnail);
        tvName.setText(product.getName());
        tvPrice.setText(decimalFormat.format(product.getPrice()) +" đ");
        tvDetail.setText(product.getDetail());

        btnAddToCart.setOnClickListener(this);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_cart);
        View actionView = menuItem.getActionView();
        textCartItemCount = actionView.findViewById(R.id.cart_badge);
        setupBadge();
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onOptionsItemSelected(menuItem);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                startActivity(new Intent(this, CartDetailActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupBadge() {
        if (textCartItemCount != null) {
            if (mCartItemCount == 0) {
                if (textCartItemCount.getVisibility() != View.GONE) {
                    textCartItemCount.setVisibility(View.GONE);
                }
            } else {
                textCartItemCount.setText(String.valueOf(Math.min(mCartItemCount, 99)));
                if (textCartItemCount.getVisibility() != View.VISIBLE) {
                    textCartItemCount.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void setBtnAddToCart() {
        Log.d("TAG", "setBtnAddToCart: 0");
        repository.getService().addToCart("Bearer " + LocalDataManager.getAccessToken(), product.getId(),Integer.valueOf(tvItemCount.getText().toString()) ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d("TAG", "onResponse:1 ");
                if (Objects.equals(response.body(), "Add success")) {
                    Log.d("TAG", "onResponse:2 ");
                    Toast.makeText(ProductDetailActivity.this, "Thêm không thành công", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this, "Thêm thành công", Toast.LENGTH_LONG).show();

            }
        });
    }

    private void tangSoLuong() {
        Integer soLuongCu = Integer.parseInt(tvItemCount.getText().toString());
        if (soLuongCu < product.getQuantity()){
            tvItemCount.setText(String.valueOf(soLuongCu + 1));
        }else {
            Toast.makeText(ProductDetailActivity.this,"You have reached your product limit!",Toast.LENGTH_SHORT).show();
        }
    }

    private void giamSoLuong() {
        Integer soLuongCu = Integer.parseInt(tvItemCount.getText().toString());

        if (soLuongCu >=2){
            tvItemCount.setText(String.valueOf(soLuongCu - 1));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddToCart:
                setBtnAddToCart();
                break;
            case R.id.ivMinus:
                giamSoLuong();
                break;
            case R.id.ivAdd:
                tangSoLuong();
                break;
            default:
                break;
        }
    }
}