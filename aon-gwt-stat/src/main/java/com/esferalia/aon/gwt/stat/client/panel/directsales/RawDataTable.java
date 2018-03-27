package com.esferalia.aon.gwt.stat.client.panel.directsales;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;

public class RawDataTable extends ScrollPanel {
	public RawDataTable(AbstractDataTable data) {
		this(data, true);
	}
	
	public RawDataTable(AbstractDataTable data, boolean calculateRatios) {
		setStyleName(AON.AON_CSS.aonScrollArea());
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonDataTable());
		setWidget(tab);
		
		double[] totals = new double[data.getNumberOfColumns()];
		
		// ------------------------------------------------------------------------------	HEADER
		int colIdx = 0;
		for ( int col = 0; col < data.getNumberOfColumns();col++) {
			tab.setWidget(0, colIdx, new Label(data.getColumnLabel(col)));
			tab.getCellFormatter().setStyleName(0, colIdx, AON.AON_CSS.aonDataTableHeader());
			totals[col] = 0.0;
			if (data.getColumnType(col) == ColumnType.NUMBER) {
				tab.getColumnFormatter().setWidth(colIdx, "120px");
				tab.getCellFormatter().addStyleName(0, colIdx, AON.AON_CSS.aonTextRight());
				++colIdx;
				
				tab.setWidget(0, colIdx, new Label("%"));
				tab.getColumnFormatter().setWidth(colIdx, "80px");
				tab.getCellFormatter().setStyleName(0, colIdx, AON.AON_CSS.aonDataTableHeader());
				tab.getCellFormatter().addStyleName(0, colIdx, AON.AON_CSS.aonTextRight());
				
			} else {
				tab.getCellFormatter().addStyleName(0, colIdx, AON.AON_CSS.aonTextCenter());
				tab.getColumnFormatter().setWidth(colIdx, "auto");
			}
			++colIdx;
		}
		
		// ------------------------------------------------------------------------------	BODY
		for ( int r = 0; r < data.getNumberOfRows();r++) {
			int row = r + 1;
			colIdx = 0;
			for ( int col = 0; col < data.getNumberOfColumns();col++) {
				tab.setWidget(row, colIdx, new Label(data.getFormattedValue(r, col)));
				if (data.getColumnType(col) == ColumnType.NUMBER) {
					totals[col] = totals[col] + data.getValueDouble(r, col);		
					tab.getCellFormatter().addStyleName(row, colIdx, AON.AON_CSS.aonTextRight());
					++colIdx;
					tab.getCellFormatter().addStyleName(row, colIdx, AON.AON_CSS.aonTextRight());
					tab.getCellFormatter().addStyleName(row, colIdx, AON.AON_CSS.aonItalic());
				} else {
					tab.getCellFormatter().addStyleName(row, colIdx, AON.AON_CSS.aonTextLeft());
				}
				++colIdx;
			}
		}
		
		// ------------------------------------------------------------------------------	FOOTER
		int row = tab.getRowCount();
		NumberFormat FMT = NumberFormat.getDecimalFormat();
		FMT.overrideFractionDigits(2, 2);

		NumberFormat FMT2 = NumberFormat.getDecimalFormat();
		FMT2.overrideFractionDigits(4, 4);

		colIdx = 0;
		for ( int col = 0; col < data.getNumberOfColumns();col++) {
			if (data.getColumnType(col) == ColumnType.NUMBER) {
				tab.getCellFormatter().setStyleName(row, colIdx, AON.AON_CSS.aonTextRight());
				tab.getCellFormatter().addStyleName(row, colIdx, AON.AON_CSS.aonBold());
				tab.setWidget(row, colIdx, new Label(FMT.format(totals[col])));
				++colIdx;
			} else {
				tab.setWidget(row, colIdx, new Label());
			}
			++colIdx;
		}
		
		// ------------------------------------------------------------------------------	RATIOS
		for ( int r = 0; r < data.getNumberOfRows();r++) {
			row = r + 1;
			colIdx = 0;
			for ( int col = 0; col < data.getNumberOfColumns();col++) {
				
				if (data.getColumnType(col) == ColumnType.NUMBER) {
					++colIdx;
					double value = data.getValueDouble(r, col);
					double ratio = AonMathUtils.isZero( totals[col] ) ? 0.0 : AonMathUtils.round( (value * 100) / totals[col], 4 );
					String label = FMT2.format(ratio) + AonStringUtils.PERCENT;  
					tab.setWidget(row, colIdx, new Label(label));
				}
				++colIdx;
			}
		}
		
	}

}
