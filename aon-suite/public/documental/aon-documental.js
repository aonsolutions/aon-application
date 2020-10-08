const A_CONTABILIZAR = 5;
const BIDOQ_METHOD = 'carpetas';
const BIDOQ_CLIENTE_ID = 'e688cab2-04fe-44cc-9771-e934ad63f5fb';
const BIDOQ_SESSION_ID = 'b3RJRmU5SHBYelpVUi1sMw==';

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
        this.bidoq().then((response) => {
            const data = JSON.parse(response);
            const folders = data.datos;

            if (typeof folders !== 'undefined') {
                let aonDocumental = document.getElementById('aonDocumental');

                aonDocumental.addToolbarOption('Subir', 'file_upload', () => {
                    const contentIframe = document.querySelector('iframe');
        
                    contentIframe.contentWindow.document.getElementById('upload').click();
                });

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

                const categoryOptions = folders.map((folder) => ({
                    name: folder.carpeta,
                    icon: 'folder',
                    fn: () => this.loadIndex(folder.carpetaID)
                }));
                aonDocumental.addSidenavOptions('CATEGORIAS', categoryOptions);

                this.loadIndex();
            }
        }).catch(function(error) {
            console.error('Ocurrió un error: ' + error.message);
        });
    }

    loadIndex(folder = A_CONTABILIZAR) {
        let aonDocumental = document.getElementById('aonDocumental');
        aonDocumental.setContentHTML('<iframe src="./index.html?folder=' + folder + '" style="width:100%;height:100%;border:none;"></iframe>');
    }

    bidoq() {
        const sendData = `method=${BIDOQ_METHOD}&device_info=phone&app_code=1&operating_system_version=4.2&clienteID=${BIDOQ_CLIENTE_ID}&sessionID=${BIDOQ_SESSION_ID}&app_version=1.0`;

        return new Promise( (resolve, reject) => {
            this.requestBidoq('POST', 'http://localhost/mispapeles/api/v2/index.php', sendData, (result, error) => {
                if(error) {
                    reject(error);
                } else {
                    resolve(result);
                }
            });
        });
    }

    requestBidoq(method, url, sendData, fn) {
        let xhr = new XMLHttpRequest();

        xhr.open(method, url);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.setRequestHeader('Access-Control-Allow-Origin', '*');

        xhr.send(sendData);

        xhr.onload = () => {
            if (xhr.status != 200) { // analyze HTTP status of the response
                console.log(`Error ${xhr.status}: ${xhr.statusText}`); // e.g. 404: Not Found
                fn(undefined, xhr.response);
            } else { // show the result
                fn(xhr.response);
            }
        };

        xhr.onerror = () => {
            console.log("Request failed");
        };
    }
}

window.customElements.define('aon-documental', AonDocumental);
