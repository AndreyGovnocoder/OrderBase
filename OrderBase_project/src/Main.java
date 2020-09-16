import javafx.application.Application;
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
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Application
{
    private int currAccount = -1;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        createFiles();
        accountSelection(primaryStage);
    }

    private void accountSelection(Stage primaryStage)
    {
        BorderPane accSelectBorderPane = new BorderPane();
        Scene accountSelectionScene = new Scene(accSelectBorderPane, 440,300);
        HBox bottomButtonsHBox = new HBox();
        Button okButton = new Button("Ок");
        Button cancelButton = new Button("Отмена");
        Button createAccountButton = new Button("Создать учётную запись");
        GridPane enterAccGridPane = new GridPane();
        TextField loginTextField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField passTextField = new TextField();
        Button hidePassButton = new Button("Скрыть");
        Button showPassButton = new Button("Показать");
        Label mainLogoLabel = new Label();
        File file = new File(DataBase.path + "\\src\\images\\mainLogo.jpg");
        StackPane stackPane = new StackPane();

        if(file.isFile()) {
            mainLogoLabel.setGraphic(MainInterface.getMainLogo());
        }

        mainLogoLabel.setPadding(new Insets(15));

        okButton.setPrefWidth(80);
        okButton.setOnAction(event ->
        {
            if(passwordField.getText().equals("")) passwordField.setText(passTextField.getText());
            if(check(loginTextField.getText(), passwordField.getText()))
            {
                primaryStage.close();
                MainInterface mainInterface = new MainInterface(primaryStage);
                mainInterface.set_currentAccount(currAccount);
                mainInterface.show();
            }
        });

        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(event -> primaryStage.close());

        createAccountButton.setOnAction(event ->
        {
            AccountDialog accountDialog = new AccountDialog();
            accountDialog.show(primaryStage);
        });

        showPassButton.setPrefWidth(80);
        showPassButton.setOnAction(event ->
        {
            enterAccGridPane.requestFocus();
            enterAccGridPane.getChildren().remove(passwordField);
            enterAccGridPane.getChildren().remove(showPassButton);
            enterAccGridPane.add(passTextField, 1,2);
            enterAccGridPane.add(hidePassButton, 2,2);
            passTextField.setText(passwordField.getText());
            passwordField.clear();

        });

        hidePassButton.setPrefWidth(80);
        hidePassButton.setOnAction(event ->
        {
            enterAccGridPane.requestFocus();
            enterAccGridPane.getChildren().remove(passTextField);
            enterAccGridPane.getChildren().remove(hidePassButton);
            enterAccGridPane.add(passwordField, 1,2);
            enterAccGridPane.add(showPassButton, 2,2);
            passwordField.setText(passTextField.getText());
            passTextField.clear();
        });

        enterAccGridPane.setHgap(10);
        enterAccGridPane.setVgap(10);
        enterAccGridPane.add(new Text("Логин: "),0,1);
        enterAccGridPane.add(new Text("Пароль: "),0,2);
        enterAccGridPane.add(loginTextField, 1,1);
        enterAccGridPane.add(passwordField, 1,2);
        enterAccGridPane.add(showPassButton, 2,2);
        enterAccGridPane.alignmentProperty().set(Pos.CENTER);

        bottomButtonsHBox.setSpacing(15);
        bottomButtonsHBox.setPadding(new Insets(15));
        bottomButtonsHBox.setAlignment(Pos.CENTER);
        bottomButtonsHBox.getChildren().addAll(okButton, cancelButton, createAccountButton);

        stackPane.getChildren().addAll(enterAccGridPane);

        accSelectBorderPane.setStyle("-fx-background-color: #f0f8ff");
        accSelectBorderPane.setTop(mainLogoLabel);
        accSelectBorderPane.setCenter(stackPane);
        accSelectBorderPane.setBottom(bottomButtonsHBox);
        BorderPane.setAlignment(stackPane, Pos.CENTER);
        BorderPane.setAlignment(mainLogoLabel, Pos.TOP_CENTER);

        primaryStage.setTitle("Регистрация аккаунта");
        primaryStage.getIcons().add(MainInterface.getIconLogo());
        primaryStage.setScene(accountSelectionScene);
        primaryStage.show();
    }

    private boolean check(String login, String password)
    {
        boolean pass = false;
        boolean checkLogin = false;
        boolean checkPassword = false;
        int idAccount = -1;

        if(DataBase.getAccountsList().isEmpty())
        {
            MainInterface.getAlertWarningDialog("База аккаунтов пуста. Создайте аккаунт");
        }
        else
        {
            for(Account account: DataBase.getAccountsList())
            {
                if(account.get_login().equals(login))
                {
                    checkLogin = true;
                    if(account.get_password().equals(password))
                    {
                        checkPassword = true;
                        idAccount = account.get_id();
                        break;
                    }
                }
            }

            if(checkLogin)
            {
                if(checkPassword)
                {
                    pass = true;
                    currAccount = idAccount;
                } else MainInterface.getAlertErrorDialog("Неверный пароль");
            } else MainInterface.getAlertWarningDialog("Такого логина не существует");
        }
        return pass;
    }

    private void createFiles()
    {
        File dirSrc = new File(DataBase.path + "\\src\\");
        if(!dirSrc.exists())
        {
            dirSrc.mkdir();
            File dirImages = new File(DataBase.path + "\\src\\images\\");
            dirImages.mkdir();
        }

        File fileLogo = new File(DataBase.path + "\\src\\images\\OrderBaseLogo.png");
        if(!fileLogo.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(1));
                ImageIO.write(bufferedImage, "PNG",fileLogo);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileLogo = new File(DataBase.path + "\\src\\images\\mainLogo.jpg");
        if(!fileLogo.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(2));
                ImageIO.write(bufferedImage, "JPEG",fileLogo);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileLogo = new File(DataBase.path + "\\src\\images\\logoPrnt.jpg");
        if(!fileLogo.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(3));
                ImageIO.write(bufferedImage, "JPEG",fileLogo);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
