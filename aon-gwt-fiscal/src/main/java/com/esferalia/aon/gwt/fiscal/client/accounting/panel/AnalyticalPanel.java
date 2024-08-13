package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.logging.Logger;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonErrorPanel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
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
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.logging.client.ConsoleLogHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;


public class AnalyticalPanel extends ScrollPanel implements HasSelectionHandlers<AccountingReportParams>{

	private static final Logger LOGGER = Logger.getLogger(AnalyticalPanel.class.getName());
	static {
		LOGGER.addHandler( new ConsoleLogHandler() );
	}

	private static AnalyticAccountingServiceAsync SERVICE;
	
	private AonErrorPanel errorContainer;
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
		errorContainer = new AonErrorPanel();
		root.add(errorContainer);
		
		addStyleName(AON.CSS.aonScrollArea());
		addStyleName(AON.CSS.aonMarginBottom());		
		
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
		tab.addStyleName(AON.CSS.aonGrid());

		int row = 0;
		int col = 1;
		
		if (report.isEmpty()) {
			tab.setWidget(0, 0, new Label(AON.MSG.noData()));
		} else {
			AonTableButton newCostCenterButton = new AonTableButton(AON.MSG.addCostCenter(),AON.CSS.aonIconAdd());
			newCostCenterButton.setWidth("auto");
			newCostCenterButton.getElement().getStyle().setPaddingLeft(20, Unit.PX);
			newCostCenterButton.getElement().getStyle().setProperty("background-position","left center");
			newCostCenterButton.setText(AON.MSG.addCostCenter());
			newCostCenterButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Analytical analytical = report.getAnalytical();
					if (analytical == null) {
						analytical = new Analytical();
						report.setAnalytical(analytical);
					}
					editCostCenter( report, null );
				}
			});
			tab.setWidget(0, 0, newCostCenterButton );
			tab.getCellFormatter().setStyleName(0, 0, AON.CSS.aonTextRight());
			tab.getFlexCellFormatter().setColSpan(0, 0, 2);
			
			tab.setWidget(1, 0, new Label(AON.MSG.account()));
			tab.getCellFormatter().setStyleName(1, 0, AON.CSS.aonGridHeader());
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
				costCenter.setStyleName(AON.CSS.aonNowrap());
				costCenter.setStyleName(AON.CSS.aonFlexBlock());
				Label name = new Label(column.getName() + (column.isMain()?" (*)":""));
				name.setStyleName(AON.CSS.aonFlexGrow1());
				name.addStyleName(AON.CSS.aonTextCenter());
				costCenter.add(name);
				tab.setWidget(0, iter, costCenter);
				tab.getCellFormatter().setStyleName(0, iter, AON.CSS.aonGridHeader());
				tab.getCellFormatter().addStyleName(0, iter, AON.CSS.aonTextCenter());

				if (!column.isTotalColumn()) {
					AonTableButton editCostCenterButton = new AonTableButton(AON.MSG.editCostCenter(),AON.CSS.aonIconMoreVertical());
					editCostCenterButton.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							editCostCenter( report, report.getAnalytical().getCostCenters().get( column.getName()) );
						}
					});
					costCenter.add(editCostCenterButton);
					
					tab.getFlexCellFormatter().setColSpan(0, iter, columnsPerColumn);
					tab.setWidget(1, col  , percentWidget( report, column , null));
					tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
					tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
					col++;
				}
				
				tab.setWidget(1, col, new Label("Saldo"));
				tab.getCellFormatter().setStyleName(1, col, AON.CSS.aonGridHeader());
				tab.getCellFormatter().addStyleName(1, col, AON.CSS.aonTextCenter());
				col++;
				
				iter++;
			}
			row = 2;

			for (AccountOperatingAccount account : report.getAccounts()) {
				boolean title = account.getId() == null;
				Label codeLabel = new Label(title?"":account.getCode());
				if (!title) {
					codeLabel.setStyleName(AON.CSS.aonClickableLabel());
					codeLabel.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							AccountingReportParams newParams = params.copy();
							newParams.setAccount( new Account()
									.setId(account.getId())
									.setCode(account.getCode()));
							SelectionEvent.fire(AnalyticalPanel.this, newParams );						
						}
					});
				}
				tab.setWidget(row, 0, codeLabel);
				
				Label descriptionLabel = new Label(account.getDescription());
				tab.setWidget(row, 1, descriptionLabel);
				
				if (title) {
					tab.getCellFormatter().setStyleName(row, 1, AON.CSS.aonTextRight());
					tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonBold());
					tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonPaddingRight());
					tab.getCellFormatter().addStyleName(row, 1, AON.CSS.aonNowrap());
				}

				col = 2;
				int oddColumns = 0;
				for (AccountingAnalyticalColumn column : report.getColumns()) {
					String backgroundStyle = (oddColumns%2==0)? AON.CSS.aonBackgroundLigthGray() : AON.CSS.aonBackgroundLigthYellow();
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
						tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
						tab.getCellFormatter().addStyleName(row, col, backgroundStyle);
						if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
						col++;
					}
					
					balanceText = AON.ACCOUNT_FMT.format(balance);
					tab.setWidget(row, col, new Label(balanceText));
					tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
					tab.getCellFormatter().addStyleName(row, col, backgroundStyle);
					if (title) tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBold());
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
			percentLabel.addStyleName(AON.CSS.aonColorGreen());
		} else {
			percentLabel.setStyleName(AON.CSS.aonColorBlue());
		}
		if ( AonMathUtils.isLessThanZero( perc ) ) {
			percentLabel.addStyleName(AON.CSS.aonTabIcon());
			percentLabel.addStyleName(AON.CSS.aonIconWarning());
			percentLabel.addStyleName(AON.CSS.aonColorRed());
		}
		if ( !column.isMain()) {
			FlowPanel percentPanel = new FlowPanel();
			
			percentLabel.addStyleName(AON.CSS.aonClickableLabel());
			percentLabel.setTitle(percentLabel.getTitle() + AON.MSG.clickToUpdate());
			percentPanel.add(percentLabel);
			
			AonDoubleBox percentBox = new AonDoubleBox( 6 );
			percentBox.setVisible(false);
			percentBox.setStyleName(AON.CSS.aonBorderNone());
			percentBox.addStyleName(AON.CSS.aonWidthAll());
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
		final AonCustomDialog dialog = new AonCustomDialog();
		dialog.setCaption(AON.MSG.costCenter());
		final AnalyticalCostCenterPanel costCenterPanel = new AnalyticalCostCenterPanel( costCenter, new AnalyticalCostCenterPanelCallback() {
			
			@Override
			public Analytical getAnalytical() {
				return report.getAnalytical();
			}
			
			@Override
			public void onCancel() {
				dialog.hide();
			}
			
			@Override
			public void onCreate(AnalyticalCostCenter result) {
				dialog.hide();
				getAnalytical().add(result);
				if (result.isMain()) {
					getAnalytical().setDefaultCostCenter(result.getName());
				}
				checkTotal( );
				checkDefault( result );
				report.getColumns().add( new AccountingAnalyticalColumn()
						.setName(result.getName())
						.setTotalColumn(false)
						.setPercent(result.getPercent())
						.setDefaultColumn(result.isMain()));
				saveConfiguration(report);
			}
			
			private void checkTotal() {
				double mainPercent = 100;
				for (AnalyticalCostCenter costCenter : getAnalytical().getCostCenters().values() ) {
					if (!costCenter.isMain()) {
						mainPercent = AonMathUtils.round( mainPercent -  costCenter.getPercent( ));
					}
				}
				for (AnalyticalCostCenter costCenter : getAnalytical().getCostCenters().values() ) {
					if (costCenter.isMain()) {
						costCenter.setPercent(mainPercent);
					}
				}
				
			}

			@Override
			public void onUpdate(String originalName, AnalyticalCostCenter result) {
				dialog.hide();
				AnalyticalCostCenter cc = getAnalytical().getCostCenters().get(originalName);
				cc.setName(result.getName());
				cc.setPercent(result.getPercent());
				cc.setMain(result.isMain());
				if (AonStringUtils.equals(originalName, getAnalytical().getDefaultCostCenter())) {
					getAnalytical().setDefaultCostCenter( cc.isMain()? result.getName() : null);
				}
				checkDefault( cc );
				checkTotal( );
				saveConfiguration(report);
			}
			
			@Override
			public void onRemove(AnalyticalCostCenter result) {
				dialog.hide();
				getAnalytical().remove(costCenter.getName());
				if (result.isMain()) {
					if (getAnalytical().getCostCenters().size() > 0) {
						for (AnalyticalCostCenter costCenter : getAnalytical().getCostCenters().values() ) {
							costCenter.setMain(true);
							getAnalytical().setDefaultCostCenter(costCenter.getName());
							break;
						}
					}
				}
				checkTotal( );
				saveConfiguration(report);
			}
			
			private void checkDefault(AnalyticalCostCenter cc) {
				LOGGER.info("Default Cost Center ..: " + getAnalytical().getDefaultCostCenter());
				LOGGER.info("isMain ..: " + cc.isMain());
				
				if (!cc.isMain()) {
					if (getAnalytical().getCostCenters().size() == 1) {
						cc.setMain(true);
						getAnalytical().setDefaultCostCenter(cc.getName());
					} else {
						boolean hasMain = false;
						for (AnalyticalCostCenter costCenter : getAnalytical().getCostCenters().values() ) {
							if (costCenter.isMain()) {
								hasMain = true;
								break;
							}
						}
						if (!hasMain) {
							for (AnalyticalCostCenter costCenter : getAnalytical().getCostCenters().values() ) {
								if (!AonStringUtils.equals( costCenter.getName(), cc.getName())) {
									costCenter.setMain(true);
									getAnalytical().setDefaultCostCenter(cc.getName());
									break;
								}		
							}
						}
					}
				} else {
					for (AnalyticalCostCenter costCenter : getAnalytical().getCostCenters().values() ) {
						if (AonStringUtils.equals( costCenter.getName(), cc.getName())) {
							costCenter.setMain(true);
							getAnalytical().setDefaultCostCenter(cc.getName());			
						} else {
							costCenter.setMain(false);
						}
					}
				}
				
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
