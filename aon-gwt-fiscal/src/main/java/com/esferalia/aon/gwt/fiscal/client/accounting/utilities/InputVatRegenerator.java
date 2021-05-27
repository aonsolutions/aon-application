package com.esferalia.aon.gwt.fiscal.client.accounting.utilities;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesRegenerateInputVatItem;
import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesResult;
import com.esferalia.aon.occam.api.model.accounting.utilities.IAccUtilitiesItem;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

class InputVatRegenerator extends OptionBase {

	private static AccountingUtilitiesServiceAsync SERVICE;
	 
	SimpleLayoutPanel content;
	ScrollPanel container;
	private String domainName;
	private String user;
	private Domain domain;
	
	protected InputVatRegenerator(String domainName, String user, Domain domain) {
		super(domainName, user, domain);
		this.domainName = domainName;
		this.user = user;
		this.domain = domain;
		
		AccountingUtilitiesServiceAsync serviceRaw = GWT.create(AccountingUtilitiesService.class);
		SERVICE = new AccountingUtilitiesServiceAsyncDecorator(serviceRaw);
		
		content = new SimpleLayoutPanel();
		container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		content.add(container);
		setContent(content);
	}
	
	@Override
	public String getOptionDescription() {
		return AonStringUtils.BULLET + " Regenerar el n\u00FAmero de IVA soportado";
	}

