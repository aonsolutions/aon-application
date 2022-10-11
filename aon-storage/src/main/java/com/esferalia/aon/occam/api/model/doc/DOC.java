package com.esferalia.aon.occam.api.model.doc;

import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;

import java.net.URL;
import java.util.Optional;

import org.jooq.DSLContext;

import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.esferalia.aon.jooq.tables.records.ContractDocRecord;

import net.aonsolutions.storage.s3.S3;

public class DOC {
	
	public static Optional<IDoc<?>> getContratDoc(DSLContext dslContext, Integer id ) {
		return 
		dslContext.select()
		.from(CONTRACT_DOC)
		.where(CONTRACT_DOC.ID.eq(id))
		.fetchOptionalInto(CONTRACT_DOC)
		.map( DOC::getS3Doc )
		;
		
	}
	
	
	private static IDoc<?> getS3Doc(ContractDocRecord record){
		return new S3Doc()
				.setS3Key(record.getS3Key())
				.setMimeType(record.getMimetype())
				.setDescription(record.getDescription());
	}
	
	
	private static class S3Doc<T extends Enum<?>> implements IDoc<T> {
		
		private T type;
		private String s3Key;
		private Byte mimeType;
		private String description;
		
		public S3Doc<T> setS3Key(String s3Key) {
			this.s3Key = s3Key;
			return this;
		}
		
		public S3Doc<T> setDescription(String description) {
			this.description = description;
			return this;
		}
		
		public S3Doc<T> setMimeType(byte mimeType) {
			this.mimeType = mimeType;
			return this;
		}
		
		@Override
		public T getType() {
			return type;
		}

		@Override
		public String getDescription() {
			return description;
		}

		@Override
		public URL getDownloadURL() {
			return S3.getContractDocDownloadURL(s3Key);
		}
		
		@Override
		public URL getDownloadURL(String contentDisposition) {
			return S3.getContractDocDownloadURL(s3Key, contentDisposition);
		}
		
		@Override
		public <M extends Enum<?>> M getMimeType(Class<M> enumClass) {
			if ( mimeType == null  )
				return null;
			if ( mimeType < 0 )
				return null;
			M [] enumConstants = enumClass.getEnumConstants();
			if ( mimeType >= enumConstants.length)
				return null;
			return enumConstants[mimeType];
		}

		
	}
	

}
