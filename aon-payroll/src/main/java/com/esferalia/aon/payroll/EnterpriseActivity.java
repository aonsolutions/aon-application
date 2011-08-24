package com.esferalia.aon.payroll;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;
import com.code.aon.config.CNAE2009;
import com.esferalia.aon.payroll.enumeration.EnterpriseActivityType;

@Entity
@Table(name="enterprise_activity")
public class EnterpriseActivity implements ITransferObject {
	
	private static final long serialVersionUID = 7441683008416320456L;

	private Integer id;
    private String description;
    private Enterprise enterprise; 
    private CNAE2009 cnae2009; 
	private EnterpriseActivityType type;

	private Set<EnterpriseCCC> cccs = new HashSet<EnterpriseCCC>();
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length = 32, nullable = false)
    public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_ENTERPRICE_ACTIVITY_ENTERPRISE")
    @Index(name = "IDX_ENTERPRICE_ACTIVITY_ENTERPRISE")    
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	@ManyToOne
    @JoinColumn(name="cnae2009" )
    @ForeignKey(name = "FK_ENTERPRICE_ACTIVITY_CNAE2009")
    @Index(name = "IDX_ENTERPRICE_ACTIVITY_CNAE2009")    
	public CNAE2009 getCnae2009() {
		return cnae2009;
	}
	public void setCnae2009(CNAE2009 cnae2009) {
		this.cnae2009 = cnae2009;
	}
	
	@Column( nullable = false )
	public EnterpriseActivityType getType() {
		return type;
	}
	public void setType(EnterpriseActivityType type) {
		this.type = type;
	}

	@OneToMany(mappedBy = "activity", cascade={CascadeType.REMOVE})
	public Set<EnterpriseCCC> getCccs() {
		return cccs;
	}
	public void setCccs(Set<EnterpriseCCC> cccs) {
		this.cccs = cccs;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseActivity o = (EnterpriseActivity) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.cnae2009, o.cnae2009)
				.append(this.description, o.description)
				.append(this.enterprise, o.enterprise)
				.append(this.type, o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(cnae2009)
			.append(description)
			.append(enterprise)
			.append(id)
			.append(type)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}
