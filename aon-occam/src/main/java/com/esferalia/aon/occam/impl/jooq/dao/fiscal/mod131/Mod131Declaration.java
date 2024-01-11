package com.esferalia.aon.occam.impl.jooq.dao.fiscal.mod131;

import java.text.MessageFormat;
import java.util.Arrays;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.fiscal.Mod131;
import com.esferalia.aon.occam.api.model.fiscal.Mod131Activity;
import com.esferalia.aon.watson.error.AonCoreException;

public abstract class Mod131Declaration {
	private enum Declarations {
		AEAT_2023 {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT2023Declaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT2023Declaration();}
		}
		,AEAT_2022_4T {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT20224TDeclaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT20224TDeclaration();}
		}
		,AEAT_2020_4T {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT20204TDeclaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT20204TDeclaration();}
		}
		,AEAT_2016 {
			@Override boolean accept(Mod131 mod) { return Mod131AEAT2016Declaration.accept(mod);}
			@Override Mod131Declaration get() {return new Mod131AEAT2016Declaration();}
		}
		;
		abstract boolean accept(Mod131 mod);
		abstract Mod131Declaration get();
	}
	
	public static Mod131Declaration getInstance( Mod131 mod) {
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
					"No existe una declaración para el modelo solicitado ({0} - {1} - {2})",
					mod.getAdministration().getDescription()
					,mod.getYear()
					,mod.getPeriod().getDescription())));
	}
	
	abstract Mod131Activity calculateActivity(AONContext ctx, Mod131Activity act);

}
