/**
 * 
 */
package com.code.aon.company;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="enterprise_ccc")
public class EnterpriseCCC implements ITransferObject {
	
	private static final long serialVersionUID = -8232319254583226675L;

	/** CCC identifier */
	private Integer id;
	
	/** CCC value */
    private String ccc;

	/** Indicates the enterprise that this ccc belongs to */
    private Enterprise enterprise; 

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="ccc", length = 11)
	public String getCCC() {
		return ccc;
	}

	public void setCCC(String ccc) {
		this.ccc = ccc;
	}

	@OneToOne
    @JoinColumn(name="enterprise", updatable = false)
    @ForeignKey(name = "FK_ENTERPRICE_CCC_ENTERPRICE")
    @Index(name = "IDX_ENTERPRICE_CCC_ENTERPRICE")    
    public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseCCC o = (EnterpriseCCC) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.ccc, o.ccc)
				.append(this.enterprise, o.enterprise)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(ccc)
			.append(enterprise)
			.append(id)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
