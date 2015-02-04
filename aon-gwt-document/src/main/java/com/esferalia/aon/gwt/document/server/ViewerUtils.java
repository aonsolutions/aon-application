package com.esferalia.aon.gwt.document.server;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Enterprise;
import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.RattachColumns;

public class ViewerUtils {

	protected static class RAttach {

		byte[] bytes;
		MimeType mimeType;
		String driveId;
		Integer domainId;
	}
	
	protected static RAttach getRAttach(Integer id)
			throws SQLException, IOException {
		Connection conn = null;

		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			conn = DatabaseSync.getConnection(AonUtil.getDomainName());

			stmt = conn.prepareStatement("SELECT *" + " FROM "
					+ SQLConstants.RATTACH + " WHERE " + RattachColumns.ID
					+ "= ? ");
			stmt.setInt(1, id);

			rs = stmt.executeQuery();

			if (!rs.next()) {
				throw new OpenDocumentConverterServlet.NoSuchDocumentException(
						id);
			}

			RAttach rattach = new RAttach();
			Blob blob = rs.getBlob(RattachColumns.DATA);
			rattach.driveId = rs.getString(RattachColumns.DRIVE_ID);
			if(rattach.driveId == null) rattach.bytes = blob.getBytes(1, (int) blob.length());
			rattach.mimeType = OpenDocumentConverterServlet.mimeTypeOf(rs
					.getInt(RattachColumns.MIMETYPE));
			rattach.domainId = rs.getInt(RattachColumns.DOMAIN);
			return rattach;

		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				conn.close();
			}
		}
	}
	protected static Integer getEnterpriseID() throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.code.aon.company.Enterprise.class);

		Criteria criteria = new Criteria();
		criteria.addEqualExpression(
				beanManager.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN),
				getDomainID() );

		List<ITransferObject> tos = beanManager.getList(criteria);
		
		if( tos == null || tos.isEmpty() )
			return null;
		
		return ((Enterprise) tos.get(0)).getId();
		
	}

	protected static Integer getDomainID() {
		DomainSwitcher domainSwitcher = (DomainSwitcher)AonUtil
				.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return domainSwitcher.getDomainId();
	}
}
