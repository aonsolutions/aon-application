package com.code.gbp.report;

import java.util.List;

import com.code.aon.common.ITransferObject;
import com.code.gbp.Offer;

public class OfferReport implements ITransferObject {

	private Offer offer;
	
	private List<ITransferObject> incidences;

	private List<ITransferObject> signatures;

	public Offer getOffer() {
		return offer;
	}

	public void setOffer(Offer offer) {
		this.offer = offer;
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

	
}
