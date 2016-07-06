package com.swcguild.flooringmastery.dao;

import com.swcguild.flooringmastery.model.Order;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface FlooringMasteryDAO {

    public boolean readConfigFile() throws FileNotFoundException;

    public void saveOrdersListToFile() throws IOException;

    public void loadOrdersListHashMapFromFiles() throws FileNotFoundException, IOException;

    public void readTaxFromFile() throws FileNotFoundException;

    public void readProductDetailsFromFile() throws FileNotFoundException;

    public int ordersCount();

    public double findMaxKey();

    public boolean findTaxKey(String userState);

    public boolean findProductKey(String userProduct);

    public void postNewOrder(Order aNewOrder);

    public Order removeExistingFromHashMap(double orderToRemoveOrderID);

    public Collection<Order> listAllOrders();

    public double getTaxRateFromFile(String userState) throws FileNotFoundException;

    public double getMaterialCostFromProductFile(String userFlooringType);

    public double getLaborCostFromProductFile(String userFlooringType);

    public List<Order> findByDate(String date);

}
