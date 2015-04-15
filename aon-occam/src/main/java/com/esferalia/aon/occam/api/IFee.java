package com.esferalia.aon.occam.api;

import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.fee.Fee;

public interface IFee {


	public void insertFee(AONContext ctx, Fee f);
	public void insertFee(AONContext ctx, Stream<Fee> fs);	
	public void updateFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Fee f);
	public void deleteFee(AONContext ctx,Stream<Fee> fs);
	
}
