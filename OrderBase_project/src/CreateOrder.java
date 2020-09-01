import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.StringConverter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

class CreateOrder {
    Stage parentStage;
    Stage createOrderStage;
    Order order;
    TextField textFieldAmount = new TextField();
    TableView<OrdersPosition> tablePositionsList;
    ComboBox<String> comboBoxPayment = new ComboBox<>();
    ComboBox<Staff> comboBoxManager = new ComboBox<>();
    ComboBox<Staff> comboBoxDesigner = new ComboBox<>();
    GridPane gridPaneCreateOrder = new GridPane();
    DatePicker datePicker = new DatePicker();
    TextArea textAreaRemark = new TextArea();
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    static DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
    ObservableList<OrdersPosition> positionsList;
    private static ObservableList<String> paymentList = FXCollections.observableArrayList("Оплачено", "Не оплачено", "50%");
    ObservableList<Issue> issueList = FXCollections.observableArrayList(Issue.values());
    int focusOrder = 0;
    BorderPane createOrderBorderPane;
    ArrayList<Client> foundClients;
    ArrayList<Client> allClients = new ArrayList<>(DataBaseHelper.getClientsList());
    static ComboBox<Client> clientCombobox;
    static TextField clientTextField = new TextField();
    static HBox clientHBox = new HBox();


    CreateOrder(Stage parentStage){
        this.parentStage = parentStage;
    }
    CreateOrder(Stage parentStage, Order order){
        this.parentStage = parentStage;
        this.order = order;
        datePicker.setValue(order.getDate().toLocalDate());
        setClientCombobox(new ArrayList<>(DataBaseHelper.getClientsList()));
        clientCombobox.setValue(order.getClient());
        clientTextField.setText(order.getClient().toString());
        comboBoxPayment.setValue(order.getPayment());
        textFieldAmount.setText(order.getAmount());
        comboBoxManager.setValue(new Staff(order.getManager(), "менеджер"));
        comboBoxDesigner.setValue(new Staff(order.getDesigner(), "дизайнер"));
        positionsList = FXCollections.observableArrayList(order.getPositions());
        textAreaRemark.setText(order.getRemark());
    }
    CreateOrder(){}

