package com.esferalia.aon.occam.test;

import java.sql.Timestamp;

import com.esferalia.aon.occam.api.model.DiscountExpression;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import uk.co.jemos.podam.api.AbstractRandomDataProviderStrategy;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.DefaultClassInfoStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.PodamUtils;
import uk.co.jemos.podam.common.ManufacturingContext;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.StringTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.TypeManufacturer;

public class OccamMocker {
	
	private OccamMocker () {
	}

	private static final TypeManufacturer<String> STRING_MANUFACTURER = new StringTypeManufacturerImpl() {

		@Override
		public String getType(DataProviderStrategy strategy, AttributeMetadata attributeMetadata,
				ManufacturingContext manufacturingCtx) {
			if (AonStringUtils.equals("discountExpression",attributeMetadata.getAttributeName())) {
				int i = PodamUtils.getIntegerInRange(0, 100);
				if (i < 10) return null;
					else if (i < 70) return "0.0";
					else return AonNumberUtils.toString(PodamUtils.getDoubleInRange(0.0, 100.0));
			}
			return super.getType(strategy, attributeMetadata,manufacturingCtx);
		}
		
	};
	
    private static final TypeManufacturer<Integer> INT_MANUFACTURER = new IntTypeManufacturerImpl() {
        @Override
        public Integer getInteger(AttributeMetadata attributeMetadata) {
        	return  (attributeMetadata.getPojoClass() == Timestamp.class)
    			?PodamUtils.getIntegerInRange(1, 999999999)
				:super.getInteger(attributeMetadata);
        }
    };
	
	private static class MyDataProviderStrategy extends AbstractRandomDataProviderStrategy {
		public MyDataProviderStrategy() {
			super.setMemoization(false);
		}

		@Override
		public int getMaxDepth(Class<?> type) {
			if(Invoice.class.isAssignableFrom(type)) {
				return 1;
			} else {
				return 1;
			}
		}
	}
	private static final MyDataProviderStrategy DATA_PROVIDER_STRATEGY = new MyDataProviderStrategy();    
    private static final DefaultClassInfoStrategy CLASS_INFO_STRATEGY = DefaultClassInfoStrategy.getInstance();
    private static final PodamFactory FACTORY = new PodamFactoryImpl();
    static {
    	CLASS_INFO_STRATEGY
			.addExcludedField(Invoice.class, "fileUrl")
			.addExcludedField(Invoice.class, "epigraph")
			.addExcludedField(Invoice.class, "registryData")
			.addExcludedField(Invoice.class, "breakdown")
			.addExcludedField(InvoiceDetail.class, "invoice")
			.addExcludedField(InvoiceDetail.class, "purchaseDetail")
	    	.addExcludedField(InvoiceDetail.class, "salesDetail")
	    	.addExcludedField(InvoiceDetail.class, "deliveryDetail")
	    	.addExcludedField(InvoiceDetail.class, "incomeDetail")
	    	.addExcludedField(InvoiceDetail.class, "offerDetail")
	    	.addExcludedField(InvoiceDetail.class, "discountExpression")
	    	.addExcludedField(DiscountExpression.class, "discountExpr")
	    	.addExcludedField(Item.class, "domain")
	    	.addExcludedField(Item.class, "product")
	    	.addExcludedField(Product.class, "domain")
	    	.addExcludedField(Product.class, "itemComposition")
	    	.addExcludedField(ItemComposition.class, "discountExpression")
		;
    	
    	DATA_PROVIDER_STRATEGY
    		.addOrReplaceTypeManufacturer(String.class, STRING_MANUFACTURER)
    		.addOrReplaceTypeManufacturer(int.class, INT_MANUFACTURER)
		;
    	FACTORY
    		.setClassStrategy(CLASS_INFO_STRATEGY)
    		.setStrategy(DATA_PROVIDER_STRATEGY)
    		;    	
    }
	
	
    public static <T> T mock(Class<T> clazz){
    	return FACTORY.manufacturePojo(clazz);
    }
    
}
