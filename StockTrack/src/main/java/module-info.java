module com.stocktrack {
    requires java.sql;
    requires javafx.controls;
    requires javafx.fxml;

    exports com.stocktrack;
    exports com.stocktrack.bean;
    exports com.stocktrack.boundary;
    exports com.stocktrack.config;
    exports com.stocktrack.controller;
    exports com.stocktrack.entity;
    exports com.stocktrack.exceptions;
    exports com.stocktrack.pattern.adapter;
    exports com.stocktrack.pattern.factory;
    exports com.stocktrack.pattern.singleton;
    exports com.stocktrack.persistence.dao;
    exports com.stocktrack.security;

    opens com.stocktrack.bean to javafx.base;
    opens com.stocktrack.entity to javafx.base;
    opens com.stocktrack.view.fx.controller to javafx.fxml;
}
