package com.code.aon.tasCommercial.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.tasCommercial.TasOffer;

/**
 * This Class manages the DAOConstantsEntry group of this package,
 * witch manages information of an IDAO constants stored in a file,
 * 
 * @author Consulting & Development.
 * @version 1.0
 */
public interface ITasCommercialAlias {


    /**
     * DAOConstantsEntry for TasOffer.
     */
	DAOConstantsEntry TAS_OFFER_ENTRY = DAOConstants.getDAOConstant(TasOffer.class);

    /**
     * Alias name.
     */
	// Alias value: TasOffer_id
	// Hibernate value: TasOffer.id
	String  TAS_OFFER_ID = TAS_OFFER_ENTRY.getAliasNames()[0];

    /**
     * Alias name.
     */
	// Alias value: TasOffer_offer_id
	// Hibernate value: TasOffer.offer.id
	String  TAS_OFFER_OFFER_ID = TAS_OFFER_ENTRY.getAliasNames()[1];

    /**
     * Alias name.
     */
	// Alias value: TasOffer_supportOrder_id
	// Hibernate value: TasOffer.supportOrder.id
	String  TAS_OFFER_SUPPORT_ORDER_ID = TAS_OFFER_ENTRY.getAliasNames()[2];


}