package com.esferalia.aon.payroll.ctsql2mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.company.enumeration.CCCType;
import com.code.aon.company.enumeration.EnterpriseActivityType;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.person.enumeration.Gender;
import com.code.aon.person.enumeration.MaritalStatus;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cliente;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Cnae;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Delegacion;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Domicilio;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Empract;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprccc;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprdom;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprnif;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Emprper;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Persona;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Provincia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Tipovia;
import com.esferalia.aon.payroll.ctsql2mysql.AbstractCtsqlDB.Trabajo;


/********************************************************************
* Copyright (c) 2010, esferalia NETWORKS S.A
*
* The copyright of the computer program herein is the property 
* of esferalia NETWORKS.
*********************************************************************
* The program may be used and/or copied only with the written 
* permission of esferalia NETWORKS, or in accordance with the 
* terms and conditions stipulated in the agreement contract 
* under which the program has been supplied.
*********************************************************************
*/

/**
 * 
 */
@SuppressWarnings("serial")
public class MysqlDB extends AbstractMysqlDB{
	
	
	// --------------------------------------------------------------
	// constants
	// --------------------------------------------------------------
	final static short MAIN_ADDRESS   = 0;
	final static short OTHER_ADDRESS  = 1;

	final static int COMPANY_REGISTRY = 1;
	
	final static Locale SPANISH		= new Locale("es");
	
	final static Hashtable<String, String> NEW_GEOZONES= 
		new Hashtable<String, String>() {
			{
				put("ORENSE","OURENSE");
				put("CORUÑA","A CORUÑA");

				put("LERIDA","LLEIDA");
				put("BALEARES","ILLES BALEARS");
				
			}
		};
		

	final static HashMap<String, Short> MARITAL_STATUS= 
		new HashMap<String, Short>() {
			{
				put("S",enum2short(MaritalStatus.SINGLE));
				put("C",enum2short(MaritalStatus.MARRIED));
				put("V",enum2short(MaritalStatus.WIDOWED));
				put("D",enum2short(MaritalStatus.SEPARATED));
				put("R",enum2short(MaritalStatus.SINGLE));
				put("T",enum2short(MaritalStatus.SINGLE)); 
				
				put(null,enum2short(MaritalStatus.UNKNOWN)); 
				
			}
		};
			
	final static Logger LOGGER = LoggerFactory.getLogger(MysqlDB.class);
	

	// --------------------------------------------------------------

	private static Short enum2short(Enum<?> type) {
		return (short ) type.ordinal();
	}

	private static interface ForeachRowCallback {
		public Object row(ResultSet rs) throws SQLException;
	}
	
	// --------------------------------------------------------------
	// State related members 
	private Integer 	scopeId;				// 'scope' id where we are in 
	
	private Integer 	enterpriseId;			// 'registry' id of enterprise where we are in
	private Integer 	enterpriseActivityId;

	// --------------------------------------------------------------
	
	
	private HashMap<String, Integer> cnaes = 
		new HashMap<String, Integer>();

	private HashMap<Integer, Integer> persons = 
		new HashMap<Integer, Integer>();

	private HashMap<String, Integer> geozones = 
		new HashMap<String, Integer>();
	
	private HashMap<String, Short> streetTypes = 
		new HashMap<String, Short>();
	
	private HashMap<Integer, Integer> raddresses = 
		new HashMap<Integer, Integer>();

	private HashMap<Integer, Integer> enterprises = 
		new HashMap<Integer, Integer>();
	
	private HashMap<String, Integer> cccs= 
		new HashMap<String, Integer>();


	// --------------------------------------------------------------
	
	public MysqlDB(Connection mysqlConnection) throws SQLException {
		super(mysqlConnection);
	}
	
	private Object foreachRow (String sqlQuery, ForeachRowCallback cb ) 
	throws SQLException{
		
		Statement queryStmt = mysqlConnection.createStatement();
		ResultSet rs = queryStmt.executeQuery(sqlQuery);
		Object retObject = null;
		while ( retObject == null && rs.next() ) {
			retObject = cb.row(rs);
		}
		queryStmt.close();
		rs.close();
		return retObject;
	}
	
