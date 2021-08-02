import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class EditMaterialDialog
{
    private Material _material;
    private Stage _editMaterialDialogStage;
    private Scene _editMaterialDialogScene;
    private BorderPane _editMaterialDialogBorderPane;
    private boolean _ok = false;
    private Button _addManufacturerButton;
    private Button _addColorButton;
    private Button _addPropertyButton;
    private Button _addAttributeButton;
    private ComboBox<MaterialsValue> _manufacturerComboBox;
    private ComboBox<MaterialsValue> _colorComboBox;
    private ComboBox<MaterialsValue> _propertyComboBox;
    private ComboBox<MaterialsValue> _attributeComboBox;
    private TextField _widthTextField;
    private TextField _heightTextField;
    private TextField _thiknessTextField;
    private TextField _priceTextField;
    private TextField _sellPriceTextField;

    EditMaterialDialog(Material material)
    {
        this._material = material;
    }

    void showAndWait(Stage primaryStage)
    {
        _editMaterialDialogStage = new Stage();
        _editMaterialDialogBorderPane = new BorderPane();
        //_editMaterialDialogScene = new Scene(_editMaterialDialogBorderPane, 310, 350);
        _editMaterialDialogScene = new Scene(_editMaterialDialogBorderPane);
        Label headKindLabel = new Label("Материал: " + Finder.getMaterialKind(_material.get_kind()));
        HBox headHBox = new HBox();

        headKindLabel.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));

        headHBox.setAlignment(Pos.BOTTOM_CENTER);
        headHBox.setPadding(new Insets(15));
        headHBox.setStyle("-fx-background-color: #f0f8ff");
        headHBox.getChildren().add(headKindLabel);

        _editMaterialDialogBorderPane.setTop(headHBox);
        _editMaterialDialogBorderPane.setCenter(getCenter());
        _editMaterialDialogBorderPane.setBottom(getBottom());
        BorderPane.setAlignment(headHBox, Pos.CENTER);

        _editMaterialDialogStage.initModality(Modality.WINDOW_MODAL);
        _editMaterialDialogStage.initOwner(primaryStage);
        _editMaterialDialogStage.setTitle("Редактирование материала");
        _editMaterialDialogStage.getIcons().add(MainInterface.getIconLogo());
        _editMaterialDialogStage.setScene(_editMaterialDialogScene);
        _editMaterialDialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        GridPane centerGridPane = new GridPane();
        Label headManufacturerLabel = new Label("Производитель");
        Label headWidthLabel = new Label("Ширина (мм)");
        Label headHeightLabel = new Label("Высота/метраж (мм)");
        Label headColorLabel = new Label("Цвет");
        Label headPropertyLabel = new Label("Свойство");
        Label headThicknessLabel = new Label("Толщина");
        Label headAttributeLabel = new Label("Атрибут");
        Label headPriceLabel = new Label("Закуп. цена");
        Label headSellPriceLabel = new Label("Цена продажи");
        _widthTextField = new TextField();
        _heightTextField = new TextField();
        _thiknessTextField = new TextField();
        _priceTextField = new TextField(String.valueOf(0));
        _sellPriceTextField = new TextField(String.valueOf(0));
        MaterialsKind kind = Finder.getMaterialKind(_material.get_kind());

        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
        {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        _widthTextField.textProperty().addListener(MaterialsForm.getChangeListener(_widthTextField));
        _heightTextField.textProperty().addListener(MaterialsForm.getChangeListener(_heightTextField));
        _thiknessTextField.setTextFormatter(formatter);
        _priceTextField.textProperty().addListener(MaterialsForm.getChangeListener(_priceTextField));
        _sellPriceTextField.textProperty().addListener(MaterialsForm.getChangeListener(_sellPriceTextField));

        initializationComboBox();
        initializationAddButtons();

        if(kind.get_manufacturer())
        {
            centerGridPane.add(headManufacturerLabel, 0,1);
            centerGridPane.add(_manufacturerComboBox, 1,1);
            _manufacturerComboBox.setValue(
                    Finder.getManufacturer(_material.get_manufacturer()));
            centerGridPane.add(_addManufacturerButton, 2,1);
        }

        if(kind.get_width())
        {
            centerGridPane.add(headWidthLabel, 0,2);
            centerGridPane.add(_widthTextField, 1,2);
            _widthTextField.setText(String.valueOf(_material.get_width()));
        }

        if(kind.get_height())
        {
            centerGridPane.add(headHeightLabel, 0,3);
            centerGridPane.add(_heightTextField,1,3);
            _heightTextField.setText(String.valueOf(_material.get_height()));
        }

        if(kind.get_color())
        {
            centerGridPane.add(headColorLabel, 0,4);
            centerGridPane.add(_colorComboBox, 1,4);
            _colorComboBox.setValue(
                    Finder.getColor(_material.get_color()));
            centerGridPane.add(_addColorButton, 2,4);
        }

        if(kind.get_property())
        {
            centerGridPane.add(headPropertyLabel, 0,5);
            centerGridPane.add(_propertyComboBox, 1,5);
            _propertyComboBox.setValue(Finder.getProperty(_material.get_property()));
            centerGridPane.add(_addPropertyButton, 2,5);
        }

        if(kind.get_thickness())
        {
            centerGridPane.add(headThicknessLabel, 0,6);
            centerGridPane.add(_thiknessTextField, 1,6);
            _thiknessTextField.setText(String.valueOf(_material.get_thickness()));
        }

        if(kind.get_attribute())
        {
            centerGridPane.add(headAttributeLabel, 0,7);
            centerGridPane.add(_attributeComboBox, 1,7);
            _attributeComboBox.setValue(Finder.getAttribute(_material.get_attribute()));
            centerGridPane.add(_addAttributeButton, 2,7);
        }

        centerGridPane.add(headPriceLabel, 0, 8);
        centerGridPane.add(_priceTextField, 1,8);
        centerGridPane.add(headSellPriceLabel, 0, 9);
        centerGridPane.add(_sellPriceTextField, 1, 9);
        _priceTextField.setText(String.valueOf(_material.get_price()));
        _sellPriceTextField.setText(String.valueOf(_material.get_sellPrice()));

        centerGridPane.setVgap(5);
        centerGridPane.setHgap(5);
        centerGridPane.setAlignment(Pos.CENTER);

        centerVBox.setPadding(new Insets(15));
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().add(centerGridPane);

        return centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button saveButton = new Button("Сохранить");
        Button cancelButton = new Button("Отмена");
        MaterialsKind kind = Finder.getMaterialKind(_material.get_kind());

        saveButton.setPrefWidth(80);
        saveButton.setOnAction(event ->
        {
            if (kind.get_manufacturer() && _manufacturerComboBox.getSelectionModel().getSelectedItem() != null)
                _material.set_manufacturer(_manufacturerComboBox.getSelectionModel().getSelectedItem().get_id());

            if(kind.get_width() && !_widthTextField.getText().isEmpty())
                _material.set_width(Integer.parseInt(_widthTextField.getText()));

            if(kind.get_height() && !_heightTextField.getText().isEmpty())
                _material.set_height(Integer.parseInt(_heightTextField.getText()));

            if(kind.get_color() && _colorComboBox.getSelectionModel().getSelectedItem() != null)
                _material.set_color(_colorComboBox.getSelectionModel().getSelectedItem().get_id());

            if(kind.get_property() && _propertyComboBox.getSelectionModel().getSelectedItem() != null)
                _material.set_property(_propertyComboBox.getSelectionModel().getSelectedItem().get_id());

            if(kind.get_thickness() && !_thiknessTextField.getText().isEmpty())
                _material.set_thickness(Float.parseFloat(_thiknessTextField.getText()));

            if(kind.get_attribute() && _attributeComboBox.getSelectionModel().getSelectedItem() != null)
                _material.set_attribute(_attributeComboBox.getSelectionModel().getSelectedItem().get_id());

            if(_priceTextField.getText().isEmpty())
                _material.set_price(0);
            else
                _material.set_price(Integer.parseInt(_priceTextField.getText()));

            if (_sellPriceTextField.getText().isEmpty())
                _material.set_sellPrice(0);
            else
                _material.set_sellPrice(Integer.parseInt(_sellPriceTextField.getText()));

            _ok = true;
            _editMaterialDialogStage.close();
        });

        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(event -> _editMaterialDialogStage.close());

        bottomAnchorPane.getChildren().addAll(saveButton, cancelButton);
        AnchorPane.setTopAnchor(saveButton, 5.0);
        AnchorPane.setLeftAnchor(saveButton,5.0);
        AnchorPane.setBottomAnchor(saveButton,5.0);
        AnchorPane.setTopAnchor(cancelButton,5.0);
        AnchorPane.setRightAnchor(cancelButton,5.0);
        AnchorPane.setBottomAnchor(cancelButton,5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);
        return bottomVBox;
    }

    public void initializationComboBox()
    {
        int width = 150;

        _manufacturerComboBox = new ComboBox<>(
                FXCollections.observableArrayList(MaterialsForm._activeManufacturers));
        _colorComboBox = new ComboBox<>(FXCollections.observableArrayList(MaterialsForm._activeColors));
        _propertyComboBox = new ComboBox<>(FXCollections.observableArrayList(MaterialsForm._activeProperties));
        _attributeComboBox = new ComboBox<>(FXCollections.observableArrayList(MaterialsForm._actitveAttributes));

        _manufacturerComboBox.setPrefWidth(width);
        _colorComboBox.setPrefWidth(width);
        _propertyComboBox.setPrefWidth(width);
        _attributeComboBox.setPrefWidth(width);
    }

    private void addMaterialsValue(final String title, final String header,  final String context, final String TABLE)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(context);
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && !result.get().isEmpty())
        {

            MaterialsValue value = new MaterialsValue();
            int valueId = -1;
            /*
            if (MaterialsForm.checkNameOfNewMaterialsValue(result.get(), ))
            value.set_name(result.get());
            if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
            {
                value.set_id(DataBaseStorehouse.getLastId(TABLE));
            }
             */

            switch (TABLE)
            {
                case "manufacturers":
                    valueId =MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allManufacturers());
                    if (valueId != -1 && Finder.getManufacturer(valueId) != null)
                    {
                        value = Finder.getManufacturer(valueId);
                        final int indexInArrayAll = Finder.get_allManufacturers().indexOf(value);
                        value.set_active(true);
                        if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                        {
                            MaterialsForm._activeManufacturers.add(value);
                            Finder.get_allManufacturers().set(indexInArrayAll, value);
                        }
                    } else
                    {
                        value.set_name(result.get());
                        if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                        {
                            value.set_id(DataBaseStorehouse.getLastId(TABLE));
                            MaterialsForm._activeManufacturers.add(value);
                            Finder.get_allManufacturers().add(value);
                        }
                    }
                    _manufacturerComboBox.getItems().add(value);
                    _manufacturerComboBox.setValue(value);
                    break;

                case "colors":
                    valueId = MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allColors());
                    if (valueId != -1 && Finder.getColor(valueId) != null)
                    {
                        value = Finder.getColor(valueId);
                        final int indexInArrayAll = Finder.get_allColors().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                MaterialsForm._activeColors.add(value);
                                Finder.get_allColors().set(indexInArrayAll, value);
                            }
                        } else
                        {
                            value = new MaterialsValue();
                            value.set_name(result.get());
                            if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                            {
                                value.set_id(DataBaseStorehouse.getLastId(TABLE));
                                MaterialsForm._activeColors.add(value);
                                Finder.get_allColors().add(value);
                            }
                        }
                    }
                    _colorComboBox.getItems().add(value);
                    _colorComboBox.setValue(value);
                    break;

                case "properties":
                    valueId = MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allProperties());
                    if (valueId != -1 && Finder.getProperty(valueId) != null)
                    {
                        value = Finder.getProperty(valueId);
                        final int indexInArrayAll = Finder.get_allProperties().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                MaterialsForm._activeProperties.add(value);
                                Finder.get_allProperties().set(indexInArrayAll, value);
                            }
                        }
                    } else
                    {
                        value = new MaterialsValue();
                        value.set_name(result.get());
                        if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                        {
                            value.set_id(DataBaseStorehouse.getLastId(TABLE));
                            MaterialsForm._activeProperties.add(value);
                            Finder.get_allProperties().add(value);
                        }
                    }
                    _propertyComboBox.getItems().add(value);
                    _propertyComboBox.setValue(value);
                    break;

                case "attributes":
                    valueId = MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allAttributes());
                    if (valueId != -1 && Finder.getAttribute(valueId) != null)
                    {
                        value = Finder.getAttribute(valueId);
                        final int indexInArrayAll = Finder.get_allAttributes().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                MaterialsForm._actitveAttributes.add(value);
                                Finder.get_allAttributes().set(indexInArrayAll, value);
                            }
                        }
                    } else
                    {
                        value = new MaterialsValue();
                        value.set_name(result.get());
                        if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                        {
                            value.set_id(DataBaseStorehouse.getLastId(TABLE));
                            MaterialsForm._actitveAttributes.add(value);
                            Finder.get_allAttributes().add(value);
                        }
                    }
                    _attributeComboBox.getItems().add(value);
                    _attributeComboBox.setValue(value);
                    break;
            }
        }
    }

    void initializationAddButtons()
    {
        _addManufacturerButton = new Button("+");
        _addColorButton = new Button("+");
        _addPropertyButton = new Button("+");
        _addAttributeButton = new Button("+");

        _addManufacturerButton.setOnAction(event ->
        {
            addMaterialsValue(
                    "Новый производитель",
                    "Введите название производителя",
                    "Производитель: ",
                    DataBaseStorehouse.MANUFACTURERS_TABLE
            );
        });

        _addColorButton.setOnAction(event ->
        {
            addMaterialsValue(
                    "Новый цвет",
                    "Введите название цвета",
                    "Цвет: ",
                    DataBaseStorehouse.COLORS_TABLE
            );
        });

        _addPropertyButton.setOnAction(event ->
        {
            addMaterialsValue(
                    "Новое свойство",
                    "Введите новое свойство",
                    "Свойство: ",
                    DataBaseStorehouse.PROPERTIES_TABLE
            );
        });

        _addAttributeButton.setOnAction(event ->
        {
            addMaterialsValue(
                    "Новый атрибут",
                    "Введите новый атрибут",
                    "Aтрибут: ",
                    DataBaseStorehouse.ATTRIBUTES_TABLE
            );
        });
    }

    public boolean is_ok()
    {
        return _ok;
    }

    public void set_material(Material material)
    {
        this._material = material;
    }

    public Material get_material()
    {
        return _material;
    }
}
