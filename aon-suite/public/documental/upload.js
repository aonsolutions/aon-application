/*

 * Subir documentos de la plataforma - JL
 * 
 * 
 * Documentacion mirada:
 * https://developer.mozilla.org/es/docs/Web/API/FileReader
 * https://www.javascripture.com/FileReader

    -- PRUEBASSSSSSSSSSSSSSS
    ESTA MONTADO DE PRUEBAS, TENDRA QUE CAMBIARSE
    -- PRUEBASSSSSSSSSSSSSSS

 */

if (typeof jQuery === 'undefined') {
    throw new Error('La subida de documentos requiere jQuery');
}

import { BIDOQ_CLIENTE_ID, BIDOQ_TIPO_USUARIO, bidoq } from "./aon-documental.js";
import { getList, renderList, formatDocumentData, getDocumentsTableDOM, getDocumentsCardsDOM } from './table.js';

export function UploadDocumentos(){
    //  
    // Configuracion 
    //  
        // Obtenemos las extensiones de archivo que tenemos en cuenta
        const fileExtensionsConfig = getFileExtensionsConfig();
        const {
            notAllowedByUserType: extNoAceptadas,
            havePreview: extPrevios,
            havePreviewIconsByExtension: extPreviosIcono
        } = fileExtensionsConfig;
        let { allowed: extAceptadas } = fileExtensionsConfig;
        var drop            = true;                                         // Si permetimos la opcion de arrastrar documentos
        var cantidad        = 50;                                           // Cantidad maxima de documentos permitidos
        var peso            = 5;                                            // Peso maximo de un documento permitido en MB
        var extNoAceptada   = '<i class="material-icons">clear</i>';        // Extenciones que se permiten en algunos casos
        var errorMensaje    = {                                             // Errores que damos
            usuario                 : 'No disponemos del campo id.',
            usuarioTipo             : 'No disponemos del campo sube.',
            usuarioComprobarEstado  : 'No se puede comprobar el estado.',
            usuarioPendientePago    : 'No se puede subir documentación, se encuentra en estado de Pendiente de pago.',
            usuarioInactivo         : 'No se puede subir documentación, se encuentra en estado Inactivo.',
            cantidad                : 'No se pueden subir más de '+cantidad+' documentos a la vez.',
            extencion               : 'No es una extensión permitida',
            peso                    : 'El tamaño máximo de subida es '+peso+'MB. Por favor, cambia la resolución de los documentos o escanee el documento de nuevo  con una resolución menor (recomendamos 150ppp). Los documentos mayores que '+peso+'MB no son subidos por seguridad. Gracias',
            error                   : 'No se puede subir o se ha subido corrupto'
        };
        var comprobacionMensaje    = {                                      // Errores que damos
            carpetaContable : "Recuerda que si lo que quieres es subir documentos para que los contabilicemos debes subirlo a la carpeta 'A CONTABILIZAR'"
        };

    //
    // Variables generales
    //
        var documentos      = '';                                           // Donde cargaremos los documentos
        var formulario      = '';                                           // Donde cargaremos los demas datos del formulario
        var url             = '';                                           // Donde subimos
        var usuarioID       = BIDOQ_CLIENTE_ID;                             // Al usuario que le subimos
        var usuarioTipoID   = BIDOQ_TIPO_USUARIO;                           // El tipo de usuario que sube
        var previoCantidad  = 0;                                            // Donde iremos controlando la cantidad de previos que tenemos
        var reloadTabla     = '';                                           // Tabla que recarga
        var ajaxTabla       = '';                                           // Tabla que recarga por ajax el listado
        var ticketPrevio    = false;                                        // Si es para los ticket solo mostraremos previos
    
    //
    // Miramos si tenemos que cargar el listado de documentos por ajax
    //
        $('html').find('input').each(function (){
            if(this.type === 'file'){
                ajaxTabla = this.dataset.ajaxTabla;
                // Cargar la tabla con los documentos por ajax
                if(ajaxTabla !== undefined){
                    CargarDivAjaxDocumentos(ajaxTabla, '../ajax/documentos_asesores_ajax.php');
                }
            }
        });
        
    //
    // Cogemos los documentos
    //
        // Subir por drop
        if(drop){
            // Evento que se ejecuta cuando se arrastran ficheros a la pagina
            $('html').on('dragover', function(e) {
                e.preventDefault();
                e.stopPropagation();
            }).on('dragenter', function(e) {
                // Bloquemos para controlar, el dragleave
                arrastrasArchivo();
                e.preventDefault();
                e.stopPropagation();
            }).on('dragleave', function(e) {
                // Volvemos a hacer visible la pagina
                sueltasArchivo();
                e.preventDefault();
                e.stopPropagation();
            }).on("drop",function(e){
                e.preventDefault();  
                e.stopPropagation();
                // Volvemos a hacer visible la pagina
                sueltasArchivo();
                if(e.originalEvent.dataTransfer){
                    if(e.originalEvent.dataTransfer.files.length) {
                        // Pasamos a subir la documentacion
                        subirDocumentos(e.originalEvent.dataTransfer, $('#upload-file')[0]);
                    }   
                }
            });
        }

        // Subir por input
        $(document).on('change', '#upload-file', function() {
            subirDocumentos(this, this);
        });
    
        // Cogemos los documentos que se quieren subir
        function subirDocumentos(documentosParaSubir, input){
            documentos  = documentosParaSubir.files;
            url         = input.closest("form").action;
            formulario  = input.closest("form");
            reloadTabla = input.dataset.reloadTabla;
            
            // Mirar si tenemos ID, solo lo cogemos una vez
            if( usuarioID === '' ){
                $(formulario).find('input').each(function () {
                    
                    if(this.id === 'upload-solo-precarga' && this.value){
                        // Es un ticket solo mostramos previos sin subir
                        ticketPrevio = true;
                    }
                    
                    if(this.id === 'id'){
                        // Si existe metemos el valor
                        usuarioID = this.value;
                        // El tipo de usuario que sube
                        usuarioTipoID = $(this).data('sube');
                    }
                });
            }

            if(ticketPrevio){
                // Todo correcto ya que solo mostramos el previo
                comprobar();
                return 0;
            }
            
            // Si sigue sin tener valor
            if( usuarioID === '' ){
                erroresAlert(errorMensaje.usuario);
                return 0;
            }
            
            // Si no tenemos el tipo de usuario que sube
            if( usuarioTipoID === '' || usuarioTipoID === undefined ){
                erroresAlert(errorMensaje.usuarioTipo);
                return 0;
            }
            
            // Todo correcto // NO COMPROBAMOS SESSION
            comprobar();
            /*
            // Hacemos comprobacion del estado del cliente
            $.ajax({
                type            : "POST",
                url             : "../function/upload_comp.php",
                data: { 
                    id          : usuarioID
                },
                dataType        : "json",
                success: function(data) {
                    if(data.succes){
                        // Todo correcto
                        comprobar();
                    } else {
                        // Ha dado algun error
                        // Errores que devuelve:
                        //      usuarioComprobarEstado
                        //      usuarioPendientePago
                        //     usuarioInactivo
                        //
                        erroresAlert(errorMensaje[data.mensaje]);
                        return 0;
                    }
                },
                error:function (xhr, ajaxOptions, thrownError){
                    //alert(xhr.status);
                    //alert(thrownError);
                }
            });
            */
        };
    
    //
    // Comprobaciones
    //  
        // Pasamos a hacer las comprobaciones
        function comprobar(){
            // Miramos primero si la cantidad de documentos es correcta
            if(documentos.length > cantidad){
                erroresAlert(errorMensaje.cantidad);
                return 0;
            }

            // Llamamos para montar donde mostraremos los previos
            montarDivPrevios();
            // Precargamos los previos de los documentos a subir
            $.each(documentos, function (index, documento) {
                // Comprobar cuanto pesa
                if(documento.size > (peso * 1024 * 1024)){
                    // Supera el peso máximo permitido
                    errores(documento, errorMensaje.peso);
                    return;
                }
                
                if(extNoAceptadas[usuarioTipoID]){
                    // Extenciones que no se le permiten a este tipo de usuario
                    $.each( extNoAceptadas[usuarioTipoID], function( key, valueNoAceptadas ){
                        if(extAceptadas.includes(valueNoAceptadas)){
                            // Quitamos la extencion
                            extAceptadas = $.grep(extAceptadas, function(valueAceptadas) {
                                return valueAceptadas != valueNoAceptadas;
                            });
                        }
                    });
                }
                
                if(jQuery.inArray(documentoExtension(documento), extAceptadas) === -1) {
                    // No es permitida esa extension
                    errores(documento, errorMensaje.extencion);
                    return;
                }
                
                // Todo correcto
                previos(documento);
            });
        }
    
    //
    // Cargar previos ( Desde aquí llamamos para subir )
    //
        function previos(documento){
            var reader = new FileReader();                                  // Usamos el objeto FileReader que permite a las aplicaciones web leer ficheros
            
            reader.onload = function(){
                $('.files').append(montarPrevio(documento));                // Agregar donde meteremos el previo
                // Dependiendo del tipo de documento, mostramos el previo o un archivo predefinido
                if(jQuery.inArray(documentoExtension(documento), extPrevios) !== -1) {
                    // Montar el previo. es una extencion que permite mostrar el previo
                    var dataURL = reader.result;
                    var output  = $('#previo-'+previoCantidad)[0];          // Que retorne el HTML DOM Object
                    output.src  = dataURL;
                }
                // Subimos el documento, si no es solo previo
                if(!ticketPrevio){
                    subir(documento, previoCantidad);
                }
                // Sumamos el previoCantidad, para coger donde mostramos el siguiente previo
                previoCantidad++;
            };

            reader.onerror = function(event) {
                alert("EXISTE UN ERROR: " + event.target.error.code);
            };

            reader.readAsDataURL(documento);
        }

        function montarDivPrevios(){
            var cerrarPrevio = ticketPrevio ? '' : `
                <div class="col-12">
                    <div id="upload-cerrar" class="btn btn-warning float-right boton-cancelar-archivos">
                        <i class="material-icons align-middle">clear</i>
                        <span>Cerrar</span>
                    </div>
                </div>`;
            // Miramos si tenemos que cargar o no
            if ($('.files')[0] == undefined ){
                var div = 
                '<div class="row pb-3">' +
                    '<div class="col-12 p-3 div-archivos fondo-gris">' +
                        '<div class="row">' +
                            '<div class="files col-12 row p-3"></div>' +
                            cerrarPrevio +
                        '</div>' +
                    '</div>' +
                '</div>';
                // Cargamos
                $('.archivos-previos').html(div);
            }
        }

        function montarPrevio(documento, correcto = true, mensaje = ''){
            // El id que llevara el previo
            var id = 'previo-'+previoCantidad;
            // Donde meteremos el tipo de previo que es
            var tipoprevio = '';
            // Comprobamos que tipo de previo cargamos   
            tipoprevio = montarPrevioType(id, documento, correcto);
            // Si cargamos la barra o la referencia de la imagen para los ticket
            var barra_o_referencia = MontarBarraReferencia(documento, correcto);
            // Si el previo tiene enlace o no
                //var previo = ticketPrevio ? '<span class="upload-previa upload-ruta-'+previoCantidad+'">' + tipoprevio + '</span>' : '<a href="" title="'+documento.name+'" class="upload-previa upload-ruta-'+previoCantidad+'" target="_blank">' + tipoprevio + '</a>';
            var previo = '<span title="'+documento.name+'" class="upload-previa upload-ruta-'+previoCantidad+'">' + tipoprevio + '</span>';
            // Cargamos todo el contenido del previo
            var previo =
            '<div class="row col-6 template-download upload-subido fadeIn">' +
                // Previo y el cargando
                '<div class="col-5">' +
                    // Previo de la imagen
                    '<span class="upload-previo">' +
                        previo +
                    '</span>' +
                    // Barra cargando - donde va o la referencia de la imagen para los ticket
                    barra_o_referencia +
                '</div>' +
                // Mensajes una vez subido correctamente
                '<div class="col-7">' +
                    '<p class="upoload-name" title="'+documento.name+'">' +
                        documento.name +
                    '</p>' +
                    '<span class="size">' +
                        formatearSizeUnits(documento) +
                    '</span>' +
                    '<p class="upload-mensaje mensaje-'+previoCantidad+'">' +
                        mensaje +
                    '</p>' +
                    '<div class="correcto-'+previoCantidad+'">' +
                    '</div>' +
                '</div>' +
            '</div>';
            // Retornamos el div del previo
            return previo;
        }
        
        function montarPrevioType(id, documento, correcto){
            // lo que retornamos
            var metemosPrevio = '';
            if(correcto){
                // Si es una extencion que dejamos mostrar el previo
                if(jQuery.inArray(documentoExtension(documento), extPrevios) !== -1) {
                    // Es una imagen
                    if (documentoType(documento) === 'image'){
                        metemosPrevio = '<img id="'+id+'">';
                    }
                    // si es PDF
                    if (documentoType(documento) === 'application'){
                        metemosPrevio = '<div class="upload-pdf"><iframe id="'+id+'" class="upload-iframe" src="" allowfullscreen="" frameborder="0"></iframe></div>';
                    }
                } else {
                    // No podemos mostrar el previo, mostramos la extencion del documento a subir
                    metemosPrevio = '<div class="previo-mal"><i class="material-icons">'+documentoIcono(documento)+'</i></div>';
                }
            } else {
                // No se permite subir este documento
                metemosPrevio = '<div class="previo-mal">'+extNoAceptada+'</div>';
            }
            // Retornamos
            return metemosPrevio;
        }
        
        function MontarBarraReferencia(documento, correcto){
            var resultado = '';
            if(ticketPrevio){
                // Es solo el previo con la imagen
                // Es una imagen
                if (documentoType(documento) === 'image' && correcto){
                    // Cargamos la referencia de la imagen simplemente si es una imagen
                    resultado = 
                        '<div class="upload-ticket-img naranja" data-ticket-img="IMG-'+documento.size+'-'+documento.name+'" title="Agregar imagen dentro en el texto del comentario" >' + '\
                            IMG-'+previoCantidad +
                        '</div>';
                }
            } else {
                // Cargamos la barra por que subimos el documento
                resultado = 
                    '<div class="progress">' +
                        '<div id="progressBar-'+previoCantidad+'" class="progress-bar" role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"></div>' +
                    '</div>';
            }
            // Devolvemos lo que cargamos
            return resultado;
        }
        
    //
    // Empezamos el proceso de subir el documento
    //
        async function subir(documento, recorrido){
            // recorrido = previoCantidad, para saber que documento estamos subiendo
            var barra = $('#progressBar-'+recorrido);                       // Barra cargando - donde la metemos el recorrido de la barra
            // Datos del formulario
            var formData = new FormData();
            formData.append('documento', documento);                        // Agregamos el documento
            // recorremos los valores del formulario para agregar
            $(formulario).find('input, select').each(function () {
                if(this.type !== 'file'){                                   // Siempre y cuando no sea file el campo
                    var campoAgregar = updateForm(this);
                    // Agregamos el campo siempre tengamos un valor
                    if(campoAgregar !== undefined){
                        formData.append(campoAgregar.elName, campoAgregar.elValue); // Agregamos los demas datos del formulario
                    }
                }
            });
            
            // Cambios para AON
            // no tenemos en cuenta el formData
            // Cogemos solo el documento
            //
            // Le quitamos la extensión al fichero para quedarnos solo con el nombre
                let uploadError = false;
                let fileName    = documento.name.split('.');
                fileName.pop();
                fileName     = fileName.join('.');
                const filesPromises = [{
                    "image_content": await readFile(documento),
                    "image_name": fileName,
                    "image_type": documentoExtension(documento),
                    "image_size": documento.size
                }];

                // Barra cargando - Al inicio
                barraBeforeSend(barra);
                // Subimos la barra hasta el 90% simplemente
                let porcentaje = 90;
                barra.text(porcentaje+'%');
                barra.css('width', porcentaje+'%');

                Promise.all(filesPromises).then(async (files) => {
                    // Hacemos una petición a bidoq para subir los archivos seleccionados
                    try {
                        const data = await bidoq({
                            "method"    : "upload",
                            "services"  : new URLSearchParams(window.location.search).get('folder'),
                            "files"     : JSON.stringify(files)
                        });

                        let response = JSON.parse(data);

                        if (typeof response !== 'undefined') {
                            const list = [];

                            for (let i = 0; i < response.length; i++) {
                                const documentResponse = response[i];

                                if (documentResponse.code !== 0) {
                                    uploadError = true;
                                    // Barra cargando - Si tenemos algún tipo de error
                                    barraError(barra);
                                    // Error
                                    erroresPHP(recorrido, documentResponse.message);
                                } else if (window.intersectionObserverIsSupported) {
                                    // Formateamos los datos del documento y lo añadimos a la lista de elementos
                                    const formattedDocumentData = formatDocumentData(documentResponse.datos);

                                    list.push(formattedDocumentData);
                                }
                            }

                            // Si el navegador soporta IntersectionObserver, añadimos a la lista los documentos subidos
                            if (window.intersectionObserverIsSupported) {
                                const documentsTable = getDocumentsTableDOM(list);
                                const documentsCards = getDocumentsCardsDOM(list);

                                if (list.length) {
                                    // Comprobamos si se ha introducido algún filtro de búsqueda
                                    if ($('.aonSearchBox').val().length) {
                                        // Si es así, borramos el filtro de búsqueda y forzamos la recarga del listado
                                        $('.aonSearchBox').val('').trigger('keyup');
                                    } else {
                                        // Si no, simplemente añadimos los documentos subidos al listado
                                        $('#tabla_documentos tbody').prepend(documentsTable);
                                        $('#doc_cards_list').prepend(documentsCards);
    
                                        $('.document-list-empty').remove();
                                    }
                                }
                            }

                            if (!uploadError) {
                               //
                               // Correcto
                               //
                                
                               // Barra cargando - Completado correctamente
                                barraSuccess(barra);
                                // Mensaje de correcto
                                // Tenemos nombre y ruta de bidoq devueltos
                                correctoPHP(recorrido, '<p class="upload-succes">Subido correctamente!!</p>');
                                // Recargar tabla si se pide y el js
                                if(reloadTabla !== undefined){
                                    $(reloadTabla).load(location.href + ' '+reloadTabla, function(){
                                        // Recargamos el popover para los previos de las imagenes
                                        $(".popover_previo").popover('destroy');
                                        $(".popover_previo").popover({
                                            placement: 'right',
                                            trigger: 'hover',
                                            html: true
                                        });
                                    });
                                }
                                // Cargar la tabla con los documentos por ajax
                                CargarDivAjaxDocumentos();
                            }
                        } else {
                            // Barra cargando - Si tenemos algún tipo de error
                            barraError(barra);
                            // Error
                            erroresPHP(recorrido, '2 - Ocurrió un error al intentar subir los documentos');
                        }
                    } catch (error) {
                        // Barra cargando - Si tenemos algún tipo de error
                        barraError(barra);
                        // Error
                        erroresPHP(recorrido, '1 - Ocurrió un error al intentar subir los documentos');
                        console.error('Ocurrió un error: ' + error.message);
                    }
                });
        }
        
    //
    // Cerrar previos
    // 
        $(document).on('click', '#upload-cerrar', function() {
            // Cerramos el previo quitando todo el contenido que tuviera
            $('.archivos-previos').html('');
            // Reseteamos el contador
            previoCantidad = 0;
        });
        
    //
    // Funciones generales
    // 
        function erroresAlert(mensaje) {
            alert(mensaje);
            //swal('Oops', mensaje, 'warning');
        }
        
        function errores(documento, mensaje) {
            $('.files').append(montarPrevio(documento, false, mensaje));    // Agregar donde meteremos el previo
            var barra = $('#progressBar-'+previoCantidad);                  // Barra cargando - Mostrramos error ya que este tipo de documento no se permite subir
            barraError(barra);
            // Sumamos el previoCantidad, para coger donde mostramos el siguiente previo
            previoCantidad++;
        }
        
        function erroresPHP(recorrido, mensaje) {
            $('.mensaje-'+recorrido).append(mensaje);                       // Donde imprimimos el error devuelto por PHP
        }

        function correctoPHP(recorrido, mensaje, ruta = null) {
            $('.correcto-'+recorrido).append(mensaje);                      // Donde imprimimos lo devuelto por PHP
            //$('.upload-ruta-'+recorrido).attr("href", ruta);                // Ruta devueltoa por PHP del previo
        }

        function documentoType(documento){
            // Retornamos el type
            return documento.type.split('/').shift().toLowerCase();
        }

        function documentoExtension(documento){
            // Retornamos la extencion
            return documento.name.split('.').pop().toLowerCase();
        }

        function documentoIcono(documento){
            // Retornamos la extencion
            return extPreviosIcono[documentoExtension(documento)];
        }
        
        function formatearSizeUnits(documento, decimales = 2){
            var bytes = documento.size;
            if (bytes === 0) return '0 Bytes';

            const k     = 1024;
            const dm    = decimales < 0 ? 0 : decimales;
            const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];

            const i = Math.floor(Math.log(bytes) / Math.log(k));

            return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
        }
        
        // Agregar imagen al texto del comentario
        $(document).on('click', '.upload-ticket-img', function() {
            // agregamos la imagen al texto
            if(theEditor) {
                var imagenID     = $(this).data('ticket-img');
                var imagen       = $(this).siblings("span").find("img").attr('src');
                var imgenHtml    = "<img class='"+imagenID+"' src='"+imagen+"' alt='' align='left' width='400'/>";
                // Agregar la imagen el CKEditor 5
                const viewFragment  = theEditor.data.processor.toView( imgenHtml );
                const modelFragment = theEditor.data.toModel( viewFragment );
                theEditor.model.insertContent( modelFragment, theEditor.model.document.selection );
            }
        });
        
    // 
    // Funciones Barra
    // 
        function barraBeforeSend(barra){
            barra.text('0%');
            barra.css('width', '0%');
        }
        
        function barraSuccess(barra){
            barra.css('width', '100%');
            barra.addClass("progress-bar-success");
            barra.text('subido!');
        }
        
        function barraError(barra){
            barra.text('Error!');
            barra.css('width', '100%');
            barra.removeClass("progress-bar-success");
            barra.addClass("progress-bar-danger");
        }
        
    // 
    // Comprobaciones en los formularios
    // 
        function updateForm(campo){
            if(campo.name !== ''){
                var campoValue = '';
                // Cogemos el value dependiendo del tipo de campo que sea
                switch (campo.type) {
                    case 'checkbox':
                        // si el campo es type checkbox
                        campoValue = $(campo).is(':checked') ? true : false;
                    break;
                    case 'radio':
                        // si el campo es type checkbox
                        if ($(campo).is(':checked')){
                            campoValue = campo.value;
                        } else {
                            // Si no esta marcado  no cogemos valor
                            return;
                        }
                    break;
                    default:
                        // Los demas type lo hacesmos normal
                        campoValue = campo.value;
                    break;
                }
                return {
                    elName  : campo.name,
                    elValue : campoValue
                };
            }
        }
        
        $('#upload-carpeta').on('change', function() {
            // Si escogemos la carpeta contable
            var valor = $(this).val();
            if($(this).val() == 15){
                mensajeComprobacion(comprobacionMensaje.carpetaContable);
            }
        });
    
        function mensajeComprobacion(mensaje){
            swal({
                title               : "¿Estás seguro?",
                text                : mensaje,
                type                : "info",
                confirmButtonColor  : "#3aabe1",
                confirmButtonText   : "Entendido"
            });
        }
        
    //
    // Funciones DROP
    //
        function arrastrasArchivo(){
            $('body').css('display','none');
            $('html').addClass('upoload-arrastras-entras');
            if(!$(".upoload-arrastras-entras-mensaje")[0]){
                $( "html" ).append( "<span class='upoload-arrastras-entras-mensaje'>Suelta la documentación <i class='material-icons align-middle'>get_app</i><span>" );
            }
        }

        function sueltasArchivo(){
            $('body').css('display','');
            $(".upoload-arrastras-entras-mensaje").remove();
            $('html').removeClass('upoload-arrastras-entras');
        }
}

