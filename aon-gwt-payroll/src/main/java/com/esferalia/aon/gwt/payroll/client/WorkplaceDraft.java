package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDraft extends Composite {
	
	private class WorkplaceImplementation extends Workplace{

		@Override
		public void onWorkplaceDescriptionChange() {
			String workplacedescription = workplaceDescription.getValue();
			if(AonStringUtils.isBlank(workplacedescription)) {
				AonMessageDialog.warning("Este campo es obligatorio");
				workplaceDescription.setValue(workplaceDraftObject.getWorkplaceDescription());
			} else
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
			workplaceDraftObject.setWorkplaceAgreement(AonNumberUtils.equals(-1, agreementId) ? null : agreementId);
		}

		@Override
		public void onWorkplaceActivityChange(Integer activityId) {
			workplaceDraftObject.setWorkplaceActivity(AonNumberUtils.equals(-1, activityId) ? null : activityId);
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, WorkplaceDraft> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String container();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel centerContainer;
	
	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private WorkplaceDraftObject workplaceDraftObject;
	
	private Workplace workplace;

	private AonToolbar toolbar;
	private AonToolbarButton accept;
	private AonToolbarButton newContract;
	private AonToolbarButton undoAll;
	private AonToolbarButton undo;
	private AonToolbarButton redo;
	
	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public WorkplaceDraft() {
		workplace = new WorkplaceImplementation();
		toolbar = getToolbarPanel();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		dockLayoutPanel.addStyleName(style.container());
		
		centerContainer.add(workplace);
	}

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

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

		workplaceDraftObject.addUndoManagerListener( (undoManager) -> {
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
			setSelectedValueLB((ListBox) workplace.workplaceAgreementPanel.getWidget(0), workplaceDraftObject.getWorkplaceAgreement());
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
	
	// ----------------------------------------------- TOOLBAR ------------------------------------------------
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("Centro de trabajo");

		accept = new AonToolbarButton( AON.MSG.saveAction(), AON.CSS.aonIconSave() );
		accept.setAccessKey('G');
		accept.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAccept(event);
			}
		});
		toolbar.add(accept);
		
		newContract = new AonToolbarButton( "Nuevo contrato", AON.CSS.aonIconAdd() );
		newContract.setAccessKey('N');
		newContract.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onNewContract(event);
			}
		});
		toolbar.add(newContract);
		
		undoAll = new AonToolbarButton( "Deshacer todo", AON.CSS.aonIconUndoAll() );
		undoAll.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onUndoAll(event);
			}
		});
		toolbar.add(undoAll);
		
		undo = new AonToolbarButton( AON.MSG.undo(), AON.CSS.aonIconUndo() );
		undo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onUndo(event);
			}
		});
		toolbar.add(undo);
		
		redo = new AonToolbarButton( "Rehacer", AON.CSS.aonIconRedo() );
		redo.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onRedo(event);
			}
		});
		toolbar.add(redo);

		return toolbar;

	}
	
	private void onAccept(ClickEvent event) {
		workplaceDraftObject.updateWorkplace(
				r -> {}, 
				t -> {}
		);
	}
	
	private void onNewContract(ClickEvent event) {
		EmployeeTree.showNewContract();
	}

	private void onUndoAll(ClickEvent event) {
		while ( workplaceDraftObject.canUndo() )
			workplaceDraftObject.undo();
		initializeView();
	}

	private void onUndo(ClickEvent event) {
		workplaceDraftObject.undo();
		initializeView();
	}
	
	private void onRedo(ClickEvent event) {
		workplaceDraftObject.redo();
		initializeView();
	}
	
}
