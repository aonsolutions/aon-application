package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod347;

import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

import org.json.JSONArray;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.json.VatContextJSON;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalModelKeyInfoVisitor;
import com.esferalia.aon.occam.api.model.fiscal.Mod347;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Declared;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.FiscalModelKeyInfo;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.Mod347Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod347InfoDAO {
	
	private Mod347InfoDAO() {
	}

	public static String getInfo(AONContext ctx, Mod347 mod347, Mod347Declared declared, FiscalModelKeyInfo infoKey) {
		
		return infoKey.visit( new IFiscalModelKeyInfoVisitor<String>() {
			@Override
			public String visitModelInvoiceVatBreakdown() {
				return formatInvoices347( 
						getInvoicesInfoBreakdown(ctx, mod347, declared)
							.filter(vat -> !(mod347.isCanarias() && vat.hasRetention() && vat.getWithholdingType() == WithholdingType.RENTING) )  // Si el modelo es de Canarias no sacar las facturas de arrendamientos de locales
					);
			}
			@Override public String visitModelInvoiceIrpfBreakdown() {
				return formatInvoices347(
						getInvoicesInfoBreakdown(ctx, mod347, declared)
					    	.filter(vat -> mod347.isCanarias() && vat.hasRetention() && vat.getWithholdingType() == WithholdingType.RENTING)  // Si el modelo es de Canarias sacar las facturas de arrendamientos de locales
					);
			}
			@Override public String visitInvoice() {
				return visitNone(); 
			}
			@Override public String visitInAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitOutAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitDiffInAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitDiffOutAccrualInvoice() {
				return visitNone(); 
			}
			@Override public String visitSalary() {
				return visitNone(); 
			}
			@Override public String visitDiffSalary() {
				return visitNone(); 
			}
			@Override public String visitActAccount() {
				return visitNone(); 
			}
			@Override public String visitTitle() {
				return visitNone(); 
			}
			@Override public String visitIrpfActivity() {
				return visitNone(); 
			}
			@Override public String visitCorporate() {
				return visitNone(); 
			}
			@Override public String visitModelSalaryIrpfBreakdown() {
				return visitNone(); 
			}
			@Override
			public String visitCompute() {
				return visitNone();
			}
			@Override
			public String visitComputeKey() {
				return visitNone();
			}
			@Override
			public String visitProrratedModelInvoiceVatBreakdown() {
				return visitNone();
			}
			@Override
			public String visitModelOutVatAccrualInvoice() {
				return visitNone();
			}
			@Override
			public String visitModelInVatAccrualInvoice() {
				return visitNone();
			}
			@Override
			public String visitDiffInvoice() {
				return visitNone();
			}
			@Override 
			public String visitNone()    {
				return AonStringUtils.EMPTY; 
			}
		});
		
	}
	
	private static String formatInvoices347(Stream<VatContext> vatContextStream) {
		return Objects.requireNonNullElse( vatContextStream
					.map(VatContextJSON::toJSON)
					.collect(JSONArray::new, JSONArray::put, JSONArray::put)
					,new JSONArray()).toString();
	}	
	
	private static Stream<VatContext> getInvoicesInfoBreakdown(final AONContext ctx, final Mod347 mod347, final Mod347Declared declared) {
		
		// Tipo de Facturas según la clave de la linea del modelo que se le pasa (se hace la operacion inversa que cuando se crea el modelo)
		final InvoiceType invoiceType1;
		final InvoiceType invoiceType2;
		
		// Tipo de transaccion según si está marcado o no ISP (solo compras)
		final InvoiceTransactionType invoiceTransaction1;
		final InvoiceTransactionType invoiceTransaction2;
		
		if (declared.getType() == Mod347Key.A) {       // Adquisiciones de bienes y servicios superiores a 3.005,06 euros (Compras y Gastos)
			invoiceType1 = InvoiceType.PURCHASE;
			invoiceType2 = InvoiceType.EXPENSES;			
			invoiceTransaction1 = declared.isIsp() ? InvoiceTransactionType.OTHER_ISP : InvoiceTransactionType.NATIONAL;
			invoiceTransaction2 = null;
	    }
	    else if (declared.getType() == Mod347Key.B) {  // Entregas de bienes y prestaciones de servicios superiores a 3.005,06 euros (Ventas)
	    	invoiceType1 = InvoiceType.SALES;
	    	invoiceType2 = null;
	    	invoiceTransaction1 = InvoiceTransactionType.NATIONAL;
	    	invoiceTransaction2 = InvoiceTransactionType.OTHER_ISP;
	    }
	    else {
	    	invoiceType1 = null;
	    	invoiceType2 = null;
	    	invoiceTransaction1 = null;
			invoiceTransaction2 = null;
	    }
		
		Date fromDate = AonDateUtils.getYearFirstDay(mod347.getYear());
		Date toDate = AonDateUtils.getYearLastDay(mod347.getYear());
		String registryDocument = AonStringUtils.isBlank(declared.getOperatorNif())
			? declared.getDocument()
			: AonStringUtils.substring(declared.getOperatorNif(),2); 
		return Mod347DAO.getInvoiceBreakdown(ctx, fromDate, toDate, mod347)
			.filter( vat -> AonStringUtils.equals(AonStringUtils.trimToEmpty(vat.getRegistryDocument()),AonStringUtils.trimToEmpty(registryDocument)))
			.filter( vat -> (vat.getTransaction() == invoiceTransaction1 || vat.getTransaction() == invoiceTransaction2 || vat.getTransaction() == InvoiceTransactionType.INTRACOMMUNITY || (vat.getTransaction() == InvoiceTransactionType.EXTRACOMMUNITY && vat.isService()) || (vat.getTransaction() == InvoiceTransactionType.CAN_CEU_MEL && vat.isService()) ) &&  // Nacional o ISP o intracomunitarias o servicios extracomunitarios
		                    (vat.getInvoiceType() == invoiceType1 || vat.getInvoiceType() == invoiceType2) &&  				 // Tipo (Ventas o Compras/Gastos)		                    
		                    (vat.isVatAccrualRegime() == declared.isVatAccrual())                                            // Criterio de caja		                    
	                    );  
	}
	
}
