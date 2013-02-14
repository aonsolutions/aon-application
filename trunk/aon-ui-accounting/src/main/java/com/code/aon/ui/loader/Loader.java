package com.code.aon.ui.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ui.loader.pojo.ILoadedPojo;

public class Loader implements ILoaderEngine {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Loader.class.getName());
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/loader/";
	private static final String VM_HELP_TEMPLATE = "help.html.vm";
	private static final String VM_FACTORIES_KEY = "factories";
	private static final String VM_BUNDLE_KEY = "bundle";
	private static final String VM_HELP_BUNDLE = "com.code.aon.ui.loader.help";
	
	private static final String SEMICOLON = ";";
	private static final String EQUALS = "=";
	private static final String METADATA_MARK_0 = "0";
	private static final String METADATA_MARK_1 = "1";
	private static final String SEPARATOR_KEY = "Separador";
	private static final String ENCODING_KEY = "Codificacion";
	private LoaderParams params;
	private LoaderFactoryManager factoryManager;
	private Map<String, Map<String, Integer>> ids;
	private VelocityHelper velocityHelper;
	
	private String encoding = "ISO-8859-1";
	private String sep = "|";
	private Map<String, Column[]> columns;
	
	public Loader(LoaderParams params) {
		this.params = params;
		this.factoryManager = new LoaderFactoryManager(this);
	}
	
	public LoaderFactoryManager getFactoryManager() {
		return factoryManager;
	}
	public void setFactoryManager(LoaderFactoryManager factoryManager) {
		this.factoryManager = factoryManager;
	}

	public LoaderParams getParams() {
		return params;
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

	public Map<String, Map<String, Integer>> getIds() {
		return ids;
	}

	public void loadMetadata(InputStream input) throws AonException {
		log("Comienza la carga de la meta información.");
		columns = new HashMap<String, Column[]>();
		if (input == null) {
			raiseException(0, "La entrada está vacia!");
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
			log("Meta: Metadata loaded!");
			
			if (params.getWorkPlace() == null && getColumns().containsKey( ILoaderFactory.FRA ) ) {
				raiseException(0, "Si se desea cargar facturas, hay que definir el centro de trabajo");
			}
			if (params.getCategory() == null && getColumns().containsKey( ILoaderFactory.FRA ) ) {
				raiseException(0, "Si se desea cargar facturas, hay que definir la categoría");
			}
			if (params.getAccountPeriod() == null && ( getColumns().containsKey( ILoaderFactory.ASI ) || getColumns().containsKey( ILoaderFactory.FRA ) )) {
				raiseException(0, "Si se desea cargar asientos contables o facturas , hay que definir el ejercicio contable");
			}
			
			
		} catch (UnsupportedEncodingException e) {
			raiseException(0, "La codificación no es válida");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}
	
	public void log( String msg  ) {
		LogPanelController logger = LogPanelController.getInstance();
		logger.info(msg);
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
		if (!getFactoryManager().accept( entity )) {
			raiseException(line, "La entidad " + entity+" no está soportada.");
		}
		log("Meta: Found " + entity + " entity.");
		ILoaderFactory<?> factory = getFactoryManager().getFactory(entity);
		if (getColumns().containsKey(factory.getKey())) {
			raiseException(line, "Ya existe una definición para el tipo " + entity+".");
		}
		String cols = StringUtils.substringAfter(subLine, getSep());
		String[] columns = StringUtils.split(cols, getSep());
		List<Column> inputColumns = new LinkedList<Column>();
		for (String column : columns) {
			boolean added = false;
			for (Column c : factory.getSupportedColumns()){
				if (StringUtils.equals(column, c.getName())) {
					log("\t Meta: Encontrada la columna '" + c.getName() + "'.");
					inputColumns.add(c);
					added = true;	
				}
			}
			if (!added) {
				raiseException(line, "La columna " + column + " no está soportada.");
			}
		}
		getColumns().put(entity, inputColumns.toArray(new Column[inputColumns.size()]));
		log("Meta: Entidad " + entity + " registrada.");
	}
	
	public void validate(InputStream input) throws AonException {
		try {
			int errors = 0;
			int warnings = 0;
			log("Comienza la validación de formato!");
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
						if (!getFactoryManager().accept( entity )) {
							raiseException(i, "La entidad " + entity+" no está soportada.");
						}
						try {
							getFactoryManager().validate( params );
						} catch (AonException e) {
							raiseException(i, e.getMessage());
						}
						ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(entity);
						ILoadedPojo loaded = factory.getTargetBean();
						parseLine(i,loaded,getColumns().get(entity),StringUtils.substringAfter(line, getSep()));
						PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
						for (Column c : getColumns().get(entity)) {
							try {
								if (c.hasValidation()) {
									Object data = propertyUtilsBean.getProperty(loaded, c.getName());
									String msg = c.validate( data ); 
									if ( msg != null) {
										log("VALIDACIÓN: Línea " + i +": " + msg);
									}
								}
							} catch (IllegalAccessException e) {
								raiseException(i, e.getMessage());
							} catch (InvocationTargetException e) {
								raiseException(i, e.getMessage());
							} catch (NoSuchMethodException e) {
								raiseException(i, e.getMessage()
							);							}
						}
					} 
				}
				 
			}
			reader.close();				
			log(" Finalizada la validación de formato!");
			log(" " + errors + " errores, " + warnings + " avisos");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		}
	}
	
	public void load(InputStream input,Session session) throws AonException {
		int i = 0;
		getParams().setBytesRead(0L);
		try {
			ids = new HashMap<String, Map<String,Integer>>();
			int errors = 0;
			int warnings = 0;
			log(" Comienza la carga de datos!");
			InputStreamReader inputReader = new InputStreamReader(input,getEncoding());
			LineNumberReader reader = new LineNumberReader(inputReader);
			while (reader.ready()) {
				++i;
				String line = reader.readLine();
				getParams().addBytes( line.length() );
				if (!StringUtils.isBlank(line)) {
					String mark = line.substring(0,1);
					if (!METADATA_MARK_0.equals(mark) && !METADATA_MARK_1.equals(mark) ) {
						String entity = StringUtils.substringBefore(line, getSep());
						if (!getFactoryManager().accept( entity )) {
							raiseException(i, "La entidad " + entity+" no está soportada.");
						}
						ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(entity);
						ILoadedPojo loaded = factory.getTargetBean();
						parseLine(i,loaded,getColumns().get(entity),StringUtils.substringAfter(line, getSep()));
						try {
							insertAonEntity(params, loaded);
						} catch (AonException e){
							e.printStackTrace();
							raiseException(i, e.getMessage());
						}
						if (i%50 == 0) {
							log("" + i + " líneas insertadas");
						}
						if (i%10 == 0) {
							session.flush();
							session.clear();
						}
					} else {
						log ( "Skipping metadata line " + i);
					}
					
				}
				 
			}
			reader.close();
			log("" + i + " líneas insertadas");
			LOGGER.info("Carga de datos finalizada!");
			log("Carga de datos finalizada!");
			log(" " + errors + " errores, " + warnings + " avisos");
		} catch (IOException e) {
			log(e.getMessage());
			raiseException(0, "Se produjo un error de entrada/salida");
		} catch (Throwable e) {
			e.printStackTrace();
			log(e.getMessage());
			raiseException(i, e.getMessage());
		}
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
					if (c.getType() == 3 && data == null) {
						// Para evitar la excepcion "No value specified for 'Date'"
						// que lanza BeanUtils para las fecha nulas.
					} else {
						BeanUtils.setProperty(target, c.getName(), data);
					}
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
			Date date = getParams().getDateFormatter().parse(data);
			String ensure = getParams().getDateFormatter().format(date);
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

	private void cacheId(String key, String identifier, Integer id) {
		Map<String, Integer> entityIds = getIds().get(key);
		if (entityIds == null) {
			entityIds = new HashMap<String, Integer>();
			getIds().put(key,entityIds);	
		}
		if (StringUtils.isBlank(identifier)) {
			identifier = Integer.toString(id);
		}
		entityIds.put(identifier, id);
	}
	
	private Integer getAonId(String key, String identifier) {
		Map<String, Integer> entityIds = getIds().get(key);
		if (entityIds != null) {
			return entityIds.get(identifier);
		}
		return null;
	}

	@Override
	public ITransferObject ensureAonEntity(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(loadedPojo.getClass());
		String key = factory.getKey();
		String identifier = loadedPojo.getIdentifier();
		ITransferObject to = getAonEntity(key, identifier);
		if (to == null) {
			to = factory.get(params, loadedPojo);
			if (to == null) {
				Integer id = insertAonEntity(params, loadedPojo);
				to = getAonEntity(key, StringUtils.isBlank(identifier)?Integer.toString(id):identifier);
			}
		}
		return to;
	}

	@Override
	public ITransferObject get(String key, Integer aonId) throws AonException {
		if (aonId != null) {
			ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(key);
			return factory.get(aonId);
		}
		return null;
	}

	@Override
	public ITransferObject getAonEntity(String key, String identifier) throws AonException {
		if (StringUtils.isNotBlank(identifier)) {
			Integer id = getAonId(key, identifier);
			if (id != null) {
				ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(key);
				return factory.get(id);
			}
		}
		return null;
	}

	@Override
	public Integer insertAonEntity(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(loadedPojo.getClass());
		Integer id = factory.insert(getParams(), loadedPojo );
		if (id != null) {
			cacheId(factory.getKey(), loadedPojo.getIdentifier(), id );	
		}
		return id;
	}

	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		ILoaderFactory<ILoadedPojo> factory = getFactoryManager().getFactory(loadedPojo.getClass());
		return factory.get(params,loadedPojo);
	}
	
	public void help(Writer output) {
		try {
			processTemplate(output);
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
			throw new AbortProcessingException(e.getMessage());
		}
	}
	

	private VelocityHelper getVelocityHelper() {
		if ( this.velocityHelper == null ) {
			this.velocityHelper = new VelocityHelper();
			try {
				this.velocityHelper.init( VM_PATH_DEFAULT );
			} catch (Exception e) {
				LOGGER.error( "Velocity engine could not be initialized", e );
			}
		}
		return this.velocityHelper;
	}
	
	private void processTemplate(Writer output) throws IOException, AonException {
		TemplateHelper th = getVelocityHelper().getTemplateHelper();
		ResourceBundle bundle = ResourceBundle.getBundle(VM_HELP_BUNDLE);
		th.putInContext( VM_BUNDLE_KEY, bundle );
		th.putInContext( VM_FACTORIES_KEY, getFactoryManager().getFactories() );
		th.processTemplate(VM_HELP_TEMPLATE, output);
	}

}
