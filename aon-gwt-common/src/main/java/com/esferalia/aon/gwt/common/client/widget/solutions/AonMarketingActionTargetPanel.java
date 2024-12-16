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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SimplePanel;

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

	private HTMLPanel messagePanel = new HTMLPanel(AonStringUtils.EMPTY);
	
	private AonCustomSuggestBox target = new AonCustomSuggestBox("Cliente Potencial");
	private List<Target> targets = new ArrayList<>();
	
	public AonMarketingActionTargetPanel(final String domainName,final int domain, final String user, final MarketingAction marketingAction, final AonMarketingActionTargetPanelCallback aonMarketingActionTargetPanelCallback) {
		initializeCommonService();
		show(domainName, domain, user, new MarketingActionTarget().setMarketingAction(marketingAction), aonMarketingActionTargetPanelCallback);
	}

	public void show(final String domainName, final int domain, final String user, final MarketingActionTarget marketingActionTarget, final AonMarketingActionTargetPanelCallback callback) {
		getElement().getStyle().setProperty("padding", "1rem");
		getElement().getStyle().setProperty("min-width", "20rem");
		
		FlowPanel rootPanel = new FlowPanel();
		rootPanel.setStyleName(AON.CSS.aonFlexColumnBetween());
		
		rootPanel.add(messagePanel);
		
		KeyUpHandler keyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					callback.onCancel();	
				}
			}
		};

		
		getTargetSuggestion(domainName, domain, user);
		target.setAutoSelectEnabled(false);
		target.getSuggestBox().getElement().setPropertyString("placeholder", "Ctrl + espacio para ver sugerencias");
		target.getSuggestBox().addKeyUpHandler(e -> {
			if(e.isControlKeyDown() && e.getNativeKeyCode() == 32) {
				target.showSuggestionList();
			}
		});
		rootPanel.add(target);
		
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
						AonMessagePanel.showError(messagePanel, caught.getMessage());
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
	
	private void getTargetSuggestion(String domainName, int domain, String user) {
		commonService.getTargetSuggestion(domainName, domain, user, new AsyncCallback<List<Target>>() {
			
			@Override
			public void onSuccess(List<Target> targetSuggestion) {
				targets = targetSuggestion;
				
				List<String> suggestions = new ArrayList<String>();
				targets.forEach(target -> suggestions.add("[" + target.getId() + "] " + target.getName()));
				
				MultiWordSuggestOracle orclSb = (MultiWordSuggestOracle) target.getSuggestBox().getSuggestOracle();
				orclSb.clear();
				orclSb.addAll(suggestions);
				orclSb.setDefaultSuggestionsFromText(suggestions);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}
			
		});
	}

	protected abstract void onResize();

}
