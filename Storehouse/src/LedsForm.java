import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

public class LedsForm
{
    private static Stage _ledsFormStage;
    private BorderPane _ledsFormBorderPane;
    private Scene _ledsFormScene;
    private VBox _centerVBox;
    static Accordion _accordion;
    //static ArrayList<Led> _ledArrayList;
    static ArrayList<LedAccounting> _ledAccountingsList;
    //static ArrayList<MaterialsValue> _allLedKindsList;
    static ArrayList<MaterialsValue> _activeLedKindsList;
    private static TableView<LedAccounting> _ledAccountingsTableView;
    private static TextField _lumFluxTextField;
    private static TextField _powerTextField;
    private static TextField _colorTextField;
    private static final RadioButton _dollarsRBtn = new RadioButton("В долларах");
    private static final RadioButton _rublesRBtn = new RadioButton("В рублях");

    LedsForm()
    {
        //_ledArrayList = new ArrayList<>(DataBaseStorehouse.getLedsArrayList());
        _ledAccountingsList = new ArrayList<>(DataBaseStorehouse.getLedAccountingList());
        //_allLedKindsList = new ArrayList<>(DataBaseStorehouse.getMaterialsValuesList(DataBaseStorehouse.LED_KINDS_TABLE));
        _activeLedKindsList = new ArrayList<>();
        for (MaterialsValue value : Finder.get_allLedKindsList())
            if (value.is_active())
                _activeLedKindsList.add(value);
    }

    void showAndWait(Stage primaryStage)
    {
        _ledsFormStage = new Stage();
        _ledsFormBorderPane = new BorderPane();
        _ledsFormScene = new Scene(_ledsFormBorderPane, 900, 800);

        _ledsFormBorderPane.setCenter(getCenter());
        _ledsFormBorderPane.setBottom(getBottom());

        _ledsFormStage.initModality(Modality.WINDOW_MODAL);
        _ledsFormStage.initOwner(primaryStage);
        _ledsFormStage.setTitle("Светодиоды");
        _ledsFormStage.getIcons().add(MainInterface.getIconLogo());
        _ledsFormStage.setScene(_ledsFormScene);
        _ledsFormStage.showAndWait();
    }

