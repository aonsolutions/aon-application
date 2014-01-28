package com.esferalia.aon.ui.payroll.controller.batch;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class FanListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(FanListController.class);
	
	private boolean searchPanelExpanded;

	private Enterprise enterprise;
	private GeoZone geozone;
	private BatchListCheckHandler checkHandler;
	
	public boolean isSearchPanelExpanded() {
		return searchPanelExpanded;
	}

	public void setSearchPanelExpanded(boolean searchPanelExpanded) {
		this.searchPanelExpanded = searchPanelExpanded;
	}
	
	public Enterprise getEnterprise() {
		try {
			if(enterprise == null){
				IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
				enterprise = (Enterprise) bean.createNewTo();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on getEnterprise: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public BatchListCheckHandler getCheckHandler() {
		if(checkHandler == null){
			checkHandler = new BatchListCheckHandler(this);
		}
		return checkHandler;
	}

	public void setCheckHandler(BatchListCheckHandler checkHandler) {
		this.checkHandler = checkHandler;
	}

	public GeoZone getGeozone() {
		return geozone;
	}

	public void setGeozone(GeoZone geozone) {
		this.geozone = geozone;
	}

	public void init(){
		try {
			setEnterprise( (Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo() );
			setGeozone(null);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on init ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		try {
			this.setCriteria( new Criteria() );
			PayrollUtils utils = PayrollUtils.getInstance();
			
			if(getGeozone()!=null){
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), getGeozone().getId());
			}

			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());			
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.ENTERPRISE_CCC_DOMAIN), getEnterprise().getDomain());
			} else if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.ENTERPRISE_CCC_DOMAIN), utils.getCurrentChildDomainIds());
			}
			
//			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
//			if(list!=null && !list.isEmpty()){
//				for(ITransferObject to: list){
//					FanBatchDetail d = (FanBatchDetail) to;
//					getCriteria().addNotEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ID), d.getCcc().getId());
//				}
//			}
			
//			getCriteria().addOrder(getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID));
			getCriteria().addOrder("EnterpriseCCC.activity.enterprise.registry.name");
			
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}

}
