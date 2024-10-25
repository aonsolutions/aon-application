package net.aonsolutions.occam.api.model;

import java.sql.Timestamp;

import com.esferalia.aon.watson.util.AonCollectionUtils;

import net.aonsolutions.occam.api.model.type.VATTaxRegime;
import uk.co.jemos.podam.api.AttributeMetadata;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.PodamUtils;
import uk.co.jemos.podam.typeManufacturers.IntTypeManufacturerImpl;
import uk.co.jemos.podam.typeManufacturers.TypeManufacturer;

public class AonMocker {
	private AonMocker () {
	}
	
    private static TypeManufacturer<Integer> MANUFACTURER = new IntTypeManufacturerImpl() {
        @Override
        public Integer getInteger(AttributeMetadata attributeMetadata) {
        	return  (attributeMetadata.getPojoClass() == Timestamp.class)
    			?PodamUtils.getIntegerInRange(1, 999999999)
				:super.getInteger(attributeMetadata);
        }
    };
    
    private static PodamFactory FACTORY = new PodamFactoryImpl();
    static {
    	FACTORY
    		.getStrategy()
    		.addOrReplaceTypeManufacturer(int.class, MANUFACTURER);
    }
	
	
    public static <T> T mock(Class<T> clazz){
    	T t = FACTORY.manufacturePojo(clazz);
    	
    	if (t instanceof InvoiceFiscal invFiscal) {
    		AonCollectionUtils.stream(VATTaxRegime.values())
				.forEach( e -> invFiscal.setVatRegime(e, AonRandom.gt(50)))
			;
    	}
    	
    	if (t instanceof AonEntity) {
    		AonEntity<?> ent = (AonEntity<?>) t;
    		ent.markAsClean();
    	}
    	
    	return t;
    }
    
    
    
}
