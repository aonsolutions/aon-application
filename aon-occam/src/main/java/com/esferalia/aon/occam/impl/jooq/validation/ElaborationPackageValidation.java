package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.List;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.product.ItemComposition;
import com.esferalia.aon.occam.api.model.warehouse.DeliveryPackaging;
import com.esferalia.aon.occam.impl.jooq.dao.DeliveryPackagingDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemCompositionDAO;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class ElaborationPackageValidation {
	
	public static final BiConsumer<AONContext, ElaborationDetail> CHECK_TYPE = (ctx, elaborationPackage) -> {
		if(!ElaborationDetailType.PACKAGING.equals(elaborationPackage.getType())) {
			throw new AonCoreException("El envase de la elboración que intenta borrar no es de tipo envase");
		}
	};
	
	public static final BiConsumer<AONContext, ElaborationDetail> CHECK_DELIVERY_PACKAGING = (ctx, elaborationPackage) -> {
		Integer itemId = elaborationPackage.getItem().getId();
		DeliveryPackaging deliveryPackaging = DeliveryPackagingDAO.get(ctx, f -> f.getItemProperty().eq(itemId));
		if(deliveryPackaging != null && !deliveryPackaging.isEmpty()) {
			throw new AonCoreException("El envase se está utilizando en un albarán");
		}
	};
	
	public static final BiConsumer<AONContext, ElaborationDetail> CHECK_COMPOSITION = (ctx, elaborationPackage) -> {
		Integer itemId = elaborationPackage.getItem().getId();
		List<ItemComposition> icList = ItemCompositionDAO.getList(ctx, f -> f.getItemProperty().eq(itemId));
		boolean error = icList.size() != elaborationPackage.getComposition().size();
		if(!error) {
			for(ItemComposition r : icList) {
				ElaborationDetailComposition edc= elaborationPackage.getComposition().stream().filter(f -> f.getItem().getId().equals(r.getCompositionItemId())).findFirst().orElse(null);
				if(edc == null || edc.getQuantity() != r.getQuantity()) {
					error = true; 
				}				
			}		
		}
		if(error) throw new AonCoreException("El envase ha sido modificado, no contiene lo mismo que se elaboró");
	};

	public static void validatePackageDeletion(AONContext ctx, ElaborationDetail elaborationPackage) {
		CHECK_TYPE
		.andThen(CHECK_DELIVERY_PACKAGING)
		.andThen(CHECK_COMPOSITION)
		.accept(ctx, elaborationPackage);
	}
	

}
