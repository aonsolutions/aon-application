# Database: aon_master
# Version: Actualizacion de la version 4.6.0 a la version 4.6.1.
# Created by: girazu
# Creation Date: 12/10/2009 18:48
# Comentarios: esta actualización no contiene cambios invalidantes de base de datos.



ALTER TABLE `ec_config` MODIFY `tariff` int(4) default NULL COMMENT 'Identificador de Tarifa para Ecommerce';

INSERT IGNORE INTO `ec_config` (`id`, `active`, `name`, `skin`, `header_img`, `series`, `commerce`, `show_login`, `price`, `tax_in_price`, `discount`, `bank_transfer`, `cash_on_delivery`, `visa`, `paypal`, `bank_draft`, `legal_note1`, `legal_note2`, `legal_note3`, `tariff`, `header_color`, `telephone`, `row_items`, `left_banner`, `right_banner`, `welcome_banner`, `ecommerce_status`, `shipping_costs`, `free_shipping`, `title_note1`, `title_note2`, `title_note3`, `email`) VALUES 
  (1, 1, 'default', 0, NULL, NULL, 1, 2, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 2, NULL, NULL, NULL, 1, 0, 0, NULL, NULL, NULL, 'your@email.com');


UPDATE `db_version` SET `version_number` = '4.6.1';

COMMIT;
