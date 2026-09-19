package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomCard;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDateBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDockLayout;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomSuggestBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessagePanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class Certifica2WidgetDialog extends AonCustomDialog {
	
	private AonCustomDockLayout dockLayout;
	
	private HTMLPanel container = new HTMLPanel("");
	private HTMLPanel messagePanel = new HTMLPanel("");
	
	private ScrollPanel scrollPanel = new ScrollPanel();
	
	private AonCustomCard representativeCard = new AonCustomCard("Datos Representante");
	private AonCustomTextBox representativeDocument = new AonCustomTextBox("Documento");
	private AonCustomTextBox representativeName = new AonCustomTextBox("Nombre");
	private AonCustomTextBox representativeSurname = new AonCustomTextBox("Apellidos");
	
	private AonCustomCard enterpriseCard = new AonCustomCard("Datos Empresa/Actividad");
	private AonCustomTextBox enterpriceDocument = new AonCustomTextBox("C.I.F.");
	private AonCustomTextBox completeCCC = new AonCustomTextBox("C.C.C.");
	
	private AonCustomCard contractCard = new AonCustomCard("Datos Contrato/Trabajador");
	private AonCustomTextBox document = new AonCustomTextBox("Documento");
	private AonCustomTextBox fullName = new AonCustomTextBox("Trabajador");
	private AonCustomTextBox contractType = new AonCustomTextBox("TC2");
	private AonCustomTextBox quoteGroup = new AonCustomTextBox("Grupo Cotizaci\u00f3n");
	private AonCustomTextBox startDate = new AonCustomTextBox("F. Inicio");
	private AonCustomTextBox endDate = new AonCustomTextBox("F. Fin");
	private AonCustomTextBox contractDuration = new AonCustomTextBox("Duraci\u00f3n Contrato (D\u00edas)");
	
	private HTMLPanel ertePanel = new HTMLPanel("");
	private AonCustomTextBox erteCodeTB = new AonCustomTextBox("C\u00f3digo ERTE");
	private AonCustomTextBox erteCoefTB = new AonCustomTextBox("Red. Jor. ERTE (%)");
	private AonCustomDateBox erteEnd = new AonCustomDateBox("F. Fin ERTE");
	
	private AonCustomListBox suspensionCodeLB = new AonCustomListBox("C\u00f3digo Suspensi\u00f3n");
	private AonCustomSuggestBox profesionalCategorySB = new AonCustomSuggestBox("CNO");
	
	private AonCustomTextBox settleQuoteDays = new AonCustomTextBox("Dias Cotizaci\u00f3n (Finiquito)");
	private AonCustomTextBox baseCgc = new AonCustomTextBox("Bases CGC (Finiquito)");
	private AonCustomTextBox baseUnemployment = new AonCustomTextBox("Bases Desempleo (Finiquito)");
	
	private AonCustomCard economicCard = new AonCustomCard("Tramos Certfic@2");
	
	private HTMLPanel buttonsPanel = new HTMLPanel("");

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
	private Hidden ereCodeHidden;
	private Hidden ereEndHidden;
	
	private Map<String, CNO> cnoMap;
	
	private boolean hasChange = false;
	
	// ------------------------------------------------- Constructor
	
	private static enum COLUMNS {
		  MON("Mes"									,"5rem"  			,"")
		, YEA("A\u00f1o"							,"5rem"  			,"")
		, DAY("D\u00edas"							,"5rem"  			,"")
		, CGC("C.G.C."								,"10rem"  			,"text-align: right;")
		, UNE("Desempleo"							,"10rem"  			,"text-align: right;")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;

		private COLUMNS(String headerLabel,String colWidth,String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	// ------------------------------------------------- Constructor
	
	public Certifica2WidgetDialog(Integer contractId) {
		
		setCaption("Certific\u00402");
		setWidth("60rem");
		
		
		this.contractId = contractId;
		this.cnoMap = new HashMap<>();
		
		dockLayout = new AonCustomDockLayout("Certific@2 Empresa", false) {
			
			@Override
			protected void onClearFilter() {}
		};
		
		dockLayout.setHeight("40rem");
		
		getDurAndCNOs(end -> {
			createToolbar();
			initializeView();
			getButtonsPanel();
			
			dockLayout.add(container);
			setWidget(dockLayout);
			
			showDialog();
		});
	}
	
	private void initializeView() {
		container.addStyleName(AON.CSS.aonFlexColumn());
		container.getElement().getStyle().setProperty("padding", "1rem");
		
		container.add(messagePanel);
		
		scrollPanel.setHeight("100%");
		
		HTMLPanel scrollContent = new HTMLPanel("");
		scrollContent.addStyleName(AON.CSS.aonFlexColumn());
		
		HTMLPanel row = new HTMLPanel("");
		row.addStyleName(AON.CSS.aonItemFlex());
		
		FlowPanel representativeCardTable = createFlexColumnPanel();
		representativeDocument.setValue(certifica2Info.getRepresentativeDocument());
		representativeDocument.setEnable(false);
		representativeName.setValue(certifica2Info.getRepresentativeName());
		representativeName.setEnable(false);
		representativeSurname.setValue(certifica2Info.getRepresentativeSurname());
		representativeSurname.setEnable(false);
		representativeCardTable.add(createRow(representativeDocument, representativeName, representativeSurname));
		representativeCard.add(representativeCardTable);
		row.add(representativeCard);
		
		FlowPanel enterpriseCardTable = createFlexColumnPanel();
		enterpriceDocument.setValue(certifica2Info.getEnterpriseDocument());
		enterpriceDocument.setEnable(false);
		completeCCC.setValue(certifica2Info.getCompleteCCC());
		completeCCC.setEnable(false);
		enterpriseCardTable.add(createRow(enterpriceDocument, completeCCC, null));
		enterpriseCard.add(enterpriseCardTable);
		enterpriseCard.setWidth("100%");
		row.add(enterpriseCard);
		
		scrollContent.add(row);
		
		FlowPanel contractCardTable = createFlexColumnPanel();
		document.setValue(certifica2Info.getDocument());
		document.setEnable(false);
		document.getElement().getStyle().setProperty("max-width", "10rem");
		fullName.setValue(certifica2Info.getFullName());
		fullName.setEnable(false);
		contractType.setValue(certifica2Info.getContractType());
		contractType.setEnable(false);
		contractType.getElement().getStyle().setProperty("max-width", "10rem");
		quoteGroup.setValue(certifica2Info.getQuoteGroup());
		quoteGroup.setEnable(false);
		quoteGroup.getElement().getStyle().setProperty("max-width", "10rem");
		contractCardTable.add(createRow(document, fullName, contractType, quoteGroup));
		startDate.setValue(dateFormat.format(certifica2Info.getStartDate()));
		startDate.setEnable(false);
		endDate.setValue(null == certifica2Info.getEndDate() ? "" : dateFormat.format(certifica2Info.getEndDate()));
		endDate.setEnable(false);
		contractDuration.setValue(null == certifica2Info.getContractDuration() ? "N/D" : certifica2Info.getContractDuration().toString());
		contractDuration.setEnable(false);
		contractCardTable.add(createRow(startDate, endDate, contractDuration));
		iniSuspensionCodeLB();
		suspensionCodeLB.setValue(certifica2Info.getSuspensionCode());
		contractCardTable.add(createRow(suspensionCodeLB, null, null));
		ertePanel.addStyleName(AON.CSS.aonItemFlex());
		ertePanel.setWidth("100%");
		erteCodeTB.addValueChangeHandler(e -> {
			certifica2Info.setErteCode(e.getValue());
			ereCodeHidden.setValue(e.getValue());
		});
		erteEnd.addValueChangeHandler(e -> {
			certifica2Info.setErteEnd(e.getValue());
			ereEndHidden.setValue(null == erteEnd.getValue() ? "" : dateFormat.format(erteEnd.getValue()));
		});
		ertePanel.add(erteCodeTB);
		ertePanel.add(erteCoefTB);
		ertePanel.add(erteEnd);
		if(AonStringUtils.equalsIgnoreCase(certifica2Info.getSuspensionCode(),"16") || AonStringUtils.equalsIgnoreCase(certifica2Info.getSuspensionCode(),"17") || AonStringUtils.equalsIgnoreCase(certifica2Info.getSuspensionCode(),"18")) {
			this.ertePanel.getElement().getStyle().clearDisplay();
			this.erteCoefTB.setValue(certifica2Info.getErteCoef());
			this.erteEnd.setValue(null == certifica2Info.getErteEnd() ? null : certifica2Info.getErteEnd());
		} else this.ertePanel.getElement().getStyle().setDisplay(Display.NONE);
		contractCardTable.add(createRow(ertePanel, null, null));
		initializeCNOSuggestions();
		CNO cnoObj = cnoMap.get(certifica2Info.getProfesionalCategory());
		if(null != cnoObj) profesionalCategorySB.setValue(cnoObj.getCode() + " - " + cnoObj.getTitle());
		contractCardTable.add(createRow(profesionalCategorySB, null, null));
		
		settleQuoteDays.setValue(null == certifica2Info.getSettleQuoteDays() ? "N/D" : certifica2Info.getSettleQuoteDays().toString());
		settleQuoteDays.setEnable(false);
		baseCgc.setValue(null == certifica2Info.getBaseCgc() ? "0.00" : certifica2Info.getBaseCgc().toString());
		baseCgc.setEnable(false);
		baseUnemployment.setValue(null == certifica2Info.getBaseUnemployment() ? "0.00" : certifica2Info.getBaseUnemployment().toString());
		baseUnemployment.setEnable(false);
		contractCardTable.add(createRow(settleQuoteDays, baseCgc, baseUnemployment));
		contractCard.add(contractCardTable);
		scrollContent.add(contractCard);
		
		if(!certifica2Info.getQuoteDataList().isEmpty()) {
			fillQuoteDataListPanel();
			scrollContent.add(economicCard);
		} else {
			AonMessagePanel.showError(messagePanel, "No existen datos econ\u00f3micos. Revise que existan n\u00f3minas en los \u00faltimos 180 d\u00edas");
		}
		
		scrollPanel.setWidget(scrollContent);
		container.add(scrollPanel);
	}
	
	private void fillQuoteDataListPanel() {
		AonCustomTable quoteTable = new AonCustomTable();
		
		quoteTable.createHeader();
		for ( COLUMNS col : COLUMNS.values()) 
			quoteTable.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getCellStyleClass());
		
		for(Map<String, String> quoteData : certifica2Info.getQuoteDataList()) {
			
			HTMLPanel row = quoteTable.createRow();
			
			quoteTable.addRow(row, new Label(quoteData.get("monthCtz")), COLUMNS.MON.getColWidth());
			quoteTable.addRow(row, new Label(quoteData.get("anioCtz")), COLUMNS.YEA.getColWidth());
			quoteTable.addRow(row, new Label(quoteData.get("daysCtz")), COLUMNS.DAY.getColWidth());
			
			Label cgcLabel = new Label(quoteData.get("bccc"));
			quoteTable.addInlineStyle(cgcLabel, COLUMNS.CGC.getCellStyleClass());
			quoteTable.addRow(row, cgcLabel, COLUMNS.CGC.getColWidth());
			
			Label unemploymentcabel = new Label(quoteData.get("bcd"));
			quoteTable.addInlineStyle(unemploymentcabel, COLUMNS.UNE.getCellStyleClass());
			quoteTable.addRow(row, unemploymentcabel, COLUMNS.UNE.getColWidth());
		}
		
		economicCard.add(quoteTable);
	}
	
	private void iniSuspensionCodeLB() {
		this.suspensionCodeLB.clearItems();
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
			certifica2Info.setSuspensionCode(this.suspensionCodeLB.getValue());
			suspensionReasonCodeHidden.setValue(this.suspensionCodeLB.getValue());
			suspensionReasonHidden.setValue(this.suspensionCodeLB.getListBox().getSelectedItemText().split(" - ")[1]);
			
			if(AonStringUtils.equalsIgnoreCase(this.suspensionCodeLB.getValue(),"16") || AonStringUtils.equalsIgnoreCase(this.suspensionCodeLB.getValue(),"17") || AonStringUtils.equalsIgnoreCase(this.suspensionCodeLB.getValue(),"18"))
				this.ertePanel.getElement().getStyle().clearDisplay();
			else
				this.ertePanel.getElement().getStyle().setDisplay(Display.NONE);
		});
	}

	private FlowPanel createFlexColumnPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.addStyleName(AON.CSS.aonFlexColumn());
        panel.setWidth("100%");
        return panel;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2, Widget widget3) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) row.add(widget2);
        if (widget3 != null) row.add(widget3);
        return row;
    }
	
	private FlowPanel createRow(Widget widget1, Widget widget2, Widget widget3, Widget widget4) {
        FlowPanel row = createFlexPanel();
        row.add(widget1);
        if (widget2 != null) row.add(widget2);
        if (widget3 != null) row.add(widget3);
        if (widget4 != null) row.add(widget4);
        return row;
    }
	
	private FlowPanel createFlexPanel() {
        FlowPanel panel = new FlowPanel();
        panel.addStyleName(AON.CSS.aonItemFlex());
        panel.setWidth("100%");
        return panel;
    }

	private void createToolbar() {
		AonToolbarSmallButton downloadCertifica2 = new AonToolbarSmallButton("Descargar XML", AON.CSS.aonIconDownload());
		downloadCertifica2.addClickHandler(e -> {
				if(!isSuspensionCode())
					return;
				
				AonMessagePanel.showLoading(messagePanel, "Generando Certific\u00402 XML...");
				
				employeesService.generateCertifaca2(contractId, certifica2Info, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						AonMessagePanel.showLoading(messagePanel, "Descargando XML Certific\u00402...");
						hasChange = false;
						formPanelXML.submit();
					}
					
					@Override
					public void onFailure(Throwable caught) {}
					
				});
		});
		
		dockLayout.addToolbarButton(downloadCertifica2);
		
		comunicateCertifica2 = new AonToolbarSmallButton("Comunicar Certific\u00402", AON.CSS.aonIconSend());
		comunicateCertifica2.setVisible(userRoles.isComunica());
		comunicateCertifica2.addClickHandler(e -> {
			if(!isSuspensionCode())
				return;
			
			if(hasChange) {
				AonMessagePanel.showLoading(messagePanel, "Generando nuevo Certific\u00402...");
				
				employeesService.generateCertifaca2(contractId, certifica2Info, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						hasChange = false;
						
						AonMessagePanel.showLoading(messagePanel, "Comunicando Certific\u00402 al SEPE...");
						
						employeesService.sendCertifica2(contractId, certifica2Info, new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								comunicateCertifica2PDF.setVisible(true);
								AonMessagePanel.showSuccess(messagePanel, "Certific\u00402 comunicado correctamente");
							}
							
							@Override
							public void onFailure(Throwable caught) {
								comunicateCertifica2PDF.setVisible(false);
								AonMessagePanel.showError(messagePanel, "Error comunicaci\u00f3n : " + caught.getMessage());
							}
						});
					}
					
					@Override
					public void onFailure(Throwable caught) {}
				});
				
			} else {
			
				AonMessagePanel.showLoading(messagePanel, "Comunicando Certific\u00402 al SEPE...");
				
				employeesService.sendCertifica2(contractId, certifica2Info, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						comunicateCertifica2PDF.setVisible(true);
						AonMessagePanel.showSuccess(messagePanel, "Certific\u00402 comunicado correctamente. Puede consultar la respuesta en el apartado de Documentos");
					}
					
					@Override
					public void onFailure(Throwable caught) {
						comunicateCertifica2PDF.setVisible(false);
						AonMessagePanel.showError(messagePanel, "Error comunicaci\u00f3n : " + caught.getMessage());
					}
				});
			}
		});
		
		dockLayout.addToolbarButton(comunicateCertifica2);
		
		manualCertifica2PDF = new AonToolbarSmallButton("Certific\u00402 PDF (Manual)", AON.CSS.aonIconPdf());
		manualCertifica2PDF.addClickHandler(e -> {
			
			AonMessagePanel.showLoading(messagePanel, "Generando Certific\u00402 PDF...");
			
			suspensionReasonCodeHidden.setValue(this.suspensionCodeLB.getValue());
			suspensionReasonHidden.setValue(this.suspensionCodeLB.getListBox().getSelectedItemText().split(" - ")[1]);
			ereCodeHidden.setValue(this.erteCodeTB.getValue());
			ereEndHidden.setValue(null == erteEnd.getValue() ? "" : dateFormat.format(erteEnd.getValue()));
			
			formPanelManual.submit();
		});
		
		dockLayout.addToolbarButton(manualCertifica2PDF);
		
		comunicateCertifica2PDF = new AonToolbarSmallButton("Certific\u00402 PDF", AON.CSS.aonIconPdf());
		comunicateCertifica2PDF.setVisible(false);
		comunicateCertifica2PDF.addClickHandler(e -> {
			
			AonMessagePanel.showLoading(messagePanel, "Obteniendo Certific\u00402 PDF del SEPE...");
			
			suspensionReasonCodeHidden.setValue(this.suspensionCodeLB.getValue());
			ereCodeHidden.setValue(this.erteCodeTB.getValue());
			ereEndHidden.setValue(null == erteEnd.getValue() ? "" : dateFormat.format(erteEnd.getValue()));
			
			formPanelPDF.submit();
		});
		
		dockLayout.addToolbarButton(comunicateCertifica2PDF);
		
		createFormXMLPanel();
		dockLayout.addToolbarButton(formPanelXML);
		
		createFormPDFPanel();
		dockLayout.addToolbarButton(formPanelPDF);
		
		createFormManualPanel();
		dockLayout.addToolbarButton(formPanelManual);
		
		documentHidden.setValue(certifica2Info.getDocument());
		endDateHidden.setValue(dateFormat.format(certifica2Info.getEndDate()));
	}
	
	private void initializeCNOSuggestions() {
		List<String> cnoEntries = new ArrayList<String>();
		cnoMap.entrySet().forEach(entry -> cnoEntries.add(entry.getKey() + " - " + entry.getValue().getTitle()));
		
		List<String> cnoSuggest = new ArrayList<>();
		for(String cnoStr : cnoEntries) cnoSuggest.add(cnoStr);
		
		MultiWordSuggestOracle orclIbans = (MultiWordSuggestOracle) profesionalCategorySB.getSuggestBox().getSuggestOracle();
		orclIbans.addAll(cnoSuggest);
		profesionalCategorySB.setAutoSelectEnabled(true);
		
		profesionalCategorySB.getSuggestBox().addSelectionHandler(e -> {
			hasChange = true;
			certifica2Info.setProfesionalCategory(AonStringUtils.isBlank(profesionalCategorySB.getValue()) ? "" : profesionalCategorySB.getValue().split(" -")[0]);
		});
	}

	private void getDurAndCNOs(Consumer<Void> end){
		enterprisesService.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				userRoles = result;
				
				enterprisesService.getCNOs(new AsyncCallback<Map<String, CNO>>() {

					@Override
					public void onFailure(Throwable caught) {
						// Not user here
					}

					@Override
					public void onSuccess(Map<String, CNO> result) {
						cnoMap = result;
						
						employeesService.getCertifica2Info(contractId, new AsyncCallback<Certifica2Info>() {

							@Override
							public void onFailure(Throwable caught) {
								AonDialog dialog = new AonDialog("Error CNOs", new HTML(caught.getMessage()));
								dialog.warning();
							}

							@Override
							public void onSuccess(Certifica2Info certifica2InfoDB) {
								certifica2Info = certifica2InfoDB;
								end.accept(null);
							}
						});
					}});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonDialog dialog = new AonDialog("Error Dur", new HTML(caught.getMessage()));
				dialog.warning();
			}
			
		});
	}

	// ------------------------------------------------- Auxiliar Methods
	
	public void showDialog() {
		// Show center
		Scheduler.get().scheduleDeferred(() -> {
			center();
			show();
		});
	}

	private boolean isSuspensionCode() {
		if(suspensionCodeLB.getListBox().getSelectedIndex() == 0) {
			AonMessagePanel.showWarning(messagePanel, "El c\u00F3digo de suspensi\u00F3n es obligatorio");
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
		suspensionReasonCodeHidden = new Hidden("suspensionReasonCode", "");
		ereCodeHidden = new Hidden("ereCode", "");
		ereEndHidden = new Hidden("ereEnd", "");
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(contractIdHidden);
		flowPanel.add(fileTypeHidden);
		flowPanel.add(documentHidden);
		flowPanel.add(suspensionReasonCodeHidden);
		flowPanel.add(ereCodeHidden);
		flowPanel.add(ereEndHidden);
		
		formPanelXML.add(flowPanel);
		formPanelXML.addSubmitCompleteHandler(e -> AonMessagePanel.hideMessage(messagePanel));
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
		suspensionReasonCodeHidden = new Hidden("suspensionReasonCode", "");
		ereCodeHidden = new Hidden("ereCode", "");
		ereEndHidden = new Hidden("ereEnd", "");
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(contractIdHidden);
		flowPanel.add(fileTypeHidden);
		flowPanel.add(documentHidden);
		flowPanel.add(endDateHidden);
		flowPanel.add(suspensionReasonCodeHidden);
		flowPanel.add(ereCodeHidden);
		flowPanel.add(ereEndHidden);
		
		formPanelPDF.add(flowPanel);
		formPanelPDF.addSubmitCompleteHandler(e -> AonMessagePanel.hideMessage(messagePanel));
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
		ereCodeHidden = new Hidden("ereCode", "");
		ereEndHidden = new Hidden("ereEnd", "");
		
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
		flowPanel.add(ereCodeHidden);
		flowPanel.add(ereEndHidden);
		
		formPanelManual.add(flowPanel);
		formPanelManual.addSubmitCompleteHandler(e -> AonMessagePanel.hideMessage(messagePanel));
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		buttonsPanel.addStyleName(AON.CSS.aonDisplayFlexEnd());
		buttonsPanel.setWidth("100%");
		
		Button closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> hide());
		
		buttonsPanel.add(closeBtnDialog);
		container.add(buttonsPanel);
	}
	
}
