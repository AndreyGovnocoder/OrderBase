import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
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
import java.util.Properties;

public class PolygraphyForm
{
    private Stage _polygraphyFormStage;
    private BorderPane _mainBorderPane;
    private TableView<Polygraphy> _polygraphyTableView;
    private TableView<PolygraphyAccounting> _polygraphyAccountingsTableView;
    //static ArrayList<Polygraphy> _polygraphyList;
    static ArrayList<PolygraphyAccounting> _polygraphyAccountingsList;
    private ContextMenu _contextMenu;
    private MenuItem _addPolygraphyMenuItem;
    private MenuItem _editPolygraphyMenuItem;
    private MenuItem _removePolygraphyMenuItem;
    private Menu _accountingMenu;
    public static final String CONSUMPTION = "Расход";
    public static final String INCOMING = "Приход";
    private TextArea _descriptionTextArea = new TextArea();

    PolygraphyForm()
    {
        //_polygraphyList = DataBaseStorehouse.getPolygraphyArrayList();
        _polygraphyTableView = new TableView<>();
        _polygraphyAccountingsList = DataBaseStorehouse.getPolygraphyAccountingList();
        _polygraphyAccountingsTableView = new TableView<>(FXCollections.observableArrayList(_polygraphyAccountingsList));
        setPolygraphyTableView();
        setPolygraphyAccountingsTableView();
        setContextMenu();
    }

    void showAndWait(Stage primaryStage)
    {
        _polygraphyFormStage = new Stage();
        _mainBorderPane = new BorderPane();
        Scene polygraphyFormScene = new Scene(_mainBorderPane, 675, 800);

        _mainBorderPane.setCenter(getCenter());
        _mainBorderPane.setBottom(getBottom());

        _polygraphyFormStage.initModality(Modality.WINDOW_MODAL);
        _polygraphyFormStage.initOwner(primaryStage);
        _polygraphyFormStage.setScene(polygraphyFormScene);
        _polygraphyFormStage.setTitle("Полиграфия");
        _polygraphyFormStage.getIcons().add(MainInterface.getIconLogo());
        _polygraphyFormStage.setOnCloseRequest(event ->
        {
            savePolygraphyTableColsWidth();
            savePolygraphyTableAccountingsColsWidth();
            savePolygraphyStageSize(_polygraphyFormStage);
        });
        loadPolygraphyStageSize(_polygraphyFormStage);
        _polygraphyFormStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        TitledPane descriptionTitledPane = new TitledPane();
        TitledPane accountingsTitledPane = new TitledPane();

        _descriptionTextArea.setWrapText(true);
        _descriptionTextArea.setEditable(false);

        descriptionTitledPane.setText("Описание полиграфии");
        descriptionTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        descriptionTitledPane.setExpanded(true);
        descriptionTitledPane.setCollapsible(false);
        descriptionTitledPane.setContent(_descriptionTextArea);

        accountingsTitledPane.setText("Учёт полиграфии");
        accountingsTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        accountingsTitledPane.setExpanded(true);
        accountingsTitledPane.setCollapsible(false);
        accountingsTitledPane.setContent(_polygraphyAccountingsTableView);

        centerVBox.setPadding(new Insets(15));
        centerVBox.setSpacing(15);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(_polygraphyTableView, descriptionTitledPane, accountingsTitledPane);

        return centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button closeButton = new Button("Закрыть");

        closeButton.setPrefWidth(80);
        closeButton.setOnAction(event ->
        {
            savePolygraphyTableColsWidth();
            savePolygraphyTableAccountingsColsWidth();
            savePolygraphyStageSize(_polygraphyFormStage);
            _polygraphyFormStage.close();
        });

        bottomAnchorPane.getChildren().addAll(closeButton);
        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private void setPolygraphyTableView()
    {
        TableColumn<Polygraphy, String> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("_name"));
        nameCol.setStyle("-fx-alignment: " + Pos.CENTER_LEFT +";");

        TableColumn<Polygraphy, Integer> priceCol = new TableColumn<>("Цена");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("_price"));
        priceCol.setStyle("-fx-alignment: CENTER;");

        TableColumn<Polygraphy, Integer> quantityCol = new TableColumn<>("В наличии");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");

