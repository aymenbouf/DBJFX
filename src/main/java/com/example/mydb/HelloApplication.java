package com.example.mydb;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

import java.io.*;
import java.net.SocketException;
import java.sql.*;

public class HelloApplication extends Application {
    private ObservableList<ObservableList> data;
    private TableView tableview;

    String checkUser, checkPw, checkHostname,checkPort,checkSID,checkQuery,srvr,usrnm,prt,psswrd,rmtfile;
    String result = "";

    private void showAlertWithHeaderText(String title,String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Compiere database Management");
        primaryStage.setMaxHeight(500);
        primaryStage.setMaxWidth(1000);


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
        Label lblsql = new Label("SQL query :");
        Button btnsend = new Button("Send");
        Button InsertFile = new Button("Insert from file");
        HBox hBox = new HBox();
        hBox.getChildren().addAll(btnsend,InsertFile);
        gridPane2.add(lblsql, 0, 0);
        gridPane2.add(txtsql, 1, 0);
        gridPane2.add(hBox, 1, 1);
        Reflection r2 = new Reflection();
        bp2.setId("bp2");
        gridPane2.setId("root2");
        bp2.setCenter(gridPane2);
        InsertFile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt*"));
                File file = fileChooser.showSaveDialog(primaryStage);
                if(file == null){
                    showAlertWithHeaderText("No file selected","please select a file");
                }else{
                    BufferedReader reader;
                    try {
                        DBConnect DB = new DBConnect(checkHostname,checkUser,checkPort,checkSID,checkPw);
                        Connection con = DB.Connect();
                        reader = new BufferedReader(new FileReader(file));
                        String line = reader.readLine();
                        boolean found = false;
                        while (line != null) {
                            String[] values = line.split(",  ");
                            PreparedStatement st = con.prepareStatement("select * from ad_user where AD_USER_ID = ?");
                            st.setInt(1, Integer.parseInt(values[0]));
                            ResultSet resultSet = st.executeQuery();
                            if (resultSet.next()){
                                System.out.println("Row already exists");
                            }else{
                                found = true;
                                ResultSet resultset;
                                PreparedStatement stmt=con.prepareStatement("insert into ad_user (AD_USER_ID,AD_CLIENT_ID,AD_ORG_ID,ISACTIVE,CREATED,CREATEDBY,UPDATED,UPDATEDBY,NAME,ISFULLBPACCESS,NOTIFICATIONTYPE,VALUE) values (?,?,?,?,?,?,?,?,?,?,?,?)");
                                stmt.setInt(1, Integer.parseInt(values[0]));
                                stmt.setInt(2, Integer.parseInt(values[1]));
                                stmt.setInt(3, Integer.parseInt(values[2]));
                                stmt.setString(4,values[3]);
                                stmt.setTimestamp(5, Timestamp.valueOf(values[4]));
                                stmt.setInt(6, Integer.parseInt(values[5]));
                                stmt.setTimestamp(7, Timestamp.valueOf(values[6]));
                                stmt.setInt(8, Integer.parseInt(values[7]));
                                stmt.setString(9,values[8]);
                                stmt.setString(10,values[31]);
                                stmt.setString(11,values[33]);
                                stmt.setString(12,values[35]);
                                resultset = stmt.executeQuery();
                                System.out.println(resultset.toString());
                                showAlertWithHeaderText("Success","Done reading the file, please check your database");
                            }
                            line = reader.readLine();
                        }
                        if (found == false) showAlertWithHeaderText("Nothing to add","No new rows has been detected");
                        con.close();
                        reader.close();
                    } catch (IOException e) {
                        showAlertWithHeaderText("Error",e.getMessage());
                        e.printStackTrace();
                    } catch (SQLException e) {
                        showAlertWithHeaderText("SQL Exception",e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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
        tableview = new TableView();
        tableview.prefHeightProperty().bind(primaryStage.heightProperty());
        tableview.prefWidthProperty().bind(primaryStage.widthProperty());
        data = FXCollections.observableArrayList();

        //Scene 3 : SQl Data in table view
        VBox VB = new VBox();
        VB.setPadding(new Insets(20,20,20,20));
        Button btnSvFile = new Button("Save to File");
        btnSvFile.setPadding(new Insets(10,10,10,10));
        Button btnSvFtp = new Button("Save to Server");
        btnSvFtp.setPadding(new Insets(10,10,10,10));
        Button btnBack = new Button("Execute Sql");
        btnBack.setPadding(new Insets(10,10,10,10));
        HBox HB = new HBox();
        HB.setPadding(new Insets(20,20,20,20));

        HB.getChildren().addAll(btnSvFtp,btnSvFile,btnBack);
        VB.getChildren().addAll(tableview,HB);
        Scene scene3 = new Scene(VB);
        btnsend.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                checkQuery = txtsql.getText();
                if(checkQuery.isEmpty()){
                    showAlertWithHeaderText("No sql query detected","Please enter an sql query");
                }else{
                    try {
                        DBConnect DB = new DBConnect(checkHostname,checkUser,checkPort,checkSID,checkPw);
                        Connection con = DB.Connect();
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery(checkQuery);
                        for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                            //We are using non property style for making dynamic table
                            final int j = i;
                            TableColumn<ObservableList, String> col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                            col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                                public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                    if (param.getValue().get(j) == null){
                                        return new SimpleStringProperty("null");
                                    }else {
                                        return new SimpleStringProperty(param.getValue().get(j).toString());
                                    }
                                }
                            });

                            tableview.getColumns().addAll(col);
                            System.out.println("Column ["+i+"] ");
                        }
                        while(rs.next()){
                            ObservableList<String> row = FXCollections.observableArrayList();
                            for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                                if (i > 1) result = result + ",  ";
                                String columnValue = rs.getString(i);
                                result = result + columnValue;
                                row.add(rs.getString(i));
                            }
                            result = result + System.lineSeparator();
                            System.out.println("Row [1] added "+row );
                            data.add(row);
                        }
                        System.out.println(result);
                        tableview.setItems(data);
                        primaryStage.setScene(scene3);
                    } catch (Exception e) {
                        showAlertWithHeaderText("Error",e.getMessage());
                        System.out.println(e);
                    }
                }
            }
        });
        btnSvFile.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Save");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text File", "*.txt*"));
                File file = fileChooser.showSaveDialog(primaryStage);
                if(file == null){
                    showAlertWithHeaderText("No file selected","please select a file");
                }else{
                    boolean res;
                    try {
                        res = file.createNewFile();
                        if (res)
                        {
                            System.out.println("file created " + file.getCanonicalPath()); //returns the path string
                        } else {
                            System.out.println("File already exist at location: " + file.getCanonicalPath());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        FileWriter myWriter = new FileWriter(file);
                        myWriter.write(result);
                        myWriter.close();
                        System.out.println("Successfully wrote to the file.");
                    } catch (IOException e) {
                        System.out.println("An error occurred.");
                        e.printStackTrace();
                    }
                    System.out.println(file.getAbsolutePath());
                }
            }
        });
        btnSvFtp.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Dialog dialog = new Dialog<>();
                dialog.setTitle("Upload to ftp server");

                // Set the button types.
                ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

                GridPane gridPane = new GridPane();
                gridPane.setHgap(10);
                gridPane.setVgap(10);
                gridPane.setPadding(new Insets(20, 150, 10, 10));

                TextField server = new TextField();
                server.setPromptText("Server");
                TextField username = new TextField();
                username.setPromptText("Username");
                TextField file = new TextField();
                file.setPromptText("Remote file name");
                TextField port = new TextField();
                port.setPromptText("Port");
                PasswordField pass = new PasswordField();
                pass.setPromptText("Password");
                gridPane.add(new Label("Server"),0,0);
                gridPane.add(server, 1, 0);
                gridPane.add(new Label("Username"), 0, 1);
                gridPane.add(username, 1, 1);
                gridPane.add(new Label("Password"),0,2);
                gridPane.add(pass,1,2);
                gridPane.add(new Label("Port"),0,3);
                gridPane.add(port,1,3);
                gridPane.add(new Label("Remote File"),0,4);
                gridPane.add(file,1,4);


                dialog.getDialogPane().setContent(gridPane);

                // Request focus on the username field by default.
                Platform.runLater(() -> server.requestFocus());

                // Convert the result to a username-password-pair when the login button is clicked.
                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loginButtonType) {
                        srvr = server.getText().toString();
                        prt = port.getText().toString();
                        usrnm = username.getText().toString();
                        psswrd = pass.getText().toString();
                        rmtfile = file.getText().toString();
                        FTPClient ftpClient = new FTPClient();
                        boolean res;
                        try {
                            ftpClient.connect(srvr, Integer.parseInt(prt));
                            ftpClient.login(usrnm, psswrd);
                            ftpClient.enterLocalPassiveMode();
                            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
                            String path = System.getProperty("user.dir");
                            File firstLocalFile = new File(path +"/src/Files/result.txt");
                            res = firstLocalFile.createNewFile();
                            if (res)
                            {
                                System.out.println("file created " + firstLocalFile.getCanonicalPath()); //returns the path string
                            } else {
                                System.out.println("File already exist at location: " + firstLocalFile.getCanonicalPath());
                            }
                            FileWriter myWriter = new FileWriter(path + "/src/Files/result_aymen.txt");
                            myWriter.write(result);
                            myWriter.close();
                            System.out.println("Successfully wrote to the file.");

                            String firstRemoteFile = rmtfile;
                            InputStream inputStream = new FileInputStream(firstLocalFile);

                            System.out.println("Starting uploading the file");
                            boolean done = ftpClient.storeFile(firstRemoteFile, inputStream);
                            inputStream.close();
                            if (done) {
                                System.out.println("The file is uploaded successfully.");
                            }

                        } catch (SocketException e) {
                            throw new RuntimeException(e);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    return null;
                });
                dialog.showAndWait();
            }
        });
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                result = "";
                tableview.getColumns().clear();
                data.clear();
                primaryStage.setScene(scene2);
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