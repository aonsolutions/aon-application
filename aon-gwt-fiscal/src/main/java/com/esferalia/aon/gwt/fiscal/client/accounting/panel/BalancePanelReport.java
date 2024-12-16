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
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountingReportModuleOptions;
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
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;


public class BalancePanelReport extends DockLayoutPanel implements HasAccountEntrySelectionHandlers{

	private static CommonServiceAsync commonService;

	private SimpleLayoutPanel centerPanel;
	private TabLayoutPanel tabPanel;
	private BalancePanelFilter filter;
	SimpleLayoutPanel filterPanel = new SimpleLayoutPanel();

	
	public BalancePanelReport(final AccountingReportModuleOptions options) {
		this(options,null);
	}
	
	public BalancePanelReport(final AccountingReportModuleOptions options, AccountingReportParams params) {
		super(Unit.PX);
		if (options.getConfiguration() == null) {
			CommonServiceAsync commonServiceRaw = GWT.create(CommonService.class);
			commonService = new CommonServiceAsyncDecorator(commonServiceRaw);
			commonService.getAonConfiguration(options.getDomainName(), options.getDomain(), options.getUser()
				,new AsyncCallback<AonConfiguration>() {
					@Override
					public void onSuccess(AonConfiguration result) {
						fill( options.setConfiguration(result), params);
					}
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error al leer la configuraci\u00F3n");
					}
				});
		} else {
			fill (options,params);
		}
	}
	
	private void fill(final AccountingReportModuleOptions options, AccountingReportParams params) {
		if (options.getConfiguration().accounting().getPeriods() == null || options.getConfiguration().accounting().getPeriods().size() == 0 ) {
			Window.alert("No se han encontrado ejercicios contables");
		} else {
			addStyleName(AON.CSS.aonScrollArea());
			addStyleName(AON.CSS.aonMarginBottom());
			addNorth(filterPanel, 150);
			centerPanel = new SimpleLayoutPanel();
			centerPanel.setStyleName(AON.CSS.aonSelector());
			tabPanel = new TabLayoutPanel(30, Unit.PX);
			tabPanel.add(new SimpleLayoutPanel(),new CloseTab("Resultados", false));
			
			centerPanel.setWidget(tabPanel);
			add(centerPanel);
			
			filter = new BalancePanelFilter(options, params);
			filter.addMaximizeHandler(new MaximizeHandler() {
				
				@Override
				public void onMaximize(MaximizeEvent event) {
					BalancePanelReport.this.setWidgetSize(filterPanel, 150 );
					BalancePanelReport.this.animate(500, new AnimationCallback() {
						
						@Override
						public void onAnimationComplete() {
							MaximizeEvent.fire(BalancePanelReport.this);
						}

						@Override
						public void onLayout(Layer arg0, double arg1) {
						}
						
					});
					
				}
			});
			filter.addMinimizeHandler(new MinimizeHandler() {
				
				@Override
				public void onMinimize(MinimizeEvent event) {
					BalancePanelReport.this.setWidgetSize(filterPanel, 0);
					BalancePanelReport.this.animate(500, new AnimationCallback() {
						
						@Override
						public void onAnimationComplete() {
							MinimizeEvent.fire(BalancePanelReport.this);
						}

						@Override
						public void onLayout(Layer arg0, double arg1) {
						}
						
					});
					
				}
			});
			filter.addValueChangeHandler(new ValueChangeHandler<AccountingReportParams>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<AccountingReportParams> event) {
					AccountingReportParams params = event.getValue();
					onSearch(options,params,0);
				}
			});
			filterPanel.setWidget(filter);
			onSearch(options,filter.getWidgetParams(options),0);
		}
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}

	private void onSearch(final AccountingReportModuleOptions options, AccountingReportParams params, int tab) {
		SimpleLayoutPanel panel = (SimpleLayoutPanel) tabPanel.getWidget(tab);
		panel.clear();
		panel.add(getResultPanel(options,params));
		tabPanel.selectTab(tab);
	}
	
	private BalancePanel getResultPanel(final AccountingReportModuleOptions options, AccountingReportParams params) {
		BalancePanel resultsPanel = new BalancePanel(options, params);
		resultsPanel.addSelectionHandler( new SelectionHandler<AccountingReportParams>() {
			
			@Override
			public void onSelection(SelectionEvent<AccountingReportParams> event) {
				AccountingReportParams newParams = event.getSelectedItem();
				newParams.setLevel(9);
				newParams.setNoActivityAccountVisible(true);
				newParams.setLowLevelAccountVisible(true);
				TrialBalancePanel trialBalancePanel = getTrialBalanceResultPanel(options,newParams);
				SimpleLayoutPanel breakdownPanel = new SimpleLayoutPanel();
				String code = newParams.getAccount().getCode();
				String prefix = "Bal S/S: ";
				String tabLabel = prefix + AonStringUtils.abbreviate(code, 15);
				breakdownPanel.add(trialBalancePanel);
				CloseTab closeTab = new CloseTab(tabLabel, true);
				closeTab.setTitle(prefix + code);
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

	public AccountingReportParams getWidgetParams(final AccountingReportModuleOptions options) {
		return filter.getWidgetParams(options);
	}
	
	private TrialBalancePanel getTrialBalanceResultPanel(final AccountingReportModuleOptions options, AccountingReportParams params) {
		TrialBalancePanel resultsPanel = new TrialBalancePanel(options, params);
		resultsPanel.addSelectionHandler( new TrialBalanceSelectionHandler(options) );
		return resultsPanel;
	}
	
	public class TrialBalanceSelectionHandler implements SelectionHandler<AccountingReportParams> {
		private final AccountingReportModuleOptions options;
		
		public TrialBalanceSelectionHandler(AccountingReportModuleOptions options) {
			this.options = options;
		}

		@Override
		public void onSelection(SelectionEvent<AccountingReportParams> event) {
			AccountingReportParams newParams = event.getSelectedItem();
			SimpleLayoutPanel breakdownPanel = new SimpleLayoutPanel();
			String tabLabel = "";
			String prefix = "";
			String code = newParams.getAccount().getCode();
			if (AonStringUtils.length( code ) < 9 ) {
				if (newParams.getLevel() == 1) newParams.setLevel(2);
				else if (newParams.getLevel() == 2) newParams.setLevel(3);
				else if (newParams.getLevel() == 3) newParams.setLevel(4);
				else if (newParams.getLevel() == 4) newParams.setLevel(9);
				else newParams.setLevel(9);
				TrialBalancePanel breakdown = getTrialBalanceResultPanel(this.options,newParams);
				prefix = "Bal S/S: ";
				tabLabel = prefix + AonStringUtils.abbreviate(code, 15);
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
				StatementPanel statement = new StatementPanel(new AccountingReportModuleOptions()
						.setDomainName(this.options.getDomainName())
						.setDomain(this.options.getDomain())
						.setUser(this.options.getUser())
						, stmParams, true);
				statement.addSelectionHandler(new AccountEntrySelectionHandler () {
					
					@Override
					public void onSelection(AccountEntrySelectionEvent  event) {
						AccountEntrySelectionEvent.fire(BalancePanelReport.this, event.getSelectedItem(), event.getCallback() );
					}
				});
				prefix = "Extr: ";
				tabLabel = prefix + code;
				breakdownPanel.add(statement);
			}
			CloseTab closeTab = new CloseTab(tabLabel, true);
			closeTab.setTitle(prefix + code);
			closeTab.addCloseHandler(new CloseHandler<Integer>() {
				@Override
				public void onClose(CloseEvent<Integer> event) {
					tabPanel.remove(breakdownPanel); 
				}
			});
			tabPanel.add(breakdownPanel,closeTab);
			tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
		}
	}
	
	public void closeFilterPanel() {
		MinimizeEvent.fire(filter);
	}
	
	public void openFilterPanel() {
		MaximizeEvent.fire(filter);
	}
	
	public boolean isFilterPanelOpened() {
		double filterPanelSize = getWidgetSize(filterPanel);
		if (Double.compare(filterPanelSize, 0.0) == 0) {
			return false;
		}
		return true;
	}

	public HandlerRegistration addFilterMinimizeHandler(MinimizeHandler handler) {
//		return filter.addMinimizeHandler(handler);
		return addHandler(handler, MinimizeEvent.getType());
	}

	public HandlerRegistration addFilterMaximizeHandler(MaximizeHandler handler) {
//		return filter.addMaximizeHandler(handler);
		return addHandler(handler, MaximizeEvent.getType());
	}
	
	
	
}
