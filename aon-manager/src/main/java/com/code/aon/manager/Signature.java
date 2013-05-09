package com.code.aon.manager;

import static com.code.aon.ldap.IAonObjectClasses.SIGNATURE;
import static com.code.aon.ldap.IAonObjectClasses.TOP;

import javax.naming.Name;
import javax.persistence.Id;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.code.aon.dao.ldap.ILdapTransferObject;
import com.code.aon.dao.ldap.annotations.Attribute;
import com.code.aon.dao.ldap.annotations.EntryObject;
import com.code.aon.dao.ldap.annotations.RDN;
import com.code.aon.webmail.ISignature;

@EntryObject(mainObjectClass=SIGNATURE, objectClasses={TOP})
public class Signature implements ILdapTransferObject, ISignature {

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
	@Attribute(name=COMMON_NAME_ATTRIBUTE, length=32768)    	
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Signature o = (Signature) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)
				.append(this.signature, o.signature)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(name)			
			.append(signature)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
    
}
