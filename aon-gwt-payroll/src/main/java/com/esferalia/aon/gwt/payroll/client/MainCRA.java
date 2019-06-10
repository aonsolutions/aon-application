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
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
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
		String exportBtnPadding(); 
	}
	
	@UiField
	DockLayoutPanel dockLayout;
	
	@UiField
	Button exportButton;
	
	@UiField
	HTMLPanel newCRAPanel;
	
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
	
	@UiField
	VerticalPanel mainRectificativoCRAsPanel;
	
	@UiField
	Grid mainRectificativoCRAsTable;
	
	private Enterprise enterpriseInfo = new Enterprise();
	private String enterpriseName = "";
	private Map<String, CCC> enterpriseCCCs = new HashMap<>();
	private Integer cccId = 0;
	private String ccc = "";
	private Integer domainId = 0;
	
	private Date startDate = null;
	private Date endDate = null;
	
	private List<CRA> cras;
	private List<Enterprise> enterprisesList;
	
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
		
		//Hide CRAPanel
		mainCRAsPanel.addStyleName(style.hide());
		
		//Hide RectificativoCRAPanel
		mainRectificativoCRAsPanel.addStyleName(style.hide());
		
		initLogic();
				
	}
	
	private void initLogic() {
		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				enterprisesList = enterprises;
				
				impl.getDomain(new AsyncCallback<Integer>() {
					
					@Override
					public void onSuccess(Integer result) {
						domainId = result;
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
				
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
		if(cras.size() > 0) {
			for(CRA cra : cras) {
				if(cra.getType() == "N") {
					mainCRAsPanel.removeStyleName(style.hide());
					fillHeader(mainCRAsTable);
					break;
				}
			}
			
			for(CRA cra : cras) {
				if(cra.getType() == "R") {
					mainRectificativoCRAsPanel.removeStyleName(style.hide());
					fillHeader(mainRectificativoCRAsTable);
					break;
				}
			}
		}else {
			//Add hide
			mainCRAsPanel.addStyleName(style.hide());
			
			//Add hide
			mainRectificativoCRAsPanel.addStyleName(style.hide());
		}
	}
	
	private void fillHeader(Grid mainTable) {
		//Header Grid
		mainTable.resize(0,9);
		Integer newRow = mainTable.insertRow(mainTable.getRowCount());

		Label code = new Label("C"+String.valueOf("\u00F3")+"digo");
		code.addStyleName(style.bold());
		code.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 0, code);
		mainTable.getWidget(newRow, 0).addStyleName(style.width50());
		
		Label crationDate = new Label("Periodo CRA");
		crationDate.addStyleName(style.bold());
		crationDate.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 1, crationDate);
		mainTable.getWidget(newRow, 1).addStyleName(style.width150());
		
		Label status = new Label("Estado");
		status.addStyleName(style.bold());
		status.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 2, status);
		mainTable.getWidget(newRow, 2).addStyleName(style.width100());
		
		Label activity = new Label("Actividad");
		activity.addStyleName(style.bold());
		activity.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 3, activity);
		mainTable.getWidget(newRow, 3).addStyleName(style.width150());
		
		Label activityType = new Label("Tipo Actividad");
		activityType.addStyleName(style.bold());
		activityType.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 4, activityType);
		mainTable.getWidget(newRow, 4).addStyleName(style.width100());
		
		Label ccc = new Label("Cuenta de cotizaci"+String.valueOf("\u00F3")+"n");
		ccc.addStyleName(style.bold());
		ccc.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 5, ccc);
		mainTable.getWidget(newRow, 5).addStyleName(style.width150());
		
		Label cccProvince = new Label("Provincia");
		cccProvince.addStyleName(style.bold());
		cccProvince.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 6, cccProvince);
		mainTable.getWidget(newRow, 6).addStyleName(style.width100());
		
		Label delete = new Label("");
		mainTable.setWidget(newRow, 7, delete);
		mainTable.getWidget(newRow, 7).addStyleName(style.width50());
		
		Label download = new Label("");
		mainTable.setWidget(newRow, 8, download);
		mainTable.getWidget(newRow, 8).addStyleName(style.width50());
	}

	private void fillTable() {
		for(CRA cra : cras) {
			if(cra.getType() == "N") {
				insertRow(cra, mainCRAsTable);
			}
			else if(cra.getType() == "R") {
				insertRow(cra, mainRectificativoCRAsTable);
			}
		}
	}
	
	private void insertRow(CRA cra, Grid mainTable) {
		//Fill Practice Row
		Integer newRow = mainTable.insertRow(mainTable.getRowCount());
		
		Label code = new Label(cra.getCode()+"");
		code.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 0, code);
		
		Date date = cra.getCreationDate();
		Label creationDate = new Label((null == date) ? "" : date.getDate() + "/" + (date.getMonth()+1) + "/" + (date.getYear()+1900));
		creationDate.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 1, creationDate);
		
		Label status = new Label((cra.getStatus() == (byte)0) ? "Pendiente" : "Generado");
		status.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 2, status);
		
		Label activity = new Label(cra.getActivityName());
		activity.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 3, activity);
		
		Label activityType = new Label(cra.getCccType());
		activityType.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 4, activityType);
		
		Label ccc = new Label(cra.getCcc());
		ccc.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 5, ccc);
		
		Label cccProvince = new Label(cra.getCccProvince());
		cccProvince.addStyleName(style.textCenter());
		mainTable.setWidget(newRow, 6, cccProvince);
		
		Button delete = new Button();
		delete.setStyleName("aon-editDataTable-button aon-icon-delete");
		delete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				impl.deleteCRA(/*enterpriseInfo.getDomain()*/ domainId, cra.getCode(), new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(String result) {
						mainCRAsTable.clear();
						mainRectificativoCRAsTable.clear();
						//Hide CRAPanel
						mainCRAsPanel.addStyleName(style.hide());
						//Hide RectificativoCRAPanel
						mainRectificativoCRAsPanel.addStyleName(style.hide());
						initLogic();
					}
					
				});
			}
		});
		mainTable.setWidget(newRow, 7, delete);
		mainTable.getCellFormatter().addStyleName(newRow, 7, style.textCenter());
		
		Button download = new Button();
		download.setStyleName("aon-editDataTable-button aon-icon-mail-save");
		download.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String fileDownloadURL = GWT.getModuleBaseURL()+ "/download_cra/"
			            + "?domainId=" + /*enterpriseInfo.getDomain()*/ domainId
			            + "&craBatchId=" + cra.getCode()
				        ;
				
