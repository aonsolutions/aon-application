package com.esferalia.aon.ui.payroll.controller.batch;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.geozone.GeoZone;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.CraBatch;
import com.esferalia.aon.payroll.CraBatchDetail;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class CraListController extends BasicController implements BatchListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(CraListController.class);
	
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
	
	public String getFullQuoteRegime() throws ManagerBeanException {
		if (this.getModel().isRowAvailable()) {
			return PayrollUtils.getInstance().getRegimeCode(
					(EnterpriseCCC) this.getModel().getRowData())
					+ ((EnterpriseCCC) this.getModel().getRowData()).getCcc();
		}
		return null;
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
			
			
			CraBatchController batchController = (CraBatchController) AonUtil.getRegisteredBean(IPayrollConstants.CRA_BATCH_CONTROLLER_NAME);
			if(batchController.isNevv()){
				List<ITransferObject> list = batchController.getNewBatchWizard().getSelectedList();
				if(list!=null){
					for(ITransferObject to: list){
						CraBatchDetail d = (CraBatchDetail) to;
						getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.ENTERPRISE_CCC_ID), d.getCcc().getId());
					}
					getCriteria().addOrder("EnterpriseCCC.activity.enterprise.registry.name");
				}
			} else {
				List<Integer> list = getDetailCCCIds((CraBatch)batchController.getTo());
				if(list!=null){
					for(Integer id: list){
						getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.ENTERPRISE_CCC_ID), id);
					}
					getCriteria().addOrder("EnterpriseCCC.activity.enterprise.registry.name");
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}
	
	private List<Integer> getDetailCCCIds(CraBatch craBatch){
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT enterprise_ccc FROM cra_batch_detail WHERE cra_batch = " + craBatch.getId() + ";";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			List<Integer> list = new LinkedList<Integer>();
			while(rs.next()){
				list.add(rs.getInt(1));
			}
			return list;
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener el dato requerido. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}
	
	@Override
	public List<ITransferObject> getAllList() throws ManagerBeanException {
		return this.getManagerBean().getList(this.getCriteria());
	}

}
