import { CARPETA_A_CONTABILIZAR, CARPETA_CONTABILIZADOS, CARPETA_FISCAL, bidoq } from "./aon-documental.js";
import { AVAILABLE_OPTIONS, MULTIPLE_DOWNLOAD_OPTION, ADD_NOTE_OPTION, MULTIPLE_DELETE_OPTION } from './toolbar_options.js';

const ITEMS_PER_PAGE = 10;
const TYPES = {
    'sent': 'Enviado',
    'received': 'Recibido'
};

export const getList = async (page_this = 1) => {
    const folder = new URLSearchParams(window.location.search).get('folder');
    const tagID = new URLSearchParams(window.location.search).get('tag');
    const uploadButton = window.parent.document.getElementById('aonDocumentalToolbarSubirButton');

    // Seleccionamos en el sidenav la opción de la que vamos a obtener los datos (necesario por si se vuelve atrás en el navegador)
        const selectedFolderData = window.folders.find((currentFolder) => {
            return parseInt(currentFolder.carpetaID) === parseInt(folder);
        });
        // Obtenemos el nombre de la carpeta seleccionada, el cual se utiliza en el ID de la opción
        const folderName = selectedFolderData.carpeta;
        // Obtenemos el sidenav
        const sidenav = window.parent.document.getElementById(window.aonDocumental.getId() + 'Sidenav');
        // Con el ID del sidenav y el nombre de la carpeta obtenemos el ID de la opción
        const selectedOptionID = sidenav.id + folderName;

        // Comprobamos si la opción se encuentra ya seleccionada, si no, la seleccionamos
        // Comprobamos también que la opción seleccionada no sea "A contabilizar", ya que por ahora cargamos el listado de "Pendientes" como si fuera el listado de la carpeta "A contabilizar"
        if (window.aonDocumental.selected !== selectedOptionID && parseInt(folder) !== parseInt(CARPETA_A_CONTABILIZAR)) {
            window.aonDocumental.selectOption(folderName);
        }

    window.aonDocumental.removeToolbarOptions(AVAILABLE_OPTIONS.map((option) => option.name));

    // Añadimos (si es necesario) la opción de subir documentos
        // Si existe el botón de subir documentos y estamos en la carpeta "Contabilizados", lo eliminamos
        // Si estamos filtrando por TAG eliminamos el boton de subir tambien
        if ((parseInt(folder) === CARPETA_CONTABILIZADOS && uploadButton !== null) || tagID !== null) {
            window.aonDocumental.removeToolbarOptions(['Subir']);
        }

        // Añadimos el botón de subir documentos si no se ha añadido ya y siempre y cuando no estemos en la carpeta "Contabilizados"
        // y no se este filtrando pot TAG
        if (parseInt(folder) !== CARPETA_CONTABILIZADOS && uploadButton === null && tagID === null) {
            window.aonDocumental.addToolbarOption('Subir', 'file_upload', () => {
                $('#upload-file').trigger('click');
            }, 'Subir documentos');
        }

    if (typeof window.folders !== 'undefined') {
        const subfolders = {};
        const foldersByID = {};

        // Recorremos las carpetas para almacenar en una variable todas las subcarpetas del cliente
        for (let i = 0; i < window.folders.length; i++) {
            const currentFolder = window.folders[i];

            foldersByID[currentFolder.carpetaID] = currentFolder;

            if (currentFolder.subcarpetas.length) {
                for (let j = 0; j < currentFolder.subcarpetas.length; j++) {
                    const subfolder = currentFolder.subcarpetas[j];

                    subfolders[subfolder.subcarpetaID] = subfolder.subcarpeta;
                }
            }
        }

        try {
            // Hacemos una petición a bidoq para obtener los documentos de la carpeta seleccionada
            const defaultRequestData = {
                "method"    : "list_docs",
                "carpeta"   : folder,
                "pagina"    : page_this - 1
            };

            const data = (tagID !== null)
                ? await bidoq({...defaultRequestData, tagID})
                : await bidoq(defaultRequestData);

            const jsonData = JSON.parse(data).datos;

            return new Promise((resolve, reject) => {
                if (typeof jsonData !== 'undefined') {
                    const documents = jsonData.documentos;

                    if (typeof documents !== 'undefined') {
                        page_this = jsonData.pagina_actual + 1;

                        // Actualizamos el número de página actual almacenado en el elemento aon-documental
                        window.aonDocumentalContainer.page = page_this;

                        const page_this_real    = page_this == 1 ? page_this : ((page_this - 1) * ITEMS_PER_PAGE) + 1;
                        const page_total        = page_this_real + documents.length;
                        const page_this_element = !documents.length ? documents.length : page_this_real;

                        const list = documents.map((document) => ({
                            "id"                : document.id,
                            "url"               : document.image,
                            "date"              : document.date,
                            "file_name"         : document.name,
                            "stored_file_name"  : document.stored_file_name,
                            "category"          : (typeof foldersByID[document.service] !== 'undefined') ? {
                                "id": document.service,
                                "name": foldersByID[document.service].carpeta
                            } : null,
                            "subfolder"         : (document.subcarpeta !== null && typeof subfolders[document.subcarpeta] !== 'undefined') ? subfolders[document.subcarpeta] : '',
                            "model"             : document.model,
                            "year"              : document.year,
                            "period"            : document.period,
                            "type"              : (document.type == 1) ? 'received' : 'sent',
                            "uploaded_by"       : document.uploaded_by,
                            "tags"              : document.tags,
                            "read"              : document.read
                        }));

                        const paginationList = {
                            list,
                            "total_data": jsonData.total_resultados,                    // cantidad total de elementos
                            "total_page": jsonData.total_paginas + 1,                   // total de paginas
                            "page"      : jsonData.pagina_actual + 1,                   // pagina en la que estamos
                            "shown_page": page_this_element + ' - ' + (page_total - 1), // cantidad mostrada por paginas 1 - 10
                        };

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
    const FILE_NAME_MAX_LENGTH = 35;
    const list = data.list;
    const folder = new URLSearchParams(window.location.search).get('folder');

    // Recorremos los datos a mostrar
    let tbody = '';

    // Número de columnas base para la tabla, dependiendo de la carpeta seleccionada, etc. puede tener más o menos columnas
    let numberOfColumns = 8;

    if (list.length) {
        const selectedFolderData = window.folders.find((currentFolder) => {
            return parseInt(currentFolder.carpetaID) === parseInt(folder);
        });
        const hasSubfolders = (typeof selectedFolderData !== 'undefined' && selectedFolderData.subcarpetas.length);

        if (hasSubfolders) {
            $('#subfolder_column').removeClass('d-none');

            numberOfColumns += 1;
        }

        if (parseInt(folder) === CARPETA_FISCAL) {
            $('#model_column, #year_column, #period_column').removeClass('d-none');

            numberOfColumns += 3;
        }

        $.each(list, function(i, item) {
            const allowedOptions = getAllowedOptions(item);

            tbody+= '<tr class="show_doc_container" data-allowed_options="' +  allowedOptions.join(',')+ '" data-id="' + item.id + '" data-type="' + item.type + '" data-file_name="' + item.file_name + '" data-tags="' + item.tags.map((tag) => tag.id).join(',') + '">';

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
                        case 'stored_file_name': // No mostramos el nombre almacenado del archivo en la tabla
                            break;
                        case 'tags':
                            tbody+= `<td>
                                    <span>`;
                                        if ($.isArray(val) && val.length) {
                                            const tags = val.map((tag) => `<a class="documentTags" href="#" data-tag="${tag.id}">#${tag.name}</a>`).join('<br>');

                                            tbody += tags;
                                        }
                                    tbody += `</span>
                                </td>`;
                            break;
                        case 'date':
                            const formattedDate = getFormattedDate(val);
                            tbody+= `<td class="show_doc pointer">
                                        <span>${formattedDate}</span>
                                    </td>`;
                            break;
                        case 'file_name':
                            const truncatedFileName = (val.length > FILE_NAME_MAX_LENGTH) ? `${val.substr(0, FILE_NAME_MAX_LENGTH)}&hellip;` : val;
                            tbody+= `<td class="show_doc pointer" title="${val}">
                                        <span>${truncatedFileName}</span>
                                    </td>`;
                            break;
                        case 'category':
                            const category = (val === null) ? '' : val.name;

                            tbody+= `<td class="show_doc pointer">
                                        <span>${category}</span>
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
                            if (parseInt(folder) === CARPETA_FISCAL) {
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

    // Obtenemos el paginado
    const pagination = createPaginate(data.page, data.total_page);
    // Creamos el contenedor con la paginación para la tabla
    const tablePagination = `
        <tr>
            <td colspan="${numberOfColumns}">
                <div class="row">
                    <div class="col-md-6">
                        Mostrando del ${data.shown_page} de un total de ${data.total_data} elementos
                    </div>
                    <div class="col-md-6">
                        ${pagination}
                    </div>
                </div>
            </td>
        </tr>
    `;
    // Creamos el contenedor con la paginación para las tarjetas
    const cardsPagination = `
        <div class="row">
            <div class="col-md-6">
                ${pagination}
            </div>
        </div>
    `;

    // Agregamos el pie de la tabla
    $('.table tfoot').html(tablePagination);

    // Creamos las tarjetas para el modo responsive
    createCards(list);

    $('#doc_cards_footer').html(cardsPagination);
}

function createCards(list) {
    const cards = list.map(({id, type, date, category, uploaded_by: uploadedBy, file_name, stored_file_name: storedFileName}) => {
        const formattedDate = getFormattedDate(date);
        const storedFileNameSplitted = storedFileName.split('.');
        const extension = storedFileNameSplitted[storedFileNameSplitted.length - 1].toLowerCase();
        const docIcon = getIconFromDocExtension(extension);

        return `
            <div class="show_doc_container" data-id="${id}" data-type="${type}">
                <div class="card mb-3 show_doc" role="button">
                    <div class="card-header d-flex align-items-start">
                        <ul class="list-inline mb-0 d-flex flex-wrap align-items-center">
                            <li class="list-inline-item">
                                <i class="material-icons align-middle">${docIcon}</i>
                            </li>
                            <li class="list-inline-item font-weight-bold">
                                <span class="align-middle">${category.name}</span>
                            </li>
                            <li class="list-inline-item" title="${uploadedBy}">
                                <span class="align-middle">${uploadedBy}</span>
                            </li>
                        </ul>
                        <ul class="list-inline mb-0 d-flex ml-auto align-items-center">
                            <li class="d-flex list-inline-item ml-auto">
                                <small class="align-middle ml-1">${formattedDate}</small>
                            </li>
                        </ul>
                    </div>
                    <div class="card-body">
                        <p class="card-text">
                            ${file_name}
                        </p>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    $('#doc_cards_list').html(cards);
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
        if (parseInt(document.category.id) === CARPETA_A_CONTABILIZAR) {
            allowedOptions.push(MULTIPLE_DELETE_OPTION);
        }
    }

    return allowedOptions;
}

function getFormattedDate(milliseconds) {
    const date = new Date(milliseconds * 1000);
    const year = date.getFullYear();
    const month = "0" + (date.getMonth() + 1);
    const day = "0" + date.getDate();
    const formattedDate = day.substr(-2) + '-' + month.substr(-2) + '-' + year;

    return formattedDate;
}

function getIconFromDocExtension(extension) {
    let icon = '';

    switch (extension) {
        case 'tiff':
        case 'tif':
        case 'txt':
        case 'rtf':
        case 'odt':
        case 'doc':
        case 'docx':
        case 'ods':
        case 'xls':
        case 'xlsx':
        case 'xlsb':
            icon = 'text_snippet';
            break;
        case 'bmp':
        case 'jpg':
        case 'jpeg':
        case 'png':
        case 'gif':
            icon = 'photo';
            break;
        case 'pdf':
            icon = 'picture_as_pdf';
            break;
        case 'zip':
        case 'rar':
            icon = 'archive';
            break;
        default:
            icon = 'text_snippet';
    }

    return icon;
}
