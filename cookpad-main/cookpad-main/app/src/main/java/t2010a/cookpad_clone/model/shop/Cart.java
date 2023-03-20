package t2010a.cookpad_clone.model.shop;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable {
    private List<CartItem> cartItemList;

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }
}
