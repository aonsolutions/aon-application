package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.Country;
import com.code.aon.fiscal.enumeration.Mod347Type;

@Entity
@Table(name = "fs_mod347_detail")
public class Mod347Detail implements ITransferObject {
	
	private static final long serialVersionUID = -7918348181910780031L;
	
	private Integer id;
	private Mod347 mod347;
	private Mod347Type type;
	private String document;
	private Integer registry;
	private String name;
	private Integer province;
	private Country country;
	private Double amount;

	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="fs_mod347", nullable = false)
    public Mod347 getMod347() {
        return mod347;
    }
    public void setMod347(Mod347 mod347) {
        this.mod347 = mod347;
    }
    
    @Column(name="type")
   	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.fiscal.enumeration.Mod347Type") })
	public Mod347Type getType() {
		return type;
	}
	public void setType(Mod347Type type) {
		this.type = type;
	}
	
	@Column(name="document")	
	public String getDocument() {
		return document;
	}
	public void setDocument(String document) {
		this.document = document;
	}

	@Column(name="registry")	
	public Integer getRegistry() {
		return registry;
	}
	public void setRegistry(Integer registry) {
		this.registry = registry;
	}

    @Column(name="name")	
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Column(name="province")	
	public Integer getProvince() {
		return province;
	}
	public void setProvince(Integer province) {
		this.province = province;
	}

	@Type(type = "stringEnum", parameters = { @Parameter(name = "enumClassname", value = "com.code.aon.common.enumeration.Country") })
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}

	@Column(name="amount")	
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Mod347Detail o = (Mod347Detail) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.mod347,o.mod347)
				.append(this.type,o.type)
				.append(this.document,o.document)
				.append(this.registry,o.registry)
				.append(this.name,o.name)
				.append(this.province,o.province)
				.append(this.country,o.country)
				.append(this.amount,o.amount)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.mod347)
			.append(this.type)
			.append(this.document)
			.append(this.registry)
			.append(this.name)
			.append(this.province)
			.append(this.country)
			.append(this.amount)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
