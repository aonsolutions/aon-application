package com.esferalia.aon.occam.server.fiscal.format.mod140;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod140;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;
import com.esferalia.aon.occam.api.model.type.InvoiceType;


public class Mod140Format {

	public static void fill(Writer writer, Mod140Context m140ctx, Mod140 invoice) throws IOException {
		if (invoice.getType() == InvoiceType.SALES) {
			Record4Bizkaia2015.fill(writer, m140ctx, invoice);
		} else {
			Record5Bizkaia2015.fill(writer, m140ctx, invoice);
		}
		
	}
	
}
