package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.event.AonCheckedEvent;
import com.esferalia.aon.gwt.common.client.event.AonCheckedHandler;
import com.esferalia.aon.gwt.common.client.event.AonUncheckedEvent;
import com.esferalia.aon.gwt.common.client.event.AonUncheckedHandler;
import com.esferalia.aon.gwt.common.client.event.HasCheckedHandlers;
import com.esferalia.aon.gwt.common.client.event.HasUncheckedHandlers;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable.AonFlexTableRow;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonMessageToast;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.accounting.AccountingAmortization;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.eccounting.AmortizationParams;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus.AmortizationDetailStatusVisitor;
import com.esferalia.aon.watson.mutable.MutableBoolean;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

class AccountingAmortizationDetailTable extends ScrollPanel implements HasCheckedHandlers<AccountingAmortization>, HasUncheckedHandlers<AccountingAmortization>{

	private FlowPanel containerPanel = new FlowPanel();
	private AonFlexTable grid = new AonFlexTable(COLUMN_WIDTHS, AON.CSS.aonBlockCenter());

	private static final int LIMIT = 50;
	private final MutableInt offset = new MutableInt(0);
	private final MutableBoolean moreData = new MutableBoolean(true);
	private final MutableBoolean searchEnabled = new MutableBoolean( true );
	private int lastScrollPos = 0;
	
	private static final String[] COLUMN_WIDTHS = new String[] {
		 "40px"
		,"1fr"
		,"120px"
		,"120px"
		,"120px"
		,"80px"
		,"120px"
		,"120px"
		,"120px"
		,"100px"
		,"100px" 
	};
	
	AccountingAmortizationDetailTable( AmortizationModuleOptions opts, AmortizationParams params) {
		setStyleName( AON.CSS.aonScrollArea() );
		addStyleName(AON.CSS.aonMarginTop());

		setWidget(containerPanel);
		
		containerPanel.add(grid);
		fillHeader( );

		addScrollHandler(event -> {
			// ------------------------------------ Ignore scroll up.
			int oldScrollPos = lastScrollPos;
			lastScrollPos = getVerticalScrollPosition();
			if (oldScrollPos >= lastScrollPos) {
				return;
			}
			// -----------------------------------------------------
			if (isSearchEnabled()) {
				int maxScrollTop = getWidget().getOffsetHeight() - getOffsetHeight();
				if (lastScrollPos >= maxScrollTop) {
					disableSearch();
					onSearch(opts, params);
				}
			}
		});
		
		onSearch(opts, params);
	}
	
	private boolean isSearchEnabled() {
		return searchEnabled.isTrue();
	}
	private void disableSearch() {
		searchEnabled.setValue(false);
	}
	private void enableSearch() {
		searchEnabled.setValue(true);
	}
	private boolean isMoreData() {
		return moreData.isTrue();
	}
	private void enableMoreData() {
		moreData.setValue(true);
	}
	private void disableMoreData() {
		moreData.setValue(false);
	}
	
	private void fillHeader() {
		grid
			.addHeaderCell( new Label())
			.addHeaderCell( new Label(AON.MSG.description()))
			.addHeaderCell( new Label(AON.MSG.from()))
			.addHeaderCell( new Label(AON.MSG.until()))
			.addHeaderCell( new Label(AON.MSG.amount()), AON.CSS.aonTextRight())
			.addHeaderCell( new Label(AON.MSG.percent()), AON.CSS.aonTextRight())
			.addHeaderCell( new Label(AON.MSG.allocation()), AON.CSS.aonTextRight())
			.addHeaderCell( new Label(AON.MSG.accumulated()), AON.CSS.aonTextRight())
			.addHeaderCell( new Label(AON.MSG.pending()), AON.CSS.aonTextRight())
			.addHeaderCell( new Label(AON.MSG.status()))
			.addHeaderCell( new Label(""))
		;
	}

