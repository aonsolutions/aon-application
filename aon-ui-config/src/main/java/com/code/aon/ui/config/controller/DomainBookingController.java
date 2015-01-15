package com.code.aon.ui.config.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.DomainBookingData;
import com.code.aon.ui.config.DomainData;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;

public class DomainBookingController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private int totalOneUsers;
	private int totalAiOUsers;
	private int totalPortals;
	private int totalDEHOnlines;
	private int totalTirants;
	
	private boolean showInactive;
	
	public void onInit( ActionEvent event ) {
		initializeModel();
	}
	
	public boolean isShowInactive() {
		return showInactive;
	}

	public void setShowInactive(boolean showInactive) {
		this.showInactive = showInactive;
	}

	public void onChangeShowInactive(ActionEvent event) {
		initializeModel();
	}	
	
	private String getAppParamenter( AONContext ctx, DomainData data, AppParam param ) {
		return ctx.getDslContext()
				.select(APP_PARAM.VALUE)
				.from(APP_PARAM)
				.where(APP_PARAM.DOMAIN.eq(data.getId()).and(
						APP_PARAM.NAME.eq(param.getValue())))
				.fetchOne(0, String.class);		
	}
	
	private void fillDomainData(AONContext ctx, DomainBookingData data) {
		String portalValue = getAppParamenter(ctx, data, AppParam.AON_PORTAL);
		if (!StringUtils.isEmpty(portalValue)) {
			data.setPortal(NumberUtils.toInt(portalValue) > 0);
		}
		String externalApplicationsValue = getAppParamenter(ctx, data, AppParam.AON_EXTERNAL_APPLICATIONS);
		if (!StringUtils.isEmpty(externalApplicationsValue)) {
			int value = NumberUtils.toInt(externalApplicationsValue);
			data.setTirant( (value & ICommonConstants.TIRANT_EXTERNAL_APP) != 0 );
			data.setDehOnline( (value & ICommonConstants.DEH_ONLINE_EXTERNAL_APP) != 0 );
			String dehUser = getAppParamenter(ctx, data, AppParam.AON_DEH_ONLINE_USER);
			if (!StringUtils.isEmpty(dehUser)) {
				String dehPassword = getAppParamenter(ctx, data, AppParam.AON_DEH_ONLINE_PASSWORD);
				data.setDehOnlineConfigured(!StringUtils.isEmpty(dehPassword));
			}
		}
		int activeUsers = ctx
				.getDslContext()
				.selectCount()
				.from(USER)
				.where(USER.DOMAIN.eq(data.getId())
						.and(USER.ACTIVE.eq((byte) 1))
						.and(USER.ENTERPRISE.isNull())).fetchOne(0, int.class);
		data.setActiveUsers(activeUsers);
		int aonOneModule = ctx
				.getDslContext()
				.selectCount()
				.from(DOMAIN_APPLICATION_MODULE)
				.where(DOMAIN_APPLICATION_MODULE.DOMAIN.eq(data.getId()).and(
						DOMAIN_APPLICATION_MODULE.MODULE
								.eq((byte) Module.AON_ONE.ordinal())))
				.fetchOne(0, int.class);
		data.setAonOne(aonOneModule > 0);
	}	
	
	private void initializeModel() {
		List<DomainBookingData> domains = Collections.emptyList();
		this.totalAiOUsers = 0;
		this.totalOneUsers = 0;
		this.totalPortals = 0;
		this.totalDEHOnlines = 0;
		this.totalTirants = 0;
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		AONContext ctx = AONContext.getAONContext(ds.getDomainNameURL(), ds.getDomainId());
		domains = ctx
				.getDslContext()
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION,
						DOMAIN.ACTIVE,  DOMAIN.ENABLEHEREDITY,
						DOMAIN.MAXDEFINEDUSERS, DOMAIN.TYPE, DOMAIN.MAXTOTALDOCUMENTSIZE)
				.from(DOMAIN).where(ds.getDomainCondition(isShowInactive()))
				.orderBy(DOMAIN.DESCRIPTION).fetch().into(DomainBookingData.class);

		for (DomainBookingData data : domains) {
			fillDomainData(ctx, data);
			if ( data.isAonOne() ) {
				this.totalOneUsers += data.getMaxDefinedUsers();
			} else {
				this.totalAiOUsers += data.getMaxDefinedUsers();
			}
			if ( data.isPortal() ) {
				this.totalPortals++;
			}
			if ( data.isTirant() ) {
				this.totalTirants++;
			}
			if ( data.isDehOnline() ) {
				this.totalDEHOnlines++;
			}
		}
		ctx.finalize();
		setModel(new SerializableListDataModel(domains));
	}

	public int getTotalOneUsers() {
		return totalOneUsers;
	}

	public int getTotalAiOUsers() {
		return totalAiOUsers;
	}

	public int getTotalPortals() {
		return totalPortals;
	}

	public int getTotalDEHOnlines() {
		return totalDEHOnlines;
	}

	public int getTotalTirants() {
		return totalTirants;
	}

	
}
