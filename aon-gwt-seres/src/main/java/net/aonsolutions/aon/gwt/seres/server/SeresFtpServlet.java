package net.aonsolutions.aon.gwt.seres.server;

import static com.code.aon.customer.IEdiSupport.ACTIVE;
import static com.code.aon.customer.IEdiSupport.ALBARANES;
import static com.code.aon.customer.IEdiSupport.CABECERA;
import static com.code.aon.customer.IEdiSupport.EDI_CODES_PATTERN;
import static com.code.aon.customer.IEdiSupport.EDI_PACKING_PATTERN;
import static com.code.aon.customer.IEdiSupport.FACTURA;
import static com.code.aon.customer.IEdiSupport.FINANCIERA;
import static com.code.aon.customer.IEdiSupport.MEDIDA;
import static com.code.aon.customer.IEdiSupport.PEDIDOS;
import static com.code.aon.customer.IEdiSupport.PTO_ENTREGA;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.json.JSONObject;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.company.Company;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Tag;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.IEdiSupport;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryNote;
import com.code.aon.registry.enumeration.NoteType;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.util.ftp.FtpException;
import com.esferalia.aon.file.seres.util.ftp.FtpLoginException;
import com.esferalia.aon.file.seres.util.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.file.seres.util.writer.connect.ConnectSaleInvoiceWriter;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.error.AonCoreException;

