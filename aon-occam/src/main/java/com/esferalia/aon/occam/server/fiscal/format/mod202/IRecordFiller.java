package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod202;

@FunctionalInterface
public interface IRecordFiller {

	void fill(Writer writer, Mod202 mod202) throws IOException;
	
}
