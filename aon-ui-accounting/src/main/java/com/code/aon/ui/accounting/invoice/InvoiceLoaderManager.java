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
import com.code.aon.account.IAccount;
import com.code.aon.account.bridge.CreditorAccount;
import com.code.aon.account.bridge.CustomerAccount;
import com.code.aon.account.bridge.SupplierAccount;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.company.WorkPlace;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.customer.Customer;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.enumeration.CreditorStatus;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.supplier.Supplier;
import com.code.aon.supplier.enumeration.SupplierStatus;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceLoaderManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceLoaderManager.class.getName());
	
	private static final String SEMICOLON = ";";
	private static final String EQUALS = "=";
	private static final String METADATA_MARK_0 = "0";
	private static final String METADATA_MARK_1 = "1";
	private static final String SEPARATOR_KEY = "Separador";
	private static final String ENCODING_KEY = "Codificacion";
	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm:ss");
	
	private static final String FRA = "FRA";
	private static final String DET = "DET";
	private static final String VTO = "VTO";
	
	private static final String[] SUPPORTED_ENTITIES = {FRA,DET,VTO};

	private static final Column[][] SUPPORTED_COLUMNS = {
		{ 
			 new Column(FRA,"id"			,0,6	,true	,null)
			,new Column(FRA,"serie"			,2,5	,true	,null)
			,new Column(FRA,"numero"		,0,6	,true	,null)
			,new Column(FRA,"referencia"	,2,16	,true	,null)
			,new Column(FRA,"cuenta"		,2,9	,true	,null)
			,new Column(FRA,"documento"		,2,16	,true	,null)
			,new Column(FRA,"tipoDocumento"	,4,1	,true	,new int[] {0,1,2,3,4,5})
			,new Column(FRA,"paisDocumento"	,2,2	,true	,null)
			,new Column(FRA,"razonSocial"	,2,128	,true	,null)
			,new Column(FRA,"fechaFactura"	,3,10	,true	,null)
			,new Column(FRA,"fechaIva"		,3,10	,false	,null)
			,new Column(FRA,"tipo"			,0,1	,true	,new int[] {0,1,2,3})
			,new Column(FRA,"inversion"		,0,1	,false	,null)
			,new Column(FRA,"transaccion"	,0,1	,false	,new int[] {0,1,2,3})
			,new Column(FRA,"comentario"	,2,256	,false	,null)
		}
		,{ 
			 new Column(DET,"factura"			,0,6	,true	,null)
			,new Column(DET,"linea"				,0,6	,true	,null)
			,new Column(DET,"articulo"			,2,15	,true	,null)
			,new Column(DET,"concepto"			,2,64	,true	,null)
			,new Column(DET,"cantidad"			,1,16	,true	,null)
			,new Column(DET,"precio"			,1,16	,true	,null)
			,new Column(DET,"baseImponible"		,1,17	,true	,null)
			,new Column(DET,"porcentajeIva"		,1,6	,true	,null)
			,new Column(DET,"cuotaIva"			,1,16	,true	,null)
			,new Column(DET,"re"				,1,6	,true	,null)
			,new Column(DET,"cuotaRe"			,1,16	,false	,null)
			,new Column(DET,"porcentajeIrpf"	,1,6	,true	,null)
			,new Column(DET,"cuotaIrpf"			,1,17	,false	,null)
			,new Column(DET,"tipoDeduccionIva"	,0,1	,false	,new int[] {0,1})
			,new Column(DET,"tipoIrpf"			,0,1	,false	,new int[] {0,1,2,3,4})
			,new Column(DET,"cuenta"			,2,9	,false	,null)
			,new Column(DET,"cuentaIva"			,2,9	,false	,null)
			,new Column(DET,"cuentaIrpf"		,2,9	,false	,null)
		}

		,{ 
			 new Column("vencimiento","id"	,0,0	,true	,null)
		}
		
	};

	private PrintWriter log;
	private Scope scope;
	private SecurityLevel securityLevel;
	private WorkPlace workPlace;
	private ProductCategory category;
	
	private String encoding = "ISO-8859-1";
	private String sep = "|";
	private Map<String, Column[]> columns;
	private Map<Integer, Invoice> invoices;
	
	public InvoiceLoaderManager(Scope scope, SecurityLevel securityLevel,WorkPlace workPlace,ProductCategory category,PrintWriter log) {
		this.log = log;
		this.scope = scope;
		this.securityLevel = securityLevel;
		this.workPlace = workPlace;
		this.category = category;
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

	public ProductCategory getCategory() {
		return category;
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
	
	public Map<Integer, Invoice> getInvoices() {
		if (invoices == null) {
			invoices = new HashMap<Integer, Invoice>();
		}
		return invoices;
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
			if (!getColumns().containsKey(FRA)) {
				log("Meta: FRA entity metadata not found in file. Default used;");
				getColumns().put(FRA, SUPPORTED_COLUMNS[0]);
			}
			if (!getColumns().containsKey(DET)) {
				log("Meta: DET entity metadata not found in file. Default used;");
				getColumns().put(DET, SUPPORTED_COLUMNS[1]);
			}
			if (!getColumns().containsKey(VTO)) {
				log("Meta: VTO entity metadata not found in file. Default used;");
				getColumns().put(VTO, SUPPORTED_COLUMNS[2]);
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
							LOGGER.info("" + i + " facturas insertadas");
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
		if (bean instanceof LoadedInvoice) {
			insertInvoice( (LoadedInvoice) bean);
		} else if (bean instanceof LoadedInvoiceDetail) {
			insertInvoiceDetail( (LoadedInvoiceDetail) bean );
		} else if (bean instanceof LoadedInvoiceFinance) {
			insertInvoiceFinance( (LoadedInvoiceFinance) bean );
		}
	}

	private Object getTargetBean(String entity) {
		if (FRA.equals(entity)) {
			return new LoadedInvoice();
		} else  if (DET.equals(entity)) {
			return new LoadedInvoiceDetail();
		} 
		return new LoadedInvoiceFinance();
		
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

	private void insertInvoiceFinance(LoadedInvoiceFinance bean) {
		// TODO Auto-generated method stub
	}

	private void insertInvoiceDetail(LoadedInvoiceDetail loaded) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(InvoiceDetail.class);
		InvoiceDetail detail = new InvoiceDetail();
		Invoice invoice = getInvoices().get(loaded.getFactura());
		if (invoice == null) {
			throw new AonException("La factura con identiicador " + loaded.getFactura() + " no existe.");
		}
		detail.setInvoice(invoice);
		detail.setLine(loaded.getLinea());
		if (StringUtils.isBlank(loaded.getArticulo())) {
			detail.setSource(InvoiceSource.ACCOUNT);	

			StringBuilder sb = new StringBuilder();
			sb.append("Fra. Nº: ");
			sb.append(invoice.getReferenceCode());
			sb.append(" del ");
			sb.append(FORMATTER.format(invoice.getIssueDate()));
			detail.setDescription(sb.toString());
		} else {
			Item item = obtainItem( loaded );
			detail.setItem(item);
			detail.setDescription(StringUtils.join(new String[]{item.getProduct().getName(),item.getDescription()}," "));
			detail.setSource(InvoiceSource.DIRECT_INVOICE);
		}
		detail.setPrice(loaded.getPrecio());
		detail.setQuantity(loaded.getCantidad());
		detail.setTaxableBase(loaded.getBaseImponible());
		detail.setWorkPlace(getWorkPlace());
		// No actualiza la linea.
		detail.setUpdateEnabled(false);
		// No actualiza los totales. 
		//detail.getInvoice().setUpdateEnabled(false);
		//
		detail = (InvoiceDetail) bean.insert(detail);

		if (detail.getSource() == InvoiceSource.ACCOUNT) {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			InvoiceTax invoiceTax = new InvoiceTax();
			
			invoiceTax.setInvoiceDetail(detail);
			invoiceTax.setPercentage(loaded.getPorcentajeIva());
			invoiceTax.setQuota(loaded.getCuotaIva());
			if (!invoice.isSurcharge() && loaded.getRe() > 0) {
				invoice.setDefaultTaxInfo(false);
				invoice.setSurcharge(true);
			}
			invoiceTax.setSurcharge(loaded.getRe());
			invoiceTax.setSurchargeQuota(loaded.getCuotaRe());
			invoiceTax.setTaxType(TaxType.VAT);
			invoiceTax.setVatDeductionType(loaded.getVatDeductionType());
			invoiceTaxBean.insert(invoiceTax);
			
			if (loaded.getPorcentajeIrpf() > 0) {
				if (!invoice.isWithholding()) {
					invoice.setDefaultTaxInfo(false);
					invoice.setWithholding(true);	
				}
				invoiceTax = new InvoiceTax();
				invoiceTax.setInvoiceDetail(detail);
				invoiceTax.setPercentage(loaded.getPorcentajeIrpf());
				invoiceTax.setQuota(loaded.getCuotaIrpf());
				invoiceTax.setSurcharge(0);
				invoiceTax.setTaxType(TaxType.RETENTION);
				invoiceTax.setWithholdingType(loaded.getWithholdingType());
				invoiceTaxBean.insert(invoiceTax);
			}
			IManagerBean invoiceBean = BeanManager.getManagerBean(Invoice.class);
			invoice = (Invoice) invoiceBean.update(invoice);
			detail.setInvoice(invoice);
			getInvoices().put(loaded.getFactura(), invoice);
		}

	}

	private Item obtainItem(LoadedInvoiceDetail loaded) throws ManagerBeanException {
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE), loaded.getArticulo());
		List<ITransferObject> list = itemBean.getList(criteria);
		Item item = null;
		if (list.isEmpty()) {
			Product product = new Product();
			product.setCode(loaded.getArticulo());
			product.setName(loaded.getConcepto());
			product.setVat( obtainTax( TaxType.VAT, loaded ) );
			if (loaded.getPorcentajeIrpf() != 0) {
				product.setRetention(obtainTax( TaxType.RETENTION, loaded ) );
			}
			product.setCategory( getCategory() );
			product.setStatus(ProductStatus.ACTIVE);
			product.setType(ProductType.COMMERCIAL_PRODUCT);
			item = new Item();
			item.setProduct(product);
			item.setPrice( loaded.getPrecio() );
			item.setStatus(ProductStatus.ACTIVE);
			item = (Item) itemBean.insert(item);
		} else {
			item = (Item) list.get(0);	
		}
		return item;
	}

	private Tax obtainTax(TaxType type, LoadedInvoiceDetail loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_TYPE), type);
		if (type == TaxType.VAT) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), loaded.getPorcentajeIva());
			if ( loaded.getRe() != 0) {
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_SURCHARGE), loaded.getRe());	
			}
		} else {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_PERCENTAGE), loaded.getPorcentajeIrpf());
		}
		List<ITransferObject> list = bean.getList(criteria);
		Tax tax = null;
		if (list.isEmpty()) {
			tax = new Tax();
			tax.setType(type);
			try {
				tax.setStartDate(FORMATTER.parse("01/01/2000"));
			} catch (ParseException e) {
				/// nothing
			}
			tax.setPercentage((type == TaxType.VAT)?loaded.getPorcentajeIva():loaded.getPorcentajeIrpf() );
			tax.setName((type == TaxType.VAT)?"IVA " + loaded.getPorcentajeIva():"IRPF " + loaded.getPorcentajeIrpf() );
			tax.setSurcharge( (type == TaxType.VAT)?loaded.getRe(): 0.0 );
			tax.setVatDeductionType((type == TaxType.VAT)?loaded.getVatDeductionType(): null );
			tax.setWithholdingType((type == TaxType.VAT)?null:loaded.getWithholdingType());
			tax = (Tax) bean.insert(tax);
		} else {
			tax = (Tax) list.get(0);
		}
		return tax;
	}

	private void insertInvoice(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Invoice invoice = new Invoice();
		InvoiceType type = InvoiceType.values()[loaded.getTipo()];
		if (type == InvoiceType.SALES ) {
			Series series = lookForSeries( loaded.getSerie() ); 
			invoice.setSeries( series.getCode() );
			invoice.setNumber( loaded.getNumero() );
		} else {
			invoice.setReferenceCode(loaded.getReferencia());
		}
		invoice.setType(type);
		Registry registry = obtainRegistry( type , loaded);
		invoice.setRegistry(registry);
		invoice.setRegistryDocument(loaded.getDocumento());
		invoice.setRegistryDocumentType(DocumentType.values()[loaded.getTipoDocumento()]);
		invoice.setRegistryDocumentCountry(Country.valueOf( loaded.getPaisDocumento()));
		invoice.setRegistryName(loaded.getRazonSocial());
		invoice.setIssueDate(loaded.getFechaFactura());
		invoice.setTaxDate(loaded.getFechaIva());
		invoice.setInvestment( loaded.isInvestment() );
		invoice.setTransaction(loaded.getInvoiceTransactionType());
		invoice.setComments(loaded.getComentario());
		Date now = new Date();
		invoice.setRemarks("Importada de fichero " + FORMATTER.format(now) + " - " + TIME_FORMATTER.format(now));
		invoice.setStatus(InvoiceStatus.PENDING);
		invoice = (Invoice) bean.insert(invoice);
		getInvoices().put(loaded.getId(), invoice);
	}
	
	private Registry obtainRegistry(InvoiceType type, LoadedInvoice loaded) throws ManagerBeanException {
		IRegistry r = null;
		if ( type == InvoiceType.SALES ) {
			r = obtainCustomer( loaded );		
		} else if ( type == InvoiceType.PURCHASE ) {
			r = obtainSupplier( loaded );
		} else if ( type == InvoiceType.EXPENSES || type == InvoiceType.UNDEDUCTIBLE) {
			r = obtainCreditor( loaded );
		}
		return r.getRegistry();
	}

	private Creditor obtainCreditor(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Creditor.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_STATUS), CreditorStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CREDITOR_SCOPE_ID), getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un acreedor activo con el número de documento " + loaded.getDocumento());
			}
			Creditor creditor = (Creditor) list.get(0); 
			if (!StringUtils.isBlank(loaded.getCuenta())) {
				IManagerBean caBean = BeanManager.getManagerBean(CreditorAccount.class);
				String alias = caBean.getFieldName( IEntityAlias.CREDITOR_ACCOUNT_CREDITOR_ID );
				Integer id = creditor.getId();
				String msg = "acreedor";
				CreditorAccount emptyIAccount = new CreditorAccount();
				checkIAccount(loaded,caBean,alias,creditor,id,msg,emptyIAccount);
			}
			return creditor;
		}
		Creditor creditor = new Creditor();
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getDocumentCountry());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getSecurityLevel());
		creditor.setRegistry(registry);
		creditor.setScope(getScope());
		creditor.setTransaction(loaded.getInvoiceTransactionType());
		bean.insert(creditor);
		if (!StringUtils.isBlank(loaded.getCuenta())) {
			IManagerBean caBean = BeanManager.getManagerBean(CreditorAccount.class);
			Account account = obtainAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			CreditorAccount ca = new CreditorAccount();
			ca.setAccount(account);
			ca.setCreditor(creditor);
			caBean.insert(ca);					
		}
		return creditor;
	}

	private Supplier obtainSupplier(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Supplier.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_STATUS), SupplierStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SUPPLIER_SCOPE_ID), getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un proveedor activo con el número de documento " + loaded.getDocumento());
			}
			Supplier supplier = (Supplier) list.get(0); 
			if (!StringUtils.isBlank(loaded.getCuenta())) {
				IManagerBean caBean = BeanManager.getManagerBean(SupplierAccount.class);
				String alias = caBean.getFieldName( IEntityAlias.SUPPLIER_ACCOUNT_SUPPLIER_ID );
				Integer id = supplier.getId();
				String msg = "proveedor";
				SupplierAccount emptyIAccount = new SupplierAccount();
				checkIAccount(loaded,caBean,alias,supplier,id,msg,emptyIAccount);
			}
			return supplier;
		}
		Supplier supplier = new Supplier();
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getDocumentCountry());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getSecurityLevel());
		supplier.setRegistry(registry);
		supplier.setScope(getScope());
		supplier.setTransaction(loaded.getInvoiceTransactionType());
		supplier = (Supplier) bean.insert(supplier);
		if (!StringUtils.isBlank(loaded.getCuenta())) {
			IManagerBean caBean = BeanManager.getManagerBean(SupplierAccount.class);
			Account account = obtainAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			SupplierAccount ca = new SupplierAccount();
			ca.setAccount(account);
			ca.setSupplier(supplier);
			caBean.insert(ca);					
		}
		return supplier;
	}

	private Customer obtainCustomer(LoadedInvoice loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_DOCUMENT), loaded.getDocumento());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_STATUS), CustomerStatus.ACTIVE);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_SCOPE_ID), getScope().getId());
		List<ITransferObject> list = bean.getList(criteria); 
		if ( list.size() > 0 ) {
			if ( list.size() > 1 ) {
				throw new ManagerBeanException("Existe más de un cliente activo con el número de documento " + loaded.getDocumento());
			}
			Customer customer = (Customer) list.get(0);  
			if (!StringUtils.isBlank(loaded.getCuenta())) {
				IManagerBean caBean = BeanManager.getManagerBean(CustomerAccount.class);
				String alias = caBean.getFieldName( IEntityAlias.CUSTOMER_ACCOUNT_CUSTOMER_ID );
				Integer id = customer.getId();
				String msg = "cliente";
				CustomerAccount emptyIAccount = new CustomerAccount();
				checkIAccount(loaded,caBean,alias,customer,id,msg,emptyIAccount);
			}
			return customer; 
		}
		Customer customer = new Customer();
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getDocumentCountry());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(getSecurityLevel());
		customer.setRegistry(registry);
		customer.setScope(getScope());
		customer.setTransaction(loaded.getInvoiceTransactionType());
		customer = (Customer) bean.insert(customer);
		if (!StringUtils.isBlank(loaded.getCuenta())) {
			IManagerBean caBean = BeanManager.getManagerBean(CustomerAccount.class);
			Account account = obtainAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			CustomerAccount ca = new CustomerAccount();
			ca.setAccount(account);
			ca.setCustomer(customer);
			caBean.insert(ca);					
		}
		return customer; 
	}

	private void checkIAccount(LoadedInvoice loaded,IManagerBean bean,String alias,ITransferObject iRegistry,Integer id,String msg,IAccount emptyIAccount) throws ManagerBeanException {
		Criteria c = new Criteria();
		c.addEqualExpression(alias, id );
		List<ITransferObject> l = bean.getList(c);		
		if ( l.size() > 0 ) {
			IAccount iAccount = (IAccount) l.get(0);
			if (!StringUtils.equals(iAccount.getAccount().getCode(),loaded.getCuenta())) {
				throw new ManagerBeanException("Existe una cuenta cuenta contable vinculada al " + msg +" y no coincide con la indicada." +
												" Grabada '" + iAccount.getAccount().getCode() + "', en el fichero '"+ loaded.getCuenta()+"'");						
			}
		} else {
			Account account = obtainAccount( loaded.getCuenta(), loaded.getRazonSocial() );
			emptyIAccount.setAccount(account);
			emptyIAccount.setLinkedTo(iRegistry);
			bean.insert((ITransferObject) emptyIAccount);					
		}
	}

	private Account obtainAccount(String cuenta, String name) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), cuenta);
		List<ITransferObject> list = bean.getList(criteria);
		if (list.isEmpty()) {
			Account account = new Account();
			account.setCode(cuenta);
			account.setDescription(name);
			return (Account) bean.insert(account);
		}
		return (Account) list.get(0);
	}

	private Series lookForSeries(String serie) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Series.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_CODE), serie);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_ACTIVE), true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SERIES_INVOICE), true);
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.size() > 0 ) {
			Series series = (Series) list.get(0);
			return series;
		}
		throw new ManagerBeanException("La serie de factura " + serie + " no está definida, no está activa o no es de facturas");
	}
	
}
