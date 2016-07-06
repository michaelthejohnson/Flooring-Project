package com.swcguild.flooringmastery.dao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Scanner;
import com.swcguild.flooringmastery.model.Flooring;
import com.swcguild.flooringmastery.model.Order;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FlooringMasteryDAOImpl implements FlooringMasteryDAO {

    private HashMap<Double, Order> ordersList;
    private HashMap<String, Double> taxFileHashMap;
    private HashMap<String, Flooring> productFileHashMap;
    private String folderToWrite = "OrdersByDate/Orders_";
    private String folderToRead = "OrdersByDate";
    private String fileNameTax = "taxByState.txt";
    private String fileNameProduct = "productInfo.txt";
    private final String DELIMITER = "::";
    Flooring aNewFlooring = new Flooring();
    double materialCost = 0;
    double laborCost = 0;

    public FlooringMasteryDAOImpl(String folder) throws IOException {
        folderToRead = folder;
        ordersList = new HashMap<>();
        taxFileHashMap = new HashMap<>();
        productFileHashMap = new HashMap<>();
        try {
            loadOrdersListHashMapFromFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public FlooringMasteryDAOImpl() throws IOException {
        ordersList = new HashMap<>();
        taxFileHashMap = new HashMap<>();
        productFileHashMap = new HashMap<>();
        try {
            loadOrdersListHashMapFromFiles();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean readConfigFile() throws FileNotFoundException {
        boolean PROD = false;
        Scanner configFileScanner = new Scanner(new BufferedReader(new FileReader("config")));
        if (configFileScanner.hasNext("PROD")) {
            PROD = true;
        }
        return PROD;
    }

    public void saveOrdersListToFile() throws IOException {
        if (readConfigFile()) {
            HashMap<String, ArrayList<Order>> ordersByDateSortedMap = new HashMap<>();
            for (Order orderToSort : ordersList.values()) {
                String orderDateToMatch = orderToSort.getOrderDate();
                if (ordersByDateSortedMap.get(orderDateToMatch) == null) {
                    ordersByDateSortedMap.put(orderDateToMatch, new ArrayList<Order>());
                }
                ArrayList<Order> arrayListOfIdenticalDatedOrders = ordersByDateSortedMap.get(orderDateToMatch);
                arrayListOfIdenticalDatedOrders.add(orderToSort);
            }
            for (String iteratingOverEachDate : ordersByDateSortedMap.keySet()) {
                PrintWriter orderFileWriter = new PrintWriter(new FileWriter(folderToWrite
                        + iteratingOverEachDate + ".txt"));
                for (Order eachOrderinDatedBox : ordersByDateSortedMap.get(iteratingOverEachDate)) {
                    orderFileWriter.println(
                            eachOrderinDatedBox.getOrderId() + DELIMITER
                            + eachOrderinDatedBox.getCustomerName() + DELIMITER
                            + eachOrderinDatedBox.getCustomerState() + DELIMITER
                            + eachOrderinDatedBox.getTaxRate() + DELIMITER
                            + eachOrderinDatedBox.getArea() + DELIMITER
                            + eachOrderinDatedBox.getMaterialCostTotal() + DELIMITER
                            + eachOrderinDatedBox.getLaborCostTotal() + DELIMITER
                            + eachOrderinDatedBox.getTaxTotal() + DELIMITER
                            + eachOrderinDatedBox.getCostTotal() + DELIMITER
                            + eachOrderinDatedBox.getOrderDate() + DELIMITER
                            + eachOrderinDatedBox.getFlooringObject().getFlooringType() + DELIMITER
                            + eachOrderinDatedBox.getFlooringObject().getMaterialCostPerSqFt() + DELIMITER
                            + eachOrderinDatedBox.getFlooringObject().getLaborCostPerSqFt() + DELIMITER);
                }
                orderFileWriter.flush();
                orderFileWriter.close();
            }
        }
    }

    @Override
    public void loadOrdersListHashMapFromFiles() throws FileNotFoundException, IOException {
        File folderOfOrderFiles = new File(folderToRead);
        File[] arrayOfFiles = folderOfOrderFiles.listFiles();
        for (File eachFileInArray : arrayOfFiles) {
            Scanner ordersListFileReader = new Scanner(new BufferedReader(new FileReader(eachFileInArray)));
            while (ordersListFileReader.hasNextLine()) {
                Order aNewOrder = new Order();
                String orderFileLineItem = ordersListFileReader.nextLine();
                String[] orderProperties = orderFileLineItem.split(DELIMITER);
                if (orderProperties.length != 13) {
                    continue;
                }
                aNewOrder.setOrderId(Double.parseDouble(orderProperties[0]));
                aNewOrder.setCustomerName(orderProperties[1]);
                aNewOrder.setCustomerState(orderProperties[2]);
                aNewOrder.setTaxRate(Double.parseDouble(orderProperties[3]));
                aNewOrder.setArea(Double.parseDouble(orderProperties[4]));
                aNewOrder.setMaterialCostTotal(Double.parseDouble(orderProperties[5]));
                aNewOrder.setLaborCostTotal(Double.parseDouble(orderProperties[6]));
                aNewOrder.setTaxTotal(Double.parseDouble(orderProperties[7]));
                aNewOrder.setCostTotal(Double.parseDouble(orderProperties[8]));
                aNewOrder.setOrderDate(orderProperties[9]);
                aNewFlooring.setFlooringType(orderProperties[10]);
                aNewFlooring.setMaterialCostPerSqFt(Double.parseDouble(orderProperties[11]));
                aNewFlooring.setLaborCostPerSqFt(Double.parseDouble(orderProperties[12]));
                aNewOrder.setFlooringObject(aNewFlooring);
                ordersList.put(aNewOrder.getOrderId(), aNewOrder);
                eachFileInArray.delete();
            }
        }
    }

    @Override
    public void readTaxFromFile() throws FileNotFoundException {
        Scanner taxFileReader = new Scanner(new BufferedReader(new FileReader(fileNameTax)));
        while (taxFileReader.hasNextLine()) {
            String taxFileLineItem = taxFileReader.nextLine();
            String[] taxProperties = taxFileLineItem.split(DELIMITER);
            if (taxProperties.length != 2) {
                continue;
            }
            Double rate = (Double.parseDouble(taxProperties[1]));
            taxFileHashMap.put(taxProperties[0], rate);
        }
    }

    @Override
    public void readProductDetailsFromFile() throws FileNotFoundException {
        Scanner productsFileReader = new Scanner(new BufferedReader(new FileReader(fileNameProduct)));
        while (productsFileReader.hasNextLine()) {
            Flooring newFlooringDetailsFromFile = new Flooring();
            String productFileLineItem = productsFileReader.nextLine();
            String[] productProperties = productFileLineItem.split(DELIMITER);
            if (productProperties.length != 3) {
                continue;
            }
            newFlooringDetailsFromFile.setFlooringType(productProperties[0]);
            newFlooringDetailsFromFile.setMaterialCostPerSqFt(Double.parseDouble(productProperties[1]));
            newFlooringDetailsFromFile.setLaborCostPerSqFt(Double.parseDouble(productProperties[2]));
            productFileHashMap.put(newFlooringDetailsFromFile.getFlooringType(), newFlooringDetailsFromFile);
        }
    }

    @Override
    public int ordersCount() {
        return ordersList.size();
    }

    @Override
    public double findMaxKey() {
        double largestKeyNumber;
        if (ordersList.isEmpty()) {
            largestKeyNumber = 1;
        } else {
            largestKeyNumber = Collections.max(ordersList.keySet());
        }
        return largestKeyNumber;
    }

    @Override
    public void postNewOrder(Order aNewOrder) {
        ordersList.put(aNewOrder.getOrderId(), aNewOrder);
    }

    @Override
    public Order removeExistingFromHashMap(double orderToRemoveOrderID) {
        Order orderIdBeingRemoved = ordersList.remove(orderToRemoveOrderID);
        return orderIdBeingRemoved;
    }

    @Override
    public boolean findTaxKey(String userState) {
        if (taxFileHashMap.keySet().contains(userState)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
     public boolean findProductKey(String userProduct) {
        if (productFileHashMap.keySet().contains(userProduct)) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public Collection<Order> listAllOrders() {
        Collection<Order> allOrders = ordersList.values();
        return allOrders;
    }

    @Override
    public List<Order> findByDate(String date) {
        Collection<Order> myOrders = ordersList.values();
        List<Order> orders = new ArrayList<>(myOrders);
        List<Order> ordersToReturnForDisplay = new ArrayList<>();
        for (Order order : orders) {
            if (order.getOrderDate().equals(date)) {
                ordersToReturnForDisplay.add(order);
            }
        }
        return ordersToReturnForDisplay;
    }

    @Override
    public double getTaxRateFromFile(String userState) {
        Double rate = taxFileHashMap.get(userState);
        return rate;
    }

    @Override
    public double getMaterialCostFromProductFile(String userFlooringType) {
        for (String productToFind : productFileHashMap.keySet()) {
            Flooring flooring = productFileHashMap.get(productToFind);
            if (userFlooringType.equals(flooring.getFlooringType())) {
                materialCost = flooring.getMaterialCostPerSqFt();
                break;
            }
        }
        return materialCost;
    }

    @Override
    public double getLaborCostFromProductFile(String userFlooringType) {
        for (String productToFind : productFileHashMap.keySet()) {
            Flooring flooring = productFileHashMap.get(productToFind);
            if (userFlooringType.equals(flooring.getFlooringType())) {
                laborCost = flooring.getLaborCostPerSqFt();
                break;
            }
        }
        return laborCost;
    }

}
