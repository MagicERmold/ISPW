package com.stocktrack;

import com.stocktrack.view.cli.AcquistaProdottiFornitoriCLI;
import javafx.application.Application;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {

    private static final String GUI_VIEW = "GUI";
    private static final String ENABLE_JAVAFX_NATIVE_ACCESS = "--enable-native-access=javafx.graphics";
    private static final String ALLOW_UNSAFE_MEMORY_ACCESS = "--sun-misc-unsafe-memory-access=allow";
    private static final int UNSAFE_MEMORY_ACCESS_WARNING_JDK = 23;

    public static void main(String[] args) {
        String viewType = loadViewType();
        if (GUI_VIEW.equalsIgnoreCase(viewType)) {
            relaunchGuiWithRequiredJvmOptionsIfNeeded(args);
            Application.launch(JavaFXApp.class, args);
        } else {
            new AcquistaProdottiFornitoriCLI().start();
        }
    }

    private static String loadViewType() {
        Properties properties = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                return "CLI";
            }
            properties.load(input);
            return properties.getProperty("view.type", "CLI");
        } catch (IOException e) {
            return "CLI";
        }
    }

    private static void relaunchGuiWithRequiredJvmOptionsIfNeeded(String[] args) {
        List<String> missingOptions = findMissingGuiJvmOptions();
        if (missingOptions.isEmpty()) {
            return;
        }

        List<String> command = buildRelaunchCommand(args, missingOptions);
        try {
            Process process = new ProcessBuilder(command).inheritIO().start();
            int exitCode = process.waitFor();
            System.exit(exitCode);
        } catch (IOException e) {
            throw new IllegalStateException("Impossibile riavviare la GUI con i parametri JVM richiesti.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Riavvio della GUI interrotto.", e);
        }
    }

    private static List<String> findMissingGuiJvmOptions() {
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments();
        List<String> missingOptions = new ArrayList<>();

        if (inputArguments.stream().noneMatch(Main::enablesJavaFxNativeAccess)) {
            missingOptions.add(ENABLE_JAVAFX_NATIVE_ACCESS);
        }
        if (Runtime.version().feature() >= UNSAFE_MEMORY_ACCESS_WARNING_JDK
                && inputArguments.stream().noneMatch(Main::configuresUnsafeMemoryAccess)) {
            missingOptions.add(ALLOW_UNSAFE_MEMORY_ACCESS);
        }

        return missingOptions;
    }

    private static boolean enablesJavaFxNativeAccess(String option) {
        return option.startsWith("--enable-native-access=") && option.contains("javafx.graphics");
    }

    private static boolean configuresUnsafeMemoryAccess(String option) {
        return option.startsWith("--sun-misc-unsafe-memory-access=");
    }

    private static List<String> buildRelaunchCommand(String[] args, List<String> missingOptions) {
        List<String> command = new ArrayList<>();
        command.add(javaExecutable());
        command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        command.addAll(missingOptions);
        appendLaunchTarget(command);
        command.addAll(Arrays.asList(args));
        return command;
    }

    private static String javaExecutable() {
        return System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
    }

    private static void appendLaunchTarget(List<String> command) {
        Module module = Main.class.getModule();
        String modulePath = System.getProperty("jdk.module.path");
        if (module.isNamed() && modulePath != null && !modulePath.isBlank()) {
            command.add("--module-path");
            command.add(modulePath);
            command.add("-m");
            command.add(module.getName() + "/" + Main.class.getName());
            return;
        }

        command.add("-cp");
        command.add(System.getProperty("java.class.path"));
        command.add(Main.class.getName());
    }
}
