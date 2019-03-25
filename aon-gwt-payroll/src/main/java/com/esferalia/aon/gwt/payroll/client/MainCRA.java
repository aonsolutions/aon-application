package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainCRA extends MainEntryPoint {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, MainCRA> {
	}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bold();
		String hide();
		String paddingDays();
		String widthDays();
		String widthName();
		String textCenter();
		String widthFirstColumn();
		String paddingText();
	}
	
	@UiField
	Button exportButton;
	
	@UiField
	TableElement dataTable;
	
	@UiField
	Label enterprise;
	
	@UiField
	ListBox cccs;
	
	@UiField
	ListBox monthList;
	
	@UiField
	ListBox yearList;
	
	@UiField
	VerticalPanel activityCCCPanel;
	
	@UiField
	Grid activityCCCTable;
	
	private Enterprise enterpriseInfo = new Enterprise();
	private String enterpriseName = "";
	private Map<String, CCC> enterpriseCCCs = new HashMap<>();
	private Integer cccId = 0;
	private String ccc = "";
	
	private Date startDate = null;
	private Date endDate = null;
	
	// ------------------------------------------------------------------------
	//						On Module Load
	// ------------------------------------------------------------------------
	
	@Override
	public void onModuleLoad() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get("rootPanel").add(ui);
		
		//Add Months
		monthList.addItem("Enero");
		monthList.addItem("Febrero");
		monthList.addItem("Marzo");
		monthList.addItem("Abril");
		monthList.addItem("Mayo");
		monthList.addItem("Junio");
		monthList.addItem("Julio");
		monthList.addItem("Agosto");
		monthList.addItem("Septiembre");
		monthList.addItem("Octubre");
		monthList.addItem("Noviembre");
		monthList.addItem("Diciembre");
		
		//Add Years
		Date currentDate = new Date();
		Integer currentYear = currentDate.getYear();
		Integer currentParseYear = currentYear + 1900;
		Integer previusYear = currentParseYear -1;
		
		yearList.addItem(currentParseYear+"");
		yearList.addItem(previusYear+"");
		
		//Style Data Table
		dataTable.getStyle().setMargin(5, Unit.PX);
		
		//Hide EmployeePanel
		activityCCCPanel.addStyleName(style.hide());
		
		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				if(enterprises.size() == 1){
					enterpriseName = enterprises.get(0).getName();
					enterpriseInfo = enterprises.get(0);
				}
				
				for (Enterprise enterprise: enterprises)
					for(Activity activity : enterprise.getActivities())
						for(CCC ccc : activity.getCccs())
							enterpriseCCCs.put(activity.getDescription(), ccc);
				
				initializeView();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
				
	}
	
	private void initializeView(){
		//Enterprise Name
		enterprise.setText(this.enterpriseName);
		enterprise.addStyleName(style.bold());
		enterprise.addStyleName(style.paddingText());
		
		//Enterprise CCCs
		if(this.enterpriseCCCs.isEmpty()){
			this.cccs.setEnabled(false);
			this.monthList.setEnabled(false);
			this.yearList.setEnabled(false);
			WarningDialog warning = new WarningDialog("Aviso", "No existe ninguna cuenta de cotizaci"+String.valueOf("\u00F3")+"n para esta empresa.");
			warning.center();
			warning.show();
		}else{
			for(Entry<String, CCC> entry : this.enterpriseCCCs.entrySet()){
				this.cccs.addItem(entry.getKey() + " - " + getRegimeName(entry.getValue().getRegime()) + " - " 
						+ entry.getValue().getCode() + " - (" + ProvinceContract.getName(entry.getValue().getGeozone()) +")");
			}
			
			setFindingCCC( this.cccs.getSelectedItemText().split("- ")[2].split(" ")[0] );
		}
		
		//Set current date
		Date currentDate = new Date();
		this.monthList.setSelectedIndex(currentDate.getMonth());

	}
	

	// ------------------------------------------------------------------------
	//							UiHandler Generate CRA
	// ------------------------------------------------------------------------
	
	@UiHandler("exportButton")
	void exportButton(ClickEvent event){
		if(checkDate()){
//			Window.alert("GENERANDO FICHERO AFI");
//			Window.alert("INFORMACION AFI => ENTERPRISE: " + enterpriseName + ", CCC Id : " + this.cccId + ", CCC : " + this.ccc + 
//					", CCC Start Date : " + this.startDate + ", CCC End Date : " +this.endDate);
			
			String fileDownloadURL = GWT.getModuleBaseURL()+ "/main_cra/"
		            + "?domainId=" + enterpriseInfo.getDomain()
		            + "&enterpriseId=" + enterpriseInfo.getId()
		            + "&enterpriseName=" + enterpriseName
			        + "&startDate=" + startDate.getTime()
			        + "&endDate=" + endDate.getTime()
			        + "&ccc=" + ccc
			        + "&cccId=" + cccId
			        ;
			
//			Window.alert(fileDownloadURL);
			
			Window.open(fileDownloadURL, "_blank", null);
		
		}else{
			WarningDialog warning = new WarningDialog("Aviso", "No se puede generar el CRA para un mes posterior o igual al actual.");
			warning.center();
			warning.show();
		}
		
	}
	
	@UiHandler("cccs")
	void changeCCCList(ChangeEvent event){
		setFindingCCC( this.cccs.getSelectedItemText().split("- ")[2].split(" ")[0] );
	}
	
	@UiHandler("monthList")
	void changeMonthList(ChangeEvent event){
		Date date = new Date(
				Integer.parseInt(this.yearList.getSelectedItemText()) - 1900,
				this.monthList.getSelectedIndex(),
				1
		);
		setFindingDates(date);
	}
	
	@UiHandler("yearList")
	void changeYearList(ChangeEvent event){
		Date date = new Date(
				Integer.parseInt(this.yearList.getSelectedItemText()) - 1900,
				this.monthList.getSelectedIndex(),
				1
		);
		setFindingDates(date);
	}

	// ------------------------------------------------------------------------
	//							AUX Methods
	// ------------------------------------------------------------------------
	
	private boolean checkDate() {
		Date currentDate = new Date(new Date().getYear(), new Date().getMonth(), 1);
		
		Integer selectedMonth = this.monthList.getSelectedIndex();
		Integer selectedYear = Integer.parseInt(this.yearList.getSelectedItemText()) - 1900;
		Date selectedDate = new Date(selectedYear, selectedMonth, 1);
		
		if (currentDate.after(selectedDate))
			return true;
		else
			return false;
	}
	
	private String getRegimeName( String regimeCode ){
		switch (regimeCode) {
		case "0163":
			return "Trabajadores cuenta ajena agrarios";
		case "0138":
			return "Emplead@s de hogar";
		case "0111":
			return "Principal";
		default:
			return "Desconocido";
		}
	}
	
	private void setFindingCCC(String selectedCCC) {
		for(Activity activity : enterpriseInfo.getActivities())
			for(CCC ccc : activity.getCccs())
				if(ccc.getCode().equals(selectedCCC))
					this.cccId = ccc.getId();
		
		this.ccc = selectedCCC;
	}

	private void setFindingDates(Date selectedDate) {
		this.startDate = selectedDate;
		this.endDate = DateUtils.getLastDayOfMonth(selectedDate);
	}

}
