package com.esferalia.aon.gwt.payroll.client;

import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbar;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonToolbarSmallButton;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.gwt.pdfjs.client.Viewer;

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
	Label suspensionCodeL;
	
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
	
	private AonToolbar toolbar;
	private AonToolbarSmallButton downloadCertifica2;
	private AonToolbarSmallButton comunicateCertifica2;
	private AonToolbarSmallButton comunicateCertifica2PDF;
	
	private Button closeBtnDialog;

	private DomainUserRoles userRoles;
	private Integer contractId;
	private Certifica2Info certifica2Info;
	
	private Viewer pdfViewer;
	private FormPanel formPanel;
	private Hidden documentHidden;
	
	// ------------------------------------------------- Constructor
	
	public Certifica2Dialog(Integer contractId) {
		
		setCaption("Certifica2");
		
		setWidget(binder.createAndBindUi(this));
		
		this.contractId = contractId;
		this.pdfViewer = new Viewer();
		
		getToolbarPanel();
		getButtonsPanel();
		initLoadingPanel();
		
		enterprisesService.getDomainUserRoles(new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				userRoles = result;
				
				if(!userRoles.isComunica())
					comunicateCertifica2.setVisible(false);
				
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
						fillFields();
						showDialog();
					}
				});
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonDialog dialog = new AonDialog("Error", new HTML(caught.getMessage()));
				dialog.warning();
			}
			
		});
	}
	
	// ------------------------------------------------- Constructor Methods
	
	private void fillFields() {
		this.enterpriceDocumentL.setText(certifica2Info.getEnterpriseDocument());
		this.completeCCCL.setText(certifica2Info.getCompleteCCC());
		this.documentL.setText(certifica2Info.getDocument());
		this.fullNameL.setText(certifica2Info.getFullName());
		this.contractTypeL.setText(certifica2Info.getContractType());
		this.quoteGroupL.setText(certifica2Info.getQuoteGroup());
		this.startDateL.setText(dateFormat.format(certifica2Info.getStartDate()));
		this.endDateL.setText(null == certifica2Info.getEndDate() ? "" : dateFormat.format(certifica2Info.getEndDate()));
		this.contractDurationL.setText(certifica2Info.getContractDuration() + " d\u00EDa(s)");
		this.suspensionCodeL.setText(certifica2Info.getSuspensionCode());
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
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				center();
				show();
			}
		});
	}
	
	// ------------------------------------------------- ToolbarPanel
	
	private void getToolbarPanel() {
		toolbarPanel.clear();
		toolbar = new AonToolbar("");
		
		downloadCertifica2 = new AonToolbarSmallButton("Descargar XML", AON.CSS.aonIconDownload());
		downloadCertifica2.getElement().getStyle().setMarginRight(10, Unit.PX);
		downloadCertifica2.addClickHandler(e -> {
//			String certifica2URL = GWT.getModuleBaseURL()+ "/certifica2/"
//		            + "?currentUser=" + Wnd.getCurrentUser()
//					+ "&currentDomain=" +  Wnd.getCurrentDomainNameURL()
//					+ "&token=" + Wnd.getToken()
//					+ "&contractId=" + contractId
//					+ "&document=" + certifica2Info.getDocument();
//			
//			Window.open(certifica2URL, "_blank", null);
			
			formPanel.submit();
		});
		
		toolbar.add(downloadCertifica2);
		
		comunicateCertifica2 = new AonToolbarSmallButton("Comunicar Certifica2", AON.CSS.aonIconSend());
		comunicateCertifica2.getElement().getStyle().setMarginRight(10, Unit.PX);
		comunicateCertifica2.addClickHandler(e -> {
			
			messageL.setText("Comunicando Certifica2 al SEPE...");
			messagesPanel.setVisible(true);
			
			employeesService.sendCertifica2(contractId, new AsyncCallback<Void>() {
				
				@Override
				public void onSuccess(Void result) {
					messagesPanel.setVisible(false);
					comunicateCertifica2PDF.setVisible(true);
					AonDialog dialog = new AonDialog("Comunic@", new HTML("Certifica2 comunicado correctamente"));
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
		});
		
		toolbar.add(comunicateCertifica2);
		
		comunicateCertifica2PDF = new AonToolbarSmallButton("Certifica2 PDF", AON.CSS.aonIconPdf());
		comunicateCertifica2PDF.setVisible(false);
		comunicateCertifica2PDF.addClickHandler(e -> {
			
			messageL.setText("Obteniendo Certifica2 PDF del SEPE...");
			messagesPanel.setVisible(true);
			
			employeesService.getCertifica2PDF(certifica2Info.getDocument(), certifica2Info.getEndDate(), new AsyncCallback<String>() {
				
				@Override
				public void onSuccess(String dataURI) {
					messagesPanel.setVisible(false);
					pdfViewer.setDocument(dataURI, 135/ 100.00);
					String fileName = "Certifica2_" + certifica2Info.getDocument() + ".pdf";
					pdfViewer.download(fileName);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					messagesPanel.setVisible(false);
					AonDialog dialog = new AonDialog("Error Certifica2 PDF", new HTML(caught.getMessage()));
					dialog.warning();
				}
			});
		});
		
		toolbar.add(comunicateCertifica2PDF);
		
		createFormPanel();
		toolbar.add(formPanel);
		
		toolbarPanel.add(toolbar);
	}

	private void createFormPanel() {
		// Create Form Panel
		formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL()+ "certifica2/");
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		
		Hidden userLoginHidden = new Hidden("currentUser", Wnd.getCurrentUser());
		Hidden currentDomainHidden = new Hidden("currentDomain", Wnd.getCurrentDomainNameURL());
		Hidden tokenHidden = new Hidden("token", Wnd.getToken());
		Hidden contractIdHidden = new Hidden("contractId", contractId.toString());
		documentHidden = new Hidden("document", "");
		
		//Add all to FlowPanel to add to FormPanel
		FlowPanel flowPanel = new FlowPanel();
				
		flowPanel.add(userLoginHidden);
		flowPanel.add(currentDomainHidden);
		flowPanel.add(tokenHidden);
		flowPanel.add(contractIdHidden);
		flowPanel.add(documentHidden);
		
		formPanel.add(flowPanel);
	}

	// ------------------------------------------------- ButtonsPanel
	
	private void getButtonsPanel() {
		closeBtnDialog = new Button();
		closeBtnDialog.setStyleName(AON.CSS.aonCancelButtonSmall());
		closeBtnDialog.setText("Cerrar");
		closeBtnDialog.addClickHandler(e -> {
			onCloseDialog(e);
		});
		
		buttonsPanel.add(closeBtnDialog);
	}
	
	private void onCloseDialog(ClickEvent event) {
		hide();
	}
	
	// ------------------------------------------------- LoadingPanel
	
	private void initLoadingPanel() {
		AonTableButton loadingBtn = new AonTableButton("", AON.CSS.aonIconRenew());
		loadingBtn.addStyleName(style.loadingPanel());
		loadingPanel.add(loadingBtn);
		
		messagesPanel.setVisible(false);
	}
	
}
