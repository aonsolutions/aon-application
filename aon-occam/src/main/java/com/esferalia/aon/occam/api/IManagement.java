package com.esferalia.aon.occam.api;

import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.management.OfferDetail;
import com.esferalia.aon.occam.api.model.management.OfferFilter;

public interface IManagement {
	
	
	// 	***********************************************
	// 	************************************* OFFER ***
	// 	***********************************************
	void getOfferDetails(AONContext ctx,Consumer<OfferDetail> action, OfferFilter filter);

	
	
}
