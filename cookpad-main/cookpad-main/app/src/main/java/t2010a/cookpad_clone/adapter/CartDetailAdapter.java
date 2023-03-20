package t2010a.cookpad_clone.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import t2010a.cookpad_clone.R;
import t2010a.cookpad_clone.interfaces.ItemClickListener;
import t2010a.cookpad_clone.local_data.LocalDataManager;
import t2010a.cookpad_clone.model.shop.CartItem;
import t2010a.cookpad_clone.repository.Repository;

public class CartDetailAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<CartItem> cartItemList;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    private Repository repository = Repository.getInstance();

    public CartDetailAdapter(Activity activity, List<CartItem> cartItemList) {
        this.activity = activity;
        this.cartItemList = cartItemList;
    }

    public void reloadData(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = activity.getLayoutInflater().inflate(R.layout.item_cart_detail, parent, false);
        CartDetailViewHolder holder = new CartDetailViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartDetailViewHolder viewHolder = (CartDetailViewHolder) holder;
        CartItem model = cartItemList.get(position);
        Glide.with(activity).load(model.getProduct().getThumbnails()).into(viewHolder.ivThumbnail);
        viewHolder.tvPrice.setText(decimalFormat.format(model.getProduct().getPrice())+" đ");
        viewHolder.tvName.setText(model.getProduct().getName());
        viewHolder.tvQuantity.setText(model.getQuantity().toString()+ " sp");
        viewHolder.tvTotalUnit.setText(decimalFormat.format(model.getProduct().getPrice().multiply(BigDecimal.valueOf(model.getQuantity())))+" đ");
        LocalDataManager.setItemCount(model.getQuantity());
        viewHolder.btnDeleteCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repository.getService().deleteCart(model.getId().getShoppingCartId(), model.getProduct().getId(), "Bearer " +LocalDataManager.getAccessToken()).enqueue(new Callback<CartItem>() {
                    @Override
                    public void onResponse(Call<CartItem> call, Response<CartItem> response) {
                        cartItemList.remove(holder.getAdapterPosition());
                        notifyItemRemoved(holder.getAdapterPosition());
                        notifyItemRangeChanged(holder.getAdapterPosition(),getItemCount());
                        Toast.makeText(activity.getApplication(), "Access done!",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<CartItem> call, Throwable t) {
                        Toast.makeText(activity.getApplication(), "Error!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

//        viewHolder.setListener(new ItemClickListener() {
//            @Override
//            public void onClick(View view, int position, int i) {
//                if (i == 1) {
//                    if (model.getQuantity() > 1) {
//                        int newQuantity = model.getQuantity() - 1;
//                        model.setQuantity(newQuantity);
//                        viewHolder.tvItemCount.setText(model.getQuantity());
//                        viewHolder.tvTotalPrice.setText((model.getProduct().getPrice().intValueExact() * model.getQuantity()));
//                        CartDetailActivity cartDetailActivity = new CartDetailActivity();
//                        cartDetailActivity.removeCart(model.getId());
//                    }
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        if (cartItemList != null) {
            return cartItemList.size();
        }
        return 0;
    }

    public class CartDetailViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvQuantity, tvTotalUnit, tvPrice;
        ImageView ivThumbnail, btnDeleteCart;
        ItemClickListener listener;

        public CartDetailViewHolder(@NonNull View itemView) {
            super(itemView);

            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnDeleteCart = itemView.findViewById(R.id.btnDeleteCart);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalUnit = itemView.findViewById(R.id.tvTotalUnit);

        }

    }
}
