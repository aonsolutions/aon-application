package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_INVOICE_INTEGRITY_NO_RESULT;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;

public class InvoiceIntegrityController extends DataScrollerState {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Date startDate;
	private Date endDate;
	private DataModel breakDownModel;
	private Integer registry;
	
	private static String INVOICE_UPDATE_STMT =
		"UPDATE invoice set rdocument = ?,rdocument_type=?,rdocument_country=?,rname=? WHERE id = ?";
	private static String FINANCE_UPDATE_STMT =
		"UPDATE finance set rdocument = ?,rdocument_type=?,rdocument_country=?,rname=? WHERE invoice = ?";
		
	private String MAIN_STMT =
	"select count(*) count,r.id id,r.document rd ,r.document_type rdt ,r.document_country rdc,r.name rn"
	+" FROM invoice i, registry r"
	+" WHERE " + DomainManager.getStaticSQLWhereClause("i.domain")
	+" AND i.issue_date BETWEEN ? AND ?"
	+" AND i.registry = r.id"
	+" AND (r.document != i.rdocument"
	+" OR r.document_type != i.rdocument_type"
	+" OR r.document_country != i.rdocument_country"
	+" OR r.name != i.rname)"
	+" GROUP BY id,rd,rdt,rdc,rn";

	private String STMT =
		"select r.id id,r.document rd ,r.document_type rdt ,r.document_country rdc,r.name rn"
		+" ,i.id iid,i.rdocument ird ,i.rdocument_type irdt ,i.rdocument_country irdc,i.rname irn"
		+" FROM invoice i, registry r"
		+" WHERE i.registry = ?"
		+" AND i.issue_date BETWEEN ? AND ?"
		+" AND i.registry = r.id"
		+" AND (r.document != i.rdocument"
		+" OR r.document_type != i.rdocument_type"
		+" OR r.document_country != i.rdocument_country"
		+" OR r.name != i.rname)";
		

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public Integer getRegistry() {
		return registry;
	}
	public void setRegistry(Integer registry) {
		this.registry = registry;
	}

	public DataModel getBreakDownModel() {
		return breakDownModel;
	}
	public void setBreakDownModel(DataModel breakDownModel) {
		this.breakDownModel = breakDownModel;
	}

	public void onStart(ActionEvent event) {
		Date date = new Date();
		setStartDate(CommonUtil.getYearFirstDay(date));
		setEndDate(CommonUtil.getYearLastDay(date));
	}
	
