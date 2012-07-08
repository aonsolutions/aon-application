package com.code.aon.file.tax.model.MOD349.data;

import java.util.LinkedList;
import java.util.List;

public class Deponent {

	private Integer year;
	private String period;
	private String province;
	private String document;
	private String type;
	private String name;
	private Integer relPhone;
	private String relName;
	private Long number;
	private boolean complementary = false;
	private boolean replacement = false;
	private Long replacedNumber;
	private List<Operator> operators;
	private List<Rectification> rectifications;

	public int getC001() {
		return getOperators().size();
	}

	public double getC002() {
		double c002 = 0;
		for (Operator r: getOperators()) {
			c002 += r.getAmount();
		}
		return c002;
	}
	
	public double getC003() {
		return getRectifications().size();
	}
	
	public double getC004() {
		double c004 = 0;
		for (Operator r: getOperators()) {
			c004 += r.getAmount();
		}
		return c004;
	}
	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	public String getComplementary() {
		return complementary ? "C" : null;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}

	public Long getNumber() {
		return number;
	}
	public void setNumber(Long number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getRelName() {
		return relName;
	}
	public void setRelName(String relName) {
		this.relName = relName;
	}

	public Integer getRelPhone() {
		return relPhone;
	}
	public void setRelPhone(Integer relPhone) {
		this.relPhone = relPhone;
	}

	public Long getReplacedNumber() {
		return replacedNumber;
	}
	public void setReplacedNumber(Long replacedNumber) {
		this.replacedNumber = replacedNumber;
	}

	public boolean isExtraDeclaration() {
		return (replacement || complementary);
	}

	public String getReplacement() {
		return replacement ? "S" : null;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}

	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer("Registro Declarante ");
		buf.append("EJERCICIO: ");
		buf.append(year == null ? "Vacio" : year.toString());
		buf.append(" DOCUMENTO: ");
		buf.append(document == null ? "Vacio" : document);
		buf.append(" NOMBRE: ");
		buf.append( name == null ? "Vacio" : name);
		return buf.toString();
	}

	public List<Operator> getOperators() {
		if (this.operators == null) {
			this.operators = new LinkedList<Operator>();
		}
		return this.operators;
	}
	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}

	public List<Rectification> getRectifications() {
		if (this.rectifications == null) {
			this.rectifications = new LinkedList<Rectification>();
		}
		return this.rectifications;
	}
	public void setRectifications(List<Rectification> rectifications) {
		this.rectifications = rectifications;
	}

}
