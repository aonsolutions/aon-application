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
}
