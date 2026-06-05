package com.stocktrack;

import com.stocktrack.view.cli.InputHelper;
import com.stocktrack.view.cli.LoginCLI;
import com.stocktrack.view.fx.JavaFXApp;
import javafx.application.Application;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {
    private static final String JVM_RELAUNCH_PROPERTY = "stocktrack.jvm.relaunched";
    private static final String JAVAFX_GRAPHICS_MODULE = "javafx.graphics";
    private static final String ENABLE_NATIVE_ACCESS = "--enable-native-access=" + JAVAFX_GRAPHICS_MODULE;
    private static final String ENABLE_UNSAFE_MEMORY_ACCESS = "--sun-misc-unsafe-memory-access=allow";

    public static void main(String[] args) {
        String viewType = loadViewType();

        if ("GUI".equalsIgnoreCase(viewType)) {
            if (shouldRelaunchGuiWithRequiredJvmOptions()) {
                relaunchGuiWithRequiredJvmOptions(args);
                return;
            }
            InputHelper.print("Avvio interfaccia grafica (GUI)...");
            Application.launch(JavaFXApp.class, args);
        } else {
            InputHelper.print("Avvio interfaccia testuale (CLI)...");
            LoginCLI loginCLI = new LoginCLI();
            loginCLI.start();
        }
    }

    private static String loadViewType() {
        Properties prop = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                InputHelper.print("Attenzione: config.properties non trovato. Default su CLI.");
                return "CLI";
            }
            prop.load(input);
            return prop.getProperty("view.type");
        } catch (IOException ex) {
            InputHelper.print("Errore lettura config: " + ex.getMessage());
            return "CLI";
        }
    }

    private static boolean shouldRelaunchGuiWithRequiredJvmOptions() {
        if (Boolean.getBoolean(JVM_RELAUNCH_PROPERTY)) {
            return false;
        }

        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        return !hasNativeAccessForJavaFx(inputArguments) || !inputArguments.contains(ENABLE_UNSAFE_MEMORY_ACCESS);
    }

    private static boolean hasNativeAccessForJavaFx(List<String> inputArguments) {
        for (String argument : inputArguments) {
            if (argument.startsWith("--enable-native-access=") && argument.contains(JAVAFX_GRAPHICS_MODULE)) {
                return true;
            }
        }
        return false;
    }

    private static void relaunchGuiWithRequiredJvmOptions(String[] args) {
        List<String> command = new ArrayList<>();
        command.add(Path.of(System.getProperty("java.home"), "bin", "java").toString());
        command.add(ENABLE_NATIVE_ACCESS);
        command.add(ENABLE_UNSAFE_MEMORY_ACCESS);
        command.add("-D" + JVM_RELAUNCH_PROPERTY + "=true");

        String moduleName = System.getProperty("jdk.module.main");
        if (moduleName != null && !moduleName.isBlank()) {
            command.add("--module-path");
            command.add(System.getProperty("jdk.module.path"));
            command.add("-m");
            command.add(moduleName + "/" + Main.class.getName());
        } else {
            command.add("-cp");
            command.add(System.getProperty("java.class.path"));
            command.add(Main.class.getName());
        }

        command.addAll(List.of(args));

        try {
            Process process = new ProcessBuilder(command)
                    .inheritIO()
                    .start();
            System.exit(process.waitFor());
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile riavviare la GUI con le opzioni JVM richieste", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Riavvio della GUI interrotto", e);
        }
    }
}
