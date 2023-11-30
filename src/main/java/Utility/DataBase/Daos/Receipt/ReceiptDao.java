package Utility.DataBase.Daos.Receipt;

import sep.DtoSendReceipt;

import java.util.ArrayList;

public interface ReceiptDao {
    ArrayList<DtoSendReceipt> getReceiptsByFarmer(String farmer);
    ArrayList<DtoSendReceipt> getReceiptsByCustomer(String customer);

    String FarmersApproval(boolean approval,int orderId);
}
