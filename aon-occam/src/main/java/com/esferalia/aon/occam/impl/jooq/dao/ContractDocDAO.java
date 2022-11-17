package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;

import java.net.URL;
import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ContractDocFilter;
import com.esferalia.aon.occam.api.model.doc.Doc;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractDocPropertiesDAO;

import net.aonsolutions.storage.s3.S3;

public class ContractDocDAO {
	
	
	private static final ContractDocPropertiesDAO PROPERTIES_DAO = new ContractDocPropertiesDAO();
	
	public  static Optional<Doc<?>> getContractDoc(AONContext aonContext, ContractDocFilter filter ) {
		return
		aonContext
		.getDslContext()
		.select()
		.from(CONTRACT_DOC)
		.where(PROPERTIES_DAO.getConditions(filter))
		.fetchOptional(ContractDocDAO::getS3Doc);
	}

	private static Doc<?> getS3Doc(org.jooq.Record record){
		return new S3Doc()
				.setS3Key(record.get(CONTRACT_DOC.S3_KEY))
				.setMimeType(record.get(CONTRACT_DOC.MIMETYPE))
				.setDescription(record.get(CONTRACT_DOC.DESCRIPTION));
	}
	
	
	private static class S3Doc<T extends Enum<?>> extends Doc<T> {
		
		private String s3Key;
		
		public S3Doc<T> setS3Key(String s3Key) {
			this.s3Key = s3Key;
			return this;
		}

		@Override
		public URL getDownloadURL() {
			return S3.getContractDocDownloadURL(s3Key);
		}
		
		@Override
		public URL getDownloadURL(String contentDisposition) {
			return S3.getContractDocDownloadURL(s3Key, contentDisposition);
		}
		
	}
	
	

}
