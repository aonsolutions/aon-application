package com.code.aon.composition.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.CompositionExpense;
import com.code.aon.composition.Production;
import com.code.aon.composition.ProductionDetail;
import com.code.aon.composition.ProductionExpense;

/**
 * This Class manages the DAOConstantsEntry group of this package,
 * which manages information of an IDAO constants stored in a file.
 * 
 * @author Consulting & Development.
 * @version 1.0
 */
public interface ICompositionAlias {


    /**
     * DAOConstantsEntry for Composition.
     */
	DAOConstantsEntry COMPOSITION_ENTRY = DAOConstants.getDAOConstant(Composition.class);

	// Alias value: Composition_id
	// Hibernate value: Composition.id
	String  COMPOSITION_ID = COMPOSITION_ENTRY.getAliasNames()[0];

	// Alias value: Composition_type
	// Hibernate value: Composition.type
	String  COMPOSITION_TYPE = COMPOSITION_ENTRY.getAliasNames()[1];

	// Alias value: Composition_description
	// Hibernate value: Composition.description
	String  COMPOSITION_DESCRIPTION = COMPOSITION_ENTRY.getAliasNames()[2];

	// Alias value: Composition_item
	// Hibernate value: Composition.item.id
	String  COMPOSITION_ITEM = COMPOSITION_ENTRY.getAliasNames()[3];

	// Alias value: Composition_quantity
	// Hibernate value: Composition.quantity
	String  COMPOSITION_QUANTITY = COMPOSITION_ENTRY.getAliasNames()[4];

	// Alias value: Composition_price
	// Hibernate value: Composition.price
	String  COMPOSITION_PRICE = COMPOSITION_ENTRY.getAliasNames()[5];

	// Alias value: Composition_expenses_percent
	// Hibernate value: Composition.expensesPercent
	String  COMPOSITION_EXPENSES_PERCENT = COMPOSITION_ENTRY.getAliasNames()[6];

	// Alias value: Composition_expenses_fixed
	// Hibernate value: Composition.expensesFixed
	String  COMPOSITION_EXPENSES_FIXED = COMPOSITION_ENTRY.getAliasNames()[7];

	// Alias value: Composition_price_in_details
	// Hibernate value: Composition.priceInDetails
	String  COMPOSITION_PRICE_IN_DETAILS = COMPOSITION_ENTRY.getAliasNames()[8];

	// Alias value: Composition_item_product_name
	// Hibernate value: Composition.item.product.name
	String  COMPOSITION_ITEM_PRODUCT_NAME = COMPOSITION_ENTRY.getAliasNames()[9];

	// Alias value: Composition_item_product_code
	// Hibernate value: Composition.item.product.code
	String  COMPOSITION_ITEM_PRODUCT_CODE = COMPOSITION_ENTRY.getAliasNames()[10];

	// Alias value: Composition_item_product_category
	// Hibernate value: Composition.item.product.category.name
	String  COMPOSITION_ITEM_PRODUCT_CATEGORY = COMPOSITION_ENTRY.getAliasNames()[11];

	// Alias value: Composition_item_product_composition
	// Hibernate value: Composition.item.product.composition
	String  COMPOSITION_ITEM_PRODUCT_COMPOSITION = COMPOSITION_ENTRY.getAliasNames()[12];


    /**
     * DAOConstantsEntry for CompositionDetail.
     */
	DAOConstantsEntry COMPOSITION_DETAIL_ENTRY = DAOConstants.getDAOConstant(CompositionDetail.class);

	// Alias value: CompositionDetail_composition_id
	// Hibernate value: CompositionDetail.composition.id
	String  COMPOSITION_DETAIL_COMPOSITION_ID = COMPOSITION_DETAIL_ENTRY.getAliasNames()[0];

	// Alias value: CompositionDetail_description
	// Hibernate value: CompositionDetail.description
	String  COMPOSITION_DETAIL_DESCRIPTION = COMPOSITION_DETAIL_ENTRY.getAliasNames()[1];

	// Alias value: CompositionDetail_id
	// Hibernate value: CompositionDetail.id
	String  COMPOSITION_DETAIL_ID = COMPOSITION_DETAIL_ENTRY.getAliasNames()[2];

	// Alias value: CompositionDetail_item_id
	// Hibernate value: CompositionDetail.item.id
	String  COMPOSITION_DETAIL_ITEM_ID = COMPOSITION_DETAIL_ENTRY.getAliasNames()[3];

	// Alias value: CompositionDetail_price
	// Hibernate value: CompositionDetail.price
	String  COMPOSITION_DETAIL_PRICE = COMPOSITION_DETAIL_ENTRY.getAliasNames()[4];

	// Alias value: CompositionDetail_quantity
	// Hibernate value: CompositionDetail.quantity
	String  COMPOSITION_DETAIL_QUANTITY = COMPOSITION_DETAIL_ENTRY.getAliasNames()[5];


    /**
     * DAOConstantsEntry for CompositionExpense.
     */
	DAOConstantsEntry COMPOSITION_EXPENSE_ENTRY = DAOConstants.getDAOConstant(CompositionExpense.class);

	// Alias value: CompositionExpense_composition_id
	// Hibernate value: CompositionExpense.composition.id
	String  COMPOSITION_EXPENSE_COMPOSITION_ID = COMPOSITION_EXPENSE_ENTRY.getAliasNames()[0];

	// Alias value: CompositionExpense_description
	// Hibernate value: CompositionExpense.description
	String  COMPOSITION_EXPENSE_DESCRIPTION = COMPOSITION_EXPENSE_ENTRY.getAliasNames()[1];

