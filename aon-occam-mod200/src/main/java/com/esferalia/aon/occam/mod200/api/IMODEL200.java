package com.esferalia.aon.occam.mod200.api;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.mod200.api.model.Mod200;

public interface IMODEL200 {

	public LinkedList<Mod200> getMod200s(AONContext ctx, int domain);
	public Mod200 getMod200(AONContext ctx, Integer id);
	public Mod200 saveComments(AONContext ctx, Mod200 mod200);
	
}
