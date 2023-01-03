package com.esferalia.aon.gwt.fiscal.server;

import java.util.LinkedList;
import java.util.stream.Collectors;

import javax.servlet.annotation.WebServlet;

import com.esferalia.aon.gwt.common.server.AonStatelessRemoteServiceServlet;
import com.esferalia.aon.gwt.fiscal.client.FiscalMSService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.CompanyBank;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.registry.Creditor;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Aon MS Fiscal Servlet", urlPatterns = { "/aon_gwt_fiscal/ms/Fiscal" })
public class FiscalMSServiceImpl extends AonStatelessRemoteServiceServlet implements FiscalMSService {

	private static final long serialVersionUID = 4908377540728384390L;

	// -------------------------------------------------------------- COMPANY
	@Override
	public LinkedList<CompanyBank> getCompanyBanks(String domainName, String user, int domain) throws AonCoreException {
		return AON.getCompanyBanks(domainName, domain, user);
	}

	// -------------------------------------------------------------- CREDITOR
	@Override
	public LinkedList<Creditor> getBasicCreditors(Occam occam, String query) throws AonCoreException {
		final String q = (!AonStringUtils.contains(query, AonStringUtils.PERCENT))
			 	?(AonStringUtils.PERCENT + query + AonStringUtils.PERCENT)
				:(query);
		return AON.getBasicCreditors(occam,
				p ->  p.getStatusProperty().eq( RegistryStatus.ACTIVE.value())
					.and(p.getDocumentProperty().like(q)
					 .or(p.getNameProperty().like(q))
					 .or(p.getAliasProperty().like(q)))
				).collect(Collectors.toCollection(LinkedList::new));
	}
	
//	// -------------------------------------------------------------- ACTIVITIES
//	@Override
//	public LinkedList<Activity> getActivities(int activityGroup) throws AonCoreException {
//		LinkedList<Activity> list = new LinkedList<Activity>();
//		TypeActivity[] types = null;
//		if (activityGroup == 0) {
//			types = Type1Activities.values();
//		} if (activityGroup == 1) {
//			types = Type2Activities.values();
//		} if (activityGroup == 2) {
//			types = Type3Activities.values();
//		} if (activityGroup == 3) {
//			types = Type4Activities.values();
//		} if (activityGroup == 6) {
//			types = Type7Activities.values();
//		}
//		if (types == null) {
//			throw new AonCoreException("Grupo de actividad no soportado " + activityGroup );
//		}
//		Activity a;
//		for (TypeActivity type : types) {
//			a = new Activity();
//			a.setEpigraph(type.getEpigraph());
//			a.setDescription(type.getLiteral());
//			list.add(a);
//		}
//		return list;
//	}

