package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.css.images.Images;
import com.esferalia.aon.gwt.common.client.widget.DetailPanel;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.common.shared.NumberUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

interface Listener {

	void onDeleteAgreementAction(Agreement agreement);

	void onRestoreAgreementAction(Agreement agreement);
	
	void onDeleteEmployeeAction(Employee employee);
	
	void onRestoreEmployeeAction(Employee employee);

}

public class MainTrash extends MainEntryPoint implements
		AgreementsTree.Listener, Listener, EmployeesTrashTree.Listener {

	static interface Binder extends UiBinder<Widget, MainTrash> {
	}

	private static final Images IMAGES = GWT.create(Images.class);

	private static final Binder binder = GWT.create(Binder.class);


	@UiField
	Button deleteAgreementButton;
	@UiField
	Button restoreAgreementButton;
	@UiField
	Button clearEmployeeButton;
	@UiField
	Button deleteEmployeeButton;
	@UiField
	Button restoreEmployeeButton;
	@UiField
	DetailPanel detailPanel;
	@UiField
	EmployeesTrashTree employeeTrash;	

	@UiField
	AgreementsTree agreementsTree;	
	
	private Employee employee;
	private AgreementDraft agreementDraft;
	private JSF jsf;
	private Integer domain;
	private Agreement agreement;
	private List<Listener> listeners;
	private Map<Integer, AgreementDraftObject> agreementDrafts;

	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources> create(
				MainEntryPoint.CodeMirrorResources.class).css()
				.ensureInjected();

		// Create the UI defined in Employee.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel");
		root.add(ui);

		this.listeners = new ArrayList<Listener>();
		this.agreementsTree.addListener(this);
		this.employeeTrash.addListener(this);
		this.employee = null;
		this.agreement = null;
		this.agreementDraft = new AgreementDraft();
		this.jsf = new JSF();
		this.agreementDrafts = new HashMap<Integer, AgreementDraftObject>();

		addListener(this);

		agreementsTree.getEnterpriseService().getDomain(
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						MainTrash.this.domain = result;
						MainTrash.this.getAgreements();
					}
				});
		
		loadEnterprises();
	}
	
	private void loadEnterprises() {
		employeeTrash.clear();
		employeeTrash.getEmployeesService().getEnterprises(
				new AsyncCallback<Enterprise[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Enterprise[] enterprises) {
						for(Enterprise enterprise : enterprises)
							onEnterprise(enterprise);
					}
				});
		
	}

	public void addListener(Listener listener) {
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public Integer getDomain() {
		return this.domain;
	}

	@UiHandler("deleteAgreementButton")
	void onDeleteClickButton(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onDeleteAgreementAction(agreement);
	}

	@UiHandler("restoreAgreementButton")
	void onRestoreClickButton(ClickEvent event) {
		for (Listener listener : listeners)
			listener.onRestoreAgreementAction(agreement);
	}
	
	@UiHandler("deleteEmployeeButton")
	void onDeleteEmployeeButton(ClickEvent event) {
		if(this.employee != null)
			for(Listener listener : listeners)
				listener.onDeleteEmployeeAction(this.employee);
	}
	
	@UiHandler("restoreEmployeeButton")
	void onRestoreEmployeeButton(ClickEvent event) {
		if(this.employee != null)
			for(Listener listener : listeners)
				listener.onRestoreEmployeeAction(this.employee);
	
	}


	private boolean getTreeCount() {
		return agreementsTree.getTree().getItemCount() > 0;
	}

	@Override
	public void onRestoreAgreementAction(Agreement agreement) {

		agreementsTree.getEnterpriseService().updateAgreementId(agreement,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());

					}

					@Override
					public void onSuccess(Void result) {
						MainTrash.this.detailPanel.setWidget(null);
						MainTrash.this.getAgreements();
					}
				});
	}
	
	@Override
	public void onDeleteAgreementAction(Agreement agreement) {
		
		if(Window.confirm("\u00BFDesea eliminar definitivamente el convenio "
					+ agreement.getDescription() + "\u003F")) {
			agreementsTree.getEnterpriseService().deleteAgreement(agreement, new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());				
				}

				@Override
				public void onSuccess(Void result) {
					MainTrash.this.detailPanel.setWidget(null);
					MainTrash.this.getAgreements();				
				}
			});
			
		}
	}
	
	@Override
	public void onDeleteEmployeeAction(Employee employee) {
		
		if(Window.confirm("\u00BFDesea eliminar a "
					+ employee.getFullname() + "\u003F")) {
			
			employeeTrash.getEmployeesService().deleteContract(employee, 
					new AsyncCallback<Void>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());			
				}

				@Override
				public void onSuccess(Void result) {
					MainTrash.this.detailPanel.setWidget(null);
					MainTrash.this.loadEnterprises();			
				}
			});
			
		}
		
	}
	
	@Override
	public void onRestoreEmployeeAction(Employee employee) {
		
		employeeTrash.getEmployeesService().moveContractId(employee, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				MainTrash.this.detailPanel.setWidget(null);
				MainTrash.this.loadEnterprises();
			}
		});
		
	}
	
	private void onEnterprise(Enterprise enterprise) {
		
		List<Workplace> workplaces = enterprise.getWorkplaces();
		
		for(Workplace workplace : workplaces)
			onWorkplace(workplace.getId());
	}
	
	private void onWorkplace(int workplaceId) {
		
		employeeTrash.getEmployeesService().getTrashEmployees(workplaceId, 
				new AsyncCallback<List<Employee>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(List<Employee> employees) {
				for(Employee employee : employees)
					employeeTrash.addEmployeeItem(employee);
			}
		});
	}

	// ---------------------------------------------------- Agreements.Listener

	@Override
	public boolean evaluateId(Agreement agreement) {
		return agreement.getId() < 0;
	}

	@Override
	public void getAgreements() {
		agreementsTree.clearTree();
		agreementsTree.getEnterpriseService().getAgreements(0, 10000,
				new AsyncCallback<List<Agreement>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(List<Agreement> agreements) {

						int item2Select = -1;
						for (int i = 0; i < agreements.size(); i++) {

							Agreement agreement = agreements.get(i);
							if (evaluateId(agreement))
								addAgreementItem(agreement);

							if (agreement.isRedefined()) {
								if (item2Select == -1)
									item2Select = i;
							}
							if (agreement.hasEmployees()) {
								if (item2Select == -1)
									item2Select = i;
							}

						}
						// Select the first one.
						if ( getTreeCount() ) {							
							agreementsTree.getTree().setSelectedItem(
									agreementsTree.getTree().getItem(
											Math.max(item2Select, 0)), true);
							Agreement agreement = (Agreement) agreementsTree
									.getSelectedItem().getUserObject();
							agreementSelected(agreement);
						}
					}
				});
	}

	private TreeItem addAgreementItem(Agreement agreement) {		
		String style = AON.AON_GREEN;
		String description = agreement.getDescription();
		String title = "El convenio '" + description + "' puede eliminarse definitivamente.";
		if (agreement.getHasContract()) {
			title = "Imposible eliminar '" +description + "'. " 
					+ "Convenio asociado a contratos";
			style = AON.AON_RED + " " + AON.AON_ICON_CMD_BUTTON;
		}

		List<ImageResource> marks = new ArrayList<ImageResource>();
		if (NumberUtils.notEquals(domain, agreement.getDomain()))
			marks.add(IMAGES.parent());

		TreeItem treeItem = new TreeItem(AgreementsTree.imageItemSafeHtml(
				description, AgreementsTree.getImageResource(agreement),
				marks.toArray(new ImageResource[marks.size()])));
		treeItem.setStyleName(style);
		treeItem.setTitle(title);
		treeItem.setUserObject(agreement);

		agreementsTree.getTree().addItem(treeItem);

		return treeItem;
	}

	@Override
	public void onTreeItemSelected(SelectionEvent<TreeItem> event) {
		TreeItem selectedItem = event.getSelectedItem();
		Object object = selectedItem.getUserObject();

		if (object instanceof Agreement)
			agreementSelected((Agreement) object);

	}

	private void agreementSelected(Agreement agreement) {		
		
		this.agreement = agreement;
		this.restoreAgreementButton.setEnabled(true);
		this.deleteAgreementButton.setEnabled(!agreement.getHasContract());
		
		detailPanel.setWidget(agreementDraft);
		
		AgreementDraftObject agreementDraftObject = agreementDrafts
				.get(agreement.getId());
		if (agreementDraftObject == null) {
			com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft = 
					new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

			draft.setId(agreement.getId());
			draft.setDomain(agreement.getDomain());
			draft.setDescription(agreement.getDescription());

			draft.setStartDate(DateUtils.getFirstDayOfMonth());
			draft.setEndDate(DateUtils.getLastDayOfMonth());

			agreementDraftObject = new AgreementDraftObject(
					getDomain(),
					Wnd.getCurrentDomainNameURL(),
					draft,
					agreementsTree.getEmployeesService()) {
				@Override
				public boolean isMine() {
					return false;
				}
			};
			agreementDrafts.put(agreement.getId(), agreementDraftObject);			
	
		} // end-if: Not exists, create it then...
//		else {
		agreementDraft.setAgreementDraftObject(agreementDraftObject);
//		}
//		agreementDraft.setReadOnly(true);
	}

	@Override
	public void onAgreementCtrlC(Agreement agreement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAgreementCtrlV(Agreement agreement) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAgreementSupr(Agreement agreement) {
		if( ! agreement.getHasContract() ) {
			for(Listener listener : listeners)
				listener.onDeleteAgreementAction(agreement);	
			
		}
	}

	@Override
	public void onAgreementContextMenu(Agreement agreement,
			ContextMenuEvent event) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onSuprKeyDown(Employee employee) {
		
		
	}
	
	public void disablePopup() {
		
	}
	
	@Override
	public void onEmployeeItemSelected(Employee employee) {
		this.employee = employee;
		
		detailPanel.setWidget(jsf);
		jsf.employeeSelected(employee.getId());
	}
}
