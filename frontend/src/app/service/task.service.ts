import {HttpClient, HttpHeaders, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {apiUrl} from "../utils/api.config";
import { AuthService } from './auth.service';

const BASE_URL = apiUrl.BASE_URL + '/process';

@Injectable({
  providedIn: 'root'
})
export class TaskService {

  constructor(
  private http: HttpClient,
  private authService: AuthService
) {}

  startLoginProcess(username: string, password: string): Observable<any> {
    const params = new HttpParams()
      .set('username', username)
      .set('password', password);

    return this.http.get(`${BASE_URL}/start`, {params});
  }

  getCurrentTasks(): Observable<any> {
    return this.http.get(`${BASE_URL}/tasks`);
  }

  completeTask(taskId: string, variables: any = {}): Observable<any> {
    return this.http.post(`${BASE_URL}/tasks/${taskId}/complete`, variables);
  }




}
