package com.code.aon.product.dao;

import com.code.aon.common.dao.DAOConstants;
import com.code.aon.common.dao.DAOConstantsEntry;
import com.code.aon.product.Brand;
import com.code.aon.product.Catalogue;
import com.code.aon.product.CatalogueCategory;
import com.code.aon.product.CatalogueItem;
import com.code.aon.product.Item;
import com.code.aon.product.ItemAlternative;
import com.code.aon.product.ItemAttachment;
import com.code.aon.product.ItemPos;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.ProductCategoryGroup;
import com.code.aon.product.ProductCategoryTree;
import com.code.aon.product.TariffCatalogue;

/** 
* Interface for holding entity properties constants.
*/ 
public interface IProductAlias {



	/** 
	* DAOConstantsEntry for Brand entity.
	*/ 
	DAOConstantsEntry BRAND_ENTRY = DAOConstants.getDAOConstant(Brand.class);

	/** 
	* Alias value: Brand_id
	* Hibernate value: Brand.id
	*/
	String  BRAND_ID = BRAND_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Brand_name
	* Hibernate value: Brand.name
	*/
	String  BRAND_NAME = BRAND_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for Catalogue entity.
	*/ 
	DAOConstantsEntry CATALOGUE_ENTRY = DAOConstants.getDAOConstant(Catalogue.class);

	/** 
	* Alias value: Catalogue_endDate
	* Hibernate value: Catalogue.endDate
	*/
	String  CATALOGUE_END_DATE = CATALOGUE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Catalogue_id
	* Hibernate value: Catalogue.id
	*/
	String  CATALOGUE_ID = CATALOGUE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Catalogue_name
	* Hibernate value: Catalogue.name
	*/
	String  CATALOGUE_NAME = CATALOGUE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Catalogue_startDate
	* Hibernate value: Catalogue.startDate
	*/
	String  CATALOGUE_START_DATE = CATALOGUE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for CatalogueCategory entity.
	*/ 
	DAOConstantsEntry CATALOGUE_CATEGORY_ENTRY = DAOConstants.getDAOConstant(CatalogueCategory.class);

	/** 
	* Alias value: CatalogueCategory_id
	* Hibernate value: CatalogueCategory.id
	*/
	String  CATALOGUE_CATEGORY_ID = CATALOGUE_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CatalogueCategory_catalogue_id
	* Hibernate value: CatalogueCategory.catalogue.id
	*/
	String  CATALOGUE_CATEGORY_CATALOGUE_ID = CATALOGUE_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CatalogueCategory_category_id
	* Hibernate value: CatalogueCategory.category.id
	*/
	String  CATALOGUE_CATEGORY_CATEGORY_ID = CATALOGUE_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CatalogueCategory_quantity
	* Hibernate value: CatalogueCategory.quantity
	*/
	String  CATALOGUE_CATEGORY_QUANTITY = CATALOGUE_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CatalogueCategory_discount
	* Hibernate value: CatalogueCategory.discount
	*/
	String  CATALOGUE_CATEGORY_DISCOUNT = CATALOGUE_CATEGORY_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CatalogueCategory_category_name
	* Hibernate value: CatalogueCategory.category.name
	*/
	String  CATALOGUE_CATEGORY_CATEGORY_NAME = CATALOGUE_CATEGORY_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for CatalogueItem entity.
	*/ 
	DAOConstantsEntry CATALOGUE_ITEM_ENTRY = DAOConstants.getDAOConstant(CatalogueItem.class);

	/** 
	* Alias value: CatalogueItem_id
	* Hibernate value: CatalogueItem.id
	*/
	String  CATALOGUE_ITEM_ID = CATALOGUE_ITEM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: CatalogueItem_catalogue_id
	* Hibernate value: CatalogueItem.catalogue.id
	*/
	String  CATALOGUE_ITEM_CATALOGUE_ID = CATALOGUE_ITEM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: CatalogueItem_item_id
	* Hibernate value: CatalogueItem.item.id
	*/
	String  CATALOGUE_ITEM_ITEM_ID = CATALOGUE_ITEM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: CatalogueItem_quantity
	* Hibernate value: CatalogueItem.quantity
	*/
	String  CATALOGUE_ITEM_QUANTITY = CATALOGUE_ITEM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: CatalogueItem_discount
	* Hibernate value: CatalogueItem.discount
	*/
	String  CATALOGUE_ITEM_DISCOUNT = CATALOGUE_ITEM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: CatalogueItem_item_product_name
	* Hibernate value: CatalogueItem.item.product.name
	*/
	String  CATALOGUE_ITEM_ITEM_PRODUCT_NAME = CATALOGUE_ITEM_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Item entity.
	*/ 
	DAOConstantsEntry ITEM_ENTRY = DAOConstants.getDAOConstant(Item.class);

