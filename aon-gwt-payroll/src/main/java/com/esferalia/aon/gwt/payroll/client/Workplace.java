package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class Workplace extends ScrollPanel {
	
	private HTMLPanel content = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel gridPanel = new HTMLPanel(AonStringUtils.EMPTY);

	// TABLA DATOS CENTRO DE TRABAJO
	
	private AonCustomTextBox workplaceDescription = new AonCustomTextBox("Descripci\u00f3n");
	private HTMLPanel workplaceAddressPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private AonCustomListBox workplaceEconomicConcert = new AonCustomListBox("Concierto Econ\u00f3mico");
	
	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	private HTMLPanel workplaceCalendarPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel workplaceAgreementPanel = new HTMLPanel(AonStringUtils.EMPTY);
	private HTMLPanel workplaceActivityPanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	// ------------------------------------------------ Variables
	
	private List<Agreement> workplacesAgreements;
	private Map<Integer, String> workplaceAddresses;
	private Map<Integer, String> workplaceActivities;

	// ------------------------------------------------ Constructor

	protected Workplace() {
		initializeView();
	}
	
	public void initializeView() {
		clear();
		content.clear();
		content.addStyleName(AON.CSS.aonFlexColumn());
		content.add(messagePanel);
		
		initializeListBox();
		
		initHandlers();
		
		initView();
		
		Scheduler.get().scheduleDeferred(() -> {
			setWidget(content);
		});
	}

	private void initializeListBox() {
		//CONCIERTO ECONOMICO
		workplaceEconomicConcert.clearItems();
		workplaceEconomicConcert.addItem("-", "");
		workplaceEconomicConcert.addItem(String.valueOf("\u00C1")+"lava", "0");
		workplaceEconomicConcert.addItem("Bizkaia", "1");
		workplaceEconomicConcert.addItem("Gipuzkoa", "2");
		workplaceEconomicConcert.addItem("Navarra", "3");
		workplaceEconomicConcert.addItem("Territorio Com\u00FAn", "4");
	}
	
	private void initHandlers() {
		workplaceDescription.addValueChangeHandler(e -> {
			if(AonStringUtils.isNotBlank(workplaceDescription.getValue())) {
				workplaceDescription.removeError();
				hideMessage();
				onWorkplaceDescriptionChange(workplaceDescription.getValue());		
			} else {
				workplaceDescription.addError();
				showError("Descripci\u00F3n Obligatoria");
			}
		});
		
		workplaceEconomicConcert.addChangeHandler(e -> {
			onWorkplaceEconomicConcertChange(AonStringUtils.isBlank(workplaceEconomicConcert.getValue()) ? null : Byte.parseByte(workplaceEconomicConcert.getValue()));
		});
	}
	
	private void initView() {
		gridPanel.clear();
		gridPanel.setStyleName(AON.CSS.aonGridTwoCols());
		gridPanel.getElement().getStyle().setProperty("padding", "0 1rem");
		
		AonCustomCard infoCard = new AonCustomCard("Informaci\u00f3n General");
		
		HTMLPanel tableInfo = createTable();
		
		workplaceEconomicConcert.setWidth("100%");
		
		tableInfo.add(createRow(workplaceDescription, null, null));
		tableInfo.add(createRow(workplaceAddressPanel, workplaceEconomicConcert, null));
		
		infoCard.add(tableInfo);
		gridPanel.add(infoCard);
		
		AonCustomCard otherDataCard = new AonCustomCard("Datos Laborales");
		
		HTMLPanel tableOther = createTable();
		
		tableOther.add(createRow(workplaceCalendarPanel, workplaceActivityPanel, null));
		tableOther.add(createRow(workplaceAgreementPanel, null, null));
		
		otherDataCard.add(tableOther);
		gridPanel.add(otherDataCard);
		
		content.remove(gridPanel);
		content.add(gridPanel);
	}
	
	private HTMLPanel createTable() {
		HTMLPanel table = new HTMLPanel("");
		table.setStyleName(AON.CSS.aonFlexColumn());
		return table;
	}
	
	private HTMLPanel createRow(Widget w1, Widget w2, Widget w3) {
		HTMLPanel panel = new HTMLPanel("");
		panel.setStyleName(AON.CSS.aonItemFlex());
		
		panel.add(w1);
		if(null != w2) panel.add(w2);
		if(null != w3) panel.add(w3);
		
		return panel;
	}
	
	// ------------------------------------------------- AbstractMethods
	
	// TABLA DATOS CENTRO DE TRABAJO
	
	public abstract void onWorkplaceDescriptionChange(String workplacedescription);
	public abstract void onWorkplaceAddressChange(Integer addressId);
	public abstract void onWorkplaceEconomicConcertChange(Byte economicConcert);
	
	// TABLA DATOS CENTRO DE TRABAJO (LABORAL)
	
	public abstract void onWorkplaceAgreementChange(Integer agreementId);
	public abstract void onWorkplaceActivityChange(Integer activityId);
	
	// ------------------------------------------------- Initialize Cells

	public void initializeAddressCell(Map<Integer, String> workplaceAddressesDb) {
		workplaceAddresses = workplaceAddressesDb;
		
		workplaceAddressPanel.clear();
		workplaceAddressPanel.setWidth("100%");
		
		Widget workplaceAddressWidget;
		
		if(workplaceAddresses.size() == 0)
			workplaceAddressWidget = createEmptyLabel("Direcci\u00f3n");
		else{
			AonCustomListBox addressListBox = new AonCustomListBox("Direcci\u00f3n");
			addressListBox.addItem("-", "-1");
			 workplaceAddresses.entrySet().forEach(entry -> addressListBox.addItem(entry.getValue(), entry.getKey().toString()));
			
			addressListBox.addChangeHandler(e -> {
				Integer addressId = Integer.valueOf(addressListBox.getValue());
				if(addressId == -1) {
					addressListBox.addWarning();
					showError("Direcci\u00F3n Obligatoria");
				} else {
					addressListBox.removeWarning();
					hideMessage();
					onWorkplaceAddressChange(addressId);
				}
			});
			
			// If only one activity, selected it and fire event
			if(addressListBox.getListBox().getItemCount() == 2){
				addressListBox.getListBox().setSelectedIndex(1);
				Integer addressId = Integer.valueOf(addressListBox.getValue());
				onWorkplaceAddressChange(addressId);
			}
			
			workplaceAddressWidget = addressListBox;
		}
		
		workplaceAddressPanel.add(workplaceAddressWidget);
	}
	
	public void initializeCalendarCell(String calendarDescription, CalendarDraftObjectData calendarDraftObjectData) {
		Widget calendarWidget;
		workplaceCalendarPanel.clear();
		workplaceCalendarPanel.setWidth("100%");
		
		if(AonStringUtils.isBlank(calendarDescription))
			calendarWidget = createEmptyLabel("Calendario");
		else {
			AonTableButton calendarButton = new AonTableButton("Calendario", AON.CSS.aonIconEditCalendar());
			calendarButton.addClickHandler(e -> EmployeeTree.showWorkplaceCalendar(calendarDraftObjectData));
			
			AonCustomTextBox calendar = new AonCustomTextBox("Calendario");
			calendar.setValue(calendarDescription);
			calendar.setEnable(false);
			//calendar.addButton(calendarButton);
			
			calendarWidget = calendar;
		}
		
		workplaceCalendarPanel.add(calendarWidget);
	}

	public void initializeAgreementCell(List<Agreement> workplacesAgreements) {
		workplaceAgreementPanel.clear();
		workplaceAgreementPanel.setWidth("100%");
		
		Widget workplaceAgreementWidget;
		
		if(workplacesAgreements.isEmpty())
			workplaceAgreementWidget = createEmptyLabel("Convenio");
		else{
			AonCustomSuggestBox agreementSuggestBox = new AonCustomSuggestBox("Convenio");
			
			this.workplacesAgreements = workplacesAgreements;
			
			List<String> agreementDescriptions = new ArrayList<>();
			
			for (Agreement agreement : this.workplacesAgreements)
				agreementDescriptions.add(agreement.getDescription());
			
			MultiWordSuggestOracle orclAgreements = (MultiWordSuggestOracle) agreementSuggestBox.getSuggestBox().getSuggestOracle();
			orclAgreements.addAll(agreementDescriptions);
			orclAgreements.setDefaultSuggestionsFromText(agreementDescriptions);
			agreementSuggestBox.setAutoSelectEnabled(true);
			agreementSuggestBox.getElement().setPropertyString("placeholder", "Escriba el nombre del convenio... (Ctrl + espacio para ver sugerencias)");
			
			agreementSuggestBox.getSuggestBox().getValueBox().addKeyUpHandler(e -> {
				if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
					agreementSuggestBox.setValue(AonStringUtils.EMPTY);
					agreementSuggestBox.showSuggestionList();
				} else if(e.getNativeKeyCode() == KeyCodes.KEY_ESCAPE)
					agreementSuggestBox.hideSuggestionList();
			});
			
			agreementSuggestBox.getSuggestBox().addSelectionHandler(e -> {
				String agreementDescription = agreementSuggestBox.getValue();
				for(Agreement agreement : this.workplacesAgreements)
					if(AonStringUtils.equalsIgnoreCase(agreement.getDescription(), agreementDescription))
						onWorkplaceAgreementChange(agreement.getId());		
			});
			
			agreementSuggestBox.getSuggestBox().addValueChangeHandler(e -> {
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
		
		workplaceActivityPanel.clear();
		workplaceActivityPanel.setWidth("100%");
		
		this.workplaceActivities = workplaceActivities;
		
		if(workplaceActivities.size() == 0)
			workplaceActivityWidget = createEmptyLabel("Actividad");
		else{
			AonCustomListBox activityListBox = new AonCustomListBox("Actividad");
			activityListBox.addItem("-", "-1");
			this.workplaceActivities.entrySet().forEach(entry -> activityListBox.addItem(entry.getValue(), entry.getKey().toString()));
			
			activityListBox.addChangeHandler(e -> {
				Integer activityId = Integer.valueOf(activityListBox.getValue());
				onWorkplaceActivityChange(activityId);
			}); 
			
			// If only one activity, selected it and fire event
			if(activityListBox.getListBox().getItemCount() == 2){
				activityListBox.getListBox().setSelectedIndex(1);
				Integer activityId = Integer.valueOf(activityListBox.getValue());
				onWorkplaceActivityChange(activityId);
			}
			
			workplaceActivityWidget = activityListBox;
		}
		
		workplaceActivityPanel.add(workplaceActivityWidget);
	}
	
	// ------------------------------------------------- Auxiliar Methods
	
	private AonCustomTextBox createEmptyLabel(String title) {
		AonCustomTextBox text = new AonCustomTextBox(title);
		text.setEnable(false);
		text.setValue("No hay entradas disponibles");
		text.addWarning();
		
		text.setWidth("11rem");
		
		return text;
	}
	
	public void showError(String message) {
		AonMessagePanel.showError(messagePanel, message);
	}
	
	public void showSucces(String message) {
		AonMessagePanel.showSuccess(messagePanel, message);
	}
	
	private void hideMessage() {
		AonMessagePanel.hideMessage(messagePanel);
	}
	
	public void hideCalendarPanel() {
		workplaceCalendarPanel.setVisible(false);
	}

	public void fillWorkplace(WorkplaceInfo workplaceInfo) {
		workplaceDescription.setValue(workplaceInfo.getDescription());
		if(!workplaceAddresses.isEmpty()) 
			((AonCustomListBox) workplaceAddressPanel.getWidget(0)).setValue(null == workplaceInfo.getAddressId() ? null : workplaceInfo.getAddressId().toString());
		workplaceEconomicConcert.setValue(String.valueOf(workplaceInfo.getEconomicConcert()));
		if(!workplacesAgreements.isEmpty()) 
			((AonCustomSuggestBox) workplaceAgreementPanel.getWidget(0)).setValue(getAgreementDescription(workplaceInfo));
		if(!workplaceActivities.isEmpty())
			((AonCustomListBox) workplaceActivityPanel.getWidget(0)).setValue(null == workplaceInfo.getActivityId() ? null : workplaceInfo.getActivityId().toString());
	}
	
	private String getWorkplaceAgreement(WorkplaceInfo workplaceInfo){
		Integer agreeementId = workplaceInfo.getAgreementId();
		return null == agreeementId ? null : agreeementId.toString();
	}
	
	private String getAgreementDescription(WorkplaceInfo workplaceInfo) {
		String workplaceAgreementId = getWorkplaceAgreement(workplaceInfo);
		if(AonStringUtils.isBlank(workplaceAgreementId))
			return null;
		else {
			Integer agreementId = Integer.parseInt(workplaceAgreementId);
			for(Agreement agreement : workplacesAgreements)
				if(agreement.getId().equals(agreementId))
					return agreement.getDescription();
		}
		return null;
	}

	public AonCustomTextBox getWorkplaceDescription() {
		return workplaceDescription;
	}

	public HTMLPanel getWorkplaceAddressPanel() {
		return workplaceAddressPanel;
	}

}
