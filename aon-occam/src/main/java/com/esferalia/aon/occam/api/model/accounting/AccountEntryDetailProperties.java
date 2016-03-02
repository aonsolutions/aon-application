package com.esferalia.aon.occam.api.model.accounting;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface AccountEntryDetailProperties extends  AccountEntryProperties {
	
	Property<Integer> getAccountProperty();
	Property<String> getAccountCodeProperty();
	Property<String> getAccountDescriptionProperty();
	Property<String> getConceptProperty();
	Property<Double> getDebitProperty();
	Property<Double> getCreditProperty();
	Property<String> getDocumentNumber();

}
