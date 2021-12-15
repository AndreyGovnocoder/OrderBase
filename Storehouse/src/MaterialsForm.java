import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Properties;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

class MaterialsForm
{
    private Stage _materialsFormStage;
    private Stage _addMaterialStage;
    private Scene _materialsFormScene;
    private BorderPane _mainBorderPane;
    private VBox _centerVBox;
    private Accordion _centerAccordion;
    private GridPane _selectedValueGridPane;
    private final Label _headManufacturerLabel = new Label("Производитель");
    private final Label _headWidthLabel = new Label("Ширина (мм)");
    private final Label _headHeightLabel = new Label("Высота/метраж (мм)");
    private final Label _headColorLabel = new Label("Цвет");
    private final Label _headPropertyLabel = new Label("Свойство");
    private final Label _headThicknessLabel = new Label("Толщина");
    private final Label _headAttributeLabel = new Label("Атрибут");
    private final Label _headColorNumberLabel = new Label("Номер цвета");
    private final Label _headQuantityLabel = new Label("Количество");
    private final Label _headPriceLabel = new Label("Закуп. цена");
    private final Label _headSellPriceLabel = new Label("Цена продажи");
    private Button _addKindButton;
    private Button _addManufacturerButton;
    private Button _addColorButton;
    private Button _addPropertyButton;
    private Button _addAttributeButton;
    static ArrayList<MaterialsKind> _activeKinds;
    static ArrayList<MaterialsValue> _activeManufacturers;
    static ArrayList<MaterialsValue> _activeColors;
    static ArrayList<MaterialsValue> _activeProperties;
    static ArrayList<MaterialsValue> _actitveAttributes;
    static ArrayList<Material> _activeMaterialsList;
    private ComboBox<MaterialsKind> _kindComboBox;
    private ComboBox<MaterialsValue> _manufacturerComboBox;
    private ComboBox<MaterialsValue> _colorComboBox;
    private ComboBox<MaterialsValue> _propertyComboBox;
    private ComboBox<MaterialsValue> _attributeComboBox;
    private TextField _widthTextField;
    private TextField _heightTextField;
    private TextField _quantityTextField;
    private TextField _thiknessTextField;
    private TextField _priceTextField;
    private TextField _sellPriceTextField;
    private TextField _colorNumberTextField;
    public static final String CONSUMPTION = "Расход";
    public static final String INCOMING = "Приход";

    MaterialsForm()
    {
        _activeMaterialsList = new ArrayList<>();
        for (final Material material : Finder.get_allMaterialsList())
            if (material.is_active())
                _activeMaterialsList.add(material);
        _activeKinds = new ArrayList<>();
        for (final MaterialsKind kind : Finder.get_allMaterialsKinds())
            if (kind.is_active())
                _activeKinds.add(kind);
        _activeManufacturers = new ArrayList<>();
        for (final MaterialsValue value : Finder.get_allManufacturers())
            if (value.is_active())
                _activeManufacturers.add(value);
        _activeColors = new ArrayList<>();
        for (final MaterialsValue value : Finder.get_allColors())
            if (value.is_active())
                _activeColors.add(value);
        _activeProperties = new ArrayList<>();
        for (final MaterialsValue value : Finder.get_allProperties())
            if (value.is_active())
                _activeProperties.add(value);
        _actitveAttributes = new ArrayList<>();
        for (final MaterialsValue value : Finder.get_allAttributes())
            if (value.is_active())
                _actitveAttributes.add(value);
        initializationComboBox();
        initializationAddButtons();
    }

    void showAndWait(Stage primaryStage)
    {
        _materialsFormStage = new Stage();
        _mainBorderPane = new BorderPane();
        _materialsFormScene = new Scene(_mainBorderPane, 800, 900);

        setMainBorderPane();

        _materialsFormStage.initModality(Modality.WINDOW_MODAL);
        _materialsFormStage.initOwner(primaryStage);
        _materialsFormStage.setTitle("Склад: материалы");
        _materialsFormStage.getIcons().add(MainInterface.getIconLogo());
        _materialsFormStage.getIcons().add(MainInterface.getIconLogo());
        _materialsFormStage.setScene(_materialsFormScene);
        _materialsFormStage.setOnCloseRequest(event ->
        {
            saveMaterialsStageSize(_materialsFormStage);
            saveColumnsPosition();
            //DataBaseStorehouse.closeConnection();
        });

        loadMaterialsStageSize(_materialsFormStage);
        _materialsFormStage.showAndWait();
    }

    private VBox getTop()
    {
        HBox topHBox = new HBox();
        VBox topVBox = new VBox();
        Button editBtn = new Button("Редактирование данных");
        Button accountingBtn = new Button("Учёт материалов");

        editBtn.setOnAction(event -> editMaterialForm());
        accountingBtn.setOnAction(event ->
        {
            MaterialAccountingForm accountingForm = new MaterialAccountingForm();
            accountingForm.set_showKinds(true);
            accountingForm.show(_materialsFormStage);
        });

        topHBox.setSpacing(10);
        topHBox.setPadding(new Insets(5));
        topHBox.getChildren().addAll(editBtn, accountingBtn);

        topVBox.getChildren().addAll(topHBox, new Separator());

        return topVBox;
    }

