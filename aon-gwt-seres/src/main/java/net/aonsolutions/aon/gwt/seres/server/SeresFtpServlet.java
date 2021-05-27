package net.aonsolutions.aon.gwt.seres.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
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
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.registry.NoteType;
import com.esferalia.aon.occam.api.model.registry.RegistryNote;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.Delivery;
import com.esferalia.aon.seres.ftp.FtpException;
import com.esferalia.aon.seres.ftp.FtpLoginException;
import com.esferalia.aon.seres.ftp.SeresFtpConnectionProvider;
import com.esferalia.aon.seres.ftp.seres.FtpDeliveryUploadOccamHandler;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess.ResponseMessageType;
import com.esferalia.aon.seres.ftp.seres.FtpStoreProcess.SeresFtpProcessThread;
import com.esferalia.aon.seres.writer.connect.ConnectSaleInvoiceWriter;
import com.esferalia.aon.watson.error.AonCoreException;

import net.aonsolutions.aon.gwt.seres.shared.CommunicationTarget;

@SuppressWarnings("serial")
@WebServlet(name = "SeresFtpServlet", urlPatterns = { "/seres_ftp/*", "/aon_gwt_aio/seres_ftp/*" })
public class SeresFtpServlet extends HttpServlet {

	private static final Logger LOGGER = Logger.getLogger(SeresFtpServlet.class.getName());
	

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
				
				CommunicationTarget target = CommunicationTarget.getEnumByValue(pathInfo[3]);
				switch (target) {
				case OUTCOME_DELIVERY:
					sendDeliveries(domain, userName, req, resp, idLsit);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} finally {
			AonServletUtils.releaseFacesContext();
		}
	}
	
	public void sendDeliveries(Domain domain, String loggedUser, HttpServletRequest req, HttpServletResponse resp, String[] _idList){
		List<Integer> idList = Arrays.asList(_idList).stream().map(o -> Integer.parseInt(o))
				.collect(Collectors.toCollection(LinkedList::new));
		try {
			List<Delivery> list = getDeliveryList(domain, loggedUser, idList);
			if (list != null && list.size() > 0) {
				new FtpDeliveryUploadOccamHandler(domain.getName(), domain.getId(), loggedUser)
						.onEdiFtpTransfer(list.stream().map(o -> (Delivery) o).collect(Collectors.toList()));
			}
		} catch (FtpLoginException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (FtpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
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
	
	private List<Delivery> getDeliveryList(Domain domain, String loggedUser, List<Integer> _idList)
			throws ManagerBeanException {
		Integer[] idList = _idList.toArray(new Integer[_idList.size()]);
		return AON.getDeliveryStream(domain.getName(), domain.getId(), loggedUser, f -> f.getIdProperty().in(idList))
				.collect(Collectors.toList());
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
				FtpStoreProcess fsp = new FtpStoreProcess(DataResponseSource.SERES_INVOICE, this.domain.getName(), this.domain.getId(), this.loggedUser,
						this.ftpRemotePath, this.ftpServer, this.ftpPort, this.ftpUser, this.ftpPassword);
				
				for(Invoice invoice: invoiceList) {
					String referenceCode = invoice.getSeries()+"_"+invoice.getNumber();
					FileOutput output = exportEdiFile(invoice);
					if(output!=null && output.getErrors()!=null && output.getErrors().size()>0){
						for(Exception e: output.getErrors()){
							Fd0Exception fd0 = (Fd0Exception) e;
							LOGGER.log(Level.SEVERE, fd0.getMessage());
						}
						fsp.track(Level.SEVERE, ResponseMessageType.COMMIT, invoice.getId(), referenceCode);
					} else {
						byte[] data = output.getContent();
						fsp.put(invoice.getId(), data, referenceCode);
						fsp.track(Level.INFO, ResponseMessageType.COMMIT, invoice.getId(), referenceCode);
					}
				}
				
				SeresFtpProcessThread thread = fsp.new SeresFtpProcessThread(fsp); 
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
					CustomerEdiSupport ediSupport = new CustomerEdiSupport();
					boolean isInvoicingMainAddress = false;
					try {
						Customer customer = (Customer) BeanManager.getManagerBean(Customer.class).get(invoice.getRegistry().getId());
						ediSupport.init(customer);
						RegistryNote rNote = searchCustomerNote(domain, loggedUser, invoice.getRegistry().getId(), IEdiSupport.SERES_INVOICING_MAIN_ADDRESS);
						isInvoicingMainAddress = rNote!=null && new Boolean(rNote.getComments());
					} catch (ManagerBeanException e) {
						throw new AonCoreException(e.getMessage(), e);
					}
					
					if(ediSupport.isEnabled()){
						Map<String, String> ediCodes =  ediSupport.getEdiCodes(
								invoice.getRegistry(), invoice.getRegistryAddress());
						
						String customerEdiCabeceraCode = ediCodes.get(IEdiSupport.CABECERA);
						String customerEdiPtoEntregaCode = ediCodes.get(IEdiSupport.PTO_ENTREGA);
						String customerEdiFacturaCode = ediCodes.get(IEdiSupport.FACTURA);
						
						Tag packingTag = ediSupport.obtainPackingTagInvoice(
								invoice.getRegistry(),
								invoice.getRegistryAddress());
						if(packingTag!=null && packingTag.getId()!=null){
							String customerPackage = packingTag.getName();
							
							ApplicationParameter param = AppParamUtil.getParameter(AppParam.EDI_COMPANY_CODE);
							String companyEdiCode = param!=null?param.getValue():null;
							
							// writer file
							ConnectSaleInvoiceWriter writer = new ConnectSaleInvoiceWriter();
							Company company = getCompany(invoice.getDomain());
							
							output = writer.createFile(invoice, company, isInvoicingMainAddress, companyEdiCode,
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
		private RegistryNote searchCustomerNote(Domain domain, String loggedUser, Integer customerId, String key) {
			List<RegistryNote> rNotes = AON.getRNoteList(
					domain.getName(),
					domain.getId(),
					loggedUser,
					f -> f.getNoteTypeProperty().eq(NoteType.FACTURAE.value())
							.and(f.getRegistryProperty().eq(customerId))
							.and(f.getDescriptionProperty().eq(key))
							);
			return rNotes!=null && rNotes.size()>0?rNotes.get(0):null;
		}
	}
	

}
