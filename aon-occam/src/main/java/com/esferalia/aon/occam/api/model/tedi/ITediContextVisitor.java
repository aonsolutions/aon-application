package com.esferalia.aon.occam.api.model.tedi;

public interface ITediContextVisitor {
	void visitDomain(ICallback callback );
	void visitType(ICallback callback  );
	void visitSeries(ICallback callback  );
	void visitNumber(ICallback callback  );
	void visitReferenceCode(ICallback callback  );
	void visitTransaction(ICallback callback  );
	void visitIssueDate(ICallback callback );
	void visitTaxDate(ICallback callback );
	void visitTaxRate(ICallback callback );
	void visitTaxBase(ICallback callback );
	void visitTaxQuota(ICallback callback );
	void visitIrpfRate(ICallback callback );
	void visitIrpfQuota(ICallback callback );
	void visitScope(ICallback callback  );
	void visitRegistry(ICallback callback  );
	void visitAmbiguousRegistry(ICallback callback  );
	void visitRdocument(ICallback callback );
	void visitRdocumentCountry(ICallback callback );
	void visitRname(ICallback callback );
	void visitAddress(ICallback callback );
	void visitDetailDescription(ICallback callback );
	void visitDetails(ICallback callback );
	void visitAccountEntry(ICallback callback );
	void visitDuplicatedSeriesNumber(ICallback callback);
	void visitDuplicatedReferenceCode(ICallback callback);
	void visitFinanceAmountZero(ICallback callback);
	void visitFinanceWrongDate(ICallback callback);
	void visitFinanceAccountBank(ICallback callback);
	void visitWorkplace(ICallback callback);
	void visitBasesQuotas(ICallback callback);
	void visitPayMethod(ICallback callback);
	
	void visitTotal(ICallback callback);
	
}
