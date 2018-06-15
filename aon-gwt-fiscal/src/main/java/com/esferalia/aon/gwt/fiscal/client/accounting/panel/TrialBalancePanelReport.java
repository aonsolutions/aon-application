package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.CommonService;
import com.esferalia.aon.gwt.common.client.CommonServiceAsync;
import com.esferalia.aon.gwt.common.client.CommonServiceAsyncDecorator;
import com.esferalia.aon.gwt.common.client.widget.CloseTab;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MaximizeHandler;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeEvent;
import com.esferalia.aon.gwt.common.client.widget.MinimizePanel.MinimizeHandler;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.AonConfiguration;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;


public class TrialBalancePanelReport extends DockLayoutPanel implements HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AonConfiguration config;
	
	private SimpleLayoutPanel centerPanel;
	private TabLayoutPanel tabPanel;
	private TrialBalancePanelFilter filter;
	
	public TrialBalancePanelReport(String domainName,String user, int domainId) {
		this(domainName,user,domainId,Integer.MAX_VALUE,null,null);
	}
	
	public TrialBalancePanelReport(String domainName,String user,int domainId, int tabIndex, AonConfiguration config, AccountingReportParams params) {
		super(Unit.PX);
		this.currentDomainName = domainName;
		this.currentUser = user;
		this.currentDomainId = domainId;
		this.config = config;
		
		if (config == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(domainName, domainId
				,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						TrialBalancePanelReport.this.config = result;
						fill(tabIndex, params);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al leer la configuraci\u00F3n");
					}
				});
		} else {
			fill (tabIndex,params);
		}
	}
	
	private void fill(int tabIndex, AccountingReportParams params) {
		if (config.getPeriods() == null || config.getPeriods().size() == 0 ) {
			Window.alert("No se han encontrado ejercicios contables");
		} else {
			addStyleName(AON.AON_CSS.aonScrollArea());
			addStyleName(AON.AON_CSS.aonMarginBottom());
			SimpleLayoutPanel northPanel = new SimpleLayoutPanel();
			addNorth(northPanel, 118);
			centerPanel = new SimpleLayoutPanel();
			centerPanel.setStyleName(AON.AON_CSS.aonSelector());
			tabPanel = new TabLayoutPanel(30, Unit.PX);
			tabPanel.add(new SimpleLayoutPanel(),new CloseTab("Resultados", false));
			
			centerPanel.setWidget(tabPanel);
			add(centerPanel);
			
			filter = new TrialBalancePanelFilter(getCurrentDomainName(),getCurrentUser(),getCurrentDomainId(), config, params);
			filter.addMaximizeHandler(new MaximizeHandler() {
				
				@Override
				public void onMaximize(MaximizeEvent event) {
					TrialBalancePanelReport.this.setWidgetSize(northPanel, 118 );
					TrialBalancePanelReport.this.animate(500);
				}
			});
			filter.addMinimizeHandler(new MinimizeHandler() {
				
				@Override
				public void onMinimize(MinimizeEvent event) {
					TrialBalancePanelReport.this.setWidgetSize(northPanel, 25);
					TrialBalancePanelReport.this.animate(500);
				}
			});
			filter.addValueChangeHandler(new ValueChangeHandler<AccountingReportParams>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<AccountingReportParams> event) {
					AccountingReportParams params = event.getValue();
					onSearch(params,0);
				}
			});
			northPanel.setWidget(filter);
			onSearch(filter.getWidgetParams(),0);
		}
	}
	
	private String getCurrentDomainName() {
		return currentDomainName;
	}
	private Integer getCurrentDomainId() {
		return currentDomainId;
	}
	private String getCurrentUser() {
		return currentUser;
	}

	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

//	private void onNewTab() {
//		SimpleLayoutPanel panel = new SimpleLayoutPanel();
//		CloseTab closeTab = new CloseTab("Balance S./S.", true);
//		closeTab.addCloseHandler(new CloseHandler<Integer>() {
//			
//			@Override
//			public void onClose(CloseEvent<Integer> event) {
//				tabPanel.remove(panel); 
//			}
//		});
//		tabPanel.insert(panel, closeTab, (tabPanel.getWidgetCount()-1));
//	}
	
	private void onSearch(AccountingReportParams params, int tab) {
		SimpleLayoutPanel panel = (SimpleLayoutPanel) tabPanel.getWidget(tab);
		panel.clear();
		panel.add(getResultPanel(params));
		tabPanel.selectTab(tab);
	}
	
	private TrialBalancePanel getResultPanel(AccountingReportParams params) {
		TrialBalancePanel resultsPanel = new TrialBalancePanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), params);
		resultsPanel.addSelectionHandler( new SelectionHandler<AccountingReportParams>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingReportParams> event) {
				AccountingReportParams newParams = event.getSelectedItem();
				SimpleLayoutPanel breakdownPanel = new SimpleLayoutPanel();
				String tabLabel = "";
				String code = newParams.getAccount().getCode();
				if (AonStringUtils.length( code ) < 9 ) {
					if (newParams.getLevel() == 1) newParams.setLevel(2);
					else if (newParams.getLevel() == 2) newParams.setLevel(3);
					else if (newParams.getLevel() == 3) newParams.setLevel(4);
					else if (newParams.getLevel() == 4) newParams.setLevel(9);
					else newParams.setLevel(9);
					TrialBalancePanel breakdown = getResultPanel(newParams);
					tabLabel = "Bal S/S: (" + code + "*)";
					breakdownPanel.add(breakdown);
				} else {
					AccountingReportParams stmParams = new AccountingReportParams()
							.setDomain( newParams.getDomain() )
							.setPeriod( newParams.getPeriod() )
							.setFromDate( newParams.getFromDate() )
							.setToDate( newParams.getToDate() )
							.setActivity( newParams.getActivity() )
							.setSecurityLevel( newParams.getSecurityLevel() )
							.setAccount( newParams.getAccount().clone() )
					;
					StatementPanel statement = new StatementPanel(getCurrentDomainName(), getCurrentUser(), getCurrentDomainId(), stmParams, true);
					statement.addSelectionHandler(new AccountEntrySelectionHandler () {
						
						@Override
						public void onSelection(AccountEntrySelectionEvent  event) {
							AccountEntrySelectionEvent.fire(TrialBalancePanelReport.this, event.getSelectedItem(), event.getCallback() );
						}
					});
					
					tabLabel = "Extr: " + code;
					breakdownPanel.add(statement);
				}
				CloseTab closeTab = new CloseTab(tabLabel, true);
				closeTab.addCloseHandler(new CloseHandler<Integer>() {
					@Override
					public void onClose(CloseEvent<Integer> event) {
						tabPanel.remove(breakdownPanel); 
					}
				});
				tabPanel.add(breakdownPanel,closeTab);
				tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
			}
		});
		return resultsPanel;
	}

	public AccountingReportParams getWidgetParams() {
		return filter.getWidgetParams();
	}

}
