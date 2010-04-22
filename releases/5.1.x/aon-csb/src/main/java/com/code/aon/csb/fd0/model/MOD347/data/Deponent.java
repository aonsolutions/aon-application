package com.code.aon.csb.fd0.model.MOD347.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The deponent
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 * 
 */
public class Deponent {

	/**
	 * The year
	 */
	private Integer year;

	/**
	 * The code
	 */
	private String code;

	/**
	 * The type
	 */
	private String type;

	/**
	 * The anme
	 */
	private String name;

	/**
	 * The relationship phone
	 */
	private Integer relPhone;

	/**
	 * The relationship name
	 */
	private String relName;

	/**
	 * Justification number
	 */
	private Integer justify;

	/**
	 * is complementary
	 */
	private boolean complementary = false;

	/**
	 * is replacement
	 */
	private boolean replaces = false;

	/**
	 * The replaced justification
	 */
	private Integer replacedJustify;

	/**
	 * Declareds list
	 */
	private ArrayList<Declared> declareds = new ArrayList<Declared>();

	/**
	 * Buildings list
	 */
	private ArrayList<Building> buildings = new ArrayList<Building>();

	/**
	 * adds a declared
	 * 
	 * @param declared
	 */
	public void addDeclared(Declared declared) {
		this.declareds.add(declared);
	}

	/**
	 * @return declareds iterator
	 */
	public Iterator<Declared> getDeclaredsIterator() {
		return this.declareds.iterator();
	}

	/**
	 * Adds a building
	 * 
	 * @param building
	 */
	public void addBuilding(Building building) {
		this.buildings.add(building);
	}

	/**
	 * @return buildings iterator
	 */
	public Iterator<Building> getBuildingsIterator() {
		return this.buildings.iterator();
	}

	/**
	 * @return the c001
	 */
	public int getC001() {
		return declareds.size();
	}

	/**
	 * @return the c002
	 */
	public double getC002() {
		double c002 = 0;
		Iterator iter = getDeclaredsIterator();
		while (iter.hasNext()) {
			Declared r = (Declared) iter.next();
			c002 += r.getQuantity();
		}
		return c002;
	}

	/**
	 * @return the c003
	 */
	public double getC003() {
		return buildings.size();
	}

	/**
	 * @return the c004
	 */
	public double getC004() {
		double c004 = 0;
		Iterator iter = getBuildingsIterator();
		while (iter.hasNext()) {
			Building r = (Building) iter.next();
			c004 += r.getQuantity();
		}
		return c004;
	}

	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * @return the complementary code
	 */
	public String getComplementary() {
		return complementary ? "C" : null;
	}

	/**
	 * @param complementary
	 *            the complementary to set
	 */
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}

	/**
	 * @return the justify
	 */
	public Integer getJustify() {
		return justify;
	}

	/**
	 * @param justify
	 *            the justify to set
	 */
	public void setJustify(Integer justify) {
		this.justify = justify;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the relName
	 */
	public String getRelName() {
		return relName;
	}

	/**
	 * @param relName
	 *            the relName to set
	 */
	public void setRelName(String relName) {
		this.relName = relName;
	}

	/**
	 * @return the relPhone
	 */
	public Integer getRelPhone() {
		return relPhone;
	}

	/**
	 * @param relPhone
	 *            the relPhone to set
	 */
	public void setRelPhone(Integer relPhone) {
		this.relPhone = relPhone;
	}

	/**
	 * @return the replacedJustify
	 */
	public Integer getReplacedJustify() {
		return replacedJustify;
	}

	/**
	 * @param replacedJustify
	 *            the replacedJustify to set
	 */
	public void setReplacedJustify(Integer replacedJustify) {
		this.replacedJustify = replacedJustify;
	}

	/**
	 * @return the replaces code
	 */
	public String getReplaces() {
		return replaces ? "S" : null;
	}

	/**
	 * @param replaces
	 *            the replaces to set
	 */
	public void setReplaces(boolean replaces) {
		this.replaces = replaces;
	}

	/**
	 * @return the year
	 */
	public Integer getYear() {
		return year;
	}

	/**
	 * @param year
	 *            the year to set
	 */
	public void setYear(Integer year) {
		this.year = year;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