	private Integer getGeozone(String province ) 
	throws SQLException{
		
		
		final String provinceTrimed = province.trim() ;
		
		return ( Integer ) foreachRow("SELECT id, name FROM geozone", new ForeachRowCallback() {
			@Override
			public Object row(ResultSet rs) throws SQLException {
				String name = rs.getString("name");
				if ( name != null ) {
					if ( provinceTrimed.equalsIgnoreCase(name.trim()))
						return (Integer) rs.getInt("id");
				}
				return null;
			}
		} );
	}
	
	private StreetType getStreetType(String tipoVia) {
		StreetType  streetTypes [] = StreetType.values();
		for (StreetType streetType : streetTypes) {
			String streeTypeName = streetType.getName(SPANISH);
			if ( tipoVia.trim().equalsIgnoreCase(streeTypeName) )
				return streetType;
		}
		return null;
	}
	
	
	private void insertFax(Integer registry, String email)
	throws SQLException
	{
		insertRmedia(registry, 
				(short)MediaType.FAX.ordinal(), 
				email, 
				null, 
				true,		// administrative 
				false, 		// not commercial
				false);		// tecnical
	}

	private void insertEmail(Integer registry, String email)
	throws SQLException
	{
		insertRmedia(registry, 
				(short)MediaType.EMAIL.ordinal(), 
				email, 
				null, 
				true,		// administrative 
				false, 		// not commercial
				true);		// tecnical
	}
	
	private void insertTelephone(Integer registry, String telephone)
	throws SQLException
	{
		short mediaType ;
		
		if ( telephone.trim().startsWith("9"))
			mediaType = (short)MediaType.FIXED_PHONE.ordinal();
		else 
			mediaType = (short)MediaType.CELLULAR.ordinal();
		
		insertRmedia(registry, 
				mediaType, 
				telephone, 
				null, 
				true,		// administrative 
				false, 		// not commercial
				false);		// not tecnical
	}
	
	@Override
	public boolean visitCnae(Cnae cnae) throws SQLException {
		// TODO: ¿ sobra ocupación ?.
		int id = insertCnae(cnae.getCdg(), cnae.getDescripcion());
		cnaes.put(cnae.getCdg(), id);
		return true;
	}

	@Override
	public boolean visitTipovia(Tipovia tipovia) throws SQLException {
		String descripcion =  tipovia.getDescripcion();
		if ( descripcion == null )
			return true;
		
		StreetType streetType = getStreetType(descripcion);
		if ( streetType != null ) {
			streetTypes.put(tipovia.getCdg(), (short) streetType.ordinal());
			return true;
		}
		
		LOGGER.warn("No se ha encontrado ningun tipo de via para la {}", 
				descripcion );
		
		return true;
	}
	
	@Override
	public boolean visitProvincia(Provincia provincia) throws SQLException {
		
		String descripcion = provincia.getDescripcion();
		if ( descripcion == null )
			return  true;
		
		Integer geozone = getGeozone(descripcion);
		
		if ( geozone != null ) {
			geozones.put(provincia.getCdg(), geozone);
			return true;
		}
		String newDescripcion = NEW_GEOZONES.get(descripcion.trim());
		if ( newDescripcion != null  ) {
			geozone = getGeozone(newDescripcion);
			LOGGER.warn("La provincia '{}' será el geozone '{}'", descripcion, newDescripcion);
			geozones.put(provincia.getCdg(), geozone);
			return true;
		}	
		
		geozone = insertGeozone(descripcion);
		LOGGER.warn("No se ha encontrado ningun geozone para la provincia {}, hemos añadimos un nuevo {}", 
				provincia.getDescripcion(), geozone);
		geozones.put(provincia.getCdg(), geozone);
		return true;

	}
	
