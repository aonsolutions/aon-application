package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.jooq.Field;
import org.jooq.impl.DSL;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.occam.api.model.type.Mod1902014Key;
import com.esferalia.aon.occam.api.model.type.Mod1902015Key;
import com.esferalia.aon.occam.api.model.type.Mod1902016Key;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryAddressDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

public abstract class Mod190Declaration {
	private enum Declarations {
		MOD190_GENERIC_2022 {
			
			@Override
			boolean accept(Mod190 mod) {
				return mod.getYear() >= 2022;
			}

			@Override
			Mod190Declaration get() {
				return new Mod190ALL2022Declaration();
			}
		}
		,
		MOD190_GENERIC_2017 {
			
			@Override
			boolean accept(Mod190 mod) {
				return mod.getYear() >= 2017 && mod.getYear() <= 2021;
			}

			@Override
			Mod190Declaration get() {
				return new Mod190ALL2017Declaration();
			}
		}
		,
		MOD190_ALL_2016 {

			@Override
			boolean accept(Mod190 mod) {
				return mod.getYear() < 2017;
			}

			@Override
			Mod190Declaration get() {
				return new Mod190ALL2016Declaration();
			}
			
		};
		
		abstract boolean accept(Mod190 mod);
		abstract Mod190Declaration get();
		
	}

	
	static Mod190Declaration getInstance( Mod190 mod) {
		if (mod.getAdministration() == null) {
			throw new AonCoreException("No se ha indicado administraci\u00F3n para la declaraci\u00F3n");
		}
		if (mod.getYear() < 2010 && mod.getYear() > 2025) {
			throw new AonCoreException("No se ha indicado una ejercicio vÃ¡lido para la declaraci\u00F3n");
		}
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaraci\u00F3n");	
		}
		return Arrays.stream(Declarations.values())
			.filter(dec -> dec.accept(mod))
			.map(Declarations::get)
			.findFirst()
			.orElseThrow( () -> new AonCoreException(MessageFormat.format(
				"No existe una declaración para el modelo solicitado ({0} - {1})", mod.getAdministration().getDescription(),mod.getYear())));
	}
	
	Mod190 insertDetailsFromInvoice(AONContext ctx, final Mod190 mod190) {
		java.sql.Date firstDay = AonDateUtils.toSql(AonDateUtils.getYearFirstDay(mod190.getYear()));
		java.sql.Date lastDay = AonDateUtils.toSql(AonDateUtils.getYearLastDay(mod190.getYear()));

		Field<Integer> minRegistry = DSL.min(INVOICE.REGISTRY).as(INVOICE.REGISTRY.getName());
		Field<BigDecimal> sumBase = DSL.sum(INVOICE_TAX.BASE).as(INVOICE_TAX.BASE.getName());
		Field<Double> invoiceTaxSum = DSL.round((INVOICE_TAX.BASE.mul(INVOICE_TAX.PERCENTAGE)).div(100), 2);
		Field<BigDecimal> quotaOp = DSL.sum(DSL.decode()
				.when(INVOICE_TAX.QUOTA.notEqual(0.0), INVOICE_TAX.QUOTA)
				.when(INVOICE_TAX.QUOTA.equal(0.0), invoiceTaxSum));
		Map<String,Mod190Detail> map = new LinkedHashMap<>();
		ctx.getDslContext()
				.select(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE, INVOICE_TAX.PERCENTAGE, minRegistry, sumBase,quotaOp)
				.from(INVOICE)
				.join(INVOICE_DETAIL).on(INVOICE_DETAIL.INVOICE.equal(INVOICE.ID))
				.join(INVOICE_TAX).on(INVOICE_TAX.INVOICE_DETAIL.equal(INVOICE_DETAIL.ID))
				.where(INVOICE.DOMAIN.equal(mod190.getDomain()))
				.and(INVOICE.TYPE.notEqual((byte) 1)) 		// No Ventas
				.and(INVOICE_TAX.TAX_TYPE.equal((byte) 2))	// IRPF
				.and(INVOICE_TAX.WITHHOLDING_TYPE.in((byte) 0, (byte) 3,(byte) 4)) 
				.and(INVOICE.ISSUE_DATE.between(firstDay,lastDay))
				.groupBy(INVOICE.RDOCUMENT, INVOICE.RNAME,INVOICE_TAX.WITHHOLDING_TYPE,INVOICE_TAX.PERCENTAGE)
				.fetch()
				.stream()
				.forEach(
						rec -> {
							
							String document = rec.getValue(INVOICE.RDOCUMENT);
							String key = null;
							String subKey = null;
							
							double percent = rec.getValue(INVOICE_TAX.PERCENTAGE);
							WithholdingType withholding = WithholdingType.safeValueOf(rec.getValue(INVOICE_TAX.WITHHOLDING_TYPE));
							if (withholding == WithholdingType.PROFESSIONAL) {
								if (mod190.getYear() == 2014) {
									key = Mod1902014Key.getDefaultKeyForProfessionalRetentions().getValue();
									subKey = Mod1902014Key.getDefaultSubkeyForProfessionalRetentions();
								}else if (mod190.getYear() == 2015) {
									key = Mod1902015Key.getDefaultKeyForProfessionalRetentions().getValue();
									subKey = Mod1902015Key.getDefaultSubkeyForProfessionalRetentions();
								} else{
									key = Mod1902016Key.getDefaultKeyForProfessionalRetentions().getValue();
									if (AonNumberUtils.equals(percent, 7.0) ) {
										subKey = Mod1902016Key.getDefaultSubkeyForNewProfessionalRetentions();
									} else {
										subKey = Mod1902016Key.getDefaultSubkeyForProfessionalRetentions();
									}
								}
							} else if (withholding == WithholdingType.FARMER) { // AGRICULTOR - FARMER
								if (mod190.getYear() == 2014) {
									key = Mod1902014Key.getDefaultKeyForFarmerRetentions().getValue();
									subKey = Mod1902014Key.getDefaultSubkeyForFarmerRetentions();
								} else if (mod190.getYear() == 2015) {
									key = Mod1902015Key.getDefaultKeyForFarmerRetentions().getValue();
									subKey = Mod1902015Key.getDefaultSubkeyForFarmerRetentions();
								} else {
									key = Mod1902016Key.getDefaultKeyForFarmerRetentions().getValue();
									subKey = Mod1902016Key.getDefaultSubkeyForFarmerRetentions();
								}
							} else if (withholding == WithholdingType.TRANSPORT_OPERATOR) { // TRANSPORTISTAS Y ASIMILADOS - TRANSPORT_OPERATOR
								if (mod190.getYear() == 2014) {
									key = Mod1902014Key.getDefaultKeyForTransportRetentions().getValue();
									subKey = Mod1902014Key.getDefaultSubkeyForTransportRetentions();
								} else if (mod190.getYear() == 2015) {
									key = Mod1902015Key.getDefaultKeyForTransportRetentions().getValue();
									subKey = Mod1902015Key.getDefaultSubkeyForTransportRetentions();
								} else {
									key = Mod1902016Key.getDefaultKeyForTransportRetentions().getValue();
									subKey = Mod1902016Key.getDefaultSubkeyForTransportRetentions();
								}
							}
							String mapKey = document + "|" + key + "|" + subKey;
							Mod190Detail detail = null; 
							if (!map.containsKey(mapKey)) {
								detail = new Mod190Detail();
								detail.setDomain(mod190.getDomain());
								detail.setMod190(mod190.getId());
								detail.setDocument(document);
								detail.setName(rec.getValue(INVOICE.RNAME));
								detail.setProvince( RegistryAddressDAO.getMainAddressProvince(ctx, rec.getValue(minRegistry)) );
								detail.setKey(key);
								detail.setSubKey(subKey);
								map.put(mapKey, detail);
							}
							detail = map.get(mapKey);
							detail.setPerception(AonMathUtils.round(detail.getPerception() + rec.getValue(sumBase).doubleValue()));
							detail.setRetention(AonMathUtils.round(detail.getRetention() + rec.getValue(quotaOp).doubleValue()));
						});
		mod190.getDetails().addAll(map.values());
		return mod190;
	}
	
	abstract Mod190 insertDetailsFromSalary(AONContext ctx, final Mod190 mod190);;
	abstract LinkedList<Mod190Detail> validateSalaries(AONContext ctx, final Mod190 mod190);;
	
}
