import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class PowerModuleDialog
{
    private Stage _dialogStage;
    private BorderPane _dialogBorderPane;
    private Scene _dialogScene;
    private PowerModule _powerModule;
    private ComboBox<MaterialsValue> _bodiesComboBox;
    private ComboBox<MaterialsValue> _powersComboBox;
    private TextField _nameTextField;
    private TextField _quantityTextField;
    private TextField _priceTextField;
    private boolean _edit = false;
    private boolean _ok = false;
    RadioButton _dollarRBtn = new RadioButton("В долларах");
    RadioButton _rubleRBtn = new RadioButton("В рублях");

    PowerModuleDialog(PowerModule powerModule)
    {
        _powerModule = powerModule;
        _edit = true;
    }

    PowerModuleDialog(){}

    void show(Stage primaryStage)
    {
        _dialogStage = new Stage();
        _dialogBorderPane = new BorderPane();
        _dialogScene = new Scene(_dialogBorderPane);

        _dialogBorderPane.setCenter(getCenter());
        _dialogBorderPane.setBottom(getBottom());

        _dialogStage.initModality(Modality.WINDOW_MODAL);
        _dialogStage.initOwner(primaryStage);
        if(is_edit())
            _dialogStage.setTitle("Редактирование блока питания");
        else
            _dialogStage.setTitle("Добавить блок питания");
        _dialogStage.setScene(_dialogScene);
        _dialogStage.getIcons().add(MainInterface.getIconLogo());
        _dialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        GridPane dialogGridPane = new GridPane();
        ToggleGroup currencyGroup = new ToggleGroup();
        HBox currencyHBox = new HBox();
        Button addBodyBtn = new Button("+");
        Button addPowerBtn = new Button("+");
        final Label nameHeadLabel = new Label("Название:");
        final Label bodyHeadLabel = new Label("Корпус:");
        final Label powerHeadLabel = new Label("Мощность:");
        final Label priceHeadLable = new Label("Цена:");
        final Label quantityHeadLabel = new Label("Количество:");
        final Label currencyHeadLabel = new Label("Валюта:");
        _nameTextField = new TextField();
        _quantityTextField = new TextField();
        _priceTextField = new TextField();
        _bodiesComboBox = new ComboBox<>(FXCollections.observableArrayList(PowerModulesForm._activeBodiesList));
        _powersComboBox = new ComboBox<>(FXCollections.observableArrayList(PowerModulesForm._activePowersList));
        //Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        Pattern pattern = Pattern.compile("\\d*|\\d+,\\d*|\\d+\\.\\d*");

        _nameTextField.setPrefWidth(200);

        _quantityTextField.setPrefWidth(50);
        _quantityTextField.setMaxWidth(50);
        _quantityTextField.setMinWidth(50);
        _quantityTextField.textProperty().addListener(
            MaterialsForm.getChangeListener(_quantityTextField));

        _rubleRBtn.setToggleGroup(currencyGroup);
        _rubleRBtn.setOnAction(event ->
        {
            if (!_priceTextField.getText().isEmpty())
            {
                final double price = Double.parseDouble(_priceTextField.getText().replace(',', '.'));
                _priceTextField.setText(MainInterface.DF.format(MainInterface.toRuble(price)));
            }
        });
        _dollarRBtn.setToggleGroup(currencyGroup);
        _dollarRBtn.setOnAction(event ->
        {
            if (!_priceTextField.getText().isEmpty())
            {
                final double price = Double.parseDouble(_priceTextField.getText().replace(',', '.'));
                _priceTextField.setText(MainInterface.DF.format(MainInterface.toDollar(price)));
            }
        });

        if (!_rubleRBtn.isSelected() && !_dollarRBtn.isSelected())
            _dollarRBtn.setSelected(true);

        _bodiesComboBox.setPrefWidth(200);
        _powersComboBox.setPrefWidth(200);
        _priceTextField.setMaxWidth(200);
        _priceTextField.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null));

        final MaterialsValue currentBody = Finder.getBody(Integer.parseInt(PowerModulesForm.getAccordion().getExpandedPane().getUserData().toString()));
        _bodiesComboBox.setValue(currentBody);

        currencyHBox.getChildren().addAll(_dollarRBtn, _rubleRBtn);
        currencyHBox.setSpacing(10);
        currencyHBox.setAlignment(Pos.CENTER);

        if(is_edit())
            setData();

        addBodyBtn.setOnAction(event ->
        {
            MaterialsValue newPowModuleBody = PowerModulesForm.addPowModuleBody();
            if (newPowModuleBody != null)
            {
                _bodiesComboBox.getItems().add(newPowModuleBody);
                _bodiesComboBox.setValue(newPowModuleBody);
            }
        });

        addPowerBtn.setOnAction(event ->
        {
            MaterialsValue newPowModulePower = PowerModulesForm.addPowModulePower();
            if (newPowModulePower != null)
            {
                _powersComboBox.getItems().add(newPowModulePower);
                _powersComboBox.setValue(newPowModulePower);
            }
        });

        dialogGridPane.setHgap(10);
        dialogGridPane.setVgap(10);
        dialogGridPane.add(nameHeadLabel, 0,0);
        dialogGridPane.add(bodyHeadLabel, 0,1);
        dialogGridPane.add(powerHeadLabel, 0,2);
        dialogGridPane.add(priceHeadLable, 0,4);
        dialogGridPane.add(currencyHeadLabel, 0,5);
        dialogGridPane.add(_nameTextField, 1,0);
        dialogGridPane.add(_bodiesComboBox, 1,1);
        dialogGridPane.add(addBodyBtn, 2,1);
        dialogGridPane.add(_powersComboBox, 1,2);
        dialogGridPane.add(addPowerBtn, 2,2);
        dialogGridPane.add(_priceTextField, 1,4);
        dialogGridPane.add(currencyHBox, 1,5);

        GridPane.setHalignment(nameHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(bodyHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(powerHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(priceHeadLable, HPos.RIGHT);
        GridPane.setHalignment(currencyHeadLabel, HPos.RIGHT);

        if (!is_edit())
        {
            dialogGridPane.add(quantityHeadLabel, 0, 3);
            dialogGridPane.add(_quantityTextField, 1, 3);
            GridPane.setHalignment(quantityHeadLabel, HPos.RIGHT);
        }

        centerVBox.setPadding(new Insets(15));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.getChildren().addAll(dialogGridPane);

        return centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button addBtn = new Button("Добавить");
        Button saveBtn = new Button("Сохранить");
        Button cancelBtn = new Button("Отмена");

        addBtn.setOnAction(event ->
        {
            _powerModule = new PowerModule();
            if(check())
            {
                set_powerModule();
                if(DataBaseStorehouse.addPowerModule(_powerModule))
                {
                    _powerModule.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POWERMODULES_TABLE));
                    _powerModule.set_active(true);
                    _ok = true;
                    _dialogStage.close();
                }
            }
        });

        saveBtn.setOnAction(event ->
        {
            if(check())
            {
                set_powerModule();
                if(DataBaseStorehouse.editPowerModule(_powerModule))
                {
                    _ok = true;
                    _dialogStage.close();
                }
            }
        });

        cancelBtn.setOnAction(event -> _dialogStage.close());

        if(is_edit())
        {
            bottomAnchorPane.getChildren().addAll(saveBtn, cancelBtn);
            AnchorPane.setTopAnchor(saveBtn, 5.0);
            AnchorPane.setLeftAnchor(saveBtn, 5.0);
            AnchorPane.setBottomAnchor(saveBtn, 5.0);
        }
        else
        {
            bottomAnchorPane.getChildren().addAll(addBtn, cancelBtn);
            AnchorPane.setTopAnchor(addBtn, 5.0);
            AnchorPane.setLeftAnchor(addBtn, 5.0);
            AnchorPane.setBottomAnchor(addBtn, 5.0);
        }

        AnchorPane.setTopAnchor(cancelBtn, 5.0);
        AnchorPane.setRightAnchor(cancelBtn, 5.0);
        AnchorPane.setBottomAnchor(cancelBtn, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);
        return bottomVBox;
    }

    private void setData()
    {
        _nameTextField.setText(_powerModule.get_name());

        if (Finder.getBody(_powerModule.get_body()) != null)
            _bodiesComboBox.setValue(Finder.getBody(_powerModule.get_body()));

        if (Finder.getPower(_powerModule.get_power()) != null)
            _powersComboBox.setValue(Finder.getPower(_powerModule.get_power()));
        if (_dollarRBtn.isSelected())
            _priceTextField.setText(MainInterface.DF.format(_powerModule.get_price()));
        else if (_rubleRBtn.isSelected())
            _priceTextField.setText(MainInterface.DF.format(MainInterface.toRuble(_powerModule.get_price())));
    }

    private boolean check()
    {
        if(_nameTextField.getText().isEmpty())
        {
            MainInterface.getAlertWarningDialog("Не указано название");
            return false;
        }
        else if(_bodiesComboBox.getSelectionModel().getSelectedItem() == null)
        {
            MainInterface.getAlertWarningDialog("Не указан корпус");
            return false;
        }
        else if (_powersComboBox.getSelectionModel().getSelectedItem() == null)
        {
            MainInterface.getAlertWarningDialog("Не указана мощность");
            return false;
        }

        return true;
    }

    private void set_powerModule()
    {
        _powerModule.set_name(_nameTextField.getText());
        _powerModule.set_body(_bodiesComboBox.getSelectionModel().getSelectedItem().get_id());
        _powerModule.set_power(_powersComboBox.getSelectionModel().getSelectedItem().get_id());
        if (_priceTextField.getText().isEmpty())
            _powerModule.set_price(0);
        else
        {
            if (_dollarRBtn.isSelected())
                _powerModule.set_price(Double.parseDouble(_priceTextField.getText().replace(',', '.')));
            else
            {
                final double rub = Double.parseDouble(_priceTextField.getText().replace(',', '.'));
                _powerModule.set_price(MainInterface.toDollar(rub));
            }
        }
        if (!is_edit())
        {
            if (_quantityTextField.getText().isEmpty())
                _powerModule.set_quantity(0);
            else
                _powerModule.set_quantity(Integer.parseInt(_quantityTextField.getText()));
        }
    }

    public PowerModule get_powerModule()
    {
        return _powerModule;
    }

    public boolean is_ok()
    {
        return _ok;
    }

    public boolean is_edit()
    {
        return _edit;
    }
}
