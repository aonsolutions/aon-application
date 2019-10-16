package es.translogia.tedi.ewok;

import java.io.Serializable;

public enum TediPayMethod implements Serializable {

	CASH, CARD, TRANSFER, BANK, DRAFT, OTHER;

	private TediPayMethod() {
	}
}
