package com.esferalia.aon.in.payroll.pdf.maker.enterprisepayroll.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ColManager {
	
	private static final int MAX_LENGTH = 13;

	private HashMap<String, Integer> indexes;
	private int[] entriesPerColumn;
	private boolean[] enabled;
	
	private double[] subtotalSs; 
	private double[] subtotalAon; 
	private double[] totalSs;
	private double[] totalAon;
	
	public ColManager() {
		
		entriesPerColumn = new int[MAX_LENGTH];
		enabled = new boolean[MAX_LENGTH];
		indexes = new HashMap<>();
		
		subtotalAon = new double[MAX_LENGTH];
		subtotalSs = new double[MAX_LENGTH];
		totalAon = new double[MAX_LENGTH];
		totalSs = new double[MAX_LENGTH];
		
		indexes.put("trabajador", 0);
		indexes.put("tipo", 1);
		indexes.put("devengado", 2);
		indexes.put("ssTrab", 3);
		indexes.put("irpf", 4);
		indexes.put("Otr. ded.", 5);
		indexes.put("inKind", 6);
		indexes.put("liquido", 7);
		indexes.put("ssEmpr", 8);
		indexes.put("bonificaciones", 9);
		indexes.put("fundae", 10);
		indexes.put("ssTotal", 11);
		indexes.put("costeTotal", 12);

	}
	
	public Integer entriesForCol(String col) {
		if(indexes.get(col) != null) 
			return entriesPerColumn[indexes.get(col)];
		return null;
	}
	
	public void enable(String col) {
		if(indexes.get(col) != null) 
			enabled[indexes.get(col)] = true;
	}
	
	public boolean isActive(String col) {
		if(indexes.get(col) == null) return false;
		return enabled[indexes.get(col)];
	}
	
	public void add(EnterprisePayrollEntry e) {
		if(e.empleado.isPresent() || e.empleadoSS.isPresent())
			entriesPerColumn[0]++;
		if(e.tipo.isPresent() || e.tipoSS.isPresent())
			entriesPerColumn[1]++;
		if(e.devengado.isPresent() || e.devengadoSS.isPresent())
			entriesPerColumn[2]++;
		if(e.ssTrab.isPresent() || e.ssTrabSS.isPresent())
			entriesPerColumn[3]++;
		if(e.irpf.isPresent() || e.irpfSS.isPresent())
			entriesPerColumn[4]++;
		if(e.deducciones.isPresent() || e.deduccionesSS.isPresent())
			entriesPerColumn[5]++;
		if(e.inKind.isPresent() || e.inKind.isPresent())
			entriesPerColumn[6]++;
		if(e.liquido.isPresent() || e.liquidoSS.isPresent())
			entriesPerColumn[7]++;
		if(e.ssEmpr.isPresent() || e.ssEmprSS.isPresent())
			entriesPerColumn[8]++;
		if(e.bonificaciones.isPresent() || e.bonificacionesSS.isPresent() )
			entriesPerColumn[9]++;
		if(e.fundae.isPresent())
			entriesPerColumn[10]++;
		if(e.ssTotal.isPresent() || e.ssTotalSS.isPresent() )
			entriesPerColumn[11]++;
		if(e.costeTotal.isPresent() || e.costeTotalSS.isPresent())
			entriesPerColumn[12]++;
	}
	
	public void showEnabled() {
		//	System.out.println(Arrays.toString(enabled));
	}
	
	public int countEnabled() {
		int c = 0;
		for (int i = 0; i < enabled.length; i++)
			if(enabled[i])
				c++;
		return c;
	}
	
	public void addToSubtotal(EnterprisePayrollEntry e) {
		
		subtotalAon[2]  += e.devengado.orElse(0.00);
		subtotalAon[3]  += e.ssTrab.orElse(0.00);
		subtotalAon[4]  += e.irpf.orElse(0.00);
		subtotalAon[5]  += e.deducciones.orElse(0.00);
		subtotalAon[6]  += e.inKind.orElse(0.00);
		subtotalAon[7]  += e.liquido.orElse(0.00);
		subtotalAon[8]  += e.ssEmpr.orElse(0.00);
		subtotalAon[9]  += e.bonificaciones.orElse(0.00);
		subtotalAon[10]  += e.fundae.orElse(0.00);
		subtotalAon[11] += e.ssTotal.orElse(0.00) + e.fundae.orElse(0.00);
		subtotalAon[12] += e.costeTotal.orElse(0.00);
		
		subtotalSs[2]  += 	e.devengadoSS.orElse(0.00);
		subtotalSs[3]  += 	e.ssTrabSS.orElse(0.00);
		subtotalSs[4]  += 	e.irpfSS.orElse(0.00);
		subtotalSs[5]  += 	e.deduccionesSS.orElse(0.00);
		subtotalSs[7]  += 	e.liquidoSS.orElse(0.00);
		subtotalSs[8]  += 	e.ssEmprSS.orElse(0.00);
		subtotalSs[9]  += 	e.bonificacionesSS.orElse(0.00);
		subtotalSs[11] += 	e.ssTotalSS.orElse(0.00);
		subtotalSs[12] += 	e.costeTotalSS.orElse(0.00);
				
	}
	
	public void manageSubtotalWithFudae() {
		if (Arrays.stream(subtotalSs).anyMatch(value -> value != 0)) {
			subtotalSs[10] += subtotalAon[9]; 
		}
	}
	
	public void addToTotal() {
		
		totalAon[2]  +=  subtotalAon[2];
		totalAon[3]  +=  subtotalAon[3];
		totalAon[4]  +=  subtotalAon[4];
		totalAon[5]  +=  subtotalAon[5];
		totalAon[6]  +=  subtotalAon[6];
		totalAon[7]  +=  subtotalAon[7];
		totalAon[8]  +=  subtotalAon[8];
		totalAon[9]  +=  subtotalAon[9];
		totalAon[10] +=  subtotalAon[10];
		totalAon[11] +=  subtotalAon[11];
		totalAon[12] +=  subtotalAon[12];
		
		totalSs[2]  +=  subtotalSs[2];
		totalSs[3]  +=  subtotalSs[3];
		totalSs[4]  +=  subtotalSs[4];
		totalSs[5]  +=  subtotalSs[5];
		totalSs[6]  +=  subtotalSs[7];
		totalSs[7]  +=  subtotalSs[8];
		totalSs[8]  +=  subtotalSs[9];
		totalSs[9]  +=  subtotalSs[10];
		totalSs[10] +=  subtotalSs[11];
		totalSs[11] +=  subtotalSs[12];
				
		subtotalAon = new double[MAX_LENGTH];
		subtotalSs = new double[MAX_LENGTH];
	}

	public ArrayList<Double> getSsSubtotal() {
		ArrayList<Double> arr =  new ArrayList<>();
		
		for (int i = 0; i < subtotalSs.length; i++) 
			if(enabled[i]) arr.add(subtotalSs[i]);
			
		return arr;
	}
	
	public ArrayList<Double> getAonSubtotal() {
		ArrayList<Double> arr = new ArrayList<>();
		
		for (int i = 0; i < subtotalAon.length; i++) 
			if(enabled[i]) arr.add(subtotalAon[i]);
		
		return arr;
	}
	
	public ArrayList<Double> getSsTotal() {
		ArrayList<Double> arr = new ArrayList<>();
		
		for (int i = 0; i < totalSs.length; i++) 
			if(enabled[i]) arr.add(totalSs[i]);
			
		return arr;
	}
	
	public ArrayList<Double> getAonTotal() {
		ArrayList<Double> arr = new ArrayList<>();
	
		for (int i = 0; i < totalAon.length; i++) 
			if(enabled[i]) arr.add(totalAon[i]);
			
		return arr;
	}
	
}
