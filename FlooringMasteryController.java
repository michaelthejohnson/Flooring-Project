package com.swcguild.flooringmastery.controller;

import com.swcguild.flooringmastery.console.FlooringMasteryConsole;
import com.swcguild.flooringmastery.dao.FlooringMasteryDAO;
import com.swcguild.flooringmastery.model.Flooring;
import com.swcguild.flooringmastery.model.Order;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class FlooringMasteryController {

    FlooringMasteryConsole console;
    FlooringMasteryDAO daoLayer;

    DecimalFormat df = new DecimalFormat("#.00");

    public FlooringMasteryController(FlooringMasteryConsole console, FlooringMasteryDAO daoLayer) {
        this.console = console;
        this.daoLayer = daoLayer;
    }

    public void run() throws FileNotFoundException, IOException {

        daoLayer.loadOrdersListHashMapFromFiles();
        daoLayer.readTaxFromFile();
        daoLayer.readProductDetailsFromFile();

        boolean keepRunning = true;

        while (keepRunning) {
            printMenu();
            int userChoice = console.readInt("Please make a selection: ");
            switch (userChoice) {
                case 1: // display order
                    displayByDate();
                    break;
                case 2: // add an order
                    createNewOrder();
                    break;
                case 3: //edit an existing order
                    editOrder();
                    break;
                case 4: // remove an order
                    removeOrder();
                    break;
                case 5: // save file
                    daoLayer.saveOrdersListToFile();
                    break;
                case 6: // exit 
                    daoLayer.saveOrdersListToFile();
                    console.print("Good-bye.");
                    keepRunning = false;
                    break;
                case 7: //list all
                    listAllOrders();
                    break;
                default:
                    console.print("Please choose a valid selection.");
            }
        }
    }

    public void printMenu() {
        console.print("1. Display orders by date. ");
        console.print("2. Add an order. ");
        console.print("3. Edit an existing order. ");
        console.print("4. Remove an order. ");
        console.print("5. Save current work. ");
        console.print("6. Exit. ");
    }

    public void displayByDate() {
        String userDateString = console.readString(
                "What is the date for the orders you would like to view? Please enter as MMDDYYYY. ");
        List<Order> ordersToPrint = daoLayer.findByDate(userDateString);
        for (Order order : ordersToPrint) {
            console.print("\n"
                    + "Order no.: " + order.getOrderId() + "\n"
                    + "Name: " + order.getCustomerName() + "\n"
                    + "State and tax rate: " + order.getCustomerState() + " " + order.getTaxRate() + "\n"
                    + "Flooring area: " + order.getArea() + " square feet. \n"
                    + "Flooring type: " + order.getFlooringObject().getFlooringType() + "\n"
                    + "Material cost: $" + df.format(order.getMaterialCostTotal()) + " \n"
                    + "Labor cost: $" + df.format(order.getLaborCostTotal()) + "\n"
                    + "Tax total: $" + df.format(order.getTaxTotal()) + "\n"
                    + "Total cost: $" + df.format(order.getCostTotal()) + "\n"
                    + "Order date: " + order.getOrderDate() + "\n");
        }
        if (ordersToPrint.isEmpty()) {
            console.print("No orders were found with an order date of " + userDateString + ".");
        }
    }

    private void createNewOrder() throws FileNotFoundException {
        double materialCostPerSqFt = 0;
        double laborCostPerSqFt = 0;
        String flooringType = null;
        String state = null;
        String custName = null;
        Flooring aNewFlooringOrder = new Flooring();

        double newOrderId = (daoLayer.findMaxKey() + 1.0);

        boolean typeKeepRunning = true;
        while (typeKeepRunning) {
            flooringType = console.readString("What is your preferred flooring type? \n"
                    + "Please choose from:\n"
                    + "\tCarpet \n"
                    + "\tLaminate \n"
                    + "\tTile \n"
                    + "\tWood \n");

            if (daoLayer.findProductKey(flooringType) == true) {
                materialCostPerSqFt = daoLayer.getMaterialCostFromProductFile(flooringType);
                laborCostPerSqFt = daoLayer.getLaborCostFromProductFile(flooringType);
                aNewFlooringOrder = new Flooring(flooringType, materialCostPerSqFt, laborCostPerSqFt);
                typeKeepRunning = false;
            } else {
                console.print("Invalid selection. Please try again.");
            }
        }

        double stateTax = 0;
        boolean stateKeepRunning = true;
        while (stateKeepRunning) {
            state = console.readString("What is your state? \n"
                    + "Please choose from:\n"
                    + "\tOhio \n"
                    + "\tPennsylvania \n"
                    + "\tMichigan \n"
                    + "\tIndiana \n");

            if (daoLayer.findTaxKey(state) == true) {
                stateTax = daoLayer.getTaxRateFromFile(state);
                stateKeepRunning = false;
            } else {
                console.print("Invalid selection. Please try again.");
            }
        }

        boolean nameKeepRunning = true;
        while (nameKeepRunning) {
            custName = console.readString("What is your first and last name? ");
            if ("".equals(custName)) {
                console.print("Please enter your name. ");
            } else {
                nameKeepRunning = false;
            }
        }

        double custArea = console.readDouble("What is your total flooring area in square feet? ");
        String custDate = console.readString("What is your order submission date? \n"
                + "Please enter as MMDDYYYY. ");
        double totalCostMaterials = custArea * materialCostPerSqFt;
        double totalCostLabor = custArea * laborCostPerSqFt;
        double totalCostPreTax = totalCostLabor + totalCostMaterials;
        double totalCostWithTax = totalCostPreTax + (totalCostPreTax * stateTax);
        double totalOfTax = totalCostWithTax - totalCostPreTax;

        Order aNewOrder = new Order(
                newOrderId,
                custName,
                state,
                stateTax,
                custArea,
                totalCostMaterials,
                totalCostLabor,
                totalOfTax,
                totalCostWithTax,
                custDate,
                aNewFlooringOrder);

        String userConfirm = console.readString("Please review and confirm your order: \n"
                + "You want " + custArea + " square feet of " + flooringType + " at a cost of $"
                + (df.format(totalCostMaterials)) + " for materials and $" + (df.format(totalCostLabor))
                + " for labor. \nWith tax, this order will cost $"
                + (df.format(totalCostWithTax)) + ". \nDo you wish to place this order? y/n ");

        switch (userConfirm) {
            case "y":
                console.print("Your order has been placed. Your order number is " + newOrderId
                        + " and your reference date is " + custDate + ".");
                daoLayer.postNewOrder(aNewOrder);
                break;
            case "n":
                console.print("Are you kidding you want to discard after all that work!?");
                break;
        }
    }

    public void editOrder() throws IOException {
        double materialCostPerSqFt = 0;
        double laborCostPerSqFt = 0;
        boolean editKeepRunning = true;
        Flooring aNewFlooringOrder = new Flooring();

        while (editKeepRunning) {
            double orderToEditOrderID = console.readDouble(
                    "What is the order number you wish to edit? ");
            int orderIdWasFound = 0;
            for (Order findOrderToEdit : daoLayer.listAllOrders()) {
                if (findOrderToEdit.getOrderId() == (orderToEditOrderID)) {
                    String orderToEditDate = console.readString(
                            "What is the date of the order you wish to edit?\n"
                            + "Please enter as MMDDYYYY ");
                    if ((findOrderToEdit.getOrderId() == (orderToEditOrderID))
                            && findOrderToEdit.getOrderDate().equals(orderToEditDate)) {
                        orderIdWasFound++;
                        console.print("Ok great! Let's edit order no. " + findOrderToEdit.getOrderId() + ". ");
                        console.print("Enter customer name (" + findOrderToEdit.getCustomerName() + "): ");


                        String newName = console.readString("");

                        if ("".equals(newName)) {
                            findOrderToEdit.setCustomerName(findOrderToEdit.getCustomerName());
                        } else {
                            findOrderToEdit.setCustomerName(newName);
                        }
                        boolean typeKeepRunning = true;
                        while (typeKeepRunning) {

                            console.print("What is your preferred flooring type? \n"
                                    + "Please choose from:\n"
                                    + "\tCarpet \n"
                                    + "\tLaminate \n"
                                    + "\tTile \n"
                                    + "\tWood \n"
                                    + " Your current type is " + findOrderToEdit.getFlooringObject().getFlooringType() + ". ");

                            String newFlooringType = console.readString("");

                            if ((!"".equals(newFlooringType)) && (daoLayer.findProductKey(newFlooringType) == true)) {
                                materialCostPerSqFt = daoLayer.getMaterialCostFromProductFile(newFlooringType);
                                laborCostPerSqFt = daoLayer.getLaborCostFromProductFile(newFlooringType);
                                Flooring updatedFlooringOrder = new Flooring(newFlooringType, materialCostPerSqFt, laborCostPerSqFt);
                                findOrderToEdit.setFlooringObject(updatedFlooringOrder);
                                typeKeepRunning = false;
                            } else if ("".equals(newFlooringType)) {
                                findOrderToEdit.setFlooringObject(findOrderToEdit.getFlooringObject());
                                materialCostPerSqFt = daoLayer.getMaterialCostFromProductFile(findOrderToEdit.getFlooringObject().getFlooringType());
                                laborCostPerSqFt = daoLayer.getLaborCostFromProductFile(findOrderToEdit.getFlooringObject().getFlooringType());
                                typeKeepRunning = false;
                            } else {
                                console.print("Invalid selection. Please try again.");
                            }
                        }

                        double stateTax = 0;
                        boolean stateKeepRunning = true;
                        while (stateKeepRunning) {
                            console.print("Enter state (" + findOrderToEdit.getCustomerState() + "): \n"
                                    + "Please choose from:\n"
                                    + "\tOhio \n"
                                    + "\tPennsylvania \n"
                                    + "\tMichigan \n"
                                    + "\tIndiana \n"
                                    + " Your current state is " + findOrderToEdit.getCustomerState() + ". ");

                            String newState = console.readString("");

                            if ((!"".equals(newState)) && (daoLayer.findTaxKey(newState) == true)) {
                                findOrderToEdit.setCustomerState(newState);
                                stateTax = daoLayer.getTaxRateFromFile((findOrderToEdit.getCustomerState()));
                                stateKeepRunning = false;
                            } else if ("".equals(newState)) {
                                findOrderToEdit.setCustomerState((findOrderToEdit.getCustomerState()));
                                stateKeepRunning = false;
                            } else {
                                console.print("Invalid selection. Please try again.");
                            }
                        }

                        double newArea = console.readDouble("Enter flooring area (" + findOrderToEdit.getArea() + " square feet): ");
                        if (!"".equals(String.valueOf(newArea))) {
                            findOrderToEdit.setArea(newArea);
                        } else {
                            findOrderToEdit.setArea(findOrderToEdit.getArea());
                        }
                        
                        String newDate = console.readString("Enter order submission date (" + findOrderToEdit.getOrderDate() + "):");
                        if ("".equals(newDate)) {
                            findOrderToEdit.setOrderDate(findOrderToEdit.getOrderDate());
                        } else {
                            findOrderToEdit.setOrderDate(newDate);
                        }

                        double totalCostMaterials = findOrderToEdit.getArea() * materialCostPerSqFt;
                        double totalCostLabor = findOrderToEdit.getArea() * laborCostPerSqFt;
                        double totalCostPreTax = totalCostLabor + totalCostMaterials;
                        double totalCostWithTax = totalCostPreTax + (totalCostPreTax * stateTax);
                        double totalOfTax = totalCostWithTax - totalCostPreTax;
                        Order editedOrder = new Order(
                                findOrderToEdit.getOrderId(),
                                findOrderToEdit.getCustomerName(),
                                findOrderToEdit.getCustomerState(),
                                stateTax,
                                findOrderToEdit.getArea(),
                                totalCostMaterials,
                                totalCostLabor,
                                totalOfTax,
                                totalCostWithTax,
                                findOrderToEdit.getOrderDate(),
                                aNewFlooringOrder);
                        String userConfirm = console.readString("Please review and confirm your changes: Order no. " + findOrderToEdit.getOrderId() + " is for " + findOrderToEdit.getArea() + " square feet of " + findOrderToEdit.getFlooringObject().getFlooringType() + " at a cost of $" + (df.format(totalCostMaterials)) + " for materials and $" + (df.format(totalCostLabor)) + " for labor. With tax, this order will cost $" + (df.format(totalCostWithTax)) + ". Do you wish to place this order? y/n ");
                        switch (userConfirm) {
                            case "y":
                                console.print("Your order has been changed. "
                                        + "Your order number is "
                                        + findOrderToEdit.getOrderId()
                                        + " and your reference date is " + findOrderToEdit.getOrderDate() + ".");
                                daoLayer.removeExistingFromHashMap(orderToEditOrderID);
                                daoLayer.postNewOrder(editedOrder);
                                break;
                            case "n":
                                console.print("Are you kidding you want to discard after all that work!?");
                                break;
                        }
                        editKeepRunning = false;
                        break;
                    }
                    if (orderIdWasFound == 0) {
                        console.print("No orders were found for that ID and date. ");
                    }
                }
            }
        }
    }


public void removeOrder() {
        double orderToRemoveOrderID = console.readDouble("What is the order number you wish to remove? ");
        int orderIdWasFound = 0;
        for (Order findOrderForRemoval : daoLayer.listAllOrders()) {
            if (findOrderForRemoval.getOrderId() == (orderToRemoveOrderID)) {
                String orderToRemoveDate = console.readString("What is the date of the order you wish to remove? Enter as MMDDYYYY ");
                if ((findOrderForRemoval.getOrderId() == (orderToRemoveOrderID))
                        && findOrderForRemoval.getOrderDate().equals(orderToRemoveDate)) {
                    orderIdWasFound++;
                    daoLayer.removeExistingFromHashMap(orderToRemoveOrderID);
                    console.print("Order ID " + orderToRemoveOrderID + " for " + findOrderForRemoval.getCustomerName() + " was removed. ");
                    break;
                }
            }
        }
        if (orderIdWasFound == 0) {
            console.print("No orders were found for that ID and/or date. ");
        }
    }

    private void listAllOrders() {
        if (daoLayer.ordersCount() == 0) {
            console.print("There are no active orders at this time.");
        } else {
            console.print("These are all orders in your records: ");
            for (Order anOrder : daoLayer.listAllOrders()) {
                console.print(" * " + anOrder.getOrderId() + ", "
                        + anOrder.getCustomerName() + ", "
                        + anOrder.getCustomerState() + ", "
                        + anOrder.getTaxRate() + ", "
                        + anOrder.getArea() + ", "
                        + anOrder.getMaterialCostTotal() + ", "
                        + anOrder.getLaborCostTotal() + ", "
                        + anOrder.getTaxTotal() + ", "
                        + anOrder.getCostTotal() + ", "
                        + anOrder.getOrderDate() + ", "
                        + anOrder.getFlooringObject().getFlooringType() + ", "
                        + anOrder.getFlooringObject().getMaterialCostPerSqFt() + ", "
                        + anOrder.getFlooringObject().getLaborCostPerSqFt() + ".");
            }
        }
    }
}
