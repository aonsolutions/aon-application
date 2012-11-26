
USE `aon_master`;

SET FOREIGN_KEY_CHECKS=0;

SET @Domain = 1;

INSERT IGNORE INTO `pcategory` (`id`, `domain`, `name`) VALUES (1, @Domain,'HABITACION') 
	ON DUPLICATE KEY UPDATE `name` = 'HABITACION';

COMMIT;

SET FOREIGN_KEY_CHECKS=1;
