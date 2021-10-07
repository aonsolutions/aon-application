package com.esferalia.aon.gwt.fiscal.client.mod303;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCards.AonCard;
import com.esferalia.aon.gwt.fiscal.client.AonCertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.mod303.Model303.Model303Callback;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.NamedFrame;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;

class Model303AEAT extends Model303Base {

	private static final String VALIDATE_PRINT_ACTION = "/aon_gwt_fiscal/ms/Model303PrintAEAT";
	protected static final String FRAME_NAME = "aeatFrame";
	
	protected static final String WIDTH_150PX = "150px";
	protected static final String SI_1 = "(1) SI";
	protected static final String NO_2 = "(2) NO";
	
	private FormPanel aeatForm = new FormPanel(FRAME_NAME);
	private Hidden domainIdAeatHidden = new Hidden("domainId");
	private Hidden domainNameAeatHidden = new Hidden("domainName");
	private Hidden userAeatHidden = new Hidden("user");
	private Hidden modAeatHidden = new Hidden("mod");
	private Hidden certAeatHidden = new Hidden("cert");
	private Hidden passAeatHidden = new Hidden("pass");
	private Hidden nameAeatHidden = new Hidden("name");
	private Hidden documentAeatHidden = new Hidden("document");
	private Hidden nrcAeatHidden = new Hidden("nrc");
	private Hidden testHidden = new Hidden("test");
	
	
	protected Model303AEAT(Mod303 mod303, Model303Callback cbk, Model303ModuleOptions options) {
		super(mod303, cbk, options);
	}

	protected void paintIdentificationTab(TabLayoutPanel tabPanel) {
		Model303IdentificationData identificationData = new Model303IdentificationData( new Model303IdentificationDataCallback()) ;
		tabPanel.add(identificationData, TAB_TEMPLATE.render(AON.MSG.identification(), AON.CSS.aonIconEmployee()));
	}

	@Override
	protected LinkedList<Pair<String, String>> getInformationLinks() {
		return new LinkedList<>();
	}
	
	protected void paintAdministrationTab(TabLayoutPanel tabPanel) {
		DockLayoutPanel dockLayout = new DockLayoutPanel(Unit.PX);
		tabPanel.add(dockLayout,TAB_TEMPLATE.render("Agencia Tributaria", FiscalModelUtils.getAdministrationBWIconStyle(getMod303().getAdministration())));		
		
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel diskFormPanel = new FlowPanel();
		diskFormPanel.add(mod303Hidden);
		diskFormPanel.add(domainIdHidden);
		diskFormPanel.add(domainNameHidden);
		diskFormPanel.add(userHidden);
		diskForm.setWidget(diskFormPanel);
		
		aeatForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel aeatFormPanel = new FlowPanel();
		aeatFormPanel.add(modAeatHidden);
		aeatFormPanel.add(domainIdAeatHidden);
		aeatFormPanel.add(domainNameAeatHidden);
		aeatFormPanel.add(userAeatHidden);
		aeatFormPanel.add(nameAeatHidden);
		aeatFormPanel.add(documentAeatHidden);
		aeatFormPanel.add(certAeatHidden);
		aeatFormPanel.add(passAeatHidden);
		aeatFormPanel.add(nrcAeatHidden);
		aeatFormPanel.add(testHidden);
		aeatForm.setWidget(aeatFormPanel);

		FlowPanel formContainer = new FlowPanel();
		formContainer.add(diskForm);
		formContainer.add(aeatForm);
		dockLayout.addNorth(formContainer,0);

		ScrollPanel scrollPanel = new ScrollPanel(); 
		AonCards cards = new AonCards();
		cards.addStyleName(AON.CSS.aonBackgroundLigthGray());
		
		FlowPanel cardBody0 = new FlowPanel();
		cardBody0.setStyleName(AON.CSS.aonTextCenter());
		Image cardImage0 = new Image(AON.AON_SOLUTIONS_RESOURCES.aonIconAeat().getSafeUri());
		cardImage0.setStyleName(AON.CSS.aonMarginBottom());
		cardImage0.setWidth("40px");
		cardBody0.add( cardImage0 );
		Label cardLabel0 = new Label("Modelo 303. IVA. Autoliquidaci\u00F3n.");
		cardBody0.add( cardLabel0 );
		AonCard card0 = new AonCard( false , new Label(), cardBody0);
		card0.addClickHandler(event -> Window.open("https://www.agenciatributaria.gob.es/AEAT.sede/procedimientoini/G414.shtml", "", "")); 
		cards.addCard( card0 );

		FlowPanel cardBody1 = new FlowPanel();
		cardBody1.setStyleName(AON.CSS.aonTextCenter());
		Image cardImage1 = new Image(AON.AON_SOLUTIONS_RESOURCES.aonIconDownload().getSafeUri());
		cardImage1.setStyleName(AON.CSS.aonMarginBottom());
		cardImage1.setWidth("40px");
		cardBody1.add( cardImage1 );
		Label cardLabel1 = new Label("Descargar fichero para su presentaci\u00F3n");
		cardBody1.add( cardLabel1 );
		AonCard card1 = new AonCard( false , new Label(), cardBody1);
		card1.addClickHandler(event -> downloadFile()); 
		cards.addCard( card1 );
		
		FlowPanel cardBody2 = new FlowPanel();
		cardBody2.setStyleName(AON.CSS.aonTextCenter());
		Image cardImage2 = new Image(AON.AON_SOLUTIONS_RESOURCES.aonIconValid().getSafeUri());
		cardImage2.setStyleName(AON.CSS.aonMarginBottom());
		cardImage2.setWidth("40px");
		cardBody2.add( cardImage2 );
		Label cardLabel2 = new Label("Validar e imprimir (PDF) via Agencia Tributaria");
		cardBody2.add( cardLabel2 );
		AonCard card2 = new AonCard( false , new Label(), cardBody2);
		card2.addClickHandler(event -> submitAEAT(VALIDATE_PRINT_ACTION)); 
		cards.addCard( card2 );

		FlowPanel cardBody3 = new FlowPanel();
		cardBody3.setStyleName(AON.CSS.aonTextCenter());
		Image cardImage3 = new Image(AON.AON_SOLUTIONS_RESOURCES.aonIconSend().getSafeUri());
		cardImage3.setStyleName(AON.CSS.aonMarginBottom());
		cardImage3.setWidth("40px");
		cardBody3.add( cardImage3 );
		Label cardLabel3 = new Label("Presentaci\u00F3n via Agencia Tributaria con firma no criptogr\u00e1fica");
		cardBody3.add( cardLabel3 );
		AonCard card3 = new AonCard( false , new Label(), cardBody3);
		card3.addClickHandler(event -> sendToAEAT()); 
		cards.addCard( card3 );
		scrollPanel.setWidget(cards);
		dockLayout.addNorth(scrollPanel, 160);
		
		SimpleLayoutPanel aeatPanel = new SimpleLayoutPanel();
		NamedFrame aeatFrame = new NamedFrame(FRAME_NAME);
		aeatFrame.setStyleName(AON.CSS.aonWidthAll());
		aeatFrame.addStyleName(AON.CSS.aonBlockCenter());
		aeatFrame.addStyleName(AON.CSS.aonBorderNone());
		aeatFrame.addStyleName(AON.CSS.aonBorderTop());
		aeatFrame.addStyleName(AON.CSS.aonHeightAll());
		aeatFrame.addStyleName(AON.CSS.aonMarginTop());
		aeatPanel.setWidget(aeatFrame);
		dockLayout.add(aeatPanel);
		
	}
	