	private void onSearch(AmortizationModuleOptions opts, AmortizationParams params) {
		if (!isMoreData()) return;
		params.setOffset( offset.getValue() );
		params.setLimit( LIMIT );
		AccountingAmortizationModule.SERVICE.getAccountingAmortizations(opts.getOccam(), opts.getDomain(), params
			, new AsyncCallback<LinkedList<AccountingAmortization>>() {
				@Override
				public void onFailure(Throwable ex) {
					AonMessageToast.showError( "ERROR: " + ex.getMessage() );
				}

				@Override
				public void onSuccess(LinkedList<AccountingAmortization> list) {
					int size = AonCollectionUtils.size(list);
					AonCollectionUtils.stream(list)
					.forEach(aa -> {
						AonFlexTableRow row = grid.addRow();
						fillRow(opts, row, aa);
					});

					offset.add( size );
					enableMoreData();
					if ( size < params.getLimit() ) {
						FlowPanel line = new FlowPanel();
						line.setStyleName(AON.CSS.aonTextCenter());
						if (offset.getValue() > 0) {
							line.add(new InlineLabel(AON.MSG.noMoreData()));
						} else {
							line.add(new InlineLabel(AON.MSG.noData()));
						}
						containerPanel.add(line);
						disableMoreData();
					}
					enableSearch();
				}
		});
	}
	