        _polygraphyTableView.setPlaceholder(new Text("Полиграфия отсутствует"));
        //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        _polygraphyTableView.getColumns().addAll(
                nameCol,
                priceCol,
                quantityCol);

        _polygraphyTableView.setContextMenu(_contextMenu);
        _polygraphyTableView.setOnMouseClicked(event ->
        {
            System.out.println("click");
            if(event.getButton() == MouseButton.SECONDARY)
            {
                if(_polygraphyTableView.getItems().isEmpty())
                {
                    System.out.println("items is empty");
                    _editPolygraphyMenuItem.setDisable(true);
                    _removePolygraphyMenuItem.setDisable(true);
                    _accountingMenu.setDisable(true);
                    _contextMenu.show(_polygraphyTableView, event.getScreenX() + 10, event.getScreenY() + 5);
                } else
                {
                    System.out.println("item is not empty");
                    _contextMenu.show(_polygraphyTableView, 0,0);
                    _contextMenu.hide();
                }
            }
        });

        _polygraphyTableView.setRowFactory(new Callback<>()
        {
            @Override
            public TableRow<Polygraphy> call(TableView<Polygraphy> tableView)
            {
                TableRow<Polygraphy> row = new TableRow<Polygraphy>();

                row.setOnMouseClicked(event ->
                {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1)
                    {
                        clickOnPolygraphy();
                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY))
                    {
                        row.setOnContextMenuRequested(event1 ->
                        {
                            clickOnPolygraphy();
                            if (row.getItem() != null)
                            {
                                System.out.println("row.getItem() != null");
                                _contextMenu.getItems().get(1).setDisable(false);
                                _contextMenu.getItems().get(2).setDisable(false);
                                _contextMenu.getItems().get(4).setDisable(false);
                                _accountingMenu.getItems().get(0).setDisable(row.getItem().get_quantity() == 0);
                            } else
                            {
                                _editPolygraphyMenuItem.setDisable(true);
                                _removePolygraphyMenuItem.setDisable(true);
                                _accountingMenu.setDisable(true);
                            }
                            _contextMenu.show(row, event1.getScreenX() + 10, event1.getScreenY() + 5);
                        });
                    } else if (row.isEmpty() && event.getButton() == MouseButton.SECONDARY)
                    {
                        row.setOnContextMenuRequested(event1 ->
                        {
                            System.out.println("row is empty");
                            _editPolygraphyMenuItem.setDisable(true);
                            _removePolygraphyMenuItem.setDisable(true);
                            _accountingMenu.setDisable(true);
                            _contextMenu.show(row, event1.getScreenX() + 10, event1.getScreenY() + 5);
                        });
                    }
                });
                return row;
            }
        });

        for (Polygraphy polygraphy : Finder.get_polygraphyList())
            if( polygraphy.isActive())
                _polygraphyTableView.getItems().add(polygraphy);

        loadPolygraphyTableColsWidth();
    }

    private void setPolygraphyAccountingsTableView()
    {
        TableColumn<PolygraphyAccounting, LocalDateTime> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_dateTime"));
        dateCol.setCellFactory(tc -> new TableCell<PolygraphyAccounting, LocalDateTime>()
        {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty)
            {
                super.updateItem(date, empty);
                if (empty)
                {
                    setText(null);
                }
                else
                {
                    String dateTime = MainInterface._formatter.format(date) + " " +
                            MainInterface._formatterTime.format(date);
                    setText(dateTime);
                }
            }
        });

        TableColumn<PolygraphyAccounting, Integer> polygraphyCol = new TableColumn<>("Полиграфия");
        polygraphyCol.setStyle("-fx-alignment: CENTER;");
        polygraphyCol.setCellValueFactory(new PropertyValueFactory<>("_polygraphyId"));
        polygraphyCol.setCellFactory(tc -> new TableCell<PolygraphyAccounting, Integer>()
        {
            @Override
            protected void updateItem(Integer polygraphyId, boolean empty)
            {
                super.updateItem(polygraphyId, empty);
                if(empty)
                    setText(null);
                else
                {
                    Polygraphy polygraphy = Finder.getPolygraphy(polygraphyId);
                    assert polygraphy != null;
                    String name = polygraphy.get_name();
                    setText(name);
                }
            }
        });

        TableColumn<PolygraphyAccounting, Integer> accountCol = new TableColumn<>("Аккаунт");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("_accountId"));
        accountCol.setStyle("-fx-alignment: CENTER;");
        accountCol.setCellFactory(tc -> new TableCell<PolygraphyAccounting, Integer>()
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

        TableColumn<PolygraphyAccounting, String> procedureCol = new TableColumn<>("Процедура");
        procedureCol.setStyle("-fx-alignment: CENTER;");
        procedureCol.setCellValueFactory(new PropertyValueFactory<>("_procedure"));

        TableColumn<PolygraphyAccounting, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));

        _polygraphyAccountingsTableView.getColumns().addAll(
                dateCol,
                polygraphyCol,
                accountCol,
                procedureCol,
                quantityCol);

        _polygraphyAccountingsTableView.setStyle("");
        _polygraphyAccountingsTableView.setEditable(false);
        _polygraphyAccountingsTableView.setSelectionModel(null);

        _polygraphyAccountingsTableView.setRowFactory(new Callback<TableView<PolygraphyAccounting>, TableRow<PolygraphyAccounting>>()
        {
            @Override
            public TableRow<PolygraphyAccounting> call(TableView<PolygraphyAccounting> tableView)
            {
                return new TableRow<PolygraphyAccounting>()
                {
                    @Override
                    protected void updateItem(PolygraphyAccounting accounting, boolean empty)
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
            }
        });


        _polygraphyAccountingsTableView.setPlaceholder(new Text("Данные отсутствуют"));
        _polygraphyAccountingsTableView.setItems(FXCollections.observableArrayList(_polygraphyAccountingsList));
        //_polygraphyAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        _polygraphyAccountingsTableView.scrollTo(_polygraphyAccountingsTableView.getItems().size()-1);
        loadPolygraphyTableAccountingsColsWidth();
    }

    private void setContextMenu()
    {
        _contextMenu = new ContextMenu();
        _addPolygraphyMenuItem = new MenuItem("Добавить полиграфию");
        _editPolygraphyMenuItem = new MenuItem("Редактировать полиграфию");
        _removePolygraphyMenuItem = new MenuItem("Удалить полиграфию");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        _accountingMenu = new Menu("Операция учёта");
        MenuItem consumptionMenuItem = new MenuItem("Расход");
        MenuItem incomingMenuItem = new MenuItem("Приход");
        MenuItem addRequestItem = new MenuItem("Создать заявку");

        _addPolygraphyMenuItem.setOnAction(event ->
        {
            PolygraphyDialog polygraphyDialog = new PolygraphyDialog();
            polygraphyDialog.showAndWait(_polygraphyFormStage);
            if(polygraphyDialog.isOk())
            {
                Polygraphy polygraphy = polygraphyDialog.get_polygraphy();
                if(DataBaseStorehouse.addPolygraphy(polygraphy))
                {
                    polygraphy.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POLYGRAPHY_TABLE));
                    polygraphy.set_active(true);
                    Finder.get_polygraphyList().add(polygraphy);
                    _polygraphyTableView.getItems().add(polygraphy);
                    _polygraphyTableView.scrollTo(polygraphy);

                    if (polygraphy.get_quantity() > 0)
                    {
                        PolygraphyAccounting accounting = new PolygraphyAccounting();
                        accounting.set_polygraphyId(polygraphy.get_id());
                        accounting.set_accountId(MainInterface.get_currentAccount());
                        accounting.set_quantity(polygraphy.get_quantity());
                        accounting.set_procedure(INCOMING);
                        accounting.set_dateTime(LocalDateTime.now());
                        if(DataBaseStorehouse.addPolygraphyAccounting(accounting))
                        {
                            accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POLYGRAPHY_ACCOUNTINGS_TABLE));
                            _polygraphyAccountingsList.add(accounting);
                            _polygraphyAccountingsTableView.getItems().add(accounting);
                            //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                            _descriptionTextArea.clear();
                        }
                    }
                }
            }
        });

        _editPolygraphyMenuItem.setOnAction(event ->
        {
            if(_polygraphyTableView.getSelectionModel().getSelectedItem() != null)
            {
                Polygraphy polygraphy = _polygraphyTableView.getSelectionModel().getSelectedItem();
                int indexInArray = Finder.get_polygraphyList().indexOf(polygraphy);
                int indexInTableView = _polygraphyTableView.getItems().indexOf(polygraphy);
                PolygraphyDialog polygraphyDialog = new PolygraphyDialog(polygraphy);
                polygraphyDialog.showAndWait(_polygraphyFormStage);
                if(polygraphyDialog.isOk() && DataBaseStorehouse.editPolygraphy(polygraphy))
                {
                    Finder.get_polygraphyList().set(indexInArray, polygraphyDialog.get_polygraphy());
                    _polygraphyTableView.getItems().set(indexInTableView, polygraphyDialog.get_polygraphy());
                    //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                    refreshPolygraphyAccountingsTableView();
                    _descriptionTextArea.clear();
                }
            }
        });

        _removePolygraphyMenuItem.setOnAction(event ->
        {
            if (MainInterface.getAlertAskConfirmationDialog("Вы уверены что хотите удалить полиграфию?"))
            {
                if(_polygraphyTableView.getSelectionModel().getSelectedItem() != null)
                {
                    Polygraphy polygraphy = _polygraphyTableView.getSelectionModel().getSelectedItem();
                    int indexInArray = Finder.get_polygraphyList().indexOf(polygraphy);
                    if(checkPolygraphyInAccountings(polygraphy.get_id()))
                    {
                        polygraphy.set_active(false);
                        if(DataBaseStorehouse.editPolygraphy(polygraphy))
                        {
                            Finder.get_polygraphyList().set(indexInArray, polygraphy);
                            _polygraphyTableView.getItems().remove(polygraphy);
                            //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                            _descriptionTextArea.clear();
                        }
                    } else
                    {
                        if(DataBaseStorehouse.deletePolygraphy(polygraphy.get_id()))
                        {
                            Finder.get_polygraphyList().remove(polygraphy);
                            _polygraphyTableView.getItems().remove(polygraphy);
                            //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                            _descriptionTextArea.clear();
                        }
                    }
                }
            }
        });

        addRequestItem.setOnAction(event ->
        {
            Polygraphy selectedPolygraphy = _polygraphyTableView.getSelectionModel().getSelectedItem();
            if (selectedPolygraphy == null)
                return;
            RequestDialog dialog = new RequestDialog(5, selectedPolygraphy.get_id());
            dialog.set_kindComboBox();
            dialog.showAndWait(_polygraphyFormStage);
        });

        incomingMenuItem.setOnAction(event ->
        {
            if(_polygraphyTableView.getSelectionModel().getSelectedItem() != null)
            {
                Polygraphy polygraphy = _polygraphyTableView.getSelectionModel().getSelectedItem();
                int indexInArray = Finder.get_polygraphyList().indexOf(polygraphy);
                int indexInTableView = _polygraphyTableView.getItems().indexOf(polygraphy);
                PolygraphyAccounting accounting = new PolygraphyAccounting();
                int oldQuantity = polygraphy.get_quantity();
                int inputAmount = addPolygraphyAmount(polygraphy.get_quantity(), INCOMING);
                if (inputAmount != 0)
                {
                    int newQuantity = oldQuantity + inputAmount;
                    accounting.set_polygraphyId(polygraphy.get_id());
                    accounting.set_accountId(MainInterface.get_currentAccount());
                    accounting.set_quantity(inputAmount);
                    accounting.set_procedure(INCOMING);
                    accounting.set_dateTime(LocalDateTime.now());
                    if(DataBaseStorehouse.addPolygraphyAccounting(accounting))
                    {
                        accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POLYGRAPHY_ACCOUNTINGS_TABLE));
                        _polygraphyAccountingsList.add(accounting);
                        _polygraphyAccountingsTableView.getItems().add(accounting);
                        polygraphy.set_quantity(newQuantity);
                        if(DataBaseStorehouse.editPolygraphy(polygraphy))
                        {
                            Finder.get_polygraphyList().set(indexInArray, polygraphy);
                            _polygraphyTableView.getItems().set(indexInTableView, polygraphy);
                            //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                        }
                    }
                }
            }
        });

        consumptionMenuItem.setOnAction(event ->
        {
            if(_polygraphyTableView.getSelectionModel().getSelectedItem() != null)
            {
                Polygraphy polygraphy = _polygraphyTableView.getSelectionModel().getSelectedItem();
                int indexInArray = Finder.get_polygraphyList().indexOf(polygraphy);
                int indexInTableView = _polygraphyTableView.getItems().indexOf(polygraphy);
                PolygraphyAccounting accounting = new PolygraphyAccounting();
                int oldQuantity = polygraphy.get_quantity();
                int inputAmount = addPolygraphyAmount(polygraphy.get_quantity(), CONSUMPTION);

                if (inputAmount != 0)
                {
                    int newQuantity = oldQuantity - inputAmount;
                    accounting.set_polygraphyId(polygraphy.get_id());
                    accounting.set_accountId(MainInterface.get_currentAccount());
                    accounting.set_quantity(inputAmount);
                    accounting.set_procedure(CONSUMPTION);
                    accounting.set_dateTime(LocalDateTime.now());
                    if(DataBaseStorehouse.addPolygraphyAccounting(accounting))
                    {
                        accounting.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.POLYGRAPHY_ACCOUNTINGS_TABLE));
                        _polygraphyAccountingsList.add(accounting);
                        _polygraphyAccountingsTableView.getItems().add(accounting);
                        polygraphy.set_quantity(newQuantity);
                        if(DataBaseStorehouse.editPolygraphy(polygraphy))
                        {
                            Finder.get_polygraphyList().set(indexInArray, polygraphy);
                            _polygraphyTableView.getItems().set(indexInTableView, polygraphy);
                            //_polygraphyTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
                        }
                    }
                }
            }
        });

        _accountingMenu.getItems().addAll(consumptionMenuItem, incomingMenuItem);
        _contextMenu.getItems().addAll(
                _addPolygraphyMenuItem,
                _editPolygraphyMenuItem,
                _removePolygraphyMenuItem,
                separator,
                _accountingMenu,
                new SeparatorMenuItem(),
                addRequestItem);
    }

    private void clickOnPolygraphy()
    {
        if (_polygraphyTableView.getSelectionModel().getSelectedItem() != null)
        {

            Polygraphy polygraphy = _polygraphyTableView.getSelectionModel().getSelectedItem();
            System.out.println(polygraphy.get_name());
            if(polygraphy.get_description() != null)
                _descriptionTextArea.setText(polygraphy.get_description());
        }
    }

    private Integer addPolygraphyAmount(int currentQuantity, String operation)
    {
        int amount = 0;
        TextInputDialog inputAmountDialog = new TextInputDialog();
        inputAmountDialog.graphicProperty().set(null);
        inputAmountDialog.setTitle(operation);
        inputAmountDialog.setHeaderText("Введите количество полиграфии");
        inputAmountDialog.setContentText("Количество: ");
        Stage alertStage = (Stage) inputAmountDialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(MainInterface.getIconLogo());
        inputAmountDialog.getEditor().textProperty().addListener(
                MaterialsForm.getChangeListener(inputAmountDialog.getEditor()));
        Optional<String> optionalResult = inputAmountDialog.showAndWait();
        if(optionalResult.isPresent())
        {
            amount = Integer.parseInt(inputAmountDialog.getEditor().getText());
            if(operation.equals(CONSUMPTION))
            {
                int result = currentQuantity - amount;
                if(result < 0)
                {
                    MainInterface.getAlertWarningDialog("Операция невозможна: введенное число превышает количество полиграфии в наличии");
                    amount = 0;
                }
            }
        }

        return amount;
    }

    private boolean checkPolygraphyInAccountings(final int polygraphyId)
    {
        for (PolygraphyAccounting accounting : _polygraphyAccountingsList)
            if (accounting.get_polygraphyId() == polygraphyId)
                return true;

        return false;
    }

    private void refreshPolygraphyAccountingsTableView()
    {
        _polygraphyAccountingsTableView.getItems().clear();
        _polygraphyAccountingsTableView.getItems().addAll(FXCollections.observableArrayList(_polygraphyAccountingsList));
        //_polygraphyAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void savePolygraphyTableColsWidth()
    {
        Properties tableColumnsWidthProp =
                Finder._settings.getPropertiesTableColumsWidth("_polygraphyTableView");
        if (tableColumnsWidthProp == null)
        {
            tableColumnsWidthProp = new Properties();
            for (int i = 0; i < _polygraphyTableView.getColumns().size(); ++i)
            {
                tableColumnsWidthProp.put(String.valueOf(i), _polygraphyTableView.getColumns().get(i).getWidth());
            }
            Finder._settings.addPropertiesColWidths("_polygraphyTableView", tableColumnsWidthProp);
        }
        else
        {
            for (int i = 0; i < _polygraphyTableView.getColumns().size(); ++i)
                tableColumnsWidthProp.put(String.valueOf(i), _polygraphyTableView.getColumns().get(i).getWidth());
        }
    }

    private void loadPolygraphyTableColsWidth()
    {
        try
        {
            Properties tableProperties = Finder._settings.getPropertiesTableColumsWidth("_polygraphyTableView");
            if (tableProperties != null && tableProperties.size() > 0)
            {
                for (int i = 0; i < _polygraphyTableView.getColumns().size(); ++i)
                {
                    //System.out.println("col " + i + ": " + (double)tableProperties.get(String.valueOf(i)));
                    _polygraphyTableView.getColumns().get(i).setPrefWidth((double)tableProperties.get(String.valueOf(i)));
                }
            }
            else
                _polygraphyTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }

    private void savePolygraphyTableAccountingsColsWidth()
    {
        Properties tableColumnsWidthProp =
                Finder._settings.getPropertiesTableColumsWidth("_polygraphyAccountingsTableView");
        if (tableColumnsWidthProp == null)
        {
            tableColumnsWidthProp = new Properties();
            for (int i = 0; i < _polygraphyAccountingsTableView.getColumns().size(); ++i)
            {
                tableColumnsWidthProp.put(String.valueOf(i), _polygraphyAccountingsTableView.getColumns().get(i).getWidth());
            }
            Finder._settings.addPropertiesColWidths("_polygraphyAccountingsTableView", tableColumnsWidthProp);
        }
        else
        {
            for (int i = 0; i < _polygraphyAccountingsTableView.getColumns().size(); ++i)
            {
                tableColumnsWidthProp.put(String.valueOf(i), _polygraphyAccountingsTableView.getColumns().get(i).getWidth());
            }
        }
    }

    private void loadPolygraphyTableAccountingsColsWidth()
    {
        try
        {
            Properties tableProperties = Finder._settings.getPropertiesTableColumsWidth("_polygraphyAccountingsTableView");
            if (tableProperties != null && tableProperties.size() > 0)
            {
                for (int i = 0; i < _polygraphyAccountingsTableView.getColumns().size(); ++i)
                {
                    _polygraphyAccountingsTableView.getColumns().get(i).setPrefWidth((double)tableProperties.get(String.valueOf(i)));
                }
            }
            else
                _polygraphyAccountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }

    private void savePolygraphyStageSize(Stage polygraphyStage)
    {
        Properties propertiesStageSizes =
                Finder._settings.getPropertiesStageSizes("polygraphyStage");
        if (propertiesStageSizes == null)
        {
            propertiesStageSizes = new Properties();
            propertiesStageSizes.put("width", polygraphyStage.getWidth());
            propertiesStageSizes.put("height", polygraphyStage.getHeight());
            Finder._settings.addPropertiesStageSizes("polygraphyStage", propertiesStageSizes);
        } else
        {
            propertiesStageSizes.put("width", polygraphyStage.getWidth());
            propertiesStageSizes.put("height", polygraphyStage.getHeight());
        }
    }

    private void loadPolygraphyStageSize(Stage polygraphyStage)
    {
        try
        {
            Properties properties = Finder._settings.getPropertiesStageSizes("polygraphyStage");
            if (properties != null && properties.size() > 0)
            {
                polygraphyStage.setWidth((double)properties.get("width"));
                polygraphyStage.setHeight((double)properties.get("height"));
            }

        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }
}
