package com.esferalia.aon.occam.api.model.finance;

import java.time.LocalDate;
import java.util.Date;

import com.esferalia.aon.occam.api.model.Filter.Property;

public interface InvoiceRawDocProperties {
	Property<Integer> getIdProperty();
	Property<Integer> getDomainProperty();
	Property<LocalDate> getDateProperty();
	Property<Byte> getTypeProperty();
	Property<String> getReferenceCodeProperty();
	Property<String> getRegistryNameProperty();
	Property<Byte> getStatusProperty();
}
