package com.esferalia.aon.occam.impl.jooq.validation;

import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;

public class ElaborationPackageValidation {
	
	public static final BiConsumer<AONContext, ElaborationDetail> CHECK_TYPE = (ctx, elaborationPackage) -> {
		if(!ElaborationDetailType.PACKAGING.equals(elaborationPackage.getType())) {
			// TODO ERROR..
		}
	};
	
	public static final BiConsumer<AONContext, ElaborationDetail> CHECK_DELIVERY_PACKAGING = (ctx, elaborationPackage) -> {
		// TODO Buscar envase SSCC y verificar que no se utilice en ningún albarán (delivery_packaging)
	};
	
	public static final BiConsumer<AONContext, ElaborationDetail> CHECK_COMPOSITION = (ctx, elaborationPackage) -> {
		// TODO Comparar el contenido de item_composition y elaboration_detail_compisition. Si son diferentes devolver error.
	};

	public static void validatePackageDeletion(AONContext ctx, ElaborationDetail elaborationPackage) {
		CHECK_TYPE
		.andThen(CHECK_DELIVERY_PACKAGING)
		.andThen(CHECK_COMPOSITION)
		.accept(ctx, elaborationPackage);
	}
	

}
