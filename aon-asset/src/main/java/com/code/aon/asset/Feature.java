package com.code.aon.asset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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

/**
 * Entity class for representing a feature.
 * 
 * @author Esferalia Networks. eagirrezabal - 26/12/2011
 * 
 */
@Entity
@Table(name = "feature")
public class Feature implements ITransferObject {

	private static final long serialVersionUID = 512904295414043135L;

	private Integer id;

	private String name;
	
//	private String description;
	
//	private AssetType type;
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="name", nullable = false, length = 10)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
//	@Column(name="description", nullable = false, length = 64)
//	public String getDescription() {
//		return description;
//	}
//
//	public void setDescription(String description) {
//		this.description = description;
//	}
	
//	@ManyToOne
//	@JoinColumn(name="asset_type")
//	@ForeignKey(name = "FK_FEATURE_ASSET_TYPE")
//	@Index(name = "IDX_FK_FEATURE_ASSET_TYPE")
//	public AssetType getType() {
//		return type;
//	}
//	
//	public void setType(AssetType type) {
//		this.type = type;
//	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Feature o = (Feature) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name,o.name)
//				.append(this.description,o.description)
//				.append(this.type,o.type)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(name)
//			.append(description)
//			.append(type)
			.toHashCode();
		
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}