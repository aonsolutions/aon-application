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
 * Entity class for representing an asset feature.
 * 
 * @author Esferalia Networks. eagirrezabal - 26/12/2011
 * 
 */
@Entity
@Table(name = "asset_feature")
public class AssetFeature implements ITransferObject {

	private static final long serialVersionUID = -6473829580874424891L;

	private Integer id;
	
	private Asset asset;
	
	private Feature feature;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne
	@JoinColumn(name="asset", nullable = false, updatable = false )
	@ForeignKey(name = "FK_ASSET_FEATURE_ASSET")
	@Index(name = "IDX_ASSET_FEATURE_ASSET")
	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	
	@ManyToOne
	@JoinColumn(name="feature", nullable = false, updatable = false )
	@ForeignKey(name = "FK_ASSET_FEATURE_FEATURE")
	@Index(name = "IDX_ASSET_FEATURE_FEATURE")
	public Feature getFeature() {
		return this.feature;
	}
	
	public void setFeature(Feature feature) {
		this.feature = feature;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AssetFeature o = (AssetFeature) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.asset,o.asset)
				.append(this.feature,o.feature)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(asset)
			.append(feature)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}



}