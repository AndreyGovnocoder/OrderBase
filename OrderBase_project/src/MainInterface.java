import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

class MainInterface
{
    private Stage _mainStage;
    private Scene _mainScene;
    private static int _currentAccount;
    static private ArrayList<Order> _allOrders;
    static private ArrayList<Client> _allClients;
    static private ArrayList<Staff> _allStaffs;
    static private ArrayList<Account> _allAccounts;
    static private ArrayList<OrderPosition> _allOrdersPositions;
    private TableView<Order> _orderTableView;
    private TableView<OrderPosition> _positionsTableView;
    private TableView<Client> _clientsTableView;
    private static int _clickedOrderIndex = -1;
    static DateTimeFormatter _formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static DateTimeFormatter _formatterTime = DateTimeFormatter.ofPattern("HH:mm");
    private TextArea _remarkTextArea;
    private TextField _clientNameTextField;
    private TextField _clientPhoneTextField;
    private TextField _clientMailTextField;
    private TextField _clientContactPersonTextField;
    private TextField _searchOrdersTextField;
    private TextField _searchClientsTextField;
    private Text _dateCreateText;
    private Text _dateEditText;
    private Text _dateAvailabilityText;
    private Text _accountCreateText;
    private Text _accountEditText;
    private Text _accountAvailabilityText;

    MainInterface(Stage stage)
    {
        this._mainStage = stage;
        _mainStage.setOnCloseRequest(event -> DataBase.closeConnection());
        _allOrders = DataBase.getOrdersList();
        _allClients = DataBase.getClientsList();
        _allStaffs = DataBase.getStaffsList();
        _allAccounts = DataBase.getAccountsList();
        _allOrdersPositions = DataBase.getOrderPositionsList();
    }

    void show()
    {
        BorderPane mainBorderPane = new BorderPane();
        _mainScene = new Scene(mainBorderPane, 890, 900);
        mainBorderPane.setTop(getTop());
        mainBorderPane.setCenter(getCenter());
        mainBorderPane.setBottom(getBottom());

        _mainStage.setTitle("EXPERTPrint: База заказов     Аккаунт: " + DataBase.getAccount(get_currentAccount()).get_name());
        _mainStage.getIcons().add(getIconLogo());
        _mainStage.setScene(_mainScene);
        _mainStage.show();
    }

    private MenuBar getTop()
    {
        MenuBar mainMenuBar = new MenuBar();
        Menu staffsMenu = new Menu("Сотрудники");
        MenuItem staffsListMenuItem = new MenuItem("Список сотрудников");
        staffsListMenuItem.setOnAction(event -> showStaffsForm());
        staffsMenu.getItems().add(staffsListMenuItem);
        Menu clientsMenu = new Menu("Клиенты");
        MenuItem clientsListMenuItem = new MenuItem("Список клиентов");
        clientsListMenuItem.setOnAction(event -> showClientsForm());
        clientsMenu.getItems().add(clientsListMenuItem);
        mainMenuBar.getMenus().addAll(clientsMenu, staffsMenu);
        return mainMenuBar;
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        VBox orderTableViewVBox = new VBox();
        HBox orderDetailsHBox = new HBox();
        VBox detailsLeftVBox = new VBox();
        VBox detailsRightVBox = new VBox();

        detailsLeftVBox.setPrefWidth(_mainScene.getWidth()/2 - 10);
        detailsLeftVBox.setSpacing(10);
        detailsLeftVBox.getChildren().addAll(get_positionsTableView(), getRemarkPane());

        detailsRightVBox.setPrefWidth(_mainScene.getWidth()/2-10);
        detailsRightVBox.setSpacing(10);
        detailsRightVBox.getChildren().addAll(getClientPane(), getOrderChangesPane());

        orderTableViewVBox.getChildren().add(get_orderTableView());

        orderDetailsHBox.setPrefHeight(_mainScene.getHeight()/2);
        orderDetailsHBox.getChildren().addAll(detailsLeftVBox, detailsRightVBox);

        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(getSearchBox(), orderTableViewVBox, orderDetailsHBox, new Separator());

        HBox.setMargin(detailsLeftVBox, new Insets(10));
        HBox.setMargin(detailsRightVBox, new Insets(10));
        VBox.setMargin(_orderTableView, new Insets(10));
        HBox.setHgrow(detailsLeftVBox, Priority.ALWAYS);
        HBox.setHgrow(detailsRightVBox, Priority.ALWAYS);
        return centerVBox;
    }

