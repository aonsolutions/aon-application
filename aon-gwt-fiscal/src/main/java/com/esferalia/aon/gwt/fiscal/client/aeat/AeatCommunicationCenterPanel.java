package com.esferalia.aon.gwt.fiscal.client.aeat;

import com.esferalia.aon.gwt.api.client.API;
import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonSplash;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup.AonCertificationPopupParams;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.gwt.fiscal.shared.JsonParams;
import com.esferalia.aon.occam.api.model.ddff.AeatFiscalData;
import com.esferalia.aon.occam.api.model.fiscal.aeat.AEATParams;
import com.esferalia.aon.watson.http.AonHttpUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
import com.google.gwt.xhr.client.XMLHttpRequest;

public class AeatCommunicationCenterPanel extends DockLayoutPanel {
	
	static final AeatServiceAsync AEAT_SERVICE;
	static {
		AeatServiceAsync aeatServiceRaw = GWT.create(AeatService.class);
		AEAT_SERVICE = new AeatServiceAsyncDecorator(aeatServiceRaw); 
	}

	public static interface IAeatCommunicationCenterPanelCallback {
		AeatModuleModuleOptions getOptions();
		void showError(String string);
		String getAddressCheckAction();
		String getFiscalDataAction();
	}
	
	private API api;
	
	private SimpleLayoutPanel aeatPanel;

	private boolean checkingAddressAEAT;
	private boolean runningFiscalData;
	
	public AeatCommunicationCenterPanel(IAeatCommunicationCenterPanelCallback callback) {
		super(Unit.PX);
		this.api = new API(GWT.getModuleBaseURL(), 
			callback.getOptions().getConfiguration().getMd5(),
			callback.getOptions().getConfiguration().getDomain().getName(), 
			callback.getOptions().getConfiguration().getDomain().getId(),
			callback.getOptions().getConfiguration().getUser().getLogin());

		ScrollPanel scrollPanel = new ScrollPanel(); 
		scrollPanel.setStyleName(AON.CSS.aonBorderRight());
		
		FlowPanel cards = new FlowPanel();
		cards.setStyleName(AON.CSS.aonPaddingLeft());
		
		AonLink checkAddressLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconAeatBw(), "Chequear ratificaci\u00F3n de la direcci\u00F3n.");
		checkAddressLink.addClickHandler(event -> addressCheck( callback )); 
		cards.add( checkAddressLink );
		
		AonLink fiscalDataLink = new AonLink(AON.AON_SOLUTIONS_RESOURCES.aonIconAeatBw(), "Obtener datos fiscales.");
		fiscalDataLink.addClickHandler(event -> fiscalData( callback )); 
		cards.add( fiscalDataLink );

		scrollPanel.setWidget(cards);
		addWest(scrollPanel, 250);
		
		aeatPanel = new SimpleLayoutPanel();
		add(aeatPanel);
		
	}
	
	private API getAPI() {
		return this.api;
	}
	
	private void cleanViewers() {
		aeatPanel.clear();
	}

	private void addressCheck(IAeatCommunicationCenterPanelCallback callback) {
		if (!checkingAddressAEAT) {
			checkingAddressAEAT = true;
			String doc = callback.getOptions().getConfiguration().fiscal().getCertificateDocument();
			String name = callback.getOptions().getConfiguration().fiscal().getCertificateName();
			AonCertificationPopupParams params = new AonCertificationPopupParams()
				.setDocument(doc)
				.setName(name)
				.setShowNRC(false)
				.setTestEnvironment(callback.getOptions().getConfiguration().fiscal().isTestEnvironment());
			AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), params) {
				
				@Override
				protected void onCancel() {
					checkingAddressAEAT = false;
				}
				
				@Override
				protected void onAccept( AEATParams params) {
					AonConfirmDialog cd = new AonConfirmDialog();
					cd.confirm(AON.MSG.confirmDeclarationsendAction(), new AonConfirmDialogCallback() {

						@Override
						public void onAccept() {
							params.setDomainName(callback.getOptions().getDomainName())
								.setDomainId(callback.getOptions().getDomain())
								.setUser(callback.getOptions().getUser())
								.setDocument(callback.getOptions().getConfiguration().getCompany().getDocument() )
								;
							addressCheck(callback, params);
						}

						@Override
						public void onCancel() {
							checkingAddressAEAT = false;
						}
					});
				}
			};
			certPopup.center();
		}
	}
	
	private void addressCheck(IAeatCommunicationCenterPanelCallback callback, AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		
		cleanViewers();
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open(FormPanel.METHOD_POST, callback.getAddressCheckAction());
		xhr.setRequestHeader(AonHttpUtils.CONTENT_TYPE,AonHttpUtils.APPLICATION_FORM_URLENCODED);
		xhr.setOnReadyStateChange(xhreq -> {
			int state = xhreq.getReadyState();
			if (state == XMLHttpRequest.DONE) {
				ArrayBuffer buff = xhreq.getResponseArrayBuffer();
				showHtml( buff.toString() );
				checkingAddressAEAT = false;
				popup.hide();
			}
		});	
		StringBuilder requestData = new StringBuilder();
		requestData.append("&"+IRequestParamsNames.AEAT_PARAMS +"=" + JsonParams.convert( params ));
		xhr.send(requestData.toString());
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
	}
	
	private void fiscalData(IAeatCommunicationCenterPanelCallback callback) {
		if (!runningFiscalData) {
			runningFiscalData = true;
			String doc = callback.getOptions().getConfiguration().fiscal().getCertificateDocument();
			String name = callback.getOptions().getConfiguration().fiscal().getCertificateName();
			AonCertificationPopupParams params = new AonCertificationPopupParams()
				.setDocument(doc)
				.setName(name)
				.setShowNRC(false)
				.setTestEnvironment(callback.getOptions().getConfiguration().fiscal().isTestEnvironment());
			AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), params) {
				
				@Override
				protected void onCancel() {
					runningFiscalData = false;
				}
				
				@Override
				protected void onAccept( AEATParams params) {
					params.setDomainName(callback.getOptions().getDomainName())
						.setDomainId(callback.getOptions().getDomain())
						.setUser(callback.getOptions().getUser())
						.setDocument(callback.getOptions().getConfiguration().getCompany().getDocument() );
					fiscalData(callback, params);
				}
			};
			certPopup.center();
		}
	}
	
	private void fiscalData(IAeatCommunicationCenterPanelCallback callback, AEATParams params) {
		final PopupPanel popup = new PopupPanel(false, true);
		popup.add( new AonSplash());
		popup.setGlassEnabled(true);
		popup.setAnimationEnabled(true);
		popup.center();
		cleanViewers();
		AEAT_SERVICE.getAeatFiscalData(params, new AsyncCallback<AeatFiscalData>() {
			@Override
			public void onSuccess(AeatFiscalData fiscalData) {
				aeatPanel.setWidget( new AeatFiscalDataPanel( fiscalData ) );
				popup.hide();
				runningFiscalData = false;
			}
			
			@Override
			public void onFailure(Throwable err) {
				AonErrorPanel errorPanel = new AonErrorPanel();
				errorPanel.addError( err );
				aeatPanel.setWidget( errorPanel );
				popup.hide();
				runningFiscalData = false;
			}
		});
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
	
}
