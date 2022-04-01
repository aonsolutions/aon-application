package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;
import com.esferalia.aon.occam.impl.jooq.fiscal.MODEL200Impl;

public class MODEL200 {
	
	private MODEL200() {

	}

	private static IMODEL200 getImpl() {
		return new MODEL200Impl();
	}

	public static LinkedList<Mod200> getMod200s(Occam occam) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod200s(ctx, occam.getDomain());
		}
	}

	public static Mod200 getMod200(Occam occam, Integer id) {
		try (AONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod200(ctx, id);
		}
	}

}
