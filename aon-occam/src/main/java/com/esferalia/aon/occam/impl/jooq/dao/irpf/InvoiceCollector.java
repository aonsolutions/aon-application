package com.esferalia.aon.occam.impl.jooq.dao.irpf;

import java.text.MessageFormat;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRPFParamsGroupedByVisitor;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IRPFParamsOrderByVisitor;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

class InvoiceCollector extends IRPFAbstractCollector {
	
	private IRPFParams params;
	private InvoiceCollectorVisitor visitor;
	 
	InvoiceCollector( IRPFParams params) {
		this.params = params;
		this.visitor = new InvoiceCollectorVisitor(params);
	}
	
	@Override
	public BiConsumer<TreeMap<String, IrpfBreakdown>, IrpfBreakdown> accumulator() {
		return ((map, js) -> merge( map.computeIfAbsent( getInvoiceGroupedKey( js ), k -> initialize(js)), js ) );
	}
	private String getInvoiceGroupedKey(IrpfBreakdown br) {
		String key = ""; 
		if (params.getGroupedBy() == null) {
			key = WithholdingType.safeToString(br.getWithholdingType()) + "-" + AonNumberUtils.toString( br.getInvoice());
		} else {
			key = params.getGroupedBy().visit( visitor, br );	
		}
		return key;
	}
	
	private class InvoiceCollectorVisitor implements IRPFParamsGroupedByVisitor<String,IrpfBreakdown> {
		
		private IRPFParams params;
		private InvoiceCollectorVisitor(IRPFParams params) {
			this.params = params;
		}

		@Override
		public String visitInvoice(IrpfBreakdown br) {
			if (params.getOrderBy() != null) {
				return params.getOrderBy().visit(invoiceIRPFParamsOrderByVisitor, br);
			}
			return AonNumberUtils.toString( br.getInvoice());
		}

		@Override
		public String visitRegistry(IrpfBreakdown br) {
			if (params.getOrderBy() != null) {
				return params.getOrderBy().visit(registryIRPFParamsOrderByVisitor, br);
			}
			return AonStringUtils.defaultIfBlank(br.getRegistryDocument(),"<null>");
		}

		@Override
		public String visitInvoiceDetail(IrpfBreakdown br) {
			return null;
		}
	};
	
	
	private IRPFParamsOrderByVisitor<String, IrpfBreakdown> invoiceIRPFParamsOrderByVisitor = 
		new IRPFParamsOrderByVisitor<String, IrpfBreakdown>() {

		@Override
		public String visitInvoiceIssueDate(IrpfBreakdown br) {
			return MessageFormat.format("{0}-{1}"
				, br.getIssueDate()
				, br.getInvoice()
				);  						
		}

		@Override
		public String visitInvoiceNumber(IrpfBreakdown br) {
			return MessageFormat.format("{0}-{1}-{2}-{3}"
				, br.getInvoiceType() 
				, br.getSeries()
				, br.getNumber()
				, br.getInvoice()
				);
		}

		@Override
		public String visitInvoiceRegistryName(IrpfBreakdown br) {
			return MessageFormat.format("{0}-{1}"
					, br.getName()
					, br.getInvoice()
					);  						
		}

		@Override
		public String visitInvoiceRegistryDocument(IrpfBreakdown br) {
			return MessageFormat.format("{0}-{1}"
				, br.getRegistryDocument()
				, br.getInvoice());  						
		}
	};

	private IRPFParamsOrderByVisitor<String, IrpfBreakdown> registryIRPFParamsOrderByVisitor = 
		new IRPFParamsOrderByVisitor<String, IrpfBreakdown>() {

			@Override
			public String visitInvoiceIssueDate(IrpfBreakdown br) {
				return AonStringUtils.defaultIfBlank(br.getRegistryDocument(),"<null>");
			}

			@Override
			public String visitInvoiceNumber(IrpfBreakdown br) {
				return AonStringUtils.defaultIfBlank(br.getRegistryDocument(),"<null>");
			}

			@Override
			public String visitInvoiceRegistryName(IrpfBreakdown br) {
				return br.getName();
			}

			@Override
			public String visitInvoiceRegistryDocument(IrpfBreakdown br) {
				return AonStringUtils.defaultIfBlank(br.getRegistryDocument(),"<null>");
			}
		};
}
