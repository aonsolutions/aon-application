import { CARPETA_A_CONTABILIZAR, CARPETA_CONTABILIZADOS, CARPETA_FISCAL, bidoq } from "./aon-documental.js";
import { AVAILABLE_OPTIONS, MULTIPLE_DOWNLOAD_OPTION, ADD_NOTE_OPTION, MULTIPLE_DELETE_OPTION } from './toolbar_options.js';
import { getFileExtensionsConfig } from './upload.js';

const ITEMS_PER_PAGE = 50;
const TYPES = {
    'sent': 'Enviado',
    'received': 'Recibido'
};
const intersectionObserverIsSupported = "IntersectionObserver" in window;

export function getNumberOfColumns() {
    // Número de columnas base para la tabla, dependiendo de la carpeta seleccionada, etc. puede tener más o menos columnas
    let numberOfColumns = 8;

    if (window.selectedFolderHasSubfolders) {
        $('#subfolder_column').removeClass('d-none');

        numberOfColumns += 1;
    }

    if (parseInt(window.folder) === CARPETA_FISCAL) {
        $('#model_column, #year_column, #period_column').removeClass('d-none');

        numberOfColumns += 3;
    }

    return numberOfColumns;
}

export function selectedFolderHasSubfolders() {
    const selectedFolderData = window.folders.find((currentFolder) => {
        return parseInt(currentFolder.carpetaID) === parseInt(window.folder);
    });

    const hasSubfolders = (typeof selectedFolderData !== 'undefined' && selectedFolderData.subcarpetas.length);

    return hasSubfolders;
}

export function formatDocumentData(document, foldersByID, subfolders) {
    return {
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
    };
}

export function getDocumentsTableDOM(list) {
    let documentsTableDOM = '';

    $.each(list, function(i, document) {
        const allowedOptions = getAllowedOptions(document);

        documentsTableDOM+= '<tr class="show_doc_container" data-allowed_options="' +  allowedOptions.join(',')+ '" data-id="' + document.id + '" data-type="' + document.type + '" data-file_name="' + document.file_name + '" data-tags="' + document.tags.map((tag) => tag.id).join(',') + '">';

            // Columna para seleccionar documentos
            documentsTableDOM+= `<td>
                        <div class="form-check form-check-inline mr-0 d-inline-block align-top">
                            <input class="select_doc form-check-input position-static" type="checkbox" aria-label="Seleccionar documento">
                        </div>
                    </td>`;

            // Recorremos los <td>
            $.each(document, function(key, val) {
                switch (key) {
                    case 'id': // No mostramos el ID en la tabla
                        break;
                    case 'url': // No mostramos la URL en la tabla
                        break;
                    case 'stored_file_name': // No mostramos el nombre almacenado del archivo en la tabla
                        break;
                    case 'tags':
                        documentsTableDOM+= `<td>
                                <span>`;
                                    if ($.isArray(val) && val.length) {
                                        const tags = val.map((tag) => `<a class="documentTags pointer" data-tag="${tag.id}">#${tag.name}</a>`).join('<br>');

                                        documentsTableDOM += tags;
                                    }
                                documentsTableDOM += `</span>
                            </td>`;
                        break;
                    case 'date':
                        const formattedDate = getFormattedDate(val);
                        documentsTableDOM+= `<td class="show_doc pointer">
                                    <span>${formattedDate}</span>
                                </td>`;
                        break;
                    case 'file_name':
                        const truncatedFileName = truncateString(val);
                        documentsTableDOM+= `<td class="show_doc pointer" title="${val}">
                                    <span>${truncatedFileName}</span>
                                </td>`;
                        break;
                    case 'category':
                        const category = (val === null) ? '' : val.name;

                        documentsTableDOM+= `<td class="show_doc pointer">
                                    <span>${category}</span>
                                </td>`;
                        break;
                    case 'type':
                        const type = TYPES[document.type];

                        documentsTableDOM+= `<td class="show_doc pointer">
                                    <span>${type}</span>
                                </td>`;
                        break;
                    case 'model':
                    case 'year':
                    case 'period':
                        if (parseInt(window.folder) === CARPETA_FISCAL) {
                            let value = '';

                            if (val !== null) {
                                // Comprobamos que la propiedad que estamos recorriendo es la correspondiente al trimestre y que el valor sea un número
                                value = (key === 'period' && !isNaN(val)) ? `${val}T` : val;
                            }

                            documentsTableDOM+= `<td class="show_doc pointer">
                                        <span>${value}</span>
                                    </td>`;
                        }
                        break;
                    case 'read':
                        const read = (document.type === 'received' && parseInt(val) !== 0) ? '<i class="material-icons">check</i>' : '';

                        documentsTableDOM+= `<td class="text-center show_doc pointer">
                                    <span>${read}</span>
                                </td>`;
                        break;
                    case 'subfolder':
                        if (window.selectedFolderHasSubfolders) {
                            const subfolder = (val !== null) ? val : '';

                            documentsTableDOM+= `<td class="show_doc pointer">
                                        <span>${subfolder}</span>
                                    </td>`;
                        }
                        break;
                    default:
                        documentsTableDOM+= `<td class="show_doc pointer">
                                    <span>${val}</span>
                                </td>`;
                        break;
                }
            });

        // Cerramos el <tr>
        documentsTableDOM+= '</tr>';
    });

    return documentsTableDOM;
}

