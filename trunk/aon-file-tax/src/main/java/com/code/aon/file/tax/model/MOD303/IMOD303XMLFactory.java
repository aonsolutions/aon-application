package com.code.aon.file.tax.model.MOD303;

import java.io.Writer;

import com.code.aon.file.tax.model.MOD303.data.Declaration;

public interface IMOD303XMLFactory {

	void createDocument(Declaration declaration, Writer out);


}
