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
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		String width50();
		String width100();
		String width150();
		String widthName();
		String textCenter();
		String widthFirstColumn();
		String paddingText();
	}
	
	@UiField
	Button exportButton;
	
	@UiField
	Button refreshTableButton;
	
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
	VerticalPanel mainCRAsPanel;
	
	@UiField
	Grid mainCRAsTable;
	
	private Enterprise enterpriseInfo = new Enterprise();
	private String enterpriseName = "";
	private Map<String, CCC> enterpriseCCCs = new HashMap<>();
	private Integer cccId = 0;
	private String ccc = "";
	
	private Date startDate = null;
	private Date endDate = null;
	
	private List<CRA> cras;
	
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
		mainCRAsPanel.addStyleName(style.hide());
		
		initLogic();
				
	}
	
	private void initLogic() {
		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				impl.getCRAs(new AsyncCallback<List<CRA>>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(List<CRA> result) {
						if(enterprises.size() == 1){
							enterpriseName = enterprises.get(0).getName();
							enterpriseInfo = enterprises.get(0);
						}
						
						for (Enterprise enterprise: enterprises)
							for(Activity activity : enterprise.getActivities())
								for(CCC ccc : activity.getCccs())
									enterpriseCCCs.put(activity.getDescription(), ccc);
						
//						Window.alert(result.size()+"");
						cras = result;
						
						initializeView();
					}
					
				});
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
		this.cccs.clear();
		
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
		
		
		//TABLE
		initializeHeader();
		fillTable();

	}

	private void initializeHeader() {
		//Remove hide
		mainCRAsPanel.removeStyleName(style.hide());
		
		//Header Grid
		mainCRAsTable.resize(0,9);
		Integer newRow = mainCRAsTable.insertRow(mainCRAsTable.getRowCount());

		Label code = new Label("C"+String.valueOf("\u00F3")+"digo");
		code.addStyleName(style.bold());
		code.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 0, code);
		mainCRAsTable.getWidget(newRow, 0).addStyleName(style.width50());
		
		Label crationDate = new Label("Fecha de creaci"+String.valueOf("\u00F3")+"n");
		crationDate.addStyleName(style.bold());
		crationDate.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 1, crationDate);
		mainCRAsTable.getWidget(newRow, 1).addStyleName(style.width150());
		
		Label status = new Label("Estado");
		status.addStyleName(style.bold());
		status.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 2, status);
		mainCRAsTable.getWidget(newRow, 2).addStyleName(style.width100());
		
		Label activity = new Label("Actividad");
		activity.addStyleName(style.bold());
		activity.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 3, activity);
		mainCRAsTable.getWidget(newRow, 3).addStyleName(style.width150());
		
		Label activityType = new Label("Tipo Actividad");
		activityType.addStyleName(style.bold());
		activityType.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 4, activityType);
		mainCRAsTable.getWidget(newRow, 4).addStyleName(style.width100());
		
		Label ccc = new Label("Cuenta de cotizaci"+String.valueOf("\u00F3")+"n");
		ccc.addStyleName(style.bold());
		ccc.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 5, ccc);
		mainCRAsTable.getWidget(newRow, 5).addStyleName(style.width150());
		
		Label cccProvince = new Label("Provincia");
		cccProvince.addStyleName(style.bold());
		cccProvince.addStyleName(style.textCenter());
		mainCRAsTable.setWidget(newRow, 6, cccProvince);
		mainCRAsTable.getWidget(newRow, 6).addStyleName(style.width100());
		
		Label delete = new Label("");
		mainCRAsTable.setWidget(newRow, 7, delete);
		mainCRAsTable.getWidget(newRow, 7).addStyleName(style.width50());
		
		Label download = new Label("");
		mainCRAsTable.setWidget(newRow, 8, download);
		mainCRAsTable.getWidget(newRow, 8).addStyleName(style.width50());
	}
	
	private void fillTable() {
		for(CRA cra : cras) {
			//Fill Practice Row
			Integer newRow = mainCRAsTable.insertRow(mainCRAsTable.getRowCount());
			
			Label code = new Label(cra.getCode()+"");
			code.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 0, code);
			
			Date date = cra.getCreationDate();
			Label creationDate = new Label((null == date) ? "" : date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getYear()+1900));
			creationDate.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 1, creationDate);
			
			Label status = new Label((cra.getStatus() == (byte)0) ? "Pendiente" : "Generado");
			status.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 2, status);
			
			Label activity = new Label(cra.getActivityName());
			activity.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 3, activity);
			
			Label activityType = new Label(cra.getCccType());
			activityType.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 4, activityType);
			
			Label ccc = new Label(cra.getCcc());
			ccc.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 5, ccc);
			
			Label cccProvince = new Label(cra.getCccProvince());
			cccProvince.addStyleName(style.textCenter());
			mainCRAsTable.setWidget(newRow, 6, cccProvince);
			
			Button delete = new Button();
			delete.setStyleName("aon-editDataTable-button aon-icon-delete");
			delete.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String fileDownloadURL = GWT.getModuleBaseURL()+ "/delete_main_cra/"
				            + "?domainId=" + enterpriseInfo.getDomain()
				            + "&craBatchId=" + cra.getCode()
					        ;
					
//					Window.alert(fileDownloadURL);
					Window.open(fileDownloadURL, "_blank", null);
					
					mainCRAsTable.clear();
					initLogic();
				}
			});
			mainCRAsTable.setWidget(newRow, 7, delete);
			mainCRAsTable.getCellFormatter().addStyleName(newRow, 7, style.textCenter());
			
			Button download = new Button();
			download.setStyleName("aon-editDataTable-button aon-icon-mail-save");
			download.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					String fileDownloadURL = GWT.getModuleBaseURL()+ "/download_cra/"
				            + "?domainId=" + enterpriseInfo.getDomain()
				            + "&craBatchId=" + cra.getCode()
					        ;
					
//					Window.alert(fileDownloadURL);
					Window.open(fileDownloadURL, "_blank", null);
				}
			});
			mainCRAsTable.setWidget(newRow, 8, download);
			mainCRAsTable.getCellFormatter().addStyleName(newRow, 8, style.textCenter());
		}
	}
	

	// ------------------------------------------------------------------------
	//							UiHandler Generate CRA
	// ------------------------------------------------------------------------
	
	
	@UiHandler("refreshTableButton")
	void refreshTableButton(ClickEvent event){
		mainCRAsTable.clear();
		initLogic();
	}
	
	@UiHandler("exportButton")
	void exportButton(ClickEvent event){
		if(checkDate()){			
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
