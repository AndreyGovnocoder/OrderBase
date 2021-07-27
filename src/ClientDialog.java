import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

class ClientDialog
{
    private Client _client;
    private boolean _editClient = false;
    boolean _ok = false;

    ClientDialog(Client client)
    {
        this._editClient = true;
        this._client = client;
    }

    ClientDialog(){}

    void showAndWait(Stage parentStage)
    {
        Stage clientDialogStage = new Stage();
        BorderPane clientDialogBorderPane = new BorderPane();
        Scene clientDialogScene = new Scene(clientDialogBorderPane, 400,210);
        GridPane clientDialogGrigPane = new GridPane();
        TextField nameTextField = new TextField();
        TextField phoneTextField = new TextField();
        TextField mailTextField = new TextField();
        TextField contactPersonTextField = new TextField();
        Label nameLabel = new Label("Клиент: ");
        Label phoneLabel = new Label("Телефон: ");
        Label mailLabel = new Label ("E-mail: ");
        Label contactPersonLabel = new Label("Контактное лицо: ");

        AnchorPane buttonsPane = new AnchorPane();

        Button addClientButton = new Button("Добавить");
        Button saveChangesButton = new Button("Сохранить изменения");
        Button closeButton = new Button("Закрыть");

        addClientButton.setOnAction(event ->
        {
            _client = new Client();
            _client.set_name(nameTextField.getText());
            _client.set_phone(phoneTextField.getText());
            _client.set_mail(mailTextField.getText());
            _client.set_contactPerson(contactPersonTextField.getText());
            _ok = true;
            clientDialogStage.close();
        });

        saveChangesButton.setOnAction(event ->
        {
            _client.set_name(nameTextField.getText());
            _client.set_phone(phoneTextField.getText());
            _client.set_mail(mailTextField.getText());
            _client.set_contactPerson(contactPersonTextField.getText());
            _ok = true;
            clientDialogStage.close();
        });

        closeButton.setOnAction(event -> clientDialogStage.close());

        if(_editClient)
        {
            nameTextField.setText(_client.get_name());
            phoneTextField.setText(_client.get_phone());
            mailTextField.setText(_client.get_mail());
            contactPersonTextField.setText(_client.get_contactPerson());
            buttonsPane.getChildren().addAll(saveChangesButton, closeButton);
        }
        else
        {
            buttonsPane.getChildren().addAll(addClientButton, closeButton);
        }

        nameTextField.setMinWidth(clientDialogScene.getWidth()/2);
        nameTextField.setPrefWidth(clientDialogScene.getWidth()/2);

        AnchorPane.setTopAnchor(addClientButton, 5.0);
        AnchorPane.setLeftAnchor(addClientButton, 5.0);
        AnchorPane.setBottomAnchor(addClientButton, 5.0);

        AnchorPane.setTopAnchor(saveChangesButton, 5.0);
        AnchorPane.setLeftAnchor(saveChangesButton, 5.0);
        AnchorPane.setBottomAnchor(saveChangesButton, 5.0);

        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        clientDialogStage.initModality(Modality.WINDOW_MODAL);
        clientDialogStage.initOwner(parentStage);

        clientDialogGrigPane.add(nameLabel, 0,0);
        clientDialogGrigPane.add(phoneLabel, 0,1);
        clientDialogGrigPane.add(mailLabel, 0,2);
        clientDialogGrigPane.add(contactPersonLabel, 0, 3);
        clientDialogGrigPane.add(nameTextField, 1,0);
        clientDialogGrigPane.add(phoneTextField, 1,1);
        clientDialogGrigPane.add(mailTextField,1,2);
        clientDialogGrigPane.add(contactPersonTextField, 1,3);
        clientDialogGrigPane.setVgap(10);
        clientDialogGrigPane.setHgap(15);
        clientDialogGrigPane.setPadding(new Insets(15));
        clientDialogGrigPane.setPrefWidth(clientDialogScene.getWidth());
        clientDialogGrigPane.alignmentProperty().set(Pos.CENTER);

        clientDialogBorderPane.setCenter(clientDialogGrigPane);
        clientDialogBorderPane.setBottom(buttonsPane);
        BorderPane.setAlignment(clientDialogGrigPane, Pos.CENTER);

        if(_editClient) clientDialogStage.setTitle("Редактирование данных клиента");
        else clientDialogStage.setTitle("Создание нового клиента");
        clientDialogStage.getIcons().add(MainInterface.getIconLogo());
        clientDialogStage.setMinHeight(180);
        clientDialogStage.setMinWidth(300);
        clientDialogStage.setScene(clientDialogScene);
        clientDialogStage.showAndWait();

    }

    Client get_client(){return _client;}
}
