package com.code.aon.file.tax.model.MOD347.data;

import java.util.LinkedList;
import java.util.List;

public class Deponent {

	private Integer year;
	private String code;
	private String type;
	private String name;
	private Integer relPhone;
	private String relName;
	private Integer justify;
	private boolean complementary = false;
	private boolean replaces = false;
	private Integer replacedJustify;
	private List<Declared> declareds;
	private List<Building> buildings;

	public List<Declared> getDeclareds() {
		if (this.declareds == null) {
			this.declareds = new LinkedList<Declared>();
		}
		return this.declareds;
	}
	public void setDeclareds(List<Declared> declareds) {
		this.declareds = declareds;
	}

	public List<Building> getBuildings() {
		if (this.buildings == null) {
			this.buildings = new LinkedList<Building>();
		}
		return this.buildings;
	}
	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

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
		return getBuildings().size();
	}
	public double getC004() {
		double c004 = 0;
		for (Building r: getBuildings()) {
			c004 += r.getQuantity();
		}
		return c004;
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

	public Integer getJustify() {
		return justify;
	}
	public void setJustify(Integer justify) {
		this.justify = justify;
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

	public Integer getReplacedJustify() {
		return replacedJustify;
	}
	public void setReplacedJustify(Integer replacedJustify) {
		this.replacedJustify = replacedJustify;
	}

	public String getReplaces() {
		return replaces ? "S" : null;
	}
	public void setReplaces(boolean replaces) {
		this.replaces = replaces;
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

	public String toString() {
		String description = "DEPONENT ";
		description += "YEAR ";
		description += year == null ? "NULL " : "'" + year.toString() + "'";
		description += "CODE ";
		description += code == null ? "NULL " : "'" + code + "''";
		description += "NAME ";
		description += name == null ? "NULL " : "'" + name + "'; ";
		return description;
	}

}
