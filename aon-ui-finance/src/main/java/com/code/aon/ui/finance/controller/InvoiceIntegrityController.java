package com.code.aon.ui.finance.controller;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.util.AonUtil;

public class InvoiceIntegrityController {

	private Date startDate;
	private Date endDate;
	private DataModel model;
	private DataModel breakDownModel;
	private Integer registry;
	
	private static String UPDATE_STMT =
		"UPDATE invoice set rdocument = ?,rdocument_type=?,rdocument_country=?,rname=? WHERE id = ?";
		
	private static String MAIN_STMT =
	"select count(*) count,r.id id,r.document rd ,r.document_type rdt ,r.document_country rdc,r.name rn"
	+" FROM invoice i, registry r"
	+" WHERE i.issue_date BETWEEN ? AND ?"
	+" AND i.registry = r.id"
	+" AND (r.document != i.rdocument"
	+" OR r.document_type != i.rdocument_type"
	+" OR r.document_country != i.rdocument_country"
	+" OR r.name != i.rname)"
	+" GROUP BY id,rd,rdt,rdc,rn";

	private static String STMT =
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

	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
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
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName(InvoiceIntegrityController.class.getName());
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(MAIN_STMT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
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
			setModel(new ListDataModel(list));
			if (list.size() == 0) {
				AonUtil.addWarningMessageFromBundle(IFinanceMessages.BUNDLE_KEY,IFinanceMessages.FINANCE_INVOICE_INTEGRITY_NO_RESULT );
			}
		} catch (SQLException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
	public void onSelect(ActionEvent event) {
		Preview preview = (Preview) getModel().getRowData();
		setRegistry( preview.getId());
		onBreakDown(event);
	}

	public void onBreakDown(ActionEvent event) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName(InvoiceIntegrityController.class.getName());
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(STMT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
			setBreakDownModel(new ListDataModel(list));
		} catch (SQLException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	public class Preview {
		int id;
		int count;
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

	public void onUpdate(ActionEvent event) {
		BreakDown b = (BreakDown) getBreakDownModel().getRowData();
		
		b.setInvoiceDocument( b.getDocument() );
		b.setInvoiceDocumentType( b.getDocumentType() );
		b.setInvoiceDocumentCountry( b.getDocumentCountry() );
		b.setName( b.getName() );

		PreparedStatement ps = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName(InvoiceIntegrityController.class.getName());
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(UPDATE_STMT);
			int i = 0;
			ps.setString(++i, b.getInvoiceDocument());
			ps.setInt(++i, b.getInvoiceDocumentType().ordinal());
			ps.setString(++i, b.getInvoiceDocumentCountry());
			ps.setString(++i, b.getInvoiceName());
			ps.setInt(++i, b.getInvoice());
			ps.execute();
		} catch (SQLException e) {
			String msg = "No se pudo generar la lista de facturas.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
		
		
	}
	
	public class BreakDown {
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