package com.code.aon.ui.purchase.event;

import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.WorkPlace;
import com.code.aon.purchase.enumeration.ProposalStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.WarehouseCollectionsController;
import com.code.aon.warehouse.Warehouse;
import com.esferalia.aon.entity.IEntityAlias;

public class ProposalSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private WorkPlace workPlace;
	private Warehouse warehouse;
	private boolean itemReturn;
	private ProposalStatus[] proposalStatuses;

	public boolean isItemReturn() {
		return itemReturn;
	}

	public void setItemReturn(boolean itemReturn) {
		this.itemReturn = itemReturn;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}
	
	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public ProposalStatus[] getProposalStatuses() {
		return proposalStatuses;
	}

	public void setProposalStatuses(ProposalStatus[] proposalStatuses) {
		this.proposalStatuses = proposalStatuses;
	}

	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setWorkPlace((WorkPlace)BeanManager.getManagerBean(WorkPlace.class).createNewTo());
		setWarehouse((Warehouse)BeanManager.getManagerBean(Warehouse.class).createNewTo());
		ProposalStatus[] defaultProposalStatus = {ProposalStatus.PENDING, ProposalStatus.PARTIAL_PROCESSED};
		setProposalStatuses(defaultProposalStatus);
		setItemReturn(false);
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		CompanyCollectionsController controller = (CompanyCollectionsController) AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		super.completeCriteria( criteria );		
		if (getWorkPlace() != null && getWorkPlace().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_WORK_PLACE_ID), getWorkPlace().getId());			
		} else {
			criteria.addInExpression(getFieldName(IEntityAlias.PROPOSAL_WORK_PLACE_ID), controller.getCurrentUserWorkPlacesIds());
		}
		if (getWarehouse() != null && getWarehouse().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_WAREHOUSE_ID), getWarehouse().getId());			
		}
		if (!ArrayUtils.isEmpty(getProposalStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.PROPOSAL_STATUS);
			addEnumToCriteria(criteria, status, getProposalStatuses());
		}
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROPOSAL_ITEM_RETURN), isItemReturn());			
	}	

	public List<SelectItem> getWarehouses() throws ManagerBeanException {
		return WarehouseCollectionsController.getWarehouses(getWorkPlace());
	}
	
}