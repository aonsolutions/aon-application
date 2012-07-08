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

import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.SupplierAccount;
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
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceLoaderManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceLoaderManager.class.getName());
	
	private static final String SEMICOLON = ";";
	private static final String EQUALS = "=";
	private static final String METADATA_MARK_0 = "0";
	private static final String METADATA_MARK_1 = "1";
	private static final String SEPARATOR_KEY = "Separador";
	private static final String ENCODING_KEY = "Codificacion";
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	private static final String VTO = "VTO";
	
	private static final String[] SUPPORTED_ENTITIES = {VTO};

	private static final Column[][] SUPPORTED_COLUMNS = {
		{ 
			 new Column(VTO,"id"							,0,6	,true	,null)
			,new Column(VTO,"tipo"							,2,1	,false	,null)
			,new Column(VTO,"tipoDocumento"					,4,1	,true	,new int[] {0,1,2,3,4,5})
			,new Column(VTO,"paisDocumento"					,2,2	,true	,null)
			,new Column(VTO,"documento"						,2,16	,true	,null)
			,new Column(VTO,"razonSocial"					,2,64	,true	,null)
			,new Column(VTO,"importe"						,1,10	,false	,null)
			,new Column(VTO,"concepto"						,2,64	,false	,null)
			,new Column(VTO,"fechaVto"						,3,32	,false	,null)
			,new Column(VTO,"formaPago"						,2,32	,false	,null)
			,new Column(VTO,"cuentaBanco"					,2,23	,false	,null)
			,new Column(VTO,"cuenta"						,2,9	,false	,null)
		}
	};

	private PrintWriter log;
	private Scope scope;
	private SecurityLevel securityLevel;
	private WorkPlace workPlace;
	
	private String encoding = "ISO-8859-1";
	private String sep = "|";
	private Map<String, Column[]> columns;
	
	public FinanceLoaderManager(Scope scope, SecurityLevel securityLevel,WorkPlace workPlace,PrintWriter log) {
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
			if (!getColumns().containsKey(VTO)) {
				log("Meta: CLI entity metadata not found in file. Default used;");
				getColumns().put(VTO, SUPPORTED_COLUMNS[0]);
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
							LOGGER.info("" + i + " acreedores insertadas");
						}
					} else {
						log ( "Skipping metadata line " + i);
					}
					
				}
				 
			}
			reader.close();
			LOGGER.info(" Data load finish!");
			log(" Data load finish!");
			log(" " + errors + " errors, " + warnings + " warnings");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}

	private void insert(Object bean) throws AonException {
		if (bean instanceof LoadedFinance) {
			insertFinance( (LoadedFinance) bean);
		}
	}

	private Object getTargetBean(String entity) {
		if (VTO.equals(entity)) {
			return new LoadedFinance();
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

	private void insertFinance(LoadedFinance loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Finance.class);
		Finance finance  = new Finance();
		
		String cuenta = loaded.getCuenta();
		Registry registry = null;
		if (StringUtils.startsWith(cuenta, "400")) {
			registry = getRegistry(SupplierAccount.class,"SupplierAccount.account.code",loaded.getCuenta());
		}
		if (StringUtils.startsWith(cuenta, "410")) {
			registry = getRegistry(CreditorAccount.class,"CreditorAccount.account.code",loaded.getCuenta());
		}
		if (StringUtils.startsWith(cuenta, "430")) {
			registry = getRegistry(CustomerAccount.class,"CustomerAccount.account.code",loaded.getCuenta());
		}
		if (registry == null) {
			throw new ManagerBeanException("Registry es nulo!");
		}
		finance.setRegistry(registry);
		finance.setScope(getScope());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setRegistryDocumentType(loaded.getDocumentType());
		finance.setRegistryDocumentCountry(loaded.getDocumentCountry());
		finance.setRegistryDocument(loaded.getDocumento());
		finance.setRegistryName(loaded.getRazonSocial());
		finance.setAmount(loaded.getImporte());
		finance.setConcept(loaded.getConcepto());
		if (StringUtils.isNotBlank(loaded.getFormaPago())) {
			finance.setPayMethod(ensurePayMethod(loaded.getFormaPago()));	
		}
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
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
				throw new ManagerBeanException("El CCC del vto "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
			}
			finance.setBankAccount(bankAccount);
			Bank bank = ensureBank(ccc[0] );
			finance.setBank(bank);
		}
		finance = (Finance) bean.insert(finance);
	}

	private Bank ensureBank(String bankCode) throws ManagerBeanException {
		IManagerBean bankBean = BeanManager.getManagerBean(Bank.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bankBean.getFieldName( IEntityAlias.BANK_CODE) , bankCode);
		List<ITransferObject> list = bankBean.getList(c);
		if (list != null && list.size() > 0) {
			return (Bank) list.get(0);	
		} 
		return null;
	}

	private Registry getRegistry(Class<? extends ITransferObject> clazz, String alias, String cuenta) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(clazz);
		Criteria c = new Criteria();
		c.addEqualExpression(alias, cuenta);
		List<ITransferObject> list = bean.getList(c);
		if (list != null && list.size() > 0) {
			IAccount account = (IAccount) list.get(0);
			IRegistry ir = (IRegistry) account.getLinkedTo();
			return ir.getRegistry();
		}
		return null;
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
	
}
