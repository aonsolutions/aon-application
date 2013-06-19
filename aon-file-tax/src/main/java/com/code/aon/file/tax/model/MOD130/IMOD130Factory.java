package com.code.aon.file.tax.model.MOD130;

import java.io.Writer;
import java.util.List;


public interface IMOD130Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
