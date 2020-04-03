package com.esferalia.aon.occam.api.model.finance;

public class EnumVisitors {

	public static interface IFinanceStatusVisitor {
		void visitPending();
		void visitBatched();
		void visitReturned();
		void visitPaid();
		void visitSettled();
	}
	
	public static interface IFinanceTrackingTypeVisitor {
		void visitBatched();
		void visitPaid();
		void visitReturned();
		void visitFractioned();
		void visitSettled();
	}
}
