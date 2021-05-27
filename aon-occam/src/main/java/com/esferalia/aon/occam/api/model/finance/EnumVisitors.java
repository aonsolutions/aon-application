package com.esferalia.aon.occam.api.model.finance;

public class EnumVisitors {

	public static interface IInvoiceTransactionTypeVisitor {
		void visitNational();
		void visitIntracommunity();
		void visitExtracommunity();
		void visitCanCeuMel();
		void visitOtherISP();
	}

	public static interface IFinanceStatusVisitor {
		void visitPending();
		void visitBatched();
		void visitReturned();
		void visitPaid();
		void visitSettled();
	}
	
	public static interface IRegistryStatusVisitor {
		void visitActive();
		void visitInactive();
		void visitBlocked();
	}

	public static interface IFinanceTrackingTypeVisitor {
		void visitBatched();
		void visitPaid();
		void visitReturned();
		void visitFractioned();
		void visitSettled();
	}
	
	public static interface IAccountPeriodStatusVisitor {
		void visitActive();
		void visitInactive();
		void visitOpening();
		void visitOperating();
		void visitClosed();
	}
	
	public static interface IFiscalModelTypeVisitor {
		void visitM111();
		void visitM115();
		void visitM123();
		void visitM130();
		void visitM131();
		void visitM347();
		void visitM349();
		void visitM390();
		void visitM390HF();
		void visitM180();
		void visitM184();
		void visitM190();
		void visitM193();
		void visitM200();
		void visitM202();
		void visitM303();
	}
}
