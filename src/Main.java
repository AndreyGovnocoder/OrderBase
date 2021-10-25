import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Optional;
import org.ini4j.*;

public class Main extends Application
{
    public static File file = new File("");
    public static String path = file.getAbsolutePath();
    private int currAccount = -1;
    static int primaryVersion = 4;
    static int secondaryVersion = 27;
    private TextField _loginTextField;
    private PasswordField _passwordField;
    private TextField _passTextField;

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)
    {
        if(DataBase.testConnection())
        {
            createFiles();
        /*
        MainInterface mainInterface = new MainInterface(primaryStage);
        mainInterface.set_currentAccount(7);
        mainInterface.show();
        //
        */
            accountSelection(primaryStage);
        }
        else
        {
            MainInterface.getAlertErrorDialog("Отсутствет доступ к базе данных заказов." +
                    "\n Запуск приложения невозможен. Обратитесь к разработчику приложения.");
        }
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

        _loginTextField = new TextField();
        _passwordField = new PasswordField();
        _passTextField = new TextField();

        Button hidePassButton = new Button("Скрыть");
        Button showPassButton = new Button("Показать");
        Label mainLogoLabel = new Label();
        File file = new File(DataBase.path + "\\src\\images\\mainLogo.jpg");
        StackPane stackPane = new StackPane();

        if(file.isFile()) {
            mainLogoLabel.setGraphic(MainInterface.getMainLogo());
        }

        mainLogoLabel.setPadding(new Insets(15));

        _loginTextField.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
            {
                if(_passwordField.isVisible())
                    _passwordField.requestFocus();
                else if(_passTextField.isVisible())
                    _passTextField.requestFocus();
            }
        });

        _passwordField.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
                onOkButton(primaryStage);

        });

        _passTextField.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
                onOkButton(primaryStage);
        });

        okButton.setPrefWidth(80);
        okButton.setOnAction(event ->
        {
            onOkButton(primaryStage);
        });

        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(event -> primaryStage.close());

        createAccountButton.setOnAction(event ->
        {
            AccountDialog accountDialog = new AccountDialog();
            accountDialog.showAndWait(primaryStage);
        });

        showPassButton.setPrefWidth(80);
        showPassButton.setOnAction(event ->
        {
            enterAccGridPane.requestFocus();
            enterAccGridPane.getChildren().remove(_passwordField);
            enterAccGridPane.getChildren().remove(showPassButton);
            _passTextField.setVisible(true);
            enterAccGridPane.add(_passTextField, 1,2);
            enterAccGridPane.add(hidePassButton, 2,2);
            _passTextField.setText(_passwordField.getText());
            _passwordField.clear();
            _passwordField.setVisible(false);

        });

        hidePassButton.setPrefWidth(80);
        hidePassButton.setOnAction(event ->
        {
            enterAccGridPane.requestFocus();
            enterAccGridPane.getChildren().remove(_passTextField);
            enterAccGridPane.getChildren().remove(hidePassButton);
            _passwordField.setVisible(true);
            enterAccGridPane.add(_passwordField, 1,2);
            enterAccGridPane.add(showPassButton, 2,2);
            _passwordField.setText(_passTextField.getText());
            _passTextField.clear();
            _passTextField.setVisible(false);
        });

        enterAccGridPane.setHgap(10);
        enterAccGridPane.setVgap(10);
        enterAccGridPane.add(new Text("Логин: "),0,1);
        enterAccGridPane.add(new Text("Пароль: "),0,2);
        enterAccGridPane.add(_loginTextField, 1,1);
        enterAccGridPane.add(_passwordField, 1,2);
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

        primaryStage.setTitle("Авторизация аккаунта (ver. " + primaryVersion +"."+secondaryVersion +")");
        primaryStage.getIcons().add(MainInterface.getIconLogo());
        primaryStage.setScene(accountSelectionScene);



        if(primaryVersion >= DataBase.getPrimaryVersion() && secondaryVersion >= DataBase.getSecondaryVersion())
            primaryStage.show();
        else if(primaryVersion < DataBase.getPrimaryVersion() || secondaryVersion < DataBase.getSecondaryVersion())
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("У вас устаревшая версия программы (версия " +
                    primaryVersion +"."+ secondaryVersion +")" + "\n" +
                    "Для корректной работы рекомендуется обновить программу\n" +
                    "(до версии " + DataBase.getPrimaryVersion() + "." + DataBase.getSecondaryVersion() + ")");
            alert.setTitle("Внимание!");
            alert.setContentText("Всё равно продолжить?");

            ButtonType buttonYes = new ButtonType("Да, я все равно хочу продолжить!");
            ButtonType buttonNo = new ButtonType("Нет, я сейчас всё обновлю");

            alert.getButtonTypes().clear();
            alert.getButtonTypes().addAll(buttonYes, buttonNo);
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
            alertStage.getIcons().add(MainInterface.getIconLogo());
            Optional<ButtonType> option = alert.showAndWait();
            if( option.get() == buttonYes) primaryStage.show();
        }
    }

    private void onOkButton(Stage primaryStage)
    {
        if(_passwordField.getText().equals("")) _passwordField.setText(_passTextField.getText());
        if(check(_loginTextField.getText(), _passwordField.getText()))
        {
            primaryStage.close();
            MainInterface mainInterface = new MainInterface(primaryStage);
            mainInterface.set_currentAccount(currAccount);
            mainInterface.show();
        }
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
            dirSrc.mkdir();
        File dirImages = new File(DataBase.path + "\\src\\images\\");
        if(!dirImages.exists())
            dirImages.mkdir();
        File dirIcons = new File(DataBase.path + "\\icons\\");
        if(!dirIcons.exists())
            dirIcons.mkdir();

        File fileImg = new File(DataBase.path + "\\src\\images\\OrderBaseLogo.png");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(1));
                ImageIO.write(bufferedImage, "PNG", fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
/*
        String versLine = primaryVersion + "," + secondaryVersion;
        Writer writer = null;


        try
        {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(DataBase.path + "\\src\\version.txt"), StandardCharsets.UTF_8));
            writer.write(versLine);
        } catch (IOException ex)
        {
            // Report
        } finally
        {
            try
            {
                assert writer != null;
                writer.close();
            } catch (Exception ex)
            {
                //ex
            }
        }
*/

        fileImg = new File(DataBase.path + "\\src\\images\\mainLogo.jpg");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(2));
                ImageIO.write(bufferedImage, "JPEG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileImg = new File(DataBase.path + "\\src\\images\\logoPrnt.jpg");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(3));
                ImageIO.write(bufferedImage, "JPEG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        fileImg = new File(DataBase.path + "\\src\\images\\dr.png");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(13));
                ImageIO.write(bufferedImage, "PNG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileImg = new File(DataBase.path + "\\src\\images\\dr2.jpg");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(14));
                ImageIO.write(bufferedImage, "JPG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }


        fileImg = new File(DataBase.path + "\\icons\\error.png");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(10));
                ImageIO.write(bufferedImage, "PNG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileImg = new File(DataBase.path + "\\icons\\error.png");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(10));
                ImageIO.write(bufferedImage, "PNG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileImg = new File(DataBase.path + "\\icons\\OrderBaseLogo.png");
        if(!fileImg.isFile())
        {
            try
            {
                BufferedImage bufferedImage = ImageIO.read(DataBase.getImage(11));
                ImageIO.write(bufferedImage, "PNG",fileImg);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    static String getPathToDB()
    {
        String pathToDB = "";
        try
        {
            Wini ini = new Wini(new File(path + "\\src\\settings.ini"));
            pathToDB = ini.get("General", "PathToDB", String.class);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return pathToDB + "/";
    }
}
