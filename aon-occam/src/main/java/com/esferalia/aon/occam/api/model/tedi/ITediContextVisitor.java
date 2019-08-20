package com.esferalia.aon.occam.api.model.tedi;

public interface ITediContextVisitor {
	void visitDomain( TediResult result );
	void visitType( TediResult result );
	void visitSeries( TediResult result );
	void visitNumber( TediResult result );
	void visitReferenceCode( TediResult result );
	void visitTransaction( TediResult result );
	void visitIssueDate( TediResult result );
	void visitTaxDate( TediResult result );
	void visitScope( TediResult result );
	void visitRegistry( TediResult result );
	void visitRdocument( TediResult result );
	void visitRdocumentCountry( TediResult result );
	void visitRname( TediResult result );
	void visitAddress( TediResult result );
	void visitDetailDescription( TediResult result );
}
