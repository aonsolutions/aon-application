package com.esferalia.aon.gwt.mod200.client.mod200;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.Upload;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.mod200.client.AonCertificationPopup;
import com.esferalia.aon.gwt.mod200.client.AonCertificationPopup.AonCertificationPopupParams;
import com.esferalia.aon.gwt.mod200.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.mod200.shared.JsonParams;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
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

public class Model200AdmonPanel extends DockLayoutPanel {
	
//	public static interface IFiscalModelAdmonPanelCallback<T,O> {
//		O getOptions();
//		T getModel();
//		void showError(String string);
//		
//		String getModelInformationURL();
//		String getValidatePrintAction();
//		String getDownloadFileAction();
//		String getSendAction();
//		void sendSuccessfully();
//		String getCheckAction();
//		String getCheckDataResponseDataAction();
////		default boolean isBoeFormatEnabled() {
////			return false;
////		}
//		boolean isDirty();
//		String getImportAccountingAction();
//	}
	
	private API api;
//	private IFiscalModelAdmonPanelCallback<T,O> callback;
	private IModel200PageCallback callback;
	
	private FormPanel diskForm = new FormPanel("_blank");
	private Hidden mod200Hidden = new Hidden("modelID");
	private Hidden domainIdHidden = new Hidden("domainId");
	private Hidden domainNameHidden = new Hidden("domainName");
	private Hidden userHidden = new Hidden("user");
//	private Hidden boeFormatHidden = new Hidden("boeFormat");
	
	private DeckLayoutPanel deckLayoutPanel;
	private SimpleLayoutPanel aeatPanel;
	private SimpleLayoutPanel pdfViewerPanel;
	private FullViewer pdfViewer = new FullViewer();

	private AonLink modelInfoLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconLink(), "Informaci\u00F3n de procedimiento del modelo.");
	
	private AonLink importAccountingLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconUpload(), AON.MSG.importAccounting());
	private AonLink exportAccountingLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload(), AON.MSG.aeatAccountingFile());
	
	private AonLink validateLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconValid(), "Validar / Borrador PDF via AEAT");
	private AonLink downloadLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload(), "Archivo para la presentaci\u00F3n");
