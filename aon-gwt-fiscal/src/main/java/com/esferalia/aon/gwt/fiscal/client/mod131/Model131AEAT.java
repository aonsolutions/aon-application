package com.esferalia.aon.gwt.fiscal.client.mod131;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.AonData;
import com.esferalia.aon.gwt.fiscal.client.CertificationPopup;
import com.esferalia.aon.gwt.fiscal.client.FiscalModelUtils;
import com.esferalia.aon.gwt.fiscal.client.model.IFiscalModelCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;

public class Model131AEAT extends Model131Base {
	
	final Mod131ServiceAsync MOD131Service = GWT.create(Mod131Service.class);

	public Model131AEAT(IFiscalModelCallback<Mod131> callback, AonData aonData) {
		super(callback, aonData);
	}
	
	public FlowPanel getDeclarationPanel(){
		FlowPanel panel = new FlowPanel();
		panel.setWidth("98%");
		
		FlowPanel formContainer = new FlowPanel();
		aeatForm.setMethod(FormPanel.METHOD_POST);
		diskForm.setMethod(FormPanel.METHOD_POST);
		FlowPanel formFlowPanel = new FlowPanel();
		FlowPanel aeatFormFlowPanel = new FlowPanel();
		diskForm.add(formFlowPanel);
		aeatForm.add(aeatFormFlowPanel);
		
		formFlowPanel.add(mod131Hidden);
		formFlowPanel.add(domainIdHidden);
		formFlowPanel.add(domainNameHidden);
		formFlowPanel.add(userHidden);
		
		aeatFormFlowPanel.add(modAeatHidden);
		aeatFormFlowPanel.add(domainIdAeatHidden);
		aeatFormFlowPanel.add(domainNameAeatHidden);
		aeatFormFlowPanel.add(userAeatHidden);
		aeatFormFlowPanel.add(nameAeatHidden);
		aeatFormFlowPanel.add(documentAeatHidden);
		aeatFormFlowPanel.add(certAeatHidden);
		aeatFormFlowPanel.add(passAeatHidden);
		
		formContainer.add(diskForm);
		formContainer.add(aeatForm);

		panel.add(formContainer);
		FlowPanel administrationPanel = getAdministrationPanel(); 
		panel.add(administrationPanel);
		FlowPanel informationPanel = getInformationPanel();
		panel.add(informationPanel);
		return panel;
	}
	
