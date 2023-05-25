package com.code.aon.asset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.AonVersion;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Entity class for representing an asset interval.
 * 
 * @author Esferalia Networks. eagirrezabal - 26/12/2011
 * 
 */
@Entity
@Table(name = "asset_interval")
public class AssetInterval implements ITransferObject {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer id;

	private String description;
	
	private Double interval;
	
	private String startTime;
	
	private String endTime;
	
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="description", nullable = false, length = 64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(name = "interval", precision = 15, scale = 3, nullable = false)
	public Double getInterval() {
		return interval;
	}
	
	public void setInterval(Double interval) {
		this.interval = interval;
	}
	
//	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "start_time", nullable = false )
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

//	@Temporal(TemporalType.TIMESTAMP)
	@Column( name = "end_time", nullable = false )
	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AssetInterval o = (AssetInterval) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.description,o.description)
				.append(this.interval,o.interval)
				.append(this.startTime,o.startTime)
				.append(this.endTime,o.endTime)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(description)
			.append(interval)
			.append(startTime)
			.append(endTime)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}