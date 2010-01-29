<?php

$cuantos = 0;

class file_search {
	var $found = array();
	function file_search($files, $dirs = '.', $sub = 1, $case = 0) {
		$dirs = (!is_array($dirs)) ? array($dirs) : $dirs;
		foreach ($dirs as $dir) {
			$dir .= (!ereg('/$', $dir)) ? '/' : '';
			$directory = @opendir($dir);

			while (($file = @readdir($directory)) !== FALSE) {
				if ($file != '.' && $file != '..') {
					if ($sub && is_dir($dir . $file)) {
						$this->file_search($files, $dir . $file, $sub, $case);
					}
					else {
						$files = (!is_array($files)) ? array($files) : $files;
						foreach ($files as $target) {
							$tar_ext = substr(strrchr($target, '.'), 1);
							$tar_name = substr($target, 0, strrpos($target, '.'));
							$fil_ext = substr(strrchr($file, '.'), 1);
							$fil_name = substr($file, 0, strrpos($file, '.'));

							$ereg = ($case) ? 'ereg' : 'eregi';
							if ($ereg($tar_name, $fil_name) && eregi($tar_ext, $fil_ext)) {
								$this->found[] = $dir . $file;
							}
						}
					}
				}
		      }
		}
	}
}

function getTitle($text, &$title) {
	$g = strpos($text, "</title>",1);
	$i = strpos($text,"<title>",1);
	$f = substr($text,$i,$g);
	if ($f) $title = strip_tags($f);
}

function curPageURL() {
	$pageURL  = 'http';
	$pageURL .= "://";
	$pageURL .= $_SERVER["SERVER_NAME"].$_SERVER["REQUEST_URI"];
	$pageURL = eregi_replace("search.php", "", $pageURL);
	return $pageURL;
}

function findInFile($php_file, $php_text) {
	// Obtiene un archivo en una matriz. En este ejemplo usaremos HTTP
	// para obtener el código fuente HTML de una URL.
	$resultado = "";
	$encontrado = FALSE;
	foreach($php_file as $file) {
		$lineas = file($file, FILE_SKIP_EMPTY_LINES);

		// Recorrer nuestra matriz, mostrar el código HTML como código fuente
		// HTML, y los números de línea también.
		$title = "";
		foreach ($lineas as $linea_num => $linea) {
			getTitle($linea, $title);
			$linea_limpia = strip_tags($linea);
			$linea_limpia_normal = trim($linea_limpia); 
			$linea_limpia = strtolower($linea_limpia_normal); 

			if ($linea_limpia) {
				if (strlen($linea_limpia) > 40) {
					if (strstr($linea_limpia, $php_text)) {
						$resultado .= " ... ".eregi_replace("$php_text","<STRONG>$php_text</STRONG>",$linea_limpia_normal)."";
						$encontrado = TRUE;
					}
				}
			}
		}
		if ($encontrado) {
			$encontrado = FALSE;
			echo "<a href='".curPageUrl().eregi_replace("./","",$file)."' style='color:blue;font-size:16px'>".eregi_replace("$php_text","<STRONG>$php_text</STRONG>",$title)."</a><br>";
			echo $resultado." ...<br>";
			echo "<font color=green font-size=10px>".curPageUrl().eregi_replace("./","",$file)."</font><br><br>";
			$resultado = "";
			$GLOBALS["cuantos"] = $GLOBALS["cuantos"] + 1;
		}
	}
}

$cadena = $_REQUEST['busqueda'];

$cadena = trim($cadena); 
$cadena = strtolower($cadena); 

if(!$cadena) { 
	echo "Error en la busqueda<br><br>"; 
	echo " - Debe inidcar al menos criterio de busqueda."; 
}
else {
	if (strlen($cadena) > 3) {
		$find = array('[a-z].html');
		$search = new file_search($find);
		echo "Resultados de la busqueda de <strong>$cadena</strong><br><br>"; 
		foreach ($search as $file) {
			findInFile($file, $cadena);
		}
		echo "Se han encontrado <strong>$cuantos</strong> resultados.<br>"; 
	}
	else {
		echo "Error en la busqueda<br><br>"; 
		echo " - Debe inidcar al menos 4 caracteres."; 
	}
}

?>