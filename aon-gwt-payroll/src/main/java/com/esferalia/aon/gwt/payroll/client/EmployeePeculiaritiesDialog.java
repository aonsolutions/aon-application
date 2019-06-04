package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities.Peculiarity;
import com.esferalia.aon.gwt.payroll.shared.StringUtils;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class EmployeePeculiaritiesDialog extends CustomDialog {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, EmployeePeculiaritiesDialog> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String hidden();
		String deleteButtonUp();
		String tabSelected();
		String tabIconSelected();
		String marginTab();
	}
	
	@UiField
	ListBox peculiarities;
	
	@UiField
	DateBoxEx start_date_peculiarity;
	
	@UiField
	HorizontalPanel tabsPanel;
	
	//EMPLOYEE TABLE
	
	@UiField
	CheckBox cgcCheck;
	
	@UiField
	Label cgcDate;

	@UiField
	TextBox cgcValue;
	
	@UiField
	CheckBox desmplCheck;
	
	@UiField
	Label desmplDate;

	@UiField
	TextBox desmplValue;
	
	@UiField
	CheckBox fpCheck;
	
	@UiField
	Label fpDate;

	@UiField
	TextBox fpValue;
	
	//ENTERPRISE TABLE
	
	@UiField
	CheckBox cgc_eCheck;
	
	@UiField
	Label cgc_eDate;

	@UiField
	TextBox cgc_eValue;
	
	@UiField
	CheckBox it_Check;
	
	@UiField
	Label it_Date;

	@UiField
	TextBox it_Value;
	
	@UiField
	CheckBox ims_Check;
	
	@UiField
	Label ims_Date;

	@UiField
	TextBox ims_Value;
	
	@UiField
	CheckBox fogasa_Check;
	
	@UiField
	Label fogasa_Date;

	@UiField
	TextBox fogasa_Value;
	
	@UiField
	CheckBox fp_eCheck;
	
	@UiField
	Label fp_eDate;

	@UiField
	TextBox fp_eValue;
	
	@UiField
	CheckBox desmpl_eCheck;
	
	@UiField
	Label desmpl_eDate;

	@UiField
	TextBox desmpl_eValue;
	
	//BUTTONS ACCEPT AND CANCEL
	
	@UiField
	Button acceptButton;
	
	@UiField
	Button cancelButton;
	
	
	//BEGIN OF CLASS
	
	private ArrayList<Date> dateList;
	private Peculiarities peculiaritiesMap;
	private Integer contractId;
	private Date contractStartDate;

	public EmployeePeculiaritiesDialog(Integer contractId, Date contractStartDate) {
		setCaption("Peculiaridades de cotizaci"+String.valueOf("\u00D3")+"n");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractId = contractId;
		this.contractStartDate = contractStartDate;
		
		acceptButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
				onAccept();
			}
		});		
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		impl.getEmployeePeculiarities(contractId, new AsyncCallback<Peculiarities>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Peculiarities result) {
				peculiaritiesMap = result;
				dateList = new ArrayList<Date>();
				dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
				
				if(dateList.isEmpty())
					peculiarities.setEnabled(false);
				
				initView();
			}
		});
	}
	
	@UiHandler("peculiarities")
	void onPeculiaritiesChange(ChangeEvent event) {
		Integer selectedPeculiarity = this.peculiarities.getSelectedIndex();
		switch (selectedPeculiarity) {
		case 1: //JUBILACION ACTIVA
			peculiaritiesMap.addPeculiarityJubAct(this.start_date_peculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 2: //COOPERATIVAS
			peculiaritiesMap.addPeculiarityCoop(this.start_date_peculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 3: //BECARIOS
			
			break;
		case 4: //REGIMEN GENERAL ASIMILADOS
			peculiaritiesMap.addPeculiarityRegGen(this.start_date_peculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 5: //MAYORES 65 > 4 AÑOS COTIZADOS
			peculiaritiesMap.addPeculiarity65Old(this.start_date_peculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 6: // MINISTRO DE CULTO
			peculiaritiesMap.addPeculiarityMinCult(this.start_date_peculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		default: //MANUAL
			break;
		}
	}
	
	@UiHandler("start_date_peculiarity")
	void onDateChange(ValueChangeEvent<Date> event) {
		if(null != event.getValue()) {
			if(event.getValue().after(contractStartDate) || event.getValue().equals(contractStartDate)) {
				this.peculiarities.setEnabled(true);
				activateChecks();
				if(!dateList.contains(event.getValue())) {
					dateList.add(event.getValue());
					peculiaritiesMap.addPeculiarity(event.getValue());
					addTab(event.getValue());
				}else {
					start_date_peculiarity.setValue(null);
				}
			}else {
				this.peculiarities.setEnabled(false);
				this.peculiarities.setSelectedIndex(0);
				this.start_date_peculiarity.setValue(null);
			}
		}else {
			this.peculiarities.setEnabled(false);
			this.peculiarities.setSelectedIndex(0);
		}
	}
	
	@UiHandler("cgcCheck")
	void onCgcCheckClick(ClickEvent event) {
		if(cgcCheck.getValue()) {
			cgcValue.setEnabled(true);
			cgcValue.setValue("");
		}else {
			cgcValue.setEnabled(false);
			cgcValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_CGC");
		}
	}
	
	@UiHandler("cgcValue")
	void onCgcValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_CGC");
		}
	}
	
	@UiHandler("desmplCheck")
	void onDesmplCheckClick(ClickEvent event) {
		if(desmplCheck.getValue()){
			desmplValue.setEnabled(true);
			desmplValue.setValue("");
		}else {
			desmplValue.setEnabled(false);
			desmplValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_DESMPL");
		}
	}
	
	@UiHandler("desmplValue")
	void onDesmplValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_DESMPL");
		}
	}
	
	@UiHandler("fpCheck")
	void onFpCheckClick(ClickEvent event) {
		if(fpCheck.getValue()){
			fpValue.setEnabled(true);
			fpValue.setValue("");
		}else {
			fpValue.setEnabled(false);
			fpValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_FP");
		}
	}
	
	@UiHandler("fpValue")
	void onFpValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_FP");
		}
	}
	
	@UiHandler("cgc_eCheck")
	void onCgc_eCheckClick(ClickEvent event) {
		if(cgc_eCheck.getValue()) {
			cgc_eValue.setEnabled(true);
			cgc_eValue.setValue("");
		}else {
			cgc_eValue.setEnabled(false);
			cgc_eValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_CGC_E");
		}
	}
	
	@UiHandler("cgc_eValue")
	void onCgc_eValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_CGC_E");
		}
	}
	
	@UiHandler("it_Check")
	void onIt_CheckClick(ClickEvent event) {
		if(it_Check.getValue()) {
			it_Value.setEnabled(true);
			it_Value.setValue("");
		}else {
			it_Value.setEnabled(false);
			it_Value.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_IT");
		}
	}
	
	@UiHandler("it_Value")
	void onIt_ValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_IT");
		}
	}
	
	@UiHandler("ims_Check")
	void onIms_CheckClick(ClickEvent event) {
		if(ims_Check.getValue()) {
			ims_Value.setEnabled(true);
			ims_Value.setValue("");
		}else {
			ims_Value.setEnabled(false);
			ims_Value.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_IMS");
		}
	}
	
	@UiHandler("ims_Value")
	void onIms_ValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_IMS");
		}
	}
	
	@UiHandler("fogasa_Check")
	void onFogasa_CheckClick(ClickEvent event) {
		if(fogasa_Check.getValue()) {
			fogasa_Value.setEnabled(true);
			fogasa_Value.setValue("");
		}else {
			fogasa_Value.setEnabled(false);
			fogasa_Value.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_FOGASA");
		}
	}
	
	@UiHandler("fogasa_Value")
	void onFogasa_ValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_FOGASA");
		}
	}
	
	@UiHandler("fp_eCheck")
	void onFp_eCheckClick(ClickEvent event) {
		if(fp_eCheck.getValue()) {
			fp_eValue.setEnabled(true);
			fp_eValue.setValue("");
		}else {
			fp_eValue.setEnabled(false);
			fp_eValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_FP_E");
		}
	}
	
	@UiHandler("fp_eValue")
	void onFp_eValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_FP_E");
		}
	}
	
	@UiHandler("desmpl_eCheck")
	void onDesmpl_eCheckClick(ClickEvent event) {
		if(desmpl_eCheck.getValue()) {
			desmpl_eValue.setEnabled(true);
			desmpl_eValue.setValue("");
		}else {
			desmpl_eValue.setEnabled(false);
			desmpl_eValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.start_date_peculiarity.getValue(), "Sistema", "PORCENTAJE_DESMPL_E");
		}
	}
	
	@UiHandler("desmpl_eValue")
	void onDesmpl_eValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.start_date_peculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_DESMPL_E");
		}
	}
	

	
	@SuppressWarnings("deprecation")
	private void initView() {
		if(dateList.size() == 0) {
			initializePeculiaritiesTable();
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
							start_date_peculiarity.setValue(findingDate);
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
						AcceptCancelDialog confirm = new AcceptCancelDialog("Eliminar tramo", String.valueOf("\u00BF") + "Deasea eliminar este tramo?", "", "") {
							
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
								peculiaritiesMap.deletePeculiaritiesByDate(findingDate);
								dateList.clear();
								dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
								tabsPanel.clear();
								initView();
							}
						};
						
						confirm.center();
						confirm.show();
					}
				});
				
				deleteButton.addStyleName(style.deleteButtonUp());
				hPanel.add(deleteButton);
				hPanel.addStyleName(style.marginTab());
				tabsPanel.add(hPanel);
			}
		}
		
		initListBox();
		
		if(this.dateList.size() > 0) {
			if(null == this.start_date_peculiarity.getValue() || this.dateList.size() == 1 ) {
				initFirstToggleButton();
				this.start_date_peculiarity.setValue(this.dateList.get(0));
				initPeculiaritiesTable(this.dateList.get(0));
			}else {
				Date dateAux = this.start_date_peculiarity.getValue();
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
	
	private void initFirstToggleButton(){
		putAllToggleButtonsUp();
		HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(0);
		hPanel.addStyleName(style.tabSelected());
		ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
		toggleButton.setDown(true);
		Button deleteButton = (Button) hPanel.getWidget(1);
		deleteButton.addStyleName(style.tabIconSelected());
		deleteButton.removeStyleName(style.hidden());
	}

	private void initListBox() {
		peculiarities.clear();
		peculiarities.addItem("MANUAL");
		peculiarities.addItem("JUBILACION ACTIVA");
		peculiarities.addItem("COOPERATIVAS");
		peculiarities.addItem("BECARIOS");
		peculiarities.addItem("REGIMEN GENERAL ASIMILADOS");
		peculiarities.addItem("MAYOR 65 A" + String.valueOf("\u00D1") + "OS > 38 A" + String.valueOf("\u00D1") + "OS COTIZADOS");
		peculiarities.addItem("MINISTRO DE CULTO");
		
		peculiarities.getElement().getElementsByTagName("option").getItem(3).setAttribute("disabled", "disabled");
	}
	
	@SuppressWarnings("deprecation")
	private void initPeculiaritiesTable(Date date) {
		if(this.dateList.size() != 0) {
			ArrayList<Peculiarity> peculiritiesList = peculiaritiesMap.getPeculiaritiesByDate(date);
			
			if(peculiritiesList.size() > 0)
				this.peculiarities.setSelectedIndex(peculiritiesList.get(0).getType());
			
			for(Peculiarity peculiarity : peculiritiesList) {
				switch (peculiarity.getName()) {
				case "PORCENTAJE_CGC":
					cgcCheck.setValue(peculiarity.isChecked());
					cgcDate.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					cgcValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) cgcValue.setEnabled(true); else cgcValue.setEnabled(false);
					break;
				case "PORCENTAJE_DESMPL":
					desmplCheck.setValue(peculiarity.isChecked());
					desmplDate.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					desmplValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) desmplValue.setEnabled(true); else desmplValue.setEnabled(false);
					break;
				case "PORCENTAJE_FP":
					fpCheck.setValue(peculiarity.isChecked());
					fpDate.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					fpValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) fpValue.setEnabled(true); else fpValue.setEnabled(false);
					break;
				case "PORCENTAJE_CGC_E":
					cgc_eCheck.setValue(peculiarity.isChecked());
					cgc_eDate.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					cgc_eValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) cgc_eValue.setEnabled(true); else cgc_eValue.setEnabled(false);
					break;
				case "PORCENTAJE_IT":
					it_Check.setValue(peculiarity.isChecked());
					it_Date.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					it_Value.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) it_Value.setEnabled(true); else it_Value.setEnabled(false);
					break;
				case "PORCENTAJE_IMS":
					ims_Check.setValue(peculiarity.isChecked());
					ims_Date.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					ims_Value.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) ims_Value.setEnabled(true); else ims_Value.setEnabled(false);
					break;
				case "PORCENTAJE_FOGASA":
					fogasa_Check.setValue(peculiarity.isChecked());
					fogasa_Date.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					fogasa_Value.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) fogasa_Value.setEnabled(true); else fogasa_Value.setEnabled(false);
					break;
				case "PORCENTAJE_FP_E":
					fp_eCheck.setValue(peculiarity.isChecked());
					fp_eDate.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					fp_eValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) fp_eValue.setEnabled(true); else fp_eValue.setEnabled(false);
					break;
				case "PORCENTAJE_DESMPL_E":
					desmpl_eCheck.setValue(peculiarity.isChecked());
					desmpl_eDate.setText(StringUtils.leftPad(date.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((date.getMonth()+1)+"", 2, '0')+"/"+(date.getYear()+1900));
					desmpl_eValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) desmpl_eValue.setEnabled(true); else desmpl_eValue.setEnabled(false);
					break;
				default:
					break;
				}
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
						start_date_peculiarity.setValue(findingDate);
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
					AcceptCancelDialog confirm = new AcceptCancelDialog("Eliminar tramo", String.valueOf("\u00BF") + "Deasea eliminar este tramo?", "", "") {
						
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
							peculiaritiesMap.deletePeculiaritiesByDate(findingDate);
							dateList.clear();
							dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
							tabsPanel.clear();
							initView();
						}
					};
					
					confirm.center();
					confirm.show();
				}
			});
			
			deleteButton.addStyleName(style.deleteButtonUp());
			hPanel.add(deleteButton);
			hPanel.addStyleName(style.marginTab());
			tabsPanel.add(hPanel);
		}
		
		if(this.dateList.size() > 0) {
			if(null == this.start_date_peculiarity.getValue() || this.dateList.size() == 1 ) {
				initFirstToggleButton();
				this.start_date_peculiarity.setValue(this.dateList.get(0));
				initPeculiaritiesTable(this.dateList.get(0));
			}else {
				Date dateAux = this.start_date_peculiarity.getValue();
				selectTab(StringUtils.leftPad(dateAux.getDate()+"", 2, '0')+"/"+StringUtils.leftPad((dateAux.getMonth()+1)+"", 2, '0')+"/"+(dateAux.getYear()+1900));
				initPeculiaritiesTable(dateAux);
			}
			
		}
	}

	private void initializePeculiaritiesTable() {
		this.start_date_peculiarity.setValue(null);
		
		this.cgcCheck.setEnabled(false);
		this.cgcCheck.setValue(false);
		this.cgcDate.setText("");
		this.cgcValue.setText("Sistema");
		this.cgcValue.setEnabled(false);
		
		this.desmplCheck.setEnabled(false);
		this.desmplCheck.setValue(false);
		this.desmplDate.setText("");
		this.desmplValue.setText("Sistema");
		this.desmplValue.setEnabled(false);
		
		this.fpCheck.setEnabled(false);
		this.fpCheck.setValue(false);
		this.fpDate.setText("");
		this.fpValue.setText("Sistema");
		this.fpValue.setEnabled(false);
		
		this.cgc_eCheck.setEnabled(false);
		this.cgc_eCheck.setValue(false);
		this.cgc_eDate.setText("");
		this.cgc_eValue.setText("Sistema");
		this.cgc_eValue.setEnabled(false);
	
		this.it_Check.setEnabled(false);
		this.it_Check.setValue(false);
		this.it_Date.setText("");
		this.it_Value.setText("Sistema");
		this.it_Value.setEnabled(false);
		
		this.ims_Check.setEnabled(false);
		this.ims_Check.setValue(false);
		this.ims_Date.setText("");
		this.ims_Value.setText("Sistema");
		this.ims_Value.setEnabled(false);
		
		this.fogasa_Check.setEnabled(false);
		this.fogasa_Check.setValue(false);
		this.fogasa_Date.setText("");
		this.fogasa_Value.setText("Sistema");
		this.fogasa_Value.setEnabled(false);
	
		this.fp_eCheck.setEnabled(false);
		this.fp_eCheck.setValue(false);
		this.fp_eDate.setText("");
		this.fp_eValue.setText("Sistema");
		this.fp_eValue.setEnabled(false);
		
		this.desmpl_eCheck.setEnabled(false);
		this.desmpl_eCheck.setValue(false);
		this.desmpl_eDate.setText("");
		this.desmpl_eValue.setText("Sistema");
		this.desmpl_eValue.setEnabled(false);
	
	}
	
	private void activateChecks() {
		this.cgcCheck.setEnabled(true);
		this.desmplCheck.setEnabled(true);
		this.fpCheck.setEnabled(true);
		this.cgc_eCheck.setEnabled(true);
		this.it_Check.setEnabled(true);
		this.ims_Check.setEnabled(true);
		this.fogasa_Check.setEnabled(true);
		this.fp_eCheck.setEnabled(true);
		this.desmpl_eCheck.setEnabled(true);	
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
		hPanel.getWidget(1).removeStyleName(style.hidden());
		hPanel.getWidget(1).addStyleName(style.tabIconSelected());
		
		//Get selected date
		ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
		toggleButton.setDown(true);
	}

	private void onAccept() {
		impl.setEmployeePeculiarities(this.contractId, this.peculiaritiesMap, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String result) {
				hide();
			}});	
	}
}
