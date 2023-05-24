package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class MainContrataIT extends MainEntryPoint {
	
	// --------------------------------------------------- ITWidget Impl
	
	private class ITWidgetImpl extends ITWidget {
		
		@Override
		protected void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			mainContrataITObject.getEmployeesInfo(true, 
				success::accept,  
				f -> {}
			);
		}
		
		@Override
		protected List<IT> getITsList() {
			return mainContrataITObject.getITsList();
		}

		@Override
		public boolean isUserComunica() {
			return mainContrataITObject.isUserComunica();
		}

		@Override
		public SortedSet<Integer> getAviableYears() {
			return mainContrataITObject.getAviableYears();
		}

		@Override
		public ITEmployee getITEmployee(Integer contractId) {
			return mainContrataITObject.getITEmployee(contractId);
		}

		@Override
		public IT getIT(Integer itId) {
			return mainContrataITObject.getITs(itId);
		}

		@Override
		protected List<ITEmployee> getActiveEmployeesList() {
			return mainContrataITObject.getActiveEmployeesList();
		}

		@Override
		protected List<ITEmployee> getFilterITEmployeeList(Boolean allContracts, Date start, Date end) {
			return mainContrataITObject.getEmployeesList(allContracts, start, end);
		}

		@Override
		protected void setITEmployeeList(List<ITEmployee> itEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			mainContrataITObject.setEmployeesInfo(itEmployees,
				success::accept, 
				f -> {}
			);
		}

		@Override
		protected void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure) {
			mainContrataITObject.createUpdateITEmployee(itEmployee,
				success::accept, 
				f -> {}
			);
		}

		@Override
		protected void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.removeIT(itEmployee, it,
					s -> {
						if(Boolean.TRUE.equals(it.isComunicate()) && mainContrataITObject.isUserComunica())
							mainContrataITObject.deleteComunicateIT(itEmployee, it, 
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
			mainContrataITObject.deleteIT(it,
					s -> {
						if(Boolean.TRUE.equals(it.isComunicate()) && mainContrataITObject.isUserComunica())
							mainContrataITObject.deleteComunicateIT(itEmployee, it, 
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
			mainContrataITObject.comunicateITBaja(itEmployee, it, 
				success::accept, 
				d -> {}
			);
		}

		@Override
		protected void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.comunicatePaternityIT(itEmployee, it, 
				success::accept, 
				d -> {}
			);
		}

		@Override
		protected void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure) {
			mainContrataITObject.getNafxIpf(itEmployee, 
				success::accept,  
				f -> {}
			);
		}
		
		@Override
		protected void syncITs(Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.syncITs( 
					success::accept, 
					failure::accept);
		}
		
		
		@Override
		protected void communicateITPart(ITEmployee itEmployee, IT it, ITPart itPart, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.communicateITPart(itEmployee, it, itPart,
					success::accept, 
					failure::accept);
		}
		
		@Override
		protected void checkStatus(Consumer<EnterpriseITStatus> success, Consumer<Throwable> failure) {
			mainContrataITObject.checkStatus(success::accept, failure::accept);
		}
				
		
		@Override
		protected void saveITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.saveITParts(list,
					success::accept, 
					failure::accept);
		}

		@Override
		protected void removeITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.removeITParts(list,
					success::accept, 
					failure::accept);
		}
	}
	
	// --------------------------------------------------- Binder
	
	interface Binder extends UiBinder<Widget, MainContrataIT> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	// --------------------------------------------------- UiField
	
	@UiField (provided = true)
	ITWidget itWidget;
	
	// --------------------------------------------------- Variables
	
	private MainContrataITObject mainContrataITObject;
	
	// --------------------------------------------------- Constructor
	
	public MainContrataIT() {		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		itWidget = new ITWidgetImpl();

		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
	}
	
	// --------------------------------------------------- OnModuleLoad
	
	@Override
	public void onModuleLoad() {
		onModuleLoad(new MainContrataITObject());
	}
	
	public void onModuleLoad(MainContrataITObject mainContrataITObject) {
		this.mainContrataITObject = mainContrataITObject;
		itWidget.loadITWidget();
	}
	
}
