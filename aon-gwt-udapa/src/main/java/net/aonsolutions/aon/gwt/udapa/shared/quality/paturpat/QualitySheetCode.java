package net.aonsolutions.aon.gwt.udapa.shared.quality.paturpat;

import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public enum QualitySheetCode implements IsSerializable{
	
	// ANALISIS CALIDAD
	
	/** ANALISIS CALIDAD - OLOR */
	PFQAC1("pfqac1"), 
	
	/** ANALISIS CALIDAD - SABOR */
	PFQAC2("pfqac2"), 
	
	/** ANALISIS CALIDAD - COLOR */
	PFQAC3("pfqac3"), 
	
	/** ANALISIS CALIDAD - TEXTURA */
	PFQAC4("pfqac4"), 
	
	/** ANALISIS CALIDAD - DEFECTOS > 3mm */
	PFQAC5("pfqac5"),
	
	/** ANALISIS CALIDAD - DEFECTOS > 3mm - APTO / NO APTO*/
	PFQAC5B("pfqac5b"),
	
	/** ANALISIS CALIDAD - DEFECTOS < 3mm */
	PFQAC6("pfqac6"), 

	/** ANALISIS CALIDAD - DEFECTOS < 3mm - APTO / NO APTO*/
	PFQAC6B("pfqac6b"),
	
	/** ANALISIS CALIDAD - PH */
	PFQAC7("pfqac7"), 
	
	/** ANALISIS CALIDAD - CONCENTRACION SAL */
	PFQAC8("pfqac8"), 
	
	/** ANALISIS CALIDAD - CONCENTRACION SAL - APTO / NO APTO*/
	PFQAC8B("pfqac8b"),
	
	/** ANALISIS CALIDAD - PESO PATATA */
	PFQAC9("pfqac9"), 
	
	/** ANALISIS CALIDAD - PESO LIQUIDO GOBIERNO */
	PFQAC10("pfqac10"), 

	/** ANALISIS CALIDAD - TEMPERATURA */
	PFQAC11("pfqac11"), 
	
	/** ANALISIS CALIDAD - DEFECTOS > 3mm - APTO / NO APTO*/
	PFQAC11B("pfqac11b"),
	
	/** ANALISIS CALIDAD - FECHA DE CADUCIDAD */
	PFQAC12("pfqac12"), 
	
	// OBSERVACIONES
	
	/** OBSERVACIONES */
	PFQO("pfqo")
	;
	
	String name;
	
	private QualitySheetCode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public static LinkedList<String> valueLinkedList(){
		LinkedList<String> list = new LinkedList<>();
		for(Integer i = 0; i < values().length; i++){
			list.add(values()[i].getName());
		}
		return list;
	}
}
