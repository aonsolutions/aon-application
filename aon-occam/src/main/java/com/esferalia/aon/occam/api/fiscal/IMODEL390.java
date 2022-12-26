package com.esferalia.aon.occam.api.fiscal;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;

public interface IMODEL390 {

	public Mod390 getMod390(AONContext ctx, Integer id);
	public LinkedList<Mod390> getMod390s(AONContext ctx, int domain);
	public Mod390 initialize(AONContext ctx, int year);
//	public Mod390 create(AONContext ctx, Mod390 mod390);
	public Mod390 saveComments(AONContext ctx, Mod390 mod390);
	public void deleteMod390(AONContext ctx, Mod390 mod390);
	
}
