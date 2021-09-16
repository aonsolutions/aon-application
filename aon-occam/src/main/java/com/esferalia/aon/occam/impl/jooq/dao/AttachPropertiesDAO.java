package com.esferalia.aon.occam.impl.jooq.dao;


import static com.esferalia.aon.jooq.tables.AuthAttach.AUTH_ATTACH;
import static com.esferalia.aon.jooq.tables.ContractAttach.CONTRACT_ATTACH;
import static com.esferalia.aon.jooq.tables.DataAttach.DATA_ATTACH;
import static com.esferalia.aon.jooq.tables.Iattach.IATTACH;
import static com.esferalia.aon.jooq.tables.InvoiceAttach.INVOICE_ATTACH;
import static com.esferalia.aon.jooq.tables.OfferAttach.OFFER_ATTACH;
import static com.esferalia.aon.jooq.tables.PayrollBatchAttach.PAYROLL_BATCH_ATTACH;
import static com.esferalia.aon.jooq.tables.ProjectAttach.PROJECT_ATTACH;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.RattachTag.RATTACH_TAG;
import static com.esferalia.aon.jooq.tables.SepeBatchAttach.SEPE_BATCH_ATTACH;

import java.sql.Timestamp;

import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.Select;
import org.jooq.SelectJoinStep;

import com.esferalia.aon.occam.api.model.Filter.AttachFilter;
import com.esferalia.aon.occam.api.model.Filter.AuthAttachFilter;
import com.esferalia.aon.occam.api.model.Filter.Property;
import com.esferalia.aon.occam.api.model.Filter.RattachTagFilter;
import com.esferalia.aon.occam.api.model.Properties.AttachProperties;
import com.esferalia.aon.occam.api.model.Properties.AuthAttachProperties;
import com.esferalia.aon.occam.api.model.Properties.RattachTagProperties;

public class AttachPropertiesDAO {
	

