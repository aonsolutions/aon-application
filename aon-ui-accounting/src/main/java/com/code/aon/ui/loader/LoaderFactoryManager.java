package com.code.aon.ui.loader;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.ui.loader.factory.AccountEntryDetailLoaderFactory;
import com.code.aon.ui.loader.factory.AccountEntryLoaderFactory;
import com.code.aon.ui.loader.factory.AccountInvoiceLoaderFactory;
import com.code.aon.ui.loader.factory.AccountLoaderFactory;
import com.code.aon.ui.loader.factory.AmortizationDetailLoaderFactory;
import com.code.aon.ui.loader.factory.AmortizationLoaderFactory;
import com.code.aon.ui.loader.factory.AutoConceptLoaderFactory;
import com.code.aon.ui.loader.factory.CompanyBankLoaderFactory;
import com.code.aon.ui.loader.factory.CompanyDirStaffFactory;
import com.code.aon.ui.loader.factory.CompanyLoaderFactory;
import com.code.aon.ui.loader.factory.CreditorLoaderFactory;
import com.code.aon.ui.loader.factory.CustomerFeeLoaderFactory;
import com.code.aon.ui.loader.factory.CustomerLoaderFactory;
import com.code.aon.ui.loader.factory.EnterpriseActivityLoaderFactory;
import com.code.aon.ui.loader.factory.FinanceLoaderFactory;
import com.code.aon.ui.loader.factory.InvoiceDetailLoaderFactory;
import com.code.aon.ui.loader.factory.InvoiceLoaderFactory;
import com.code.aon.ui.loader.factory.ItemLoaderFactory;
import com.code.aon.ui.loader.factory.SupplierLoaderFactory;
import com.code.aon.ui.loader.factory.TargetLoaderFactory;
import com.code.aon.ui.loader.pojo.ILoadedPojo;

public class LoaderFactoryManager {
	
	private List<ILoaderFactory<ILoadedPojo>> factories;
	
	public LoaderFactoryManager(ILoaderEngine engine) {
		factories = new LinkedList<ILoaderFactory<ILoadedPojo>>();
		
		factories.add( new CustomerLoaderFactory(engine));
		factories.add( new CreditorLoaderFactory(engine));
		factories.add( new SupplierLoaderFactory(engine));
		factories.add( new TargetLoaderFactory(engine));
		factories.add( new ItemLoaderFactory(engine));
		factories.add( new InvoiceLoaderFactory(engine));
		factories.add( new InvoiceDetailLoaderFactory(engine));
		factories.add( new FinanceLoaderFactory(engine));
		factories.add( new AccountLoaderFactory(engine));
		factories.add( new AccountInvoiceLoaderFactory(engine));
		factories.add( new AccountEntryDetailLoaderFactory(engine));
		factories.add( new AccountEntryLoaderFactory(engine));
		factories.add( new CompanyLoaderFactory(engine));
		factories.add( new AmortizationLoaderFactory(engine));
		factories.add( new AmortizationDetailLoaderFactory(engine));
		factories.add( new CompanyBankLoaderFactory(engine));
		factories.add( new CompanyDirStaffFactory(engine));
		factories.add( new AutoConceptLoaderFactory(engine));
		factories.add( new CustomerFeeLoaderFactory(engine));
		factories.add( new EnterpriseActivityLoaderFactory(engine));		
	}
	
	public List<ILoaderFactory<ILoadedPojo>> getFactories() {
		return factories;
	}
	
	public boolean accept( String key ) {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			if (f.accept(key)) {
				return true;
			}
		}
		return false;
	}

	public ILoaderFactory<ILoadedPojo > getFactory( String key ) {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			if (f.accept(key)) {
				return f;
			}
		}
		return null;
	}

	public ILoaderFactory<ILoadedPojo> getFactory(Class<? extends ILoadedPojo> clazz) {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			if (f.accept(clazz)) {
				return f;
			}
		}
		return null;
	}

	public void validate(LoaderParams params) throws AonException {
		for (ILoaderFactory<ILoadedPojo> f : getFactories()) {
			f.validate(params);
		}
		
	}
	
}
