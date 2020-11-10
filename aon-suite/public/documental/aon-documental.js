import { requestBidoq } from  '../components/request.js';

const BIDOQ_CLIENTE_ID = 'e688cab2-04fe-44cc-9771-e934ad63f5fb';

// Local
// const BIDOQ_URL = 'http://localhost/mispapeles/api/v2/index.php';
// const BIDOQ_SESSION_ID = 'b3RJRmU5SHBYelpVUi1sMw==';

// DEV
const BIDOQ_URL = 'https://dev.mispapeles.es/api/v2/index.php';
const BIDOQ_SESSION_ID = 'c2d3Y3lRUzExdFBxckxlTQ==';

export const CARPETA_A_CONTABILIZAR = 5;
export const CARPETA_CONTABILIZADOS = 14;
export const CARPETA_FISCAL = 8;

export const bidoq = async (additionalData) => {
    // Unimos en un objeto los datos genéricos necesarios en todas las peticiones con los datos específicos de esta petición
    const data = Object.assign({
        "device_info": "phone",
        "app_code": "1",
        "operating_system_version": "4.2",
        "clienteID":BIDOQ_CLIENTE_ID,
        "sessionID":BIDOQ_SESSION_ID,
        "app_version": "1.0"
    }, additionalData);

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

    folder = null;
    page = null;

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

        this.addDocumentOptions(aonDocumental);

        this.addCategoryOptions(aonDocumental, folders);

        this.loadIndex();
    }

    loadIndex({folder = 'pendientes', tag = null} = {}) {
        const contentIframe = document.querySelector('iframe');
        const aonDocumental = document.getElementById('aonDocumental');

        // Por ahora cargamos el listado de "Pendientes" como si fuera el listado de la carpeta "A contabilizar"
        this.folder = (folder === 'pendientes') ? CARPETA_A_CONTABILIZAR : folder;

        const tagParameter = (tag === null) ? '' : `?tag=${tag}`;
        const indexURL = `./index.html${tagParameter}`;

        if (contentIframe === null) {
            aonDocumental.setContentHTML(`<iframe src="${indexURL}" style="width:100%;height:100%;border:none;"></iframe>`);
        } else {
            contentIframe.src = indexURL;
        }
    }

    loadShow(id, type) {
        const contentIframe = document.querySelector('iframe');

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

    addDocumentOptions(aonDocumental) {
        let documentOptions = [
            {
                name: 'Pendientes',
                icon: 'inbox',
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
                name: folder.carpeta,
                icon: 'folder',
                fn: () => this.loadIndex({folder: folder.carpetaID})
            };

            return option;
        });

        aonDocumental.addSidenavOptions('CATEGORIAS', categoryOptions);
    }

}

window.customElements.define('aon-documental', AonDocumental);