export function getDocumentsCardsDOM(list) {
    const { iconsByExtension } = getFileExtensionsConfig();

    const documentsCardsDOM = list.map((card) => {
        const formattedDate = getFormattedDate(card.date);
        const storedFileNameSplitted = card.stored_file_name.split('.');
        const extension = storedFileNameSplitted[storedFileNameSplitted.length - 1].toLowerCase();
        const docIcon = iconsByExtension[extension];
        const truncatedUploadedBy = truncateString(card.uploaded_by);

        return `
            <div class="show_doc_container" data-id="${card.id}" data-type="${card.type}">
                <div class="card mb-3 show_doc pointer">
                    <div class="card-header d-flex align-items-start">
                        <ul class="list-inline mb-0 d-flex flex-wrap align-items-center">
                            <li class="list-inline-item">
                                <i class="material-icons align-middle">${docIcon}</i>
                            </li>
                            <li class="list-inline-item font-weight-bold">
                                <span class="align-middle">${card.category.name}</span>
                            </li>
                            <li class="list-inline-item" title="${card.uploaded_by}">
                                <span class="align-middle">${truncatedUploadedBy}</span>
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
                            ${card.file_name}
                        </p>
                    </div>
                </div>
            </div>
        `;
    }).join('');

    return documentsCardsDOM;
}

export function getFoldersByID(folders) {
    let subfolders = {};
    let foldersByID = {};

    // Recorremos las carpetas para almacenar en una variable todas las subcarpetas del cliente
    for (let i = 0; i < folders.length; i++) {
        const currentFolder = folders[i];

        foldersByID[currentFolder.carpetaID] = currentFolder;

        if (currentFolder.subcarpetas.length) {
            for (let j = 0; j < currentFolder.subcarpetas.length; j++) {
                const subfolder = currentFolder.subcarpetas[j];

                subfolders[subfolder.subcarpetaID] = subfolder.subcarpeta;
            }
        }
    }

    return {foldersByID, subfolders}
}

