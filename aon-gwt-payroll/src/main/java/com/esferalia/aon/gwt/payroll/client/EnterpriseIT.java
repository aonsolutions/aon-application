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
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseIT extends Composite {
	
	// --------------------------------------------------- ITWidget Impl
	
	private class ITWidgetImpl extends ITWidget {
		
		@Override
		protected void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			enterpriseITObject.getEmployeesInfo(true, itEmployeeList -> {
				success.accept(itEmployeeList);
			}, f -> {});
		}

		@Override
		public boolean isUserComunica() {
			return enterpriseITObject.isUserComunica();
		}

		@Override
		public SortedSet<Integer> getAviableYears() {
			return enterpriseITObject.getAviableYears();
		}

		@Override
		public ITEmployee getITEmployee(Integer contractId) {
			return enterpriseITObject.getITEmployee(contractId);
		}

		@Override
		public IT getIT(Integer itId) {
			return enterpriseITObject.getITs(itId);
		}

		@Override
		protected List<ITEmployee> getActiveEmployeesList() {
			return enterpriseITObject.getActiveEmployeesList();
		}

		@Override
		protected List<ITEmployee> getFilterITEmployeeList(Boolean allContracts, Date start, Date end) {
			return enterpriseITObject.getEmployeesList(allContracts, start, end);
		}

		@Override
		protected void setITEmployeeList(List<ITEmployee> itEmployees, Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			enterpriseITObject.setEmployeesInfo(itEmployees,
				itEmployeeList -> {
					success.accept(itEmployeeList);
				},
				f -> {}
			);
		}

		@Override
		protected void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure) {
			enterpriseITObject.createUpdateITEmployee(itEmployee,
				message -> {
					success.accept(message);
				},
				f -> {});
		}

		@Override
		protected void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.removeIT(itEmployee, it,
					s -> {
						if(it.isComunicate() && enterpriseITObject.isUserComunica())
							enterpriseITObject.deleteComunicateIT(itEmployee, it, t -> {
								success.accept(t);
							}, d -> {});
						else
							success.accept(s);
					},
					f -> {});
		}

		@Override
		protected void deletePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.deleteIT(it,
					s -> {
						if(it.isComunicate() && enterpriseITObject.isUserComunica())
							enterpriseITObject.deleteComunicateIT(itEmployee, it, t -> {
								success.accept(t);
							}, d -> {});
						else
							success.accept(s);
					},
					f -> {});
		}

		@Override
		protected void comunicateIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.comunicateITBaja(itEmployee, it, t -> {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
				success.accept(t);
			}, d -> {});
		}

		@Override
		protected void comunicatePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success,
				Consumer<Throwable> failure) {
			enterpriseITObject.comunicatePaternityIT(itEmployee, it, t -> {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: COMUNICA", "El parte IT ha sido comunicado correctamente");
				success.accept(t);
			}, d -> {});
		}

		@Override
		protected void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure) {
			enterpriseITObject.getNafxIpf(itEmployee, employeeSegSocial -> {
				success.accept(employeeSegSocial);
			}, f -> {});
		}

	}
	
	// --------------------------------------------------- Binder
	
	interface Binder extends UiBinder<Widget, EnterpriseIT> {}
	
	private static final Binder uiBinder = GWT.create(Binder.class);
	
	// --------------------------------------------------- UiField
	
	@UiField (provided = true)
	ITWidget itWidget;
	
	// --------------------------------------------------- Variables
	
	private EnterpriseITObject enterpriseITObject;
	
	// --------------------------------------------------- Constructor
	
	public EnterpriseIT() {		
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
		
		itWidget = new ITWidgetImpl();

		initWidget(uiBinder.createAndBindUi(this)); 
	}
	
	// --------------------------------------------------- OnModuleLoad
	
	public void setEnterpriseITObject(EnterpriseITObject enterpriseITObject) {
		this.enterpriseITObject = enterpriseITObject;
		itWidget.loadITWidget();
	}
	
}
