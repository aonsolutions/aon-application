package com.code.aon.ui.warehouse.event;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.warehouse.controller.IWarehouseConstants;
import com.code.aon.ui.warehouse.controller.IncomeController;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.enumeration.IncomeStatus;

public class IncomeControllerListener extends ControllerAdapter implements IWarehouseConstants {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(COMPANY_COLLECTIONS_CONTROLLER_NAME);
		IncomeController controller = (IncomeController)event.getController();
		try {
			((Income)controller.getTo()).setSecurityLevel(SecurityLevel.OFFICIAL);
			((Income)controller.getTo()).setStatus(IncomeStatus.PENDING);
			((Income)controller.getTo()).setWorkPlace((WorkPlace)((SelectItem)companyColls.getWorkPlaces().get(0)).getValue());
			controller.setAddresses(null);
			controller.setProjects(null);
	        controller.setWarehouse(controller.obtainWarehouse((Income)controller.getTo()));
			controller.setDefaultPayMethod(null);
			controller.resetIncomePayMethod();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		IncomeController controller = (IncomeController)event.getController();
		try {
			controller.loadAddresses(((Income)controller.getTo()).getSupplier().getRegistry().getId());
			controller.loadProjects(((Income)controller.getTo()).getSupplier().getRegistry().getId());
	        controller.setWarehouse(controller.obtainWarehouse((Income)controller.getTo()));
			controller.loadDefaultPayMethod(((Income)controller.getTo()).getSupplier().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
		incomeDetailController.onReset(null);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IncomeController incomeController = (IncomeController)this.getController();
		Income income = (Income)incomeController.getTo();
		if (income.getProject() != null && income.getProject().getId() != null) {
			IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
			incomeDetailController.onSearch(null);
		}
	}

}