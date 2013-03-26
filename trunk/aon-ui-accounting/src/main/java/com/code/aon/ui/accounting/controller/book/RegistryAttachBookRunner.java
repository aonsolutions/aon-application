package com.code.aon.ui.accounting.controller.book;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.RegistryAttachment;

public class RegistryAttachBookRunner extends AbsAccountingBookRunner {

	@Override
	public boolean accept(BookType type) {
		return type == BookType.OTROS 
			|| type == BookType.MEMORIA
			|| type == BookType.INVENTAR;
	}

	@Override
	public void run( OutputStream out ) throws AccountingBookException {
		try {
			AccountingBook book = getAccountingContext().getAccountingBook();
    		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
    		RegistryAttachment ra = (RegistryAttachment) bean.get( book.getAttachId() );
    		book.setDescription(ra.getDescription());
    		book.setMimeType( ra.getMimeType() );
    		IOUtils.copy(new ByteArrayInputStream(ra.getData()), out);
		} catch (IOException e) {
			throw new AccountingBookException(e);
		} catch (ManagerBeanException e) {
			throw new AccountingBookException(e);
		}
	}
	

}
