
package com.swcguild.flooringmastery.model;

import java.time.LocalDate;

public class Order {
    
    private double orderId;
    private String customerName;
    private String customerState;
    private double taxRate;
    private double area;
    private double materialCostTotal;
    private double laborCostTotal;
    private double taxTotal;
    private double costTotal;
    private String orderDate;
    private Flooring flooring;

    public Order(){}

    
    public Order(double orderId,  String customerName, String customerState, double taxRate, double area, double materialCostTotal, double laborCostTotal, double taxTotal, double costTotal, String orderDate, Flooring flooring) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.customerState = customerState;
        this.taxRate = taxRate;
        this.area = area;
        this.materialCostTotal = materialCostTotal;
        this.laborCostTotal = laborCostTotal;
        this.taxTotal = taxTotal;
        this.costTotal = costTotal;
        this.orderDate = orderDate;
        this.flooring = flooring;
    }
    
    public Flooring getFlooringObject() {
        return flooring;
    }

    public void setFlooringObject(Flooring flooringObject) {
        this.flooring = flooringObject;
    }

    public double getOrderId() {
        return orderId;
    }

    public void setOrderId(double orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public double setTaxRate(double taxRate) {
        this.taxRate = taxRate;
        return taxRate;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public double getMaterialCostTotal() {
        materialCostTotal = flooring.getMaterialCostPerSqFt() * area;
        return materialCostTotal;
    }

    public void setMaterialCostTotal(double materialCostTotal) {
        this.materialCostTotal = materialCostTotal;
    }

    public double getLaborCostTotal() {
        laborCostTotal = flooring.getLaborCostPerSqFt() * area;
        return laborCostTotal;
    }

    public void setLaborCostTotal(double laborCostTotal) {
        this.laborCostTotal = laborCostTotal;
    }

    public double getTaxTotal() {
        taxTotal = taxRate * area;
        return taxTotal;
    }

    public void setTaxTotal(double taxTotal) {
        this.taxTotal = taxTotal;
    }

    public double getCostTotal() {
        costTotal = laborCostTotal + taxTotal + materialCostTotal;
        return costTotal;
    }

    public void setCostTotal(double costTotal) {
        this.costTotal = costTotal;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    
    
    
    
    
}
