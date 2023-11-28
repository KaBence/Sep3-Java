package Utility.DataBase.Daos.Order;

import sep.DtoOrder;
import sep.DtoOrderItem;

import java.util.List;

public interface OrderDao {
    String createOrder(DtoOrder order, List<DtoOrderItem> orderItems);
}
