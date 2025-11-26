
package com.esferalia.aon.occam.api.model.fiscal.mod425;

import java.io.Serializable;

public class SimpliedRegimeActivity425 implements Serializable {

	private static final long serialVersionUID = 228587060914869702L;
	
	private String epigrafe;  // Epígrafe IAE/Código
	private String sector;    // Sector (para los epígrafes 722 y 691.9 
    private double unit1;     // Unidades módulo (1)
    private double unit2;     // Unidades módulo (2)
    private double unit3;     // Unidades módulo (3)
    private double unit4;     // Unidades módulo (4)
    private double unit5;     // Unidades módulo (5)
    private double unit6;     // Unidades módulo (6)
    private double unit7;     // Unidades módulo (7)
    
    // EN EL XML SOLO SE PONEN LAS UNIDADES, EN PANTALLA SE PODRIA MOSTRAR IMPORTE/UNITARIO Y TOTAL, 
    // PERO HABRIA QUE TENER EN ALGUN SITIO LOS IMPORTES UNITARIOS DE CADA ACTIVIDAD Y HACER LOS CALCULOS
    // POR AHORA SE PONEN SOLO LAS UNIDADES, PUES EN EL MODELO 390 AEAT ES COMPLETAMENTE MANUAL
//    private double amount1;    
//    private double amount2;
//    private double amount3;
//    private double amount4;
//    private double amount5;
//    private double amount6;
//    private double amount7;
    
    private double boxA; // Cuota anual devengada por operaciones corrientes
    private double boxB; // Cuotas soportadas por operaciones corrientes
    private double boxC; // Indice corrector
    private double boxD; // Diferencia
    private double boxE; // Porcentaje cuota mínima operaciones corrientes
    private double boxF; // Cuota mínima    
    private double boxG; // Cuota anual derivada de régimen simplificado
    
	public String getEpigrafe() {
		return epigrafe;
	}
	public SimpliedRegimeActivity425 setEpigrafe(String epigrafe) {
		this.epigrafe = epigrafe;
		return this;
	}
	
	public String getSector() {
		return sector;
	}
	public SimpliedRegimeActivity425 setSector(String sector) {
		this.sector = sector;
		return this;
	}
	
	public double getUnit1() {
		return unit1;
	}
	public SimpliedRegimeActivity425 setUnit1(double unit1) {
		this.unit1 = unit1;
		return this;
	}
	
	public double getUnit2() {
		return unit2;
	}
	public SimpliedRegimeActivity425 setUnit2(double unit2) {
		this.unit2 = unit2;
		return this;
	}
	
	public double getUnit3() {
		return unit3;
	}
	public SimpliedRegimeActivity425 setUnit3(double unit3) {
		this.unit3 = unit3;
		return this;
	}
	
	public double getUnit4() {
		return unit4;
	}
	public SimpliedRegimeActivity425 setUnit4(double unit4) {
		this.unit4 = unit4;
		return this;
	}
	
	public double getUnit5() {
		return unit5;
	}
	public SimpliedRegimeActivity425 setUnit5(double unit5) {
		this.unit5 = unit5;
		return this;
	}
	
	public double getUnit6() {
		return unit6;
	}
	public SimpliedRegimeActivity425 setUnit6(double unit6) {
		this.unit6 = unit6;
		return this;
	}
	
	public double getUnit7() {
		return unit7;
	}
	public SimpliedRegimeActivity425 setUnit7(double unit7) {
		this.unit7 = unit7;
		return this;
	}
	
	public double getBoxA() {
		return boxA;
	}
	public SimpliedRegimeActivity425 setBoxA(double boxA) {
		this.boxA = boxA;
		return this;
	}
	public double getBoxB() {
		return boxB;
	}
	public SimpliedRegimeActivity425 setBoxB(double boxB) {
		this.boxB = boxB;
		return this;
	}
	public double getBoxC() {
		return boxC;
	}
	public SimpliedRegimeActivity425 setBoxC(double boxC) {
		this.boxC = boxC;
		return this;
	}
	
	public double getBoxD() {
		return boxD;
	}
	public SimpliedRegimeActivity425 setBoxD(double boxD) {
		this.boxD = boxD;
		return this;
	}
	
	public double getBoxE() {
		return boxE;
	}
	public SimpliedRegimeActivity425 setBoxE(double boxE) {
		this.boxE = boxE;
		return this;
	}
	
	public double getBoxF() {
		return boxF;
	}
	public SimpliedRegimeActivity425 setBoxF(double boxF) {
		this.boxF = boxF;
		return this;
	}
	
	public double getBoxG() {
		return boxG;
	}
	public SimpliedRegimeActivity425 setBoxG(double boxG) {
		this.boxG = boxG;
		return this;
	}
	
//	public SimpliedRegimeActivity425 setUnit(int line, double value) {
//		if (line == 1) setUnit1(value);
//		else if (line == 2) setUnit2(value);
//		else if (line == 3) setUnit3(value);
//		else if (line == 4) setUnit4(value);
//		else if (line == 5) setUnit5(value);
//		else if (line == 6) setUnit6(value);
//		else if (line == 7) setUnit7(value);
//		return this;
//	}
	
}
