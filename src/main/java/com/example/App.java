package com.example;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.time.LocalDate;

public class App extends Application {

    private Label budgetLabel;
    private TextField amountField;
    private RadioButton incomeRadioButton, expenseRadioButton;
    private Button addTransactionButton;
    private ComboBox<String> categoryComboBox;
    private CheckBox showTransactionsCheckBox;
    private TableView<Transaction> transactionTable;
    private ObservableList<Transaction> transactions;
    private double currentBudget = 0.00;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        budgetLabel = new Label("Current Budget: $0.00");

        amountField = new TextField();
        amountField.setPromptText("Enter amount");

        incomeRadioButton = new RadioButton("Income");
        expenseRadioButton = new RadioButton("Expense");
        ToggleGroup group = new ToggleGroup();
        incomeRadioButton.setToggleGroup(group);
        expenseRadioButton.setToggleGroup(group);
        incomeRadioButton.setSelected(true);

        categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Salary", "Freelance", "Shopping", "Food", "Bills");
        categoryComboBox.setValue("Salary");

        showTransactionsCheckBox = new CheckBox("Show Transactions");

        transactionTable = new TableView<>();
        transactionTable.setVisible(false);
        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        TableColumn<Transaction, String> typeColumn = new TableColumn<>("Type");
        TableColumn<Transaction, Double> amountColumn = new TableColumn<>("Amount");

        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        typeColumn.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        amountColumn.setCellValueFactory(cellData -> cellData.getValue().amountProperty().asObject());

        transactionTable.getColumns().addAll(dateColumn, typeColumn, amountColumn);

        transactions = FXCollections.observableArrayList();
        transactionTable.setItems(transactions);

        addTransactionButton = new Button("Add Transaction");
        addTransactionButton.setOnAction(this::handleAddTransaction);

        StackPane background = new StackPane();
        Rectangle bgShape = new Rectangle(600, 500, Color.LIGHTGRAY);
        background.getChildren().add(bgShape);

        background.setOnMouseClicked(this::handleMouseClick);

        showTransactionsCheckBox.setOnAction(e -> transactionTable.setVisible(showTransactionsCheckBox.isSelected()));

        VBox mainLayout = new VBox(10);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(budgetLabel, amountField, incomeRadioButton, expenseRadioButton, categoryComboBox, addTransactionButton, showTransactionsCheckBox);

        HBox tableLayout = new HBox(10);
        tableLayout.setAlignment(Pos.CENTER);
        tableLayout.getChildren().add(transactionTable);

        GridPane gridLayout = new GridPane();
        gridLayout.setVgap(10);
        gridLayout.setHgap(10);
        gridLayout.add(budgetLabel, 0, 0);
        gridLayout.add(amountField, 0, 1);
        gridLayout.add(incomeRadioButton, 1, 1);
        gridLayout.add(expenseRadioButton, 1, 2);
        gridLayout.add(categoryComboBox, 0, 2);
        gridLayout.add(addTransactionButton, 0, 3);
        gridLayout.add(showTransactionsCheckBox, 1, 3);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(gridLayout);
        borderPane.setCenter(background);
        borderPane.setBottom(tableLayout);

        Scene scene = new Scene(borderPane, 600, 500);
        primaryStage.setTitle("Budget Tracker");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleAddTransaction(ActionEvent event) {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String transactionType = incomeRadioButton.isSelected() ? "Income" : "Expense";

            if ("Income".equals(transactionType)) {
                currentBudget += amount;
            } else if ("Expense".equals(transactionType)) {
                currentBudget -= amount;
            }

            budgetLabel.setText(String.format("Current Budget: $%.2f", currentBudget));

            transactions.add(new Transaction(LocalDate.now(), transactionType, amount));

            amountField.clear();
            categoryComboBox.setValue("Salary");
            incomeRadioButton.setSelected(true);
        } catch (NumberFormatException e) {
            showAlert("Invalid input", "Please enter a valid amount.");
        }
    }

    private void handleMouseClick(MouseEvent event) {
        System.out.println("Mouse clicked at: " + event.getX() + ", " + event.getY());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Transaction {
        private final LocalDate date;
        private final String type;
        private final double amount;

        public Transaction(LocalDate date, String type, double amount) {
            this.date = date;
            this.type = type;
            this.amount = amount;
        }

        public String getDate() {
            return date.toString();
        }

        public String getType() {
            return type;
        }

        public double getAmount() {
            return amount;
        }

        public StringProperty dateProperty() {
            return new SimpleStringProperty(date.toString());
        }

        public StringProperty typeProperty() {
            return new SimpleStringProperty(type);
        }

        public SimpleDoubleProperty amountProperty() {
            return new SimpleDoubleProperty(amount);
        }
    }
}
