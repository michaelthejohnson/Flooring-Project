
package com.swcguild.flooringmastery.model;

public class Flooring {
    
    private String flooringType;
    private double materialCostPerSqFt;
    private double laborCostPerSqFt;

    public Flooring() {}

    public Flooring(String flooringType, double materialCostPerSqFt, double laborCostPerSqFt) {
        this.flooringType = flooringType;
        this.materialCostPerSqFt = materialCostPerSqFt;
        this.laborCostPerSqFt = laborCostPerSqFt;
    }

    public String getFlooringType() {
        return flooringType;
    }

    public void setFlooringType(String flooringType) {
        this.flooringType = flooringType;
    }

    public double getMaterialCostPerSqFt() {
        return materialCostPerSqFt;
    }

    public void setMaterialCostPerSqFt(double materialCostPerSqFt) {
        this.materialCostPerSqFt = materialCostPerSqFt;
    }

    public double getLaborCostPerSqFt() {
        return laborCostPerSqFt;
    }

    public void setLaborCostPerSqFt(double laborCostPerSqFt) {
        this.laborCostPerSqFt = laborCostPerSqFt;
    }
    
    
    
}
