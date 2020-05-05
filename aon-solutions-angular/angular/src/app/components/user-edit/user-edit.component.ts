import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SharedService } from '../../services/shared.service';
import { UserService } from '../../services/user.service';
import { MatDialog } from '@angular/material';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.css']
})
export class UserEditComponent implements OnInit {

  title = 'Editar Usuario';
  isMe = false;

  constructor(private router: Router, public service: SharedService,
     public dialog: MatDialog, public userService: UserService) {}

  ngOnInit() {
    this.isMe = (this.service.getUserEmail() === this.userService.user.email);
  }

  updateUserName(name: string): void {
    this.userService.user.name = name;
    this.userService.updateUser();
  }

  updateUserSurname(surname: string): void {
    this.userService.user.surname = surname;
    this.userService.updateUser();
  }

  updateUserDocument(document: string): void {
    this.userService.user.document = document;
    this.userService.updateUser();
  }

  updateUserPhone(phone: string): void {
    this.userService.user.phone = phone;
    this.userService.updateUser();
  }

  public changePassword() {
    this.router.navigate(['/myAccount/changePassword', {email: this.userService.user.email}]);
  }
}
