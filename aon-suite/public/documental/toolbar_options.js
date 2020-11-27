import { bidoq, CARPETA_A_CONTABILIZAR } from './aon-documental.js';
import { getList, renderList } from './table.js';

export const SINGLE_DOWNLOAD_OPTION = 'singleDownload';
export const MULTIPLE_DOWNLOAD_OPTION = 'multipleDownload';
export const EDIT_OPTION = 'edit';
export const SINGLE_DELETE_OPTION = 'singleDelete';
export const MULTIPLE_DELETE_OPTION = 'multipleDelete';
export const ADD_NOTE_OPTION = 'createNote';
export const VIEW_NOTE_OPTION = 'viewNote';
export const AVAILABLE_OPTIONS = [
    {
        name: SINGLE_DOWNLOAD_OPTION,
        icon: 'file_download',
        title: 'Descargar documento',
        fn: singleDownloadOption
    },
    {
        name: MULTIPLE_DOWNLOAD_OPTION,
        icon: 'file_download',
        title: 'Descargar los documentos seleccionados',
        fn: multipleDownloadOption
    },
    {
        name: EDIT_OPTION,
        icon: 'edit',
        title: 'Editar documento',
        fn: editOption
    },
    {
        name: ADD_NOTE_OPTION,
        icon: 'note_add',
        title: 'Añadir nota a los documentos seleccionados',
        fn: addNoteOption
    },
    {
        name: VIEW_NOTE_OPTION,
        icon: 'sticky_note_2',
        title: 'Ver nota del documento',
        fn: viewNoteOption
    },
    {
        name: SINGLE_DELETE_OPTION,
        icon: 'delete_forever',
        title: 'Eliminar documento',
        fn: singleDeleteOption
    },
    {
        name: MULTIPLE_DELETE_OPTION,
        icon: 'delete_forever',
        title: 'Eliminar los documentos seleccionados',
        fn: multipleDeleteOption
    }
];

export function getAllowedOptions(document, multiple) {
    const download_option = (multiple) ? MULTIPLE_DOWNLOAD_OPTION : SINGLE_DOWNLOAD_OPTION;

    // Por defecto permitimos solo la opción de descargar
    const allowedOptions = [download_option];

    // Si estamos viendo solo un documento, permitimos la opción de editar
    if (!multiple) {
        allowedOptions.push(EDIT_OPTION);
    }

    // Si el usuario ha enviado el documento, permitimos opciones adicionales
    if (document.type === 'sent') {
        const note_option = (multiple) ? ADD_NOTE_OPTION : VIEW_NOTE_OPTION;

        allowedOptions.push(note_option);

        // Solo puede eliminar los documentos de la carpeta "A contabilizar" enviados por él mismo
        if (document.category !== null) {
            if (parseInt(document.category.id) === CARPETA_A_CONTABILIZAR) {
                const delete_option = (multiple) ? MULTIPLE_DELETE_OPTION : SINGLE_DELETE_OPTION;

                allowedOptions.push(delete_option);
            }
        }
    }

    return allowedOptions;
}

function singleDownloadOption() {
    alert('Descargar documento');
}

function multipleDownloadOption() {
    alert('Descargar los documentos seleccionados');
}

function editOption() {
    const { id, type, file_name, tags } = window.currentFile;
    const tagIDs = tags.map((tag) => tag.id);

    // Insertamos en el modal el ID del documento que vamos a editar
    $('#edit_doc_id').attr('value', id)

    // Insertamos en el modal el tipo de documento que vamos a editar ("Enviado" o "Recibido")
    $('#edit_doc_type').attr('value', type);

    // Ocultamos los errores si están visibles
    if (!$('.error-empty-file-name-doc').hasClass('d-none')) {
        $('.error-empty-file-name-doc').addClass('d-none');
    }

    // Insertamos el nombre del documento en el campo correspondiente del modal
    $('#file_name_doc').attr('value', file_name);

    // Desmarcamos todos los option
    $('#tags_doc option').prop('selected', false);

    // La primera vez que abrimos el modal renderizamos los option del select con los tags
    if (!$('#tags_doc option').length) {
        window.tags.map((tag) => {
            $('#tags_doc').append(`
                <option value="${tag.tagsID}">${tag.tag}</option>
            `);
        });
    }

    // Marcamos en el select los tags que tiene el documento
    $('#tags_doc option').each(function() {
        // Comprobamos si el tag que estamos recorriendo se encuentra entre los tags del documento
        if ($.inArray($(this).attr('value'), tagIDs) !== -1) {
            $(this).prop('selected', true);
        }
    });

    // Mostramos el modal
    $("#edit_doc").modal();
}

