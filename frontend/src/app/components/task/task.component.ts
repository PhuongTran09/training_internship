import {Component, OnInit} from '@angular/core';
import {RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {CommonModule} from "@angular/common";
import {TaskService} from "../../service/task.service";


@Component({
  selector: 'login-task',
  templateUrl: './task.component.html',
  styleUrls: ['./task.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    FormsModule,
  ]
})
export class LoginTaskComponent implements OnInit{
  username: string = '';
  password: string = '';
  taskMessage: string = '';
  tasks: any[] = [];
  userName: string = '';

  constructor(private taskService: TaskService) {
  }
  ngOnInit(): void {
    this.loadTasks();
  }
  create(): void {
    this.taskMessage = '';
    this.userName = '';

    this.taskService.startLoginProcess(this.username, this.password).subscribe({
      next: (res: any) => {
        console.log( res);

        this.taskMessage = "Tạo task thành công";
        this.loadTasks();
      },
      error: (err) => {
        console.error(err);
        this.taskMessage = "Tạo thất bại";
      }
    });
  }


  isLoading = false;

  loadTasks(): void {
    this.isLoading = true;
    this.taskService.getCurrentTasks().subscribe({
      next: (data) => {
        this.tasks = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error(err);
        this.taskMessage = "Lỗi load task";
        this.isLoading = false;
      }
    });
  }

  completeTask(taskId: string): void {
    this.taskService.completeTask(taskId, {}).subscribe({
      next: () => {
        this.loadTasks();
      },
      error: (err) => {
        console.error(err)
        this.taskMessage = "Lỗi Complete"
      }

    });
  }


}
