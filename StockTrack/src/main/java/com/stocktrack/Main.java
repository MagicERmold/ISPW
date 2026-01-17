package com.stocktrack;

import com.stocktrack.view.cli.LoginCLI;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        LoginCLI loginCLI = new LoginCLI();
        loginCLI.start();
    }
}
