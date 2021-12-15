import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class LedsDialog
{
    private Led _led;
    private int _kind;
    private Stage _dialogStage;
    private Scene _dialogScene;
    private BorderPane _dialogBorderPane;
    private boolean _edit = false;
    private boolean _ok = false;
    private static ComboBox<MaterialsValue> _ledKindComboBox = new ComboBox<>(FXCollections.observableArrayList(LedsForm._activeLedKindsList));;
    private ComboBox<MaterialsValue> _ledKindComboBox2;
    private TextField _nameTextField;
    private TextField _luminousFluxTextField;
    private TextField _powerTextField;
    private TextField _quantityTextField;
    private TextField _ledsColorTextField;
    private TextField _priceTextField;
    RadioButton _dollarRBtn = new RadioButton("В долларах");
    RadioButton _rubleRBtn = new RadioButton("В рублях");

    LedsDialog(Led led)
    {
        this._led = led;
        _edit = true;
    }

    LedsDialog(int kind)
    {
        _kind = kind;
    }

    void show(Stage primaryStage)
    {
        _dialogStage = new Stage();
        _dialogBorderPane = new BorderPane();
        _dialogScene = new Scene(_dialogBorderPane);

        _dialogBorderPane.setCenter(getCenter());
        _dialogBorderPane.setBottom(getBottom());

        if(_edit)
            _dialogStage.setTitle("Редактирование светодиода");
        else
            _dialogStage.setTitle("Добавить светодиод");
        _dialogStage.initModality(Modality.WINDOW_MODAL);
        _dialogStage.initOwner(primaryStage);
        _dialogStage.getIcons().addAll(MainInterface.getIconLogo());
        _dialogStage.setScene(_dialogScene);
        _dialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        GridPane centerGridPane = new GridPane();
        ToggleGroup currencyGroup = new ToggleGroup();
        HBox currencyHBox = new HBox();
        Button addLedKindButton = new Button("+");
        final Label nameHeadLabel = new Label("Название: ");
        final Label kindHeadLabel = new Label("Вид: ");
        final Label luminousFluxHeadLabel = new Label("Сила светового потока (лм): ");
        final Label powerHeadLabel = new Label("Мощность (Вт): ");
        final Label colorHeadLabel = new Label("Цвет: ");
        final Label priceHeadLabel = new Label("Цена: ");
        final Label quantityHeadLabel = new Label("Количество: ");
        final Label currencyHeadLabel = new Label("Валюта: ");

        _nameTextField = new TextField();
        _luminousFluxTextField = new TextField();
        _powerTextField = new TextField();
        _quantityTextField = new TextField();
        _ledsColorTextField = new TextField();
        _priceTextField = new TextField();

        _ledKindComboBox.setPrefWidth(300);
        _ledKindComboBox.setValue(Finder.getLedKind(_kind));
        _ledKindComboBox2.setPrefWidth(300);
        _ledKindComboBox2.setValue(Finder.getLedKind(_kind));

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

        if (!_dollarRBtn.isSelected() && !_rubleRBtn.isSelected())
            _dollarRBtn.setSelected(true);

        //Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        Pattern pattern = Pattern.compile("\\d*|\\d+,\\d*|\\d+\\.\\d*");

        _nameTextField.setPrefWidth(300);
        _powerTextField.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null));
        _powerTextField.setMaxWidth(100);
        _luminousFluxTextField.textProperty().addListener(MaterialsForm.getChangeListener(_luminousFluxTextField));
        _luminousFluxTextField.setMaxWidth(100);
        _quantityTextField.textProperty().addListener(MaterialsForm.getChangeListener(_quantityTextField));
        _quantityTextField.setMaxWidth(100);
        _ledsColorTextField.setMaxWidth(100);
        _priceTextField.setTextFormatter(new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
                pattern.matcher(change.getControlNewText()).matches() ? change : null));
        _priceTextField.setMaxWidth(100);

        currencyHBox.getChildren().addAll(_dollarRBtn, _rubleRBtn);
        currencyHBox.setSpacing(10);
        currencyHBox.setAlignment(Pos.CENTER);

        if(_edit)
            setData();

        centerGridPane.add(nameHeadLabel, 0,0);
        centerGridPane.add(kindHeadLabel, 0,1);
        centerGridPane.add(luminousFluxHeadLabel, 0,2);
        centerGridPane.add(powerHeadLabel, 0,3);
        centerGridPane.add(colorHeadLabel, 0,4);
        centerGridPane.add(priceHeadLabel, 0,6);
        centerGridPane.add(currencyHeadLabel, 0, 7);
        centerGridPane.add(_nameTextField, 1,0);
        //centerGridPane.add(_ledKindComboBox, 1,1);
        centerGridPane.add(_ledKindComboBox2, 1,1);
        centerGridPane.add(addLedKindButton, 2,1);
        centerGridPane.add(_luminousFluxTextField, 1,2);
        centerGridPane.add(_powerTextField, 1,3);
        centerGridPane.add(_ledsColorTextField, 1,4);
        centerGridPane.add(_priceTextField, 1,6);
        centerGridPane.add(currencyHBox, 1,7);
        if(!_edit)
        {
            centerGridPane.add(quantityHeadLabel, 0, 5);
            centerGridPane.add(_quantityTextField, 1, 5);
            GridPane.setHalignment(quantityHeadLabel, HPos.RIGHT);
        }
        centerGridPane.setVgap(5);
        centerGridPane.setHgap(5);
        GridPane.setHalignment(nameHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(kindHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(luminousFluxHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(powerHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(colorHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(priceHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(currencyHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(_powerTextField, HPos.CENTER);
        GridPane.setHalignment(_luminousFluxTextField, HPos.CENTER);
        GridPane.setHalignment(_quantityTextField, HPos.CENTER);
        GridPane.setHalignment(_ledsColorTextField, HPos.CENTER);
        GridPane.setHalignment(_priceTextField, HPos.CENTER);
        GridPane.setHalignment(currencyHBox, HPos.CENTER);

        addLedKindButton.setOnAction(event ->
        {
            MaterialsValue newKind = LedsForm.addLedKind();
            if (newKind != null)
            {
                _ledKindComboBox.getItems().add(newKind);
                _ledKindComboBox.setValue(newKind);
                _ledKindComboBox2.getItems().add(newKind);
                _ledKindComboBox2.setValue(newKind);
            }
        });

        centerVBox.setSpacing(10);
        centerVBox.setPadding(new Insets(15));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.getChildren().addAll(centerGridPane);
        return centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button cancelBtn = new Button("Отмена");
        Button addBtn = new Button("Добавить");
        Button saveBtn = new Button("Сохранить");

        addBtn.setOnAction(event ->
        {
            if(_nameTextField.getText().isEmpty())
                MainInterface.getAlertWarningDialog("Введите название");
            else if (_ledKindComboBox2.getValue() == null)
                MainInterface.getAlertWarningDialog("Выберите вид");
            else
            {
                _led = new Led();
                set_led();
                if(DataBaseStorehouse.addLed(_led))
                {
                    _led.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.LEDS_TABLE));
                    _led.set_active(true);
                    _ok = true;
                    _dialogStage.close();
                }
            }
        });

        saveBtn.setOnAction(event ->
        {
            if(_nameTextField.getText().isEmpty())
                MainInterface.getAlertWarningDialog("Введите название");
            else if (_ledKindComboBox2.getValue() == null)
                MainInterface.getAlertWarningDialog("Выберите вид");
            else
            {
                set_led();
                if(DataBaseStorehouse.editLed(_led))
                {
                    _ok = true;
                    _dialogStage.close();
                }
            }
        });

        cancelBtn.setOnAction(event -> _dialogStage.close());

        if(_edit)
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

    void setData()
    {
        if (_led.get_name() != null)
            _nameTextField.setText(_led.get_name());
        if (_led.get_kind() != -1)
            _ledKindComboBox2.setValue(Finder.getLedKind(_led.get_kind()));
        if (_led.get_luminousFlux() != 0)
            _luminousFluxTextField.setText(String.valueOf(_led.get_luminousFlux()));
        if (_led.get_color() != null)
            _ledsColorTextField.setText(_led.get_color());
        if (_led.get_power() != 0)
            _powerTextField.setText(String.valueOf(_led.get_power()));
        _quantityTextField.setText(String.valueOf(_led.get_quantity()));
        if (_dollarRBtn.isSelected())
            _priceTextField.setText(MainInterface.DF.format(_led.get_price()));
        else if (_rubleRBtn.isSelected())
            _priceTextField.setText(MainInterface.DF.format(MainInterface.toRuble(_led.get_price())));
    }

    void set_ledKindComboBox2(ArrayList<MaterialsValue> kindsList)
    {
        _ledKindComboBox2 = new ComboBox<>(FXCollections.observableArrayList(kindsList));
    }
    public boolean is_ok() { return _ok; }

    public Led get_led() { return _led; }

    private void set_led()
    {
        _led.set_name(_nameTextField.getText());
        _led.set_kind(_ledKindComboBox2.getValue().get_id());
        if(_luminousFluxTextField.getText().isEmpty())
            _led.set_luminousFlux(0);
        else
            _led.set_luminousFlux(Integer.parseInt(_luminousFluxTextField.getText()));
        if(_powerTextField.getText().isEmpty())
            _led.set_power(0);
        else
            _led.set_power(Float.parseFloat(_powerTextField.getText().replace(',', '.')));
        if(_ledsColorTextField.getText().isEmpty())
            _led.set_color("");
        else
            _led.set_color(_ledsColorTextField.getText());
        if (_priceTextField.getText().isEmpty())
            _led.set_price(0);
        else
        {
            if (_dollarRBtn.isSelected())
                _led.set_price(Double.parseDouble(_priceTextField.getText().replace(',', '.')));
            else if (_rubleRBtn.isSelected())
            {
                final double rub = Double.parseDouble(_priceTextField.getText().replace(',', '.'));
                _led.set_price(MainInterface.toDollar(rub));
            }
        }
        if(!_edit)
        {
            if(_quantityTextField.getText().isEmpty())
                _led.set_quantity(0);
            else
                _led.set_quantity(Integer.parseInt(_quantityTextField.getText()));
        }
    }
}
