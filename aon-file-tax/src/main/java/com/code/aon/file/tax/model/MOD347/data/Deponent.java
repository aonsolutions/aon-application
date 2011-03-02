package com.code.aon.file.tax.model.MOD347.data;

import java.util.LinkedList;
import java.util.List;

public class Deponent {

	private Integer year;
	private String province;
	private String code;
	private String type;
	private String name;
	private Integer relPhone;
	private String relName;
	private Long number;
	private boolean complementary = false;
	private boolean replacement = false;
	private Long replacedNumber;
	private List<Declared> declareds;

	public int getC001() {
		return getDeclareds().size();
	}

	public double getC002() {
		double c002 = 0;
		for (Declared r: getDeclareds()) {
			c002 += r.getQuantity();
		}
		return c002;
	}
	public double getC003() {
		return 0.0;
	}
	public double getC004() {
		return 0.0;
	}
	public double getC005() {
		return 0.0;
	}
	public double getC006() {
		return 0.0;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
		buf.append(code == null ? "Vacio" : code);
		buf.append(" NOMBRE: ");
		buf.append( name == null ? "Vacio" : name);
		return buf.toString();
	}

	public List<Declared> getDeclareds() {
		if (this.declareds == null) {
			this.declareds = new LinkedList<Declared>();
		}
		return this.declareds;
	}
	public void setDeclareds(List<Declared> declareds) {
		this.declareds = declareds;
	}

}
