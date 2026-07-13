package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.ModuleCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomPopup;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonFlexTable.AonFlexTableRow;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModule;
import com.esferalia.aon.gwt.fiscal.client.accounting.AccountEntryModuleOptions;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationPanel.AmortizationPanelCallback;
import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus.AmortizationDetailStatusVisitor;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class AmortizationDetailTable extends ScrollPanel {

	private static final String[] COLUMN_WIDTHS = new String[] {
			"120px","120px","80px","150px","150px","150px","150px","150px","150px","150px","100px","100px" 
	};
	
	AmortizationDetailTable( AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		setStyleName( AON.CSS.aonScrollArea() );
		addStyleName(AON.CSS.aonMarginTop());
		setWidget( getTable(opts, callback) );
	}

	private Widget getTable(AmortizationModuleOptions opts, AmortizationPanelCallback callback) {
		AonFlexTable grid = new AonFlexTable(COLUMN_WIDTHS, AON.CSS.aonBlockCenter());
		
		grid
			.addHeaderCell( new Label(AON.MSG.from()))
			.addHeaderCell( new Label(AON.MSG.until()))
			.addHeaderCell( new Label(AON.MSG.percent()))
			.addHeaderCell( new Label(AON.MSG.allocation()))
			.addHeaderCell( new Label(AON.MSG.accumulated()))
			.addHeaderCell( new Label(AON.MSG.pending()))
			.addHeaderCell( new Label(AON.MSG.allocation()))
			.addHeaderCell( new Label(AON.MSG.accumulated()))
			.addHeaderCell( new Label(AON.MSG.pending()))
			.addHeaderCell( new Label(AON.MSG.taxAdjust()))
			.addHeaderCell( new Label(AON.MSG.status()))
			.addHeaderCell( new Label(""))
		;
		callback.getAmortization().detailStream()
			.forEach( d -> {
				AonFlexTableRow row = grid.addRow();
				fillRow(opts, callback, row, d);
			});
		;
		return grid;
	}
	
	private void fillRow(AmortizationModuleOptions opts, AmortizationPanelCallback callback, AonFlexTableRow row, AmortizationDetail d) {
		row	.clear();
		String fromDate = ensure(d.getFromDate(), () -> AON.DATE_FORMAT.format(d.getFromDate()), AonStringUtils.EMPTY);
		Label fromDateLabel = new Label(fromDate);
		String toDate = ensure(d.getToDate(), () -> AON.DATE_FORMAT.format(d.getToDate()), AonStringUtils.EMPTY);
		Label toDateLabel = new Label(toDate);
		row
			.addCell( fromDateLabel)
			.addCell( toDateLabel )
			.addCell( new AonDoubleLabel( d.getCoefficient() ) )
			.addCell( new AonDoubleLabel( d.getAllocation() ) )
			.addCell( new AonDoubleLabel( d.getAccumulated() ) )
			.addCell( new AonDoubleLabel( d.getPending() ) )
			.addCell( new AonDoubleLabel( d.getFiscalAllocation() ) )
			.addCell( new AonDoubleLabel( d.getFiscalAccumulated() ) )
			.addCell( new AonDoubleLabel( d.getFiscalPending() ) )
			.addCell( new AonDoubleLabel( AonMathUtils.round(  d.getAllocation() - d.getFiscalAllocation() ) ) )
			.addCell( new Label( ensure(d.getStatus(), () -> d.getStatus().getDescription() , "") ) )
			.addCell( getActionsPanel( opts, callback, row, d) )
		;
	}

	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}

	private FlowPanel getActionsPanel(AmortizationModuleOptions opts, AmortizationPanelCallback callback, AonFlexTableRow row, AmortizationDetail d) {
		FlowPanel actionsPanel = new FlowPanel();
		actionsPanel.setStyleName( AON.CSS.aonNowrap() );
		d.getStatus().accept( new AmortizationDetailStatusVisitor() {

			@Override
			public void visitPending() {
				Label blockLabel = new Label();
				blockLabel.setStyleName( AON.CSS.aonIconLabel());
				blockLabel.addStyleName( AON.CSS.aonMarginLeft());
				blockLabel.addStyleName( AON.CSS.aonIconUnLock());
				blockLabel.addStyleName( AON.CSS.aonClickable() );
				blockLabel.setTitle( AON.MSG.blockAction() );
				blockLabel.addClickHandler( e -> 
					AmortizationModule.SERVICE.blockDetail(opts.getOccam()
						, d
						, new AsyncCallback<AmortizationDetail>() {
							@Override
							public void onFailure(Throwable ex) {
								callback.showError( "ERROR: " + ex.getMessage() );
							}
		
							@Override
							public void onSuccess(AmortizationDetail det) {
								fillRow(opts, callback, row, det);
							}
						}
					)
				);
				actionsPanel.add( blockLabel );
				
				Label recordLabel = new Label();
				recordLabel.setStyleName( AON.CSS.aonIconLabel());
				recordLabel.addStyleName( AON.CSS.aonMarginLeft());
				recordLabel.addStyleName( AON.CSS.aonIconAddTask());
				recordLabel.addStyleName( AON.CSS.aonClickable());
				recordLabel.setTitle( AON.MSG.record() );
				recordLabel.addClickHandler( e -> 
					AmortizationModule.SERVICE.recordAllocation( opts.getOccam()
						, callback.getAmortization()
						, d
						, new AsyncCallback<AmortizationDetail>() {
							@Override
							public void onFailure(Throwable ex) {
								callback.showError( "ERROR: " + ex.getMessage() );
							}
		
							@Override
							public void onSuccess(AmortizationDetail det) {
								fillRow(opts, callback, row, det);
							}
						}
					)
				);
				actionsPanel.add( recordLabel );
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
				
				Label unrecordLabel = new Label();
				unrecordLabel.setStyleName( AON.CSS.aonIconLabel());
				unrecordLabel.addStyleName( AON.CSS.aonMarginLeft());
				unrecordLabel.addStyleName( AON.CSS.aonIconCancelCircle());
				entryLabel.addStyleName( AON.CSS.aonClickable());
				unrecordLabel.setTitle( AON.MSG.unrecord() );
				unrecordLabel.addClickHandler( e -> {
					AmortizationModule.SERVICE.unrecordAllocation( opts.getOccam(), d, new AsyncCallback<AmortizationDetail>() {
						@Override
						public void onFailure(Throwable ex) {
							callback.showError( "ERROR: " + ex.getMessage() );
						}
	
						@Override
						public void onSuccess(AmortizationDetail det) {
							fillRow(opts, callback, row, det);
						}
					});
				});
				actionsPanel.add( unrecordLabel );
				
			}

			@Override
			public void visitBlocked() {
				Label unblockLabel = new Label();
				unblockLabel.setStyleName( AON.CSS.aonIconLabel());
				unblockLabel.addStyleName( AON.CSS.aonMarginLeft());
				unblockLabel.addStyleName( AON.CSS.aonIconLock());
				unblockLabel.setTitle( AON.MSG.unblockAction() );
				unblockLabel.addClickHandler( e -> 
					AmortizationModule.SERVICE.unblockDetail(opts.getOccam()
						, d
						, new AsyncCallback<AmortizationDetail>() {
							@Override
							public void onFailure(Throwable ex) {
								callback.showError( "ERROR: " + ex.getMessage() );
							}
		
							@Override
							public void onSuccess(AmortizationDetail det) {
								fillRow(opts, callback, row, det);
							}
						}
					)
				);
				actionsPanel.add( unblockLabel );
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
	
}
