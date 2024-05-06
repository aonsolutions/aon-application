package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingAction.MarketingActionMediaType;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;

public abstract class AonMarketingActionPanel extends SimplePanel {
	
	public static interface AonMarketingActionPanelCallback {
		void onAccept(MarketingAction marketingAction);
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private TextBox description = new TextBox();
	private ListBox typeListBox = new ListBox();
	private AonDoubleBox budget = new AonDoubleBox(15, 2);
	private AonDateBox startDate = new AonDateBox();
	private AonDateBox endDate = new AonDateBox();
	
	public AonMarketingActionPanel(final String domainName,final int domain, final String user, final MarketingCampaign marketingCampaign, final AonMarketingActionPanelCallback aonMarketingActionPanelCallback) {
		initializeCommonService();
		show(domainName, domain, user, new MarketingAction().setMarketingCampaign(marketingCampaign), aonMarketingActionPanelCallback);
	}

	public void show(final String domainName, final int domain, final String user, final MarketingAction marketingAction, final AonMarketingActionPanelCallback callback) {
		setWidth("650px");
		getElement().getStyle().setProperty("padding", "1rem 0");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		final AonErrorPanel errorPanel = new AonErrorPanel();
		errorPanel.addStyleName(AON.CSS.aonMarginTop());
		rootPanel.add(errorPanel);
		
		FlowPanel tablePanel = new FlowPanel();
		tablePanel.setStyleName(AON.CSS.aonScrollArea());
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		FlexTable table = new FlexTable();
		table.setStyleName(AON.CSS.aonTable());
		table.addStyleName(AON.CSS.aonWidthAll());
		
		table.setWidget(0, 0, new InlineLabel("Descripci\u00f3n"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(0, 0).setPropertyString("min-width", "135px");
		description.setMaxLength(64);
		description.setStyleName(AON.CSS.aonInputText());
		description.addStyleName(AON.CSS.aonWidthAll());

		table.setWidget(0,1,description);
		table.getCellFormatter().setStyleName(0, 1, AON.CSS.aonWidthAll());
		table.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		table.setWidget(1,0,new InlineLabel("Canal"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(1, 0).setPropertyString("min-width", "135px");
		for(int i=0; i<MarketingActionMediaType.values().length; i++) {
			MarketingActionMediaType marketingActionMediaType = MarketingActionMediaType.values()[i];
			typeListBox.addItem(marketingActionMediaType.getDescription(), marketingActionMediaType.getValue().toString());
		}
		typeListBox.setStyleName(AON.CSS.aonInputText());
		table.setWidget(1,1,typeListBox);
		
		table.setWidget(2,0,new InlineLabel("Presupuesto"));
		table.getCellFormatter().setStyleName(2, 0, AON.CSS.aonTableLabel());
		table.setWidget(2,1,budget);
		
		table.setWidget(3,0,new InlineLabel("F. Inicio"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(3, 0).setPropertyString("min-width", "135px");
		startDate.setStyleName(AON.CSS.aonInputText());
		table.setWidget(3,1,startDate);
		
		table.setWidget(4,0,new InlineLabel("F. Fin"));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(4, 0).setPropertyString("min-width", "135px");
		endDate.setStyleName(AON.CSS.aonInputText());
		table.setWidget(4,1,endDate);
		
		tablePanel.add( table );
		
		rootPanel.add( tablePanel );
		
		FlowPanel buttons = new FlowPanel();
    	buttons.setStyleName(AON.CSS.aonTextCenter());
    	
    	final Button okButton = new Button();
    	okButton.setStyleName(AON.CSS.aonOkButton());
    	okButton.setText( AON.MSG.accept());
    	okButton.addKeyUpHandler( keyUpHandler);
    	
    	okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				okButton.setEnabled(false);
				
				marketingAction.setDomain(domain);
				marketingAction.setMediaType(MarketingActionMediaType.getMediaType(Integer.parseInt(typeListBox.getSelectedValue())));
				marketingAction.setStartDate(startDate.getValue());
				marketingAction.setEndDate(endDate.getValue());
				marketingAction.setDescription(description.getValue());
				marketingAction.setBudget(budget.getValue());
				
				commonService.saveMarketingAction(domainName, domain, user, marketingAction, new AsyncCallback<MarketingAction>() {

					@Override
					public void onSuccess(MarketingAction marketingAction) {
						callback.onAccept(marketingAction);
					}
					@Override
					public void onFailure(Throwable caught) {
						errorPanel.showError(caught.getMessage());
						okButton.setEnabled(true);
					}
				});
			}
		});
    	
    	buttons.add(okButton);
    	
    	final Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
    	cancelButton.addKeyUpHandler( keyUpHandler);
    	cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cancelButton.setEnabled(false);
				callback.onCancel();
			}
		});
    	buttons.add(cancelButton);
    	rootPanel.add(buttons);
		setWidget(rootPanel);
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	description.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	protected abstract void onResize();

}
