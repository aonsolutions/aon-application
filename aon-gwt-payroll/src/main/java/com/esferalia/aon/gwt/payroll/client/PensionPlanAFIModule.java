package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Activity;
import com.esferalia.aon.gwt.payroll.shared.CCC;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.ProvinceContract;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class PensionPlanAFIModule extends MainEntryPoint {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	interface Binder extends UiBinder<Widget, PensionPlanAFIModule> {}
	
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String bold();
		String paddingDays();
		String widthDays();
		String textCenter();
		String widthFirstColumn();
		String paddingText();
		String headerStyle();
		String cellStyle();
	}
	
	@UiField
	DockLayoutPanel dockLayoutPanel;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	Label enterprise;
	
	@UiField
	ListBox enterpriseList;
	
	@UiField
	ListBox cccsLB;
	
	@UiField
	CheckBox allCCCs;
	
	@UiField
	Label allCCCsLabel;
	
	@UiField
	ListBox monthList;
	
	@UiField
	ListBox yearList;
	
	// All enterpises of a domain
	private List<Enterprise> enterprisesList;
	
	// Selected enterprise, if only one that is the one selected
	private Enterprise selectedEnterprise;
	
	// All the CCCs of the selected enterprise
	private Map<String, List<CCC>> cccs = new HashMap<>();
	
	// The list of CCCs we are going to generate CRA
	private List<Integer> cccIdList = new ArrayList<Integer>();
	
	// The date to generate AFI
	private Date startDate = null;
	
	// CCC Id we are going to generate AFI of
	
	private AonToolbar toolbar;
	private AonToolbarButton exportButton;
	
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("ddMMyyyy");
	
	public void onModuleLoad() {
		// Inject style
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
		AON.ensureInjected();
	
		// Create UiBinder
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
		
		// Create toolbar
		toolbar = getToolbarPanel();
		dockLayoutPanel.addNorth( toolbar , AonToolbar.HEIGTH );
		
		initListBoxes();
		initLogic();	
	}
	
	// ---------------------------------------------------------------------------------------------
	//										LOAD PREVIEW
	// ---------------------------------------------------------------------------------------------
	
	private void initListBoxes() {
		// Set list box for filter by dates
		monthList.clear();
		yearList.clear();
		
		String[] months = new String[]{"Enero", "Frebero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
		for(int i=0; i<months.length; i++) {
			monthList.addItem(months[i], i+"");
		}
		
		Integer yearInt = DateUtils.getYear();
		
		yearList.addItem(yearInt +"", yearInt + "");
		yearList.addItem((yearInt - 1) + "", (yearInt - 1) + "");
		yearList.addItem((yearInt - 2) + "", (yearInt - 2) + "");
	}

	private void initLogic() {
		cccs.clear();
		enterpriseList.clear();
		
		impl.getEnterprises(0, Integer.MAX_VALUE, new AsyncCallback<List<Enterprise>>() {
			
			@Override
			public void onSuccess(List<Enterprise> enterprises) {
				if(enterprises.isEmpty())
					AonMessagePanel.showError(messagePanel, "No se ha podido cargar la empresa");
				else {
					// Set all enterprises of a domain
					enterprisesList = enterprises;
					
					if(enterprises.size() == 1){
						setVisible(enterpriseList, false);
						setVisible(enterprise, true);
					} else {
						setVisible(enterpriseList, true);
						setVisible(enterprise, false);
						
						// Fill enterprise list box
						for (Enterprise enterprise: enterprises)
							enterpriseList.addItem(enterprise.getName(), enterprise.getId().toString());
					}
					
					selectedEnterprise = enterprises.get(0);
					
					for(Activity activity : selectedEnterprise.getActivities())
						for(CCC ccc : activity.getCccs())
							addCCCToActivity(activity.getDescription(), ccc);
					
					initializeView();
				}
			}
			
			@Override
			public void onFailure(Throwable caught) { }
			
		});
	}
	
	private void initializeView(){
		//Enterprise Name
		enterprise.setText(this.selectedEnterprise.getName());
		allCCCsLabel.setText(" Todos los CCCs de " + this.selectedEnterprise.getName());
		enterprise.addStyleName(style.bold());
		
		if(this.cccs.isEmpty()){
			this.cccsLB.setEnabled(false);
			this.monthList.setEnabled(false);
			this.yearList.setEnabled(false);
			
			AonMessagePanel.showError(messagePanel, "No existe ninguna cuenta de cotizaci"+String.valueOf("\u00F3")+"n.");
		}else{
			for(Entry<String, List<CCC>> entry : this.cccs.entrySet()){
				for(CCC ccc : entry.getValue()) {
					this.cccsLB.addItem(entry.getKey() + " - " + getRegimeName(ccc.getType()) + " - " 
							+ ccc.getCode() + " - (" + ProvinceContract.getName(ccc.getGeozone()) +")", ccc.getId().toString());
				}
			}
		}
		
		// Period
		Date currentDate = new Date();
		currentDate = DateUtils.addMonths2Date(currentDate, -1);
		
		setSelectedValueLB(monthList, currentDate.getMonth() + "");
		setSelectedValueLB(yearList, DateUtils.getYear(currentDate) + "");
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

	// ---------------------------------------------------------------------------------------------
	//										UI HANDLERS
	// ---------------------------------------------------------------------------------------------
	
	@UiHandler("enterpriseList")
	void changeEnterpriseList(ChangeEvent event){
		for(Enterprise enterprise: enterprisesList) {
			if(enterprise.getId().equals(Integer.parseInt(enterpriseList.getSelectedValue()))) {
				this.selectedEnterprise = enterprise;
				continue;
			}
		}
		
		cccsLB.clear();
		cccs.clear();
		
		for(Activity activity : selectedEnterprise.getActivities())
			for(CCC ccc : activity.getCccs())
					addCCCToActivity(activity.getDescription(), ccc);
		
		for(Entry<String, List<CCC>> entry : this.cccs.entrySet()){
			for(CCC ccc : entry.getValue()) {
				this.cccsLB.addItem(entry.getKey() + " - " + getRegimeName(ccc.getType()) + " - " 
						+ ccc.getCode() + " - (" + ProvinceContract.getName(ccc.getGeozone()) +")", ccc.getId().toString());
			}
			
		}
		
		if(this.cccs.isEmpty()){
			this.cccsLB.setEnabled(false);
			this.monthList.setEnabled(false);
			this.yearList.setEnabled(false);
			
			AonMessagePanel.showError(messagePanel, "No existe ninguna cuenta de cotizaci"+String.valueOf("\u00F3")+"n.");
		}else{
			this.cccsLB.setEnabled(true);
			this.monthList.setEnabled(true);
			this.yearList.setEnabled(true);
		}
		
		allCCCsLabel.setText(" Todos los CCCs de " + this.selectedEnterprise.getName());
	}
	
	@UiHandler("allCCCs")
	void clickAllCCCs(ClickEvent event){
		cccsLB.setEnabled(!allCCCs.getValue());
	}

	// ---------------------------------------------------------------------------------------------
	//										AUXILIAR METHODS
	// ---------------------------------------------------------------------------------------------
	
	private void setVisible(Widget widget, boolean isVisible) {
		widget.setVisible(isVisible);
	}
	
	private void addCCCToActivity(String activityDescription, CCC ccc) {
		if(cccs.get(activityDescription) == null) {
			List<CCC> cccsList = new ArrayList<CCC>();
			cccsList.add(ccc);
			cccs.put(activityDescription, cccsList);
		} else {
			cccs.get(activityDescription).add(ccc);
		}
	}
	
	private String getRegimeName( byte type ){
		switch (type) {
		case 0:
			return "Principal";
		case 1:
			return "Formacion y aprendizaje";
		case 2:
			return "Aprendizaje";
		case 3:
			return "Representantes de comercio";
		case 4:
			return "Asimilados R.General";
		case 5:
			return "Becarios";
		case 6:
			return "Emplead@s de hogar";
		case 7:
			return "Trabajadores cuenta ajena agrarios";
		case 8:
			return "Artistas";
		default:
			return "Desconocido";
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	//										TOOLBAR
	// ---------------------------------------------------------------------------------------------
	
	private AonToolbar getToolbarPanel() {
		
		AonToolbar toolbar = new AonToolbar("AFI - Reducciones por contribuciones a planes de pensiones de empleo");
		
		exportButton = new AonToolbarButton( "Generar AFI", AON.CSS.aonIconTgssAfi() );
		exportButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onExportButton(event);
			}
		});
		toolbar.add(exportButton);

		return toolbar;
	}
	
	@SuppressWarnings("deprecation")
	private void onExportButton(ClickEvent event) {
		AonMessagePanel.showLoading(messagePanel, "Comprobando si existen devengos con CRA 0000 - APORTACION EMPRESARIAL AL PLAN DE PENSIONES DE EMPLEO");
		
		startDate = new Date(
			 Integer.parseInt(yearList.getSelectedValue()) - 1900,  
			 Integer.parseInt(monthList.getSelectedValue()), 
			 1);
		
		getCccIdList();
		
		impl.checkPensionPlanAFI(startDate.getTime(), cccIdList, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String message) {
				if(AonStringUtils.isNotBlank(message)) {
					AonMessagePanel.showWarning(messagePanel, message);
				} else {
					AonMessagePanel.showLoading(messagePanel, "Generando fichero AFI...");
					
					String fileDownloadURL = GWT.getModuleBaseURL()+ "pension_plan_afi/"
				            + "?findingDate=" + dateFormat.format(startDate)
				            + "&selectedCCCs="+ cccIdList.size();
						
					for(int i=0; i<cccIdList.size(); i++) {
						fileDownloadURL += "&ccc"+i+"Id=" + cccIdList.get(i);
					}
					
					Window.open(fileDownloadURL, "_blank", null);
					
					new Timer() {
						@Override
						public void run() {
							AonMessagePanel.hideMessage(messagePanel);
						}
					}.schedule(1500);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}
		});
		
	}

	private void getCccIdList() {
		cccIdList.clear();
		
		if(allCCCs.getValue()) {
			for(Activity activity : selectedEnterprise.getActivities())
				for(CCC ccc : activity.getCccs())
					cccIdList.add(ccc.getId());
		} else {
			cccIdList.add(Integer.parseInt(cccsLB.getSelectedValue()));
		}
		
		
	}

}
