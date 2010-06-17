package com.code.aon.ui.product.sw.controller;

import java.util.Map;

import com.code.aon.common.ILookupObject;
import com.code.aon.product.Item;
import com.code.aon.ui.form.NewWindowController;
import com.code.aon.ui.product.util.ItemPriceProvider;

/**
 * Controller used in the creation window of <code>Item</code>.
 */
public class ItemNewWindowController extends NewWindowController {

    /** ITEM_TOTAL_PRICE. */
    private static final String ITEM_TOTAL_PRICE = "Item_total_price";
    
    /** ITEM_BASE_PRICE. */
    private static final String ITEM_BASE_PRICE = "Item_base_price";

    /** ITEM_TAXES. */
    private static final String ITEM_TAXES = "Item_taxes";

    /** ITEM_PRODUCT_CATEGORY_ID. */
    private static final String ITEM_PRODUCT_CATEGORY_ID = "Item_product_category_id";
    
    /** PRODUCT_ID. */
    private static final String PRODUCT_ID = "Product_id";

    /**
	 * Adds custom entries into the lookup map.
	 * 
	 * @param ito the ILookupObject
	 * @param map the map
	 */
    protected void customizeLookupMap(ILookupObject ito, Map<String,Object> map) {
        ItemPriceProvider provider = new ItemPriceProvider();

        map.put(ITEM_TOTAL_PRICE, new Double(provider.getRealTotalPrice((Item)ito)));
        map.put(ITEM_BASE_PRICE, new Double(provider.getRealBasePrice((Item)ito)));
        map.put(ITEM_TAXES, new Double(provider.getExpensesRate((Item)ito)));
        map.put(ITEM_PRODUCT_CATEGORY_ID, ((Item)ito).getProduct().getCategory().getId());
        map.put(PRODUCT_ID, ((Item)ito).getProduct().getId());
    }
}