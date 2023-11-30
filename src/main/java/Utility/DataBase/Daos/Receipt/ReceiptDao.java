package Utility.DataBase.Daos.Receipt;

import sep.DtoCustomerSendReceipt;
import sep.DtoSendReceipt;

import java.util.ArrayList;

public interface ReceiptDao {
    ArrayList<DtoSendReceipt> getReceiptsByFarmer(String farmer);
    ArrayList<DtoCustomerSendReceipt> getReceiptsByCustomer(String customer);

    ArrayList<DtoSendReceipt> getPendingReceiptsByFarmer(String farmer);

    ArrayList<DtoSendReceipt> getApprovedReceiptsByFarmer(String farmer);

    ArrayList<DtoSendReceipt> getRejectedReceiptsByFarmer(String farmer);

    String FarmersApproval(boolean approval,int orderId) throws Exception;
}
