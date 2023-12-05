import Utility.DataBase.DaoImplementations.Order.OrderDaoImplementation;
import Utility.DataBase.Daos.Order.OrderDao;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

public class OrderDaoImplementationTest {

    private OrderDao orderDao;

    @BeforeEach
    void setUp(){
        orderDao=new OrderDaoImplementation();
    }

    @Test
    void test(){

    }
}
