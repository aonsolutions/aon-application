package com.code.aon.file.tax.model.MOD303;

import java.io.Writer;
import java.util.List;

import com.code.aon.file.tax.model.MOD303.Declaration;

public interface IMOD303Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