    private VBox getCenter()
    {
        _centerVBox = new VBox();
        _accordion = new Accordion();
        _ledAccountingsTableView = new TableView<>();
        ToggleGroup currencyToggleGroup = new ToggleGroup();
        HBox topHBox = new HBox();
        Button ledKindsButton = new Button("Виды светодиодов");
        VBox accountingsVBox = new VBox();
        HBox accountingInfoHBox = new HBox();
        TitledPane accountingsTableTitledPane = new TitledPane();
        TitledPane lumFluxTitledPane = new TitledPane();
        TitledPane powerTitledPane = new TitledPane();
        TitledPane colorTitledPane = new TitledPane();

        _dollarsRBtn.setToggleGroup(currencyToggleGroup);
        _rublesRBtn.setToggleGroup(currencyToggleGroup);
        _rublesRBtn.setSelected(true);

        _dollarsRBtn.setPadding(new Insets(0,0,0,20));

        _rublesRBtn.setOnAction(event -> setPriceColumns());
        _dollarsRBtn.setOnAction(event -> setPriceColumns());

        topHBox.getChildren().addAll(ledKindsButton, _dollarsRBtn, _rublesRBtn);
        topHBox.setSpacing(10);
        topHBox.setAlignment(Pos.CENTER);
        topHBox.setPadding(new Insets(5));

        setAccordion();
        setLedAccountingsTableView();
        setTextFields();

        lumFluxTitledPane.setText("Сила светового потока (лм)");
        lumFluxTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        lumFluxTitledPane.setCollapsible(false);
        lumFluxTitledPane.setExpanded(true);
        lumFluxTitledPane.setContent(_lumFluxTextField);

        powerTitledPane.setText("Мощность (Вт)");
        powerTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        powerTitledPane.setCollapsible(false);
        powerTitledPane.setExpanded(true);
        powerTitledPane.setContent(_powerTextField);

        colorTitledPane.setText("Цвет");
        colorTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        colorTitledPane.setCollapsible(false);
        colorTitledPane.setExpanded(true);
        colorTitledPane.setContent(_colorTextField);

        accountingInfoHBox.setSpacing(10);
        accountingInfoHBox.setAlignment(Pos.CENTER);
        accountingInfoHBox.getChildren().addAll(lumFluxTitledPane, powerTitledPane, colorTitledPane);

        accountingsVBox.setSpacing(10);
        accountingsVBox.getChildren().addAll(accountingInfoHBox, _ledAccountingsTableView);

        accountingsTableTitledPane.setText("Учёт светодиодов");
        accountingsTableTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        accountingsTableTitledPane.setCollapsible(false);
        accountingsTableTitledPane.setExpanded(true);
        accountingsTableTitledPane.setContent(accountingsVBox);

        ledKindsButton.setOnAction( event -> ledKindsDialog());

        _centerVBox.setSpacing(10);
        _centerVBox.setPadding(new Insets(15));
        _centerVBox.setMaxHeight(800);
        _centerVBox.setStyle("-fx-background-color: #f0f8ff");
        _centerVBox.setAlignment(Pos.TOP_CENTER);
        _centerVBox.getChildren().addAll(topHBox, _accordion, new Separator(), accountingsTableTitledPane);

        return _centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button closeBtn = new Button("Закрыть");

        closeBtn.setOnAction(event -> _ledsFormStage.close());

        bottomAnchorPane.getChildren().addAll(closeBtn);
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        AnchorPane.setBottomAnchor(closeBtn, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private void setAccordion()
    {
        for (MaterialsValue kind : _activeLedKindsList)
        {
            TitledPane kindTitledPane = new TitledPane();
            kindTitledPane.setText(kind.get_name());
            kindTitledPane.setUserData(kind.get_id());

            TableView<Led> ledsOfKindTableView = new TableView<>();
            kindTitledPane.setContent(ledsOfKindTableView);
            setLedsTableView(ledsOfKindTableView, kind.get_id());
            if (!ledsOfKindTableView.getItems().isEmpty() || kind.is_active())
                _accordion.getPanes().add(kindTitledPane);
        }
    }

    private static void setLedsTableView(TableView<Led> tableView, final int kindId)
    {
        TableColumn<Led, String> nameCol = new TableColumn<>("Название");
        TableColumn<Led, Integer> luminousFluxCol = new TableColumn<>("Сила светового потока (лм)");
        TableColumn<Led, Float> powerCol = new TableColumn<>("Мощность (Вт)");
        TableColumn<Led, String> colorCol = new TableColumn<>("Цвет");
        TableColumn<Led, Double> priceInDollCol = new TableColumn<>("Цена ($)");
        TableColumn<Led, Double> priceInRubCol = new TableColumn<>("Цена (руб.)");
        TableColumn<Led, Integer> quantityCol = new TableColumn<>("В наличии");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("_name"));
        nameCol.setCellFactory(tc ->
        {
            TableCell<Led, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(nameCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        luminousFluxCol.setCellValueFactory(new PropertyValueFactory<>("_luminousFlux"));
        luminousFluxCol.setStyle("-fx-alignment: CENTER;");

        powerCol.setCellValueFactory(new PropertyValueFactory<>("_power"));
        powerCol.setStyle("-fx-alignment: CENTER;");

        colorCol.setCellValueFactory(new PropertyValueFactory<>("_color"));
        colorCol.setStyle("-fx-alignment: CENTER;");

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

        for (Led led : Finder.get_ledArrayList())
            if (led.isActive() && led.get_kind() == kindId)
                tableView.getItems().add(led);

        tableView.setPlaceholder(new Text("Данные отсутствуют"));
        tableView.getColumns().addAll(nameCol, luminousFluxCol, powerCol, colorCol, priceInDollCol, priceInRubCol, quantityCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setContextMenu(getLedsContextMenu(tableView));
    }

    private void setLedAccountingsTableView()
    {
        TableColumn<LedAccounting, LocalDateTime> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_dateTime"));
        dateCol.setCellFactory(tc -> new TableCell<LedAccounting, LocalDateTime>()
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

        TableColumn<LedAccounting, Integer> ledCol = new TableColumn<>("Светодиод");
        ledCol.setStyle("-fx-alignment: CENTER;");
        ledCol.setCellValueFactory(new PropertyValueFactory<>("_ledId"));
        ledCol.setCellFactory(tc -> new TableCell<LedAccounting, Integer>()
        {
            @Override
            protected void updateItem(final Integer ledId, boolean empty)
            {
                super.updateItem(ledId, empty);
                if(empty)
                    setText(null);
                else
                {
                    Led led = Finder.getLed(ledId);
                    String name = led.get_name();
                    setText(name);
                }
            }
        });

        TableColumn<LedAccounting, Integer> ledKindCol = new TableColumn<>("Вид");
        ledKindCol.setStyle("-fx-alignment: CENTER;");
        ledKindCol.setCellValueFactory(new PropertyValueFactory<>("_ledId"));
        ledKindCol.setCellFactory(tc -> new TableCell<LedAccounting, Integer>()
        {
            @Override
            protected void updateItem(final Integer ledId, boolean empty)
            {
                super.updateItem(ledId, empty);
                if(empty)
                    setText(null);
                else
                {
                    Led led = Finder.getLed(ledId);
                    String kind = "";
                    if (Finder.getLedKind(led.get_kind()) != null)
                        kind = Finder.getLedKind(led.get_kind()).get_name();
                    setText(kind);
                }
            }
        });

        TableColumn<LedAccounting, Integer> accountCol = new TableColumn<>("Аккаунт");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("_accountId"));
        accountCol.setStyle("-fx-alignment: CENTER;");
        accountCol.setCellFactory(tc -> new TableCell<LedAccounting, Integer>()
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

        TableColumn<LedAccounting, String> procedureCol = new TableColumn<>("Процедура");
        procedureCol.setStyle("-fx-alignment: CENTER;");
        procedureCol.setCellValueFactory(new PropertyValueFactory<>("_procedure"));

        TableColumn<LedAccounting, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));

        TableColumn<LedAccounting, String> remarkCol = new TableColumn<>("Примечание");
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("_remark"));
        remarkCol.setCellFactory(tc ->
        {
            TableCell<LedAccounting, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(remarkCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });

        _ledAccountingsTableView.getColumns().addAll(
                dateCol,
                ledCol,
                ledKindCol,
                accountCol,
                procedureCol,
                quantityCol,
                remarkCol);

        _ledAccountingsTableView.setContextMenu(getAccountingContextMenu());
        _ledAccountingsTableView.setStyle("");
        _ledAccountingsTableView.setEditable(false);
        _ledAccountingsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _ledAccountingsTableView.setRowFactory(new Callback<TableView<LedAccounting>, TableRow<LedAccounting>>()
        {
            @Override
            public TableRow<LedAccounting> call(TableView<LedAccounting> tableView)
            {
                TableRow<LedAccounting> row = new TableRow<LedAccounting>()
                {
                    @Override
                    protected void updateItem(LedAccounting accounting, boolean empty)
                    {
                        super.updateItem(accounting, empty);
                        this.setFocused(true);
                        if(!empty)
                        {
                            if (accounting != null && accounting.get_procedure().equals("Приход"))
                            {
                                this.setStyle("-fx-background-color: #AFEEEE;");
                            } else if (accounting != null && accounting.get_procedure().equals("Расход"))
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
                        clickOnRowAccounting(Finder.getLed(row.getItem().get_ledId()));
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


        _ledAccountingsTableView.setPlaceholder(new Text("Данные отсутствуют"));
        _ledAccountingsTableView.setItems(FXCollections.observableArrayList(_ledAccountingsList));
        _ledAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        _ledAccountingsTableView.scrollTo(_ledAccountingsTableView.getItems().size()-1);
    }

    private void setTextFields()
    {
        _lumFluxTextField = new TextField();
        _powerTextField = new TextField();
        _colorTextField = new TextField();

        _lumFluxTextField.setEditable(false);
        _powerTextField.setEditable(false);
        _colorTextField.setEditable(false);
    }

    private void setPriceColumns()
    {
        for (TitledPane pane : _accordion.getPanes())
        {
            TableView<Led> tableView = (TableView<Led>)pane.getContent();
            if (tableView.getColumns().get(4).isVisible())
            {
                tableView.getColumns().get(4).setVisible(false);
                tableView.getColumns().get(5).setVisible(true);
            } else
            {
                tableView.getColumns().get(4).setVisible(true);
                tableView.getColumns().get(5).setVisible(false);
            }
        }
    }

    private static void clearTextFields()
    {
        _lumFluxTextField.clear();
        _powerTextField.clear();
        _colorTextField.clear();
    }

    private static ContextMenu getLedsContextMenu(TableView<Led> tableView)
    {
        ContextMenu ledsContextMenu = new ContextMenu();
        MenuItem addLedMenuItem = new MenuItem("Добавить");
        MenuItem editLedMenuItem = new MenuItem("Редактировать");
        MenuItem removeLedMenuItem = new MenuItem("Удалить");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        Menu accountingMenu = new Menu("Операция учёта");
        MenuItem consumptionMenuItem = new MenuItem("Расход");
        MenuItem incomingMenuItem = new MenuItem("Приход");
        MenuItem addRequestItem = new MenuItem("Создать заявку");

        accountingMenu.getItems().addAll(consumptionMenuItem, incomingMenuItem);

        addLedMenuItem.setOnAction(event ->
        {
            LedsDialog ledsDialog = new LedsDialog(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString()));
            /*
            if (_dollarsRBtn.isSelected())
                ledsDialog._dollarRBtn.setSelected(true);
            else if (_rublesRBtn.isSelected())
                ledsDialog._rubleRBtn.setSelected(true);
             */
            ledsDialog.set_ledKindComboBox2(_activeLedKindsList);
            ledsDialog.show(_ledsFormStage);
            if(ledsDialog.is_ok())
            {
                final Led led = ledsDialog.get_led();
                Finder.get_ledArrayList().add(led);
                if (led.get_kind() == Integer.parseInt(_accordion.getExpandedPane().getUserData().toString()))
                    tableView.getItems().add(led);
                else
                {
                    for (TitledPane pane : _accordion.getPanes())
                    {
                        if (led.get_kind() == Integer.parseInt(pane.getUserData().toString()))
                        {
                            TableView<Led> tableViewInPane = (TableView<Led>)pane.getContent();
                            tableViewInPane.getItems().add(led);
                            break;
                        }
                    }
                }

                if(led.get_quantity() > 0)
                {
                    LedAccounting accounting = new LedAccounting();
                    accounting.set_ledId(led.get_id());
                    accounting.set_accountId(MainInterface.get_currentAccount());
                    accounting.set_quantity(led.get_quantity());
                    accounting.set_procedure(MaterialsForm.INCOMING);
                    accounting.set_dateTime(LocalDateTime.now());
                    if (DataBaseStorehouse.addLedAccounting(accounting))
                    {
                        accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.LED_ACCOUNTINGS_TABLE));
                        _ledAccountingsList.add(accounting);
                        _ledAccountingsTableView.getItems().add(accounting);
                        _ledAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                        clearTextFields();
                    }
                }
            }
        });

        editLedMenuItem.setOnAction(event ->
        {
            if(tableView.getSelectionModel().getSelectedItem() != null)
            {
                Led editableLed = tableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_ledArrayList().indexOf(editableLed);
                final int indexInTableView = tableView.getSelectionModel().getSelectedIndex();
                LedsDialog ledsDialog = new LedsDialog(editableLed);
                /*
                if (_dollarsRBtn.isSelected())
                    ledsDialog._dollarRBtn.setSelected(true);
                else if (_rublesRBtn.isSelected())
                    ledsDialog._rubleRBtn.setSelected(true);
                 */
                ledsDialog.set_ledKindComboBox2(_activeLedKindsList);
                ledsDialog.show(_ledsFormStage);
                if (ledsDialog.is_ok())
                {
                    final Led editedLed = ledsDialog.get_led();
                    Finder.get_ledArrayList().set(indexInArray, editedLed);
                    if (editedLed.get_kind() == Integer.parseInt(_accordion.getExpandedPane().getUserData().toString()))
                        tableView.getItems().set(indexInTableView, editedLed);
                    else
                    {
                        tableView.getItems().remove(indexInTableView);
                        if (tableView.getItems().isEmpty() && !Finder.getLedKind(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString())).is_active())
                            _accordion.getPanes().remove(_accordion.getExpandedPane());
                        for (TitledPane pane : _accordion.getPanes())
                        {
                            if (editedLed.get_kind() == Integer.parseInt(pane.getUserData().toString()))
                            {
                                TableView<Led> tableViewInPane = (TableView<Led>)pane.getContent();
                                tableViewInPane.getItems().add(editedLed);
                                break;
                            }
                        }
                    }

                    _ledAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    clearTextFields();
                    refreshLedAccTableView();
                }
            }
        });

        removeLedMenuItem.setOnAction(event ->
        {
            if(tableView.getSelectionModel().getSelectedItem() != null &&
                    MainInterface.getAlertAskConfirmationDialog("Вы уверены что хотите удалить светодиод?"))
            {
                Led led = tableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_ledArrayList().indexOf(led);

                if (checkLedInAccountings(led.get_id()))
                {
                    led.set_active(false);
                    if (DataBaseStorehouse.editLed(led))
                    {
                        Finder.get_ledArrayList().set(indexInArray, led);
                        tableView.getItems().remove(led);
                        if (tableView.getItems().isEmpty() && !Finder.getLedKind(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString())).is_active())
                            _accordion.getPanes().remove(_accordion.getExpandedPane());
                        tableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                        clearTextFields();
                    }
                } else
                {
                    if(DataBaseStorehouse.deleteLed(led.get_id()))
                    {
                        Finder.get_ledArrayList().remove(led);
                        tableView.getItems().remove(led);
                        //if (tableView.getItems().isEmpty() && !getLedKind(Integer.parseInt(_accordion.getExpandedPane().getUserData().toString())).is_active())
                            //_accordion.getPanes().remove(_accordion.getExpandedPane());
                        tableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                        clearTextFields();
                    }
                }
            }
        });

        addRequestItem.setOnAction(event ->
        {
            Led editableLed = tableView.getSelectionModel().getSelectedItem();
            if (editableLed == null)
                return;
            RequestDialog dialog = new RequestDialog(3, editableLed.get_id());
            dialog.set_kindComboBox();
            dialog.showAndWait(_ledsFormStage);
        });

        incomingMenuItem.setOnAction(event ->
        {
            if (tableView.getSelectionModel().getSelectedItem() != null)
            {
                Led led = tableView.getSelectionModel().getSelectedItem();
                final int indexInArray = Finder.get_ledArrayList().indexOf(led);
                final int indexInTableView = tableView.getItems().indexOf(led);
                LedAccounting accounting = new LedAccounting();
                final int oldQuantity = led.get_quantity();
                final int inputAmount = addLedAmount(oldQuantity, MaterialsForm.INCOMING);
                if (inputAmount != 0)
                {
                    final int newQuantity = oldQuantity + inputAmount;
                    accounting.set_ledId(led.get_id());
                    accounting.set_accountId(MainInterface.get_currentAccount());
                    accounting.set_quantity(inputAmount);
                    accounting.set_procedure(MaterialsForm.INCOMING);
                    accounting.set_dateTime(LocalDateTime.now());
                    if (DataBaseStorehouse.addLedAccounting(accounting))
                    {
                        accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.LED_ACCOUNTINGS_TABLE));
                        _ledAccountingsList.add(accounting);
                        _ledAccountingsTableView.getItems().add(accounting);
                        led.set_quantity(newQuantity);
                        if (DataBaseStorehouse.editLed(led))
                        {
                            Finder.get_ledArrayList().set(indexInArray, led);
                            tableView.getItems().set(indexInTableView, led);
                            tableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                            clearTextFields();
                        }
                    }
                }
            }
        });

        consumptionMenuItem.setOnAction(event ->
        {
            if (tableView.getSelectionModel().getSelectedItem() != null)
                consumptionDialog(tableView);
        });

        ledsContextMenu.getItems().addAll(
                addLedMenuItem,
                editLedMenuItem,
                removeLedMenuItem,
                separator,
                accountingMenu,
                new SeparatorMenuItem(),
                addRequestItem);
        return ledsContextMenu;
    }

    private static ContextMenu getAccountingContextMenu()
    {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem addRemarkMenuItem = new MenuItem("Добавить примечание");

        addRemarkMenuItem.setOnAction(event ->
        {
            if (_ledAccountingsTableView.getSelectionModel().getSelectedItem() != null)
                addAccountingRemark();
        });

        contextMenu.getItems().addAll(addRemarkMenuItem);
        return contextMenu;
    }

    private static Integer addLedAmount(int currentQuantity, String operation)
    {
        int amount = 0;
        TextInputDialog inputAmountDialog = new TextInputDialog();
        inputAmountDialog.graphicProperty().set(null);
        inputAmountDialog.setTitle(operation);
        inputAmountDialog.setHeaderText("Введите количество светодиодов");
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
                    MainInterface.getAlertWarningDialog("Операция невозможна: введенное число превышает количество светодиодов в наличии");
                    amount = 0;
                }
            }
        }

        return amount;
    }

