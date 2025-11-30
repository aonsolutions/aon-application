package com.esferalia.aon.gwt.fiscal.client.invoice;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.google.gwt.user.client.ui.Label;

public class FinanceStatusLabel extends Label {
	
	public FinanceStatusLabel(FinanceStatus status) {
		super(status == null? "??" : status.getDescription());
		if (status != null) {
			status.visit( new IFinanceStatusVisitor() {
				@Override
				public void visitSettled() {
					setStyleName(AON.CSS.aonColorBlue());
				}
				
				@Override
				public void visitReturned() {
					setStyleName(AON.CSS.aonColorRed());
					addStyleName(AON.CSS.aonBold());
				}
				
				@Override
				public void visitPending() {
					setStyleName(AON.CSS.aonColorRed());
				}
				
				@Override
				public void visitPaid() {
					setStyleName(AON.CSS.aonColorGreen());
				}
				
				@Override
				public void visitBatched() {
					setStyleName(AON.CSS.aonColorGreen());
				}
			});
		}
	}
	
}
