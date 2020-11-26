import { requestBidoq } from  '../components/request.js';

export const BIDOQ_CLIENTE_ID = 'e688cab2-04fe-44cc-9771-e934ad63f5fb';
export const BIDOQ_TIPO_USUARIO = 6;

// Local
//const BIDOQ_URL = 'http://localhost/mispapeles/api/v2/index.php';
//const BIDOQ_SESSION_ID = 'ZlVZZGk1dDVuRVNPTWlNSQ==';

// DEV
const BIDOQ_URL = 'https://dev.mispapeles.es/api/v2/index.php';
const BIDOQ_SESSION_ID = 'c2d3Y3lRUzExdFBxckxlTQ==';

export const CARPETA_A_CONTABILIZAR = 5;
export const CARPETA_CONTABILIZADOS = 14;
export const CARPETA_FISCAL = 8;

export const bidoq = async (additionalData) => {
    // Unimos en un objeto los datos genéricos necesarios en todas las peticiones con los datos específicos de esta petición
    const data = {
        "device_info": "phone",
        "app_code": "1",
        "operating_system_version": "4.2",
        "clienteID": BIDOQ_CLIENTE_ID,
        "sessionID": BIDOQ_SESSION_ID,
        "app_version": "1.0",
        ...additionalData
    };

    // Codificamos el objeto a una query string de URL
    const sendData = new URLSearchParams(data).toString();

    return new Promise( (resolve, reject) => {
        requestBidoq('POST', BIDOQ_URL, sendData, (result, error) => {
            if(error) {
                reject(error);
            } else {
                resolve(result);
            }
        });
    });
};

class AonDocumental extends HTMLElement {

    page = null;
    search = null;

    constructor () {
        super();
    }

    connectedCallback () {
        this.innerHTML = `
            <aon-application id="aonDocumental" title="DOCUMENTAL"></aon-application>
        `;
        this.build();
    }

    async build() {
        const aonDocumental = document.getElementById('aonDocumental');

        const folders = await this.getFolders();
        aonDocumental.dataset['folders'] = JSON.stringify(folders);

        const tags = await this.getTags();
        aonDocumental.dataset['tags'] = JSON.stringify(tags);

        this.addDocumentOptions(aonDocumental, folders);

        this.addCategoryOptions(aonDocumental, folders);

        this.loadIndex();
    }

    loadIndex({folder = 'pendientes', tag = null} = {}) {
        const aonDocumental = document.getElementById('aonDocumental');
        const contentIframe = document.querySelector('iframe');
        const tagParameter = (tag === null) ? '' : `&tag=${tag}`;
        const indexURL = `./index.html?folder=${folder}${tagParameter}`;
        const uploadButton = document.getElementById('aonDocumentalToolbarSubirButton');

        // Reseteamos el término de búsqueda
        this.search = null;

        // Eliminamos todas las opciones de la barra de herramientas
        aonDocumental.removeToolbarOptions();

        // Añadimos el botón de subir documentos si no se ha añadido ya y siempre y cuando no estemos en la carpeta "Contabilizados"
        if (parseInt(folder) !== CARPETA_CONTABILIZADOS && uploadButton === null) {
            aonDocumental.addToolbarOption('Subir', 'file_upload', () => {
                document.querySelector('iframe').contentWindow.document.getElementById('upload-file').click();
            }, 'Subir documentos');
        }

        if (contentIframe === null) {
            aonDocumental.setContentHTML(`<iframe src="${indexURL}" style="width:100%;height:100%;border:none;"></iframe>`);
        } else {
            contentIframe.src = indexURL;
        }
    }

    loadShow(id, type) {
        const contentIframe = document.querySelector('iframe');

        // Reseteamos el término de búsqueda
        this.search = null;

        contentIframe.src = `./show.html?id=${id}&type=${type}`;
    }

    async getFolders() {
        try {
            const data = await bidoq({
                "method": "carpetas"
            });
            const folders = JSON.parse(data).datos;

            return new Promise((resolve, reject) => {
                if (typeof folders !== 'undefined') {
                    resolve(folders);
                } else {
                    reject('Ocurrió un error al intentar obtener las carpetas');
                }
            });
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }

    async getTags() {
        try {
            const data = await bidoq({
                "method": "tags"
            });
            const tags = JSON.parse(data).datos;

            return new Promise((resolve, reject) => {
                if (typeof tags !== 'undefined') {
                    resolve(tags);
                } else {
                    reject('Ocurrió un error al intentar obtener los tags');
                }
            });
        } catch (error) {
            console.error('Ocurrió un error: ' + error.message);
        }
    }

    addDocumentOptions(aonDocumental, folders) {
        // Coger las cantidades
        const documentArrayTotalUnread = folders.map((folder) => {
            return parseInt(folder.total_no_leidos);
        });
        // Sumar las cantidades
        const documentTotalUnread = documentArrayTotalUnread.reduce((a, b) => a + b, 0);

        let documentOptions = [
            {
                name    : 'Pendientes',
                icon    : 'inbox',
                total   : documentTotalUnread,
                folder  : 'Pendientes',
                fn: () => this.loadIndex({folder: 'pendientes'}),
                default: true
            },
            {
                name: 'Recientes',
                icon: 'access_time',
                fn: () => this.loadIndex({folder: 'recientes'})
            }
        ];

        aonDocumental.addSidenavOptions('DOCUMENTOS', documentOptions);
    }

    addCategoryOptions(aonDocumental, folders) {
        const categoryOptions = folders.map((folder) => {
            const option = {
                name    : folder.carpeta,
                icon    : 'folder',
                total   : folder.total_no_leidos,
                folder  : folder.carpetaID,
                fn: () => this.loadIndex({folder: folder.carpetaID})
            };

            return option;
        });

        aonDocumental.addSidenavOptions('CATEGORIAS', categoryOptions);
    }

}

window.customElements.define('aon-documental', AonDocumental);