	/** 
	* Alias value: Item_id
	* Hibernate value: Item.id
	*/
	String  ITEM_ID = ITEM_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Item_barcode
	* Hibernate value: Item.barcode
	*/
	String  ITEM_BARCODE = ITEM_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Item_internet
	* Hibernate value: Item.internet
	*/
	String  ITEM_INTERNET = ITEM_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Item_product_id
	* Hibernate value: Item.product.id
	*/
	String  ITEM_PRODUCT_ID = ITEM_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Item_detail
	* Hibernate value: Item.detail
	*/
	String  ITEM_DETAIL = ITEM_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Item_price
	* Hibernate value: Item.price
	*/
	String  ITEM_PRICE = ITEM_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Item_status
	* Hibernate value: Item.status
	*/
	String  ITEM_STATUS = ITEM_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Item_expenses_percent
	* Hibernate value: Item.expensesPercent
	*/
	String  ITEM_EXPENSES_PERCENT = ITEM_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Item_expenses_fixed
	* Hibernate value: Item.expensesFixed
	*/
	String  ITEM_EXPENSES_FIXED = ITEM_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Item_profit_percent
	* Hibernate value: Item.profitPercent
	*/
	String  ITEM_PROFIT_PERCENT = ITEM_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Item_purchase_price
	* Hibernate value: Item.purchasePrice
	*/
	String  ITEM_PURCHASE_PRICE = ITEM_ENTRY.getAliasNames()[10];

	/** 
	* Alias value: Item_product_name
	* Hibernate value: Item.product.name
	*/
	String  ITEM_PRODUCT_NAME = ITEM_ENTRY.getAliasNames()[11];

	/** 
	* Alias value: Item_product_code
	* Hibernate value: Item.product.code
	*/
	String  ITEM_PRODUCT_CODE = ITEM_ENTRY.getAliasNames()[12];

	/** 
	* Alias value: Item_product_brand_id
	* Hibernate value: Item.product.brand.id
	*/
	String  ITEM_PRODUCT_BRAND_ID = ITEM_ENTRY.getAliasNames()[13];

	/** 
	* Alias value: Item_product_brand_name
	* Hibernate value: Item.product.brand.name
	*/
	String  ITEM_PRODUCT_BRAND_NAME = ITEM_ENTRY.getAliasNames()[14];

	/** 
	* Alias value: Item_product_category_id
	* Hibernate value: Item.product.category.id
	*/
	String  ITEM_PRODUCT_CATEGORY_ID = ITEM_ENTRY.getAliasNames()[15];

	/** 
	* Alias value: Item_product_category_name
	* Hibernate value: Item.product.category.name
	*/
	String  ITEM_PRODUCT_CATEGORY_NAME = ITEM_ENTRY.getAliasNames()[16];

	/** 
	* Alias value: Item_product_inventoriable
	* Hibernate value: Item.product.inventoriable
	*/
	String  ITEM_PRODUCT_INVENTORIABLE = ITEM_ENTRY.getAliasNames()[17];

	/** 
	* Alias value: Item_product_composition
	* Hibernate value: Item.product.composition
	*/
	String  ITEM_PRODUCT_COMPOSITION = ITEM_ENTRY.getAliasNames()[18];



	/** 
	* DAOConstantsEntry for ItemAlternative entity.
	*/ 
	DAOConstantsEntry ITEM_ALTERNATIVE_ENTRY = DAOConstants.getDAOConstant(ItemAlternative.class);

	/** 
	* Alias value: ItemAlternative_alternativeItem_id
	* Hibernate value: ItemAlternative.alternativeItem.id
	*/
	String  ITEM_ALTERNATIVE_ALTERNATIVE_ITEM_ID = ITEM_ALTERNATIVE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ItemAlternative_id
	* Hibernate value: ItemAlternative.id
	*/
	String  ITEM_ALTERNATIVE_ID = ITEM_ALTERNATIVE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ItemAlternative_item_id
	* Hibernate value: ItemAlternative.item.id
	*/
	String  ITEM_ALTERNATIVE_ITEM_ID = ITEM_ALTERNATIVE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ItemAlternative_priority
	* Hibernate value: ItemAlternative.priority
	*/
	String  ITEM_ALTERNATIVE_PRIORITY = ITEM_ALTERNATIVE_ENTRY.getAliasNames()[3];



	/** 
	* DAOConstantsEntry for ItemAttachment entity.
	*/ 
	DAOConstantsEntry ITEM_ATTACHMENT_ENTRY = DAOConstants.getDAOConstant(ItemAttachment.class);