//				Window.alert(fileDownloadURL);
				Window.open(fileDownloadURL, "_blank", null);
			}
		});
		mainTable.setWidget(newRow, 8, download);
		mainTable.getCellFormatter().addStyleName(newRow, 8, style.textCenter());
	}

	// ------------------------------------------------------------------------
	//							UiHandler Generate CRA
	// ------------------------------------------------------------------------
	
	@UiHandler("exportButton")
	void exportButton(ClickEvent event){
		if(checkDate()){
			if(checkRectificavo()) {
				impl.createNewCRA(/*enterpriseInfo.getDomain()*/ domainId, enterpriseInfo.getId(), enterpriseName, startDate.getTime(), endDate.getTime(), ccc, cccId, "N", new AsyncCallback<String>() {
					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub	
					}
	
					@Override
					public void onSuccess(String result) {
						resetTables();
					}
				});
			}else {
				AcceptCancelDialog dialog = new AcceptCancelDialog("AVISO", "Ya existe un fichero CRA para esta cuenta de cotizaci"+String.valueOf("\u00F3")+"n en este periodo. ",
						"Recuerde que puede eliminar de la tabla dicho fichero CRA. Si por lo contrario quiere generar un fichero CRA rectificativo puede acepte esta ventana.", String.valueOf("\u00BF")+"Desea generar un fichero rectificativo?") {
					
					@Override
					protected void onAccept() {
						impl.createNewCRA(enterpriseInfo.getDomain(), enterpriseInfo.getId(), enterpriseName, startDate.getTime(), endDate.getTime(), ccc, cccId, "R", new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub	
							}
			
							@Override
							public void onSuccess(String result) {
								WarningDialog warning = new WarningDialog("INTRUCCIONES", "Para poder llevar a cabo la rectificaci"+String.valueOf("\u00F3")+"n del fichero "
										+ "CRA, deber"+String.valueOf("\u00E1")+" seguir las siguientes instrucciones : <br><br> 1- Enviar el CRA Rectificativo que se ha generado en el historial de CRAs rectificativos. ");
								warning.center();
								warning.show();
								
								resetTables();
							}
						});
					}
				};
				
				dialog.center();
				dialog.show();
			}
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
		
		for (Enterprise enterprise: enterprisesList)
			for(Activity activity : enterprise.getActivities())
				for(CCC ccc : activity.getCccs())
					if(ccc.getCode().equals(selectedCCC)) {
						this.cccId = ccc.getId();
						enterpriseInfo = enterprise;
						break;
					}
		
//		for(CCC ccc : enterpriseCCCs.values()) {
//			Window.alert(ccc.getCode() + " == " + selectedCCC);
//			if(ccc.getCode().equals(selectedCCC)) {
//				Window.alert("CCC Id : " + ccc.getId());
//				this.cccId = ccc.getId();
//				break;
//			}
//		}
		
		this.enterpriseName = this.enterpriseInfo.getName();
		this.enterprise.setText(this.enterpriseInfo.getName());
		this.ccc = selectedCCC;
	}

	private void setFindingDates(Date selectedDate) {
		this.startDate = selectedDate;
		this.endDate = DateUtils.getLastDayOfMonth(selectedDate);
	}
	
	private boolean checkRectificavo() {
		for(CRA cra : cras) {
			if(new Date(cra.getCreationDate().getTime()).equals(new Date(this.startDate.getTime()))) {
				if(cra.getCcc().substring(4) == this.ccc) {
					return false;
				}
			}
		}
		return true;
	}
	
	private void resetTables() {
		mainCRAsTable.clear();
		mainRectificativoCRAsTable.clear();
		//Hide CRAPanel
		mainCRAsPanel.addStyleName(style.hide());
		//Hide RectificativoCRAPanel
		mainRectificativoCRAsPanel.addStyleName(style.hide());
		initLogic();
	}

}