export function getFileExtensionsConfig() {
    const iconsByExtension = {
        tiff: 'text_snippet',
        tif: 'text_snippet',
        txt: 'text_snippet',
        rtf: 'text_snippet',
        odt: 'text_snippet',
        doc: 'text_snippet',
        docx: 'text_snippet',
        ods: 'text_snippet',
        xls: 'text_snippet',
        xlsx: 'text_snippet',
        xlsb: 'text_snippet',
        n43: 'text_snippet',
        bmp: 'photo',
        jpg: 'photo',
        jpeg: 'photo',
        png: 'photo',
        gif: 'photo',
        pdf: 'picture_as_pdf',
        zip: 'archive',
        rar: 'archive',
    };

    // Extensiones que se permiten en algunos casos
    const allowed = [
        'bmp', 'jpg', 'jpeg', 'png', 'gif',
        'tiff', 'tif', 'txt', 'rtf', 'odt',
        'doc', 'docx', 'ods', 'xls', 'pdf',
        'xlsx', 'xlsb', 'n43', 'zip', 'rar'
    ];
    // Extensiones no permitidas dependiendo del tipo de usuario
    const notAllowedByUserType = {
        6 : ['zip', 'rar']
    };
    // Extensiones con las que mostramos una previsualización del archivo
    const havePreview = [
        'bmp', 'jpg', 'jpeg', 'png', 'gif',
        'tiff', 'tif', 'pdf'
    ];
    // Iconos que mostramos en lugar de la previsualización según la extensión del archivo
    const havePreviewIconsByExtension = {
        txt  : iconsByExtension['txt'],
        rtf  : iconsByExtension['rtf'],
        odt  : iconsByExtension['odt'],
        doc  : iconsByExtension['doc'],
        docx : iconsByExtension['docx'],
        ods  : iconsByExtension['ods'],
        xls  : iconsByExtension['xls'],
        xlsx : iconsByExtension['xlsx'],
        xlsb : iconsByExtension['xlsb'],
        n43  : iconsByExtension['n43'],
        zip  : iconsByExtension['zip'],
        rar  : iconsByExtension['rar']
    };

    return {
        allowed,
        notAllowedByUserType,
        iconsByExtension,
        havePreview,
        havePreviewIconsByExtension
    };
}

async function CargarDivAjaxDocumentos(id, url) {
    // Recargar tabla y paginado
        try {
            if (!window.intersectionObserverIsSupported) {
                const list = await getList({loading: true});

                renderList(list);
            }
        } catch (error) {
            const list = {
                "list"      : [],
                "total_data": 0,                // cantidad total de elementos
                "total_page": 1,                // total de paginas
                "page"      : 1,                // pagina en la que estamos
                "shown_page": 0 + ' - ' + 0,    // cantidad mostrada por paginas 1 - 10
            }

            renderList(list);

            console.error(error);
        }
}

function readFile(file) {
    return new Promise((resolve, reject) => {
        let reader = new FileReader();

        reader.readAsDataURL(file);

        reader.onload = function() {
            resolve(reader.result.split(',')[1]);
        };

        reader.onerror = function(error) {
            reject(error);
        };
    });
}