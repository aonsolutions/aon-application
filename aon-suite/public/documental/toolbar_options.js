import { bidoq } from './aon-documental.js';
import { createTable, getList } from './table.js';

export const SINGLE_DOWNLOAD_OPTION = 'singleDownload';
export const MULTIPLE_DOWNLOAD_OPTION = 'multipleDownload';
export const EDIT_OPTION = 'edit';
export const DELETE_OPTION = 'delete';
export const CREATE_NOTE_OPTION = 'createNote';
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
        name: DELETE_OPTION,
        icon: 'delete_forever',
        title: 'Eliminar los documentos seleccionados',
        fn: deleteOption
    },
    {
        name: CREATE_NOTE_OPTION,
        icon: 'note_add',
        title: 'Crear nota en los documentos seleccionados',
        fn: createNoteOption
    },
    {
        name: VIEW_NOTE_OPTION,
        icon: 'sticky_note_2',
        title: 'Ver nota del documento',
        fn: viewNoteOption
    },
];

function singleDownloadOption() {
    alert('Descargar documento');
}

function multipleDownloadOption() {
    alert('Descargar los documentos seleccionados');
}

function editOption() {
    const { id, type, file_name, tags } = window.currentFile;
    const tagValues = tags.map((tag) => tag.id);

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
        if ($.inArray($(this).attr('value'), tagValues) !== -1) {
            $(this).prop('selected', true);
        }
    });

    // Mostramos el modal
    $("#edit_doc").modal();
}

async function deleteOption() {
    const confirmed = confirm('¿Estás seguro de que deseas eliminar los documentos de tipo "Enviado" seleccionados?');

    if (confirmed) {
        const service = window.selectedFolder;

        // Filtramos los documentos seleccionados quitando aquellos que no tengan la opción de eliminar
        const docs = $('.select_doc:checked').filter(function() {
            const row = $(this).closest('tr');
            const allowedOptions = row.data('allowed_options').split(',');

            return ($.inArray(DELETE_OPTION, allowedOptions) !== -1);
        }).map(function() {
            // Mapeamos el listado de objetos jQuery ya filtrado en un nuevo listado de objetos con las propiedades id y type
            const row = $(this).closest('tr');
            const id = row.data('id');
            const type = (row.data('type') === 'sent') ? 2 : 1;

            return {
                id,
                type
            };
        }).toArray(); // Obtenemos el listado de objetos jQuery como un array JS

        try {
            // Hacemos una petición a bidoq para eliminar los documentos seleccionado
            const data = await bidoq({
                "method": "delete_docs",
                service,
                "docs": JSON.stringify(docs)
            });

            const response = JSON.parse(data);

            if (typeof response !== 'undefined') {
                if (typeof response.code !== 'undefined' && response.code === 0) {
                    alert('Los documentos de tipo "Enviado" seleccionados han sido eliminados con éxito');
                } else {
                    if (typeof response['1047'] !== 'undefined') {
                        alert(`Ocurrieron errores al intentar eliminar algunos de los documentos`);
                    }

                    if (typeof response['1048'] !== 'undefined') {
                        const failedDocuments = response['1048'].map((document) => {
                            return document.name;
                        }).join(', ');

                        alert(`Los siguientes documentos no pudieron ser eliminados debido a que se encuentran marcados como incidencia: ${failedDocuments}`);
                    }
                }

                try {
                    const list = await getList(window.page);

                    createTable(list);
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

function createNoteOption() {
    // Filtramos los documentos seleccionados quitando aquellos que no tengan la opción de crear nota
    const docs = $('.select_doc:checked').filter(function() {
        const row = $(this).closest('tr');
        const allowedOptions = row.data('allowed_options').split(',');

        return ($.inArray(CREATE_NOTE_OPTION, allowedOptions) !== -1);
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

    // Vaciamos el contenido del campo si existe
    if ($('#note_doc').length) {
        $('#note_doc').attr('value', '');
    }

    // Mostramos el modal
    $("#new_note_doc").modal();
}

function viewNoteOption() {
    alert('Ver nota del documento');
}

export const getOptionButtons = (documentObject) => {
    // Recuperamos del DOM los botones correspondientes a las opciones
    const optionButtons = AVAILABLE_OPTIONS.map((option) => ({
        name: option.name,
        element: documentObject.getElementById(`aonDocumentalToolbar${option.name}Button`)
    }));

    return optionButtons;
};