	public void onPreview(ActionEvent event) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());

			ps = conn.prepareStatement(MAIN_STMT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 1;
			i = DomainManager.fillHostVariables(ps, i);
			ps.setDate(++i, new java.sql.Date( getStartDate().getTime()));
			ps.setDate(++i, new java.sql.Date( getEndDate().getTime()));
			rs = ps.executeQuery();
			List<Preview> list = new LinkedList<Preview>();
			while ( rs.next() ) {
				Preview p = new Preview();
				p.setId( rs.getInt("id"));
				p.setCount( rs.getInt("count"));
				p.setDocument(rs.getString("rd"));
				p.setDocumentType(DocumentType.values()[rs.getInt("rdt")]);
				p.setDocumentCountry(rs.getString("rdc"));
				p.setName(rs.getString("rn"));
				list.add(p);
			}
			setModel(new SerializableListDataModel(list));
			if (list.size() == 0) {
				AonUtil.addWarningMessageFromBundle(FINANCE_INVOICE_INTEGRITY_NO_RESULT );
			}
		} catch (SQLException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (AonConnectionException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	public void onSelect(ActionEvent event) {
		Preview preview = (Preview) getDirectModel().getRowData();
		setRegistry( preview.getId());
		onBreakDown(event);
	}
	
	public void onCheckAll(ActionEvent event) {
		checkAll(true);
	}
	public void onUnCheckAll(ActionEvent event) {
		checkAll(false);
	}

	@SuppressWarnings("unchecked")
	private void checkAll(boolean check) {
		List<BreakDown> list =  (List<BreakDown>) getBreakDownModel().getWrappedData();
		for (BreakDown b : list) {
			b.setChecked(check);
		}
	}
	
	public void onPreviewCheckAll(ActionEvent event) {
		previewCheckAll(true);
	}
	public void onPreviewUnCheckAll(ActionEvent event) {
		previewCheckAll(false);
	}

	@SuppressWarnings("unchecked")
	private void previewCheckAll(boolean check) {
		List<Preview> list =  (List<Preview>) getDirectModel().getWrappedData();
		int i = 0;
		for (Preview p : list) {
			p.setChecked(check);
			i++;
		}
		// El scroll de pantalla es de 20, de notifica al usuario que hay más filas
		if (i > 20) {
			AonUtil.addInfoMessage("Se han " + (check?"marcado":"desmarcado") + " " + i + " filas");	
		}
	}

	
	public void onBreakDown(ActionEvent event) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			ps = conn.prepareStatement(STMT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, getRegistry());
			ps.setDate(++i, new java.sql.Date( getStartDate().getTime()));
			ps.setDate(++i, new java.sql.Date( getEndDate().getTime()));
			rs = ps.executeQuery();
			List<BreakDown> list = new LinkedList<BreakDown>();
			while ( rs.next() ) {
				BreakDown b = new BreakDown();
				b.setRegistry( rs.getInt("id"));
				b.setDocument(rs.getString("rd"));
				b.setDocumentType(DocumentType.values()[rs.getInt("rdt")]);
				b.setDocumentCountry(rs.getString("rdc"));
				b.setName(rs.getString("rn"));
				b.setInvoice( rs.getInt("iid"));
				b.setInvoiceDocument(rs.getString("ird"));
				b.setInvoiceDocumentType(DocumentType.values()[rs.getInt("irdt")]);
				b.setInvoiceDocumentCountry(rs.getString("irdc"));
				b.setInvoiceName(rs.getString("irn"));
				list.add(b);
			}
			setBreakDownModel(new SerializableListDataModel(list));
		} catch (SQLException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} catch (AonConnectionException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	public static class Preview implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		int id;
		int count;
		boolean checked;
		String document;
		DocumentType documentType;
		String documentCountry;
		String name;
		
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		public boolean isChecked() {
			return checked;
		}
		public void setChecked(boolean checked) {
			this.checked = checked;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}
		public DocumentType getDocumentType() {
			return documentType;
		}
		public void setDocumentType(DocumentType documentType) {
			this.documentType = documentType;
		}
		public String getDocumentCountry() {
			return documentCountry;
		}
		public void setDocumentCountry(String documentCountry) {
			this.documentCountry = documentCountry;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
	}

	@SuppressWarnings("unchecked")
	public void onUpdatePreview(ActionEvent event) {
		try {
			List<Preview> list =  (List<Preview>) getDirectModel().getWrappedData();
			for (Preview p : list) {
				if (p.isChecked()) {
					setRegistry( p.getId());
					onBreakDown(event);
					onCheckAll(event);
					onUpdate(event);
				}
			}
		} finally {
			onPreview(event);	
		}
	}

	@SuppressWarnings("unchecked")
	public void onUpdate(ActionEvent event) {
		PreparedStatement ips = null;
		PreparedStatement fps = null;
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());

			ips = conn.prepareStatement(INVOICE_UPDATE_STMT);
			fps = conn.prepareStatement(FINANCE_UPDATE_STMT);

			List<BreakDown> list =  (List<BreakDown>) getBreakDownModel().getWrappedData();
			for (BreakDown b : list) {
				if (b.isChecked()) {
					b.setInvoiceDocument( b.getDocument() );
					b.setInvoiceDocumentType( b.getDocumentType() );
					b.setInvoiceDocumentCountry( b.getDocumentCountry() );
					b.setInvoiceName( b.getName() );

					int i = 0;
					ips.setString(++i, b.getInvoiceDocument());
					ips.setInt(++i, b.getInvoiceDocumentType().ordinal());
					ips.setString(++i, b.getInvoiceDocumentCountry());
					ips.setString(++i, b.getInvoiceName());
					ips.setInt(++i, b.getInvoice());
					ips.execute();

					i = 0;
					fps.setString(++i, b.getInvoiceDocument());
					fps.setInt(++i, b.getInvoiceDocumentType().ordinal());
					fps.setString(++i, b.getInvoiceDocumentCountry());
					fps.setString(++i, b.getInvoiceName());
					fps.setInt(++i, b.getInvoice());
					fps.execute();
				}
			}
			onBreakDown(event);
		} catch (Exception e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			DatabaseUtil.closeQuietly(ips);
			DatabaseUtil.closeQuietly(fps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
	
	public static class BreakDown implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		boolean checked;
		int registry;
		String document;
		DocumentType documentType;
		String documentCountry;
		String name;
		
		int invoice;
		String invoiceDocument;
		DocumentType invoiceDocumentType;
		String invoiceDocumentCountry;
		String invoiceName;
		
		boolean sameDocument;
		boolean sameDocumentType;
		boolean sameDocumentCountry;
		boolean sameName;

		public boolean isChecked() {
			return checked;
		}
		public void setChecked(boolean checked) {
			this.checked = checked;
		}
		public int getRegistry() {
			return registry;
		}
		public void setRegistry(int registry) {
			this.registry = registry;
		}
		public String getDocument() {
			return document;
		}
		public void setDocument(String document) {
			this.document = document;
		}
		public DocumentType getDocumentType() {
			return documentType;
		}
		public void setDocumentType(DocumentType documentType) {
			this.documentType = documentType;
		}
		public String getDocumentCountry() {
			return documentCountry;
		}
		public void setDocumentCountry(String documentCountry) {
			this.documentCountry = documentCountry;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getInvoice() {
			return invoice;
		}
		public void setInvoice(int invoice) {
			this.invoice = invoice;
		}
		public String getInvoiceDocument() {
			return invoiceDocument;
		}
		public void setInvoiceDocument(String invoiceDocument) {
			this.invoiceDocument = invoiceDocument;
		}
		public DocumentType getInvoiceDocumentType() {
			return invoiceDocumentType;
		}
		public void setInvoiceDocumentType(DocumentType invoiceDocumentType) {
			this.invoiceDocumentType = invoiceDocumentType;
		}
		public String getInvoiceDocumentCountry() {
			return invoiceDocumentCountry;
		}
		public void setInvoiceDocumentCountry(String invoiceDocumentCountry) {
			this.invoiceDocumentCountry = invoiceDocumentCountry;
		}
		public String getInvoiceName() {
			return invoiceName;
		}
		public void setInvoiceName(String invoiceName) {
			this.invoiceName = invoiceName;
		}
		public boolean isSameDocument() {
			return ObjectUtils.equals(getDocument(), getInvoiceDocument());
		}
		public boolean isSameDocumentType() {
			return ObjectUtils.equals(getDocumentType(), getInvoiceDocumentType());
		}
		public boolean isSameDocumentCountry() {
			return ObjectUtils.equals(getDocumentCountry(), getInvoiceDocumentCountry());
		}
		public boolean isSameName() {
			return ObjectUtils.equals(getName(), getInvoiceName());
		}
		public boolean isSame() {
			return isSameDocumentCountry() && isSameDocumentType() && isSameName() && isSameDocument();
		}
	}
}