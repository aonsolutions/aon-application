package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod200;

public interface IMODEL200 {

	public LinkedList<Mod200> getMod200s(AONContext ctx, int domain);
	public Mod200 getMod200(AONContext ctx, Integer id);
	
}
