package com.esferalia.aon.in.payroll.pdf.creators.enterprise_payroll.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ColManager {

	private HashMap<String, Integer> indexes;
	private int[] entries_per_column;
	private boolean[] enabled;
	
	private double[] subtotal_ss; 
	private double[] subtotal_aon; 
	private double[] total_ss;
	private double[] total_aon;
	
	public ColManager() {

		entries_per_column = new int[11];
		enabled = new boolean[11];
		indexes = new HashMap<String, Integer>();
		
		subtotal_aon = new double[11];
		subtotal_ss = new double[11];
		total_aon = new double[11];
		total_ss = new double[11];
		
		indexes.put("trabajador", 0);
		indexes.put("tipo", 1);
		indexes.put("devengado", 2);
		indexes.put("ssTrab", 3);
		indexes.put("irpf", 4);
		indexes.put("deducciones", 5);
		indexes.put("liquido", 6);
		indexes.put("ssEmpr", 7);
		indexes.put("bonificaciones", 8);
		indexes.put("ssTotal", 9);
		indexes.put("costeTotal", 10);

	}
	
	public Integer entries_for_col(String col) {
		if(indexes.get(col) != null) 
			return entries_per_column[indexes.get(col)];
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
		if(e.empleado.isPresent() || e.empleadoSS.isPresent()) 					entries_per_column[0]++;
		if(e.tipo.isPresent() || e.tipoSS.isPresent()) 							entries_per_column[1]++;
		if(e.devengado.isPresent() || e.devengadoSS.isPresent()) 				entries_per_column[2]++;
		if(e.ssTrab.isPresent() || e.ssTrabSS.isPresent()) 						entries_per_column[3]++;
		if(e.irpf.isPresent() || e.irpfSS.isPresent()) 							entries_per_column[4]++;
		if(e.deducciones.isPresent() || e.deduccionesSS.isPresent()) 			entries_per_column[5]++;
		if(e.liquido.isPresent() || e.liquidoSS.isPresent()) 					entries_per_column[6]++;
		if(e.ssEmpr.isPresent() || e.ssEmprSS.isPresent() ) 					entries_per_column[7]++;
		if(e.bonificaciones.isPresent() || e.bonificacionesSS.isPresent() ) 	entries_per_column[8]++;
		if(e.ssTotal.isPresent() || e.ssTotalSS.isPresent() ) 					entries_per_column[9]++;
		if(e.costeTotal.isPresent() || e.costeTotalSS.isPresent() ) 			entries_per_column[10]++;
	}
	
	public void show_enabled() {
		System.out.println(Arrays.toString(enabled));
	}
	
	public int count_enabled() {
		int c = 0;
		for (int i = 0; i < enabled.length; i++) if(enabled[i]) c++;
		return c;
	}
	
	public void add_to_subtotal(EnterprisePayrollEntry e) {
		
		subtotal_aon[2]  += e.devengado.orElse(0.00);
		subtotal_aon[3]  += e.ssTrab.orElse(0.00);
		subtotal_aon[4]  += e.irpf.orElse(0.00);
		subtotal_aon[5]  += e.deducciones.orElse(0.00);
		subtotal_aon[6]  += e.liquido.orElse(0.00);
		subtotal_aon[7]  += e.ssEmpr.orElse(0.00);
		subtotal_aon[8]  += e.bonificaciones.orElse(0.00);
		subtotal_aon[9]  += e.ssTotal.orElse(0.00);
		subtotal_aon[10] += e.costeTotal.orElse(0.00);
		
		subtotal_ss[2]  += 	e.devengadoSS.orElse(0.00);
		subtotal_ss[3]  += 	e.ssTrabSS.orElse(0.00);
		subtotal_ss[4]  += 	e.irpfSS.orElse(0.00);
		subtotal_ss[5]  += 	e.deduccionesSS.orElse(0.00);
		subtotal_ss[6]  += 	e.liquidoSS.orElse(0.00);
		subtotal_ss[7]  += 	e.ssEmprSS.orElse(0.00);
		subtotal_ss[8]  += 	e.bonificacionesSS.orElse(0.00);
		subtotal_ss[9]  += 	e.ssTotalSS.orElse(0.00);
		subtotal_ss[10] += 	e.costeTotalSS.orElse(0.00);
				
	}
	public void add_to_total() {
		
		total_aon[2]  +=  subtotal_aon[2];
		total_aon[3]  +=  subtotal_aon[3];
		total_aon[4]  +=  subtotal_aon[4];
		total_aon[5]  +=  subtotal_aon[5];
		total_aon[6]  +=  subtotal_aon[6];
		total_aon[7]  +=  subtotal_aon[7];
		total_aon[8]  +=  subtotal_aon[8];
		total_aon[9]  +=  subtotal_aon[9];
		total_aon[10] +=  subtotal_aon[10];
		
		total_ss[2]  +=  subtotal_ss[2];
		total_ss[3]  +=  subtotal_ss[3];
		total_ss[4]  +=  subtotal_ss[4];
		total_ss[5]  +=  subtotal_ss[5];
		total_ss[6]  +=  subtotal_ss[6];
		total_ss[7]  +=  subtotal_ss[7];
		total_ss[8]  +=  subtotal_ss[8];
		total_ss[9]  +=  subtotal_ss[9];
		total_ss[10] +=  subtotal_ss[10];
				
		subtotal_aon = new double[11];
		subtotal_ss = new double[11];
	}

	public ArrayList<Double> get_ss_subtotal() {
		ArrayList<Double> arr =  new ArrayList<>();
		
		for (int i = 0; i < subtotal_ss.length; i++) 
			if(enabled[i]) arr.add(subtotal_ss[i]);
			
		return arr;
	}
	
	public ArrayList<Double> get_aon_subtotal() {
		ArrayList<Double> arr = new ArrayList<>();
		
		for (int i = 0; i < subtotal_aon.length; i++) 
			if(enabled[i]) arr.add(subtotal_aon[i]);
		
		return arr;
	}
	
	public ArrayList<Double> get_ss_total() {
		ArrayList<Double> arr = new ArrayList<>();
		
		for (int i = 0; i < total_ss.length; i++) 
			if(enabled[i]) arr.add(total_ss[i]);
			
		return arr;
	}
	
	public ArrayList<Double> get_aon_total() {
		ArrayList<Double> arr = new ArrayList<>();
	
		for (int i = 0; i < total_aon.length; i++) 
			if(enabled[i]) arr.add(total_aon[i]);
			
		return arr;
	}
	
}