    private VBox getCenter()
    {
        _centerVBox = new VBox();
        _centerAccordion = new Accordion();

        _centerVBox.setPrefHeight(_materialsFormScene.getHeight()-20);
        _centerVBox.setPrefWidth(_materialsFormScene.getWidth()-20);
        _centerVBox.setSpacing(5);
        _centerVBox.setPadding(new Insets(15));
        _centerVBox.setStyle("-fx-background-color: #f0f8ff");

        _centerAccordion.setStyle("-fx-background-color: #f0f8ff");
        _centerAccordion.setSnapToPixel(true);

        for(MaterialsKind kind : _activeKinds)
            setMaterialsToCenterVBox(kind);

        _centerVBox.getChildren().addAll(_centerAccordion);
        return _centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button closeBtn = new Button("Закрыть");

        closeBtn.setOnAction(event ->
        {
            saveMaterialsStageSize(_materialsFormStage);
            saveColumnsPosition();
            _materialsFormStage.close();
        });

        bottomAnchorPane.getChildren().add(closeBtn);
        AnchorPane.setTopAnchor(closeBtn,5.0);
        AnchorPane.setRightAnchor(closeBtn,5.0);
        AnchorPane.setBottomAnchor(closeBtn,5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private void setMainBorderPane()
    {
        _mainBorderPane.setTop(getTop());
        _mainBorderPane.setCenter(getCenter());
        _mainBorderPane.setBottom(getBottom());
    }

    private void clearComboBoxValues()
    {
        _kindComboBox.setValue(new MaterialsKind());
        _manufacturerComboBox.setValue(new MaterialsValue());
        _colorComboBox.setValue(new MaterialsValue());
        _propertyComboBox.setValue(new MaterialsValue());
        _attributeComboBox.setValue(new MaterialsValue());
    }

    private void setMaterialsToCenterVBox(MaterialsKind kind)
    {
        TitledPane kindTitledPane = new TitledPane();
        TableView<Material> tableView = new TableView<>();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Редактировать");
        MenuItem addMenuItem = new MenuItem("Добавить");
        MenuItem deleteMenuItem = new MenuItem("Удалить");
        MenuItem addRequestItem = new MenuItem("Создать заявку");
        Menu absenceMenu = new Menu("Наличие");
        MenuItem setAbsenceTrueItem = new MenuItem("В работе");
        MenuItem setAbsenceFalseItem = new MenuItem("Отсутствует");

        //tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setUserData(kind.get_id());

        for(Material material : _activeMaterialsList)
        {
            if (material.get_kind() == kind.get_id())
                tableView.getItems().add(material);
        }

        getColumns(tableView, kind);

        editMenuItem.setOnAction(event ->
        {
            if(tableView.getSelectionModel().getSelectedItem() != null)
            {
                EditMaterialDialog dialog = new EditMaterialDialog(tableView.getSelectionModel().getSelectedItem());
                dialog.showAndWait(_materialsFormStage);
                if (dialog.is_ok())
                {
                    int indexInArray = _activeMaterialsList.indexOf(tableView.getSelectionModel().getSelectedItem());
                    int indexInTableView = tableView.getSelectionModel().getSelectedIndex();
                    if (DataBaseStorehouse.editMaterial(dialog.get_material()))
                    {
                        _activeMaterialsList.set(indexInArray, dialog.get_material());
                        tableView.getItems().set(indexInTableView, dialog.get_material());
                    }
                }
            }
        });

        addMenuItem.setOnAction(event ->
        {
            for(MaterialsKind focusedKind : _activeKinds)
                if(focusedKind.get_name().equals(getFocusedTitledPane(_centerAccordion).getText()))
                    addMaterialForm(focusedKind);
        });

        deleteMenuItem.setOnAction(event ->
        {
            if(tableView.getSelectionModel().getSelectedItem() != null)
            {
                Material material = tableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_allMaterialsList().indexOf(material);
                if (MainInterface.getAlertAskConfirmationDialog("Удалить этот материал безвозвратно?"))
                {
                    if (checkMaterialsInAccountings(material.get_id()))
                    {
                        material.set_active(false);
                        if (DataBaseStorehouse.editMaterial(material))
                        {
                            _activeMaterialsList.remove(material);
                            tableView.getItems().remove(material);
                            Finder.get_allMaterialsList().set(indexInArray, material);
                        }
                    } else
                    {
                        if (DataBaseStorehouse.deleteMaterial(material.get_id()))
                        {
                            _activeMaterialsList.remove(material);
                            Finder.get_allMaterialsList().remove(material);
                            tableView.getItems().remove(material);
                        }
                    }
                }
            }
        });

        addRequestItem.setOnAction(event ->
        {
            Material selectedMaterial = tableView.getSelectionModel().getSelectedItem();
            if (selectedMaterial == null)
                return;
            RequestDialog dialog = new RequestDialog(1, selectedMaterial.get_id());
            dialog.set_kindComboBox();
            dialog.showAndWait(_materialsFormStage);
        });

        setAbsenceTrueItem.setOnAction(event ->
        {
            if (tableView.getSelectionModel().getSelectedItem() != null)
            {
                Material selectedMaterial = tableView.getSelectionModel().getSelectedItem();
                final int indexInTableView = tableView.getItems().indexOf(selectedMaterial);
                final int indexInArrayAll = Finder.get_allMaterialsList().indexOf(selectedMaterial);
                final int indexInArrayActive = _activeMaterialsList.indexOf(selectedMaterial);
                selectedMaterial.set_absence(true);

                if (DataBaseStorehouse.editMaterial(selectedMaterial))
                {
                    tableView.getItems().set(indexInTableView, selectedMaterial);
                    Finder.get_allMaterialsList().set(indexInArrayAll, selectedMaterial);
                    _activeMaterialsList.set(indexInArrayActive, selectedMaterial);
                }
            }
        });

        setAbsenceFalseItem.setOnAction(event ->
        {
            if (tableView.getSelectionModel().getSelectedItem() != null)
            {
                Material selectedMaterial = tableView.getSelectionModel().getSelectedItem();
                final int indexInTableView = tableView.getItems().indexOf(selectedMaterial);
                final int indexInArrayAll = Finder.get_allMaterialsList().indexOf(selectedMaterial);
                final int indexInArrayActive = _activeMaterialsList.indexOf(selectedMaterial);
                if (selectedMaterial.get_quantity() == 0)
                {
                    selectedMaterial.set_absence(false);
                    if (DataBaseStorehouse.editMaterial(selectedMaterial))
                    {
                        tableView.getItems().set(indexInTableView, selectedMaterial);
                        Finder.get_allMaterialsList().set(indexInArrayAll, selectedMaterial);
                        _activeMaterialsList.set(indexInArrayActive, selectedMaterial);
                    }
                } else
                    MainInterface.getAlertInformationDialog("Материал ещё есть на складе!");
            }
        });

        absenceMenu.getItems().addAll(setAbsenceTrueItem, setAbsenceFalseItem);

        tableView.setRowFactory(new Callback<TableView<Material>, TableRow<Material>>()
        {
            @Override
            public TableRow<Material> call(TableView<Material> tableView)
            {
                TableRow<Material> row = new TableRow<Material>()
                {
                    @Override
                    protected void updateItem(Material material, boolean empty)
                    {
                        super.updateItem(material, empty);
                        this.setFocused(true);
                        if(!empty)
                        {
                            if (material != null && !material.is_absence())
                                this.setStyle("-fx-background-color: #FFC0CB");
                            else
                                this.setStyle("");
                        } else
                            this.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;");
                    }
                };

                row.setOnMouseClicked(event ->
                {
                    if(!row.isEmpty())
                    {
                        if(!row.getItem().is_absence())
                            row.setStyle("-fx-background-color: #8B0000; " +
                                    "-fx-text-background-color: #ffffff");
                        else
                            row.setStyle("");
                    }
                });
                return row;
            }
        });
        tableView.setPlaceholder(new Text("Материалы данного вида отсутствуют"));
        tableView.setContextMenu(contextMenu);
        tableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);

        contextMenu.getItems().addAll(
                editMenuItem,
                addMenuItem,
                deleteMenuItem,
                new SeparatorMenuItem(),
                absenceMenu,
                new SeparatorMenuItem(),
                addRequestItem);

        kindTitledPane.setPrefWidth(_materialsFormScene.getWidth()-50);
        kindTitledPane.setText(kind.get_name());
        kindTitledPane.setContent(tableView);
        kindTitledPane.setExpanded(false);

        _centerAccordion.getPanes().add(kindTitledPane);
        //_centerVBox.getChildren().add(kindTitledPane);
        VBox.setVgrow(kindTitledPane,Priority.ALWAYS);
    }

    private TableColumn<Material, Void> getPlusBtnColumn(TableView<Material> tableView)
    {
        TableColumn<Material, Void> colPlusBtn = new TableColumn<>("+");

        Callback<TableColumn<Material, Void>, TableCell<Material, Void>> cellFactory = new Callback<TableColumn<Material, Void>, TableCell<Material, Void>>()
        {
            @Override
            public TableCell<Material, Void> call(final TableColumn<Material, Void> param)
            {
                return new TableCell<Material, Void>()
                {
                    private final Button plusBtn = new Button("+");
                    {
                        plusBtn.setPrefWidth(30);
                        plusBtn.setPrefHeight(25);
                        plusBtn.setMinHeight(25);
                        plusBtn.setMaxHeight(25);
                        plusBtn.setOnAction(event ->
                        {
                            Material material = getTableView().getItems().get(getIndex());
                            int newQuantity = material.get_quantity();
                            int inputQuantity = addMaterialsAmount();
                            if(inputQuantity > 0)
                            {
                                newQuantity += inputQuantity;
                                material.set_quantity(newQuantity);
                                if (!material.is_absence())
                                    material.set_absence(true);
                                getTableView().getItems().set(getIndex(), material);
                                _centerVBox.requestFocus();

                                if(DataBaseStorehouse.changeMaterialQuantity(
                                        getTableView().getItems().get(getIndex()).get_id(),
                                        newQuantity))
                                {
                                    MaterialAccounting accounting = new MaterialAccounting();
                                    accounting.set_accountId(MainInterface.get_currentAccount());
                                    accounting.set_material(material.get_id());
                                    accounting.set_procedure(INCOMING);
                                    accounting.set_quantity(inputQuantity);
                                    accounting.set_dateTime(LocalDateTime.now());

                                    if(DataBaseStorehouse.addMaterialAccounting(accounting))
                                        System.out.println("добавлено");
                                }

                                getTableColumn().getCellData(getTableView().
                                        getItems().
                                        get(getIndex()).
                                        get_quantity());
                            }
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (empty)
                        {
                            setGraphic(null);
                        } else
                        {
                            setGraphic(plusBtn);
                        }
                    }
                };
            }
        };

        colPlusBtn.setCellFactory(cellFactory);
        colPlusBtn.setMaxWidth(32);
        colPlusBtn.setMinWidth(32);
        colPlusBtn.setStyle("-fx-alignment: CENTER;");
        return colPlusBtn;
    }

    private TableColumn<Material, Void> getMinusBtnColumn(TableView<Material> tableView)
    {
        TableColumn<Material, Void> colMinusBtn = new TableColumn<>("-");

        Callback<TableColumn<Material, Void>, TableCell<Material, Void>> cellFactory = new Callback<TableColumn<Material, Void>, TableCell<Material, Void>>()
        {
            @Override
            public TableCell<Material, Void> call(final TableColumn<Material, Void> param)
            {
                return new TableCell<Material, Void>()
                {
                    private final Button minusBtn = new Button("-");
                    {

                        minusBtn.setPrefWidth(25);
                        minusBtn.setPrefHeight(25);
                        minusBtn.setMinHeight(25);
                        minusBtn.setMaxHeight(25);
                        minusBtn.setOnAction(event ->
                        {
                            Material material = getTableView().getItems().get(getIndex());
                            int newQuantity = material.get_quantity();
                            --newQuantity;

                            if(newQuantity >= 0)
                            {
                                material.set_quantity(newQuantity);
                                getTableView().getItems().set(getIndex(), material);

                                if(DataBaseStorehouse.changeMaterialQuantity(
                                        getTableView().getItems().get(getIndex()).get_id(),
                                        newQuantity))
                                {

                                    MaterialAccounting accounting = new MaterialAccounting();
                                    accounting.set_accountId(MainInterface.get_currentAccount());
                                    accounting.set_material(material.get_id());
                                    accounting.set_procedure("Расход");
                                    accounting.set_quantity(1);
                                    accounting.set_dateTime(LocalDateTime.now());

                                    if(DataBaseStorehouse.addMaterialAccounting(accounting))
                                        System.out.println("добавлено");
                                }
                            }

                            if(newQuantity < 0)
                            {
                                MainInterface.getAlertWarningDialog("Данный материал отсутствует на складе!\n" +
                                        "(Там же 0, куда руки тянешь?!)");
                            }

                            _centerVBox.requestFocus();
                            //getTableColumn().getCellData(getTableView().getItems().get(getIndex()).get_quantity());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty)
                    {
                        super.updateItem(item, empty);
                        if (empty)
                        {
                            setGraphic(null);
                        } else
                        {
                            setGraphic(minusBtn);
                        }
                    }
                };
            }
        };

        colMinusBtn.setCellFactory(cellFactory);
        colMinusBtn.setMaxWidth(32);
        colMinusBtn.setMinWidth(32);
        colMinusBtn.setStyle("-fx-alignment: CENTER;");
        return colMinusBtn;
    }

    private void getColumns(TableView<Material> tableView, MaterialsKind kind)
    {
        TableColumn<Material, Integer> manufacturerCol = new TableColumn<>("Производитель");
        manufacturerCol.setCellValueFactory(new PropertyValueFactory<>("_manufacturer"));
        manufacturerCol.setStyle("-fx-alignment: CENTER;");
        manufacturerCol.prefWidthProperty().bind(manufacturerCol.widthProperty());
        manufacturerCol.setCellFactory(tc -> new TableCell<Material, Integer>()
        {
            @Override
            protected void updateItem(Integer idManufacturer, boolean empty)
            {
                super.updateItem(idManufacturer, empty);
                if(empty || getMaterialsValue(idManufacturer, Finder.get_allManufacturers()) == null)
                {
                    setText(null);
                } else
                {
                    setText(getMaterialsValue(idManufacturer, Finder.get_allManufacturers()).get_name());
                }
            }
        });

        TableColumn<Material, Integer> widthCol = new TableColumn<>("Ширина");
        widthCol.setCellValueFactory(new PropertyValueFactory<>("_width"));
        widthCol.setStyle("-fx-alignment: CENTER;");
        widthCol.prefWidthProperty().bind(widthCol.widthProperty());

        TableColumn<Material, Integer> heightCol = new TableColumn<>("Высота/Метраж");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("_height"));
        heightCol.setStyle("-fx-alignment: CENTER;");
        heightCol.prefWidthProperty().bind(heightCol.widthProperty());


        TableColumn<Material, Integer> colorCol = new TableColumn<>("Цвет");
        colorCol.setCellValueFactory(new PropertyValueFactory<>("_color"));
        colorCol.setStyle("-fx-alignment: CENTER;");
        colorCol.prefWidthProperty().bind(colorCol.widthProperty());
        colorCol.setCellFactory(tc -> new TableCell<Material, Integer>()
        {
            @Override
            protected void updateItem(Integer idColor, boolean empty)
            {
                super.updateItem(idColor, empty);
                if(empty || getMaterialsValue(idColor, Finder.get_allColors()) == null)
                    setText(null);
                else
                    setText(getMaterialsValue(idColor, Finder.get_allColors()).get_name());
            }
        });

        TableColumn<Material, Integer> propertyCol = new TableColumn<>("Свойство");
        propertyCol.setCellValueFactory(new PropertyValueFactory<>("_property"));
        propertyCol.setStyle("-fx-alignment: CENTER;");
        propertyCol.prefWidthProperty().bind(propertyCol.widthProperty());
        propertyCol.setCellFactory(tc -> new TableCell<Material, Integer>()
        {
            @Override
            protected void updateItem(Integer idProperty, boolean empty)
            {
                super.updateItem(idProperty, empty);
                if(empty || getMaterialsValue(idProperty, Finder.get_allProperties()) == null)
                    setText(null);
                else
                    setText(getMaterialsValue(idProperty, Finder.get_allProperties()).get_name());
            }
        });

        TableColumn<Material, Float> thicknessCol = new TableColumn<>("Толщина");
        thicknessCol.setCellValueFactory(new PropertyValueFactory<>("_thickness"));
        thicknessCol.setStyle("-fx-alignment: CENTER;");
        thicknessCol.prefWidthProperty().bind(thicknessCol.widthProperty());

        TableColumn<Material, Integer> colorNumberCol = new TableColumn<>("Номер цвета");
        colorNumberCol.setCellValueFactory(new PropertyValueFactory<>("_colorNumber"));
        colorNumberCol.setStyle("-fx-alignment: CENTER;");
        colorNumberCol.prefWidthProperty().bind(colorNumberCol.widthProperty());

        TableColumn<Material, Integer> attributeCol = new TableColumn<>("Атрибут");
        attributeCol.setCellValueFactory(new PropertyValueFactory<>("_attribute"));
        attributeCol.setStyle("-fx-alignment: CENTER;");
        attributeCol.prefWidthProperty().bind(attributeCol.widthProperty());
        attributeCol.setCellFactory(tc -> new TableCell<Material, Integer>()
        {
            @Override
            protected void updateItem(Integer idAttribute, boolean empty)
            {
                super.updateItem(idAttribute, empty);
                if(empty || getMaterialsValue(idAttribute, Finder.get_allAttributes()) == null)
                    setText(null);
                else
                    setText(getMaterialsValue(idAttribute, Finder.get_allAttributes()).get_name());
            }
        });

        TableColumn<Material, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.prefWidthProperty().bind(quantityCol.widthProperty());

        TableColumn<Material, Boolean> absenceCol = new TableColumn<>("Наличие");
        absenceCol.setCellValueFactory(new PropertyValueFactory<>("_absence"));
        absenceCol.setStyle("-fx-alignment: CENTER;");
        absenceCol.prefWidthProperty().bind(attributeCol.widthProperty());
        absenceCol.setCellFactory(tc -> new TableCell<>()
        {
            @Override
            protected void updateItem(Boolean isAbsence, boolean empty)
            {
                super.updateItem(isAbsence, empty);
                if(empty )
                    setText(null);
                else
                {
                    if (isAbsence)
                        setText("в работе");
                    else
                        setText("Отсутствует");
                }
            }
        });

        TableColumn<Material, Integer> priceCol = new TableColumn<>("Закуп. цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("_price"));
        priceCol.setStyle("-fx-alignment: CENTER;");
        priceCol.prefWidthProperty().bind(priceCol.widthProperty());

        TableColumn<Material, Integer> sellPriceCol = new TableColumn<>("Цена продажи");
        sellPriceCol.setCellValueFactory(new PropertyValueFactory<>("_sellPrice"));
        sellPriceCol.setStyle("-fx-alignment: CENTER;");
        sellPriceCol.prefWidthProperty().bind(priceCol.widthProperty());

        for(String columnName : DataBaseStorehouse.getKindColumnsArray(kind.get_id()))
        {
            switch (columnName)
            {
                case "+":
                    tableView.getColumns().add(getPlusBtnColumn(tableView));
                    break;
                case "-":
                    tableView.getColumns().add(getMinusBtnColumn(tableView));
                    break;
                case "Количество":
                    tableView.getColumns().add(quantityCol);
                    break;
                case "Наличие":
                    tableView.getColumns().add(absenceCol);
                    break;
                case "Закуп. цена":
                    tableView.getColumns().add(priceCol);
                    break;
                case "Цена продажи":
                    tableView.getColumns().add(sellPriceCol);
                    break;
                case "Атрибут":
                    if(kind.get_attribute())
                        tableView.getColumns().add(attributeCol);
                    break;
                case "Толщина":
                    if(kind.get_thickness())
                        tableView.getColumns().add(thicknessCol);
                    break;
                case "Свойство":
                    if(kind.get_property())
                        tableView.getColumns().add(propertyCol);
                    break;
                case "Цвет":
                    if(kind.get_color())
                        tableView.getColumns().add(colorCol);
                    break;
                case "Высота/Метраж":
                    if(kind.get_height())
                        tableView.getColumns().add(heightCol);
                    break;
                case "Ширина":
                    if(kind.get_width())
                        tableView.getColumns().add(widthCol);
                    break;
                case "Производитель":
                    if(kind.get_manufacturer())
                        tableView.getColumns().add(manufacturerCol);
                    break;
                case "Номер цвета":
                    if(kind.get_colorNumber())
                        tableView.getColumns().add(colorNumberCol);
            }
        }
    }

    private MaterialsValue getMaterialsValue(int id, ArrayList<MaterialsValue> valuesList)
    {
        for(MaterialsValue value : valuesList)
            if(value.get_id() == id)
                return value;

        return null;
    }

    void initializationAddButtons()
    {
        _addKindButton = new Button("+");
        _addManufacturerButton = new Button("+");
        _addColorButton = new Button("+");
        _addPropertyButton = new Button("+");
        _addAttributeButton = new Button("+");

        _addKindButton.setOnAction(event ->
        {
            MaterialsKindDialog kindDialog = new MaterialsKindDialog();
            kindDialog.showAndWait(_addMaterialStage);
            if(kindDialog.is_ok())
            {
                _activeKinds.add(kindDialog.get_kind());
                Finder.get_allMaterialsKinds().add(kindDialog.get_kind());
                _kindComboBox.getItems().add(kindDialog.get_kind());
                setMaterialsToCenterVBox(kindDialog.get_kind());
            }
        });

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

    public void initializationComboBox()
    {
        _kindComboBox = new ComboBox<>(FXCollections.observableArrayList(_activeKinds));
        _manufacturerComboBox = new ComboBox<>(FXCollections.observableArrayList(_activeManufacturers));
        _colorComboBox = new ComboBox<>(FXCollections.observableArrayList(_activeColors));
        _propertyComboBox = new ComboBox<>(FXCollections.observableArrayList(_activeProperties));
        _attributeComboBox = new ComboBox<>(FXCollections.observableArrayList(_actitveAttributes));
        int width = 150;

        _manufacturerComboBox.setPrefWidth(width);
        _colorComboBox.setPrefWidth(width);
        _propertyComboBox.setPrefWidth(width);
        _attributeComboBox.setPrefWidth(width);
    }

    private void addMaterialForm(MaterialsKind materialsKind)
    {
        _addMaterialStage = new Stage();
        BorderPane addMaterialBorderPane = new BorderPane();
        Scene addMaterialScene = new Scene(addMaterialBorderPane, 400,500);
        VBox centerVBox = new VBox();
        VBox bottomVBox = new VBox();
        AnchorPane buttonsAnchorPane = new AnchorPane();
        _selectedValueGridPane = new GridPane();
        HBox kindHBox = new HBox();
        Button addButton = new Button("Добавить материал");
        Button cancelButton = new Button("Отмена");
        Label headKindLabel = new Label("Материал");

        _widthTextField = new TextField();
        _heightTextField = new TextField();
        _quantityTextField = new TextField();
        _thiknessTextField = new TextField();
        _priceTextField = new TextField(String.valueOf(0));
        _sellPriceTextField = new TextField(String.valueOf(0));
        _colorNumberTextField = new TextField();

        Pattern pattern = Pattern.compile("\\d*|\\d+\\.\\d*");
        TextFormatter formatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->
        {
            return pattern.matcher(change.getControlNewText()).matches() ? change : null;
        });

        _widthTextField.textProperty().addListener(getChangeListener(_widthTextField));
        _heightTextField.textProperty().addListener(getChangeListener(_heightTextField));
        _quantityTextField.textProperty().addListener(getChangeListener(_quantityTextField));
        _thiknessTextField.setTextFormatter(formatter);
        _priceTextField.textProperty().addListener(getChangeListener(_priceTextField));
        _sellPriceTextField.textProperty().addListener(getChangeListener(_sellPriceTextField));
        _colorNumberTextField.textProperty().addListener(getChangeListener(_colorNumberTextField));

        initializationComboBox();

        selectValueKindComboBox(materialsKind);

        _selectedValueGridPane.setHgap(5);
        _selectedValueGridPane.setVgap(5);
        _selectedValueGridPane.setAlignment(Pos.CENTER);

        addButton.setOnAction(event ->
        {
            if(checkToAddMaterial())
            {
                Material material = new Material();
                material.set_kind(_kindComboBox.getSelectionModel().getSelectedItem().get_id());
                if(_manufacturerComboBox.isVisible() &&
                        _manufacturerComboBox.getSelectionModel().getSelectedItem() != null)
                    material.set_manufacturer(_manufacturerComboBox.getSelectionModel().getSelectedItem().get_id());
                if(_widthTextField.isVisible())
                    material.set_width(Integer.parseInt(_widthTextField.getText()));
                if(_heightTextField.isVisible() && !_heightTextField.getText().isEmpty())
                    material.set_height(Integer.parseInt(_heightTextField.getText()));
                if(_colorComboBox.isVisible() &&
                        _colorComboBox.getSelectionModel().getSelectedItem() != null)
                    material.set_color(_colorComboBox.getSelectionModel().getSelectedItem().get_id());
                if(_propertyComboBox.isVisible() &&
                        _propertyComboBox.getSelectionModel().getSelectedItem() != null)
                    material.set_property(_propertyComboBox.getSelectionModel().getSelectedItem().get_id());
                if(_thiknessTextField.isVisible() && !_thiknessTextField.getText().isEmpty())
                    material.set_thickness(Float.parseFloat(_thiknessTextField.getText()));
                if(_attributeComboBox.isVisible() &&
                        _attributeComboBox.getSelectionModel().getSelectedItem() != null)
                    material.set_attribute(_attributeComboBox.getSelectionModel().getSelectedItem().get_id());
                if (_colorNumberTextField.isVisible() && !_colorNumberTextField.getText().isEmpty())
                    material.set_colorNumber(Integer.parseInt(_colorNumberTextField.getText()));
                material.set_quantity(Integer.parseInt(_quantityTextField.getText()));
                if (_priceTextField.getText().isEmpty())
                    material.set_price(0);
                else
                    material.set_price(Integer.parseInt(_priceTextField.getText()));

                if (_sellPriceTextField.getText().isEmpty())
                    material.set_sellPrice(0);
                else
                    material.set_sellPrice(Integer.parseInt(_sellPriceTextField.getText()));

                material.set_active(true);
                material.set_absence(true);
                if(DataBaseStorehouse.addMaterial(material))
                {
                    material.set_id(
                            DataBaseStorehouse.getLastId(DataBaseStorehouse.MATERIALS_TABLE));
                    _activeMaterialsList.add(material);
                    Finder.get_allMaterialsList().add(material);
                    addMaterialToTable(material);

                    if(material.get_quantity() != 0)
                    {
                        MaterialAccounting accounting = new MaterialAccounting();
                        accounting.set_material(material.get_id());
                        accounting.set_accountId(MainInterface.get_currentAccount());
                        accounting.set_quantity(material.get_quantity());
                        accounting.set_procedure(INCOMING);
                        accounting.set_dateTime(LocalDateTime.now());
                        if(DataBaseStorehouse.addMaterialAccounting(accounting))
                            System.out.println("приход материала добавлен в бд");
                    }

                    _addMaterialStage.close();
                }
            }
        });

        cancelButton.setOnAction(event ->
        {
            clearComboBoxValues();
            _addMaterialStage.close();
        });

        _kindComboBox.getSelectionModel().selectedItemProperty().addListener(event ->
        {
            selectValueKindComboBox(_kindComboBox.getSelectionModel().getSelectedItem());
        });

        kindHBox.setSpacing(10);
        kindHBox.setPadding(new Insets(20));
        kindHBox.setAlignment(Pos.CENTER);
        kindHBox.setStyle("-fx-background-color: #f0f8ff");
        kindHBox.getChildren().addAll(headKindLabel, _kindComboBox, _addKindButton);

        buttonsAnchorPane.getChildren().addAll(addButton, cancelButton);
        AnchorPane.setTopAnchor(addButton,5.0);
        AnchorPane.setLeftAnchor(addButton,5.0);
        AnchorPane.setBottomAnchor(addButton,5.0);
        AnchorPane.setTopAnchor(cancelButton,5.0);
        AnchorPane.setRightAnchor(cancelButton,5.0);
        AnchorPane.setBottomAnchor(cancelButton,5.0);

        centerVBox.setSpacing(10);
        centerVBox.setPadding(new Insets(15));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.setAlignment(Pos.CENTER);
        centerVBox.getChildren().add(_selectedValueGridPane);

        bottomVBox.getChildren().addAll(new Separator(), buttonsAnchorPane);

        addMaterialBorderPane.setTop(kindHBox);
        addMaterialBorderPane.setBottom(bottomVBox);
        addMaterialBorderPane.setCenter(centerVBox);
        BorderPane.setAlignment(_selectedValueGridPane, Pos.CENTER);
        BorderPane.setAlignment(centerVBox, Pos.BOTTOM_CENTER);

        _addMaterialStage.setOnCloseRequest(event -> clearComboBoxValues());
        _addMaterialStage.initModality(Modality.WINDOW_MODAL);
        _addMaterialStage.initOwner(_materialsFormStage);
        _addMaterialStage.setTitle("Новый материал");
        _addMaterialStage.getIcons().add(MainInterface.getIconLogo());
        _addMaterialStage.setScene(addMaterialScene);
        _addMaterialStage.showAndWait();
    }

    private void selectValueKindComboBox(MaterialsKind kind)
    {
        _selectedValueGridPane.getChildren().clear();
        _manufacturerComboBox.setVisible(false);
        _widthTextField.setVisible(false);
        _heightTextField.setVisible(false);
        _colorComboBox.setVisible(false);
        _propertyComboBox.setVisible(false);
        _thiknessTextField.setVisible(false);
        _attributeComboBox.setVisible(false);
        _colorNumberTextField.setVisible(false);

        _kindComboBox.setValue(kind);

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_manufacturer())
        {
            _selectedValueGridPane.add(_headManufacturerLabel, 0,0);
            _selectedValueGridPane.add(_manufacturerComboBox, 1,0);
            _selectedValueGridPane.add(_addManufacturerButton, 2,0);
            _manufacturerComboBox.setVisible(true);
        }

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_width())
        {
            _selectedValueGridPane.add(_headWidthLabel, 0,1);
            _selectedValueGridPane.add(_widthTextField, 1,1);
            _widthTextField.setVisible(true);
        }

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_height())
        {
            _selectedValueGridPane.add(_headHeightLabel, 0,2);
            _selectedValueGridPane.add(_heightTextField, 1,2);
            _heightTextField.setVisible(true);
        }

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_color())
        {
            _selectedValueGridPane.add(_headColorLabel, 0,3);
            _selectedValueGridPane.add(_colorComboBox, 1,3);
            _selectedValueGridPane.add(_addColorButton, 2,3);
            _colorComboBox.setVisible(true);
        }

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_property())
        {
            _selectedValueGridPane.add(_headPropertyLabel, 0,4);
            _selectedValueGridPane.add(_propertyComboBox, 1,4);
            _selectedValueGridPane.add(_addPropertyButton, 2,4);
            _propertyComboBox.setVisible(true);
        }

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_thickness())
        {
            _selectedValueGridPane.add(_headThicknessLabel, 0,5);
            _selectedValueGridPane.add(_thiknessTextField, 1,5);
            _thiknessTextField.setVisible(true);
        }

        if(_kindComboBox.getSelectionModel().getSelectedItem().get_attribute())
        {
            _selectedValueGridPane.add(_headAttributeLabel, 0,6);
            _selectedValueGridPane.add(_attributeComboBox, 1,6);
            _selectedValueGridPane.add(_addAttributeButton, 2,6);
            _attributeComboBox.setVisible(true);
        }

        if (_kindComboBox.getSelectionModel().getSelectedItem().get_colorNumber())
        {
            _selectedValueGridPane.add(_headColorNumberLabel, 0, 7);
            _selectedValueGridPane.add(_colorNumberTextField, 1, 7);
            _colorNumberTextField.setVisible(true);
        }

        _selectedValueGridPane.add(_headQuantityLabel,0,8);
        _selectedValueGridPane.add(_quantityTextField,1,8);
        _selectedValueGridPane.add(_headPriceLabel, 0, 9);
        _selectedValueGridPane.add(_priceTextField, 1,9);
        _selectedValueGridPane.add(_headSellPriceLabel, 0, 10);
        _selectedValueGridPane.add(_sellPriceTextField, 1, 10);

        _selectedValueGridPane.alignmentProperty().set(Pos.CENTER);
    }

