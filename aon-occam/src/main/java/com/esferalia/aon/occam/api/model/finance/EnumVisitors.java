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
		T visitM190G02(T t);
		T visitM190G03(T t);
		T visitM190H02(T t);
		T visitM190H03(T t);
		T visitM190I01(T t);
		T visitM190I02(T t);
		T visitM190J(T t);
		T visitM190K01(T t);
		T visitM190K03(T t);
		T visitM190K02(T t);
		T visitM193C1(T t);
		T visitM193C2(T t);
		T visitM193C3(T t);
		T visitM190F01(T t);
		T visitM190F021(T t);
		T visitM190F022(T t);
	}
	
	public static interface IRPFParamsOrderByVisitor<T,P> {
		T visitInvoiceIssueDate(P p);
		T visitInvoiceNumber(P p);
		T visitInvoiceRegistryName(P p);
		T visitInvoiceRegistryDocument(P p);
	}
	
	public static interface IRPFParamsGroupedByVisitor<T,P> {
		T visitInvoice(P p);
		T visitRegistry(P p);
		T visitInvoiceDetail(P p);
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
	
	public static interface IInvoiceCommunicationTypeVisitor {
		void visitSII();
		void visitTBAI();
		void visitLROE();
	}
	
	public static interface IFiscalModelKeyInfoVisitor<T> {
		T visitNone();
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
		T visitDiffInvoice();
		
		@Deprecated
		T visitInvoice();
		@Deprecated
		T visitInAccrualInvoice();
		@Deprecated
		T visitOutAccrualInvoice();
		@Deprecated
		T visitDiffInAccrualInvoice();
		@Deprecated
		T visitDiffOutAccrualInvoice();
		@Deprecated
		T visitSalary();
		@Deprecated
		T visitDiffSalary();
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
