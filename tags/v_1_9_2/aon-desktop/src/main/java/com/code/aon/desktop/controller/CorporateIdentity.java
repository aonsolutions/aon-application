package com.code.aon.desktop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.velocity.TemplateHelper;
import com.code.aon.common.velocity.VelocityHelper;
import com.code.aon.company.Company;
import com.code.aon.desktop.report.IdentityReport;
import com.code.aon.desktop.utils.identity.n2t;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.util.AonUtil;

/**
 * @author igayarre
 *
 */
public class CorporateIdentity implements ICollectionProvider{

	private IdentityReport identityReport;

	public IdentityReport getIdentityReport() {
		return identityReport;
	}

	public void setIdentityReport(IdentityReport identityReport) {
		this.identityReport = identityReport;
	}

	public CorporateIdentity()  {
		identityReport = new IdentityReport();
        try {
        	Company company = recoverCompany();
        	if (company!=null){
        		identityReport.setCompany(company);
        		identityReport.setAddress(recoverCompanyAddress(company));
        		identityReport.setCellular(recoverCompanyMediasString(company,MediaType.CELLULAR));
        		identityReport.setEmail(recoverCompanyMediasString(company,MediaType.EMAIL));
        		identityReport.setFax(recoverCompanyMediasString(company,MediaType.FAX));
        		identityReport.setPhone(recoverCompanyMediasString(company,MediaType.FIXED_PHONE));
        		identityReport.setWeb(recoverCompanyMediasString(company,MediaType.WEB));
        	}
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
    public void onInit(ActionEvent event) throws ManagerBeanException{
    	identityReport.setFax_to("");
    	identityReport.setFax_from("");
    	identityReport.setFax_subject("");
    	identityReport.setFax_content("");
    	identityReport.setLetter_to("");
    	identityReport.setLetter_from("");
    	identityReport.setLetter_content("");
    	identityReport.setPagare_num("");
    	identityReport.setPagare_de("");
    	identityReport.setPagare_cantidad("");
    	identityReport.setPagare_cantidad_num("");
    	identityReport.setPagare_fecha_dia("");
    	identityReport.setPagare_fecha_mes("");
    	identityReport.setPagare_fecha_ano("");
    }

	private Company recoverCompany() throws ManagerBeanException{
    	IManagerBean bean = BeanManager.getManagerBean(Company.class);
        List list = bean.getList(null);
        if (list.size() > 0) {
            return (Company)list.get(0);
        }
        return null;
	}

	private RegistryAddress recoverCompanyAddress(Company company) throws ManagerBeanException{
    	IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE), AddressType.MAIN);
    	List list = bean.getList(criteria);
    	if (list.isEmpty())
    		return null;
    	return (RegistryAddress)list.iterator().next();
	}

	private String recoverCompanyMediasString(Company company, MediaType type_) throws ManagerBeanException{
		String data_ = new String();
		List<ITransferObject> list = recoverCompanyMedias(company, type_);
		for (ITransferObject transferObject : list) {
			data_ += ((RegistryMedia)transferObject).getValue();
			data_ += " ";
		}
		return data_;
	}

	private List<ITransferObject> recoverCompanyMedias(Company company, MediaType type_) throws ManagerBeanException{
    	IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_REGISTRY_ID), company.getId());
    	criteria.addEqualExpression(bean.getFieldName(IRegistryAlias.REGISTRY_MEDIA_MEDIA_TYPE), type_);
        return bean.getList(criteria);
	}

	public Collection getCollection() {
		List<IdentityReport> list = new LinkedList<IdentityReport>();
		list.add(identityReport);
		return list;
	}

	public Collection getCollection(boolean arg0) throws ManagerBeanException {
		// TODO Auto-generated method stub
		return null;
	}

	public InputStream getAttachAsInputStream() throws IOException, ManagerBeanException{
		File file = File.createTempFile("image", ".tmp");
		RegistryAttachment attach = obtainCompanyLogo();
		if(attach != null){
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(attach.getData());
			outputStream.close();
			return new FileInputStream(file);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private RegistryAttachment obtainCompanyLogo() throws ManagerBeanException {
		IManagerBean registryAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(registryAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), identityReport.getCompany().getId());
		Iterator iter = registryAttachBean.getList(criteria).iterator();
		if(iter.hasNext()){
			return (RegistryAttachment)iter.next();
		}
		return null;
	}
	
	private static final String VM_PATH_DEFAULT = "com/code/aon/ui/desktop/report/";

	private static final String PRINT_TEMPLATE = "print_fax.html.vm";

    public void onPrintFax(ActionEvent event) throws ManagerBeanException{
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		try {
			response.setContentType(MimeType.MIME_HTML.getName());
			Writer out = new OutputStreamWriter( response.getOutputStream() );
			VelocityHelper velocityHelper = new VelocityHelper();
			try {
				velocityHelper.init( VM_PATH_DEFAULT );
			} catch (Exception e) {
				System.out.println(e);
			}
			TemplateHelper th = velocityHelper.getTemplateHelper();
			Locale locale = AonUtil.getCurrentLocale();
			ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.desktop.i18n.messages", locale);	
			th.putInContext("toLiteral", bundle.getString("aon_corporate_identity_to"));
			th.putInContext("fromLiteral", bundle.getString("aon_corporate_identity_from"));
			th.putInContext("subjectLiteral", bundle.getString("aon_corporate_identity_subject"));
			th.putInContext("contextPath", context.getExternalContext().getRequestContextPath());
			SimpleDateFormat df = new SimpleDateFormat("EEE, dd/MM/yy-HH:mm");
			th.putInContext("nowDate", df.format(new Date()));
			th.putInContext("addressStr", identityReport.getAddressStr());
			th.putInContext("phone", identityReport.getPhone());
			th.putInContext("fax", identityReport.getFax());
			th.putInContext("email", identityReport.getEmail());
			th.putInContext("web", identityReport.getWeb());
			th.putInContext("fax_to", identityReport.getFax_to());
			th.putInContext("fax_from", identityReport.getFax_from());
			th.putInContext("fax_subject", identityReport.getFax_subject());
			th.putInContext("fax_content", identityReport.getFax_content());
			th.processTemplate(PRINT_TEMPLATE, out);
			response.flushBuffer();
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		} catch (AonException e) {
			System.out.println(e);
		} 	
		context.responseComplete();    	
    }
	
    public void onN2T(ActionEvent event){
    	try{
			String res;
			n2t numero;
			String num = identityReport.getPagare_cantidad_num();
			if (num.lastIndexOf(".")!= -1){
				String str1 = num.substring(0,num.lastIndexOf("."));
				String str2 = num.substring(num.lastIndexOf(".")+1);
		        int num_ = Integer.parseInt(str1);
		        int dec_ = Integer.parseInt(str2);
		        numero = new n2t();
		        res = numero.convertirLetras(num_);
		        res += " con ";
		        res += numero.convertirLetras(dec_);
			}else{
		        int num_ = Integer.parseInt(num);
		        numero = new n2t();
		        res = numero.convertirLetras(num_);
			}
			identityReport.setPagare_cantidad(res);
    	}catch (Exception e) {
		}
    }
    
}