    private void editMaterialForm()
    {
        Stage editMaterialStage = new Stage();
        BorderPane editMaterialBorderPane = new BorderPane();
        Scene editMaterialScene = new Scene(editMaterialBorderPane, 300,500);
        VBox centerVBox = new VBox();
        VBox bottomVBox = new VBox();
        AnchorPane bottomAnchorPane = new AnchorPane();
        Button closeButton = new Button("Закрыть");
        ListView<MaterialsKind> kindsListView = new ListView<>(FXCollections.observableArrayList(_activeKinds));
        ListView<MaterialsValue> attributesListView = new ListView<>(FXCollections.observableArrayList(_actitveAttributes));
        ListView<MaterialsValue> colorsListView = new ListView<>(FXCollections.observableArrayList(_activeColors));
        ListView<MaterialsValue> manufacturersListView = new ListView<>(FXCollections.observableArrayList(_activeManufacturers));
        ListView<MaterialsValue> propertiesListView = new ListView<>(FXCollections.observableArrayList(_activeProperties));
        TitledPane kindsTitledPane = new TitledPane();
        TitledPane attributesTitledPane = new TitledPane();
        TitledPane colorsTitledPane = new TitledPane();
        TitledPane manufacturersTitledPane = new TitledPane();
        TitledPane propertiesTitledPane = new TitledPane();
        ContextMenu kindContextMenu = new ContextMenu();
        ContextMenu materialsValueContextMenu = new ContextMenu();
        MenuItem removeMaterialsValueItem = new MenuItem("Удалить");
        MenuItem editMaterialsValueItem = new MenuItem("Редактировать");
        MenuItem addMaterialsValueItem = new MenuItem("Добавить");
        MenuItem removeKindMenuItem = new MenuItem("Удалить");
        MenuItem editKindMenuItem = new MenuItem("Редактировать");
        MenuItem addKindMenuItem = new MenuItem("Добавить");

        closeButton.setOnAction(event ->
        {
            _mainBorderPane.getChildren().clear();
            setMainBorderPane();
            editMaterialStage.close();
        });

        attributesTitledPane.setText("Атрибуты");
        attributesTitledPane.setContent(attributesListView);
        attributesTitledPane.setExpanded(false);

        colorsTitledPane.setText("Цвета");
        colorsTitledPane.setContent(colorsListView);
        colorsTitledPane.setExpanded(false);

        manufacturersTitledPane.setText("Производители");
        manufacturersTitledPane.setContent(manufacturersListView);
        manufacturersTitledPane.setExpanded(false);

        propertiesTitledPane.setText("Свойства");
        propertiesTitledPane.setContent(propertiesListView);
        propertiesTitledPane.setExpanded(false);

        kindsTitledPane.setText("Виды материала");
        kindsTitledPane.setContent(kindsListView);
        kindsTitledPane.setExpanded(false);

        centerVBox.setAlignment(Pos.TOP_CENTER);
        centerVBox.setSpacing(15);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.setPadding(new Insets(15));
        centerVBox.getChildren().addAll(
                kindsTitledPane,
                attributesTitledPane,
                colorsTitledPane,
                manufacturersTitledPane,
                propertiesTitledPane);

        removeKindMenuItem.setOnAction(event ->
        {
            if(kindsListView.getSelectionModel().getSelectedItem() != null)
            {
                Node removableNode = null;
                TitledPane pane = null;
                MaterialsKind kind = kindsListView.getSelectionModel().getSelectedItem();
                final int indexInArrayAll = Finder.get_allMaterialsKinds().indexOf(kind);

                for (Node node : _centerAccordion.getPanes())
                {
                    pane = (TitledPane) node;
                    if (pane.getText().equals(kind.get_name()))
                        removableNode = node;
                }

                if (removableNode != null)
                {
                    TableView tableView = (TableView) pane.getContent();
                    if (!tableView.getItems().isEmpty())
                    {
                        if (MainInterface.getAlertAskConfirmationDialog(
                                "Существуют материалы данного вида. Вы уверены что хотите всё удалить?"))
                        {
                            if (checkKindsInMaterials(kind.get_id()))
                            {
                                kind.set_active(false);
                                if (DataBaseStorehouse.editMaterialsKind(kind))
                                {
                                    _centerAccordion.getPanes().remove(removableNode);
                                    _activeKinds.remove(kind);
                                    Finder.get_allMaterialsKinds().set(indexInArrayAll, kind);
                                    kindsListView.getItems().remove(kind);
                                    _kindComboBox.getItems().remove(kind);
                                    _activeMaterialsList.removeIf(material -> material.get_kind() == kind.get_id());
                                }
                            } else
                            {
                                if (DataBaseStorehouse.deleteMaterialsKind(kind.get_id()))
                                {
                                    _centerAccordion.getPanes().remove(removableNode);
                                    //DataBaseStorehouse.deleteAllMaterialsByKind(kind.get_id());
                                    _activeKinds.remove(kind);
                                    Finder.get_allMaterialsKinds().remove(kind);
                                    kindsListView.getItems().remove(kind);
                                    _kindComboBox.getItems().remove(kind);
                                    _activeMaterialsList.removeIf(material -> material.get_kind() == kind.get_id());
                                }
                            }
                        }
                    } else
                    {
                        if (checkKindsInMaterials(kind.get_id()))
                        {
                            kind.set_active(false);
                            if (DataBaseStorehouse.editMaterialsKind(kind))
                            {
                                _centerAccordion.getPanes().remove(removableNode);
                                Finder.get_allMaterialsKinds().set(indexInArrayAll, kind);
                                _activeKinds.remove(kind);
                                kindsListView.getItems().remove(kind);
                                _kindComboBox.getItems().remove(kind);
                                _activeMaterialsList.removeIf(material -> material.get_kind() == kind.get_id());
                            }
                        } else
                        {
                            if (DataBaseStorehouse.deleteMaterialsKind(kind.get_id()))
                            {
                                _centerAccordion.getPanes().remove(removableNode);
                                Finder.get_allMaterialsKinds().remove(kind);
                                _activeKinds.remove(kind);
                                kindsListView.getItems().remove(kind);
                                _kindComboBox.getItems().remove(kind);
                                _activeMaterialsList.removeIf(material -> material.get_kind() == kind.get_id());
                            }
                        }
                    }
                }
            }
        });

        editKindMenuItem.setOnAction(event ->
        {
            if(kindsListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsKind editableKind = kindsListView.getSelectionModel().getSelectedItem();
                final int indexInListView = kindsListView.getSelectionModel().getSelectedIndex();
                final int indexInArrayActive = _activeKinds.indexOf(editableKind);
                final int indexInArrayAll = Finder.get_allMaterialsKinds().indexOf(editableKind);
                final int indexInComboBox = _kindComboBox.getItems().indexOf(editableKind);
                MaterialsKindDialog kindDialog = new MaterialsKindDialog(editableKind);
                kindDialog.showAndWait(editMaterialStage);
                if (kindDialog.is_ok())
                {
                    Finder.get_allMaterialsKinds().set(indexInArrayAll, kindDialog.get_kind());
                    _activeKinds.set(indexInArrayActive, kindDialog.get_kind());
                    kindsListView.getItems().set(indexInListView, kindDialog.get_kind());
                    _kindComboBox.getItems().set(indexInComboBox, kindDialog.get_kind());
                    for (Node node : _centerAccordion.getPanes())
                    {
                        TitledPane pane = (TitledPane) node;
                        if (pane.getText().equals(editableKind.get_name()))
                        {
                            TableView tableView = (TableView) pane.getContent();
                            tableView.getColumns().clear();
                            getColumns(tableView, kindDialog.get_kind());
                            pane.setText(kindDialog.get_kind().get_name());
                        }
                    }
                    //DataBaseStorehouse.checkAllMaterialsByKind(kindDialog.get_kind());
                    for (Material material : _activeMaterialsList)
                    {
                        if (material.get_kind() == kindDialog.get_kind().get_id())
                        {
                            if (!kindDialog.get_kind().get_manufacturer())
                                material.set_manufacturer(0);
                            if (!kindDialog.get_kind().get_width())
                                material.set_width(0);
                            if (!kindDialog.get_kind().get_height())
                                material.set_height(0);
                            if (!kindDialog.get_kind().get_color())
                                material.set_color(0);
                            if (!kindDialog.get_kind().get_property())
                                material.set_property(0);
                            if (!kindDialog.get_kind().get_thickness())
                                material.set_thickness(0);
                            if (!kindDialog.get_kind().get_attribute())
                                material.set_attribute(0);

                            DataBaseStorehouse.editMaterial(material);
                        }
                    }
                    DataBaseStorehouse.editMaterialsKind(kindDialog.get_kind());
                }
            }
        });

        addKindMenuItem.setOnAction(event ->
        {
            MaterialsKindDialog kindDialog = new MaterialsKindDialog();
            kindDialog.showAndWait(editMaterialStage);
            if(kindDialog.is_ok())
            {
                Finder.get_allMaterialsKinds().add(kindDialog.get_kind());
                _activeKinds.add(kindDialog.get_kind());
                _kindComboBox.getItems().add(kindDialog.get_kind());
                kindsListView.getItems().add(kindDialog.get_kind());
                setMaterialsToCenterVBox(kindDialog.get_kind());
            }
        });

        removeMaterialsValueItem.setOnAction(event ->
        {
            MaterialsValue value;
            final String TABLE;

            switch (getFocusedTitledPane(centerVBox).getText())
            {
                case "Атрибуты":
                    if(attributesListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = attributesListView.getSelectionModel().getSelectedItem();
                        //DataBaseStorehouse.setAttributesToZero(value.get_id());
                        TABLE = DataBaseStorehouse.ATTRIBUTES_TABLE;
                        if (value.get_id() != 0)
                        {
                            if (checkAttributeInMaterials(value.get_id()))
                            {
                                final int indexInArrayAll = Finder.get_allAttributes().indexOf(value);
                                value.set_active(false);
                                if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                                {
                                    _actitveAttributes.remove(value);
                                    Finder.get_allAttributes().set(indexInArrayAll, value);
                                }
                            } else
                            {
                                if (DataBaseStorehouse.deleteMaterialsValue(TABLE, value.get_id()))
                                {
                                    _actitveAttributes.remove(value);
                                    Finder.get_allAttributes().remove(value);
                                }
                            }
                            attributesListView.getItems().remove(value);
                            _attributeComboBox.getItems().remove(value);
                        }
                    }
                    break;

                case "Цвета":
                    if(colorsListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = colorsListView.getSelectionModel().getSelectedItem();
                        //DataBaseStorehouse.setColorsToZero(value.get_id());
                        TABLE = DataBaseStorehouse.COLORS_TABLE;
                        if(value.get_id() != 0)
                        {
                            if (checkColorInMaterials(value.get_id()))
                            {
                                final int indexInArrayAll = Finder.get_allColors().indexOf(value);
                                value.set_active(false);
                                if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                                {
                                    _activeColors.remove(value);
                                    Finder.get_allColors().set(indexInArrayAll, value);
                                }
                            } else
                            {
                                if(DataBaseStorehouse.deleteMaterialsValue(TABLE, value.get_id()))
                                {
                                    _activeColors.remove(value);
                                    Finder.get_allColors().remove(value);
                                }
                            }
                            colorsListView.getItems().remove(value);
                            _colorComboBox.getItems().remove(value);
                        }
                    }
                    break;

                case "Производители":
                    if(manufacturersListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = manufacturersListView.getSelectionModel().getSelectedItem();
                        //DataBaseStorehouse.setManufacturersToZero(value.get_id());
                        TABLE = DataBaseStorehouse.MANUFACTURERS_TABLE;
                        if (value.get_id() != 0)
                        {
                            if (checkManufacturerInMaterials(value.get_id()))
                            {
                                final int indexInArrayAll = Finder.get_allManufacturers().indexOf(value);
                                value.set_active(false);
                                if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                                {
                                    _activeManufacturers.remove(value);
                                    Finder.get_allManufacturers().set(indexInArrayAll, value);
                                }
                            } else
                            {
                                if (DataBaseStorehouse.deleteMaterialsValue(TABLE, value.get_id()))
                                {
                                    _activeManufacturers.remove(value);
                                    Finder.get_allManufacturers().remove(value);
                                }
                            }
                            manufacturersListView.getItems().remove(value);
                            _manufacturerComboBox.getItems().remove(value);
                        }
                    }
                    break;

                case "Свойства":
                    if(propertiesListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = propertiesListView.getSelectionModel().getSelectedItem();

                        //DataBaseStorehouse.setPropertiesToZero(value.get_id());
                        TABLE = DataBaseStorehouse.PROPERTIES_TABLE;
                        if (value.get_id() != 0)
                        {
                            if (checkPropertyInMaterials(value.get_id()))
                            {
                                final int indexInArrayAll = Finder.get_allProperties().indexOf(value);
                                value.set_active(false);
                                if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                                {
                                    _activeProperties.remove(value);
                                    Finder.get_allProperties().set(indexInArrayAll, value);
                                }
                            } else
                            {
                                if (DataBaseStorehouse.deleteMaterialsValue(TABLE, value.get_id()))
                                {
                                    _activeProperties.remove(value);
                                    Finder.get_allProperties().remove(value);
                                }
                            }
                            propertiesListView.getItems().remove(value);
                            _propertyComboBox.getItems().remove(value);
                        }
                    }
                    break;
            }
        });

        editMaterialsValueItem.setOnAction(event ->
        {
            MaterialsValue value;
            final String TABLE;
            TextInputDialog dialog = new TextInputDialog();
            dialog.graphicProperty().set(null);
            Optional<String> result;
            int indexInListView;
            int indexInArrayAll;
            int indexInArrayActive;
            int indexInComboBox;

            switch (getFocusedTitledPane(centerVBox).getText())
            {
                case "Атрибуты":
                    if(attributesListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = attributesListView.getSelectionModel().getSelectedItem();
                        TABLE = DataBaseStorehouse.ATTRIBUTES_TABLE;
                        indexInListView = attributesListView.getSelectionModel().getSelectedIndex();
                        indexInArrayAll = Finder.get_allAttributes().indexOf(value);
                        indexInArrayActive = _actitveAttributes.indexOf(value);
                        indexInComboBox = _attributeComboBox.getItems().indexOf(value);
                        dialog.setTitle("Редактирование: атрибут");
                        dialog.setHeaderText("Введите атрибут");
                        dialog.setContentText("Атрибут: ");
                        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                        alertStage.getIcons().add(MainInterface.getIconLogo());
                        dialog.getEditor().setText(value.get_name());
                        result = dialog.showAndWait();
                        result.ifPresent(value::set_name);
                        if(DataBaseStorehouse.editMaterialsValue(TABLE, value))
                        {
                            attributesListView.getItems().set(indexInListView, value);
                            _attributeComboBox.getItems().set(indexInComboBox, value);
                            _actitveAttributes.set(indexInArrayActive, value);
                            Finder.get_allAttributes().set(indexInArrayAll, value);
                        }
                    }
                    break;

                case "Цвета":
                    if(colorsListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = colorsListView.getSelectionModel().getSelectedItem();
                        TABLE = DataBaseStorehouse.COLORS_TABLE;
                        indexInListView = colorsListView.getSelectionModel().getSelectedIndex();
                        indexInArrayAll = Finder.get_allColors().indexOf(value);
                        indexInArrayActive = _activeColors.indexOf(value);
                        indexInComboBox = _colorComboBox.getItems().indexOf(value);
                        dialog.setTitle("Редактирование: цвет");
                        dialog.setHeaderText("Введите цвет");
                        dialog.setContentText("Цвет: ");
                        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                        alertStage.getIcons().add(MainInterface.getIconLogo());
                        dialog.getEditor().setText(value.get_name());
                        result = dialog.showAndWait();
                        result.ifPresent(value::set_name);
                        if(DataBaseStorehouse.editMaterialsValue(TABLE, value))
                        {
                            colorsListView.getItems().set(indexInListView, value);
                            _colorComboBox.getItems().set(indexInComboBox, value);
                            _activeColors.set(indexInArrayActive, value);
                            Finder.get_allColors().set(indexInArrayAll, value);
                        }
                    }
                    break;

                case "Производители":
                    if(manufacturersListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = manufacturersListView.getSelectionModel().getSelectedItem();
                        TABLE = DataBaseStorehouse.MANUFACTURERS_TABLE;
                        indexInListView = manufacturersListView.getSelectionModel().getSelectedIndex();
                        indexInArrayAll = Finder.get_allManufacturers().indexOf(value);
                        indexInArrayActive = _activeManufacturers.indexOf(value);
                        indexInComboBox = _manufacturerComboBox.getItems().indexOf(value);
                        dialog.setTitle("Редактирование: производитель");
                        dialog.setHeaderText("Введите производителя");
                        dialog.setContentText("Производитель: ");
                        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                        alertStage.getIcons().add(MainInterface.getIconLogo());
                        dialog.getEditor().setText(value.get_name());
                        result = dialog.showAndWait();
                        result.ifPresent(value::set_name);
                        if(DataBaseStorehouse.editMaterialsValue(TABLE, value))
                        {
                            manufacturersListView.getItems().set(indexInListView, value);
                            _manufacturerComboBox.getItems().set(indexInComboBox, value);
                            Finder.get_allManufacturers().set(indexInArrayAll, value);
                            _activeManufacturers.set(indexInArrayActive, value);
                        }
                    }
                    break;

                case "Свойства":
                    if(propertiesListView.getSelectionModel().getSelectedItem() != null)
                    {
                        value = propertiesListView.getSelectionModel().getSelectedItem();
                        TABLE = DataBaseStorehouse.PROPERTIES_TABLE;
                        indexInListView = propertiesListView.getSelectionModel().getSelectedIndex();
                        indexInArrayAll = Finder.get_allProperties().indexOf(value);
                        indexInArrayActive = _activeProperties.indexOf(value);
                        indexInComboBox = _propertyComboBox.getItems().indexOf(value);
                        dialog.setTitle("Редактирование: свойство");
                        dialog.setHeaderText("Введите свойство");
                        dialog.setContentText("Свойство: ");
                        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                        alertStage.getIcons().add(MainInterface.getIconLogo());
                        dialog.getEditor().setText(value.get_name());
                        result = dialog.showAndWait();
                        result.ifPresent(value::set_name);
                        if(DataBaseStorehouse.editMaterialsValue(TABLE, value))
                        {
                            propertiesListView.getItems().set(indexInListView, value);
                            _propertyComboBox.getItems().set(indexInComboBox, value);
                            Finder.get_allProperties().set(indexInArrayAll, value);
                            _activeProperties.set(indexInArrayActive, value);
                        }
                    }
                    break;
            }
        });

        addMaterialsValueItem.setOnAction(event ->
        {
            MaterialsValue value;
            switch (getFocusedTitledPane(centerVBox).getText())
            {
                case "Атрибуты":
                    value = addMaterialsValue(
                            "Новый атрибут",
                            "Введите новый атрибут",
                            "Aтрибут: ",
                            DataBaseStorehouse.ATTRIBUTES_TABLE);
                    if(value != null)
                        attributesListView.getItems().add(value);
                    break;

                case "Цвета":
                    value =addMaterialsValue(
                            "Новый цвет",
                            "Введите название цвета",
                            "Цвет: ",
                            DataBaseStorehouse.COLORS_TABLE);
                    if(value != null)
                        colorsListView.getItems().add(value);
                    break;

                case "Производители":
                    value = addMaterialsValue(
                            "Новый производитель",
                            "Введите название производителя",
                            "Производитель: ",
                            DataBaseStorehouse.MANUFACTURERS_TABLE);
                    if(value != null)
                        manufacturersListView.getItems().add(value);
                    break;

                case "Свойства":
                    value = addMaterialsValue(
                            "Новое свойство",
                            "Введите новое свойство",
                            "Свойство: ",
                            DataBaseStorehouse.PROPERTIES_TABLE);
                    if(value != null)
                        propertiesListView.getItems().add(value);
                    break;
            }
        });

        kindContextMenu.getItems().addAll(
                editKindMenuItem,
                addKindMenuItem,
                removeKindMenuItem);
        materialsValueContextMenu.getItems().addAll(
                editMaterialsValueItem,
                addMaterialsValueItem,
                removeMaterialsValueItem);

        kindsListView.setContextMenu(kindContextMenu);
        attributesListView.setContextMenu(materialsValueContextMenu);
        colorsListView.setContextMenu(materialsValueContextMenu);
        manufacturersListView.setContextMenu(materialsValueContextMenu);
        propertiesListView.setContextMenu(materialsValueContextMenu);

        bottomAnchorPane.getChildren().addAll(closeButton);
        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        editMaterialBorderPane.setCenter(centerVBox);
        editMaterialBorderPane.setBottom(bottomVBox);

        editMaterialStage.setOnCloseRequest(event ->
        {
            _mainBorderPane.getChildren().clear();
            setMainBorderPane();
        });

        editMaterialStage.setTitle("Редактирование данных");
        editMaterialStage.getIcons().add(MainInterface.getIconLogo());
        editMaterialStage.initModality(Modality.WINDOW_MODAL);
        editMaterialStage.initOwner(_materialsFormStage);
        editMaterialStage.setScene(editMaterialScene);
        editMaterialStage.showAndWait();
    }

