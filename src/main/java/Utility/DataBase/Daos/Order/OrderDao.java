package Utility.DataBase.Daos.Order;

import sep.DtoOrder;
import sep.DtoOrderItem;

import java.util.ArrayList;
import java.util.List;

public interface OrderDao {
    String createOrder(DtoOrder order, List<DtoOrderItem> orderItems,String paymentMethod,String note) throws Exception;

    ArrayList<DtoOrderItem> getOrderItemsById(int orderId);

    ArrayList<DtoOrderItem> getOrderItemsByGroup(int orderId);

    //accept/decline order, getby farmer
}
