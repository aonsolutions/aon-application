package net.aonsolutions.aon.tedi;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiConsumer;

import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.registry.RAddress;
import com.esferalia.aon.occam.api.model.registry.Registry;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.impl.jooq.dao.OCRDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryDAO;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediAddress;
import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediInvoiceType;
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediRegistry;
import es.translogia.tedi.ewok.TediTaxType;
import solutions.aon.in.invoice.tedi.TediInsightInvoiceBuilder;
import solutions.aon.in.invoice.templates.Document;

public class TediInvoiceBuilder extends TediInsightInvoiceBuilder {
	
	private TediContext ctx;
	
	public TediInvoiceBuilder( TediContext ctx) {
		super();
		this.ctx = ctx;
	}
	
	@Override
	public void addInsightNifs(Collection<Document> nifs) {
		super.addInsightNifs(nifs);
		if (get().getInsight() != null) {
			TICKET_TYPE
			.andThen(RECIBIDA_TYPE)
			.andThen(RECEIVER)
			.andThen(SENDER)
			.accept(ctx, get());
		}
	}
	
	@Override
	public void finalizeParse() {
		super.finalizeParse();
		if (get().getInsight() != null) {
			DATE
			.andThen(TOTAL)
			.andThen(SUPER_SIMPLE_21_IVA)
			.andThen(SIMPLE_IVA)
			.andThen(COMPLEX_IVA)
			.andThen(SIMPLE_IRPF)
			.andThen(CHECK_TOTAL_INVOICE)
			.accept(ctx, get());
		}
	}

	private static BiConsumer<TediContext,TediInvoice> TICKET_TYPE = (ctx,inv) -> {
		if (inv.getInsight().getNifs() != null 
			&& inv.getInsight().getNifs().length == 1 
			&& !AonStringUtils.equals(ctx.getCompanyDocument(), inv.getInsight().getNifs()[0].getStr() )) {
			inv.setType(TediInvoiceType.TICKET);	
		}
	};
	
	private static BiConsumer<TediContext,TediInvoice> RECIBIDA_TYPE = (ctx,inv) -> {
		if (inv.getType() == null) {
			if (inv.getInsight().getNifs() != null 
				&& inv.getInsight().getNifs().length > 1) {
				inv.setType(TediInvoiceType.RECIBIDA);	
			}
		}
	};

	private static BiConsumer<TediContext,TediInvoice> DATE = (ctx,inv) -> {
		inv.setDate( inv.getInsight().getIssueDate() );
	};
	private static BiConsumer<TediContext,TediInvoice> TOTAL = (ctx,inv) -> {
		inv.setTotal( inv.getInsight().getTotal() );
	};
	private static BiConsumer<TediContext,TediInvoice> RECEIVER = (ctx,inv) -> {
		Company company = ctx.getAonConfiguration().getCompany();
		inv.ensureReceiver()
			.setDocument(company.getDocument())
			.setDocumentCountry(getCountryCode(company.getDocumentCountry()))
			.setName(company.getName())
			.setAddress(getTediAddress( company.getMainAddress()));
		
	};
	private static BiConsumer<TediContext,TediInvoice> SENDER = (ctx,inv) -> {
		if ( inv.getReceiver() != null && inv.getReceiver().getDocument() != null) {
			String companyDoc = inv.getReceiver().getDocument(); 
			if (inv.getInsight().getNifs() != null && inv.getInsight().getNifs().length > 0) {
				for (TediNif nif : inv.getInsight().getNifs()) {
					if (!companyDoc.equals(nif.getStr())) {
						TediRegistry reg = getRegistryData(ctx, nif.getStr() );
						if (reg == null) {
							reg = new TediRegistry()
								.setDocument(nif.getStr());
						}
						inv.setSender( reg );
						break;
					}
				}
			}
		}
	};
	
	private static String getCountryCode( Country country ) {
		return country==null?null:country .getIso2();  
	}

	private static TediAddress getTediAddress(RAddress raddress) {
		return (raddress == null) 
			? null
			: new TediAddress()
				.setAddress(raddress.getFullAddress())
				.setCity(raddress.getCity())
				.setCountry(getCountryCode(raddress.getCountry()))
				.setProvince(raddress.getGeozoneName())
				.setPostalCode(raddress.getZip());
	}
	
	private static TediRegistry getRegistryData(TediContext ctx, String document) {
		TediRegistry registry = null;
		Registry reg = OCRDAO.getRegistry(ctx.getUser(),document);
		if (reg == null) {
			reg = RegistryDAO.getStream(ctx.getAONContext(), p -> p.getDocumentProperty().eq(document))
					.findFirst()
					.orElse(null);
		}
		if (reg != null) {
			registry = new TediRegistry()
				.setDocument(reg.getDocument())
				.setDocumentCountry(getCountryCode(reg.getDocumentCountry()))
				.setName(reg.getName())
				.setAddress(getTediAddress( reg.getMainAddress()));
		}
		return registry;
	}

	@Override
	public String[] getReferencePatterns() {
		if (get().getSender() != null) {
			return OCRDAO.getReferencePatterns(null, get().getSender().getDocument());
		}
		return null;
	}
	
	private static int indexOf(Double amounts [], double amount, int start ) {
		double delta =(amount > 0.1 ? 0.019 : 0.0019);
		return indexOf(amounts, amount, start, delta);
	}

	private static int indexOf(Double amounts [], double amount, int start , double delta ) {
		int indexOf = -1;
		double diffOf = 0.5;
		for (int i = start; i < amounts.length; i++) {
			double diff = Math.abs(amounts[i] - amount);
			if ( diff < delta && diff < diffOf ) {
				indexOf = i;
				diffOf = diff;
			}
		}
		return indexOf;
	}
	
