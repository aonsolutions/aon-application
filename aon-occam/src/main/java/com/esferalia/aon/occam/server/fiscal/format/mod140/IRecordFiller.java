package com.esferalia.aon.occam.server.fiscal.format.mod140;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod140;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;

@FunctionalInterface
public interface IRecordFiller {

	void fill(Writer writer, Mod140Context ctx, Mod140 invoice, String account, Integer vatIdx)
			throws IOException;
}
