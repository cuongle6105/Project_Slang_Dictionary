package com.project1.slangdictionary.controller;

import com.project1.slangdictionary.entity.HistorySearchEntity;
import com.project1.slangdictionary.entity.SlangWordEntity;
import com.project1.slangdictionary.service.HistorySearchService;
import com.project1.slangdictionary.service.SlangWordService;
import com.project1.slangdictionary.service.impl.HistorySearchServiceImpl;
import com.project1.slangdictionary.service.impl.SlangWordServiceImpl;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Dictionary;
import java.util.List;

public class MainController {
    @FXML
    private TableView<SlangWordEntity> tableViewSearch;
    @FXML
    private TableView<HistorySearchEntity> tableViewHistory;
    @FXML
    private TableColumn<SlangWordEntity, String> columnWord, columnDefs;
    @FXML
    private TableColumn<HistorySearchEntity, String> columnSearchBy, columnTime, columnContent;
    @FXML
    private TextField searchWord, searchDefinition;
    @FXML
    private Tab historyTab;


    private final SlangWordService slangWordService;

    private final HistorySearchService historySearchService;

    private final ObservableList<SlangWordEntity> itemSearch = FXCollections.observableArrayList();
    private final ObservableList<HistorySearchEntity> itemHistory = FXCollections.observableArrayList();

    public MainController() {
        this.slangWordService = new SlangWordServiceImpl();
        this.historySearchService = new HistorySearchServiceImpl();
    }

    @FXML
    private void initialize() {
        columnWord.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getWord()));
        columnDefs.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.join(" | ", c.getValue().getDefinition())));
        columnWord.prefWidthProperty().bind(tableViewSearch.widthProperty().multiply(0.30));
        columnDefs.prefWidthProperty().bind(tableViewSearch.widthProperty().multiply(0.70)
                .subtract(2));
        tableViewSearch.setItems(itemSearch);
        tableViewSearch.setPlaceholder(new Label("Not Found!"));
        itemSearch.setAll(slangWordService.findAll());

        columnSearchBy.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getMethodSearch()));
        columnTime.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTime()));
        columnContent.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getContent()));
        columnSearchBy.prefWidthProperty().bind(tableViewHistory.widthProperty().multiply(0.20));
        columnTime.prefWidthProperty().bind(tableViewHistory.widthProperty().multiply(0.30));
        columnContent.prefWidthProperty().bind(tableViewHistory.widthProperty().multiply(0.50).subtract(2));
        tableViewHistory.setItems(itemHistory);
        tableViewHistory.setPlaceholder(new Label("History Empty!"));
        itemHistory.setAll(historySearchService.findAll());
    }

    @FXML
    private void handleSearchWord() {
        if (searchWord.getText().isEmpty()) {
            itemSearch.setAll(slangWordService.findAll());
            return;
        }
        historySearchService.saveHistorySearch(new HistorySearchEntity("Slang Word", LocalDateTime.now().toString(), searchWord.getText()));
        SlangWordEntity slangWordEntity = slangWordService.findByWord(searchWord.getText());
        if (slangWordEntity == null) {
            itemSearch.clear();
        }
        else {
            itemSearch.setAll(slangWordEntity);
        }
    }

    @FXML
    private void handleSearchDefinition() {
        if (searchDefinition.getText().isEmpty()) {
            itemSearch.setAll(slangWordService.findAll());
            return;
        }
        historySearchService.saveHistorySearch(new HistorySearchEntity("Definition", LocalDateTime.now().toString(), searchDefinition.getText()));
        List<SlangWordEntity> slangWordEntities = slangWordService.findByDefinition(searchDefinition.getText());
        if (slangWordEntities == null || slangWordEntities.isEmpty()) {
            itemSearch.clear();
        }
        else {
            itemSearch.setAll(slangWordEntities);
        }
    }

    @FXML
    private void handleHistoryTabChanged(javafx.event.Event e) {
        if (historyTab.isSelected()) {
            itemHistory.setAll(historySearchService.findAll());
        }
    }
    
    @FXML
    private void handleClearHistory() throws IOException {
        historySearchService.clearHistorySearch();
        itemHistory.setAll(historySearchService.findAll());
    }
}