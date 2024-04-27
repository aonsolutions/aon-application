package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.ArrayList;
import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.MarketingAction;
import com.esferalia.aon.occam.api.model.MarketingActionTarget;
import com.esferalia.aon.occam.api.model.registry.Target;
import com.esferalia.aon.watson.util.AonStringUtils;
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
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;

public abstract class AonMarketingActionTargetPanel extends SimplePanel {
	
	public static interface AonMarketingActionTargetPanelCallback {
		void onAccept();
		void onCancel();
	}

	static CommonServiceAsync commonService;
	
	private static void initializeCommonService() {
		if (commonService == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
		}
	}

	private SuggestBox target = new SuggestBox();
	private List<Target> targets = new ArrayList<>();
	
	public AonMarketingActionTargetPanel(final String domainName,final int domain, final String user, final MarketingAction marketingAction, final AonMarketingActionTargetPanelCallback aonMarketingActionTargetPanelCallback) {
		initializeCommonService();
		show(domainName, domain, user, new MarketingActionTarget().setMarketingAction(marketingAction), aonMarketingActionTargetPanelCallback);
	}

	public void show(final String domainName, final int domain, final String user, final MarketingActionTarget marketingActionTarget, final AonMarketingActionTargetPanelCallback callback) {
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
		
		table.setWidget(0, 0, new InlineLabel("Cliente Potencial"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		table.getCellFormatter().getElement(0, 0).setPropertyString("min-width", "135px");
		getTargetSuggestion(domainName, domain, user, errorPanel);
		target.setStyleName(AON.CSS.aonInputText());
		target.addStyleName(AON.CSS.aonWidthAll());
		target.setAutoSelectEnabled(false);
		target.getElement().setPropertyString("placeholder", "Cuota: ctrl + espacio para ver sugerencias");
		target.addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				target.showSuggestionList();
			}
		});
		table.setWidget(0,1,target);
		table.getCellFormatter().setStyleName(0, 1, AON.CSS.aonWidthAll());
		
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
				
				marketingActionTarget.setActionTargetDomain(domain);
				
				if(AonStringUtils.isNotBlank(target.getValue())) {
					Integer targetId = Integer.parseInt(target.getValue().split("\\[")[1].split("\\]")[0]);
					marketingActionTarget.setId(targetId);
				} else marketingActionTarget.setId(null);
				
				marketingActionTarget.setActionTargetStatus((byte)0); // Pendiente
				
				commonService.saveMarketingActionTarget(domainName, domain, user, marketingActionTarget, new AsyncCallback<MarketingActionTarget>() {

					@Override
					public void onSuccess(MarketingActionTarget marketingActionTarget) {
						callback.onAccept();
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
	        	target.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void getTargetSuggestion(String domainName, int domain, String user, AonErrorPanel errorPanel) {
		commonService.getTargetSuggestion(domainName, domain, user, new AsyncCallback<List<Target>>() {
			
			@Override
			public void onSuccess(List<Target> targetSuggestion) {
				targets = targetSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				targets.forEach(target -> suggestions.add("[" + target.getId() + "] " + target.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) target.getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(suggestions);
				orclSb.setDefaultSuggestionsFromText(suggestions);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				errorPanel.showError(caught.getMessage());
			}
			
		});
	}

	protected abstract void onResize();

}
