package com.project1.slangdictionary.controller;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.service.SlangWordService;
import com.project1.slangdictionary.service.impl.SlangWordServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Dictionary;

public class MainController {
    @FXML
    private TableView<SlangWordEntity> tableView;
    @FXML
    private TableColumn<SlangWordEntity, String> columnWord, columnDefs;

    private final SlangWordService slangWordService;


    public MainController() {
        this.slangWordService = new SlangWordServiceImpl();
    }

    @FXML
    private void initialize() {
        columnWord.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getWord()));
        columnDefs.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(String.join(" | ", c.getValue().getDefinition())));
        tableView.setItems(FXCollections.observableArrayList(slangWordService.findAll()));
    }
}