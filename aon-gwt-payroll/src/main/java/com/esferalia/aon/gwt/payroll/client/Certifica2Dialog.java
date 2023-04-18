package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.Widget;

public class Certifica2Dialog extends AonCustomDialog {
	
	// ------------------------------------------------- UIBinder
	
	interface Certifica2DialogUIBinder extends UiBinder<Widget, Certifica2Dialog> {}

	private static final Certifica2DialogUIBinder binder = GWT.create(Certifica2DialogUIBinder.class);
	
	// ------------------------------------------------- UIFileds
	
	@UiField
	MyStyle style;

	interface MyStyle extends CssResource {
		String flex();
		String subTitle();
		String loadingPanel();
	}
	
	@UiField
	HTMLPanel toolbarPanel;
	
	@UiField
	Label representativeDocumentL;
	
	@UiField
	Label representativeNameL;
	
	@UiField
	Label representativeSurnameL;
	
	@UiField
	Label enterpriceDocumentL;
	
	@UiField
	Label completeCCCL;
	
	@UiField
	Label documentL;
	
	@UiField
	Label fullNameL;
	
	@UiField
	Label contractTypeL;
	
	@UiField
	Label quoteGroupL;
	
	@UiField
	Label startDateL;
	
	@UiField
	Label endDateL;
	
	@UiField
	Label contractDurationL;
	
	@UiField
	ListBox suspensionCodeLB;
	
	@UiField
	SuggestBox profesionalCategorySB;
	
	@UiField
	Label settleQuoteDaysL;
	
	@UiField
	Label baseCgcL;
	
	@UiField
	Label baseUnemploymentL;
	
	@UiField
	HTMLPanel quoteDataListTitlePanel;
	
	@UiField
	HTMLPanel quoteDataListPanel;
	
	@UiField
	HTMLPanel messagesPanel;
	
	@UiField
	HTMLPanel loadingPanel;
	
	@UiField
	Label messageL;
	
	@UiField
	HTMLPanel buttonsPanel;
	
	// ------------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync enterprisesService = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	private AonToolbarSmallButton comunicateCertifica2;
	private AonToolbarSmallButton comunicateCertifica2PDF;
	private AonToolbarSmallButton manualCertifica2PDF;
	
	private DomainUserRoles userRoles;
	private Integer contractId;
	private Certifica2Info certifica2Info;
	
	private FormPanel formPanelXML;
	private FormPanel formPanelPDF;
	private FormPanel formPanelManual;
	private Hidden documentHidden;
	private Hidden endDateHidden;
	private Hidden suspensionReasonCodeHidden;
	private Hidden suspensionReasonHidden;
	
	private Map<String, CNO> cnoMap;
	
	private boolean hasChange = false;
	
	// ------------------------------------------------- Constructor
	
