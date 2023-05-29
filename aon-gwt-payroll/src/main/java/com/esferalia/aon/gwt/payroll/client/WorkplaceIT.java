package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceIT extends Composite {
	
	// --------------------------------------------------- ITWidget Impl
	
	private class ITWidgetImpl extends ITWidget {
		
		@Override
		protected void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			workplaceITObject.getEmployeesInfo(true, 
					success::accept, 
					f -> {}
			);
		}

		@Override
		protected List<IT> getITsList() {
			return workplaceITObject.getITsList();
		}
		
		@Override
		public boolean isUserComunica() {
			return workplaceITObject.isUserComunica();
		}

		@Override
		public SortedSet<Integer> getAviableYears() {
			return workplaceITObject.getAviableYears();
		}

		@Override
		public ITEmployee getITEmployee(Integer contractId) {
			return workplaceITObject.getITEmployee(contractId);
		}

		@Override
		public IT getIT(Integer itId) {
			return workplaceITObject.getITs(itId);
		}

		@Override
		protected List<ITEmployee> getActiveEmployeesList() {
			return workplaceITObject.getActiveEmployeesList();
		}

		@Override
		protected List<ITEmployee> getFilterITEmployeeList(Boolean allContracts, Date start, Date end) {
			return workplaceITObject.getEmployeesList(allContracts, start, end);
		}

		@Override
		protected void setITEmployeeList(List<ITEmployee> itEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			workplaceITObject.setEmployeesInfo(itEmployees,
				success::accept,
				f -> {}
			);
		}

		@Override
		protected void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure) {
			workplaceITObject.createUpdateITEmployee(itEmployee,
				success::accept, 
				f -> {}
			);
		}

		@Override
		protected void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			workplaceITObject.removeIT(itEmployee, it,
					s -> {
						if(Boolean.TRUE.equals(it.isComunicate()) && workplaceITObject.isUserComunica())
							workplaceITObject.deleteComunicateIT(itEmployee, it, 
									success::accept, 
									failure::accept
							);
						else
							success.accept(s);
					},
					f -> {});
		}

		@Override
		protected void deletePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			workplaceITObject.deleteIT(it,
					s -> {
						if(Boolean.TRUE.equals(it.isComunicate()) && workplaceITObject.isUserComunica())
							workplaceITObject.deleteComunicateIT(itEmployee, it, 
									success::accept, 
									failure::accept
							);
						else
							success.accept(s);
					},
					f -> {});
		}

		@Override
		protected void comunicateIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			workplaceITObject.comunicateITBaja(itEmployee, it, 
					success::accept,  
					d -> {}
			);
		}

		@Override
		protected void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			workplaceITObject.comunicatePaternityIT(itEmployee, it, 
					success::accept,  
					d -> {}
			);
		}

		@Override
		protected void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure) {
			workplaceITObject.getNafxIpf(itEmployee, 
					success::accept,  
					f -> {}
			);
		}
		
		@Override
		protected void syncITs(Consumer<Void> success, Consumer<Throwable> failure) {
			workplaceITObject.syncITs( 
					success::accept, 
					failure::accept);
		}

		@Override
		protected void communicateITPart(ITEmployee itEmployee, IT it, ITPart itPart, Consumer<Void> success, Consumer<Throwable> failure) {
			workplaceITObject.communicateITPart(itEmployee, it, itPart,
					success::accept, 
					failure::accept);
		}
		
		@Override
		protected void checkStatus(Consumer<EnterpriseITStatus> success, Consumer<Throwable> failure) {
			failure.accept(null);
//			enterpriseITObject.checkStatus(success::accept, failure::accept);
		}

		@Override
		protected void saveITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure){}

		@Override
		protected void removeITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure){}

	}
	
	// --------------------------------------------------- Binder
	
	interface Binder extends UiBinder<Widget, WorkplaceIT> {}
	
	private static final Binder uiBinder = GWT.create(Binder.class);
	
	// --------------------------------------------------- UiField
	
	@UiField (provided = true)
	ITWidget itWidget;
	
	// --------------------------------------------------- Variables
	
	private WorkplaceITObject workplaceITObject;
	
	// --------------------------------------------------- Constructor
	
	public WorkplaceIT() {		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		itWidget = new ITWidgetImpl();
		itWidget.removeFootPanel();

		initWidget(uiBinder.createAndBindUi(this)); 
	}
	
	// --------------------------------------------------- OnModuleLoad
	
	public void setWorkplaceITObject(WorkplaceITObject workplaceITObject) {
		this.workplaceITObject = workplaceITObject;
		itWidget.loadITWidget();
	}
	
}
