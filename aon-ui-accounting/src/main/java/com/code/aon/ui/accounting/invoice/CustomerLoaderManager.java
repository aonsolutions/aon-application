package com.code.aon.ui.accounting.invoice;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.enumeration.MediaType;
import com.esferalia.aon.entity.IEntityAlias;

public class CustomerLoaderManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerLoaderManager.class.getName());
	
	private static final String SEMICOLON = ";";
	private static final String EQUALS = "=";
	private static final String METADATA_MARK_0 = "0";
	private static final String METADATA_MARK_1 = "1";
	private static final String SEPARATOR_KEY = "Separador";
	private static final String ENCODING_KEY = "Codificacion";
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	private static final String CLI = "CLI";
	
	private static final String[] SUPPORTED_ENTITIES = {CLI};

	private static final Column[][] SUPPORTED_COLUMNS = {
		{ 
			 new Column(CLI,"id"							,0,6	,true	,null)
			,new Column(CLI,"razonSocial"					,2,64	,true	,null)
			,new Column(CLI,"alias"							,2,32	,false	,null)
			,new Column(CLI,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
			,new Column(CLI,"paisDocumento"					,2,2	,true	,null)
			,new Column(CLI,"documento"						,2,16	,true	,null)
			,new Column(CLI,"nacionalidad"					,2,2	,true	,null)
			,new Column(CLI,"cuenta"						,2,9	,false	,null)
			,new Column(CLI,"re"							,0,1	,false	,null)
			,new Column(CLI,"transaccion"					,0,1	,false	,new int[] {0,1,2,3})
			,new Column(CLI,"retencion"						,0,1	,false	,null)
			,new Column(CLI,"facturarAlbaranesAgrupados"	,0,1	,false	,null)
			,new Column(CLI,"tipoVia"						,2,2	,false	,null)
			,new Column(CLI,"direccion"						,2,128	,false	,null)
			,new Column(CLI,"numero"						,0,6	,false	,null)
			,new Column(CLI,"direccion2"					,2,128	,false	,null)
			,new Column(CLI,"direccion3"					,2,128	,false	,null)
			,new Column(CLI,"cp"							,2,16	,false	,null)
			,new Column(CLI,"ciudad"						,2,64	,false	,null)
			,new Column(CLI,"provincia"						,2,3	,false	,null)
			,new Column(CLI,"nombreProvincia"				,2,32	,false	,null)
			,new Column(CLI,"pais"							,2,2	,false	,null)
			,new Column(CLI,"telefono1"						,2,64	,false	,null)
			,new Column(CLI,"telefono2"						,2,64	,false	,null)
			,new Column(CLI,"fax"							,2,64	,false	,null)
			,new Column(CLI,"email"							,2,64	,false	,null)
			,new Column(CLI,"web"							,2,64	,false	,null)
			,new Column(CLI,"banco"							,2,64	,false	,null)
			,new Column(CLI,"cuentaBanco"					,2,23	,false	,null)
			,new Column(CLI,"formaPago"						,2,32	,false	,null)
			,new Column(CLI,"numeroVtos"					,0,6	,false	,null)
			,new Column(CLI,"diasAlPrimerVto"				,0,6	,false	,null)
			,new Column(CLI,"diasEntreVtos"					,0,6	,false	,null)
			,new Column(CLI,"diasPago"						,2,8	,false	,null)
		}
	};

	private PrintWriter log;
	private Scope scope;
	private SecurityLevel securityLevel;
	private WorkPlace workPlace;
	
	private String encoding = "ISO-8859-1";
	private String sep = "|";
	private Map<String, Column[]> columns;
	
	public CustomerLoaderManager(Scope scope, SecurityLevel securityLevel,WorkPlace workPlace,PrintWriter log) {
		this.log = log;
		this.scope = scope;
		this.securityLevel = securityLevel;
		this.workPlace = workPlace;
	}
	
	public Scope getScope() {
		return scope;
	}

	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public String getEncoding() {
		return encoding;
	}
	private void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getSep() {
		return sep;
	}
	private void setSep(String sep) {
		this.sep = sep;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}

	public void loadMetadata(InputStream input) throws AonException {
		log("Meta: Start loading Metadata.");
		columns = new HashMap<String, Column[]>();
		if (input == null) {
			raiseException(0, "Input is null!");
		}
		try {
			InputStreamReader inputReader = new InputStreamReader(input,getEncoding());
			LineNumberReader reader = new LineNumberReader(inputReader);
			int i = 0;
			while (reader.ready()) {
				++i;
				String line = reader.readLine();
				if (!StringUtils.isBlank(line)) {
					String mark = line.substring(0,1);
					if (METADATA_MARK_0.equals(mark)) {
						String subLine = StringUtils.substringAfter(line, SEMICOLON);
						parseMetaMark0(i,subLine);
					} else if (METADATA_MARK_1.equals(mark)) {
						String subLine = StringUtils.substringAfter(line, SEMICOLON);
						parseMetaMark1(i,subLine);
					} else {
						break;
					}
				}
			}
			reader.close();
			input.close();
			if (i == 0) {
				raiseException(0, "No existen datos en el canal de entrada");
			}
			if (!getColumns().containsKey(CLI)) {
				log("Meta: CLI entity metadata not found in file. Default used;");
				getColumns().put(CLI, SUPPORTED_COLUMNS[0]);
			}
			log("Meta: Metadata loaded!");
		} catch (UnsupportedEncodingException e) {
			raiseException(0, "La codificación no es válida");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}
	
	private void log(String msg  ) {
		log.println( msg );
		log.flush();
	}
	
	private void parseMetaMark0(int line,String subLine) throws AonException {
		if (StringUtils.isBlank(subLine)) {
			raiseException(line,"No existe linea de meta información o no está correctamente definida.");
		}
		String[] nameValue = subLine.split(EQUALS);
		if (nameValue == null || nameValue.length != 2) {
			raiseException(line,"No existe linea de meta información o no está correctamente definida.");
		}
		String name = nameValue[0]; 
		String value = nameValue[1];
		log("Meta: Found " + name + "=" + value);
		if (SEPARATOR_KEY.equals(name)) {
			if (StringUtils.isNotBlank(value)) {
				setSep(value);	
				log("Meta: " + SEPARATOR_KEY + " set to " + value);
			} else {
				raiseException(line,"El separador indicado no puede estar vacio.");
			}
		} else if (ENCODING_KEY.equals(name)) {
			if (StringUtils.isNotBlank(value)) {
				setEncoding(value);
				log("Meta: " + ENCODING_KEY + " set to " + value);
			} else {
				raiseException(line,"La codificacion indicada no puede estar vacio.");
			}
		} else {
			raiseException(line,"El parámetro " + name + " de meta información, no está soportado.");
		}
	}
	
	private void raiseException(int i, String message) throws AonException {
		String msg = "Línea " + i +": " + message;
		log("ERROR: " + msg);
		throw new AonException(msg);
	}
	
	private void parseMetaMark1(int line,String subLine) throws AonException {
		String[] tokens = StringUtils.split(subLine, getSep());
		if (tokens == null) {
			raiseException(line, "No está correctamente definida.");
		}
		String entity = tokens[0];
		int entityIndex = ArrayUtils.indexOf(SUPPORTED_ENTITIES, entity);
		if (entityIndex == -1) {
			raiseException(line, "La entidad " + entity+" no está soportada.");
		}
		log("Meta: Found " + entity + " entity.");
		if (getColumns().containsKey(entity)) {
			raiseException(line, "Ya existe una definición para el tipo " + entity+".");
		}
		String cols = StringUtils.substringAfter(subLine, getSep());
		String[] columns = StringUtils.split(cols, getSep());
		List<Column> inputColumns = new LinkedList<Column>();
		for (String column : columns) {
			boolean added = false;
			for (Column c : SUPPORTED_COLUMNS[entityIndex]){
				if (StringUtils.equals(column, c.getName())) {
					log("\t Meta: Found '" + c.getName() + "' column.");
					inputColumns.add(c);
					added = true;	
				}
			}
			if (!added) {
				raiseException(line, "La columna " + column + " no está soportada.");
			}
		}
		getColumns().put(entity, inputColumns.toArray(new Column[inputColumns.size()]));
		log("Meta: Entity " + entity + " registered.");
	}
	
	public void validateFormat(InputStream input) throws AonException {
		try {
			int errors = 0;
			int warnings = 0;
			log(" Data format start Validation!");
			InputStreamReader inputReader = new InputStreamReader(input,getEncoding());
			LineNumberReader reader = new LineNumberReader(inputReader);
			int i = 0;
			while (reader.ready()) {
				++i;
				String line = reader.readLine();
				if (!StringUtils.isBlank(line)) {
					String mark = line.substring(0,1);
					if (!METADATA_MARK_0.equals(mark) && !METADATA_MARK_1.equals(mark) ) {
						String entity = StringUtils.substringBefore(line, getSep());
						int entityIndex = ArrayUtils.indexOf(SUPPORTED_ENTITIES, entity);
						if (entityIndex == -1) {
							raiseException(i, "La entidad " + entity+" no está soportada.");
						}
						parseLine(i,null,getColumns().get(entity),StringUtils.substringAfter(line, getSep()));
					} else {
						log ( "Skipping metadata line " + i);
					}
					
				}
				 
			}
			reader.close();				
			log(" Data format validation finish!");
			log(" " + errors + " errors, " + warnings + " warnings");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}
	
	public void load(InputStream input) throws AonException {
		try {
			int errors = 0;
			int warnings = 0;
			log(" Data load start!");
			InputStreamReader inputReader = new InputStreamReader(input,getEncoding());
			LineNumberReader reader = new LineNumberReader(inputReader);
			int i = 0;
			while (reader.ready()) {
				++i;
				String line = reader.readLine();
				if (!StringUtils.isBlank(line)) {
					String mark = line.substring(0,1);
					if (!METADATA_MARK_0.equals(mark) && !METADATA_MARK_1.equals(mark) ) {
						String entity = StringUtils.substringBefore(line, getSep());
						int entityIndex = ArrayUtils.indexOf(SUPPORTED_ENTITIES, entity);
						if (entityIndex == -1) {
							raiseException(i, "La entidad " + entity+" no está soportada.");
						}
						Object bean = getTargetBean(entity);
						parseLine(i,bean,getColumns().get(entity),StringUtils.substringAfter(line, getSep()));
						try {
							insert( bean );	
						} catch (AonException e){
							raiseException(i, e.getMessage());
						}
						if (i%50 == 0) {
							LOGGER.info("" + i + " clientes insertadas");
						}
					} else {
						log ( "Skipping metadata line " + i);
					}
					
				}
				 
			}
			reader.close();				
			log(" Data load finish!");
			log(" " + errors + " errors, " + warnings + " warnings");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}

	private void insert(Object bean) throws AonException {
		if (bean instanceof LoadedCustomer) {
			insertCustomer( (LoadedCustomer) bean);
		}
	}

	private Object getTargetBean(String entity) {
		if (CLI.equals(entity)) {
			return new LoadedCustomer();
		} 
		return null;
		
	}

	private void parseLine(int i, Object target, Column[] definition, String columns) throws AonException {
		String[] cols = StringUtils.splitByWholeSeparatorPreserveAllTokens(columns,getSep());
		if (cols == null || cols.length != definition.length) {
			raiseException(i, " El número de columnas no coincide con la definición, debe haber " + definition.length + " columnas");
		}
		int x = 0;
		for (Column c : definition) {
			Object data = parseData(i, cols[x], c );
			if (target != null) {
				try {
					BeanUtils.setProperty(target, c.getName(), data);
				} catch (IllegalAccessException e) {
					raiseException(i, e.getMessage());
				} catch (InvocationTargetException e) {
					raiseException(i, e.getMessage());
				}
			}
			++x;
		}
	}


	private Object parseData(int i, String string, Column c) throws AonException {
		try {
			if (StringUtils.isBlank(string)) {
				return null;
			}
			string = StringUtils.trim(string);
			if ( string.length() > c.getLength() ) {
				raiseException(i, "Superada máxima longitud (" + c.getLength() + ")");	
			}
			if (c.getType() == 0) {
				return getInt(string); 
			} else if (c.getType()== 1) {
				return getDouble(string);
			} else if (c.getType() == 3) {
				return getDate(string);
			}
			return string;
		} catch (AonException e) {
			raiseException(i, "Col: " + c.getName() + " con valor '" + string +  "'. " + e.getMessage() );
			return null; //??
		}
	}

	private Date getDate(String data) throws AonException {
		try {
			Date date = FORMATTER.parse(data);
			String ensure = FORMATTER.format(date);
			if (!StringUtils.equals(data, ensure)) {
				throw new AonException("Fecha no correcta");	
			}
			return date;
		} catch (ParseException e) {
			throw new AonException("Fecha no correcta");
		}
	}
	
	private Double getDouble(String data) throws AonException {
		try {
			double d = Double.parseDouble(data);
			return d;
		} catch (NumberFormatException e) {
			throw new AonException("Número decimal no correcto");
		}
	}
	
	private Integer getInt(String data) throws AonException {
		try {
			int d = Integer.parseInt(data);
			return d;
		} catch (NumberFormatException e) {
			throw new AonException("Número no correcto");
		}
	}

	private void insertCustomer(LoadedCustomer loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Customer customer  = new Customer ();
		
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getNationality());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getSecurityLevel());
		customer.setRegistry(registry);
		
		customer.setScope(getScope());
		customer.setTransaction(loaded.getInvoiceTransactionType());
		customer.setSurcharge(loaded.isSurcharge());
		customer.setWithholding(loaded.isWithholding());
		customer.setDeliveryGrouped(loaded.isDeliveryGrouped());
		customer.setStatus(CustomerStatus.ACTIVE);
		customer = (Customer) bean.insert(customer);
		
		
		if (StringUtils.isNotBlank(loaded.getTipoVia())
			|| StringUtils.isNotBlank(loaded.getDireccion())
			|| StringUtils.isNotBlank(loaded.getNumero())
			|| StringUtils.isNotBlank(loaded.getDireccion2())
			|| StringUtils.isNotBlank(loaded.getDireccion3())
			|| StringUtils.isNotBlank(loaded.getCp())
			|| StringUtils.isNotBlank(loaded.getCiudad())
			|| StringUtils.isNotBlank(loaded.getProvincia())
			|| StringUtils.isNotBlank(loaded.getNombreProvincia())
			|| StringUtils.isNotBlank(loaded.getPais())) {
			insertRegistryAddress(customer.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.EMAIL,loaded.getEmail());
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(customer.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(customer.getRegistry(),loaded);	
		}
		if (rbank != null || StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(customer.getRegistry(),rbank,loaded);
		}
		if (StringUtils.isNotBlank(loaded.getCuenta())) {
			insertRegistryCustomerAccount( customer, loaded);
		}
		
	}

	private void insertRegistryCustomerAccount(Customer customer, LoadedCustomer loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CustomerAccount.class);
		CustomerAccount ca = new CustomerAccount();
		Account account = ensureAccount(loaded.getCuenta(), customer.getRegistry().getName());
		ca.setCustomer(customer);
		ca.setAccount(account);
		bean.insert(ca);
	}

	private Account ensureAccount(String accountCode, String description) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.ACCOUNT_CODE) , accountCode);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (Account) list.get(0);	
		} 
		if (StringUtils.isNotBlank(description)) {
			Account account = new Account();
			account.setCode(accountCode);
			account.setDescription(description);
			return (Account) bean.insert(account);
		}
		return null;
	}

	private RegistryBank insertRegistryBank(Registry registry,LoadedCustomer loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rbank = new RegistryBank();
		rbank.setRegistry(registry);
		BankAccount bankAccount = new BankAccount();
		String[] ccc = StringUtils.split(loaded.getCuentaBanco(),".");
		if (ccc.length != 4) {
			throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
		}
		bankAccount.setEntity(ccc[0]);
		bankAccount.setOffice(ccc[1]);
		bankAccount.setControl(ccc[2]);
		bankAccount.setAccount(ccc[3]);
		if (!bankAccount.isValid()) {
			throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
		}
		Bank bank = ensureBank(ccc[0], loaded.getBanco() );
		rbank.setBank(bank);
		rbank.setBankAccount(bankAccount);
		return (RegistryBank) bean.insert(rbank);		
	}

	private Bank ensureBank(String bankCode, String banco) throws ManagerBeanException {
		IManagerBean bankBean = BeanManager.getManagerBean(Bank.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bankBean.getFieldName( IEntityAlias.BANK_CODE) , bankCode);
		List<ITransferObject> list = bankBean.getList(c);
		if (list != null && list.size() > 0) {
			return (Bank) list.get(0);	
		} 
		if (StringUtils.isNotBlank(banco)) {
			Bank bank = new Bank();
			bank.setCode(bankCode);
			bank.setName(banco);
			return (Bank) bankBean.insert(bank);
		}
		return null;
	}

	private void insertRegistryPayMethod(Registry registry,RegistryBank rbank,LoadedCustomer loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryPayMethod.class);
		RegistryPayMethod rPayMethod = new RegistryPayMethod();
		rPayMethod.setRegistry(registry);
		rPayMethod.setRegistryBank(rbank);
		rPayMethod.setNumberOfPayments(loaded.getNumeroVtos());
		rPayMethod.setDaysBetweenPayments(loaded.getDiasEntreVtos());
		rPayMethod.setDaysToFirstPayment(loaded.getDiasAlPrimerVto());
		if (StringUtils.isNotBlank(loaded.getDiasPago())) {
			rPayMethod.setPaymentDays(loaded.getDiasPago());	
		}
		if (StringUtils.isNotBlank(loaded.getFormaPago())) {
			PayMethod payMethod = ensurePayMethod(loaded.getFormaPago());
			rPayMethod.setPayment(payMethod);
		}
		bean.insert(rPayMethod);
	}

	private PayMethod ensurePayMethod(String formaPago) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName( IEntityAlias.PAY_METHOD_NAME) , formaPago);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			return (PayMethod) list.get(0);	
		} 
		PayMethod payMethod = new PayMethod();
		payMethod.setName(formaPago);
		payMethod.setType(PayMethodType.OTHER);
		return (PayMethod) bean.insert(payMethod);
	}

	private void insertRegistryAddress(Registry registry, LoadedCustomer loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		RegistryAddress address = new RegistryAddress();
		address.setRegistry(registry);
		address.setAddress(loaded.getDireccion());
		address.setNumber(loaded.getNumero());
		address.setAddress2(loaded.getDireccion2());
		address.setAddress3(loaded.getDireccion3());
		address.setZip(loaded.getCp());
		address.setCity(loaded.getCiudad());
		GeoZone geozone = ensureGeoZone(loaded.getPais(),loaded.getProvincia(),loaded.getNombreProvincia() );
		address.setGeozone(geozone);
		bean.insert(address);		
	}

	private GeoZone ensureGeoZone(String pais, String provincia, String nombreProvincia) throws ManagerBeanException {
		IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
		Criteria c = new Criteria();
		c.addEqualExpression(geozoneBean.getFieldName( IEntityAlias.GEO_ZONE_CODE) , provincia);
		List<ITransferObject> list = geozoneBean.getList(c);
		if (list != null && list.size() > 0) {
			return (GeoZone) list.get(0);	
		}
		return null;
	}

	private void insertRegistryMedia(Registry registry, MediaType type, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		RegistryMedia rmedia = new RegistryMedia();
		rmedia.setRegistry(registry);
		rmedia.setMediaType(type);
		rmedia.setValue(value);
		bean.insert(rmedia);		
	}
}
