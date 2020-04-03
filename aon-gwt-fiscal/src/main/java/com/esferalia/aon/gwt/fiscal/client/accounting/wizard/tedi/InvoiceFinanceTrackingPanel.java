package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceTrackingTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class InvoiceFinanceTrackingPanel extends ScrollPanel implements HasAccountEntrySelectionHandlers {
	
	public InvoiceFinanceTrackingPanel(LinkedList<FinanceTracking> list) {
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonDataTable());
		tab.addStyleName(AON.AON_CSS.aonBlockCenter());
		tab.addStyleName(AON.AON_CSS.aonWidthAutoImportant());
		int row = 0;
		int col = 0;
		
		tab.setWidget(row,col, new Label(AON.MSG.date()));
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "80px");
		++col;
		tab.setWidget(row,col, new Label(AON.MSG.action()));
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "100px");
		++col;
		tab.setWidget(row,col, new Label());
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "auto");
		++col;
		tab.setWidget(row,col, new Label(AON.MSG.amount()));
		tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextRight());
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "100px");
		++col;
		tab.setWidget(row,col, new Label());
		tab.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "40px");
		++col;
		tab.setWidget(row,col, new Label( AON.MSG.audit()));
		tab.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "350px");
		++col;
		++row;
		
		for (FinanceTracking ft : list) {
			col = 0;
			tab.setWidget(row,col, new Label( AON.DATE_FORMAT.format(ft.getTrackingDate())));
			++col;
			
			Label typeLabel = new Label( ft.getType().getDescription() );
			ft.getType().visit( new IFinanceTrackingTypeVisitor() {
				@Override
				public void visitSettled() {
					typeLabel.setStyleName(AON.AON_CSS.aonColoRoyalblue());
				}
				
				@Override
				public void visitReturned() {
					typeLabel.setStyleName(AON.AON_CSS.aonColorRed());
					typeLabel.addStyleName(AON.AON_CSS.aonBold());
				}
				
				@Override
				public void visitFractioned() {
					typeLabel.setStyleName(AON.AON_CSS.aonColoRoyalblue());
				}
				
				@Override
				public void visitPaid() {
					typeLabel.setStyleName(AON.AON_CSS.aonColorGreen());
				}
				
				@Override
				public void visitBatched() {
					typeLabel.setStyleName(AON.AON_CSS.aonColorGreen());
				}
			});
			tab.setWidget(row,col, typeLabel);
			++col;
			
			tab.setWidget(row,col, new Label(ft.getDescription()));
			tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextLeft());
			++col;
			
			tab.setWidget(row,col, new Label(AON.FMT.format( ft.getAmount()) ));
			tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextRight());
			++col;
			
			if (ft.getAccountEntry() != null) {
				Label entryLink = new Label();
				entryLink.setTitle(AON.MSG.gotoAccountEntry());
				entryLink.setStyleName(AON.AON_CSS.aonIconGoto());
				entryLink.addStyleName(AON.AON_CSS.aonIconPaddingLeft());
				entryLink.addStyleName(AON.AON_CSS.aonClickable());
				entryLink.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AccountEntry entry = new AccountEntry().setId(ft.getAccountEntry()); 
						AccountEntrySelectionEvent.fire( InvoiceFinanceTrackingPanel.this, entry, null );
					}
				});
				tab.setWidget(row,col, entryLink);
			}
			tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextCenter());
			++col;

			Label auditLabel = new Label(AonStringUtils.isEmpty(ft.getCreationUser()) ? AON.MSG.emptyCreatedBy() : AON.MSG.createdBy(ft.getCreationUser(), ft.getCreationDate())); 
			tab.setWidget(row,col, auditLabel);
			tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonNowrap());
			++col;
			++row;
		}
		setWidget(tab);
	}
	
	@Override
	public HandlerRegistration addSelectionHandler(AccountEntrySelectionHandler handler) {
		return super.addHandler(handler, AccountEntrySelectionEvent.getType());
	}
	
}
