package com.esferalia.aon.occam.impl.jooq.dao;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.ElaborationDetail;
import com.esferalia.aon.occam.impl.jooq.validation.ElaborationPackageValidation;

public class ElaborationPackageDAO {
	
	private ElaborationPackageDAO() {
	
	}
	
	public static ElaborationDetail delete(AONContext ctx, Integer id) {
		ElaborationDetail elaborationPackage = ElaborationDetailDAO.getFull(ctx, f -> f.getIdProperty().eq(id));
		ElaborationPackageValidation.validatePackageDeletion(ctx, elaborationPackage);
		
		ElaborationDetailCompositionDAO.deleteElaborationDetailComposition(ctx, f -> 
				f.getDomainProperty().eq(ctx.getDomainId())
				.and(f.getElaborationDetailProperty().eq(id)));
		
		ElaborationDetailDAO.delete(ctx, id);
		
		ItemCompositionDAO.delete(ctx, f -> f.getItemProperty().eq(elaborationPackage.getItem().getId()));
		
		ItemDAO.delete(ctx, elaborationPackage.getItem().getId());
		
		return elaborationPackage;
	}
	
}
