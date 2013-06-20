package com.code.aon.file.tax.model.MOD131;

import java.io.Writer;
import java.util.List;

public interface IMOD131Factory {

	List<Exception> createDocument(List<com.code.aon.file.tax.model.MOD131.Declaration> declarations, Writer out);


}
