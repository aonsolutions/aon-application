package net.aonsolutions.db.up2date.finance;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.Registry.REGISTRY;

import java.sql.Connection;
import java.util.stream.Stream;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.ParamType;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import net.aonsolutions.db.up2date.Update;

public class UpdateInvoiceRegistryDocument implements Update {

    public static final UpdateInvoiceRegistryDocument UPDATE_INVOICE_REGISTRY_DOCUMENT = new UpdateInvoiceRegistryDocument();

    private UpdateInvoiceRegistryDocument() {
        
    }

    @Override
    public void upgrade(Connection connection) {
        Settings settings;
        DSLContext dslContext;

        settings = new Settings();
        settings.setRenderSchema(false);
        settings.setParamType(ParamType.INLINED);

        dslContext = DSL.using(connection, SQLDialect.MYSQL, settings);
        
        Stream<Invoice> invoicesRDocumentType = dslContext.select(INVOICE.ID, INVOICE.REGISTRY)
                .from(INVOICE).where(INVOICE.RDOCUMENT_TYPE.isNull()).fetch()
                .stream().map(r -> new Invoice().setId(r.getValue(INVOICE.ID)).setRegistry(r.getValue(INVOICE.REGISTRY)));
        
        invoicesRDocumentType.forEach(invoice -> {
            Registry registry = dslContext.select(REGISTRY.DOCUMENT_TYPE, REGISTRY.DOCUMENT_COUNTRY)
            .from(REGISTRY)
            .where(REGISTRY.ID.eq(invoice.getRegistry()))
            .fetch().stream().map(r -> new Registry().setDocumentCountry(r.getValue(REGISTRY.DOCUMENT_COUNTRY))
                    .setDocumentType(r.getValue(REGISTRY.DOCUMENT_TYPE))).findFirst().orElse(new Registry());
            
            dslContext.update(INVOICE)
            .set(INVOICE.RDOCUMENT_TYPE, registry.getDocumentType())
            .where(INVOICE.ID.eq(invoice.getId()).and(INVOICE.REGISTRY.eq(invoice.getRegistry())))
            .execute();
        });
 
        Stream<Invoice> invoicesRDocumentCountry = dslContext.select(INVOICE.ID, INVOICE.REGISTRY)
                .from(INVOICE).where(INVOICE.RDOCUMENT_COUNTRY.isNull()).fetch()
                .stream().map(r -> new Invoice().setId(r.getValue(INVOICE.ID)).setRegistry(r.getValue(INVOICE.REGISTRY)));
        
        invoicesRDocumentCountry.forEach(invoice -> {
            Registry registry = dslContext.select(REGISTRY.DOCUMENT_TYPE, REGISTRY.DOCUMENT_COUNTRY)
            .from(REGISTRY)
            .where(REGISTRY.ID.eq(invoice.getRegistry()))
            .fetch().stream().map(r -> new Registry().setDocumentCountry(r.getValue(REGISTRY.DOCUMENT_COUNTRY))
                    .setDocumentType(r.getValue(REGISTRY.DOCUMENT_TYPE))).findFirst().orElse(new Registry());
            
            dslContext.update(INVOICE)
            .set(INVOICE.RDOCUMENT_COUNTRY, registry.getDocumentCountry())
            .where(INVOICE.ID.eq(invoice.getId()).and(INVOICE.REGISTRY.eq(invoice.getRegistry())))
            .execute();
        });
    }
    
    public class Invoice {
        Integer id;
        Integer registry;
        
        public Integer getId() {
            return id;
        }
        
        public Invoice setId(Integer id) {
            this.id = id;
            return this;
        }
        
        public Integer getRegistry() {
            return registry;
        }
        
        public Invoice setRegistry(Integer registry) {
            this.registry = registry;
            return this;
        }
    }
    
    public class Registry { 
        
        Byte documentType;
        String documentCountry;

        public Byte getDocumentType() {
            return documentType;
        }
        
        public Registry setDocumentType(Byte documentType) {
            this.documentType = documentType;
            return this;
        }
        
        public String getDocumentCountry() {
            return documentCountry;
        }
        
        public Registry setDocumentCountry(String documentCountry) {
            this.documentCountry = documentCountry;
            return this;
        }
    }
    

}