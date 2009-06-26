package com.code.aon.report.jr;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSourceProvider;

import com.code.aon.common.IFinderBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

/**
 * Class for providing a <code>net.sf.jasperreports.engine.JRDataSource</code>.
 * This implementation constructs a
 * <code>net.sf.jasperreports.engine.data.JRBeanCollectionDataSource</code>
 * from the <code>java.util.Collection</code> extracted from the asgined
 * <code>com.code.aon.common.IFinderBean</code>.
 * 
 * @author Consulting & Development. ecastellano - 14-nov-2005
 * @see net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
 * @see com.code.aon.common.IFinderBean#getList(Criteria)
 */
public class JRBeanCollectionDataSourceProvider extends
		JRAbstractBeanDataSourceProvider {
	/**
	 * The collection provider bean.
	 */
	private IFinderBean bean;

	/**
	 * The criteria that the data will match.
	 */
	private Criteria criteria;

	/**
	 * Constructor for this class.
	 * 
	 * @param clazz
	 *            A valid class that will be used to introspect the available
	 *            bean fields.
	 * @param bean
	 *            The collection provider bean.
	 * @param criteria
	 *            The criteria that the data will match.
	 */
	public JRBeanCollectionDataSourceProvider(Class clazz, IFinderBean bean,
			Criteria criteria) {
		super(clazz);
		this.bean = bean;
		this.criteria = criteria;
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#create(net.sf.jasperreports.engine.JasperReport)
	 */
	public JRDataSource create(JasperReport report) throws JRException {
		try {
			return new JRBeanCollectionDataSource(bean.getList(criteria));
		} catch (ManagerBeanException e) {
			throw new JRException(e.getMessage(), e);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.jasperreports.engine.JRDataSourceProvider#dispose(net.sf.jasperreports.engine.JRDataSource)
	 */
	public void dispose(JRDataSource dataSource) throws JRException {

	}

}
