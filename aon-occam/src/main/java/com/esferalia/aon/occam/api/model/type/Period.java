package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum Period implements Serializable {

	M01(0,0,"01","Enero"),
	M02(1,1,"02","Febrero"),
	M03(2,2,"03","Marzo"),
	M04(3,3,"04","Abril"),
	M05(4,4,"05","Mayo"),
	M06(5,5,"06","Junio"),
	M07(6,6,"07","Julio"),
	M08(7,7,"08","Agosto"),
	M09(8,8,"09","Septiembre"),
	M10(9,9,"10","Octubre"),
	M11(10,10,"11","Noviembre"),
	M12(11,11,"12","Diciembre"),
	T1(0,2,"T1","1\u00AA Trim."),	//12
	T2(3,5,"T2","2\u00AA Trim."),	//13
	T3(6,8,"T3","3\u00AA Trim."),	//14
	T4(9,11,"T4","4\u00AA Trim."),	//15
	YEAR(0,11,"An","Anual"); //16
	
 	private int startMonth;
	private int dueMonth;
	private String name;
	private String description;
	
	private Period(int startMonth,int dueMonth,String name,String description) {
		this.startMonth= startMonth;
		this.dueMonth= dueMonth;
		this.name = name;
		this.description= description;
	}

    public String getName() {
    	return name;
    }
    public String getName(Administration admon) {
    	if (admon == Administration.COMMON_TERRITORY) {
    		if (this == T1) return "1T";
    		if (this == T2) return "2T";
    		if (this == T3) return "3T";
    		if (this == T4) return "4T";
    	}
    	return name;
    }
    
    public String getDescription() {
    	return description;
	}
    
	public int getStartMonth() {
		return startMonth;
	}
	public void setStartMonth(int startMonth) {
		this.startMonth = startMonth;
	}

	public int getDueMonth() {
		return dueMonth;
	}
	public void setDueMonth(int dueMonth) {
		this.dueMonth = dueMonth;
	}

	public static Period getMonthlyPeriod(int month) {
		if (month==0) return M01;
		else if (month==1) return M02;
		else if (month==2) return M03;
		else if (month==3) return M04;
		else if (month==4) return M05;
		else if (month==5) return M06;
		else if (month==6) return M07;
		else if (month==7) return M08;
		else if (month==8) return M09;
		else if (month==9) return M10;
		else if (month==10) return M11;
		else if (month==11) return M12;
		throw new IllegalArgumentException("Invalid month!");
	}
	public static Period getQuarterlyPeriod(int month) {
		if (month>=0 && month<3) return T1;
		else if (month>=3 && month<6) return T2;
		else if (month>=6 && month<9) return T3;
		else if (month>=9 && month<12) return T4;
		throw new IllegalArgumentException("Invalid month!");
	}
	
	public boolean isQuarterPeriod() {
		return (this == T1 || this == T2 || this == T3 || this == T4);
	}
	
	public boolean isMonthPeriod() {
		return (this.ordinal() < 12);
	}

	public byte getValue() {
		return (byte) ordinal();
	}

}