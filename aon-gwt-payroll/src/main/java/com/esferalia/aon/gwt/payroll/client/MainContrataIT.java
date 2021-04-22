package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

public class MainContrataIT extends MainEntryPoint {
	
	// --------------------------------------------------- ITWidget Impl
	
	private class ITWidgetImpl extends ITWidget {
		
		@Override
		protected void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			mainContrataITObject.getEmployeesInfo(true, itEmployeeList -> {
				success.accept(itEmployeeList);
			}, f -> {});
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
				itEmployeeList -> {
					success.accept(itEmployeeList);
				},
				f -> {}
			);
		}

		@Override
		protected void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure) {
			mainContrataITObject.createUpdateITEmployee(itEmployee,
				message -> {
					success.accept(message);
				},
				f -> {});
		}

		@Override
		protected void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.removeIT(itEmployee, it,
					s -> {
						if(it.isComunicate() && mainContrataITObject.isUserComunica())
							mainContrataITObject.deleteComunicateIT(itEmployee, it, t -> {
								success.accept(t);
							}, d -> {});
						else
							success.accept(s);
					},
					f -> {});
		}

		@Override
		protected void deletePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.deleteIT(it,
					s -> {
						if(it.isComunicate() && mainContrataITObject.isUserComunica())
							mainContrataITObject.deleteComunicateIT(itEmployee, it, t -> {
								success.accept(t);
							}, d -> {});
						else
							success.accept(s);
					},
					f -> {});
		}

		@Override
		protected void comunicateIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			mainContrataITObject.comunicateITBaja(itEmployee, it, t -> {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
				success.accept(t);
			}, d -> {});
		}

		@Override
		protected void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success,
				Consumer<Throwable> failure) {
			mainContrataITObject.comunicatePaternityIT(itEmployee, it, t -> {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
				success.accept(t);
			}, d -> {});
		}

		@Override
		protected void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure) {
			mainContrataITObject.getNafxIpf(itEmployee, employeeSegSocial -> {
				success.accept(employeeSegSocial);
			}, f -> {});
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
	
	public void onModuleLoad(MainContrataITObject mainContrataITObject) {
		this.mainContrataITObject = mainContrataITObject;
		itWidget.loadITWidget();
	}
	
}
