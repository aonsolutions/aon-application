import { requestBidoq } from  '../components/request.js';

const BIDOQ_URL = 'https://dev.mispapeles.es/api/v2/index.php';
// const BIDOQ_URL = 'http://localhost/mispapeles/api/v2/index.php';
const BIDOQ_CLIENTE_ID = 'e688cab2-04fe-44cc-9771-e934ad63f5fb';
// const BIDOQ_SESSION_ID = 'b3RJRmU5SHBYelpVUi1sMw==';
const BIDOQ_SESSION_ID = 'c2d3Y3lRUzExdFBxckxlTQ==';

export const CARPETA_A_CONTABILIZAR = 5;
const CARPETA_CONTABILIZADOS = 14;

export const bidoq = (additionalData) => {
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

    constructor () {
        super();
    }

    connectedCallback () {
        this.innerHTML = `
            <aon-application id="aonDocumental" title="DOCUMENTAL"></aon-application>
        `;
        this.build();
    }

    build() {
        this.getFolders((folders) => {
            let aonDocumental = document.getElementById('aonDocumental');

            aonDocumental.dataset['folders'] = JSON.stringify(folders);

            aonDocumental.addToolbarOption('Subir', 'file_upload', () => {
                const contentIframe = document.querySelector('iframe');
    
                contentIframe.contentWindow.document.getElementById('upload').click();
            });

            this.addDocumentOptions(aonDocumental);

            this.addCategoryOptions(aonDocumental, folders);

            this.loadIndex();
        });
    }

    loadIndex(folder = CARPETA_A_CONTABILIZAR) {
        let aonDocumental = document.getElementById('aonDocumental');
        const uploadButton = document.getElementById('aonDocumentalToolbarSubirButton');

        // Quitamos el botón de subir documentos si estamos en la carpeta "Contabilizados"
        if (parseInt(folder) === CARPETA_CONTABILIZADOS) {
            uploadButton.style.display = 'none';
        } else if (uploadButton.style.display === 'none') {
            uploadButton.style.display = '';
        }

        aonDocumental.setContentHTML('<iframe src="./index.html?folder=' + folder + '" style="width:100%;height:100%;border:none;"></iframe>');
    }

    getFolders(callback) {
        bidoq({
            "method": "carpetas"
        }).then((data) => {
            const folders = JSON.parse(data).datos;

            if (typeof folders !== 'undefined') {
                callback(folders);
            } else {
                console.error('Ocurrió un error al intentar obtener las carpetas');
            }
        }).catch(function(error) {
            console.error('Ocurrió un error: ' + error.message);
        });
    }

    addDocumentOptions(aonDocumental) {
        let documentOptions = [
            {
                name: 'Recientes',
                icon: 'access_time',
                fn: () => this.loadIndex('recientes')
            },
            {
                name: 'Pendientes',
                icon: 'inbox',
                fn: () => this.loadIndex('pendientes')
            }
        ];

        aonDocumental.addSidenavOptions('DOCUMENTOS', documentOptions);
    }

    addCategoryOptions(aonDocumental, folders) {
        const categoryOptions = folders.map((folder) => {
            const option = {
                name: folder.carpeta,
                icon: 'folder',
                fn: () => this.loadIndex(folder.carpetaID)
            };

            if (parseInt(folder.carpetaID) === CARPETA_A_CONTABILIZAR) {
                option['default'] = true;
            }

            return option;
        });

        aonDocumental.addSidenavOptions('CATEGORIAS', categoryOptions);
    }
}

window.customElements.define('aon-documental', AonDocumental);
