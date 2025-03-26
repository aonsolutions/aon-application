package com.esferalia.aon.gwt.fiscal.client.rawdoc;

import java.util.Optional;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTableButton;
import com.esferalia.aon.gwt.fiscal.client.rawdoc.RawdocModuleNew.RawdocCallback;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.accounting.AccountingIncome;
import com.google.gwt.user.client.ui.Label;

public class RawdocTableRowIncome extends RawdocTableRowAbs<AccountingIncome> {

	public RawdocTableRowIncome(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		super( opt, cbk, rawdoc);
	}

	@Override
	protected Optional<AccountingIncome> getDoc(Rawdoc rawdoc) {
		if (rawdoc == null) return Optional.empty();
		return Optional.empty();
	}

	@Override
	protected Label getDocumentLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc)
			.flatMap( i -> i.getCustomer() )
			.map( c -> c.getDocument() )
			.orElse( "" ));
	}

	@Override
	protected Label getNameLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc)
			.flatMap( i -> i.getCustomer() )
			.map( r -> r.getName() )
			.orElse( "" ));
	}
	
	@Override
	protected Label getAmountLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( i -> i.getAmount() ).map( AON.FMT::format ).orElse( "" ));
	}

	@Override
	protected Label getDateLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( i -> i.getDate() ).map( AON.DATE_FORMAT::format ).orElse( "" ));
	}

	@Override
	protected Label getReferenceLabel(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		return new Label( getDoc(rawdoc).map( i -> i.getReferenceCode() ).orElse( "" ) );
	}
	
	@Override
	protected AonTableButton getActionButton(RawdocModuleOptions opt, RawdocCallback cbk, Rawdoc rawdoc) {
		// TODO Contabilizar un ingreso
		return null;
	}
	
}