	protected Widget getToolbarPanel() {
		FlowPanel toolbarPanel = new FlowPanel();
		toolbarPanel.setStyleName(AON.AON_CSS.aonFindingTitleToolbar());
		toolbarPanel.addStyleName(AON.AON_CSS.aonWidthAll());
		FlexTable toolbar = new FlexTable();
		toolbar.setCellPadding(0);
		toolbar.setCellSpacing(0);
		toolbar.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel titlePanel = new FlowPanel();
		titlePanel.setStyleName(AON.AON_CSS.aonFindingTitleInternal());
		toolbar.setWidget(0, 0, titlePanel);
		toolbar.setWidget(0, 0, new Label(getOptionDescription()));
		toolbar.getCellFormatter().setStyleName(0,0, AON.AON_CSS.aonFindingTitle());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonBold());
		toolbar.getCellFormatter().addStyleName(0,0, AON.AON_CSS.aonNowrap());
		toolbar.setWidget(0, 1, new Label());
		toolbar.getCellFormatter().setStyleName(0,1, AON.AON_CSS.aonFindingSubtitleIternal());
		FlowPanel buttonContainer = new FlowPanel();
		buttonContainer.setStyleName(AON.AON_CSS.aonFindingToolbarItemGroup());
		toolbar.setWidget(0, 2, buttonContainer);
		toolbar.getCellFormatter().setStyleName(0,2, AON.AON_CSS.aonFindingToolbar());
		toolbarPanel.add(toolbar);
		return toolbarPanel;
	}
	
	public void run() {
		SERVICE.getInputVatRegenerationInfo(domainName, user, domain.getId(), new AsyncCallback<AccUtilitiesResult>() {

			@Override
			public void onFailure(Throwable caught) {
				showErrorPanel(caught.getMessage());
			}

			@Override
			public void onSuccess(AccUtilitiesResult result) {
				container.setWidget(paintResults(result));
			}
		});
	}
	
	@Override
	protected Widget paintResults(AccUtilitiesResult result) {
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGrid());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());

		tab.getColumnFormatter().setWidth(0, "100px");
		tab.getColumnFormatter().setWidth(1, "auto");
		tab.getColumnFormatter().setWidth(2, "20px");
		tab.getColumnFormatter().setWidth(3, "90px");

		tab.setWidget(0, 0, new Label(AON.MSG.fiscalYear()));
		tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonDataTableHeader());

		tab.setWidget(0, 1, new Label(AON.MSG.message()));
		tab.getCellFormatter().setStyleName(0, 1, AON.AON_CSS.aonDataTableHeader());
		
		tab.setWidget(0, 2, new Label("Acciones disponibles"));
		tab.getCellFormatter().setStyleName(0, 2, AON.AON_CSS.aonDataTableHeader());
		tab.getFlexCellFormatter().setColSpan(0, 2, 2);
		
		int row = 1;
		for (IAccUtilitiesItem it :  result.getItems()) {
			AccUtilitiesRegenerateInputVatItem item = (AccUtilitiesRegenerateInputVatItem) it;
			tab.setWidget(row, 0, new Label(""+item.getYear()));
			tab.getCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonPanelGridEven());

			FlowPanel messagePanel = new FlowPanel();
			String[] seriesInfo = AonStringUtils.split(it.getMessage(), '#');
			if (seriesInfo != null && seriesInfo.length > 0) {
				for (String series : seriesInfo) {
					String[] tokens = AonStringUtils.split(series, '|');
					FlowPanel seriesPanel = new FlowPanel("pre");
					seriesPanel.setStyleName(AON.AON_CSS.aonIconRowSelector());
					seriesPanel.addStyleName(AON.AON_CSS.aonPaddingLeft());
					seriesPanel.addStyleName(AON.AON_CSS.aonFixedFont());
					String msg = "Serie: " 
							+ AonStringUtils.rightPad("\"" + tokens[0] + "\". ",10) 
							+ "Facturas: " 
							+ AonStringUtils.rightPad(tokens[1]+ ". ",7)
							+ "Primer n\u00FAmero: " 
							+ AonStringUtils.rightPad(tokens[2] + ". ",7)
							+ "\u00DAltimo n\u00FAmero: "
							+ AonStringUtils.rightPad(tokens[3],7); 
					seriesPanel.add(new Label(msg));
					messagePanel.add(seriesPanel);		
				}
			}
			tab.setWidget(row, 1, messagePanel);
			tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonPanelGridEven());
			
			InlineLabel reg = new InlineLabel();
			reg.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
			if (item.isRegenerable() ) {
				reg.addStyleName(AON.AON_CSS.aonIconWarn());
			} else {
				reg.addStyleName(AON.AON_CSS.aonIconValidate());
			}
			tab.setWidget(row, 2, reg);
			tab.getCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonPanelGridEven());
			
			Button regenerate = new Button("Regenerar");
			regenerate.setTitle("Regenerar " + item.getYear());
			regenerate.setStyleName(AON.AON_CSS.aonIconCommandButton());
			regenerate.addStyleName(AON.AON_CSS.aonIconSettings());
			regenerate.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					ConfirmDialog cd = new ConfirmDialog();
					cd.confirm("Confirme","Se proceder\u00E1 a la renumeraci\u00F3n del "
							+ "n\u00FAmero de IVA soportado, en funci\u00F3n de la fecha de factura."
							+" Afecta a el n\u00FAmero de documento de la factura, "
							+ "el concepto de los vencimientos y el n\u00FAmero de documento "
							+" en l\u00EDneas de apuntes.  \u00BFDesea continuar?"
					, new ConfirmDialogCallback() {
						
						@Override public void onCancel() {}
						
						@Override
						public void onAccept() {
							regenerate();
						}
					});
				}

				private void regenerate() {
					final PopupPanel popup = new PopupPanel(false, true);
					Label label = new Label(AON.MSG.processing());
					label.addStyleName(AON.AON_CSS.aonTimer());
					popup.add(label);
					popup.setGlassEnabled(true);
					popup.setAnimationEnabled(true);
					popup.center();

					SERVICE.regenerateInputVat(domainName, user, domain.getId(), item.getYear(), new AsyncCallback<AccUtilitiesResult>() {

						@Override
						public void onFailure(Throwable caught) {
							popup.hide();
							showErrorPanel(caught.getMessage());
						}

						@Override
						public void onSuccess(AccUtilitiesResult result) {
							if (result.getItems() != null && result.getItems().size()>0) {
								showInfoPanel(result.getItems().get(0).getMessage());
							}
							run();
							Scheduler.get().scheduleDeferred(new ScheduledCommand() {
								public void execute() {
									popup.hide();
								}
							});
						}
					});
				}
			});
			tab.setWidget(row, 3, regenerate);
			tab.getCellFormatter().setStyleName(row, 3, AON.AON_CSS.aonPanelGridEven());
			
			row++;
		};
		return tab;
	}
}