	protected static class AuthAttachPropertiesDAO implements AuthAttachProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, AuthAttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(AuthAttachFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(AUTH_ATTACH.ID);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(AUTH_ATTACH.TYPE);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(AUTH_ATTACH.MIMETYPE);}
		@Override public Property<byte[]> getAuthProperty() {return new FilterDAO.PropertyDAO<byte[]>(AUTH_ATTACH.AUTH);}
	}

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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(RATTACH.TYPE);}
		@Override public Property<java.util.Date> getAttachDateProperty() {return new FilterDAO.LocalDatePropertyDAO(RATTACH.ATTACH_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.CATEGORY);}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(RATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.CREATION_USER);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DATA);}
		@Override public Property<String> getDparentIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DPARENT_ID);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.DRIVE_ID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.MIMETYPE);}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(RATTACH.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.MODIFICATION_USER);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.REGISTRY);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(RATTACH.SECURITY_LEVEL);}
		
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}

		@Override public Property<Integer> getTagProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH_TAG.TAG);}
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(CONTRACT_ATTACH.ATTACH_DATE);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.CONTRACT);}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return new FilterDAO.PropertyDAO<>(CONTRACT_ATTACH.SECURITY_LEVEL);}
		@Override public Property<java.util.Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		@Override public Property<java.util.Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(CONTRACT_ATTACH.ATTACH_DATE);}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return new FilterDAO.PropertyDAO<>(INVOICE_ATTACH.INVOICE);}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		
		@Override public Property<java.util.Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		@Override public Property<java.util.Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		@Override public Property<java.util.Date> getAttachDateProperty() {return  new FilterDAO.LocalDatePropertyDAO(PAYROLL_BATCH_ATTACH.ATTACH_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		
		@Override public Property<java.util.Date> getAttachDateProperty() {return new FilterDAO.LocalDatePropertyDAO(PROJECT_ATTACH.ATTACH_DATE);}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return null;}
		@Override public Property<Byte> getSourceTypeProperty() {return null;}
		
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(PROJECT_ATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.CREATION_USER);}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(PROJECT_ATTACH.MODIFICATION_DATE);}
		@Override public Property<String> getModificationUserProperty() {return new FilterDAO.PropertyDAO<>(PROJECT_ATTACH.MODIFICATION_USER);}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.DOMAIN);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.DESCRIPTION);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.DRIVEID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return null;}
		@Override public Property<Integer> getScopeProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.SCOPE);}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<java.util.Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return null;}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.SOURCE_BATCH);}
		@Override public Property<Byte> getSourceTypeProperty() {return new FilterDAO.PropertyDAO<>(SEPE_BATCH_ATTACH.SOURCE_TYPE);}
		@Override public Property<Integer> getTagProperty() {return null;}
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
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<>(DATA_ATTACH.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<>(DATA_ATTACH.DOMAIN);}
		@Override public Property<Byte> getTypeProperty() {return new FilterDAO.PropertyDAO<>(DATA_ATTACH.TYPE);}
		@Override public Property<Timestamp> getAttachDateTimeStampProperty() {return null;}
		@Override public Property<byte[]> getDataProperty() {return new FilterDAO.PropertyDAO<>(DATA_ATTACH.DATA);}
		@Override public Property<String> getDriveIdProperty() {return new FilterDAO.PropertyDAO<>(DATA_ATTACH.DRIVE_ID);}
		@Override public Property<Byte> getMimeTypeProperty() {return new FilterDAO.PropertyDAO<>(DATA_ATTACH.MIMETYPE);}
		@Override public Property<Integer> getAttachModuleProperty() {return null;}
		@Override public Property<Integer> getScopeProperty() {return null;}
		@Override public Property<Byte> getSecurityLevelProperty() {return null;}
		@Override public Property<java.util.Date> getAttachDateProperty() {return null;}
		@Override public Property<Integer> getCategoryProperty() {return null;}
		@Override public Property<java.util.Date> getAttachCreationDateProperty() {return null;}
		@Override public Property<Timestamp> getCreationDateTimeStampProperty() {return new FilterDAO.LocalDateTimePropertyDAO(DATA_ATTACH.CREATION_DATE);}
		@Override public Property<String> getCreationUserProperty() {return null;}
		@Override public Property<String> getDparentIdProperty() {return null;}
		@Override public Property<java.util.Date> getAttachModificationDateProperty() {return null;}
		@Override public Property<Timestamp> getModificationDateTimeStampProperty() {return null;}
		@Override public Property<String> getModificationUserProperty() {return null;}
		@Override public Property<Integer> getSourceBatchProperty() {return new FilterDAO.PropertyDAO<Integer>(DATA_ATTACH.SOURCE_ID);}
		@Override public Property<Byte> getSourceTypeProperty() {return new FilterDAO.PropertyDAO<Byte>(DATA_ATTACH.SOURCE);}
		@Override public Property<String> getDescriptionProperty() {return new FilterDAO.PropertyDAO<String>(DATA_ATTACH.DESCRIPTION);}
		@Override public Property<Integer> getTagProperty() {return null;}
	}
	
	protected static class RattachTagPropertiesDAO implements RattachTagProperties {
		protected Select<Record> build(SelectJoinStep<Record> select, RattachTagFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			return filterDAO.build(select);
		}
		
		protected Condition[] getConditions(RattachTagFilter filter) {
			FilterDAO filterDAO = (FilterDAO) filter.filter(this);
			if (filterDAO == null) return new Condition[0];
			return new Condition[] { filterDAO.getCondition() };
		}
		@Override public Property<Integer> getIdProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH_TAG.ID);}
		@Override public Property<Integer> getDomainProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH_TAG.DOMAIN);}
		@Override public Property<Integer> getRattachProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH_TAG.RATTACH);}
		@Override public Property<Integer> getTagProperty() {return new FilterDAO.PropertyDAO<Integer>(RATTACH_TAG.TAG);}
	}
}