	private static final double IVA_PERCENTS [] = {21.0, 10.0, 4.0};
	private static final double IRPF_PERCENTS [] = {19.0};
	
	public static BiConsumer<TediContext,TediInvoice> SUPER_SIMPLE_21_IVA = (ctx,inv) -> {
		if (inv.getTotal() != null 
			&& AonMathUtils.isNotZero(inv.getTotal()) 
			&& inv.getInsight() != null 
			&& inv.getInsight().getAmounts() != null ) {
			Double[] amounts = inv.getInsight().getAmounts(); 
			double total = inv.getTotal(); // amounts[i];
			double percentage = IVA_PERCENTS[0];
			double base = total / (1 + percentage / 100.0);
			for (int i = 0; i < amounts.length; i++) {
				if (amounts[i] != 0.0) {
					int indexOfBase = indexOf(amounts, base, i + 1);
					if ( indexOfBase >= 0 ) {
						double quota = total - base;
						int indexOfQuota = indexOf(amounts, quota, i+1);
						if ( indexOfQuota >= 0 ) {
							// inv.setTotal(total);
							inv.ensureTax(
								new TediInvoiceTax()
									.setTaxType( TediTaxType.IVA )
									.setBase(amounts[indexOfBase])
									.setPercentage(percentage )
									.setQuota(amounts[indexOfQuota])
									);
							break;
						}
					}
				}
			}
		}
	};
	
	public static BiConsumer<TediContext,TediInvoice> SIMPLE_IVA = (ctx,inv) -> {
		if ( !isSettled(inv) ) {
			if (inv.getInsight() != null && inv.getInsight().getAmounts() != null ) {
				Double[] amounts = inv.getInsight().getAmounts(); 
				Arrays.sort(amounts, Collections.reverseOrder());
				iva: {
					for (int i = 0; i < amounts.length; i++) {
						double total = inv.getTotal(); // amounts[i];
						if (amounts[i] != 0.0) {
							for (double percentage : IVA_PERCENTS ) {
								double base = total / (1 + percentage / 100.0);
								if (base != 0.0) {
									int indexOfBase = indexOf(amounts, base, i + 1);
									if ( indexOfBase >= 0 ) {
										double quota = total - base;
										int indexOfQuota = indexOf(amounts, quota, i+1);
										if ( indexOfQuota >= 0 ) {
											// inv.setTotal(total);
											inv.ensureTax(
												new TediInvoiceTax()
													.setTaxType( TediTaxType.IVA )
													.setBase(amounts[indexOfBase])
													.setPercentage(percentage )
													.setQuota(amounts[indexOfQuota])
													);
											break iva;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	};

	public static BiConsumer<TediContext,TediInvoice> SIMPLE_IRPF = (ctx,inv) -> {
		if ( !isSettled(inv) ) {
			if (inv.getInsight() != null && inv.getInsight().getAmounts() != null ) {
				Double[] amounts = inv.getInsight().getAmounts(); 
				for (int i = 0; i < amounts.length; i++) {
					if (amounts[i] != 0.0) {
						double base = amounts[i];
						for (double percentage : IRPF_PERCENTS ) {
							double quota = base * percentage / 100.0;
							int indexOfQuota = indexOf(amounts, quota, i+1);
							if ( indexOfQuota >= 0 ) {
								inv.ensureTax(
										new TediInvoiceTax()
										.setTaxType( TediTaxType.IRPF )
										.setBase(amounts[i])
										.setPercentage( percentage )
										.setQuota(amounts[indexOfQuota])
										);
							}
						}
					}
				}
			}
		}
	};
	
	public static BiConsumer<TediContext,TediInvoice> COMPLEX_IVA = (ctx,inv) -> {
		if ( !isSettled(inv) ) {
			if (inv.getInsight() != null && inv.getInsight().getAmounts() != null ) {
				Double[] amounts = inv.getInsight().getAmounts();
				for (int i = 0; i < amounts.length; i++) {
					if (amounts[i] != 0.0) {
						double base = amounts[i];
						for (double percentage : IVA_PERCENTS ) {
							double quota = base * percentage / 100.0;
							int indexOfQuota = indexOf(amounts, quota, i+1);
							if ( indexOfQuota >= 0 ) {
								inv.ensureTax(
										new TediInvoiceTax()
										.setTaxType( TediTaxType.IVA )
										.setBase(amounts[i])
										.setPercentage(percentage )
										.setQuota(amounts[indexOfQuota])
										);
							}
						}
					}
				}
			}
			}
	};
	
	public static BiConsumer<TediContext,TediInvoice> CHECK_TOTAL_INVOICE = (ctx,inv) -> {
		inv.getInsight().setSettledManually(false);
		if ( !isSettled(inv) ) {
			if (inv.getTaxes() != null && inv.getTaxes().size() > 0) {
				inv.getTaxes().clear();
			}
			inv.getInsight().setSettledManually(true);
			inv.ensureTax(
					new TediInvoiceTax()
					.setTaxType( TediTaxType.IVA )
					.setBase( inv.getTotal() == null ? 0.0 : inv.getTotal() )
					.setPercentage( 0.0 )
					.setQuota(0.0)
					);
		}
	};
	
	
	private static boolean isSettled(TediInvoice inv) {
		if ( inv == null) return false;
		if ( inv.getTotal() == null) return false;
		if ( inv.getTaxes() == null) return false;
		double total = 0.0;
		for (TediInvoiceTax tax : inv.getTaxes()) {
			if ( tax.getTaxType() == TediTaxType.IVA) {
				total = total + tax.getBase() + tax.getQuota();
			}
			if ( tax.getTaxType() == TediTaxType.IRPF) {
				total = total - tax.getQuota();
			}
		}
		return AonMathUtils.equals(inv.getTotal(), total);
	}	
}	
