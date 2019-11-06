package com.esferalia.aon.occam.api.model.finance.utilities;

import java.io.Serializable;

public interface IFinanceUtilitiesItem extends Serializable {
	public FinanceUtilitiesItemType getType();
	public String getMessage();
	public Integer getDomain();
	public String getDomainName();
	
	public static interface IFinanceUtilitiesItemTypeVisitor {
		void visitOther(FinanceUtilitiesItemType type);
		void visitErrorMessage(FinanceUtilitiesItemType type);
		void visitInfoMessage(FinanceUtilitiesItemType type);
		void visitMissingFinanceInvoice(FinanceUtilitiesItemType type);
	}

	public static enum FinanceUtilitiesItemType {
		 ERROR_MESSAGE{
			@Override
			public void visit(IFinanceUtilitiesItemTypeVisitor visitor) {
				visitor.visitErrorMessage(this);
			}
		 }
		 ,INFO_MESSAGE{
			@Override
			public void visit(IFinanceUtilitiesItemTypeVisitor visitor) {
				visitor.visitInfoMessage(this);
			}
		 }
		 		 
		,MISSING_FINANCE_INVOICE{
			@Override
			public void visit(IFinanceUtilitiesItemTypeVisitor visitor) {
				visitor.visitMissingFinanceInvoice(this);		
			}
		}
		;
		
		public void visit(IFinanceUtilitiesItemTypeVisitor visitor) {
			visitor.visitOther(this);
		}
	}
	
}