	/** 
	* Alias value: ItemAttachment_data
	* Hibernate value: ItemAttachment.data
	*/
	String  ITEM_ATTACHMENT_DATA = ITEM_ATTACHMENT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ItemAttachment_description
	* Hibernate value: ItemAttachment.description
	*/
	String  ITEM_ATTACHMENT_DESCRIPTION = ITEM_ATTACHMENT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ItemAttachment_id
	* Hibernate value: ItemAttachment.id
	*/
	String  ITEM_ATTACHMENT_ID = ITEM_ATTACHMENT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ItemAttachment_item_id
	* Hibernate value: ItemAttachment.item.id
	*/
	String  ITEM_ATTACHMENT_ITEM_ID = ITEM_ATTACHMENT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ItemAttachment_mimeType
	* Hibernate value: ItemAttachment.mimeType
	*/
	String  ITEM_ATTACHMENT_MIME_TYPE = ITEM_ATTACHMENT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ItemAttachment_size
	* Hibernate value: ItemAttachment.size
	*/
	String  ITEM_ATTACHMENT_SIZE = ITEM_ATTACHMENT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: ItemAttachment_type
	* Hibernate value: ItemAttachment.type
	*/
	String  ITEM_ATTACHMENT_TYPE = ITEM_ATTACHMENT_ENTRY.getAliasNames()[6];



	/** 
	* DAOConstantsEntry for ItemPos entity.
	*/ 
	DAOConstantsEntry ITEM_POS_ENTRY = DAOConstants.getDAOConstant(ItemPos.class);

	/** 
	* Alias value: ItemPos_barcode
	* Hibernate value: ItemPos.barcode
	*/
	String  ITEM_POS_BARCODE = ITEM_POS_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ItemPos_id
	* Hibernate value: ItemPos.id
	*/
	String  ITEM_POS_ID = ITEM_POS_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ItemPos_item_id
	* Hibernate value: ItemPos.item.id
	*/
	String  ITEM_POS_ITEM_ID = ITEM_POS_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ItemPos_plu
	* Hibernate value: ItemPos.plu
	*/
	String  ITEM_POS_PLU = ITEM_POS_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ItemPos_pluProductType
	* Hibernate value: ItemPos.pluProductType
	*/
	String  ITEM_POS_PLU_PRODUCT_TYPE = ITEM_POS_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: ItemPos_shortDescription
	* Hibernate value: ItemPos.shortDescription
	*/
	String  ITEM_POS_SHORT_DESCRIPTION = ITEM_POS_ENTRY.getAliasNames()[5];



	/** 
	* DAOConstantsEntry for Product entity.
	*/ 
	DAOConstantsEntry PRODUCT_ENTRY = DAOConstants.getDAOConstant(Product.class);

	/** 
	* Alias value: Product_brand_id
	* Hibernate value: Product.brand.id
	*/
	String  PRODUCT_BRAND_ID = PRODUCT_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: Product_product_category_id
	* Hibernate value: Product.category.id
	*/
	String  PRODUCT_PRODUCT_CATEGORY_ID = PRODUCT_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: Product_code
	* Hibernate value: Product.code
	*/
	String  PRODUCT_CODE = PRODUCT_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: Product_composition
	* Hibernate value: Product.composition
	*/
	String  PRODUCT_COMPOSITION = PRODUCT_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: Product_id
	* Hibernate value: Product.id
	*/
	String  PRODUCT_ID = PRODUCT_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: Product_inventoriable
	* Hibernate value: Product.inventoriable
	*/
	String  PRODUCT_INVENTORIABLE = PRODUCT_ENTRY.getAliasNames()[5];

	/** 
	* Alias value: Product_name
	* Hibernate value: Product.name
	*/
	String  PRODUCT_NAME = PRODUCT_ENTRY.getAliasNames()[6];

	/** 
	* Alias value: Product_retention_id
	* Hibernate value: Product.retention.id
	*/
	String  PRODUCT_RETENTION_ID = PRODUCT_ENTRY.getAliasNames()[7];

	/** 
	* Alias value: Product_status
	* Hibernate value: Product.status
	*/
	String  PRODUCT_STATUS = PRODUCT_ENTRY.getAliasNames()[8];

	/** 
	* Alias value: Product_type
	* Hibernate value: Product.type
	*/
	String  PRODUCT_TYPE = PRODUCT_ENTRY.getAliasNames()[9];

	/** 
	* Alias value: Product_vat_id
	* Hibernate value: Product.vat.id
	*/
	String  PRODUCT_VAT_ID = PRODUCT_ENTRY.getAliasNames()[10];



	/** 
	* DAOConstantsEntry for ProductCategory entity.
	*/ 
	DAOConstantsEntry PRODUCT_CATEGORY_ENTRY = DAOConstants.getDAOConstant(ProductCategory.class);

