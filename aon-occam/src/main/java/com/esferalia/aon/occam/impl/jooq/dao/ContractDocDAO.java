package com.esferalia.aon.occam.impl.jooq.dao;

import static com.esferalia.aon.jooq.tables.ContractDoc.CONTRACT_DOC;

import java.util.Optional;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Filter.ContractDocFilter;
import com.esferalia.aon.occam.api.model.doc.Doc;
import com.esferalia.aon.occam.api.model.doc.S3Doc;
import com.esferalia.aon.occam.impl.jooq.dao.PropertiesDAO.ContractDocPropertiesDAO;

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

	private static Doc<?> getS3Doc(org.jooq.Record r){
		return new S3Doc()
				.setAonTable("contract_doc")
				.setS3Key(r.get(CONTRACT_DOC.S3_KEY))
				.setMimeType(r.get(CONTRACT_DOC.MIMETYPE))
				.setDescription(r.get(CONTRACT_DOC.DESCRIPTION));
	}
}