    private AnchorPane getBottom()
    {
        AnchorPane buttonsPane = new AnchorPane();
        HBox buttonsHBox = new HBox();
        HBox printBtnHBox = new HBox();
        Button createOrderButton = new Button("Создать заказ");
        Button editOrderButton = new Button("Редактировать заказ");
        Button deleteOrderButton = new Button("Удалить заказ");
        Button printReceiptButton = new Button("Печать квитанции");
        /////////////////

        createOrderButton.setOnAction(event ->
        {
            OrderDialog orderDialog = new OrderDialog();
            orderDialog.show(_mainStage);
            if(orderDialog._ok)
            {
                clearOrderDetails();
                DataBase.addOrder(orderDialog.get_order());
                orderDialog.get_order().set_id(DataBase.getLastId(DataBase.ORDERS_TABLE));
                for(OrderPosition position : orderDialog.getOrderPositionsList())
                {
                    position.set_idOrder(orderDialog.get_order().get_id());
                    DataBase.addOrderPosition(position);
                }
                _allOrders.add(orderDialog.get_order());
                _allOrdersPositions.addAll(orderDialog.getOrderPositionsList());
                _orderTableView.getItems().add(orderDialog.get_order());
                _orderTableView.scrollTo(orderDialog.get_order());
            }
        });

        editOrderButton.setOnAction(event ->
        {
            OrderDialog orderDialog = new OrderDialog(_orderTableView.getFocusModel().getFocusedItem());
            orderDialog.show(_mainStage);
            if(orderDialog._ok)
            {
                clearOrderDetails();
                DataBase.editOrder(orderDialog.get_order());
                DataBase.removeOrderPositions(orderDialog.get_order().get_id());
                _allOrders.remove(_orderTableView.getFocusModel().getFocusedItem());
                _allOrders.add(orderDialog.get_order());
                _allOrdersPositions.removeAll(getOrdersPositionsList(orderDialog.get_order().get_id()));

                _orderTableView.getItems().set(_orderTableView.getFocusModel().getFocusedIndex(), orderDialog.get_order());
                for(OrderPosition position : orderDialog.getOrderPositionsList())
                {
                    position.set_idOrder(orderDialog.get_order().get_id());
                    DataBase.addOrderPosition(position);
                    _allOrdersPositions.add(position);
                }

                _orderTableView.scrollTo(orderDialog.get_order());
                _orderTableView.getFocusModel().focus(_clickedOrderIndex);
                onClickOnRowOrder(orderDialog.get_order());
            }
        });

        deleteOrderButton.setOnAction(event ->
        {
            String question = "Вы уверены, что хотите удалить заказ?";
            int idOrder = _orderTableView.getFocusModel().getFocusedItem().get_id();
            if(_clickedOrderIndex != -1 && getAlertAskConfirmationDialog(question))
            {
                _orderTableView.getItems().remove(_clickedOrderIndex);
                DataBase.removeObject(idOrder, DataBase.ORDERS_TABLE);
                DataBase.removeOrderPositions(idOrder);
                _orderTableView.scrollTo(_orderTableView.getItems().size()-1);
                clearOrderDetails();
            }
        });

        printReceiptButton.setOnAction(event ->
        {
            Print.toPrint(_orderTableView.getFocusModel().getFocusedItem(), _mainStage);
        });

        buttonsHBox.setPadding(new Insets(15));
        buttonsHBox.setSpacing(10);
        buttonsHBox.getChildren().addAll(
                createOrderButton,
                editOrderButton,
                deleteOrderButton);

        printBtnHBox.setAlignment(Pos.CENTER);
        printBtnHBox.setPadding(new Insets(0,5,0,0));
        printBtnHBox.getChildren().addAll(printReceiptButton);

        buttonsPane.setStyle("-fx-background-color: #ADD8E6");
        buttonsPane.getChildren().addAll(buttonsHBox, printBtnHBox);
        AnchorPane.setTopAnchor(buttonsHBox, 0.0);
        AnchorPane.setLeftAnchor(buttonsHBox, 5.0);
        AnchorPane.setBottomAnchor(buttonsHBox, 0.0);
        AnchorPane.setTopAnchor(printBtnHBox, 0.0);
        AnchorPane.setRightAnchor(printBtnHBox, 5.0);
        AnchorPane.setBottomAnchor(printBtnHBox, 0.0);

        return buttonsPane;
    }

