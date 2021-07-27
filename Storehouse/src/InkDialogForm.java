import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class InkDialogForm
{
    private Stage _inkDialogStage;
    private BorderPane _mainBorderPane;
    private Scene _inkDialogScene;
    private ComboBox<Machine> _machinesComboBox;
    private ComboBox<String> _colorsComboBox;
    private TextField _inkNameTextField;
    private TextField _inkVolumeTextField;
    private TextField _inkQuantityTextField;
    private ObservableList<String> _colors;
    private boolean _edit = false;
    private Ink _ink;
    boolean _ok = false;
    private Machine _currMachine;

    InkDialogForm(){}

    InkDialogForm(Ink ink)
    {
        this._ink = ink;
        this._edit = true;
    }

    void showAndWait(Stage primaryStage)
    {

        _inkDialogStage = new Stage();
        _mainBorderPane = new BorderPane();
        _inkDialogScene = new Scene(_mainBorderPane);
        _colors = FXCollections.observableArrayList("Cyan", "Magenta", "Yellow", "Black");

        _mainBorderPane.setBottom(getBottom());
        _mainBorderPane.setCenter(getCenter());
        //BorderPane.setAlignment(getCenter(), Pos.CENTER);

        if(_edit)
            _inkDialogStage.setTitle("Редактирование чернил");
        else
            _inkDialogStage.setTitle("Добавляем чернила");
        _inkDialogStage.initModality(Modality.WINDOW_MODAL);
        _inkDialogStage.initOwner(primaryStage);
        _inkDialogStage.setScene(_inkDialogScene);
        _inkDialogStage.getIcons().add(MainInterface.getIconLogo());
        _inkDialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        GridPane gridPane = new GridPane();
        Label nameLabel = new Label("Название чернил: ");
        Label machineLabel = new Label("Принтер: ");
        Label colorLabel = new Label("Цвет: ");
        Label volumeLabel = new Label("Объем ёмкости (л): ");
        Label quantityLabel = new Label("Количество: ");
        _machinesComboBox = new ComboBox<>(FXCollections.observableArrayList(InksForm._activeMachinesArrayList));
        _colorsComboBox = new ComboBox<>(_colors);
        _inkNameTextField = new TextField();
        _inkVolumeTextField = new TextField();
        _inkQuantityTextField = new TextField();
        int textFieldWidth = 150;

        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
        {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        _machinesComboBox.setPrefWidth(textFieldWidth);
        if(_currMachine != null)
            _machinesComboBox.setValue(_currMachine);
        _colorsComboBox.setPrefWidth(textFieldWidth);
        _inkNameTextField.setPrefWidth(textFieldWidth);
        _inkVolumeTextField.setPrefWidth(textFieldWidth);
        _inkVolumeTextField.setTextFormatter(formatter);
        _inkQuantityTextField.setPrefWidth(textFieldWidth);
        _inkQuantityTextField.textProperty().addListener(
                MaterialsForm.getChangeListener(_inkQuantityTextField));

        if(_edit)
        {
            _machinesComboBox.setValue(Finder.getMachine(_ink.get_machine()));
            _colorsComboBox.setValue(_ink.get_color());
            _inkNameTextField.setText(_ink.get_name());
            _inkVolumeTextField.setText(String.valueOf(_ink.get_volume()));
        }

        gridPane.add(nameLabel, 0,0);
        gridPane.add(_inkNameTextField, 1,0);
        gridPane.add(machineLabel, 0,1);
        gridPane.add(_machinesComboBox, 1,1);
        gridPane.add(colorLabel, 0,2);
        gridPane.add(_colorsComboBox, 1,2);
        gridPane.add(volumeLabel, 0,3);
        gridPane.add(_inkVolumeTextField, 1,3);
        if(!_edit)
        {
            gridPane.add(quantityLabel, 0,4);
            gridPane.add(_inkQuantityTextField, 1,4);
            GridPane.setHalignment(quantityLabel, HPos.RIGHT);
        }
        GridPane.setHalignment(nameLabel, HPos.RIGHT);
        GridPane.setHalignment(machineLabel, HPos.RIGHT);
        GridPane.setHalignment(colorLabel, HPos.RIGHT);
        GridPane.setHalignment(volumeLabel, HPos.RIGHT);
        gridPane.setHgap(5);
        gridPane.setVgap(10);

        centerVBox.setPadding(new Insets(20));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.getChildren().add(gridPane);

        return centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button addButton = new Button("Добавить");
        Button saveButton = new Button("Сохранить изменения");
        Button cancelButton = new Button("Отмена");

        addButton.setPrefWidth(80);
        addButton.setOnAction(event ->
        {
            _ink = new Ink();
            if(check())
            {
                _ok = true;
                _inkDialogStage.close();
            }
        });

        saveButton.setOnAction(event ->
        {
            if(check())
            {
                _ok = true;
                _inkDialogStage.close();
            }
        });

        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(event -> _inkDialogStage.close());

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

    private boolean check()
    {
        if(_inkNameTextField.getText().equals(""))
        {
            MainInterface.getAlertWarningDialog("Не указано название чернил");
            return false;
        }
        else if(_colorsComboBox.getSelectionModel().getSelectedItem() == null)
        {
            MainInterface.getAlertWarningDialog("Не указан цвет чернил");
            return false;
        } else if(_inkVolumeTextField.getText().equals(""))
        {
            MainInterface.getAlertWarningDialog("Не указан объём");
            return false;
        } else if(!_edit && _inkQuantityTextField.getText().equals(""))
        {
            MainInterface.getAlertWarningDialog("Не указано количество");
            return false;
        }
        else
        {
            _ink.set_name(_inkNameTextField.getText());
            _ink.set_color(_colorsComboBox.getSelectionModel().getSelectedItem());
            if(_machinesComboBox.getSelectionModel().getSelectedItem() != null)
                _ink.set_machine(_machinesComboBox.getSelectionModel().getSelectedItem().get_id());
            _ink.set_volume(Float.parseFloat(_inkVolumeTextField.getText()));
            if(!_edit)
                _ink.set_quantity(Integer.parseInt(_inkQuantityTextField.getText()));
            return true;
        }
    }

    public void set_ink(Ink ink)
    {
        this._ink = ink;
    }

    public Ink get_ink()
    {
        return _ink;
    }

    public void set_currMachine(Machine currMachine)
    {
        this._currMachine = currMachine;
    }

    public Machine get_currMachine()
    {
        return _currMachine;
    }

    public boolean is_ok()
    {
        return _ok;
    }
}
