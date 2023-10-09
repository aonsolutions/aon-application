import { Component, OnInit } from '@angular/core';

import { UserService } from '../../../../core/services/user.service';

@Component({
  selector: 'app-input-profile-personal-data',
  templateUrl: './input-profile-personal-data.component.html',
  styleUrls: ['./input-profile-personal-data.component.scss'],
})
export class InputProfilePersonalDataComponent implements OnInit {
  users: any[] = [];
  showPasswordFields: boolean = false;


  constructor(private userService: UserService) {
    this.userService.getCurrentUserData().then((user) => {
      this.users.push(user);
    });
  }

  togglePasswordFields() {
    this.showPasswordFields = !this.showPasswordFields;
  }

  getValue(newValue: any, user: any, propertyName: string) {
    // Actualiza el valor correspondiente en el objeto user
    user[propertyName] = newValue;
     // Emite el evento con el valor actualizado

  }

  onSave() {
    this.userService
      .updateUser(this.users[0])
      .then((updatedUser) => {
      // Actualiza el valor correspondiente en el objeto user
      this.users[0] = updatedUser;
    });
  }

  onCancel() {
  }

  ngOnInit(): void {
  }

}
