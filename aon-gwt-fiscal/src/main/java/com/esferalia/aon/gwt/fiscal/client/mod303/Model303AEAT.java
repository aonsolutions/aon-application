package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

abstract class Model303AEAT extends Model303Base {

	private static final String VALIDATE_PRINT_ACTION = GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303ValidatePrintAEAT";
	private static final String SEND_ACTION = GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303SendAEAT";
	private static final String CHECK_DATA_RESPONSE_DATA = GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303CheckDataResponseData";
	private static final String CHECK_AEAT = GWT.getHostPageBaseURL() +"aon_gwt_fiscal/ms/Mod303CheckAEAT";
	
	protected static final String FRAME_NAME = "aeatFrame";
	
	protected static final String WIDTH_150PX = "150px";
	protected static final String WIDTH_140PX = "140px";
	protected static final String SI_1 = "(1) SI";
	protected static final String NO_2 = "(2) NO";

	private API api;
	
	private boolean validating;
	private boolean sending;
	private boolean checkingDataResponse;
	private boolean checkingAEAT;
	
	private DeckLayoutPanel deckLayoutPanel;
	private SimpleLayoutPanel aeatPanel;
	private SimpleLayoutPanel pdfViewerPanel;
	private FullViewer pdfViewer = new FullViewer();
	
