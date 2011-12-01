package com.code.aon.asset;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.ITransferObject;

/**
 * Entity class for representing an asset activity.
 * 
 * @author Consulting & Development. eagirrezabal - 04/02/2009
 * 
 */
@Entity
@Table(name = "asset_activity")
public class AssetActivity implements ITransferObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -767738401305249724L;
	
	private Integer id;
	private Date date;
	private Date fromTime;
	private Date toTime;
	private String who;
	private String why;
	private Asset asset;
	private ActivityStatus status;
	private boolean check;

	/**
	 * 
	 * @return
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	@Temporal(TemporalType.DATE)
	@Column(name="date", nullable=false, length=10)
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 
	 * @return
	 */
	//@Temporal(TemporalType.TIME)
	@Column(name="from_time", nullable=false)
	public Date getFromTime() {
		return fromTime;
	}

	public void setFromTime(Date fromTime) {
		this.fromTime = fromTime;
	}

	/**
	 * 
	 * @return
	 */
	//@Temporal(TemporalType.TIME)
	@Column(name="to_time", nullable=false)
	public Date getToTime() {
		return toTime;
	}

	public void setToTime(Date toTime) {
		this.toTime = toTime;
	}

	/**
	 * 
	 * @return
	 */
	@Column(name="who", length=20)
	public String getWho() {
		return who;
	}

	public void setWho(String who) {
		this.who = who;
	}

	/**
	 * 
	 * @return
	 */
	@Column(name="why", length=128)
	public String getWhy() {
		return why;
	}

	public void setWhy(String why) {
		this.why = why;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="asset", nullable=false )
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
		if (obj == null) {
			return super.equals(obj);
		}
		if (obj instanceof AssetActivity) {
			AssetActivity o = (AssetActivity) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id != null ? this.getClass().hashCode() + id.hashCode() : super
				.hashCode();
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