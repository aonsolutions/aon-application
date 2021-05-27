package com.code.aon.ui.fiscal.file;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Country;
import com.code.aon.company.Company;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.file.format.model.FileFiller;
import com.code.aon.file.format.output.FileOutput;
import com.code.aon.file.tax.FileTaxUtil;
import com.code.aon.file.tax.model.MOD347.MOD347;
import com.code.aon.file.tax.model.MOD347.MOD347Format;
import com.code.aon.file.tax.model.MOD347.data.Asset;
import com.code.aon.file.tax.model.MOD347.data.Declared;
import com.code.aon.file.tax.model.MOD347.data.Deponent;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.geozone.GeoZone;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class MOD347Writer implements IFinanceConstants{
	
	private Company company;
	
	public Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			setCompany( companyController.obtainCompany() );
		}
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	public FileOutput createMOD347(Mod347 mod347) throws ManagerBeanException {
		try {
			MOD347Format format = obtainFormat(mod347);
			if (format == null) {
				throw new ManagerBeanException("No existe soporte para el formato de la declaración " +
						"347 del ejercicio " + mod347.getYear() + " en la administración " + 
						mod347.getAdministration());	
			}
			Deponent deponent = getDeponent(mod347,format);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(out,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(out);
			}
			PrintWriter writer = new PrintWriter(wr);
			FileFiller mod347Filler = new MOD347(deponent, format, writer);
			
			FileOutput output = new FileOutput();
			output.setErrors(mod347Filler.create());
			output.setContent(out.toByteArray());
			return output;
		} catch (IOException e) {
			throw new ManagerBeanException(e);
		}
	}

	private MOD347Format obtainFormat(Mod347 mod347) {
		int year = mod347.getYear();
		Administration adm = mod347.getAdministration();
		MOD347Format f = null;
		for (MOD347Format format : MOD347Format.values() ) {
			if (format.getAdministration() == adm && year >= format.getYear()) {
				if (f == null || f.getYear() < format.getYear() ) {
					f = format;	
				}
			}
		}
		return f;
	}
	
	private Deponent getDeponent(Mod347 mod347,MOD347Format format) throws ManagerBeanException {
			Deponent deponent = new  Deponent();
			deponent.setCode(getCompany().getDocument());
			deponent.setComplementary(mod347.isComplementary());
			deponent.setReplacement(mod347.isReplacement());
			int number = 1;
			try {
				number = Integer.parseInt(mod347.getNumber());	
			} catch (NumberFormatException e) {
				number = 1;
			}
			long a = 3470000000000L + number;
			deponent.setNumber(a); 
			deponent.setName(getCompany().getName());
			RegistryAddress address = getCompany().getDefaultAddress();
			if (address != null) {
				GeoZone geozone = address.getGeozone();
				if (geozone != null) {
					deponent.setProvince(geozone.getCode());		
				}
			}
			FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil
					.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
			if (StringUtils.isNotBlank(fiscalParams.getContactPhone())) {
				try {
					String phone = fiscalParams.getContactPhone();
					phone = StringUtils.remove(phone," ");
					deponent.setRelPhone(Integer.parseInt( phone ));
				} catch (NumberFormatException e) {
					// Nothing
				}
			} else {
				RegistryMedia  phone = getCompany().getPhone();
				deponent.setRelPhone(null);
				if (phone != null){
					try {
						deponent.setRelPhone(Integer.parseInt( phone.getValue() ));
					} catch (NumberFormatException e) {
						// Nothing
					}
				}
			}
			
			if (StringUtils.isNotBlank(fiscalParams.getContactPerson())) {
				deponent.setRelName(FileTaxUtil.changeInvalidCharacters(fiscalParams.getContactPerson()));	
			} else {
				deponent.setRelName(getCompany().getName());	
			}
			
			if (mod347.getReplacedNumber() != null) {
				int replacedNumber = 1;
				try {
					replacedNumber = Integer.parseInt(mod347.getReplacedNumber());	
				} catch (NumberFormatException e) {
					replacedNumber = 1;
				}
				long b = 3470000000000L + replacedNumber;
				deponent.setReplacedNumber(b);
			} 
			deponent.setType("T");
			deponent.setYear(mod347.getYear());
			fillDeclared(deponent,mod347,format);
			return deponent;
	}
	
	private void fillDeclared(Deponent deponent, Mod347 mod347, MOD347Format format)  throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Mod347Detail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.MOD347DETAIL_MOD347_ID), mod347.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			Mod347Detail detail = (Mod347Detail) to;
			if ("I".equals(detail.getSheet())) {
				Asset asset = new Asset();
				asset.setCode(detail.getDocument()==null?null:detail.getDocument());
				asset.setManagerCode(null);
				String name = detail.getName();
				name = FileTaxUtil.changeInvalidCharacters(name);
				asset.setName(name);
				asset.setQuantity(detail.getAmount());
				asset.setAssetLocation(detail.getAssetLocation());
				asset.setCadasdralReference(detail.getCadasdralReference());
				asset.setAssetStreetType(detail.getAssetStreetType());
				asset.setAssetStreet(FileTaxUtil.changeInvalidCharacters(detail.getAssetStreet()));
				asset.setAssetStreetNumberType(detail.getAssetStreetNumberType());
				int i = 0;
				if (StringUtils.isNotBlank(detail.getAssetStreetNumber()) 
				 && StringUtils.isNumeric(detail.getAssetStreetNumber())) {
					i = Integer.parseInt(detail.getAssetStreetNumber());		
				}
				asset.setAssetStreetNumber(i);
				asset.setAssetStreetNumberSuffix(detail.getAssetStreetNumberSuffix());
				asset.setAssetStreetBlock(detail.getAssetStreetBlock());
				asset.setAssetStreetHall(detail.getAssetStreetHall());
				asset.setAssetStreetStair(detail.getAssetStreetStair());
				asset.setAssetStreetFloor(detail.getAssetStreetFloor());
				asset.setAssetStreetDoor(detail.getAssetStreetDoor());
				asset.setAssetStreetComplement(FileTaxUtil.changeInvalidCharacters(detail.getAssetStreetComplement()));
				asset.setAssetStreetCity(FileTaxUtil.changeInvalidCharacters(detail.getAssetStreetCity()));
				asset.setAssetStreetTown(FileTaxUtil.changeInvalidCharacters(detail.getAssetStreetTown()));
				
				if (StringUtils.isNotBlank(detail.getAssetStreetTownCode()) 
				 && StringUtils.isNumeric(detail.getAssetStreetTownCode())) {
					i = Integer.parseInt(detail.getAssetStreetTownCode());		
				} else {
					i=0;	
				}
				asset.setAssetStreetTownCode(i);
				
				if (StringUtils.isNotBlank(detail.getAssetStreetProvince()) 
				 && StringUtils.isNumeric(detail.getAssetStreetProvince())) {
					i = Integer.parseInt(detail.getAssetStreetProvince());		
				} else {
					i=0;	
				}
				asset.setAssetStreetProvince(i);
				
				if (StringUtils.isNotBlank(detail.getAssetStreetZip()) 
				 && StringUtils.isNumeric(detail.getAssetStreetZip())) {
					i = Integer.parseInt(detail.getAssetStreetZip());		
				} else {
					i=0;	
				}
				asset.setAssetStreetZip(i);
				
				deponent.getAssets().add(asset);
			} else {
				Declared dec = new Declared();
				dec.setCode(detail.getDocument()==null?null:detail.getDocument());
				dec.setManagerCode(null);
				String name = detail.getName();
				name = FileTaxUtil.changeInvalidCharacters(name);
				dec.setName(name);
				dec.setKey(detail.getType().getValue());
				if (detail.getProvince() == null) {
					throw new ManagerBeanException("El registro " + dec.getCode() + " - " + dec.getName() + " no tiene una provincia válida");
				} 
				int prov = detail.getProvince().ordinal();
				if (prov == 0) {
					prov = 99;
				}
				dec.setProvince(prov);
				dec.setCountry(detail.getCountry()==null?null:detail.getCountry().getValue());
				if (Country.ES == detail.getCountry()) {
					dec.setCountry("  ");		
				} else {
					if (Country.GR == detail.getCountry()) {
						dec.setCountry("EL");	
					}
					dec.setProvince(99);		
				}
				dec.setQuantity(detail.getAmount());
				dec.setQuantityQuarter1(detail.getFirstQuarterAmount());
				dec.setQuantityQuarter2(detail.getSecondQuarterAmount());
				dec.setQuantityQuarter3(detail.getThirdQuarterAmount());
				dec.setQuantityQuarter4(detail.getFourthQuarterAmount());
				dec.setRenting( detail.isBusinessPremiseRental() );
				dec.setInsurance( detail.isInsuranceOperation() );
				dec.setAssetAmount( detail.getAssetAmount() );
				dec.setAssetFirstQuarterAmount( detail.getAssetFirstQuarterAmount() );
				dec.setAssetSecondQuarterAmount( detail.getAssetSecondQuarterAmount() );
				dec.setAssetThirdQuarterAmount( detail.getAssetThirdQuarterAmount() );
				dec.setAssetFourthQuarterAmount( detail.getAssetFourthQuarterAmount() );
				dec.setCashAmount( detail.getCashAmount() );
				dec.setCashYear( detail.getCashYear() );
				dec.setOperatorNif(detail.getOperatorNif());
				dec.setVatAccrual(detail.isVatAccrual());
				dec.setIsp(detail.isIsp());
				dec.setDepositRegime(detail.isDepositRegime());
				dec.setVatAccrualAmount(detail.getVatAccrualAmount());

				deponent.getDeclareds().add(dec);
			}
		}
	}
	
}