	private AonLink modelInfoLinklink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconAeat(), "Modelo 303. IVA. Autoliquidaci\u00F3n.");
	private AonLink validateLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconValid(), "Validar / Borrador PDF via AEAT");
	private AonLink downloadLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload(), "Archivo para la presentaci\u00F3n");
	private AonLink sendLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconSend(), "Presentaci\u00F3n via AEAT.");
	private AonLink checkLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconAeatBw(), "Consultar Presentaci\u00F3n via AEAT.");
	private AonLink viewDocumentLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconPdf(), "Consultar Presentaci\u00F3n guardada.");
	
	protected Model303AEAT(Mod303 mod303, Model303Callback cbk, Model303ModuleOptions options) {
		super(mod303, cbk, options);
		this.api = new API(GWT.getModuleBaseURL(), options.getAonData().getMd5(),
				options.getAonData().getDomain().getName(), options.getAonData().getDomain().getId(),
				options.getAonData().getUser().getLogin());
		
	}

	protected void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, AON.MSG.identification());
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		return new LinkedList<>();
	}
	
	protected Widget paintAdministrationTab(TabLayoutPanel tabPanel) {
		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		tabPanel.add(dockLayout,"Agencia Tributaria");		
		
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel diskFormPanel = new FlowPanel();
		diskFormPanel.add(mod303Hidden);
		diskFormPanel.add(domainIdHidden);
		diskFormPanel.add(domainNameHidden);
		diskFormPanel.add(userHidden);
		diskForm.setWidget(diskFormPanel);
		
		FlowPanel formContainer = new FlowPanel();
		formContainer.add(diskForm);
		dockLayout.addNorth(formContainer,0);

		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel cards = new FlowPanel();
		cards.setStyleName(AON.CSS.aonPaddingLeft());
		
		modelInfoLinklink.addClickHandler(event -> Window.open("https://www.agenciatributaria.gob.es/AEAT.sede/procedimientoini/G414.shtml", "", "")); 
		cards.add( modelInfoLinklink );

		validateLink.addClickHandler(event -> validateAEAT()); 
		cards.add( validateLink );

		downloadLink.addClickHandler(event -> downloadFile()); 
		cards.add( downloadLink );
		
		sendLink.addClickHandler(event -> sendToAEAT()); 
		cards.add( sendLink );
		
		checkLink.addClickHandler(event -> checkAEAT()); 
		cards.add( checkLink );
		
		viewDocumentLink.addClickHandler(event -> checkDataResponseData()); 
		cards.add( viewDocumentLink );
		
		
		scrollPanel.setWidget(cards);
		dockLayout.addWest(scrollPanel, 250);
		
		deckLayoutPanel = new DeckLayoutPanel();
		aeatPanel = new SimpleLayoutPanel();
		deckLayoutPanel.add(aeatPanel);
		
		pdfViewerPanel = new SimpleLayoutPanel();
		pdfViewerPanel.setWidget(pdfViewer);
		deckLayoutPanel.add(pdfViewerPanel);
		dockLayout.add(deckLayoutPanel);
		
		decorateAdministrationTab();

		return dockLayout;
	}
	
	@Override
	protected void decorateAdministrationTab() {
		modelInfoLinklink.setVisible(true);
		validateLink.setVisible( getCallback().getMod303().isFinished() );
		downloadLink.setVisible( getCallback().getMod303().isFinished() );
		sendLink.setVisible( getCallback().getMod303().isFinished() );
		checkLink.setVisible( getCallback().getMod303().isSent() && AonStringUtils.isNotBlank(getCallback().getMod303().getNumber()) ); 
		viewDocumentLink.setVisible( getCallback().getMod303().isSent() ); 
	}

	private void downloadFile() {
		if (getMod303().isFinished() || getMod303().isSent()) {
			submitForm(DOWNLOAD_FILE_ACTION);
		} else {
			getCallback().showError(AON.MSG.mustFinishModel());
		}
	}

	private void cleanViewers() {
		aeatPanel.clear();
		pdfViewer.open("data:application/pdf;base64," +
			"JVBERi0xLjAKMSAwIG9iajw8L1BhZ2VzIDIgMCBSPj5lbmRvYmogMiAwIG9iajw8L0tpZHNbMy" +
			"Aw\nIFJdL0NvdW50IDE+PmVuZG9iaiAzIDAgb2JqPDwvTWVkaWFCb3hbMCAwIDMgM10+PmVuZG" +
			"9iagp0\ncmFpbGVyPDwvUm9vdCAxIDAgUj4+Cg=="
		);
	}
	
	private void sendToAEAT() {
		if (!sending) {
			sending = true;
			if (getMod303().isSent()) {
				getCallback().showError("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");	
			} else if (!getMod303().isFinished()) {
				getCallback().showError(AON.MSG.mustFinishModel());	
			} else {
				boolean showNRC = getMod303().isStrictToDeposit();
				String doc = getCallback().getOptions().getAonData().getCertificateDocument();
				String name = getCallback().getOptions().getAonData().getCertificateName();
				AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), name, doc, showNRC) {
					
					@Override
					protected void onCancel() {
						sending = false;
					}
					
					@Override
					protected void onAccept( AEATParams params) {
						params
						.setDomainName(getCallback().getDomainName())
						.setDomainId(getCallback().getDomain())
						.setUser(getCallback().getUser())
						.setMod(getMod303().getId())
						;
						sendAEAT(params);
					}
				};
				certPopup.center();
			}
		}
	}

	private API getAPI() {
		return this.api;
	}

	private void sendAEAT(AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, SEND_ACTION);
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
				if (AonStringUtils.equals(MimeType.PDF.getName(), xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE))) {
					showPDF( buff.toString() );
				} else if (AonStringUtils.equals(MimeType.JSON.getName(), xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE))) {
					Model303.service.getMod303(getCallback().getOptions().getDomainName(), getCallback().getOptions().getUser(), getCallback().getOptions().getDomain(), 
							getCallback().getMod303().getId(), new AsyncCallback<Mod303>() {
						@Override
						public void onSuccess(Mod303 selected) {
							selectAndPopulate(selected,getCallback().getOptions());
							checkDataResponseData();
						}
						@Override
						public void onFailure(Throwable caught) {
							getCallback().showError(AON.MSG.unableToReadDeclaration(caught.getMessage()));
						}
					});
				} else {
					showHtml( buff.toString() );
				}
				sending = false;
				popup.hide();					
			}
		});	
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.AEAT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
		
	}
	
	protected void showPDF(String dataURI) {
		deckLayoutPanel.setWidget(pdfViewerPanel);
		pdfViewer.open("data:application/pdf;base64," + dataURI);
	}

	protected void showHtml(String dataURI) {
		Frame aeatFrame = new Frame( "data:text/html;base64," + dataURI);
		aeatFrame.setWidth("100%");
		aeatFrame.setHeight("100%");
		aeatFrame.setStyleName(AON.CSS.aonWidthAll());
		aeatFrame.addStyleName(AON.CSS.aonHeightAll());
		aeatFrame.addStyleName(AON.CSS.aonBlockCenter());
		aeatFrame.addStyleName(AON.CSS.aonBorderNone());
		aeatFrame.addStyleName(AON.CSS.aonBorderTop());
		aeatFrame.addStyleName(AON.CSS.aonMarginTop());
		aeatPanel.setWidget( aeatFrame );
		deckLayoutPanel.setWidget(aeatPanel);
	}

	private void validateAEAT() {
		if (!validating) {
			validating = true;
			if (getMod303().isSent()) {
				getCallback().showError("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");	
			} else if (!getMod303().isFinished()) {
				getCallback().showError(AON.MSG.mustFinishModel());	
			} else {
				validateAEAT(new AEATParams()
						.setDomainName(getCallback().getDomainName())
						.setDomainId(getCallback().getDomain())
						.setUser(getCallback().getUser())
						.setMod(getMod303().getId()));
			}
		}
	}

	private void validateAEAT(AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, VALIDATE_PRINT_ACTION);
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
				if (AonStringUtils.equals(MimeType.PDF.getName(), xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE))) {
					showPDF( buff.toString() );
				} else {
					showHtml( buff.toString() );
				}
				validating = false;
				popup.hide();
			}
		});	
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.AEAT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}
	
	protected void checkDataResponseData() {
		if (!checkingDataResponse) {
			checkingDataResponse = true;
			if (!getMod303().isSent()) {
				getCallback().showError("El modelo no est\u00E1 presentado.");	
			} else {
				checkDataResponseData(new AEATParams()
						.setDomainName(getCallback().getDomainName())
						.setDomainId(getCallback().getDomain())
						.setUser(getCallback().getUser())
						.setMod(getMod303().getId()));
			}
		}
	}

	private void checkDataResponseData(AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, CHECK_DATA_RESPONSE_DATA);
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange( xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
				if (AonStringUtils.equals(MimeType.PDF.getName(), xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE))) {
					showPDF( buff.toString() );
				} else {
					showHtml( buff.toString() );
				}
				checkingDataResponse = false;
				popup.hide();
			}
		});	
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.AEAT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}
	
	private void checkAEAT() {
		if (!checkingAEAT) {
			checkingAEAT = true;
			if (!getMod303().isSent()) {
				getCallback().showError("El modelo no est\u00E1 presentado.");	
			} else {
				String doc = getCallback().getOptions().getAonData().getCertificateDocument();
				String name = getCallback().getOptions().getAonData().getCertificateName();
				AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), name, doc, false) {
					
					@Override
					protected void onCancel() {
						checkingAEAT = false;
					}
					
					@Override
					protected void onAccept( AEATParams params) {
						params
						.setDomainName(getCallback().getDomainName())
						.setDomainId(getCallback().getDomain())
						.setUser(getCallback().getUser())
						.setMod(getMod303().getId())
						;
						checkAEAT(params);
					}
				};
				certPopup.center();
			}
		}
	}
	private void checkAEAT(AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, CHECK_AEAT);
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
				if (AonStringUtils.equals(MimeType.PDF.getName(), xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE))) {
					showPDF( buff.toString() );
				} else {
					showHtml( buff.toString() );
				}
				checkingAEAT = false;
				popup.hide();
			}
		});	
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.AEAT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
	}

	private static class AonLink extends FocusPanel {
		
		protected AonLink(DataResource resource, String label) {
			setStyleName(AON.CSS.aonWidthAll());
			FlowPanel container = new FlowPanel();
			container.setWidth("95%");
			container.setStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonClickableBlock());
			container.addStyleName(AON.CSS.aonMarginTop());
			container.addStyleName(AON.CSS.aonFlexBlock());
			
			Image cardImage2 = new Image(resource.getSafeUri());
			cardImage2.setWidth("20px");
			container.add( cardImage2 );
			InlineLabel cardLabel2 = new InlineLabel( label );
			cardLabel2.getElement().getStyle().setPaddingLeft(5.0, Unit.PX);
			container.add( cardLabel2 );
			setWidget(container);
		}
	}

	
}
