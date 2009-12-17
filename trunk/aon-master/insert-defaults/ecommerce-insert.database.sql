
USE `aon_master`;

SET FOREIGN_KEY_CHECKS=0;

INSERT INTO `ec_config` (`id`, `active`, `name`, `skin`, `header_img`, `series`, `commerce`, `show_login`, `price`, `tax_in_price`, `discount`, `bank_transfer`, `cash_on_delivery`, `visa`, `paypal`, `bank_draft`, `legal_note1`, `legal_note2`, `legal_note3`, `tariff`, `header_color`, `telephone`, `row_items`, `left_banner`, `right_banner`, `welcome_banner`, `ecommerce_status`, `shipping_costs`, `free_shipping`, `title_note1`, `title_note2`, `title_note3`, `email`) VALUES 
  (1, 1, 'default', 0, NULL, NULL, 0, 2, 0, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '', 2, NULL, NULL, NULL, 0, 0, 0, NULL, NULL, NULL, 'your@email.com');

INSERT INTO `app_param` (name, value) values ('EC_SALES_ALLOWED', 'false');

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