@SuppressWarnings("serial")
@WebServlet(name = "SeresFtpServlet", urlPatterns = { "/seres_ftp/*", "/aon_gwt_aio/seres_ftp/*" })
public class SeresFtpServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SeresFtpServlet.class.getName());
	
	final String OUTCOME_DELIVERY = "outcome_delivery";
	final String OUTCOME_INVOICE = "outcome_invoice";
	final String INCOME_SALES = "income_sales";
	final String INCOME_INVOICE = "income_invoice";
	final String INGENET_DELIVERY = "ingenet_delivery";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Seres FTP Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1];
		String userName = pathInfo[2];
		String md5 = Utils.getMd5(userName + domainName);
		
		Map<String, String[]> filterMap = req.getParameterMap(); filterMap.size();filterMap.keySet();
		String[] idLsit = null;
		if (filterMap.containsKey("id_list")) {
			idLsit = filterMap.get("id_list");
		}
		
		if (accessToken.equals(md5)) {
			Domain domain = AON.getDomain(domainName, 1, userName, f -> f.getNameProperty().eq(domainName));
			if (pathInfo.length > 3) {
				Object object = new Object();
				JSONObject meta = new JSONObject();
				
				switch (pathInfo[3]) {
				case OUTCOME_DELIVERY:
					sendDeliveries(req, resp);
					break;
				case OUTCOME_INVOICE:
					sendInvoices(req, resp, idLsit);
					break;
				case INCOME_SALES:
					retrieveSales(req, resp);
					break;
				case INCOME_INVOICE:
					retrieveInvoices(req, resp);
					break;
				case INGENET_DELIVERY:
//					object = getIngenetDelivery(domain, userName, req);
					break;
				default:
					break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Seres Servlet - POST METHOD");
	}

	
	public void sendInvoices(HttpServletRequest req, HttpServletResponse resp, String[] _idList) {
		List<Integer> idList = Arrays.asList(_idList).stream().map(o -> Integer.parseInt(o))
				.collect(Collectors.toCollection(LinkedList::new));
		;
		try {
			AonServletUtils.initFacesContext(getServletContext(), req, resp);
			List<ITransferObject> list = getInvoiceList(idList);
			if (list != null && list.size() > 0) {
				new FtpSaleInvoiceUploaderHandler()
						.onEdiFtpTransfer(list.stream().map(o -> (Invoice) o).collect(Collectors.toList()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} finally {
			AonServletUtils.releaseFacesContext();
		}
	}
	
	// TODO sendDeliveries
	public void sendDeliveries(HttpServletRequest req, HttpServletResponse resp){
		
	}
	
	// TODO retrieveInvoices
	public void retrieveInvoices(HttpServletRequest req, HttpServletResponse resp){
		
	}
	
	// TODO retrieveSales
	public void retrieveSales(HttpServletRequest req, HttpServletResponse resp){
		
	}
	
	
	
	private List<ITransferObject> getInvoiceList(Collection<Integer> idList) throws ManagerBeanException{
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.code.aon.finance.Invoice.class);
		Criteria criteria = new Criteria();
		criteria.addInExpression(
				beanManager.getFieldName(IEntityAlias.INVOICE_ID),
				idList );
		return beanManager.getList(criteria);
	}
	
	
	// /////////////////// 
	// SERVLET UTILS
	// ///////////////////
	static interface MSG {
		final static String ACCESS_TOKEN = "access_token";
		final static String CALLBACK = "callback";
	}
	
	static class Utils {
	
		public static String getMd5(String str){
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
	        md.update(str.getBytes());
	        byte byteData[] = md.digest();
	
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	        	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	        
	        return sb.toString();
		}
		
		public static void giveBack(HttpServletRequest req, HttpServletResponse resp,
				Object object, JSONObject meta) {
			try {
				String js = req.getParameter(MSG.CALLBACK);
				if(js != null){
					resp.setContentType("application/javascript; charset=utf-8");     
					PrintWriter out = resp.getWriter();
					out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
					out.flush();
				} else {
					resp.setContentType("application/json");     
					PrintWriter out = resp.getWriter();
					out.print(object);
					out.flush();
				}
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		}
	
	}
	
	// //////////////////////
	// FTP 
	// //////////////////////
	public class FtpSaleInvoiceUploaderHandler implements Serializable {
		
		private final String PARAM_FTP_SERVER_NAME = "SERES_FTP_SERVER_NAME";
		private final String PARAM_FTP_PORT = "SERES_FTP_SERVER_PORT";
		private final String PARAM_FTP_USER = "SERES_FTP_USER";
		private final String PARAM_FTP_PASSWORD = "SERES_FTP_PASSWORD";
		private final String PARAM_FTP_REMOTE_PATH = "SERES_FTP_PATH_PUSH_INVOICE";
		
		private boolean showEdiFtpWindow;
		
		private String server;
		private Integer port;
		private String user;
		private String password;
		private String remotePath;
		
		private boolean showFtpServerConnectionData;

		public boolean isShowEdiFtpWindow() {
			return showEdiFtpWindow;
		}

		public void setShowEdiFtpWindow(boolean showEdiFtpWindow) {
			this.showEdiFtpWindow = showEdiFtpWindow;
		}

		public String getServer() {
			return server;
		}

		public void setServer(String server) {
			this.server = server;
		}

		public Integer getPort() {
			return port;
		}

		public void setPort(Integer port) {
			this.port = port;
		}

		public String getUser() {
			return user;
		}

		public void setUser(String user) {
			this.user = user;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getRemotePath() {
			return remotePath;
		}

		public void setRemotePath(String remotePath) {
			this.remotePath = remotePath;
		}

		public boolean isShowFtpServerConnectionData(){
			return showFtpServerConnectionData;
		}
		
		private void initContext() {
			ApplicationParameter pServer = AppParamUtil.getParameter(PARAM_FTP_SERVER_NAME);
			ApplicationParameter pPort = AppParamUtil.getParameter(PARAM_FTP_PORT);
			ApplicationParameter pUser = AppParamUtil.getParameter(PARAM_FTP_USER);
			ApplicationParameter pPasswd = AppParamUtil.getParameter(PARAM_FTP_PASSWORD);
			ApplicationParameter pPath = AppParamUtil.getParameter(PARAM_FTP_REMOTE_PATH);
			
			if (pServer != null)
				server = pServer.getValue();
			if (pPort != null && NumberUtils.isNumber(pPort.getValue()))
				port = Integer.valueOf(pPort.getValue());
			else 
				port = 21;
			if (pUser != null)
				user = pUser.getValue();
			if (pPasswd != null)
				password = pPasswd.getValue();
			if (pPath != null)
				remotePath = pPath.getValue();
			else 
				remotePath = "/";
		}

		private void checkValidLogin() {
			try {
				showFtpServerConnectionData = false;
				SeresFtpConnectionProvider.checkLogin(server, port, user, password);
			} catch (FtpLoginException e) {
//				showFtpServerConnectionData = true;
//				AonUtil.addErrorMessage(e.getMessage());
			} catch (FtpException e) {
//				showFtpServerConnectionData = true;
//				AonUtil.addErrorMessage(e.getMessage());
			}
		}
		

		public void onEdiFtpTransfer(List<Invoice> invoiceList) {
			initContext();
			checkValidLogin();
			
			try {
				FtpStoreProcess fsp = new FtpStoreProcess();
				
				invoiceList.forEach(invoice -> {
					FileOutput output = exportEdiFile(invoice);
					if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
						for(Exception e: output.getErrors()){
							Fd0Exception fd0 = (Fd0Exception) e;
							LOGGER.log(Level.SEVERE, fd0.getMessage());
						}
					} else {
						byte[] data = output.getContent();
						String referenceCode = invoice.getSeries()+"_"+invoice.getNumber();
						fsp.add(data, referenceCode);
					}
				});
				
				SeresFtpProcessThread thread = new SeresFtpProcessThread(fsp); 
				thread.start();
				// TODO: mark this invoice as sended
			
			} catch (Throwable e) {
				throw new AonCoreException(e.getMessage(), e);
			}
		}
		
		public void onEdiFtpTransfer(Invoice invoice) {
			initContext();
			checkValidLogin();
			
			FileOutput output = null;
			try {
				output = exportEdiFile(invoice);
				if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
					for(Exception e: output.getErrors()){
						Fd0Exception fd0 = (Fd0Exception) e;
						LOGGER.log(Level.SEVERE, fd0.getMessage());
					}
				} else {
					// upload file
					byte[] data = output.getContent();
					String referenceCode = invoice.getSeries()+"_"+invoice.getNumber();
					FtpStoreProcess sdp = new FtpStoreProcess(data, referenceCode);
					SeresFtpProcessThread thread = new SeresFtpProcessThread(sdp); 
					thread.start();
					
					// TODO: mark this invoice as sended 
				}
			} catch (Throwable e) {
				throw new AonCoreException(e.getMessage(), e);
			}
		}
		
		public FileOutput exportEdiFile(Invoice invoice){
			FileOutput output = null;
			try {
				RegistryAddress raddress = invoice.getRegistryAddress();
				if (raddress != null && raddress.getId() != null) {
					CustomerEdiSupport ediSuport = new CustomerEdiSupport();
					try {
						Customer customer = (Customer) BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
						ediSuport.init(customer);
					} catch (ManagerBeanException e) {
						throw new AonCoreException(e.getMessage(), e);
					}
					
					if(ediSuport.isEnabled()){
						Map<String, String> ediCodes =  ediSuport.getEdiCodes(
								invoice.getRegistry(), invoice.getRegistryAddress());
						
						String customerEdiCabeceraCode = ediCodes.get(IEdiSupport.CABECERA);
						String customerEdiPtoEntregaCode = ediCodes.get(IEdiSupport.PTO_ENTREGA);
						String customerEdiFacturaCode = ediCodes.get(IEdiSupport.FACTURA);
						
						Tag packingTag = ediSuport.obtainPackingTag(
								invoice.getRegistry(),
								invoice.getRegistryAddress());
						if(packingTag!=null && packingTag.getId()!=null){
							String customerPackage = packingTag.getName();
							
							ApplicationParameter param = AppParamUtil.getParameter(AppParam.EDI_COMPANY_CODE);
							String companyEdiCode = param!=null?param.getValue():null;
							
							// writer file
							ConnectSaleInvoiceWriter writer = new ConnectSaleInvoiceWriter();
							Company company = getCompany(invoice.getDomain());
							company.getId();
							output = writer.createFile(invoice, company, companyEdiCode,
									customerEdiCabeceraCode, customerEdiPtoEntregaCode, customerEdiFacturaCode,
									customerPackage);
						} else {
							throw new AonCoreException("No se ha definido el envase para 'mensajería EDI'");
						}
					} else {
						throw new AonCoreException("El cliente no tiene el soporte EDI habilitado");	
					}
					return output;
				} else {
					throw new AonCoreException("La factura no tiene direccion.");
				}
			} catch (IOException e) {
				throw new AonCoreException(e.getMessage(), e);
			}
		}
		
		private Company getCompany(Integer domainId) {
			try {
				IManagerBean bean = BeanManager.getManagerBean(Company.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COMPANY_DOMAIN), domainId);
				criteria.addOrder(bean.getFieldName(IEntityAlias.COMPANY_ID), false);
				List<ITransferObject> list = bean.getList(criteria);
				if (!list.isEmpty()) {
					return (Company) list.get(0);
				}
			} catch (ManagerBeanException e) {
				 LOGGER.log(Level.SEVERE, e.getMessage(), e);
				 throw new AonCoreException("No se han podido obtener los datos de empresa");
			}
			return null;
		}


		public class CustomerEdiSupport {
			private Map<Integer, List<String>> addressCodes;
			private List<RegistryAddress> customerAddresses;
			private boolean enabled;
			
			public boolean isEnabled(){
				return enabled;
			}
			
			private void init(Customer customer) throws ManagerBeanException {
				this.customerAddresses = this.getAddresses(customer);
				this.addressCodes = new HashMap<Integer, List<String>>();
				
				RegistryNote active = this.getRegistryNote(ACTIVE, customer.getId());
				enabled = active != null && new Boolean(active.getComments());
				
				this.customerAddresses.forEach(
						address -> {
							addressCodes.put(
									address.getId(),
									getAddressCodes(this.obtainRegistryNote(address.getId(),
											customer)));
						});
			}
			
			private List<RegistryAddress> getAddresses(Customer customer) {
				try {
					IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(
							registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID),
							customer.getId());
					criteria.addOrder(registryAddressBean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS));
					return (List) registryAddressBean.getList(criteria);
				} catch (ManagerBeanException e) {
					 LOGGER.info(e.getMessage());
				}
				return null;
			}
			
			private List<String> getAddressCodes(RegistryNote registryNote) {
				String value = null;
				if (registryNote != null) {
					value = registryNote.getComments();
				}
				String[] values = { "", "", "", "", "", "", "" };
				Matcher m;

				Pattern p1 = Pattern.compile(EDI_CODES_PATTERN);
				if (value != null && (m = p1.matcher(value)).find()) {
					values[0] = m.group(1);
					values[1] = m.group(2);
					values[2] = m.group(3);
					values[3] = m.group(4);
					values[4] = m.group(5);
					values[5] = m.group(6);
				}
				Pattern p2 = Pattern.compile(EDI_PACKING_PATTERN);
				if (value != null && (m = p2.matcher(value)).find()) {
					values[6] = m.group(1);
				}
				return Arrays.asList(values);
			}
			
			private RegistryNote obtainRegistryNote(Integer addressId, Customer customer) {
				RegistryNote note = this.getRegistryNote(addressId.toString(), customer.getId());
				if (note == null) {
					note = getEmptyNote(customer.getRegistry(), addressId.toString());
				}
				return note;
			}

			private RegistryNote getEmptyNote(Registry registry, String key) {
				RegistryNote note = new RegistryNote();
				note.setNoteDate(new Date());
				note.setRegistry(registry);
				note.setNotetype(NoteType.FACTURAE);
				note.setDescription(key);
				return note;
			}

			private RegistryNote getRegistryNote(String key, Integer registryId) {
				RegistryNote note = null;
				try {
					IManagerBean bean = BeanManager.getManagerBean(RegistryNote.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_REGISTRY_ID), registryId);
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_NOTETYPE),
							NoteType.FACTURAE);
					criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_NOTE_DESCRIPTION), key);
					List<ITransferObject> list = bean.getList(criteria);
					if (!list.isEmpty()) {
						note = (RegistryNote) list.get(0);
					}
				} catch (ManagerBeanException e) {
					 LOGGER.info(e.getMessage());
				}
				return note;
			}
			
			public Map<String, String> getEdiCodes(Registry registry, RegistryAddress address){
				RegistryNote rnote = this.getRegistryNote(address.getId().toString(),
						registry.getId());
				String value = null;
				if (rnote != null) {
					value = rnote.getComments();
				}
				Map<String, String> values = new HashMap<>();
				Matcher m;
				Pattern p = Pattern.compile(EDI_CODES_PATTERN);
				if (value != null && (m = p.matcher(value)).find()) {
					values.put(CABECERA, m.groupCount()>0 ? m.group(1) : null);
					values.put(PEDIDOS, m.groupCount()>1 ? m.group(2) : null);
					values.put(PTO_ENTREGA, m.groupCount()>2 ? m.group(3) : null);
					values.put(FACTURA, m.groupCount()>3 ? m.group(4) : null);
					values.put(FINANCIERA, m.groupCount()>4 ? m.group(5) : null);
					values.put(ALBARANES, m.groupCount()>5 ? m.group(6) : null);
					values.put(MEDIDA, m.groupCount()>6 ? m.group(7) : null);
				}
				return values;
			}
			
			public Tag obtainPackingTag(Registry registry, RegistryAddress address) {
				RegistryNote rNote = this.getRegistryNote(address.getId().toString(),
						registry.getId());
				String value = null;
				if (rNote != null) {
					value = rNote.getComments();
				}
				Matcher m;
				Pattern p = Pattern.compile(MEDIDA + "=([^;]*);");
				try {
					if (value != null && (m = p.matcher(value)).find()) {
						IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
						return (Tag) tagBean.get(Integer.valueOf(m.group(1)));
					}
				} catch (ManagerBeanException e) {
					LOGGER.info(e.getMessage());
				} catch (NumberFormatException e) {
					LOGGER.info(e.getMessage());
				}
				return null;
			}
			
		}
		
		
		public class FtpStoreProcess implements ILongProcess {

			private boolean success = false;
			
			private List<byte[]> dataList;
			private List<String> referenceCodeList;
			
			public FtpStoreProcess() {
				dataList = new ArrayList<>();
				referenceCodeList = new ArrayList<>();
			}
			public FtpStoreProcess(byte[] data, String referenceCode) {
				this();
				if(data!=null) this.add(data, referenceCode);
			}

			public void add(byte[] data, String referenceCode) {
				dataList.add(data);
				referenceCodeList.add(referenceCode);
			}

			@Override
			public void execute() {
				for(int i=0; i<dataList.size(); i++){
					InputStream inputStream = new BufferedInputStream(
							new ByteArrayInputStream(dataList.get(i)));
					success = storeFtpFile("factura-" + referenceCodeList.get(i) + ".edi",
							inputStream);
					IOUtils.closeQuietly(inputStream);
				}
				
				LOGGER.info("FTP STORE: " + success);
				if (success) {
					LOGGER.info("Fichero EDI generado y enviado CORRECTAMENTE.");
				} else {
					LOGGER.info("El fichero no se ha podido enviar.");
				}
			}
			
			private boolean storeFtpFile(String fileName, InputStream inputStream) {
				try {
					return SeresFtpConnectionProvider.storeFile(remotePath, fileName,
							inputStream, server, port, user, password);
				} catch (FtpLoginException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				} catch (FtpException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
				return false;
			}

		}
	}
	public interface ILongProcess {
		void execute();		
	}
	public class SeresFtpProcessThread implements Runnable {

		private ILongProcess longProcess;
		private Thread thread;
	    
	    public SeresFtpProcessThread(ILongProcess longProcess) {
	         this.longProcess = longProcess;
	    }

		public void start() {
	         thread = new Thread(this);
	         thread.start();
	    }
		
		public void interrupt() {
			if(thread!=null){
				thread.interrupt();
			}
		}
		
		public boolean isTerminated(){
			return thread.getState()==Thread.State.TERMINATED;
		}
			
		@Override
		public void run() {
	        if (thread != null) {
	    		try {
	    			longProcess.execute();
	    		} finally {
	    		}
	        }
		}

	}
	

}
