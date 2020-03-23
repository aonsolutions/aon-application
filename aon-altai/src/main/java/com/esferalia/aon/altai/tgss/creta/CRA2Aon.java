package com.esferalia.aon.altai.tgss.creta;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;


public class CRA2Aon {

	private static interface TrabajadorCallback  {
		void trabajador(String ccc, CRAParser.Trabajador trabajador);
	}
	
	
	Condition where;
	DSLContext dslContext;
	Connection connection;


	public CRA2Aon(Connection connection, Condition where ) {
		Settings settings;
		settings = new Settings();
		settings.setRenderSchema(false);
		settings.setParamType(ParamType.INLINED);
		
		
		this.where = where;
		this.connection = connection;
		this.dslContext = DSL.using(connection, SQLDialect.MARIADB, settings);
	}
	
	public void fix(File file) throws IOException {
		dslContext.transaction((configuration)->{			
		parse(file, this::fixTrabajador);
		});
	}	
	
	// ------------------------------------------------------------------------
	
	private void parse(File file, TrabajadorCallback cb) throws IOException {
		Reader reader = null; 
		try {
			reader = new FileReader(file);
			parse(reader, cb);
		} 
		catch ( CRAException e ) {
			System.out.println("Not CRA file " + file );
		}
		finally {
			if ( reader != null )
				reader.close();
		}
	}
	
	private void parse(Reader reader, TrabajadorCallback cb) throws IOException {
		CRAParser.CRA cra = CRAParser.parse(reader);
		cra.getLiquidaciones().forEach(l -> liquidacion2Aon(l, cb));
	}	
	
	private void liquidacion2Aon(CRAParser.Liquidacion liquidacion, TrabajadorCallback cb) {
		CRAParser.CtaCot ctaCot = liquidacion.getCcc();
		String ccc = String.format("%s%s", ctaCot.getProvincia() ,ctaCot.getNumero());

		liquidacion.getTrabajadores()
		.forEach(trabajador -> cb.trabajador(ccc, trabajador));
		;
	}
	
	private void fixTrabajador(String ccc, CRAParser.Trabajador trabajador) {
		System.out.println(ccc + ", " + trabajador.getNaf());
		trabajador.getConceptosRetributivos().forEach(c -> System.out.println("\t" + c.getCodigo() +":" + c.getImporte() + "(" + c.getIndicadorExcluidoIncluido() + ")"));
	}
}
