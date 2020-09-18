package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges.AFIChange;
import com.esferalia.aon.gwt.payroll.shared.ContractType;
import com.esferalia.aon.gwt.payroll.shared.ContractType.ContractTypeRecord;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeeAFIDialog extends CustomDialog {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, EmployeeAFIDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hidden();
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
	CheckBox startContractCkBox;
	
	@UiField
	Label endContractLabel;
	
	@UiField
	CheckBox endContractCkBox;
	
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
	CheckBox generationAFICkBox;
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
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
	
	private ArrayList<Date> dateList;
	private AFIChanges afiChangesMap;

	// -------------------------------------------------------------------------------------------
	// ----------------------------------- CONSTRUCTOR -------------------------------------------
	// -------------------------------------------------------------------------------------------
	
	public EmployeeAFIDialog(Date startDate, Date endDate, String tc2, String quoteGroup, String ocupation, Date payrollDate, Integer contractId, Integer domainId, Integer workplaceId) {
		setCaption("Datos AFI");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractType = new ContractType();
		this.payrollDate = payrollDate;
		
		tc2Original = tc2;
		quoteGroupOriginal = quoteGroup;
		ocupationOriginal = ocupation;
		
		this.contractId = contractId;
		this.domainId = domainId;
		this.workplaceId = workplaceId;
		
		checkStartEndContractAFI(startDate, endDate);	
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		impl.getEmployeeAFIChanges(contractId, new AsyncCallback<AFIChanges>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(AFIChanges result) {
				afiChangesMap = result;
				dateList = new ArrayList<Date>();
				dateList.addAll(afiChangesMap.getAFIChanges().keySet());
				
				initView();
				
				acceptButton.setEnabled(true);
				generationAFICkBox.setEnabled(true);
			}
		});
		
		//EnsureDebugID para TEST
		this.acceptButton.ensureDebugId("input_accept");
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
			this.startContractCkBox.addStyleName(style.hidden());
			this.startContractLabel.addStyleName(style.hidden());
		}else if( (currentDate.before(startDate) || currentDate.equals(startDate)) &&
			(currentDate.after(currentStartDateM60) || currentDate.equals(currentStartDateM60)) ) {
			
			this.startContractCkBox.removeStyleName(style.hidden());
			this.startContractLabel.removeStyleName(style.hidden());
			
		}else {
			this.startContractCkBox.addStyleName(style.hidden());
			this.startContractLabel.addStyleName(style.hidden());
		}
		
		
		if(null == endDate) {
			this.endContractCkBox.addStyleName(style.hidden());
			this.endContractLabel.addStyleName(style.hidden());
		} else if( (currentDate.before(currentEndDateP3) || currentDate.equals(currentEndDateP3)) &&
			(currentDate.after(currentEndDateM60) || currentDate.equals(currentEndDateM60)) ) {
			
			this.endContractCkBox.removeStyleName(style.hidden());
			this.endContractLabel.removeStyleName(style.hidden());
			
		}else {
			this.endContractCkBox.addStyleName(style.hidden());
			this.endContractLabel.addStyleName(style.hidden());
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
	}
	
	private void initTabs() {
		if(dateList.size() == 0) {
			resetListBox();
		}else {
			for(int i=0; i < dateList.size(); i++){
				HorizontalPanel hPanel = new HorizontalPanel();
				
				ToggleButton button = new ToggleButton(StringUtils.leftPad(dateList.get(i).getDate()+"", 2, '0')+
														"/"+StringUtils.leftPad((dateList.get(i).getMonth()+1)+"", 2, '0')+
														"/"+(dateList.get(i).getYear()+1900));
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
							hPanel.getWidget(1).removeStyleName(style.hidden());
							hPanel.getWidget(1).addStyleName(style.tabIconSelected());
							
							//Get selected date
							ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
							String dateStr = toggleButton.getText();
							Integer date = Integer.parseInt(dateStr.split("/")[0]);
							Integer month = Integer.parseInt(dateStr.split("/")[1]) - 1;
							Integer year = Integer.parseInt(dateStr.split("/")[2]) - 1900;
							Date findingDate = new Date(year, month, date);
							DateUtils.resetTime(findingDate);
							
							//Set date and paint data
							newDate.setValue(findingDate);
							if(contractStartDate.equals(findingDate)) {
								hPanel.getWidget(1).addStyleName(style.hidden());;
							}
							initPeculiaritiesTable(findingDate);
							
						}else{
							button.setDown(true);
							return;
						}
					}	
				});
				
				hPanel.add(button);
					
				Button deleteButton = new Button();
				deleteButton.setStyleName("aon-editDataTable-button aon-icon-draft");
				deleteButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AcceptCancelDialog confirm = new AcceptCancelDialog("Eliminar tramo", String.valueOf("\u00BF") + "Deasea eliminar este tramo?") {
							
							@Override
							protected void onAccept() {
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
								Integer date = Integer.parseInt(dateStr.split("/")[0]);
								Integer month = Integer.parseInt(dateStr.split("/")[1]) - 1;
								Integer year = Integer.parseInt(dateStr.split("/")[2]) - 1900;
								Date findingDate = new Date(year, month, date);
								DateUtils.resetTime(findingDate);
								
								//Delete strech and update dates
								afiChangesMap.deleteAFIChangeByDate(findingDate);
								dateList.clear();
								dateList.addAll(afiChangesMap.getAFIChanges().keySet());
								tabsPanel.clear();
								initView();
							}
						};
						
						confirm.center();
						confirm.show();
					}
				});
				
				deleteButton.addStyleName(style.deleteButtonUp());
				if(contractStartDate.equals(dateList.get(i))) {
					deleteButton.addStyleName(style.hidden());;
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
				selectTab(StringUtils.leftPad(dateAux.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((dateAux.getMonth()+1)+"", 2, '0')+"/"+(dateAux.getYear()+1900));
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
			deleteButton.addStyleName(style.hidden());
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
			
			for(AFIChange afiChange : afiChangeList) {
				switch (afiChange.getName()) {
				case "TC2":
					tc2.setSelectedIndex(getContractTC2Idx(afiChange.getValue()));
					continue;
				case "GRUPO_COTIZACION":
					quoteGroup.setSelectedIndex(getContractQuoteGroupIdx(afiChange.getValue()));
					continue;
				case "OCUPACION":
					ocupation.setSelectedIndex(getContractOcupationIdx(afiChange.getValue()));
					continue;
				default:
					continue;
				}
			}
		}
	}

	private void blockListbox() {
		this.tc2.setEnabled(false);
		this.quoteGroup.setEnabled(false);
		this.ocupation.setEnabled(false);
	}
	
	private void unblockListbox() {
		this.tc2.setEnabled(true);
		this.quoteGroup.setEnabled(true);
		this.ocupation.setEnabled(true);
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
				WarningDialog dialog = new WarningDialog("Error fecha", "La fecha seleccionada es anterior a la fecha de inicio de contrato o anterior a la ultima nomina");
				dialog.center();
				dialog.show();
			}
		}
	}

	@SuppressWarnings("deprecation")
	private void addTab(Date date) {
		tabsPanel.clear();
		
		for(int i=0; i < dateList.size(); i++){
			HorizontalPanel hPanel = new HorizontalPanel();
			
			ToggleButton button = new ToggleButton(StringUtils.leftPad(dateList.get(i).getDate()+"", 2, '0')+
													"/"+StringUtils.leftPad((dateList.get(i).getMonth()+1)+"", 2, '0')+
													"/"+(dateList.get(i).getYear()+1900));
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
						hPanel.getWidget(1).removeStyleName(style.hidden());
						hPanel.getWidget(1).addStyleName(style.tabIconSelected());
						
						//Get selected date
						ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
						String dateStr = toggleButton.getText();
						Integer date = Integer.parseInt(dateStr.split("/")[0]);
						Integer month = Integer.parseInt(dateStr.split("/")[1]) - 1;
						Integer year = Integer.parseInt(dateStr.split("/")[2]) - 1900;
						Date findingDate = new Date(year, month, date);
						DateUtils.resetTime(findingDate);
						
						//Set date and paint data
						newDate.setValue(findingDate);
						if(contractStartDate.equals(findingDate)) {
							hPanel.getWidget(1).addStyleName(style.hidden());;
						}
						initPeculiaritiesTable(findingDate);
						
					}else{
						button.setDown(true);
						return;
					}
				}	
			});
			
			hPanel.add(button);
			
			Button deleteButton = new Button();
			deleteButton.setStyleName("aon-editDataTable-button aon-icon-draft");
			deleteButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AcceptCancelDialog confirm = new AcceptCancelDialog("Eliminar tramo", String.valueOf("\u00BF") + "Deasea eliminar este tramo?") {
						
						@Override
						protected void onAccept() {
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
							Integer date = Integer.parseInt(dateStr.split("/")[0]);
							Integer month = Integer.parseInt(dateStr.split("/")[1]) - 1;
							Integer year = Integer.parseInt(dateStr.split("/")[2]) - 1900;
							Date findingDate = new Date(year, month, date);
							DateUtils.resetTime(findingDate);
							
							//Delete strech and update dates
							afiChangesMap.deleteAFIChangeByDate(findingDate);
							dateList.clear();
							dateList.addAll(afiChangesMap.getAFIChanges().keySet());
							tabsPanel.clear();
							initView();
						}
					};
					
					confirm.center();
					confirm.show();
				}
			});
			
			deleteButton.addStyleName(style.deleteButtonUp());
			if(contractStartDate.equals(dateList.get(i))) {
				deleteButton.addStyleName(style.hidden());;
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
				selectTab(StringUtils.leftPad(dateAux.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((dateAux.getMonth()+1)+"", 2, '0')+"/"+(dateAux.getYear()+1900));
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
			hPanel.getWidget(1).removeStyleName(style.hidden());
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
			deleteButton.removeStyleName(style.hidden());
	}

	@UiHandler("startContractCkBox")
	void onStartDateClick(ValueChangeEvent<Boolean> event) {
		if(hasChange()) {
//			acceptButton.setEnabled(true);
			generationAFICkBox.setEnabled(true);
			generationAFICkBox.setChecked(true);
			this.newDate.setValue(contractStartDate, true);
		}else {
//			acceptButton.setEnabled(false);
			generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
			this.newDate.setValue(null, true);
		}
	}
	
	@UiHandler("endContractCkBox")
	void onEndDateClick(ValueChangeEvent<Boolean> event) {
		if(hasChange()) {
			generationAFICkBox.setEnabled(true);
			generationAFICkBox.setChecked(true);
		}else {
			generationAFICkBox.setEnabled(false);
			generationAFICkBox.setChecked(false);
		}
	}
	
	@UiHandler("acceptButton")
	void onAcceptClick(ClickEvent event) {
		onAccept();
		hide();
		onAcceptCb();
	}
	
	protected abstract void onAcceptCb();

	@UiHandler("tc2")
	void onTC2Change(ChangeEvent event) {
		if(tc2.getSelectedIndex() == 0)
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "TC2", null);
		else
			afiChangesMap.addAFIChangeByDate(newDate.getValue(), "TC2", tc2.getSelectedItemText().split(" -")[0]);
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
	
	private void initListBox() {
		this.tc2.clear();
		this.quoteGroup.clear();
		this.ocupation.clear();
		
		// TC2
		this.tc2.addItem("-");
		for (Entry<Integer, ContractTypeRecord> entry : contractType.getContractTypes().entrySet())
			this.tc2.addItem(entry.getKey() + " - " + entry.getValue().getContractTypeDescription(), entry.getKey().toString());
		
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

		// OCUPACION
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
		return (null == startContractCkBox) ? false : startContractCkBox.isChecked();
	}
	
	public boolean isEndContract() {
		return (null == endContractCkBox) ? false : endContractCkBox.isChecked();
	}
	
	public boolean isChangeContract() {
		return afiChangesMap.hasChange("TC2");
	}
	
	public boolean isQuoteContract() {
		return afiChangesMap.hasChange("GRUPO_COTIZACION");
	}
	
	public boolean isOcupationContract() {
		return afiChangesMap.hasChange("OCUPACION");
	}
	
	public boolean isGenerationAFI() {
		return generationAFICkBox.isChecked();
	}
	
	public Date getNewDate() {
		return this.newDate.getValue();
	}
	
	public Integer getTC2Idx() {
		return this.tc2.getSelectedIndex();
	}
	
	public Integer getQuoteGroupdx() {
		return this.quoteGroup.getSelectedIndex();
	}
	
	public Integer getOcupationIdx() {
		return this.ocupation.getSelectedIndex();
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
		
		return false;
	}
	
	public String getOcupationByIndex(Integer index) {
		if(null == index)
			return null;
		
		switch (index) {
		case 1:
			return "\"a\"";
		case 2:
			return "\"b\"";
		case 3:
			return "\"d\"";
		case 4:
			return "\"e\"";
		case 5:
			return "\"f\"";
		case 6:
			return "\"g\"";
		case 7:
			return "\"h\"";
		default:
			return null;
		}
	}
	
	public Integer getContractQuoteGroupIdx(String quoteGroup) {
		
		if(null == quoteGroup) return 0;
		
		if(quoteGroup.contains("\"")) {
			quoteGroup = quoteGroup.split("\"")[1];
		}
		
		return Integer.parseInt(quoteGroup);
	}
	
	public Integer getContractOcupationIdx(String ocupation) {

		if(null == ocupation) return 0;
		
		if(ocupation.contains("\"")) {
			ocupation = ocupation.split("\"")[1];
		}
		
		return getCharIndex(ocupation);
	}
	
	private int getContractTC2Idx(String tc2){
		
		if(null == tc2) return 0;
		
		if(tc2.contains("\"")) {
			tc2 = tc2.split("\"")[1];
		}
		
		return  contractType.getContractTypeIndex(Integer.parseInt(tc2)) + 1;
	}
	
	public int getCharIndex(String ocupation) {
		switch (ocupation) {
		case "a":
			return 1;
		case "b":
			return 2;
		case "d":
			return 3;
		case "e":
			return 4;
		case "f":
			return 5;
		case "g":
			return 6;
		case "h":
			return 7;
		default:
			return 0;
		}
	}
	
	// -------------------------------------------------------------------------------------------
	// ------------------------------------ SAVE INFO --------------------------------------------
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
				        ;
				
				
				Window.open(fileDownloadURL, "_blank", null);
			}
		}, f -> {});
	}
	
	private void saveAFIChanges(Consumer<String> success, Consumer<Throwable> failure) {
			impl.setEmployeeAFIChanges(contractId,	afiChangesMap, new AsyncCallback<String>() {

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

}
