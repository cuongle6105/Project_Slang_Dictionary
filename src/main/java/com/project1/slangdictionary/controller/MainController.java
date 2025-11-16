package com.project1.slangdictionary.controller;

import com.project1.slangdictionary.dto.QuizQuestionDTO;
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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private TableColumn<SlangWordEntity, Void> columnActions;
    @FXML
    private TextField searchWord, searchDefinition;
    @FXML
    private Tab historyTab;

    @FXML
    private BorderPane rootPane;


    private final SlangWordService slangWordService;

    private final HistorySearchService historySearchService;

    private final ObservableList<SlangWordEntity> itemSearch = FXCollections.observableArrayList();
    private final ObservableList<HistorySearchEntity> itemHistory = FXCollections.observableArrayList();
    private static final int NUMBER_OF_QUIZ_QUESTIONS = 10;
    private static final int NUMBER_OF_OPTIONS = 4;

    public MainController() {
        this.slangWordService = new SlangWordServiceImpl();
        this.historySearchService = new HistorySearchServiceImpl();
    }

    @FXML
    private void initialize() {
        columnWord.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getWord()));
        columnDefs.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.join(" | ", c.getValue().getDefinition())));
        columnWord.prefWidthProperty().bind(tableViewSearch.widthProperty().multiply(0.30));
        columnDefs.prefWidthProperty().bind(tableViewSearch.widthProperty().multiply(0.55));
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

        columnActions.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox container = new HBox(5, editButton, deleteButton);

            {

                editButton.setOnAction(e -> {
                    SlangWordEntity slangWordEntity = getTableView().getItems().get(getIndex());
                    showEditDialog(slangWordEntity);
                });

                deleteButton.setOnAction(e -> {
                    SlangWordEntity slangWordEntity = getTableView().getItems().get(getIndex());
                    handleDelete(slangWordEntity);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
        columnActions.prefWidthProperty().bind(tableViewSearch.widthProperty().multiply(0.15)
                .subtract(2));
    }

    @FXML
    private void handleSearchWord() {
        if (searchWord.getText().isEmpty()) {
            itemSearch.setAll(slangWordService.findAll());
            return;
        }
        historySearchService.saveHistorySearch(new HistorySearchEntity("Slang Word", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), searchWord.getText()));
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
        historySearchService.saveHistorySearch(new HistorySearchEntity("Definition", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")), searchDefinition.getText()));
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

    @FXML
    private void handleAddSlangWord() {
        Dialog<SlangWordEntity> dialog = new Dialog<>();
        dialog.setTitle("Add new slang word");
        dialog.setHeaderText("Enter slang word and definitions (separated by \"|\")");

        if (rootPane != null && rootPane.getScene() != null) {
            dialog.initOwner(rootPane.getScene().getWindow());
        }

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField wordField = new TextField();
        wordField.setPromptText("Slang word");

        TextField defsField = new TextField();
        defsField.setPromptText("Definition1 | Definition2 | ...");

        grid.add(new Label("Slang word:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(new Label("Definitions:"), 0, 1);
        grid.add(defsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().lookupButton(okButtonType).setDisable(true);
        wordField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean disable = newVal.trim().isEmpty() || defsField.getText().trim().isEmpty();
            dialog.getDialogPane().lookupButton(okButtonType).setDisable(disable);
        });
        defsField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean disable = newVal.trim().isEmpty() || wordField.getText().trim().isEmpty();
            dialog.getDialogPane().lookupButton(okButtonType).setDisable(disable);
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButtonType) {
                String w = wordField.getText().trim();
                String d = defsField.getText().trim();
                if (w.isEmpty() || d.isEmpty()) {
                    return null;
                }
                return new SlangWordEntity(w, Arrays.asList(d.split("\\s*\\|\\s*")));
            }
            return null;
        });

        Optional<SlangWordEntity> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        SlangWordEntity slangWordEntity = result.get();

        if (slangWordService.add(slangWordEntity)) {
            showInfo("Success", "Added new slang word successfully.");
        }
        else handleDuplicateOrOverwrite(slangWordEntity);
        itemSearch.setAll(slangWordService.findAll());
    }


    private void handleDuplicateOrOverwrite(SlangWordEntity slangWordEntity) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Slang word already exists");
        alert.setHeaderText("The slang word \"" + slangWordEntity.getWord() + "\" already exists.");
        alert.setContentText("Choose how to handle it:");

        ButtonType duplicateBtn = new ButtonType("Duplicate");
        ButtonType overwriteBtn = new ButtonType("Overwrite");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(duplicateBtn, overwriteBtn, cancelBtn);

        Optional<ButtonType> choice = alert.showAndWait();

        if (choice.isEmpty() || choice.get() == cancelBtn) {

            return;
        }

        if (choice.get() == duplicateBtn) {
            slangWordService.addDuplicate(slangWordEntity);
            showInfo("Success", "Duplicated slang word successfully.");
        } else if (choice.get() == overwriteBtn) {
            slangWordService.addOverwrite(slangWordEntity);
            showInfo("Success", "Overwrote slang word successfully.");
        }
    }

    private void showInfo(String title, String message) {
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle(title);
        info.setHeaderText(null);
        info.setContentText(message);
        info.showAndWait();
    }

    private void showEditDialog(SlangWordEntity slangWordEntity) {
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Edit slang word");
        dialog.setHeaderText("Edit definitions for slang word");

        if (rootPane != null && rootPane.getScene() != null) {
            dialog.initOwner(rootPane.getScene().getWindow());
        }

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, cancelButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField wordField = new TextField(slangWordEntity.getWord());
        wordField.setEditable(false);
        wordField.setDisable(true);

        String currentDefinitions = String.join(" | ", slangWordEntity.getDefinition());
        TextField defsField = new TextField(currentDefinitions);
        defsField.setPromptText("Definition1 | Definition2 | ...");

        grid.add(new Label("Slang word:"), 0, 0);
        grid.add(wordField, 1, 0);
        grid.add(new Label("Definitions:"), 0, 1);
        grid.add(defsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.getDialogPane().lookupButton(okButtonType).setDisable(defsField.getText().trim().isEmpty());;

        defsField.textProperty().addListener((obs, oldVal, newVal) -> {
            boolean disable = newVal.trim().isEmpty();
            dialog.getDialogPane().lookupButton(okButtonType).setDisable(disable);
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType == okButtonType) {
                String d = defsField.getText().trim();
                if (d.isEmpty()) {
                    return null;
                }
                List<String> definitions = Arrays.asList(d.split("\\s*\\|\\s*"));
                if (definitions.isEmpty()) return null;
                return definitions;
            }
            return null;
        });

        Optional<List<String>> result = dialog.showAndWait();

        if (result.isEmpty()) {
            return;
        }

        slangWordEntity.setDefinition(result.get());

        slangWordService.addOverwrite(slangWordEntity);
        showInfo("Success", "Updated definitions successfully.");
        itemSearch.setAll(slangWordService.findAll());
    }

    private void handleDelete(SlangWordEntity slangWordEntity) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete slang word");
        confirm.setHeaderText("Delete \"" + slangWordEntity.getWord() + "\"?");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        slangWordService.remove(slangWordEntity);

        // Xóa khỏi TableView
        showInfo("Deleted", "Slang word deleted successfully.");
        itemSearch.setAll(slangWordService.findAll());
    }

    @FXML
    private void handleReset() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset slang word");
        confirm.setHeaderText("Reset to original language word");
        confirm.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        slangWordService.reset();
        itemSearch.setAll(slangWordService.findAll());
    }
    @FXML
    private void handleExit() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm");
        confirm.setHeaderText("Do you want to exit?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }
        System.exit(0);
    }

    @FXML
    private void handleRandom() {
        SlangWordEntity slangWordEntity = slangWordService.random();
        Alert confirm = new Alert(Alert.AlertType.INFORMATION);
        confirm.setTitle("Random slang word");
        confirm.setHeaderText(slangWordEntity.getWord());
        confirm.setContentText(String.join(" | ", slangWordEntity.getDefinition()));
        confirm.showAndWait();
    }

    @FXML
    private void handleQuizSlangWord() {
        List<SlangWordEntity> slangWordEntities = slangWordService.findAll();
        if (slangWordEntities.size() < NUMBER_OF_QUIZ_QUESTIONS) {
            showWarningAlert("Quiz", "No Enough Data", "Please add enough data to make it " + NUMBER_OF_QUIZ_QUESTIONS + " slang words, or reset the slang words to the default");
            return;
        }
        showQuizStage(slangWordService.createSlangWordsQuizQuestions(NUMBER_OF_QUIZ_QUESTIONS, NUMBER_OF_OPTIONS), "Quiz: Slang Word -> Definition");
    }

    @FXML
    private void handleQuizDefinition() {
        List<SlangWordEntity> slangWordEntities = slangWordService.findAll();
        if (slangWordEntities.size() < NUMBER_OF_QUIZ_QUESTIONS) {
            showWarningAlert("Quiz", "No Enough Data", "Please add enough data to make it " + NUMBER_OF_QUIZ_QUESTIONS + " slang words, or reset the slang words to the default");
            return;
        }
        showQuizStage(slangWordService.createDefinitionsQuizQuestions(NUMBER_OF_QUIZ_QUESTIONS, NUMBER_OF_OPTIONS), "Quiz: Definition -> Slang Word");
    }

    private void showWarningAlert(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void showQuizStage(List<QuizQuestionDTO> questions, String title) {
        Stage stage = new Stage();
        stage.initOwner(rootPane.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label lblIndex = new Label();
        Label lblQuestion = new Label();
        lblQuestion.setWrapText(true);

        ToggleGroup toggleGroup = new ToggleGroup();
        List<RadioButton> optionButtons = new ArrayList<>();
        VBox optionsBox = new VBox(8);

        for (int i = 0; i < NUMBER_OF_OPTIONS; i++) {
            RadioButton rb = new RadioButton();
            rb.setToggleGroup(toggleGroup);
            rb.setWrapText(true);
            rb.setMaxWidth(Double.MAX_VALUE);
            optionButtons.add(rb);
            optionsBox.getChildren().add(rb);
        }

        Label lblFeedback = new Label();

        Button btnNext = new Button("Next");
        Button btnRestart = new Button("Restart");
        Button btnFinish = new Button("Finish");

        HBox buttonsBox = new HBox(10, btnNext, btnRestart, btnFinish);

        VBox root = new VBox(12,
                lblTitle,
                lblIndex,
                lblQuestion,
                optionsBox,
                lblFeedback,
                buttonsBox
        );
        root.setPadding(new Insets(15));
        Scene scene = new Scene(root, 600, 350);
        stage.setScene(scene);


        final int[] currentIndex = {0};
        final int[] score = {0};
        final boolean[] questionAnswered = {false};


        Runnable loadQuestion = () -> {
            QuizQuestionDTO quizQuestionDTO = questions.get(currentIndex[0]);
            lblIndex.setText("Question " + (currentIndex[0] + 1) + "/" + questions.size());
            String text = "Which option best matches \"" + quizQuestionDTO.getQuestion() + "\"?";
            lblQuestion.setText(text);


            toggleGroup.selectToggle(null);
            lblFeedback.setText("");
            for (RadioButton rb : optionButtons) {
                rb.setStyle("");
            }

            for (int i = 0; i < NUMBER_OF_OPTIONS; i++) {
                optionButtons.get(i).setText(quizQuestionDTO.getOptions().get(i));
            }

            questionAnswered[0] = false;
            btnNext.setDisable(true);
        };


        loadQuestion.run();


        toggleGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle == null || questionAnswered[0]) return;

            QuizQuestionDTO questionDTO = questions.get(currentIndex[0]);
            int selectedIndex = optionButtons.indexOf(newToggle);


            for (RadioButton rb : optionButtons) {
                rb.setStyle("");
            }

            if (selectedIndex == questionDTO.getCorrectAnswerIndex()) {
                score[0]++;
                lblFeedback.setText("Yep, that's right!");
                optionButtons.get(selectedIndex)
                        .setStyle("-fx-background-color: #b9f6ca;");
            } else {
                lblFeedback.setText("Nope, that’s not right");
                optionButtons.get(selectedIndex)
                        .setStyle("-fx-background-color: #ff8a80;");
                optionButtons.get(questionDTO.getCorrectAnswerIndex())
                        .setStyle("-fx-background-color: #b9f6ca;");
            }

            questionAnswered[0] = true;
            btnNext.setDisable(currentIndex[0] == questions.size() - 1);
        });


        btnNext.setOnAction(e -> {
            if (!questionAnswered[0]) {
                showWarningAlert(title, "No answer selected", "You have to answer this question before going to the next one");
                return;
            }
            if (currentIndex[0] < questions.size() - 1) {
                currentIndex[0]++;
                loadQuestion.run();
            }
        });

        // Nút Finish
        btnFinish.setOnAction(e -> {
            showFinalScore(score[0], questions.size());
            stage.close();
        });

        btnRestart.setOnAction(e -> {
            score[0] = 0;
            currentIndex[0] = 0;
            Collections.shuffle(questions);
            loadQuestion.run();
        });

        stage.show();
    }

    private void showFinalScore(int score, int total) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Quiz Result");
        alert.setHeaderText("Your Quiz Result: " + score + "/" + total);

        String message;
        if (score >= 8) {
            message = "Congratulations! You really know your slang!";
        } else if (score < 5) {
            message = "Your score isn’t very high yet—keep trying!";
        } else {
            message = "Pretty good already—just a bit more practice and you’ll nail it!";
        }

        alert.setContentText(message);
        alert.showAndWait();
    }
}