	@Override
	public boolean visitPersona(Persona persona) throws SQLException { 
		
		Short gender = "V".equals(persona.getSexo()) ?
				enum2short(Gender.MALE) :
				enum2short(Gender.FEMALE);	
		
				
		String apellido = String.format("%s %s", 
				persona.getDescripcion(),
				persona.getApellido2());
		
		Integer registry = insertRegistry(persona.getNumdoc(), 
				persona.getNombre(), 
				apellido, 
				persona.getAlias(), 
				enum2short(RegistryType.NATURAL));
		
		
		// there are some unknow values in ctsql database. 
		Short maritalStatus =  MARITAL_STATUS.get(persona.getEstciv());
		if ( maritalStatus == null ){
			maritalStatus  = enum2short(MaritalStatus.UNKNOWN);
		}
		
		insertPerson(registry, 
				persona.getFecnac(), 
				gender, 
				maritalStatus, 
				persona.getNumss());
		
		
		insertRaddress(registry, 
				enum2short(AddressType.MAIN), 
				null, 									//TODO: raddress 'recipient'
				streetTypes.get(persona.getTipovia()), 
				persona.getNomvia(), 
				persona.getNumero(), 
				persona.getOtrdir(), 
				persona.getCodpos(), 
				persona.getLocalidad(), 
				geozones.get (persona.getProvincia()));

		if ( persona.getTelefono() != null )
			insertTelephone (registry, persona.getTelefono() );
		
		if ( persona.getEmail() != null )
			insertEmail(registry, persona.getEmail()) ;
		
		persons.put(persona.getCdg(), registry);
		
		return true;
	}
	
	@Override
	public boolean visitDelegacion(AbstractCtsqlDB.Delegacion delegacion)
			throws SQLException {

		int 	newScope;

		String 	description = delegacion.getDescripcion();
		if ( description != null && description.length() > 16 )
		{
			
			String truncated = description.substring(0,15);
			LOGGER.warn("Delegación '{}' es muy larga. Ámbito '{}'.", 
					description, truncated );
			description = truncated;
		}
		
		newScope = insertScope(description);

		this.scopeId = newScope;
		delegacion.visitCliente(this);
		this.scopeId = null;
		
		// each delegacion will be one 'raddress' of company.
		insertRaddress(COMPANY_REGISTRY, 			
						enum2short(AddressType.DELEGATION), 				
						null,										
						streetTypes.get(delegacion.getTipovia()),	
						delegacion.getNomvia(),			
						delegacion.getNumero(), 					
						delegacion.getOtrdir(), 					
						delegacion.getCodpos(), 
						delegacion.getLocalidad(), 
						geozones.get (delegacion.getProvincia()));
		
		if ( delegacion.getTelefono() != null )
			insertTelephone (COMPANY_REGISTRY, delegacion.getTelefono() );
		
		return true;
	}

	@Override
	public boolean visitCliente(Cliente cliente, Delegacion delegacion) throws SQLException {
		cliente.visitEmprnif(this); 
		cliente.visitDomicilio(this);
		return true;
	}
	
	@Override
	public boolean visitEmprnif(Emprnif emprnif, Cliente cliente) throws SQLException {
		// TODO: ¿ Donde meto los datos de emprnif ?
		
		Integer registry ;
		
		registry = insertRegistry(emprnif.getNumdoc(), 
				emprnif.getDescripcion(), 
				null,							// enterprise hasn't surname 
				emprnif.getAlias(), 	
				enum2short(RegistryType.LEGAL));

		Short status = "N".equals(cliente.getInactivo()) ? 
				enum2short(CustomerStatus.ACTIVE) :
				enum2short(CustomerStatus.INACTIVE);

		insertCustomer(registry, 
				null, 
				false, 
				false, 
				false, 
				null, 
				status, 
				null, 
				scopeId, 
				false, 
				true, 
				true);
		
		insertEnterprise(registry, 
					this.scopeId);
		
		enterprises.put(emprnif.getCdg(), registry);
		
		this.enterpriseId = registry;
		emprnif.visitEmpract(this);
		this.enterpriseId = null;
		
		return true;
	}
	
	
	@Override
	public boolean visitDomicilio(Domicilio domicilio, Cliente cliente) throws SQLException {
		
		domicilio.visitEmprdom(this);

		return true;
	}
	
