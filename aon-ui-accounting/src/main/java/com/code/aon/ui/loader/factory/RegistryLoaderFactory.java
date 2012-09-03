package com.code.aon.ui.loader.factory;


import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Bank;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.LoadedRegistry;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryLoaderFactory {
	
	private LoaderUtils loaderUtils;
	
	protected LoaderUtils getLoaderUtils() {
		if (loaderUtils == null) {
			loaderUtils = new LoaderUtils();
		}
		return loaderUtils;
	}

	public Registry populateRegistry(LoaderParams params,LoadedRegistry loaded) {
		Registry registry = new Registry();
		registry.setDocument(loaded.getDocumento());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setNationality(loaded.getNationality());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(params.getSecurityLevel());
		return registry;
	}
	
	public void insertRegistryAddress(Registry registry, LoadedRegistry loaded) throws ManagerBeanException {
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
	
	public GeoZone ensureGeoZone(String pais, String provincia, String nombreProvincia) throws ManagerBeanException {
		IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
		Criteria c = new Criteria();
		c.addEqualExpression(geozoneBean.getFieldName( IEntityAlias.GEO_ZONE_CODE) , provincia);
		List<ITransferObject> list = geozoneBean.getList(c);
		if (list != null && list.size() > 0) {
			return (GeoZone) list.get(0);	
		}
		return null;
	}

	public void insertRegistryMedia(Registry registry, MediaType type, String value) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		RegistryMedia rmedia = new RegistryMedia();
		rmedia.setRegistry(registry);
		rmedia.setMediaType(type);
		rmedia.setValue(value);
		bean.insert(rmedia);		
	}

	public RegistryBank insertRegistryBank(ILoaderEngine engine, LoaderParams params,Registry registry,LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rbank = new RegistryBank();
		rbank.setRegistry(registry);
		BankAccount bankAccount = new BankAccount();
		String[] ccc = StringUtils.split(loaded.getCuentaBanco(),".");
		if (ccc.length == 4) {
			throw new ManagerBeanException("El CCC del cliente "+ loaded.getRazonSocial() +" no es correcto ("+loaded.getCuentaBanco()+")");
		}
		bankAccount.setEntity(ccc[0]);
		bankAccount.setOffice(ccc[1]);
		bankAccount.setControl(ccc[2]);
		bankAccount.setAccount(ccc[3]);
		if (bankAccount.isValid()) {
			Bank bank = getLoaderUtils().ensureBank(ccc[0], loaded.getBanco() );
			rbank.setBank(bank);
			rbank.setBankAccount(bankAccount);
			return (RegistryBank) bean.insert(rbank);		
		} else {
			engine.log("WARNING. La cuenta de banco " + bankAccount.toString() + " no es válida.");
		}
		return null;
	}

	public void insertRegistryPayMethod(LoaderParams params,Registry registry,RegistryBank rbank,LoadedRegistry loaded) throws ManagerBeanException {
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
			PayMethod payMethod = getLoaderUtils().ensurePayMethod(loaded.getFormaPago());
			rPayMethod.setPayment(payMethod);
		}
		bean.insert(rPayMethod);
	}

}
