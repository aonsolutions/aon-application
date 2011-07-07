package com.code.aon.tas;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="tas_item")
public class TasItem implements ITransferObject {

	private static final long serialVersionUID = -2161261906986667487L;
	
	private Integer id;
	private Model model;
	private String publicCode;
	private String privateCode;
	private String description;
	private String addInfo;

	@Id
	@GeneratedValue
	public Integer getId() {
		return this.id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="model", nullable=false)
	public Model getModel() {
		return this.model;
	}
	public void setModel(Model model) {
		this.model = model;
	}

	@Column(length=25, nullable=false)
	public String getPublicCode() {
		return this.publicCode;
	}
	public void setPublicCode(String publicCode) {
		this.publicCode = publicCode;
	}

	@Column(length=25)
	public String getPrivateCode() {
		return this.privateCode;
	}
	public void setPrivateCode(String privateCode) {
		this.privateCode = privateCode;
	}

	@Lob
	public String getDescription() {
		return this.description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "add_info")
	@Lob
	public String getAddInfo() {
		return addInfo;
	}
	public void setAddInfo(String addInfo) {
		this.addInfo = addInfo;
	}

	@Transient
	public String getFullName() {
		return ((publicCode != null) ? publicCode + " " : "") + ((model != null && model.getId() != null) ? "(" + model.getFullName() + ")" : "");
	}
	public void setFullName(String value) {
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TasItem o = (TasItem) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.model, o.model)								
				.append(this.publicCode, o.publicCode)								
				.append(this.privateCode, o.privateCode)								
				.append(this.description, o.description)								
				.append(this.addInfo, o.addInfo)								
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()		
			.append(id)								
			.append(this.model)								
			.append(this.publicCode)								
			.append(this.privateCode)								
			.append(this.description)								
			.append(this.addInfo)								
			.toHashCode();
	}

	@Override
	public String toString() {
		return PojoToStringBuilder.reflectionToString(this);
	}
}