export const getList = async (page_this = 1) => {
    const folder        = new URLSearchParams(window.location.search).get('folder');
    const tagID         = new URLSearchParams(window.location.search).get('tag');
    const uploadButton  = window.parent.document.getElementById('aonDocumentalToolbarSubirButton');

    if (intersectionObserverIsSupported) {
        $('#loader').removeClass('d-none');
    }

    //
    // Seleccionamos en el sidenav la opción de la que vamos a obtener los datos (necesario por si se vuelve atrás en el navegador)
    //
        // Si estamos filtrando por tags, deseleccionamos todas las opciones del sidenav
        if (tagID !== null) {
            window.aonDocumental.unselectOptions();
        } else {
            let folderName = '';

            if (folder === 'pendientes' || folder === 'recientes') {
                folderName = (folder === 'pendientes') ? 'Pendientes' : 'Recientes';
            } else {
                // Obtenemos los datos de la carpeta seleccionada
                const selectedFolderData = window.folders.find((currentFolder) => {
                    return parseInt(currentFolder.carpetaID) === parseInt(folder);
                });

                // Obtenemos el nombre de la carpeta seleccionada, el cual se utiliza en el ID de la opción
                folderName = selectedFolderData.carpeta;
            }

            // Obtenemos el sidenav
            const sidenav = window.parent.document.getElementById(window.aonDocumental.getId() + 'Sidenav');

            // Con el ID del sidenav y el nombre de la carpeta obtenemos el ID de la opción
            const selectedOptionID = sidenav.id + folderName;

            // Comprobamos si la opción se encuentra ya seleccionada, si no, la seleccionamos
            if (window.aonDocumental.selected !== selectedOptionID) {
                window.aonDocumental.selectOption(folderName);
            }
        }

    // Borramos las opciones de la barra de herramientas
    window.aonDocumental.removeToolbarOptions(AVAILABLE_OPTIONS.map((option) => option.name));

    //
    // Si no mostramos la carpeta Pendientes
    //
    if (folder !== 'pendientes' && folder !== 'recientes') {
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
    } else {
        // Quitamos el boton de subir
        window.aonDocumental.removeToolbarOptions(['Subir']);
    }

    if (typeof window.folders !== 'undefined') {
        try {
            // Hacemos una petición a bidoq para obtener los documentos de la carpeta seleccionada
            const defaultRequestData = {
                "method"    : "list_docs",
                "carpeta"   : folder,
                "pagina"    : page_this - 1,
                "limit"     : ITEMS_PER_PAGE
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

                        const list = documents.map((document) => formatDocumentData(document, window.foldersByID, window.subfolders));

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
// Renderizamos el listado a partir de los datos obtenidos
//
export const renderList = (data) => {
    const list = data.list;

    // Renderizamos la tabla para el modo de escritorio
    renderTable(list);

    // Renderizamos las tarjetas para el modo responsive
    renderCards(list);

    if (intersectionObserverIsSupported) {
        $('#loader').addClass('d-none');

        // Comprobamos que el observer no haya sido ya inicializado y que además el listado obtenido tenga más de una página
        if (typeof window.observer === 'undefined' && list.length && data.total_page > 1) {
            window.observer = initInfiteScrollObserver(['#intersectionObserverTarget']);
        }
    } else {
        renderPagination(data);
    }
}

function renderTable(list) {
    // Recorremos los datos a mostrar
    let tbody = '';

    if (list.length) {
        tbody += getDocumentsTableDOM(list);
    } else {
        tbody+= '' +
            '<tr>'+
                '<td colspan="' + window.numberOfColumns + '">'+
                    '<div class="row">'+
                        '<div class="col-12 text-center">'+
                            'No existen datos para mostrar'
                        '</div>'+
                    '</div>'+
                '</td>'
            '</tr>';
    }

    if (intersectionObserverIsSupported) {
        $('.table tbody').append(tbody);
    } else {
        // Agregamos el cuerpo de la tabla
        $('.table tbody').html(tbody);
    }
}

function renderCards(list) {
    let cards = '';

    if (list.length) {
        cards = getDocumentsCardsDOM(list);
    } else {
        cards = '<div class="text-center">No existen datos para mostrar</div>';
    }

    if (intersectionObserverIsSupported) {
        $('#doc_cards_list').append(cards);
    } else {
        $('#doc_cards_list').html(cards);
    }
}

function initInfiteScrollObserver(targetSelector) {
    // Almacenamos en una variable global el tiempo transcurrido entre una intersección y otra
    window.previousIntersectingTime = undefined;

    // Devolvemos el objeto para comprobar si el IntersectionObserver ha sido ya creado
    let observer = undefined;

    // Obtenemos el elemento que queremos observar
    const elementToObserve = document.querySelector(targetSelector);

    // Definimos las opciones que tendrá el observer
    const options = {
        root: document, // Indicamos que el elemento raíz es el document para que la propiedad rootMargin funcione en el iframe
        rootMargin: '0px 0px 100px 0px', // Especificamos un margin-bottom para que se ejecute el callback x píxeles antes de que el elemento sea visible
        threshold: 0 // Establecemos qué % del elemento debe ser visible para que se ejecute el callback
    };

    // Si existe el elemento a observar
    if (elementToObserve !== null) {
        // Creamos el observer pasando la función callback que se ejecutará cada vez que el elemento sea visible, junto con las opciones
        observer = new IntersectionObserver(getMoreDocsToScroll, options);

        // Empezamos a observar el elemento
        observer.observe(elementToObserve);
    }

    return observer;
}

async function getMoreDocsToScroll([entry], observer) {
    // Comprobamos si es la primera vez que el scroll se acerca al elemento que estamos observando
    const firstTimeIntersecting = (typeof window.previousIntersectingTime === 'undefined');
    // Creamos una variable para comprobar si ha transcurrido el tiempo suficiente entre una intersección y otra como para volver a ejecutar la petición
    let intersectionDelayed = false;

    // Si no es la primera vez que el scroll se acerca al elemento, comprobamos el tiempo transcurrido entre la intersección anterior y esta
    if (!firstTimeIntersecting) {
        const intersectionTimeDiff = entry.time - window.previousIntersectingTime;

        // Comprobamos que el margen de tiempo entre una intersección y otra no sea demasiado pequeño, de este modo evitamos ejecutar la petición más de una vez en poco tiempo
        intersectionDelayed = (intersectionTimeDiff >= 500);
    }

    // Si el scroll se acerca al elemento que estamos observando, comprobamos que haya pasado suficiente tiempo entre la intersección anterior y esta
    if (entry.isIntersecting && (firstTimeIntersecting || intersectionDelayed)) {
        window.previousIntersectingTime = entry.time

        // Indicamos que queremos solicitar la siguiente página
        window.aonDocumentalContainer.page += 1;

        // Obtenemos los documentos de la siguiente página
        const list = await getList(window.aonDocumentalContainer.page);

        // Añadimos los nuevos elementos a la lista
        renderList(list);

        // Si la página solicitada es la última
        if (list.page >= list.total_page) {
            // Dejamos de observar todos los elementos
            observer.disconnect();
        }
    }
}

//
// Renderizar el paginado
//
function renderPagination(data) {
    // Obtenemos el DOM de la paginación
    const pagination = getPaginationDOM(data.page, data.total_page)

    // Creamos el contenedor con la paginación para la tabla
    const tablePagination = `
        <tr>
            <td colspan="${window.numberOfColumns}">
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

    // Renderizamos la paginación en el pie de la tabla
    $('.table tfoot').html(tablePagination);

    $('#doc_cards_footer').html(cardsPagination);
}

// Obtener el DOM de la paginación
function getPaginationDOM(page, page_total){
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

function truncateString(string) {
    const FILE_NAME_MAX_LENGTH = 35;

    return (string.length > FILE_NAME_MAX_LENGTH) ? `${string.substr(0, FILE_NAME_MAX_LENGTH)}&hellip;` : string;
}
