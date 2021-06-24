package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.accounting.utilities.AccUtilitiesAccountChangeParams;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IAccountDependencyVisitor;

public enum AccountDependency {
	ACCOUNT_ENTRY_DETAIL {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitAccountEntryDetail();
		}

		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInEntriesEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta contable en l\u00EDneas de apuntes ({0}).";
		}
	},
	ACCOUNT_ENTRY_DETAIL_BALANCING {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitAccountEntryDetaiBalancing();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInEntriesEnabled();
		}
		@Override
		public String getDescription() {
			return "Contrapartida en l\u00EDneas de apuntes.({0}).";
		}
	},
	INVOICE_DETAIL_ACCOUNT {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitInvoiceDetailAccount();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInEntriesEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta contable en enlace con l\u00EDneas facturas.";
		}
		
	},
	INVOICE_TAX_ACCOUNT {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitInvoiceTaxAccount();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInEntriesEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuentas contable en enlace con impuestos de facturas.";
		}
	},
	PRODUCT_PURCHASE {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitProductPurchase();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Enlaces con productos (compras).";
		}
	},
	PRODUCT_SALES {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitProductSales();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Enlaces con productos (ventas).";
		}
	},
	AMORTIZATION_ACCUMULATED {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitAmortizationAccumulated();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta de acumulado en fichas de amortizaci\u00F3n.";
		}
	},
	AMORTIZATION_ALLOCATION {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitAmortizationAllocation();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta de dotaci\u00F3n en fichas de amortización.";
		}
	},
	AMORTIZATION_FIXED_ASSET {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitAmortizationFixedAsset();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta de inmoviliazado en fichas de amortizaci\u00F3n.";
		}
	},
	CREDITOR {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitCreditor();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuentas enlazadas con acreedores.";
		}
	},
	CUSTOMER {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitCustomer();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuentas enlazadas con clientes.";
		}
	},
	SUPPLIER {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitSupplier();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuentas enlazadas con proveedores.";
		}
	},
	BANK_CONCEPT {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitBankConcept();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta enlazada con conceptos bancarios.";
		}
	},
	LOAN {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitLoan();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta enlazada con pr\u00E9stamos";
		}
	},
	PM_TYPE_DETAIL {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitPmTypeDetail();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta enlazada con tipos de pagos.";
		}
	},
	RBANK {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitRbank();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta enlazada con bancos.";
		}
	},
	TAX_PURCHASE {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitTaxPurchase();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta enlazada con impuestos (compras).";
		}
	},
	TAX_SALES {
		@Override
		public <T> T visit(IAccountDependencyVisitor<T> visitor) {
			return visitor.visitTaxSales();
		}
		
		@Override
		public boolean accept(AccUtilitiesAccountChangeParams params) {
			return params.isChangeInMastersEnabled();
		}
		@Override
		public String getDescription() {
			return "Cuenta enlazada con impuestos (ventas).";
		}
	}
	;
	
	public abstract <T> T visit(IAccountDependencyVisitor<T> visitor);
	public abstract boolean accept(AccUtilitiesAccountChangeParams params);
	public abstract String getDescription();
}
