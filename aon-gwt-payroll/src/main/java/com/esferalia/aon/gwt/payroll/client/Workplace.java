package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public abstract class Workplace extends ResizeComposite{
	
	// -------------------------------------------------- UiBinder

	private static WorkplaceUiBinder uiBinder = GWT.create(WorkplaceUiBinder.class);

	interface WorkplaceUiBinder extends UiBinder<Widget, Workplace> {
	}

	// -------------------------------------------------- UiFields

	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String inputPadding();
		String inputLBHeight();
		String inputTextHeight();
		String warningTB();
	}

	// TABLA DATOS CENTRO DE TRABAJO
	
	@UiField
	HTMLPanel workplaceDescriptionPanel;
	
	@UiField
	TextBox workplaceDescription;
	
	@UiField
	HTMLPanel workplaceAddressParentPanel;
	
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
	
	// ------------------------------------------------ Variables
	
	private static final String STYLESELECT = "aon-selectOneMenu";
	private List<Agreement> workplacesAgreements;

	// ------------------------------------------------ Constructor

	protected Workplace() {
		initWidget(uiBinder.createAndBindUi(this));
		initializeView();
	}

	// ------------------------------------------------- UiHandlers
	
	@UiHandler("workplaceDescription")
	void onWorkplaceDescriptionChangeValue(ChangeEvent event) {
		if(AonStringUtils.isNotBlank(workplaceDescription.getValue())) {
			removeWarning(workplaceDescription);
			fireHideMessage();
			onWorkplaceDescriptionChange();		
		} else {
			addWarning(workplaceDescription);
			fireErrorMessage(new HashMap<String, String>(){{ put("Descripci\u00F3n Obligatoria", "Este campo es obligatorio"); }});
		}
	}

	@UiHandler("workplaceEconomicConcert")
	void onWorkplaceEconomicConcertChangeValue(ChangeEvent event) {
		onWorkplaceEconomicConcertChange();
	}
	
	// ------------------------------------------------- AbstractMethods
	
	// TABLA DATOS CENTRO DE TRABAJO
	
	public abstract void onWorkplaceDescriptionChange();
	public abstract void onWorkplaceAddressChange(Integer addressId);
	public abstract void onWorkplaceEconomicConcertChange();
	
	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	public abstract void onWorkplaceAgreementChange(Integer agreementId);
	public abstract void onWorkplaceActivityChange(Integer activityId);
	
	public abstract void fireErrorMessage(Map<String, String> messages);
	public abstract void fireHideMessage();

	// ------------------------------------------------- Initialize View

	public void initializeView() {
		removeWarning(workplaceDescription);
		
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
		this.workplaceEconomicConcert.addItem("Territorio Com\u00FAn", "4");
	}
	
	// ------------------------------------------------- Initialize Cells

	public void initializeAddressCell(Map<Integer, String> workplaceAddresses) {
		Widget workplaceAddressWidget;
		workplaceAddressPanel.clear();
		
		if(workplaceAddresses.size() == 0)
			workplaceAddressWidget = createEmptyLabel();
		else{
			ListBox addressListBox = new ListBox();
			addressListBox.ensureDebugId("address");
			addressListBox.setStyleName(STYLESELECT);
			addressListBox.addStyleName(style.inputLBHeight());
			addressListBox.addStyleName(style.inputPadding());
			addressListBox.getElement().getStyle().setWidth(100.00, Unit.PCT);
			
			addressListBox.addItem("-", "-1");
			
			for(Entry<Integer, String> entry : workplaceAddresses.entrySet())
				addressListBox.addItem(entry.getValue(), entry.getKey().toString());
			
			addressListBox.addChangeHandler(e -> {
				Integer addressId = Integer.valueOf(addressListBox.getSelectedValue());
				if(addressId == -1) {
					addWarning(addressListBox);
					fireErrorMessage(new HashMap<String, String>(){{ put("Direcci\u00F3n Obligatoria", "Este campo es obligatorio"); }});
				} else {
					removeWarning(addressListBox);
					fireHideMessage();
					onWorkplaceAddressChange(addressId);
				}
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
			calendarButton.addClickHandler(e -> EmployeeTree.showWorkplaceCalendar(calendarDraftObjectData));
			
			hPanel.add(calendarLabel);
			hPanel.add(calendarButton);
			calendarWidget = hPanel;
		}
		
		workplaceCalendarPanel.add(calendarWidget);
	}

	public void initializeAgreementCell(List<Agreement> workplacesAgreements) {
		Widget workplaceAgreementWidget;
		workplaceAgreementPanel.clear();
		
		if(workplacesAgreements.isEmpty())
			workplaceAgreementWidget = createEmptyLabel();
		else{
			SuggestBox agreementSuggestBox = new SuggestBox();
			agreementSuggestBox.setStyleName(STYLESELECT);
			agreementSuggestBox.addStyleName(style.inputTextHeight());
			agreementSuggestBox.addStyleName(style.inputPadding());
			agreementSuggestBox.getElement().getStyle().setProperty("width", "calc(100% - 13px)");
			
			this.workplacesAgreements = workplacesAgreements;
			
			List<String> agreementDescriptions = new ArrayList<>();
			
			for (Agreement agreement : this.workplacesAgreements)
				agreementDescriptions.add(agreement.getDescription());
			
			MultiWordSuggestOracle orclAgreements = (MultiWordSuggestOracle) agreementSuggestBox.getSuggestOracle();
			orclAgreements.addAll(agreementDescriptions);
			orclAgreements.setDefaultSuggestionsFromText(agreementDescriptions);
			agreementSuggestBox.setAutoSelectEnabled(true);
			agreementSuggestBox.getElement().setPropertyString("placeholder", "Escriba el nombre del convenio... (Ctrl + espacio para ver sugerencias)");
			
			agreementSuggestBox.getValueBox().addKeyUpHandler(e -> {
				if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
					agreementSuggestBox.setText("");
					agreementSuggestBox.showSuggestionList();
				} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
					agreementSuggestBox.hideSuggestionList();
			});
			
			agreementSuggestBox.addSelectionHandler(e -> {
				String agreementDescription = agreementSuggestBox.getValue();
				for(Agreement agreement : this.workplacesAgreements)
					if(AonStringUtils.equalsIgnoreCase(agreement.getDescription(), agreementDescription))
						onWorkplaceAgreementChange(agreement.getId());		
			});
			
			agreementSuggestBox.addValueChangeHandler(e -> {
				String agreementDescription = agreementSuggestBox.getValue();
				if(AonStringUtils.isBlank(agreementDescription))
					onWorkplaceAgreementChange(null);
			});
			
			workplaceAgreementWidget = agreementSuggestBox;
		}
		
		workplaceAgreementPanel.add(workplaceAgreementWidget);
	}

	public void initializeActivityCell(Map<Integer, String> workplaceActivities) {
		Widget workplaceActivityWidget;
		
		if(workplaceActivities.size() == 0)
			workplaceActivityWidget = createEmptyLabel();
		else{
			ListBox activityListBox = new ListBox();
			activityListBox.setStyleName(STYLESELECT);
			activityListBox.addStyleName(style.inputLBHeight());
			activityListBox.addStyleName(style.inputPadding());
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
	
	// ------------------------------------------------- Auxiliar Methods
	
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
	
	private void addWarning(Widget widget) {
		widget.addStyleName(style.warningTB());
	}
	
	private void removeWarning(Widget widget) {
		widget.removeStyleName(style.warningTB());
	}

}