    void  show(){
        createOrderStage = new Stage();
        if(order == null) createOrderStage.setTitle("Создание нового заказа");
        else createOrderStage.setTitle("Редактирование заказа");
        createOrderStage.initModality(Modality.WINDOW_MODAL);
        createOrderStage.initOwner(parentStage);
        createOrderStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if(positionsList!=null){
                    positionsList.clear();
                    textAreaRemark.clear();
                    textFieldAmount.clear();
                    clientTextField.clear();
                    clientCombobox.getItems().clear();
                    clientTextField.clear();
                    clientHBox.getChildren().clear();
                }
            }
        });

        createOrderBorderPane = new BorderPane();
        Scene createScene = new Scene(createOrderBorderPane, 760,700);
        createOrderBorderPane.setPrefWidth(createScene.getWidth());
        createOrderBorderPane.setPrefHeight(createScene.getHeight());
        createOrderStage.setScene(createScene);
        createOrderStage.show();

        createOrderBorderPane.setCenter(setCenter());
        createOrderBorderPane.setBottom(setBottom());
        createOrderBorderPane.setPadding(new Insets(4));

        focusOrder = MainInterface.tableViewOrders.getSelectionModel().getFocusedIndex();
    }

    ArrayList<Client>  searchClient(String searchClient){
        System.out.println("in search");

        if(!foundClients.isEmpty()) {
            foundClients.clear();
        }


        String delimetr = " ";
        searchClient = searchClient.toLowerCase();
        char[] searchCharArray = searchClient.toCharArray();



        for(Client client : DataBaseHelper.getClientsList()){
            String name = client.getClient().toLowerCase();
            String[] nameWordsArray = name.split(delimetr);

            label1: for(String word: nameWordsArray){
                char[] nameCharArray = word.toCharArray();

                for(int i = 0; i<searchCharArray.length; i++){
                    if(i==nameCharArray.length) break;
                    if(searchCharArray[i] != nameCharArray[i]) break;
                    if(i == searchCharArray.length-1){
                        foundClients.add(client);
                        System.out.println("add");
                        break label1;
                    }
                }
            }
        }

        if(foundClients.isEmpty()){
            for(Client client : DataBaseHelper.getClientsList()){
                String name = client.getClient().toLowerCase();
                char[] nameCharArray = name.toCharArray();
                for(int i = 0; i < searchCharArray.length; i++){
                    if (i == nameCharArray.length) break;
                    if (searchCharArray[i] != nameCharArray[i]) break;
                    if (i == searchCharArray.length - 1){
                        foundClients.add(client);
                        System.out.println("add2");
                    }
                }
            }
        }

        ArrayList<Client> clientsList = new ArrayList<>(foundClients);
        return clientsList;
    }

    static void setClientCombobox(ArrayList<Client> clients){
        ArrayList<Client> clientsList = new ArrayList<>(clients);
        clientCombobox = new ComboBox<>();
        clientCombobox.getItems().addAll(clientsList);


        clientCombobox.itemsProperty().getName();
        clientCombobox.setPrefWidth(25);
        clientCombobox.setMaxWidth(25);
        clientCombobox.setMinWidth(25);

        clientCombobox.setOnAction(event -> {
            System.out.println("in setOnAction");
            if(clientCombobox.getValue() != null) {
                clientHBox.requestFocus();
                clientTextField.setText(clientCombobox.getSelectionModel().getSelectedItem().toString());
            }
        });

        switch (clientHBox.getChildren().size()){
            case 1:
                clientHBox.getChildren().addAll(clientCombobox);
                break;
            case 2:
                clientHBox.getChildren().remove(1);
                clientHBox.getChildren().addAll(clientCombobox);
                break;

                default:
                    System.out.println("default");
                    break;
        }
    }

    VBox setCenter(){
        foundClients = new ArrayList<>();
        if(order!=null){
            datePicker.setValue(order.getDate().toLocalDate());
        } else {
            datePicker.setValue(LocalDate.now());
        }
        datePicker.setConverter(getConverter());
        datePicker.setPrefWidth(100);

        VBox vBox = new VBox();
        vBox.setPrefWidth(createOrderBorderPane.getWidth());
        vBox.setSpacing(15);

        Text textDate = new Text("Дата");
        Text textClient = new Text("Клиент");
        Text textPayment = new Text("Оплата");
        Text textAmount = new Text ("Сумма");
        Text textManager = new Text("Менеджер");
        Text textDesigner = new Text("Дизайнер");

        clientTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("in clientTextField");
            if(clientCombobox.isShowing()) clientCombobox.hide();
            ArrayList<Client> foundClients = new ArrayList<>();
            if(clientTextField.isFocused()) {
                System.out.println("isFocused");
                foundClients.addAll(searchClient(newValue));
                setClientCombobox(foundClients);
                clientCombobox.show();
            }

            if(clientTextField.getText().isEmpty()){
                System.out.println("textField is empty");
                foundClients.clear();
                foundClients.addAll(DataBaseHelper.getClientsList());
                setClientCombobox(foundClients);
                clientCombobox.show();
            }
        });

        clientHBox.getChildren().addAll(clientTextField);
        setClientCombobox(new ArrayList<Client>(DataBaseHelper.getClientsList()));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItemCreateClient = new MenuItem("Новый клиент");
        menuItemCreateClient.setOnAction(event -> {
            MainInterface.clientDialogPane("addInCreate",new Client(), createOrderStage);
        });

        contextMenu.getItems().addAll(menuItemCreateClient);
        clientCombobox.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(clientCombobox, event.getScreenX()+5, event.getScreenY()+5);
            }
        });
        clientTextField.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                contextMenu.show(clientTextField, event.getScreenX()+5, event.getScreenY()+5);
            }
        });
