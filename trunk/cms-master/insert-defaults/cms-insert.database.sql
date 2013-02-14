# SQL Manager 2007 for MySQL 4.2.1.1
# ---------------------------------------
# Host     : 192.168.2.4
# Port     : 3306
# Database : cms-master


SET FOREIGN_KEY_CHECKS=0;

USE `cms-master`;

#
# Data for the `language` table  (LIMIT 0,500)
#

INSERT INTO `language` (`id`, `language`, `description`, `position`, `defaultLanguage`) VALUES 
  (0,0,'Español',0,1);

COMMIT;

#
# Data for the `menu` table  (LIMIT 0,500)
#

INSERT INTO `menu` (`id`, `alias`, `type`, `defaultMenu`) VALUES 
  (1,'DEFAULT',0,1),
  (2,'DEFAULT',1,1),
  (3,'DEFAULT',2,1),
  (4,'DEFAULT',3,1);

COMMIT;

#
# Data for the `header` table  (LIMIT 0,500)
#

INSERT INTO `header` (`id`, `alias`, `menu`, `language_menu`, `language_menu_type`, `css`, `javascript`, `default_`, `banner_category`) VALUES 
  (1,'DEFAULT',1,1,0,'','',1,NULL);

COMMIT;

#
# Data for the `sidebar` table  (LIMIT 0,500)
#

INSERT INTO `sidebar` (`id`, `alias`, `default_`) VALUES 
  (1,'DEFAULT',1);

COMMIT;

#
# Data for the `footer` table  (LIMIT 0,500)
#

INSERT INTO `footer` (`id`, `alias`, `menu`, `default_`) VALUES 
  (1,'DEFAULT',2,1);

COMMIT;

#
# Data for the `section` table  (LIMIT 0,500)
#

INSERT INTO `section` (`id`, `header`, `sidebar`, `footer`, `show_header`, `show_sidebar_left`, `show_footer`, `alias`, `menu`, `show_menu`, `default_`, `show_sidebar_right`, `menu_alt`, `show_menu_alt`, `parent_`, `parent_sidebar_right`, `parent_sidebar_left`) VALUES 
  (1,1,1,1,1,1,1,'DEFAULT',3,1,1,1,NULL,1,NULL,0,0);

COMMIT;

#
# Data for the `album_config` table  (LIMIT 0,500)
#

INSERT INTO `album_config` (`id`, `section`) VALUES 
  (1,1);

COMMIT;

#
# Data for the `article_config` table  (LIMIT 0,500)
#

INSERT INTO `article_config` (`id`, `section`) VALUES 
  (1,1);

COMMIT;

#
# Data for the `config` table  (LIMIT 0,500)
#

INSERT INTO `config` (`id`, `online`, `domain`, `preview_host`, `host`, `ftp_server`, `ftp_user`, `ftp_password`, `ftp_path`, `smtp_server`, `smtp_auth`, `smtp_user`, `smtp_password`, `from_name`, `from_email`, `template`, `footer`) VALUES 
  (1,1,'default.org','preview','www','192.168.3.47','ftpcms','cms2001','/default.org/WEBSITES/www.default.org','192.168.3.46',0,'julio@aonsolutions.es','Garcia','Administrator','info@default.org','default',1);

COMMIT;

#
# Data for the `config_i18n` table  (LIMIT 0,500)
#

INSERT INTO `config_i18n` (`id`, `config`, `language`, `sitename`, `offline_message`, `css`, `javascript`, `description`, `keywords`) VALUES 
  (1,1,0,'Default','<p>Sitio web</p>','','','Sitio web','web');

COMMIT;

#
# Data for the `download_config` table  (LIMIT 0,500)
#

INSERT INTO `download_config` (`id`, `section`) VALUES 
  (1,1);

COMMIT;

#
# Data for the `footer_i18n` table  (LIMIT 0,500)
#

INSERT INTO `footer_i18n` (`id`, `footer`, `language`, `content`) VALUES 
  (1,1,0,'<p>&copy; 2007 Copyright</p>');

COMMIT;

#
# Data for the `generic_page` table  (LIMIT 0,500)
#

INSERT INTO `generic_page` (`id`, `alias`, `active`, `create_date`, `menu`, `section`) VALUES 
  (1,'Index',1,'2008-06-04 10:46:50',0,NULL);

COMMIT;

#
# Data for the `generic_page_i18n` table  (LIMIT 0,500)
#

INSERT INTO `generic_page_i18n` (`id`, `generic_page`, `language`, `title`, `content`, `description`, `keywords`) VALUES 
  (1,1,0,'CMS','Welcome to CMS',NULL,NULL);

COMMIT;

#
# Data for the `header_i18n` table  (LIMIT 0,500)
#

INSERT INTO `header_i18n` (`id`, `header`, `language`, `sitename`, `image`, `content`, `alt`) VALUES 
  (1,1,0,'Default',NULL,'Content header',NULL);

COMMIT;

#
# Data for the `link_config` table  (LIMIT 0,500)
#

INSERT INTO `link_config` (`id`, `section`) VALUES 
  (1,1);

COMMIT;

#
# Data for the `menu_option` table  (LIMIT 0,500)
#

INSERT INTO `menu_option` (`id`, `alias`, `menu`, `sep`, `type`, `level`, `ident`, `active`, `position`) VALUES 
  (1,'Index',3,0,5,NULL,1,1,0),
  (2,'Index2',1,0,5,NULL,1,1,0),
  (3,'Index3',2,0,5,NULL,1,1,0),
  (4,'Index4',4,0,5,NULL,1,1,0);

COMMIT;

#
# Data for the `menu_option_i18n` table  (LIMIT 0,500)
#

INSERT INTO `menu_option_i18n` (`id`, `menu_option`, `language`, `label`, `url`) VALUES 
  (1,1,0,'Index',NULL),
  (2,2,0,'Index',NULL),
  (3,3,0,'Index',NULL),
  (4,4,0,'Index',NULL);

COMMIT;

#
# Data for the `modular_page` table  (LIMIT 0,500)
#

INSERT INTO `modular_page` (`id`, `alias`, `type`, `homepage`, `active`, `section`) VALUES 
  (1,'Index',0,1,1,1);

COMMIT;

#
# Data for the `modular_page_i18n` table  (LIMIT 0,500)
#

INSERT INTO `modular_page_i18n` (`id`, `modular_page`, `language`, `label`) VALUES 
  (1,1,0,'Index');

COMMIT;

#
# Data for the `modular_page_option` table  (LIMIT 0,500)
#

INSERT INTO `modular_page_option` (`id`, `alias`, `modular_page`, `type`, `ident`, `active`, `position`) VALUES 
  (1,'Index_1',1,0,1,1,0);

COMMIT;

#
# Data for the `modular_page_option_i18n` table  (LIMIT 0,500)
#

INSERT INTO `modular_page_option_i18n` (`id`, `modular_page_option`, `language`, `label`) VALUES 
  (1,1,0,'Welcome');

COMMIT;

#
# Data for the `activity_config` table  (LIMIT 0,500)
#

INSERT INTO `activity_config` (`id`, `section`) VALUES 
  (1,1);


SET FOREIGN_KEY_CHECKS=1;
