package t2010a.cookpad_clone.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import t2010a.cookpad_clone.R;
import t2010a.cookpad_clone.model.shop.CartItem;

public class OrderAdapter extends RecyclerView.Adapter {
    private Activity activity;
    private List<CartItem> listCartItem;
    DecimalFormat decimalFormat = new DecimalFormat("###,###,###");

    public OrderAdapter(Activity activity, List<CartItem> listCartItem) {
        this.activity = activity;
        this.listCartItem = listCartItem;
    }

    public void reloadData(List<CartItem> listCartItem) {
        this.listCartItem = listCartItem;
        totalPrice();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = activity.getLayoutInflater().inflate(R.layout.item_cart_detail, parent, false);
        OrderHodel holder = new OrderHodel(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OrderHodel vh = (OrderHodel) holder;
        CartItem cartItem = listCartItem.get(position);
        vh.tvName.setText(cartItem.getProduct().getName());

        vh.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
        Glide.with(activity).load(cartItem.getProduct().getThumbnails()).into(vh.ivThumbnail);
        BigDecimal total = cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        vh.tvTotalUnit.setText(decimalFormat.format(String.valueOf(total)) + " VND");
        vh.tvPrice.setText(String.valueOf(cartItem.getProduct().getPrice()));
    }

    @Override
    public int getItemCount() {
        return listCartItem.size();
    }

    public class OrderHodel extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvTotalUnit, tvPrice;
        ImageView ivThumbnail;

        public OrderHodel(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvTotalUnit = itemView.findViewById(R.id.tvTotalUnit);
        }
    }

    public BigDecimal totalPrice() {
        BigDecimal price = BigDecimal.valueOf(0);
        for (int i = 0; i < listCartItem.size(); i++) {
            price = price.add(listCartItem.get(i).getProduct().getPrice().multiply(BigDecimal.valueOf(listCartItem.get(i).getQuantity())));
        }
        return price;
    }
}
