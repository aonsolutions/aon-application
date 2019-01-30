package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class WorkplaceDraft extends Composite implements ContextMenuHandler {
	
	private class WorkplaceImplementation extends Workplace{
		
		@Override
		public void onWorkplaceDescriptionChange() {
//			workplaceDraftObject.setWorkplaceDescription(workplaceDescription.getValue());
//			saving();
		}

		@Override
		public void onWorkplaceEconomicConcertChange() {
			workplaceDraftObject.setWorkplaceEconomicConcert(workplaceEconomicConcert.getSelectedIndex() - 1);
			saving();
		}

		@Override
		public void onWorkplaceAgreementChange() {
			Integer agreementId = workplaceDraftObject.getAgreementId(this.workpalceAgreement.getSelectedItemText());
			workplaceDraftObject.setWorkplaceAgreement(agreementId);
			saving();
		}
		
	}
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static EmployeeDraftUiBinder uiBinder = GWT.create(EmployeeDraftUiBinder.class);

	interface EmployeeDraftUiBinder extends UiBinder<Widget, WorkplaceDraft> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hide();
		String paddingEnableDisable();
		String maxWidth();
		String fontDisableStyle();
		String fontEnableStyle();
		String warningColor();
		String maxWidthTextBox();
		String borderNone();
	}
	

	@UiField
	Button newContractButton;
	
	@UiField
	Label saveStatus;
	
	@UiField
	HorizontalPanel enableWorkplacePanel;
	
	@UiField
	Button enableWorkplaceButton;
	
	@UiField
	HorizontalPanel disableWorkplacePanel;
	
	@UiField
	Button disableWorkplaceButton;
	
	@UiField (provided = true)
	Workplace workplace;
	
	@UiField
	Button redoButton;

	@UiField
	Button undoButton;

	@UiField
	Button undoAllButton;
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
	
	public WorkplaceDraft setOnSaved(Consumer<WorkplaceInfo> onSaved) {
		this.onSaved = onSaved;
		return this;
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

	// ------------------------------------------------- UiHandlers ------------------------------------------------------

	@UiHandler("newContractButton")
	void onNewContractClick(ClickEvent event) {
		EmployeeTree.showNewContract();
	}
	
	@UiHandler("enableWorkplaceButton")
	void onEnableWorkplaceClick(ClickEvent event) {
		workplaceDraftObject.setWorkplaceActive(true);
		//save();
	}

	@UiHandler("disableWorkplaceButton")
	void onDisableWorkplaceClick(ClickEvent event) {
		workplaceDraftObject.setWorkplaceActive(false);
		//save();
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
	
	
	private void initializeScheduler() {
		saveStatus.setText("");
		
		saveTimer = new Timer() {

			@Override
			public void run() {
				save();
			}
		};
		
	}

	private void initializeView() {
		enableWorkplaceButton.addStyleName(AON.AON_ICON_DISABLE);
		disableWorkplaceButton.addStyleName(AON.AON_ICON_ENABLE);
		enableWorkplaceButton.addStyleName(style.paddingEnableDisable());
		disableWorkplaceButton.addStyleName(style.paddingEnableDisable());
		if(workplaceDraftObject.getWorkplaceInfo().isActive() == 1){
			enableWorkplacePanel.addStyleName(style.hide());
			disableWorkplacePanel.removeStyleName(style.hide());
			disableWorkplacePanel.addStyleName(style.fontDisableStyle());
		}else{
			enableWorkplacePanel.removeStyleName(style.hide());
			enableWorkplacePanel.addStyleName(style.fontEnableStyle());
			disableWorkplacePanel.addStyleName(style.hide());
		}
		
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
		workplace.workplaceEconomicConcert.clear();
		workplace.workplaceScopePanel.clear();

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
		
		//CONCIERTO ECONOMICO
		workplace.workplaceEconomicConcert.addItem("-");
		workplace.workplaceEconomicConcert.addItem(String.valueOf("\u00C1")+"lava");
		workplace.workplaceEconomicConcert.addItem("Bizkaia");
		workplace.workplaceEconomicConcert.addItem("Gipuzkoa");
		workplace.workplaceEconomicConcert.addItem("Navarra");
		workplace.workplaceEconomicConcert.addItem("Territorio Com"+ String.valueOf("\u00FA") +"n");
		
		//CALENDARIO
		initializeCalendarCell();
		
		// CONVENIO
		workplace.workpalceAgreement.addItem("-");
		List<Agreement> agreements = workplaceDraftObject.getActiveAgreements();
		for (Agreement a : agreements) {
			workplace.workpalceAgreement.addItem(a.getDescription());
		}
		
		//ACTIVIDADES
		initializeActivityCell();

	}

	private void initializeAddressCell() {
		Widget workplaceAddressWidget;
		if(workplaceDraftObject.getWorkplaceAddresses().values().size() == 1){
			Integer addressId = workplaceDraftObject.getWorkplaceAddressId();
			workplaceAddressWidget = new Label((null == addressId) ? "" : workplaceDraftObject.getWorkplaceAddresses().get(addressId));
			workplaceAddressWidget.setStyleName("aon-inputText");
			workplaceAddressWidget.addStyleName(style.maxWidthTextBox());
			workplace.workplaceAddressPanel.add(workplaceAddressWidget);
		}else{
			workplaceAddressWidget = new ListBox();
			for(String address : workplaceDraftObject.getWorkplaceAddresses().values()){
				((ListBox) workplaceAddressWidget).addItem(address);
			}
			workplaceAddressWidget.setStyleName("aon-selectOneMenu");
			workplaceAddressWidget.addStyleName(style.maxWidth());
			((ListBox) workplaceAddressWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String address = ((ListBox) workplaceAddressWidget).getSelectedItemText();
					Integer addressId = -1;
					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceAddresses().entrySet()){
						if(address == entry.getValue()){
							addressId = entry.getKey();
							break;
						}
					}
					workplaceDraftObject.setWorkplaceAddress(addressId);
					saving();
				}
			});
			workplace.workplaceAddressPanel.add(workplaceAddressWidget);
			if(((ListBox) workplaceAddressWidget).getItemCount() != 0){
				((ListBox) workplaceAddressWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceAddressIndex());
			}
		}
	}
	
	private void initializeCalendarCell() {
		Widget workplaceCalendarWidget;
		if(workplaceDraftObject.getWorkplacesCalendars().values().size() == 0){
			workplaceCalendarWidget = new Label("No hay calendarios disponibles");
			workplaceCalendarWidget.setStyleName("aon-inputText");
			workplaceCalendarWidget.addStyleName(style.maxWidthTextBox());
			workplaceCalendarWidget.addStyleName(style.warningColor());
			workplaceCalendarWidget.addStyleName(style.borderNone());
			workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
		}else if(workplaceDraftObject.getWorkplacesCalendars().values().size() == 1){
			Integer calendarId = workplaceDraftObject.getWorkplaceInfo().getCalendarId();
			workplaceCalendarWidget = new Label(workplaceDraftObject.getWorkplacesCalendars().get(calendarId));
			workplaceCalendarWidget.setStyleName("aon-inputText");
			workplaceCalendarWidget.addStyleName(style.maxWidthTextBox());
			workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
		}else{
			workplaceCalendarWidget = new ListBox();
			for(String calendar : workplaceDraftObject.getWorkplacesCalendars().values()){
				if(null != calendar)
					((ListBox) workplaceCalendarWidget).addItem(calendar);
			}
			workplaceCalendarWidget.setStyleName("aon-selectOneMenu");
			workplaceCalendarWidget.addStyleName(style.maxWidth());
			((ListBox) workplaceCalendarWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String calendar = ((ListBox) workplaceCalendarWidget).getSelectedItemText();
					Integer calendarId = -1;
					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplacesCalendars().entrySet()){
						if(calendar.equals(entry.getValue())){
							calendarId = entry.getKey();
							break;
						}
					}
					workplaceDraftObject.setWorkplaceCalendar(calendarId);
					saving();
					//save();
					
				}
			});
			workplace.workplaceCalendarPanel.add(workplaceCalendarWidget);
			if(((ListBox) workplaceCalendarWidget).getItemCount() != 0){
				((ListBox) workplaceCalendarWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceCalendarIndex());
			}
		}	
	}

	private void initializeActivityCell() {
		Widget workplaceActivityWidget;
		if(workplaceDraftObject.getWorkplaceActivities().values().size() == 0){
			workplaceActivityWidget = new Label("No hay actividades disponibles");
			workplaceActivityWidget.setStyleName("aon-inputText");
			workplaceActivityWidget.addStyleName(style.maxWidthTextBox());
			workplaceActivityWidget.addStyleName(style.warningColor());
			workplaceActivityWidget.addStyleName(style.borderNone());
			workplace.workplaceActivityPanel.add(workplaceActivityWidget);
//		}else if(workplaceDraftObject.getWorkplaceActivities().values().size() == 1){
//			Integer activityId = workplaceDraftObject.getWorkplaceInfo().getActivityId();
//			workplaceActivityWidget = new Label(workplaceDraftObject.getWorkplaceActivities().get(activityId));
//			workplaceActivityPanel.add(workplaceActivityWidget);	
		}else{
			workplaceActivityWidget = new ListBox();
			((ListBox) workplaceActivityWidget).addItem("-");
			for(String activity : workplaceDraftObject.getWorkplaceActivities().values()){
				((ListBox) workplaceActivityWidget).addItem(activity);
			}
			workplaceActivityWidget.setStyleName("aon-selectOneMenu");
			workplaceActivityWidget.addStyleName(style.maxWidth());
			((ListBox) workplaceActivityWidget).addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					String activity = ((ListBox) workplaceActivityWidget).getSelectedItemText();
					Integer activityId = -1;
					for(Entry<Integer, String> entry : workplaceDraftObject.getWorkplaceActivities().entrySet()){
						if(activity.equals(entry.getValue())){
							activityId = entry.getKey();
							break;
						}
					}
					workplaceDraftObject.setWorkplaceActivity(activityId);
					saving();
					
				}
			});
			workplace.workplaceActivityPanel.add(workplaceActivityWidget);	
			if(((ListBox) workplaceActivityWidget).getItemCount() != 0){
				if(((ListBox) workplaceActivityWidget).getItemCount() == 2){
					if(null == workplaceDraftObject.getWorkplaceInfo().getActivityId()) {
//						Window.alert("IS NULL");
						((ListBox) workplaceActivityWidget).setSelectedIndex(1);
						DomEvent.fireNativeEvent(Document.get().createChangeEvent(), workplaceActivityWidget);
					}else
						((ListBox) workplaceActivityWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceActivityIndex()+1);
				}else
					((ListBox) workplaceActivityWidget).setSelectedIndex(workplaceDraftObject.getWorkplaceActivityIndex()+1);
			}
		}
		
	}

	private void fillWorkplaceInfo() {
		workplace.workplaceDescription.setValue(workplaceDraftObject.getWorkplaceInfo().getDescription());
		
		if(workplace.workplaceEconomicConcert.getItemCount() != 0){
			workplace.workplaceEconomicConcert.setSelectedIndex(workplaceDraftObject.getWorkplaceEconomicConcert());
		}
		
		String workplaceAgreement = workplaceDraftObject.getWorkplaceAgreementDescription();
		if(workplace.workpalceAgreement.getItemCount() != 0){
			workplace.workpalceAgreement.setSelectedIndex(workplaceDraftObject.getAgreementIndex(workplaceAgreement) + 1);
		}	
		
		workplace.workplaceDescription.addKeyUpHandler(e-> {
			
			String value = workplace.workplaceDescription.getValue();
			String saved = workplaceDraftObject.getWorkplaceInfo().getDescription();
			if ( AonStringUtils.equals(value, saved))
				return;
			
			workplaceDraftObject.setWorkplaceDescription(workplace.workplaceDescription.getValue());
			saving();
		});
		
	}
	
	private void saving() {
		saveStatus.setText("Guardando...");
		saveTimer.schedule(2500);
	}
	
	private void saved() {
		saveStatus.setText("Todos los cambios guardados");	
		onSaved.accept(workplaceDraftObject.getWorkplaceInfo());
	}

	protected void onSavedNoop(WorkplaceInfo workplaceInfo) {
		
	}


	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// TODO Auto-generated method stub
	}
	
}
