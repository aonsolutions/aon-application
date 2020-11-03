import { CARPETA_A_CONTABILIZAR, CARPETA_CONTABILIZADOS, CARPETA_FISCAL, bidoq } from "./aon-documental.js";
import { AVAILABLE_OPTIONS, MULTIPLE_DOWNLOAD_OPTION, ADD_NOTE_OPTION, MULTIPLE_DELETE_OPTION } from './toolbar_options.js';

const ITEMS_PER_PAGE = 10;
const TYPES = {
    'sent': 'Enviado',
    'received': 'Recibido'
};

export const getList = async (page_this = 1) => {
    // Actualizamos la variable global que almacena el número de página actual
    window.page = page_this;

    const uploadButton = window.frameElement.ownerDocument.getElementById('aonDocumentalToolbarSubirButton');

    window.aonDocumental.removeToolbarOptions(AVAILABLE_OPTIONS.map((option) => option.name));

    // Si existe el botón de subir documentos y estamos en la carpeta "Contabilizados", lo eliminamos
    if (parseInt(window.selectedFolder) === CARPETA_CONTABILIZADOS && uploadButton !== null) {
        window.aonDocumental.removeToolbarOptions(['Subir']);
    }

    // Añadimos el botón de subir documentos si no se ha añadido ya y siempre y cuando no estemos en la carpeta "Contabilizados"
    if (parseInt(window.selectedFolder) !== CARPETA_CONTABILIZADOS && uploadButton === null) {
        window.aonDocumental.addToolbarOption('Subir', 'file_upload', () => {
            //$('#upload').trigger('click');
            $('#upload-file').trigger('click');
        }, 'Subir documentos');
    }

    if (typeof window.folders !== 'undefined') {
        const subfolders = {};
        const foldersByID = {};

        // Recorremos las carpetas para almacenar en una variable todas las subcarpetas del cliente
        for (let i = 0; i < window.folders.length; i++) {
            const folder = window.folders[i];

            foldersByID[folder.carpetaID] = folder;

            if (folder.subcarpetas.length) {
                for (let j = 0; j < folder.subcarpetas.length; j++) {
                    const subfolder = folder.subcarpetas[j];

                    subfolders[subfolder.subcarpetaID] = subfolder.subcarpeta;
                }
            }
        }

        try {
            // Hacemos una petición a bidoq para obtener los documentos de la carpeta seleccionada
            const data = await bidoq({
                "method": "list_docs",
                "carpeta": window.selectedFolder,
                "pagina": page_this - 1
            });

            const jsonData = JSON.parse(data).datos;

            return new Promise((resolve, reject) => {
                if (typeof jsonData !== 'undefined') {
                    const documents = jsonData.documentos;

                    if (typeof documents !== 'undefined') {
                        const page_this_element = page_this == 1 ? page_this : ((page_this - 1) * ITEMS_PER_PAGE) + 1;
                        const page_total = page_this_element + documents.length;

                        const list = documents.map((document) => ({
                                "id": document.id,
                                "url": document.image,
                                "date": document.date,
                                "file_name": document.name,
                                "category": (typeof foldersByID[document.service] !== 'undefined') ? foldersByID[document.service].carpeta : '',
                                "subfolder": (document.subcarpeta !== null && typeof subfolders[document.subcarpeta] !== 'undefined') ? subfolders[document.subcarpeta] : '',
                                "model": document.model,
                                "year": document.year,
                                "period": document.period,
                                "type": (document.type == 1) ? 'received' : 'sent',
                                "uploaded_by": document.uploaded_by,
                                "tags": document.tags,
                                "read": document.read
                        }));

                        const paginationList = {
                            "list": list,
                            "total_data": jsonData.total_resultados, // cantidad total de elementos
                            "total_page": jsonData.total_paginas + 1, // total de paginas
                            "page": page_this, // pagina en la que estamos
                            "shown_page": page_this_element + ' - ' + (page_total - 1), // cantidad mostrada por paginas 1 - 10
                        }

                        resolve(paginationList);
                    } else {
                        reject('Ocurrió un error al intentar obtener los documentos');
                    }
                } else {
                    reject('Ocurrió un error al intentar obtener los documentos');
                }
            });
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    } else {
        console.error('Ocurrió un error al intentar obtener las carpetas');
    }
}

//
// Creamos la tabla con los datos a listar
//
export const createTable = (data) => {
    // Simulamos que solo tenga datos la carpeta "A contabilizar"
    const list = data.list;

    // Recorremos los datos a mostrar
    let tbody = '';

    // Número de columnas base para la tabla, dependiendo de la carpeta seleccionada, etc. puede tener más o menos columnas
    let numberOfColumns = 8;

    if (list.length) {
        const selectedFolderObject = window.folders.find((folder) => {
            return parseInt(folder.carpetaID) === parseInt(window.selectedFolder);
        });
        const hasSubfolders = (typeof selectedFolder !== 'undefined' && selectedFolderObject.subcarpetas.length);

        if (hasSubfolders) {
            $('#subfolder_column').removeClass('d-none');

            numberOfColumns += 1;
        }

        if (parseInt(window.selectedFolder) === CARPETA_FISCAL) {
            $('#model_column, #year_column, #period_column').removeClass('d-none');

            numberOfColumns += 3;
        }

        $.each(list, function(i, item) {
            const allowedOptions = getAllowedOptions(item);

            tbody+= '<tr data-allowed_options="' +  allowedOptions.join(',')+ '" data-id="' + item.id + '" data-type="' + item.type + '" data-file_name="' + item.file_name + '" data-tags="' + item.tags.map((tag) => tag.id).join(',') + '">';

                // Columna para seleccionar documentos
                tbody+= `<td>
                            <div class="form-check form-check-inline mr-0 d-inline-block align-top">
                                <input class="select_doc form-check-input position-static" type="checkbox" aria-label="Seleccionar documento">
                            </div>
                        </td>`;

                // Recorremos los <td>
                $.each(item, function(key, val) {
                    switch (key) {
                        case 'id': // No mostramos el ID en la tabla
                            break;
                        case 'url': // No mostramos la URL en la tabla
                            break;
                        case 'tags':
                            tbody+= `<td>
                                        <span>`;
                                            if ($.isArray(val) && val.length) {
                                                const tags = val.map((tag) => `<a href="#">#${tag.name}</a>`).join('<br>');

                                                tbody += tags;
                                            }
                                        tbody += `</span>
                                    </td>`;
                            break;
                        case 'date':
                            // Formateamos la fecha
                            const date = new Date(val * 1000);
                            const year = date.getFullYear();
                            const month = "0" + (date.getMonth() + 1);
                            const day = "0" + date.getDate();
                            const formattedDate = day.substr(-2) + '-' + month.substr(-2) + '-' + year;

                            tbody+= `<td class="show_doc pointer">
                                        <span>${formattedDate}</span>
                                    </td>`;
                            break;
                        case 'type':
                            const type = TYPES[item.type];

                            tbody+= `<td class="show_doc pointer">
                                        <span>${type}</span>
                                    </td>`;
                            break;
                        case 'model':
                        case 'year':
                        case 'period':
                            if (parseInt(window.selectedFolder) === CARPETA_FISCAL) {
                                let value = '';

                                if (val !== null) {
                                    value = (key === 'period') ? `${val}T` : val;
                                }

                                tbody+= `<td class="show_doc pointer">
                                            <span>${value}</span>
                                        </td>`;
                            }
                            break;
                        case 'read':
                            const read = (item.type === 'received' && parseInt(val) !== 0) ? '<i class="material-icons">check</i>' : '';

                            tbody+= `<td class="text-center show_doc pointer">
                                        <span>${read}</span>
                                    </td>`;
                            break;
                        case 'subfolder':
                            if (hasSubfolders) {
                                const subfolder = (val !== null) ? val : '';

                                tbody+= `<td class="show_doc pointer">
                                            <span>${subfolder}</span>
                                        </td>`;
                            }
                            break;
                        default:
                            tbody+= `<td class="show_doc pointer">
                                        <span>${val}</span>
                                    </td>`;
                            break;
                    }
                });

            // Cerramos el <tr>
            tbody+= '</tr>';
        });
    } else {
        tbody+= '' +
            '<tr>'+
                '<td colspan="' + numberOfColumns + '">'+
                    '<div class="row">'+
                        '<div class="col-12 text-center">'+
                            'No existen datos para mostrar'
                        '</div>'+
                    '</div>'+
                '</td>'
            '</tr>';
    }

    // Agregamos el cuerpo de la tabla
    $('.table tbody').html(tbody);

    // Cogemos el paginado que tendía la tabla
    const pagination = createPaginate(data.page, data.total_page);

    // Agregamos el paginado en el footer de la tabla
    const tfoot =
    '<tr>'+
        '<td colspan="' + numberOfColumns + '">'+
            '<div class="row">'+
                '<div class="col-md-6">'+
                    'Mostrando del '+data.shown_page+' de un total de '+data.total_data+' elementos'+
                '</div>'+
                '<div class="col-md-6">'+
                    pagination+
                '</div>'+
            '</div>'+
        '</td>'
    '</tr>';

    // Agregamos el pie de la tabla
    $('.table tfoot').html(tfoot);
}

//
// Crear el paginado
//
function createPaginate(page, page_total){
    let paginate = '<ul class="pagination display-block float-right">';

    // Si tenemos paginas
    if (page > 1){
        paginate+=
            '<li class="pointer">'+
                '<a class="go_page" data-page="1">'+
                    '<i class="material-icons" title="Principio">fast_rewind</i>'+
                '</a>'+
            '</li>'+

            '<li class="pointer">'+
                '<a class="go_page" data-page="'+(page-1)+'">'+
                    '<i class="material-icons" title="Volver">arrow_left</i>'+
                '</a>'+
            '</li>';
    }

    if(page_total > 0){
        paginate+= '<li class="ml-2 mr-2">'+page+'/'+page_total+'</li>';
    }

    if (page < page_total) {
        paginate+=
            '<li class="pointer">'+
                '<a class="go_page" data-page="'+(page+1)+'">'+
                    '<i class="material-icons .display-1" title="Siguiente">arrow_right</i>'+
                '</a>'+
            '</li>'+

            '<li class="pointer">'+
                '<a class="go_page" data-page="'+page_total+'">'+
                    '<i class="material-icons" title="Final">fast_forward</i>'+
                '</a>'+
            '</li>';
    }
    paginate+= '</ul>';

    return paginate;
}

function getAllowedOptions(document) {
    // Por defecto permitimos solo la opción de descargar
    const allowedOptions = [MULTIPLE_DOWNLOAD_OPTION];

    // Si el usuario ha enviado el documento, permitimos opciones adicionales
    if (document.type === 'sent') {
        allowedOptions.push(ADD_NOTE_OPTION);

        // Solo puede eliminar los documentos de la carpeta "A contabilizar" enviados por él mismo
        if (parseInt(window.selectedFolder) === CARPETA_A_CONTABILIZAR) {
            allowedOptions.push(MULTIPLE_DELETE_OPTION);
        }
    }

    return allowedOptions;
}