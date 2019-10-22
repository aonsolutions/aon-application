package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.jooq.tables.AppParam.APP_PARAM;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.DomainApplicationModule.DOMAIN_APPLICATION_MODULE;
import static com.esferalia.aon.jooq.tables.User.USER;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jooq.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.Domain;
import com.code.aon.ui.admin.BookingInfo;
import com.code.aon.ui.admin.DomainModuleInfo;
import com.code.aon.ui.admin.PortalInfo;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.DomainBookingData;
import com.code.aon.ui.config.DomainData;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AONContext;

public class DomainBookingController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(DomainBookingController.class);
	
	private int totalOneUsers;
	private int totalAiOUsers;
	private int totalPortals;
	private int totalTedis;
	
	private DataScrollerState parentState;
	
	private Domain domain;
	
	private BookingInfo bookingInfo;
	
	private boolean showInactive;
	
	private boolean showExpired;
	
	private String backAction;
	
	public void onInit( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain  = (Domain) bean.get(DomainManager.getCurrentDomain());
			init( domain );
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}			
	}

	public void init( Domain domain ) {
		this.domain = domain;
		initializeModel();
		initBookingInfo();
	}
	
	public boolean isShowInactive() {
		return showInactive;
	}

	public void setShowInactive(boolean showInactive) {
		this.showInactive = showInactive;
	}
	
	public boolean isShowExpired() {
		return showExpired;
	}

	public void setShowExpired(boolean showExpired) {
		this.showExpired = showExpired;
	}

	public void onChangeShowInactive(ActionEvent event) {
		initializeModel();
	}	

	public void onChangeShowExpired(ActionEvent event) {
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
		String payerDomainId = getAppParamenter(ctx, data, AppParam.AON_DOMAIN_PAYER);
		if (!StringUtils.isEmpty(payerDomainId)) {
			int domainId = NumberUtils.toInt(payerDomainId);
			String description = ctx.getDslContext()
					.select(DOMAIN.DESCRIPTION)
					.from(DOMAIN).where(DOMAIN.ID.eq(domainId))
					.fetchOne(0, String.class);
			data.setPayerDomain(description);
		}
		String portalValue = getAppParamenter(ctx, data, AppParam.AON_PORTAL);
		if (!StringUtils.isEmpty(portalValue)) {
			int value = NumberUtils.toInt(portalValue);
			data.setPortal(PortalInfo.isPortalActive(value));
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
	
	private List<DomainBookingData> getDomainBookingDatas( AONContext ctx, Condition condition ) {
		return ctx.getDslContext()
				.select(DOMAIN.ID, DOMAIN.NAME, DOMAIN.DESCRIPTION,
						DOMAIN.EXPIRATIONDATE,  DOMAIN.ACTIVE, DOMAIN.ENABLEHEREDITY,
						DOMAIN.MAXDEFINEDUSERS, DOMAIN.TYPE, DOMAIN.MAXTOTALDOCUMENTSIZE)
				.from(DOMAIN).where(condition)
				.orderBy(DOMAIN.DESCRIPTION).fetch().into(DomainBookingData.class);		
	}
	
	private void initBookingInfo() {
		this.bookingInfo = DomainController.getBookingInfo(this.domain);
		List<Domain> list = new LinkedList<Domain>();
		list.add(domain);
		this.parentState = new DataScrollerState(new SerializableListDataModel(list), "domainParent");			
	}
	
	private void initializeModel() {
		List<DomainBookingData> domains = Collections.emptyList();
		this.totalAiOUsers = 0;
		this.totalOneUsers = 0;
		this.totalPortals = 0;
		this.setTotalTedis(0);
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
		AONContext ctx = AONContext.getAONContext(domain.getName(), domain.getId(), "");
		Condition condition = ds.getDomainCondition(domain.getId(), isShowInactive(), isShowExpired());
		domains = getDomainBookingDatas(ctx, condition);
		for (DomainBookingData data : domains) {
			fillDomainData(ctx, data);
			if ( data.getPayerDomain() == null ) {
				if ( data.isAonOne() ) {
					this.totalOneUsers += data.getMaxDefinedUsers();
				} else {
					this.totalAiOUsers += data.getMaxDefinedUsers();
				}				
			}
			if ( data.isPortal() ) {
				this.totalPortals++;
			}
			if(data.isTediCenter()) {
				this.totalTedis++;
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
	
	public Domain getDomain() {
		return domain;
	}
	
	public DataScrollerState getParentState() {
		return this.parentState;
	}

	public BookingInfo getBookingInfo() {
		return bookingInfo;
	}

	public String getMaxTotalDocumentSize() {
		long value = (getDomain().getMaxTotalDocumentSize() > 0) ? getDomain().getMaxTotalDocumentSize() : 100;
		return FileUtils.byteCountToDisplaySize(value * FileUtils.ONE_MB); 
	}
	
	public int getNumberOfActiveUsers() {
		return DomainUserController.getNumberOfActiveUsers(domain.getId());
	}

	public String getBookingModuleList() throws ManagerBeanException {
		Set<String> modules = new TreeSet<String>();
		for( DomainModuleInfo dim : bookingInfo.getBookingModules() ) {
			if ( dim.isChecked() ) {
				modules.add( dim.getDescription() );
			}
		}
		return StringUtils.join(modules, ", ");					
	}

	public String backAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}	
	
	public boolean isShowBackButton() {
		return ! StringUtils.isEmpty(backAction);
	}

	public int getTotalTedis() {
		return totalTedis;
	}

	public void setTotalTedis(int totalTedis) {
		this.totalTedis = totalTedis;
	}	

}