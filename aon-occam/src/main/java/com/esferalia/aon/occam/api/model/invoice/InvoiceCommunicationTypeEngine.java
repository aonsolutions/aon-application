package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.function.Consumer;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

class InvoiceCommunicationTypeEngine implements Serializable{
	
	private static final long serialVersionUID = -344152625960332283L;

	private static class EngineContext {
		LinkedList<CommunicationData> out = new LinkedList<>();
		private InvoiceCommunicationConfiguration config;
		private InvoiceType type;
		private Date atDate;
		
		InvoiceCommunicationConfiguration getConfig() {
			return config;
		}
		void setConfig(InvoiceCommunicationConfiguration config) {
			this.config = config;
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
		LinkedList<CommunicationData> getOut() {
			return out;
		}
		void add(CommunicationData ict) {
			out.add(ict);
		}
		
		boolean isSales() 				{return type.isSales();}
		boolean isNotSales() 			{return !type.isSales();}
		public boolean isTbai() 		{return getConfig().isTbai( getAtDate() );}
		public boolean isNoSif() 		{return getConfig().isNoSif( getAtDate() );}
		
	}
	
	public LinkedList<CommunicationData> getTypes(InvoiceCommunicationConfiguration config, InvoiceType invoiceType ,Date atDate) {
		EngineContext context = new EngineContext();
		context.setConfig(config);
		context.setType(invoiceType);
		context.setAtDate(atDate);
		return getTypes(context);
	}
	
	private LinkedList<CommunicationData> getTypes(EngineContext t) {
		TBAI_RULE
			.andThen(LROE_RULE)
			.andThen(VERIFACTU_RULE)
			.andThen(NO_VERIFACTU_RULE)
			.andThen(SII_RULE)
			.andThen(SIF_RULE)
			.andThen(NO_SIF_RULE)
			.accept( t );
		return t.getOut();
	}

	static final Consumer<EngineContext> TBAI_RULE = t -> {
		if (t.isNotSales()) return;
		t.config.getTbaiData().ifPresent( t::add );
	};

	private static final Consumer<EngineContext> LROE_RULE = t -> {
		if (t.isSales() && t.isNoSif()) return;
		t.config.getLroeData().ifPresent( t::add );
	};	

	private static final Consumer<EngineContext> SIF_RULE = t -> {
		if (t.isNotSales()) return;
		t.config.getSifData().ifPresent( t::add );
	};
	
	private static final Consumer<EngineContext> VERIFACTU_RULE = t -> {
		if (t.isNotSales()) return;
		t.config.getVerifactuData().ifPresent( t::add );
	};
	
	private static final Consumer<EngineContext> NO_VERIFACTU_RULE = t -> {
    	if (t.isNotSales()) return;
		t.config.getNoVerifactuData().ifPresent( t::add );
	};
	
	private static final Consumer<EngineContext> SII_RULE = t -> {
		if (t.isSales() && (t.isNoSif() || t.isTbai())) return;
		t.config.getSiiData().ifPresent( t::add );
	};
	
	private static final Consumer<EngineContext> NO_SIF_RULE = t -> {
		t.config.getNoSifData().ifPresent( t::add );
	};

}