	public Certifica2Dialog(Integer contractId) {
		
		setCaption("Certific\u00402");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractId = contractId;
		this.cnoMap = new HashMap<>();
		
		getToolbarPanel();
		getButtonsPanel();
		initLoadingPanel();
		initListBox();
		
		enterprisesService.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				userRoles = result;
				
				if(Boolean.FALSE.equals(userRoles.isComunica()))
					comunicateCertifica2.setVisible(false);
				
				enterprisesService.getCNOs(new AsyncCallback<Map<String, CNO>>() {

					@Override
					public void onFailure(Throwable caught) {
						// Not user here
					}

					@Override
					public void onSuccess(Map<String, CNO> result) {
						cnoMap = result;
						initSuggestBox();
						
						employeesService.getCertifica2Info(contractId, new AsyncCallback<Certifica2Info>() {

							@Override
							public void onFailure(Throwable caught) {
								AonDialog dialog = new AonDialog("Error", new HTML(caught.getMessage()));
								dialog.warning();
							}

							@Override
							public void onSuccess(Certifica2Info certifica2InfoDB) {
								certifica2Info = certifica2InfoDB;
								documentHidden.setValue(certifica2Info.getDocument());
								endDateHidden.setValue(dateFormat.format(certifica2Info.getEndDate()));
								fillFields();
								showDialog();
							}
						});
					}});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonDialog dialog = new AonDialog("Error", new HTML(caught.getMessage()));
				dialog.warning();
			}
			
		});
	}

	// ------------------------------------------------- Constructor Methods
	
	private void initListBox() {
		this.suspensionCodeLB.addItem("-", "-1");
		this.suspensionCodeLB.addItem("01 - DESPIDO DE LA PERSONA TRABAJADORA", "01");
		this.suspensionCodeLB.addItem("02 - DESPIDO POR CAUSAS OBJETIVAS. AMORTIZACI\u00d3N POR CAUSAS ECON\u00d3MICAS, T\u00c9CNICAS, ORGANIZATIVAS O DE PRODUCCI\u00d3N", "02");
		this.suspensionCodeLB.addItem("03 - MUERTE DEL EMPRESARIO/A", "03");
		this.suspensionCodeLB.addItem("04 - JUBILACION DEL EMPRESARIO/A", "04");
		this.suspensionCodeLB.addItem("05 - INCAPACIDAD DEL EMPRESARIO/A", "05");
		this.suspensionCodeLB.addItem("06 - CESE POR DECLARACI\u00d3N DE GRAN INVALIDEZ, INVALIDEZ PERMANENTE TOTAL O ABSOLUTA DE LA PERSONA TRABAJADORA", "06");
		this.suspensionCodeLB.addItem("07 - CESE EN PERIODO DE PRUEBA A INSTANCIA DEL EMPRESARIO/A", "07");
		this.suspensionCodeLB.addItem("08 - CESE EN PERIODO DE PRUEBA POR ACUERDO DEL CONSEJO RECTOR EN EL SUPUESTO DE SOCIOS DE COOPERATIVAS", "08");
		this.suspensionCodeLB.addItem("09 - CESE EN PERIODO PRUEBA A INSTANCIA DE LA PERSONA TRABAJADORA", "09");
		this.suspensionCodeLB.addItem("10 - CESE POR VOLUNTAD DEL EMPRESARIO/A EN LA RELACI\u00d3N LABORAL DE ALTA DIRECCI\u00d3N", "10");
		this.suspensionCodeLB.addItem("11 - CESE POR EXPIRACI\u00d3N DEL TIEMPO CONVENIDO EN EL CONTRATO DE DURACI\u00d3N DETERMINADA", "11");
		this.suspensionCodeLB.addItem("12 - FIN DE CONTRATO TEMPORAL A INSTANCIA DE LA PERSONA TRABAJADORA (RECHAZO PR\u00d3RROGA)", "12");
		this.suspensionCodeLB.addItem("13 - FIN DE LA RELACION ADMINISTRATIVA TEMPORAL DE FUNCIONARIOS DE EMPLEO Y CONTRATADOS ADMINISTRATIVOS", "13");
		this.suspensionCodeLB.addItem("14 - RESOLUCI\u00d3N DE LA PERSONA TRABAJADORA POR TRASLADO", "14");
		this.suspensionCodeLB.addItem("15 - INTERRUPCI\u00d3N DE LA ACTIVIDAD DE LAS PERSONAS TRABAJADORAS FIJAS-DISCONTINUAS", "15");
		this.suspensionCodeLB.addItem("16 - DESPIDO COLECTIVO O EXTINCI\u00d3N DEL CONTRATO POR ERE", "16");
		this.suspensionCodeLB.addItem("17 - SUSPENSI\u00d3N DEL CONTRATO O ERTE", "17");
		this.suspensionCodeLB.addItem("18 - REDUCCI\u00d3N TEMPORAL DE JORNADA O ERTE", "18");
		this.suspensionCodeLB.addItem("19 - SUSPENSI\u00d3N VOLUNTARIA DE LA RELACI\u00d3N LABORAL. V\u00cdCTIMAS DE VIOLENCIA DE G\u00c9NERO O SEXUAL", "19");
		this.suspensionCodeLB.addItem("20 - EXPULSI\u00d3N DEL SOCIO/A DE LA COOPERATIVA, POR ACUERDO DEL CONSEJO RECTOR", "20");
		this.suspensionCodeLB.addItem("21 - BAJA VOLUNTARIA DE LA PERSONA TRABAJADORA", "21");
		this.suspensionCodeLB.addItem("22 - FINALIZACION O RESOLUCION INVOLUNTARIA DEL COMPROMISO CON LAS FUERZAS(INDICAR ARMADAS, CON O SIN DERECHO A PENSION DE RETIRO)", "22");
		this.suspensionCodeLB.addItem("23 - FIN DE ACTUACION CON FINALIZACION DE CONTRATO, EN EL CASO DE ARTISTAS", "23");
		this.suspensionCodeLB.addItem("25 - FINALIZACION DEL VINCULO SOCIETARIO DE DURACION DETERMINADA, FIJADO EN EL ACUERDO DE ADMISION Y EN LOS ESTATUTOS DE LA COOPERATIVA", "25");
		this.suspensionCodeLB.addItem("26 - EXCEDENCIA", "26");
		this.suspensionCodeLB.addItem("27 - CESE INVOLUNTARIO Y CON CARACTER DEFINITIVO EN CARGO PUBLICO O SINDICAL", "27");
		this.suspensionCodeLB.addItem("28 - PERDIDA CON CARACTER INVOLUNTARIO Y DEFINITIVO DE LA DEDICACION EXCLUSIVA O PARCIAL POR PARTE DE UN CARGO PUBLICO O SINDICAL", "28");
		this.suspensionCodeLB.addItem("29 - CONCLUSI\u00d3N DEL SERVICIO O DEL TIEMPO M\u00c1XIMO COMO RESERVISTA VOLUNTARIO ACTIVADO EN LAS FUERZAS ARMADAS", "29");
		this.suspensionCodeLB.addItem("30 - DESPIDO POR CAUSAS OBJETIVAS. INEPTITUD O FALTA DE ADAPTACI\u00d3N", "30");
		this.suspensionCodeLB.addItem("31 - RESOLUCI\u00d3N DE LA PERSONA TRABAJADORA POR MODIFICACI\u00d3N SUSTANCIAL DE LAS CONDICIONES DE TRABAJO", "31");
		this.suspensionCodeLB.addItem("32 - EXTINCI\u00d3N VOLUNTARIA DE LA RELACI\u00d3N LABORAL. V\u00cdCTIMAS DE VIOLENCIA DE G\u00c9NERO O SEXUAL", "32");
		this.suspensionCodeLB.addItem("33 - RESOLUCI\u00d3N DE LA PERSONA TRABAJADORA POR CAUSA JUSTA", "33");
		this.suspensionCodeLB.addItem("34 - EXTINCI\u00d3N DEL CONTRATO POR MOTIVOS INHERENTES A LA PERSONA TRABAJADORA EN EL SECTOR DE LA CONSTRUCCI\u00d3N", "34");
		this.suspensionCodeLB.addItem("35 - EXTINCI\u00d3N DE LA RELACI\u00d3N LABORAL DE PERSONAS TRABAJADORAS AL SERVICIO DEL HOGAR POR LAS CAUSAS DEL ART. 11.2 RD 1620/2011", "35");
		
		this.suspensionCodeLB.addChangeHandler(e -> {
			hasChange = true;
			certifica2Info.setSuspensionCode(this.suspensionCodeLB.getSelectedValue());
			suspensionReasonCodeHidden.setValue(this.suspensionCodeLB.getSelectedValue());
			suspensionReasonHidden.setValue(this.suspensionCodeLB.getSelectedItemText().split(" - ")[1]);
		});
	}
	
	private void initSuggestBox() {
		List<String> cnoEntry = new ArrayList<>();
		for(Entry<String, CNO> entry : cnoMap.entrySet())
			cnoEntry.add(entry.getKey() + " - " + entry.getValue().getTitle());
		List<String> cnoSuggest = new ArrayList<>();
		for(String cno : cnoEntry)
			cnoSuggest.add(cno);
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) profesionalCategorySB.getSuggestOracle();
		orclIbans.addAll(cnoSuggest);
		
		profesionalCategorySB.setAutoSelectEnabled(true);
		profesionalCategorySB.addSelectionHandler(e -> {
			hasChange = true;
			
			String cnoStr = profesionalCategorySB.getValueBox().getValue();
			String cno = "";
			if(!AonStringUtils.isBlank(cnoStr))
				cno = cnoStr.split(" -")[0];
			
			certifica2Info.setProfesionalCategory(cno);
		});
	}
	
	private void fillFields() {
		this.representativeDocumentL.setText(certifica2Info.getRepresentativeDocument());
		this.representativeNameL.setText(certifica2Info.getRepresentativeName());
		this.representativeSurnameL.setText(certifica2Info.getRepresentativeSurname());
		this.enterpriceDocumentL.setText(certifica2Info.getEnterpriseDocument());
		this.completeCCCL.setText(certifica2Info.getCompleteCCC());
		this.documentL.setText(certifica2Info.getDocument());
		this.fullNameL.setText(certifica2Info.getFullName());
		this.contractTypeL.setText(certifica2Info.getContractType());
		this.quoteGroupL.setText(certifica2Info.getQuoteGroup());
		this.startDateL.setText(dateFormat.format(certifica2Info.getStartDate()));
		this.endDateL.setText(null == certifica2Info.getEndDate() ? "" : dateFormat.format(certifica2Info.getEndDate()));
		this.contractDurationL.setText(certifica2Info.getContractDuration() + " d\u00EDa(s)");
		setSelectedValueLB(this.suspensionCodeLB, certifica2Info.getSuspensionCode());
		CNO cnoObj = cnoMap.get(certifica2Info.getProfesionalCategory());
		if(null != cnoObj)
			profesionalCategorySB.setText(cnoObj.getCode() + " - " + cnoObj.getTitle());
		this.settleQuoteDaysL.setText(certifica2Info.getSettleQuoteDays() + " d\u00EDas");
		this.baseCgcL.setText(certifica2Info.getBaseCgc() + "");
		this.baseUnemploymentL.setText(certifica2Info.getBaseUnemployment() + "");
		
		fillQuoteDataListPanel();
	}
	
	private void fillQuoteDataListPanel() {
		quoteDataListPanel.clear();
		
		if(certifica2Info.getQuoteDataList().isEmpty())
			quoteDataListTitlePanel.setVisible(false);
		
		for(Map<String, String> quoteData : certifica2Info.getQuoteDataList()) {
			HTMLPanel row = new HTMLPanel("");
			row.setStyleName(style.flex());
			
			Label monthL = new Label("Mes: ");
			monthL.addStyleName(style.subTitle());
			Label monthValue = new Label(quoteData.get("monthCtz"));
			
			Label yearL = new Label("A\u00F1o: ");
			yearL.addStyleName(style.subTitle());
			Label yearValue = new Label(quoteData.get("anioCtz"));
			
			Label daysL = new Label("D\u00EDas: ");
			daysL.addStyleName(style.subTitle());
			Label daysValue = new Label(quoteData.get("daysCtz"));
			
			Label cgcL = new Label("CGC: ");
			cgcL.addStyleName(style.subTitle());
			Label cgcValue = new Label(quoteData.get("bccc"));
			
			Label unemploymentL = new Label("Desempleo: ");
			unemploymentL.addStyleName(style.subTitle());
			Label unemploymentValue = new Label(quoteData.get("bcd"));
			
			row.add(monthL);
			row.add(monthValue);
			row.add(yearL);
			row.add(yearValue);
			row.add(daysL);
			row.add(daysValue);
			row.add(cgcL);
			row.add(cgcValue);
			row.add(unemploymentL);
			row.add(unemploymentValue);
			
			quoteDataListPanel.add(row);
		}
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
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
	
	// ------------------------------------------------- ToolbarPanel
	
	private void getToolbarPanel() {
		toolbarPanel.clear();
		AonToolbar toolbar = new AonToolbar("");
		
		AonToolbarSmallButton downloadCertifica2 = new AonToolbarSmallButton("Descargar XML", AON.CSS.aonIconDownload());
		downloadCertifica2.getElement().getStyle().setMarginRight(10, Unit.PX);
		downloadCertifica2.addClickHandler(e -> {
				if(!isSuspensionCode())
					return;
				
				messageL.setText("Generando Certific\u00402...");
				messagesPanel.setVisible(true);
				
				employeesService.generateCertifaca2(contractId, certifica2Info, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						messageL.setText("Descargando XML Certific\u00402...");
						hasChange = false;
						formPanelXML.submit();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Not use here
					}
				});
		});
		
		toolbar.add(downloadCertifica2);
		
		comunicateCertifica2 = new AonToolbarSmallButton("Comunicar Certific\u00402", AON.CSS.aonIconSend());
		comunicateCertifica2.getElement().getStyle().setMarginRight(10, Unit.PX);
		comunicateCertifica2.addClickHandler(e -> {
			if(!isSuspensionCode())
				return;
			
			if(hasChange) {
				messageL.setText("Generando nuevo Certific\u00402...");
				messagesPanel.setVisible(true);
				
				employeesService.generateCertifaca2(contractId, certifica2Info, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						hasChange = false;
						messageL.setText("Comunicando Certific\u00402 al SEPE...");
						messagesPanel.setVisible(true);
						
						employeesService.sendCertifica2(contractId, certifica2Info, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								messagesPanel.setVisible(false);
								comunicateCertifica2PDF.setVisible(true);
								AonDialog dialog = new AonDialog("Comunic@", new HTML("Certific\u00402 comunicado correctamente"));
								dialog.info();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								messagesPanel.setVisible(false);
								comunicateCertifica2PDF.setVisible(false);
								AonDialog dialog = new AonDialog("Error Certifica2", new HTML(caught.getMessage()));
								dialog.warning();
							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {
						// Not use here
					}
				});
			} else {
			
				messageL.setText("Comunicando Certific\u00402 al SEPE...");
				messagesPanel.setVisible(true);
				
				employeesService.sendCertifica2(contractId, certifica2Info, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						messagesPanel.setVisible(false);
						comunicateCertifica2PDF.setVisible(true);
						AonDialog dialog = new AonDialog("Comunic\u0040", new HTML("Certific\u00402 comunicado correctamente. Puede consultar la respuesta en el apartado de Documentos"));
						dialog.info();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						messagesPanel.setVisible(false);
						comunicateCertifica2PDF.setVisible(false);
						AonDialog dialog = new AonDialog("Error Certific\u00402", new HTML(caught.getMessage()));
						dialog.warning();
					}
				});
			}
		});
		
		toolbar.add(comunicateCertifica2);
		
		manualCertifica2PDF = new AonToolbarSmallButton("Certific\u00402 PDF (Manual)", AON.CSS.aonIconPdf());
		manualCertifica2PDF.addClickHandler(e -> {
			
			messageL.setText("Generando Certific\u00402 PDF...");
			messagesPanel.setVisible(true);
			
			formPanelManual.submit();
		});
		
		toolbar.add(manualCertifica2PDF);
		
		comunicateCertifica2PDF = new AonToolbarSmallButton("Certific\u00402 PDF", AON.CSS.aonIconPdf());
		comunicateCertifica2PDF.setVisible(false);
		comunicateCertifica2PDF.addClickHandler(e -> {
			
			messageL.setText("Obteniendo Certific\u00402 PDF del SEPE...");
			messagesPanel.setVisible(true);
			
			formPanelPDF.submit();
		});
		
		toolbar.add(comunicateCertifica2PDF);
		
		createFormXMLPanel();
		toolbar.add(formPanelXML);
		
		createFormPDFPanel();
		toolbar.add(formPanelPDF);
		
		createFormManualPanel();
		toolbar.add(formPanelManual);
		
		toolbarPanel.add(toolbar);
	}

	private boolean isSuspensionCode() {
		if(suspensionCodeLB.getSelectedIndex() == 0) {
			AonDialog dialog = new AonDialog("Certific\u00402", new HTML("El c\u00F3digo de suspensi\u00F3n es obligatorio"));
			dialog.warning();
			return false;
		}
		return true;
	}

	private void createFormXMLPanel() {
		// Create Form Panel
		formPanelXML = new FormPanel();
		formPanelXML.setAction(GWT.getModuleBaseURL()+ "certifica2/");
		formPanelXML.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanelXML.setMethod(FormPanel.METHOD_POST);
		
		Hidden userLoginHidden = new Hidden("userLogin", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden contractIdHidden = new Hidden("contractId", contractId.toString());
		Hidden fileTypeHidden = new Hidden("type", "XML");
		documentHidden = new Hidden("document", "");
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(contractIdHidden);
		flowPanel.add(fileTypeHidden);
		flowPanel.add(documentHidden);
		
		formPanelXML.add(flowPanel);
		formPanelXML.addSubmitCompleteHandler(e -> messagesPanel.setVisible(false));
	}
	
	private void createFormPDFPanel() {
		// Create Form Panel
		formPanelPDF = new FormPanel();
		formPanelPDF.setAction(GWT.getModuleBaseURL()+ "certifica2/");
		formPanelPDF.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanelPDF.setMethod(FormPanel.METHOD_POST);
		
		Hidden userLoginHidden = new Hidden("userLogin", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden contractIdHidden = new Hidden("contractId", contractId.toString());
		Hidden fileTypeHidden = new Hidden("type", "PDF");
		documentHidden = new Hidden("document", "");
		endDateHidden = new Hidden("endDate", "");
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(contractIdHidden);
		flowPanel.add(fileTypeHidden);
		flowPanel.add(documentHidden);
		flowPanel.add(endDateHidden);
		
		formPanelPDF.add(flowPanel);
		formPanelPDF.addSubmitCompleteHandler(e -> messagesPanel.setVisible(false));
	}
	
	private void createFormManualPanel() {
		// Create Form Panel
		formPanelManual = new FormPanel();
		formPanelManual.setAction(GWT.getModuleBaseURL()+ "certifica2/");
		formPanelManual.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanelManual.setMethod(FormPanel.METHOD_POST);
		
		Hidden userLoginHidden = new Hidden("userLogin", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden contractIdHidden = new Hidden("contractId", contractId.toString());
		Hidden fileTypeHidden = new Hidden("type", "MANUAL");
		documentHidden = new Hidden("document", "");
		endDateHidden = new Hidden("endDate", "");
		suspensionReasonCodeHidden = new Hidden("suspensionReasonCode", "");
		suspensionReasonHidden = new Hidden("suspensionReason", "");
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(contractIdHidden);
		flowPanel.add(fileTypeHidden);
		flowPanel.add(documentHidden);
		flowPanel.add(endDateHidden);
		flowPanel.add(suspensionReasonCodeHidden);
		flowPanel.add(suspensionReasonHidden);
		
		formPanelManual.add(flowPanel);
		formPanelManual.addSubmitCompleteHandler(e -> messagesPanel.setVisible(false));
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
	}
	
	// ------------------------------------------------- LoadingPanel
	
	private void initLoadingPanel() {
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		loadingPanel.add(loadingBtn);
		
		messagesPanel.setVisible(false);
	}
	
}
