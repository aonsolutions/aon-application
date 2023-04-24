package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.MultiFileUpload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarButton;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsContractInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsEmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsIT;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsITEmployee;
import com.esferalia.aon.gwt.payroll.shared.FIEService.JsITPart;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class MainMassiveFie extends MainEntryPoint{
	
	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainMassiveFie> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String dockLayoutPanel();
		String filterPanel();
		String flex();
		String gridTitle();
		String headerFixed();
		String headerFSize();
		String modify();
		String oddRow();
		String pl05();
	}
	
	@UiField(provided = true)
	AonToolbar toolbar;
	
	@UiField
	HTMLPanel messagePanel;
	
	@UiField
	Grid fieDataTableHeader;
	
	@UiField
	ScrollPanel scrollPanel;
	
	@UiField
	DeckPanel deckPanel;
	
	@UiField
	Grid fieDataTable;
	
	// ----------------------------------------------- Variables
	
	private DateTimeFormat formatDate = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonToolbarButton fieBtn;
	private MultiFileUpload msjFIEFileUpload;
	
	// ----------------------------------------------- Constructor

	public MainMassiveFie() {	
		createToolbar();
		
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		ui.addStyleName(style.dockLayoutPanel());
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);

		AonMessagePanel.hideMessage(messagePanel);
	}
	
	// ----------------------------------------------- onModuleLoad
	
	@Override
	public void onModuleLoad() {
		showFIEMessage();
	}
	
	private void initPreview(List<ITEmployee> itEmployees) {
		if(itEmployees.isEmpty())
			showFIEMessage();
		else {
			
			showFIETable();
			fieDataTableHeader.clear();
			fieDataTableHeader.resize(0, 0);
			fieDataTableHeader.resizeColumns(9);
			
			fieDataTable.clear();
			fieDataTable.resize(0, 0);
			fieDataTable.resizeColumns(9);
			
			paintHeader();
			fillFIETable(itEmployees);
			setColumnWidth();
			setScrollHeight();
		}
	}

	private void paintHeader() {
		int row = fieDataTableHeader.insertRow(fieDataTableHeader.getRowCount());
		
		Label enterprise = new Label("EMPRESA");
		Label name = new Label("NOMBRE");
		Label document = new Label("DOCUMENTO");
		Label nss = new Label("NSS");
		Label type = new Label("TIPO");
		Label startDate = new Label("F.BAJA");
		Label endDate = new Label("F.ALTA");
		Label lowCause = new Label("M.BAJA");
		Label highCause = new Label("M.ALTA");
		
		enterprise.addStyleName(style.gridTitle());
		enterprise.addStyleName(style.headerFSize());
		name.addStyleName(style.gridTitle());
		name.addStyleName(style.headerFSize());
		document.addStyleName(style.gridTitle());
		document.addStyleName(style.headerFSize());
		nss.addStyleName(style.gridTitle());
		nss.addStyleName(style.headerFSize());
		type.addStyleName(style.gridTitle());
		type.addStyleName(style.headerFSize());
		startDate.addStyleName(style.gridTitle());
		startDate.addStyleName(style.headerFSize());
		endDate.addStyleName(style.gridTitle());
		endDate.addStyleName(style.headerFSize());
		lowCause.addStyleName(style.gridTitle());
		lowCause.addStyleName(style.headerFSize());
		highCause.addStyleName(style.gridTitle());
		highCause.addStyleName(style.headerFSize());
		
		fieDataTableHeader.setWidget(row, 0, enterprise);
		fieDataTableHeader.setWidget(row, 1, name);
		fieDataTableHeader.setWidget(row, 2, document);
		fieDataTableHeader.setWidget(row, 3, nss);
		fieDataTableHeader.setWidget(row, 4, type);
		fieDataTableHeader.setWidget(row, 5, startDate);
		fieDataTableHeader.setWidget(row, 6, endDate);
		fieDataTableHeader.setWidget(row, 7, lowCause);
		fieDataTableHeader.setWidget(row, 8, highCause);
		
		fieDataTableHeader.getCellFormatter().addStyleName(row, 0, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 1, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 2, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 3, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 4, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 5, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 6, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 7, style.headerFixed());
		fieDataTableHeader.getCellFormatter().addStyleName(row, 8, style.headerFixed());
	}
	
	private void fillFIETable(List<ITEmployee> itEmployees) {
		for (ITEmployee itEmployee : itEmployees) {
			
			String enterpriseName = itEmployee.getContractInfo().getEnterpriseName();
			String fullName = itEmployee.getEmployeeInfo().getFullName();
			String document = itEmployee.getEmployeeInfo().getDocument();
			String nss = itEmployee.getEmployeeInfo().getSsNumber();
			
			for(IT it : itEmployee.getIts()) {
				
				Byte lowCause = it.getTypeLowPart();
				Byte highCause = it.getTypeHighPart();
				
				for( ITPart itPart : it.getITParts()) {
					
					int row = fieDataTable.insertRow(fieDataTable.getRowCount());
					
					fieDataTable.setWidget(row, 0, new Label(enterpriseName));
					fieDataTable.setWidget(row, 1, new Label(fullName));
					fieDataTable.setWidget(row, 2, new Label(document));
					fieDataTable.getWidget(row, 2).getElement().getStyle().setTextAlign(TextAlign.CENTER);
					fieDataTable.setWidget(row, 3, new Label(nss));
					fieDataTable.getWidget(row, 3).getElement().getStyle().setTextAlign(TextAlign.CENTER);
					
					if(itPart.getType() == (byte) 0) {
						fieDataTable.setWidget(row, 4, new Label("Baja"));
						fieDataTable.setWidget(row, 5, new Label(parseDate(itPart.getDate())));
						fieDataTable.getWidget(row, 5).getElement().getStyle().setTextAlign(TextAlign.CENTER);
						fieDataTable.setWidget(row, 6, new Label());
						
						Label label = new Label(parseShortLowCauseByte(lowCause));
						label.setTitle(parseShortLowCauseByteTitle(lowCause));
						fieDataTable.setWidget(row, 7, label);
						fieDataTable.getWidget(row, 7).getElement().getStyle().setTextAlign(TextAlign.CENTER);
						fieDataTable.setWidget(row, 8, new Label());
						
						
					} else if(itPart.getType() == (byte) 1) {
						fieDataTable.setWidget(row, 4, new Label("Confirmaci\u00f3n"));
						fieDataTable.setWidget(row, 5, new Label(parseDate(itPart.getDate())));
						fieDataTable.getWidget(row, 5).getElement().getStyle().setTextAlign(TextAlign.CENTER);
						fieDataTable.setWidget(row, 6, new Label());
						fieDataTable.setWidget(row, 7, new Label());
						fieDataTable.setWidget(row, 8, new Label());
						
					} else if(itPart.getType() == (byte) 2) {
						fieDataTable.setWidget(row, 4, new Label("Alta"));
						fieDataTable.setWidget(row, 5, new Label());
						fieDataTable.setWidget(row, 6, new Label(parseDate(itPart.getDate())));
						fieDataTable.getWidget(row, 6).getElement().getStyle().setTextAlign(TextAlign.CENTER);
						fieDataTable.setWidget(row, 7, new Label());
						
						Label label = new Label(parseShortHighCauseByte(highCause));
						label.setTitle(parseShortHighCauseByteTitle(highCause));
						fieDataTable.setWidget(row, 8, label);
						fieDataTable.getWidget(row, 8).getElement().getStyle().setTextAlign(TextAlign.CENTER);
					}
					
					if (row % 2 == 0) {
						fieDataTable.getCellFormatter().addStyleName(row, 0, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 1, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 2, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 3, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 4, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 5, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 6, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 7, style.oddRow());
						fieDataTable.getCellFormatter().addStyleName(row, 8, style.oddRow());
					}
					
					fieDataTable.getRowFormatter().getElement(row).getStyle().setHeight(25.00, Unit.PX);
				}
			}
		}
	}
	
	private void setColumnWidth() {
		fieDataTableHeader.getColumnFormatter().getElement(0).getStyle().setWidth(20, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(1).getStyle().setWidth(20, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(2).getStyle().setWidth(10, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(3).getStyle().setWidth(10, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(4).getStyle().setWidth(7, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(5).getStyle().setWidth(7, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(6).getStyle().setWidth(7, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(7).getStyle().setWidth(7, Unit.PCT);
		fieDataTableHeader.getColumnFormatter().getElement(8).getStyle().setWidth(7, Unit.PCT);
		
		fieDataTable.getColumnFormatter().getElement(0).getStyle().setWidth(20, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(1).getStyle().setWidth(20, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(2).getStyle().setWidth(10, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(3).getStyle().setWidth(10, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(4).getStyle().setWidth(7, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(5).getStyle().setWidth(7, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(6).getStyle().setWidth(7, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(7).getStyle().setWidth(7, Unit.PCT);
		fieDataTable.getColumnFormatter().getElement(8).getStyle().setWidth(7, Unit.PCT);
	}
	
	private String parseShortLowCauseByte(Byte typeLowPart) {
		if(null == typeLowPart) return "-";
		
		switch (typeLowPart) {
			case (byte)0:
				return "ECC";
			case (byte)1:
				return "ATT";
			case (byte)2:
				return "MAT";
			case (byte)3:
				return "PAT";
			case (byte)4:
				return "REM";
			case (byte)5:
				return "RLA";
			case (byte)6:
				return "ANL";
			case (byte)7:
				return "ECC";
			case (byte)8:
				return "COV";
			case (byte)9:
				return "OEP";
			default:
				return "-";
		}
	}
	
	private String parseShortLowCauseByteTitle(Byte typeLowPart) {
		if(null == typeLowPart) return "";
		
		switch (typeLowPart) {
			case (byte)0:
				return "Enfermedad Com\u00Fan";
			case (byte)1:
				return "Accidente de trabajo";
			case (byte)2:
				return "Maternidad";
			case (byte)3:
				return "Paternidad";
			case (byte)4:
				return "Riesgo para el embarazo";
			case (byte)5:
				return "Riesgo durante la lactancia";
			case (byte)6:
				return "Accidente no laboral";
			case (byte)7:
				return "Enfermedad com\u00Fan periodo de carencia";
			case (byte)8:
				return "Enfermedad com\u00Fan, prestaci\u00F3n profesional (COVID-19)";
			default:
				return "";
		}
	}
	
	private String parseShortHighCauseByte(Byte typeHighPart) {
		if(null == typeHighPart) return "-";
		
		switch (typeHighPart) {
			case (byte)0:
				return "CUR";
			case (byte)1:
				return "FAL";
			case (byte)2:
				return "INM";
			case (byte)3:
				return "PRI";
			case (byte)4:
				return "APL";
			case (byte)5:
				return "MPT";
			case (byte)6:
				return "INC";
			case (byte)7:
				return "CID";
			case (byte)8:
				return "RCP";
			case (byte)9:
				return "ICF";
			default:
				return "-";
		}
	}
	
	private String parseShortHighCauseByteTitle(Byte typeHighPart) {
		if(null == typeHighPart) return "";
		
		switch (typeHighPart) {
			case (byte)0:
				return "Curaci\u00F3n";
			case (byte)1:
				return "Fallecimiento";
			case (byte)2:
				return "Inspecci\u00F3n m\u00e9dica";
			case (byte)3:
				return "Propuesta incapacidad";
			case (byte)4:
				return "Agotamiento de plazo";
			case (byte)5:
				return "Mejor\u00eda que permite realizar el trabajo habitual";
			case (byte)6:
				return "Incomparecencia";
			case (byte)7:
				return "Control INSS duraci\u00F3n 12 meses";
			case (byte)8:
				return "Recuperaci\u00F3n capacidad profesional";
			case (byte)9:
				return "Incomparecencia contratos de formaci\u00F3n";
			default:
				return "";
		}
	}
	
	private String parseDate(Date date) {
		return null == date ? "" : formatDate.format(date);
	}

	private void setScrollHeight() {
		scrollPanel.setHeight((Window.getClientHeight() - 230) + "px");
	}
	
	private void showFIETable() {
		deckPanel.showWidget(0);
	}
	
	private void showFIEMessage() {
		deckPanel.showWidget(1);
	}
	
	// ----------------------------------------------- Toolbar
	
	private void createToolbar() {
		this.toolbar = new AonToolbar("FIE");
		
		// FORM
		FormPanel msjFIEFormPanel = new FormPanel();
		msjFIEFormPanel.setMethod(FormPanel.METHOD_POST);
		msjFIEFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		msjFIEFormPanel.setAction(URL.encode(GWT.getModuleBaseURL() + "fie_massive"));
		
		Hidden userNameHidden = new Hidden(FIEService.Parameter.USER.name(), Wnd.getCurrentUser());
		Hidden domainNameHidden = new Hidden(FIEService.Parameter.DOMAIN.name(), Wnd.getCurrentDomainNameURL());
		
		msjFIEFileUpload = new MultiFileUpload();
		msjFIEFileUpload.setName(FIEService.Parameter.FILE.name());
		msjFIEFileUpload.setVisible(false);
		msjFIEFileUpload.setAccept(".msj");
		msjFIEFileUpload.addChangeHandler(e -> {
			AonMessagePanel.showLoading(messagePanel, "Analizando fichero FIE para importaci\u00f3n ...");
			msjFIEFormPanel.submit();
			
		});
		msjFIEFormPanel.addSubmitCompleteHandler(e -> {
			String json = e.getResults();

			JsArray<JsITEmployee> jsITEmployees = eval("(" + json + ")");
		
			List<ITEmployee> itEmployees = new ArrayList<>(jsITEmployees.length());
			
			for (int i = 0; i < jsITEmployees.length(); i++ ) {
				JsITEmployee jsITEmployee = jsITEmployees.get(i);			
				ITEmployee itEmployee = fromJsITEmployee(jsITEmployee);
				itEmployees.add(itEmployee); 		
			}
			
			AonMessagePanel.showLoading(messagePanel, "Cargando partes I.T. importados ...");
			
			initPreview(itEmployees);
			
			AonMessagePanel.showSuccess(messagePanel, "FIE importado correctamente. Puede visualizar los datos importados en la tabla.");
		});
		
		FlowPanel formFlowPanel = new FlowPanel();
		formFlowPanel.add(userNameHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(msjFIEFileUpload);
		
		msjFIEFormPanel.add(formFlowPanel);
		toolbar.add(msjFIEFormPanel);
		
		fieBtn = new AonToolbarButton("Mensaje del INSS Empresa (FIE)", AON.CSS.aonIconTgss());
		fieBtn.ensureDebugId("fieBtn");
		fieBtn.addClickHandler(e -> msjFIEFileUpload.click());
		
		toolbar.add(fieBtn);
	}
	
	// --------------------------------------------------- FromJS to ITEmployee, IT, ContractInfo, EmployeeInfo
	
	private static ITEmployee fromJsITEmployee(JsITEmployee jsITEmployee) {
		ITEmployee itEmployee = new ITEmployee();
		
		JsEmployeeInfo jsEmployeeInfo = jsITEmployee.getEmployeeInfo();
		EmployeeInfo employeeInfo = fromJsEmployeeInfo(jsEmployeeInfo);
		
		JsContractInfo jsContractInfo = jsITEmployee.getContractInfo();
		ContractInfo contractInfo = fromJsContractInfo(jsContractInfo);
		
		JsArray<JsIT> jsITs = jsITEmployee.getITs();
		List<IT> its = new ArrayList<>();
		for ( int i = 0; i < jsITs.length(); i++ )
			its.add(fromJsIT(jsITs.get(i)));

		itEmployee.setEmployeeInfo(employeeInfo);
		itEmployee.setContractInfo(contractInfo);
		itEmployee.setIts(its);
		
		itEmployee.setStatus(jsITEmployee.getStatus());

		return itEmployee;
	}
	
	private static IT fromJsIT(JsIT jsIT) {
		
		IT it = new IT();
		it.setContract(jsIT.getContract());
		it.setDailyCGCBase(jsIT.getDailyCGCBase());
		it.setDailyCGPBase(jsIT.getDailyCGPBase());
		it.setDailyREGBase(jsIT.getDailyREGBase());
		it.setDescription(jsIT.getDescription());
		it.setDomain(jsIT.getDomain());
		it.setEndDate(jsIT.getEndDate());
		it.setFullName(jsIT.getFullName());
		it.setMaternityReason(jsIT.getMaternityReason());
		it.setMaternityType(jsIT.getMaternityType());
		it.setId(jsIT.getId());
		it.setIsParent(jsIT.isParent());
		it.setITParts(createDefaultITParts(jsIT));
		it.setParent(jsIT.getParent());
		it.setStartDate(jsIT.getStartDate());
		it.setTypeHighPart(jsIT.getTypeHighPart());
		it.setTypeLowPart(jsIT.getTypeLowPart());
		return it;
	}
	
	private static List<ITPart> createDefaultITParts(JsIT jsIT) {
		List<ITPart> itParts = new ArrayList<>();
		
		for ( int i = 0; i < jsIT.getITParts().length(); i++ ) {
			JsITPart jsITPart = jsIT.getITParts().get(i);
			
			// Low ITPart
			ITPart itPart = new ITPart();
			itPart.setId(jsITPart.getId());
			itPart.setDomain(jsITPart.getDomain());
			itPart.setType(jsITPart.getType());
			itPart.setIt(jsITPart.getIt());
			itPart.setCollegeNumber(jsITPart.getCollegeNumber());
			itPart.setConfirmOrderNumber(jsITPart.getConfirmOrderNumber());
			itPart.setCias(jsITPart.getCias());
			itPart.setDate(jsITPart.getDate());
			itPart.setStatus(jsITPart.getStatus());
			
			itParts.add(itPart);
		}
		
		itParts.sort((o1, o2) -> o1.getType().compareTo(o2.getType()));
		return itParts;
	}

	private static ContractInfo fromJsContractInfo(JsContractInfo jsContractInfo) {
		ContractInfo contractInfo = new ContractInfo();	
		contractInfo.setActivityId(jsContractInfo.getActivityId());
		contractInfo.setEnterpriseCIF(jsContractInfo.getEnterpriseCIF());
		contractInfo.setEnterpriseName(jsContractInfo.getEnterpriseName());
		contractInfo.setCccId(jsContractInfo.getCccId());
		contractInfo.setCompleteCCC(jsContractInfo.getCompleteCCC());
		contractInfo.setCccType(jsContractInfo.getCccType());
		contractInfo.setWorkplaceId(jsContractInfo.getWorkplaceId());
		contractInfo.setWorkplaceZIP(jsContractInfo.getWorkplaceZIP());
		contractInfo.setWorkplaceFullAddress(jsContractInfo.getWorkplaceFullAddress());
		contractInfo.setContractType(jsContractInfo.getContractType());
		contractInfo.setContractModel(jsContractInfo.getContractModel());
		contractInfo.setStartDate(jsContractInfo.getStartDate());
		contractInfo.setEndDate(jsContractInfo.getEndDate());
		contractInfo.setSeniorityDate(jsContractInfo.getSeniorityDate());
		contractInfo.setAgreementId(jsContractInfo.getAgreementId());
		contractInfo.setAgreementLevelId(jsContractInfo.getAgreementLevelId());
		contractInfo.setAgreementCategory(jsContractInfo.getAgreementCategory());
		contractInfo.setQuoteGroup(jsContractInfo.getQuoteGroup());
		contractInfo.setOcupation(jsContractInfo.getOcupation());
		contractInfo.setJourneyType(jsContractInfo.getJourneyType());
		contractInfo.setSsRegimen(jsContractInfo.getSsRegimen());
		contractInfo.setContractId(jsContractInfo.getContractId());
		contractInfo.setContracttypeId(jsContractInfo.getContracttypeId());
		contractInfo.setQuotegroupId(jsContractInfo.getQuotegroupId());
		contractInfo.setOcupationId(jsContractInfo.getOcupationId());
		contractInfo.setJourneytypeId(jsContractInfo.getJourneytypeId());
		contractInfo.setContractmodelId(jsContractInfo.getContractmodelId());
		contractInfo.setRetaId(jsContractInfo.getRetaId());
		contractInfo.setHasPayroll(jsContractInfo.getHasPayroll());
		contractInfo.setPayrollDate(jsContractInfo.getPayrollDate());
		return contractInfo;
	}

	private static EmployeeInfo fromJsEmployeeInfo(JsEmployeeInfo jsEmployeeInfo) {		
		EmployeeInfo employeeInfo = new EmployeeInfo();
		
		employeeInfo.setAccount(jsEmployeeInfo.getAccount());
		employeeInfo.setAddresNum(jsEmployeeInfo.getAddresNum());
		employeeInfo.setAddress(jsEmployeeInfo.getAddress());
		employeeInfo.setAddressCity(jsEmployeeInfo.getAddressCity());
		employeeInfo.setAddressInfo(jsEmployeeInfo.getAddressInfo());
		employeeInfo.setAddressZip(jsEmployeeInfo.getAddressZip());
		employeeInfo.setBic(jsEmployeeInfo.getBic());
		employeeInfo.setBirthdate(jsEmployeeInfo.getBirthdate());
		employeeInfo.setCivilStatus(jsEmployeeInfo.getCivilStatus());
		employeeInfo.setContractActive(jsEmployeeInfo.getContractActive());
		employeeInfo.setContractId(jsEmployeeInfo.getContractId());
		employeeInfo.setDocument(jsEmployeeInfo.getDocument());
		employeeInfo.setDocumentType(jsEmployeeInfo.getDocumentType());
		employeeInfo.setDomain(jsEmployeeInfo.getDomain());
		employeeInfo.setEmail(jsEmployeeInfo.getEmail());
		employeeInfo.setEmailId(jsEmployeeInfo.getEmailId());
		employeeInfo.setEmployeeId(jsEmployeeInfo.getEmployeeId());
		employeeInfo.setGender(jsEmployeeInfo.getGender());
		employeeInfo.setIsFullTime(jsEmployeeInfo.getIsFullTime());
		employeeInfo.setMobile(jsEmployeeInfo.getMobile());
		employeeInfo.setMobileId(jsEmployeeInfo.getMobileId());
		employeeInfo.setName(jsEmployeeInfo.getName());
		employeeInfo.setNationality(jsEmployeeInfo.getNationality());
		employeeInfo.setPaymethodId(jsEmployeeInfo.getPaymethodId());
		employeeInfo.setPayMethodType(jsEmployeeInfo.getPayMethodType());
		employeeInfo.setPayMethodTypeB(jsEmployeeInfo.getPayMethodTypeB());
		employeeInfo.setPhone(jsEmployeeInfo.getPhone());
		employeeInfo.setPhoneId(jsEmployeeInfo.getPhoneId());
		employeeInfo.setRaddressId(jsEmployeeInfo.getRaddressId());
		employeeInfo.setRbankId(jsEmployeeInfo.getRbankId());
		employeeInfo.setRpaymethodId(jsEmployeeInfo.getRpaymethodId());
		employeeInfo.setSecondSurName(jsEmployeeInfo.getSecondSurName());
		employeeInfo.setSsNumber(jsEmployeeInfo.getSsNumber());
		employeeInfo.setStreetType(jsEmployeeInfo.getStreetType());
		employeeInfo.setSurName(jsEmployeeInfo.getSurName());
		
		return employeeInfo;
	}

	private static Date parseJsDate(String date) {
		if(AonStringUtils.isBlank(date) || date.length() != 8)
			return null;
		
		String year = date.substring(0, 4);
		String month = date.substring(4, 6);
		String day = date.substring(6, 8);
		
		return new Date(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
	}
	
	private static Date parseDate(String str) {
		if ( str == null || str.trim().length() == 0)
			return null;
		
		try {
			return DateTimeFormat.getFormat("yyyy-MM-dd").parse(str);
		} catch ( IllegalArgumentException e ) {
			return null;
		}
	}
	
	private static native <T extends JavaScriptObject> T eval(String javascript) /*-{
		return eval(javascript);
	}-*/;

}
