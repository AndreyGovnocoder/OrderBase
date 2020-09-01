import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
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

import java.awt.desktop.SystemEventListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

public class MainInterface {
    private Stage stageClients;
    private static Account account;
    private static Stage mainStage;
    private ListView<Staff> staffsListView;
    private static TableView<Client> tableClients;
    static ArrayList<Order> allOrders;
    static ArrayList<Order> foundOrders;
    static TableView<Order> tableViewOrders = new TableView<>();
    private static TableView<OrdersPosition> tableViewPositions = new TableView<>();
    private ObservableList<Staff> staffs;
    private static Scene mainScene;
    static HBox hBoxPositionsAndClient;
    static TextField searchTextField = new TextField();
    private static StackPane stackPaneOrders = new StackPane();
    private static VBox vBoxOrderCard = new VBox();
    static String db;
    static LocalDate localDate = LocalDate.now();
    static int year;
    static int month;
    private static ObservableList<Integer> yearList = FXCollections.observableArrayList(2018,2019,2020,2021,2022,2023,2024,2025);
    Button pressedButton;
    private static int clickedOrderIndex=-1;
    private static Text placeHolderText = new Text("База заказов пуста");

    VBox vBox;

    MainInterface(Account account, String db){
        MainInterface.account = account;
        MainInterface.db = db;
    }

    MainInterface(String db){
        MainInterface.db = db;
    }

    void go(){
        year = localDate.getYear();
        month = localDate.getMonthValue();
        allOrders = new ArrayList<>(DataBaseHelper.getOrdersList());
        foundOrders = new ArrayList<>();

        mainStage = new Stage();

        BorderPane mainBorderPane = new BorderPane();

        mainScene = new Scene(mainBorderPane, 890, 900);
        mainBorderPane.setBottom(setBottom());
        mainBorderPane.setCenter(setCenter());
        clickedRowOrder(new Order());
        mainBorderPane.setTop(getMainMenuBar());

        mainBorderPane.setPrefSize(mainScene.getWidth(), mainScene.getHeight());
        //mainStage.setResizable(false);
        mainStage.setTitle("База заказов ExpertPRINT");
        mainStage.setScene(mainScene);
        //mainStage.getIcons().addAll(getLogo());
        mainStage.show();
    }

    private BorderPane setCenter(){

        BorderPane borderPaneCenter;
        borderPaneCenter = new BorderPane();

        vBox = new VBox();
        vBox.setPadding(new Insets(10));
        vBox.setPrefWidth(mainScene.getWidth());
        vBox.setSpacing(10);
        hBoxPositionsAndClient = new HBox();
        hBoxPositionsAndClient.setSpacing(20);
        hBoxPositionsAndClient.setPrefWidth(mainScene.getWidth());
        //hBoxPositionsAndClient.setStyle("-fx-border-color: yellow");
        hBoxPositionsAndClient.setAlignment(Pos.TOP_CENTER);

        tableViewPositions.setPrefWidth(vBox.getPrefWidth()/2-10);
        tableViewPositions.setPrefHeight(mainScene.getHeight()/3);
        tableViewPositions.setPlaceholder(new Text("Выберите заказ для отображения позиций"));


        TableColumn<OrdersPosition, String> descriptionCol = new TableColumn<>("Позиция");
        //descriptionCol.setPrefWidth(tableViewPositions.getPrefWidth()/1.6);
        descriptionCol.prefWidthProperty().bind(tableViewPositions.widthProperty().multiply(0.58));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionCol.setCellFactory(new ToolTipCellFactory<>());
        TableColumn<OrdersPosition, String> quantityCol = new TableColumn<>("Количество");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        //quantityCol.setPrefWidth(80);
        quantityCol.prefWidthProperty().bind(tableViewPositions.widthProperty().multiply(0.2));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrdersPosition, String> issueCol = new TableColumn<>("Выдача");
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        issueCol.setStyle("-fx-alignment: CENTER;");
        //issueCol.setPrefWidth(70);
        issueCol.prefWidthProperty().bind(tableViewPositions.widthProperty().multiply(0.18));
        tableViewPositions.getColumns().addAll(descriptionCol, quantityCol, issueCol);

        //tableViewOrders = getTableViewOrders();
        setTableViewOrders(tableViewOrders, DataBaseHelper.getOrdersList(), placeHolderText);
        stackPaneOrders.setPrefWidth(mainScene.getWidth());
        stackPaneOrders.getChildren().addAll(tableViewOrders);
        refreshOrdersList();

        Button testBtn = new Button("test");
        testBtn.setOnAction(event ->
        {
            if(DataBaseSQLite.testConnection())
            {
                System.out.println("тест соединения с sqlite успешен");

            }
            else
            {
                System.out.println("тетс соединения с sqlite неуспешен");
            }
        });

        vBox.getChildren().addAll(testBtn, getSortButtons(), getSearchBox(), stackPaneOrders, new Separator(), hBoxPositionsAndClient);
        borderPaneCenter.setCenter(vBox);
        return borderPaneCenter;
    }