	private void downloadFile() {
		if (getMod303().isFinished() || getMod303().isSent()) {
			submitForm(DOWNLOAD_FILE_ACTION);
		} else {
			getCallback().showBreakdownPanel(AON.MSG.mustFinishModel());
		}
	}

	private void sendToAEAT() {
		if (getMod303().isSent()) {
			getCallback().showBreakdownPanel("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");	
		} else if (!getMod303().isFinished()) {
			getCallback().showBreakdownPanel(AON.MSG.mustFinishModel());	
		} else {
			boolean showNRC = FiscalModelDeclarationType.DEPOSIT.equals(getMod303().getDeclarationType()); 
			AonCertificationPopup certPopup = new AonCertificationPopup(getAPI(), getMod303().getName(), getMod303().getDocument(), showNRC) {
				
				@Override
				protected void onCancel() {
					// Nothing
				}
				
				@Override
				protected void onAccept() {
					submitAEAT(VALIDATE_PRINT_ACTION, getCert(), getPass(), getDocument(), getName(), showNRC ? getNRC() : null);
				}
			};
			certPopup.center();
		}
		
	}
	
	private void submitAEAT(String action) {
		submitAEAT(action, "null", "null", "null", "null", "null");
	}
	
	private void submitAEAT(String action, String cert, String pass, String document, String name, String nrc) {
		aeatForm.setAction(GWT.getHostPageBaseURL() + action);
		modAeatHidden.setValue(String.valueOf(getMod303().getId()));
		domainIdAeatHidden.setValue(String.valueOf(getCallback().getDomain()));
		domainNameAeatHidden.setValue(getCallback().getDomainName());
		userAeatHidden.setValue(getCallback().getUser());
		certAeatHidden.setValue(cert);
		passAeatHidden.setValue(pass);
		nameAeatHidden.setValue(name);
		documentAeatHidden.setValue(document);
		nrcAeatHidden.setValue(nrc != null ? nrc : "null");
		testHidden.setValue(getTest() ? "1" : "0");

		aeatForm.submit();
	}
	
}