	@Override
	public boolean visitEmprdom(Emprdom emprdom, Domicilio domicilio) throws SQLException {
		
		Integer enterprise = enterprises.get(emprdom.getCodemp());
		
		if ( enterprise == null )
		{
			LOGGER.error("Domicilio '{}-{}-{}' no asociado a ninguna empresa.", 
					new Integer [] {
					emprdom.getCdg(),
					emprdom.getCodemp(),
					emprdom.getCodcli()});
			return true;
		}
		
		Integer raddress = raddresses.get(emprdom.getCdg());
		
		if ( raddress == null ) {

			raddress = insertRaddress(enterprise, 
					enum2short ( AddressType.DELEGATION ), 
					null, 
					streetTypes.get(domicilio.getTipovia()), 
					domicilio.getNomvia(), 
					domicilio.getNumero(), 
					domicilio.getOtrdir(), 
					domicilio.getCodpos(), 
					domicilio.getLocalidad(), 
					geozones.get(domicilio.getProvincia()));
			
			if ( domicilio.getTelefono() != null )
				insertTelephone (enterprise, domicilio.getTelefono() );
			if ( domicilio.getTelefono2() != null )
				insertTelephone (enterprise, domicilio.getTelefono2() );
			if ( domicilio.getTelefono3() != null )
				insertTelephone (enterprise, domicilio.getTelefono3() );
	
			if ( domicilio.getFax() != null )
				insertFax(enterprise, domicilio.getFax() );

			raddresses.put(domicilio.getCdg(), raddress);
		}

		// emprdom.tipdom available values
		// -------------------------------
		// A : Todos
		// T : Centro de trabajo
		// S : Social
		// ...
		// O : Otros
		if ( "T".equals(emprdom.getTipdom()) ){ 

			String workplaceDescrp = domicilio.getAclaracion();
			if ( workplaceDescrp == null ){
				// TODO: ¿ Ddescripción no 'nula' ?
				workplaceDescrp = domicilio.getNomvia(); 
			}
	
			insertWorkplace(enterprise, 
					workplaceDescrp, 
					raddress, 
					null,				// TODO:  Concierto Económico del Centro de Trabajo
					true);
		}
		
		return true;
	}
	
	
	@Override
	public boolean visitEmpract(Empract empract, Emprnif emprnif) throws SQLException {
		
		if ( empract.getCnae() == null )
			return true;
		
		String 	description = empract.getDescripcion();
		if ( description != null && description.length() > 32 )
		{
			
			String truncated = description.substring(0,31);
			LOGGER.warn("Actividad '{}' es muy larga, truncamos a '{}'.", 
					description, truncated );
			description = truncated;
		}
		
		Integer cnae = cnaes.get(empract.getCnae());
		if ( cnae == null ) {
			LOGGER.warn("CNAE {}-{} no encontrado.", 
					empract.getCnae(), empract.getActeco());
			cnae = insertCnae(empract.getCnae(), empract.getActeco());
			cnaes.put(empract.getCnae(), cnae);
		}
		
		this.enterpriseActivityId = insertEnterprise_activity(description, 
				this.enterpriseId, 
				cnae, 
				enum2short(EnterpriseActivityType.PRINCIPAL));
		// TODO : ¿ Cómo elegimos el tipo de atividad ?
		
		empract.visitEmprccc(this);
		empract.visitEmprper(this);
		
		this.enterpriseActivityId = null;
		
		
		
		return true;
	}
	
	
	
