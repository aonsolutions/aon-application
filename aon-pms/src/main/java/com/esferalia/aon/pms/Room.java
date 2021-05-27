package com.esferalia.aon.pms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.AssetFeature;
import com.code.aon.asset.IAsset;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.RoomDB;
import com.esferalia.aon.pms.enumeration.RoomStatus;
import com.esferalia.aon.watson.server.AonDateUtils;

@Entity
@Table(name="room")
public class Room extends RoomDB implements IAsset{

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<AssetFeature> features = new HashSet<AssetFeature>();

	public Room() {
		setStatus(RoomStatus.DIRTY);
		setActive(true);
	}

	@OneToMany(mappedBy = "asset", cascade={CascadeType.REMOVE})
	@OrderBy()
	public Set<AssetFeature> getFeatures() {
		return this.features;
	}
	public void setFeatures(Set<AssetFeature> features) {
		this.features = features;
	}

	@Transient
	public boolean isCleanRoom() throws ManagerBeanException {
		return AonDateUtils.isSameDay(getLastCleaningDate(), new Date()) && getStatus() == RoomStatus.CLEAN;
	}
	
	@Transient
	public boolean isTodayLastCleaningDate() throws ManagerBeanException {
		return AonDateUtils.isSameDay(getLastCleaningDate(), new Date());
	}
	
	@Transient
	public boolean isCleaningDateExpired() throws ManagerBeanException {
		String value = AppParamUtil.getParameter(AppParam.PMS_ROOM_CLEAN_MAX_DAYS).getValue();
		Date expirationDate = AonDateUtils.addDays(getLastCleaningDate(), NumberUtils.isDigits(value)?Integer.valueOf(value):0);
		return expirationDate.before(new Date());
	}
	
	@Transient
	public boolean isBusyRoom() throws ManagerBeanException {
		return getStatus() == RoomStatus.DO_NOT_DISTURB;
	}

	@Transient
	public boolean isUsedRoom() throws ManagerBeanException {
		if (getId() != null) {
			IManagerBean assetActivityBean = BeanManager.getManagerBean(AssetActivity.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(assetActivityBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), getId());
			return assetActivityBean.getCount(criteria) > 0;
		}
		return false;
	}

}
