package com.code.aon.groupware;

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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;

@Entity
@Table(name="campaign_type")
public class CampaignType implements ITransferObject, IEnterprise {
	
	private static final long serialVersionUID = 3901317302374722766L;
	
	private Integer id;
	private Enterprise enterprise;
	private String description;
    private boolean active;
    
    public CampaignType() {
		this.active = true;
	}

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_PROCESS_ENTERPRISE")
    @Index(name = "IDX_PROCESS_ENTERPRISE")
    @Override
	public Enterprise getEnterprise() {
		return enterprise;
	}
    @Override
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

    @Column(length=30, nullable=false)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Column(nullable=false)
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

	@Override	
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final CampaignType o = (CampaignType) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
			.append(this.id, o.id)
				.append(this.enterprise, o.enterprise)
				.append(this.description, o.description)
				.append(this.active, o.active)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.id)
			.append(this.enterprise)
			.append(this.description)
			.append(this.active)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}