package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDraft extends Composite {
	
	private class WorkplaceImplementation extends Workplace{
		
		@Override
		public void onWorkplaceDescriptionChange() {
			String value = workplace.workplaceDescription.getValue();
			
			if(StringUtils.isBlank(value)) {
				WarningDialog warningDialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
				warningDialog.center();
				warningDialog.show();
				
				//Set last good value
				workplace.workplaceDescription.setValue(workplaceDraftObject.getWorkplaceDescription());
			}
		}

		@Override
		public void onWorkplaceEconomicConcertChange() {
			Byte economicConcert = Byte.valueOf(workplaceEconomicConcert.getSelectedValue());
			workplaceDraftObject.setWorkplaceEconomicConcert(economicConcert);
			saving();
		}

		@Override
		public void onWorkplaceAgreementChange() {
			if (this.workpalceAgreement.getSelectedIndex() == 0 ) {
				workplaceDraftObject.setWorkplaceAgreement(null);
				saving();
				return;
			}
			
			Integer agreementId = Integer.valueOf(this.workpalceAgreement.getSelectedValue()); 
			workplaceDraftObject.setWorkplaceAgreement(agreementId);
			saving();
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, WorkplaceDraft> {}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	Button newContractButton;
	
	@UiField
	Label saveStatus;
	
	@UiField
	Button redoButton;

	@UiField
	Button undoButton;

	@UiField
	Button undoAllButton;
	
	@UiField (provided = true)
	Workplace workplace;
	
	// ------------------------------------------------------ VARIABLES DE LA CLASE --------------------------------------------------

	private Timer saveTimer;
	
	private WorkplaceDraftObject workplaceDraftObject;

	private Consumer<WorkplaceInfo> onSaved ;
	
	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public WorkplaceDraft() {
		workplace = new WorkplaceImplementation();
		
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		
		saveStatus.setTitle("Cada cambio que hagas se guarda autom\u00E1ticamente");
		
		onSaved = this::onSavedNoop;
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------

	@UiHandler("newContractButton")
	void onNewContractClick(ClickEvent event) {
		EmployeeTree.showNewContract();
	}
	
	@UiHandler("undoButton")
	void onUndoButtonClick(ClickEvent event) {
		workplaceDraftObject.undo();
		initializeView();
		saving();
	}

	@UiHandler("undoAllButton")
	void onUndoAllButtonClick(ClickEvent event) {
		while ( workplaceDraftObject.canUndo() )
			workplaceDraftObject.undo();
		initializeView();
		saving();
	}

	@UiHandler("redoButton")
	void onRedoButtonClick(ClickEvent event) {
		workplaceDraftObject.redo();
		initializeView();
		saving();
	}

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void setWorkplaceDraftObject(WorkplaceDraftObject workplaceDraftObject) {
		this.workplaceDraftObject = workplaceDraftObject;
		this.workplaceDraftObject.initializeWorkplace(
				s -> { initializeView();
				   	   initializeUndoRedo();
					   initializeScheduler();
					 }
				, f -> {}
		);
	}
	
	private void initializeView() {
		//SOCPE HIDE
		workplace.generalDataTable.getRows().getItem(4).getStyle().setDisplay(Display.NONE);

		resetElements();
		initializeListBox();
		fillWorkplaceInfo();	
	}
	
	private void resetElements() {
		// Clear general elements
		workplace.workplaceDescription.setValue("");
		workplace.workplaceAddressPanel.clear();

		// Clear payroll elements
		workplace.workplaceCalendarPanel.clear();
		workplace.workpalceAgreement.clear();
		workplace.workplaceActivityPanel.clear();
	}

	private void initializeUndoRedo() {
		undoButton.setEnabled(workplaceDraftObject.canUndo());
		undoAllButton.setEnabled(workplaceDraftObject.canUndo());
		redoButton.setEnabled(workplaceDraftObject.canRedo());

		workplaceDraftObject.addUndoManagerListener( (undoManager) -> {
			undoButton.setEnabled(undoManager.canUndo());
			undoAllButton.setEnabled(undoManager.canUndo());
			redoButton.setEnabled(undoManager.canRedo());
		});
	}
	

	private void initializeListBox() {
		//DIRECCION
		initializeAddressCell();
		
		//CALENDARIO
		initializeCalendarCell();
		
		// CONVENIO
		initializeAgreementCell();
		
		//ACTIVIDADES
		initializeActivityCell();
	}

	private void initializeAddressCell() {
		Widget workplaceAddressWidget;
		
		if(workplaceDraftObject.getWorkplaceAddresses().values().size() == 0)
			workplaceAddressWidget = createEmptyListLabel();
		else{
			ListBox addressListBox = new ListBox();
			addressListBox.addItem("-", "-1");
			for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceAddresses().entrySet())
				addressListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			addressListBox.setStyleName("aon-selectOneMenu");
			addressListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			addressListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					if(addressListBox.getSelectedIndex() == 0) {
						WarningDialog warningDialog = new WarningDialog("Aviso", "Hay que rellenar los campos azules obligatoriamente.");
						warningDialog.center();
						warningDialog.show();
						
						ListBox wokplaceAddressLB = (ListBox) workplace.workplaceAddressPanel.getWidget(0);
						wokplaceAddressLB.setSelectedIndex(workplaceDraftObject.getWorkplaceAddressIndex());
						
						return;
					}
					Integer activityId = Integer.valueOf(addressListBox.getSelectedValue());
					workplaceDraftObject.setWorkplaceAddress(activityId);
					saving();
				}
			});
			
			// If only one activity, selected it and fire event
			//if(addressListBox.getItemCount() != 0 && addressListBox.getItemCount() == 1){
			//	addressListBox.setSelectedIndex(1);
			//	DomEvent.fireNativeEvent(Document.get().createChangeEvent(), addressListBox);
			//}
			
			workplaceAddressWidget = addressListBox;
		}
		
		workplace.workplaceAddressPanel.add(workplaceAddressWidget);
	}
	
	private void initializeCalendarCell() {
		Widget workplaceCalendarWidget;
		
		if(workplaceDraftObject.getWorkplaceCalendars().values().size() == 0)
			workplaceCalendarWidget = createEmptyListLabel();
		else{
			ListBox calendarListBox = new ListBox();
			calendarListBox.addItem("-", "-1");
			for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceCalendars().entrySet())
				calendarListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			calendarListBox.setStyleName("aon-selectOneMenu");
			calendarListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			calendarListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Integer calendarId = Integer.valueOf(calendarListBox.getSelectedValue());
					workplaceDraftObject.setWorkplaceCalendar(calendarId);
					saving();
				}
			});
			
			// If only one calendar, selected it and fire event
			//if(calendarListBox.getItemCount() != 0 && calendarListBox.getItemCount() == 2){
			//	calendarListBox.setSelectedIndex(1);
			//	DomEvent.fireNativeEvent(Document.get().createChangeEvent(), calendarListBox);
			//}
			
			workplaceCalendarWidget = calendarListBox;
		}
		
		workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
	}
	
	private void initializeAgreementCell() {
		this.workplace.workpalceAgreement.addItem("-", "-1");
		List<Agreement> agreements = workplaceDraftObject.getWorkplaceAgreements();
		for (Agreement agreement : agreements)
			this.workplace.workpalceAgreement.addItem(agreement.getDescription(), String.valueOf(agreement.getId()));
	}

	private void initializeActivityCell() {
		Widget workplaceActivityWidget;
		
		if(workplaceDraftObject.getWorkplaceActivities().values().size() == 0)
			workplaceActivityWidget = createEmptyListLabel();
		else{
			ListBox activityListBox = new ListBox();
			activityListBox.addItem("-", "-1");
			for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceActivities().entrySet())
				activityListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			activityListBox.setStyleName("aon-selectOneMenu");
			activityListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			activityListBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					Integer activityId = Integer.valueOf(activityListBox.getSelectedValue());
					workplaceDraftObject.setWorkplaceActivity(activityId);
					saving();
				}
			});
			
			// If only one activity, selected it and fire event
			//if(activityListBox.getItemCount() != 0 && activityListBox.getItemCount() == 2){
			//	activityListBox.setSelectedIndex(1);
			//	DomEvent.fireNativeEvent(Document.get().createChangeEvent(), activityListBox);
			//}
			
			workplaceActivityWidget = activityListBox;
		}
		
		workplace.workplaceActivityPanel.add(workplaceActivityWidget);
	}

	private void fillWorkplaceInfo() {
		workplace.workplaceDescription.setValue(workplaceDraftObject.getWorkplaceDescription());
		
		if(!workplaceDraftObject.getWorkplaceAddresses().isEmpty()) {
			ListBox wokplaceAddressLB = (ListBox) workplace.workplaceAddressPanel.getWidget(0);
			wokplaceAddressLB.setSelectedIndex(workplaceDraftObject.getWorkplaceAddressIndex());
		}
		
		workplace.workplaceEconomicConcert.setSelectedIndex(workplaceDraftObject.getWorkplaceEconomicConcert());
		
		if(!workplaceDraftObject.getWorkplaceCalendars().isEmpty()) {
			ListBox wokplaceCalendarLB = (ListBox) workplace.workplaceCalendarPanel.getWidget(0);
			wokplaceCalendarLB.setSelectedIndex(workplaceDraftObject.getWorkplaceCalendarIndex());
		}
		
		if(!workplaceDraftObject.getWorkplaceAgreements().isEmpty()) {
			workplace.workpalceAgreement.setSelectedIndex(workplaceDraftObject.getWorkplaceAgreementIndex());
		}
		
		if(!workplaceDraftObject.getWorkplaceActivities().isEmpty()) {
			ListBox wokplaceActivityLB = (ListBox) workplace.workplaceActivityPanel.getWidget(0);
			wokplaceActivityLB.setSelectedIndex(workplaceDraftObject.getWorkplaceActivityIndex());
		}
		
		workplace.workplaceDescription.addKeyUpHandler(e-> {
			
			String value = workplace.workplaceDescription.getValue();
			String saved = workplaceDraftObject.getWorkplaceInfo().getDescription();
			
			if (AonStringUtils.equals(value, saved) || StringUtils.isBlank(value))
				return;
			
			workplaceDraftObject.setWorkplaceDescription(workplace.workplaceDescription.getValue());
			saving();
		});
		
	}
	
	public Label createEmptyListLabel() {
		Label label = new Label();
		
		label.setText("No hay entradas disponibles");
		label.setStyleName("aon-inputText");
		label.addStyleName(workplace.style.warningColor());
		label.getElement().getStyle().setWidth(99.7, Unit.PCT);	
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	public Label createEmptyListLabel(String labelMessage) {
		Label label = new Label();
		
		label.setText(labelMessage);
		label.setStyleName("aon-inputText");
		label.addStyleName(workplace.style.warningColor());
		label.getElement().getStyle().setWidth(99.7, Unit.PCT);	
		label.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
		
		return label;
	}
	
	// ----------------------------------------------- CALLBACK TO SAVE ------------------------------------------------
	
	private void initializeScheduler() {
		saveStatus.setText("");
		
		saveTimer = new Timer() {
			@Override
			public void run() {
				save();
			}
		};		
	}
	
	public WorkplaceDraft setOnSaved(Consumer<WorkplaceInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
	}
	
	private void saving() {
		saveStatus.setText("Guardando...");
		saveTimer.schedule(2500);
	}
	
	private void save() {
		saveStatus.setText("Guardando...");
		workplaceDraftObject.updateWorkplace(
				r -> { 
					saved();
				}, 
				t -> {
					saveStatus.setText("Error, los cambios no se han guardado");
				}
		);
	}
	
	private void saved() {
		saveStatus.setText("Todos los cambios guardados");	
		onSaved.accept(workplaceDraftObject.getWorkplaceInfo());
	}

	protected void onSavedNoop(WorkplaceInfo workplaceInfo) {}
	
}
