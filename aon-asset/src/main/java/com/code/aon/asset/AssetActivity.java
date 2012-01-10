package com.code.aon.asset;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Entity class for representing an asset activity.
 * 
 * @author Consulting & Development. eagirrezabal - 04/02/2009
 * 
 */
@Entity
@Table(name = "asset_activity")
public class AssetActivity implements ITransferObject {

	private static final long serialVersionUID = -767738401305249724L;
	
	private Integer id;
	
	private Date date;
	
	private Date fromTime;
	
	private Date toTime;
	
	private String holder;
	
	private String comments;
	
	private Asset asset;
	
	private ActivityStatus status;
	
	private boolean check;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Temporal(TemporalType.DATE)
	@Column(name="date", nullable=false, length=10)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="from_time", nullable=false)
	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="to_time", nullable=false)
	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	@Column(name="who", length=32)
	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	@Column(name="why", length=128)
	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	@ManyToOne
	@JoinColumn(name="asset", nullable = false, updatable = false )
	@ForeignKey(name = "FK_ASSET_ACTIVITY_ASSET")
	@Index(name = "IDX_ASSET_ACTIVITY_ASSET")
	public Asset getAsset() {
		return this.asset;
	}

	public void setAsset(Asset asset) {
		this.asset = asset;
	}
	
	@Column(name = "status", length = 1)
	public ActivityStatus getStatus() {
		return status;
	}
	public void setStatus(ActivityStatus status) {
		this.status = status;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final AssetActivity o = (AssetActivity) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.date,o.date)
				.append(this.fromTime,o.fromTime)
				.append(this.toTime,o.toTime)
				.append(this.holder,o.holder)
				.append(this.comments,o.comments)
				.append(this.asset,o.asset)
				.append(this.status,o.status)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(date)
			.append(fromTime)
			.append(toTime)
			.append(holder)
			.append(comments)
			.append(asset)
			.append(status)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

	@Transient
	public boolean isCheck() {
		return check;
	}

	@Transient
	public void setCheck(boolean check) {
		this.check = check;
	}

}