package com.code.aon.csb.fd0.model.CSB19.data;

import java.util.Date;

/**
 * The presenter
 * 
 * @author Consulting & Development. Iñigo GAyarre - 06/02/2007
 * @since 1.0
 *
 */
public class Presenter {

	/**
	 * Identifies the presenter
	 */
	private String code;
	
	/**
	 * The sufix
	 */
	private String sufix;

	/**
	 * The file make date
	 */
	private Date makeDate;
	
	/**
	 * Presenter name
	 */
	private String name;
	
	/**
	 * The entity that receives the file
	 */
	private String entity;
	
	/**
	 * The office that receives the file
	 */
	private String office;


	/**
	 * Returns the code
	 * 
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Assigns the code
	 * 
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

	/**
	 * Returns the entity
	 * 
	 * @return the entity
	 */
	public String getEntity() {
		return entity;
	}

	/**
	 * Assigns the entity
	 * 
	 * @param entity the entity to set
	 */
	public void setEntity(String entity) {
		this.entity = entity;
	}

	/**
	 * Returns the making date
	 * 
	 * @return the makeDate
	 */
	public Date getMakeDate() {
		return makeDate;
	}

	/**
	 * Assigns making date
	 * 
	 * @param makeDate the makeDate to set
	 */
	public void setMakeDate(Date makeDate) {
		this.makeDate = makeDate;
	}

	/**
	 * Returns the name
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Assigns the name
	 * 
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the office
	 * 
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}

	/**
	 * Assigns the office
	 * 
	 * @param office the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
	}

	/**
	 * Returns the office
	 * 
	 * @return the sufix
	 */
	public String getSufix() {
		return sufix;
	}

	/**
	 * Assigns the sufix
	 * 
	 * @param sufix the sufix to set
	 */
	public void setSufix(String sufix) {
		this.sufix = sufix;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		String description = "PRESENTER ";
		description += "CODE "+code+"; ";
		description += "SUFIX "+sufix+"; ";
		description += "MAKE_DATE "+makeDate+"; ";
		description += "NAME "+name+"; ";
		description += "ENTITY "+entity+"; ";
		description += "OFFICE "+office+"; ";
		return description;
	}

	
}
