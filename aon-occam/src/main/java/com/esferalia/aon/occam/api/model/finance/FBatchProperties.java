package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface FBatchProperties {
	
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<String> getDescriptionProperty();
	Property<Date> getIssueDateProperty();
	Property<Byte> getTypeProperty();
	Property<Byte> getStatusProperty();
	Property<Integer> getRBankProperty();
	Property<Integer> getBankStatementLinkProperty();
	Property<Byte> getPaymentProperty();
	Property<Byte> getConfidentialProperty();
	Property<Integer> getRAttachProperty();

}