async function singleDeleteOption() {
    const confirmed = confirm('¿Estás seguro de que deseas eliminar el documento?');

    if (confirmed) {
        const { id, folder: service } = window.currentFile;
        let { type } = window.currentFile;

        type = (type === 'sent') ? 2 : 1;

        const docs = [{id, type, service}];

        try {
            // Hacemos una petición a bidoq para eliminar el documento
            const data = await bidoq({
                "method": "delete_docs",
                "docs": JSON.stringify(docs)
            });

            const response = JSON.parse(data);

            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0) {
                    avisoFlotante('El documento ha sido eliminado con éxito');

                    window.history.back();
                } else {
                    if (typeof response['1047'] !== 'undefined') {
                        avisoFlotante('Ocurrió un error al intentar eliminar el documento');
                    }

                    if (typeof response['1048'] !== 'undefined') {
                        avisoFlotante('El documento no pudo ser eliminado debido a que se encuentra marcado como incidencia');
                    }
                }
            } else {
                console.error('Ocurrió un error al intentar eliminar los documentos');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }
}

async function multipleDeleteOption() {
    const service = new URLSearchParams(window.location.search).get('folder');
    let confirmText = '';

    if (parseInt(service) === CARPETA_A_CONTABILIZAR) {
        confirmText = '¿Estás seguro de que deseas eliminar los documentos de tipo "Enviado" seleccionados?';
    } else {
        confirmText = '¿Estás seguro de que deseas eliminar los documentos seleccionados de tipo "Enviado" que pertenezcan a la categoría "A contabilizar"?';
    }

    const confirmed = confirm(confirmText);

    if (confirmed) {
        // Filtramos los documentos seleccionados quitando aquellos que no tengan la opción de eliminar
        const elementsToDelete = $('.select_doc:checked').filter(function() {
            const row = $(this).closest('tr');
            const allowedOptions = row.data('allowed_options').split(',');

            return ($.inArray(MULTIPLE_DELETE_OPTION, allowedOptions) !== -1);
        });
        const docsToDelete = elementsToDelete.map(function() {
            // Mapeamos el listado de objetos jQuery ya filtrado en un nuevo listado de objetos con las propiedades id y type
            const row = $(this).closest('tr');
            const id = row.data('id');
            const type = (row.data('type') === 'sent') ? 2 : 1;
            const category = row.data('category');

            return {
                id,
                type,
                "service": category
            };
        }).toArray(); // Obtenemos el listado de objetos jQuery como un array JS

        try {
            // Hacemos una petición a bidoq para eliminar los documentos seleccionados
            const data = await bidoq({
                "method": "delete_docs",
                "docs": JSON.stringify(docsToDelete)
            });

            const response = JSON.parse(data);

            if (typeof response !== 'undefined') {
                let failedToDeleteIDs = [];

                if (typeof response.code !== 'undefined' && response.code === 0) {
                    if (parseInt(service) === CARPETA_A_CONTABILIZAR) {
                        avisoFlotante('Los documentos de tipo "Enviado" seleccionados han sido eliminados con éxito');
                    } else {
                        avisoFlotante('Los documentos seleccionados de tipo "Enviado" que pertenecen a la categoría "A contabilizar" han sido eliminados con éxito');
                    }
                } else {
                    if (typeof response['1047'] !== 'undefined') {
                        response['1047'].forEach((document) => {
                            failedToDeleteIDs.push(document.id);
                        });
                        avisoFlotante(`Ocurrieron errores al intentar eliminar algunos de los documentos`);
                    }

                    if (typeof response['1048'] !== 'undefined') {
                        let failedToDeleteNames = [];

                        response['1048'].forEach((document) => {
                            failedToDeleteIDs.push(document.id);
                            failedToDeleteNames.push(document.name);
                        });

                        failedToDeleteNames = failedToDeleteNames.join(', ');

                        avisoFlotante(`Los siguientes documentos no pudieron ser eliminados debido a que se encuentran marcados como incidencia: ${failedToDeleteNames}`);
                    }
                }

                try {
                    if (window.intersectionObserverIsSupported) {
                        // Recorremos los elementos que vamos a eliminar
                        elementsToDelete.each(function() {
                            // Obtenemos el ID del documento
                            const documentContainer = $(this).closest('.show_doc_container');
                            const id = documentContainer.data('id');

                            // Si el ID no se encuentra entre los documentos que han fallado, eliminamos el documento de la lista
                            if (!failedToDeleteIDs.includes(id)) {
                                $(`.show_doc_container[data-id="${id}"]`).remove();
                            }
                        });

                        // Comprobamos si tras borrar los documentos del DOM, queda algún documento seleccionado
                        if ($('.select_doc:checked').length) {
                            // Si hay algún documento seleccionado, forzamos un cambio en el checkbox de estos, de manera que se actualicen las opciones de la barra de herramientas
                            $('.select_doc:checked').trigger('change');
                        } else {
                            // Si no hay ningún documento seleccionado, borramos todas las opciones de la barra de herramientas
                            window.aonDocumental.removeToolbarOptions();
                        }
                    } else {
                        const list = await getList({page: window.aonDocumentalContainer.page});

                        renderList(list);
                    }
                } catch (error) {
                    console.error(error.message);
                }
            } else {
                console.error('Ocurrió un error al intentar eliminar los documentos');
            }
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }
}

function addNoteOption() {
    // Filtramos los documentos seleccionados quitando aquellos que no tengan la opción de crear nota
    const docs = $('.select_doc:checked').filter(function() {
        const row = $(this).closest('tr');
        const allowedOptions = row.data('allowed_options').split(',');

        return ($.inArray(ADD_NOTE_OPTION, allowedOptions) !== -1);
    }).map(function() {
        // Mapeamos el listado de objetos jQuery ya filtrado en un nuevo listado de objetos con las propiedades id y type
        const row = $(this).closest('tr');
        const id = row.data('id');
        const type = (row.data('type') === 'sent') ? 'Recibes' : 'Envias';

        return {
            id,
            type
        };
    }).toArray(); // Obtenemos el listado de objetos jQuery como un array JS

    window.docsCreateNote = docs;

    // Ocultamos los errores si están visibles
    if (!$('.error-empty-note-doc').hasClass('d-none')) {
        $('.error-empty-note-doc').addClass('d-none');
    }

    // Vaciamos el contenido del campo si tiene
    if (document.getElementById('note_doc').value.length) {
        document.getElementById('note_doc').value = '';
    }

    // Mostramos el modal
    $("#new_note_doc").modal();
}

async function viewNoteOption() {
    window.noteAction = 'add';

    const { id } = window.currentFile;

    // Vaciamos el contenido del campo si tiene
    if (document.getElementById('note_doc').value.length) {
        document.getElementById('note_doc').value = '';
    }

    $('#title_view_note_doc').text('Añadir nota al documento');
    $('#confirm_view_note_doc').text('Añadir');

    const data = await bidoq({
        "method": "obtener_nota_doc",
        id
    });

    const response = JSON.parse(data);

    if (typeof response !== 'undefined') {
        if (response.code === 0 && response.datos.length) {
            window.noteAction = 'edit';

            document.getElementById('note_doc').value = response.datos;

            $('#confirm_view_note_doc').text('Editar');
            $('#title_view_note_doc').text('Editar la nota del documento');
        }
    }

    // Mostramos el modal
    $('#view_note_doc').modal();
}

export const getOptionButtons = (documentObject) => {
    // Recuperamos del DOM los botones correspondientes a las opciones
    const optionButtons = AVAILABLE_OPTIONS.map((option) => ({
        name: option.name,
        element: documentObject.getElementById(`aonDocumentalToolbar${option.name}Button`)
    }));

    return optionButtons;
};