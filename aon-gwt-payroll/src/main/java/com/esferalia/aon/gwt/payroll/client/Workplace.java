package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Workplace extends ResizeComposite{
	
	// -------------------------------------------------- UiBinder --------------------------------------------------

	private static WorkplaceUiBinder uiBinder = GWT.create(WorkplaceUiBinder.class);

	interface WorkplaceUiBinder extends UiBinder<Widget, Workplace> {
	}

	// -------------------------------------------------- UiFields --------------------------------------------------

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {}

	// TABLA DATOS CENTRO DE TRABAJO
	
	@UiField
	TextBox workplaceDescription;
	
	@UiField
	HTMLPanel workplaceAddressPanel;
	
	@UiField
	ListBox workplaceEconomicConcert;

	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	@UiField
	HTMLPanel workplaceCalendarHTMLPanel;
	
	@UiField
	HTMLPanel workplaceCalendarPanel;

	@UiField
	HTMLPanel workplaceAgreementPanel;

	@UiField
	HTMLPanel workplaceActivityPanel;

	// ------------------------------------------------ CONSTRUCTOR ------------------------------------------------------

	public Workplace() {
		// Inicializamos la vista del empleado
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}

	// ------------------------------------------------- UiHandlers ------------------------------------------------------
	
	@UiHandler("workplaceDescription")
	void onWorkplaceDescriptionChangeValue(ChangeEvent event) {
		onWorkplaceDescriptionChange();
	}

	@UiHandler("workplaceEconomicConcert")
	void onWorkplaceEconomicConcertChangeValue(ChangeEvent event) {
		onWorkplaceEconomicConcertChange();
	}
	
	// ------------------------------------------------------------------------
	//							Abstraact Methods
	// ------------------------------------------------------------------------
	
	// TABLA DATOS CENTRO DE TRABAJO
	
	public abstract void onWorkplaceDescriptionChange();
	public abstract void onWorkplaceAddressChange(Integer addressId);
	public abstract void onWorkplaceEconomicConcertChange();
	
	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	public abstract void onWorkplaceAgreementChange(Integer agreementId);
	public abstract void onWorkplaceActivityChange(Integer activityId);

	// ------------------------------------------------------ METODOS DE LA CLASE --------------------------------------------------

	public void initializeView() {
		resetElements();
		initializeListBox();
	}

	private void resetElements() {
		// Clear general elements
		this.workplaceDescription.setValue("");
		this.workplaceAddressPanel.clear();
		this.workplaceEconomicConcert.clear();

		// Clear payroll elements
		this.workplaceCalendarPanel.clear();
		this.workplaceAgreementPanel.clear();
		this.workplaceActivityPanel.clear();	
	}

	private void initializeListBox() {
		//CONCIERTO ECONOMICO
		this.workplaceEconomicConcert.addItem("-", "-1");
		this.workplaceEconomicConcert.addItem(String.valueOf("\u00C1")+"lava", "0");
		this.workplaceEconomicConcert.addItem("Bizkaia", "1");
		this.workplaceEconomicConcert.addItem("Gipuzkoa", "2");
		this.workplaceEconomicConcert.addItem("Navarra", "3");
		this.workplaceEconomicConcert.addItem("Territorio Com"+ String.valueOf("\u00FA") +"n", "4");
	}

	public void initializeAddressCell(Map<Integer, String> workplaceAddresses) {
		Widget workplaceAddressWidget;
		workplaceAddressPanel.clear();
		
		if(workplaceAddresses.size() == 0)
			workplaceAddressWidget = createEmptyLabel();
		else{
			ListBox addressListBox = new ListBox();
			addressListBox.setStyleName("aon-selectOneMenu");
			addressListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			addressListBox.addItem("-", "-1");
			
			for(Entry<Integer, String> entry : workplaceAddresses.entrySet())
				addressListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			addressListBox.addChangeHandler(e -> {
				Integer addressId = Integer.valueOf(addressListBox.getSelectedValue());
				onWorkplaceAddressChange(addressId);
			});
			
			// If only one activity, selected it and fire event
			if(addressListBox.getItemCount() == 2){
				addressListBox.setSelectedIndex(1);
				Integer addressId = Integer.valueOf(addressListBox.getSelectedValue());
				onWorkplaceAddressChange(addressId);
			}
			
			workplaceAddressWidget = addressListBox;
		}
		
		workplaceAddressPanel.add(workplaceAddressWidget);
	}
	
	public void initializeCalendarCell(String calendarDescription, CalendarDraftObjectData calendarDraftObjectData) {
		Widget calendarWidget;
		
		if(AonStringUtils.isBlank(calendarDescription))
			calendarWidget = createEmptyLabel();
		else {
			HTMLPanel hPanel = new HTMLPanel("");
			hPanel.getElement().getStyle().setDisplay(Display.FLEX);
			
			Label calendarLabel = new Label(calendarDescription);
			calendarLabel.getElement().getStyle().setMarginRight(5.00, Unit.PX);
			
			Button calendarButton = new Button();
			calendarButton.setStyleName("aon-editDataTable-button aon-icon-calendar");
			calendarButton.addClickHandler(e -> {
				EmployeeTree.showWorkplaceCalendar(calendarDraftObjectData);
			});
			
			hPanel.add(calendarLabel);
			hPanel.add(calendarButton);
			calendarWidget = hPanel;
		}
		
		workplaceCalendarPanel.add(calendarWidget);
	}

	public void initializeAgreementCell(List<Agreement> workplacesAgreements) {
		Widget workplaceAgreementWidget;
		workplaceAgreementPanel.clear();
		
		if(workplacesAgreements.size() == 0)
			workplaceAgreementWidget = createEmptyLabel();
		else{
			ListBox agreementListBox = new ListBox();
			agreementListBox.setStyleName("aon-selectOneMenu");
			agreementListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			agreementListBox.addItem("-", "-1");
			
			for(Agreement agreement : workplacesAgreements)
				agreementListBox.addItem(agreement.getDescription(), agreement.getId().toString());
			
			agreementListBox.addChangeHandler(e -> {
				Integer agreementId = Integer.valueOf(agreementListBox.getSelectedValue());
				onWorkplaceAgreementChange(agreementId);
			});
			
			// If only one activity, selected it and fire event
			if(agreementListBox.getItemCount() == 2){
				agreementListBox.setSelectedIndex(1);
				Integer agreementId = Integer.valueOf(agreementListBox.getSelectedValue());
				onWorkplaceAgreementChange(agreementId);
			}
			
			workplaceAgreementWidget = agreementListBox;
		}
		
		workplaceAgreementPanel.add(workplaceAgreementWidget);
	}

	public void initializeActivityCell(Map<Integer, String> workplaceActivities) {
		Widget workplaceActivityWidget;
		
		if(workplaceActivities.size() == 0)
			workplaceActivityWidget = createEmptyLabel();
		else{
			ListBox activityListBox = new ListBox();
			activityListBox.setStyleName("aon-selectOneMenu");
			activityListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			activityListBox.addItem("-", "-1");
			
			for(Entry<Integer, String> entry : workplaceActivities.entrySet())
				activityListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			activityListBox.addChangeHandler(e -> {
				Integer activityId = Integer.valueOf(activityListBox.getSelectedValue());
				onWorkplaceActivityChange(activityId);
			}); 
			
			// If only one activity, selected it and fire event
			if(activityListBox.getItemCount() == 2){
				activityListBox.setSelectedIndex(1);
				Integer activityId = Integer.valueOf(activityListBox.getSelectedValue());
				onWorkplaceActivityChange(activityId);
			}
			
			workplaceActivityWidget = activityListBox;
		}
		
		workplaceActivityPanel.add(workplaceActivityWidget);
	}
	
	private Label createEmptyLabel() {
		Label label = new Label();
		
		label.setText("No hay entradas disponibles");
		label.getElement().getStyle().setColor("red");
		label.getElement().getStyle().setFontWeight(FontWeight.BOLD);
		
		return label;
	}
	
	public void hideCalendarPanel() {
		workplaceCalendarHTMLPanel.setVisible(false);
	}

}
