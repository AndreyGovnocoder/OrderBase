import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

class MainInterface
{
    private final Stage _mainStage;
    private Scene _mainScene;
    private static int _currentAccount;
    //static private ArrayList<Order> _allOrders;
    //static private ArrayList<Client> _allClients;
    static private ArrayList<Client> _activeClients;
    //static private ArrayList<Staff> _allStaffs;
    static private ArrayList<Staff> _activeStaffs;
    //static private ArrayList<Account> _allAccounts;
    //static private ArrayList<OrderPosition> _allOrdersPositions;
    //static private ArrayList<Receipt> _allReceipts;
    private TableView<Order> _orderTableView;
    private TableView<OrderPosition> _positionsTableView;
    private TableView<Client> _clientsTableView;
    private static int _clickedOrderIndex = -1;
    static DateTimeFormatter _formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static DateTimeFormatter _formatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private HBox _notificationHBox;
    private Label _indicatorLabel;
    private Timeline _indicatorTimeLine;
    private Button _notificationBtn;
    private TextArea _remarkTextArea;
    private TextField _clientNameTextField;
    private TextField _clientPhoneTextField;
    private TextField _clientMailTextField;
    private TextField _clientContactPersonTextField;
    private TextField _searchByClientTextField;
    private TextField _searchByReceiptNumberTextField;
    private TextField _searchClientsTextField;
    private Text _dateCreateText;
    private Text _dateEditText;
    private Text _dateAvailabilityText;
    private Text _accountCreateText;
    private Text _accountEditText;
    private Text _accountAvailabilityText;
    private Label _dollarRateLabel;
    final static String COMPLETE = "Готово";
    final static String INWORK = "В работе";
    final static String PAYMENT_PAID = "Оплачено";
    final static String PAYMENT_UNPAID = "Не оплачено";
    final static String PAYMENT_50 = "50%";
    final static DollarRate _currDollarRate;
    final static DecimalFormat DF = new DecimalFormat("#.##");
    final Finder finder = new Finder();

    static
    {
        DollarRate dollarRate = null;
        try
        {
            dollarRate = setDollarRate();
            if (dollarRate != null)
            {
                DataBase.setDollarRate(dollarRate);
            } else
                dollarRate = DataBase.getDollarRate();
        }
        catch (Exception exc)
        {
            dollarRate = DataBase.getDollarRate();
            exc.printStackTrace();
        }
        finally
        {
            _currDollarRate = dollarRate;
        }
    }

    MainInterface(Stage stage)
    {
        this._mainStage = stage;
        _activeClients = new ArrayList<>();
        for (final Client client : Finder.get_allClients())
            if(client.is_active())
                _activeClients.add(client);
        _activeStaffs = new ArrayList<>();
        for (final Staff staff : Finder.get_allStaffs())
            if(staff.is_active())
                _activeStaffs.add(staff);
    }

    void show()
    {
        BorderPane mainBorderPane = new BorderPane();
        _mainScene = new Scene(mainBorderPane, 890, 900);
        mainBorderPane.setTop(getTop());
        mainBorderPane.setCenter(getCenter());
        mainBorderPane.setBottom(getBottom());
        Account currAcc = DataBase.getAccount(get_currentAccount());
        _mainStage.setTitle(
                "EXPERTPrint: База заказов (ver. " + Main.primaryVersion + "." + Main.secondaryVersion + ") " +
                "    Аккаунт: " + currAcc.get_name());
        _mainStage.getIcons().add(getIconLogo());
        _mainStage.setScene(_mainScene);
        _mainStage.setOnCloseRequest(event ->
        {
            if (getAlertAskConfirmationDialog("Закрыть программу?"))
                _mainStage.close();
            else
                event.consume();
        });
        _mainStage.show();

        for (final int ver : UpdateInfo.getUpdatesList())
        {
            boolean isPresent = currAcc.get_informedList().contains(ver);
            if(!isPresent)
            {
                final String updateDescription = UpdateInfo.getUpdateDescription(ver);
                final String primVer = UpdateInfo.getVersion(ver, true);
                final String secondVer = UpdateInfo.getVersion(ver, false);
                if(showUpdateInfo(updateDescription, primVer, secondVer))
                {
                    currAcc.setInformedVers(ver);
                    DataBase.setAccountInformed(currAcc.get_id(), currAcc.get_informedList());
                }
            }
        }
    }

