package com.code.gbp.report;

import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.gbp.ProFormaInvoice;

public class ProFormaReport implements ITransferObject {

	private ProFormaInvoice proFormaInvoice;
	
	private List<ITransferObject> incidences;

	private List<ITransferObject> signatures;

	private List<ITransferObject> banks;

	public ProFormaInvoice getProFormaInvoice() {
		return proFormaInvoice;
	}

	public void setProFormaInvoice(ProFormaInvoice proFormaInvoice) {
		this.proFormaInvoice = proFormaInvoice;
	}

	public List<ITransferObject> getIncidences() {
		return incidences;
	}

	public void setIncidences(List<ITransferObject> incidences) {
		this.incidences = incidences;
	}

	public List<ITransferObject> getSignatures() {
		return signatures;
	}

	public void setSignatures(List<ITransferObject> signatures) {
		this.signatures = signatures;
	}

	public List<ITransferObject> getBanks() {
		return banks;
	}

	public void setBanks(List<ITransferObject> banks) {
		this.banks = banks;
	}

}
