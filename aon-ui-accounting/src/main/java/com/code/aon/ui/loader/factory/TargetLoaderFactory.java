package com.code.aon.ui.loader.factory;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Target;
import com.code.aon.commercial.enumeration.Advertising;
import com.code.aon.commercial.enumeration.TargetStatus;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.enumeration.MediaType;
import com.code.aon.ui.loader.Column;
import com.code.aon.ui.loader.ILoaderEngine;
import com.code.aon.ui.loader.ILoaderFactory;
import com.code.aon.ui.loader.LoaderParams;
import com.code.aon.ui.loader.pojo.ILoadedPojo;
import com.code.aon.ui.loader.pojo.LoadedTarget;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetLoaderFactory extends RegistryLoaderFactory implements ILoaderFactory<ILoadedPojo>{
	
	private static final Column[] SUPPORTED_COLUMNS = {
			 new Column(CLP,"id"							,0,6	,false	,null)
			,new Column(CLP,"razonSocial"					,2,64	,true	,null)
			,new Column(CLP,"alias"							,2,32	,false	,null)
			,new Column(CLP,"tipoDocumento"					,0,1	,false	,new int[] {0,1,2,3,4,5})
			,new Column(CLP,"paisDocumento"					,2,2	,false	,null)
			,new Column(CLP,"documento"						,2,16	,false	,null)
			,new Column(CLP,"nacionalidad"					,2,2	,false	,null)
			,new Column(CLP,"re"							,0,1	,false	,new int[] {0,1})
			,new Column(CLP,"transaccion"					,0,1	,false	,new int[] {0,1,2,3,4})
			,new Column(CLP,"retencion"						,0,1	,false	,new int[] {0,1})
			,new Column(CLP,"tipoVia"						,2,2	,false	,null)
			,new Column(CLP,"direccion"						,2,128	,false	,null)
			,new Column(CLP,"numero"						,0,6	,false	,null)
			,new Column(CLP,"direccion2"					,2,128	,false	,null)
			,new Column(CLP,"direccion3"					,2,128	,false	,null)
			,new Column(CLP,"cp"							,2,16	,false	,null)
			,new Column(CLP,"ciudad"						,2,64	,false	,null)
			,new Column(CLP,"provincia"						,2,3	,false	,null)
			,new Column(CLP,"nombreProvincia"				,2,32	,false	,null)
			,new Column(CLP,"pais"							,2,2	,false	,null)
			,new Column(CLP,"telefono1"						,2,64	,false	,null)
			,new Column(CLP,"telefono2"						,2,64	,false	,null)
			,new Column(CLP,"fax"							,2,64	,false	,null)
			,new Column(CLP,"email"							,2,64	,false	,null)
			,new Column(CLP,"web"							,2,64	,false	,null)
			,new Column(CLP,"banco"							,2,64	,false	,null)
			,new Column(CLP,"cuentaBanco"					,2,23	,false	,null)
			,new Column(CLP,"formaPago"						,2,32	,false	,null)
			,new Column(CLP,"numeroVtos"					,0,6	,false	,null)
			,new Column(CLP,"diasAlPrimerVto"				,0,6	,false	,null)
			,new Column(CLP,"diasEntreVtos"					,0,6	,false	,null)
			,new Column(CLP,"diasPago"						,2,8	,false	,null)
			,new Column(CLP,"segmento"						,2,32	,false	,null)
	};

	private Map<String, Column[]> columns;
	private ILoaderEngine engine;

	public TargetLoaderFactory() {
	}
	public TargetLoaderFactory(ILoaderEngine engine) {
		this.engine = engine;
	}

	public Map<String, Column[]> getColumns() {
		return columns;
	}

	@Override
	public boolean accept(String obj) {
		return StringUtils.equals(obj,CLP);
	}
	@Override
	public boolean accept(Class<? extends ILoadedPojo> clazz) {
		return ClassUtils.isAssignable(clazz, LoadedTarget.class);
	}

	@Override
	public String getKey() {
		return CLP;
	}

	@Override
	public Column[] getSupportedColumns() {
		return SUPPORTED_COLUMNS;
	}

	@Override
	public LoadedTarget getTargetBean() {
		return new LoadedTarget();
	}

	@Override
	public ITransferObject get(Integer id) throws AonException {
		IManagerBean bean = BeanManager.getManagerBean(Target.class);
		return bean.get(id);
	}

	@Override
	public Integer insert(LoaderParams params,ILoadedPojo loadedPojo) throws AonException {
		LoadedTarget loaded = (LoadedTarget) loadedPojo;
		IManagerBean bean = BeanManager.getManagerBean(Target.class);
		Target target  = new Target();
		
		target.setRegistry(populateRegistry(params,loaded));
		target.setScope(params.getScope());
		target.setTransaction(loaded.getInvoiceTransactionType());
		target.setSurcharge(loaded.isSurcharge());
		target.setWithholding(loaded.isWithholding());
		target.setStatus(TargetStatus.ACTIVE);
		
		target.setAdvertising( Advertising.ALLOWED );
		
		target = (Target) bean.insert(target);
		
		if (StringUtils.isNotBlank(loaded.getTipoVia())
			|| StringUtils.isNotBlank(loaded.getDireccion())
			|| StringUtils.isNotBlank(loaded.getNumero())
			|| StringUtils.isNotBlank(loaded.getDireccion2())
			|| StringUtils.isNotBlank(loaded.getDireccion3())
			|| StringUtils.isNotBlank(loaded.getCp())
			|| StringUtils.isNotBlank(loaded.getCiudad())
			|| StringUtils.isNotBlank(loaded.getProvincia())
			|| StringUtils.isNotBlank(loaded.getNombreProvincia())
			|| StringUtils.isNotBlank(loaded.getPais())) {
			insertRegistryAddress(target.getRegistry(),loaded);	
		}
		
		if (StringUtils.isNotBlank(loaded.getTelefono1())) {
			insertRegistryMedia(target.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono1());
		}
		if (StringUtils.isNotBlank(loaded.getTelefono2())) {
			insertRegistryMedia(target.getRegistry(),MediaType.FIXED_PHONE,loaded.getTelefono2());
		}
		if (StringUtils.isNotBlank(loaded.getFax())) {
			insertRegistryMedia(target.getRegistry(),MediaType.FAX,loaded.getFax());
		}
		if (StringUtils.isNotBlank(loaded.getEmail())) {
			insertRegistryMedia(target.getRegistry(),MediaType.EMAIL,loaded.getEmail(),false,true,false);
		}
		if (StringUtils.isNotBlank(loaded.getWeb())) {
			insertRegistryMedia(target.getRegistry(),MediaType.WEB,loaded.getWeb());
		}
		if (StringUtils.isNotBlank(loaded.getSegmento())) {
			insertRegistrySegment(target.getRegistry(),loaded.getSegmento());
		}
		RegistryBank rbank = null;
		if (StringUtils.isNotBlank(loaded.getCuentaBanco())) {
			rbank = insertRegistryBank(engine,params,target.getRegistry(),loaded);	
		}
		if (rbank != null || StringUtils.isNotBlank(loaded.getFormaPago())) {
			insertRegistryPayMethod(params,target.getRegistry(),rbank,loaded);
		}
		return target.getId();
	}
	
	@Override
	public ITransferObject get(LoaderParams params, ILoadedPojo loadedPojo) throws AonException {
		LoadedTarget loaded = (LoadedTarget) loadedPojo;
		Target target = searchTargetByDocument( params, loaded );
		return target;
	}
	
	private Target searchTargetByDocument(LoaderParams params, LoadedTarget loaded) throws ManagerBeanException {
		if ( StringUtils.isNotEmpty(loaded.getDocumento())) {
			IManagerBean bean = BeanManager.getManagerBean(Target.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARGET_REGISTRY_DOCUMENT), loaded.getDocumento());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARGET_STATUS), TargetStatus.ACTIVE);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TARGET_SCOPE_ID), params.getScope().getId());
			List<ITransferObject> list = bean.getList(criteria); 
			if ( list.size() > 0 ) {
				if ( list.size() > 1 ) {
					throw new ManagerBeanException("Existe más de un cliente potencial activo con el número de documento " + loaded.getDocumento());
				}
				return (Target) list.get(0);
			}
		}
		return null;
	}
	
	@Override
	public void validate(LoaderParams params) throws AonException {
		// Nothing to validate.
	}
}