    private MenuBar getTop()
    {
        MenuBar mainMenuBar = new MenuBar();
        Menu staffsMenu = new Menu("Сотрудники");
        MenuItem staffsListMenuItem = new MenuItem("Список сотрудников");
        Menu clientsMenu = new Menu("Клиенты");
        MenuItem clientsListMenuItem = new MenuItem("Список клиентов");
        Menu storehouseMenu = new Menu("Склад");
        MenuItem materialsMenuItem = new MenuItem("Материалы");
        MenuItem inksMenuItem = new MenuItem("Чернила");
        Menu lightingMenu = new Menu("Светотехника");
        MenuItem ledsMenuItem = new MenuItem("Светодиоды");
        MenuItem powerSuppliesMenuItem = new MenuItem("Блоки питания");
        Menu montages = new Menu("Монтажи");
        MenuItem montagesShow = new MenuItem("Показать монтажи");
        MenuItem constructionsMenuItem = new MenuItem("Конструкции");
        MenuItem polygraphyMenuItem = new MenuItem("Полиграфия");
        MenuItem requestsMenuItem = new MenuItem("Заявки");

        staffsListMenuItem.setOnAction(event -> showStaffsForm());
        clientsListMenuItem.setOnAction(event -> showClientsForm());
        materialsMenuItem.setOnAction(event ->
        {
            MaterialsForm materialsForm = new MaterialsForm();
            materialsForm.showAndWait(_mainStage);
            for (MaterialsKind kind : Finder.get_allMaterialsKinds())
            {
                System.out.println(kind.get_name());
            }
        });
        inksMenuItem.setOnAction(event ->
        {
            InksForm inksForm = new InksForm();
            inksForm.set_currAccount(get_currentAccount());
            inksForm.showAndWait(_mainStage);
        });
        ledsMenuItem.setOnAction(event ->
        {
            LedsForm ledsForm = new LedsForm();
            ledsForm.showAndWait(_mainStage);
        });
        powerSuppliesMenuItem.setOnAction(event ->
        {
            PowerModulesForm powerModulesForm = new PowerModulesForm();
            powerModulesForm.showAndWait(_mainStage);
        });

        montagesShow.setOnAction(event ->
        {
            MontagesForm montagesForm = new MontagesForm();
            montagesForm.show();
        });

        constructionsMenuItem.setOnAction(event ->
        {
            ConstructionsForm constructionsForm = new ConstructionsForm();
            constructionsForm.showAndWait(_mainStage);
        });

        polygraphyMenuItem.setOnAction(event ->
        {
            PolygraphyForm polygraphyForm = new PolygraphyForm();
            polygraphyForm.showAndWait(_mainStage);
        });

        requestsMenuItem.setOnAction(event ->
        {
            _indicatorLabel.setStyle("-fx-background-color: #33ff00;");
            _indicatorTimeLine.pause();
            RequestsForm requestsForm = new RequestsForm();
            requestsForm.showAndWait(_mainStage);
            checkRequestsViewed();
        });

        staffsMenu.getItems().add(staffsListMenuItem);
        clientsMenu.getItems().add(clientsListMenuItem);
        lightingMenu.getItems().addAll(ledsMenuItem, powerSuppliesMenuItem);
        storehouseMenu.getItems().addAll(
                materialsMenuItem,
                inksMenuItem,
                lightingMenu,
                constructionsMenuItem,
                polygraphyMenuItem,
                new SeparatorMenuItem(),
                requestsMenuItem
        );
        //storehouseMenu.getItems().addAll(materialsMenuItem, inksMenuItem, lightingMenu);
        montages.getItems().addAll(montagesShow);

        mainMenuBar.getMenus().addAll(clientsMenu, staffsMenu, storehouseMenu, montages);
        return mainMenuBar;
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        HBox topHBox = new HBox();
        VBox orderTableViewVBox = new VBox();
        HBox orderDetailsHBox = new HBox();
        VBox detailsLeftVBox = new VBox();
        VBox detailsRightVBox = new VBox();
        _notificationHBox = new HBox();

        detailsLeftVBox.setPrefWidth(_mainScene.getWidth()/2 - 10);
        detailsLeftVBox.setSpacing(10);
        detailsLeftVBox.getChildren().addAll(get_positionsTableView(), getRemarkPane());

        detailsRightVBox.setPrefWidth(_mainScene.getWidth()/2-10);
        detailsRightVBox.setSpacing(10);
        detailsRightVBox.getChildren().addAll(getClientPane(), getOrderChangesPane());

        orderTableViewVBox.getChildren().add(get_orderTableView());

        orderDetailsHBox.setPrefHeight(_mainScene.getHeight()/2);
        orderDetailsHBox.getChildren().addAll(detailsLeftVBox, detailsRightVBox);

        _notificationHBox.setAlignment(Pos.CENTER);
        _notificationHBox.setSpacing(10);
        _notificationHBox.setPadding(new Insets(0,0,0,30));
        setRequestNotification();

        topHBox.setSpacing(10);
        topHBox.getChildren().addAll(getSearchBox(), _notificationHBox, getDollarRateBox());

        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(topHBox, orderTableViewVBox, orderDetailsHBox);

        HBox.setMargin(detailsLeftVBox, new Insets(10));
        HBox.setMargin(detailsRightVBox, new Insets(10));
        VBox.setMargin(_orderTableView, new Insets(10));
        HBox.setHgrow(detailsLeftVBox, Priority.ALWAYS);
        HBox.setHgrow(detailsRightVBox, Priority.ALWAYS);
        return centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane buttonsPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button createOrderButton = new Button("Создать заказ");
        Button printReceiptButton = new Button("Печать квитанции");

        createOrderButton.setOnAction(event ->
        {
            OrderDialog orderDialog = new OrderDialog();
            orderDialog.showAndWait(_mainStage);
            if(orderDialog._ok)
            {
                clearOrderDetails();
                Order newOrder = orderDialog.get_order();
                if(DataBase.addOrder(newOrder))
                {
                    newOrder.set_id(DataBase.getLastId(DataBase.ORDERS_TABLE));
                    for(OrderPosition position : orderDialog.getOrderPositionsList())
                    {
                        position.set_idOrder(newOrder.get_id());
                        if (DataBase.addOrderPosition(position))
                            position.set_id(DataBase.getLastId(DataBase.POSITIONS_TABLE));
                    }
                    Finder.get_allOrders().add(newOrder);
                    Finder.get_allOrdersPositions().addAll(orderDialog.getOrderPositionsList());
                    _orderTableView.getItems().add(newOrder);
                    _orderTableView.scrollTo(newOrder);
                    _orderTableView.getSelectionModel().select(newOrder);
                }
            }
        });

        printReceiptButton.setOnAction(event ->
        {
            if(_orderTableView.getSelectionModel().getSelectedItem() != null)
            {
                if (_orderTableView.getSelectionModel().getSelectedItem().get_isPrintReceipt())
                {
                    if (getAlertAskConfirmationDialog("Квитанция к этому заказу уже печаталась\nВсё равно продолжить?"))
                        printReceipt();
                } else
                    printReceipt();
            }
        });

        buttonsPane.getChildren().addAll(createOrderButton, printReceiptButton);
        AnchorPane.setTopAnchor(createOrderButton, 5.0);
        AnchorPane.setLeftAnchor(createOrderButton, 5.0);
        AnchorPane.setBottomAnchor(createOrderButton, 5.0);
        AnchorPane.setTopAnchor(printReceiptButton, 5.0);
        AnchorPane.setRightAnchor(printReceiptButton, 5.0);
        AnchorPane.setBottomAnchor(printReceiptButton, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), buttonsPane);
        return bottomVBox;
    }

    private TableView<Order> get_orderTableView()
    {
        _orderTableView = new TableView<>();
        _orderTableView.setPrefHeight(_mainScene.getHeight()/2);
        TableColumn<Order, java.sql.Date> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.076));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_date"));
        dateCol.setCellFactory(tc -> new TableCell<>()
        {
            @Override
            protected void updateItem(java.sql.Date date, boolean empty)
            {
                super.updateItem(date, empty);
                if (empty)
                {
                    setText(null);
                } else
                {
                    setText(_formatter.format(date.toLocalDate()));
                }
            }
        });
        TableColumn<Order, Integer> clientCol = new TableColumn<>("Заказчик");
        clientCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.385));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("_client"));
        clientCol.setCellFactory(tc -> new TableCell<Order, Integer>()
        {
            @Override
            protected void updateItem(Integer clientId, boolean empty)
            {
                super.updateItem(clientId, empty);
                if(empty)
                {
                    setText(null);
                } else
                {
                    setText(Finder.getClient(clientId).get_name());
                }
            }
        });
        TableColumn<Order, Boolean> isPrintReceiptCol = new TableColumn<>("Отгрузка");
        isPrintReceiptCol.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().get_isPrintReceipt()));
        isPrintReceiptCol.setCellFactory( tc -> new CheckBoxTableCell<>());
        isPrintReceiptCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.04));

        TableColumn<Order, String> paymentCol = new TableColumn<>("Оплата");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("_payment"));
        paymentCol.setStyle("-fx-alignment: CENTER;");
        paymentCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.094));
        TableColumn<Order, String> amountCol = new TableColumn<>("Сумма");
        amountCol.setStyle("-fx-alignment: CENTER;");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("_amount"));
        amountCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.08));

        TableColumn<Order, Integer> managerCol = new TableColumn<>("Менеджер");
        managerCol.setStyle("-fx-alignment: CENTER;");
        managerCol.setCellValueFactory(new PropertyValueFactory<>("_manager"));
        managerCol.setCellFactory(tc -> new TableCell<Order, Integer>()
        {
            @Override
            protected void updateItem(Integer managerId, boolean empty)
            {
                super.updateItem(managerId, empty);
                if(empty)
                {
                    setText(null);
                } else
                {
                    setText(Finder.getStaff(managerId).get_name());
                }
            }
        });
        managerCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.1));

        TableColumn<Order, Integer> designerCol = new TableColumn<>("Дизайнер");
        designerCol.setStyle("-fx-alignment: CENTER;");
        designerCol.setCellValueFactory(new PropertyValueFactory<>("_designer"));
        designerCol.setCellFactory(tc -> new TableCell<Order, Integer>()
        {
            @Override
            protected void updateItem(Integer designerId, boolean empty)
            {
                super.updateItem(designerId, empty);
                if(empty)
                {
                    setText(null);
                } else
                {
                    setText(Finder.getStaff(designerId).get_name());
                }
            }
        });
        designerCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.1));

        TableColumn<Order, String> availabilityCol = new TableColumn<>("Готовность");
        availabilityCol.setStyle("-fx-alignment: CENTER;");
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("_availability"));
        availabilityCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.079));

        _orderTableView.setPrefHeight((_mainScene.getHeight()*2)/3);
        //tableViewOrders.setPlaceholder(new Text("База заказов пуста"));
        _orderTableView.getColumns().addAll(dateCol,clientCol, isPrintReceiptCol, paymentCol, amountCol, managerCol, designerCol, availabilityCol);
        _orderTableView.setStyle("");
        ContextMenu contextMenu = new ContextMenu();

        _orderTableView.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>()
        {
            @Override
            public TableRow<Order> call(TableView<Order> param)
            {
                return null;
            }
        });

        _orderTableView.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>()
        {
            @Override
            public TableRow<Order> call(TableView<Order> tableView)
            {
                TableRow<Order> row = new TableRow<Order>()
                {
                    @Override
                    protected void updateItem(Order order, boolean empty)
                    {
                        super.updateItem(order, empty);
                        this.setFocused(true);
                        if(!empty)
                        {
                            if (order != null && order.get_availability().equals(COMPLETE))
                            {
                                this.setStyle("-fx-background-color: #3cb380");
                            } else this.setStyle("");
                        }
                        else
                        {
                            this.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;");
                        }

                    }
                };

                row.setOnMouseClicked(event ->
                {
                    if (!row.isEmpty())
                    {
                        if (row.getItem().get_availability().equals(COMPLETE))
                            row.setStyle("-fx-background-color: #006400;" +
                                    "-fx-text-background-color: #ffffff");
                    }

                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1)
                    {
                        onClickOnRowOrder(row.getItem());
                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY))
                    {
                        onClickOnRowOrder(row.getItem());
                        row.setOnContextMenuRequested(event1 ->
                        {
                            if(row.getItem() != null)
                            {
                                setContextMenu(contextMenu).show(row, event1.getScreenX() + 10, event1.getScreenY() + 5);
                                //setAvailabilityContextMenu(row.getItem(), contextMenu, tableViewOrders.getFocusModel().getFocusedIndex()).show(row, event.getScreenX() + 10, event.getScreenY() + 5);
                            }
                        });
                    }
                });
                return row;
            }
        });

        if(_clickedOrderIndex>=0) _orderTableView.scrollTo(_clickedOrderIndex);
        else _orderTableView.scrollTo(_orderTableView.getItems().size()-1);
        _orderTableView.setItems(FXCollections.observableArrayList(Finder.get_allOrders()));
        _orderTableView.scrollTo(_orderTableView.getItems().size()-1);
        return _orderTableView;
    }

    private TableView<OrderPosition> get_positionsTableView()
    {
        _positionsTableView = new TableView<>();
        _positionsTableView.setPlaceholder(new Text("Выберите заказ для отображения позиций заказа"));
        _positionsTableView.setPrefHeight(_mainScene.getHeight()/3);
        TableColumn<OrderPosition, String> descriptionCol = new TableColumn<>("Позиция");
        descriptionCol.prefWidthProperty().bind(_positionsTableView.widthProperty().multiply(0.58));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("_description"));
        descriptionCol.setCellFactory(tc ->
        {
            TableCell<OrderPosition, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
        TableColumn<OrderPosition, String> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.prefWidthProperty().bind(_positionsTableView.widthProperty().multiply(0.2));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrderPosition, String> issueCol = new TableColumn<>("Выдача");
        issueCol.setCellValueFactory(new PropertyValueFactory<>("_issue"));
        issueCol.setStyle("-fx-alignment: CENTER;");
        issueCol.prefWidthProperty().bind(_positionsTableView.widthProperty().multiply(0.18));
        _positionsTableView.getColumns().addAll(descriptionCol, quantityCol, issueCol);

        return _positionsTableView;
    }

    private TitledPane getRemarkPane()
    {
        TitledPane remarkTitledPane = new TitledPane();
        _remarkTextArea = new TextArea();
        _remarkTextArea.setWrapText(true);
        _remarkTextArea.setEditable(false);
        remarkTitledPane.setText("Примечание");
        remarkTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        remarkTitledPane.setCollapsible(false);
        remarkTitledPane.setExpanded(true);
        remarkTitledPane.setContent(_remarkTextArea);
        return remarkTitledPane;
    }

    private TitledPane getClientPane()
    {
        TitledPane clientTitledPane = new TitledPane();
        GridPane clientCardGridPane = new GridPane();
        _clientNameTextField = new TextField();
        _clientPhoneTextField = new TextField();
        _clientMailTextField = new TextField();
        _clientContactPersonTextField = new TextField();
        Text clientNameText = new Text("Клиент: ");
        Text clientPhoneText = new Text("Телефон: ");
        Text clientMailText = new Text("E-mail: ");
        Text clientContactPersonText = new Text("Контактное лицо: ");
        String textFieldStyle = "-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;";

        _clientNameTextField.setEditable(false);
        _clientNameTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientNameTextField.setStyle(textFieldStyle);
        _clientPhoneTextField.setEditable(false);
        _clientPhoneTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientPhoneTextField.setStyle(textFieldStyle);
        _clientMailTextField.setEditable(false);
        _clientMailTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientMailTextField.setStyle(textFieldStyle);
        _clientContactPersonTextField.setEditable(false);
        _clientContactPersonTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientContactPersonTextField.setStyle(textFieldStyle);

        clientCardGridPane.add(clientNameText,0,0);
        clientCardGridPane.add(clientPhoneText,0,1);
        clientCardGridPane.add(clientMailText,0,2);
        clientCardGridPane.add(clientContactPersonText, 0, 3);
        clientCardGridPane.add(_clientNameTextField,1,0);
        clientCardGridPane.add(_clientPhoneTextField,1,1);
        clientCardGridPane.add(_clientMailTextField,1,2);
        clientCardGridPane.add(_clientContactPersonTextField, 1,3);
        clientCardGridPane.setHgap(3);
        GridPane.setHalignment(clientNameText, HPos.RIGHT);
        GridPane.setHalignment(clientPhoneText, HPos.RIGHT);
        GridPane.setHalignment(clientMailText, HPos.RIGHT);

        clientTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        clientTitledPane.setText("Карта клиента");
        clientTitledPane.setExpanded(true);
        clientTitledPane.setCollapsible(false);
        clientTitledPane.setContent(clientCardGridPane);

        return clientTitledPane;
    }

    private TitledPane getOrderChangesPane()
    {
        TitledPane orderChangesTitledPane = new TitledPane();
        GridPane orderChangersGridPane = new GridPane();


        orderChangesTitledPane.setText("Карта изменений заказа");
        orderChangesTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        orderChangesTitledPane.setExpanded(true);
        orderChangesTitledPane.setCollapsible(false);
        orderChangesTitledPane.setContent(orderChangersGridPane);

        Label dateTimeLabel = new Label("Дата/Время");
        Label accountsLabel = new Label("Аккаунт");
        Label createLabel = new Label("Создание заказа");
        Label editLabel = new Label("Редактирование заказа");
        Label availabilityLabel = new Label("Изменение\n(готово/в работе)");

        dateTimeLabel.setPadding(new Insets(5));
        accountsLabel.setPadding(new Insets(5));
        createLabel.setPadding(new Insets(5));
        editLabel.setPadding(new Insets(5));
        availabilityLabel.setPadding(new Insets(5));

        _dateCreateText = new Text();
        _dateEditText = new Text();
        _dateAvailabilityText = new Text();
        _accountCreateText = new Text();
        _accountEditText = new Text();
        _accountAvailabilityText = new Text();

        ColumnConstraints column1 = new ColumnConstraints(50, 160, Double.MAX_VALUE);
        ColumnConstraints column2 = new ColumnConstraints(50, 150, Double.MAX_VALUE);
        ColumnConstraints column3 = new ColumnConstraints(50, 150, Double.MAX_VALUE);
        orderChangersGridPane.getColumnConstraints().addAll(column1, column2, column3);

        orderChangersGridPane.getRowConstraints().add(new RowConstraints(20));
        orderChangersGridPane.getRowConstraints().add(new RowConstraints(45));
        orderChangersGridPane.getRowConstraints().add(new RowConstraints(45));
        orderChangersGridPane.getRowConstraints().add(new RowConstraints(45));

        orderChangersGridPane.add(createLabel, 0,1);
        orderChangersGridPane.add(editLabel, 0,2);
        orderChangersGridPane.add(availabilityLabel, 0,3);
        orderChangersGridPane.add(dateTimeLabel, 1,0);
        orderChangersGridPane.add(_dateCreateText, 1, 1);
        orderChangersGridPane.add(_dateEditText, 1,2);
        orderChangersGridPane.add(_dateAvailabilityText, 1,3);
        orderChangersGridPane.add(accountsLabel, 2,0);
        orderChangersGridPane.add(_accountCreateText,2,1);
        orderChangersGridPane.add(_accountEditText, 2,2);
        orderChangersGridPane.add(_accountAvailabilityText, 2,3);

        orderChangersGridPane.setGridLinesVisible(true);
        GridPane.setHalignment(createLabel, HPos.LEFT);
        GridPane.setHalignment(editLabel, HPos.LEFT);
        GridPane.setHalignment(availabilityLabel, HPos.LEFT);
        GridPane.setHalignment(dateTimeLabel,HPos.CENTER);
        GridPane.setHalignment(accountsLabel,HPos.CENTER);
        GridPane.setHalignment(_dateCreateText,HPos.CENTER);
        GridPane.setHalignment(_dateEditText,HPos.CENTER);
        GridPane.setHalignment(_dateAvailabilityText,HPos.CENTER);
        GridPane.setHalignment(_accountCreateText,HPos.CENTER);
        GridPane.setHalignment(_accountEditText,HPos.CENTER);
        GridPane.setHalignment(_accountAvailabilityText,HPos.CENTER);

        GridPane.setValignment(createLabel, VPos.CENTER);
        GridPane.setValignment(editLabel, VPos.CENTER);
        GridPane.setValignment(availabilityLabel, VPos.CENTER);
        GridPane.setValignment(dateTimeLabel, VPos.CENTER);
        GridPane.setValignment(_accountCreateText, VPos.CENTER);
        GridPane.setValignment(_dateCreateText, VPos.CENTER);
        GridPane.setValignment(_dateEditText, VPos.CENTER);
        GridPane.setValignment(_dateAvailabilityText, VPos.CENTER);
        GridPane.setValignment(_accountCreateText, VPos.CENTER);
        GridPane.setValignment(_accountEditText, VPos.CENTER);
        GridPane.setValignment(_accountAvailabilityText, VPos.CENTER);

        return orderChangesTitledPane;
    }

    private void showStaffsForm()
    {
        Stage staffsStage = new Stage();
        BorderPane staffsBorderPane = new BorderPane();
        AnchorPane buttonsPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Scene staffsScene = new Scene(staffsBorderPane, 300,300);
        VBox centerVBox = new VBox();
        TableView<Staff> staffsTableView = new TableView<Staff>();
        ContextMenu contextMenuStaff = new ContextMenu();
        MenuItem menuItemDeleteStaff = new MenuItem("Удалить");
        MenuItem menuItemEditStaff = new MenuItem("Редактировать");
        Button addStaffButton = new Button("Добавить сотрудника");
        Button closeButton = new Button("Закрыть");

        menuItemDeleteStaff.setOnAction(event ->
        {
            Staff staff = staffsTableView.getSelectionModel().getSelectedItem();
            final int allArrayIndex = Finder.get_allStaffs().indexOf(staff);

            if(checkStaffInOrders(staff.get_id()))
            {
                if(DataBase.editActive(staff.get_id(), false, DataBase.STAFFS_TABLE))
                {
                    staffsTableView.getItems().remove(staff);
                    _activeStaffs.remove(staff);
                    staff.set_active(false);
                    Finder.get_allStaffs().set(allArrayIndex, staff);
                }
                else
                    getAlertErrorDialog("Ошибка базы данных");
            }
            else
            {
                if(DataBase.removeObject(staff.get_id(), DataBase.STAFFS_TABLE))
                {
                    staffsTableView.getItems().remove(staff);
                    Finder.get_allStaffs().remove(staff);
                    _activeStaffs.remove(staff);
                }
            }
        });

        menuItemEditStaff.setOnAction(event ->
        {
            Staff staff = staffsTableView.getSelectionModel().getSelectedItem();
            int tableIndex = staffsTableView.getSelectionModel().getSelectedIndex();
            int allArrayIndex = Finder.get_allStaffs().indexOf(staff);
            int activeArrayIndex = _activeStaffs.indexOf(staff);
            StaffDialog staffDialog = new StaffDialog(staff);
            staffDialog.showAndWait(staffsStage);
            if(staffDialog._ok)
            {
                if(DataBase.editStaff(staff))
                {
                    staffsTableView.getItems().set(tableIndex, staffDialog.get_staff());
                    Finder.get_allStaffs().set(allArrayIndex, staffDialog.get_staff());
                    _activeStaffs.set(activeArrayIndex, staffDialog.get_staff());
                }
            }
        });

        addStaffButton.setOnAction(event ->
        {
            StaffDialog staffDialog = new StaffDialog();
            staffDialog.showAndWait(staffsStage);
            if(staffDialog._ok)
            {
                if(DataBase.addStaff(staffDialog.get_staff()))
                {
                    staffDialog.get_staff().set_id(DataBase.getLastId(DataBase.STAFFS_TABLE));
                    staffsTableView.getItems().add(staffDialog.get_staff());
                    Finder.get_allStaffs().add(staffDialog.get_staff());
                    _activeStaffs.add(staffDialog.get_staff());
                }
            }
        });

        closeButton.setOnAction(event -> staffsStage.close());

        contextMenuStaff.getItems().addAll(menuItemEditStaff, menuItemDeleteStaff);

        staffsTableView.setPlaceholder(new Text("Список сотрудников пуст"));
        TableColumn<Staff, String> nameCol = new TableColumn<Staff, String>("Имя");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("_name"));
        nameCol.prefWidthProperty().bind(staffsScene.widthProperty().multiply(0.6));
        TableColumn<Staff, String> positionCol = new TableColumn<Staff, String>("Должность");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("_position"));
        staffsTableView.setItems(FXCollections.observableArrayList(_activeStaffs));
        staffsTableView.getColumns().addAll(nameCol, positionCol);
        staffsTableView.setRowFactory(new Callback<TableView<Staff>, TableRow<Staff>>()
        {
            @Override
            public TableRow<Staff> call(TableView<Staff> param)
            {
                TableRow<Staff> row = new TableRow<Staff>();
                row.setOnMouseClicked(event ->
                {
                    staffsTableView.setOnContextMenuRequested(event1 ->
                    {
                        contextMenuStaff.show(row, event1.getScreenX()+10, event1.getScreenY()+10);
                    });
                });
                return row;
            }
        });

        buttonsPane.getChildren().addAll(addStaffButton, closeButton);

        AnchorPane.setLeftAnchor(addStaffButton, 5.0);
        AnchorPane.setTopAnchor(addStaffButton, 5.0);
        AnchorPane.setBottomAnchor(addStaffButton, 5.0);

        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), buttonsPane);

        centerVBox.getChildren().addAll(staffsTableView);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        VBox.setVgrow(staffsTableView, Priority.ALWAYS);
        VBox.setMargin(staffsTableView, new Insets(10));

        staffsBorderPane.setBottom(bottomVBox);
        staffsBorderPane.setCenter(centerVBox);

        staffsStage.setTitle("Список сотрудников");
        staffsStage.getIcons().add(getIconLogo());
        staffsStage.setMinWidth(300);
        staffsStage.setMinHeight(300);
        staffsStage.setScene(staffsScene);
        staffsStage.initModality(Modality.WINDOW_MODAL);
        staffsStage.initOwner(_mainStage);
        staffsStage.show();
    }

    private void showClientsForm()
    {
        Stage clientsStage = new Stage();
        BorderPane clientsBorderPane = new BorderPane();
        AnchorPane buttonsPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        VBox centerVBox = new VBox();
        Scene clientsScene = new Scene(clientsBorderPane, 900,600);
        _clientsTableView = new TableView<>();
        _searchClientsTextField = new TextField();
        Label searchClientLabel = new Label("Поиск клиентов");
        HBox searchClientHBox = new HBox();
        VBox searchClientVBox = new VBox();
        ContextMenu contextMenuClients = new ContextMenu();
        MenuItem menuItemDeleteClient = new MenuItem("Удалить");
        MenuItem menuItemEditClient = new MenuItem("Редактировать");
        Button addClientButton = new Button("Добавить клиента");
        Button closeButton = new Button("Закрыть");

        _searchClientsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchClients(newValue);
        });
        _searchClientsTextField.setMinWidth(500);
        _searchClientsTextField.setPrefWidth(500);
        _searchClientsTextField.setMaxWidth(500);

        searchClientHBox.setSpacing(10);
        searchClientHBox.setAlignment(Pos.CENTER_LEFT);
        searchClientHBox.setPadding(new Insets(10,0,10,10));
        searchClientHBox.getChildren().addAll(searchClientLabel, _searchClientsTextField);
        searchClientVBox.getChildren().addAll(searchClientHBox, new Separator());

        menuItemDeleteClient.setOnAction(event ->
        {
            Client client = _clientsTableView.getSelectionModel().getSelectedItem();
            final int allArrayIndex = Finder.get_allClients().indexOf(client);
            if(checkClientInOrders(client.get_id()))
            {
                if(DataBase.editActive(client.get_id(), false, DataBase.CLIENTS_TABLE))
                {
                    _clientsTableView.getItems().remove(client);
                    _activeClients.remove(client);
                    client.set_active(false);
                    Finder.get_allClients().set(allArrayIndex, client);
                }
                else
                    getAlertErrorDialog("Ошибка базы данных");
            }
            else
            {
                if(DataBase.removeObject(client.get_id(), DataBase.CLIENTS_TABLE))
                {
                    _clientsTableView.getItems().remove(client);
                    Finder.get_allClients().remove(client);
                    _activeClients.remove(client);
                }
            }
        });

        menuItemEditClient.setOnAction(event ->
        {
            Client client = _clientsTableView.getSelectionModel().getSelectedItem();
            int tableIndex = _clientsTableView.getSelectionModel().getSelectedIndex();
            int allArrayIndex = Finder.get_allClients().indexOf(client);
            int activeArrayIndex = _activeClients.indexOf(client);
            ClientDialog clientDialog = new ClientDialog(client);
            clientDialog.showAndWait(clientsStage);
            if(clientDialog._ok)
            {
                if(DataBase.editClient(client))
                {
                    _clientsTableView.getItems().set(tableIndex, clientDialog.get_client());
                    Finder.get_allClients().set(allArrayIndex, clientDialog.get_client());
                    _activeClients.set(activeArrayIndex, clientDialog.get_client());
                }
            }
        });

        addClientButton.setOnAction(event ->
        {
            ClientDialog clientDialog = new ClientDialog();
            clientDialog.showAndWait(clientsStage);
            if(clientDialog._ok)
            {
                if(DataBase.addClient(clientDialog.get_client()))
                {
                    clientDialog.get_client().set_id(DataBase.getLastId(DataBase.CLIENTS_TABLE));
                    _clientsTableView.getItems().add(clientDialog.get_client());
                    Finder.get_allClients().add(clientDialog.get_client());
                    _activeClients.add(clientDialog.get_client());
                    _clientsTableView.scrollTo(_clientsTableView.getItems().size() - 1);
                }
            }
        });

        closeButton.setOnAction(event -> clientsStage.close());

        contextMenuClients.getItems().addAll(menuItemDeleteClient, menuItemEditClient);

        _clientsTableView.setPlaceholder(new Text("Список клиентов пуст"));
        TableColumn<Client, String> nameCol = new TableColumn<Client, String>("Клиент");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("_name"));
        nameCol.prefWidthProperty().bind(clientsScene.widthProperty().multiply(0.3));
        TableColumn<Client, String> phoneCol = new TableColumn<Client, String>("Телефон");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("_phone"));
        phoneCol.prefWidthProperty().bind(clientsScene.widthProperty().multiply(0.27));
        TableColumn<Client, String> mailCol = new TableColumn<Client, String>("E-mail");
        mailCol.setCellValueFactory(new PropertyValueFactory<>("_mail"));
        mailCol.prefWidthProperty().bind(clientsScene.widthProperty().multiply(0.2));
        TableColumn<Client, String> contactPersonCol = new TableColumn<>("Контактное лицо");
        contactPersonCol.setCellValueFactory(new PropertyValueFactory<>("_contactPerson"));
        contactPersonCol.prefWidthProperty().bind(clientsScene.widthProperty().multiply(0.19));
        _clientsTableView.setItems(FXCollections.observableArrayList(_activeClients));
        _clientsTableView.getColumns().addAll(nameCol, phoneCol, mailCol, contactPersonCol);
        _clientsTableView.setRowFactory(new Callback<TableView<Client>, TableRow<Client>>()
        {
            @Override
            public TableRow<Client> call(TableView<Client> param)
            {
                TableRow<Client> row = new TableRow<Client>();
                row.setOnMouseClicked(event ->
                {
                    _clientsTableView.setOnContextMenuRequested(event1 ->
                    {
                        contextMenuClients.show(row, event1.getScreenX()+10, event1.getScreenY()+10);
                    });
                });
                return row;
            }
        });

        centerVBox.getChildren().addAll(_clientsTableView);
        VBox.setMargin(_clientsTableView, new Insets(10));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        VBox.setVgrow(_clientsTableView, Priority.ALWAYS);

        buttonsPane.getChildren().addAll(addClientButton, closeButton);
        AnchorPane.setTopAnchor(addClientButton, 5.0);
        AnchorPane.setLeftAnchor(addClientButton, 5.0);
        AnchorPane.setBottomAnchor(addClientButton, 5.0);

        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), buttonsPane);

        clientsBorderPane.setTop(searchClientVBox);
        clientsBorderPane.setBottom(bottomVBox);
        clientsBorderPane.setCenter(centerVBox);

        clientsStage.setTitle("Список клиентов");
        clientsStage.getIcons().add(getIconLogo());
        clientsStage.initModality(Modality.WINDOW_MODAL);
        clientsStage.initOwner(_mainStage);
        clientsStage.setScene(clientsScene);
        clientsStage.setMinWidth(500);
        clientsStage.setMinHeight(400);
        clientsStage.show();
    }

    private ContextMenu setContextMenu(ContextMenu contextMenu)
    {
        Order order = _orderTableView.getSelectionModel().getSelectedItem();
        MenuItem setAvailability = new MenuItem();
        Menu setPayment = new Menu("Оплата");
        MenuItem payment1 = new MenuItem();
        MenuItem payment2 = new MenuItem();
        MenuItem editOrderItem = new MenuItem("Редактировать заказ");
        MenuItem deleteOrderItem = new MenuItem("Удалить заказ");
        MenuItem repeatOrderItem = new MenuItem("Повторить заказ");
        SeparatorMenuItem separator = new SeparatorMenuItem();
        setPayment.getItems().clear();
        setPayment.getItems().addAll(payment1, payment2);

        switch (order.get_availability())
        {
            case INWORK:
                setAvailability.setText(COMPLETE);
                break;

            case COMPLETE:
                setAvailability.setText(INWORK);
                break;
        }

        setAvailability.setOnAction(event ->
        {
            int indexInTableView = _orderTableView.getSelectionModel().getSelectedIndex();
            int indexInArray = Finder.get_allOrders().indexOf(order);
            order.set_availability(setAvailability.getText());
            order.set_accountAvailability(get_currentAccount());
            order.set_dateTimeAvailability(LocalDateTime.now());
            if(DataBase.editOrder(order))
            {
                _orderTableView.getItems().set(indexInTableView, order);
                Finder.get_allOrders().set(indexInArray, order);
                onClickOnRowOrder(order);
            }
        });

        switch (order.get_payment()){
            case PAYMENT_PAID:
                payment1.setText(PAYMENT_UNPAID);
                payment2.setText(PAYMENT_50);
                break;
            case PAYMENT_UNPAID:
                payment1.setText(PAYMENT_PAID);
                payment2.setText(PAYMENT_50);
                break;
            case PAYMENT_50:
                payment1.setText(PAYMENT_PAID);
                payment2.setText(PAYMENT_UNPAID);
                break;
        }

        payment1.setOnAction(event ->
        {
            int indexInTableView = _orderTableView.getSelectionModel().getSelectedIndex();
            int indexInArray = Finder.get_allOrders().indexOf(order);
            order.set_payment(payment1.getText());
            order.set_accountEdit(get_currentAccount());
            order.set_dateTimeEdit(LocalDateTime.now());
            if(DataBase.editOrder(order))
            {
                _orderTableView.getItems().set(indexInTableView, order);
                Finder.get_allOrders().set(indexInArray, order);
                onClickOnRowOrder(order);
            }
        });

        payment2.setOnAction(event ->
        {
            int indexInTableView = _orderTableView.getSelectionModel().getSelectedIndex();
            int indexInArray = Finder.get_allOrders().indexOf(order);
            order.set_payment(payment2.getText());
            order.set_accountEdit(get_currentAccount());
            order.set_dateTimeEdit(LocalDateTime.now());
            if(DataBase.editOrder(order))
            {
                onClickOnRowOrder(order);
                _orderTableView.getItems().set(indexInTableView, order);
                Finder.get_allOrders().set(indexInArray, order);
            }
        });

        editOrderItem.setOnAction(event ->
        {
            if (_orderTableView.getSelectionModel().getSelectedItem() != null)
            {
                Order oldOrder = _orderTableView.getSelectionModel().getSelectedItem();
                OrderDialog orderDialog = new OrderDialog(oldOrder);
                final int oldDesignerId = oldOrder.get_designer();
                final int oldManagerId = oldOrder.get_manager();
                final int oldClientId = oldOrder.get_client();
                orderDialog.showAndWait(_mainStage);
                if(orderDialog._ok)
                {
                    clearOrderDetails();
                    if(DataBase.editOrder(orderDialog.get_order()))
                    {
                        if(DataBase.removeOrderPositions(orderDialog.get_order().get_id()))
                        {
                            Finder.get_allOrders().remove(_orderTableView.getSelectionModel().getSelectedItem());
                            Finder.get_allOrders().add(orderDialog.get_order());
                            Finder.get_allOrdersPositions().removeAll(Finder.getOrdersPositionsList(orderDialog.get_order().get_id()));
                            _orderTableView.getItems().set(
                                    _orderTableView.getSelectionModel().getSelectedIndex(),
                                    orderDialog.get_order());
                            for (OrderPosition position : orderDialog.getOrderPositionsList())
                            {
                                position.set_idOrder(orderDialog.get_order().get_id());
                                if(DataBase.addOrderPosition(position))
                                    Finder.get_allOrdersPositions().add(position);
                            }
                        }
                        _orderTableView.scrollTo(orderDialog.get_order());
                        _orderTableView.getFocusModel().focus(_clickedOrderIndex);
                        onClickOnRowOrder(orderDialog.get_order());

                        if(!checkClientInOrders(oldClientId))
                            DataBase.removeObject(oldClientId, DataBase.CLIENTS_TABLE);
                        if(!checkStaffInOrders(oldManagerId))
                            DataBase.removeObject(oldManagerId, DataBase.STAFFS_TABLE);
                        if(!checkStaffInOrders(oldDesignerId))
                            DataBase.removeObject(oldDesignerId, DataBase.STAFFS_TABLE);
                    }
                }
            }
        });

        deleteOrderItem.setOnAction(event ->
        {
            final String question = "Вы уверены, что хотите удалить заказ?";
            final int idOrder = _orderTableView.getSelectionModel().getSelectedItem().get_id();
            final Order removedOrder = Finder.getOrder(idOrder);
            final int removedDesignerId = removedOrder.get_designer();
            final int removedManagerId = removedOrder.get_manager();
            final int removedClientId = removedOrder.get_client();
            if(_clickedOrderIndex != -1 && getAlertAskConfirmationDialog(question))
            {
                if(DataBase.removeObject(idOrder, DataBase.ORDERS_TABLE))
                {
                    _orderTableView.getItems().remove(_clickedOrderIndex);
                    Finder.get_allOrders().remove(removedOrder);
                    DataBase.removeOrderPositions(idOrder);
                    _orderTableView.scrollTo(_orderTableView.getItems().size()-1);
                    clearOrderDetails();
                    _orderTableView.getSelectionModel().clearAndSelect(_orderTableView.getItems().size()-1);
                    onClickOnRowOrder(_orderTableView.getSelectionModel().getSelectedItem());
                    if(!checkClientInOrders(removedClientId))
                        DataBase.removeObject(removedClientId, DataBase.CLIENTS_TABLE);
                    if(!checkStaffInOrders(removedManagerId))
                        DataBase.removeObject(removedManagerId, DataBase.STAFFS_TABLE);
                    if(!checkStaffInOrders(removedDesignerId))
                        DataBase.removeObject(removedDesignerId, DataBase.STAFFS_TABLE);
                }
            }
        });

        repeatOrderItem.setOnAction(event ->
        {
            //Order copyOfOrder = _orderTableView.getSelectionModel().getSelectedItem();
            Order selectedOrder = _orderTableView.getSelectionModel().getSelectedItem();
            Order copyOfOrder = new Order();
            copyOrder(selectedOrder, copyOfOrder);

            if (getAlertAskConfirmationDialog("Повторить заказ клиента '"
                    + Finder.getClient(selectedOrder.get_client()) + "' от " + _formatter.format(copyOfOrder.get_date().toLocalDate()) + "?"))
            {
                if(DataBase.addOrder(copyOfOrder))
                {
                    copyOfOrder.set_id(DataBase.getLastId(DataBase.ORDERS_TABLE));
                    for (final OrderPosition position : Finder.getOrdersPositionsList(selectedOrder.get_id()))
                    {
                            OrderPosition newPosition = new OrderPosition();
                            newPosition.set_idOrder(copyOfOrder.get_id());
                            newPosition.set_description(position.get_description());
                            newPosition.set_quantity(position.get_quantity());
                            newPosition.set_issue(position.get_issue());
                            if (DataBase.addOrderPosition(newPosition))
                                Finder.get_allOrdersPositions().add(newPosition);
                    }

                    Finder.get_allOrders().add(copyOfOrder);
                    _orderTableView.getItems().add(copyOfOrder);
                    _orderTableView.scrollTo(copyOfOrder);
                    _orderTableView.getSelectionModel().select(copyOfOrder);
                    onClickOnRowOrder(copyOfOrder);
                }
            }
        });

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(setAvailability, separator,
                setPayment, new SeparatorMenuItem(),
                editOrderItem, deleteOrderItem, new SeparatorMenuItem(), repeatOrderItem);
        return contextMenu;
    }

    private void onClickOnRowOrder(Order clickedOrder)
    {
        clearOrderDetails();
        _clickedOrderIndex = _orderTableView.getSelectionModel().getSelectedIndex();

        _positionsTableView.getItems().clear();
        _positionsTableView.getItems().addAll(Finder.getOrdersPositionsList(clickedOrder.get_id()));

        Client client = Finder.getClient(clickedOrder.get_client());
        _clientNameTextField.setText(client.get_name());
        _clientPhoneTextField.setText(client.get_phone());
        _clientMailTextField.setText(client.get_mail());
        _clientContactPersonTextField.setText(client.get_contactPerson());

        if(clickedOrder.get_dateTimeCreate() != null)
            _dateCreateText.setText(
                    clickedOrder.get_dateTimeCreate().toLocalDate().format(_formatter) + "\n" +
                    clickedOrder.get_dateTimeCreate().toLocalTime().format(_formatterTime));

        if(clickedOrder.get_dateTimeEdit() != null)
            _dateEditText.setText(
                    clickedOrder.get_dateTimeEdit().toLocalDate().format(_formatter) + "\n" +
                    clickedOrder.get_dateTimeEdit().toLocalTime().format(_formatterTime));

        if(clickedOrder.get_dateTimeAvailability() != null)
            _dateAvailabilityText.setText(
                    clickedOrder.get_dateTimeAvailability().toLocalDate().format(_formatter) + "\n" +
                    clickedOrder.get_dateTimeAvailability().toLocalTime().format(_formatterTime));

        if(clickedOrder.get_accountCreate() != -1)
            _accountCreateText.setText(Finder.getAccount(clickedOrder.get_accountCreate()).get_name());

        if(clickedOrder.get_accountEdit() != -1)
            _accountEditText.setText(Finder.getAccount(clickedOrder.get_accountEdit()).get_name());

        if(clickedOrder.get_accountAvailability() != -1)
            _accountAvailabilityText.setText(Finder.getAccount(clickedOrder.get_accountAvailability()).get_name());

        _remarkTextArea.setText(clickedOrder.get_remark());
    }

    private HBox getSearchBox()
    {
        HBox searchHBox = new HBox();
        TitledPane searchByClientTitledPane = new TitledPane();
        TitledPane searchByReceiptNumberTitPane = new TitledPane();
        _searchByClientTextField = new TextField();
        _searchByReceiptNumberTextField = new TextField();
        _searchByReceiptNumberTextField.focusedProperty().addListener((observable, oldValue, newValue) -> _searchByClientTextField.clear());
        _searchByReceiptNumberTextField.textProperty().addListener(getSearchByReceiptNumberListener(_searchByReceiptNumberTextField));


        searchByClientTitledPane.setText("Поиск по клиенту");
        searchByClientTitledPane.setExpanded(true);
        //searchByClientTitledPane.setCollapsible(false);
        searchByClientTitledPane.setContent(_searchByClientTextField);

        searchByReceiptNumberTitPane.setText("Поиск по номеру квитанции");
        searchByReceiptNumberTitPane.setExpanded(true);
        searchByReceiptNumberTitPane.setContent(_searchByReceiptNumberTextField);

        searchHBox.setSpacing(10);
        searchHBox.setPadding(new Insets(10,0,0,10));

        _searchByClientTextField.setPrefWidth(200);
        _searchByClientTextField.focusedProperty().addListener((observable, oldValue, newValue) -> _searchByReceiptNumberTextField.clear());
        _searchByClientTextField.textProperty().addListener((observable, oldValue, newValue) ->
                searchOrders(newValue));

        //searchHBox.getChildren().addAll(searchLabel, _searchOrdersTextField);
        searchHBox.getChildren().addAll(searchByClientTitledPane, searchByReceiptNumberTitPane);

        return searchHBox;
    }

    private AnchorPane getDollarRateBox()
    {
        AnchorPane dollarRateAnchorPane = new AnchorPane();
        HBox dollarRateHBox = new HBox();
        Label headDollRateLabel = new Label("Курс доллара по ЦБ: ");
        _dollarRateLabel = new Label();

        headDollRateLabel.setAlignment(Pos.CENTER_RIGHT);
        _dollarRateLabel.setAlignment(Pos.CENTER_RIGHT);

        if (_currDollarRate != null)
            _dollarRateLabel.setText(_currDollarRate.toString());
        else
            _dollarRateLabel.setText("ошибка подключения");

        dollarRateHBox.setPadding(new Insets(10,10,0,10));
        dollarRateHBox.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(dollarRateAnchorPane, Priority.ALWAYS);
        dollarRateHBox.getChildren().addAll(headDollRateLabel, _dollarRateLabel);

        dollarRateAnchorPane.getChildren().add(dollarRateHBox);
        AnchorPane.setTopAnchor(dollarRateHBox, 5.0);
        AnchorPane.setRightAnchor(dollarRateHBox, 5.0);
        AnchorPane.setBottomAnchor(dollarRateHBox, 5.0);
        return dollarRateAnchorPane;
    }

    private void searchOrders(String searchText)
    {
        _orderTableView.getItems().clear();
        _searchByReceiptNumberTextField.clear();
        String delimetr = " ";
        searchText = searchText.toLowerCase();
        char[] searchCharArray = searchText.toCharArray();

        for(Client cl : Finder.get_allClients())
        {
            String client = cl.get_name().toLowerCase();
            String[] clientWordsArray = client.split(delimetr); //массив слов Клиента

            label1: for (String word: clientWordsArray){
                char [] clientCharArray = word.toCharArray();

                for (int i=0; i<searchCharArray.length; i++)
                {
                    if (i==clientCharArray.length) break;
                    if (searchCharArray[i]!=clientCharArray[i]) break;
                    if (i==searchCharArray.length-1)
                    {
                        _orderTableView.getItems().addAll(getOrdersListByClient(cl.get_id()));
                        break label1;
                    }
                }
            }
        }

        if(_orderTableView.getItems().isEmpty())
        {
            if (_searchByClientTextField.getText().isEmpty())
                _orderTableView.getItems().addAll(Finder.get_allOrders());
            else
                _orderTableView.setPlaceholder(new Text("Поиск не дал результатов"));
        }
        clearOrderDetails();
        _clickedOrderIndex = -1;
        _orderTableView.scrollTo(_orderTableView.getItems().size() - 1);
    }

    private boolean searchByReceiptNumber(int number)
    {
        if (number == -1)
        {
            _orderTableView.getItems().addAll(Finder.get_allOrders());
            _orderTableView.scrollTo(_orderTableView.getItems().size() - 1);
            return false;
        }
        Receipt receipt = Finder.getReceipt(number);
        _orderTableView.getItems().clear();
        if (receipt != null)
        {
            for (final Order order : Finder.get_allOrders())
            {
                if (order.get_id() != receipt.get_orderId())
                    continue;
                _orderTableView.getItems().add(order);
                int indexInTable = _orderTableView.getItems().indexOf(order);
                _orderTableView.getSelectionModel().clearAndSelect(indexInTable);
                onClickOnRowOrder(order);
                break;
            }
        }
        else
        {
            _orderTableView.setPlaceholder(new Text("Поиск не дал результатов"));
        }
        return true;
    }

    private void searchClients(String searchText)
    {
        _clientsTableView.getItems().clear();
        String delimetr = " ";
        searchText = searchText.toLowerCase();
        char[] searchCharArray = searchText.toCharArray();

        for(Client cl:_activeClients){
            String client = cl.get_name().toLowerCase();
            String[] clientWordsArray = client.split(delimetr); //массив слов Клиента

            label1: for (String word: clientWordsArray){
                char [] clientCharArray = word.toCharArray();

                for (int i=0; i<searchCharArray.length; i++)
                {
                    if (i==clientCharArray.length) break;
                    if (searchCharArray[i]!=clientCharArray[i]) break;
                    if (i==searchCharArray.length-1)
                    {
                        _clientsTableView.getItems().add(cl);
                        break label1;
                    }
                }
            }
        }

        if(_clientsTableView.getItems().isEmpty())
        {
            if (_searchClientsTextField.getText().isEmpty())
            {
                _clientsTableView.getItems().addAll(_activeClients);
            } else
            {
                _clientsTableView.setPlaceholder(new Text("Поиск не дал результатов"));
            }
        }

        _clientsTableView.scrollTo(_clientsTableView.getItems().size() - 1);
    }

    private void printReceipt()
    {
        if(_orderTableView.getSelectionModel().getSelectedItem()!= null)
        {
            final Order order = _orderTableView.getSelectionModel().getSelectedItem();
            if (Print.toPrint(order, _mainStage))
            {
                final int indexInArray = Finder.get_allOrders().indexOf(order);
                final int indexInTableView = _orderTableView.getSelectionModel().getSelectedIndex();
                order.set_isPrintReceipt(true);
                Finder.get_allOrders().set(indexInArray, order);
                _orderTableView.getItems().set(indexInTableView, order);
                _orderTableView.getFocusModel().focus(_clickedOrderIndex);
                onClickOnRowOrder(order);
            }
        }
    }

    private ArrayList<Order> getOrdersListByClient(int idClient)
    {
        ArrayList<Order> ordersList = new ArrayList<>();

        for(Order order : Finder.get_allOrders())
            if(order.get_client() == idClient)
                ordersList.add(order);

        return ordersList;
    }

    private void clearOrderDetails()
    {
        _clientNameTextField.clear();
        _clientPhoneTextField.clear();
        _clientMailTextField.clear();
        _dateCreateText.setText("");
        _dateEditText.setText("");
        _dateAvailabilityText.setText("");
        _accountCreateText.setText("");
        _accountEditText.setText("");
        _accountAvailabilityText.setText("");
        _remarkTextArea.clear();
        _positionsTableView.getItems().clear();
    }

    static boolean checkClientInOrders(final int clientId)
    {
        for (Order order : Finder.get_allOrders())
            if(order.get_client() == clientId)
                return true;

        return false;
    }

    static boolean checkStaffInOrders(final int staffId)
    {
        for (Order order : Finder.get_allOrders())
            if(order.get_manager() == staffId || order.get_designer() == staffId)
                return true;

        return false;
    }

    static boolean showUpdateInfo(final String UPDATE_INFO, final String PRIMARY, final String SECONDARY)
    {
        String headerText = "Обновление "
                + PRIMARY
                + "." + SECONDARY + " :";
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Информация о обновлении");
        dialog.setHeaderText(headerText);
        dialog.setContentText(UPDATE_INFO);

        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(getIconLogo());

        ButtonType gotIt = new ButtonType("Окей, мне всё понятно");
        dialog.getButtonTypes().clear();
        dialog.getButtonTypes().addAll(gotIt);
        Optional<ButtonType> option = dialog.showAndWait();

        return option.filter(buttonType -> buttonType == gotIt).isPresent();
    }

    static boolean showSmile()
    {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Информация о обновлении");
        dialog.setHeaderText("Люба! Улыбнись! ");
        dialog.setContentText("Улыбайся! ;)");

        Stage alertStage = (Stage) dialog.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(getIconLogo());

        ButtonType smile = new ButtonType("Я улыбнулась");
        ButtonType noSmile = new ButtonType("Не хочу улыбаться :(");
        dialog.getButtonTypes().clear();
        dialog.getButtonTypes().addAll(smile, noSmile);
        Optional<ButtonType> option = dialog.showAndWait();

        return option.filter(buttonType -> buttonType == smile).isPresent();
    }

    private static DollarRate setDollarRate()
    {
        try
        {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("https://www.cbr-xml-daily.ru/daily.xml");
            Node root = document.getDocumentElement();
            String[] date = root.getAttributes().item(0).toString().split("\"");

            NodeList valutes = root.getChildNodes();
            for (int i = 0; i < valutes.getLength(); ++i)
            {
                Node valute = valutes.item(i);
                if (valute.getChildNodes().item(1).getTextContent().equals("USD"))
                {
                    System.out.println("Курс доллара: $" + valute.getChildNodes().item(4).getTextContent());
                    final String rateString = valute.getChildNodes().item(4).getTextContent();
                    try
                    {
                        //dollarRate.set_dollar(Double.parseDouble(rateString.replace(',', '.')));
                        final double doll = Double.parseDouble(rateString.replace(',', '.'));
                        DollarRate dollarRate = new DollarRate();
                        dollarRate.set_date(LocalDate.parse(date[1], _formatter));
                        dollarRate.set_dollar(doll);
                        return dollarRate;
                    } catch (NumberFormatException nfe)
                    {
                        nfe.printStackTrace();
                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void setRequestNotification()
    {
        _indicatorLabel = new Label();
        _notificationBtn = new Button("Новые заявки");
        int side = 15;
        _indicatorTimeLine = new Timeline (
                new KeyFrame (
                        Duration.millis(300),
                        ae -> { switchNotificationColor(); }));
        _indicatorTimeLine.setCycleCount(Timeline.INDEFINITE);
        _indicatorTimeLine.play();

        _notificationBtn.setGraphic(_indicatorLabel);


        _indicatorLabel.setMinSize(side, side);
        _indicatorLabel.setMaxSize(side, side);
        _indicatorLabel.setPrefSize(side, side);

        //_indicatorLabel.setStyle("-fx-background-color: #99ff00");
        _indicatorLabel.setStyle("-fx-background-color: #33ff00;");
        _indicatorLabel.setPadding(new Insets(0,5,0,5));

        _notificationBtn.setOnAction(event ->
        {
            _indicatorLabel.setStyle("-fx-background-color: #33ff00;");
            _indicatorTimeLine.pause();
            RequestsForm requestsForm = new RequestsForm();
            requestsForm.showAndWait(_mainStage);
            checkRequestsViewed();
        });

        _notificationHBox.getChildren().addAll(_notificationBtn);
        if (get_currentAccount() == 9 || get_currentAccount() == 7)
            checkRequestsViewed();
        else
            _notificationHBox.setVisible(false);
    }

    private void checkRequestsViewed()
    {
        boolean visible = false;
        for (final Request request : Finder.get_allRequests())
        {
            if (!request.is_viewed())
            {
                visible = true;
                break;
            }
        }

        if (visible)
            _indicatorTimeLine.play();
        else
            _indicatorTimeLine.stop();

        _notificationHBox.setVisible(visible);
    }

    private void switchNotificationColor()
    {
        if (_indicatorLabel.getStyle().equals("-fx-background-color: #ff0033;"))
            _indicatorLabel.setStyle("-fx-background-color: #33ff00;");
        else
            _indicatorLabel.setStyle("-fx-background-color: #ff0033;");

    }

    private void copyOrder(Order originalOrder, Order copyOrder)
    {
        copyOrder.set_date(Date.valueOf(LocalDate.now()));
        copyOrder.set_client(originalOrder.get_client());
        copyOrder.set_payment(PAYMENT_UNPAID);
        copyOrder.set_amount(originalOrder.get_amount());
        copyOrder.set_manager(originalOrder.get_manager());
        copyOrder.set_designer(originalOrder.get_designer());
        copyOrder.set_accountCreate(get_currentAccount());
        copyOrder.set_accountEdit(-1);
        copyOrder.set_accountAvailability(-1);
        copyOrder.set_availability(INWORK);
        copyOrder.set_dateTimeCreate(LocalDateTime.now());
        copyOrder.set_dateTimeEdit(null);
        copyOrder.set_dateTimeAvailability(null);
        copyOrder.set_remark(originalOrder.get_remark());
        copyOrder.set_isPrintReceipt(false);
    }

    static double toRuble(double price)
    {
        return price*_currDollarRate.get_dollar();
    }

    static double toDollar(double price)
    {
        return price/_currDollarRate.get_dollar();
    }
    static ArrayList<Client> getActiveClients(){return _activeClients;}
    static ArrayList<Staff> getActiveStaffs(){return _activeStaffs;}

    void set_currentAccount(int currentAccount) {_currentAccount = currentAccount;}
    static int get_currentAccount() {return _currentAccount;}

    private ChangeListener<String> getSearchByReceiptNumberListener(TextField txtpoint)
    {
        return (observable, oldValue, newValue) ->
        {
            int number = -1;
            if (!newValue.isEmpty())
            {
                try
                {
                    long pointI = Long.parseLong(newValue);
                    txtpoint.setText(String.valueOf(pointI));

                    if (!txtpoint.getText().isEmpty())
                        number = Integer.parseInt(txtpoint.getText());
                    searchByReceiptNumber(number);
                } catch (Exception e)
                {
                    txtpoint.clear();
                    txtpoint.setText(getNumber(oldValue));
                }
            }
            else
                searchByReceiptNumber(number);
        };
    }

    static ChangeListener<String> getInputOnlyNumbersListener(TextField txtpoint)
    {
        return (observable, oldValue, newValue) ->
        {
            if (!newValue.isEmpty())
            {
                try
                {
                    long pointI = Long.parseLong(newValue);
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
            return String.valueOf(Long.parseLong(value));
        } catch (Exception e)
        {
            String[] array = value.split("");
            for (String tab : array)
            {
                try
                {
                    n = n.concat(String.valueOf(Long.parseLong(String.valueOf(tab))));
                } catch (Exception ignored){}
            }
            return n;
        }
    }

    public class ToolTipCellFactory<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>>
    {
        public TableCell<S, T> call(TableColumn<S, T> param)
        {
            return new TableCell<S, T>()
            {
                @Override
                protected void updateItem(T item, boolean empty)
                {
                    super.updateItem(item, empty);
                    if (item==null)
                    {
                        setTooltip(null);
                        setText(null);
                    }else
                    {
                        Tooltip tooltip = new Tooltip();
                        tooltip.setAutoHide(false);
                        Label label = new Label(item.toString());
                        label.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 16));
                        tooltip.setGraphic(label);
                        setTooltip(tooltip);
                        setText(item.toString());
                    }
                }
            };
        }
    }

    static void getAlertErrorDialog(String text)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(text);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(getIconLogo());
        alertStage.showAndWait();
    }

    static void getAlertWarningDialog(String text)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText(null);
        alert.setContentText(text);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(getIconLogo());
        alertStage.showAndWait();
    }

    static void getAlertInformationDialog(String text)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Внимание!");
        alert.setHeaderText(null);
        alert.setContentText(text);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(getIconLogo());
        alertStage.showAndWait();
    }

    static boolean getAlertAskConfirmationDialog(String text)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setTitle("Вы уверены?");
        alert.setContentText(text);

        ButtonType buttonYes = new ButtonType("О да, мой господин!");
        ButtonType buttonNo = new ButtonType("У меня нету такой уверености");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(buttonYes, buttonNo);
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(getIconLogo());

        Optional<ButtonType> option = alert.showAndWait();
        //return option.get() == buttonYes;
        return option.filter(buttonType -> buttonType == buttonYes).isPresent();
    }

    static ImageView getMainLogo()
    {
        Image mainLogo = null;
        try
        {
            FileInputStream fs = new FileInputStream(DataBase.path + "\\src\\images\\mainLogo.jpg");
            mainLogo = new Image(fs);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(mainLogo);
        imageView.setFitHeight(50);
        imageView.setFitWidth(256);

        return imageView;
    }

    static Image getIconLogo()
    {
        Image logoImage = null;
        try
        {
            FileInputStream fs = new FileInputStream(DataBase.path + "\\src\\images\\OrderBaseLogo.png");
            logoImage = new Image(fs);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return logoImage;
    }

    void showcongratulations()
    {
        BorderPane borderPane = new BorderPane();
        Scene scene = new Scene(borderPane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Поздравляем!");
        stage.getIcons().add(getIconLogo());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(_mainStage);
        stage.setResizable(false);

        StackPane stackPane = new StackPane();
        StackPane topStackPane = new StackPane();
        StackPane centerStackPane = new StackPane();
        StackPane bottomStackPane = new StackPane();
        topStackPane.setAlignment(Pos.TOP_LEFT);
        centerStackPane.setAlignment(Pos.TOP_LEFT);
        bottomStackPane.setAlignment(Pos.TOP_LEFT);

        final Font headFont = Font.font("Segoe Script", FontWeight.BOLD, FontPosture.REGULAR, 18);
        final Font congrFont = Font.font("Segoe Script", FontWeight.NORMAL, FontPosture.REGULAR, 16);
        final Font signagureFont = Font.font("Segoe Script", FontWeight.NORMAL, FontPosture.ITALIC, 14);

        Text headText = new Text();
        Text congrText = new Text("Поздравляем с наступающим Женским Днём!\n\n" +
                "8 марта — это день, который напоминает каждому из нас, насколько важны женщины " +
                "с первого и до последнего мгновения нашей жизни, с их умеренностью, " +
                "материнской любовью и мудростью. Спасибо вам всем за наполнение этого " +
                "мира светом и радостью. Всегда оставайтесь такими же настойчивыми и смелыми," +
                " нежными и искренними. Вы пример для подражания всем сильным мужчинам, " +
                "чья решимость тает под лучезарным светом ваших глаз. С самой искренней " +
                "любовью к вам, поздравляем с 8 Марта!");
        Text signatureText = new Text("мужской коллектив EXPERT Print");
        headText.setFont(headFont);
        congrText.setFont(congrFont);
        signatureText.setFont(signagureFont);

        topStackPane.getChildren().addAll(headText);
        centerStackPane.getChildren().addAll(congrText);
        bottomStackPane.getChildren().addAll(signatureText);

        Image image = null;
        try
        {
            FileInputStream fs = new FileInputStream(DataBase.path + "\\src\\images\\8.png");
            image = new Image(fs);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

        ImageView backgroundImageView = new ImageView(image);
        backgroundImageView.setFitWidth(1024);
        backgroundImageView.setFitHeight(768);
        stackPane.setMaxSize(1024, 768);

        congrText.setWrappingWidth(470);
        centerStackPane.setPadding(new Insets(180,0,0,450));
        bottomStackPane.setPadding(new Insets(580,0,0,640));

        stackPane.getChildren().addAll(backgroundImageView, topStackPane, centerStackPane, bottomStackPane);
        borderPane.setCenter(stackPane);

        switch (_currentAccount)
        {
            case 8:
                headText.setText("Дорогая наша Любочка! ");
                topStackPane.setPadding(new Insets(120,0,0,550));
                break;
            case 9:
                headText.setText("Дорогая наша Елена Николаевна! ");
                topStackPane.setPadding(new Insets(120,0,0,530));
                break;
            case 13:
                headText.setText("Дорогая наша Таня! ");
                topStackPane.setPadding(new Insets(120,0,0,550));
                break;
            default:
                String text = "Дорогая наша " + Finder.getAccount(_currentAccount).get_name();
                headText.setText(text);
                topStackPane.setPadding(new Insets(120,0,0,550));
                break;
        }

        stage.showAndWait();
    }
}
