package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.IAccountEntryWrapper;

public class EnumVisitors {

	public static interface IAccountEntryUpdateVisitor {
		IAccountEntryWrapper visitOpeningType(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitSecurityLevel(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitInvestment(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitTaxDate(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitActivity(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitService(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitVatAccrualPayment(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitWithholdingType(IAccountEntryWrapper wrapper);
		IAccountEntryWrapper visitOperatingAccount(IAccountEntryWrapper wrapper);
	}

	public static interface IInvoiceTransactionTypeVisitor {
		void visitNational();
		void visitIntracommunity();
		void visitExtracommunity();
		void visitCanCeuMel();
		void visitOtherISP();
	}

	public static interface IWithholdingTypeVisitor<T> {
		T visitProfessional(T t);
		T visitRenting(T t);
		T visitMovableCapital(T t);
		T visitFarmer(T t);
		T visitTransportOperator(T t);	
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
	
	public static interface IFiscalModelKeyInfoVisitor<T> {
		T visitNone();
		T visitInvoice();
		T visitInAccrualInvoice();
		T visitOutAccrualInvoice();
		T visitDiffInvoice();
		T visitDiffInAccrualInvoice();
		T visitDiffOutAccrualInvoice();
		T visitSalary();
		T visitDiffSalary();
		T visitCompute();
		T visitComputeKey();
		T visitActAccount();
		T visitTitle();
		T visitIrpfActivity();
		T visitCorporate();
		T visitModelInvoiceIrpfBreakdown();
		T visitModelSalaryIrpfBreakdown();
		T visitModelInvoiceVatBreakdown();
		T visitProrratedModelInvoiceVatBreakdown();
		T visitModelOutVatAccrualInvoice();
		T visitModelInVatAccrualInvoice();
	}
	
	public static interface IFiscalStatusVisitor<T> {
		T visitPending();
		T visitFinished();
		T visitBatched();
		T visitBlocked();
		T visitSent();
		T visitMissing();
		T visitCustomerCheck();
		T visitCustomerAccepted();
		T visitCustomerRejected();
	}
	
	public static interface IAdministrationVisitor<T> {
		T visitAlava();
		T visitBizkaia();
		T visitGipuzkoa();
		T visitNavarra();
		T visitCommonTerritory();
		T visitUnknown();
	}
	
	public static interface IAccountDependencyVisitor<T> {
		T visitAccountEntryDetail();
		T visitAccountEntryDetaiBalancing();
		T visitInvoiceDetailAccount();
		T visitInvoiceTaxAccount();
		T visitProductPurchase();
		T visitProductSales();
		T visitAmortizationAccumulated();
		T visitAmortizationAllocation();
		T visitAmortizationFixedAsset();
		T visitCreditor();
		T visitCustomer();
		T visitSupplier();
		T visitBankConcept();
		T visitLoan();
		T visitPmTypeDetail();
		T visitRbank();
		T visitTaxPurchase();
		T visitTaxSales();
	}

	public static interface IVATTaxRegimeVisitor {
		void visitVatGeneral();
		void visitVatSurcharge();
		void visitVatSimplified();
		void visitVatAccrualPayment();
		void visitVatRebuOperation();
		void visitVatRebuProfit();
		void visitVatTravelAgency();
		void visitVatAgriculture();
		void visitVatGold();
		void visitVatUnionExternal();
		void visitVatUnion();
		void visitVatImportation();
		void visitVatExempt();
	}
	
		
}
