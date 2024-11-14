package com.esferalia.aon.gwt.fiscal.client.finance;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomTable;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceTrackingTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class AonCustomTrackingPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers {
	
	private static enum COLS {
		  DAT("Fecha"					, "6rem" 			,"")
		, ACT("Acci\u00f3n"				, "5rem" 			,"")
		, DES("Descripci\u00f3n"		, "-moz-available"  ,"min-width: 5rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
		, AMO("Importe"					, "8rem" 			,"text-align: right;")
		, AUD("Auditoria"				, "-moz-available"	,"min-width: 20rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;")
//		, BTN(""						, "3rem"  			,"")
		;

		String headerLabel;
		String colWidth;
		String cellStyleClass;
		
		private COLS(String headerLabel, String colWidth, String cellStyleClass) {
			this.headerLabel = headerLabel;
			this.colWidth = colWidth;
			this.cellStyleClass = cellStyleClass;
		}
		public String getColWidth() {
			return colWidth;
		}
		public String getHeaderLabel() {
			return headerLabel;
		}
		public String getCellStyleClass() {
			return cellStyleClass;
		}
	}
	
	public AonCustomTrackingPanel(LinkedList<FinanceTracking> list) {
		AonCustomTable tab = new AonCustomTable();
		tab.createHeader();
		
		for ( COLS col : COLS.values())
			tab.addHeader(new Label( AonStringUtils.isBlank(col.getHeaderLabel()) ? "" : col.getHeaderLabel() ), col.getColWidth(), col.getCellStyleClass());
		
		for (FinanceTracking ft : list) {
			HTMLPanel row = tab.createRow();
			
			tab.addRow(row, new Label( AON.DATE_FORMAT.format(ft.getTrackingDate())), COLS.DAT.getColWidth());
			
			Label typeLabel = new Label( ft.getType().getDescription() );
			ft.getType().visit( new IFinanceTrackingTypeVisitor() {
				@Override
				public void visitSettled() {
					typeLabel.setStyleName(AON.CSS.aonColorBlue());
				}
				
				@Override
				public void visitReturned() {
					typeLabel.setStyleName(AON.CSS.aonColorRed());
					typeLabel.addStyleName(AON.CSS.aonBold());
				}
				
				@Override
				public void visitFractioned() {
					typeLabel.setStyleName(AON.CSS.aonColorBlue());
				}
				
				@Override
				public void visitPaid() {
					typeLabel.setStyleName(AON.CSS.aonColorGreen());
				}
				
				@Override
				public void visitBatched() {
					typeLabel.setStyleName(AON.CSS.aonColorGreen());
				}
			});
			tab.addRow(row, typeLabel, COLS.ACT.getColWidth());
			
			Label description = new Label(ft.getDescription());
			description.setTitle(description.getText());
			tab.addInlineStyle(description, COLS.DES.getCellStyleClass());
			tab.addRow(row, description, COLS.DES.getColWidth());
			
			Label amount = new Label(formatToEuro(ft.getAmount()) );
			tab.addInlineStyle(amount, COLS.AMO.getCellStyleClass());
			tab.addRow(row, amount, COLS.AMO.getColWidth());
			
			Label auditLabel = new Label(AonStringUtils.isEmpty(ft.getCreationUser()) ? AON.MSG.emptyCreatedBy() : AON.MSG.createdBy(ft.getCreationUser(), ft.getCreationDate())); 
			auditLabel.setTitle(auditLabel.getText());
			tab.addInlineStyle(auditLabel, COLS.AUD.getCellStyleClass());
			tab.addRow(row, auditLabel, COLS.AUD.getColWidth());
			
//			if (ft.getAccountEntry() != null) {
//				AonTableButton entryLink = new AonTableButton(AON.MSG.gotoAccountEntry(), AON.CSS.aonIconLaunch());
//				entryLink.addClickHandler( new ClickHandler() {
//					
//					@Override
//					public void onClick(ClickEvent event) {
//						AccountEntry entry = new AccountEntry().setId(ft.getAccountEntry()); 
//						AccountEntrySelectionEvent.fire( AonCustomTrackingPanel.this, entry, null );
//					}
//				});
//				tab.addRow(row, entryLink, COLS.BTN.getColWidth());
//			} else tab.addRow(row, new Label(""), COLS.BTN.getColWidth());
			
		}
		
		setWidget(tab);
	}

	private static String formatToEuro(double amount) {
        // Format the double value as a number with two decimal places
        NumberFormat numberFormat = NumberFormat.getFormat("#,##0.00");

        // Manually append the Euro symbol ()
        return numberFormat.format(amount) + " \u20AC";
    }
	
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}
	
}
