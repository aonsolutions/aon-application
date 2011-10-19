package com.code.aon.warehouse;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.WorkPlace;

@Entity
@Table(name="warehouse")
public class Warehouse implements ITransferObject{
	
	private static final long serialVersionUID = 6558594692980896245L;

	private Integer id;
	private String name;
	private WorkPlace workPlace;

    @Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
        return id;
    }
    public void setId(Integer primaryKey) {
        this.id = primaryKey;
    }

    @Column(length=32, nullable = false)
	public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name="workplace")
    @ForeignKey(name="FK_WAREHOUSE_WORKPLACE")
    @Index(name="IDX_WAREHOUSE_WORKPLACE")                                            
	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Warehouse o = (Warehouse) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name, o.name)
				.append(this.workPlace, o.workPlace)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(name)			
			.append(workPlace)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}