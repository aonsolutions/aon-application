package com.code.aon.file.tax.model.MOD311;

import java.io.Writer;
import java.util.List;


public interface IMOD311Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
