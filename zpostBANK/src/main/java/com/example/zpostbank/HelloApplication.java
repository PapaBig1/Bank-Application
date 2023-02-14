package com.example.zpostbank;
/*


Programmed by yours truly, Zachary Post
11/16/2022


 */
import java.sql.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.sql.Connection;


public class HelloApplication extends Application {

    private TextField firstName;
    private TextField lastName;
    private TextField address;
    private TextField phoneNumber;

    private TextField accountNumber;
    private TextField accountBalance;
    private TextField WD;
    private TextField interestMonth;
    private TextField calInterest;
    int currentC = 0;

    @Override
    public void start(Stage stage) {
        try {
            String dbUrl = "jdbc:sqlite:bank.sqlite";
            connection = DriverManager.getConnection(dbUrl);
        } catch (SQLException e) {
            System.err.println(e);
            return;
        }
        stage.setTitle("ZMAN Bank");
        GridPane grid = new GridPane();
        VBox appContainer = new VBox(10);
        //appContainer.setPrefWidth(300);
        appContainer.setSpacing(20);
        appContainer.setPadding(new Insets(15,15,15,15));

        //first name
        grid.add(new Label("Customer First Name"),0,0);
        firstName = new TextField();
        grid.add(firstName,1,0);


        //last name
        grid.add(new Label("Customer Last Name"),0,1);
        lastName = new TextField();
        grid.add(lastName,1,1);


        //address
        grid.add(new Label("Address"),0,2);
        address = new TextField();
        grid.add(address,1,2);


        //phone number
        grid.add(new Label("Phone Number"),0,3);
        phoneNumber = new TextField();
        grid.add(phoneNumber,1,3);


        //Account number

        grid.add(new Label("Account Number"),0,4);
        accountNumber = new TextField();
        grid.add(accountNumber,1,4);

        //Account balance
        grid.add(new Label("Account Balance"),0,5);
        accountBalance = new TextField();
        accountBalance.setEditable(false);
        grid.add(accountBalance,1,5);

        //Account number

        grid.add(new Label("Withdraw/Deposit"),0,6);
        WD = new TextField();
        grid.add(WD,1,6);

        grid.add(new Label("Interest Month"),0,7);
        interestMonth = new TextField();
        grid.add(interestMonth,1,7);

        grid.add(new Label("Calculated Interest"),0,8);
        calInterest = new TextField();
        calInterest.setEditable(false);
        grid.add(calInterest,1,8);

        Scene scene = new Scene(grid, 550, 320);
        stage.setScene(scene);
        stage.show();

        //buttons

        Button addC = new Button("Add Customer");
        grid.add(addC,2,0);
        addC.setOnAction(event -> addCustomer());

        Button searchC = new Button("Search Customer By Last Name");
        grid.add(searchC,2,1);
        searchC.setOnAction(event -> searchCustomer());

        Button updateC = new Button("Update Customer By Last Name");
        grid.add(updateC,2,2);
        updateC.setOnAction(event -> updateCustomer());

        Button openS = new Button("Open Account/Join Existing by Account Number");
        grid.add(openS,2,3);
        openS.setOnAction(event -> openAccount());

        Button searchS = new Button("Search Account By Account Number");
        grid.add(searchS,2,4);
        searchS.setOnAction(event -> searchAccount());

        Button withdraw = new Button("Withdraw");
        grid.add(withdraw,2,5);
        withdraw.setOnAction(event -> withdrawAccount());

        Button deposit = new Button("Deposit");
        grid.add(deposit,2,6);
        deposit.setOnAction(event -> depositAccount());

        Button calI = new Button("Calculate Interest");
        grid.add(calI,2,7);
        calI.setOnAction(event -> calculateInterest());

        Button nextC = new Button("Next Customer");
        grid.add(nextC,2,8);
        nextC.setOnAction(event -> nextCustomer());

        Button previousC = new Button("Previous Customer");
        grid.add(previousC,2,9);
        previousC.setOnAction(event -> previousCustomer());

        Button clear = new Button("Clear");
        grid.add(clear,2,10);
        clear.setOnAction(event -> clearTextFields());

        Button exit = new Button("Exit");
        grid.add(exit,2,11);
        exit.setOnAction(event -> exitButtonClicked());




    }

