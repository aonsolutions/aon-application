package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMinimizePanel;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class EnterpriseIT extends Composite {
	
	// --------------------------------------------------- ITWidget Impl
	
	private class ITWidgetImpl extends ITWidget {
		
		@Override
		protected void getITEmployeeListDB(Consumer<List<ITEmployee>> success, Consumer<Throwable> failure) {
			enterpriseITObject.getEmployeesInfo(true, success::accept, failure::accept);
		}
		
		@Override
		protected List<IT> getITsList() {
			return enterpriseITObject.getITsList();
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
			enterpriseITObject.setEmployeesInfo(itEmployees, success::accept, failure::accept);
		}

		@Override
		protected void setITEmployee(ITEmployee itEmployee, Consumer<String> success, Consumer<Throwable> failure) {
			enterpriseITObject.createUpdateITEmployee(itEmployee, success::accept, failure::accept);
		}

		@Override
		protected void deleteIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.removeIT(itEmployee, it,
				s -> {
					if(Boolean.TRUE.equals(it.isComunicate()) && enterpriseITObject.isUserComunica()) {
						enterpriseITObject.deleteComunicateIT(itEmployee, it, success::accept, failure::accept);
				    } else {					    
					    success.accept(s);
					}
				},
				failure::accept
			);
		}

		@Override
		protected void deletePaternityIT(ITEmployee itEmployee, IT it, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.deleteIT(it,
				s -> {
					if(Boolean.TRUE.equals(it.isComunicate()) && enterpriseITObject.isUserComunica()) {
						enterpriseITObject.deleteComunicateIT(itEmployee, it, success::accept, failure::accept);
					} else {
					    success.accept(s);					    
					}
				},
				failure::accept
			);
		}

		@Override
		protected void getNafxIpf(ITEmployee itEmployee, Consumer<EmployeeSegSocial> success, Consumer<Throwable> failure) {
			enterpriseITObject.getNafxIpf(itEmployee, success::accept, failure::accept);
		}

		@Override
		protected void syncITs(Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.syncITs(success::accept, failure::accept);
		}

		@Override
		protected void sendEconomicData(ITEmployee itEmployee, IT it, ITPart part, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.sendEconomicData(itEmployee, it, part, success::accept, failure::accept);
		}

		@Override
		protected void checkStatus(Consumer<EnterpriseITStatus> success, Consumer<Throwable> failure) {
			enterpriseITObject.checkStatus(success::accept, failure::accept);
		}
				
		@Override
		protected void saveITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.saveITParts(list, success::accept, failure::accept);
		}

		@Override
		protected void removeITParts(List<ItNotExist> list, Consumer<Void> success, Consumer<Throwable> failure) {
			enterpriseITObject.removeITParts(list, success::accept, failure::accept);
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
	
	// ------ setFooter
	public void setFooter(SplitLayoutPanel dockLayoutPanel, TabLayoutPanel tab, AonMinimizePanel footPanel) {		
		itWidget.setFooter(dockLayoutPanel, tab, footPanel);
	}
}
