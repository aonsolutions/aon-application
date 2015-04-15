package com.esferalia.aon.occam.impl.jooq;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.IFee;
import com.esferalia.aon.occam.api.model.fee.Fee;
import com.esferalia.aon.occam.impl.jooq.dao.FeeDAO;

public class FeeImpl implements IFee{

	@Override
	public void insertFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.insert(ctx, f);
		} );		
	}

	@Override
	public void insertFee(AONContext ctx, Stream<Fee> fs) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.insert(ctx, fs);
		} );			
	}

	@Override
	public void updateFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.update(ctx, f);
		} );			
	}

	@Override
	public void deleteFee(AONContext ctx, Fee f) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, f);
		} );			
	}

	@Override
	public void deleteFee(AONContext ctx, Stream<Fee> fs) {
		ctx.getDslContext().transaction(configuration -> {
			FeeDAO.delete(ctx, fs);
		} );			
	}
}
