import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

class AccountDialog {
    private Stage _accDialogStage;

    void show(Stage primaryStage)
    {
        _accDialogStage = new Stage();
        BorderPane accDialogBorderPane = new BorderPane();
        Scene _accDialogScene = new Scene(accDialogBorderPane, 360, 230);
        AnchorPane bottonButtonsAnchorPane = new AnchorPane();
        Button createButton = new Button("Создать");
        Button cancelButton = new Button("Отмена");
        GridPane accDialogGridPane = new GridPane();
        TextField loginTextField = new TextField();
        TextField userNameTextField = new TextField();
        TextField jobPositionTextField = new TextField();
        TextField passwordTextField = new TextField();
        TextField passwordAgainTextField = new TextField();
        Text loginText = new Text("Введите логин: ");
        Text userNameText = new Text("Введите Ваше имя: ");
        Text userPositionText = new Text("Введите Вашу должность: ");
        Text passwordText = new Text("Введите пароль: ");
        Text passwordAgainText = new Text("Повторите пароль: ") ;

        accDialogGridPane.alignmentProperty().set(Pos.CENTER);
        accDialogGridPane.setVgap(5);
        accDialogGridPane.setHgap(5);
        accDialogGridPane.add(loginText, 0,0);
        accDialogGridPane.add(userNameText, 0,1);
        accDialogGridPane.add(userPositionText, 0,2);
        accDialogGridPane.add(passwordText, 0,3);
        accDialogGridPane.add(passwordAgainText,0,4);
        accDialogGridPane.add(loginTextField, 1,0);
        accDialogGridPane.add(userNameTextField, 1,1);
        accDialogGridPane.add(jobPositionTextField, 1,2);
        accDialogGridPane.add(passwordTextField, 1,3);
        accDialogGridPane.add(passwordAgainTextField, 1,4);
        GridPane.setHalignment(loginText, HPos.RIGHT);
        GridPane.setHalignment(userNameText, HPos.RIGHT);
        GridPane.setHalignment(passwordText, HPos.RIGHT);
        GridPane.setHalignment(passwordAgainText, HPos.RIGHT);

        cancelButton.setOnAction(event -> _accDialogStage.close());

        createButton.setOnAction(event ->
        {
            if (passwordTextField.getText().equals(passwordAgainTextField.getText()))
            {
                boolean coincidence = false;
                for (Account account : DataBase.getAccountsList())
                {
                    if(loginTextField.getText().equals(account.get_login())) coincidence = true;
                }
                if (coincidence){
                    MainInterface.getAlertWarningDialog("Такой логин уже существует");
                } else
                {
                    DataBase.addAccount(new Account(
                            loginTextField.getText(),
                            jobPositionTextField.getText(),
                            loginTextField.getText(),
                            passwordTextField.getText()
                    ));
                    _accDialogStage.close();
                    MainInterface.getAlertInformationDialog("Аккаунт успешно создан");
                }
            } else
            {
                MainInterface.getAlertErrorDialog("Введеные пароли не совпадают");
            }
        });

        bottonButtonsAnchorPane.setPadding(new Insets(15));
        bottonButtonsAnchorPane.getChildren().addAll(createButton, cancelButton);
        AnchorPane.setTopAnchor(createButton, 0.0);
        AnchorPane.setLeftAnchor(createButton, 5.0);
        AnchorPane.setBottomAnchor(createButton, 0.0);
        AnchorPane.setTopAnchor(cancelButton, 0.0);
        AnchorPane.setRightAnchor(cancelButton, 5.0);
        AnchorPane.setBottomAnchor(cancelButton, 0.0);


        accDialogBorderPane.setCenter(accDialogGridPane);
        accDialogBorderPane.setBottom(bottonButtonsAnchorPane);
        BorderPane.setAlignment(accDialogGridPane, Pos.CENTER);

        _accDialogStage.setScene(_accDialogScene);
        _accDialogStage.setTitle("Создание аккаунта");
        _accDialogStage.getIcons().add(MainInterface.getIconLogo());
        _accDialogStage.initModality(Modality.WINDOW_MODAL);
        _accDialogStage.initOwner(primaryStage);
        _accDialogStage.showAndWait();
    }
}