    HBox getSortButtons(){
        HBox hBox = new HBox();
        hBox.setSpacing(1);
        ComboBox<Integer> comboBoxYear = new ComboBox<Integer>(yearList);
        comboBoxYear.setValue(localDate.getYear());
        Button buttonAllMonth = new Button("Все месяцы");
        buttonAllMonth.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 0;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth1 = new Button("Январь");
        buttonMonth1.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 1;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth2 = new Button("Февраль");
        buttonMonth2.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 2;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth3 = new Button("Март");
        buttonMonth3.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 3;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth4 = new Button("Апрель");
        buttonMonth4.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 4;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth5 = new Button("Май");
        buttonMonth5.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 5;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth6 = new Button("Июнь");
        buttonMonth6.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 6;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth7 = new Button("Июль");
        buttonMonth7.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 7;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth8 = new Button("Август");
        buttonMonth8.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 8;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth9 = new Button("Сентябрь");
        buttonMonth9.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 9;
            searchTextField.clear();
            refreshOrdersList();

        });
        Button buttonMonth10 = new Button("Октябрь");
        buttonMonth10.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 10;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth11 = new Button("Ноябрь");
        buttonMonth11.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 11;
            searchTextField.clear();
            refreshOrdersList();
        });
        Button buttonMonth12 = new Button("Декабрь");
        buttonMonth12.setOnAction(event -> {
            year = comboBoxYear.getValue();
            month = 12;
            searchTextField.clear();
            refreshOrdersList();
        });

        hBox.getChildren().addAll(comboBoxYear, buttonAllMonth, buttonMonth1, buttonMonth2, buttonMonth3,
                buttonMonth4, buttonMonth5, buttonMonth6, buttonMonth7, buttonMonth8, buttonMonth9, buttonMonth10,
                buttonMonth11, buttonMonth12);

        return hBox;
    }

    HBox getSearchBox(){
        HBox searchHBox = new HBox();
        searchHBox.setSpacing(10);
        searchTextField.setPrefWidth(200);
        Label searchLabel = new Label("Поиск по клиенту: ");
        searchLabel.setAlignment(Pos.CENTER);
        searchLabel.setMinHeight(23.5);
        //searchButton.setPrefSize(120,40);
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            search(newValue);
        });
        searchHBox.getChildren().addAll(searchLabel,searchTextField);

        return searchHBox;
    }

    static void search(String searchText){
        foundOrders.clear();
        stackPaneOrders.getChildren().clear();
        hBoxPositionsAndClient.getChildren().clear();
        String delimetr = " ";
        searchText = searchText.toLowerCase();
        char[] searchCharArray = searchText.toCharArray();

        for(Order order:allOrders){
            String client = order.getClient().getClient().toLowerCase();
            String[] clientWordsArray = client.split(delimetr); //массив слов Клиента

            label1: for (String word: clientWordsArray){
                char [] clientCharArray = word.toCharArray();

                for (int i=0; i<searchCharArray.length; i++){
                    if (i==clientCharArray.length) break;
                    if (searchCharArray[i]!=clientCharArray[i]) break;
                    if (i==searchCharArray.length-1){
                        foundOrders.add(order);
                        break label1;
                    }
                }
            }
        }

        if(foundOrders.isEmpty()) {
            if (searchTextField.getText().isEmpty()) {
                setTableViewOrders(tableViewOrders, allOrders, placeHolderText);
            } else {
                setTableViewOrders(tableViewOrders, foundOrders, new Text("Поиск не дал результатов"));
            }
        } else {
            setTableViewOrders(tableViewOrders, foundOrders, placeHolderText);

        }
            stackPaneOrders.getChildren().addAll(tableViewOrders);
            clickedRowOrder(new Order());
            clickedOrderIndex = -1;

       /*

        for (Order order: allOrders){
            String delimetr = " ";
            String searchString = "";
            String clientString = "";
            int count1 = 0;
            int count2 = 0;
            int count3 = 0;
            int length = 0;
            String[] foundArr = searchText.split(delimetr);
            String[] clientArr = order.getClient().getClient().split(delimetr);
            for (String str : foundArr) searchString += str;
            for (String str : clientArr) clientString += str;
            clientString.toLowerCase();

            char[] searchStringCharArr = searchString.toCharArray();
            char[] clientStringCharArr = clientString.toCharArray();

            if (searchStringCharArr.length >= clientStringCharArr.length) length = clientStringCharArr.length;
            else length = searchStringCharArr.length;

            for (int i=0; i<length; i++){
                if(i==searchStringCharArr.length || i==clientStringCharArr.length) break;
                else {
                    if (searchStringCharArr[i] == clientStringCharArr[i]) {
                        count1++;
                    }

                    if ((i+1) == searchStringCharArr.length || (i+1) == clientStringCharArr.length) break;
                    else {
                        if (searchStringCharArr[i + 1] == clientStringCharArr[i]) {
                            count2++;
                        }
                    }

                    if(i!=0) {
                        if ((i - 1) == searchStringCharArr.length || (i - 1) == clientStringCharArr.length) break;
                        else {
                            System.out.println(searchStringCharArr[i-1] + " - "+ clientStringCharArr[i]);
                            if (searchStringCharArr[i - 1] == clientStringCharArr[i]) {
                                count3++;
                            }
                        }
                    }
                }
            }
            System.out.println("count1: "+count1);
            System.out.println("count2: "+count2);
            System.out.println("count3: "+count3);
            System.out.println("clientStringCharArr.length/2 = "+clientStringCharArr.length/2);
            System.out.println(count3 + " - " + clientStringCharArr.length/2);
            if (count1>=clientStringCharArr.length/2 && count1>=searchStringCharArr.length/2){
                foundOrders.add(order);
            } else if (count2>=clientStringCharArr.length/2 && count2>=searchStringCharArr.length/2){
                foundOrders.add(order);
            } else if (count3>=clientStringCharArr.length/2 && count3>=searchStringCharArr.length/2){
                foundOrders.add(order);
            }
        }

        if(foundOrders.isEmpty()){
            getAlertWarningDialog("Результаты по данному клиенту не найдены");
        } else {
            ObservableList<Order> ordersList = FXCollections.observableList(foundOrders);
            tableViewOrders = getTableViewOrders();
            stackPaneOrders.getChildren().clear();
            hBoxPositionsAndClient.getChildren().clear();
            tableViewOrders.setItems(ordersList);
            stackPaneOrders.getChildren().addAll(tableViewOrders);
            clickedRowOrder(new Order());
            clickedOrderIndex=-1;
        }
        */
    }

    void buttonPressed(Button pressedButton){
        this.pressedButton = pressedButton;
    }

    private static ContextMenu setAvailabilityContextMenu(Order order, ContextMenu contextMenu, int focusedIndex){
        MenuItem setAvailability;
        Menu setPayment = new Menu("Оплата");
        MenuItem payment1 = new MenuItem();
        MenuItem payment2 = new MenuItem();
        SeparatorMenuItem separator = new SeparatorMenuItem();
        setPayment.getItems().clear();
        setPayment.getItems().addAll(payment1, payment2);

        setAvailability = new MenuItem();
        switch (order.getAvailability()){
            case "В работе":
                setAvailability.setText("Готово");
                break;

            case "Готово":
                setAvailability.setText("В работе");
                break;
        }
        setAvailability.setOnAction(event -> {
            DataBaseHelper.setAvailabilityOrders(order, MainInterface.getAccount().getUserName());
            setAllOrders();
            refreshOrdersList();
            tableViewOrders.getSelectionModel().select(focusedIndex);
            tableViewOrders.getFocusModel().focus(focusedIndex);
            tableViewOrders.scrollTo(focusedIndex);
        });

        switch (order.getPayment()){
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

        payment1.setOnAction(event -> setOrderPayment(payment1.getText(), order, focusedIndex));

        payment2.setOnAction(event -> setOrderPayment(payment2.getText(), order, focusedIndex));

        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(setAvailability, separator, setPayment);
        return contextMenu;
    }

    private static void setOrderPayment(String payment, Order order, int focusedIndex){
        order.setPayment(payment);
        DataBaseHelper.setPaymentOrders(order, MainInterface.getAccount().getUserName());
        setAllOrders();
        refreshOrdersList();
        tableViewOrders.getSelectionModel().select(focusedIndex);
        tableViewOrders.getFocusModel().focus(focusedIndex);
        tableViewOrders.scrollTo(focusedIndex);
    }

    private static VBox getGridPaneClientCard(Order order){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().clear();
        TitledPane titledPane = new TitledPane();
        titledPane.setPrefWidth(vBox.getPrefWidth()/2-10);

        Text textHeadClient = new Text("Клиент: ");
        Text textHeadPhone =  new Text("Телефон: ");
        Text textHeadMail = new Text("E-mail: ");

        GridPane gridPaneClientCard = new GridPane();
        gridPaneClientCard.getChildren().clear();
        gridPaneClientCard.add(textHeadClient,0,0);
        gridPaneClientCard.add(textHeadPhone,0,2);
        gridPaneClientCard.add(textHeadMail,0,3);

        TextField textClient = new TextField();
        if(order.getClient()!=null){
            textClient.setText(order.getClient().getClient());
            textClient.setPrefColumnCount(order.getClient().getClient().length());
        }
        textClient.setEditable(false);
        textClient.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");

        TextField textPhone = new TextField();
        if(order.getClient()!=null && order.getClient().getId()!=0) {
            textPhone.setText(order.getClient().getPhone());
            textPhone.setPrefColumnCount(order.getClient().getPhone().length());
        }
        textPhone.setEditable(false);
        textPhone.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");
        TextField textMail = new TextField();
        if(order.getClient()!=null && order.getClient().getId()!=0) {
            textMail.setText(order.getClient().getMail());
            textMail.setPrefColumnCount(order.getClient().getMail().length());
        }
        textMail.setEditable(false);
        textMail.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;\n" +
                "    -fx-background-radius: 0, 0, 0, 0;\n" +
                "    -fx-padding: 0.166667em;");
        gridPaneClientCard.add(textClient,1,0);
        gridPaneClientCard.add(textPhone,1,2);
        gridPaneClientCard.add(textMail,1,3);
        gridPaneClientCard.setHgap(3);
        GridPane.setHalignment(textHeadClient, HPos.RIGHT);
        GridPane.setHalignment(textHeadPhone, HPos.RIGHT);
        GridPane.setHalignment(textHeadMail, HPos.RIGHT);

        titledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        titledPane.setText("Карта клиента");
        titledPane.setExpanded(true);
        titledPane.setCollapsible(false);
        titledPane.setContent(gridPaneClientCard);
        vBox.getChildren().addAll(titledPane);

        return vBox;
    }

    private static VBox getGridPaneChangesOrderCard(Order order){
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.getChildren().clear();
        TitledPane titledPane = new TitledPane();
        titledPane.setPrefWidth(vBox.getPrefWidth()/2-10);

        GridPane gridPaneChangesOrderCard = new GridPane();

        ColumnConstraints column1 = new ColumnConstraints(50, 160, Double.MAX_VALUE);
        ColumnConstraints column2 = new ColumnConstraints(50, 150, Double.MAX_VALUE);
        ColumnConstraints column3 = new ColumnConstraints(50, 150, Double.MAX_VALUE);
        gridPaneChangesOrderCard.getColumnConstraints().addAll(column1, column2, column3);

        gridPaneChangesOrderCard.getRowConstraints().add(new RowConstraints(20));
        gridPaneChangesOrderCard.getRowConstraints().add(new RowConstraints(45));
        gridPaneChangesOrderCard.getRowConstraints().add(new RowConstraints(45));
        gridPaneChangesOrderCard.getRowConstraints().add(new RowConstraints(45));


        Label labelHeadDate = new Label("Дата/Время");
        labelHeadDate.setAlignment(Pos.CENTER);
        labelHeadDate.setPadding(new Insets(5));
        Label labelHeadLogin = new Label("Логин");
        labelHeadLogin.setAlignment(Pos.CENTER);
        labelHeadLogin.setPadding(new Insets(5));
        Label labelHeadCreate = new Label("Создание заказа");
        labelHeadCreate.setWrapText(true);
        labelHeadCreate.setAlignment(Pos.CENTER);
        labelHeadCreate.setPadding(new Insets(5));
        labelHeadCreate.setWrapText(true);
        Label labelHeadEdit = new Label("Редактирование заказа");
        labelHeadEdit.setWrapText(true);
        labelHeadEdit.setAlignment(Pos.CENTER_RIGHT);
        labelHeadEdit.setPadding(new Insets(5));
        Label labelHeadAvailability = new Label("Изменение (готово/в работе)");
        labelHeadAvailability.setWrapText(true);
        labelHeadAvailability.setAlignment(Pos.CENTER_RIGHT);
        labelHeadAvailability.setPadding(new Insets(5));
        Text textDateCreate = new Text();
        if(order.getDateCreate()!=null) textDateCreate.setText(order.getDateCreate().toLocalDate().format(CreateOrder.formatter)+"\n"+
                order.getTimeCreate().format(CreateOrder.formatterTime));

        Text textDateEdit = new Text();
        if(order.getDateEdit()!=null) textDateEdit.setText(order.getDateEdit().toLocalDate().format(CreateOrder.formatter)+"\n"+
                order.getTimeEdit().format(CreateOrder.formatterTime));
        Text textDateAvailability = new Text();
        if(order.getDateAvailability()!=null) textDateAvailability.setText(order.getDateAvailability().toLocalDate().format(CreateOrder.formatter)+"\n"+
                order.getTimeAvailability().format(CreateOrder.formatterTime));
        Text textLoginCreate = new Text(order.getLoginCreate());
        Text textLoginEdit = new Text(order.getLoginEdit());
        Text textLoginAvailability = new Text(order.getLoginAvailability());

        gridPaneChangesOrderCard.add(labelHeadDate,1,0);
        gridPaneChangesOrderCard.add(labelHeadLogin,2,0);
        gridPaneChangesOrderCard.add(labelHeadCreate,0,1);
        gridPaneChangesOrderCard.add(labelHeadEdit, 0,2);
        gridPaneChangesOrderCard.add(labelHeadAvailability, 0,3);
        gridPaneChangesOrderCard.add(textDateCreate, 1,1);
        gridPaneChangesOrderCard.add(textDateEdit, 1,2);
        gridPaneChangesOrderCard.add(textDateAvailability, 1,3);
        gridPaneChangesOrderCard.add(textLoginCreate, 2,1);
        gridPaneChangesOrderCard.add(textLoginEdit, 2,2);
        gridPaneChangesOrderCard.add(textLoginAvailability, 2,3);

        gridPaneChangesOrderCard.setGridLinesVisible(true);

        GridPane.setHalignment(labelHeadCreate,HPos.LEFT);
        GridPane.setHalignment(labelHeadEdit,HPos.LEFT);
        GridPane.setHalignment(labelHeadAvailability,HPos.LEFT);
        GridPane.setHalignment(labelHeadDate,HPos.CENTER);
        GridPane.setHalignment(labelHeadLogin,HPos.CENTER);
        GridPane.setHalignment(textDateCreate,HPos.CENTER);
        GridPane.setHalignment(textDateEdit,HPos.CENTER);
        GridPane.setHalignment(textDateAvailability,HPos.CENTER);
        GridPane.setHalignment(textLoginCreate,HPos.CENTER);
        GridPane.setHalignment(textLoginEdit,HPos.CENTER);
        GridPane.setHalignment(textLoginAvailability,HPos.CENTER);

        GridPane.setValignment(labelHeadCreate, VPos.CENTER);
        GridPane.setValignment(labelHeadEdit, VPos.CENTER);
        GridPane.setValignment(labelHeadAvailability, VPos.CENTER);
        GridPane.setValignment(labelHeadDate, VPos.CENTER);
        GridPane.setValignment(labelHeadLogin, VPos.CENTER);
        GridPane.setValignment(textDateCreate, VPos.CENTER);
        GridPane.setValignment(textDateEdit, VPos.CENTER);
        GridPane.setValignment(textDateAvailability, VPos.CENTER);
        GridPane.setValignment(textLoginCreate, VPos.CENTER);
        GridPane.setValignment(textLoginEdit, VPos.CENTER);
        GridPane.setValignment(textLoginAvailability, VPos.CENTER);

        titledPane.setText("Карта изменений заказа");
        titledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        titledPane.setExpanded(true);
        titledPane.setCollapsible(false);
        titledPane.setContent(gridPaneChangesOrderCard);
        vBox.setPrefWidth(tableViewPositions.getPrefWidth());
        vBox.getChildren().addAll(titledPane);
        return vBox;
    }

    static void refreshOrdersList(){
        //tableViewOrders = getTableViewOrders();
        if(!searchTextField.getText().isEmpty()){
            search(searchTextField.getText());
        } else {
            ArrayList<Order> ordersListSorted = new ArrayList<>();
            for (Order order : allOrders) {
                if (order.getDate().toLocalDate().getYear() == year) {
                    if (month != 0) {
                        if (order.getDate().toLocalDate().getMonthValue() == month) {
                            ordersListSorted.add(order);
                        }
                    } else {
                        ordersListSorted.add(order);
                    }
                }
            }
            System.out.println("refresh");
            System.out.println("size: " + ordersListSorted.size());
            stackPaneOrders.getChildren().clear();
            hBoxPositionsAndClient.getChildren().clear();
            setTableViewOrders(tableViewOrders, ordersListSorted, placeHolderText);
            //tableViewOrders.setItems(ordersListSorted);
            stackPaneOrders.getChildren().addAll(tableViewOrders);
            clickedRowOrder(new Order());
            clickedOrderIndex = -1;
        }
    }