    private void clearTextFields() {
        firstName.setText("");
        lastName.setText("");
        address.setText("");
        phoneNumber.setText("");
        accountNumber.setText("");
        accountBalance.setText("");
        WD.setText("");
        interestMonth.setText("");
        calInterest.setText("");
    }

    private void previousCustomer() {
        currentC -= 1;
        int lookup = currentC;
        Alert a = new Alert(Alert.AlertType.NONE);
        if (lookup <= 0) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: ID cannot be 0 or below");
            a.show();
            currentC ++;
        } else {
            //currectC -= 1;
            lookup = currentC;
            try (Statement statement = connection.createStatement();) {

                String selectSQL = "SELECT * FROM customers WHERE customerID = ?";
                PreparedStatement selectPS = connection.prepareStatement(selectSQL);
                selectPS.setString(1, String.valueOf(lookup));
                ResultSet customers = selectPS.executeQuery();

                int id = customers.getInt("customerID");
                String fName = customers.getString("firstName");
                String lName = customers.getString("lastName");
                String address2 = customers.getString("address");
                String phoneNum = customers.getString("phoneNum");
                String savingsNum = customers.getString("savingsNum");


                String message;
                message = "Customer ID: " + id
                        + "\nCustomer First Name: " + fName
                        + "\nCustomer Last Name: " + lName
                        + "\nCustomer Address: " + address2
                        + "\nCustomer Phone Number: " + phoneNum
                        + "\nCustomer Savings Account Number: " + savingsNum;

                // display message

                a.setAlertType(Alert.AlertType.INFORMATION);
                a.setContentText(message);
                a.show();

                firstName.setText(fName);
                lastName.setText(lName);
                address.setText(address2);
                phoneNumber.setText(phoneNum);
                accountNumber.setText(savingsNum);
                accountBalance.setText("");
                WD.setText("");
                interestMonth.setText("");
                calInterest.setText("");

                //System.out.println(lName);
                selectPS.close();
            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Customer does not exist");
                a.show();
                System.out.println(e);
            }
        }
    }

    private void nextCustomer() {
        currentC ++;
        int lookup = currentC;
        Alert a = new Alert(Alert.AlertType.NONE);
        try (Statement statement = connection.createStatement();) {

            String selectSQL = "SELECT * FROM customers WHERE customerID = ?";
            PreparedStatement selectPS = connection.prepareStatement(selectSQL);
            selectPS.setString(1, String.valueOf(lookup));
            ResultSet customers = selectPS.executeQuery();

            int id = customers.getInt("customerID");
            String fName = customers.getString("firstName");
            String lName = customers.getString("lastName");
            String address2 = customers.getString("address");
            String phoneNum = customers.getString("phoneNum");
            String savingsNum = customers.getString("savingsNum");


            String message;
            message = "Customer ID: " + id
                    + "\nCustomer First Name: " + fName
                    + "\nCustomer Last Name: " + lName
                    + "\nCustomer Address: " + address2
                    + "\nCustomer Phone Number: " + phoneNum
                    + "\nCustomer Savings Account Number: " + savingsNum;

            // display message

            a.setAlertType(Alert.AlertType.INFORMATION);
            a.setContentText(message);
            a.show();

            firstName.setText(fName);
            lastName.setText(lName);
            address.setText(address2);
            phoneNumber.setText(phoneNum);
            accountNumber.setText(savingsNum);
            accountBalance.setText("");
            WD.setText("");
            interestMonth.setText("");
            calInterest.setText("");

            //System.out.println(lName);
            selectPS.close();
        } catch (SQLException e) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Customer does not exist");
            a.show();
            System.out.println(e);
        }
    }

    private void calculateInterest() {
        int month = 0;
        int accountN = 0;
        double balanceS = 0;
        double inter = 0;

        Alert a = new Alert(Alert.AlertType.NONE);

        try {
            accountN = Integer.parseInt(accountNumber.getText());
            month = Integer.parseInt(interestMonth.getText());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        if (accountN <= 0 || month <= 0 || month > 12) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Month or account number cannot be blank,0, or over 12");
            a.show();
        } else {
            try (Statement statement = connection.createStatement();) {

                String selectSQL = "SELECT * FROM savings WHERE accountNum = ?";
                PreparedStatement selectPS = connection.prepareStatement(selectSQL);
                selectPS.setString(1, String.valueOf(accountN));
                ResultSet savings = selectPS.executeQuery();

                balanceS = Double.parseDouble(savings.getString("balance"));
                inter = Double.parseDouble(savings.getString("interestRate"));


                //System.out.println(lName);
                selectPS.close();
            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Account does not exist");
                a.show();
                System.out.println(e);
            }

            double calculatedRate = balanceS * inter * month;

            calInterest.setText(String.valueOf(calculatedRate));
            System.out.println(inter);

        }
    }

    private void depositAccount() {
        int accountN = 0;
        double deposit = 0;
        double balanceS = 0;
        Alert a = new Alert(Alert.AlertType.NONE);
        try {
            accountN = Integer.parseInt(accountNumber.getText());
            deposit = Double.parseDouble(WD.getText());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        if (accountN <= 0 || deposit <= 0) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Account Number or Withdraw amount cannot be blank or 0");
            a.show();
        } else {
            try (Statement statement = connection.createStatement();) {

                String selectSQL = "SELECT * FROM savings WHERE accountNum = ?";
                PreparedStatement selectPS = connection.prepareStatement(selectSQL);
                selectPS.setString(1, String.valueOf(accountN));
                ResultSet savings = selectPS.executeQuery();

                balanceS = Double.parseDouble(savings.getString("balance"));


                //System.out.println(lName);
                selectPS.close();
            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Account does not exist");
                a.show();
                System.out.println(e);
            }

                balanceS += deposit;
                try (Statement state = connection.createStatement();) {
                    String updateS = "UPDATE savings " +
                            "SET balance = ?" +
                            "WHERE accountNum = ?";
                    PreparedStatement updatePS = connection.prepareStatement(updateS);
                    updatePS.setString(1, String.valueOf(balanceS));
                    updatePS.setString(2, String.valueOf(accountN));
                    System.out.println(balanceS);
                    System.out.println(accountN);
                    int att = updatePS.executeUpdate();
                    updatePS.close();
                    a.setAlertType(Alert.AlertType.INFORMATION);
                    a.setContentText("SUCCESS: Deposited " + deposit + " into account " + accountN);
                    a.show();
                } catch (SQLException e) {
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("ERROR: cannot update");
                    a.show();
                    System.out.println(e.getMessage());
                }
            }
        }


    private void withdrawAccount() {
        int accountN = 0;
        double withdraw = 0;
        double balanceS = 0;
        Alert a = new Alert(Alert.AlertType.NONE);
        try {
            accountN = Integer.parseInt(accountNumber.getText());
            withdraw = Double.parseDouble(WD.getText());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        if (accountN <= 0 || withdraw <= 0) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Account Number or Withdraw amount cannot be blank or 0");
            a.show();
        } else {
            try (Statement statement = connection.createStatement();) {

                String selectSQL = "SELECT * FROM savings WHERE accountNum = ?";
                PreparedStatement selectPS = connection.prepareStatement(selectSQL);
                selectPS.setString(1, String.valueOf(accountN));
                ResultSet savings = selectPS.executeQuery();

                balanceS = Double.parseDouble(savings.getString("balance"));


                //System.out.println(lName);
                selectPS.close();
            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Account does not exist");
                a.show();
                System.out.println(e);
            }

            if (balanceS < withdraw) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Insufficient funds");
                a.show();
            } else {
                balanceS -= withdraw;
                try (Statement state = connection.createStatement();) {
                    String updateS = "UPDATE savings " +
                            "SET balance = ?" +
                            "WHERE accountNum = ?";
                    PreparedStatement updatePS = connection.prepareStatement(updateS);
                    updatePS.setString(1, String.valueOf(balanceS));
                    updatePS.setString(2, String.valueOf(accountN));
                    System.out.println(balanceS);
                    System.out.println(accountN);
                    int att = updatePS.executeUpdate();
                    updatePS.close();
                    a.setAlertType(Alert.AlertType.INFORMATION);
                    a.setContentText("SUCCESS: Withdrew " + withdraw + " from account " + accountN);
                    a.show();
                } catch (SQLException e) {
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("ERROR: cannot update");
                    a.show();
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void searchAccount() {
        int accountN = 0;
        Alert a = new Alert(Alert.AlertType.NONE);

        try {
            accountN = Integer.parseInt(accountNumber.getText());
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
        }
        if(accountN <= 0) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Account Number cannot be blank or 0");
            a.show();
        } else {
            try (Statement statement = connection.createStatement();) {

                String selectSQL = "SELECT * FROM savings WHERE accountNum = ?";
                PreparedStatement selectPS = connection.prepareStatement(selectSQL);
                selectPS.setString(1, String.valueOf(accountN));
                ResultSet savings = selectPS.executeQuery();

                int accountNumber = savings.getInt("accountNum");
                double balanceS = Double.parseDouble(savings.getString("balance"));
                double interestS = Double.parseDouble(savings.getString("interestRate"));


                String message;
                message = "Account Number: " + accountNumber
                        + "\nAccount Balance: " + balanceS
                        + "\nAccount Interest Rate: " + interestS;

                // display message

                a.setAlertType(Alert.AlertType.INFORMATION);
                a.setContentText(message);
                a.show();
                accountBalance.setText(String.valueOf(balanceS));

                //System.out.println(lName);
                selectPS.close();
            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Account does not exist");
                a.show();
                System.out.println(e);
            }

        }
    }


    private void openAccount() {
        String lName = lastName.getText();
        Alert a = new Alert(Alert.AlertType.NONE);
        int accountN = 0;
        double bal = 0;
        double inter = 0.09;
        int savingsN = 0;

        try {
            accountN = Integer.parseInt(accountNumber.getText());
        } catch (NumberFormatException e) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Account Number cannot be blank or 0");
            a.show();
            System.out.println(e.getMessage());
        }
        if (lName == "" || lName == null || accountN <= 0) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Account Number and Last Name must be filled out");
            a.show();
            System.out.println(accountN);
        } else {
            //System.out.println("success!");

            try (Statement statement = connection.createStatement();) {

                String selectSQL = "SELECT savingsNum FROM customers WHERE lastName = ?";
                PreparedStatement selectPS = connection.prepareStatement(selectSQL);
                selectPS.setString(1, lName);
                ResultSet customerS = selectPS.executeQuery();

                try {
                    savingsN = customerS.getInt("savingsNum");
                    System.out.println(savingsN);
                } catch (NumberFormatException e) {
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("ERROR: Cannot read");
                    a.show();
                    System.out.println(e.getMessage());
                }

                customerS.close();

            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Customer last name must exist");
                a.show();
                System.out.println(e.getMessage());
            }
            if (savingsN <= 0) {
                try (Statement statement = connection.createStatement();) {
                    String attachC = "UPDATE customers " +
                            "SET savingsNum = ?" +
                            "WHERE lastName = ?";
                    PreparedStatement attachPS = connection.prepareStatement(attachC);
                    attachPS.setString(1, String.valueOf(accountN));
                    attachPS.setString(2, lName);
                    int att = attachPS.executeUpdate();
                    attachPS.close();
                } catch (SQLException e) {
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("ERROR: cannot update");
                    a.show();
                    System.out.println(e.getMessage());
                }

                try (Statement statement = connection.createStatement();) {
                    String openSQL = "INSERT INTO savings (accountNum, balance, interestRate)" +
                            "VALUES (?, ?, ?)";
                    PreparedStatement openPS = connection.prepareStatement(openSQL);
                    openPS.setString(1, String.valueOf(accountN));
                    openPS.setString(2, String.valueOf(bal));
                    openPS.setString(3, String.valueOf(inter));
                    int open = openPS.executeUpdate();
                    openPS.close();
                    a.setAlertType(Alert.AlertType.INFORMATION);
                    a.setContentText("SUCCESS");
                    a.show();
                } catch (SQLException e) {
                    a.setAlertType(Alert.AlertType.ERROR);
                    a.setContentText("ERROR: Account already exists, joining accounts");
                    a.show();
                    System.out.println(e.getMessage());
                }
            } else {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: customer cannot switch accounts");
                a.show();
            }

        }










                firstName.setText("");
                lastName.setText("");
                address.setText("");
                phoneNumber.setText("");
                accountNumber.setText("");



        }


    public void updateCustomer() {
        String fName = firstName.getText();
        String lName = lastName.getText();
        String address2 = address.getText();
        String phone = phoneNumber.getText();

        Alert a = new Alert(Alert.AlertType.NONE);

        if (fName == "" || fName == null || lName == "" || lName == null || address2 == "" || address2 == null || phone == "" || phone == null) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: All fields must be filled out");
            a.show();
        } else {
            try (Statement statement = connection.createStatement();) {

                String updateSQL = "UPDATE customers " +
                        "SET firstName = ?, lastName = ?, address = ?, phoneNum = ? " +
                        "WHERE lastName = ?";
                PreparedStatement updatePS = connection.prepareStatement(updateSQL);
                updatePS.setString(1, fName);
                updatePS.setString(2, lName);
                updatePS.setString(3, address2);
                updatePS.setString(4, phone);
                updatePS.setString(5, lName);
                int up = updatePS.executeUpdate();
                updatePS.close();
                a.setAlertType(Alert.AlertType.INFORMATION);
                a.setContentText("SUCCESS customer " + fName + " has been updated");
                a.show();
                firstName.setText("");
                lastName.setText("");
                address.setText("");
                phoneNumber.setText("");

            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Cannot have duplicate data");
                a.show();
                System.out.println(e);
            }
        }
    }

    public void addCustomer() {
        //System.out.println("Test!");


        String fName = firstName.getText();
        String lName = lastName.getText();
        String address2 = address.getText();
        String phoneNum = phoneNumber.getText();

        Alert a = new Alert(Alert.AlertType.NONE);

        if (fName == "" || fName == null || lName == "" || lName == null || address2 == "" || address2 == null || phoneNum == "" || phoneNum == null) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: All fields must be filled out");
            a.show();
        } else {
            try (Statement statement = connection.createStatement();) {

                String insertSQL = "INSERT INTO customers (firstName, lastName, address, phoneNum)" +
                        "VALUES (?, ?, ?, ?)";
                PreparedStatement insertPS = connection.prepareStatement(insertSQL);
                insertPS.setString(1, fName);
                insertPS.setString(2, lName);
                insertPS.setString(3, address2);
                insertPS.setString(4, phoneNum);
                int inserts = insertPS.executeUpdate();
                insertPS.close();
                a.setAlertType(Alert.AlertType.INFORMATION);
                a.setContentText("SUCCESS customer " + fName + " has been added to the database");
                a.show();
                firstName.setText("");
                lastName.setText("");
                address.setText("");
                phoneNumber.setText("");

            } catch (SQLException e) {
                a.setAlertType(Alert.AlertType.ERROR);
                a.setContentText("ERROR: Customer already exists");
                a.show();
                System.out.println(e);
            }


        }
    }

    public void searchCustomer() {
        //System.out.println("Test!");

        String lookup = lastName.getText();
        Alert a = new Alert(Alert.AlertType.NONE);
        try (Statement statement = connection.createStatement();) {

            String selectSQL = "SELECT * FROM customers WHERE lastName = ?";
            PreparedStatement selectPS = connection.prepareStatement(selectSQL);
            selectPS.setString(1,lookup);
            ResultSet customers = selectPS.executeQuery();

            int id = customers.getInt("customerID");
            String fName = customers.getString("firstName");
            String lName = customers.getString("lastName");
            String address2 = customers.getString("address");
            String phoneNum = customers.getString("phoneNum");
            String savingsNum = customers.getString("savingsNum");


            String message;
                message = "Customer ID: " + id
                        + "\nCustomer First Name: " + fName
                        + "\nCustomer Last Name: " + lName
                        + "\nCustomer Address: " + address2
                        + "\nCustomer Phone Number: " + phoneNum
                        + "\nCustomer Savings Account Number: " + savingsNum;

            // display message

            a.setAlertType(Alert.AlertType.INFORMATION);
            a.setContentText(message);
            a.show();

            firstName.setText(fName);
            lastName.setText(lName);
            address.setText(address2);
            phoneNumber.setText(phoneNum);
            accountNumber.setText(savingsNum);
            accountBalance.setText("");
            WD.setText("");
            interestMonth.setText("");
            calInterest.setText("");


            //System.out.println(lName);
            selectPS.close();
        } catch (SQLException e) {
            a.setAlertType(Alert.AlertType.ERROR);
            a.setContentText("ERROR: Customer does not exist");
            a.show();
            System.out.println(e);
        }

    }

    public void exitButtonClicked() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        System.exit(0);
    }

    private static Connection connection;
    public static void main(String[] args) {
        launch();
    }
}