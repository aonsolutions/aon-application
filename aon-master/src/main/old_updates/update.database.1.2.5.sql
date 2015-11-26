# Database: aon_master
# Version: Actualizacion de la version 1.2.5 a la version 1.2.6
# Created by: girazu
# Creation Date: 21/12/2007 10:25
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



DROP FUNCTION `inventarioCategoriaFecha`;

CREATE FUNCTION `inventarioCategoriaFecha`(d DATE, c INT)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(inventory_detail.cost) IS NULL, 0, SUM(inventory_detail.cost * inventory_detail.real_quantity))
          FROM inventory, inventory_detail, item, product, pcategory
         WHERE item.product = product.id
           AND product.category = pcategory.id
           AND inventory_detail.item = item.id
           AND inventory.id = inventory_detail.inventory
           AND pcategory.id = c
           AND inventory.inventory_date = d);

DROP FUNCTION `inventarioGrupoCategoriaFecha`;

CREATE FUNCTION `inventarioGrupoCategoriaFecha`(d DATE, c INT)
    RETURNS double
    NOT DETERMINISTIC
    SQL SECURITY DEFINER
    COMMENT ''
RETURN (SELECT IF (SUM(inventory_detail.cost) IS NULL, 0, SUM(inventory_detail.cost * inventory_detail.real_quantity))
       FROM inventory, inventory_detail, item, product, pcategory, pcategory_group
       WHERE item.product = product.id  AND product.category = pcategory.id
       AND pcategory_group.id = pcategory.pcategory_group  AND inventory_detail.item = item.id
       AND inventory.id = inventory_detail.inventory  AND pcategory_group.id = c
       AND inventory.inventory_date = d);


UPDATE `db_version` SET `version_number` = '1.2.6';

COMMIT;
