package com.esferalia.aon.gwt.fiscal.client.accounting.wizard.tedi;

import java.util.LinkedList;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.AuditDialog;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionEvent;
import com.esferalia.aon.gwt.fiscal.client.AccountEntrySelectionHandler;
import com.esferalia.aon.gwt.fiscal.client.HasAccountEntrySelectionHandlers;
import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;
import com.esferalia.aon.occam.api.model.type.FinanceTrackingType;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
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
		tab.getColumnFormatter().setWidth(col, "100px");
		++col;
		tab.setWidget(row,col, new Label(AON.MSG.action()));
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "200px");
		++col;
		tab.setWidget(row,col, new Label(AON.MSG.description()));
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "300px");
		++col;
		tab.setWidget(row,col, new Label(AON.MSG.amount()));
		tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextRight());
		tab.getCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "100px");
		++col;
		tab.setWidget(row,col, new Label());
		tab.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "110px");
		++col;
		tab.setWidget(row,col, new Label());
		tab.getFlexCellFormatter().addStyleName(row, col,AON.AON_CSS.aonDataTableHeader());
		tab.getColumnFormatter().setWidth(col, "40px");
		++col;
		++row;
		
		for (FinanceTracking ft : list) {
			col = 0;
			tab.setWidget(row,col, new Label( AON.DATE_FORMAT.format(ft.getTrackingDate())));
			++col;
			Label typeLabel = new Label( ft.getType().getDescription() );
			if (ft.getType() == FinanceTrackingType.RETURNED) {
				typeLabel.setStyleName(AON.AON_CSS.aonColorRed());
			} else  if (ft.getType() == FinanceTrackingType.FRACTIONED) {
				typeLabel.setStyleName(AON.AON_CSS.aonColoRoyalblue());
			} else {
				typeLabel.setStyleName(AON.AON_CSS.aonColorGreen());
			}
			tab.setWidget(row,col, typeLabel);
			++col;
			tab.setWidget(row,col, new Label( ft.getDescription()));
			++col;
			tab.setWidget(row,col, new Label(AON.FMT.format( ft.getAmount()) ));
			tab.getCellFormatter().setStyleName(row, col,AON.AON_CSS.aonTextRight());
			++col;
			
			if (ft.getAccountEntry() != null) {
				Label entryLink = new Label("[" + AON.MSG.accountEntry() + "]");
				entryLink.setStyleName(AON.AON_CSS.aonTextCenter());
				entryLink.addStyleName(AON.AON_CSS.aonClickableLabel());
				entryLink.addClickHandler( new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						AccountEntry entry = new AccountEntry().setId(ft.getAccountEntry()); 
						AccountEntrySelectionEvent.fire( InvoiceFinanceTrackingPanel.this, entry, null );
					}
				});
				tab.setWidget(row,col, entryLink);
			}
			++col;

			FlowPanel buttons = new FlowPanel();
			Button auditInfo = new Button();
			buttons.add(auditInfo);
			auditInfo.setTitle( AON.MSG.tracking() );
			auditInfo.setStyleName(AON.AON_CSS.aonIconAudit());
			auditInfo.addStyleName(AON.AON_CSS.aonIconCommandButton());
			auditInfo.addStyleName(AON.AON_CSS.aonMarginLeft());
			auditInfo.addStyleName(AON.AON_CSS.aonMarginLeft5());
			auditInfo.addClickHandler( new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					AuditDialog dialog = new AuditDialog();
					dialog.show( ft );
				}
			});
			tab.setWidget(row,col, buttons);
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
