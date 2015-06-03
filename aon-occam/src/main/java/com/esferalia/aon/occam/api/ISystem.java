package com.esferalia.aon.occam.api;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;

public interface ISystem {

	/**
	 * Return all available bonus.
	 * 
	 * @param ctx
	 * @param supplier
	 * @return
	 */
	public Stream<Bonus> getAvailableBonus(AONContext ctx, BonusFilter filter,
			Supplier<Bonus> supplier);

}
