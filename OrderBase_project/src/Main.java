import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;


public class Main extends Application {

    private static File file = new File("");
    private static String path = file.getAbsolutePath();
    private MainInterface mainInterface;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage)  {

        accountSelection(primaryStage);

    }

    private void accountSelection(Stage primaryStage){

        if(DataBaseHelper.testConnection()){
            mainInterface = new MainInterface("work");
        } else {
            MainInterface.getAlertInformationDialog("Рабочая база данных недоступна.\nБудет подключена локальная база данных");
            mainInterface = new MainInterface("home");
        }
        Stage accountSelectionStage = new Stage();
        accountSelectionStage.setTitle("Выбор учетной записи");
        accountSelectionStage.initModality(Modality.WINDOW_MODAL);
        accountSelectionStage.initOwner(primaryStage);

        BorderPane accSelectBorderPane = new BorderPane();
        Scene accountSelectionScene = new Scene(accSelectBorderPane, 440,300);
        accountSelectionStage.setScene(accountSelectionScene);

        HBox hBoxBottomButtons = new HBox();
        hBoxBottomButtons.setSpacing(15);
        hBoxBottomButtons.setPadding(new Insets(15));
        hBoxBottomButtons.setAlignment(Pos.CENTER);
        Button buttonOk = new Button("Ок");
        buttonOk.setPrefSize(100, 50);
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(100,50);
        buttonCancel.setOnAction(event -> {
            primaryStage.close();
            accountSelectionStage.close();
        });
        Button buttonCreateAccount = new Button("Создать учётную запись");
        buttonCreateAccount.setPrefSize(150,50);
        buttonCreateAccount.setOnAction(event -> createAccount(accountSelectionStage));

        hBoxBottomButtons.getChildren().addAll(buttonOk, buttonCancel, buttonCreateAccount);

        GridPane enterAccGridPane = new GridPane();
        enterAccGridPane.setHgap(10);
        enterAccGridPane.setVgap(10);
        TextField textFieldLogin = new TextField();
        textFieldLogin.setFocusTraversable(false);
        PasswordField passwordField = new PasswordField();
        passwordField.setFocusTraversable(false);
        TextField textFieldPass = new TextField();
        textFieldPass.setFocusTraversable(false);
        Button buttonHidePass = new Button("Скрыть");
        buttonHidePass.setPrefWidth(80);
        Button buttonShowPass = new Button("Показать");
        buttonShowPass.setPrefWidth(80);
        buttonShowPass.setOnAction(event -> {
            enterAccGridPane.getChildren().remove(passwordField);
            enterAccGridPane.getChildren().remove(buttonShowPass);
            enterAccGridPane.add(textFieldPass, 1,1);
            enterAccGridPane.add(buttonHidePass, 2,1);
            textFieldPass.setText(passwordField.getText());
            passwordField.clear();

        });

        buttonHidePass.setOnAction(event -> {
            enterAccGridPane.add(passwordField, 1,1);
            enterAccGridPane.add(buttonShowPass, 2,1);
            passwordField.setText(textFieldPass.getText());
            enterAccGridPane.getChildren().remove(textFieldPass);
            enterAccGridPane.getChildren().remove(buttonHidePass);
            textFieldPass.clear();
        });

        Label mainLogoLabel = new Label();

        File file = new File(DataBaseHelper.path + "\\src\\images\\mainLogo.jpg");

        if(file.isFile()) {
            mainLogoLabel.setGraphic(MainInterface.getMainLogo());
        }

        mainLogoLabel.setPadding(new Insets(15));

        enterAccGridPane.add(new Text("Логин: "),0,1);
        enterAccGridPane.add(new Text("Пароль: "),0,2);
        enterAccGridPane.add(textFieldLogin, 1,1);
        enterAccGridPane.add(passwordField, 1,2);
        enterAccGridPane.add(buttonShowPass, 2,2);
        enterAccGridPane.alignmentProperty().set(Pos.CENTER);

        buttonOk.setOnAction(event -> {
            if(passwordField.getText().equals("")) passwordField.setText(textFieldPass.getText());
            if(checkAccount(textFieldLogin.getText(), passwordField.getText())){

                accountSelectionStage.close();
                //MainInterface mainInterface = new MainInterface(DataBaseHelper.getAccount(textFieldLogin.getText()));
                MainInterface.setAccount(DataBaseHelper.getAccount(textFieldLogin.getText()));
                mainInterface.go();
            }
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(enterAccGridPane);
        accSelectBorderPane.setTop(mainLogoLabel);
        accSelectBorderPane.setCenter(stackPane);
        accSelectBorderPane.setBottom(hBoxBottomButtons);
        BorderPane.setAlignment(stackPane, Pos.CENTER);
        BorderPane.setAlignment(mainLogoLabel, Pos.TOP_CENTER);
        accountSelectionStage.show();
    }

    private void createAccount(Stage primaryStage){
        Stage stageCreateAccount = new Stage();
        stageCreateAccount.setTitle("Создание учетной записи");
        stageCreateAccount.initModality(Modality.WINDOW_MODAL);
        stageCreateAccount.initOwner(primaryStage);
        BorderPane borderPaneCreateAcc = new BorderPane();
        Scene sceneCreateAcc = new Scene(borderPaneCreateAcc, 300,200);
        stageCreateAccount.setScene(sceneCreateAcc);

        HBox hBoxBottonButtons = new HBox();
        hBoxBottonButtons.setSpacing(10);
        Button buttonCreate = new Button("Создать");
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setOnAction(event -> stageCreateAccount.close());
        hBoxBottonButtons.getChildren().addAll(buttonCreate, buttonCancel);

        GridPane gridPaneCreateAcc = new GridPane();
        gridPaneCreateAcc.alignmentProperty().set(Pos.CENTER);
        gridPaneCreateAcc.setVgap(5);
        gridPaneCreateAcc.setHgap(5);
        TextField textFieldLogin = new TextField();
        TextField textFieldUserName = new TextField();
        TextField textFieldPass = new TextField();
        TextField textFieldPassAgain = new TextField();
        Text textLogin = new Text("Введите логин: ");
        Text textUserName = new Text("Введите Ваше имя: ");
        Text textPass = new Text("Введите пароль: ");
        Text textPassAgain = new Text("Повторите пароль: ") ;
        gridPaneCreateAcc.add(textLogin, 0,0);
        gridPaneCreateAcc.add(textUserName, 0,1);
        gridPaneCreateAcc.add(textPass, 0,2);
        gridPaneCreateAcc.add(textPassAgain,0,3);
        gridPaneCreateAcc.add(textFieldLogin, 1,0);
        gridPaneCreateAcc.add(textFieldUserName, 1,1);
        gridPaneCreateAcc.add(textFieldPass, 1,2);
        gridPaneCreateAcc.add(textFieldPassAgain, 1,3);
        GridPane.setHalignment(textLogin, HPos.RIGHT);
        GridPane.setHalignment(textUserName, HPos.RIGHT);
        GridPane.setHalignment(textPass, HPos.RIGHT);
        GridPane.setHalignment(textPassAgain, HPos.RIGHT);


        buttonCreate.setOnAction(event -> {
            if (textFieldPass.getText().equals(textFieldPassAgain.getText())){
                boolean coincidence = false;
                for (Account account : DataBaseHelper.getAccountsList()){
                    if(textFieldLogin.getText().equals(account.getLogin())) coincidence = true;
                }
                if (coincidence){
                    MainInterface.getAlertWarningDialog("Такой логин уже существует");
                } else {
                    DataBaseHelper.addAccountToDB(new Account(
                            textFieldLogin.getText(),
                            textFieldUserName.getText(),
                            textFieldPass.getText()
                    ));
                    stageCreateAccount.close();
                    MainInterface.getAlertInformationDialog("Аккаунт успешно создан");
                }
            } else {
                MainInterface.getAlertErrorDialog("Введеные пароли не совпадают");
            }
        });

        borderPaneCreateAcc.setCenter(gridPaneCreateAcc);
        borderPaneCreateAcc.setBottom(hBoxBottonButtons);
        stageCreateAccount.show();
    }

    private boolean checkAccount(String login, String password){
        boolean pass = false;
        boolean checkLogin = false;
        boolean checkPassword = false;

        if(DataBaseHelper.getAccountsList().isEmpty()){
            MainInterface.getAlertWarningDialog("База аккаунтов пуста. Создайте аккаунт");
        }

        for(Account account: DataBaseHelper.getAccountsList()){
            if(account.getLogin().equals(login)){
                checkLogin = true;
                if(account.getPassword().equals(password)){
                    checkPassword = true;
                    break;
                }
            }
        }

        if(checkLogin){
            if(checkPassword){
                pass = true;
            } else MainInterface.getAlertErrorDialog("Неверный пароль");
        } else MainInterface.getAlertWarningDialog("Такого логина не существует");

        return pass;
    }


}
