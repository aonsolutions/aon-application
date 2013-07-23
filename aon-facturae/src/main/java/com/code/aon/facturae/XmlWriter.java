package com.code.aon.facturae;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlWriter extends XMLWriter {

    public XmlWriter(Writer writer, OutputFormat format) {
    	super( writer, format );
    }	
	protected void writeDeclaration() throws IOException {
		OutputFormat format = getOutputFormat();

		String encoding = format.getEncoding();

		if (!format.isSuppressDeclaration()) {
			if (encoding.equals("UTF8")) {
				writer.write("<?xml version=\"1.0\"");

				if (!format.isOmitEncoding()) {
					writer.write(" encoding=\"UTF-8\"");
				}

				writer.write(" standalone=\"yes\"");
				writer.write("?>");
			} else {
				writer.write("<?xml version=\"1.0\"");

				if (!format.isOmitEncoding()) {
					writer.write(" encoding=\"" + encoding + "\"");
				}

				writer.write(" standalone=\"yes\"");
				writer.write("?>");
			}

			if (format.isNewLineAfterDeclaration()) {
				println();
			}
		}
	}

}
