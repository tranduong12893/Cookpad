package t2010a.cookpad_clone.model.shop;

import java.io.Serializable;

public enum OrderStatus implements Serializable {
    PENDING,
    CONFIRMED,
    CANCELLED,
    DONE,
    PROCESSING;
}
