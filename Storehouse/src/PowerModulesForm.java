import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class PowerModulesForm
{
    private static Stage _powerModulesFormStage;
    private BorderPane _mainBorderPane;
    private Scene _powerModulesFormScene;
    private VBox _centerVBox;
    private static Accordion _accordion;
    //static ArrayList<PowerModule> _powerModulesList;
    //static ArrayList<MaterialsValue> _allBodiesList;
    static ArrayList<MaterialsValue> _activeBodiesList;
    //static ArrayList<MaterialsValue> _allPowersList;
    static ArrayList<MaterialsValue> _activePowersList;
    private static ArrayList<PowerModuleAccounting> _pModuleAccuontingsList;
    private static TableView<PowerModuleAccounting> _pModuleAccountingsTableView;
    private static final RadioButton _dollarsRBtn = new RadioButton("В долларах");
    private static final RadioButton _rublesRBtn = new RadioButton("В рублях");
    //private TableView<PowerModule> _powerModulesTableView;

    PowerModulesForm()
    {
        //_powerModulesList = new ArrayList<>(DataBaseStorehouse.getPowerModulesList());
        //_allBodiesList = new ArrayList<>(DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.BODIES_TABLE));
        _activeBodiesList = new ArrayList<>();
        for (MaterialsValue value : Finder.get_allBodiesList())
            if (value.is_active())
                _activeBodiesList.add(value);
        //_allPowersList = new ArrayList<>(DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.POWERS_TABLE));
        _activePowersList = new ArrayList<>();
        for (MaterialsValue value : Finder.get_allPowersList())
            if (value.is_active())
                _activePowersList.add(value);
        _pModuleAccuontingsList = new ArrayList<>(DataBaseStorehouse.getPowerModuleAccountingList());
    }

    void showAndWait(Stage primaryStage)
    {
        _powerModulesFormStage = new Stage();
        _mainBorderPane = new BorderPane();
        _powerModulesFormScene = new Scene(_mainBorderPane, 900, 800);

        _mainBorderPane.setCenter(getCenter());
        _mainBorderPane.setBottom(getBottom());

        _powerModulesFormStage.initModality(Modality.WINDOW_MODAL);
        _powerModulesFormStage.initOwner(primaryStage);
        _powerModulesFormStage.setTitle("Блоки питания");
        _powerModulesFormStage.getIcons().add(MainInterface.getIconLogo());
        _powerModulesFormStage.setScene(_powerModulesFormScene);
        _powerModulesFormStage.showAndWait();
    }

    private VBox getCenter()
    {
        _centerVBox = new VBox();
        _accordion = new Accordion();
        ToggleGroup currencyToggleGroup = new ToggleGroup();
        HBox topHBox = new HBox();
        _pModuleAccountingsTableView = new TableView<>();

        _dollarsRBtn.setToggleGroup(currencyToggleGroup);
        _rublesRBtn.setToggleGroup(currencyToggleGroup);
        _rublesRBtn.setSelected(true);

        _dollarsRBtn.setPadding(new Insets(0,0,0,20));

        _rublesRBtn.setOnAction(event -> setPriceColumns());
        _dollarsRBtn.setOnAction(event -> setPriceColumns());

        topHBox.getChildren().addAll(_dollarsRBtn, _rublesRBtn);
        topHBox.setSpacing(10);
        topHBox.setAlignment(Pos.CENTER);
        topHBox.setPadding(new Insets(5));

        setAccordionPanes();
        setPowModuleAccountingsTableView();

        _centerVBox.setPadding(new Insets(15));
        _centerVBox.setSpacing(10);
        _centerVBox.setMaxHeight(800);
        _centerVBox.setStyle("-fx-background-color: #f0f8ff");
        _centerVBox.setAlignment(Pos.TOP_CENTER);
        _centerVBox.getChildren().addAll(topHBox, _accordion, new Separator(), _pModuleAccountingsTableView);

        return _centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button editDataBtn = new Button("Редактирование данных");
        Button closeBtn = new Button("Закрыть");

        editDataBtn.setOnAction(event -> editPowerModuleValuesForm());

        closeBtn.setOnAction(event -> _powerModulesFormStage.close());

        bottomAnchorPane.getChildren().addAll(editDataBtn, closeBtn);
        AnchorPane.setTopAnchor(editDataBtn, 5.0);
        AnchorPane.setLeftAnchor(editDataBtn, 5.0);
        AnchorPane.setBottomAnchor(editDataBtn, 5.0);
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        AnchorPane.setBottomAnchor(closeBtn, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private void setAccordionPanes()
    {
        for (MaterialsValue body : Finder.get_allBodiesList())
        {
            TitledPane bodyTitledPane = new TitledPane();
            bodyTitledPane.setText(body.get_name());
            bodyTitledPane.setUserData(body.get_id());

            TableView<PowerModule> pModuleTableView = new TableView<>();
            bodyTitledPane.setContent(pModuleTableView);
            setPowerModulesTableView(pModuleTableView, body.get_id());
            if (!pModuleTableView.getItems().isEmpty() || body.is_active())
                _accordion.getPanes().add(bodyTitledPane);
        }
    }

    private static void setPowerModulesTableView(TableView<PowerModule> pModuleTableView, final int bodyId)
    {
        //_powerModulesTableView = new TableView<>();
        TableColumn<PowerModule, String> nameCol = new TableColumn<>("Название");
        TableColumn<PowerModule, Integer> powerCol = new TableColumn<>("Мощность");
        TableColumn<PowerModule, Double> priceInDollCol = new TableColumn<>("Цена ($)");
        TableColumn<PowerModule, Double> priceInRubCol = new TableColumn<>("Цена (руб.)");
        TableColumn<PowerModule, Integer> quantityCol = new TableColumn<>("В наличии");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("_name"));
        nameCol.setStyle("-fx-alignment: CENTER;");
        nameCol.setCellFactory(tc ->
        {
            TableCell<PowerModule, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        powerCol.setCellValueFactory(new PropertyValueFactory<>("_power"));
        powerCol.setStyle("-fx-alignment: CENTER;");
        powerCol.setCellFactory(tc -> new TableCell<>()
        {
            @Override
            protected void updateItem(Integer powerId, boolean empty)
            {
                super.updateItem(powerId, empty);
                if(empty)
                {
                    setText(null);
                } else
                {
                    if(Finder.getPower(powerId) != null)
                        setText(Finder.getPower(powerId).get_name());
                }
            }
        });

        priceInDollCol.setCellValueFactory(new PropertyValueFactory<>("_price"));
        priceInDollCol.setStyle("-fx-alignment: CENTER;");
        priceInDollCol.setCellFactory(tc -> new TableCell<>()
        {
            @Override
            protected void updateItem(Double price, boolean empty)
            {
                super.updateItem(price, empty);
                if (empty)
                    setText(null);
                else
                    setText(MainInterface.DF.format(price));
            }
        });
        priceInDollCol.setVisible(false);

        priceInRubCol.setCellValueFactory(new PropertyValueFactory<>("_price"));
        priceInRubCol.setStyle("-fx-alignment: CENTER;");
        priceInRubCol.setCellFactory(tc -> new TableCell<>()
        {
            @Override
            protected void updateItem(Double price, boolean empty)
            {
                super.updateItem(price, empty);
                if (empty)
                    setText(null);
                else
                    setText(MainInterface.DF.format(MainInterface.toRuble(price)));
            }
        });

        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");

        pModuleTableView.setPlaceholder(new Text("Данные отсутствуют"));
        pModuleTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        pModuleTableView.setContextMenu(getContextMenu(pModuleTableView));
        pModuleTableView.getColumns().addAll(nameCol, powerCol, priceInDollCol, priceInRubCol, quantityCol);
        for (PowerModule pModule : Finder.get_powerModulesList())
            if (pModule.is_active() && pModule.get_body() == bodyId)
                pModuleTableView.getItems().add(pModule);
    }

    private void setPowModuleAccountingsTableView()
    {
        TableColumn<PowerModuleAccounting, LocalDateTime> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_dateTime"));
        dateCol.setCellFactory(tc -> new TableCell<PowerModuleAccounting, LocalDateTime>()
        {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty)
            {
                super.updateItem(date, empty);
                if (empty)
                    setText(null);
                else
                {
                    String dateTime = MainInterface._formatter.format(date) + " " +
                            MainInterface._formatterTime.format(date);
                    setText(dateTime);
                }
            }
        });

        TableColumn<PowerModuleAccounting, Integer> pModuleCol = new TableColumn<>("Блок питания");
        pModuleCol.setStyle("-fx-alignment: CENTER;");
        pModuleCol.setCellValueFactory(new PropertyValueFactory<>("_powModuleId"));
        pModuleCol.setCellFactory(tc -> new TableCell<PowerModuleAccounting, Integer>()
        {
            @Override
            protected void updateItem(final Integer pModuleId, boolean empty)
            {
                super.updateItem(pModuleId, empty);
                if(empty)
                    setText(null);
                else
                {
                    String name = "";
                    PowerModule module = Finder.getPowerModule(pModuleId);
                    if (module != null)
                        name = module.get_name();
                    setText(name);
                }
            }
        });

        TableColumn<PowerModuleAccounting, Integer> pModuleBodyCol = new TableColumn<>("Корпус");
        pModuleBodyCol.setStyle("-fx-alignment: CENTER;");
        pModuleBodyCol.setCellValueFactory(new PropertyValueFactory<>("_powModuleId"));
        pModuleBodyCol.setCellFactory(tc -> new TableCell<PowerModuleAccounting, Integer>()
        {
            @Override
            protected void updateItem(final Integer powModuleId, boolean empty)
            {
                super.updateItem(powModuleId, empty);
                if(empty)
                    setText(null);
                else
                {
                    if (Finder.getPowerModule(powModuleId) != null)
                    {
                        PowerModule module = Finder.getPowerModule(powModuleId);
                        String body = "";
                        if (Finder.getBody(module.get_body()) != null)
                            body = Finder.getBody(module.get_body()).get_name();
                        setText(body);
                    } else
                        setText("");
                }
            }
        });

        TableColumn<PowerModuleAccounting, Integer> pModulePowerCol = new TableColumn<>("Мощность");
        pModulePowerCol.setStyle("-fx-alignment: CENTER;");
        pModulePowerCol.setCellValueFactory(new PropertyValueFactory<>("_powModuleId"));
        pModulePowerCol.setCellFactory(tc -> new TableCell<PowerModuleAccounting, Integer>()
        {
            @Override
            protected void updateItem(final Integer powModuleId, boolean empty)
            {
                super.updateItem(powModuleId, empty);
                if(empty)
                    setText(null);
                else
                {
                    if (Finder.getPowerModule(powModuleId) != null)
                    {
                        PowerModule module = Finder.getPowerModule(powModuleId);
                        String power = "";
                        if (Finder.getPower(module.get_power()) != null)
                            power = Finder.getPower(module.get_power()).get_name();
                        setText(power);
                    } else
                        setText("");
                }
            }
        });

        TableColumn<PowerModuleAccounting, Integer> accountCol = new TableColumn<>("Аккаунт");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("_accountId"));
        accountCol.setStyle("-fx-alignment: CENTER;");
        accountCol.setCellFactory(tc -> new TableCell<PowerModuleAccounting, Integer>()
        {
            @Override
            protected void updateItem(Integer accountId, boolean empty)
            {
                super.updateItem(accountId, empty);
                if(empty)
                {
                    setText(null);
                } else
                {
                    String account = Finder.getAccount(accountId).get_name();
                    setText(account);
                }
            }
        });

        TableColumn<PowerModuleAccounting, String> procedureCol = new TableColumn<>("Процедура");
        procedureCol.setStyle("-fx-alignment: CENTER;");
        procedureCol.setCellValueFactory(new PropertyValueFactory<>("_procedure"));

        TableColumn<PowerModuleAccounting, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));

        TableColumn<PowerModuleAccounting, String> remarkCol = new TableColumn<>("Примечание");
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("_remark"));
        remarkCol.setCellFactory(tc ->
        {
            TableCell<PowerModuleAccounting, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(remarkCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        _pModuleAccountingsTableView.getColumns().addAll(
                dateCol,
                pModuleCol,
                pModuleBodyCol,
                pModulePowerCol,
                accountCol,
                procedureCol,
                quantityCol,
                remarkCol);

        _pModuleAccountingsTableView.setStyle("");
        _pModuleAccountingsTableView.setEditable(false);
        _pModuleAccountingsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _pModuleAccountingsTableView.setRowFactory(new Callback<TableView<PowerModuleAccounting>, TableRow<PowerModuleAccounting>>()
        {
            @Override
            public TableRow<PowerModuleAccounting> call(TableView<PowerModuleAccounting> tableView)
            {
                TableRow<PowerModuleAccounting> row = new TableRow<PowerModuleAccounting>()
                {
                    @Override
                    protected void updateItem(PowerModuleAccounting accounting, boolean empty)
                    {
                        super.updateItem(accounting, empty);
                        this.setFocused(true);
                        if(!empty)
                        {
                            if (accounting != null && accounting.get_procedure().equals(MaterialsForm.INCOMING))
                            {
                                this.setStyle("-fx-background-color: #AFEEEE;");
                            } else if (accounting != null && accounting.get_procedure().equals(MaterialsForm.CONSUMPTION))
                            {
                                this.setStyle("-fx-background-color: #FFC0CB");
                            } else this.setStyle("");
                        } else
                        {
                            this.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;");
                        }
                    }
                };

                row.setOnMouseClicked(event ->
                {
                    if(!row.isEmpty())
                    {
                        if(row.getItem().get_procedure().equals("Приход"))
                        {
                            row.setStyle("-fx-background-color: #4682B4;" +
                                    "-fx-text-background-color: #ffffff");
                        } else if(row.getItem().get_procedure().equals("Расход"))
                        {
                            row.setStyle("-fx-background-color: #8B0000; " +
                                    "-fx-text-background-color: #ffffff");
                        } else
                        {
                            row.setStyle("");
                        }
                    }
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1)
                    {
                        //clickOnRowAccounting(getLed(row.getItem().get_ledId()));
                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY))
                    {
                        row.setOnContextMenuRequested(event1 ->
                        {
                            if(row.getItem()!=null)
                            {
                                System.out.println("клик правой КМ");
                            }
                        });
                    }
                });
                return row;
            }
        });

        _pModuleAccountingsTableView.setContextMenu(getAccountingContextMenu());
        _pModuleAccountingsTableView.setPlaceholder(new Text("Данные отсутствуют"));
        _pModuleAccountingsTableView.setItems(FXCollections.observableArrayList(_pModuleAccuontingsList));
        _pModuleAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        _pModuleAccountingsTableView.scrollTo(_pModuleAccountingsTableView.getItems().size()-1);
    }

    private void setPriceColumns()
    {
        for (TitledPane pane : _accordion.getPanes())
        {
            TableView<PowerModule> tableView = (TableView<PowerModule>)pane.getContent();
            if (tableView.getColumns().get(2).isVisible())
            {
                tableView.getColumns().get(2).setVisible(false);
                tableView.getColumns().get(3).setVisible(true);
            } else
            {
                tableView.getColumns().get(2).setVisible(true);
                tableView.getColumns().get(3).setVisible(false);
            }
        }
    }

    private void editPowerModuleValuesForm()
    {
        Stage editValuesStage = new Stage();
        BorderPane editValuesBorderPane = new BorderPane();
        Scene editValuesScene = new Scene(editValuesBorderPane, 300,400);
        VBox editValuesCenterVBox = new VBox();
        VBox editValuesBottomVBox = new VBox();
        TitledPane editBodiesTitledPane = new TitledPane();
        TitledPane editPowersTitledPane = new TitledPane();
        AnchorPane bottomAnchorPane = new AnchorPane();
        Button closeBtn = new Button("Закрыть");
        ListView<MaterialsValue> powersListView = new ListView<>();
        ListView<MaterialsValue> bodiesListView = new ListView<>();
        ContextMenu powersContextMenu = new ContextMenu();
        ContextMenu bodiesContexMenu = new ContextMenu();
        MenuItem addBodyMenuItem = new MenuItem("Добавить");
        MenuItem editBodyMenuItem = new MenuItem("Редактировать");
        MenuItem removeBodyMenuItem = new MenuItem("Удалить");
        MenuItem addPowerMenuItem = new MenuItem("Добавить");
        MenuItem editPowerMenuItem = new MenuItem("Редактировать");
        MenuItem removePowerMenuItem = new MenuItem("Удалить");

        addBodyMenuItem.setOnAction(event ->
        {
            MaterialsValue newPowModuleBody = addPowModuleBody();
            if (newPowModuleBody != null)
                bodiesListView.getItems().add(newPowModuleBody);
        });

        editBodyMenuItem.setOnAction(event ->
        {
            if(bodiesListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsValue editableBody = bodiesListView.getSelectionModel().getSelectedItem();
                final int indexInListView = bodiesListView.getItems().indexOf(editableBody);
                final int indexInArrayAll = Finder.get_allBodiesList().indexOf(editableBody);
                final int indexInArrayActive = _activeBodiesList.indexOf(editableBody);
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Добавить корпус");
                dialog.setHeaderText("Введите название корпуса");
                dialog.setContentText("Название копруса: ");
                dialog.graphicProperty().set(null);
                Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(MainInterface.getIconLogo());
                dialog.getEditor().setText(editableBody.get_name());
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent() && !result.get().isEmpty())
                {
                    result.ifPresent(editableBody::set_name);
                    if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.BODIES_TABLE, editableBody))
                    {
                        Finder.get_allBodiesList().set(indexInArrayAll, editableBody);
                        _activeBodiesList.set(indexInArrayActive, editableBody);
                        bodiesListView.getItems().set(indexInListView, editableBody);

                        for (TitledPane pane : _accordion.getPanes())
                        {
                            if (Integer.parseInt(pane.getUserData().toString()) == editableBody.get_id())
                            {
                                pane.setText(editableBody.get_name());
                                refreshPowModuleAccTableView();
                                break;
                            }
                        }
                    }
                }
            }
        });

        removeBodyMenuItem.setOnAction(event ->
        {
            if (bodiesListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsValue removableBody = bodiesListView.getSelectionModel().getSelectedItem();

                if (MainInterface.getAlertAskConfirmationDialog("Вы уверены, что хотите удалть данный вид корпуса из списка?"))
                {
                    final int indexInArrayAll = Finder.get_allBodiesList().indexOf(removableBody);
                    final int indexInArrayActive = _activeBodiesList.indexOf(removableBody);
                    final int indexInListView = bodiesListView.getItems().indexOf(removableBody);

                    if (checkBodyInPowModules(removableBody.get_id()))
                    {
                        removableBody.set_active(false);
                        if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.BODIES_TABLE, removableBody))
                        {
                            Finder.get_allBodiesList().set(indexInArrayAll, removableBody);
                            _activeBodiesList.remove(indexInArrayActive);
                            bodiesListView.getItems().remove(indexInListView);
                        }
                    } else
                    {
                        if (DataBaseStorehouse.deleteMaterialsValue(DataBaseStorehouse.BODIES_TABLE, removableBody.get_id()))
                        {
                            Finder.get_allBodiesList().remove(indexInArrayAll);
                            _activeBodiesList.remove(indexInArrayActive);
                            bodiesListView.getItems().remove(indexInListView);

                            for (TitledPane pane : _accordion.getPanes())
                            {
                                if (Integer.parseInt(pane.getUserData().toString()) == removableBody.get_id())
                                {
                                    _accordion.getPanes().remove(pane);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });

        addPowerMenuItem.setOnAction(event ->
        {
            MaterialsValue newPowModulePower = addPowModulePower();
            if (newPowModulePower != null)
                powersListView.getItems().add(newPowModulePower);
        });

        editPowerMenuItem.setOnAction(event ->
        {
            if(powersListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsValue editablePower = powersListView.getSelectionModel().getSelectedItem();
                final int indexInListView = powersListView.getItems().indexOf(editablePower);
                final int indexInArrayAll = Finder.get_allPowersList().indexOf(editablePower);
                final int indexInArrayActive = _activePowersList.indexOf(editablePower);
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Добавить мощность");
                dialog.setHeaderText("Введите мощность");
                dialog.setContentText("Мощность: ");
                dialog.graphicProperty().set(null);
                dialog.getEditor().textProperty().addListener(
                        MaterialsForm.getChangeListener(dialog.getEditor()));
                Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(MainInterface.getIconLogo());
                dialog.getEditor().setText(editablePower.get_name());
                Optional<String> result = dialog.showAndWait();

                if (result.isPresent() && !result.get().isEmpty())
                {
                    result.ifPresent(editablePower::set_name);
                    if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.POWERS_TABLE, editablePower))
                    {
                        Finder.get_allPowersList().set(indexInArrayAll, editablePower);
                        _activePowersList.set(indexInArrayActive, editablePower);
                        powersListView.getItems().set(indexInListView, editablePower);

                        refreshPowModuleAccTableView();
                    }
                }
            }
        });

        removePowerMenuItem.setOnAction(event ->
        {
            if(powersListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsValue removablePower = powersListView.getSelectionModel().getSelectedItem();
                if (MainInterface.getAlertAskConfirmationDialog("Вы уверены, что хотите удалть данный вид корпуса из списка?"))
                {
                    final int indexInArrayAll = Finder.get_allPowersList().indexOf(removablePower);
                    final int indexInArrayActive = _activePowersList.indexOf(removablePower);
                    final int indexInListView = powersListView.getItems().indexOf(removablePower);

                    if (checkPowerInPowModules(removablePower.get_id()))
                    {
                        removablePower.set_active(false);
                        if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.POWERS_TABLE, removablePower))
                        {
                            Finder.get_allPowersList().set(indexInArrayAll, removablePower);
                            _activePowersList.remove(indexInArrayActive);
                            powersListView.getItems().remove(indexInListView);
                        }
                    } else
                    {
                        if (DataBaseStorehouse.deleteMaterialsValue(DataBaseStorehouse.POWERS_TABLE, removablePower.get_id()))
                        {
                            Finder.get_allPowersList().remove(indexInArrayAll);
                            _activePowersList.remove(indexInArrayActive);
                            powersListView.getItems().remove(indexInListView);
                        }
                    }
                }
            }
        });

        bodiesContexMenu.getItems().addAll(addBodyMenuItem, editBodyMenuItem, removeBodyMenuItem);
        powersContextMenu.getItems().addAll(addPowerMenuItem, editPowerMenuItem, removePowerMenuItem);

        bodiesListView.getItems().addAll(_activeBodiesList);
        bodiesListView.setContextMenu(bodiesContexMenu);

        powersListView.getItems().addAll(_activePowersList);
        powersListView.setContextMenu(powersContextMenu);

        editBodiesTitledPane.setText("Корпусы");
        editBodiesTitledPane.setContent(bodiesListView);
        editBodiesTitledPane.setExpanded(false);

        editPowersTitledPane.setText("Мощности");
        editPowersTitledPane.setContent(powersListView);
        editPowersTitledPane.setExpanded(false);

        editValuesCenterVBox.setAlignment(Pos.TOP_CENTER);
        editValuesCenterVBox.setSpacing(10);
        editValuesCenterVBox.setPadding(new Insets(15));
        editValuesCenterVBox.setStyle("-fx-background-color: #f0f8ff");
        editValuesCenterVBox.getChildren().addAll(editBodiesTitledPane, editPowersTitledPane);

        closeBtn.setOnAction(event -> editValuesStage.close());

        bottomAnchorPane.getChildren().add(closeBtn);
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        AnchorPane.setBottomAnchor(closeBtn, 5.0);

        editValuesBottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        editValuesBorderPane.setCenter(editValuesCenterVBox);
        editValuesBorderPane.setBottom(editValuesBottomVBox);

        editValuesStage.setScene(editValuesScene);
        editValuesStage.setTitle("Редактирование данных");
        editValuesStage.initModality(Modality.WINDOW_MODAL);
        editValuesStage.initOwner(_powerModulesFormStage);
        editValuesStage.getIcons().add(MainInterface.getIconLogo());
        editValuesStage.showAndWait();
    }

    private static ContextMenu getContextMenu(TableView<PowerModule> pModuleTableView)
    {
        ContextMenu contextMenu = new ContextMenu();
        SeparatorMenuItem separator = new SeparatorMenuItem();
        MenuItem addMenuItem = new MenuItem("Добавить");
        MenuItem editMenuItem = new MenuItem("Редактировать");
        MenuItem removeMenuItem = new MenuItem("Удалить");
        Menu accountingMenu = new Menu("Операция учёта");
        MenuItem consumptionMenuItem = new MenuItem("Расход");
        MenuItem incomingMenuItem = new MenuItem("Приход");
        MenuItem addRequestItem = new MenuItem("Создать заявку");

        addMenuItem.setOnAction(event ->
        {
            PowerModuleDialog powerModuleDialog = new PowerModuleDialog();
            /*
            if (_rublesRBtn.isSelected())
                powerModuleDialog._rubleRBtn.setSelected(true);
            else if (_dollarsRBtn.isSelected())
                powerModuleDialog._dollarRBtn.setSelected(true);
             */
            powerModuleDialog.show(_powerModulesFormStage);
            if(powerModuleDialog.is_ok())
            {
                final PowerModule pModule = powerModuleDialog.get_powerModule();
                Finder.get_powerModulesList().add(pModule);
                if (pModule.get_body() == Integer.parseInt(_accordion.getExpandedPane().getUserData().toString()))
                    pModuleTableView.getItems().add(pModule);
                else
                {
                    for (final TitledPane pane : _accordion.getPanes())
                    {
                        if (pModule.get_body() == Integer.parseInt(pane.getUserData().toString()))
                        {
                            TableView<PowerModule> tableViewInPane = (TableView<PowerModule>)pane.getContent();
                            tableViewInPane.getItems().add(pModule);
                            break;
                        }
                    }
                }

                if (pModule.get_quantity() > 0)
                {
                    PowerModuleAccounting accounting = new PowerModuleAccounting();
                    accounting.set_powModuleId(pModule.get_id());
                    accounting.set_accountId(MainInterface.get_currentAccount());
                    accounting.set_quantity(pModule.get_quantity());
                    accounting.set_procedure(MaterialsForm.INCOMING);
                    accounting.set_dateTime(LocalDateTime.now());
                    if (DataBaseStorehouse.addPowerModuleAccounting(accounting))
                    {
                        accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POW_MODULE_ACCOUNTINGS_TABLE));
                        _pModuleAccuontingsList.add(accounting);
                        _pModuleAccountingsTableView.getItems().add(accounting);
                        _pModuleAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    }
                }
            }
        });

        editMenuItem.setOnAction(event ->
        {
            if(pModuleTableView.getSelectionModel().getSelectedItem() != null)
            {
                PowerModule editablePowerModule = pModuleTableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_powerModulesList().indexOf(editablePowerModule);
                final int indexInTableView = pModuleTableView.getItems().indexOf(editablePowerModule);
                PowerModuleDialog powerModuleDialog = new PowerModuleDialog(editablePowerModule);
                powerModuleDialog.show(_powerModulesFormStage);
                if (powerModuleDialog.is_ok())
                {
                    final PowerModule editedPowModule = powerModuleDialog.get_powerModule();
                    Finder.get_powerModulesList().set(indexInArray, editedPowModule);
                    if (editedPowModule.get_body() == Integer.parseInt(_accordion.getExpandedPane().getUserData().toString()))
                        pModuleTableView.getItems().set(indexInTableView, editedPowModule);
                    else
                    {
                        pModuleTableView.getItems().remove(indexInTableView);
                        if (pModuleTableView.getItems().isEmpty() && !Finder.getBody(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString())).is_active())
                            _accordion.getPanes().remove(_accordion.getExpandedPane());
                        for (TitledPane pane : _accordion.getPanes())
                        {
                            if (editedPowModule.get_body() == Integer.parseInt(pane.getUserData().toString()))
                            {
                                TableView<PowerModule> tableViewInPane = (TableView<PowerModule>) pane.getContent();
                                tableViewInPane.getItems().add(editedPowModule);
                                break;
                            }
                        }
                    }

                    refreshPowModuleAccTableView();
                }
            }
        });

        removeMenuItem.setOnAction(event ->
        {
            if(pModuleTableView.getSelectionModel().getSelectedItem() != null &&
                    MainInterface.getAlertAskConfirmationDialog("Вы уверены что хотите удалить блок питания?"))
            {
                PowerModule powerModule = pModuleTableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_powerModulesList().indexOf(powerModule);

                if (checkPowModuleInAccountings(powerModule.get_id()))
                {
                    powerModule.set_active(false);
                    if (DataBaseStorehouse.editPowerModule(powerModule))
                    {
                        Finder.get_powerModulesList().set(indexInArray, powerModule);
                        pModuleTableView.getItems().remove(powerModule);
                        if (pModuleTableView.getItems().isEmpty() && !Finder.getBody(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString())).is_active())
                            _accordion.getPanes().remove(_accordion.getExpandedPane());
                        pModuleTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    }
                } else
                {
                    if(DataBaseStorehouse.deletePowerModule(powerModule.get_id()))
                    {
                        Finder.get_powerModulesList().remove(powerModule);
                        pModuleTableView.getItems().remove(powerModule);
                        if (pModuleTableView.getItems().isEmpty() && !Finder.getBody(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString())).is_active())
                            _accordion.getPanes().remove(_accordion.getExpandedPane());
                        pModuleTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    }
                }
            }
        });

        addRequestItem.setOnAction(event ->
        {
            PowerModule selectedPowerModule = pModuleTableView.getSelectionModel().getSelectedItem();
            if (selectedPowerModule == null)
                return;
            RequestDialog dialog = new RequestDialog(4, selectedPowerModule.get_id());
            dialog.set_kindComboBox();
            dialog.showAndWait(_powerModulesFormStage);
        });

        incomingMenuItem.setOnAction(event ->
        {
            if (pModuleTableView.getSelectionModel().getSelectedItem() != null)
            {
                PowerModule powerModule = pModuleTableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_powerModulesList().indexOf(powerModule);
                final int indexInTableView = pModuleTableView.getItems().indexOf(powerModule);
                PowerModuleAccounting accounting = new PowerModuleAccounting();
                final int oldQuantity = powerModule.get_quantity();
                final int inputAmount = addPowModuleAmount(oldQuantity, MaterialsForm.INCOMING);
                if (inputAmount != 0)
                {
                    final int newQuantity = oldQuantity + inputAmount;
                    accounting.set_powModuleId(powerModule.get_id());
                    accounting.set_accountId(MainInterface.get_currentAccount());
                    accounting.set_quantity(inputAmount);
                    accounting.set_procedure(MaterialsForm.INCOMING);
                    accounting.set_dateTime(LocalDateTime.now());
                    if (DataBaseStorehouse.addPowerModuleAccounting(accounting))
                    {
                        accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POW_MODULE_ACCOUNTINGS_TABLE));
                        _pModuleAccuontingsList.add(accounting);
                        _pModuleAccountingsTableView.getItems().add(accounting);
                        powerModule.set_quantity(newQuantity);
                        if (DataBaseStorehouse.editPowerModule(powerModule))
                        {
                            Finder.get_powerModulesList().set(indexInArray, powerModule);
                            pModuleTableView.getItems().set(indexInTableView, powerModule);
                            pModuleTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                        }
                    }
                }
            }
        });

        consumptionMenuItem.setOnAction(event ->
        {
            if (pModuleTableView.getSelectionModel().getSelectedItem() != null)
                consumptionDialog(pModuleTableView);
        });

        accountingMenu.getItems().addAll(incomingMenuItem, consumptionMenuItem);

        contextMenu.getItems().addAll(
                addMenuItem,
                editMenuItem,
                removeMenuItem,
                separator,
                accountingMenu,
                new SeparatorMenuItem(),
                addRequestItem);

        return contextMenu;
    }

    private static ContextMenu getAccountingContextMenu()
    {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addRemarkMenuItem = new MenuItem("Добавить примечание");

        addRemarkMenuItem.setOnAction(event ->
        {
            if (_pModuleAccountingsTableView.getSelectionModel().getSelectedItem() != null)
                addAccountingRemark();
        });

        contextMenu.getItems().addAll(addRemarkMenuItem);
        return contextMenu;
    }

    private static void refreshPowModuleAccTableView()
    {
        _pModuleAccountingsTableView.getItems().clear();
        _pModuleAccountingsTableView.getItems().addAll(FXCollections.observableArrayList(_pModuleAccuontingsList));
        _pModuleAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    static Accordion getAccordion() { return _accordion; }


    private static boolean checkPowModuleInAccountings(final int pModuleId)
    {
        for (PowerModuleAccounting accounting : _pModuleAccuontingsList)
            if (accounting.get_powModuleId() == pModuleId)
                return true;

        return false;
    }

    private boolean checkBodyInPowModules(final int bodyId)
    {
        for (PowerModule powerModule : Finder.get_powerModulesList())
            if (powerModule.get_body() == bodyId)
                return true;

        return false;
    }

    private boolean checkPowerInPowModules(final int powerId)
    {
        for (PowerModule powerModule : Finder.get_powerModulesList())
            if (powerModule.get_power() == powerId)
                return true;

        return false;
    }

    private static Integer addPowModuleAmount(int currentQuantity, String operation)
    {
        int amount = 0;
        TextInputDialog inputAmountDialog = new TextInputDialog();
        inputAmountDialog.graphicProperty().set(null);
        inputAmountDialog.setTitle(operation);
        inputAmountDialog.setHeaderText("Введите количество");
        inputAmountDialog.setContentText("Количество: ");
        Stage alertStage = (Stage) inputAmountDialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        inputAmountDialog.getEditor().textProperty().addListener(
                MaterialsForm.getChangeListener(inputAmountDialog.getEditor()));
        Optional<String> optionalResult = inputAmountDialog.showAndWait();
        if(optionalResult.isPresent())
        {
            amount = Integer.parseInt(inputAmountDialog.getEditor().getText());
            if(operation.equals(MaterialsForm.CONSUMPTION))
            {
                int result = currentQuantity - amount;
                if(result < 0)
                {
                    MainInterface.getAlertWarningDialog("Операция невозможна: введенное число превышает количество блоков питания в наличии");
                    amount = 0;
                }
            }
        }

        return amount;
    }

    private static void consumptionDialog(TableView<PowerModule> pModuleTableView)
    {
        PowerModule powerModule = pModuleTableView.getSelectionModel().getSelectedItem();
        final int indexInArray = Finder.get_powerModulesList().indexOf(powerModule);
        final int indexInTableView = pModuleTableView.getItems().indexOf(powerModule);
        PowerModuleAccounting accounting = new PowerModuleAccounting();
        final int oldQuantity = powerModule.get_quantity();

        AddAccountingDialog dialog = new AddAccountingDialog();
        dialog.dialogStage.setTitle(MaterialsForm.CONSUMPTION);
        dialog.dialogStage.setHeight(400);
        dialog.addTextArea();
        dialog.addTextField();
        dialog.textFieldTitledPane.setText("Количество");
        dialog.textAreaTitledPane.setText("Примечание");
        dialog.textField.textProperty().addListener(
                MaterialsForm.getChangeListener(dialog.textField));
        dialog.saveButton.setVisible(false);

        dialog.addButton.setOnAction(event ->
        {
            if (!dialog.textField.getText().isEmpty())
            {
                int amount = Integer.parseInt(dialog.textField.getText());
                int result = oldQuantity - amount;
                if(result < 0)
                    MainInterface.getAlertWarningDialog("Операция невозможна: введенное число превышает количество блоков питания в наличии");
                else
                {
                    dialog.ok = true;
                    dialog.dialogStage.close();
                }
            }
            else
                MainInterface.getAlertWarningDialog("Не введено количество");
        });

        dialog.showAndWait(_powerModulesFormStage);
        if (dialog.ok)
        {
            String remark = null;
            final int inputAmount = Integer.parseInt(dialog.textField.getText());
            if (!dialog.textArea.getText().isEmpty())
                remark = dialog.textArea.getText();
            if (inputAmount != 0)
            {
                final int newQuantity = oldQuantity - inputAmount;
                accounting.set_powModuleId(powerModule.get_id());
                accounting.set_accountId(MainInterface.get_currentAccount());
                accounting.set_quantity(inputAmount);
                accounting.set_procedure(MaterialsForm.CONSUMPTION);
                accounting.set_dateTime(LocalDateTime.now());
                accounting.set_remark(remark);
                if (DataBaseStorehouse.addPowerModuleAccounting(accounting))
                {
                    accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POW_MODULE_ACCOUNTINGS_TABLE));
                    _pModuleAccuontingsList.add(accounting);
                    _pModuleAccountingsTableView.getItems().add(accounting);
                    powerModule.set_quantity(newQuantity);
                    if (DataBaseStorehouse.editPowerModule(powerModule))
                    {
                        Finder.get_powerModulesList().set(indexInArray, powerModule);
                        pModuleTableView.getItems().set(indexInTableView, powerModule);
                        pModuleTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    }
                }
            }
        }
    }

    static void addAccountingRemark()
    {
        PowerModuleAccounting accounting = _pModuleAccountingsTableView.getSelectionModel().getSelectedItem();
        final int indexInArray = _pModuleAccuontingsList.indexOf(accounting);
        final int indexInTableView = _pModuleAccountingsTableView.getItems().indexOf(accounting);
        AddAccountingDialog dialog = new AddAccountingDialog();
        dialog.dialogStage.setTitle("Добавить примечание");
        dialog.addTextArea();
        dialog.textAreaTitledPane.setText("Примечание");
        //dialog.textField.setVisible(false);
        //dialog.textFieldLabel.setVisible(false);
        dialog.addButton.setVisible(false);
        dialog.saveButton.setText("Добавить примечание");

        if (accounting.get_remark() != null)
            dialog.textArea.setText(accounting.get_remark());

        dialog.saveButton.setOnAction(event ->
        {
            if (!dialog.textArea.getText().isEmpty())
            {
                accounting.set_remark(dialog.textArea.getText());
                dialog.ok = true;
                dialog.dialogStage.close();
            }
        });

        dialog.showAndWait(_powerModulesFormStage);
        if (dialog.ok)
        {
            if (DataBaseStorehouse.editPowerModuleAccounting(accounting))
            {
                _pModuleAccuontingsList.set(indexInArray, accounting);
                _pModuleAccountingsTableView.getItems().set(indexInTableView, accounting);
            }
        }
    }

    static MaterialsValue addPowModuleBody()
    {
        TextInputDialog dialog = new TextInputDialog();
        MaterialsValue value = null;
        int valueId = -1;
        dialog.setTitle("Добавить корпус");
        dialog.setHeaderText("Введите название корпуса");
        dialog.setContentText("Название копруса: ");
        dialog.graphicProperty().set(null);
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && !result.get().isEmpty())
        {
            valueId = MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allBodiesList());
            if (valueId != -1 && Finder.getBody(valueId) != null)
            {
                value = Finder.getBody(valueId);
                final int indexInArrayAll = Finder.get_allBodiesList().indexOf(value);
                if (value != null)
                {
                    value.set_active(true);
                    if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.BODIES_TABLE, value))
                    {
                        _activeBodiesList.add(value);
                        Finder.get_allBodiesList().set(indexInArrayAll, value);
                    }
                }
            } else
            {
                value = new MaterialsValue();
                value.set_name(result.get());
                if (DataBaseStorehouse.addMaterialsValue(value.get_name(), DataBaseStorehouse.BODIES_TABLE))
                {
                    value.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.BODIES_TABLE));
                    Finder.get_allBodiesList().add(value);
                    _activeBodiesList.add(value);
                }
            }

            TitledPane newBodyTitledPane = new TitledPane();
            newBodyTitledPane.setText(value.get_name());
            newBodyTitledPane.setUserData(value.get_id());

            TableView<PowerModule> pModuleOfBodyTableView = new TableView<>();
            newBodyTitledPane.setContent(pModuleOfBodyTableView);
            setPowerModulesTableView(pModuleOfBodyTableView, value.get_id());
            _accordion.getPanes().add(newBodyTitledPane);
        }

        return value;
    }

    static MaterialsValue addPowModulePower()
    {
        TextInputDialog dialog = new TextInputDialog();
        MaterialsValue value = null;
        int valueId = -1;
        dialog.setTitle("Добавить мощность");
        dialog.setHeaderText("Введите значение");
        dialog.setContentText("Значение: ");
        dialog.graphicProperty().set(null);
        dialog.getEditor().textProperty().addListener(
                MaterialsForm.getChangeListener(dialog.getEditor()));
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().isEmpty())
        {
            valueId = MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allPowersList());
            if (valueId != -1 && Finder.getBody(valueId) != null)
            {
                value = Finder.getPower(valueId);
                final int indexInArrayAll = Finder.get_allPowersList().indexOf(value);
                if (value != null)
                {
                    value.set_active(true);
                    if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.POWERS_TABLE, value))
                    {
                        _activePowersList.add(value);
                        Finder.get_allPowersList().set(indexInArrayAll, value);
                    }
                }
            } else
            {
                value = new MaterialsValue();
                value.set_name(result.get());
                if (DataBaseStorehouse.addMaterialsValue(value.get_name(), DataBaseStorehouse.POWERS_TABLE))
                {
                    value.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POWERS_TABLE));
                    Finder.get_allPowersList().add(value);
                    _activePowersList.add(value);
                }
            }
        }

        return value;
    }
}