	public FlowPanel getAdministrationPanel() {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		panel.addStyleName(AON.AON_CSS.aonWidthAll());
		panel.addStyleName(AON.AON_CSS.aonMarginTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingTop());
		panel.addStyleName(AON.AON_CSS.aonPaddingLeft());
		 
		FlexTable tab = new FlexTable();
		tab.getColumnFormatter().setWidth(0, "30px");
		tab.getColumnFormatter().setWidth(1, "auto");
		tab.getColumnFormatter().setWidth(2, "30px");
		tab.setStyleName(AON.AON_CSS.aonWidth90Percent());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonPanelGrid());
		Label title = new Label("Presentaci\u00F3n del modelo");
		tab.getFlexCellFormatter().setColSpan(0, 0, 3);
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonPanelGridEven());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonMarginTop());
		tab.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonFiscalModelTableHeaderTitle());
		tab.getCellFormatter().addStyleName(0, 0, FiscalModelUtils.getAdministrationBG(getModel().getAdministration()));
		tab.setWidget(0, 0, title);
		
		int row = 1;
		Label icon1 = new Label();
		icon1.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
		tab.setWidget(row, 0, icon1 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		
		FlowPanel p1 = new FlowPanel();
		p1.setStyleName(AON.AON_CSS.aonPadding2());
		Button button1 = new Button("Descargar fichero para su presentaci\u00F3n");
		button1.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button1.addStyleName(AON.AON_CSS.aonBorderNone());
		button1.addStyleName(AON.AON_CSS.aonEvenBackground());
		button1.addStyleName(AON.AON_CSS.aonClickable());
		button1.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getModel().isFinished() || getModel().isSent()) {
					submitForm(MODEL131_FILE);
				} else {
					getCallback().showInfoPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p1.add(button1);
		tab.setWidget(row, 1, p1 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(row, 1, 2);
		row++;
		
		Label icon2 = new Label();
		icon2.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
		tab.setWidget(row, 0, icon2 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p2 = new FlowPanel();
		p2.setStyleName(AON.AON_CSS.aonPadding2());
		Button button2 = new Button("Validar e imprimir (PDF) via Agencia Tributaria (a partir de los datos guardados).");
		button2.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button2.addStyleName(AON.AON_CSS.aonBorderNone());
		button2.addStyleName(AON.AON_CSS.aonEvenBackground());
		button2.addStyleName(AON.AON_CSS.aonClickable());
		button2.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (getModel().isFinished() || getModel().isSent()) {
					submitAEAT(MODEL131_PRINT_AEAT);
					getCallback().showVisorAEAT();
				} else {
					getCallback().showInfoPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
				}
			}
		});
		p2.add(button2);
		tab.setWidget(row, 1, p2 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
		tab.getFlexCellFormatter().setColSpan(2, 1, 2);
		row++;
			
		// CON FIRMA NO CRIPTOGRAFICA
		Label icon3 = new Label();
		icon3.addStyleName(FiscalModelUtils.getAdministrationIcon(getModel().getAdministration()));
		tab.setWidget(row, 0, icon3 );
		tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());
		FlowPanel p3 = new FlowPanel();
		p3.setStyleName(AON.AON_CSS.aonPadding2());
		Button button3 = new Button("Presentaci\u00F3n via Agencia Tributaria con firma no criptogr\u00e1fica (a partir de los datos guardados).");
		button3.setStyleName(AON.AON_CSS.aonPaddingLeft());
		button3.addStyleName(AON.AON_CSS.aonBorderNone());
		button3.addStyleName(AON.AON_CSS.aonEvenBackground());
		button3.addStyleName(AON.AON_CSS.aonClickable());
		button3.addStyleName("aon-icon-beta-text");
		button3.getElement().getStyle().setPaddingLeft(20, Unit.PX);
		button3.addClickHandler( new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {		
				Boolean showNRC = FiscalModelDeclarationType.DEPOSIT.equals(getModel().getDeclarationType()); 
				CertificationPopup certPopup = new CertificationPopup(getAPI(), getModel().getName(), getModel().getDocument(), showNRC) {
								
					@Override
					protected void onCancel() {
					
					}
							
					@Override
					protected void onAccept() {
						if(getModel().isSent()) {
							getCallback().showInfoPanel("La presentaci\u00F3n del modelo ya se ha realizado con anterioridad.");
						} else if (getModel().isFinished()) {
							submitAEAT(MODEL131_PRINT_AEAT, getCert(), getPass(), getName(), getDocument(), showNRC ? getNRC() : null);
							getCallback().showVisorAEAT();
						} else {
							getCallback().showInfoPanel("Para generar el fichero debe finalizar la confecci\u00F3n del modelo.");
						}	
					}
				};
				certPopup.center();
			}
		});
		p3.add(button3);
		tab.setWidget(row, 1, p3 );
		tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());

		MOD131Service.presentationFile(getCallback().getDomainName(), getCallback().getDomain(), getCallback().getUser(), getModel().getId(), new AsyncCallback<Integer>() {

			@Override public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(Integer result) {
				if(result > 0) {
					Button download2 = new Button();
					download2.setStyleName("aon-icon-mail-save");
					download2.addStyleName(AON.AON_CSS.aonIconCommandButton());
					download2.getElement().getStyle().setPaddingTop(16, Unit.PX);
					download2.addClickHandler(new ClickHandler() {
							
						@Override
						public void onClick(ClickEvent event) {
							getAPI().getFiscal().download(result +"");
						}
					});
					tab.setWidget(3, 2, download2 );
					tab.getCellFormatter().setStyleName(3, 2, AON.AON_CSS.aonPanelGridEven());
				} else tab.getFlexCellFormatter().setColSpan(3, 1, 2);
			}
		});
		row++;

		panel.add(tab);
		return panel;
	}
	
	@Override
	public LinkedList<Pair<String, String>> getInformationLinks() {
		LinkedList<Pair<String, String>> list = new LinkedList<Pair<String, String>>();
		list.add(new Pair<String, String>("Tr\u00E1mites."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/tramitacion/G602.shtml"));
		list.add(new Pair<String, String>("Informaci\u00F3n general." 
				,"https://www.agenciatributaria.gob.es/AEAT.sede/Ayuda/G602.shtml"));
		list.add(new Pair<String, String>("Ficha."
				,"https://www.agenciatributaria.gob.es/AEAT.sede/procedimientos/G602.shtml"));
		return list;
	}
}
