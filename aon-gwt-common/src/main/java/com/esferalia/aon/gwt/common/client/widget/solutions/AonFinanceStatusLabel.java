package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;
import com.esferalia.aon.occam.api.model.type.FinanceStatus;
import com.google.gwt.user.client.ui.Label;

public class AonFinanceStatusLabel extends Label {
	
	public AonFinanceStatusLabel(FinanceStatus status) {
		this.setText( status == null? "??" : status.getDescription());				
		if (status != null) {
			status.visit( new IFinanceStatusVisitor() {
				@Override
				public void visitSettled() {
					AonFinanceStatusLabel.this.setStyleName(AON.CSS.aonColorBlue());
				}
				
				@Override
				public void visitReturned() {
					AonFinanceStatusLabel.this.setStyleName(AON.CSS.aonColorRed());
					AonFinanceStatusLabel.this.addStyleName(AON.CSS.aonBold());
				}
				
				@Override
				public void visitPending() {
					AonFinanceStatusLabel.this.setStyleName(AON.CSS.aonColorRed());
				}
				
				@Override
				public void visitPaid() {
					AonFinanceStatusLabel.this.setStyleName(AON.CSS.aonColorGreen());
				}
				
				@Override
				public void visitBatched() {
					AonFinanceStatusLabel.this.setStyleName(AON.CSS.aonColorGreen());
				}
			});
		}
	}
}
