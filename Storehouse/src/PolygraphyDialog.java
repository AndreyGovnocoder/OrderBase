import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PolygraphyDialog
{
    private Stage _polygraphyDialogStage;
    boolean _ok = false;
    boolean _edit = false;
    private Polygraphy _polygraphy;
    private TextField _nameTextField = new TextField();
    private TextField _priceTextField = new TextField();
    private TextField _quantityTextField = new TextField();
    private TextArea _descriptionTextArea = new TextArea();

    PolygraphyDialog(){}

    PolygraphyDialog(Polygraphy polygraphy)
    {
        this._polygraphy = polygraphy;
        _edit = true;
    }

    void showAndWait(Stage primaryStage)
    {
        _polygraphyDialogStage = new Stage();
        BorderPane _mainBorderPane = new BorderPane();
        Scene polygraphyDialogScene = new Scene(_mainBorderPane);

        _mainBorderPane.setBottom(getBottom());
        _mainBorderPane.setCenter(getCenter());
        setTextFields();

        if(_edit)
            _polygraphyDialogStage.setTitle("Редактирование полиграфии");
        else
            _polygraphyDialogStage.setTitle("Добавляем полиграфию");
        _polygraphyDialogStage.initModality(Modality.WINDOW_MODAL);
        _polygraphyDialogStage.initOwner(primaryStage);
        _polygraphyDialogStage.setScene(polygraphyDialogScene);
        _polygraphyDialogStage.getIcons().add(MainInterface.getIconLogo());
        _polygraphyDialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        TitledPane nameTitledPane = new TitledPane();
        TitledPane priceTitledPane = new TitledPane();
        TitledPane quantityTitledPane = new TitledPane();
        TitledPane descriptionTitledPane = new TitledPane();

        nameTitledPane.setText("Наименование");
        nameTitledPane.setExpanded(true);
        nameTitledPane.setCollapsible(false);
        nameTitledPane.setContent(_nameTextField);

        priceTitledPane.setText("Цена");
        priceTitledPane.setExpanded(true);
        priceTitledPane.setCollapsible(false);
        priceTitledPane.setContent(_priceTextField);

        quantityTitledPane.setText("Количество");
        quantityTitledPane.setExpanded(true);
        quantityTitledPane.setCollapsible(false);
        quantityTitledPane.setContent(_quantityTextField);

        descriptionTitledPane.setText("Описание");
        descriptionTitledPane.setExpanded(true);
        descriptionTitledPane.setCollapsible(false);
        descriptionTitledPane.setContent(_descriptionTextArea);

        centerVBox.setPadding(new Insets(20));
        centerVBox.setSpacing(10);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.setAlignment(Pos.CENTER);

        if(_edit)
        {
            centerVBox.getChildren().addAll(
                    nameTitledPane,
                    priceTitledPane,
                    descriptionTitledPane);
        } else
        {
            centerVBox.getChildren().addAll(
                    nameTitledPane,
                    priceTitledPane,
                    quantityTitledPane,
                    descriptionTitledPane);
        }

        return centerVBox;
    }

    private VBox getBottom()
    {
        VBox bottomVBox = new VBox();
        AnchorPane bottomAnchorPane = new AnchorPane();
        Button addButton = new Button("Добавить");
        Button saveButton = new Button("Сохранить изменения");
        Button cancelButton = new Button("Отмена");

        cancelButton.setOnAction(event -> _polygraphyDialogStage.close());

        addButton.setOnAction(event ->
        {
            if(!_edit)
                _polygraphy = new Polygraphy();
            if(check())
            {
                _ok = true;
                _polygraphyDialogStage.close();
            }
        });

        saveButton.setOnAction(event ->
        {
            if (check())
            {
                _ok = true;
                _polygraphyDialogStage.close();
            }
        });

        if(_edit)
        {
            bottomAnchorPane.getChildren().addAll(saveButton, cancelButton);
            AnchorPane.setTopAnchor(saveButton, 5.0);
            AnchorPane.setLeftAnchor(saveButton, 5.0);
            AnchorPane.setBottomAnchor(saveButton, 5.0);
            AnchorPane.setTopAnchor(cancelButton, 5.0);
            AnchorPane.setRightAnchor(cancelButton, 5.0);
            AnchorPane.setBottomAnchor(cancelButton, 5.0);
        } else
        {
            bottomAnchorPane.getChildren().addAll(addButton, cancelButton);
            AnchorPane.setTopAnchor(addButton, 5.0);
            AnchorPane.setLeftAnchor(addButton, 5.0);
            AnchorPane.setBottomAnchor(addButton, 5.0);
            AnchorPane.setTopAnchor(cancelButton, 5.0);
            AnchorPane.setRightAnchor(cancelButton, 5.0);
            AnchorPane.setBottomAnchor(cancelButton, 5.0);
        }

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private void setTextFields()
    {
        if (_edit)
        {
            _nameTextField.setText(_polygraphy.get_name());
            _priceTextField.setText(String.valueOf(_polygraphy.get_price()));
            _quantityTextField.setText(String.valueOf(_polygraphy.get_quantity()));
            if(_polygraphy.get_description() != null)
                _descriptionTextArea.setText(_polygraphy.get_description());
        }

        _priceTextField.textProperty().addListener(MaterialsForm.getChangeListener(_priceTextField));
        _quantityTextField.textProperty().addListener(MaterialsForm.getChangeListener(_quantityTextField));
        _descriptionTextArea.setWrapText(true);

        _nameTextField.requestFocus();
        _nameTextField.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
                _priceTextField.requestFocus();
            else if (event.getCode() == KeyCode.TAB)
            {
                if (event.isShiftDown())
                    _nameTextField.requestFocus();
                else
                    _priceTextField.requestFocus();
            }
        });

        _priceTextField.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
                _quantityTextField.requestFocus();
            else if (event.getCode() == KeyCode.TAB)
            {
                if(event.isShiftDown())
                    _nameTextField.requestFocus();
                else
                    _quantityTextField.requestFocus();
            }
        });

        _quantityTextField.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.ENTER)
                _descriptionTextArea.requestFocus();
            else if (event.getCode() == KeyCode.TAB)
            {
                if(event.isShiftDown())
                    _priceTextField.requestFocus();
                else
                    _descriptionTextArea.requestFocus();
            }
        });
    }

    private boolean check()
    {
        if(_nameTextField.getText().isEmpty())
        {
            MainInterface.getAlertWarningDialog("Не указано название конструкции");
            return false;
        } else
        {
            _polygraphy.set_name(_nameTextField.getText());
            if(_priceTextField.getText().isEmpty())
                _polygraphy.set_price(0);
            else
                _polygraphy.set_price(Integer.parseInt(_priceTextField.getText()));
            if(!_edit)
            {
                if(_quantityTextField.getText().isEmpty())
                {
                    MainInterface.getAlertWarningDialog("Введите количество (или укажите ноль)");
                    return false;
                } else
                    _polygraphy.set_quantity(Integer.parseInt(_quantityTextField.getText()));
            }
            if(!_descriptionTextArea.getText().isEmpty())
                _polygraphy.set_description(_descriptionTextArea.getText());
            return true;
        }
    }

    public boolean isOk() { return _ok; }
    public boolean isEdit() { return _edit; }
    public Polygraphy get_polygraphy()
    {
        return _polygraphy;
    }
}
