package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod111;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod111;
import com.esferalia.aon.occam.api.model.type.Mod111Key;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.DomainDAO;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFDAO;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.error.AonCoreException;

public abstract class Mod111Declaration {
	
	@FunctionalInterface
	static interface IValueAccepter {
		boolean accept(Mod111 mod,IrpfBreakdown rc);
	}
	@FunctionalInterface
	static interface IValueIntializer {
		void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs
				,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown br);
	}
	@FunctionalInterface
	static interface IValueUniqueIntializer {
		void initialize(AONContext ctx,Mod111 mod);
	}
	
	static Mod111Declaration getInstance( Mod111 mod) {
		if (mod.getAdministration() == null) {
			throw new AonCoreException("No se ha indicado administración para la declaración");
		}
		if (mod.getYear() < 2010 && mod.getYear() > 2025) {
			throw new AonCoreException("No se ha indicado una ejercicio válido para la declaración");
		}
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaración");	
		}
		if (Mod111AEAT2021Declaration.accept(mod)) 		return new Mod111AEAT2021Declaration();
		if (Mod111Bizkaia2021Declaration.accept(mod)) 	return new Mod111Bizkaia2021Declaration();
		if (Mod110Bizkaia2021Declaration.accept(mod)) 	return new Mod110Bizkaia2021Declaration();
		if (Mod111Araba2021Declaration.accept(mod)) 	return new Mod111Araba2021Declaration();
		if (Mod111Gipuzkoa2021Declaration.accept(mod)) 	return new Mod111Gipuzkoa2021Declaration();
		if (Mod111Navarra2021Declaration.accept(mod)) 	return new Mod111Navarra2021Declaration();
		
		throw new AonCoreException(MessageFormat.format(
			"No existe una declaración para el modelo solicitado ({0} - {1} - {2})",
			mod.getAdministration().getDescription()
			,mod.getYear()
			,mod.getPeriod().getDescription()));
	}

	IMod111KeyDAO getKey(Mod111Key key) {
		for (IMod111KeyDAO keyDAO : getKeys()) {
			if (keyDAO.getKey() == key) {
				return keyDAO;
			}
		}
		return null;
	}

	void initialize(AONContext ctx,Mod111 mod,Map<Mod111Key,Set<String>> docs,Map<Mod111Key,Set<String>> pdocs,IrpfBreakdown  br) {
		for (IMod111KeyDAO key : getKeys()) {
			if (key.acceptValue(mod,br)) {
				key.initialize(ctx, mod, docs, pdocs, br); 
			}
		}
	}

	void specificInitialization(Mod111 mod111) {
	}

	static void addPerceptor(Mod111Key key,Mod111 mod
			,Map<Mod111Key,Set<String>> docs
			,Map<Mod111Key,Set<String>> pdocs
			,IrpfBreakdown br) {
		// Se suman todos los perceptores (acumulado).
		docs.computeIfAbsent(key, k -> new HashSet<String>());
		if (!docs.get(key).contains(br.getRegistryDocument())) {
			docs.get(key).add(br.getRegistryDocument());
			mod.ensureDetail(key).addAccumulatedAmount(1);
		}
		// Se suman los perceptores del periodo que se esta haciendo.
		if (FiscalUtils.isInPeriodRange(mod, br.getTaxDate())) {
			pdocs.computeIfAbsent(key, k -> new HashSet<String>());
			if (!pdocs.get(key).contains(br.getRegistryDocument())) {
				pdocs.get(key).add(br.getRegistryDocument());
				mod.ensureDetail(key).addAmount(1);
			}
		}
	}
	static void addBase(Mod111Key key,Mod111 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAmount(br.getBase());
	}
	static void addQuota(Mod111Key key,Mod111 mod,IrpfBreakdown br) {
		mod.ensureDetail(key).addAmount(br.getQuota());
	}
	static void addDeponentDocument(AONContext ctx, Mod111 mod) {
		Domain domain = DomainDAO.getDomain(ctx, ctx.getDomainId());
		if (!domain.isStandalone()) {
			Company parentCompany = CompanyDAO.getCompany(ctx, domain.getParentId());
			if (parentCompany != null) {
				mod.ensureDetail(Mod111Key.GP_X00).setDescription(parentCompany.getDocument());	
			}
		}
	}

	abstract IMod111KeyDAO valueOf(String string);
	abstract IMod111KeyDAO[] getKeys();

	void ensureDetails(Mod111 mod111) {
		Arrays.stream( getKeys() )
			.forEach(key -> mod111.ensureDetail(key.getKey()).setExpression(key.getExpression()));
	}

	void uniqueInitialize(AONContext ctx, Mod111 mod111) {
		Arrays.stream( getKeys() )
			.forEach(key -> key.uniqueInitialize(ctx, mod111));
	}

	void createFromSalary(final AONContext ctx, final Mod111 mod111) {
		final Map<Mod111Key,Set<String>> docs = new EnumMap<>(Mod111Key.class); 
		final Map<Mod111Key,Set<String>> pdocs = new EnumMap<>(Mod111Key.class);
		IRPFDAO.getSalaryIrpfBreakdown(ctx, mod111)
			.forEach(br -> Arrays.stream( getKeys() )
				.filter(key -> key.acceptValue(mod111,br))
				.forEach(key -> key.initialize(ctx, mod111, docs, pdocs, br)));
	}

	void createFromInvoices(final AONContext ctx, final Mod111 mod111) {
		final Map<Mod111Key,Set<String>> docs = new EnumMap<>(Mod111Key.class); 
		final Map<Mod111Key,Set<String>> pdocs = new EnumMap<>(Mod111Key.class);
		IRPFDAO.getInputInvoicesIrpfBreakdown(ctx, mod111)
			.forEach(br -> Arrays.stream( getKeys() )
				.filter(key -> key.acceptValue(mod111,br))
				.forEach(key -> key.initialize(ctx, mod111, docs, pdocs, br)));
	}
}
