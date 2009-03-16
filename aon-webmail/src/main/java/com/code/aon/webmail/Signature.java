package com.code.aon.webmail;

import javax.naming.Name;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.code.aon.common.ITransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;

@Entity
@Table(name="signature")
@EntryObject(mainObjectClass="aonSignature", objectClasses={"top"})
public class Signature implements ITransferObject{

	private static final long serialVersionUID = 714322089783136934L;

	// ident
	private Name id;

	// signature
    private String signature;

	// name
    private String name;

	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Name getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Name id) {
		this.id = id;
	}

	/**
	 * @return the signature
	 */
	@Attribute(name="signature", length=32768)
	public String getSignature() {
		return signature;
	}

	/**
	 * @param signature the signature to set
	 */
	public void setSignature(String signature) {
		this.signature = signature;
	}

	/**
	 * @return the name
	 */
	@RDN
	@Attribute(name="cn", length=32768)    	
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

    
}
