package com.code.aon.file.tax.model.MOD123;


import java.io.Writer;
import java.util.List;



public interface IMOD123Factory {

	List<Exception> createDocument(List<Declaration> declarations, Writer out);


}
