package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities.Peculiarity;
import com.esferalia.aon.gwt.payroll.shared.TRL;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeePeculiaritiesDialog extends AonCustomDialog {
	
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
	TableElement peculiaritiesTable;
	
	@UiField
	ListBox peculiarities;
	
	@UiField
	DateBoxEx startDatePeculiarity;
	
	@UiField
	SuggestBox trl;
	
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
	CheckBox cgcECheck;
	
	@UiField
	Label cgcEDate;

	@UiField
	TextBox cgcEValue;
	
	@UiField
	CheckBox itCheck;
	
	@UiField
	Label itDate;

	@UiField
	TextBox itValue;
	
	@UiField
	CheckBox imsCheck;
	
	@UiField
	Label imsDate;

	@UiField
	TextBox imsValue;
	
	@UiField
	CheckBox fogasaCheck;
	
	@UiField
	Label fogasaDate;

	@UiField
	TextBox fogasaValue;
	
	@UiField
	CheckBox fpECheck;
	
	@UiField
	Label fpEDate;

	@UiField
	TextBox fpEValue;
	
	@UiField
	CheckBox desmplECheck;
	
	@UiField
	Label desmplEDate;

	@UiField
	TextBox desmplEValue;
	
	//BUTTONS ACCEPT AND CANCEL
	
	@UiField
	HTMLPanel buttonsPanel;
	
	//BEGIN OF CLASS
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private ArrayList<Date> dateList;
	private Peculiarities peculiaritiesMap;
	private Integer contractId;
	private Date contractStartDate;

	protected EmployeePeculiaritiesDialog(Integer contractId, Date contractStartDate) {
		setCaption("Peculiaridades de cotizaci\u00D3n");
		
		setWidget(binder.createAndBindUi(this));
		
		getButtonsPanel();
		
		this.contractId = contractId;
		this.contractStartDate = contractStartDate;
		
		//Initialize TRL SuggestBox
		Collection<String> trlEntries = TRL.getAllEntriesCollection();
		List<String> contractTRLSuggest = new ArrayList<>();
		trlEntries.forEach(e -> contractTRLSuggest.add(e+""));
		
		MultiWordSuggestOracle orclTRL = (MultiWordSuggestOracle) this.trl.getSuggestOracle();
		orclTRL.addAll(contractTRLSuggest);
		orclTRL.setDefaultSuggestionsFromText(trlEntries);
		this.trl.setAutoSelectEnabled(true);
		
		impl.getEmployeePeculiarities(contractId, new AsyncCallback<Peculiarities>() {

			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do
			}

			@Override
			public void onSuccess(Peculiarities result) {
				peculiaritiesMap = result;
				dateList = new ArrayList<>();
				dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
				
				if(dateList.isEmpty())
					peculiarities.setEnabled(false);
				
				if(peculiaritiesMap.getTrl() == null || AonStringUtils.isBlank(peculiaritiesMap.getTrl())) {
					peculiaritiesTable.getRows().getItem(1).getStyle().setDisplay(Display.NONE);
				} else {
					peculiaritiesTable.getRows().getItem(1).getStyle().clearDisplay();
					trl.setValue(TRL.getEntryByCode(peculiaritiesMap.getTrl()));
					trl.setEnabled(false);
				}
				
				initView();
				showDialog();
			}
		});
		
	}
	
	@UiHandler("peculiarities")
	void onPeculiaritiesChange(ChangeEvent event) {
		Integer selectedPeculiarity = this.peculiarities.getSelectedIndex();
		switch (selectedPeculiarity) {
		case 1: //JUBILACION ACTIVA
			peculiaritiesMap.addPeculiarityJubAct(this.startDatePeculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 2: //COOPERATIVAS
			peculiaritiesMap.addPeculiarityCoop(this.startDatePeculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 3: //BECARIOS
			peculiaritiesMap.addPeculiarityBec(this.startDatePeculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 4: //REGIMEN GENERAL ASIMILADOS
			peculiaritiesMap.addPeculiarityRegGen(this.startDatePeculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 5: //MAYORES 65 > 4 AÑOS COTIZADOS
			peculiaritiesMap.addPeculiarity65Old(this.startDatePeculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		case 6: // MINISTRO DE CULTO
			peculiaritiesMap.addPeculiarityMinCult(this.startDatePeculiarity.getValue());
			tabsPanel.clear();
			initView();
			break;
		default: //MANUAL
			break;
		}
	}
	
	@UiHandler("startDatePeculiarity")
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
					startDatePeculiarity.setValue(null);
				}
			}else {
				this.peculiarities.setEnabled(false);
				this.peculiarities.setSelectedIndex(0);
				this.startDatePeculiarity.setValue(this.contractStartDate, true);
			}
		}else {
			this.peculiarities.setEnabled(false);
			this.peculiarities.setSelectedIndex(0);
		}
	}
	
	@UiHandler("cgcCheck")
	void onCgcCheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(cgcCheck.getValue())) {
			cgcValue.setEnabled(true);
			cgcValue.setValue("");
		}else {
			cgcValue.setEnabled(false);
			cgcValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_CGC");
		}
	}
	
	@UiHandler("cgcValue")
	void onCgcValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_CGC");
		}
	}
	
	@UiHandler("desmplCheck")
	void onDesmplCheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(desmplCheck.getValue())){
			desmplValue.setEnabled(true);
			desmplValue.setValue("");
		}else {
			desmplValue.setEnabled(false);
			desmplValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_DESMPL");
		}
	}
	
	@UiHandler("desmplValue")
	void onDesmplValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_DESMPL");
		}
	}
	
	@UiHandler("fpCheck")
	void onFpCheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(fpCheck.getValue())){
			fpValue.setEnabled(true);
			fpValue.setValue("");
		}else {
			fpValue.setEnabled(false);
			fpValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_FP");
		}
	}
	
	@UiHandler("fpValue")
	void onFpValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_FP");
		}
	}
	
	@UiHandler("cgcECheck")
	void onCgcECheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(cgcECheck.getValue())) {
			cgcEValue.setEnabled(true);
			cgcEValue.setValue("");
		}else {
			cgcEValue.setEnabled(false);
			cgcEValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_CGC_E");
		}
	}
	
	@UiHandler("cgcEValue")
	void onCgcEValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_CGC_E");
		}
	}
	
	@UiHandler("itCheck")
	void onItCheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(itCheck.getValue())) {
			itValue.setEnabled(true);
			itValue.setValue("");
		}else {
			itValue.setEnabled(false);
			itValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_IT");
		}
	}
	
	@UiHandler("itValue")
	void onItValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_IT");
		}
	}
	
	@UiHandler("imsCheck")
	void onImsCheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(imsCheck.getValue())) {
			imsValue.setEnabled(true);
			imsValue.setValue("");
		}else {
			imsValue.setEnabled(false);
			imsValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_IMS");
		}
	}
	
	@UiHandler("imsValue")
	void onImsValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_IMS");
		}
	}
	
	@UiHandler("fogasaCheck")
	void onFogasaCheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(fogasaCheck.getValue())) {
			fogasaValue.setEnabled(true);
			fogasaValue.setValue("");
		}else {
			fogasaValue.setEnabled(false);
			fogasaValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_FOGASA");
		}
	}
	
	@UiHandler("fogasaValue")
	void onFogasaValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_FOGASA");
		}
	}
	
	@UiHandler("fpECheck")
	void onFpECheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(fpECheck.getValue())) {
			fpEValue.setEnabled(true);
			fpEValue.setValue("");
		}else {
			fpEValue.setEnabled(false);
			fpEValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_FP_E");
		}
	}
	
	@UiHandler("fpEValue")
	void onFpEValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_FP_E");
		}
	}
	
	@UiHandler("desmplECheck")
	void onDesmplECheckClick(ClickEvent event) {
		if(Boolean.TRUE.equals(desmplECheck.getValue())) {
			desmplEValue.setEnabled(true);
			desmplEValue.setValue("");
		}else {
			desmplEValue.setEnabled(false);
			desmplEValue.setValue("Sistema");
			this.peculiaritiesMap.updatePecularity(this.startDatePeculiarity.getValue(), "Sistema", "PORCENTAJE_DESMPL_E");
		}
	}
	
	@UiHandler("desmplEValue")
	void onDesmplEValueChange(ValueChangeEvent<String> event) {
		if(event.getValue().length() > 0) {
			Date date = this.startDatePeculiarity.getValue();
			this.peculiaritiesMap.updatePecularity(date, event.getValue(), "PORCENTAJE_DESMPL_E");
		}
	}
	
	
	private void initView() {
		if(dateList.isEmpty()) {
			initializePeculiaritiesTable();
		}else {
			for(int i=0; i < dateList.size(); i++){
				HorizontalPanel hPanel = new HorizontalPanel();
				hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
				
				ToggleButton button = new ToggleButton(formatDate.format(dateList.get(i)));
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
							Date findingDate = formatDate.parse(dateStr);
							DateUtils.resetTime(findingDate);
							
							//Set date and paint data
							startDatePeculiarity.setValue(findingDate);
							initPeculiaritiesTable(findingDate);
							
						} else
							button.setDown(true);
						
					}	
				});
				
				hPanel.add(button);
				
				Button deleteButton = new Button();
				deleteButton.setStyleName("aon-editDataTable-button aon-icon-draft");
				deleteButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AonDialog dialog = new AonDialog("BORRADO", new HTML(String.valueOf("\u00BF") + "Deasea eliminar este tramo?"));
						dialog.confirm(new AonAcceptDialogCallback() {
							
							@Override
							public void onCancel() {}
							
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
								Date findingDate = formatDate.parse(dateStr);
								DateUtils.resetTime(findingDate);
								
								//Delete strech and update dates
								peculiaritiesMap.deletePeculiaritiesByDate(findingDate);
								dateList.clear();
								dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
								tabsPanel.clear();
								initView();
							}
						});
					}
				});
				
				deleteButton.addStyleName(style.deleteButtonUp());
				hPanel.add(deleteButton);
				hPanel.addStyleName(style.marginTab());
				tabsPanel.add(hPanel);
			}
		}
		
		initListBox();
		
		if(!this.dateList.isEmpty()) {
			if(this.dateList.size() == 1 ) {
				initFirstToggleButton();
				this.startDatePeculiarity.setValue(this.dateList.get(0));
				initPeculiaritiesTable(this.dateList.get(0));
			} else {
				this.dateList.sort((o1, o2) -> o1.compareTo(o2));
				Date dateAux = this.dateList.get(dateList.size() - 1);
				this.startDatePeculiarity.setValue(dateAux);
				selectTab(formatDate.format(dateAux));
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
		peculiarities.addItem("PERSONALIZADO");
		peculiarities.addItem("JUBILACION ACTIVA");
		peculiarities.addItem("SOCIOS COOPERATIVISTAS");
		peculiarities.addItem("BECARIOS CURRICULARES");
		peculiarities.addItem("REGIMEN GENERAL ASIMILADOS");
		peculiarities.addItem("MAYOR 65 A\u00D1OS > 38 A\u00D1OS COTIZADOS");
		peculiarities.addItem("MINISTRO DE CULTO");
	}
	
	
	private void initPeculiaritiesTable(Date date) {
		if(!this.dateList.isEmpty()) {
			ArrayList<Peculiarity> peculiritiesList = peculiaritiesMap.getPeculiaritiesByDate(date);
			
			if(!peculiritiesList.isEmpty())
				this.peculiarities.setSelectedIndex(peculiritiesList.get(0).getType());
			
			for(Peculiarity peculiarity : peculiritiesList) {
				if(isCgc(peculiarity.getName())) {
					cgcCheck.setValue(peculiarity.isChecked());
					cgcDate.setText(formatDate.format(date));
					cgcValue.setValue(peculiarity.getValue());
					cgcValue.setEnabled(peculiarity.isChecked());
				} else if(isDesmpl(peculiarity.getName())) {
					desmplCheck.setValue(peculiarity.isChecked());
					desmplDate.setText(formatDate.format(date));
					desmplValue.setValue(peculiarity.getValue());
					desmplValue.setEnabled(peculiarity.isChecked());
				} else if(isFp(peculiarity.getName())) {
					fpCheck.setValue(peculiarity.isChecked());
					fpDate.setText(formatDate.format(date));
					fpValue.setValue(peculiarity.getValue());
					fpValue.setEnabled(peculiarity.isChecked());
				} else if(isCgcE(peculiarity.getName())) {
					cgcECheck.setValue(peculiarity.isChecked());
					cgcEDate.setText(formatDate.format(date));
					cgcEValue.setValue(peculiarity.getValue());
					cgcEValue.setEnabled(peculiarity.isChecked());
				} else if(isIt(peculiarity.getName())) {
					itCheck.setValue(peculiarity.isChecked());
					itDate.setText(formatDate.format(date));
					itValue.setValue(peculiarity.getValue());
					itValue.setEnabled(peculiarity.isChecked());
				} else if(isIms(peculiarity.getName())) {
					imsCheck.setValue(peculiarity.isChecked());
					imsDate.setText(formatDate.format(date));
					imsValue.setValue(peculiarity.getValue());
					imsValue.setEnabled(peculiarity.isChecked());
				} else if(isFogasa(peculiarity.getName())) {
					fogasaCheck.setValue(peculiarity.isChecked());
					fogasaDate.setText(formatDate.format(date));
					fogasaValue.setValue(peculiarity.getValue());
					fogasaValue.setEnabled(peculiarity.isChecked());
					break;
				} else if(isFpE(peculiarity.getName())) {
					fpECheck.setValue(peculiarity.isChecked());
					fpEDate.setText(formatDate.format(date));
					fpEValue.setValue(peculiarity.getValue());
					fpEValue.setEnabled(peculiarity.isChecked());
				} else if(isDesmplE(peculiarity.getName())) {
					desmplECheck.setValue(peculiarity.isChecked());
					desmplEDate.setText(formatDate.format(date));
					desmplEValue.setValue(peculiarity.getValue());
					desmplEValue.setEnabled(peculiarity.isChecked());
				}
			}
		}
	}

	private boolean isCgc(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_CGC") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_CGC");
	}
	
	private boolean isDesmpl(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_DESMPL") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_DESMPL");
	}
	
	private boolean isFp(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_FP") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_FP");
	}
	
	private boolean isCgcE(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_CGC_E") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_CGC_E");
	}
	
	private boolean isIt(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_IT") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_IT");
	}
	
	private boolean isIms(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_IMS") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_IMS");
	}
	
	private boolean isFogasa(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_FOGASA") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_FOGASA");
	}
	
	private boolean isFpE(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_FP_E") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_FP_E");
	}
	
	private boolean isDesmplE(String value) {
		return AonStringUtils.equalsIgnoreCase(value, "PORCENTAJE_DESMPL_E") || AonStringUtils.equalsIgnoreCase(value, "TARIFA_DESMPL_E");
	}

	private void addTab(Date date) {
		tabsPanel.clear();
		
		for(int i=0; i < dateList.size(); i++){
			HorizontalPanel hPanel = new HorizontalPanel();
			hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			
			ToggleButton button = new ToggleButton(formatDate.format(dateList.get(i)));
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
						Date findingDate = formatDate.parse(dateStr);
						DateUtils.resetTime(findingDate);
						
						//Set date and paint data
						startDatePeculiarity.setValue(findingDate);
						initPeculiaritiesTable(findingDate);
						
					} else
						button.setDown(true);
				}	
			});
			
			hPanel.add(button);
			
			Button deleteButton = new Button();
			deleteButton.setStyleName("aon-editDataTable-button aon-icon-draft");
			deleteButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AonDialog dialog = new AonDialog("BORRADO", new HTML(String.valueOf("\u00BF") + "Deasea eliminar este tramo?"));
					dialog.confirm(new AonAcceptDialogCallback() {
						
						@Override
						public void onCancel() {
							//  Close dialog
						}
						
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
							Date findingDate = formatDate.parse(dateStr);
							DateUtils.resetTime(findingDate);
							
							//Delete strech and update dates
							peculiaritiesMap.deletePeculiaritiesByDate(findingDate);
							dateList.clear();
							dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
							tabsPanel.clear();
							initView();
						}
					});
				}
			});
			
			deleteButton.addStyleName(style.deleteButtonUp());
			hPanel.add(deleteButton);
			hPanel.addStyleName(style.marginTab());
			tabsPanel.add(hPanel);
		}
		
		if(!this.dateList.isEmpty()) {
			if(null == this.startDatePeculiarity.getValue() || this.dateList.size() == 1 ) {
				initFirstToggleButton();
				this.startDatePeculiarity.setValue(this.dateList.get(0));
				initPeculiaritiesTable(this.dateList.get(0));
			}else {
				Date dateAux = this.startDatePeculiarity.getValue();
				selectTab(formatDate.format(dateAux));
				initPeculiaritiesTable(dateAux);
			}
			
		}
	}

	private void initializePeculiaritiesTable() {
		this.startDatePeculiarity.setValue(null);
		
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
		
		this.cgcECheck.setEnabled(false);
		this.cgcECheck.setValue(false);
		this.cgcEDate.setText("");
		this.cgcEValue.setText("Sistema");
		this.cgcEValue.setEnabled(false);
	
		this.itCheck.setEnabled(false);
		this.itCheck.setValue(false);
		this.itDate.setText("");
		this.itValue.setText("Sistema");
		this.itValue.setEnabled(false);
		
		this.imsCheck.setEnabled(false);
		this.imsCheck.setValue(false);
		this.imsDate.setText("");
		this.imsValue.setText("Sistema");
		this.imsValue.setEnabled(false);
		
		this.fogasaCheck.setEnabled(false);
		this.fogasaCheck.setValue(false);
		this.fogasaDate.setText("");
		this.fogasaValue.setText("Sistema");
		this.fogasaValue.setEnabled(false);
	
		this.fpECheck.setEnabled(false);
		this.fpECheck.setValue(false);
		this.fpEDate.setText("");
		this.fpEValue.setText("Sistema");
		this.fpEValue.setEnabled(false);
		
		this.desmplECheck.setEnabled(false);
		this.desmplECheck.setValue(false);
		this.desmplEDate.setText("");
		this.desmplEValue.setText("Sistema");
		this.desmplEValue.setEnabled(false);
	
	}
	
	private void activateChecks() {
		this.cgcCheck.setEnabled(true);
		this.desmplCheck.setEnabled(true);
		this.fpCheck.setEnabled(true);
		this.cgcECheck.setEnabled(true);
		this.itCheck.setEnabled(true);
		this.imsCheck.setEnabled(true);
		this.fogasaCheck.setEnabled(true);
		this.fpECheck.setEnabled(true);
		this.desmplECheck.setEnabled(true);	
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
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText( AON.MSG.cancelAction());
		closeBtnDialog.addClickHandler(e -> hide());
		
		closeBtnDialog.getElement().getStyle().setMarginRight(10, Unit.PX);
		
		buttonsPanel.add(closeBtnDialog);
		
		Button acceptBtnDialog = new Button();
		acceptBtnDialog.setStyleName(AON.CSS.aonOkButtonSmall());
		acceptBtnDialog.setText("Grabar");
		acceptBtnDialog.addClickHandler(e -> onAcceptDialog());
		
		buttonsPanel.add(acceptBtnDialog);
	}
	
	private void onAcceptDialog() {
		impl.setEmployeePeculiarities(this.contractId, this.peculiaritiesMap, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// Nothing to do here
			}

			@Override
			public void onSuccess(String result) {
				hide();
				onAccept();
			}
		});	
	}
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}
	
	// Abstract method

	protected abstract void onAccept();

}
