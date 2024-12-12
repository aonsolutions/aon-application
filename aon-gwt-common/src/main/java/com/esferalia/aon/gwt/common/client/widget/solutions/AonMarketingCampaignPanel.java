package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.occam.api.model.MarketingCampaign;
import com.esferalia.aon.occam.api.model.Workgroup;
import com.esferalia.aon.occam.api.model.security.Scope;
import com.esferalia.aon.occam.api.model.task.TaskHolder;
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
import com.google.gwt.user.client.ui.SimplePanel;

public abstract class AonMarketingCampaignPanel extends SimplePanel {
	
	public static interface AonMarketingCampaignPanelCallback {
		void onAccept(MarketingCampaign marketingCampaign);
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
	
	private AonCustomTextBox description = new AonCustomTextBox("Descripci\u00f3n");
	private AonCustomNumberBox budget = new AonCustomNumberBox("Presupuesto");
	private AonCustomNumberBox expense = new AonCustomNumberBox("Gastos");
	private AonCustomListBox scope = new AonCustomListBox("Ambito");
	private AonCustomListBox workgroup = new AonCustomListBox("C. Trabajo");
	private AonCustomListBox taskHolder = new AonCustomListBox("Asignado a");
	private AonCustomCheckBox active = new AonCustomCheckBox("Activo");
	
	public AonMarketingCampaignPanel(final String domainName,final int domain, final String user, final LinkedList<Scope> aviableScopes, final AonMarketingCampaignPanelCallback callback) {
		initializeCommonService();
		show(domainName, domain, user, aviableScopes, new MarketingCampaign(), callback);
	}
	
	public void show(final String domainName, final int domain, final String user, final LinkedList<Scope> aviableScopes, final MarketingCampaign marketingCampaign, final AonMarketingCampaignPanelCallback callback) {
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

		description.getTextBox().setMaxLength(64);
		rootPanel.add(description);
		
		HTMLPanel amountsPanel = new HTMLPanel(AonStringUtils.EMPTY);
		amountsPanel.addStyleName(AON.CSS.aonItemFlex());
		
		budget.hideNearBy();
		amountsPanel.add(budget);
		expense.hideNearBy();
		amountsPanel.add(expense);
		
		rootPanel.add(amountsPanel);
		
		HTMLPanel holderPanel = new HTMLPanel(AonStringUtils.EMPTY);
		holderPanel.addStyleName(AON.CSS.aonItemFlex());
		
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(workgroup.getListBox().getSelectedIndex() == 0) {
				taskHolder.getListBox().setSelectedIndex(0);
				taskHolder.setVisible(false);
			} else {
				taskHolder.setVisible(true);
				getAviableTaskHolders(domainName, domain, user, Integer.parseInt(workgroup.getListBox().getSelectedValue()), taskHolders -> { 
					taskHolder.clearItems();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
		});
		
		holderPanel.add(workgroup);
		
		taskHolder.addItem("-", "");
		getAviableWorkgroups(domainName, domain, user, workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			taskHolder.setVisible(false);
		});

		holderPanel.add(taskHolder);
		
		rootPanel.add(holderPanel);
		
		HTMLPanel scopeActivePanel = new HTMLPanel(AonStringUtils.EMPTY);
		scopeActivePanel.addStyleName(AON.CSS.aonItemFlex());
		
		scope.clearItems();
		aviableScopes.forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		active.setValue(true);
		scopeActivePanel.add(scope);
		scopeActivePanel.add(active);
		
		rootPanel.add(scopeActivePanel);
		
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
				
				marketingCampaign.setDomain(domain);
				marketingCampaign.setActive(active.getValue());
				marketingCampaign.setDescription(description.getValue());
				marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scope.getValue())));
				marketingCampaign.setBudget(budget.getValue());
				marketingCampaign.setExpense(expense.getValue());
				marketingCampaign.setWorkgroup(AonStringUtils.isBlank(workgroup.getValue()) ? null : new Workgroup().setId(Integer.parseInt(workgroup.getValue())));
				marketingCampaign.setTaskHolder(AonStringUtils.isBlank(taskHolder.getValue()) ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getValue())));
				
				commonService.saveMarketingCampaign(domainName, domain, user, marketingCampaign, new AsyncCallback<MarketingCampaign>() {

					@Override
					public void onSuccess(MarketingCampaign marketingCampaign) {
						callback.onAccept(marketingCampaign);
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
	        	description.setFocus(true);
	        	onResize();
	        }
	    });		
		
	}
	
	private void getAviableWorkgroups(final String domainName,final int domain, final String user, Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(domainName, domain, user, new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}
		});
	}
	
	private void getAviableTaskHolders(final String domainName,final int domain, final String user, Integer workgroup, Consumer<List<TaskHolder>> success) {
		commonService.getAviableTaskHolders(domainName, domain, user, workgroup, new AsyncCallback<List<TaskHolder>>() {
			
			@Override
			public void onSuccess(List<TaskHolder> taskHolders) {
				success.accept(taskHolders);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				AonMessagePanel.showError(messagePanel, caught.getMessage());
			}
		});
	}
	
	public void focusDescription() {
		description.setFocus(true);
	}

	protected abstract void onResize();

}
