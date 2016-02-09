package com.code.aon.ui.loader.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.BankUtil;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.registry.RegistrySegment;
import com.code.aon.registry.Segment;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.LoaderUtils;
import com.code.aon.ui.loader.pojo.LoadedRegistry;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryLoaderFactory {
	
	private LoaderUtils loaderUtils;
	private Map<String,Segment> segments = new HashMap<String, Segment>();
	
	protected LoaderUtils getLoaderUtils() {
		if (loaderUtils == null) {
			loaderUtils = new LoaderUtils();
		}
		return loaderUtils;
	}

	public Registry populateRegistry(LoaderParams params,LoadedRegistry loaded) {
		Registry registry = new Registry();
		registry.setDocumentCountry(loaded.getDocumentCountry());
		registry.setDocumentType(loaded.getDocumentType());
		registry.setDocument(loaded.getDocumento());
		if (registry.getRegistryDocument() != null) {
			if (registry.getRegistryDocument().isValidNIF() 
				|| registry.getRegistryDocument().isValidNIE()) {
				registry.setType(RegistryType.NATURAL);
			} else if (registry.getRegistryDocument().isValidCIF()) {
				registry.setType(RegistryType.LEGAL);	
			}
		}	
		registry.setAlias(loaded.getAlias());
		registry.setNationality(loaded.getNationality());
		registry.setName(loaded.getRazonSocial());
		registry.setSecurityLevel(params.getSecurityLevel());
		return registry;
	}
	
	public void insertRegistryAddress(Registry registry, LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
		RegistryAddress address = new RegistryAddress();
		address.setRegistry(registry);
		if (StringUtils.isNotBlank(loaded.getTipoVia())) {
			StreetType streetType = null;
			try {
				streetType = StreetType.valueOf(loaded.getTipoVia());
				address.setStreetType(streetType);
			} catch (Throwable t) {
				// Nada.
			}
		}
		address.setAddress(loaded.getDireccion());
		address.setNumber(loaded.getNumero());
		address.setAddress2(loaded.getDireccion2());
		address.setAddress3(loaded.getDireccion3());
		address.setZip(loaded.getCp());
		address.setCity(loaded.getCiudad());
		if (StringUtils.isNotBlank( loaded.getProvincia()) || StringUtils.isNotBlank( loaded.getNombreProvincia())) {
			GeoZone geozone = ensureGeoZone(loaded.getPais(),loaded.getProvincia(),loaded.getNombreProvincia() );
			address.setGeozone(geozone);
		}
		bean.insert(address);		
	}
	
	public GeoZone ensureGeoZone(String pais, String provincia, String nombreProvincia) throws ManagerBeanException {
		IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
		Criteria c = new Criteria();
		String p = null;
		if (StringUtils.isNotBlank(provincia)) {
			c.addEqualExpression(geozoneBean.getFieldName( IEntityAlias.GEO_ZONE_CODE) , provincia);
			p = provincia;
		} else if (StringUtils.isNotBlank(nombreProvincia)) {
			c.addEqualExpression(geozoneBean.getFieldName( IEntityAlias.GEO_ZONE_NAME) , nombreProvincia);
			p = nombreProvincia;
		}
		List<ITransferObject> list = geozoneBean.getList(c);
		if (list != null && list.size() > 0) {
			return (GeoZone) list.get(0);	
		}
		throw new ManagerBeanException("No es posible encontrar la provincia '" + p + "'.");
	}

	public void insertRegistryMedia(Registry registry, MediaType type, String value) throws ManagerBeanException {
		insertRegistryMedia(registry, type, value,false,false,false);	
	}
	public void insertRegistryMedia(Registry registry, MediaType type, String value,boolean administrative,boolean commercial, boolean technical) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryMedia.class);
		RegistryMedia rmedia = new RegistryMedia();
		rmedia.setRegistry(registry);
		rmedia.setMediaType(type);
		rmedia.setAdministrative(administrative);
		rmedia.setCommercial(commercial);
		rmedia.setTechnical(technical);
		rmedia.setValue(value);
		bean.insert(rmedia);		
	}

	public RegistryBank insertRegistryBank(ILoaderEngine engine, LoaderParams params,Registry registry,LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryBank.class);
		RegistryBank rbank = new RegistryBank();
		rbank.setRegistry(registry);
		BankAccount bankAccount = new BankAccount();
		String ccc = StringUtils.replace(loaded.getCuentaBanco(), ".", "");
		String country = StringUtils.substring(ccc, 0, 2);
		if (!StringUtils.isNumeric(country)) {
			bankAccount.setCountry(Country.valueOf(country));
			bankAccount.setCheck(StringUtils.substring(ccc, 2, 4));
			bankAccount.setBban1(StringUtils.substring(ccc, 4, 8));
			bankAccount.setBban2(StringUtils.substring(ccc, 8, 12));
			bankAccount.setBban3(StringUtils.substring(ccc, 12, 16));
			bankAccount.setBban4(StringUtils.substring(ccc, 16, 20));
			bankAccount.setBban5(StringUtils.substring(ccc, 20, 24));
			bankAccount.setBban6(StringUtils.substring(ccc, 24, 28));
			bankAccount.setBban7(StringUtils.substring(ccc, 28, 32));
			bankAccount.setBban8(StringUtils.substring(ccc, 32, 34));
			if (!bankAccount.isValidIban()) {
				throw new ManagerBeanException("IBAN de " + loaded.getRazonSocial() + " incorrecto ("+loaded.getCuentaBanco()+")");
			}
		} else {
			bankAccount.setCountry(Country.ES);
			bankAccount.setBban1(StringUtils.substring(ccc, 0, 4));
			bankAccount.setBban2(StringUtils.substring(ccc, 4, 8));
			bankAccount.setBban3(StringUtils.substring(ccc, 8, 12));
			bankAccount.setBban4(StringUtils.substring(ccc, 12, 16));
			bankAccount.setBban5(StringUtils.substring(ccc, 16, 20));
			if (!bankAccount.isValidBban()) {
				throw new ManagerBeanException("CCC de "+ loaded.getRazonSocial() + " incorrecto ("+loaded.getCuentaBanco()+")");
			}
			bankAccount.setCheck(bankAccount.calculateIbanControlDigit());
		}

		if (bankAccount != null) {
			rbank.setBankAccount(bankAccount);
			BankUtil.fillBankAccountData(rbank);
		}
		return (RegistryBank) bean.insert(rbank);		
	}

	public void insertRegistryPayMethod(LoaderParams params,Registry registry,RegistryBank rbank,LoadedRegistry loaded) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryPayMethod.class);
		RegistryPayMethod rPayMethod = new RegistryPayMethod();
		rPayMethod.setRegistry(registry);
		rPayMethod.setRegistryBank(rbank);
		rPayMethod.setNumberOfPayments(ensureInteger(loaded.getNumeroVtos()));
		rPayMethod.setDaysBetweenPayments(ensureInteger(loaded.getDiasEntreVtos()));
		rPayMethod.setDaysToFirstPayment(ensureInteger(loaded.getDiasAlPrimerVto()));
		if (StringUtils.isNotBlank(loaded.getDiasPago())) {
			rPayMethod.setPaymentDays(loaded.getDiasPago());	
		}
		if (StringUtils.isNotBlank(loaded.getFormaPago())) {
			PayMethod payMethod = getLoaderUtils().ensurePayMethod(loaded.getFormaPago());
			rPayMethod.setPayment(payMethod);
		}
		bean.insert(rPayMethod);
	}
	
	private int ensureInteger(Integer integer) {
		return integer==null?0:integer;
	}

	public void insertRegistrySegment(Registry registry,String segmento) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistrySegment.class);
		RegistrySegment rSegment = new RegistrySegment();
		rSegment.setRegistry(registry);
		Segment segment = segments.get(segmento);
		if (segment == null) {
			segment = getLoaderUtils().ensureSegment(segmento);
			segments.put(segmento, segment);
		}
		rSegment.setSegment(segment);
		bean.insert(rSegment);
	}

}