	// --------------------------------------------------------------- GWT API INFO
	
//	public AonData getAonData(String domainName, Integer domainId, String login){
//		Domain domain = AON.getDomain(domainName, domainId, login);
//		User user = AON.getUser(domain.getName(), domain.getId(), login);
//		Integer operator = AON.getTaskHolder(domain.getName(), domainId, login, 
//				f -> f.getDomainProperty().eq(domainId).and(f.getUserIdProperty().eq(user.getId()))).getId();
//		ApplicationParameter beta = AON.getApplicationParameter(domainName, domainId, login, AppParam.AON_BETA_ENABLED);
//		ApplicationParameter alpha = AON.getApplicationParameter(domainName, domainId, login, AppParam.AON_ALPHA_ENABLED);
//		ApplicationParameter customerCheckEnabled  = AON.getApplicationParameter(domainName, domainId, login, AppParam.FS_CUSTOMER_CHECK_ENABLED);
//		if ( (customerCheckEnabled == null || customerCheckEnabled.getId() == null) && domain!=null && domain.getParentId() != null) {
//			customerCheckEnabled  = AON.getApplicationParameter(domainName, domain.getParentId(), login, AppParam.FS_CUSTOMER_CHECK_ENABLED);			
//		}
//		Company company = AON.getCompany(domainName, domainId, login, f -> f.getDomainProperty().eq(domainId));
//		return new AonData().setUser(user)
//				.setMd5(getMd5(user.getLogin()+domain.getName()))
//				.setDomain(domain)
//				.setUserOperator(operator)
//				.setBetaEnabled((beta!=null && Boolean.valueOf(beta.getValue())))
//				.setAlphaEnabled((alpha!=null && Boolean.valueOf(alpha.getValue())))
//				.setCompany(company)
//				;
//	}
	
//	public AonData getAonDataToken(String domainName, Integer domainId, String token){
//		Domain domain = AON.getDomain(domainName, domainId, "");
//		User user = AON_SOLUTIONS.getUser(domain, token);
//		Integer operator = AON.getTaskHolder(domain.getName(), domainId, user.getLogin(), 
//				f -> f.getDomainProperty().eq(domainId).and(f.getUserIdProperty().eq(user.getId()))).getId();
//		ApplicationParameter beta = AON.getApplicationParameter(domainName, domainId, user.getLogin(), AppParam.AON_BETA_ENABLED);
//		ApplicationParameter alpha = AON.getApplicationParameter(domainName, domainId, user.getLogin(), AppParam.AON_ALPHA_ENABLED);
//		Company company = AON.getCompany(domainName, domainId, user.getLogin(), f -> f.getDomainProperty().eq(domainId));
//		return new AonData().setUser(user)
//				.setMd5(getMd5(user.getLogin()+domain.getName()))
//				.setDomain(domain)
//				.setUserOperator(operator)
//				.setBetaEnabled((beta!=null && Boolean.valueOf(beta.getValue())))
//				.setAlphaEnabled((alpha!=null && Boolean.valueOf(alpha.getValue())))
//				.setCompany(company)
//				.setAonSolutions(true);
//	}
	
//	private String getMd5(String str){
//		MessageDigest md = null;
//		try {
//			md = MessageDigest.getInstance("MD5");
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		}
//	    md.update(str.getBytes());
//	    byte byteData[] = md.digest();
//	    //convert the byte to hex format method 1
//        StringBuffer sb = new StringBuffer();
//	    for (int i = 0; i < byteData.length; i++) {
//	     	sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
//	    }       
//        return sb.toString();
//	}
	
//	@Override
//	public Integer presentationFile(String domainName, Integer domainId, String user, FiscalModelType type,
//			Integer id) {
//		if(FiscalModelType.M303.equals(type) 
//				|| FiscalModelType.M303_RG.equals(type)
//				|| FiscalModelType.M303_RS.equals(type)) {
//			return Mod303ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
//		} else if(FiscalModelType.M111.equals(type)) {
//			return Mod111ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
//		}  else if(FiscalModelType.M115.equals(type)) {
//			return Mod115ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
//		}  else if(FiscalModelType.M123.equals(type)) {
//			return Mod123ServiceImpl.getInstance().presentationFile(domainName, domainId, user, id);
//		}
//		return -1;
//	}
	
