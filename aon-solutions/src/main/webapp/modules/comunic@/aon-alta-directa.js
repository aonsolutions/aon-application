import {AonElement} from '../../components/AonElement.js';
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-date.js';
import {serializeForm} from '../../services/utils.js'
import {getPersonas} from '../../services/service.js'
import '../../components/aon-suggestion.js';

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
                            <div id="aonAltaDirectaDivDni"></div>
                        </div>
                        <div class="aonCol aonS12 aonM6">
                            <aon-input name="naf" id="naf" description="Número de afiliación" type="text"></aon-input>
                        </div>
                        <div class="aonCol aonS12">
                            <aon-input name="nombre" id="nombre" description="Nombre" type="text"></aon-input>
                        </div>
                        <div class="aonCol aon6 aonM6">
                            <aon-input name="apellido" id="apellido1" description="1er Apellido" type="text"></aon-input>
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
                            <aon-date name="fecha" id="fecha" title="Fecha"></aon-date>
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

        let form = this.getElement(`aonAltaDirectaForm`);
        form.addEventListener('submit',this.formSubmit);

        let aonAltaDirectaDni = this.getElement(`aonAltaDirectaDivDni`);
        aonAltaDirectaDni.innerHTML = `<aon-suggestion id="aonAltaDirectaDni" title="DNI/NIE"></aon-suggestion>`;

		let searchSuggestion = this.getElement(`aonAltaDirectaDni`);
		searchSuggestion.addEventListener('keyup', ({target:{value}}) => {
            let valor = value.toString().toUpperCase();
			if(valor.length > 2) {
                getPersonas().then( resp => {
                    let filters = resp.filter(f=>f.dni.indexOf(valor) >= 0).map(m=> {
                        return {...m, value:m.dni, name:m.dni};
                    });
                    searchSuggestion.buildOptions(filters);
                });
			} else {
				searchSuggestion.closeOptions();
			}
		});
		searchSuggestion.addEventListener('select', ({detail}) => {
            if(detail){
                this.getElement('nombre').setAttribute('value', detail.nombre);
                this.getElement('apellido1').setAttribute('value', detail.last_name1);
                this.getElement('apellido2').setAttribute('value', detail.last_name2);
            }
		});
    }



    formSubmit(e){
        e.preventDefault(); 
        let formJson = serializeForm(e.target);
        console.log(formJson);
    }
}
window.customElements.define('aon-alta-directa', AonAltaDirecta);
