package Utility.DataBase.DaoImplementations.Order;

import Utility.DataBase.Daos.Order.OrderDao;
import sep.DtoOrder;
import sep.DtoOrderItem;

import java.util.List;

public class OrderDaoImplementation implements OrderDao {


    @Override
    public String createOrder(DtoOrder order, List<DtoOrderItem> orderItems) {
        return null;
    }
}
