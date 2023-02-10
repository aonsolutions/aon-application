package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup.AonCertificationPopupParams;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelModuleOptions;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModelType;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
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
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.xhr.client.XMLHttpRequest;

import net.aonsolutions.gwt.pdfjs.client.FullViewer;

public class FiscalModelAdmonPanel<T extends IFiscalModel,O extends FiscalModelModuleOptions<T>> extends DockLayoutPanel {
	
	public static interface IFiscalModelAdmonPanelCallback<T,O> {
		O getOptions();
		T getModel();
		void showError(String string);
		
		String getModelInformationURL();
		String getValidatePrintAction();
		String getDownloadFileAction();
		String getSendAction();
		void sendSuccessfully();
		String getCheckAction();
		String getCheckDataResponseDataAction();
		default boolean isBoeFormatEnabled() {
			return false;
		}
	}
	
	private API api;
	private IFiscalModelAdmonPanelCallback<T,O> callback;
	
	private FormPanel diskForm = new FormPanel("_blank");
	private Hidden mod303Hidden = new Hidden("modelID");
	private Hidden domainIdHidden = new Hidden("domainId");
	private Hidden domainNameHidden = new Hidden("domainName");
	private Hidden userHidden = new Hidden("user");
	private Hidden boeFormatHidden = new Hidden("boeFormat");
	
	private DeckLayoutPanel deckLayoutPanel;
	private SimpleLayoutPanel aeatPanel;
	private SimpleLayoutPanel pdfViewerPanel;
	private FullViewer pdfViewer = new FullViewer();

