
package com.esferalia.aon.occam.api.model.fiscal.mod390;

import java.io.Serializable;

public class SimpliedRegimeActivity implements Serializable {

	private static final long serialVersionUID = 6202543820337953651L;
	
	private String epigrafe;
    private double unit1;
    private double amount1;
    private double unit2;
    private double amount2;
    private int    moduleIndex3;
    private double unit3;
    private double amount3;
    private double unit4;
    private double amount4;
    private double unit5;
    private double amount5;
    private double unit6;
    private double amount6;
    private double unit7;
    private double amount7;
    private double boxC;
    private double boxC1;  // Reducción Lorca
    private double boxC2;  // Reducción DANA 2024
    private double boxD;
    private double boxE;
    private double boxF;
    private double boxG;
    private double boxH;
    private double boxI;
    private double boxJ;
    
	public String getEpigrafe() {
		return epigrafe;
	}
	public SimpliedRegimeActivity setEpigrafe(String epigrafe) {
		this.epigrafe = epigrafe;
		return this;
	}
	
	public double getUnit1() {
		return unit1;
	}
	public SimpliedRegimeActivity setUnit1(double unit1) {
		this.unit1 = unit1;
		return this;
	}
	
	public double getAmount1() {
		return amount1;
	}
	public SimpliedRegimeActivity setAmount1(double amount1) {
		this.amount1 = amount1;
		return this;
	}
	
	public double getUnit2() {
		return unit2;
	}
	public SimpliedRegimeActivity setUnit2(double unit2) {
		this.unit2 = unit2;
		return this;
	}
	
	public double getAmount2() {
		return amount2;
	}
	public SimpliedRegimeActivity setAmount2(double amount2) {
		this.amount2 = amount2;
		return this;
	}
	
	public int getModuleIndex3() {
		return moduleIndex3;
	}
	public SimpliedRegimeActivity setModuleIndex3(int moduleIndex3) {
		this.moduleIndex3 = moduleIndex3;
		return this;
	}
	
	public double getUnit3() {
		return unit3;
	}
	public SimpliedRegimeActivity setUnit3(double unit3) {
		this.unit3 = unit3;
		return this;
	}
	
	public double getAmount3() {
		return amount3;
	}
	public SimpliedRegimeActivity setAmount3(double amount3) {
		this.amount3 = amount3;
		return this;
	}
	
	public double getUnit4() {
		return unit4;
	}
	public SimpliedRegimeActivity setUnit4(double unit4) {
		this.unit4 = unit4;
		return this;
	}
	
	public double getAmount4() {
		return amount4;
	}
	public SimpliedRegimeActivity setAmount4(double amount4) {
		this.amount4 = amount4;
		return this;
	}
	
	public double getUnit5() {
		return unit5;
	}
	public SimpliedRegimeActivity setUnit5(double unit5) {
		this.unit5 = unit5;
		return this;
	}
	
	public double getAmount5() {
		return amount5;
	}
	public SimpliedRegimeActivity setAmount5(double amount5) {
		this.amount5 = amount5;
		return this;
	}
	
	public double getUnit6() {
		return unit6;
	}
	public SimpliedRegimeActivity setUnit6(double unit6) {
		this.unit6 = unit6;
		return this;
	}
	
	public double getAmount6() {
		return amount6;
	}
	public SimpliedRegimeActivity setAmount6(double amount6) {
		this.amount6 = amount6;
		return this;
	}
	
	public double getUnit7() {
		return unit7;
	}
	public SimpliedRegimeActivity setUnit7(double unit7) {
		this.unit7 = unit7;
		return this;
	}
	
	public double getAmount7() {
		return amount7;
	}
	public SimpliedRegimeActivity setAmount7(double amount7) {
		this.amount7 = amount7;
		return this;
	}
	
	public double getBoxC() {
		return boxC;
	}
	public SimpliedRegimeActivity setBoxC(double boxC) {
		this.boxC = boxC;
		return this;
	}
	
	public double getBoxC1() {
		return boxC1;
	}
	public SimpliedRegimeActivity setBoxC1(double boxC1) {
		this.boxC1 = boxC1;
		return this;
	}
	
	public double getBoxD() {
		return boxD;
	}
	public SimpliedRegimeActivity setBoxD(double boxD) {
		this.boxD = boxD;
		return this;
	}
	
	public double getBoxE() {
		return boxE;
	}
	public SimpliedRegimeActivity setBoxE(double boxE) {
		this.boxE = boxE;
		return this;
	}
	
	public double getBoxF() {
		return boxF;
	}
	public SimpliedRegimeActivity setBoxF(double boxF) {
		this.boxF = boxF;
		return this;
	}
	
	public double getBoxG() {
		return boxG;
	}
	public SimpliedRegimeActivity setBoxG(double boxG) {
		this.boxG = boxG;
		return this;
	}
	
	public double getBoxH() {
		return boxH;
	}
	public SimpliedRegimeActivity setBoxH(double boxH) {
		this.boxH = boxH;
		return this;
	}
	
	public double getBoxI() {
		return boxI;
	}
	public SimpliedRegimeActivity setBoxI(double boxI) {
		this.boxI = boxI;
		return this;
	}
	
	public double getBoxJ() {
		return boxJ;
	}
	public SimpliedRegimeActivity setBoxJ(double boxJ) {
		this.boxJ = boxJ;
		return this;
	}
	
	public SimpliedRegimeActivity setUnit(int line, double value) {
		if (line == 1) setUnit1(value);
		else if (line == 2) setUnit2(value);
		else if (line == 3) setUnit3(value);
		else if (line == 4) setUnit4(value);
		else if (line == 5) setUnit5(value);
		else if (line == 6) setUnit6(value);
		else if (line == 7) setUnit7(value);
		return this;
	}
	public SimpliedRegimeActivity setAmount(int line, double value) {
		if (line == 1) setAmount1(value);
		else if (line == 2) setAmount2(value);
		else if (line == 3) setAmount3(value);
		else if (line == 4) setAmount4(value);
		else if (line == 5) setAmount5(value);
		else if (line == 6) setAmount6(value);
		else if (line == 7) setAmount7(value);
		return this;
	}
	
	public double getBoxC2() {
		return boxC2;
	}
	public SimpliedRegimeActivity setBoxC2(double boxC2) {
		this.boxC2 = boxC2;
		return this;
	}
}
