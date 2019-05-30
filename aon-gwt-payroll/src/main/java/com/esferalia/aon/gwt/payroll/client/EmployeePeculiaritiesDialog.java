package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;

import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DateBoxEx;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities.Peculiarity;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public abstract class EmployeePeculiaritiesDialog extends CustomDialog {
	
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

	public EmployeePeculiaritiesDialog() {
		setCaption("Peculiaridades de cotizaci"+String.valueOf("\u00D3")+"n");
		
		setWidget(binder.createAndBindUi(this));
		
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
		
		this.peculiaritiesMap = new Peculiarities();
		this.dateList = new ArrayList<Date>();
		this.dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
		
		if(dateList.size() == 0) {
			
		}else {
			for(int i=0; i < dateList.size(); i++){
				HorizontalPanel hPanel = new HorizontalPanel();
				
				ToggleButton button = new ToggleButton(dateList.get(i).getDate()+"/"+(dateList.get(i).getMonth()+1)+"/"+(dateList.get(i).getYear()+1900));
				button.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						if(button.isDown()){
							putAllToggleButtonsUp();
							button.setDown(true);
							int selectedButton = 0;
							
							for(int i=0; i<tabsPanel.getWidgetCount(); i++){
								HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
								ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
								if(button.equals(toggleButton)){
									break;
								}
								selectedButton++;
							}
							
							HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
							hPanel.addStyleName(style.tabSelected());
							hPanel.getWidget(1).removeStyleName(style.hidden());
							hPanel.getWidget(1).addStyleName(style.tabIconSelected());
							
							ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
							String dateStr = toggleButton.getText();
							Integer date = Integer.parseInt(dateStr.split("/")[0]);
							Integer month = Integer.parseInt(dateStr.split("/")[1]) - 1;
							Integer year = Integer.parseInt(dateStr.split("/")[2]) - 1900;
							Date findingDate = new Date(year, month, date);
							DateUtils.resetTime(findingDate);
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
								int selectedButton = 0;
								for(int i=0; i<tabsPanel.getWidgetCount(); i++){
									HorizontalPanel hPanel = (HorizontalPanel) tabsPanel.getWidget(i);
									Button dButton = (Button) hPanel.getWidget(1);
									if(deleteButton.equals(dButton)){
										break;
									}
									selectedButton++;
								}
								HorizontalPanel hPanel = (HorizontalPanel)tabsPanel.getWidget(selectedButton);
								ToggleButton toggleButton = (ToggleButton) hPanel.getWidget(0);
								String dateStr = toggleButton.getText();
								Integer date = Integer.parseInt(dateStr.split("/")[0]);
								Integer month = Integer.parseInt(dateStr.split("/")[1]) - 1;
								Integer year = Integer.parseInt(dateStr.split("/")[2]) - 1900;
								Date findingDate = new Date(year, month, date);
								DateUtils.resetTime(findingDate);
								peculiaritiesMap.deletePeculiaritiesByDate(findingDate);
								dateList.clear();
								dateList.addAll(peculiaritiesMap.getPeculiarities().keySet());
								initFirstToggleButton();
								start_date_peculiarity.setValue(dateList.get(0));
								initPeculiaritiesTable(dateList.get(0));
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
		initFirstToggleButton();
		this.start_date_peculiarity.setValue(this.dateList.get(0));
		initPeculiaritiesTable(this.dateList.get(0));
	}

	protected abstract void onAccept();

	private void initListBox() {
		peculiarities.clear();
		peculiarities.addItem("MANUAL");
		peculiarities.addItem("CONTRATO TEMPORAL");
		peculiarities.addItem("JUBILACION ACTIVA");
		peculiarities.addItem("COOPERATIVAS");
		peculiarities.addItem("BECARIOS");
		peculiarities.addItem("REGIMEN GENERAL ASIMILADOS");
		peculiarities.addItem("MAYOR 65 A" + String.valueOf("\u00D1") + "OS > 38 A" + String.valueOf("\u00D1") + "OS COTIZADOS");
		peculiarities.addItem("MATERNIDAD/PATERNIDAD/R.EMBARAZO");
		peculiarities.addItem("COBRO DIRECTO IT");
		peculiarities.addItem("CONTRATOS < 6 DIAS");
	}
	
	@SuppressWarnings("deprecation")
	private void initPeculiaritiesTable(Date date) {
		if(this.dateList.size() != 0) {
			ArrayList<Peculiarity> peculiritiesList = peculiaritiesMap.getPeculiaritiesByDate(date);
			for(Peculiarity peculiarity : peculiritiesList) {
				switch (peculiarity.getName()) {
				case "PORCENTAJE_CGC":
					cgcCheck.setValue(peculiarity.isChecked());
					cgcDate.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					cgcValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) cgcValue.setEnabled(true); else cgcValue.setEnabled(false);
					break;
				case "PORCENTAJE_DESMPL":
					desmplCheck.setValue(peculiarity.isChecked());
					desmplDate.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					desmplValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) desmplValue.setEnabled(true); else desmplValue.setEnabled(false);
					break;
				case "PORCENTAJE_FP":
					fpCheck.setValue(peculiarity.isChecked());
					fpDate.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					fpValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) fpValue.setEnabled(true); else fpValue.setEnabled(false);
					break;
				case "PORCENTAJE_CGC_E":
					cgc_eCheck.setValue(peculiarity.isChecked());
					cgc_eDate.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					cgc_eValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) cgc_eValue.setEnabled(true); else cgc_eValue.setEnabled(false);
					break;
				case "PORCENTAJE_IT":
					it_Check.setValue(peculiarity.isChecked());
					it_Date.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					it_Value.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) it_Value.setEnabled(true); else it_Value.setEnabled(false);
					break;
				case "PORCENTAJE_IMS":
					ims_Check.setValue(peculiarity.isChecked());
					ims_Date.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					ims_Value.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) ims_Value.setEnabled(true); else ims_Value.setEnabled(false);
					break;
				case "PORCENTAJE_FOGASA":
					fogasa_Check.setValue(peculiarity.isChecked());
					fogasa_Date.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					fogasa_Value.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) fogasa_Value.setEnabled(true); else fogasa_Value.setEnabled(false);
					break;
				case "PORCENTAJE_FP_E":
					fp_eCheck.setValue(peculiarity.isChecked());
					fp_eDate.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					fp_eValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) fp_eValue.setEnabled(true); else fp_eValue.setEnabled(false);
					break;
				case "PORCENTAJE_DESMPL_E":
					desmpl_eCheck.setValue(peculiarity.isChecked());
					desmpl_eDate.setText(date.getDate()+"/"+(date.getMonth()+1)+"/"+(date.getYear()+1900));
					desmpl_eValue.setValue(peculiarity.getValue());
					if(peculiarity.isChecked()) desmpl_eValue.setEnabled(true); else desmpl_eValue.setEnabled(false);
					break;
				default:
					break;
				}
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
}
