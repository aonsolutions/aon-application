package com.esferalia.aon.occam.api.model.fiscal.format;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;

@FunctionalInterface
public interface IRecordFiller {

	void fill(Writer writer, Mod140Context ctx, Invoice invoice, Integer vatIdx)
			throws IOException;
}
