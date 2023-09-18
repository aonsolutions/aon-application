package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class WorkplaceDraft extends Composite {
	
	// ------------------------------------------------- Workpalce
	
	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange() {
			String workplacedescription = workplaceDescription.getValue();
			workplaceDraftObject.setWorkplaceDescription(workplacedescription);
			setToolbaTitle(workplacedescription);
			setHasChange(true);
		}

		@Override
		public void onWorkplaceAddressChange(Integer addressId) {
			workplaceDraftObject.setWorkplaceAddress(AonNumberUtils.equals(-1, addressId) ? null : addressId);
			setHasChange(true);
		}

		@Override
		public void onWorkplaceEconomicConcertChange() {
			Byte economicConcert = Byte.valueOf(this.workplaceEconomicConcert.getSelectedValue());
			workplaceDraftObject.setWorkplaceEconomicConcert(economicConcert);
			setHasChange(true);
		}

		@Override
		public void onWorkplaceAgreementChange(Integer agreementId) {
			workplaceDraftObject.setWorkplaceAgreement(agreementId);
			setHasChange(true);
		}

		@Override
		public void onWorkplaceActivityChange(Integer activityId) {
			workplaceDraftObject.setWorkplaceActivity(AonNumberUtils.equals(-1, activityId) ? null : activityId);
			setHasChange(true);
		}

		@Override
		public void fireErrorMessage(Map<String, String> messages) {
			showErrorMessage(messages);
		}

		@Override
		public void fireHideMessage() {
			hideMessage();
		}
		
	}
	
	// ------------------------------------------------- UiBinder

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, WorkplaceDraft> {}

	// ------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel centerContainer;
	
	// ------------------------------------------------- Variables

	private WorkplaceDraftObject workplaceDraftObject;
	
	private Workplace workplace;
	
	private AonToolbar toolbar;
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;
	
	// ------------------------------------------------- Constructor

	public WorkplaceDraft() {
		workplace = new WorkplaceImplementation();
		getToolbarPanel();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(workplace);
	}

	// ------------------------------------------------- setWorkplaceDraftObject

	public void setWorkplaceDraftObject(WorkplaceDraftObject workplaceDraftObject) {
		this.workplaceDraftObject = workplaceDraftObject;
		this.workplaceDraftObject.initializeWorkplace(
				s -> { 
						initializeView();
						setHasChange(false);
					 }
				, f -> {}
		);
	}
	
	private void initializeView() {
		setToolbaTitle(workplaceDraftObject.getWorkplaceDescription());
		
		workplace.initializeView();
		initializeListBox();
		fillWorkplaceInfo();	
	}

	private void initializeListBox() {
		//DIRECCION
		workplace.initializeAddressCell(workplaceDraftObject.getWorkplaceAddresses());
		
		//CALENDARIO
		workplace.initializeCalendarCell(workplaceDraftObject.getWorkplaceInfo().getCalendarDescription(), workplaceDraftObject.getCalendarDraftObjectData());
		
		// CONVENIO
		workplace.initializeAgreementCell(workplaceDraftObject.getWorkplaceAgreements());
		
		//ACTIVIDADES
		workplace.initializeActivityCell(workplaceDraftObject.getWorkplaceActivities());
	}

	private void fillWorkplaceInfo() {
		workplace.workplaceDescription.setValue(workplaceDraftObject.getWorkplaceDescription());
		if(!workplaceDraftObject.getWorkplaceAddresses().isEmpty()) 
			setSelectedValueLB((ListBox) workplace.workplaceAddressPanel.getWidget(0), workplaceDraftObject.getWorkplaceAddress());
		setSelectedValueLB(workplace.workplaceEconomicConcert, workplaceDraftObject.getWorkplaceEconomicConcert());	
		if(!workplaceDraftObject.getWorkplaceAgreements().isEmpty()) 
			((SuggestBox) workplace.workplaceAgreementPanel.getWidget(0)).setValue(workplaceDraftObject.getAgreementDescription());
		if(!workplaceDraftObject.getWorkplaceActivities().isEmpty())
			setSelectedValueLB((ListBox) workplace.workplaceActivityPanel.getWidget(0), workplaceDraftObject.getWorkplaceActivity());
	}
	
	private void setSelectedValueLB(ListBox lBox, String str) {
	    String text = str;
	    int indexToFind = 0;
	    for (int i = 0; i < lBox.getItemCount(); i++) {
	        if (lBox.getValue(i).equals(text)) {
	            indexToFind = i;
	            break;
	        }
	    }
	    lBox.setSelectedIndex(indexToFind);
	}
	
	// ------------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		toolbar = new AonToolbar("Centro de trabajo");

		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.ensureDebugId("acceptWorkplaceBtn");
		acceptButton.addClickHandler(e -> onAccept());
		toolbar.add(acceptButton);
		
		undoAllButton = new AonToolbarButton( AON.MSG.undo() + " todo", AON.CSS.aonIconUndoAll() );
		undoAllButton.ensureDebugId("undoAllButton");
		undoAllButton.addClickHandler(e -> {
			AonDialog confirmDialog =  new AonDialog("Restaurar CT", new HTMLPanel("\u00bfDesea realmente deshacer los cambios realizados en el centro de trabajo <b>" + workplaceDraftObject.getAgreementDescription() + "</b> \u003f <br>Este proceso es irreversible."));
			confirmDialog.confirm(new AonAcceptDialogCallback() {
				
				@Override
				public void onCancel() {
					// Nothing to do
				}
				
				@Override
				public void onAccept() {
					workplaceDraftObject.initializeWorkplace(
							s -> { 
									initializeView();
									setHasChange(false);
								 }
							, f -> {}
					);
				}
			});
		});
		toolbar.add(undoAllButton);
		
		AonToolbarButton newContract = new AonToolbarButton( "Nuevo contrato", AON.CSS.aonIconAdd() );
		newContract.addClickHandler(e -> onNewContract());
		toolbar.add(newContract);
	}
	
	private void setToolbaTitle(String title) {
		toolbar.setTitle(title);
	}
	
	// ------------------------------------------------- Toolbar.Methods
	
	private void onAccept() {
		workplaceDraftObject.updateWorkplace(
				r -> {
					showSuccessMessage(new HashMap<String, String>(){{ put("CT Guardado", "Todos los cambios han sido guardados correctamente"); }});
					setHasChange(false);
				}, 
				t -> {}
		);
	}
	
	private void onNewContract() {
		EmployeeTree.showNewContract();
	}

	private void setHasChange(boolean hasChange) {
		this.hasChange = hasChange;
		acceptButton.setEnabled(this.hasChange);
		undoAllButton.setEnabled(this.hasChange);
	}
	
	// -------------------------------------------------- Abstract Methods

	protected abstract void showSuccessMessage(Map<String, String> messages);
	protected abstract void showErrorMessage(Map<String, String> messages);
	protected abstract void hideMessage();
	
}
