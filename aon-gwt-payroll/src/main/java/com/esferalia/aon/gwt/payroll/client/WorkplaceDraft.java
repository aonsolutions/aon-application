package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.google.gwt.user.client.ui.HTMLPanel;

public class WorkplaceDraft extends AonCustomDockLayout {
	
	// ------------------------------------------------- Workpalce
	
	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange(String workplacedescription) {
			workplaceDraftObject.setWorkplaceDescription(workplacedescription);
			setToolbarTitle(workplacedescription);
			setHasChange(true);
		}

		@Override
		public void onWorkplaceAddressChange(Integer addressId) {
			workplaceDraftObject.setWorkplaceAddress(AonNumberUtils.equals(-1, addressId) ? null : addressId);
			setHasChange(true);
		}

		@Override
		public void onWorkplaceEconomicConcertChange(Byte economicConcert) {
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
		
	}
	
	// ------------------------------------------------- Variables

	private WorkplaceDraftObject workplaceDraftObject;
	
	private Workplace workplace;
	
	private AonToolbarButton acceptButton;
	private AonToolbarButton undoAllButton;
	
	private boolean hasChange;
	
	// ------------------------------------------------- Constructor

	public WorkplaceDraft() {
		super("Centro Trabajo");
		
		getToolbarPanel();
		hideSearchWidget();
		
		workplace = new WorkplaceImplementation();
		add(workplace);
	}
	
	@Override
	protected void onClearFilter() {}

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
		setToolbarTitle(workplaceDraftObject.getWorkplaceDescription());
		
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
		workplace.fillWorkplace(workplaceDraftObject.getWorkplaceInfo());
	}
	
	// ------------------------------------------------- Toolbar
	
	private void getToolbarPanel() {
		acceptButton = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		acceptButton.ensureDebugId("acceptWorkplaceBtn");
		acceptButton.addClickHandler(e -> onAccept());
		addToolbarButton(acceptButton);
		
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
		addToolbarButton(undoAllButton);
		
		AonToolbarButton newContract = new AonToolbarButton( "Nuevo contrato", AON.CSS.aonIconAdd() );
		newContract.addClickHandler(e -> onNewContract());
		addToolbarButton(newContract);
	}
	
	// ------------------------------------------------- Toolbar.Methods
	
	private void onAccept() {
		workplaceDraftObject.updateWorkplace(
				r -> {
					workplace.showSucces("Todos los cambios han sido guardados correctamente");
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
}