    TitledPane getFocusedTitledPane(VBox vBox)
    {
        for(Node node : vBox.getChildren())
        {
            TitledPane pane = (TitledPane) node;
            if(pane.getContent().isFocused())
                return pane;
        }

        return null;
    }

    TitledPane getFocusedTitledPane(Accordion accordion)
    {
        for(TitledPane pane : accordion.getPanes())
            if(pane.getContent().isFocused())
                return pane;

        return null;
    }

    private boolean checkToAddMaterial()
    {
        boolean check = false;
        if(_kindComboBox.getValue() == null)
            MainInterface.getAlertWarningDialog("Не выбран материал");
        else if (_widthTextField.isVisible() && _widthTextField.getText().isEmpty())
            MainInterface.getAlertWarningDialog("Не указана ширина материала");
        else if (_colorNumberTextField.isVisible() && _colorNumberTextField.getText().isEmpty())
            MainInterface.getAlertWarningDialog("Не указан номер цвета");
        else if(_quantityTextField.getText().equals(""))
        {
            _quantityTextField.setText("0");
            check = true;
        }
        else
            check = true;

        return check;
    }

    private void addMaterialToTable(Material material)
    {
        for (Node node : _centerAccordion.getPanes())
        {
            TitledPane titledPane = (TitledPane)node;
            if(_kindComboBox.getSelectionModel().getSelectedItem().get_name().equals(titledPane.getText()))
            {
                TableView tableView = (TableView)titledPane.getContent();
                tableView.getItems().add(material);
            }
        }
    }

