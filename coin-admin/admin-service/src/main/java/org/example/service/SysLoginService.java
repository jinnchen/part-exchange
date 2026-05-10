package org.example.service;

import org.example.model.LoginResult;

public interface SysLoginService {
    LoginResult login(String username, String password);
}