//	@Override
//	public void markAsFinished(String domainName, Integer domainId, String user, IFiscalModel model) {
//		if(FiscalModelType.M303.equals(model.getModel()) 
//				|| FiscalModelType.M303_RG.equals(model.getModel())
//				|| FiscalModelType.M303_RS.equals(model.getModel())) {
//			Mod303 mod303 = FISCAL.getMod303(domainName, domainId, user, model.getId());
//			for(Mod303Key k : Mod303Key.values()) {
//				if(!mod303.getMap().containsKey(k.getValue())){
//					mod303.getMap().put(k.getValue(), new FiscalModelDetail().setAmount(0.0));
//				}
//			}
//			mod303 = Mod303ServiceImpl.getInstance().initializeForFinish(domainName, user, mod303);
//			Mod303ServiceImpl.getInstance().markAsFinished(domainName, user, mod303);
//		} else if(FiscalModelType.M111.equals(model.getModel())) {
//			Mod111 mod111 = FISCAL.getMod111(domainName, domainId, user, model.getId());
//			mod111 = Mod111ServiceImpl.getInstance().initializeForFinish(domainName, user, mod111);
//			Mod111ServiceImpl.getInstance().markAsFinished(domainName, user, mod111);
//		}  else if(FiscalModelType.M115.equals(model.getModel())) {
//			Mod115 mod115 = FISCAL.getMod115(domainName, domainId, user, model.getId());
//			mod115 = Mod115ServiceImpl.getInstance().initializeForFinish(domainName, user, mod115);
//			Mod115ServiceImpl.getInstance().markAsFinished(domainName, user, mod115);
//		}  else if(FiscalModelType.M123.equals(model.getModel())) {
//			Mod123 mod123 = FISCAL.getMod123(domainName, domainId, user, model.getId());
//			mod123 = Mod123ServiceImpl.getInstance().initializeForFinish(domainName, user, mod123);
//			Mod123ServiceImpl.getInstance().markAsFinished(domainName, user, mod123);
//		} else if(FiscalModelType.M130.equals(model.getModel())) {
//			Mod130 mod130 = FISCAL.getMod130(domainName, domainId, user, model.getId());
//			mod130 = Mod130ServiceImpl.getInstance().initializeForFinish(domainName, user, mod130);
//			Mod130ServiceImpl.getInstance().markAsFinished(domainName, user, mod130);
//		} else if(FiscalModelType.M131.equals(model.getModel())) {
//			Mod131 mod131 = FISCAL.getMod131(domainName, domainId, user, model.getId());
//			mod131 = Mod131ServiceImpl.getInstance().initializeForFinish(domainName, user, mod131);
//			Mod131ServiceImpl.getInstance().markAsFinished(domainName, user, mod131);
//		} else if(FiscalModelType.M180.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod180 mod180 =  FISCAL.getMod180(domainName, domainId, user, model.getId());
//			Mod180ServiceImpl.getInstance().changeStatusMod180(domainName, user, mod180, FiscalStatus.FINISHED);
//		} else if(FiscalModelType.M184.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod184 mod184 =  FISCAL.getMod184(domainName, domainId, user, model.getId());
//			Mod184ServiceImpl.getInstance().changeStatusMod184(domainName, user, mod184, FiscalStatus.FINISHED);		
//		} else if(FiscalModelType.M190.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod190 mod190 =  FISCAL.getMod190(domainName, domainId, user, model.getId());
//			Mod190ServiceImpl.getInstance().changeStatus(domainName, user, mod190, FiscalStatus.FINISHED);
//		} else if(FiscalModelType.M193.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod193 mod193 =  FISCAL.getMod193(domainName, domainId, user, model.getId());
//			Mod193ServiceImpl.getInstance().changeStatus(domainName, user, mod193, FiscalStatus.FINISHED);
//		} else if(FiscalModelType.M200.equals(model.getModel())) {
//			// TODO
//		} else if(FiscalModelType.M202.equals(model.getModel())) {
//			Mod202 mod202 =  FISCAL.getMod202(domainName, domainId, user, model.getId());
//			mod202 = Mod202ServiceImpl.getInstance().initializeForFinish(domainName, user, mod202);
//			Mod202ServiceImpl.getInstance().markAsFinished(domainName, user, mod202);
//		} else if(FiscalModelType.M310.equals(model.getModel())) {
//			// TODO
//		} else if(FiscalModelType.M311.equals(model.getModel())) {
//			// TODO
//		} else if(FiscalModelType.M340.equals(model.getModel())) {
//			// TODO
//		} else if(FiscalModelType.M347.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod347 mod347 =  FISCAL.getMod347(domainName, domainId, user, model.getId());
//			Mod347ServiceImpl.getInstance().changeStatusMod347(domainName, user, mod347, FiscalStatus.FINISHED);			
//		} else if(FiscalModelType.M349.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod349 mod349 =  FISCAL.getMod349(domainName, domainId, user, model.getId());
//			Mod349ServiceImpl.getInstance().changeStatusMod349(domainName, user, mod349, FiscalStatus.FINISHED);
//		} else if(FiscalModelType.M390.equals(model.getModel())) {
//			// TODO INITIALIZE FOR FINISH ??
//			Mod3902015 mod390 = FISCAL.getMod3902015(domainName, domainId, user, model.getId());
//			Mod3902015ServiceImpl.getInstance().changeStatus(domainName, user, mod390, FiscalStatus.FINISHED);
//		} else if(FiscalModelType.M390_HF.equals(model.getModel())) {
//			Mod390HF mod390 = FISCAL.getMod390HF(domainName, domainId, user, model.getId());
//			mod390 = Mod390HFServiceImpl.getInstance().initializeForFinish(domainName, user, mod390);
//			Mod390HFServiceImpl.getInstance().markAsFinished(domainName, user, mod390);
//		}
//	}
//	
}
