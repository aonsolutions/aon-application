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
import com.google.gwt.dom.client.Element;
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
	
	private TextBox description = new TextBox();
	private AonDoubleBox budget = new AonDoubleBox(15, 2);
	private AonDoubleBox expense = new AonDoubleBox(15, 2);
	private ListBox scope = new ListBox();
	private ListBox workgroup = new ListBox();
	private InlineLabel taskHolderLabel = new InlineLabel("Asignado a");
	private ListBox taskHolder = new ListBox();
	private Button active = new Button();
	
	public AonMarketingCampaignPanel(final String domainName,final int domain, final String user, final LinkedList<Scope> aviableScopes, final AonMarketingCampaignPanelCallback callback) {
		initializeCommonService();
		show(domainName, domain, user, aviableScopes, new MarketingCampaign(), callback);
	}
	
	public void show(final String domainName, final int domain, final String user, final LinkedList<Scope> aviableScopes, final MarketingCampaign marketingCampaign, final AonMarketingCampaignPanelCallback callback) {
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
		table.getElement().getStyle().setProperty("width", "30rem");
		
		table.setWidget(0, 0, new InlineLabel("Descripci\u00f3n"));
		table.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTableLabel());
		description.setMaxLength(64);
		description.setStyleName(AON.CSS.aonInputText());
		addInputStyle(description.getElement());

		table.setWidget(0,1,description);
		table.getFlexCellFormatter().setColSpan(0, 1, 3);
		
		table.setWidget(1,0,new InlineLabel("Presupuesto"));
		table.getCellFormatter().setStyleName(1, 0, AON.CSS.aonTableLabel());
		addInputStyle(budget.getElement());
		table.setWidget(1,1,budget);
		
		table.setWidget(1,2,new InlineLabel("Gastos"));
		table.getCellFormatter().setStyleName(1, 2, AON.CSS.aonTableLabel());
		addInputStyle(expense.getElement());
		table.setWidget(1,3,expense);
		
		workgroup = new ListBox();
		addSelectStyle(workgroup.getElement());
		workgroup.addItem("-", "");
		workgroup.addChangeHandler(e -> {
			if(workgroup.getSelectedIndex() == 0) {
				taskHolder.setSelectedIndex(0);
				taskHolder.setVisible(false);
				taskHolderLabel.setVisible(false);
			} else {
				taskHolder.setVisible(true);
				taskHolderLabel.setVisible(true);
				getAviableTaskHolders(domainName, domain, user, Integer.parseInt(workgroup.getSelectedValue()), taskHolders -> { 
					taskHolder.clear();
					taskHolder.addItem("-", "");
					taskHolders.forEach(taskHolderIt -> taskHolder.addItem(taskHolderIt.getName(), taskHolderIt.getRegistry().toString()));
				});
			}
		});
		
		taskHolder = new ListBox();
		addSelectStyle(taskHolder.getElement());
		taskHolder.addItem("-", "");
		
		getAviableWorkgroups(domainName, domain, user, workgroups -> {
			workgroups.forEach(workgroupIt -> workgroup.addItem(workgroupIt.getDescription(), workgroupIt.getId().toString()));
			taskHolder.setVisible(false);
			taskHolderLabel.setVisible(false);
		});
		
		table.setWidget(3,0,new InlineLabel("Grupo trabajo"));
		table.getCellFormatter().setStyleName(3, 0, AON.CSS.aonTableLabel());
		table.setWidget(3,1,workgroup);
		
		table.setWidget(3,2,taskHolderLabel);
		table.getCellFormatter().setStyleName(3, 2, AON.CSS.aonTableLabel());
		table.setWidget(3,3,taskHolder);
		
		table.setWidget(4,0,new InlineLabel(AON.MSG.scope()));
		table.getCellFormatter().setStyleName(4, 0, AON.CSS.aonTableLabel());
		scope.clear();
		addSelectStyle(scope.getElement());
		aviableScopes.forEach(as -> scope.addItem(as.getDescription(), as.getId().toString()));
		scope.setStyleName(AON.CSS.aonInputText());
		table.setWidget(4,1,scope);
		
		active = new Button();
		table.setWidget(5,0,new InlineLabel("Activo"));
		table.getCellFormatter().setStyleName(5, 0, AON.CSS.aonTableLabel());
		getEnableDisableButton(active, true);
		active.addClickHandler(e -> getEnableDisableButton(active, !isActiveToggleButton(active)));
		table.setWidget(5,1,active);
		
		table.getColumnFormatter().getElement(0).getStyle().setProperty("width", "3rem");
		
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
				
				marketingCampaign.setDomain(domain);
				marketingCampaign.setActive(isActiveToggleButton(active));
				marketingCampaign.setDescription(description.getValue());
				marketingCampaign.setScope(new Scope().setId(Integer.parseInt(scope.getSelectedValue())));
				marketingCampaign.setBudget(budget.getValue());
				marketingCampaign.setExpense(expense.getValue());
				marketingCampaign.setWorkgroup(0 == workgroup.getSelectedIndex() ? null : new Workgroup().setId(Integer.parseInt(workgroup.getSelectedValue())));
				marketingCampaign.setTaskHolder(0 == taskHolder.getSelectedIndex() ? null : new TaskHolder().setRegistry(Integer.parseInt(taskHolder.getSelectedValue())));
				
				commonService.saveMarketingCampaign(domainName, domain, user, marketingCampaign, new AsyncCallback<MarketingCampaign>() {

					@Override
					public void onSuccess(MarketingCampaign marketingCampaign) {
						callback.onAccept(marketingCampaign);
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
	
	private void getEnableDisableButton(Button button, boolean disabled) {
		button.removeStyleName(disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE);
		button.removeStyleName(AON.AON_NO_MARGIN);
		button.removeStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON);
		
		button.setStyleName(!disabled ? AON.AON_ICON_DISABLE : AON.AON_ICON_ENABLE );
		button.setStyleName(AON.AON_NO_MARGIN, true);
		button.setStyleName(AON.AON_EDIT_DATA_TABLE_BUTTON, true);
	}
	
	private boolean isActiveToggleButton(Button button) {
		return AonStringUtils.containsIgnoreCase(button.getStyleName(), AON.AON_ICON_ENABLE);
	}
	
	private void addInputStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.1rem");
	}
	
	private void addSelectStyle(Element el) {
		el.getStyle().setProperty("width", "-moz-available");
		el.getStyle().setProperty("width", "-webkit-fill-available");
		el.getStyle().setProperty("height", "1.2rem");
	}
	
	private void getAviableWorkgroups(final String domainName,final int domain, final String user, Consumer<List<Workgroup>> success) {
		commonService.getAviableWorkgroups(domainName, domain, user, new AsyncCallback<List<Workgroup>>() {
			
			@Override
			public void onSuccess(List<Workgroup> workgroups) {
				success.accept(workgroups);
			}
			
			@Override
			public void onFailure(Throwable caught) {
//				errorPanel.showError(caught.getMessage());
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
//				errorPanel.showError(caught.getMessage());
			}
		});
	}

	protected abstract void onResize();

}
