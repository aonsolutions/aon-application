package com.esferalia.aon.gwt.fiscal.client.accounting.amortization;

import java.util.LinkedList;
import java.util.function.Supplier;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.AonDateUtils;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayGrid;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleLabel;
import com.esferalia.aon.gwt.fiscal.client.accounting.amortization.AmortizationFormPanel.AmortizationFormPanelCallback;
import com.esferalia.aon.occam.api.model.accounting.Amortization;
import com.esferalia.aon.occam.api.model.accounting.AmortizationDetail;
import com.esferalia.aon.watson.mutable.MutableDouble;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.mutable.MutableObject;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

class AmortizationSummaryTable extends ScrollPanel {
	
	AmortizationSummaryTable( AmortizationModuleOptions opts, AmortizationFormPanelCallback cbk ) {
		setStyleName( AON.CSS.aonScrollArea() );
		setWidget( getTable(opts, cbk ) );
	}

	private LinkedList<AmortizationDetail> summary( AmortizationFormPanelCallback cbk) {
		Amortization am = cbk.getAmortization();
		if (isNew(am)) {
			return new LinkedList<>();
		} 
		LinkedList<AmortizationDetail> list = new LinkedList<>();
		MutableInt year = new MutableInt(-1);
		MutableObject<AmortizationDetail> detail = new MutableObject<>(null);
		MutableDouble accumulated = new MutableDouble(0.0);
		MutableDouble pending = new MutableDouble(am.getAmount());
		MutableDouble fiscalAccumulated = new MutableDouble(0.0);
		MutableDouble fiscalPending = new MutableDouble(am.getAmount());
		
		am.detailStream()
			.forEach( ad -> {
				int detailYear= AonDateUtils.getYear( ad.getFromDate() );
				if ( AonNumberUtils.notEquals( year.getValue() , detailYear )) {
					year.setValue( detailYear );
					AmortizationDetail det = new AmortizationDetail()
						.setDomain( ad.getDomain())
						.setAmortization( ad.getAmortization() )
						.setFromDate(ad.getFromDate());
					list.add(det);
					detail.setValue( det );
				}
				AmortizationDetail det = detail.getValue();
				
				accumulated.setValue( AonMathUtils.round(accumulated.getValue() + ad.getAllocation()));
				pending.setValue( AonMathUtils.round(pending.getValue() - ad.getAllocation()));
				fiscalAccumulated.setValue( AonMathUtils.round(fiscalAccumulated.getValue() + ad.getFiscalAllocation()));
				fiscalPending.setValue( AonMathUtils.round(fiscalPending.getValue() - ad.getFiscalAllocation()));
				
				det.setToDate(ad.getToDate());
				det.setCoefficient( AonMathUtils.round(det.getCoefficient() + ad.getCoefficient()));
				det.setAllocation( AonMathUtils.round(det.getAllocation() + ad.getAllocation()));
				det.setAccumulated( accumulated.getValue());
				det.setPending(pending.getValue());
				det.setFiscalAllocation( AonMathUtils.round(det.getFiscalAllocation() + ad.getFiscalAllocation()));
				det.setFiscalAccumulated( fiscalAccumulated.getValue());
				det.setFiscalPending(fiscalPending.getValue());
		});
		return list;
	}
	
	private Widget getTable(AmortizationModuleOptions opts, AmortizationFormPanelCallback cbk) {
		AonDisplayGrid grid = new AonDisplayGrid();
		grid.addStyleName( AON.CSS.aonBlockCenter() );
		grid.addHeaderRow()
			.addCell( new Label(AON.MSG.from()), AON.CSS.aonWidth120())
			.addCell( new Label(AON.MSG.to()), AON.CSS.aonWidth120())
			.addCell( new Label(AON.MSG.percent()), AON.CSS.aonWidth80())
			.addCell( new Label(AON.MSG.allocation()) , AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.accumulated()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.pending()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.allocation()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.accumulated()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.pending()), AON.CSS.aonWidth150())
			.addCell( new Label(AON.MSG.taxAdjust()), AON.CSS.aonWidth150())
		;
		LinkedList<AmortizationDetail> details = summary(cbk);
		AonCollectionUtils.stream( details)
		.forEach( d -> {
			addRow(opts, grid, cbk, d);
		});
		return grid;
	}
	
	private void addRow(AmortizationModuleOptions opts, AonDisplayGrid grid, AmortizationFormPanelCallback cbk, AmortizationDetail d) {
		String fromDate = ensure(d.getFromDate(), () -> AON.DATE_FORMAT.format(d.getFromDate()), AonStringUtils.EMPTY);
		Label fromDateLabel = new Label(fromDate);
		String toDate = ensure(d.getToDate(), () -> AON.DATE_FORMAT.format(d.getToDate()), AonStringUtils.EMPTY);
		Label toDateLabel = new Label(toDate);
		AonDoubleBox fiscalAllocation = new AonDoubleBox( );
		fiscalAllocation.setValue( d.getFiscalAllocation() );
		fiscalAllocation.addValueChangeHandler(v -> changeFiscalAllocation(opts, cbk, d, fiscalAllocation ) );
		grid.addRow()
			.addCell( fromDateLabel)
			.addCell( toDateLabel )
			.addCell( new AonDoubleLabel( d.getCoefficient() ) )
			.addCell( new AonDoubleLabel( d.getAllocation() ) )
			.addCell( new AonDoubleLabel( d.getAccumulated() ) )
			.addCell( new AonDoubleLabel( d.getPending() ) )
			.addCell( fiscalAllocation )
			.addCell( new AonDoubleLabel( d.getFiscalAccumulated() ) )
			.addCell( new AonDoubleLabel( d.getFiscalPending() ) )
			.addCell( new AonDoubleLabel( AonMathUtils.round(  d.getAllocation() - d.getFiscalAllocation() ) ) )
		;
	}

	private void changeFiscalAllocation(AmortizationModuleOptions opts, AmortizationFormPanelCallback cbk, AmortizationDetail d, AonDoubleBox fiscalAllocation) {
		double newFiscalAllocation = fiscalAllocation.getValue();
		AonConfirmDialog.showConfirm( "Continuar con la modificaci\u00F3n de la asignaci\u00F3n fiscal?" , new AonConfirmDialogCallback() {
			
			@Override
			public void onAccept() {
				d.setFiscalAllocation( newFiscalAllocation );
				AmortizationModule.SERVICE.saveFiscalAllocation(opts.getOccam(), d, new AsyncCallback<Amortization>() {
					@Override
					public void onSuccess(Amortization result) {
						cbk.onChange( result );
					}
					
					@Override
					public void onFailure(Throwable caught) {
						cbk.onError(caught.getMessage());;
					}
				});
			}
			
			@Override
			public void onCancel() {
				fiscalAllocation.setValue( d.getFiscalAllocation() );
			}
		});
	}

	private <T> T ensure(Object nullable, Supplier<T>  supplier, T defaultValue) {
		return (nullable == null) 
			? defaultValue
			: supplier.get();
	}
	
	private boolean isNew(Amortization am) {
		return am.getId() == null;
	}

}
