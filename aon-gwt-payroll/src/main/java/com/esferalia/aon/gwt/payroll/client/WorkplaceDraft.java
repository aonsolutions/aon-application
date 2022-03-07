package com.esferalia.aon.gwt.payroll.client;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
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

public class WorkplaceDraft extends Composite {
	
	// ------------------------------------------------- Workpalce
	
	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange() {
			String workplacedescription = workplaceDescription.getValue();
			workplaceDraftObject.setWorkplaceDescription(workplacedescription);
		}

		@Override
		public void onWorkplaceAddressChange(Integer addressId) {
			workplaceDraftObject.setWorkplaceAddress(AonNumberUtils.equals(-1, addressId) ? null : addressId);
		}

		@Override
		public void onWorkplaceEconomicConcertChange() {
			Byte economicConcert = Byte.valueOf(this.workplaceEconomicConcert.getSelectedValue());
			workplaceDraftObject.setWorkplaceEconomicConcert(economicConcert);
		}

		@Override
		public void onWorkplaceAgreementChange(Integer agreementId) {
			workplaceDraftObject.setWorkplaceAgreement(agreementId);
		}

		@Override
		public void onWorkplaceActivityChange(Integer activityId) {
			workplaceDraftObject.setWorkplaceActivity(AonNumberUtils.equals(-1, activityId) ? null : activityId);
		}

		@Override
		public void fireWarningMessage(Map<String, String> warningMap) {
			AonMessagePanel.showWarning(messagePanel, warningMap);
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
	
	@UiField
	HTMLPanel messagePanel;
	
	// ------------------------------------------------- Variables

	private WorkplaceDraftObject workplaceDraftObject;
	
	private Workplace workplace;

	private AonToolbar toolbar;
	private AonToolbarButton undoAll;
	private AonToolbarButton undo;
	private AonToolbarButton redo;
	
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
				   	   	initializeUndoRedo();
					 }
				, f -> {}
		);
	}
	
	private void initializeView() {
		workplace.initializeView();
		initializeListBox();
		fillWorkplaceInfo();	
	}
	
	private void initializeUndoRedo() {
		undo.setEnabled(workplaceDraftObject.canUndo());
		undoAll.setEnabled(workplaceDraftObject.canUndo());
		redo.setEnabled(workplaceDraftObject.canRedo());

		workplaceDraftObject.addUndoManagerListener( undoManager -> {
			undo.setEnabled(undoManager.canUndo());
			undoAll.setEnabled(undoManager.canUndo());
			redo.setEnabled(undoManager.canRedo());
		});
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
//			setSelectedValueLB((ListBox) workplace.workplaceAgreementPanel.getWidget(0), workplaceDraftObject.getWorkplaceAgreement());
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

		AonToolbarButton accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.addClickHandler(e -> onAccept());
		toolbar.add(accept);
		
		AonToolbarButton newContract = new AonToolbarButton( "Nuevo contrato", AON.CSS.aonIconAdd() );
		newContract.addClickHandler(e -> onNewContract());
		toolbar.add(newContract);
		
		undoAll = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoAll.addClickHandler(e -> onUndoAll());
		toolbar.add(undoAll);
		
		undo = new AonToolbarButton( AON.MSG.undo(), AON.CSS.aonIconUndo() );
		undo.addClickHandler(e -> onUndo());
		toolbar.add(undo);
		
		redo = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redo.addClickHandler(e -> onRedo());
		toolbar.add(redo);
	}
	
	// ------------------------------------------------- Toolbar.Methods
	
	private void onAccept() {
		workplaceDraftObject.updateWorkplace(
				r -> {
					Map<String, String> successMap = new HashMap<>();
					successMap.put("Centro trabajo guardado", "Todos los cambios han sido guardados correctamente");
					AonMessagePanel.showSuccess(messagePanel, successMap);
				}, 
				t -> {}
		);
	}
	
	private void onNewContract() {
		EmployeeTree.showNewContract();
	}

	private void onUndoAll() {
		while ( workplaceDraftObject.canUndo() )
			workplaceDraftObject.undo();
		initializeView();
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("Cambios deshechos", "Todos los cambios han sido deshechos");
		AonMessagePanel.showInfo(messagePanel, infoMap);
	}

	private void onUndo() {
		workplaceDraftObject.undo();
		initializeView();
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("Cambio deshecho", "El \u00FAltimo cambio ha sido deshecho");
		AonMessagePanel.showInfo(messagePanel, infoMap);
	}
	
	private void onRedo() {
		workplaceDraftObject.redo();
		initializeView();
		Map<String, String> infoMap = new HashMap<>();
		infoMap.put("Cambio rehecho", "El \u00FAltimo cambio ha sido rehecho");
		AonMessagePanel.showInfo(messagePanel, infoMap);
	}
	
}
