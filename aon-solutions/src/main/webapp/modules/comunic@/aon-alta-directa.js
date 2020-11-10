import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import {serializeForm} from '../../services/utils.js'

export class AonAltaDirecta extends AonElement {

	constructor () {
		super();
	}

	connectedCallback () {
		this.innerHTML = `
			<div style="display:flex;">
			<div id="aonAltaDirecta" style="width:100%">
				<div id="aonAltaDirectaDiv" style="display:flex;">
                    <aon-card id="aonAltaDirectaCard" title="Alta Directa" style="width:100%;"></aon-card>
				</div>
			</div>
		`;

        this.build();
 	}

 	build() {

        let aonCard = this.getElement('aonAltaDirectaCard');
        aonCard.setContentHTML(`

            <form id="aonAltaDirectaForm">
                <div class="aonRow">
                    <div class="aonContainer">
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="dni" id="dni" description="DNI/NIE" type="text" required="true"></aon-input>
                        </div>
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="naf" id="naf" description="Número de afiliación" type="text"></aon-input>
                        </div>
                        <div class="aonCol aonS12">
                            <aon-input name="nombre" id="nombre" description="Nombre" type="text"></aon-input>
                        </div>
                        <div class="aonCol aon6 aonM6">
                            <aon-input name="apellido" id="apellido" description="1er Apellido" type="text"></aon-input>
                        </div>
                        <div class="aonCol aon6 aonM6">
                            <aon-input name="apellido2" id="apellido2" description="2do Apellido" type="text"></aon-input>
                        </div>
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="tipo_contrato" id="tipo_contrato" description="Tipo de contrato" type="text"></aon-input>
                        </div>
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="modalidad_contrato" id="modalidad_contrato" description="Modalidad" type="text"></aon-input>
                        </div>
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="fecha" id="fecha" description="Fecha" type="date"></aon-input>
                        </div> 
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="grupo_cotizacion" id="grupo_cotizacion" description="Grupo de cotización" type="text"></aon-input>
                        </div> 
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="tipo_jornada" id="tipo_jornada" description="Tipo de jornada" type="text"></aon-input> <!--parcial o completa, parcial = coeficiente_parcial -->
                        </div> 
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="ocupacion" id="ocupacion" description="Ocupación" type="text"></aon-input>
                        </div> 
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="convenio" id="convenio" description="Convenio" type="text"value="60888888888888"></aon-input>
                        </div> 
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="ccc" id="ccc" description="Cuenta de cotización" type="text"></aon-input>
                        </div> 
                        <button type="submit">Dar alta</button>
                    </div>
                </div>
            </form>
        `);

        let form = this.getElement('aonAltaDirectaForm');
        form.addEventListener('submit', (e)=>{
            e.preventDefault(); 
            let formJson = serializeForm(e.target);
            console.log(formJson);
        })
    }
}
window.customElements.define('aon-alta-directa', AonAltaDirecta);
