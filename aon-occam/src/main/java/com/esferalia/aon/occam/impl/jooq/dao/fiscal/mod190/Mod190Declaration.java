package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod190;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.fiscal.Mod190Detail;
import com.esferalia.aon.watson.error.AonCoreException;

public abstract class Mod190Declaration {
	private enum Declarations {
		MOD190_GENERIC_2023 {
			
			@Override
			boolean accept(Mod190 mod) {
				return mod.getYear() >= 2023;
			}

			@Override
			Mod190Declaration get() {
				return new Mod190ALL2023Declaration();
			}
		}
		,
		MOD190_GENERIC_2022 {
			
			@Override
			boolean accept(Mod190 mod) {
				return mod.getYear() == 2022;
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
			throw new AonCoreException("No se ha indicado un ejercicio v\u00E1lido para la declaraci\u00F3n");
		}
		if (mod.getPeriod() == null) {
			throw new AonCoreException("No se ha indicado periodo para la declaraci\u00F3n");	
		}
		return Arrays.stream(Declarations.values())
			.filter(dec -> dec.accept(mod))
			.map(Declarations::get)
			.findFirst()
			.orElseThrow( () -> new AonCoreException(MessageFormat.format(
				"No existe una declaraci\u00F3n para el modelo solicitado ({0} - {1})", mod.getAdministration().getDescription(),mod.getYear())));
	}
	
	abstract Mod190 insertDetailsFromInvoice(AONContext ctx, final Mod190 mod190);
	abstract Mod190 insertDetailsFromSalary(AONContext ctx, final Mod190 mod190);
	abstract LinkedList<Mod190Detail> validateSalaries(AONContext ctx, final Mod190 mod190);;
	
}