//	private AonLink boeDownloadLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload(), "Archivo para la presentaci\u00F3n. [Formato BOE]");
	private AonLink sendLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconSend(), "Envio de la presentaci\u00F3n a la AEAT.");
	private AonLink checkLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconAeatBw(), "Consultar Presentaci\u00F3n en AEAT.");
	private AonLink viewDocumentLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconPdf(), "Consultar Presentaci\u00F3n guardada.");
	private AonLink uploadPDFLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconPdf(), "Cargar PDF declaraci\u00F3n presentada.");
	
	private boolean validating;
	private boolean sending;
	private boolean checkingDataResponse;
	private boolean checkingAEAT;
	private boolean uploadingPDFData;
	
	public Model200AdmonPanel(IModel200PageCallback callback) {
		super(Unit.PX);
		this.callback = callback;
		this.api = new API(GWT.getModuleBaseURL(), 
			getCallback().getOptions().getConfiguration().getMd5(),
			getCallback().getOptions().getConfiguration().getDomain().getName(), 
			getCallback().getOptions().getConfiguration().getDomain().getId(),
			getCallback().getOptions().getConfiguration().getUser().getLogin());

		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel diskFormPanel = new FlowPanel();
		diskFormPanel.add(mod200Hidden);
		diskFormPanel.add(domainIdHidden);
		diskFormPanel.add(domainNameHidden);
		diskFormPanel.add(userHidden);
//		diskFormPanel.add(boeFormatHidden);
		diskForm.setWidget(diskFormPanel);
		
		FlowPanel formContainer = new FlowPanel();
		formContainer.add(diskForm);
		addNorth(formContainer,0);

		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel cards = new FlowPanel();
		cards.setStyleName(AON.CSS.aonPaddingLeft());
		
		modelInfoLink.addClickHandler(event -> 
			Window.open(getCallback().getModelInformationURL(), "", "")); 
		cards.add( modelInfoLink );
		
		importAccountingLink.addClickHandler(event -> importAccounting()); 
		cards.add( importAccountingLink );
		
		exportAccountingLink.addClickHandler(event -> exportAccounting()); 
		cards.add( exportAccountingLink );

		validateLink.addClickHandler(event -> validateAEAT()); 
		cards.add( validateLink );

		downloadLink.addClickHandler(event -> downloadFile()); 
		cards.add( downloadLink );
		
//		boeDownloadLink.addClickHandler(event -> downloadFile(true)); 
//		cards.add( boeDownloadLink );

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
 
	public IModel200PageCallback getCallback() {
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
		mod200Hidden.setValue(String.valueOf(getCallback().getModel().getId()));
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
//			} else if (!getCallback().getModel().canBeValidated()) {
//				getCallback().showError(AON.MSG.mustFinishModel());	
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
	
//	private void downloadFile() {
//		cleanViewers();
//		downloadFile(false);	
//	}
//
//	private void downloadFile (boolean boeFormat) {
//		if (getCallback().getModel().canBeSent() || getCallback().getModel().isSent()) {
//			boeFormatHidden.setValue(Boolean.toString(boeFormat));
//			submitForm(getCallback().getDownloadFileAction());
//		} else {
//			getCallback().showError(AON.MSG.mustFinishModel());
//		}
//	}
	
	private void importAccounting() {
		// FALTA 
		cleanViewers();
		getCallback().importAccountingFile();		
	}
	
	private void exportAccounting() {
		cleanViewers();
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.confirmAccountingFileMod200(),			
			new AonConfirmDialogCallback() {
				
				@Override
				public void onCancel() {}
				
				@Override
				public void onAccept() {
					submitForm(getCallback().getExportAccountingAction());
				}
			}
		);
	}	
	
	private void downloadFile() {
		cleanViewers();
		if (getCallback().getModel().canBeSent() || getCallback().getModel().isSent()) {			
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
				cleanViewers();
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
						.setShowNRC(false)
						.setTestEnvironment(getCallback().getOptions().getConfiguration().fiscal().isTestEnvironment());
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
						
						// FALTA - PONERLO EN COMMONSERVICE 
						// Grabar el fichero en data_response - data_attach
//						CommonServiceAsync serviceRaw = GWT.create(CommonService.class);
//						CommonServiceAsync service = new CommonServiceAsyncDecorator(serviceRaw);
//						
//						service.savePDFModel(getCallback().getOptions().getOccam(), getCallback().getModel(),  data,
//								new AsyncCallback<Void>() {
//									
//									@Override
//									public void onSuccess(Void result) {										
//										uploadingPDFData = false;
//										// Si se ha cargado de forma correcta, se muestra en pantalla
//										checkDataResponseData();
//									}
//									
//									@Override
//									public void onFailure(Throwable caught) {										
//										uploadingPDFData = false;
//										getCallback().showError("Error al cargar el archivo.");
//									}
//								});
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
		modelInfoLink.setVisible(true);  // Información del modelo
		importAccountingLink.setVisible(!getCallback().getModel().isFinished() && !getCallback().getModel().isSent());
		exportAccountingLink.setVisible(!getCallback().isDirty());		
		validateLink.setVisible( !getCallback().isDirty() && (getCallback().getModel().getStatus() == FiscalStatus.PENDING || getCallback().getModel().getStatus() == FiscalStatus.FINISHED) );  // Validación y Borrador AEAT
		downloadLink.setVisible( getCallback().getModel().canBeSent() );  // Descarga fichero para presentación
		sendLink.setVisible( getCallback().getModel().canBeSent() );  // Presentación directa
		checkLink.setVisible( getCallback().getModel().isSent() );  // Consulta presentación AEAT
		viewDocumentLink.setVisible( getCallback().getModel().isSent() );  // Consulta presentacion guardada
		uploadPDFLink.setVisible( viewDocumentLink.isVisible() );  // Cargar manualmente PDF presentado		
	}
	
}
