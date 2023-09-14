import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../../core/services/user.service';
import { FormBuilder, FormGroup } from '@angular/forms'; // Importa FormBuilder y FormGroup

@Component({
  selector: 'app-input-profile-personal-data',
  templateUrl: './input-profile-personal-data.component.html',
  styleUrls: ['./input-profile-personal-data.component.scss'],
})
export class InputProfilePersonalDataComponent implements OnInit {
  users: any[] = [];
  showPasswordFields: boolean = false;
  documentEnterprise!: string;
  profileForm: FormGroup; // Crea un FormGroup para rastrear los cambios en el formulario

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder
  ) {
    this.profileForm = this.formBuilder.group({
      //campos de formulario y sus valores iniciales aquí
      name: [''],
      lastname: [''],
      email: [''],
      phone: [''],
      // newPassword: [''],
      // repeatPassword: [''],
    });
  }

  ngOnInit(): void {
    this.userService.getUser('94385657M').then((user) => {
      this.users.push(user);
      // Actualiza los valores iniciales del formulario con los datos del usuario
      this.profileForm.patchValue(user);
      console.log('valores de user',user);
    });
  }

  togglePasswordFields() {
    this.showPasswordFields = !this.showPasswordFields;
  }

  async onSave() {

    // Obtiene los valores actualizados del formulario
    const updatedUserData = this.profileForm.value;
    console.log('datos usuario',updatedUserData);
    try {
      // Llama a la función updateUser para guardar los cambios
      const updatedUser = await this.userService.updateUser(updatedUserData);

      console.log('Usuario actualizado con éxito:', updatedUser);

      this.profileForm.patchValue(updatedUser);
    } catch (error) {
      console.error('Error al actualizar el usuario:', error);
    }

  }

  onCancel() {
    // Restablece los valores del formulario a los datos originales
    this.userService.getUser('94385657M').then((user) => {
      this.profileForm.patchValue(user);
    });
  }
}


// constructor(private userService: UserService) {
//   if (localStorage.getItem('enterprise')) {
//     this.documentEnterprise = localStorage.getItem('enterprise')!;
//   }
// }

// ngOnInit(): void {
//   console.log(this.documentEnterprise);
//   this.userService.getUser(this.documentEnterprise).then((user) => {
//     this.users.push(user);
//   });
// }
