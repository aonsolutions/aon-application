import { AonElement } from '../../components/AonElement';
import { AonCard } from '../../components/aon-card';
import { TAG } from "../../environments/environments";

export class AonCertification extends AonElement {
  constructor () {
    super();
  }

  connectedCallback () {
    this.buildDur().then(() => {
      this.build();
    });
  }

  build () {
    /*
    const container = document.createElement('div');
    container.id    = "certifications";
    container.innerHTML = `
 
            <div class="cert-card">
                <h3> <img src="../../images/apps/aeat.png" class="logo"> AEAT</h3>
                <ul>
                    <li>Homologado para la DigitalizaciÃ³n Certificada de Facturas<br> NÂº HomologaciÃ³n: RGE405069592024</li>
                    <li>Software integrado con Veri*Factu</li>
                    <li>Registrado como Colaborador Social</li>
                    <li>Comunic@ / AEAT:
                        <ul>
                            <li>ComunicaciÃ³n Online en tiempo real de modelos fiscales</li>
                            <li>Consulta y descarga de modelos fiscales presentados</li>
                        </ul>
                    </li>
                </ul>
                <br>
                <div class="link-inline">
                    <a class="link" href="https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/FZ01/RSoftHomologado_doc_base.pdf" target="_blank" rel="noopener noreferrer" >Ver software homologados</a>
                    <span> (PÃ¡gina 11) </span>
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
                    Nuestro software cumple con todos los requisitos tÃ©cnicos y legales establecidos por la Agencia Tributaria. 
                    Ayuda asesorÃ­as, Pymes y autÃ³nomos a adaptarse de manera sencilla al nuevo marco de facturaciÃ³n electrÃ³nica 
                    con total seguridad y respaldo normativo.
                </p>
                <a class="link2" href="https://drive.google.com/file/d/1xObCAXybwhoOEa339DgFy9h3KL97CYJ5/view?usp=sharing" target="_blank" rel="noopener noreferrer" >Ver DeclaraciÃ³n Responsable</a>
            </div>

            <div class="cert-card">
                <h3> <img src="../../images/apps/haciendasForales.png" class="logo"> Haciendas Forales</h3>
                <p>
                    SoluciÃ³n cloud homologada por el Gobierno Vasco como software garante del cumplimiento legal de âTicket BAIâ
                    para las tres Haciendas Forales:
                </p>
                <ul>
                    <li>Hacienda Foral de ÃLAVA/ARABA</li>
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
                    <li>Software de nÃ³minas registrado</li>
                    <li>CÃ³digo Proveedor de nÃ³minas nÂº 558</li>
                    <li>Comunic@ / TGSS:
                        <ul>
                            <li>ComunicaciÃ³n Online en tiempo real de ALTAS previas, mensajes AFI y FIE</li>
                            <li>Consulta de Certificado de empresa, Vida Laboral, IDC y FIE</li>
                            <li>SincronizaciÃ³n de datos entre TGSS y AON</li>
                        </ul>
                    </li>
                </ul>
                <br>
                <div class="link-inline">
                    <a class="link" href="https://www.seg-social.es/wps/wcm/connect/wss/7ada0194-1392-4dca-bc7f-e2496ec372b8/T86-Proveedor+de+n%C3%B3mina+2018-07.pdf?MOD=AJPERES&CVID=" target="_blank" rel="noopener noreferrer">Ver listado proveedores</a>
                    <span>(PÃ¡gina 4)</span>
                </div>				
            </div>

            <div class="cert-card">
                <h3> <img src="../../images/apps/sepe.png" class="logo"> SEPE</h3>
                <ul>
                    <li>Comunic@ / TGSS:
                        <ul>
                            <li>ComunicaciÃ³n Online en tiempo real de Contrat@, Copia bÃ¡sica, prÃ³rrogas y Certific@2</li>
                            <li>Consulta de Contratos y Copia bÃ¡sica</li>
                        </ul>
                    </li>
                </ul>
            </div>
    `;
    //this.appendChild(container);
*/
    //
    // AEAT
    const aeatCard     = new AonCard();
    aeatCard.id        = "aeat-card";
    aeatCard.title     = "AEAT";
    aeatCard.className = "notification-card";
    this.appendChild(aeatCard);
    // Contenido
      // imagen
      const img     = this.createElement(TAG.IMG);
      img.className = "card-img";
      aeatCard.setContent(img);
      // textos
      const optionsAeat = [
        "Homologado para la Digitalización Certificada de Facturas Nº Homologación: RGE405069592024",
        "Software integrado con Veri*Factu",
        "Registrado como Colaborador Social",
        {
          title   : "Comunic@ / AEAT:",
          children: [
            "Comunicación Online en tiempo real de modelos fiscales",
            "Consulta y descarga de modelos fiscales presentados"
          ]
        }
      ];
      const content = this.buildListContent(optionsAeat);
      // Agregar el contenido
      aeatCard.addContent(content);
    // Footer
      const footer = this.createCardFooter({
        text: "Ver software homologados",
        href: "https://sede.agenciatributaria.gob.es/static_files/Sede/Procedimiento_ayuda/FZ01/RSoftHomologado_doc_base.pdf",
        note: "(Página 11)"
      });
      aeatCard.addContent(footer);
    // FIN AEAT
    //

    //
    // VERIFACTU
    const verifactuCard     = new AonCard();
    verifactuCard.id        = "verifactu-card";
    verifactuCard.title     = "Software Integrado con Veri*Factu";
    verifactuCard.className = "notification-card";
    this.appendChild(verifactuCard);
    // Contenido
      const verifactuText       = this.createElement(TAG.P);
      verifactuText.textContent = `Nuestro software cumple con todos los requisitos técnicos y legales establecidos por la Agencia Tributaria. 
Ayuda asesorías, Pymes y autónomos a adaptarse de manera sencilla al nuevo marco de facturación electrónica 
con total seguridad y respaldo normativo.`;
      // Agregar el contenido
      verifactuCard.setContent(verifactuText);
    // Footer
      const verifactuFooter = this.createCardFooter({
          text : "Ver Declaración Responsable",
          href : "https://drive.google.com/file/d/1xObCAXybwhoOEa339DgFy9h3KL97CYJ5/view?usp=sharing"
      });
      verifactuCard.addContent(verifactuFooter);
    // FIN VERIFACTU
    //

    //
    // HACIENDAS FORALES
    const haciendasCard     = new AonCard();
    haciendasCard.id        = "haciendas-card";
    haciendasCard.title     = "Haciendas Forales";
    haciendasCard.className = "notification-card";
    this.appendChild(haciendasCard);
    // Contenido
      const haciendasText       = this.createElement(TAG.P);
      haciendasText.textContent = `Solución cloud homologada por el Gobierno Vasco como software garante del cumplimiento legal de “Ticket BAI” para las tres Haciendas Forales:`;

      const haciendasOptions = [
        "Hacienda Foral de ÁLAVA/ARABA",
        "Hacienda Foral de BIZKAIA",
        "Hacienda Foral de GIPUZKOA"
      ];
      const haciendasList = this.buildListContent(haciendasOptions);
      // Agregar el contenido
      haciendasCard.setContent(haciendasText);
      haciendasCard.addContent(haciendasList);
    // Footer
      const haciendasFooter = this.createCardFooter({
        text : "Ver software registrados",
        href : "https://www.batuz.eus/es/registro-de-software?q=aonSolutions"
      });
      haciendasCard.addContent(haciendasFooter);
    // FIN HACIENDAS FORALES
    //

    //
    // AGENCIA TRIBUTARIA CANARIA
    const atcCard     = new AonCard();
    atcCard.id        = "atc-card";
    atcCard.title     = "Agencia Tributaria Canaria";
    atcCard.className = "notification-card";
    this.appendChild(atcCard);
    // Contenido
      const atcOptions = [
        "Software con soporte del IGIC",
        "Software integrado con Veri*Factu",
        "Registrado como Colaborador Social"
      ];
      const atcContent = this.buildListContent(atcOptions);
      // Agregar el contenido
      atcCard.setContent(atcContent);
    // FIN AGENCIA TRIBUTARIA CANARIA
    //

    //
    // TGSS
    const tgssCard     = new AonCard();
    tgssCard.id        = "tgss-card";
    tgssCard.title     = "TGSS";
    tgssCard.className = "notification-card";
    this.appendChild(tgssCard);
    // Contenido
      const tgssOptions = [
        "Software de nóminas registrado",
        "Código Proveedor de nóminas nº 558",
        {
          title   : "Comunic@ / TGSS:",
          children: [
            "Comunicación Online en tiempo real de ALTAS previas, mensajes AFI y FIE",
            "Consulta de Certificado de empresa, Vida Laboral, IDC y FIE",
            "Sincronización de datos entre TGSS y AON"
          ]
        }
      ];
      const tgssContent = this.buildListContent(tgssOptions);
      // Agregar el contenido
      tgssCard.setContent(tgssContent);
    // Footer
    const tgssFooter = this.createCardFooter({
      text : "Ver listado proveedores",
      href : "https://www.seg-social.es/wps/wcm/connect/wss/7ada0194-1392-4dca-bc7f-e2496ec372b8/T86-Proveedor+de+n%C3%B3mina+2018-07.pdf?MOD=AJPERES&CVID=",
      note : "(Página 4)"
    });
    tgssCard.addContent(tgssFooter);
    // FIN TGSS
    //

    //
    // SEPE
    const sepeCard     = new AonCard();
    sepeCard.id        = "sepe-card";
    sepeCard.title     = "SEPE";
    sepeCard.className = "notification-card";
    this.appendChild(sepeCard);
    // Contenido
      const sepeOptions = [
        {
          title   : "Comunic@ / TGSS:",
          children: [
            "Comunicación Online en tiempo real de Contrat@, Copia básica, prórrogas y Certific@2",
            "Consulta de Contratos y Copia básica"
          ]
        }
      ];
      const sepeContent = this.buildListContent(sepeOptions);
      // Agregar el contenido
      sepeCard.setContent(sepeContent);
    // FIN SEPE
    //
  }

