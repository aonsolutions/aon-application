package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ErrorPanel;
import com.esferalia.aon.gwt.fiscal.client.AnalyticAccountingService;
import com.esferalia.aon.gwt.fiscal.client.AnalyticAccountingServiceAsync;
import com.esferalia.aon.gwt.fiscal.client.AnalyticAccountingServiceAsyncDecorator;
import com.esferalia.aon.gwt.fiscal.client.accounting.panel.AnalyticalCostCenterPanel.AnalyticalCostCenterPanelCallback;
import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.AccountOperatingAccount;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalColumn;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalReport;
import com.esferalia.aon.occam.api.model.AccountingAnalyticalStatement;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.accounting.analytical.Analytical;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalAccountLevel;
import com.esferalia.aon.occam.api.model.accounting.analytical.AnalyticalCostCenter;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class AnalyticalPanel extends ScrollPanel implements HasSelectionHandlers<AccountingReportParams>{

	private static AnalyticAccountingServiceAsync SERVICE;
	
	private ErrorPanel errorContainer;
	private FlowPanel root;
	
	private String currentDomainName;
	private String currentUser;
	private Integer currentDomainId;
	private AccountingReportParams params;
	
	public AnalyticalPanel(String currentDomainName, String currentUser, Integer currentDomainId, AccountingReportParams params) {
		this.currentDomainName = currentDomainName;
		this.currentUser = currentUser;
		this.currentDomainId = currentDomainId;
		this.params = params;

		root = new FlowPanel();
		setWidget(root);
		errorContainer = new ErrorPanel();
		root.add(errorContainer);
		
		addStyleName(AON.AON_CSS.aonScrollArea());
		addStyleName(AON.AON_CSS.aonMarginBottom());		
		
		search();
		scrollToTop();
	}

	private void search() {
		clearRoot();
		
		AnalyticAccountingServiceAsync serviceRaw = GWT.create(AnalyticAccountingService.class);
		SERVICE = new AnalyticAccountingServiceAsyncDecorator(serviceRaw);
		SERVICE.getAccountAnalyticalReport(currentDomainName,currentUser,currentDomainId,params
				,  new AsyncCallback<AccountingAnalyticalReport>() {
			
			@Override
			public void onSuccess(final AccountingAnalyticalReport report) {
				paintReport( report );
			}
				
			@Override
			public void onFailure(Throwable caught) {
				errorContainer.showError(AON.MSG.noData() + " ["+caught.getMessage()+"]");
			}
		});
	}
	
	private void clearRoot() {
		root.clear();
		errorContainer.hide();
		root.add(errorContainer);
	}
	
	private void paintReport(AccountingAnalyticalReport report) {
		clearRoot();
		FlexTable tab = new FlexTable();
		tab.addStyleName(AON.AON_CSS.aonReportTable());
		if ( report.getColumns() != null && report.getColumns().size() < 5) {
			tab.addStyleName(AON.AON_CSS.aonReportTableFontBig());
		}

		int row = 0;
		int col = 1;
		
		if (report.isEmpty()) {
			tab.setWidget(0, 0, new Label(AON.MSG.noData()));
		} else {
			Button newCostCenterButton = new Button();
			newCostCenterButton.setTitle(AON.MSG.addCostCenter());
			newCostCenterButton.setText(AON.MSG.addCostCenter());
			newCostCenterButton.setStyleName(AON.AON_CSS.aonIconReset());
			newCostCenterButton.addStyleName(AON.AON_CSS.aonIconCommandButton());
			newCostCenterButton.addStyleName(AON.AON_CSS.aonMarginRight());
			newCostCenterButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showCostCenter( report, null );
				}
			});
			tab.setWidget(0, 0, newCostCenterButton );
			tab.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonTextRight());
			tab.getFlexCellFormatter().setColSpan(0, 0, 2);
			
			tab.setWidget(1, 0, new Label(AON.MSG.account()));
			tab.getCellFormatter().setStyleName(1, 0, AON.AON_CSS.aonReportTableHeader());
			tab.getFlexCellFormatter().setColSpan(1, 0, 2);

			int columnsPerColumn = 2;
			int columns = 1 + (report.getColumns().size() * columnsPerColumn);
			for (int i = 0; i < columns; i++) {
				tab.getColumnFormatter().setWidth( i, (i%2==0)?"90px":"60px");
			}
			tab.getColumnFormatter().setWidth( 1, "auto");

			int iter = 1;
			for (AccountingAnalyticalColumn column : report.getColumns()) {
				FlowPanel costCenter = new FlowPanel();
				costCenter.setStyleName(AON.AON_CSS.aonNowrap());
				costCenter.add(new InlineLabel(column.getName() + (column.isMain()?" (*)":"")));
				tab.setWidget(0, iter, costCenter);
				tab.getCellFormatter().setStyleName(0, iter, AON.AON_CSS.aonReportTableHeader());
				tab.getCellFormatter().addStyleName(0, iter, AON.AON_CSS.aonTextCenter());

				if (!column.isTotalColumn()) {
					InlineLabel editCostCenterButton = new InlineLabel("...");
					editCostCenterButton.setTitle(AON.MSG.editCostCenter());
					editCostCenterButton.setStyleName(AON.AON_CSS.aonClickableLabel());
					editCostCenterButton.addStyleName(AON.AON_CSS.aonMarginRight());
					editCostCenterButton.addStyleName(AON.AON_CSS.aonMarginLeft());
					editCostCenterButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							editCostCenter( report, report.getAnalytical().getCostCenters().get( column.getName()) );
						}
					});
					costCenter.add(editCostCenterButton);
					
					tab.getFlexCellFormatter().setColSpan(0, iter, columnsPerColumn);
					tab.setWidget(1, col  , percentWidget( report, column , null));
					tab.getCellFormatter().setStyleName(1, col, AON.AON_CSS.aonReportTableHeader());
					tab.getCellFormatter().addStyleName(1, col, AON.AON_CSS.aonTextCenter());
					col++;
				}
				
				tab.setWidget(1, col, new Label("Saldo"));
				tab.getCellFormatter().setStyleName(1, col, AON.AON_CSS.aonReportTableHeader());
				tab.getCellFormatter().addStyleName(1, col, AON.AON_CSS.aonTextCenter());
				col++;
				
				iter++;
			}
			row = 2;

			for (AccountOperatingAccount account : report.getAccounts()) {
				boolean title = account.getId() == null;
				Label codeLabel = new Label(title?"":account.getCode());
				if (!title) {
					codeLabel.setStyleName(AON.AON_CSS.aonClickableLabel());
					codeLabel.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							AccountingReportParams newParams = params.clone();
							newParams.setAccount( new Account()
									.setId(account.getId())
									.setCode(account.getCode()));
							SelectionEvent.fire(AnalyticalPanel.this, newParams );						
						}
					});
				}
				tab.setWidget(row, 0, codeLabel);
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonReportTableRowBckHover());
				
				Label descriptionLabel = new Label(account.getDescription());
				tab.setWidget(row, 1, descriptionLabel);
				tab.getRowFormatter().setStyleName(row, AON.AON_CSS.aonReportTableRowBckHover());
				
				if (title) {
					tab.getCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonTextRight());
					tab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonReportTableBold());
					tab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonFiscalPaddingRight());
					tab.getCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonNowrap());
				}

				col = 2;
				int oddColumns = 0;
				for (AccountingAnalyticalColumn column : report.getColumns()) {
					String backgroundStyle = (oddColumns%2==0)? AON.AON_CSS.aonBackgroundDisabled() : AON.AON_CSS.aonBackgroundLightYellow();
					AccountingAnalyticalStatement aos = report.get(account.getCode(),column);
					Double percent = (aos != null)?aos.getPercent() : 0.0;
					Double balance = (aos != null)?aos.getBalance() : 0.0;
					
					String percentText =  AON.FMT.format(percent) + "%";
					String balanceText = AonStringUtils.SPACE;;
					
					if (!column.isTotalColumn()) {
						if (account.getType().isCalculated()) {
							percentText = AonStringUtils.SPACE;
							tab.setWidget(row, col, new Label(percentText));
						} else {
							balance = (aos != null)?aos.getAmount() : 0.0;
							balanceText = AON.ACCOUNT_FMT.format(balance);
							tab.setWidget(row, col, percentWidget(report, column, account ));
						}
						tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
						tab.getCellFormatter().addStyleName(row, col, backgroundStyle);
						if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
						col++;
					}
					
					balanceText = AON.ACCOUNT_FMT.format(balance);
					tab.setWidget(row, col, new Label(balanceText));
					tab.getCellFormatter().setStyleName(row, col, AON.AON_CSS.aonTextRight());
					tab.getCellFormatter().addStyleName(row, col, backgroundStyle);
					if (title) tab.getCellFormatter().addStyleName(row, col, AON.AON_CSS.aonReportTableBold());
					col++;
					
					oddColumns++;
				}
				row++;	
			}
		}
		root.add(tab);
	}

	private Widget percentWidget( AccountingAnalyticalReport report, AccountingAnalyticalColumn column, AccountOperatingAccount account) {
		AccountingAnalyticalStatement aas = null;
		double perc = column.getPercent();
		if (account != null && !account.getType().isCalculated()) {
			aas = report.get(account.getCode() , column);
			perc = aas.getPercent();		
		}
		Label percentLabel = new Label();
		String percentText =  AON.FMT.format(perc) + "%";
		percentLabel.setText(percentText);
		if (aas != null && aas.isInherited()) {
			String sourceLabel = (aas.getPercentSource()==AnalyticalAccountLevel.COST_CENTER)
				?(" del centro de costo \"" + column.getName() + "\". ")
				:(" de la cuenta \"" + aas.getSourceCode() + "\". ")
			; 
			percentLabel.setTitle("El % a aplicar procede " + sourceLabel); 
			percentLabel.addStyleName(AON.AON_CSS.aonColorGreen());
		} else {
			percentLabel.setStyleName(AON.AON_CSS.aonColoRoyalblue());
		}
		if ( AonMathUtils.isLessThanZero( perc ) ) {
			percentLabel.addStyleName(AON.AON_CSS.aonIconWarn());
			percentLabel.addStyleName(AON.AON_CSS.aonColorRed());
		}
		if ( !column.isMain()) {
			FlowPanel percentPanel = new FlowPanel();
			
			percentLabel.addStyleName(AON.AON_CSS.aonClickableLabel());
			percentLabel.setTitle(percentLabel.getTitle() + AON.MSG.clickToUpdate());
			percentPanel.add(percentLabel);
			
			DoubleBox percentBox = new DoubleBox( 6 );
			percentBox.setVisible(false);
			percentBox.setStyleName(AON.AON_CSS.aonBorderNone());
			percentBox.addStyleName(AON.AON_CSS.aonWidthAll());
			percentBox.setValue(perc);
			percentBox.addValueChangeHandler( new ValueChangeHandler<Double>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Double> event) {
					Double value =  percentBox.getValue();
					if (value == null) {
						value = 0.0;
					}
					if (AonNumberUtils.isNotValid(value) || AonMathUtils.isLessThanZero(value) || AonMathUtils.isGreatherThan(value , 100) ) {
						errorContainer.showError("Valor del porcentaje incorrecto");
						percentBox.selectAll();
						percentBox.setFocus(true);
					} else {
						report.updatePercent(column,account, value);
						percentBox.setValue( value );
						saveConfiguration(report);
					}
				}
			});
			percentBox.addBlurHandler( new BlurHandler() {
				@Override
				public void onBlur(BlurEvent event) {
					percentLabel.setVisible(false);
					percentBox.setVisible(true);
				}
			});
			
			percentLabel.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					percentLabel.setVisible(false);
					percentBox.setVisible(true);
	
					Scheduler.get().scheduleDeferred(new Command() {
				        public void execute() {
				        	percentBox.selectAll();
				        	percentBox.setFocus(true);
				        }
				    });		
				}
			});
			percentPanel.add(percentBox);
			return percentPanel;
		} else {
			return percentLabel;
		}
	}

	private void showCostCenter(AccountingAnalyticalReport report, AnalyticalCostCenter cc) {
		Analytical analytical = report.getAnalytical();
		if (analytical == null) {
			analytical = new Analytical();
			report.setAnalytical(analytical);
		}

		final CustomDialog dialog = new CustomDialog();
		dialog.setCaption(AON.MSG.account());
		final AnalyticalCostCenterPanel costCenterPanel = new AnalyticalCostCenterPanel( cc, new AnalyticalCostCenterPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			@Override
			public void onCreate(AnalyticalCostCenter result) {
				dialog.hide();
				report.getAnalytical().add(result);
				report.getColumns().add( new AccountingAnalyticalColumn()
						.setName(result.getName())
						.setTotalColumn(false)
						.setPercent(result.getPercent()));
				saveConfiguration(report);
			}
			
			@Override
			public void onUpdate(String originalName, AnalyticalCostCenter result) {
				onCancel();	
			}
			
			@Override
			public void onRemove(AnalyticalCostCenter result) {
				onCancel();	
			}
			
		});
		dialog.add( costCenterPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	costCenterPanel.setFocus(true);
	        }
	    });		
	}
	
	protected void saveConfiguration(AccountingAnalyticalReport report) {
		AccountingReportParams params = report.getParams();
		SERVICE.saveConfiguration(params.getDomainName(), params.getUser(), params.getDomain(), params, report.getAnalytical(), new AsyncCallback<AccountingAnalyticalReport>() {
			
			@Override
			public void onSuccess(AccountingAnalyticalReport report) {
				paintReport(report);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				errorContainer.showError("No se han grabado los datos: " + " ["+caught.getMessage()+"]");
			}
		});
	}

	private void editCostCenter(AccountingAnalyticalReport report, AnalyticalCostCenter costCenter) {
		final CustomDialog dialog = new CustomDialog();
		dialog.setCaption(AON.MSG.account());
		final AnalyticalCostCenterPanel costCenterPanel = new AnalyticalCostCenterPanel( costCenter, new AnalyticalCostCenterPanelCallback() {
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			@Override
			public void onCreate(AnalyticalCostCenter result) {
				dialog.hide();
			}
			
			@Override
			public void onUpdate(String originalName, AnalyticalCostCenter result) {
				dialog.hide();
				AnalyticalCostCenter cc = report.getAnalytical().getCostCenters().get(originalName);
				cc.setName(result.getName());
				cc.setPercent(result.getPercent());
				cc.setMain(result.isMain());
				saveConfiguration(report);
			}
			
			@Override
			public void onRemove(AnalyticalCostCenter result) {
				dialog.hide();
				Analytical analytical = report.getAnalytical();
				analytical.remove(costCenter.getName());
				saveConfiguration(report);
			}
			
		});
		dialog.add( costCenterPanel );
		dialog.center();
		dialog.show();
		
		Scheduler.get().scheduleDeferred(new Command() {
	        public void execute() {
	        	costCenterPanel.setFocus(true);
	        }
	    });		
	}

	@Override
	public HandlerRegistration addSelectionHandler(SelectionHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, SelectionEvent.getType());
	}
}
