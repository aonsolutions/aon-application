package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import java.util.HashMap;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageM14_2 extends PageAbs {
	
	private static final Map<Integer, String> DEFINED_ROWS = new HashMap<Integer, String>() {
		
		private static final long serialVersionUID = 1L;

		{
			put(Integer.valueOf(1), new String(AON.MSG.memory14_2Row1()));
			put(Integer.valueOf(4), new String(AON.MSG.memory14_2Row2()));
			put(Integer.valueOf(8), new String(AON.MSG.memory14_2Row3()));
			put(Integer.valueOf(9), new String(AON.MSG.memory14_2Row4()));
		};
	};
	
	interface PageBinder extends UiBinder<Widget, PageM14_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true) FlexTable table1;
	@UiField(provided = true) FlexTable table2;
	@UiField TabPanel tabPanel;

	public PageM14_2(Deposit2 deposit) {
		super(deposit);
		initialize(0);
	}
	
	public PageM14_2(Deposit2 deposit, Integer tab) {
		super(deposit);
		initialize(tab);
	}
	
	private void initialize(Integer tab) {
		table1 = new FlexTable();
		table2 = new FlexTable();
		tabPanel = new TabPanel();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(tab != null ? tab : 0);
		
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		String[][] AUXILIARES = new String[][] {
			  new String[] {AON.MSG.memory14_2Table1() , AON.MSG.fiscalYear() + " " + getYear() , AON.MSG.fiscalYear() + " " + (getYear() - 1)}
			, new String[] {AON.MSG.memory14_2Table2header1(), AON.MSG.amount()}
			, new String[] {AON.MSG.memory14_2Table2header2(), AON.MSG.amount()}
		};
		defineMRNTable(table, AUXILIARES[0], DEFINED_ROWS, D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_1);
		defineMRNTable(table1, AUXILIARES[1], null, D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_2);
		defineMRNTable(table2, AUXILIARES[2], null, D2DepositConstants.MRN14_ABREVIATE_PYMES_KEYS_3);
	}

	protected void defineMRNTable( FlexTable tab, String[] headers, Map<Integer, String> rows, D2DepositKey[][] keys){
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		
		for (String primary : headers) {
			
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(primary));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
			++col;
		}
		
		++row;
		
		for (D2DepositKey[] innerKeys : keys) {
			col = 0;
			
			if (rows != null) {
				while(rows.containsKey(row)){
					tab.setWidget(row, col, new Label(rows.get(row)));
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
					++row;
				}
			}
			
			paintKeyDescription(tab, innerKeys[0], row, col);
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
			col++;
			for (D2DepositKey key : innerKeys ) {
				
				paintKeyField(tab,key,row,col,(col==1), key.getCode()); 
				col++;
			}
			++row;
		}
	}
	
	@Override
	protected void refreshDepositPage() {
		getDeposit().refreshPage(tabPanel.getTabBar().getSelectedTab());
	}
}
