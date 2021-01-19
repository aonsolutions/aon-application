package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges.AFIChange;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeAFIDialog extends AonCustomDialog {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, EmployeeAFIDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String tabSelectedStyle();
		String tabStyle();
		String tabSelected();
		String tabIconSelected();
		String deleteButtonUp();
		String marginTab();
	}
	
	@UiField
	TableElement datesTable;
	
	@UiField
	Label startContractLabel;
	
	@UiField
	Button startContractTB;
	
	@UiField
	Label endContractLabel;
	
	@UiField
	Button endContractTB;
	
	@UiField
	TableElement dataTable;
	
	@UiField
	DateBoxEx newDate;
	
	@UiField
	HorizontalPanel tabsPanel;
	
	@UiField
	ListBox tc2;
	
	@UiField
	ListBox quoteGroup;
	
	@UiField
	ListBox ocupation;
	
	@UiField
	DoubleBox partialityCoef;
	
	@UiField
	Button generationAFITB;
	
	@UiField
	Button notifyMovTB;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private ContractType contractType;
	
	private Date contractStartDate;
	private Integer contractId;
	private Integer domainId;
	private Integer workplaceId;
	private Date payrollDate;
	
	private Date currentDate;
	private Date currentDateP3;
	private Date currentStartDateM60;
	private Date currentEndDateM60;
	private Date currentEndDateP3;
	
	private String tc2Original;
	private String quoteGroupOriginal;
	private String ocupationOriginal;
	private Double partialityCoefOriginal;
	
	private ArrayList<Date> dateList;
	private AFIChanges afiChangesMap;
	
	private Button closeBtnDialog;
	private Button acceptBtnDialog;

	// -------------------------------------------------------------------------------------------
	// ----------------------------------- CONSTRUCTOR -------------------------------------------
	// -------------------------------------------------------------------------------------------
	
	public EmployeeAFIDialog(
			Date startDate, 
			Date endDate, 
			String tc2, 
			String quoteGroup, 
			String ocupation,
			Double partialityCoef,
			Date payrollDate, 
			Integer contractId, 
			Integer domainId, 
			Integer workplaceId) {
		
		setCaption("Datos AFI");
		
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		initToggleButtons();
		
		this.contractType = new ContractType();
		this.payrollDate = payrollDate;
		
		tc2Original = tc2;
		quoteGroupOriginal = quoteGroup;
		ocupationOriginal = ocupation;
		partialityCoefOriginal = partialityCoef;
		
		this.contractId = contractId;
		this.domainId = domainId;
		this.workplaceId = workplaceId;
		
		checkStartEndContractAFI(startDate, endDate);	
		
		impl.getEmployeeAFIChanges(contractId, new AsyncCallback<AFIChanges>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(AFIChanges result) {
				afiChangesMap = result;
				dateList = new ArrayList<Date>();
				dateList.addAll(afiChangesMap.getAFIChanges().keySet());
				
				initView();
				
				acceptBtnDialog.setEnabled(true);
				generationAFITB.setEnabled(true);
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptBtnDialog.ensureDebugId("input_accept");
	}
	
	private void initToggleButtons() {
		getEnableDisableButton(generationAFITB, false);
		getEnableDisableButton(notifyMovTB, false);
		getEnableDisableButton(startContractTB, false);
		getEnableDisableButton(endContractTB, false);
	}

	private void checkStartEndContractAFI(Date startDate, Date endDate) {
		contractStartDate = new Date();
		contractStartDate = DateUtils.copyDateOnly(startDate);
		DateUtils.resetTime(contractStartDate);
		
		currentDate = new Date();
		DateUtils.resetTime(currentDate);
		currentDateP3 = DateUtils.copyDateOnly(currentDate);
		currentDateP3 = DateUtils.addDays2Date(currentDateP3, 3);
		
		if(null != startDate) {
			currentStartDateM60 = DateUtils.copyDateOnly(startDate);
			currentStartDateM60 = DateUtils.addDays2Date(currentStartDateM60, -60);
		}
		
		if(null != endDate) {
			currentEndDateM60 = DateUtils.copyDateOnly(endDate);
			currentEndDateM60 = DateUtils.addDays2Date(currentEndDateM60, -60);
			currentEndDateP3 = DateUtils.copyDateOnly(endDate);
			currentEndDateP3 = DateUtils.addDays2Date(currentEndDateP3, 3);
		}
		
		if(null == startDate) {
			setWidgetVisible(startContractTB, false);
			setWidgetVisible(startContractLabel, false);
		}else if( (currentDate.before(startDate) || currentDate.equals(startDate)) &&
			(currentDate.after(currentStartDateM60) || currentDate.equals(currentStartDateM60)) ) {
			
			setWidgetVisible(startContractTB, true);
			setWidgetVisible(startContractLabel, true);
			
		}else {
			setWidgetVisible(startContractTB, false);
			setWidgetVisible(startContractLabel, false);
		}
		
		
		if(null == endDate) {
			setWidgetVisible(endContractTB, false);
			setWidgetVisible(endContractLabel, false);
		} else if( (currentDate.before(currentEndDateP3) || currentDate.equals(currentEndDateP3)) &&
			(currentDate.after(currentEndDateM60) || currentDate.equals(currentEndDateM60)) ) {
			
			setWidgetVisible(endContractTB, true);
			setWidgetVisible(endContractLabel, true);
			
		}else {
			setWidgetVisible(endContractTB, false);
			setWidgetVisible(endContractLabel, false);
		}
	}

	private void initView() {
		//Init listbox
		initListBox();
		//Init Tabs
		initTabs();
	}
	
	private void resetListBox(){
		//Set default values
		this.tc2.setSelectedIndex(0);
		this.tc2.setEnabled(false);
		this.quoteGroup.setSelectedIndex(0);
		this.quoteGroup.setEnabled(false);
		this.ocupation.setSelectedIndex(0);
		this.ocupation.setEnabled(false);
		this.partialityCoef.setValue(null);
	}
	
	private void initTabs() {
		if(dateList.size() == 0) {
			resetListBox();
		}else {
			for(int i=0; i < dateList.size(); i++){
				HorizontalPanel hPanel = new HorizontalPanel();
				
				ToggleButton button = new ToggleButton(formatFullDate.format(dateList.get(i)));
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(button.isDown()){
							putAllToggleButtonsUp();
							button.setDown(true);
							int selectedButton = 0;
							
							//Find clicked button
							for(int i=0; i<tabsPanel.getWidgetCount(); i++){
								HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
								ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
								if(button.equals(toggleButton)){
									break;
								}
								selectedButton++;
							}
							
							//Add styles to clicked button
							HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
							hPanel.addStyleName(style.tabSelected());
							setWidgetVisible(hPanel.getWidget(1), true);
							hPanel.getWidget(1).addStyleName(style.tabIconSelected());
							
							//Get selected date
							ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
							String dateStr = toggleButton.getText();
							Date findingDate = formatFullDate.parse(dateStr);
							DateUtils.resetTime(findingDate);
							
							//Set date and paint data
							newDate.setValue(findingDate);
							if(contractStartDate.equals(findingDate)) {
								setWidgetVisible(hPanel.getWidget(1), false);
							}
							initPeculiaritiesTable(findingDate);
							
						}else{
							button.setDown(true);
							return;
						}
					}	
				});
				
				hPanel.add(button);
					
				AonTableButton deleteButton = new AonTableButton("Borrar tramo", AON.CSS.aonIconDelete());
				deleteButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AonConfirmDialog confirmDialog = new AonConfirmDialog();
						confirmDialog.confirm(
								"BORRADO", 
								String.valueOf("\u00BF") + "Desea eliminar este tramo?",
								new AonConfirmDialogCallback() {

									@Override
									public void onAccept() {
										//Find clicked button
										int selectedButton = 0;
										for(int i=0; i<tabsPanel.getWidgetCount(); i++){
											HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
											Button dButton = (Button) hPanel.getWidget(1);
											if(deleteButton.equals(dButton)){
												break;
											}
											selectedButton++;
										}
										
										//Get selected date
										HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
										ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
										String dateStr = toggleButton.getText();
										Date findingDate = formatFullDate.parse(dateStr);
										DateUtils.resetTime(findingDate);
										
										//Delete strech and update dates
										afiChangesMap.deleteAFIChangeByDate(findingDate);
										dateList.clear();
										dateList.addAll(afiChangesMap.getAFIChanges().keySet());
										tabsPanel.clear();
										initView();
									}

									@Override
									public void onCancel() {
										// TODO Auto-generated method stub
									}});
					}
				});
				
				deleteButton.addStyleName(style.deleteButtonUp());
				if(contractStartDate.equals(dateList.get(i))) {
					setWidgetVisible(deleteButton, false);
				}
				
				hPanel.add(deleteButton);
				hPanel.addStyleName(style.marginTab());
				tabsPanel.add(hPanel);
				
			}
		}
		
		if(this.dateList.size() > 0) {
			if(null == this.newDate.getValue() || this.dateList.size() == 1 ) {
				this.newDate.setValue(this.dateList.get(0));
				initFirstToggleButton();
				initPeculiaritiesTable(this.dateList.get(0));
			}else {
				Date dateAux = this.newDate.getValue();
				selectTab(formatFullDate.format(dateAux));
				initPeculiaritiesTable(dateAux);
			}
			
		}
	}
	
	private void putAllToggleButtonsUp() {
		for(int i=0; i<tabsPanel.getWidgetCount(); i++){
			HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
			ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
			toggleButton.setDown(false);
			Button deleteButton = (Button) hPanel.getWidget(1);
			deleteButton.removeStyleName(style.tabIconSelected());
			setWidgetVisible(deleteButton, false);
		}	
	}
	
	private void initPeculiaritiesTable(Date date) {
		if(null != payrollDate && date.before(payrollDate)) {
			blockListbox();
		}else {
			unblockListbox();
		}
		
		if(this.dateList.size() != 0) {
			ArrayList<AFIChange> afiChangeList = afiChangesMap.getAFIChangessByDate(date);
			
			tc2.setSelectedIndex(0);
			quoteGroup.setSelectedIndex(0);
			ocupation.setSelectedIndex(0);
			partialityCoef.setValue(null);
			
			Integer contractType = null;	
			
			for(AFIChange afiChange : afiChangeList) {
				switch (afiChange.getName()) {
				case "TC2":
					setSelectedValueLB(tc2, afiChange.getValue());
					contractType = Integer.parseInt(afiChange.getValue());
					continue;
				case "GRUPO_COTIZACION":
					setSelectedValueLB(quoteGroup, afiChange.getValue());
					continue;
				case "OCUPACION":
					setSelectedValueLB(ocupation, afiChange.getValue());
					continue;
				case "COEFICIENTE_PARCIALIDAD":
					partialityCoef.setValue(Double.parseDouble(afiChange.getValue()));
					continue;
				default:
					continue;
				}
			}
			
			if((contractType >= 200 && contractType<300) || (contractType >= 500 && contractType<600)) {
				dataTable.getRows().getItem(9).getStyle().clearDisplay();
				dataTable.getRows().getItem(10).getStyle().clearDisplay();
			} else {
				dataTable.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
				dataTable.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
			}
				
		}
	}

	private void blockListbox() {
		this.tc2.setEnabled(false);
		this.quoteGroup.setEnabled(false);
		this.ocupation.setEnabled(false);
		this.partialityCoef.setEnabled(false);
	}
	
	private void unblockListbox() {
		this.tc2.setEnabled(true);
		this.quoteGroup.setEnabled(true);
		this.ocupation.setEnabled(true);
		this.partialityCoef.setEnabled(true);
	}

	@UiHandler("newDate")
	void onDateChange(ValueChangeEvent<Date> event) {
		if(null != event.getValue()) {
			Date payroll = null;
			if(null == payrollDate)
				payroll = DateUtils.copyDateOnly(contractStartDate);
			else
				payroll = DateUtils.copyDateOnly(payrollDate);
			
			if((event.getValue().after(contractStartDate) || event.getValue().equals(contractStartDate)) && event.getValue().after(payroll)) {
				if(!dateList.contains(event.getValue())) {
					dateList.add(event.getValue());
					afiChangesMap.addAFIChange(event.getValue());
					addTab(event.getValue());
				}else {
					newDate.setValue(null);
				}
			}else {
				AonConfirmDialog dialog = new AonConfirmDialog();
				dialog.info("AVISO: Error fecha", "La fecha seleccionada es anterior a la fecha de inicio de contrato o anterior a la ultima nomina");
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void addTab(Date date) {
		tabsPanel.clear();
		
		for(int i=0; i < dateList.size(); i++){
			HorizontalPanel hPanel = new HorizontalPanel();
			
			ToggleButton button = new ToggleButton(formatFullDate.format(dateList.get(i)));
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(button.isDown()){
						putAllToggleButtonsUp();
						button.setDown(true);
						int selectedButton = 0;
						
						//Find clicked button
						for(int i=0; i<tabsPanel.getWidgetCount(); i++){
							HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
							ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
							if(button.equals(toggleButton)){
								break;
							}
							selectedButton++;
						}
						
						//Add styles to clicked button
						HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
						hPanel.addStyleName(style.tabSelected());
						setWidgetVisible(hPanel.getWidget(1), true);
						hPanel.getWidget(1).addStyleName(style.tabIconSelected());
						
						//Get selected date
						ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
						String dateStr = toggleButton.getText();
						Date findingDate = formatFullDate.parse(dateStr);
						DateUtils.resetTime(findingDate);
						
						//Set date and paint data
						newDate.setValue(findingDate);
						if(contractStartDate.equals(findingDate)) {
							setWidgetVisible(hPanel.getWidget(1), false);
						}
						initPeculiaritiesTable(findingDate);
						
					}else{
						button.setDown(true);
						return;
					}
				}	
			});
			
			hPanel.add(button);
			
			AonTableButton deleteButton = new AonTableButton("Borrar tramo", AON.CSS.aonIconDelete());
			deleteButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AonConfirmDialog confirmDialog = new AonConfirmDialog();
					confirmDialog.confirm(
							"BORRADO", 
							String.valueOf("\u00BF") + "Desea eliminar este tramo?",
							new AonConfirmDialogCallback() {

								@Override
								public void onAccept() {
									//Find clicked button
									int selectedButton = 0;
									for(int i=0; i<tabsPanel.getWidgetCount(); i++){
										HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
										Button dButton = (Button) hPanel.getWidget(1);
										if(deleteButton.equals(dButton)){
											break;
										}
										selectedButton++;
									}
									
									//Get selected date
									HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
									ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
									String dateStr = toggleButton.getText();
									Date findingDate = formatFullDate.parse(dateStr);
									DateUtils.resetTime(findingDate);
									
									//Delete strech and update dates
									afiChangesMap.deleteAFIChangeByDate(findingDate);
									dateList.clear();
									dateList.addAll(afiChangesMap.getAFIChanges().keySet());
									tabsPanel.clear();
									initView();
								}

								@Override
								public void onCancel() {
									// TODO Auto-generated method stub
								}});
				}
			});
			
			deleteButton.addStyleName(style.deleteButtonUp());
			if(contractStartDate.equals(dateList.get(i))) {
				setWidgetVisible(deleteButton, false);
			}
			hPanel.add(deleteButton);
			hPanel.addStyleName(style.marginTab());
			tabsPanel.add(hPanel);
		}
		
		if(this.dateList.size() > 0) {
			if(null == this.newDate.getValue() || this.dateList.size() == 1 ) {
				initFirstToggleButton();
				this.newDate.setValue(this.dateList.get(0));
				initPeculiaritiesTable(this.dateList.get(0));
			}else {
				Date dateAux = this.newDate.getValue();
				selectTab(formatFullDate.format(dateAux));
				initPeculiaritiesTable(dateAux);
			}
			
		}
	}
	
	private void selectTab(String dateStr) {
		putAllToggleButtonsUp();
		int selectedButton = 0;
		
		//Find clicked button
		for(int i=0; i<tabsPanel.getWidgetCount(); i++){
			HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
			ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
			if(toggleButton.getText().equals(dateStr)){
				break;
			}
			selectedButton++;
		}
		
		//Add styles to clicked button
		HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
		hPanel.addStyleName(style.tabSelected());
		if(!contractStartDate.equals(newDate.getValue()))
			setWidgetVisible(hPanel.getWidget(1), true);
		hPanel.getWidget(1).addStyleName(style.tabIconSelected());
		
		//Get selected date
		ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
		toggleButton.setDown(true);
	}
	
	private void initFirstToggleButton(){
		putAllToggleButtonsUp();
		HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(0);
		hPanel.addStyleName(style.tabSelected());
		ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
		toggleButton.setDown(true);
		Button deleteButton = (Button) hPanel.getWidget(1);
		deleteButton.addStyleName(style.tabIconSelected());
		if(!contractStartDate.equals(newDate.getValue())) 
			setWidgetVisible(deleteButton, true);
	}
	
	// -------------------------------------------------------------------------------------------
	// -------------------------------------- UI HANDLER -----------------------------------------
	// -------------------------------------------------------------------------------------------


	@UiHandler("startContractTB")
	void onStartDateClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(startContractTB);
		Boolean value = !oldValue;
		getEnableDisableButton(startContractTB, value);
		
		if(hasChange()) {
			generationAFITB.setEnabled(true);
			getEnableDisableButton(generationAFITB, true);
			this.newDate.setValue(contractStartDate, true);
		}else {
			generationAFITB.setEnabled(false);
			getEnableDisableButton(generationAFITB, false);
			this.newDate.setValue(null, true);
		}
	}
	
	@UiHandler("endContractTB")
	void onEndDateClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(endContractTB);
		Boolean value = !oldValue;
		getEnableDisableButton(endContractTB, value);
		
		if(hasChange()) {
			generationAFITB.setEnabled(true);
			getEnableDisableButton(generationAFITB, true);
		}else {
			generationAFITB.setEnabled(false);
			getEnableDisableButton(generationAFITB, false);
		}
	}
	
	@UiHandler("generationAFITB")
	void onGenerationAFITBClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(generationAFITB);
		Boolean value = !oldValue;
		getEnableDisableButton(generationAFITB, value);
	}
	
	@UiHandler("notifyMovTB")
	void onNotifyMovTBClick(ClickEvent event) {
		Boolean oldValue = isActiveToggleButton(notifyMovTB);
		Boolean value = !oldValue;
		getEnableDisableButton(notifyMovTB, value);
	}
	
	@UiHandler("tc2")
	void onTC2Change(ChangeEvent event) {
		if(tc2.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "TC2", null);
		else {
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "TC2", tc2.getSelectedItemText().split(" -")[0]);
			Integer contractType = Integer.parseInt(tc2.getSelectedValue());
			if((contractType >= 200 && contractType<300) || (contractType >= 500 && contractType<600)) {
				dataTable.getRows().getItem(9).getStyle().clearDisplay();
				dataTable.getRows().getItem(10).getStyle().clearDisplay();
			} else {
				dataTable.getRows().getItem(9).getStyle().setDisplay(Display.NONE);
				dataTable.getRows().getItem(10).getStyle().setDisplay(Display.NONE);
			}
		}
	}
	
	@UiHandler("quoteGroup")
	void onQuoteGroupChange(ChangeEvent event) {
		if(quoteGroup.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "GRUPO_COTIZACION", null);
		else
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "GRUPO_COTIZACION", quoteGroup.getSelectedItemText().split("\\.")[0]);
	}
	
	@UiHandler("ocupation")
	void onOcupationChange(ChangeEvent event) {
		if(ocupation.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "OCUPACION", null);
		else
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "OCUPACION", ocupation.getSelectedItemText().split("\\.")[0]);
	}
	
	@UiHandler("partialityCoef")
	void onPartialityCoefValueChange(ValueChangeEvent<Double> event) {
		Double value = event.getValue();
		afiChangesMap.addAFIChangeByDate(newDate.getValue(), "COEFICIENTE_PARCIALIDAD", null != value ? value.toString() : null);
	}

	// -------------------------------------------------------------------------------------------
	// ------------------------------------ CHANGE METHODS ---------------------------------------
	// -------------------------------------------------------------------------------------------
	
	private void initListBox() {
		// TC2
		this.tc2.clear();
		this.tc2.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.tc2.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), entry.getKey().toString());
		
		// Quote Group
		this.quoteGroup.clear();
		this.quoteGroup.addItem("-", "-1");
		this.quoteGroup.addItem("01. Alta direcci" + String.valueOf("\u00F3") + "n y personal no incluido en el E.T.", "01");
		this.quoteGroup.addItem("02. Ingenieros t" + String.valueOf("\u00E9") + "cnicos, peritos y ayudantes titulados", "02");
		this.quoteGroup.addItem("03. Jefes administrativos y de taller", "03");
		this.quoteGroup.addItem("04. Ayudantes no titulados", "04");
		this.quoteGroup.addItem("05. Oficiales administrativos", "05");
		this.quoteGroup.addItem("06. Subalternos", "06");
		this.quoteGroup.addItem("07. Axiliares administrativos", "07");
		this.quoteGroup.addItem("08. Oficiales de primera y segunda", "08");
		this.quoteGroup.addItem("09. Oficiales de tercera y especialista", "09");
		this.quoteGroup.addItem("10. Peones", "10");
		this.quoteGroup.addItem("11. Trabajadores menos de dieciocho a" + String.valueOf("\u00F1") + "os", "11");

		// Ocupation
		this.ocupation.clear();
		this.ocupation.addItem("-", "-1");
		this.ocupation.addItem("a. Personal en trabajos exclusivos de oficina", "a");
		this.ocupation.addItem("b. Tipo de cotizaci" + String.valueOf("\u00F3") + "n para todos los trabajadores que deban desplazarse habitalmente", "b");
		this.ocupation.addItem("d. Personal de oficios en instalaciones y reparaciones en edificios, obras y trabajos de construcci" + String.valueOf("\u00F3") + "n en general", "d");
		this.ocupation.addItem("e. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de pasajeros en general (taxis, autom" + String.valueOf("\u00F3") + "viles, autobuses, etc)", "e");
		this.ocupation.addItem("f. Conductores de veh" + String.valueOf("\u00ED") + "culo autom" + String.valueOf("\u00F3") + "vil de transporte de mercanc" + String.valueOf("\u00ED") + "as que tengan una capacidad de carga " + String.valueOf("\u00FA") + "til superior a 3,5 Tm." , "f");
		this.ocupation.addItem("g. Personal de limpieza en general. Limpieza de edificios y de todo tipo de establecimientos. Limpieza de calles", "g");
		this.ocupation.addItem("h. Vigilantes, guardas, guardas jurados y personal de seguridad", "h");
	}

	public boolean isStartContract() {
		return isActiveToggleButton(startContractTB);
	}
	
	public boolean isEndContract() {
		return isActiveToggleButton(endContractTB);
	}
	
	public boolean isChangeContract() {
		return afiChangesMap.hasChange("TC2", tc2Original);
	}
	
	public boolean isQuoteContract() {
		return afiChangesMap.hasChange("GRUPO_COTIZACION", quoteGroupOriginal);
	}
	
	public boolean isOcupationContract() {
		return afiChangesMap.hasChange("OCUPACION", ocupationOriginal);
	}
	
	public boolean isPartialityCoefContract() {
		return afiChangesMap.hasChange("COEFICIENTE_PARCIALIDAD", partialityCoefOriginal == null ? "" : partialityCoefOriginal.toString());
	}
	
	public boolean isGenerationAFI() {
		return isActiveToggleButton(generationAFITB);
	}
	
	public Date getNewDate() {
		return this.newDate.getValue();
	}
	
	public boolean hasChange() {
		if(isStartContract())
			return true;
		
		if(isEndContract())
			return true;
		
		if(tc2.getSelectedValue() != tc2Original)
			return true;
		
		if(quoteGroup.getSelectedValue() != quoteGroupOriginal)
			return true;
		
		if(ocupation.getSelectedValue() != ocupationOriginal)
			return true;
		
		if(partialityCoef.getValue() != partialityCoefOriginal)
			return true;
		
		return false;
	}
	
	// -------------------------------------------------------------------------------------------
	// ------------------------------------ PAINT METHODS -----------------------------------------
	// -------------------------------------------------------------------------------------------

	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
	
	private void setWidgetVisible(Widget widget, boolean visible) {
		widget.setVisible(visible);
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
	
	// -------------------------------------------------------------------------------------------
	// ------------------------------------ SAVE METHODS -----------------------------------------
	// -------------------------------------------------------------------------------------------
	
	private void onAccept() {
		saveAFIChanges(s -> {
			if(isGenerationAFI()) {

				String fileDownloadURL = GWT.getModuleBaseURL()+ "employee_afi/"
						+ "?domainId=" + domainId
						+ "&contractId=" + contractId
			            + "&workplaceId=" + workplaceId
			            + "&isStartContract=" + (isStartContract() ? 1 : 0)
			            + "&isEndContract=" + (isEndContract() ? 1 : 0)
			            + "&isChangeContract=" + (isChangeContract() ? 1 : 0)
			            + "&isQuoteContract=" + (isQuoteContract() ? 1 : 0)
			            + "&isOcupationContract=" + (isOcupationContract() ? 1 : 0)
			            + "&isPartialityCoefContract=" + (isPartialityCoefContract() ? 1 : 0)
				        ;
				
				
				Window.open(fileDownloadURL, "_blank", null);
			}
		}, f -> {});
	}
	
	private void saveAFIChanges(Consumer<String> success, Consumer<Throwable> failure) {
			impl.setEmployeeAFIChanges(contractId, afiChangesMap, new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							failure.accept(caught);
						}

						@Override
						public void onSuccess(String result) {
							success.accept(result);
						}
	
			});
	}
	
	// -------------------------------------------------------------------------------------------
	// -------------------------------------- TOOLBAR --------------------------------------------
	// -------------------------------------------------------------------------------------------
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.setAccessKey('C');
		closeBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onCloseDialog(event);
			}
		});
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText( AON.MSG.accept());
		acceptBtnDialog.setAccessKey('A');
		acceptBtnDialog.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAcceptDialog(event);
			}
		});
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	private void onAcceptDialog(ClickEvent event) {
		onAccept();
		hide();
		if(isActiveToggleButton(notifyMovTB)) {
			if(isStartContract()) onStartContract();
			if(isEndContract()) onEndContract();
			if(isChangeContract()) onChangeContract(afiChangesMap.getChangeValue("TC2"), afiChangesMap.getChangeDate());
			if(isQuoteContract()) onQuoteContract(afiChangesMap.getChangeValue("GRUPO_COTIZACION"),  afiChangesMap.getChangeDate());
			if(isOcupationContract()) onOcupationContract(afiChangesMap.getChangeValue("OCUPACION"),  afiChangesMap.getChangeDate());
			if(isPartialityCoefContract()) onPartialityCoefContract(afiChangesMap.getChangeValue("COEFICIENTE_PARCIALIDAD"), afiChangesMap.getChangeDate());
		}
		onAcceptCB();
	}
	
	protected abstract void onAcceptCB();
	protected abstract void onPartialityCoefContract(String partialityCoef, Date date);
	protected abstract void onOcupationContract(String ocupation, Date date);
	protected abstract void onQuoteContract(String quoteGroup, Date date);
	protected abstract void onChangeContract(String contract, Date date);
	protected abstract void onEndContract();
	protected abstract void onStartContract();
}
