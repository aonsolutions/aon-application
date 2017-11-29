package net.aonsolutions.aon.gwt.seres.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import com.code.aon.customer.CustomerEdiSupport;
import com.code.aon.customer.IEdiSupport;
import com.code.aon.file.format.model.Fd0Exception;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.file.seres.util.ftp.FtpException;
import com.esferalia.aon.file.seres.util.ftp.FtpLoginException;
import com.esferalia.aon.file.seres.util.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.file.seres.util.writer.connect.ConnectSaleInvoiceWriter;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
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
		
		Map<String, String[]> filterMap = req.getParameterMap();
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
					sendDeliveries(req, resp, idLsit);
					break;
				case OUTCOME_INVOICE:
					sendInvoices(domain, userName, req, resp, idLsit);
					break;
				case INCOME_SALES:
					retrieveSales(req, resp, idLsit);
					break;
				case INCOME_INVOICE:
					retrieveInvoices(req, resp, idLsit);
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

	
	public void sendInvoices(Domain domain, String loggedUser, HttpServletRequest req, HttpServletResponse resp, String[] _idList) {
		List<Integer> idList = Arrays.asList(_idList).stream().map(o -> Integer.parseInt(o))
				.collect(Collectors.toCollection(LinkedList::new));
		try {
			AonServletUtils.initFacesContext(getServletContext(), req, resp);
			List<ITransferObject> list = getInvoiceList(idList);
			if (list != null && list.size() > 0) {
				new FtpSaleInvoiceUploaderHandler(domain, loggedUser)
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
	public void sendDeliveries(HttpServletRequest req, HttpServletResponse resp, String[] _idList){
		
	}
	
	// TODO retrieveInvoices
	public void retrieveInvoices(HttpServletRequest req, HttpServletResponse resp, String[] _idList){
		
	}
	
	// TODO retrieveSales
	public void retrieveSales(HttpServletRequest req, HttpServletResponse resp, String[] _idList){
		
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
		
		private String ftpServer;
		private Integer ftpPort;
		private String ftpUser;
		private String ftpPassword;
		private String ftpRemotePath;
		
		private Domain domain;
		private String loggedUser;
		
		public FtpSaleInvoiceUploaderHandler(Domain domain, String loggedUser) {
			this.domain = domain;
			this.loggedUser = loggedUser;
		}
		
		private void initContext() {
			ApplicationParameter pServer = AppParamUtil.getParameter(PARAM_FTP_SERVER_NAME);
			ApplicationParameter pPort = AppParamUtil.getParameter(PARAM_FTP_PORT);
			ApplicationParameter pUser = AppParamUtil.getParameter(PARAM_FTP_USER);
			ApplicationParameter pPasswd = AppParamUtil.getParameter(PARAM_FTP_PASSWORD);
			ApplicationParameter pPath = AppParamUtil.getParameter(PARAM_FTP_REMOTE_PATH);
			
			if (pServer != null)
				ftpServer = pServer.getValue();
			if (pPort != null && NumberUtils.isNumber(pPort.getValue()))
				ftpPort = Integer.valueOf(pPort.getValue());
			else ftpPort = 21;
			if (pUser != null)
				ftpUser = pUser.getValue();
			if (pPasswd != null)
				ftpPassword = pPasswd.getValue();
			if (pPath != null)
				ftpRemotePath = pPath.getValue();
			else ftpRemotePath = "/";
		}

		private void checkValidLogin() {
			try {
				SeresFtpConnectionProvider.checkLogin(ftpServer, ftpPort, ftpUser, ftpPassword);
			} catch (FtpLoginException e) {
				throw new AonCoreException("SERES: Login rechazado, usuario y/o contraseña incorrecta.", e);
			} catch (FtpException e) {
				throw new AonCoreException("SERES: Error de conexion ftp.", e);
			}
		}
		
		public void onEdiFtpTransfer(List<Invoice> invoiceList) {
			initContext();
			checkValidLogin();
			
			try {
				FtpStoreProcess fsp = new FtpStoreProcess(this.domain.getName(), this.domain.getId(), this.loggedUser);
				
				for(Invoice invoice: invoiceList) {
					FileOutput output = exportEdiFile(invoice);
					if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
						for(Exception e: output.getErrors()){
							Fd0Exception fd0 = (Fd0Exception) e;
							LOGGER.log(Level.SEVERE, fd0.getMessage());
						}
					} else {
						byte[] data = output.getContent();
						String referenceCode = invoice.getSeries()+"_"+invoice.getNumber();
						fsp.put(invoice.getId(), data, referenceCode);
						
						invoiceTracking(this.domain.getName(), this.domain.getId(), this.loggedUser, invoice.getId(), referenceCode);
					}
				}
				
				SeresFtpProcessThread thread = new SeresFtpProcessThread(fsp); 
				thread.start();
			
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
						
						Tag packingTag = ediSuport.obtainPackingTagInvoice(
								invoice.getRegistry(),
								invoice.getRegistryAddress());
						if(packingTag!=null && packingTag.getId()!=null){
							String customerPackage = packingTag.getName();
							
							ApplicationParameter param = AppParamUtil.getParameter(AppParam.EDI_COMPANY_CODE);
							String companyEdiCode = param!=null?param.getValue():null;
							
							// writer file
							ConnectSaleInvoiceWriter writer = new ConnectSaleInvoiceWriter();
							Company company = getCompany(invoice.getDomain());
							
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
		
		private void invoiceTracking(String domainName, int domainId, String user, int invoiceId, String referenceCode) {
			DataResponse dr = AON.getDataResponse(domainName, domainId, user, DataResponseSource.SERES_INVOICE, f->f.getCodeProperty().eq(referenceCode));
			if(dr==null || dr.getId()==null)
				dr = new DataResponse();
			dr.setDomain(domainId);
			dr.setCode(referenceCode);
			dr.setResponseDate(new Date());
			dr.setSource(DataResponseSource.SERES_INVOICE);
			dr.setSourceId(invoiceId);
			dr.setCreationUser(user);
			dr.setCreationDate(new Date());
			if(dr.getId()!=null) {
				Integer drId = dr.getId();
				AON.updateDataResponse(domainName, domainId, user, dr, f->f.getIdProperty().eq(drId));
			} else {
				AON.insertDataResponse(domainName, domainId, user, dr);
			}
		}

		
		
		public class FtpStoreProcess implements ILongProcess {

			private boolean success = false;
			
			private Map<Integer, byte[]> dataMap;
			private Map<Integer, String> referenceCodeMap;
			private String domainName;
			private int domainId;
			private String loggedUser;
			
			public FtpStoreProcess(String domainName, int domainId, String loggedUser) {
				dataMap = new HashMap<>();
				referenceCodeMap = new HashMap<>();
				this.domainName = domainName;
				this.domainId = domainId;
				this.loggedUser = loggedUser;
			}
			public FtpStoreProcess(String domainName, int domainId, String loggedUser, int id, byte[] data, String referenceCode) {
				this(domainName, domainId, loggedUser);
				this.put(id, data, referenceCode);
			}

			public void put(int id, byte[] data, String referenceCode) {
				dataMap.put(id, data);
				referenceCodeMap.put(id, referenceCode);
			}

			@Override
			public void execute() {
				for(Integer id: dataMap.keySet()){
					byte[] data = dataMap.get(id);
					String referenceCode = referenceCodeMap.get(id);
					
					InputStream inputStream = new BufferedInputStream(
							new ByteArrayInputStream(data));
					success = storeFtpFile("factura-" + referenceCode + ".edi",
							inputStream);
					
					log(Level.INFO, "FTP STORE: " + success);
					if (success) {
						log(Level.INFO, "Fichero EDI generado y enviado CORRECTAMENTE.", true, domainName, domainId, loggedUser, id, referenceCode);
					} else {
						log(Level.SEVERE, "El fichero no se ha podido enviar.", true, domainName, domainId, loggedUser, id, referenceCode);
					}
					IOUtils.closeQuietly(inputStream);
				}
			}
			
			private boolean storeFtpFile(String fileName, InputStream inputStream) {
				try {
					return SeresFtpConnectionProvider.storeFile(ftpRemotePath, fileName,
							inputStream, ftpServer, ftpPort, ftpUser, ftpPassword);
				} catch (FtpLoginException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				} catch (FtpException e) {
					LOGGER.log(Level.SEVERE, e.getMessage(), e);
				}
				return false;
			}
			
			private void log(Level level, String message) {
				log(level, message, false, null, 0, null, 0, null);	
			}
			
			private void log(Level level, String message, boolean isTrackable, String domainName, int domainId, String loggedUser, int id, String referenceCode) {
				LOGGER.log(level, message);
				if(isTrackable){
					DataResponse dr = AON.getDataResponse(domainName, domainId, loggedUser, DataResponseSource.SERES_INVOICE, f->f.getCodeProperty().eq(referenceCode));
					if(dr==null || dr.getId()==null) {
						dr = new DataResponse();
						dr.setDomain(domainId);
						dr.setCode(referenceCode);
						dr.setResponseDate(new Date());
						dr.setSource(DataResponseSource.SERES_INVOICE);
						dr.setSourceId(id);
						dr.setCreationUser(loggedUser);
						dr.setCreationDate(new Date());
						AON.insertDataResponse(domainName, domainId, loggedUser, dr);
						dr = AON.getDataResponse(domainName, domainId, loggedUser, DataResponseSource.SERES_INVOICE, f->f.getCodeProperty().eq(referenceCode));
					}
					DataResponseDetail drd = new DataResponseDetail();
					drd.setDataResponse(dr.getId());
					drd.setDomain(domainId);
					drd.setDataVariable("REQUEST");
					drd.setDataValue(level.equals(Level.INFO)?"OK":"FAIL");
					drd.setCreationUser(loggedUser);
					drd.setCreationDate(new Date());
					AON.insertDataResponseDetail(domainName, domainId, loggedUser, drd);
				}
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
