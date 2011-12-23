package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.FanBatchDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class FanListController extends BasicController {

	private final static Logger LOGGER = LoggerFactory.getLogger(FanListController.class);

	private Enterprise enterprise;
	private GeoZone geozone;
	private BatchListCheckHandler checkHandler;
	
	public BatchListCheckHandler getCheckHandler() {
		if(checkHandler == null){
			checkHandler = new BatchListCheckHandler(this);
		}
		return checkHandler;
	}

	public void setCheckHandler(BatchListCheckHandler checkHandler) {
		this.checkHandler = checkHandler;
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

	public GeoZone getGeozone() {
		return geozone;
	}

	public void setGeozone(GeoZone geozone) {
		this.geozone = geozone;
	}

	
	@Override
	public void clearCriteria() throws ManagerBeanException {
		super.clearCriteria();
		setGeozone(null);
	}
	
	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		BatchDetailController controller = (BatchDetailController) FormUtil.getController(IPayrollConstants.FAN_BATCH_DETAIL_CONTROLLER_NAME);
		List<ITransferObject> list = controller.getWrappedList();
		try {
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
			if(list!=null && !list.isEmpty()){
				for(ITransferObject to: list){
					FanBatchDetail d = (FanBatchDetail) to;
					getCriteria().addNotEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ID), d.getCcc().getId());
				}
			}
			if(getGeozone()!=null){
				getCriteria().addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), getGeozone().getId());
			}
			if ((getEnterprise() != null) && (getEnterprise().getId() != null)) {
				getCriteria().addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_ACTIVITY_ENTERPRISE_ID), getEnterprise().getId());			
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}

}
