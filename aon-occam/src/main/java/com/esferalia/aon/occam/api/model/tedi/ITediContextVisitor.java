package com.esferalia.aon.occam.api.model.tedi;

import java.util.Date;

public interface ITediContextVisitor {
	void visitDomain( TediResult result, ICallback callback );
	void visitType( TediResult result, ICallback callback  );
	void visitSeries( TediResult result, ICallback callback  );
	void visitNumber( TediResult result, ICallback callback  );
	void visitReferenceCode( TediResult result, ICallback callback  );
	void visitTransaction( TediResult result, ICallback callback  );
	void visitIssueDate( TediResult result, ICallback callback );
	void visitTaxDate( TediResult result, ICallback callback );
	void visitScope( TediResult result, ICallback callback  );
	void visitRegistry( TediResult result, ICallback callback  );
	void visitRdocument( TediResult result, ICallback callback );
	void visitRdocumentCountry( TediResult result , ICallback callback );
	void visitRname( TediResult result , ICallback callback );
	void visitAddress( TediResult result , ICallback callback );
	void visitDetailDescription( TediResult result , ICallback callback );
	void visitDetails( TediResult result , ICallback callback );
}
