package com.esferalia.aon.occam.mod200.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IFiscalModel;
import com.esferalia.aon.occam.mod200.api.model.Mod200;
import com.esferalia.aon.occam.mod200.impl.jooq.MODEL200Impl;

public class MODEL200 {
	
	private MODEL200() {

	}

	private static IMODEL200 getImpl() {
		return new MODEL200Impl();
	}

	public static LinkedList<Mod200> getMod200s(Occam occam) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod200s(ctx, occam.getDomain());
		}
	}

	public static Mod200 getMod200(Occam occam, Integer id) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) {
			return getImpl().getMod200(ctx, id);
		}
	}

	public static Mod200 saveComments(Occam occam, Mod200 mod200) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) { 
			return getImpl().saveComments(ctx, mod200);
		}
	}
	
	public static void saveNrc(Occam occam, IFiscalModel model) {
		try (CloseableAONContext ctx = AONContext.getAONContext(occam)) { 
			getImpl().saveNrc(ctx, model);
		}
	}

}
