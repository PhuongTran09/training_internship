import {Injectable} from '@angular/core';
import {CanActivateFn, Router} from '@angular/router';

export const authGuard: CanActivateFn = () => {
  const token = localStorage.getItem('access_token');
  if (!token) {
    window.location.href = '/login';
    return false;
  }
  return true;
};
