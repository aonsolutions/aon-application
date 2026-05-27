package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCertificateDialog.AonCerticateDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDialog.AonAcceptDialogCallback;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateOwner;
import com.esferalia.aon.occam.api.model.Certificate.CertificateSecurity;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.EmployeeSegSocial;
import com.esferalia.aon.occam.api.model.SecondaryUserCertificate;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class AonMainCertificatesPanel extends DeckPanel {

	// ------------------------------------------------- CommonServiceAsync

	static CommonServiceAsync commonService;

	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}
	
	// ------------------------------------------------- Variables

	private AonCustomDockLayout pdfDockLayoutPanel;

	private HTMLPanel container;
	private HTMLPanel messagePanel = new HTMLPanel("");

	private AonCustomTable tab;

	private FullViewer fullViewer;

	private DateTimeFormat formatFullDate = DateTimeFormat.getFormat("dd/MM/yyyy");

	private Integer domainId;
	private String domainName;
	private String user;
	
	private List<Certificate> certificateList = new ArrayList<>();
	private List<SecondaryUserCertificate> secondaryUsers = new ArrayList<>();
	
	private static enum COLS {
		TPE("Uso", "3rem",  "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DES("Titular", "-moz-available", "min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		BUD("Representaci\u00f3n", "10rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		DOC("F. Expiraci\u00f3n", "6rem", ""),
		TYP(AON.MSG.alias(), "14rem", "white-space: nowrap; overflow: hidden; text-overflow: ellipsis;"),
		TGS("TGSS", "3rem", ""), 
		SEP("SEPE", "3rem", ""), 
		AEA("AEAT", "3rem", ""),
		BUT(AonStringUtils.EMPTY, "10rem", "");

		String headerLabel;
		String colWidth;
		String styles;

		private COLS(String headerLabel, String colWidth, String styles) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.styles = styles;
		}

		public String getColWidth() {
			return colWidth;
		}

		public String getHeaderLabel() {
			return headerLabel;
		}

		public String getStyles() {
			return styles;
		}
	}

	// ------------------------------------------------------ Constructor

	public AonMainCertificatesPanel(String domainName, Integer domainId, String user) {
		this.domainName = domainName;
		this.domainId = domainId;
		this.user = user;
		
		initializeCommonService();

		this.pdfDockLayoutPanel = new AonCustomDockLayout("Usuarios Secundarios") {
			@Override
			protected void onClearFilter() {}
		};

		container = new HTMLPanel("");
		container.addStyleName(AON.CSS.aonFlexColumn2());

		container.add(messagePanel);
		AonMessagePanel.hideMessage(messagePanel);

		initCertDataTable();

		fullViewer = new FullViewer();
		this.pdfDockLayoutPanel.add(fullViewer);

		addPDFButtonsToolbar();

		add(this.container);
		add(this.pdfDockLayoutPanel);
		showWidget(0);
		
		onModuleLoad();
	}

	// ------------------------------------------------------ Toolbar

	private void addPDFButtonsToolbar() {
		AonToolbarButton backButton = new AonToolbarButton("Volver", AON.CSS.aonIconBack());
		backButton.addClickHandler(e -> showWidget(0));

		this.pdfDockLayoutPanel.addToolbarButton(backButton);
		this.pdfDockLayoutPanel.hideSearchWidget();
	}

	// ------------------------------------------------------ Init Preview (Tables TGSS & SEPE)

	private void initCertDataTable() {
		tab = new AonCustomTable();
		tab.setMaxHeight((Window.getClientHeight() - Window.getClientHeight() / 3) + "px");
		tab.getElement().getStyle().setProperty("padding", "1rem 0");
		ScrollPanel scrollPanel = new ScrollPanel(tab);

		paintHeader();
		container.add(scrollPanel);
	}
	
	private void paintHeader() {
		tab.createHeader();
		for ( COLS col : COLS.values()) {
			if(col.equals(COLS.BUT)) {
				FlowPanel buttonContainer = new FlowPanel();
				buttonContainer.getElement().getStyle().setTextAlign(TextAlign.RIGHT);
				
				AonTableButton button = new AonTableButton("Nuevo Certificado", AON.CSS.aonIconAdd());
				button.addStyleName(AON.CSS.aonCustomRowButtom());
				button.getElement().getStyle().setProperty("border", "2px solid #434548");
				button.getElement().getStyle().setProperty("padding", "10px");
				button.getElement().getStyle().setProperty("border-radius", "50%");
				button.addClickHandler(e -> createCertificate());
				buttonContainer.add(button);
				
				tab.addHeader(buttonContainer, col.getColWidth(), col.getStyles());
			} else
				tab.addHeader(new Label(col.getHeaderLabel()), col.getColWidth(), col.getStyles());
		
		}
	}

	private void createCertificate() {
		new AonCertificateDialog(domainName, domainId, user, new AonCerticateDialogCallback() {

			@Override
			public void onAccept() {
				onModuleLoad();
			}

		});
	}

	// ------------------------------------------------------ onModuleLoad

	public void onModuleLoad() {
		getCertificates(
			s -> createCertDataTable(), 
			f -> {}
		);
	}

	// ------------------------------------------------------ Create certificate
	// tables

	private void createCertDataTable() {
		removeTableRows(tab);
		paintHeader();
		createCertDataTableRows();
	}

	private void removeTableRows(AonCustomTable table) {
		int rows = table.getRowsCount();
		while (rows >= 0) {
			table.remove(rows);
			rows--;
		}
	}

	private void createCertDataTableRows() {
		if (certificateList.isEmpty()) {
			paintNoDataRow();
		} else
			for (Certificate certificate : certificateList)
				paintRow(tab, certificate, false);

	}

	private void paintNoDataRow() {
		HTMLPanel row = tab.createRow();
		
		Label noData = new Label("No existen certificados");
		noData.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		tab.addRow(row, noData, "100%");
	}

	private void paintRow(AonCustomTable table, Certificate certificate, boolean isEnterprise) {
		HTMLPanel row = table.createRow();
		createFormCells(table, row, certificate, false);
	}

	private void createFormCells(AonCustomTable table, HTMLPanel row, Certificate certificate, boolean isEnterprise) {
		AonTableButton use = certificate.getOwner().equals(CertificateOwner.USER)
				? new AonTableButton("Privado", AON.CSS.aonIconShieldLocked())
				: certificate.getConfidential().equals(CertificateSecurity.PUBLIC)
					? new AonTableButton("Compartido Usuarios Empresa", AON.CSS.aonIconAdminPanelSettings())
					: new AonTableButton("Publico", AON.CSS.aonIconEncryptedOff());
		table.addInlineStyle(use, COLS.TPE.getStyles());
		
		String certificateFor = "-";
		if (!certificate.getCertificateInfo().isEmpty()) {
			certificateFor = (AonStringUtils.isBlank(certificate.getCertificateInfo().getDocument()) ? ""
					: "(" + certificate.getCertificateInfo().getDocument() + ") ")
					+ certificate.getCertificateInfo().getName() + " " + certificate.getCertificateInfo().getSurname();
		}

		Label certificateForL = new Label(certificateFor);
		certificateForL.setTitle(certificateFor);
		table.addInlineStyle(certificateForL, COLS.DES.getStyles());

		String representation = "-";
		if (!certificate.getCertificateInfo().isEmpty())
			representation = AonStringUtils.isBlank(certificate.getCertificateInfo().getEnterprise())
					? "PERSONA F\u00cdSICA"
					: (AonStringUtils.isBlank(certificate.getCertificateInfo().getCif()) ? ""
							: "(" + certificate.getCertificateInfo().getCif() + ") ")
							+ certificate.getCertificateInfo().getEnterprise();

		Label representationL = new Label(representation);
		representationL.setTitle(representation);
		table.addInlineStyle(representationL, COLS.BUD.getStyles());

		String expirationDate = null == certificate.getCertificateInfo().getToDate() ? ""
				: formatFullDate.format(certificate.getCertificateInfo().getToDate());
		Label expirationDateL = new Label(expirationDate);

		Label alias = new Label(certificate.getDescription());
		alias.setTitle(certificate.getDescription());
		table.addInlineStyle(alias, COLS.TYP.getStyles());

		CheckBox tgssCB = new CheckBox();
		tgssCB.getElement().getStyle().setProperty("width", "1rem");
		tgssCB.getElement().getStyle().setProperty("height", "1rem");
		tgssCB.setValue(hasTGSSCertificate(certificate));
		tgssCB.setEnabled(false);

		CheckBox sepeCB = new CheckBox();
		sepeCB.getElement().getStyle().setProperty("width", "1rem");
		sepeCB.getElement().getStyle().setProperty("height", "1rem");
		sepeCB.setValue(hasSEPECertificate(certificate));
		sepeCB.setEnabled(false);

		CheckBox aeatCB = new CheckBox();
		aeatCB.getElement().getStyle().setProperty("width", "1rem");
		aeatCB.getElement().getStyle().setProperty("height", "1rem");
		aeatCB.setValue(hasAEATCertificate(certificate));
		aeatCB.setEnabled(false);

		// Buttons Panel
		HTMLPanel buttonsPanel = new HTMLPanel("");
		buttonsPanel.addStyleName(AON.CSS.aonItemFlex());
		buttonsPanel.getElement().getStyle().setProperty("justify-content", "right");

		AonTableButton verifyButton = new AonTableButton("Verificar Certificado", AON.CSS.aonIconVerify());
		verifyButton.addStyleName(AON.CSS.aonCustomRowButtom());
		verifyButton.addClickHandler(e -> {
			e.stopPropagation();

			showLoading("Validando certificado SEPE...");

			ArrayList<CertificateType> tags = new ArrayList<CertificateType>();
			tags.add(CertificateType.SEPE);

			verifyCertificate(certificate.getId(), tags,
					s -> showSuccess("Certificado", "Certificado validado correctamente"),
					f -> showWarning("Error verificaci\u00F3n", f.getMessage()));
		});
		verifyButton.setVisible(false);

		AonTableButton secondaryUsersButton = new AonTableButton("Usuarios Secundarios", AON.CSS.aonIconList());
		secondaryUsersButton.addStyleName(AON.CSS.aonCustomRowButtom());
		secondaryUsersButton.addClickHandler(e -> {
			e.stopPropagation();
			onSecondaryUser(certificate.getId());
		});

		secondaryUsersButton.setVisible(false);

		AonTableButton assignedCCCButton = new AonTableButton("CCCs Asignados", AON.CSS.aonIconTgss());
		assignedCCCButton.addStyleName(AON.CSS.aonCustomRowButtom());
		assignedCCCButton.addClickHandler(e -> {
			e.stopPropagation();
			onAssignedCCC(certificate.getId());
		});

		assignedCCCButton.setVisible(false);

		AonTableButton deleteButton = new AonTableButton("Borrar", AON.CSS.aonIconDelete());
		deleteButton.addStyleName(AON.CSS.aonCustomRowButtom());
		deleteButton.addClickHandler(e -> {
			e.stopPropagation();
			deleteCertificate(certificate);
		});

		AonTableButton downloadButton = new AonTableButton("Descargar", AON.CSS.aonIconDownload());
		downloadButton.addStyleName(AON.CSS.aonCustomRowButtom());
		downloadButton.addClickHandler(e -> {
			e.stopPropagation();
			downloadCertificate(certificate);
		});

		AonTableButton checkCertificateButton = new AonTableButton("Informaci\u00F3n", AON.CSS.aonIconInfo());
		checkCertificateButton.addStyleName(AON.CSS.aonCustomRowButtom());
		checkCertificateButton.addClickHandler(e -> {
			e.stopPropagation();
			getCertificateInfo(certificate);
		});

		// Buttons visibility
		if (Boolean.FALSE.equals(certificate.hasCertificate())) {
			verifyButton.setVisible(false);
			secondaryUsersButton.setVisible(false);
			assignedCCCButton.setVisible(false);
			checkCertificateButton.setVisible(false);
		} else {
			if (hasTGSSCertificate(certificate)) {
				secondaryUsersButton.setVisible(true);
				assignedCCCButton.setVisible(true);
			}
			if (hasSEPECertificate(certificate))
				verifyButton.setVisible(true);
			checkCertificateButton.setVisible(true);
		}

		buttonsPanel.add(verifyButton);
		buttonsPanel.add(assignedCCCButton);
		buttonsPanel.add(secondaryUsersButton);
		buttonsPanel.add(checkCertificateButton);
		buttonsPanel.add(deleteButton);
		//buttonsPanel.add(downloadButton);
		
		table.addRow(row, use, COLS.TPE.getColWidth());
		table.addRow(row, certificateForL, COLS.DES.getColWidth());
		table.addRow(row, representationL, COLS.BUD.getColWidth());
		table.addRow(row, expirationDateL, COLS.DOC.getColWidth());
		table.addRow(row, alias, COLS.TYP.getColWidth());
		table.addRow(row, tgssCB, COLS.TGS.getColWidth());
		table.addRow(row, sepeCB, COLS.SEP.getColWidth());
		table.addRow(row, aeatCB, COLS.AEA.getColWidth());

		table.addRow(row, buttonsPanel, COLS.BUT.getColWidth());

		// Para poder visualizar certificados publicos del padre pero con edicion
		// restringida
		if (Boolean.TRUE.equals(isEnterprise)
				&& (certificate.getDomain() != null && !certificate.getDomain().equals(domainId))) {
			alias.setTitle("Certificado p\u00fablico del dominio padre");
			tgssCB.setTitle("Certificado p\u00fablico del dominio padre");
			sepeCB.setTitle("Certificado p\u00fablico del dominio padre");
			aeatCB.setTitle("Certificado p\u00fablico del dominio padre");

			verifyButton.setVisible(false);
			secondaryUsersButton.setVisible(false);
			assignedCCCButton.setVisible(false);
			deleteButton.setVisible(false);
		} else {
			row.addDomHandler(e -> onCertificaUpdate(certificate), ClickEvent.getType());
		}
	}

	private void onCertificaUpdate(Certificate certificate) {
		new AonCertificateDialog(domainName, domainId, user, certificate, new AonCerticateDialogCallback() {

			@Override
			public void onAccept() {
				showSuccess("Certitficado", "Los certificados han sido actualizados correctamente");
				onModuleLoad();
			}

		});
	}

	// ------------------------------------------------------ Insert Rows

	private void getCertificateInfo(Certificate certificate) {
		getCertificateInfo(certificate.getId(), certificateInfo -> {
			String certificateInfoStr = certificateInfo.toString();
			certificateInfoStr += "<br>Validez desde : " + formatFullDate.format(certificateInfo.getFromDate())
					+ " hasta : " + formatFullDate.format(certificateInfo.getToDate());
			AonDialog dialog = new AonDialog("Informaci\u00F3n Certificado", new HTML(certificateInfoStr));
			dialog.info();
		}, f -> showWarning("Error verificaci\u00F3n", f.getMessage()));
	}

	private void onSecondaryUser(Integer rattachId) {
		ArrayList<CertificateType> tags = new ArrayList<CertificateType>();
		tags.add(CertificateType.TGSS);

		showLoading("Verificando certificado sistema RED...");

		verifyCertificate(rattachId, tags, success -> {
			showLoading("Accediendo al sistema RED para consultar los usuarios secundarios...");

			getSecondaryUsersPDF(rattachId, dataURI -> {
				AonMessagePanel.hideMessage(messagePanel);
				this.pdfDockLayoutPanel.setToolbarTitle("Usuarios Secundarios");
				showWidget(1);
				fullViewer.open(dataURI);
			}, f -> {
				showWarning("Error usuarios secundarios", f.getMessage());
			});

		}, failure -> {
			showWarning("Error verificaci\u00F3n", failure.getMessage());
		});
	}

	private void onAssignedCCC(Integer rattachId) {
		ArrayList<CertificateType> tags = new ArrayList<CertificateType>();
		tags.add(CertificateType.TGSS);

		showLoading("Verificando certificado sistema RED...");

		verifyCertificate(rattachId, tags, success -> {
			showLoading("Accediendo al sistema RED para consultar las cuentas de cotizaci\u00f3n asignadas...");

			getAssignedCCCsPDF(rattachId, dataURI -> {
				AonMessagePanel.hideMessage(messagePanel);
				this.pdfDockLayoutPanel.setToolbarTitle("Cuentas de Cotizaci\u00f3n asignadas");
				showWidget(1);
				fullViewer.open(dataURI);
			}, f -> {
				showWarning("Error usuarios secundarios", f.getMessage());
			});

		}, failure -> {
			showWarning("Error verificaci\u00F3n", failure.getMessage());
		});
	}

	private boolean hasTGSSCertificate(Certificate certificate) {
		if (null == certificate.getTags())
			return false;

		for (CertificateType tag : certificate.getTags())
			if (tag.equals(CertificateType.TGSS))
				return true;

		return false;
	}

	private boolean hasSEPECertificate(Certificate certificate) {
		if (null == certificate.getTags())
			return false;

		for (CertificateType tag : certificate.getTags())
			if (tag.equals(CertificateType.SEPE))
				return true;

		return false;
	}

	private boolean hasAEATCertificate(Certificate certificate) {
		if (null == certificate.getTags())
			return false;

		for (CertificateType tag : certificate.getTags())
			if (tag.equals(CertificateType.AEAT))
				return true;

		return false;
	}

	// ------------------------------------------------------ Delete Certificate
	// Methods

	private void deleteCertificate(Certificate certificate) {
		AonDialog deleteDialog = new AonDialog("Eliminar certificado",
				new HTML("\u00BFDesea eliminar este certificado\u003F"));
		deleteDialog.confirm(new AonAcceptDialogCallback() {

			@Override
			public void onCancel() {
				// Nothing to do here
			}

			@Override
			public void onAccept() {
				showLoading("Eliminando certificado ...");
				deleteCertificate(certificate, s -> {
					showSuccess("Certificado eliminado", "Certificado eliminado correctamente");
					onModuleLoad();
				}, f -> {
				});
			}
		});
	}

	// ------------------------------------------------------ Donwload Certificate
	// Method

	private void downloadCertificate(Certificate certificate) {
		String filePath = "/Users/svaldepenas/Desktop/certificate.p12";
		downloadCertificate(certificate.getId(), filePath,
				s -> showSuccess("Descarga", "Certificado descargado en la ruta " + filePath), f -> {
				});

	}

	// ------------------------------------------------- Aon Messages panel

	private void showSuccess(String title, String message) {
		Map<String, String> successMap = new HashMap<>();
		successMap.put(title, message);
		AonMessagePanel.showSuccess(messagePanel, successMap);
	}

	private void showWarning(String title, String message) {
		Map<String, String> warningMap = new HashMap<>();
		warningMap.put(title, message);
		AonMessagePanel.showWarning(messagePanel, warningMap);
	}

	private void showLoading(String message) {
		AonMessagePanel.showLoading(messagePanel, message);
	}
	
	// -------------------------------------------------- DataBase methods
	
	public void getDomainUserRoles(Consumer<DomainUserRoles> success, Consumer<Throwable> failure) {
		commonService.getDomainUserRoles(domainName, domainId, user, new AsyncCallback<DomainUserRoles>() {
			
			@Override
			public void onSuccess(DomainUserRoles result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// -------------------------------------------------- DataBase methods (DigitalCertificate)
	
	public void getCertificates(Consumer<List<Certificate>> success, Consumer<Throwable> failure){
		
		commonService.getCertificates(domainName, domainId, user, true, new AsyncCallback<List<Certificate>>() {
			
			@Override
			public void onSuccess(List<Certificate> certificateListDB) {
				certificateList = certificateListDB;
				success.accept(certificateListDB);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void deleteCertificate(Certificate certificate, Consumer<Void> success, Consumer<Throwable> failure){
		commonService.deleteCertificate(domainName, domainId, user, certificate, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void downloadCertificate(Integer certificateId, String filePath, Consumer<Void> success, Consumer<Throwable> failure){
		commonService.downloadCertificate(domainName, domainId, user, certificateId, filePath, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void verifyCertificate(Integer certificateId, List<CertificateType> tags, Consumer<Void> success, Consumer<Throwable> failure){
		
		commonService.verifyCertificate(domainName, domainId, user, certificateId, tags, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getCertificateInfo(Integer certificateId, Consumer<CertificateInfo> success, Consumer<Throwable> failure){
		
		commonService.getCertificateInfo(domainName, domainId, user, certificateId, new AsyncCallback<CertificateInfo>() {
			
			@Override
			public void onSuccess(CertificateInfo result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	// -------------------------------------------------- DataBase methods (Secondary users)
	
	public void getSecondaryUsers(Integer rattachId, Consumer<List<SecondaryUserCertificate>> success, Consumer<Throwable> failure){
		
		commonService.getSecondaryUsers(domainName, domainId, user, rattachId, new AsyncCallback<List<SecondaryUserCertificate>>() {
			
			@Override
			public void onSuccess(List<SecondaryUserCertificate> result) {
				secondaryUsers = result;
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getSecondaryUsersPDF(Integer rattachId, Consumer<String> success, Consumer<Throwable> failure){
		
		commonService.getSecondaryUsersPDF(domainName, domainId, user, rattachId, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void getAssignedCCCsPDF(Integer rattachId, Consumer<String> success, Consumer<Throwable> failure){
		
		commonService.getAssignedCCCsPDF(domainName, domainId, user, rattachId, new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				success.accept(result);	
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void deleteSecondaryUser(Integer rattachId, SecondaryUserCertificate secondaryUserCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		String naf = secondaryUserCertificate.getNaf();
		ArrayList<String> nssList = new ArrayList<>();
		nssList.add(naf);
		
		commonService.getIpfxNaf(domainName, domainId, user, nssList, new AsyncCallback<EmployeeSegSocial>() {
			
			@Override
			public void onSuccess(EmployeeSegSocial result) {
				String ipf = result.getIpf();
				String ipfType = checkIPFType(ipf);
				
				commonService.deleteSecondaryUser(domainName, domainId, user, rattachId, ipfType, ipf, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						success.accept(result);	
					}

					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	public void createSecondaryUser(Integer rattachId, SecondaryUserCertificate secondaryUserCertificate, Consumer<Void> success, Consumer<Throwable> failure){
		String naf = secondaryUserCertificate.getNaf();
		ArrayList<String> nssList = new ArrayList<>();
		nssList.add(naf);
		
		commonService.getIpfxNaf(domainName, domainId, user, nssList, new AsyncCallback<EmployeeSegSocial>() {
			
			@Override
			public void onSuccess(EmployeeSegSocial result) {
				String ipf = result.getIpf();
				String ipfType = checkIPFType(ipf);
				
				commonService.createSecondaryUser(domainName, domainId, user, rattachId, ipfType, ipf, naf, new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						success.accept(result);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						failure.accept(caught);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
		
	}
	
	// -------------------------------------------------- DataBase methods (Secondary users auxiliar method)
	
	public String checkIPFType(String ipf) {
		RegExp dniPattern = RegExp.compile("\\d{8}\\-?[A-HJ-NP-TV-Z]");

		if (dniPattern.test(ipf.toUpperCase()))
			return "1";
		else
			return "6";
	}

	// -------------------------------------------------- SecondaryUsers
	
	public List<SecondaryUserCertificate> getSecondaryUsers(boolean showInactives){
		List<SecondaryUserCertificate> activeUsers = new ArrayList<>();
		
		if(!showInactives) {
			for(SecondaryUserCertificate secondaryUserCertificate : this.secondaryUsers) {
				if(!AonStringUtils.equalsIgnoreCase(secondaryUserCertificate.getSituation(), "Baja"))
					activeUsers.add(secondaryUserCertificate);
			}
			
			return activeUsers;
		} else
			return secondaryUsers;
		
	}
	
	// -------------------------------------------------- Getter methods
	
	public List<Certificate> getCertificateList(){
		return this.certificateList;
	}

	public List<Certificate> getUserCertificateList() {
		List<Certificate> certificateUserList = new ArrayList<>();
		
		for(Certificate certificate : certificateList)
			if(certificate.getOwner() == CertificateOwner.USER)
				certificateUserList.add(certificate);
		
		return certificateUserList;
	}

	public List<Certificate> getEnterpriseCertificateList() {
		List<Certificate> certificateEnterpriseList = new ArrayList<>();
		
		for(Certificate certificate : certificateList)
			if(certificate.getOwner() == CertificateOwner.ENTERPRISE)
				certificateEnterpriseList.add(certificate);
		
		return certificateEnterpriseList;
	}

	public void createCertificate(CertificateOwner owner) {
		Certificate certificate = new Certificate()
			.setOwner(owner);
		
		certificateList.add(certificate);
	}

	public boolean hasOtherHasType(CertificateType type, CertificateOwner owner) {
		for(Certificate certificate : certificateList)
			if(null != certificate.getTags() && certificate.getOwner().equals(owner))
				for(CertificateType certificateType : certificate.getTags())
					if(certificateType == type)
						return true;
				
		return false;
	}

	public Integer getCertificateTGSSId(CertificateOwner owner) {
		for(Certificate certificate : certificateList)
			if(certificate.getOwner().equals(owner))
				for(CertificateType tag : certificate.getTags())
					if(tag.equals(CertificateType.TGSS))
						return certificate.getId();
		return null;
	}

}
