package com.code.aon.file.tax.model.MOD190.data;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * The withholder
 * 
 * @author Consulting & Development. Iñigo GAyarre - 08/02/2007
 * @since 1.0
 * 
 */
public class WithHolder {

	/**
	 * The year
	 */
	private Integer year;

	/**
	 * The code
	 */
	private String code;

	/**
	 * The anme
	 */
	private String name;

	/**
	 * The raltionship phone
	 */
	private Integer relPhone;

	/**
	 * The relationship persons name
	 */
	private String relName;

	/**
	 * The justification number
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
	 * replacemd justification
	 */
	private Integer replacedJustify;

	/**
	 * Receivers list
	 */
	private ArrayList<Receiver> receivers = new ArrayList<Receiver>();

	/**
	 * Adds a receiver
	 * 
	 * @param receiver
	 */
	public void addReceiver(Receiver receiver) {
		this.receivers.add(receiver);
	}

	/**
	 * @return receivers iterator
	 */
	public Iterator<Receiver> getReceiversIterator() {
		return this.receivers.iterator();
	}

	/**
	 * @return the c001
	 */
	public int getC001() {
		return receivers.size();
	}

	/**
	 * @return the c002
	 */
	public double getC002() {
		double c002 = 0;
		Iterator iter = getReceiversIterator();
		while (iter.hasNext()) {
			Receiver r = (Receiver) iter.next();
			c002 += r.getReceibedMoney();
			c002 += r.getReceibedSpice();
		}
		return c002;
	}

	/**
	 * @return the c003
	 */
	public double getC003() {
		double c003 = 0;
		Iterator iter = getReceiversIterator();
		while (iter.hasNext()) {
			Receiver r = (Receiver) iter.next();
			c003 += r.getWithholdedMoney();
			c003 += r.getPayEfect();
		}
		return c003;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String description = "WITHHOLDER ";
		description += "YEAR ";
		description += year == null ? "NULL " : "'" + year.toString() + "'";
		description += "CODE ";
		description += code == null ? "NULL " : "'" + code + "''";
		description += "NAME ";
		description += name == null ? "NULL " : "'" + name + "'; ";
		return description;
	}

}
