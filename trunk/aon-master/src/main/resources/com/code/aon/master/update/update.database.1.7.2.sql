# Database: aon_master
# Version: Actualizacion de la version 1.7.2 a la version 1.7.3
# Created by: girazu
# Creation Date: 24/06/2008 11:15
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `alumn_loan` ADD KEY `customer` (`customer`);

ALTER TABLE `alumn_loan` ADD CONSTRAINT `alumn_loan_fk1` FOREIGN KEY (`customer`) REFERENCES `customer` (`registry`);

DROP FUNCTION `facturasCategoriaFecha`;

CREATE FUNCTION `facturasCategoriaFecha`(i DATE, f DATE, c INTEGER, t INTEGER)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(invoice_detail.taxable_base) IS NULL, 0,
               SUM(TRUNCATE(invoice_detail.taxable_base *
               (IF (invoice.surcharge = 0,1,(invoice_tax.percentage + invoice_tax.surcharge + 100) / 100)), 2)))
          FROM invoice_detail, invoice, item, product, pcategory, invoice_tax
         WHERE item.product = product.id
           AND product.category = pcategory.id
           AND invoice_detail.item = item.id
           AND invoice_detail.invoice = invoice.id
	       AND invoice_tax.invoice_detail = invoice_detail.id
       	   AND invoice.type = t
           AND pcategory.id = c
           AND invoice.issue_date BETWEEN DATE_ADD(i, INTERVAL 1 DAY) AND f);

DROP FUNCTION `facturasGrupoCategoriaFecha`;

CREATE FUNCTION `facturasGrupoCategoriaFecha`(i DATE, f DATE, c INT, t INT)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(invoice_detail.taxable_base) IS NULL, 0,
       SUM(TRUNCATE(invoice_detail.taxable_base *
       (IF (invoice.surcharge = 0,1,(invoice_tax.percentage + invoice_tax.surcharge + 100) / 100)), 2)))
FROM invoice_detail, invoice, item, product, pcategory, pcategory_group, invoice_tax
WHERE item.product = product.id  AND product.category = pcategory.id
AND pcategory_group.id = pcategory.pcategory_group
AND invoice_detail.item = item.id  AND invoice_detail.invoice = invoice.id
AND invoice_tax.invoice_detail = invoice_detail.id  AND invoice.type = t
AND pcategory_group.id = c  AND invoice.issue_date BETWEEN DATE_ADD(i, INTERVAL 1 DAY) AND f);


UPDATE `db_version` SET `version_number` = '1.7.3';

COMMIT;