	@Override
	public boolean visitEmprccc(Emprccc emprccc, Empract empract) throws SQLException {
		
		String ccc = emprccc.getDescripcion();
		
		if ( ccc == null ){
			LOGGER.error("CCC nulo en empreccc '{}'", emprccc.getCdg() );
			return true;
		}
		
		String provincia = ccc.substring(0, 2) ;
		
		Integer geozone = geozones.get(provincia);
		if ( geozone == null ) {
			LOGGER.error("La provincia {} del CCC {}, no está registarda", provincia, ccc );
			return true;
		}

		Short type = null ;
		String tipccc = emprccc.getTipccc();
		if ( "P".equals(tipccc))
			type = enum2short(CCCType.PRINCIPAL);
		if ( "A".equals(tipccc))
			type = enum2short(CCCType.TRADE_REPRESENTATIVE);
		if ( "R".equals(tipccc))
			type = enum2short(CCCType.LEARNING);
		if ( "S".equals(tipccc))
			type = enum2short(CCCType.ASSIMILATEDS);
		
		Integer cccId = insertEnterprise_ccc(ccc, 
				type, 
				enterpriseActivityId, 
				geozone);
		
		cccs.put(ccc, cccId );
		
		// TODO: Hay que añadir el geozone, a enterprise_ccc
		
		return true;
	}
	
	
	@Override
	public boolean visitEmprper(Emprper emprper, Empract empract)
			throws SQLException {
		
		emprper.visitTrabajo(this);
		
		return true;
	}
	
	@Override
	public boolean visitTrabajo(Trabajo trabajo, Emprper emprper)
			throws SQLException {
		Integer person = persons.get(emprper.getCodper());
		
		if ( person == null ){
			LOGGER.error("En el contrato {}, la persona {} no existe.", 
					emprper.getCodact(), 
					emprper.getCdg());
			return true;
		}
		

		
		return true;
	}
	
	
	public void writeAll(CtsqlDB ctsqlReader) throws SQLException {
		// Auxiliars 
		ctsqlReader.visitCnae(this);
		ctsqlReader.visitTipovia(this);
		ctsqlReader.visitProvincia(this);
		
		ctsqlReader.visitPersona(this);

		ctsqlReader.visitDelegacion(this);
	}
	
	
	
	
	public static void main(String[] args) throws ClassNotFoundException, ParseErrorException, MethodInvocationException, ResourceNotFoundException, SQLException, IOException {
		
		// create the command line parser
    	CommandLineParser parser = new PosixParser();   
    	
    	// create the Options
    	Options options = new Options();

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(false);
    	OptionBuilder.withDescription("imprime esta ayuda.");
    	Option helpOption = OptionBuilder.create( "help" );

    	
    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "URL" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "cadena de conexión." );
    	Option ctsqlURLOption = OptionBuilder.create( "url" );


    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "usuario para conectarse." );
    	Option ctsqlUserOption = OptionBuilder.create( "user" );

    	OptionBuilder.isRequired(false);
    	OptionBuilder.hasArg(true);
    	OptionBuilder.withArgName( "name" );
    	OptionBuilder.withType(String.class);
    	OptionBuilder.withDescription(  "clave para conectarse." );
    	Option ctsqlPasswdOption = OptionBuilder.create( "passwd" );
    	

    	options.addOption(helpOption);
    	options.addOption(ctsqlURLOption);
    	options.addOption(ctsqlUserOption);
    	options.addOption(ctsqlPasswdOption);
    	
    	
    	HelpFormatter helpFormatter = new HelpFormatter();
    	
    	try {
    		// first of all load JDBC drivers
            Class.forName("org.gjt.mm.mysql.Driver");

            // parse the command line arguments
            CommandLine line = parser.parse( options, args );
            
            if ( line.hasOption(helpOption.getOpt()) )
            	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
            
            String url = line.getOptionValue(ctsqlURLOption.getOpt(), 
            		"jdbc:mysql://127.0.0.1:3306/rtrepiana-esferalia-com");
            String user = line.getOptionValue(ctsqlUserOption.getOpt(), "dbuser");
            String passwd = line.getOptionValue(ctsqlPasswdOption.getOpt(), "serubd2000");
            
            String mysqlDBArgs [] = {
            		"-url", url,
            		"-user", user,
            		"-passwd", passwd,
            		"-out" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/AbstractMysqlDB.java" ,
            		"-template" , "src/main/java/com/esferalia/aon/payroll/ctsql2mysql/templates/MysqlDB.java.vm" 
            };
            

            DBContext.main(mysqlDBArgs);

            
    	}
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Error : " + exp.getMessage() );
        	helpFormatter.printHelp(HelpFormatter.DEFAULT_SYNTAX_PREFIX, options, true);
        } 

	}


}