    private MaterialsValue addMaterialsValue(final String title, final String header,  final String context, final String TABLE)
    {
        TextInputDialog dialog = new TextInputDialog();
        MaterialsValue value = null;
        int valueId = -1;
        dialog.graphicProperty().set(null);
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(context);
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && !result.get().isEmpty())
        {
            switch (TABLE)
            {
                case "manufacturers":
                    valueId = checkNameOfNewMaterialsValue(result.get(), Finder.get_allManufacturers());
                    if (valueId != -1 &&  Finder.getManufacturer(valueId) != null)
                    {
                        value =  Finder.getManufacturer(valueId);
                        final int indexInArrayAll = Finder.get_allManufacturers().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                _activeManufacturers.add(value);
                                Finder.get_allManufacturers().set(indexInArrayAll, value);
                            }
                        }
                    }
                    else
                    {
                        value = new MaterialsValue();
                        value.set_name(result.get());
                        if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                        {
                            value.set_id(DataBaseStorehouse.getLastId(TABLE));
                            value.set_active(true);
                            _activeManufacturers.add(value);
                            Finder.get_allManufacturers().add(value);
                        }
                    }
                    _manufacturerComboBox.getItems().add(value);
                    _manufacturerComboBox.setValue(value);
                    break;

                case "colors":
                    valueId = checkNameOfNewMaterialsValue(result.get(), Finder.get_allColors());
                    if (valueId != -1 && Finder.getColor(valueId) != null)
                    {
                        value = Finder.getColor(valueId);
                        final int indexInArrayAll = Finder.get_allColors().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                _activeColors.add(value);
                                Finder.get_allColors().set(indexInArrayAll, value);
                            }
                        }
                    }
                    else
                    {
                        value = new MaterialsValue();
                        value.set_name(result.get());
                        if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                        {
                            value.set_id(DataBaseStorehouse.getLastId(TABLE));
                            value.set_active(true);
                            _activeColors.add(value);
                            Finder.get_allColors().add(value);
                        }
                    }
                    _colorComboBox.getItems().add(value);
                    _colorComboBox.setValue(value);
                    break;

                case "properties":
                    valueId = checkNameOfNewMaterialsValue(result.get(), Finder.get_allProperties());
                    if (valueId != -1 && Finder.getProperty(valueId) != null)
                    {
                        value = Finder.getProperty(valueId);
                        final int indexInArrayAll = Finder.get_allProperties().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                _activeProperties.add(value);
                                Finder.get_allProperties().set(indexInArrayAll, value);
                            }
                        }
                    }
                    else
                    {
                        value = new MaterialsValue();
                        value.set_name(result.get());
                        if (DataBaseStorehouse.addMaterialsValue(value.get_name(), TABLE))
                        {
                            value.set_id(DataBaseStorehouse.getLastId(TABLE));
                            value.set_active(true);
                            _activeProperties.add(value);
                            Finder.get_allProperties().add(value);
                        }
                    }
                    _propertyComboBox.getItems().add(value);
                    _propertyComboBox.setValue(value);
                    break;

                case "attributes":
                    valueId = checkNameOfNewMaterialsValue(result.get(), Finder.get_allAttributes());
                    if (valueId != -1 && Finder.getAttribute(valueId) != null)
                    {
                        value = Finder.getAttribute(valueId);
                        final int indexInArrayAll = Finder.get_allAttributes().indexOf(value);
                        if (value != null)
                        {
                            value.set_active(true);
                            if (DataBaseStorehouse.editMaterialsValue(TABLE, value))
                            {
                                _actitveAttributes.add(value);
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
                            value.set_active(true);
                            _actitveAttributes.add(value);
                            Finder.get_allAttributes().add(value);
                        }
                    }
                    _attributeComboBox.getItems().add(value);
                    _attributeComboBox.setValue(value);
                    break;
            }
        }
        return value;
    }

    private Integer addMaterialsAmount()
    {
        int amount = 0;
        TextInputDialog inputAmountDialog = new TextInputDialog();
        inputAmountDialog.graphicProperty().set(null);
        inputAmountDialog.setTitle("Количество материала");
        inputAmountDialog.setHeaderText("Введите количество материала");
        inputAmountDialog.setContentText("Количество: ");
        Stage alertStage = (Stage) inputAmountDialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        inputAmountDialog.getEditor().textProperty().addListener(
                getChangeListener(inputAmountDialog.getEditor()));
        Optional<String> result = inputAmountDialog.showAndWait();
        if(result.isPresent())
            amount = Integer.parseInt(inputAmountDialog.getEditor().getText());

        return amount;
    }

    private void saveColumnsPosition()
    {
        for(TitledPane titledPane : _centerAccordion.getPanes())
        {
            //TitledPane titledPane = (TitledPane) node;
            TableView<Material> tableView = (TableView<Material>) titledPane.getContent();
            String columns = "";
            for (final TableColumn tableColumn : tableView.getColumns())
                columns += tableColumn.getText() + "~";

            MaterialsKind kind = Finder.getMaterialKind(Integer.parseInt(tableView.getUserData().toString()));
            DataBaseStorehouse.addColumnsToKind(kind, columns);
        }
    }



    private boolean checkMaterialsInAccountings(final int materialId)
    {
        for (MaterialAccounting accounting : DataBaseStorehouse.getMaterialAccountingList())
            if (accounting.get_material() == materialId)
                return true;

        return false;
    }

    private boolean checkKindsInMaterials(final int kindId)
    {
        for (final Material material : Finder.get_allMaterialsList())
            if (material.get_kind() == kindId)
                return true;

        return false;
    }

    static int checkNameOfNewMaterialsValue(final String newValue,  final ArrayList<MaterialsValue> valuesList)
    {
        for (final MaterialsValue value : valuesList)
            if (value.get_name().toLowerCase().equals(newValue.toLowerCase()))
                return value.get_id();
        return -1;
    }

    private boolean checkManufacturerInMaterials(final int manufacturerId)
    {
        for (final Material material : Finder.get_allMaterialsList())
            if (material.get_manufacturer() == manufacturerId)
                return true;

        return false;
    }

    private boolean checkColorInMaterials(final int colorId)
    {
        for (final Material material : Finder.get_allMaterialsList())
            if (material.get_color() == colorId)
                return true;

        return false;
    }

    private boolean checkPropertyInMaterials(final int propertyId)
    {
        for (final Material material : Finder.get_allMaterialsList())
            if (material.get_property() == propertyId)
                return true;

        return false;
    }

    private boolean checkAttributeInMaterials(final int attributeId)
    {
        for (final Material material : Finder.get_allMaterialsList())
            if (material.get_attribute() == attributeId)
                return true;

        return false;
    }

    static ChangeListener<String> getChangeListener(TextField txtpoint)
    {
        return (observable, oldValue, newValue) ->
        {
            if (!newValue.isEmpty())
            {
                try
                {
                    long pointI = Integer.parseInt(newValue);
                    txtpoint.setText(String.valueOf(pointI));
                } catch (Exception e) {
                    txtpoint.clear();
                    txtpoint.setText(getNumber(oldValue));
                }
            }
        };
    }

    static String getNumber(String value)
    {
        String n = "";
        try
        {
            return String.valueOf(Integer.parseInt(value));
        } catch (Exception e)
        {
            String[] array = value.split("");
            for (String tab : array)
            {
                try
                {
                    n = n.concat(String.valueOf(Integer.parseInt(String.valueOf(tab))));
                } catch (Exception ignored){}
            }
            return n;
        }
    }

    public static ArrayList<Material> get_activeMaterialsList()
    {
        return _activeMaterialsList;
    }

    private void saveMaterialsStageSize(Stage materialsStage)
    {
        Properties propertiesStageSizes =
                Finder._settings.getPropertiesStageSizes("materialsStage");
        if (propertiesStageSizes == null)
        {
            propertiesStageSizes = new Properties();
            propertiesStageSizes.put("width", materialsStage.getWidth());
            propertiesStageSizes.put("height", materialsStage.getHeight());
            Finder._settings.addPropertiesStageSizes("materialsStage", propertiesStageSizes);
        } else
        {
            propertiesStageSizes.put("width", materialsStage.getWidth());
            propertiesStageSizes.put("height", materialsStage.getHeight());
        }
    }

    private void loadMaterialsStageSize(Stage materialsStage)
    {
        try
        {
            Properties properties = Finder._settings.getPropertiesStageSizes("materialsStage");
            if (properties != null && properties.size() > 0)
            {
                materialsStage.setWidth((double)properties.get("width"));
                materialsStage.setHeight((double)properties.get("height"));
            }

        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }
}