	// Alias value: CompositionExpense_id
	// Hibernate value: CompositionExpense.id
	String  COMPOSITION_EXPENSE_ID = COMPOSITION_EXPENSE_ENTRY.getAliasNames()[2];

	// Alias value: CompositionExpense_price
	// Hibernate value: CompositionExpense.price
	String  COMPOSITION_EXPENSE_PRICE = COMPOSITION_EXPENSE_ENTRY.getAliasNames()[3];

	// Alias value: CompositionExpense_quantity
	// Hibernate value: CompositionExpense.quantity
	String  COMPOSITION_EXPENSE_QUANTITY = COMPOSITION_EXPENSE_ENTRY.getAliasNames()[4];


    /**
     * DAOConstantsEntry for Production.
     */
	DAOConstantsEntry PRODUCTION_ENTRY = DAOConstants.getDAOConstant(Production.class);

	// Alias value: Production_description
	// Hibernate value: Production.description
	String  PRODUCTION_DESCRIPTION = PRODUCTION_ENTRY.getAliasNames()[0];

	// Alias value: Production_id
	// Hibernate value: Production.id
	String  PRODUCTION_ID = PRODUCTION_ENTRY.getAliasNames()[1];

	// Alias value: Production_initialQuantity
	// Hibernate value: Production.initialQuantity
	String  PRODUCTION_INITIAL_QUANTITY = PRODUCTION_ENTRY.getAliasNames()[2];

	// Alias value: Production_item_id
	// Hibernate value: Production.item.id
	String  PRODUCTION_ITEM_ID = PRODUCTION_ENTRY.getAliasNames()[3];

	// Alias value: Production_lotCode
	// Hibernate value: Production.lotCode
	String  PRODUCTION_LOT_CODE = PRODUCTION_ENTRY.getAliasNames()[4];

	// Alias value: Production_price
	// Hibernate value: Production.price
	String  PRODUCTION_PRICE = PRODUCTION_ENTRY.getAliasNames()[5];

	// Alias value: Production_productionDate
	// Hibernate value: Production.productionDate
	String  PRODUCTION_PRODUCTION_DATE = PRODUCTION_ENTRY.getAliasNames()[6];

	// Alias value: Production_quantity
	// Hibernate value: Production.quantity
	String  PRODUCTION_QUANTITY = PRODUCTION_ENTRY.getAliasNames()[7];


    /**
     * DAOConstantsEntry for ProductionDetail.
     */
	DAOConstantsEntry PRODUCTION_DETAIL_ENTRY = DAOConstants.getDAOConstant(ProductionDetail.class);

	// Alias value: ProductionDetail_description
	// Hibernate value: ProductionDetail.description
	String  PRODUCTION_DETAIL_DESCRIPTION = PRODUCTION_DETAIL_ENTRY.getAliasNames()[0];

	// Alias value: ProductionDetail_id
	// Hibernate value: ProductionDetail.id
	String  PRODUCTION_DETAIL_ID = PRODUCTION_DETAIL_ENTRY.getAliasNames()[1];

	// Alias value: ProductionDetail_initialQuantity
	// Hibernate value: ProductionDetail.initialQuantity
	String  PRODUCTION_DETAIL_INITIAL_QUANTITY = PRODUCTION_DETAIL_ENTRY.getAliasNames()[2];

	// Alias value: ProductionDetail_item_id
	// Hibernate value: ProductionDetail.item.id
	String  PRODUCTION_DETAIL_ITEM_ID = PRODUCTION_DETAIL_ENTRY.getAliasNames()[3];

	// Alias value: ProductionDetail_price
	// Hibernate value: ProductionDetail.price
	String  PRODUCTION_DETAIL_PRICE = PRODUCTION_DETAIL_ENTRY.getAliasNames()[4];

	// Alias value: ProductionDetail_production_id
	// Hibernate value: ProductionDetail.production.id
	String  PRODUCTION_DETAIL_PRODUCTION_ID = PRODUCTION_DETAIL_ENTRY.getAliasNames()[5];

	// Alias value: ProductionDetail_quantity
	// Hibernate value: ProductionDetail.quantity
	String  PRODUCTION_DETAIL_QUANTITY = PRODUCTION_DETAIL_ENTRY.getAliasNames()[6];


    /**
     * DAOConstantsEntry for ProductionExpense.
     */
	DAOConstantsEntry PRODUCTION_EXPENSE_ENTRY = DAOConstants.getDAOConstant(ProductionExpense.class);

	// Alias value: ProductionExpense_description
	// Hibernate value: ProductionExpense.description
	String  PRODUCTION_EXPENSE_DESCRIPTION = PRODUCTION_EXPENSE_ENTRY.getAliasNames()[0];

	// Alias value: ProductionExpense_id
	// Hibernate value: ProductionExpense.id
	String  PRODUCTION_EXPENSE_ID = PRODUCTION_EXPENSE_ENTRY.getAliasNames()[1];

	// Alias value: ProductionExpense_price
	// Hibernate value: ProductionExpense.price
	String  PRODUCTION_EXPENSE_PRICE = PRODUCTION_EXPENSE_ENTRY.getAliasNames()[2];

	// Alias value: ProductionExpense_production_id
	// Hibernate value: ProductionExpense.production.id
	String  PRODUCTION_EXPENSE_PRODUCTION_ID = PRODUCTION_EXPENSE_ENTRY.getAliasNames()[3];

	// Alias value: ProductionExpense_quantity
	// Hibernate value: ProductionExpense.quantity
	String  PRODUCTION_EXPENSE_QUANTITY = PRODUCTION_EXPENSE_ENTRY.getAliasNames()[4];


}