/*
        comboBoxClients.setPrefWidth(150);
        comboBoxClients.setEditable(true);
        new AutoCompleteComboBoxListener<>(comboBoxClients);
        refreshComboBox();
        comboBoxClients.itemsProperty().getName();
        comboBoxClients.setOnAction(event -> {
            if(comboBoxClients.getValue()!=null && comboBoxClients.isFocused()) {
                comboBoxSelectedClient.setValue(comboBoxClients.getValue());
            }
        });
*/
        comboBoxPayment.getItems().addAll(paymentList);
        comboBoxManager.getItems().addAll(DataBaseHelper.getStaffs("менеджер"));
        comboBoxManager.itemsProperty().getName();
        comboBoxManager.setPrefWidth(100);
        comboBoxDesigner.getItems().addAll(DataBaseHelper.getStaffs("дизайнер"));
        comboBoxDesigner.itemsProperty().getName();
        comboBoxDesigner.setPrefWidth(100);

        gridPaneCreateOrder.add(textDate, 0,0);
        gridPaneCreateOrder.add(datePicker,0,1);
        gridPaneCreateOrder.add(textClient, 1,0);
        gridPaneCreateOrder.add(clientHBox, 1,1);
        //gridPaneCreateOrder.add(comboBoxClients,1,1);
        gridPaneCreateOrder.add(textPayment, 2,0);
        gridPaneCreateOrder.add(comboBoxPayment,2,1);
        gridPaneCreateOrder.add(textAmount, 3,0);
        gridPaneCreateOrder.add(textFieldAmount, 3,1);
        gridPaneCreateOrder.add(textManager, 4,0);
        gridPaneCreateOrder.add(comboBoxManager, 4,1);
        gridPaneCreateOrder.add(textDesigner, 5,0);
        gridPaneCreateOrder.add(comboBoxDesigner, 5,1);

        gridPaneCreateOrder.setVgap(5);
        gridPaneCreateOrder.setHgap(10);

        GridPane gridPanePosition = new GridPane();
        TextArea textAreaPosition = new TextArea();
        textAreaPosition.setWrapText(true);
        textAreaPosition.setPrefSize(createOrderBorderPane.getPrefWidth(),300);
        textAreaPosition.setMinHeight(100);
        textAreaPosition.setMaxHeight(250);

        Text textHeadQuantity = new Text("Количество");
        textHeadQuantity.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 12));
        TextField textFieldQuantity = new TextField();
        textFieldQuantity.setPrefWidth(80);
        Text textDefinitionPos = new Text("Описание позиции заказа");
        textDefinitionPos.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 12));

        gridPanePosition.setPadding(new Insets(10,0,10,0));
        gridPanePosition.add(textDefinitionPos,0,0,2,1);
        gridPanePosition.add(textAreaPosition, 0,1,2,3);
        gridPanePosition.add(textHeadQuantity, 3,0);
        gridPanePosition.add(textFieldQuantity,3,1);

        gridPanePosition.setVgap(5);
        gridPanePosition.setHgap(10);

        TitledPane titledPanePositionsList = new TitledPane();
        titledPanePositionsList.setText("Позиции заказа");
        titledPanePositionsList.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        titledPanePositionsList.setCollapsible(false);
        //titledPanePositionsList.setMaxWidth(710);
        titledPanePositionsList.setPrefWidth(createOrderBorderPane.getPrefWidth());
        titledPanePositionsList.setMinHeight(170);

        if(positionsList==null) positionsList = FXCollections.observableArrayList();
        tablePositionsList = new TableView<OrdersPosition>();
        TableColumn<OrdersPosition, String> descriptionCol = new TableColumn<>("Описание позиции");
        descriptionCol.prefWidthProperty().bind(tablePositionsList.widthProperty().multiply(0.738));
        descriptionCol.setOnEditStart((TableColumn.CellEditEvent<OrdersPosition, String> event) ->{
            Tooltip tooltip = new Tooltip("Для внесения изменений нажать Shift+Enter");
            tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
            Tooltip.install(tablePositionsList, tooltip);
        });
        descriptionCol.setCellFactory(TextAreaTableCell.<OrdersPosition>forTableColumn());
        descriptionCol.setOnEditCommit((TableColumn.CellEditEvent<OrdersPosition, String> event) -> {
            TablePosition<OrdersPosition, String> pos = event.getTablePosition();
            String newDescription = event.getNewValue();
            int row = pos.getRow();
            OrdersPosition ordersPosition = event.getTableView().getItems().get(row);
            ordersPosition.setDescription(newDescription);
        });
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<OrdersPosition, String> quantityCol = new TableColumn<>("Количество");
        quantityCol.prefWidthProperty().bind(tablePositionsList.widthProperty().multiply(0.14));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.<OrdersPosition> forTableColumn());
        quantityCol.setOnEditCommit((TableColumn.CellEditEvent<OrdersPosition,String> event)->{
            TablePosition<OrdersPosition, String> pos = event.getTablePosition();
            String newQuantity = event.getNewValue();
            int row = pos.getRow();
            OrdersPosition ordersPosition = event.getTableView().getItems().get(row);
            ordersPosition.setQuantity(newQuantity);
        });


        TableColumn<OrdersPosition, Issue> issueCol = new TableColumn<>("Выдача");
        //issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        issueCol.prefWidthProperty().bind(tablePositionsList.widthProperty().multiply(0.1));
        issueCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrdersPosition, Issue>, ObservableValue<Issue>>() {
            @Override
            public ObservableValue<Issue> call(TableColumn.CellDataFeatures<OrdersPosition, Issue> param) {
                OrdersPosition ordersPosition = param.getValue();
                String issueCode = ordersPosition.getIssue();
                Issue issue = Issue.getByCode(issueCode);
                return new SimpleObjectProperty<Issue>(issue);
            }
        });

        issueCol.setStyle("-fx-alignment: CENTER;");
        issueCol.setCellFactory(ComboBoxTableCell.forTableColumn(issueList));
        issueCol.setOnEditCommit((TableColumn.CellEditEvent<OrdersPosition, Issue> event)->{
            TablePosition<OrdersPosition, Issue> pos = event.getTablePosition();
            Issue newIssue = event.getNewValue();
            int row = pos.getRow();
            OrdersPosition ordersPosition = event.getTableView().getItems().get(row);
            ordersPosition.setIssue(newIssue.getCode());
        });
        tablePositionsList.setEditable(true);
        tablePositionsList.setItems(positionsList);
        tablePositionsList.getColumns().addAll(descriptionCol, quantityCol, issueCol);
        tablePositionsList.setPlaceholder(new Text("Список позиций пуст"));

        titledPanePositionsList.setContent(tablePositionsList);

        HBox hBoxButtons = new HBox();
        hBoxButtons.setSpacing(5);
        Button buttonAddPosition = new Button("Добавить позицию");
        buttonAddPosition.setPrefSize(150, 40);
        buttonAddPosition.setMinSize(150, 40);
        buttonAddPosition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(textAreaPosition.getText().equals("") ||
                textAreaPosition.getText().equals(" ") ||
                textAreaPosition.getText().equals("\n") ||
                textAreaPosition.getText().equals("   ")) MainInterface.getAlertWarningDialog("Отсутствует описание позиции");
                else {
                    if(textFieldQuantity.getText().equals("") ||
                            textFieldQuantity.getText().equals(" ") ||
                            textFieldQuantity.getText().equals("  ")) {
                        textFieldQuantity.setText("Не указано");
                    }
                    changeTablePositionsList(new OrdersPosition(textAreaPosition.getText(), textFieldQuantity.getText()), "add");
                    textAreaPosition.clear();
                    textFieldQuantity.clear();
                }
            }
        });
        Button buttonDeletePosition = new Button("Удалить позицию");
        buttonDeletePosition.setPrefSize(150, 40);
        buttonDeletePosition.setMinSize(150,40);
        buttonDeletePosition.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeTablePositionsList(tablePositionsList.getFocusModel().getFocusedItem(),"delete");
            }
        });

        hBoxButtons.getChildren().addAll(buttonAddPosition, buttonDeletePosition);

        TitledPane titledPaneRemark = new TitledPane();
        titledPaneRemark.setText("Примечание к заказу");
        titledPaneRemark.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        titledPaneRemark.setCollapsible(false);
        titledPaneRemark.setExpanded(true);
        titledPaneRemark.setPrefWidth(createOrderBorderPane.getPrefWidth());
        titledPaneRemark.setPrefHeight(createOrderBorderPane.getPrefHeight()/4);
        titledPaneRemark.setMinHeight(createOrderBorderPane.getPrefHeight()/5);

        textAreaRemark.setWrapText(true);
        //textAreaRemark.setMaxSize(titledPaneRemark.getPrefWidth(), 400);
        //textAreaRemark.setPrefSize(titledPaneRemark.getPrefWidth(),400);
        //textAreaRemark.setMinWidth(titledPaneRemark.getPrefWidth());

        titledPaneRemark.setContent(textAreaRemark);

        vBox.getChildren().addAll(gridPaneCreateOrder, new Separator(), gridPanePosition, hBoxButtons, titledPanePositionsList, titledPaneRemark);

        return vBox;
    }

    void changeTablePositionsList(OrdersPosition position, String what){
        switch (what){
            case "add":
                positionsList.add(position);
                tablePositionsList.setItems(positionsList);
                break;
            case "delete":
                positionsList.remove(position);
                tablePositionsList.setItems(positionsList);
                break;
        }
    }

    HBox setBottom(){
        HBox hBoxButtons = new HBox();
        hBoxButtons.setSpacing(10);
        hBoxButtons.setPadding(new Insets(15));
        Button buttonAddOrder = new Button("Внести заказ");
        buttonAddOrder.setPrefSize(100,40);
        buttonAddOrder.setOnAction(event -> {
            if(checkToMakeOrder()) {
                LocalDate localDate = datePicker.getValue();
                LocalDate localDateNow = LocalDate.now();
                Date date = Date.valueOf(localDate);
                Date dateCreate = Date.valueOf(localDateNow);
                LocalTime localTime = LocalTime.now();
                if(comboBoxDesigner.getValue()==null) comboBoxDesigner.setValue(new Staff(""));

                if(clientCombobox.getValue()!=null){
                    Order order = new Order();
                    order.setDate(date);
                    order.setDateCreate(dateCreate);
                    order.setTimeCreate(localTime);
                    order.setClient(clientCombobox.getValue());
                    order.setId_client(clientCombobox.getValue().getId());
                    order.setPayment(comboBoxPayment.getValue());
                    order.setAmount(textFieldAmount.getText());
                    order.setManager(comboBoxManager.getValue().getName());
                    order.setDesigner(comboBoxDesigner.getValue().getName());
                    order.setLoginCreate(MainInterface.getAccount().getUserName());
                    order.setPositions(positionsList);
                    order.setRemark(textAreaRemark.getText());
                    order.setAvailability("В работе");
                    DataBaseHelper.addOrderToDB(order);

                } else {

                    Client client = new Client();
                    client.setClient(clientTextField.getText());
                    Order order = new Order();
                    order.setDate(date);
                    order.setDateCreate(dateCreate);
                    order.setTimeCreate(localTime);
                    order.setClient(client);
                    order.setPayment(comboBoxPayment.getValue());
                    order.setAmount(textFieldAmount.getText());
                    order.setManager(comboBoxManager.getValue().getName());
                    order.setDesigner(comboBoxDesigner.getValue().getName());
                    order.setLoginCreate(MainInterface.getAccount().getUserName());
                    order.setPositions(positionsList);
                    order.setRemark(textAreaRemark.getText());
                    order.setAvailability("В работе");
                    DataBaseHelper.addOrderToDB(order);
                }
                MainInterface.setAllOrders();
                MainInterface.refreshOrdersList();
                MainInterface.tableViewOrders.getSelectionModel().select(MainInterface.tableViewOrders.getItems().size()-1);
                MainInterface.tableViewOrders.getSelectionModel().focus(MainInterface.tableViewOrders.getItems().size()-1);
                //MainInterface.tableViewOrders.getSelectionModel().selectLast();
                MainInterface.clickedRowOrder(MainInterface.tableViewOrders.getSelectionModel().getSelectedItem());
                MainInterface.tableViewOrders.scrollTo(MainInterface.tableViewOrders.getSelectionModel().getSelectedIndex());
                clientTextField.clear();
                clientHBox.getChildren().clear();
                createOrderStage.close();
            }
        });

        Button buttonEditOrder = new Button("Внести изменения");
        buttonEditOrder.setPrefSize(120,40);
        buttonEditOrder.setOnAction(event -> {
            if(checkToMakeOrder()) {
                LocalDate localDate = datePicker.getValue();
                LocalDate localDateNow = LocalDate.now();
                Date date = Date.valueOf(localDate);
                Date dateEdit = Date.valueOf(localDateNow);
                LocalTime localTime = LocalTime.now();
                order.setDate(date);
                order.setDateEdit(dateEdit);
                order.setTimeEdit(localTime);
                if(clientCombobox.getValue()!=null){
                    order.setClient(clientCombobox.getValue());
                    order.setId_client(clientCombobox.getValue().getId());
                } else {
                    order.setClient(new Client(clientTextField.getText()));
                }
                order.setPayment(comboBoxPayment.getValue());
                order.setAmount(textFieldAmount.getText());
                order.setManager(comboBoxManager.getValue().getName());
                order.setDesigner(comboBoxDesigner.getValue().getName());
                order.setLoginEdit(MainInterface.getAccount().getUserName());
                order.setPositions(positionsList);
                order.setRemark(textAreaRemark.getText());
                DataBaseHelper.editOrder(order);
                MainInterface.setAllOrders();
                MainInterface.refreshOrdersList();
                MainInterface.hBoxPositionsAndClient.getChildren().clear();
                clientTextField.clear();
                createOrderStage.close();
                MainInterface.tableViewOrders.getSelectionModel().focus(focusOrder);
                MainInterface.tableViewOrders.getSelectionModel().select(focusOrder);
                MainInterface.clickedRowOrder(MainInterface.tableViewOrders.getSelectionModel().getSelectedItem());
                MainInterface.tableViewOrders.scrollTo(MainInterface.tableViewOrders.getSelectionModel().getSelectedIndex());

                clientHBox.getChildren().clear();
            }
        });
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(100,40);
        buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientHBox.getChildren().clear();
                textAreaRemark.clear();
                textFieldAmount.clear();
                refreshComboBox();
                clientTextField.clear();
                clientCombobox.getItems().clear();
                if(positionsList!=null){
                    positionsList.clear();
                }
                createOrderStage.close();
            }
        });

        if(order == null) {
            hBoxButtons.getChildren().addAll(buttonAddOrder);
        } else {
            hBoxButtons.getChildren().addAll(buttonEditOrder);
        }

        hBoxButtons.getChildren().addAll(buttonCancel);

        return hBoxButtons;
    }

    void createClient(){
        Stage stageCreateClient = new Stage();
        stageCreateClient.setTitle("Клиент");
        stageCreateClient.initModality(Modality.WINDOW_MODAL);
        stageCreateClient.initOwner(createOrderStage);

        BorderPane borderPaneCreateClient = new BorderPane();
        GridPane gridPaneCreateClient = new GridPane();
        HBox hBoxButtons = new HBox();
        hBoxButtons.setPadding(new Insets(10));
        hBoxButtons.setSpacing(15);
        Button buttonOk = new Button("Ок");
        buttonOk.setPrefSize(100,40);
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(100,40);
        hBoxButtons.getChildren().addAll(buttonOk, buttonCancel);

        Text client = new Text("Клиент");
        TextField textFieldClient = new TextField();
        Text phone = new Text("Контактные телефоны");
        TextField textFieldPhone = new TextField();
        Text email = new Text("E-mail");
        TextField textFieldEmail = new TextField();


        gridPaneCreateClient.add(client,0,0);
        gridPaneCreateClient.add(textFieldClient, 1,0);
        gridPaneCreateClient.add(phone, 0,2);
        gridPaneCreateClient.add(textFieldPhone,1,2);
        gridPaneCreateClient.add(email, 0,3);
        gridPaneCreateClient.add(textFieldEmail,1,3);

        GridPane.setHalignment(client, HPos.RIGHT);
        GridPane.setHalignment(phone, HPos.RIGHT);
        GridPane.setHalignment(email, HPos.RIGHT);
        gridPaneCreateClient.setHgap(25);
        gridPaneCreateClient.setVgap(15);
        gridPaneCreateClient.setPadding(new Insets(15));


        borderPaneCreateClient.setCenter(gridPaneCreateClient);
        borderPaneCreateClient.setBottom(hBoxButtons);

        Scene sceneCreateClient = new Scene(borderPaneCreateClient, 300,200);
        stageCreateClient.setScene(sceneCreateClient);
        stageCreateClient.show();

        buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                refreshComboBox();
                stageCreateClient.close();
            }
        });

        buttonOk.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DataBaseHelper.addClientToDB(new Client(
                        textFieldClient.getText(),
                        textFieldPhone.getText(),
                        textFieldEmail.getText()));
                refreshComboBox();
                stageCreateClient.close();
            }
        });

    }

    static void refreshComboBox(){
        setClientCombobox(new ArrayList<>(DataBaseHelper.getClientsList()));
    }

    StringConverter<LocalDate> getConverter(){

        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if(date!=null){
                    return formatter.format(date);
                }else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if(string !=null && !string.isEmpty()){
                    return LocalDate.parse(string, formatter);
                }else {
                    return null;
                }
            }
        };

        return converter;
    }

    public enum Issue {
        OFFICE ("Офис", "Офис"), WORKSHOP("Цех","Цех");

        private String code;
        private String text;

        private Issue(String code, String text){
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }

        public static Issue getByCode(String issueCode){
            for(Issue i: Issue.values()){
                if(i.code.equals(issueCode)){
                    return i;
                }
            }
            return null;
        }

        @Override
        public String toString(){
            return this.text;
        }
    }

    Boolean checkToMakeOrder(){
        if (clientCombobox.getValue()==null && clientTextField.getText().isEmpty()){
            MainInterface.getAlertWarningDialog("Не указан клиент");
        } else if(comboBoxPayment.getValue()==null){
            MainInterface.getAlertWarningDialog("Не указана оплата");
        } else if (comboBoxManager.getValue()==null){
            MainInterface.getAlertWarningDialog("Не указан менеджер");
        } else if (positionsList.isEmpty()){
            MainInterface.getAlertWarningDialog("Не внесено ни одной позиции заказа");
        } else {
            return true;
        }

        return false;
    }

    static void setAddedClient(){
        System.out.println("in setAddedClient");
        clientHBox.requestFocus();
        clientCombobox.setValue(DataBaseHelper.getClientsList().get(DataBaseHelper.getClientsList().size()-1));
        clientTextField.setText(clientCombobox.getValue().toString());
    }

}