  buildListContent(options) {
    const ul        = this.createElement(TAG.UL);

    for (const option of options) {
      const li     = this.createElement(TAG.LI);
      if (typeof option === "string") {
        li.innerHTML = option;
      } else if ( typeof option === "object" && option.title && Array.isArray(option.children) ) {
        li.innerHTML = option.title;
        const nestedUl      = this.createElement(TAG.UL);

        for (const child of option.children) {
          const nestedLi       = this.createElement(TAG.LI);
          nestedLi.textContent = child;
          nestedUl.appendChild(nestedLi);
        }
        li.appendChild(nestedUl);
      }

      ul.appendChild(li);
    }
    return ul;
  }

  createCardFooter(footerOption) {
    const footer = this.createElement(TAG.DIV);
    footer.className = "card-footer right-align";

    const link       = this.createElement(TAG.A);
    link.href        = footerOption.href;
    link.target      = "_blank";
    link.rel         = "noopener noreferrer";
    link.className   = "card-footer-link";
    link.textContent = footerOption.text;

    footer.appendChild(link);

    if (footerOption.note) {
      const note       = this.createElement(TAG.SPAN);
      note.className   = "card-footer-note";
      note.textContent = footerOption.note;
      footer.appendChild(note);
    }

    return footer;
  }

}

if(!window.customElements.get(TAG.AON_CERTIFICATION)){
  window.customElements.define(TAG.AON_CERTIFICATION, AonCertification);
}