    private static void consumptionDialog(TableView<Led> tableView)
    {
        Led led = tableView.getSelectionModel().getSelectedItem();
        final int indexInArray = Finder.get_ledArrayList().indexOf(led);
        final int indexInTableView = tableView.getItems().indexOf(led);
        LedAccounting accounting = new LedAccounting();
        final int oldQuantity = led.get_quantity();

        AddAccountingDialog dialog = new AddAccountingDialog();
        dialog.dialogStage.setTitle(MaterialsForm.CONSUMPTION);
        dialog.addTextArea();
        dialog.addTextField();
        dialog.dialogStage.setHeight(400);
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

        dialog.showAndWait(_ledsFormStage);
        if (dialog.ok)
        {
            String remark = null;
            final int inputAmount = Integer.parseInt(dialog.textField.getText());
            if (!dialog.textArea.getText().isEmpty())
                remark = dialog.textArea.getText();
            if (inputAmount != 0)
            {
                final int newQuantity = oldQuantity - inputAmount;
                accounting.set_ledId(led.get_id());
                accounting.set_accountId(MainInterface.get_currentAccount());
                accounting.set_quantity(inputAmount);
                accounting.set_procedure(MaterialsForm.CONSUMPTION);
                accounting.set_dateTime(LocalDateTime.now());
                accounting.set_remark(remark);
                if (DataBaseStorehouse.addLedAccounting(accounting))
                {
                    accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POW_MODULE_ACCOUNTINGS_TABLE));
                    _ledAccountingsList.add(accounting);
                    _ledAccountingsTableView.getItems().add(accounting);
                    led.set_quantity(newQuantity);
                    if (DataBaseStorehouse.editLed(led))
                    {
                        Finder.get_ledArrayList().set(indexInArray, led);
                        tableView.getItems().set(indexInTableView, led);
                        tableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    }
                }
            }
        }
    }

    static void addAccountingRemark()
    {
        LedAccounting accounting = _ledAccountingsTableView.getSelectionModel().getSelectedItem();
        final int indexInArray = _ledAccountingsList.indexOf(accounting);
        final int indexInTableView = _ledAccountingsTableView.getItems().indexOf(accounting);
        AddAccountingDialog dialog = new AddAccountingDialog();
        dialog.dialogStage.setTitle("Добавить примечание");
        dialog.textAreaTitledPane.setText("Примечание");
        //dialog.textField.setVisible(false);
        //dialog.textFieldLabel.setVisible(false);
        dialog.addTextArea();
        dialog.addButton.setVisible(false);

        if (accounting.get_remark() != null)
            dialog.textArea.setText(accounting.get_remark());

        dialog.saveButton.setText("Добавить примечание");
        dialog.saveButton.setOnAction(event ->
        {
            if (!dialog.textArea.getText().isEmpty())
            {
                accounting.set_remark(dialog.textArea.getText());
                dialog.ok = true;
                dialog.dialogStage.close();
            }
        });

        dialog.showAndWait(_ledsFormStage);
        if (dialog.ok)
        {
            if (DataBaseStorehouse.editLedAccounting(accounting))
            {
                _ledAccountingsList.set(indexInArray, accounting);
                _ledAccountingsTableView.getItems().set(indexInTableView, accounting);
            }
        }
    }

    private void ledKindsDialog()
    {
        Stage ledKindsStage = new Stage();
        BorderPane ledKindsBorderPane = new BorderPane();
        VBox ledKindsCenterVBox = new VBox();
        Scene ledKindsScene = new Scene(ledKindsBorderPane);
        Button closeButton = new Button("Закрыть");
        VBox bottomVbox = new VBox();
        ContextMenu ledKindsContextMenu = new ContextMenu();
        ListView<MaterialsValue> kindsListView = new ListView<>(FXCollections.observableArrayList(_activeLedKindsList));
        AnchorPane bottomAnchorPane = new AnchorPane();
        MenuItem addKindMenuItem = new MenuItem("Добавть");
        MenuItem editKindMenuItem = new MenuItem("Редактировать");
        MenuItem removeKindMenuItem = new MenuItem("Удалить");

        addKindMenuItem.setOnAction(event ->
        {
            MaterialsValue newLedKind = addLedKind();
            if (newLedKind != null)
                kindsListView.getItems().add(newLedKind);
        });

        editKindMenuItem.setOnAction(event ->
        {
            if (kindsListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsValue editableKind = kindsListView.getSelectionModel().getSelectedItem();
                final int indexInArrayAll = Finder.get_allLedKindsList().indexOf(editableKind);
                final int indexInArrayActive = _activeLedKindsList.indexOf(editableKind);
                final int indexInListView = kindsListView.getItems().indexOf(editableKind);
                TextInputDialog dialog = new TextInputDialog();
                dialog.graphicProperty().set(null);
                Optional<String> result;
                dialog.setTitle("Редактирование: вид светодиода");
                dialog.setHeaderText("Введите название вида светодиода");
                dialog.setContentText("Вид: ");
                Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                alertStage.getIcons().add(MainInterface.getIconLogo());
                dialog.getEditor().setText(editableKind.get_name());
                result = dialog.showAndWait();
                if (result.isPresent() && !result.get().isEmpty())
                {
                    result.ifPresent(editableKind::set_name);
                    if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.LED_KINDS_TABLE, editableKind))
                    {
                        Finder.get_allLedKindsList().set(indexInArrayAll, editableKind);
                        _activeLedKindsList.set(indexInArrayActive, editableKind);
                        kindsListView.getItems().set(indexInListView, editableKind);

                        for (TitledPane pane : _accordion.getPanes())
                        {
                            if (Integer.parseInt(pane.getUserData().toString()) == editableKind.get_id())
                            {
                                pane.setText(editableKind.get_name());
                                refreshLedAccTableView();
                                break;
                            }
                        }
                    }
                }
            }
        });

        removeKindMenuItem.setOnAction(event ->
        {
            if (kindsListView.getSelectionModel().getSelectedItem() != null)
            {
                MaterialsValue removableKind = kindsListView.getSelectionModel().getSelectedItem();

                if (MainInterface.getAlertAskConfirmationDialog("Вы уверены, что хотите удалть данный вид светодиодов из списка?"))
                {
                    final int indexInArrayAll = Finder.get_allLedKindsList().indexOf(removableKind);
                    final int indexInArrayActive = _activeLedKindsList.indexOf(removableKind);
                    final int indexInListView = kindsListView.getItems().indexOf(removableKind);

                    if (checkKindInLeds(removableKind.get_id()))
                    {
                        removableKind.set_active(false);
                        if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.LED_KINDS_TABLE, removableKind))
                        {
                            Finder.get_allLedKindsList().set(indexInArrayAll, removableKind);
                            _activeLedKindsList.remove(indexInArrayActive);
                            kindsListView.getItems().remove(indexInListView);
                        }
                    } else
                    {
                        if (DataBaseStorehouse.deleteMaterialsValue(DataBaseStorehouse.LED_KINDS_TABLE, removableKind.get_id()))
                        {
                            Finder.get_allLedKindsList().remove(removableKind);
                            _activeLedKindsList.remove(removableKind);
                            kindsListView.getItems().remove(removableKind);
                            for (TitledPane pane : _accordion.getPanes())
                            {
                                if (Integer.parseInt(pane.getUserData().toString()) == removableKind.get_id())
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

        ledKindsContextMenu.getItems().addAll(addKindMenuItem, editKindMenuItem, removeKindMenuItem);
        kindsListView.setContextMenu(ledKindsContextMenu);

        ledKindsCenterVBox.setSpacing(10);
        ledKindsCenterVBox.setPadding(new Insets(15));
        ledKindsCenterVBox.setStyle("-fx-background-color: #f0f8ff");
        ledKindsCenterVBox.setAlignment(Pos.TOP_CENTER);
        ledKindsCenterVBox.getChildren().add(kindsListView);

        closeButton.setOnAction(event ->  ledKindsStage.close());

        bottomAnchorPane.getChildren().addAll(closeButton);
        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        bottomVbox.getChildren().addAll(new Separator(), bottomAnchorPane);

        ledKindsBorderPane.setCenter(ledKindsCenterVBox);
        ledKindsBorderPane.setBottom(bottomVbox);

        ledKindsStage.setTitle("Виды светодиодов");
        ledKindsStage.initModality(Modality.WINDOW_MODAL);
        ledKindsStage.initOwner(_ledsFormStage);
        ledKindsStage.getIcons().add(MainInterface.getIconLogo());
        ledKindsStage.setScene(ledKindsScene);
        ledKindsStage.showAndWait();
    }

    private void clickOnRowAccounting(Led led)
    {
        clearTextFields();
        if (led.get_luminousFlux() != 0)
            _lumFluxTextField.setText(String.valueOf(led.get_luminousFlux()));
        if (led.get_power() != 0)
            _powerTextField.setText(String.valueOf(led.get_power()));
        if (led.get_color() != null)
            _colorTextField.setText(led.get_color());
    }

    private static boolean checkLedInAccountings(final int ledId)
    {
        for (LedAccounting accounting : _ledAccountingsList)
            if (accounting.get_ledId() == ledId)
                return true;

        return false;
    }

    private boolean checkKindInLeds(final int kindId)
    {
        for (Led led : Finder.get_ledArrayList())
            if (led.get_kind() == kindId)
                return true;

        return false;
    }

    static MaterialsValue addLedKind()
    {
        TextInputDialog dialog = new TextInputDialog();
        MaterialsValue value = null;
        int valueId = -1;
        dialog.graphicProperty().set(null);
        dialog.setTitle("Новый вид светодиода");
        dialog.setHeaderText("Введите название вида");
        dialog.setContentText("Вид:");
        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && !result.get().isEmpty())
        {
            valueId = MaterialsForm.checkNameOfNewMaterialsValue(result.get(), Finder.get_allLedKindsList());
            if (valueId != -1 && Finder.getLedKind(valueId) != null)
            {
                System.out.println("valueId != -1 and getLedKind(valueId) != null");
                value = Finder.getLedKind(valueId);
                final int indexInArrayAll = Finder.get_allLedKindsList().indexOf(value);
                if (value != null)
                {
                    value.set_active(true);
                    if (DataBaseStorehouse.editMaterialsValue(DataBaseStorehouse.LED_KINDS_TABLE, value))
                    {
                        _activeLedKindsList.add(value);
                        Finder.get_allLedKindsList().set(indexInArrayAll, value);
                    }
                }
            }
            else
            {
                System.out.println("valueId == -1 and getLedKind(valueId) == null");
                value = new MaterialsValue();
                value.set_name(result.get());
                if (DataBaseStorehouse.addMaterialsValue(value.get_name(), DataBaseStorehouse.LED_KINDS_TABLE))
                {
                    value.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.LED_KINDS_TABLE));
                    System.out.println("value id = " + value.get_id());
                    Finder.get_allLedKindsList().add(value);
                    _activeLedKindsList.add(value);
                }
            }

            TitledPane kindTitledPane = new TitledPane();
            kindTitledPane.setText(value.get_name());
            kindTitledPane.setUserData(value.get_id());

            TableView<Led> ledsOfKindTableView = new TableView<>();
            kindTitledPane.setContent(ledsOfKindTableView);
            setLedsTableView(ledsOfKindTableView, value.get_id());
            _accordion.getPanes().add(kindTitledPane);
        }

        return value;
    }

    private static void refreshLedAccTableView()
    {
        _ledAccountingsTableView.getItems().clear();
        _ledAccountingsTableView.getItems().addAll(FXCollections.observableArrayList(_ledAccountingsList));
        _ledAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
