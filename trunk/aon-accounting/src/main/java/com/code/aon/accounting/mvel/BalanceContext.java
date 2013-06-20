
package com.code.aon.accounting.mvel;


import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.Period;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class BalanceContext implements Map<String, Object>,IBalanceConstants {
	
	private static final String ERR_1 = "Número incorrecto de parámetros";
	private static final String ERR_2 = "Expresión incorrecta ";
	private static final String ERR_3 = "Mapa no válido.";
	
	private Map<String, Object> templateContext;
	
	private List<BalanceMVELContext> mvelContexts;
	private Locale locale;
	private Company company;

	public BalanceContext(List<BalanceMVELContext> mvelContextsList) {
		if (mvelContextsList == null || mvelContextsList.size() < 1) {
			throw new IllegalArgumentException("No se ha definido contexto MVEL");
		}
		this.mvelContexts = mvelContextsList;
		templateContext = new HashMap<String, Object>();
	}
	
	public Locale getLocale() {
		if ( locale == null) {
			// Por defecto, TODOS los mensajes están en castellano.
			setLocale( new Locale("es") );
		}
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	
	public Company getCompany() throws ManagerBeanException {
		if (company == null) {
			IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
			List<ITransferObject> list = companyBean.getList(null);
			if (list != null && list.size() > 0) {
				company = (Company) list.get(0);
			}
		}
		return company;
	}
	
	public void intilizeContextMap( BalanceSheet sheet ) throws BalanceException {
		try {
			// Propiedades del Balance
			templateContext.put(MODULE, sheet.getModule());
			templateContext.put(MODULE_ID, sheet.getModuleId());
			templateContext.put(TEMPLATE, sheet.getTemplate());
	
			// Métodos que se utilizan el el template
			templateContext.put(CODE_METHOD, new CodeMethod());
			templateContext.put(DESCRIPTION_METHOD, new DescriptionMethod());
			templateContext.put(BALANCE_METHOD, new BalanceMethod());
			templateContext.put(NOTES_METHOD, new NotesMethod());
			templateContext.put(XML_ITEM_METHOD, new XMLItemMethod());
			
			// ¿?¿?¿?
			templateContext.put(PERIODO_METHOD, new PeriodoMethod());
			
			templateContext.put(INICIO_PERIODO_METHOD, new InicioPeriodoMethod());
			templateContext.put(FIN_PERIODO_METHOD, new FinPeriodoMethod());
			
			// Propiedades genéricas
			templateContext.put(LOCALE, getLocale());
			templateContext.put(REPORT_DATE, new Date());
			
			// Periodos
			templateContext.put(NUM_PERIODS,mvelContexts.size());
			for (int i = 0 ; i< mvelContexts.size(); i++ ) {
				Period period =  mvelContexts.get(i).getParams().getPeriod();
				templateContext.put(PERIOD + i, period.getName());
				templateContext.put(PERIOD_START + i, period.getInitiationDate());
				templateContext.put(PERIOD_END + i, period.getDeadline());
			}
	
			// TODO Resolver desde Company
			templateContext.put(COMPANY_NAME, getCompany().getName());
			templateContext.put(COMPANY_DOCUMENT, getCompany().getDocument());
			templateContext.put(COMPANY_ADDRESS, getCompany().getDefaultAddress().getFullAddress());
		} catch (ManagerBeanException e) {
			throw new BalanceException(e.getMessage(),e);
		}
	}

	// ***********************************************************************
	// java.util.Map inherited methods.
	// ***********************************************************************

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.templateContext.containsKey( (String) key );
	}
	
	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return this.templateContext.entrySet();
	}

	@Override
	public Object get(Object key) {
		Object obj = null;
		if (this.templateContext.containsKey(key) ) {
			obj = this.templateContext.get(key);
		} else {
			obj = this.mvelContexts.get(0).getObject(key);
		}
		return obj;
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		return this.templateContext.keySet();
	}

	@Override
	public Object put(String key, Object ae) {
		return this.templateContext.put(key, ae);
	}
	
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BalanceItem remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return this.templateContext.size();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void finalize() throws Throwable {
		templateContext = null;
		mvelContexts = null;
		super.finalize();
	}
	
	// ***********************************************************************
	
	public class CodeMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() != 1) {
				throw new TemplateModelException(ERR_1);
			}
			Object o = args.get(0);
			if (!(o instanceof StringModel)) {
				throw new TemplateModelException(ERR_2 + o);	
			}
			StringModel sm = (StringModel) args.get(0);
			BalanceItem ae = (BalanceItem) sm.getWrappedObject();
			return new SimpleScalar( Integer.toString(ae.getId()) );
		}
	}

	public class DescriptionMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() != 1) {
				throw new TemplateModelException(ERR_1);
			}
			Object o = args.get(0);
			if (!(o instanceof StringModel)) {
				throw new TemplateModelException(ERR_2);	
			}
			StringModel sm = (StringModel) args.get(0);
			BalanceItem ae = (BalanceItem) sm.getWrappedObject();
			return new SimpleScalar( (StringUtils.isNotEmpty(ae.getDescription())?ae.getDescription():ae.getExpression().getName(getLocale())) );
		}
	}

	public class BalanceMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() < 1 || args.size() > 2) {
				throw new TemplateModelException(ERR_1);
			}
			Object o = args.get(0);
			if (!(o instanceof StringModel)) {
				throw new TemplateModelException(ERR_2);	
			}
			StringModel sm = (StringModel) args.get(0);
			BalanceItem ae = (BalanceItem) sm.getWrappedObject();
			Double value = null;
			if (args.size() > 1) {
				Object map = args.get(1);
				if (!(map instanceof SimpleNumber)) {
					throw new TemplateModelException(ERR_3);	
				}
				SimpleNumber sn = (SimpleNumber) map;
				int idx = sn.getAsNumber().intValue();
				value = (Double) mvelContexts.get(idx).get(ae.getCode());
			} else {
				 value = ae.getValue();
			}
			return new SimpleNumber( value );
		}
	}
	
	public class NotesMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() != 1) {
				throw new TemplateModelException(ERR_1);
			}
			Object o = args.get(0);
			if (!(o instanceof StringModel)) {
				throw new TemplateModelException(ERR_2 + o);	
			}
			StringModel sm = (StringModel) args.get(0);
			BalanceItem ae = (BalanceItem) sm.getWrappedObject();
			return new SimpleScalar( ae.getNotes() );
		}
	}
	
	public class XMLItemMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() < 1 || args.size() > 2) {
				throw new TemplateModelException(ERR_1);
			}
			Object o = args.get(0);
			if (!(o instanceof StringModel)) {
				throw new TemplateModelException(ERR_2);	
			}
			StringModel sm = (StringModel) args.get(0);
			BalanceItem ae = (BalanceItem) sm.getWrappedObject();
			Double value = null;
			if (args.size() > 1) {
				Object map = args.get(1);
				if (!(map instanceof SimpleNumber)) {
					throw new TemplateModelException(ERR_3);	
				}
				SimpleNumber sn = (SimpleNumber) map;
				int idx = sn.getAsNumber().intValue();
				value = (Double) mvelContexts.get(idx).get(ae.getCode());
			} else {
				value = ae.getValue();
			}
			int val = new Double(CommonUtil.round(value * 100)).intValue();
			char sign = (val<0)?'-':'+'; 
			val = Math.abs(val);
			String ret = String.format(XML_ITEM,ae.getId(),sign,val );
			return new SimpleScalar( ret );
		}
	}
	
	public class PeriodoMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() != 1) {
				throw new TemplateModelException(ERR_1);
			}
			Object map = args.get(0);
			if (!(map instanceof SimpleNumber)) {
				throw new TemplateModelException(ERR_3);	
			}
			SimpleNumber sn = (SimpleNumber) map;
			int idx = sn.getAsNumber().intValue();
			
			String period = (String) templateContext.get( PERIOD + idx );
			return new SimpleScalar( period );			
		}
	}

	public class InicioPeriodoMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() != 1) {
				throw new TemplateModelException(ERR_1);
			}
			Object map = args.get(0);
			if (!(map instanceof SimpleNumber)) {
				throw new TemplateModelException(ERR_3);	
			}
			SimpleNumber sn = (SimpleNumber) map;
			int idx = sn.getAsNumber().intValue();
			
			Date date = (Date) templateContext.get( PERIOD_START + idx );
			return new SimpleScalar( PERIOD_FOMATTER.format(date) );			
		}
	}
	
	public class FinPeriodoMethod implements TemplateMethodModelEx {

		@SuppressWarnings("rawtypes")
		public TemplateModel exec(List args) throws TemplateModelException {
			if (args.size() != 1) {
				throw new TemplateModelException(ERR_1);
			}
			Object map = args.get(0);
			if (!(map instanceof SimpleNumber)) {
				throw new TemplateModelException(ERR_3);	
			}
			SimpleNumber sn = (SimpleNumber) map;
			int idx = sn.getAsNumber().intValue();
			
			Date date = (Date) templateContext.get( PERIOD_END + idx );
			return new SimpleScalar( PERIOD_FOMATTER.format(date) );			
		}
	}
}
