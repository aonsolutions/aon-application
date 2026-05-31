import { MSG, TAG } from "../../environments/environments.js";
import { AonSuiteMenu } from '../aon-suite-menu.js';


export class AonCertification extends AonSuiteMenu {

    constructor () {
        super();
    }

    connectedCallback () {
        this.buildDur().then(() => {
			this.certificationInitialize()
            this.build();
            this.setTitle(MSG.CERTIFICATIONS);
        })
    }
	
	certificationInitialize () {
		const container = document.createElement('div');
    	container.innerHTML = `
            <style>
			
				h2.title {
	                font-size: 20px;
					margin-left: 20px;
	                margin-bottom: 20px;
	                color: #333;
	                font-weight: bold;
			    }
				
				h3 {
					font-size: 20px;
					margin-top: 0;
				}
				
				h4 {
					text-align: center;
					margin-top: 0;
				}
				
				span {
					font-size: 10px;
				}
				 
                .cert-card {
                    background: white;
                    border-radius: 16px;
                    box-shadow: 0 2px 6px rgba(0,0,0,0.1);
                    padding: 20px;
                    margin: 10px;
                    width: 100%;
                    max-width: 500px;
					position: relative;
                }
				
				.cert-card .link{
					font-weight: bold;
					text-decoration: underline;
				}
				
				.cert-card .link2{
					font-weight: bold;
					text-decoration: underline;
					position: absolute;   
					bottom: 20px;         
					right: 20px;  
				}

                .cert-grid {
                    display: flex;
                    flex-wrap: wrap;
                    gap: 20px;
                    justify-content: center;
                }

				.cert-card h3 {
				  display: flex;
				  align-items: center;
				  gap: 8px;
				}

				.cert-card .logo {
				  height: 50px;
				  width: auto;
				  margin-right: 10px;
				}

                .cert-card ul {
                    padding-left: 20px;
                }
				
				.logo-row {
				  display: flex;
				  justify-content: space-between;
				  align-items: center;
				  margin-bottom: 1em;
				}
				
				.link-inline {
				    display: flex;
				    align-items: center;
				    gap: 8px;
					position: absolute;   
					bottom: 20px;         
					right: 20px;  
				}

                .link,
				.link2 { color: #2b475e;}
                .purple { color: #b45084; }
                .black { color: #000; }
            </style>
			<h2 class="title">Certificaciones</h2>
            <div class="cert-grid">

                <div class="cert-card">
                    <h3> <img src="../../images/apps/aeat.png" class="logo"> AEAT</h3>
                    <ul>
                        <li>Homologado para la Digitalización Certificada de Facturas<br> Nº Homologación: RGE405069592024</li>
                        <li>Software integrado con Veri*Factu</li>
                        <li>Registrado como Colaborador Social</li>
                        <li>Comunic@ / AEAT:
                            <ul>
                                <li>Comunicación Online en tiempo real de modelos fiscales</li>
                                <li>Consulta y descarga de modelos fiscales presentados</li>
                            </ul>
                        </li>
                    </ul>
					<br>
					<div class="link-inline">
                    	<a class="link" href="https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/FZ01/RSoftHomologado_doc_base.pdf" target="_blank" rel="noopener noreferrer" >Ver software homologados</a>
                		<span> (Página 11) </span>
					</div>	
				</div>

                <div class="cert-card">
					<div class="logo-row">
					    <img src="../../images/apps/aeat.png" class="logo left">
					    <img src="../../images/apps/verifactu.png" class="logo center">
					    <img src="../../images/apps/atc.png" class="logo right">
					</div>                    
					<h4> Software Integrado con Veri*Factu</h4>
                    <p>
                        Nuestro software cumple con todos los requisitos técnicos y legales establecidos por la Agencia Tributaria. 
                        Ayuda asesorías, Pymes y autónomos a adaptarse de manera sencilla al nuevo marco de facturación electrónica 
                        con total seguridad y respaldo normativo.
                    </p>
                    <a class="link2" href="https://drive.google.com/file/d/1xObCAXybwhoOEa339DgFy9h3KL97CYJ5/view?usp=sharing" target="_blank" rel="noopener noreferrer" >Ver Declaración Responsable</a>
                </div>

                <div class="cert-card">
                    <h3> <img src="../../images/apps/haciendasForales.png" class="logo"> Haciendas Forales</h3>
                    <p>
                        Solución cloud homologada por el Gobierno Vasco como software garante del cumplimiento legal de “Ticket BAI”
                        para las tres Haciendas Forales:
                    </p>
                    <ul>
                        <li>Hacienda Foral de ÁLAVA/ARABA</li>
                        <li>Hacienda Foral de BIZKAIA</li>
                        <li>Hacienda Foral de GIPUZKOA</li>
                    </ul>
					<br>
                    <a class="link2" href="https://www.batuz.eus/es/registro-de-software?q=aonSolutions" target="_blank" rel="noopener noreferrer" >Ver software registrados</a>
                </div>

                <div class="cert-card">
                    <h3> <img src="../../images/apps/atc.png" class="logo"> Agencia Tributaria Canaria</h3>
                    <ul>
                        <li>Software con soporte del IGIC</li>
                        <li>Software integrado con Veri*Factu</li>
                        <li>Registrado como Colaborador Social</li>
                    </ul>
                </div>

                <div class="cert-card">
                    <h3> <img src="../../images/apps/tgss.png" class="logo"> TGSS</h3>
                    <ul>
                        <li>Software de nóminas registrado</li>
                        <li>Código Proveedor de nóminas nº 558</li>
                        <li>Comunic@ / TGSS:
                            <ul>
                                <li>Comunicación Online en tiempo real de ALTAS previas, mensajes AFI y FIE</li>
                                <li>Consulta de Certificado de empresa, Vida Laboral, IDC y FIE</li>
                                <li>Sincronización de datos entre TGSS y AON</li>
                            </ul>
                        </li>
                    </ul>
					<br>
					<div class="link-inline">
				        <a class="link" href="https://www.seg-social.es/wps/wcm/connect/wss/7ada0194-1392-4dca-bc7f-e2496ec372b8/T86-Proveedor+de+n%C3%B3mina+2018-07.pdf?MOD=AJPERES&CVID=" target="_blank" rel="noopener noreferrer">Ver listado proveedores</a>
				        <span>(Página 4)</span>
					</div>				
				</div>

                <div class="cert-card">
                    <h3> <img src="../../images/apps/sepe.png" class="logo"> SEPE</h3>
                    <ul>
                        <li>Comunic@ / TGSS:
                            <ul>
                                <li>Comunicación Online en tiempo real de Contrat@, Copia básica, prórrogas y Certific@2</li>
                                <li>Consulta de Contratos y Copia básica</li>
                            </ul>
                        </li>
                    </ul>
                </div>

            </div>
        `;
		this.appendChild(container);
    }
}
if(!window.customElements.get(TAG.AON_CERTIFICATION)){
    window.customElements.define(TAG.AON_CERTIFICATION, AonCertification);
}