    private TableView<Order> get_orderTableView()
    {
        _orderTableView = new TableView<>();
        _orderTableView.setPrefHeight(_mainScene.getHeight()/2);
        TableColumn<Order, java.sql.Date> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.076));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_date"));
        dateCol.setCellFactory(tc -> new TableCell<Order, java.sql.Date>()
        {
            @Override
            protected void updateItem(java.sql.Date date, boolean empty)
            {
                super.updateItem(date, empty);
                if (empty)
                {
                    setText(null);
                } else {
                    setText(_formatter.format(date.toLocalDate()));
                }
            }
        });
        TableColumn<Order, Integer> clientCol = new TableColumn<>("Заказчик");
        clientCol.prefWidthProperty().bind(_mainScene.widthProperty().multiply(0.42));
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
                    setText(getClient(clientId).get_name());
                }
            }
        });

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
                    setText(getStaff(managerId).get_name());
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
                    setText(getStaff(designerId).get_name());
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
        _orderTableView.getColumns().addAll(dateCol,clientCol, paymentCol, amountCol, managerCol, designerCol, availabilityCol);
        _orderTableView.setStyle("");
        ContextMenu contextMenu = new ContextMenu();

        _orderTableView.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>()
        {
            @Override
            public TableRow<Order> call(TableView<Order> param) {
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
                            if (order != null && order.get_availability().equals("Готово"))
                            {
                                this.setStyle("-fx-background-color: #3cb380");
                            } else this.setStyle("");
                        } else
                            {
                            this.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;");
                        }
                    }
                };

                row.setOnMouseClicked(event ->
                {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1)
                    {

                        onClickOnRowOrder(row.getItem());

                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY))
                    {
                        row.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>()
                        {
                            @Override
                            public void handle(ContextMenuEvent event)
                            {
                                if(row.getItem()!=null)
                                {
                                    setAvailabilityContextMenu(contextMenu).show(row, event.getScreenX() + 10, event.getScreenY() + 5);
                                    //setAvailabilityContextMenu(row.getItem(), contextMenu, tableViewOrders.getFocusModel().getFocusedIndex()).show(row, event.getScreenX() + 10, event.getScreenY() + 5);
                                }
                            }
                        });
                    }
                });
                return row;
            }
        });

        if(_clickedOrderIndex>=0) _orderTableView.scrollTo(_clickedOrderIndex);
        else _orderTableView.scrollTo(_orderTableView.getItems().size()-1);
        _orderTableView.setItems(FXCollections.observableArrayList(_allOrders));
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
        descriptionCol.setCellFactory(new ToolTipCellFactory<>());
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

        _clientNameTextField.setEditable(false);
        _clientNameTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientNameTextField.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");
        _clientPhoneTextField.setEditable(false);
        _clientPhoneTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientPhoneTextField.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");
        _clientMailTextField.setEditable(false);
        _clientMailTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientMailTextField.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");
        _clientContactPersonTextField.setEditable(false);
        _clientContactPersonTextField.setPrefWidth(_mainScene.getWidth()/2);
        _clientContactPersonTextField.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");

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
            Staff staff = staffsTableView.getFocusModel().getFocusedItem();
            staffsTableView.getItems().remove(staff);
            _allStaffs.remove(staff);
            DataBase.removeObject(staff.get_id(), DataBase.STAFFS_TABLE);
        });

        menuItemEditStaff.setOnAction(event ->
        {
            Staff staff = staffsTableView.getFocusModel().getFocusedItem();
            int tableIndex = staffsTableView.getFocusModel().getFocusedIndex();
            int arrayIndex = _allStaffs.indexOf(staff);
            StaffDialog staffDialog = new StaffDialog(staff);
            staffDialog.show(staffsStage);
            if(staffDialog._ok)
            {
                staffsTableView.getItems().set(tableIndex, staffDialog.get_staff());
                _allStaffs.set(arrayIndex, staffDialog.get_staff());
                DataBase.editStaff(staff);
            }
        });

        addStaffButton.setOnAction(event ->
        {
            StaffDialog staffDialog = new StaffDialog();
            staffDialog.show(staffsStage);
            if(staffDialog._ok)
            {
                DataBase.addStaff(staffDialog.get_staff());
                staffDialog.get_staff().set_id(DataBase.getLastId(DataBase.STAFFS_TABLE));
                staffsTableView.getItems().add(staffDialog.get_staff());
                _allStaffs.add(staffDialog.get_staff());
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
        staffsTableView.setItems(FXCollections.observableArrayList(_allStaffs));
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

        centerVBox.getChildren().addAll(staffsTableView);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        VBox.setVgrow(staffsTableView, Priority.ALWAYS);
        VBox.setMargin(staffsTableView, new Insets(10));

        staffsBorderPane.setBottom(buttonsPane);
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
        VBox centerVBox = new VBox();
        Scene clientsScene = new Scene(clientsBorderPane, 900,600);
        _clientsTableView = new TableView<>();
        _searchClientsTextField = new TextField();
        Label searchClientLabel = new Label("Поиск клиентов");
        HBox searchClientHBox = new HBox();
        ContextMenu contextMenuClients = new ContextMenu();
        MenuItem menuItemDeleteClient = new MenuItem("Удалить");
        MenuItem menuItemEditClient = new MenuItem("Редактировать");
        Button addClientButton = new Button("Добавить клиента");
        Button closeButton = new Button("Закрыть");

        _searchClientsTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchClients(newValue);
        });

        searchClientHBox.setSpacing(10);
        searchClientHBox.setAlignment(Pos.CENTER_LEFT);
        searchClientHBox.setPadding(new Insets(10,0,10,10));
        searchClientHBox.getChildren().addAll(searchClientLabel, _searchClientsTextField);

        menuItemDeleteClient.setOnAction(event ->
        {
            Client client = _clientsTableView.getFocusModel().getFocusedItem();
            _clientsTableView.getItems().remove(client);
            _allClients.remove(client);
            DataBase.removeObject(client.get_id(), DataBase.CLIENTS_TABLE);
        });

        menuItemEditClient.setOnAction(event ->
        {
            Client client = _clientsTableView.getFocusModel().getFocusedItem();
            int tableIndex = _clientsTableView.getFocusModel().getFocusedIndex();
            int arrayIndex = _allClients.indexOf(client);

            ClientDialog clientDialog = new ClientDialog(client);
            clientDialog.show(clientsStage);
            if(clientDialog._ok)
            {
                _clientsTableView.getItems().set(tableIndex, clientDialog.get_client());
                _allClients.set(arrayIndex, clientDialog.get_client());
                DataBase.editClient(client);
            }
        });

        addClientButton.setOnAction(event ->
        {
            ClientDialog clientDialog = new ClientDialog();
            clientDialog.show(clientsStage);
            if(clientDialog._ok)
            {
                DataBase.addClient(clientDialog.get_client());
                clientDialog.get_client().set_id(DataBase.getLastId(DataBase.CLIENTS_TABLE));
                _clientsTableView.getItems().add(clientDialog.get_client());
                _allClients.add(clientDialog.get_client());
                _clientsTableView.scrollTo(_clientsTableView.getItems().size() - 1);
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
        _clientsTableView.setItems(FXCollections.observableArrayList(_allClients));
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

        clientsBorderPane.setTop(searchClientHBox);
        clientsBorderPane.setBottom(buttonsPane);
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

    private ContextMenu setAvailabilityContextMenu(ContextMenu contextMenu)
    {
        Order order = _orderTableView.getFocusModel().getFocusedItem();
        MenuItem setAvailability;
        Menu setPayment = new Menu("Оплата");
        MenuItem payment1 = new MenuItem();
        MenuItem payment2 = new MenuItem();
        SeparatorMenuItem separator = new SeparatorMenuItem();
        setPayment.getItems().clear();
        setPayment.getItems().addAll(payment1, payment2);

        setAvailability = new MenuItem();
        switch (order.get_availability()){
            case "В работе":
                setAvailability.setText("Готово");
                break;

            case "Готово":
                setAvailability.setText("В работе");
                break;
        }
        setAvailability.setOnAction(event ->
        {
            order.set_availability(setAvailability.getText());
            order.set_accountAvailability(get_currentAccount());
            order.set_dateTimeAvailability(LocalDateTime.now());
            _orderTableView.getItems().set(_clickedOrderIndex, order);
            _allOrders.set(_allOrders.indexOf(order), order);
            DataBase.editOrder(order);
            onClickOnRowOrder(order);
        });

        switch (order.get_payment()){
            case "Оплачено":
                payment1.setText("Не оплачено");
                payment2.setText("50%");
                break;
            case "Не оплачено":
                payment1.setText("Оплачено");
                payment2.setText("50%");
                break;
            case "50%":
                payment1.setText("Оплачено");
                payment2.setText("Не оплачено");
                break;
        }

        payment1.setOnAction(event ->
        {
            order.set_payment(payment1.getText());
            order.set_accountEdit(get_currentAccount());
            order.set_dateTimeEdit(LocalDateTime.now());
            _orderTableView.getItems().set(_clickedOrderIndex, order);
            _allOrders.set(_allOrders.indexOf(order), order);
            DataBase.editOrder(order);
            onClickOnRowOrder(order);
        });

        payment2.setOnAction(event ->
        {
            order.set_payment(payment2.getText());
            order.set_accountEdit(get_currentAccount());
            order.set_dateTimeEdit(LocalDateTime.now());
            _orderTableView.getItems().set(_clickedOrderIndex, order);
            _allOrders.set(_allOrders.indexOf(order), order);
            DataBase.editOrder(order);
            onClickOnRowOrder(order);
        });

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(setAvailability, separator, setPayment);
        return contextMenu;
    }

    private void onClickOnRowOrder(Order clickedOrder)
    {
        clearOrderDetails();
        _clickedOrderIndex = _orderTableView.getFocusModel().getFocusedIndex();

        _positionsTableView.getItems().clear();
        for(OrderPosition position : _allOrdersPositions)
        {
            if(position.get_idOrder() == clickedOrder.get_id())
            {
                _positionsTableView.getItems().add(position);
            }
        }

        Client client = getClient(clickedOrder.get_client());
        _clientNameTextField.setText(client.get_name());
        _clientPhoneTextField.setText(client.get_phone());
        _clientMailTextField.setText(client.get_mail());
        _clientContactPersonTextField.setText(client.get_contactPerson());

        if(clickedOrder.get_dateTimeCreate() != null)
        {
            _dateCreateText.setText(
                    clickedOrder.get_dateTimeCreate().toLocalDate().format(_formatter) + "\n" +
                    clickedOrder.get_dateTimeCreate().toLocalTime().format(_formatterTime));
        }

        if(clickedOrder.get_dateTimeEdit() != null)
        {
            _dateEditText.setText(
                    clickedOrder.get_dateTimeEdit().toLocalDate().format(_formatter) + "\n" +
                    clickedOrder.get_dateTimeEdit().toLocalTime().format(_formatterTime));
        }

        if(clickedOrder.get_dateTimeAvailability() != null)
        {
            _dateAvailabilityText.setText(
                    clickedOrder.get_dateTimeAvailability().toLocalDate().format(_formatter) + "\n" +
                    clickedOrder.get_dateTimeAvailability().toLocalTime().format(_formatterTime));
        }

        if(clickedOrder.get_accountCreate() != -1)
        {
            _accountCreateText.setText(getAccount(clickedOrder.get_accountCreate()).get_name());
        }

        if(clickedOrder.get_accountEdit() != -1)
        {
            _accountEditText.setText(getAccount(clickedOrder.get_accountEdit()).get_name());
        }

        if(clickedOrder.get_accountAvailability() != -1)
        {
            _accountAvailabilityText.setText(getAccount(clickedOrder.get_accountAvailability()).get_name());
        }

        _remarkTextArea.setText(clickedOrder.get_remark());
    }

    private HBox getSearchBox()
    {
        HBox searchHBox = new HBox();
        Label searchLabel = new Label("Поиск по клиенту: ");
        _searchOrdersTextField = new TextField();

        searchHBox.setSpacing(10);
        searchHBox.setPadding(new Insets(10,0,0,10));

        _searchOrdersTextField.setPrefWidth(200);
        _searchOrdersTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            searchOrders(newValue);
        });

        searchLabel.setAlignment(Pos.CENTER);
        searchLabel.setMinHeight(23.5);

        searchHBox.getChildren().addAll(searchLabel, _searchOrdersTextField);

        return searchHBox;
    }

    private void searchOrders(String searchText)
    {
        _orderTableView.getItems().clear();
        String delimetr = " ";
        searchText = searchText.toLowerCase();
        char[] searchCharArray = searchText.toCharArray();

        for(Client cl:_allClients){
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
            if (_searchOrdersTextField.getText().isEmpty())
            {
                _orderTableView.getItems().addAll(_allOrders);
            } else
                {
                _orderTableView.setPlaceholder(new Text("Поиск не дал результатов"));
            }
        }
        clearOrderDetails();
        _clickedOrderIndex = -1;
        _orderTableView.scrollTo(_orderTableView.getItems().size() - 1);
    }

    private void searchClients(String searchText)
    {
        _clientsTableView.getItems().clear();
        String delimetr = " ";
        searchText = searchText.toLowerCase();
        char[] searchCharArray = searchText.toCharArray();

        for(Client cl:_allClients){
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
                _clientsTableView.getItems().addAll(_allClients);
            } else
            {
                _clientsTableView.setPlaceholder(new Text("Поиск не дал результатов"));
            }
        }

        _clientsTableView.scrollTo(_clientsTableView.getItems().size() - 1);
    }

    static void refreshDataLists()
    {
        _allOrders.clear();
        _allClients.clear();
        _allStaffs.clear();
        _allAccounts.clear();
        _allOrdersPositions.clear();
        _allOrders = DataBase.getOrdersList();
        _allClients = DataBase.getClientsList();
        _allStaffs = DataBase.getStaffsList();
        _allAccounts = DataBase.getAccountsList();
        _allOrdersPositions = DataBase.getOrderPositionsList();
    }

    private ArrayList<Order> getOrdersListByClient(int idClient)
    {
        ArrayList<Order> ordersList = new ArrayList<>();

        for(Order order : _allOrders)
        {
            if(order.get_client() == idClient) ordersList.add(order);
        }

        return ordersList;
    }

    static Client getClient(int idClient)
    {
        Client client = new Client();
        for(Client clnt : _allClients)
        {
            if(clnt.get_id() == idClient) client = clnt;
        }
        return client;
    }

    static Staff getStaff(int idStaff)
    {
        Staff staff = new Staff();
        for(Staff stff : _allStaffs)
        {
            if(stff.get_id() == idStaff) staff = stff;
        }
        return staff;
    }

    static ArrayList<OrderPosition> getOrdersPositionsList(int idOrder)
    {
        ArrayList<OrderPosition> orderPositionsList = new ArrayList<>();
        for(OrderPosition position : _allOrdersPositions)
        {
            if(position.get_idOrder() == idOrder) orderPositionsList.add(position);
        }
        return orderPositionsList;
    }

    private Account getAccount(int idAccount)
    {
        Account account = new Account();
        for(Account acc : _allAccounts)
        {
            if(acc.get_id() == idAccount) account = acc;
        }

        return account;
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

    static ArrayList<Order> getAllOrders(){return _allOrders;}
    static ArrayList<Client> getAllClients(){return _allClients;}
    static ArrayList<Staff> getAllStaffs(){return _allStaffs;}
    static ArrayList<Account> getAllAccounts(){return _allAccounts;}
    static ArrayList<OrderPosition> getAllOrdersPositions(){return _allOrdersPositions;}
    void set_currentAccount(int currentAccount) {_currentAccount = currentAccount;}
    static int get_currentAccount() {return _currentAccount;}

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
        return option.get() == buttonYes;

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

}
