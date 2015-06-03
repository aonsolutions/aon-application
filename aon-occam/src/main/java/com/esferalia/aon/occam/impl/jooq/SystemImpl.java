package com.esferalia.aon.occam.impl.jooq;

import java.util.function.Supplier;
import java.util.stream.Stream;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.ISystem;
import com.esferalia.aon.occam.api.model.Bonus;
import com.esferalia.aon.occam.api.model.BonusFilter;
import com.esferalia.aon.occam.impl.jooq.dao.SystemDAO;

public class SystemImpl implements ISystem {

	@Override
	public Stream<Bonus> getAvailableBonus(AONContext ctx, BonusFilter filter,
			Supplier<Bonus> supplier) {
		return SystemDAO.getAvailableBonus(ctx, filter, supplier);
	}
}
