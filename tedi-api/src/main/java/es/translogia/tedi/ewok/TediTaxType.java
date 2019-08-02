package es.translogia.tedi.ewok;

import java.io.Serializable;

public enum TediTaxType implements Serializable {

	IVA, IRPF_ALQ, IRPF_PROF, IRPF_CM, IRPF_AGR, IRPF_TRANS;

	private TediTaxType() {
	}

}
