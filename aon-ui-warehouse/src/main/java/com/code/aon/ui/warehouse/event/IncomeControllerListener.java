package com.code.aon.ui.warehouse.event;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
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
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterModelInitialized(ControllerEvent event) throws ControllerListenerException {
		IncomeController controller = (IncomeController)event.getController();
		controller.setListTotal(null);
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CompanyCollectionsController companyColls = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		IncomeController controller = (IncomeController)event.getController();
		Income income = (Income)controller.getTo(); 
		try {
			List<SelectItem> workPlaces = companyColls.getCurrentUserWorkPlaces();
			if (workPlaces.size() == 1 ) {
				income.setWorkPlace((WorkPlace)workPlaces.get(0).getValue());
			} else if (workPlaces.isEmpty()) {
				throw new ControllerListenerException("No hay un Centro de Trabajo definido.");
			}
			income.setSecurityLevel(SecurityLevel.OFFICIAL);
			income.setStatus(IncomeStatus.PENDING);
			income.setScope(income.getWorkPlace().getScope());
			controller.setAddresses(null);
			controller.updateWarehouse();
			controller.setDefaultPayMethod(null);
			controller.resetIncomePayMethod();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		IncomeController controller = (IncomeController)event.getController();
		Income income = (Income)controller.getTo();
		try {
			controller.loadAddresses(income.getSupplier().getRegistry().getId());
			controller.updateWarehouse();
			controller.loadDefaultPayMethod(income.getSupplier().getRegistry().getId(), true);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		if(!(AonUtil.getRoleManager().isWarehouseOperator() || !AonUtil.getRoleManager().isPurchaseOperator())){
			IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
			incomeDetailController.onReset(null);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IncomeController incomeController = (IncomeController)this.getController();
		Income income = (Income)incomeController.getTo();
		if (income.getProject() == null || income.getProject().getId() == null) {
			try {
				incomeController.removeIncomeDetailProject();
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage());
			}
		}
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		IncomeController incomeController = (IncomeController)this.getController();
		Income income = (Income)incomeController.getTo();
		if (income.getProject() != null && income.getProject().getId() != null) {
			IController incomeDetailController = FormUtil.getController(INCOME_DETAIL_CONTROLLER_NAME);
			incomeDetailController.onSearch(null);
		}
		incomeController.setListTotal(null);
	}

}