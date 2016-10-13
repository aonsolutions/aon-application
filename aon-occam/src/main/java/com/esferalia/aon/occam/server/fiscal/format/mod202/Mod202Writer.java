package com.esferalia.aon.occam.server.fiscal.format.mod202;

import java.io.IOException;
import java.io.Writer;

import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.Period;


public class Mod202Writer {

	public static void fill(Writer writer, Mod202 mod202) throws IOException {
		if (mod202.getYear() < 2016) {
			for (Aeat2015Record1 item : Aeat2015Record1.values()) {
				item.getFiller().fill(writer, mod202);
			}
			for (Aeat2015Record2 item : Aeat2015Record2.values()) {
				item.getFiller().fill(writer, mod202);
			}
		} else {
			if (mod202.getYear() == 2016 && mod202.getPeriod() == Period.T1) {
				for (Aeat2016Record1 item : Aeat2016Record1.values()) {
					item.getFiller().fill(writer, mod202);
				}
				for (Aeat2016Record2 item : Aeat2016Record2.values()) {
					item.getFiller().fill(writer, mod202);
				}
			} else {
				for (Aeat20162Record1 item : Aeat20162Record1.values()) {
					item.getFiller().fill(writer, mod202);
				}
				for (Aeat20162Record2 item : Aeat20162Record2.values()) {
					item.getFiller().fill(writer, mod202);
				}
			}
		}
		writer.flush();
	}
	

}