	/** 
	* Alias value: ProductCategory_category_group
	* Hibernate value: ProductCategory.group
	*/
	String  PRODUCT_CATEGORY_CATEGORY_GROUP = PRODUCT_CATEGORY_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductCategory_category_group_id
	* Hibernate value: ProductCategory.group.id
	*/
	String  PRODUCT_CATEGORY_CATEGORY_GROUP_ID = PRODUCT_CATEGORY_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProductCategory_id
	* Hibernate value: ProductCategory.id
	*/
	String  PRODUCT_CATEGORY_ID = PRODUCT_CATEGORY_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: ProductCategory_itemPattern
	* Hibernate value: ProductCategory.itemPattern
	*/
	String  PRODUCT_CATEGORY_ITEM_PATTERN = PRODUCT_CATEGORY_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: ProductCategory_name
	* Hibernate value: ProductCategory.name
	*/
	String  PRODUCT_CATEGORY_NAME = PRODUCT_CATEGORY_ENTRY.getAliasNames()[4];



	/** 
	* DAOConstantsEntry for ProductCategoryGroup entity.
	*/ 
	DAOConstantsEntry PRODUCT_CATEGORY_GROUP_ENTRY = DAOConstants.getDAOConstant(ProductCategoryGroup.class);

	/** 
	* Alias value: ProductCategoryGroup_id
	* Hibernate value: ProductCategoryGroup.id
	*/
	String  PRODUCT_CATEGORY_GROUP_ID = PRODUCT_CATEGORY_GROUP_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductCategoryGroup_name
	* Hibernate value: ProductCategoryGroup.name
	*/
	String  PRODUCT_CATEGORY_GROUP_NAME = PRODUCT_CATEGORY_GROUP_ENTRY.getAliasNames()[1];



	/** 
	* DAOConstantsEntry for ProductCategoryTree entity.
	*/ 
	DAOConstantsEntry PRODUCT_CATEGORY_TREE_ENTRY = DAOConstants.getDAOConstant(ProductCategoryTree.class);

	/** 
	* Alias value: ProductCategoryTree_child_id
	* Hibernate value: ProductCategoryTree.child.id
	*/
	String  PRODUCT_CATEGORY_TREE_CHILD_ID = PRODUCT_CATEGORY_TREE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: ProductCategoryTree_id
	* Hibernate value: ProductCategoryTree.id
	*/
	String  PRODUCT_CATEGORY_TREE_ID = PRODUCT_CATEGORY_TREE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: ProductCategoryTree_parent_id
	* Hibernate value: ProductCategoryTree.parent.id
	*/
	String  PRODUCT_CATEGORY_TREE_PARENT_ID = PRODUCT_CATEGORY_TREE_ENTRY.getAliasNames()[2];



	/** 
	* DAOConstantsEntry for TariffCatalogue entity.
	*/ 
	DAOConstantsEntry TARIFF_CATALOGUE_ENTRY = DAOConstants.getDAOConstant(TariffCatalogue.class);

	/** 
	* Alias value: TariffCatalogue_id
	* Hibernate value: TariffCatalogue.id
	*/
	String  TARIFF_CATALOGUE_ID = TARIFF_CATALOGUE_ENTRY.getAliasNames()[0];

	/** 
	* Alias value: TariffCatalogue_tariff_id
	* Hibernate value: TariffCatalogue.tariff.id
	*/
	String  TARIFF_CATALOGUE_TARIFF_ID = TARIFF_CATALOGUE_ENTRY.getAliasNames()[1];

	/** 
	* Alias value: TariffCatalogue_catalogue_id
	* Hibernate value: TariffCatalogue.catalogue.id
	*/
	String  TARIFF_CATALOGUE_CATALOGUE_ID = TARIFF_CATALOGUE_ENTRY.getAliasNames()[2];

	/** 
	* Alias value: TariffCatalogue_catalogue_name
	* Hibernate value: TariffCatalogue.catalogue.name
	*/
	String  TARIFF_CATALOGUE_CATALOGUE_NAME = TARIFF_CATALOGUE_ENTRY.getAliasNames()[3];

	/** 
	* Alias value: TariffCatalogue_catalogue_startDate
	* Hibernate value: TariffCatalogue.catalogue.startDate
	*/
	String  TARIFF_CATALOGUE_CATALOGUE_START_DATE = TARIFF_CATALOGUE_ENTRY.getAliasNames()[4];

	/** 
	* Alias value: TariffCatalogue_catalogue_endDate
	* Hibernate value: TariffCatalogue.catalogue.endDate
	*/
	String  TARIFF_CATALOGUE_CATALOGUE_END_DATE = TARIFF_CATALOGUE_ENTRY.getAliasNames()[5];


}