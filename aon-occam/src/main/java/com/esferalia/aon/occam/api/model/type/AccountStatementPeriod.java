package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum AccountStatementPeriod implements Serializable {
	
	 BEFORE_PERIOD {
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitBeforePeriod();
		}
	 }
	,IN_PERIOD_OPENING { 
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitInPeriodOpening();
		}
	 }
	,IN_PERIOD_BEFORE { 
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitInPeriodBefore();
		}
	 } 
	,IN_PERIOD { 
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitInPeriod();
		}
	 } 
	,IN_PERIOD_AFTER { 
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitInPeriodAfter();
		}
	 } 
//	,IN_PERIOD_OPERATING { 
//		@Override
//		public void accept(IAccountStatementPeriodVisitor visitor) {
//			 visitor.visitInPeriodOperating();
//		}
//	 }
	,IN_PERIOD_CLOSING {
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitInPeriodClosing();
		}
	 }
	,AFTER_PERIOD {
		@Override
		public void accept(IAccountStatementPeriodVisitor visitor) {
			 visitor.visitAfterPeriod();
		}
	 }
	;
	
	public static interface IAccountStatementPeriodVisitor {
		void visitBeforePeriod();
		void visitInPeriodOpening();
		void visitInPeriodBefore();
		void visitInPeriod();
		void visitInPeriodAfter();
//		void visitInPeriodOperating();
		void visitInPeriodClosing();
		void visitAfterPeriod();
	}
	
	
    public void accept(IAccountStatementPeriodVisitor visitor) {
    	visitor.visitInPeriod();
    }
	
}
