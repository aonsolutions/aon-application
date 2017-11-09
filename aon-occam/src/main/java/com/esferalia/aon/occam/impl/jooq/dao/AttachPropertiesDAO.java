package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;

import java.sql.Date;
import java.sql.Timestamp;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;

public class AttachPropertiesDAO {

	protected static class RattachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(RATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RATTACH.TYPE);}
		@Override public Property<Date> getAttachDateProperty() {return new FilterDAO.PropertyDAO<Date>(RATTACH.ATTACH_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH.CATEGORY);}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<Timestamp>(RATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<String>(RATTACH.CREATION_USER);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(RATTACH.DATA);}
		@Override public Property<String> getDparentIdProperty() {return new FilterDAO.PropertyDAO<String>(RATTACH.DPARENT_ID);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(RATTACH.DRIVE_ID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RATTACH.MIMETYPE);}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<Timestamp>(RATTACH.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<String>(RATTACH.MODIFICATION_USER);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH.REGISTRY);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(RATTACH.SECURITY_LEVEL);}
		
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
	}
	
	protected static class ContractAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTRACT_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTRACT_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(CONTRACT_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(CONTRACT_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CONTRACT_ATTACH.ATTACH_DATE);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(CONTRACT_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(CONTRACT_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(CONTRACT_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTRACT_ATTACH.CONTRACT);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(CONTRACT_ATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(CONTRACT_ATTACH.SECURITY_LEVEL);}
		@Override public Property<Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
	}
	
	protected static class IattachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(IATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(IATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(IATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(IATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(IATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(IATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(IATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<Integer>(IATTACH.ITEM);}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
	}
	
	protected static class InvoiceAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(INVOICE_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return new FilterDAO.PropertyDAO<Timestamp>(CONTRACT_ATTACH.ATTACH_DATE);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(INVOICE_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(INVOICE_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(INVOICE_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<Integer>(INVOICE_ATTACH.INVOICE);}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		
		@Override public Property<Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
	}
	
	protected static class OfferAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(OFFER_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(OFFER_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(OFFER_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return null;}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(OFFER_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(OFFER_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(OFFER_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<Integer>(OFFER_ATTACH.OFFER);}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
	}
	
	protected static class PayrollAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PAYROLL_BATCH_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PAYROLL_BATCH_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(PAYROLL_BATCH_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PAYROLL_BATCH_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(PAYROLL_BATCH_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(PAYROLL_BATCH_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PAYROLL_BATCH_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return null;}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(PAYROLL_BATCH_ATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<Date> getAttachDateProperty() {return  new FilterDAO.PropertyDAO<Date>(PAYROLL_BATCH_ATTACH.ATTACH_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
	}
	
	protected static class ProjectAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.ATTACH_TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(PROJECT_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(PROJECT_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<Integer>(PROJECT_ATTACH.PROJECT);}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<Byte>(PROJECT_ATTACH.SECURITY_LEVEL);}
		
		@Override public Property<Date> getAttachDateProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.ATTACH_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.MODIFICATION_USER);}
	}

	protected static class SepeAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(SEPE_BATCH_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(SEPE_BATCH_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(SEPE_BATCH_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(SEPE_BATCH_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(SEPE_BATCH_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(SEPE_BATCH_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(SEPE_BATCH_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return null;}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<Integer>(SEPE_BATCH_ATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return new FilterDAO.PropertyDAO<Integer>(SEPE_BATCH_ATTACH.SOURCE_BATCH);}
		@Override public Property<Byte> getSourceTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(SEPE_BATCH_ATTACH.SOURCE_TYPE);}
	}
	
	protected static class DataAttachPropertiesDAO implements AttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(DATA_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(DATA_ATTACH.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(DATA_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<byte[]>(DATA_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<String>(DATA_ATTACH.DRIVE_ID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(DATA_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return null;}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.PropertyDAO<Timestamp>(DATA_ATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return new FilterDAO.PropertyDAO<Integer>(DATA_ATTACH.SOURCE_ID);}
		@Override public Property<Byte> getSourceTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(DATA_ATTACH.SOURCE);}
		@Override public Property<String> getDescriptionProperty() {return null;}
	}
}
