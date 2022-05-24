package com.example.mydb;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class HelloApplication extends Application {

    String checkUser, checkPw, checkHostname,checkPort,checkSID;

    private void showAlertWithHeaderText(String title,String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Compiere database Management");
        primaryStage.setResizable(true);

        //Scene 1 : Connecting and testing Connection
        BorderPane bp = new BorderPane();
        bp.setPadding(new Insets(10,50,50,50));
        HBox hb = new HBox();
        hb.setPadding(new Insets(20,20,20,30));
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20,20,20,20));
        gridPane.setHgap(5);
        gridPane.setVgap(5);
        Label lblHostName = new Label("Hostname");
        final TextField txtHostName = new TextField();
        Label lblSID = new Label("SID");
        final TextField txtSID = new TextField();
        Label lblPort = new Label("Port");
        final TextField txtPort = new TextField();
        Label lblUserName = new Label("Username");
        final TextField txtUserName = new TextField();
        Label lblPassword = new Label("Password");
        final PasswordField pf = new PasswordField();
        Button btnTest = new Button("Test connexion");
        final Label lblMessage = new Label();
        Button btnConnect = new Button("Connect");
        gridPane.add(lblHostName, 0, 0);
        gridPane.add(txtHostName, 1, 0);
        gridPane.add(lblUserName, 0, 1);
        gridPane.add(lblPassword,0,2);
        gridPane.add(pf,1,2);
        gridPane.add(txtUserName, 1, 1);
        gridPane.add(lblPort,0,3);
        gridPane.add(txtPort,1,3);
        gridPane.add(lblSID,0,4);
        gridPane.add(txtSID,1,4);
        gridPane.add(btnTest, 1, 5);
        gridPane.add(lblMessage, 2, 7);
        gridPane.add(btnConnect, 1, 6);
        Text text = new Text("Connect to your Database");
        text.setFont(Font.font ("Verdana", 15));
        hb.getChildren().add(text);
        bp.setId("bp");
        gridPane.setId("root");
        btnConnect.setId("btnLogin");
        btnConnect.setId("btnConnect");
        text.setId("text");
        btnTest.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                checkPort = txtPort.getText().toString();
                checkSID = txtSID.getText().toString();
                checkUser = txtUserName.getText().toString();
                checkPw = pf.getText().toString();
                checkHostname = txtHostName.getText().toString();
                try {
                    Class.forName("oracle.jdbc.driver.OracleDriver");
                    Connection con = DriverManager.getConnection(
                            "jdbc:oracle:thin:@"+checkHostname+":"+checkPort+":"+checkSID, checkUser, checkPw);
                    Statement stmt = con.createStatement();
                    ResultSet rs = stmt.executeQuery("select * from ad_user");
                    if(rs.next()){
                        lblMessage.setText("Connected to Database");
                        lblMessage.setTextFill(Color.GREEN);
                    }
                } catch (Exception e) {
                    showAlertWithHeaderText("Error",e.getMessage());
                    //lblMessage.setText(e.getMessage());
                    //lblMessage.setTextFill(Color.RED);
                    System.out.println(e);
                }
            }
        });
        bp.setTop(hb);
        bp.setCenter(gridPane);
        Scene scene = new Scene(bp);



        //Scene 2 : executing sql querry
        BorderPane bp2 = new BorderPane();
        bp2.setPadding(new Insets(10,50,50,50));
        GridPane gridPane2 = new GridPane();
        gridPane2.setPadding(new Insets(20,20,20,20));
        gridPane2.setHgap(5);
        gridPane2.setVgap(5);
        final TextArea txtsql = new TextArea();
        Label lblsql = new Label("Sql Query");
        Button btnsend = new Button("Send");
        final Label lblresult = new Label();
        gridPane2.add(lblsql, 0, 0);
        gridPane2.add(txtsql, 1, 0);
        gridPane2.add(btnsend, 1, 1);
        gridPane2.add(lblresult,1,2);
        Reflection r2 = new Reflection();
        bp2.setId("bp2");
        gridPane2.setId("root2");
        bp2.setCenter(gridPane2);
        Scene scene2 = new Scene(bp2);
        btnConnect.setOnAction(new EventHandler<ActionEvent>() {
             public void handle(ActionEvent event) {
                 checkPort = txtPort.getText().toString();
                 checkSID = txtSID.getText().toString();
                 checkUser = txtUserName.getText().toString();
                 checkPw = pf.getText().toString();
                 checkHostname = txtHostName.getText().toString();
                 try {
                     DBConnect DB = new DBConnect(checkHostname,checkUser,checkPort,checkSID,checkPw);
                     Connection con = DB.Connect();
                     Statement stmt = con.createStatement();
                     ResultSet rs = stmt.executeQuery("select * from ad_user");
                     if(rs.next()){
                         lblMessage.setText("Connected to Database");
                         lblMessage.setTextFill(Color.GREEN);
                         primaryStage.setScene(scene2);
                     }
                 } catch (Exception e) {
                     showAlertWithHeaderText("Error",e.getMessage());
                     //lblMessage.setText(e.getMessage());
                     //lblMessage.setTextFill(Color.RED);
                     System.out.println(e);
                 }

             }
         });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}