/*
    private static TableView<Order> getTableViewOrders(){
        TableView<Order> tableViewOrders = new TableView<>();
        //tableViewOrders.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ObservableList<Order> ordersList = FXCollections.observableList(DataBaseHelper.getOrdersList());
        TableColumn<Order, java.sql.Date> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.076));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(tc -> new TableCell<Order, java.sql.Date>() {
            @Override
            protected void updateItem(java.sql.Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(CreateOrder.formatter.format(date.toLocalDate()));
                }
            }
        });
        TableColumn<Order, Client> clientCol = new TableColumn<>("Заказчик");
        //clientCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.1));
        clientCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.42));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        clientCol.setCellFactory(tc -> new TableCell<Order, Client>(){
            @Override
            protected void updateItem(Client client, boolean empty){
                super.updateItem(client, empty);
                if(empty){
                    setText(null);
                } else {
                    setText(client.getClient());
                }
            }
        });

        TableColumn<Order, String> paymentCol = new TableColumn<>("Оплата");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("payment"));
        paymentCol.setStyle("-fx-alignment: CENTER;");
        paymentCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.094));
        TableColumn<Order, String> amountCol = new TableColumn<>("Сумма");
        amountCol.setStyle("-fx-alignment: CENTER;");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.08));

        TableColumn<Order, String> managerCol = new TableColumn<>("Менеджер");
        managerCol.setStyle("-fx-alignment: CENTER;");
        managerCol.setCellValueFactory(new PropertyValueFactory<>("manager"));
        managerCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.1));

        TableColumn<Order, String> designerCol = new TableColumn<>("Дизайнер");
        designerCol.setStyle("-fx-alignment: CENTER;");
        designerCol.setCellValueFactory(new PropertyValueFactory<>("designer"));
        designerCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.1));

        TableColumn<Order, String> availabilityCol = new TableColumn<>("Готовность");
        availabilityCol.setStyle("-fx-alignment: CENTER;");
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        availabilityCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.079));

        if(ordersList.isEmpty()) {
            tableViewOrders.setPlaceholder(new Text("База заказов пуста"));
        }

        tableViewOrders.setPrefHeight((mainScene.getHeight()*2)/3);
        tableViewOrders.setPlaceholder(new Text("База заказов пуста"));
        tableViewOrders.getColumns().addAll(dateCol,clientCol, paymentCol, amountCol, managerCol, designerCol, availabilityCol);
        tableViewOrders.setStyle("");
        ContextMenu contextMenu = new ContextMenu();
        tableViewOrders.getItems().clear();
        tableViewOrders.setItems(ordersList);

        tableViewOrders.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>() {
            @Override
            public TableRow<Order> call(TableView<Order> param) {
                return null;
            }
        });
        tableViewOrders.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>() {
            @Override
            public TableRow<Order> call(TableView<Order> tableView) {
                TableRow<Order> row = new TableRow<Order>() {
                    @Override
                    protected void updateItem(Order order, boolean empty) {
                        super.updateItem(order, empty);
                        this.setFocused(true);
                        if(!empty){
                            if (order != null && order.getAvailability().equals("Готово")){
                                this.setStyle("-fx-background-color: #3cb380");
                            } else this.setStyle("");

                        }
                    }
                };

                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1) {

                        clickedRowOrder(row.getItem());
                        /*
                        Order clickedRow = row.getItem();
                        contextMenu.getItems().clear();
                        tableViewPositions.setItems(DataBaseHelper.getPositionsFromOrder(clickedRow));
                        vBoxOrderCard.setSpacing(10);
                        vBoxOrderCard.setPrefWidth(mainScene.getWidth());
                        //vBoxOrderCard.setStyle("-fx-border-color: red");
                        vBoxOrderCard.getChildren().clear();
                        vBoxOrderCard.getChildren().addAll(getGridPaneClientCard(clickedRow), getGridPaneChangesOrderCard(clickedRow));
                        hBoxPositionsAndClient.getChildren().clear();
                        VBox vBoxPositionsAndRemark = new VBox();
                        vBoxPositionsAndRemark.setSpacing(10);
                        //vBoxPositionsAndRemark.setStyle("-fx-border-color: blue");
                        vBoxPositionsAndRemark.setPrefWidth(mainScene.getWidth());
                        TitledPane titledPaneRemark = new TitledPane();
                        titledPaneRemark.setText("Примечание");
                        titledPaneRemark.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
                        titledPaneRemark.setCollapsible(false);
                        titledPaneRemark.setExpanded(true);
                        titledPaneRemark.setPrefWidth(tableViewPositions.getPrefWidth());
                        TextArea textAreaRemark = new TextArea();
                        textAreaRemark.setEditable(false);
                        textAreaRemark.setText(clickedRow.getRemark());
                        titledPaneRemark.setContent(textAreaRemark);

                        if(clickedRow.getRemark()==null || clickedRow.getRemark().equals("")) {
                            titledPaneRemark.setVisible(false);
                        }
                        vBoxPositionsAndRemark.getChildren().addAll(tableViewPositions, titledPaneRemark);
                        hBoxPositionsAndClient.getChildren().addAll(vBoxPositionsAndRemark, vBoxOrderCard);
                        *//*
                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY)) {
                        row.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                            @Override
                            public void handle(ContextMenuEvent event) {
                                if(row.getItem()!=null){
                                    setAvailabilityContextMenu(row.getItem(), contextMenu, tableViewOrders.getFocusModel().getFocusedIndex()).show(row, event.getScreenX() + 10, event.getScreenY() + 5);
                                }
                            }
                        });
                    }
                });
                return row;
            }
        });
        //tableViewOrders.scrollTo(tableViewOrders.getItems().size()-1);
        if(clickedOrderIndex>=0) tableViewOrders.scrollTo(clickedOrderIndex);
        else tableViewOrders.scrollTo(tableViewOrders.getItems().size()-1);
        return tableViewOrders;
    }
*/

    static void setTableViewOrders(TableView<Order> tableViewOrders, ArrayList<Order> orderArray, Text placeHolderText){
        System.out.println("setTableViewOrders");
        System.out.println("size: "+orderArray.size());
        //tableViewOrders.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableViewOrders.getColumns().clear();
        tableViewOrders.getItems().clear();
        //ObservableList<Order> ordersList = FXCollections.observableList(orderArray);
        ObservableList<Order> ordersList = FXCollections.observableArrayList();
        ordersList.addAll(orderArray);

        System.out.println("sizeObserv: "+ordersList.size());
        TableColumn<Order, java.sql.Date> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.076));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setCellFactory(tc -> new TableCell<Order, java.sql.Date>() {
            @Override
            protected void updateItem(java.sql.Date date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(CreateOrder.formatter.format(date.toLocalDate()));
                }
            }
        });
        TableColumn<Order, Client> clientCol = new TableColumn<>("Заказчик");
        clientCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.42));
        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        clientCol.setCellFactory(tc -> new TableCell<Order, Client>(){
            @Override
            protected void updateItem(Client client, boolean empty){
                super.updateItem(client, empty);
                if(empty){
                    setText(null);
                } else {
                    setText(client.getClient());
                }
            }
        });

        TableColumn<Order, String> paymentCol = new TableColumn<>("Оплата");
        paymentCol.setCellValueFactory(new PropertyValueFactory<>("payment"));
        paymentCol.setStyle("-fx-alignment: CENTER;");
        paymentCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.094));
        TableColumn<Order, String> amountCol = new TableColumn<>("Сумма");
        amountCol.setStyle("-fx-alignment: CENTER;");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        amountCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.08));

        TableColumn<Order, String> managerCol = new TableColumn<>("Менеджер");
        managerCol.setStyle("-fx-alignment: CENTER;");
        managerCol.setCellValueFactory(new PropertyValueFactory<>("manager"));
        managerCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.1));

        TableColumn<Order, String> designerCol = new TableColumn<>("Дизайнер");
        designerCol.setStyle("-fx-alignment: CENTER;");
        designerCol.setCellValueFactory(new PropertyValueFactory<>("designer"));
        designerCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.1));

        TableColumn<Order, String> availabilityCol = new TableColumn<>("Готовность");
        availabilityCol.setStyle("-fx-alignment: CENTER;");
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
        availabilityCol.prefWidthProperty().bind(mainScene.widthProperty().multiply(0.079));

        if(ordersList.isEmpty()) {
            tableViewOrders.setPlaceholder(placeHolderText);
            System.out.println("isEmpty");
        }

        tableViewOrders.setPrefHeight((mainScene.getHeight()*2)/3);
        //tableViewOrders.setPlaceholder(new Text("База заказов пуста"));
        tableViewOrders.getColumns().addAll(dateCol,clientCol, paymentCol, amountCol, managerCol, designerCol, availabilityCol);
        tableViewOrders.setStyle("");
        ContextMenu contextMenu = new ContextMenu();

        tableViewOrders.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>() {
            @Override
            public TableRow<Order> call(TableView<Order> param) {
                return null;
            }
        });

        tableViewOrders.setRowFactory(new Callback<TableView<Order>, TableRow<Order>>() {
            @Override
            public TableRow<Order> call(TableView<Order> tableView) {
                TableRow<Order> row = new TableRow<Order>() {
                    @Override
                    protected void updateItem(Order order, boolean empty) {
                        super.updateItem(order, empty);
                        this.setFocused(true);
                        if(!empty){
                            if (order != null && order.getAvailability().equals("Готово")){
                                this.setStyle("-fx-background-color: #3cb380");
                            } else this.setStyle("");
                        } else {
                            this.setStyle("-fx-background-color: transparent, transparent, transparent, transparent;");
                        }
                    }
                };

                row.setOnMouseClicked(event -> {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1) {

                        clickedRowOrder(row.getItem());

                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY)) {
                        row.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
                            @Override
                            public void handle(ContextMenuEvent event) {
                                if(row.getItem()!=null){
                                    setAvailabilityContextMenu(row.getItem(), contextMenu, tableViewOrders.getFocusModel().getFocusedIndex()).show(row, event.getScreenX() + 10, event.getScreenY() + 5);
                                }
                            }
                        });
                    }
                });
                return row;
            }
        });
        if(clickedOrderIndex>=0) tableViewOrders.scrollTo(clickedOrderIndex);
        else tableViewOrders.scrollTo(tableViewOrders.getItems().size()-1);
        tableViewOrders.setItems(ordersList);
    }

    static void clickedRowOrder(Order clickedOrder){
        //contextMenu.getItems().clear();
        if(clickedOrder.getPositions()==null){
            ObservableList<OrdersPosition> positionsEmptyList = FXCollections.observableArrayList();
            tableViewPositions.setItems(positionsEmptyList);
        }
        tableViewPositions.setItems(DataBaseHelper.getPositionsFromOrder(clickedOrder));
        vBoxOrderCard.setSpacing(10);
        vBoxOrderCard.setPrefWidth(mainScene.getWidth());
        //vBoxOrderCard.setStyle("-fx-border-color: red");
        vBoxOrderCard.getChildren().clear();
        vBoxOrderCard.getChildren().addAll(getGridPaneClientCard(clickedOrder), getGridPaneChangesOrderCard(clickedOrder));
        hBoxPositionsAndClient.getChildren().clear();
        VBox vBoxPositionsAndRemark = new VBox();
        vBoxPositionsAndRemark.setSpacing(10);
        //vBoxPositionsAndRemark.setStyle("-fx-border-color: blue");
        vBoxPositionsAndRemark.setPrefWidth(mainScene.getWidth());
        TitledPane titledPaneRemark = new TitledPane();
        titledPaneRemark.setText("Примечание");
        titledPaneRemark.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        titledPaneRemark.setCollapsible(false);
        titledPaneRemark.setExpanded(true);
        titledPaneRemark.setPrefWidth(tableViewPositions.getPrefWidth());
        TextArea textAreaRemark = new TextArea();
        textAreaRemark.setEditable(false);
        textAreaRemark.setText(clickedOrder.getRemark());
        titledPaneRemark.setContent(textAreaRemark);

        if(clickedOrder.getRemark()==null || clickedOrder.getRemark().equals("")) {
            titledPaneRemark.setVisible(false);
        }
        vBoxPositionsAndRemark.getChildren().addAll(tableViewPositions, titledPaneRemark);
        hBoxPositionsAndClient.getChildren().addAll(vBoxPositionsAndRemark, vBoxOrderCard);
        clickedOrderIndex = tableViewOrders.getSelectionModel().getFocusedIndex();
    }

    private AnchorPane setBottom(){
        HBox buttonsHBox = new HBox();
        buttonsHBox.setPadding(new Insets(15));
        buttonsHBox.setSpacing(10);
        Button createButton = new Button("Создать");
        createButton.setPrefSize(100, 50);
        createButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CreateOrder createOrder = new CreateOrder(mainStage);
                createOrder.show();
            }
        });

        Button editButton = new Button("Редактировать");
        editButton.setPrefSize(100, 50);
        editButton.setOnAction(event -> {
            CreateOrder createOrder = new CreateOrder(mainStage, tableViewOrders.getFocusModel().getFocusedItem());
            createOrder.show();
        });

        Button deleteButton = new Button("Удалить");
        deleteButton.setPrefSize(100,50);
        deleteButton.setOnAction(event -> {
            int focusIndex = tableViewOrders.getSelectionModel().getFocusedIndex();
            if(getAlertAskConfirmationDialog("Вы уверены, что хотите безвозвратно удалить заказ?"))
            DataBaseHelper.deleteOrderFromDB(tableViewOrders.getFocusModel().getFocusedItem());
            setAllOrders();
            MainInterface.hBoxPositionsAndClient.getChildren().clear();
            refreshOrdersList();
            tableViewOrders.scrollTo(focusIndex-1);
        });

        buttonsHBox.getChildren().addAll(createButton, editButton, deleteButton);

        Button printButton = new Button("Печать");
        printButton.setPrefSize(100,50);
        printButton.setOnAction(event -> {
            Print.toPrint(tableViewOrders.getFocusModel().getFocusedItem());
        });

        HBox hBoxPrintBtn = new HBox();
        hBoxPrintBtn.setPadding(new Insets(15));
        hBoxPrintBtn.getChildren().addAll(printButton);

        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setLeftAnchor(buttonsHBox,0.0);
        AnchorPane.setRightAnchor(hBoxPrintBtn, 0.0);
        anchorPane.getChildren().addAll(buttonsHBox, hBoxPrintBtn);
        return anchorPane;
    }

    private MenuBar getMainMenuBar(){
        MenuBar mainMenuBar = new MenuBar();
        Menu menuStaffs = new Menu("Сотрудники");
        Menu menuClients = new Menu("Клиенты");
        MenuItem menuDesigners = new MenuItem("Дизайнеры");
        MenuItem menuManagers = new MenuItem("Менеджеры");
        MenuItem menuClientsBase = new MenuItem("База клиентов");
        menuStaffs.getItems().addAll(menuManagers, menuDesigners);
        menuClients.getItems().addAll(menuClientsBase);
        mainMenuBar.getMenus().addAll(menuStaffs, menuClients);

        menuManagers.setOnAction(event -> viewStaffs("менеджер"));
        menuDesigners.setOnAction(event -> viewStaffs("дизайнер"));
        menuClientsBase.setOnAction(event -> viewClientsBase());

        return mainMenuBar;
    }

    private void viewStaffs(String jobPosition){

        Stage stageStaffs = new Stage();
        stageStaffs.setTitle(jobPosition+"ы");
        stageStaffs.initModality(Modality.WINDOW_MODAL);
        stageStaffs.initOwner(mainStage);
        BorderPane borderPaneStaffs = new BorderPane();
        Scene sceneStaffs = new Scene(borderPaneStaffs, 300,300);

        staffs = FXCollections.observableArrayList(DataBaseHelper.getStaffs(jobPosition));

        staffsListView = new ListView<>(staffs);

        Button buttonAdd = new Button("Добавть нового");
        buttonAdd.setPrefHeight(40);
        Button buttonDelete = new Button("Удалить");
        buttonDelete.setPrefHeight(40);
        buttonDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(getAlertAskConfirmationDialog("Вы действительно хотите удалить этого сотрудника из базы?")) {
                    DataBaseHelper.deleteStaffFromDB(staffsListView.getFocusModel().getFocusedItem());
                    refreshStaffListView(jobPosition);
                }
            }
        });

        buttonAdd.setOnAction(event -> addStaffDialogPane(new Staff("",jobPosition), stageStaffs));

        Button buttonOk = new Button("Ок");
        buttonOk.setPrefSize(100,40);
        buttonOk.setOnAction(event -> stageStaffs.close());
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(100,40);
        buttonCancel.setOnAction(event -> stageStaffs.close());

        HBox hBoxTopButtons = new HBox();
        hBoxTopButtons.setSpacing(10);
        hBoxTopButtons.setPadding(new Insets(15));
        hBoxTopButtons.getChildren().addAll(buttonAdd, buttonDelete);
        HBox hBoxBottomButtons = new HBox();
        hBoxBottomButtons.setSpacing(15);
        hBoxBottomButtons.setPadding(new Insets(20));
        hBoxBottomButtons.setAlignment(Pos.CENTER);
        hBoxBottomButtons.getChildren().addAll(buttonOk, buttonCancel);

        borderPaneStaffs.setCenter(staffsListView);
        borderPaneStaffs.setTop(hBoxTopButtons);
        borderPaneStaffs.setBottom(hBoxBottomButtons);
        BorderPane.setMargin(staffsListView, new Insets(10));

        stageStaffs.setScene(sceneStaffs);
        stageStaffs.show();
    }

    private void addStaffDialogPane(Staff staff, Stage primaryStage){
        Stage addDialogStage = new Stage();
        addDialogStage.setTitle("Добавить собтрудника");
        addDialogStage.initModality(Modality.WINDOW_MODAL);
        addDialogStage.initOwner(primaryStage);
        BorderPane borderPane = new BorderPane();
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(15));
        Text headText = new Text();
        headText.setText("Введите имя нового "+staff.getJobPosition()+"a");
        TextField textField = new TextField();
        Button ok = new Button("Ок");
        ok.setPrefSize(100,40);
        Button cancel = new Button("Отмена");
        cancel.setPrefSize(100,40);
        ok.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                staff.setName(textField.getText());
                DataBaseHelper.addStaffToDB(staff);
                refreshStaffListView(staff.getJobPosition());
                addDialogStage.close();
            }
        });

        cancel.setOnAction(event -> addDialogStage.close());
        vBox.getChildren().addAll(headText, textField);
        hBox.getChildren().addAll(ok, cancel);

        borderPane.setCenter(vBox);
        borderPane.setBottom(hBox);

        Scene addDialogScene = new Scene(borderPane, 200,130);
        addDialogStage.setScene(addDialogScene);
        addDialogStage.show();

    }

    private TableView<Client> getTableClients(){

        tableClients = new TableView<>();
        tableClients.setPlaceholder(new Text("Список клиентов пуст"));
        TableColumn<Client, String> clientCol = new TableColumn<>("Клиент");
        TableColumn<Client, String> phoneCol = new TableColumn<>("Контактный\nтелефон");
        TableColumn<Client, String> mailCol = new TableColumn<>("E-mail");

        clientCol.setCellValueFactory(new PropertyValueFactory<>("client"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        mailCol.setCellValueFactory(new PropertyValueFactory<>("mail"));

        tableClients.setItems(DataBaseHelper.getClientsList());
        tableClients.getColumns().addAll(clientCol,  phoneCol, mailCol);

        ContextMenu contextMenuClient = new ContextMenu();
        MenuItem menuItemDelete = new MenuItem("Удалить");
        menuItemDelete.setOnAction(event -> {
            if(getAlertAskConfirmationDialog("Вы уверены, что хотите удалить данного клиента из базы?")) {
                DataBaseHelper.deleteClientFromDB(tableClients.getFocusModel().getFocusedItem());
                tableClients.setItems(DataBaseHelper.getClientsList());
            }
        });
        MenuItem menuItemEdit = new MenuItem("Редактировать");
        menuItemEdit.setOnAction(event -> {
            clientDialogPane("edit", tableClients.getFocusModel().getFocusedItem(), stageClients);
        });
        contextMenuClient.getItems().addAll(menuItemEdit, new SeparatorMenuItem(), menuItemDelete);

        tableClients.setRowFactory(new Callback<TableView<Client>, TableRow<Client>>() {
            @Override
            public TableRow<Client> call(TableView<Client> param) {
                TableRow<Client> row = new TableRow<Client>();
                row.setOnMouseClicked(event -> {
                    tableClients.setOnContextMenuRequested(event1 -> {
                        contextMenuClient.show(row, event1.getScreenX()+10, event1.getScreenY()+10);
                    });
                });
                return row;
            }
        });

        return tableClients;
    }

    private List<TitledPane> getTitledPanesList(){

        List<TitledPane> titledPanesList = new ArrayList<>();
        for (Client client : DataBaseHelper.getClientsList()){
            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(5);
            gridPane.setAlignment(Pos.TOP_LEFT);
            GridPane.setHalignment(new Text(), HPos.CENTER);
            gridPane.add(new Text("Контактный\nтелефон"),1,0);
            gridPane.add(new Text("E-mail"),2,0);
            gridPane.add(new Text(client.getPhone()),1,1);
            gridPane.add(new Text(client.getMail()),2,1);
            TitledPane titledPane = new TitledPane(client.getClient(), gridPane);
            titledPane.setExpanded(false);
            titledPanesList.add(titledPane);
        }

        return titledPanesList;
    }

    private void refreshStaffListView(String jobPosition){
        staffs = FXCollections.observableArrayList(DataBaseHelper.getStaffs(jobPosition));
        staffsListView.getItems().clear();
        staffsListView.getItems().addAll(staffs);
    }

    private void viewClientsBase(){

        stageClients = new Stage();
        stageClients.setTitle("База клиентов");
        stageClients.initModality(Modality.WINDOW_MODAL);
        stageClients.initOwner(mainStage);
        BorderPane borderPaneClients = new BorderPane();
        Scene sceneClients = new Scene(borderPaneClients, 600, 600);
        stageClients.setScene(sceneClients);


        HBox hBoxBottomButtons = new HBox();
        hBoxBottomButtons.setPadding(new Insets(15));
        hBoxBottomButtons.setSpacing(15);
        Button buttonOk = new Button("Ок");
        buttonOk.setPrefSize(100,40);
        buttonOk.setOnAction(event -> stageClients.close());
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(100,40);
        buttonCancel.setOnAction(event -> stageClients.close());
        hBoxBottomButtons.getChildren().addAll(buttonOk, buttonCancel);


        HBox hBoxTopButtons = new HBox();
        hBoxTopButtons.setSpacing(10);
        hBoxTopButtons.setPadding(new Insets(15));
        Button buttonAdd = new Button("Добавить");
        buttonAdd.setPrefHeight(40);
        buttonAdd.setOnAction(event -> clientDialogPane("add", new Client(), stageClients));
        Button buttonEdit = new Button("Редактировать");
        buttonEdit.setPrefHeight(40);
        buttonEdit.setOnAction(event -> clientDialogPane("edit", tableClients.getFocusModel().getFocusedItem(), stageClients));
        Button buttonDelete = new Button("Удалить");
        buttonDelete.setPrefHeight(40);
        buttonDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(getAlertAskConfirmationDialog("Вы уверены, что хотите удалить данного клиента из базы?")) {
                    DataBaseHelper.deleteClientFromDB(tableClients.getFocusModel().getFocusedItem());
                    tableClients.setItems(DataBaseHelper.getClientsList());
                }
            }
        });
        hBoxTopButtons.getChildren().addAll(buttonAdd);

        ObservableList<TitledPane> titledPanesClients = FXCollections.observableList(getTitledPanesList());
        ListView<TitledPane> listViewClients = new ListView<>(titledPanesClients);

        borderPaneClients.setTop(hBoxTopButtons);
        borderPaneClients.setCenter(getTableClients());
        borderPaneClients.setBottom(hBoxBottomButtons);

        stageClients.show();
    }

    static void clientDialogPane(String what, Client client, Stage primaryStage){
        Stage stageAddClient = new Stage();
        stageAddClient.setTitle("Добавить клиента");
        stageAddClient.initModality(Modality.WINDOW_MODAL);
        stageAddClient.initOwner(primaryStage);
        BorderPane borderPane = new BorderPane();
        Scene sceneAddClient = new Scene(borderPane, 430 , 230);
        stageAddClient.setScene(sceneAddClient);

        TextField textFieldClient = new TextField();
        textFieldClient.setPrefWidth(250);
        textFieldClient.setFocusTraversable(false);
        TextField textFieldPhone = new TextField();
        textFieldPhone.setPrefWidth(250);
        textFieldPhone.setFocusTraversable(false);
        TextField textFieldMail = new TextField();
        textFieldMail.setPrefWidth(250);
        textFieldMail.setFocusTraversable(false);

        HBox hBoxBottomButtons = new HBox();
        hBoxBottomButtons.setSpacing(10);
        hBoxBottomButtons.setPadding(new Insets(15));
        hBoxBottomButtons.setAlignment(Pos.CENTER);
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(100,50);
        buttonCancel.setOnAction(event -> stageAddClient.close());
        Button buttonAddClient = new Button("Добавить");
        buttonAddClient.setPrefSize(100,50);
        buttonAddClient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DataBaseHelper.addClientToDB(new Client(
                        textFieldClient.getText(),
                        textFieldPhone.getText(),
                        textFieldMail.getText()));
                if(tableClients!=null) {
                    tableClients.setItems(DataBaseHelper.getClientsList());
                }
                CreateOrder.refreshComboBox();
                stageAddClient.close();
                if(what.equals("addInCreate")){
                    CreateOrder.setAddedClient();
                }
            }
        });
        hBoxBottomButtons.getChildren().addAll(buttonAddClient, buttonCancel);

        if(what.equals("edit")){
            buttonAddClient.setText("Сохранить\nизменения");
            textFieldClient.setText(client.getClient());
            textFieldPhone.setText(client.getPhone());
            textFieldMail.setText(client.getMail());
            buttonAddClient.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    client.setClient(textFieldClient.getText());
                    client.setPhone(textFieldPhone.getText());
                    client.setMail(textFieldMail.getText());
                    DataBaseHelper.editClientToDB(client);
                    tableClients.setItems(DataBaseHelper.getClientsList());
                    stageAddClient.close();
                }
            });
        }

        Text textHeadClient = new Text("Клиент");
        Text textHeadPhone = new Text("Контактный телефон");
        Text textHeadMail = new Text("E-mail");

        GridPane gridPane = new GridPane();
        gridPane.add(textHeadClient,0,0);
        gridPane.add(textHeadPhone,0,1);
        gridPane.add(textHeadMail,0,2);
        gridPane.add(textFieldClient,1,0);
        gridPane.add(textFieldPhone,1,1);
        gridPane.add(textFieldMail,1,2);

        gridPane.setHgap(15);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        GridPane.setHalignment(textHeadClient,HPos.RIGHT);
        GridPane.setHalignment(textHeadPhone,HPos.RIGHT);
        GridPane.setHalignment(textHeadMail,HPos.RIGHT);

        borderPane.setCenter(gridPane);
        borderPane.setBottom(hBoxBottomButtons);

        stageAddClient.show();
    }

    public final class GroupBox extends Parent {

        private StackPane _stackPane;
        private TitledPane _titledPane;

        public GroupBox() {
            _stackPane = new StackPane();
            _titledPane = new TitledPane();
            setContentPadding(new Insets(10));
            _titledPane.setCollapsible(false);
            _titledPane.setContent(_stackPane);
            super.getChildren().add(_titledPane);
        }

        public GroupBox(String title, Node content) {
            this();
            setText(title);
            setContent(content);
        }

        public GroupBox(String title, Node content, Insets contentPadding) {
            this(title, content);
            setContentPadding(contentPadding);
        }

        public void setText(String value) {
            _titledPane.setText(value);
        }

        public void setContent(Node node) {
            _stackPane.getChildren().add(node);
        }

        public void setContentPadding(Insets value) {
            _stackPane.setPadding(value);
        }
    }

    static void getAlertWarningDialog(String text){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Внимание!");
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    static void getAlertInformationDialog(String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    private static boolean getAlertAskConfirmationDialog(String text){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText(null);
        alert.setContentText(text);

        ButtonType buttonYes = new ButtonType("О да, мой господин!");
        ButtonType buttonNo = new ButtonType("У меня нету такой уверености");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(buttonYes, buttonNo);
        Optional<ButtonType> option = alert.showAndWait();
        if(option.get() == buttonYes) return true;

        return false;
    }

    static void getAlertErrorDialog(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(text);
        alert.showAndWait();
    }

    static Account getAccount() {
        return account;
    }

    public static void setAccount(Account account) {
        MainInterface.account = account;
    }

    static Stage getMainStage() {
        return mainStage;
    }

    static ImageView getMainLogo(){
        Image mainLogo = null;
        try {
            FileInputStream fs = new FileInputStream(DataBaseHelper.path + "\\src\\images\\mainLogo.jpg");
            mainLogo = new Image(fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(mainLogo);
        imageView.setFitHeight(50);
        imageView.setFitWidth(256);

        return imageView;
    }

    static Image getLogo(){
        Image logo = null;
        try {
            FileInputStream fs = new FileInputStream(DataBaseHelper.path + "\\src\\images\\logo.png");
            logo = new Image(fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return logo;
    }

    public class ToolTipCellFactory<S,T> implements Callback<TableColumn<S,T>, TableCell<S,T>>{
        public TableCell<S, T> call(TableColumn<S, T> param) {
            return new TableCell<S, T>(){
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item==null){
                        setTooltip(null);
                        setText(null);
                    }else {
                        Tooltip tooltip = new Tooltip();
                        tooltip.setAutoHide(false);
                        Label label = new Label(item.toString());
                        label.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 16));
                        tooltip.setGraphic(label);
                        setTooltip(tooltip);
                        //setTooltip(new Tooltip(item.toString()));
                        setText(item.toString());
                    }
                }
            };
        }
    }

    public static void setAllOrders(){
        System.out.println("setAllOrders");
        allOrders.clear();
        allOrders.addAll(DataBaseHelper.getOrdersList());
    }

}
