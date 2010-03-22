package com.code.aon.file.tax.model.MOD349.data;

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
	 * The name
	 */
	private String name;

	/**
	 * The relation person phone
	 */
	private Integer relPhone;

	/**
	 * The relation person name
	 */
	private String relName;

	/**
	 * The justification number
	 */
	private Integer justify;

	/**
	 * Is complementary
	 */
	private boolean complementary = false;

	/**
	 * Is replaces
	 */
	private boolean replaces = false;

	/**
	 * the justification replaced
	 */
	private Integer replacedJustify;

	/**
	 * The period
	 */
	private String period;

	/**
	 * Operators list
	 */
	private ArrayList<Operator> operators = new ArrayList<Operator>();

	/**
	 * Corrections list 
	 */
	private ArrayList<Correction> corrections = new ArrayList<Correction>();

	/**
	 * Adds operator
	 * 
	 * @param operator 
	 */
	public void addOperator(Operator operator) {
		this.operators.add(operator);
	}

	/**
	 * @return an iterator for operators list
	 */
	public Iterator<Operator> getOperatorsIterator() {
		return this.operators.iterator();
	}

	/**
	 * adds a correction
	 * 
	 * @param correction
	 */
	public void addCorrection(Correction correction) {
		this.corrections.add(correction);
	}

	/**
	 * @return a corrections iterator
	 */
	public Iterator<Correction> getCorrectionsIterator() {
		return this.corrections.iterator();
	}

	/**
	 * @return the c001
	 */
	public int getC001() {
		return operators.size();
	}

	/**
	 * @return the c002
	 */
	public double getC002() {
		double c002 = 0;
		Iterator iter = getOperatorsIterator();
		while (iter.hasNext()) {
			Operator r = (Operator) iter.next();
			c002 += r.getBase();
		}
		return c002;
	}

	/**
	 * @return the c003
	 */
	public double getC003() {
		return corrections.size();
	}

	/**
	 * @return the c004
	 */
	public double getC004() {
		double c004 = 0;
		Iterator iter = getCorrectionsIterator();
		while (iter.hasNext()) {
			Correction r = (Correction) iter.next();
			c004 += r.getBase();
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

	/**
	 * @return the period
	 */
	public String getPeriod() {
		return period;
	}

	/**
	 * @param period
	 *            the period to set
	 */
	public void setPeriod(String period) {
		this.period = period;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String description = "DEPONENT ";
		description += "YEAR ";
		description += year == null ? "NULL " : "'" + year.toString() + "'";
		description += "PERIOD ";
		description += period == null ? "NULL " : "'" + period + "'";
		description += "CODE ";
		description += code == null ? "NULL " : "'" + code + "''";
		description += "NAME ";
		description += name == null ? "NULL " : "'" + name + "'; ";
		return description;
	}

}
