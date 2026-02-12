package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

class InvoiceCommunicationTypeEngine implements Serializable{
	
	private static final long serialVersionUID = -344152625960332283L;

	static class EngineContext {
		LinkedList<InvoiceCommunicationType> out = new LinkedList<>();
		private InvoiceCommunicationConfiguration config;
		private InvoiceType type;
		private Date atDate;
		
		InvoiceCommunicationConfiguration getConfig() {
			return config;
		}
		void setConfig(InvoiceCommunicationConfiguration config) {
			this.config = config;
		}
		InvoiceType getType() {
			return type;
		}
		
		void setType(InvoiceType type) {
			this.type = type;
		}
		Date getAtDate() {
			return atDate;
		}
		void setAtDate(Date atDate) {
			this.atDate = atDate;
		}
		LinkedList<InvoiceCommunicationType> getOut() {
			return out;
		}
		void add(InvoiceCommunicationType ict) {
			out.add(ict);
		}
		
		boolean isSales() 				{return type.isSales();}
		boolean isNotSales() 			{return !type.isSales();}
		public boolean isAraba() 		{return getConfig().isAraba( getAtDate() );}
		public boolean isBizkaia() 		{return getConfig().isBizkaia( getAtDate() );}
		public boolean isGipuzkoa() 	{return getConfig().isGipuzkoa( getAtDate() );}
		public boolean isAEAT() 		{return getConfig().isAEAT( getAtDate() );}
		public boolean isCanarias() 	{return getConfig().isCanarias( getAtDate() );}
		public boolean isNavarra() 		{return getConfig().isNavarra( getAtDate() );}
		public boolean isTbai() 		{return getConfig().isTbai( getAtDate() );}
		public boolean isLroe() 		{return getConfig().isLroe( getAtDate() );}
		public boolean isSii() 			{return getConfig().isSii( getAtDate() );}
		public boolean isSif() 			{return getConfig().isSif( getAtDate() );}
		public boolean isNoSif() 		{return getConfig().isNoSif( getAtDate() );}
		public boolean isVerifactu() 	{return getConfig().isVerifactu(getAtDate() );}
		public boolean isNoVerifactu() 	{return getConfig().isNoVerifactu( getAtDate() );}
		
	}
	
	public LinkedList<InvoiceCommunicationType> getTypes(InvoiceCommunicationConfiguration config, InvoiceType invoiceType ,Date atDate) {
		EngineContext context = new EngineContext();
		context.setConfig(config);
		context.setType(invoiceType);
		context.setAtDate(atDate);
		return getTypes(context);
	}
	
	private LinkedList<InvoiceCommunicationType> getTypes(EngineContext t) {
		if ( NO_SIF_DISABLED.test(t) ) {
			TBAI_RULE
				.andThen(LROE_RULE)
				.andThen(VERIFACTU_RULE)
				.andThen(NO_VERIFACTU_RULE)
				.andThen(SII_RULE)
				.andThen(SIF_RULE)
				.accept( t );
		}
		return t.getOut();
	}
	
	static final Consumer<EngineContext> TBAI_RULE = t -> {
		if (t.isNotSales()) return;
		else if (!t.isTbai()) return;
		else if (t.isAraba() || t.isGipuzkoa()) t.add(InvoiceCommunicationType.TBAI);
	};

	private static final Consumer<EngineContext> LROE_RULE = t -> {
        if (!t.isLroe()) return;
        else if (t.isBizkaia()) t.add(InvoiceCommunicationType.LROE);
	};	

	private static final Consumer<EngineContext> SIF_RULE = t -> { 
    	if (t.isSif()) t.add(InvoiceCommunicationType.SIF);
	};
	
	private static final Consumer<EngineContext> VERIFACTU_RULE = t -> {
		if (t.isNotSales()) return;
		else if (!t.isVerifactu()) return;
		else if (t.isAEAT() || t.isCanarias()) t.add(InvoiceCommunicationType.VERIFACTU);
	};
	
	private static final Consumer<EngineContext> NO_VERIFACTU_RULE = t -> {
    	if (t.isNotSales()) return;
    	else if (!t.isNoVerifactu()) return;
    	else if (t.isAEAT() || t.isCanarias()) t.add(InvoiceCommunicationType.NO_VERIFACTU);
	};
	
	private static final Consumer<EngineContext> SII_RULE = t -> {
    	if (!t.isSii()) return;
    	else if (t.isAEAT() || t.isCanarias() || t.isNavarra()) t.add(InvoiceCommunicationType.SII);
    	else if (t.isNotSales() && (t.isAraba() || t.isGipuzkoa())) t.add(InvoiceCommunicationType.SII);
	};
	
	private static final Predicate<EngineContext> NO_SIF_DISABLED = t -> {
    	if (t.isNotSales()) return true;
    	else if (!t.isNoSif()) return true;
    	return false;
	};
}
