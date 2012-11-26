package com.esferalia.aon.payroll.irpf;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import com.aeat.jaxb.AEATRetencionesEntrada2011;
import com.aeat.jaxb.ObjectFactory;
import com.aeat.jaxb.TipoDiscapacidad;
import com.aeat.jaxb.TipoDiscapacidad.Grado1;
import com.aeat.jaxb.TipoDiscapacidad.Grado1.MovilidadReducida;
import com.aeat.jaxb.TipoDiscapacidad.Grado2;
import com.aeat.jaxb.TipoRetenedorEntrada2011;
import com.aeat.jaxb.TipoRetenedorError2011;
import com.aeat.jaxb.TipoRetenedorSalida2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011;
import com.aeat.jaxb.TipoRetenidoEntrada2011.Descendiente;
import com.aeat.jaxb.TipoRetenidoError2011;
import com.aeat.jaxb.TipoRetenidoSalida2011;
import com.code.aon.config.enumeration.Administration;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneIrpfColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneIrpfDescendantColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.GeozoneIrpfHandicapColumns;
import com.esferalia.aon.salary.expression.ExpressionException;

public class GeozoneIrpfCalculator extends IrpfCalculator {
	
	private static final Map<Administration, Integer>  ADMINISTRATION_GEOZONE= 
		new HashMap<Administration, Integer>() {
		{
			put(Administration.ALAVA	, 1);
			put(Administration.GIPUZKOA	, 20);
			put(Administration.BIZKAIA	, 48);
			put(Administration.NAVARRA	, 31);
		}
	};
	
	private static final String DESCENDANTS_SQL = 
		"SELECT *" 
		+ "FROM geozone_irpf"
		+ ", geozone_irpf_descendant"
		+ " WHERE geozone_irpf.id = geozone_irpf_descendant.geozone_irpf "
		+ " AND geozone_irpf.geozone = ?" 
		+ " AND geozone_irpf.start_date <= ?"
		+ " AND ( geozone_irpf.end_date >= ? OR geozone_irpf.end_date IS NULL )"
		+ " ORDER BY geozone_irpf.amount, geozone_irpf_descendant.descendant";
	
	private static final String HANDICAP_SQL = 
		"SELECT *" 
		+ "FROM geozone_irpf"
		+ ", geozone_irpf_handicap"
		+ " WHERE geozone_irpf.id = geozone_irpf_handicap.geozone_irpf "
		+ " AND geozone_irpf.geozone = ?" 
		+ " AND geozone_irpf.start_date <= ?"
		+ " AND ( geozone_irpf.end_date >= ? OR geozone_irpf.end_date IS NULL )"
		+ " ORDER BY geozone_irpf.amount, geozone_irpf_handicap.handicap";
	

	private SortedMap<Double, SortedMap<Integer, Double>> handicap;
	private SortedMap<Double, SortedMap<Integer, Double>> descendants;
	
	public GeozoneIrpfCalculator(Connection connection, 
			Administration administration, 
			Date date) throws SQLException {
		Integer geozone = ADMINISTRATION_GEOZONE.get(administration);
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());

