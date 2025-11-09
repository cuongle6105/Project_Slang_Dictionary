package com.project1.slangdictionary.controller;

import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.service.SlangWordService;
import com.project1.slangdictionary.service.impl.SlangWordServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Dictionary;
import java.util.List;

public class MainController {
    @FXML
    private TableView<SlangWordEntity> tableView;
    @FXML
    private TableColumn<SlangWordEntity, String> columnWord, columnDefs;
    @FXML
    private TextField searchWord;


    private final SlangWordService slangWordService;

    private final ObservableList<SlangWordEntity> items = FXCollections.observableArrayList();

    public MainController() {
        this.slangWordService = new SlangWordServiceImpl();
    }

    @FXML
    private void initialize() {
        columnWord.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getWord()));
        columnDefs.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.join(" | ", c.getValue().getDefinition())));
        tableView.setItems(items);
        tableView.setPlaceholder(new Label("Not Found!"));

        items.setAll(slangWordService.findAll());
    }

    @FXML
    private void handleSearchWord() {
        if (searchWord.getText().isEmpty()) {
            items.setAll(slangWordService.findAll());
            return;
        }
        SlangWordEntity slangWordEntity = slangWordService.findByWord(searchWord.getText());
        if (slangWordEntity == null) {
            items.clear();
        }
        else {
            items.setAll(slangWordService.findByWord(searchWord.getText()));
        }
    }
}