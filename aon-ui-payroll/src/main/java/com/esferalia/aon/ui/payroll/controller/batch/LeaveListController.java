package com.esferalia.aon.ui.payroll.controller.batch;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.enumeration.ContractLeaveStatus;
import com.esferalia.aon.payroll.enumeration.LeaveReportType;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.sepe.utils.SEPEUtils;

public class LeaveListController extends BasicController implements BatchListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(LeaveListController.class);
	
	private boolean searchPanelExpanded;

	private LeaveReportType[] reportTypes = { LeaveReportType.LEAVE,LeaveReportType.CONFIRM, LeaveReportType.DISCHARGE };
	private Person person;
	private Enterprise enterprise;
	private BatchListCheckHandler checkHandler;
	
	public boolean isSearchPanelExpanded() {
		return searchPanelExpanded;
	}

	public void setSearchPanelExpanded(boolean searchPanelExpanded) {
		this.searchPanelExpanded = searchPanelExpanded;
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
	
	
	public LeaveReportType[] getReportTypes() {
		return reportTypes;
	}
	
	public void setReportTypes(LeaveReportType[] reportTypes) {
		this.reportTypes = reportTypes;
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}
	
	public void init(){
		try {
			setEnterprise( (Enterprise) BeanManager.getManagerBean(Enterprise.class).createNewTo() );
			setPerson( (Person) BeanManager.getManagerBean(Person.class).createNewTo() );
			LeaveReportType[] defaultTypes = { LeaveReportType.LEAVE,LeaveReportType.CONFIRM, LeaveReportType.DISCHARGE };
			setReportTypes(defaultTypes);
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> error on init ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	@Override
	public void onSearch(ActionEvent event) {
		getCheckHandler().clearCheckedList();
		try {
			this.setCriteria( new Criteria() );
			SEPEUtils utils = SEPEUtils.getInstance();
			
			LeaveBatchController controller = (LeaveBatchController) FormUtil.getController(IPayrollConstants.LEAVE_BATCH_CONTROLLER_NAME);
			LeaveBatch batch = (LeaveBatch) controller.getTo();
			getCriteria().addLessThanOrEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DATE), batch.getDate());
			getCriteria().addNotEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_STATUS),ContractLeaveStatus.BATCHED);
			
			if ((getPerson() != null) && (getPerson().getId() != null)) {
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_CONTRACT_PERSON_ID), getPerson().getId());			
			}
			if(getEnterprise()!=null && getEnterprise().getId()!=null){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_CONTRACT_LEAVE_CONTRACT_WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());			
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DOMAIN), getEnterprise().getDomain());
			} else if(DomainManager.isDomainManagementAvailable()){
				getCriteria().setSkipDomainFilter( true );
				getCriteria().addInExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_DOMAIN), utils.getCurrentChildDomainIds());
			}
			
			Expression expr = null;
			for(LeaveReportType t: getReportTypes()){
				if(expr == null){
					expr = ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), t); 
				} else {
					expr = ExpressionUtilities.getOrExpression(expr, ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.CONTRACT_LEAVE_DETAIL_TYPE), t));
				}
			}
			if(expr!=null){
				getCriteria().addExpression(expr);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(">>>> onSearch exception: ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
		super.onSearch(event);
	}
	
	@Override
	public List<ITransferObject> getAllList() throws ManagerBeanException {
		return this.getManagerBean().getList(this.getCriteria());
	}

}