	private AonLink modelInfoLinklink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconLink(), "Informaci\u00F3n de procedimiento del modelo.");
	private AonLink downloadLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload(), "Archivo para la presentaci\u00F3n");
	private AonLink boeDownloadLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload(), "Archivo para la presentaci\u00F3n. [Formato BOE]");

	private AonLink validateLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconValid(), "Validar / Borrador PDF via AEAT");
	private AonLink sendLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconSend(), "Envio de la presentaci\u00F3n a la AEAT.");
	private AonLink checkLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconAeatBw(), "Consultar Presentaci\u00F3n en AEAT.");
	private AonLink viewDocumentLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconPdf(), "Consultar Presentaci\u00F3n guardada.");
	private AonLink uploadPDFLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconPdf(), "Cargar PDF declaraci\u00F3n presentada.");
	
	private boolean validating;
	private boolean sending;
	private boolean checkingDataResponse;
	private boolean checkingAEAT;
	private boolean uploadingPDFData;
	
	public FiscalModelAdmonPanel(IFiscalModelAdmonPanelCallback<T,O> callback) {
		super(Unit.PX);
		this.callback = callback;
		this.api = new API(GWT.getModuleBaseURL(), 
			getCallback().getOptions().getConfiguration().getMd5(),
			getCallback().getOptions().getConfiguration().getDomain().getName(), 
			getCallback().getOptions().getConfiguration().getDomain().getId(),
			getCallback().getOptions().getConfiguration().getUser().getLogin());

		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel diskFormPanel = new FlowPanel();
		diskFormPanel.add(mod303Hidden);
		diskFormPanel.add(domainIdHidden);
		diskFormPanel.add(domainNameHidden);
		diskFormPanel.add(userHidden);
		diskFormPanel.add(boeFormatHidden);
		diskForm.setWidget(diskFormPanel);
		
		FlowPanel formContainer = new FlowPanel();
		formContainer.add(diskForm);
		addNorth(formContainer,0);

		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel cards = new FlowPanel();
		cards.setStyleName(AON.CSS.aonPaddingLeft());
		
		modelInfoLinklink.addClickHandler(event -> 
			Window.open(getCallback().getModelInformationURL(), "", "")); 
		cards.add( modelInfoLinklink );

		validateLink.addClickHandler(event -> validateAEAT()); 
		cards.add( validateLink );

		downloadLink.addClickHandler(event -> downloadFile()); 
		cards.add( downloadLink );
		
		boeDownloadLink.addClickHandler(event -> downloadFile(true)); 
		cards.add( boeDownloadLink );

		sendLink.addClickHandler(event -> sendToAdministration()); 
		cards.add( sendLink );
		
		checkLink.addClickHandler(event -> checkAEAT()); 
		cards.add( checkLink );
		
		viewDocumentLink.addClickHandler(event -> checkDataResponseData()); 
		cards.add( viewDocumentLink );
				
		uploadPDFLink.addClickHandler(event -> uploadPDFData()); 
		cards.add( uploadPDFLink );		
		
		scrollPanel.setWidget(cards);
		addWest(scrollPanel, 250);
		
		deckLayoutPanel = new DeckLayoutPanel();
		aeatPanel = new SimpleLayoutPanel();
		deckLayoutPanel.add(aeatPanel);
		
		pdfViewerPanel = new SimpleLayoutPanel();
		pdfViewerPanel.setWidget(pdfViewer);
		deckLayoutPanel.add(pdfViewerPanel);
		add(deckLayoutPanel);
		
		manageLinks();
	}
	
	public IFiscalModelAdmonPanelCallback<T,O> getCallback() {
		return callback;
	}

	private API getAPI() {
		return this.api;
	}
	
	private void cleanViewers() {
		if (aeatPanel != null) {
			aeatPanel.clear();
		}
		if (pdfViewer != null) {
			pdfViewer.open("data:application/pdf;base64," +
				"JVBERi0xLjAKMSAwIG9iajw8L1BhZ2VzIDIgMCBSPj5lbmRvYmogMiAwIG9iajw8L0tpZHNbMy" +
				"Aw\nIFJdL0NvdW50IDE+PmVuZG9iaiAzIDAgb2JqPDwvTWVkaWFCb3hbMCAwIDMgM10+PmVuZG" +
				"9iagp0\ncmFpbGVyPDwvUm9vdCAxIDAgUj4+Cg=="
				);
		}
	}

	private void submitForm(String action) {
		diskForm.setAction(GWT.getHostPageBaseURL() + action);
		mod303Hidden.setValue(String.valueOf(getCallback().getModel().getId()));
		domainIdHidden.setValue(String.valueOf(getCallback().getOptions().getDomain()));
		domainNameHidden.setValue(getCallback().getOptions().getDomainName());
		userHidden.setValue(getCallback().getOptions().getUser());
		diskForm.submit();
	}

	private void validateAEAT() {
		if (!validating) {
			validating = true;
			if (getCallback().getModel().isSent()) {
				getCallback().showError("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");	
			} else if (!getCallback().getModel().canBeValidated()) {
				getCallback().showError(AON.MSG.mustFinishModel());	
			} else {
				validateAEAT(new AEATParams()
						.setDomainName(getCallback().getOptions().getDomainName())
						.setDomainId(getCallback().getOptions().getDomain())
						.setUser(getCallback().getOptions().getUser())
						.setMod(getCallback().getModel().getId()));
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
		xhr.open(FormPanel.METHOD_POST, getCallback().getValidatePrintAction());
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
	
	private void downloadFile() {
		downloadFile(false);	
	}

	private void downloadFile (boolean boeFormat) {
		if (getCallback().getModel().canBeSent() || getCallback().getModel().isSent()) {
			boeFormatHidden.setValue(Boolean.toString(boeFormat));
			submitForm(getCallback().getDownloadFileAction());
		} else {
			getCallback().showError(AON.MSG.mustFinishModel());
		}
	}

	private void sendToAdministration() {
		if (!sending) {
			sending = true;
			if (getCallback().getModel().isSent()) {
				getCallback().showError("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");	
			} else if (!getCallback().getModel().canBeSent()) {
				getCallback().showError(AON.MSG.mustFinishModel());	
			} else {
				AonCertificationPopupParams params = new AonCertificationPopupParams()
					.setDocument(getCallback().getOptions().getConfiguration().fiscal().getCertificateDocument())
					.setName(getCallback().getOptions().getConfiguration().fiscal().getCertificateName())
					.setTestEnvironment(getCallback().getOptions().getConfiguration().fiscal().isTestEnvironment())
					.setShowNRC(getCallback().getModel().isStrictToDeposit())
					.setInfoMessage("Va a proceder a la presentaci\u00F3n del Modelo.");
				AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), params) {
					
					@Override
					protected void onCancel() {
						sending = false;
					}
					
					@Override
					protected void onAccept( AEATParams params) {
						AonConfirmDialog cd = new AonConfirmDialog();
						cd.confirm(AON.MSG.confirmDeclarationsendAction(), new AonConfirmDialogCallback() {

							@Override
							public void onAccept() {
								params
								.setDomainName(getCallback().getOptions().getDomainName())
								.setDomainId(getCallback().getOptions().getDomain())
								.setUser(getCallback().getOptions().getUser())
								.setMod(getCallback().getModel().getId());
								sendAEAT(params);
							}

							@Override
							public void onCancel() {
								sending = false;
							}
						});
					}
				};
				certPopup.center();
			}
		}
	}
	
	private void sendAEAT(AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();

		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, getCallback().getSendAction());
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
				String contentTypeHeader = xhreq.getResponseHeader( AonHttpUtils.CONTENT_TYPE);
				if (AonStringUtils.equals(MimeType.PDF.getName(), contentTypeHeader)) {
					getCallback().sendSuccessfully();
					Scheduler.get().scheduleDeferred(() -> showPDF( buff.toString() ));
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

	private void checkAEAT() {
		if (!checkingAEAT) {
			checkingAEAT = true;
			if (!getCallback().getModel().isSent()) {
				getCallback().showError("El modelo no est\u00E1 presentado.");	
			} else {
				String doc = getCallback().getOptions().getConfiguration().fiscal().getCertificateDocument();
				String name = getCallback().getOptions().getConfiguration().fiscal().getCertificateName();
				AonCertificationPopupParams params = new AonCertificationPopupParams()
						.setDocument(doc)
						.setName(name)
						.setShowNRC(false);
				AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), params) {
					
					@Override
					protected void onCancel() {
						checkingAEAT = false;
					}
					
					@Override
					protected void onAccept( AEATParams params) {
						AonConfirmDialog cd = new AonConfirmDialog();
						cd.confirm(AON.MSG.confirmDeclarationsendAction(), new AonConfirmDialogCallback() {

							@Override
							public void onAccept() {
								params.setDomainName(getCallback().getOptions().getDomainName())
									.setDomainId(getCallback().getOptions().getDomain())
									.setUser(getCallback().getOptions().getUser())
									.setMod(getCallback().getModel().getId());
								checkAEAT(params);
							}

							@Override
							public void onCancel() {
								checkingAEAT = false;
							}
						});
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
		xhr.open(FormPanel.METHOD_POST, getCallback().getCheckAction());
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
	
	protected void checkDataResponseData() {
		if (!checkingDataResponse) {
			checkingDataResponse = true;
			if (!getCallback().getModel().isSent()) {
				getCallback().showError("El modelo no est\u00E1 presentado.");	
			} else {
				checkDataResponseData(new AEATParams()
						.setDomainName(getCallback().getOptions().getDomainName())
						.setDomainId(getCallback().getOptions().getDomain())
						.setUser(getCallback().getOptions().getUser())
						.setMod(getCallback().getModel().getId()));
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
		xhr.open(FormPanel.METHOD_POST, getCallback().getCheckDataResponseDataAction());
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
	
	protected void uploadPDFData() {
		if (!uploadingPDFData) {
			if (!getCallback().getModel().isSent()) {
				getCallback().showError("El modelo no est\u00E1 presentado.");	
			} else {
				cleanViewers();
				
				Upload upload = new Upload() {
					
					@Override
					protected void onUpload(String data) {
						uploadingPDFData = true;
						
						// Grabar el fichero en data_response - data_attach
						FiscalMSServiceAsync serviceRaw = GWT.create(FiscalMSService.class);
						FiscalMSServiceAsync service = new FiscalMSServiceAsyncDecorator(serviceRaw);
						
						service.savePDFModel(getCallback().getOptions().getOccam(), getCallback().getModel(),  data,
								new AsyncCallback<Void>() {
									
									@Override
									public void onSuccess(Void result) {										
										uploadingPDFData = false;
										// Si se ha cargado de forma correcta, se muestra en pantalla
										checkDataResponseData();
									}
									
									@Override
									public void onFailure(Throwable caught) {										
										uploadingPDFData = false;
										getCallback().showError("Error al cargar el archivo.");
									}
								});
					}							
				};
				upload.upload();				
			}
		}
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
	
	private static class AonLink extends FocusPanel {
		
		protected AonLink(DataResource resource, String label) {
			setStyleName(AON.CSS.aonWidthAll());
			FlowPanel container = new FlowPanel();
			container.setWidth("95%");
			container.setStyleName(AON.CSS.aonBlockCenter());
			container.addStyleName(AON.CSS.aonClickableBlock());
			container.addStyleName(AON.CSS.aonMarginTop());
			container.addStyleName(AON.CSS.aonFlexBlock());
			container.addStyleName(AON.CSS.aonBorder());
			container.getElement().getStyle().setProperty("min-height", "35px");
			
			Image cardImage2 = new Image(resource.getSafeUri());
			cardImage2.setWidth("20px");
			container.add( cardImage2 );
			InlineLabel cardLabel2 = new InlineLabel( label );
			cardLabel2.getElement().getStyle().setPaddingLeft(5.0, Unit.PX);
			container.add( cardLabel2 );
			setWidget(container);
		}
	}

	public void manageLinks() {
		modelInfoLinklink.setVisible(true);
		downloadLink.setVisible( getCallback().getModel().canBeSent() );
		boeDownloadLink.setVisible( getCallback().isBoeFormatEnabled() && getCallback().getModel().canBeSent() );
		if (getCallback().getModel().isAEAT() &&
		   ((getCallback().getModel().getYear() > 2021)  
		   || (getCallback().getModel().getYear() == 2021 && getCallback().getModel().getPeriod().isLastSemester())
		   || (getCallback().getModel().getModel() == FiscalModelType.M202 && getCallback().getModel().getPeriod() == Period.T2))) {
			validateLink.setVisible( getCallback().getModel().canBeValidated() && AonStringUtils.isNotBlank(getCallback().getValidatePrintAction()));
			sendLink.setVisible( getCallback().getModel().canBeSent() && AonStringUtils.isNotBlank(getCallback().getSendAction() ));
			checkLink.setVisible( getCallback().getModel().isSent() && AonStringUtils.isNotBlank(getCallback().getCheckAction() ));
			viewDocumentLink.setVisible( getCallback().getModel().isSent() && AonStringUtils.isNotBlank(getCallback().getCheckDataResponseDataAction() ));
			// La opción para cargar el PDF presentado del modelo, se pone visible igual que la de consulta del documento presentado
			uploadPDFLink.setVisible( viewDocumentLink.isVisible() );
		} else {
			validateLink.setVisible( false );
			sendLink.setVisible( false );
			checkLink.setVisible( false  ); 
			viewDocumentLink.setVisible( false  );
			uploadPDFLink.setVisible( false );
		}
	}
}