		initHandicap(connection, geozone, sqlDate );
		initDescendants(connection, geozone, sqlDate );
	}
	
	@Override
	public void calculate(
			AEATRetencionesEntrada2011 aeatRetencionesEntrada2011,
			CallbackHandler cb) throws IrpfException, ExpressionException {
		
		List<TipoRetenedorEntrada2011> retenedor =
			aeatRetencionesEntrada2011.getRetenedor();
		
		for (TipoRetenedorEntrada2011 retenedorEntrada2011 : retenedor) {
			
			List<TipoRetenidoEntrada2011> retenido = 
				retenedorEntrada2011.getRetenido();
			
			TipoRetenedorSalida2011 retenedorSalida2011 = 
				createTipoRetenedorSalida2011(retenedorEntrada2011);
			
			for (TipoRetenidoEntrada2011 retenidoEntrada2011 : retenido) {
				calculate(retenedorSalida2011 , retenidoEntrada2011, cb);
			}
		}
			
	}
	
	private void  calculate( TipoRetenedorSalida2011 retenedorSalida2011, TipoRetenidoEntrada2011 retenidoEntrada2011, 
			CallbackHandler cb ) throws IrpfException, ExpressionException {
		
		BigDecimal retribAnuales = 
			retenidoEntrada2011.getRetribAnuales();
		if ( retribAnuales == null ) {
			return;
		}
		double remuneration = retribAnuales.doubleValue();
		

		int descendantCount = 
			retenidoEntrada2011.getDescendiente().size();
		Double irpf = getIrpf( remuneration, descendantCount );
		if ( irpf == null ) {
			//cb.onError(retenedorError2011, retenidoError2011);
			return;
		}
		
		DisabilityLevel disabilityLevel = 
			getDisabilityLevel(retenidoEntrada2011.getDiscapacidad());
		if ( disabilityLevel != null ) {
			Double reduction = getReduction ( remuneration, disabilityLevel.ordinal() );
			if ( reduction != null ) {
				irpf -= reduction;
			}
		}
		
		double retention = remuneration * ( irpf / 100 );  

		TipoRetenidoSalida2011  retenidoSalida2011  = 
			createTipoRetenidoSalida2011(retenidoEntrada2011);
		
		retenidoSalida2011.setTipoRetencion(BigDecimal.valueOf(irpf));
		retenidoSalida2011.setImpAnualRetencionesIngresosCuenta(BigDecimal.valueOf(retention));
		
		cb.onSalida(retenedorSalida2011, retenidoSalida2011);
	}
	
	private DisabilityLevel getDisabilityLevel(TipoDiscapacidad discapacidad){
		if ( discapacidad == null ) {
			return null;
		}

		Grado1 grado1 = discapacidad.getGrado1();
		if ( grado1 != null ) {
			MovilidadReducida movilidadReducida = 
				grado1.getMovilidadReducida();
			return movilidadReducida == null ? 
					DisabilityLevel.GT_EQ_33_LT_65 : 
						DisabilityLevel.GT_EQ_33_LT_65_DEPENDENCE;
		}
		
		Grado2 grado2 = discapacidad.getGrado2();
		if ( grado2 != null ) {
			return DisabilityLevel.GT_EQ_65;
		}
		
		return null;
	}
	
	private Double getIrpf ( double remuneration, int descendantsCount ) {
		
		SortedMap<Integer, Double> percents = get(descendants, remuneration );
		
		return percents == null ? null : get(percents, descendantsCount );
	}
	
	private Double getReduction ( double remuneration, int disability ) {
		
		SortedMap<Integer, Double> percents = get(handicap, remuneration);
		
		return percents == null ? null : get(percents, disability );
	}

	private <K extends Comparable<K>, V> V get( SortedMap<K, V> map, K key) {
		V value = null;
		for ( Entry<K,V> entry: map.entrySet() ) {
			K fromKey = entry.getKey();
			if ( key.compareTo(fromKey) < 0) {
				break;
			}
			value = entry.getValue();
		}
		return value;
	}
	
	
	private void initHandicap(Connection connection, int geozone, java.sql.Date date) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(HANDICAP_SQL);
			stmt.setInt(1, geozone ); 	// geozone_irpf.geozone = ?
			stmt.setDate(2, date );		// geozone_irpf.start_date >= ?
			stmt.setDate(3, date );		// geozone_irpf.end_date <= ? OR geozone_irpf.end_date IS NULL
			
			rs = stmt.executeQuery();
			
			handicap = new TreeMap<Double, SortedMap<Integer,Double>>();
			
			while ( rs.next() ) {
				double amount = rs.getInt(GeozoneIrpfColumns.AMOUNT);
				
				SortedMap<Integer,Double> percents = handicap.get(amount);
				if ( percents == null  ) {
					percents = new TreeMap<Integer,Double>();
					handicap.put(amount, percents);
				}
				
				int handicap = rs.getInt(GeozoneIrpfHandicapColumns.HANDICAP);
				double percent = rs.getDouble(GeozoneIrpfHandicapColumns.PERCENT);
				
				percents.put(handicap, percent);
			}
		}
		finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
		}
	}

	private void initDescendants(Connection connection, int geozone, java.sql.Date date) throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			stmt = connection.prepareStatement(DESCENDANTS_SQL);
			stmt.setInt(1, geozone ); 	// geozone_irpf.geozone = ?
			stmt.setDate(2, date );		// geozone_irpf.start_date >= ?
			stmt.setDate(3, date );		// geozone_irpf.end_date <= ? OR geozone_irpf.end_date IS NULL
			
			rs = stmt.executeQuery();
			
			descendants = new TreeMap<Double, SortedMap<Integer,Double>>();
			
			while ( rs.next() ) {
				double amount = rs.getInt(GeozoneIrpfColumns.AMOUNT);
				
				SortedMap<Integer,Double> percents = descendants.get(amount);
				if ( percents == null  ) {
					percents = new TreeMap<Integer,Double>();
					descendants.put(amount, percents);
				}
				
				int descendant = rs.getInt(GeozoneIrpfDescendantColumns.DESCENDANT);
				double percent = rs.getDouble(GeozoneIrpfHandicapColumns.PERCENT);
				
				percents.put(descendant, percent);
			}
		}
		finally {
			if ( rs != null ) {
				rs.close();
			}
			if ( stmt != null ) {
				stmt.close();
			}
		}
	}
	
	private TipoRetenidoSalida2011 createTipoRetenidoSalida2011( TipoRetenidoEntrada2011 retenidoEntrada2011){
		ObjectFactory factory = new ObjectFactory();
		
		TipoRetenidoSalida2011 retenidoSalida2011 = factory.createTipoRetenidoSalida2011();

		retenidoSalida2011.setNif(retenidoEntrada2011.getNif());
		retenidoSalida2011.setApellidosNombre(retenidoEntrada2011.getApellidosNombre());
		retenidoSalida2011.setAñoNacimiento(retenidoEntrada2011.getAñoNacimiento());
		retenidoSalida2011.setComunidadAutonoma(retenidoEntrada2011.getComunidadAutonoma());
		retenidoSalida2011.setRetribAnuales(retenidoEntrada2011.getRetribAnuales());
		retenidoSalida2011.setGastosAnuales(retenidoEntrada2011.getGastosAnuales());
		retenidoSalida2011.setSituacionLaboral(retenidoEntrada2011.getSituacionLaboral());
		retenidoSalida2011.setSituacionFamiliar(retenidoEntrada2011.getSituacionFamiliar());
		
		retenidoSalida2011.setDiscapacidad(retenidoEntrada2011.getDiscapacidad());
		//retenidoSalida2011.setDescendientes(retenidoEntrada2011.getDescendiente());
		
		return retenidoSalida2011;
	}
	
	private TipoRetenedorSalida2011 createTipoRetenedorSalida2011( TipoRetenedorEntrada2011 retenedorEntrada2011 ){
		ObjectFactory factory = new ObjectFactory();
		
		TipoRetenedorSalida2011 retenedorSalida2011 = 
			factory.createTipoRetenedorSalida2011();
		
		retenedorSalida2011.setNif(retenedorEntrada2011.getNif());
		retenedorSalida2011.setApellidosNombre(retenedorEntrada2011.getApellidosNombre());
		
		return retenedorSalida2011;
	}
	
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IrpfException, ExpressionException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://127.0.0.1:3306/payroll-esferalia-org?autoReconnect=true";
		String usr = "dbuser"; 
		String psw = "serubd2000";
		Connection connection = DriverManager.getConnection(url,usr ,psw );
		
		ObjectFactory factory = new ObjectFactory();
		
		TipoRetenidoEntrada2011 retenidoEntrada2011 = 
			factory.createTipoRetenidoEntrada2011();
		retenidoEntrada2011.setRetribAnuales(BigDecimal.valueOf(33000.00));
		
		Descendiente descendiente = new Descendiente();
		retenidoEntrada2011.getDescendiente().add(descendiente);
		retenidoEntrada2011.getDescendiente().add(descendiente);
		
		TipoDiscapacidad discapacidad = new TipoDiscapacidad();
		discapacidad.setGrado2(new Grado2());
		retenidoEntrada2011.setDiscapacidad(discapacidad);
		
		TipoRetenedorEntrada2011 retenedorEntrada2011 =
			factory.createTipoRetenedorEntrada2011();
		retenedorEntrada2011.getRetenido().add(retenidoEntrada2011);
			
		AEATRetencionesEntrada2011 aeatRetencionesEntrada2011 = 
			factory.createAEATRetencionesEntrada2011();
		aeatRetencionesEntrada2011.getRetenedor().add(retenedorEntrada2011);
		
		Date today = Calendar.getInstance().getTime();
		
		CallbackHandler cb = new CallbackHandler() {
			
			@Override
			public void onSalida(TipoRetenedorSalida2011 retenedorSalida2011,
					TipoRetenidoSalida2011 retenidoSalida2011) {
				System.out.println(retenidoSalida2011.getTipoRetencion() + "%");
			}
			
			@Override
			public void onError(TipoRetenedorError2011 retenedorError2011,
					TipoRetenidoError2011 retenidoError2011) {
			}
		};
		
		GeozoneIrpfCalculator arabaIrpfcalculator = 
			new GeozoneIrpfCalculator(connection, Administration.ALAVA, today);
		arabaIrpfcalculator.calculate(aeatRetencionesEntrada2011, cb);
		
		GeozoneIrpfCalculator gipuzkoaIrpfcalculator = 
			new GeozoneIrpfCalculator(connection, Administration.GIPUZKOA, today);
		gipuzkoaIrpfcalculator.calculate(aeatRetencionesEntrada2011, cb);

		GeozoneIrpfCalculator bizkaiaIrpfcalculator = 
			new GeozoneIrpfCalculator(connection, Administration.BIZKAIA, today);
		bizkaiaIrpfcalculator.calculate(aeatRetencionesEntrada2011, cb);

		GeozoneIrpfCalculator nafarroaIrpfcalculator = 
			new GeozoneIrpfCalculator(connection, Administration.NAVARRA, today);
		nafarroaIrpfcalculator.calculate(aeatRetencionesEntrada2011, cb);

	}
}
