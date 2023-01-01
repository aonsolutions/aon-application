package com.esferalia.aon.gwt.fiscal.client.invoice.vat;

import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.Pair;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class VatReportSummaryPanel extends ScrollPanel implements HasValueChangeHandlers<AccountingReportParams>{
	
	static interface VatReportSummaryPanelCallback {
		void refreshOutputFilter( Boolean output);
		void refreshVatSummaryTypeFilter(VatSummaryType type);
		void refreshPercentFilter(Double percent);
		void refreshSurchargePercentFilter(Double surchargePercent);
	}
	
	VatReportSummaryPanel(AccountingReportParams params, VatReportSummaryPanelCallback callback, LinkedList<VatSummaryContext> data) {
		TreeMap<VatSummaryType,TreeMap<Double,Pair<VatSummaryContext, VatSummaryContext>>> map = sort( data );
		
		FlexTable tab = new FlexTable();
		tab.setCellPadding(0);
		tab.setCellSpacing(0);
		tab.setStyleName(AON.CSS.aonBlockCenter());
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonBorderCollapse());
		
		tab.getColumnFormatter().setStyleName(0, AON.CSS.aonWidth150());
		
		tab.getColumnFormatter().setStyleName(1, AON.CSS.aonWidth120());
		tab.getColumnFormatter().setStyleName(2, AON.CSS.aonWidth60());
		tab.getColumnFormatter().setStyleName(3, AON.CSS.aonWidth120());
		
		tab.getColumnFormatter().setStyleName(4, AON.CSS.aonWidth20());
		
		tab.getColumnFormatter().setStyleName(5, AON.CSS.aonWidth120());
		tab.getColumnFormatter().setStyleName(6, AON.CSS.aonWidth60());
		tab.getColumnFormatter().setStyleName(7, AON.CSS.aonWidth120());
		tab.getColumnFormatter().setStyleName(8, AON.CSS.aonWidth120());
		
		paintTableHeader(params, callback, tab);
		
		double outputBase = 0;
		double outputQuota = 0;
		double inputBase = 0;
		double inputQuota = 0;
		double inputDeductibleQuota = 0;

		double estimation = 0;

		int row = 3;
		for (Entry<VatSummaryType, TreeMap<Double, Pair<VatSummaryContext, VatSummaryContext>>> entry :  map.entrySet() ) {
			row = paintEmptyRow(tab,row);
			VatSummaryType type = entry.getKey();
			TreeMap<Double, Pair<VatSummaryContext, VatSummaryContext>> value = entry.getValue();
			Label typeLabel = new Label(type.getDescription());
			typeLabel.addClickHandler(event -> {
					callback.refreshVatSummaryTypeFilter(type);
					callback.refreshOutputFilter(null);
					callback.refreshPercentFilter(null);
					callback.refreshSurchargePercentFilter(null);
					onClick(params
						.setVatSummaryType(type)
						.setOutput(null)
						.setPercent(null)
						.setSurchargePercent(null));	
			});
			tab.setWidget(row,0, typeLabel);
			int rowspan = value.values().size();
			tab.getFlexCellFormatter().setRowSpan(row, 0, rowspan+1);
			giveHeaderStyle(tab, row, 0, AON.CSS.aonTextCenter());
			
			boolean first = true;

			double typeOutputBase = 0;
			double typeOutputQuota = 0;
			double typeInputBase = 0;
			double typeInputQuota = 0;
			double typeInputDeductibleQuota = 0;

			for (Pair<VatSummaryContext,VatSummaryContext> pair : value.values() ) {
				int col = first? 0 : -1;
				first = false;
				if (pair.getLeft() != null) {
					ClickHandler leftClickHandler = event -> {
						callback.refreshVatSummaryTypeFilter(type);						
						callback.refreshOutputFilter(true);
						callback.refreshPercentFilter(type==VatSummaryType.SURCHARGE?null:pair.getLeft().getPercentage());
						callback.refreshSurchargePercentFilter(type==VatSummaryType.SURCHARGE?pair.getLeft().getPercentage():null);
						onClick( params
							.setVatSummaryType(type)
							.setPercent(type==VatSummaryType.SURCHARGE?null:pair.getLeft().getPercentage())
							.setSurchargePercent(type==VatSummaryType.SURCHARGE?pair.getLeft().getPercentage():null)
							.setOutput(true));
					};
					typeOutputBase = typeOutputBase + pair.getLeft().getBase();
					typeOutputQuota = typeOutputQuota + pair.getLeft().getQuota();
					
					Label baseLabel = addCell(tab, row, (col+1) , AON.FMT.format( pair.getLeft().getBase()));
					tab.getCellFormatter().addStyleName(row, (col+1), AON.CSS.aonClickableBlock());
					baseLabel.addClickHandler(leftClickHandler);
					
					Label percentLabel = addCell(tab, row, (col+2) , AON.FMT.format( pair.getLeft().getPercentage()) );
					tab.getCellFormatter().addStyleName(row, (col+2), AON.CSS.aonClickableBlock());
					percentLabel.addClickHandler(leftClickHandler);
					
					Label quotaLabel = addCell(tab, row, (col+3) , AON.FMT.format( pair.getLeft().getQuota()));
					tab.getCellFormatter().addStyleName(row, (col+3), AON.CSS.aonClickableBlock());
					quotaLabel.addClickHandler(leftClickHandler);
					
				} else {
					addCell(tab, row, (col+1) , "");
					addCell(tab, row, (col+2) , "");
					addCell(tab, row, (col+3) , "");
				}
				
				if (pair.getRight() != null) {
					typeInputBase = typeInputBase + pair.getRight().getBase();
					typeInputQuota = typeInputQuota + pair.getRight().getQuota();
					typeInputDeductibleQuota = typeInputDeductibleQuota + pair.getRight().getDeductibleQuota();
					ClickHandler rightClickHandler = event -> {
						callback.refreshVatSummaryTypeFilter(type);						
						callback.refreshOutputFilter(false);
						callback.refreshPercentFilter(type==VatSummaryType.SURCHARGE?null:pair.getRight().getPercentage());
						callback.refreshSurchargePercentFilter(type==VatSummaryType.SURCHARGE?pair.getRight().getPercentage():null);
						onClick(
							params
								.setVatSummaryType(type)
								.setPercent(type==VatSummaryType.SURCHARGE?null:pair.getRight().getPercentage())
								.setSurchargePercent(type==VatSummaryType.SURCHARGE?pair.getRight().getPercentage():null)
								.setOutput(false));
					};
					Label baseLabel = addCell(tab, row, (col+5) , AON.FMT.format( pair.getRight().getBase()));
					tab.getCellFormatter().addStyleName(row, (col+5), AON.CSS.aonClickableBlock());
					baseLabel.addClickHandler(rightClickHandler);
					
					Label percentLabel = addCell(tab, row, (col+6) , AON.FMT.format( pair.getRight().getPercentage()) );
					tab.getCellFormatter().addStyleName(row, (col+6), AON.CSS.aonClickableBlock());
					percentLabel.addClickHandler(rightClickHandler);
					
					Label quotaLabel = addCell(tab, row, (col+7) , AON.FMT.format( pair.getRight().getQuota()));
					tab.getCellFormatter().addStyleName(row, (col+7), AON.CSS.aonClickableBlock());
					quotaLabel.addClickHandler(rightClickHandler);
					
					Label deductibleQuotaLabel = addCell(tab, row, (col+8) , AON.FMT.format( pair.getRight().getDeductibleQuota()));
					tab.getCellFormatter().addStyleName(row, (col+8), AON.CSS.aonClickableBlock());
					deductibleQuotaLabel.addClickHandler(rightClickHandler);
					
				} else {
					addCell(tab, row, (col+5) , "");
					addCell(tab, row, (col+6) , "");
					addCell(tab, row, (col+7) , "");
					addCell(tab, row, (col+8) , "");
				}
				++row;
			}
				
			if (type == VatSummaryType.NATIONAL
				|| type == VatSummaryType.SURCHARGE
				|| type == VatSummaryType.FARMER) {
					estimation = estimation + typeOutputQuota;	
					estimation = estimation - typeInputDeductibleQuota;
				}

			row = paintTotal(params, callback, tab, row,type,typeOutputBase,typeOutputQuota,typeInputBase,typeInputQuota,typeInputDeductibleQuota);					
			
			if (type != VatSummaryType.SURCHARGE) {
				outputBase = outputBase + typeOutputBase;
				inputBase = inputBase + typeInputBase;
			}
			outputQuota = outputQuota  + typeOutputQuota; 
			inputQuota = inputQuota + typeInputQuota;
			inputDeductibleQuota = inputDeductibleQuota + typeInputDeductibleQuota;
		}
		row = paintEmptyRow(tab, row);
		row = paintTotal( params, callback, tab, row,null,outputBase,outputQuota,inputBase,inputQuota,inputDeductibleQuota);
		row = paintEmptyRow(tab, row);
		 
		tab.setWidget(row, 0, new Label());
		String est = "Estimaci\u00F3n: ";
		if (estimation < 0  ) {
			est = est + "A Compensar / Devolver: " + AON.FMT.format( AonMathUtils.absRounded( estimation ) );
			tab.getCellFormatter().setStyleName(row,0, AON.CSS.aonColorGreen());
		} else if (estimation > 0  ) { 
			est = est + "A Ingresar: " + AON.FMT.format( estimation );
			tab.getCellFormatter().setStyleName(row,0, AON.CSS.aonColorRed());
		} else {
			est = est + "Cero / Sin Actividad ";
		}
		tab.getFlexCellFormatter().setColSpan(row, 0, 9);
		tab.getCellFormatter().addStyleName(row, 0,AON.CSS.aonTextCenter());
		tab.getCellFormatter().addStyleName(row, 0,AON.CSS.aonFontMedium());
		tab.getCellFormatter().addStyleName(row, 0,AON.CSS.aonBorder());
		tab.getCellFormatter().addStyleName(row, 0,AON.CSS.aonBold());
		tab.setWidget(row, 0, new Label(est));
		setWidget( tab );
		scrollToTop();
	}
	
	private TreeMap<VatSummaryType, TreeMap<Double, Pair<VatSummaryContext, VatSummaryContext>>> sort(LinkedList<VatSummaryContext> data) {
		TreeMap<VatSummaryType,TreeMap<Double,Pair<VatSummaryContext, VatSummaryContext>>> map = new TreeMap<>();
		for (VatSummaryContext vat : data){
			TreeMap<Double,Pair<VatSummaryContext,VatSummaryContext>> block = map.get(vat.getSummaryType());
			if (block == null) {
				block = new TreeMap<>();
				map.put(vat.getSummaryType(), block);
			}
			Pair<VatSummaryContext,VatSummaryContext> line = block.get(vat.getPercentage());
			if (line == null) {
				line = Pair.of(vat.isOutput()?vat:null, vat.isOutput()?null:vat);
			} else {
				line = Pair.of(vat.isOutput()?vat:line.getLeft(), vat.isOutput()?line.getRight():vat);
			}
			block.put(vat.getPercentage(), line);					
		}
		return map;
	}

	private int paintEmptyRow(FlexTable tab, int row) {
		tab.setWidget(row, 0, new Label());
		tab.getFlexCellFormatter().setColSpan(row, 0, 9);
		tab.getRowFormatter().getElement(row).getStyle().setHeight(5, Unit.PX);
		return ++row;
	}

	private int paintTotal(AccountingReportParams params, VatReportSummaryPanelCallback callback, FlexTable tab, int row, VatSummaryType type,double typeOutputBase, double typeOutputQuota,
			double typeInputBase, double typeInputQuota, double typeInputDeductibleQuota) {
		int col = type==null?1:0;
		ClickHandler leftClickHandler = event -> {
			callback.refreshVatSummaryTypeFilter(type);						
			callback.refreshOutputFilter(true);
			callback.refreshPercentFilter(null);
			callback.refreshSurchargePercentFilter(null);
			onClick(params.setVatSummaryType(type)
				.setOutput(true)
				.setPercent(null)
				.setSurchargePercent(null));
		}; 
		Label obl = addCell(tab, row, col+0 , AON.FMT.format( typeOutputBase));
		obl.addClickHandler(leftClickHandler);
		Label oql = addCell(tab, row, col+2 , AON.FMT.format( typeOutputQuota));
		oql.addClickHandler(leftClickHandler);
		
		ClickHandler rightClickHandler = event -> {
			callback.refreshVatSummaryTypeFilter(type);						
			callback.refreshOutputFilter(false);
			callback.refreshPercentFilter(null);
			callback.refreshSurchargePercentFilter(null);
			onClick(params.setVatSummaryType(type)
				.setOutput(false)
				.setPercent(null)
				.setSurchargePercent(null));
		}; 
		Label ibl = addCell(tab, row, col+4 , AON.FMT.format( typeInputBase));
		ibl.addClickHandler(rightClickHandler);
		Label iql = addCell(tab, row, col+6 , AON.FMT.format( typeInputQuota));
		iql.addClickHandler(rightClickHandler);
		Label idql = addCell(tab, row, col+7 , AON.FMT.format( typeInputDeductibleQuota));
		idql.addClickHandler(rightClickHandler);
		
		tab.getCellFormatter().addStyleName(row, col+0, AON.CSS.aonClickableBlock());
		tab.getCellFormatter().addStyleName(row,col+0, AON.CSS.aonBold());
		
		tab.getCellFormatter().addStyleName(row, col+2, AON.CSS.aonClickableBlock());
		tab.getCellFormatter().addStyleName(row,col+2, AON.CSS.aonBold());
		
		tab.getCellFormatter().addStyleName(row, col+4, AON.CSS.aonClickableBlock());
		tab.getCellFormatter().addStyleName(row,col+4, AON.CSS.aonBold());
		
		tab.getCellFormatter().addStyleName(row, col+6, AON.CSS.aonClickableBlock());
		tab.getCellFormatter().addStyleName(row,col+6, AON.CSS.aonBold());
		
		tab.getCellFormatter().addStyleName(row, col+7, AON.CSS.aonClickableBlock());
		tab.getCellFormatter().addStyleName(row,col+7, AON.CSS.aonBold());
		return ++row;
	}

	private Label addCell(FlexTable tab, int row, int col, String text) {
		Label label = new Label(text);
		tab.setWidget(row, col, label);
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonTextRight());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonPaddingRight());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBorder());
		return label;
	}

	private void paintTableHeader(AccountingReportParams params, VatReportSummaryPanelCallback callback, FlexTable tab) {
		Label outputLabel = new Label( AON.MSG.outputInvoices() );
		tab.setWidget(0,1, outputLabel);
		tab.getFlexCellFormatter().setColSpan(0, 1, 3);
		giveHeaderStyle( tab, 0, 1,  AON.CSS.aonTextCenter());
		outputLabel.addClickHandler(event -> {
			callback.refreshOutputFilter(true);
			callback.refreshVatSummaryTypeFilter(null);
			callback.refreshPercentFilter(null);
			callback.refreshSurchargePercentFilter(null);
			onClick(params
				.setOutput(true)
				.setPercent(null)
				.setVatSummaryType(null)
				.setSurchargePercent(null));
		});
		
		Label inputLabel = new Label( AON.MSG.inputInvoices() );
		tab.setWidget(0,3, inputLabel);
		tab.getFlexCellFormatter().setColSpan(0, 3, 4);
		giveHeaderStyle( tab, 0, 3,  AON.CSS.aonTextCenter());
		inputLabel.addClickHandler(event -> {
			callback.refreshOutputFilter(false);
			callback.refreshVatSummaryTypeFilter(null);
			callback.refreshPercentFilter(null);
			callback.refreshSurchargePercentFilter(null);
			onClick(params
				.setOutput(false)
				.setPercent(null)
				.setVatSummaryType(null)
				.setSurchargePercent(null));
		});
		
		paintEmptyRow(tab, 1);

		Label outputBaseLabel = new Label( AON.MSG.taxableBase() );
		tab.setWidget(2,1, outputBaseLabel);
		giveHeaderStyle( tab, 2, 1,  AON.CSS.aonTextRight());
		
		Label outputPercentLabel = new Label( "%" );
		tab.setWidget(2,2, outputPercentLabel);
		giveHeaderStyle( tab, 2, 2,  AON.CSS.aonTextCenter());

		Label outputQuotaLabel = new Label( AON.MSG.quota() );
		tab.setWidget(2,3, outputQuotaLabel);
		giveHeaderStyle( tab, 2, 3,  AON.CSS.aonTextRight());

		Label inputBaseLabel = new Label( AON.MSG.taxableBase() );
		tab.setWidget(2,5, inputBaseLabel);
		giveHeaderStyle( tab, 2, 5,  AON.CSS.aonTextRight());

		Label inputPercentLabel = new Label( "%" );
		tab.setWidget(2,6, inputPercentLabel);
		giveHeaderStyle( tab, 2, 6,  AON.CSS.aonTextRight());

		Label inputQuotaLabel = new Label( AON.MSG.quota() );
		tab.setWidget(2,7, inputQuotaLabel);
		giveHeaderStyle( tab, 2, 7,  AON.CSS.aonTextCenter());
		
		Label inputDeductibleQuotaLabel = new Label( AON.MSG.dedQuota() );
		tab.setWidget(2,8, inputDeductibleQuotaLabel);
		giveHeaderStyle( tab, 2, 8,  AON.CSS.aonTextRight());
	}

	private void giveHeaderStyle(FlexTable tab, int row, int col, String textStyle) {
		tab.getCellFormatter().setStyleName(row, col, AON.CSS.aonBold());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonFontLarger());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBorder());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonClickableLabel());
		tab.getCellFormatter().addStyleName(row, col, AON.CSS.aonBackgroundLigthGray());
		tab.getCellFormatter().addStyleName(row, col, textStyle);
	}

	protected void onClick( AccountingReportParams params ) {
		ValueChangeEvent.<AccountingReportParams>fire( VatReportSummaryPanel.this, params );
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<AccountingReportParams> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}
	
}