	private void uncheckAndfillRow(AmortizationModuleOptions opts, AonFlexTableRow row, AccountingAmortization aa) {
		AonUncheckedEvent.fire(AccountingAmortizationDetailTable.this, aa);
		fillRow(opts, row, aa);			
	}
	private void fillRow(AmortizationModuleOptions opts, AonFlexTableRow row, AccountingAmortization aa) {
		row.clear();
		Amortization a = aa.getAmortization();
		AmortizationDetail d = aa.getDetail();
		String fromDate = ensure(d.getFromDate(), () -> AON.DATE_FORMAT.format(d.getFromDate()), AonStringUtils.EMPTY);
		Label fromDateLabel = new Label(fromDate);
		String toDate = ensure(d.getToDate(), () -> AON.DATE_FORMAT.format(d.getToDate()), AonStringUtils.EMPTY);
		Label toDateLabel = new Label(toDate);
		row
			.addCell( new AonCheckButton( aa ) )
			.addCell( new Label( a.getDescription() ) )
			.addCell( fromDateLabel)
			.addCell( toDateLabel )
			.addCell( new AonDoubleLabel( a.getAmount() ) , AON.CSS.aonTextRight())
			.addCell( new AonDoubleLabel( d.getCoefficient() ) , AON.CSS.aonTextRight())
			.addCell( new AonDoubleLabel( d.getAllocation() ) , AON.CSS.aonTextRight())
			.addCell( new AonDoubleLabel( d.getAccumulated() ) , AON.CSS.aonTextRight())
			.addCell( new AonDoubleLabel( d.getPending() ) , AON.CSS.aonTextRight())
			.addCell( new Label( ensure(d.getStatus(), () -> d.getStatus().getDescription() , "") ) )
			.addCell( getActionsPanel( opts, row, aa) )
		;
	}

	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	private FlowPanel getActionsPanel(AmortizationModuleOptions opts, AonFlexTableRow row, AccountingAmortization aa) {
		FlowPanel actionsPanel = new FlowPanel();
		actionsPanel.setStyleName( AON.CSS.aonNowrap() );
		AmortizationDetail d = aa.getDetail();
		if (d.getStatus() == null) return actionsPanel;
		Amortization a = aa.getAmortization();
		d.getStatus().accept( new AmortizationDetailStatusVisitor() {

			@Override
			public void visitPending() {
				AonTableButton blockButton = new AonTableButton(AON.MSG.blockAction(), AON.CSS.aonIconUnLock());
				blockButton.addStyleName( AON.CSS.aonMarginLeft());
				blockButton.addClickHandler( e -> {
					blockButton.setEnabled(false);
					AonConfirmDialog.showConfirm(AON.MSG.blockAction()
						, AON.MSG.confirmBlockAction()
						, new AonConfirmDialogCallback() {
						
							@Override
							public void onCancel() {
								blockButton.setEnabled(true);
							}
							
							@Override
							public void onAccept() {
								AmortizationModule.SERVICE.blockDetail(opts.getOccam()
									, d
									, new AsyncCallback<AmortizationDetail>() {
										@Override
										public void onFailure(Throwable ex) {
											AonMessageToast.showError( "ERROR: " + ex.getMessage() );
											blockButton.setEnabled(true);
										}
										
										@Override
										public void onSuccess(AmortizationDetail det) {
											aa.setDetail(det);
											uncheckAndfillRow(opts, row, aa);
										}
									}
								);
							}

						});
				});
				actionsPanel.add( blockButton );
				
				AonTableButton recordButton = new AonTableButton( AON.MSG.record(), AON.CSS.aonIconAddTask());
				recordButton.addStyleName( AON.CSS.aonMarginLeft());
				recordButton.addClickHandler( e -> {
					recordButton.setEnabled(false);
					AonConfirmDialog.showConfirm(AON.MSG.recordAction()
						, AON.MSG.confirmRecordAction()
						, new AonConfirmDialogCallback() {
						
							@Override
							public void onCancel() {
								recordButton.setEnabled(true);
							}
							
							@Override
							public void onAccept() {
								AmortizationModule.SERVICE.recordAllocation( opts.getOccam()
										, a
										, d
										, new AsyncCallback<AmortizationDetail>() {
											@Override
											public void onFailure(Throwable ex) {
												AonMessageToast.showError( "ERROR: " + ex.getMessage() );
												recordButton.setEnabled(true);
											}
						
											@Override
											public void onSuccess(AmortizationDetail det) {
												aa.setDetail(det);
												uncheckAndfillRow(opts, row, aa);
											}
										}
									);
							}
					});
				});
				actionsPanel.add( recordButton );
			}

			@Override
			public void visitScored() {
				Label entryLabel = new Label();
				entryLabel.setStyleName( AON.CSS.aonIconLabel());
				entryLabel.addStyleName( AON.CSS.aonMarginLeft());
				entryLabel.addStyleName( AON.CSS.aonIconLink());
				entryLabel.addStyleName( AON.CSS.aonClickable());
				entryLabel.setTitle( AON.MSG.viewAccountEntry() );
				entryLabel.addClickHandler( e -> showEntry(opts, d.getAccountEntry()) );
				actionsPanel.add( entryLabel );
				
				AonTableButton unrecordButton = new AonTableButton(AON.MSG.unrecord() , AON.CSS.aonIconCancelCircle());
				unrecordButton.addStyleName( AON.CSS.aonMarginLeft());
				unrecordButton.addClickHandler( e -> {
					unrecordButton.setEnabled(false);
					AonConfirmDialog.showConfirm(AON.MSG.unrecordAction()
						, AON.MSG.confirmUnrecordAction()
						, new AonConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
								unrecordButton.setEnabled(true);
							}
							
							@Override
							public void onAccept() {
								AmortizationModule.SERVICE.unrecordAllocation( opts.getOccam(), d, new AsyncCallback<AmortizationDetail>() {
									@Override
									public void onFailure(Throwable ex) {
										AonMessageToast.showError( "ERROR: " + ex.getMessage() );
										unrecordButton.setEnabled(true);
									}
				
									@Override
									public void onSuccess(AmortizationDetail det) {
										aa.setDetail(det);
										uncheckAndfillRow(opts, row, aa);
									}
								});
							}
					});
				});
				actionsPanel.add( unrecordButton );
				
			}

			@Override
			public void visitBlocked() {
				AonTableButton unblockButton = new AonTableButton(AON.MSG.unblockAction(), AON.CSS.aonIconLock());
				unblockButton.addStyleName( AON.CSS.aonMarginLeft());
				unblockButton.addClickHandler( e -> { 
					unblockButton.setEnabled(false);
					AonConfirmDialog.showConfirm(AON.MSG.unblockAction()
						, AON.MSG.confirmUnblockAction()
						, new AonConfirmDialogCallback() {
							
							@Override
							public void onCancel() {
								unblockButton.setEnabled(true);
							}
							
							@Override
							public void onAccept() {
								AmortizationModule.SERVICE.unblockDetail(opts.getOccam()
									, d
									, new AsyncCallback<AmortizationDetail>() {
										@Override
										public void onFailure(Throwable ex) {
											unblockButton.setEnabled(true);
											AonMessageToast.showError( "ERROR: " + ex.getMessage() );
										}
					
										@Override
										public void onSuccess(AmortizationDetail det) {
											aa.setDetail(det);
											uncheckAndfillRow(opts, row, aa);
										}
									}
								);
							}
					});
				});
				actionsPanel.add( unblockButton );
			}
		});
		
		return actionsPanel;
	}
	
	private void showEntry(AmortizationModuleOptions opts, Integer entryId) {
		AonCustomPopup entryDialog = new AonCustomPopup();
		entryDialog.setWidth((Window.getClientWidth() - 100) + "px");
		entryDialog.setHeight((Window.getClientHeight() - 100) + "px");
		entryDialog.setAnimationEnabled(true);
		entryDialog.setGlassEnabled(true);
		entryDialog.setModal(true);
		entryDialog.setCaption(AON.MSG.accountEntries());
		AccountEntryModule module = new AccountEntryModule();
		module.onModuleLoad(new AccountEntryModuleOptions()
			.setParentWidget(entryDialog)
			.setDomainName(opts.getDomainName())
			.setUser(opts.getUser())
			.setDomain(opts.getDomain())
			.setAccountEntryId(entryId)
			.setSessionLogTabVisible(false)
			.setJournalTabVisible(false)
			.setExtraInfoTabVisible(false)
			.setExternalCallback( new ModuleCallback() {
				
				private static final long serialVersionUID = -7040645114567563036L;

				@Override
				public void onRemove(IAccountEntryWrapper removed) {
					entryDialog.hide();
				}
				
				@Override
				public void onFailure(Throwable caught) {
					entryDialog.hide();
				}
				
				@Override
				public void onExit() {
					entryDialog.hide();
				}
				
				@Override
				public void onChange(IAccountEntryWrapper changed) {
					entryDialog.hide();
				}
			})
		);
		entryDialog.center();
		entryDialog.show();
	}
	
	@Override
	public HandlerRegistration addCheckedHandler(AonCheckedHandler<AccountingAmortization> handler) {
		return super.addHandler(handler, AonCheckedEvent.getType());
	}

	@Override
	public HandlerRegistration addUncheckedHandler(AonUncheckedHandler<AccountingAmortization> handler) {
		return super.addHandler(handler, AonUncheckedEvent.getType());
	}

	private class AonCheckButton extends AonTableButton {
		private boolean checked = false;
		
		public AonCheckButton( AccountingAmortization aa) {
			super("",AON.CSS.aonIconCheck());
			addClickHandler( event -> {
				checked = !checked;
				if (checked) {
					this.addStyleName(AON.CSS.aonIconChecked());
					this.removeStyleName(AON.CSS.aonIconCheck());
					AonCheckedEvent.fire(AccountingAmortizationDetailTable.this, aa);
				} else {
					this.addStyleName(AON.CSS.aonIconCheck());
					this.removeStyleName(AON.CSS.aonIconChecked());
					AonUncheckedEvent.fire(AccountingAmortizationDetailTable.this, aa);
				}
			});
		}

	}
}
