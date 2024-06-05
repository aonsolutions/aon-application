package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.api.model.ElaborationDetailComposition;
import com.esferalia.aon.occam.api.model.ElaborationDetailType;
import com.esferalia.aon.occam.api.model.warehouse.Stock;
import com.esferalia.aon.occam.impl.jooq.validation.ElaborationPackageValidation;

public class ElaborationPackageDAO {
	
	private ElaborationPackageDAO() {
	
	}
	
//	public static ElaborationDetail update(AONContext ctx, ElaborationDetail elaborationPackage) {
//		ElaborationDetail savedElaborationPackage = ElaborationDetailDAO.getFull(ctx, f -> f.getIdProperty().eq(id));
//		ElaborationPackageValidation.validatePackageDeletion(ctx, savedElaborationPackage);
//		// TODO UPDATE ELABORATION DETAIL..
//		
//		// UPDATE ELABORATION DETAIL COMPOSITION
//		elaborationPackage.getComposition().forEach(composition -> {
//			ElaborationDetailComposition savedComposition = savedElaborationPackage.getComposition().stream().filter(f -> f.getId().equals(r.getId())).findFirst().orElse(new ElaborationDetailComposition());
//			if(!savedComposition.equals(composition)) {
//				// UPDATE ELABORATION DETAIL COMPOSITION
//				ElaborationDetailCompositionDAO.updateElaborationDetailComposition(ctx, composition);
//				// UPDATE ITEM COMPOSITION
//				ItemCompositionDAO.update(ctx, null);
//				// UPDATE STOCK
//				
//				// UPDATE ELABORATION DETAIL
//			}
//		});
//		
//	}
	
	
	public static ElaborationDetail delete(AONContext ctx, Integer id) {
		ElaborationDetail elaborationPackage = ElaborationDetailDAO.getFull(ctx, f -> f.getIdProperty().eq(id));
		ElaborationPackageValidation.validatePackageDeletion(ctx, elaborationPackage);
		
		ElaborationDetailCompositionDAO.deleteElaborationDetailComposition(ctx, f -> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getElaborationDetailProperty().eq(id)));
		
		ElaborationDetailDAO.delete(ctx, id);
		
		ItemCompositionDAO.delete(ctx, f -> f.getItemProperty().eq(elaborationPackage.getItem().getId()));
		
		ItemDAO.delete(ctx, elaborationPackage.getItem().getId());
		
		elaborationPackage.getComposition().stream().forEach(composition -> {
			//UPDATE STOCK. Subtrack removed quantity.
			Stock stock = WarehouseDAO.getStock(ctx, f-> f.getItemProperty().eq(elaborationPackage.getItem().getId())
					.and(f.getWarehouseProperty().eq(elaborationPackage.getWarehouse().getId())));
			if(stock != null) {
				stock.setQuantity(stock.getQuantity() - composition.getQuantity());
				WarehouseDAO.saveStock(ctx, stock);
			}
			
			//UPDATE ELABORATION DETAIL. Subtrack removed quantity.
			ElaborationDetail elaborationDetail = ElaborationDetailDAO.get(ctx, f -> f.getElaborationProperty().eq(elaborationPackage.getElaboration().getId())
					.and(f.getTypeProperty().eq(ElaborationDetailType.ELABORATION.value()))
					.and(f.getItemProperty().eq(composition.getItem().getId())));
			elaborationDetail.setQuantity(elaborationDetail.getQuantity() - composition.getQuantity());
			ElaborationDetailDAO.save(ctx, elaborationDetail);
		});
		
		return elaborationPackage;
	}
	
}
