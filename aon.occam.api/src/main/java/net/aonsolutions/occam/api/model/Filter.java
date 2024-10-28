package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Optional;

import net.aonsolutions.occam.api.model.Properties.AccountEntryDetailProperties;
import net.aonsolutions.occam.api.model.Properties.AccountEntryProperties;
import net.aonsolutions.occam.api.model.Properties.AccountPeriodProperties;
import net.aonsolutions.occam.api.model.Properties.AccountProperties;
import net.aonsolutions.occam.api.model.Properties.CnaeProperties;
import net.aonsolutions.occam.api.model.Properties.CreditorProperties;
import net.aonsolutions.occam.api.model.Properties.CustomerProperties;
import net.aonsolutions.occam.api.model.Properties.FinanceProperties;
import net.aonsolutions.occam.api.model.Properties.GeozoneProperties;
import net.aonsolutions.occam.api.model.Properties.IaeProperties;
import net.aonsolutions.occam.api.model.Properties.InvoiceProperties;
import net.aonsolutions.occam.api.model.Properties.RegistryProperties;
import net.aonsolutions.occam.api.model.Properties.SupplierProperties;
import net.aonsolutions.occam.api.model.Properties.TariffProperties;

public interface Filter extends Serializable{
	
	public interface Property<T> {
		Filter eq(Optional<T> t);
		Filter eq(T t);
		Filter ne(T t);
		Filter le(T t);
		Filter lt(T t);
		Filter gt(T t);
		Filter ge(T t);
		Filter in(T[] t);
		Filter notIn(T[] t);
		Filter isNull();
		Filter isNotNull();
		Filter like(T t);
		Filter match(T t);
		Filter between(T min, T max);
	}
	
	
	public Filter or(Filter filter);
	public Filter and(Filter filter);
	public Filter not(Filter filter);
	
	// --------------------------------------------------------------------- [A]
	@FunctionalInterface public interface AccountEntryFilter
		{Filter filter(AccountEntryProperties properties);}
	@FunctionalInterface public interface AccountEntryDetailFilter
		{Filter filter(AccountEntryDetailProperties properties);}
	@FunctionalInterface public interface AccountPeriodFilter
		{Filter filter(AccountPeriodProperties properties);}
	@FunctionalInterface public interface AccountFilter
		{Filter filter(AccountProperties properties);}
	// --------------------------------------------------------------------- [C]
	@FunctionalInterface public interface CnaeFilter
		{Filter filter(CnaeProperties properties);}
	@FunctionalInterface public interface CreditorFilter
		{Filter filter(CreditorProperties properties);}
	@FunctionalInterface public interface CustomerFilter
		{Filter filter(CustomerProperties properties);}
	// --------------------------------------------------------------------- [G]
	@FunctionalInterface public interface FinanceFilter
		{Filter filter(FinanceProperties properties);}
	// --------------------------------------------------------------------- [G]
	@FunctionalInterface public interface GeozoneFilter
		{Filter filter(GeozoneProperties properties);}
	// --------------------------------------------------------------------- [I]
	@FunctionalInterface public interface IaeFilter
		{Filter filter(IaeProperties properties);}
	@FunctionalInterface public interface InvoiceFilter
		{Filter filter(InvoiceProperties properties);}
	// --------------------------------------------------------------------- [R]
	@FunctionalInterface public interface RegistryFilter
		{Filter filter(RegistryProperties properties);}
	// --------------------------------------------------------------------- [S]
	@FunctionalInterface public interface SupplierFilter
		{Filter filter(SupplierProperties properties);}
	// --------------------------------------------------------------------- [T]
	@FunctionalInterface public interface TariffFilter
		{Filter filter(TariffProperties properties);}
	
}
