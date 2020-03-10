package com.esferalia.aon.altai.tgss.creta;

import static com.esferalia.aon.jooq.tables.Contract.CONTRACT;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import com.esferalia.aon.jooq.tables.Contract;

import net.aonsolutions.core.tgss.creta.jaxb.Utils;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Bases;
import net.aonsolutions.core.tgss.creta.jaxb.bases.CtaCot;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Liquidacion;
import net.aonsolutions.core.tgss.creta.jaxb.bases.LiquidacionMes;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Trabajador;
import net.aonsolutions.core.tgss.creta.jaxb.bases.Tramo;

public class Bases2Aon {
	
	DSLContext dslContext;
	
	public Bases2Aon(Connection connection) {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	}
	
	
	public void parse(File file) throws IOException {
		InputStream is = null; 
		try {
			is = new FileInputStream(file);
			parse(is);
		} catch ( JAXBException e ) {
			System.err.printf("Warnning: '%s' is not an valid 'SLD-Basis File'\r\n", file.getPath() );
		}
		finally {
			if ( is != null )
				is.close();
		}
		
	}
	
	public void parse(InputStream is) throws JAXBException {
		Bases bases = Utils.unmarshal(Bases.class, is);
		bases.getLiquidacion().stream()
		.filter(l -> "L00".equals(l.getTipo()))
		.forEach(l -> liquidacion2Aon(l) );
	}
	
	// ------------------------------------------------------------------------
	
	private void liquidacion2Aon(Liquidacion liquidacion) {
		CtaCot ctaCot = liquidacion.getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia() ,ctaCot.getNumero());
		
		liquidacion.getLiquidacionMes().stream()
		.flatMap(liquidacionMes -> liquidacionMes.getTrabajadores().getTrabajador().stream())
		.forEach(trabajador -> trabajador2Aon(ccc, trabajador) );
		;
	}
	

	private void trabajador2Aon(String ccc, Trabajador trabajador) {
		String naf = trabajador.getNaf();
		trabajador.getTramos().getTramo().stream()
		.forEach(tramo ->tramo2Aon(ccc, naf, tramo));
	}
	
	private void tramo2Aon ( String ccc, String naf, Tramo tramo) {
//		dslContext
//		.select()
//		.from(CONTRACT)
//		.where(CONTRACT.eq())
//		
	}
	
	
}	
