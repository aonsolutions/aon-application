package com.code.aon.fiscal.model303;

import java.util.HashMap;
import java.util.Map;

import com.code.aon.fiscal.enumeration.Model303Column;
import com.code.aon.fiscal.enumeration.Model303Key;


public class Model303 {
	private Model303Key key;
	private Integer box;	// Numero que aparece a la izquierda en la declaracion
	private double percent;
	private Map<Model303Column,Model303Detail> map;

	public Model303() {
		map = new HashMap<Model303Column,Model303Detail>();
		map.put(Model303Column.ACUMULADO, new Model303Detail()); 
		map.put(Model303Column.DECLARADO, new Model303Detail());
		map.put(Model303Column.RESULTADO, new Model303Detail()); 
		map.put(Model303Column.DECLARAR, new Model303Detail()); 
		map.put(Model303Column.AJUSTE, new Model303Detail()); 
	}
	public Model303(Model303Key key) {
		this();
		this.key = key;
	}
	
	public Model303Key getKey() {
		return key;
	}
	public void setKey(Model303Key key) {
		this.key = key;
	}
	
	public Integer getBox() {
		return box;
	}
	public void setBox(Integer box) {
		this.box = box;
	}

	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
	
	public Map<Model303Column,Model303Detail> getDetailMap() {
		 return map;
	}

	public double getAcumuladoTaxableBase() {
		return map.get(Model303Column.ACUMULADO).getTaxableBase();
	}
	public void setAcumuladoTaxableBase(double t) {
		map.get(Model303Column.ACUMULADO).setTaxableBase(t);
	}

	public double getDeclaradoTaxableBase() {
		return map.get(Model303Column.DECLARADO).getTaxableBase();
	}
	public void setDeclaradoTaxableBase(double t) {
		map.get(Model303Column.DECLARADO).setTaxableBase(t);
	}

	public double getResultadoTaxableBase() {
		return map.get(Model303Column.RESULTADO).getTaxableBase();
	}
	public void setResultadoTaxableBase(double t) {
		map.get(Model303Column.RESULTADO).setTaxableBase(t);
	}

	public double getDeclararTaxableBase() {
		return map.get(Model303Column.DECLARAR).getTaxableBase();
	}
	public void setDeclararTaxableBase(double t) {
		map.get(Model303Column.DECLARAR).setTaxableBase(t);
	}

	public double getAjusteTaxableBase() {
		return map.get(Model303Column.AJUSTE).getTaxableBase();
	}
	public void setAjusteTaxableBase(double t) {
		map.get(Model303Column.AJUSTE).setTaxableBase(t);
	}

	public double getAcumuladoQuota() {
		return map.get(Model303Column.ACUMULADO).getQuota();
	}
	public void setAcumuladoQuota(double t) {
		map.get(Model303Column.ACUMULADO).setQuota(t);
	}

	public double getDeclaradoQuota() {
		return map.get(Model303Column.DECLARADO).getQuota();
	}
	public void setDeclaradoQuota(double t) {
		map.get(Model303Column.DECLARADO).setQuota(t);
	}

	public double getResultadoQuota() {
		return map.get(Model303Column.RESULTADO).getQuota();
	}
	public void setResultadoQuota(double t) {
		map.get(Model303Column.RESULTADO).setQuota(t);
	}

	public double getDeclararQuota() {
		return map.get(Model303Column.DECLARAR).getQuota();
	}
	public void setDeclararQuota(double t) {
		map.get(Model303Column.DECLARAR).setQuota(t);
	}

	public double getAjusteQuota() {
		return map.get(Model303Column.AJUSTE).getQuota();
	}
	public void setAjusteQuota(double t) {
		map.get(Model303Column.AJUSTE).setQuota(t);
	}
	
	
}
