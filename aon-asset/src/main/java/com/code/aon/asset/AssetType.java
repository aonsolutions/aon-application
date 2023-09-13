package com.code.aon.asset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Entity class for representing an asset type.
 * 
 * @author Esferalia Networks. eagirrezabal - 26/12/2011
 * 
 */
@Entity
@Table(name = "asset_type")
public class AssetType implements ITransferObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;

	private String name;
	
	private AssetInterval assetInterval;
	
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
	
	@ManyToOne
	@JoinColumn(name="asset_interval")
	@ForeignKey(name = "FK_ASSET_TYPE_ASSET_INTERVAL")
	@Index(name = "IDX_ASSET_TYPE_ASSET_INTERVAL")
	public AssetInterval getAssetInterval() {
		return assetInterval;
	}

	public void setAssetInterval(AssetInterval assetInterval) {
		this.assetInterval = assetInterval;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AssetType o = (AssetType) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name,o.name)
				.append(this.assetInterval,o.assetInterval)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(name)
			.append(assetInterval)
			.toHashCode();
		
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}