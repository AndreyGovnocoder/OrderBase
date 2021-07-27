import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.PopupWindow;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class OrderDialog
{
    private Order _order;
    private Stage _orderDialogStage;
    private Scene _orderDialogScene;
    private boolean _editOrder = false;
    private DatePicker _datePicker;
    private TextField _clientTextField;
    private ComboBox<Client> _clientCombobox;
    private HBox _clientHBox;
    private ComboBox<String> _paymentCombobox;
    private ComboBox<Staff> _managerCombobox;
    private ComboBox<Staff> _designerCombobox;
    private TextField _amountTextField;
    private TableView<OrderPosition> _positionsTableView;
    private TextArea _remarkTextArea;
    private ArrayList<Client> _foundClients;
    final private List<String> _paymentList = FXCollections.observableArrayList(
            MainInterface.PAYMENT_PAID,
            MainInterface.PAYMENT_UNPAID,
            MainInterface.PAYMENT_50);
    boolean _ok = false;

    OrderDialog(Order order)
    {
        _editOrder = true;
        this._order = order;
    }

    OrderDialog(){}

    void showAndWait(Stage primaryStage)
    {
        _orderDialogStage = new Stage();
        BorderPane orderDialogBorderPane = new BorderPane();
        _orderDialogScene = new Scene(orderDialogBorderPane, 810, 700);

        orderDialogBorderPane.setCenter(getCenter());
        orderDialogBorderPane.setBottom(getBottom());

        _clientTextField.requestFocus();

        _orderDialogStage.setOnCloseRequest(event ->
        {
            if(MainInterface.getAlertAskConfirmationDialog("Все несохранённые данные будут потеряны\n\nВы уверены?\n"))
                _orderDialogStage.close();
            else
                event.consume();
        });

        if(_editOrder)
            _orderDialogStage.setTitle("Редактирование заказа");
        else
            _orderDialogStage.setTitle("Создание нового заказа");
        _orderDialogStage.getIcons().add(MainInterface.getIconLogo());
        _orderDialogStage.initModality(Modality.WINDOW_MODAL);
        _orderDialogStage.initOwner(primaryStage);
        _orderDialogStage.setScene(_orderDialogScene);
        _orderDialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        GridPane centerGridPane = new GridPane();
        GridPane positionGridPane = new GridPane();
        _clientHBox = new HBox();
        Text dateText = new Text("Дата");
        Text clientText = new Text("Клиент");
        Text paymentText = new Text("Оплата");
        Text amountText = new Text ("Сумма");
        Text managerText = new Text("Менеджер");
        Text designerText = new Text("Дизайнер");
        Text headQuantityText = new Text("Количество");
        Text definitionPosText = new Text("Описание позиции заказа");
        TextField quantityTextField = new TextField();
        _datePicker = new DatePicker();
        _clientTextField = new TextField();
        _clientCombobox = new ComboBox<>();
        _paymentCombobox = new ComboBox<>();
        _amountTextField = new TextField();
        _managerCombobox = new ComboBox<>();
        _designerCombobox = new ComboBox<>();
        _remarkTextArea = new TextArea();
        _foundClients = new ArrayList<>();
        TitledPane positionsListTitledPane = new TitledPane();
        TextArea positionTextArea = new TextArea();
        HBox buttonsHBox = new HBox();
        Button addPositionButton = new Button("Добавить позицию");
        Button deletePositionButton = new Button("Удалить позицию");
        Text issueText = new Text("Выдача");
        ComboBox<Issue> issueComboBox = new ComboBox<>();
        VBox issueVBox = new VBox();
        TitledPane remarkTitledPane = new TitledPane();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem createClientMenuItem = new MenuItem("Новый клиент");

        _datePicker.setConverter(getConverter());
        _datePicker.setValue(LocalDate.now());
        _datePicker.setPrefWidth(100);
        _datePicker.setEditable(false);

        setStaffsCombobox();
        setClientCombobox();

        _clientCombobox.setOnAction(event ->
        {
            if(_clientCombobox.getValue() != null)
            {
                _clientHBox.requestFocus();
                _clientTextField.setText(_clientCombobox.getSelectionModel().getSelectedItem().toString());
            }
        });

        _clientTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            if(_clientCombobox.isShowing())
                _clientCombobox.hide();
            if(_clientTextField.isFocused())
            {
                _clientCombobox.getItems().clear();
                _clientCombobox.setItems(FXCollections.observableArrayList(searchClient(newValue)));
                _clientCombobox.show();
            }

            if(_clientTextField.getText().isEmpty())
            {
                _clientCombobox.getItems().clear();
                _clientCombobox.setItems(FXCollections.observableArrayList(MainInterface.getActiveClients()));
                _clientCombobox.show();
            }
        });
        _clientHBox.getChildren().addAll(_clientTextField, _clientCombobox);

        _paymentCombobox.getItems().addAll(_paymentList);
        _paymentCombobox.setValue(_paymentList.get(1));

        createClientMenuItem.setOnAction(event ->
        {
            ClientDialog clientDialog = new ClientDialog();
            clientDialog.showAndWait(_orderDialogStage);
            if(clientDialog._ok)
            {
                if(DataBase.addClient(clientDialog.get_client()))
                {
                    clientDialog.get_client().set_id(DataBase.getLastId(DataBase.CLIENTS_TABLE));
                    _clientCombobox.getItems().add(clientDialog.get_client());
                    _clientCombobox.setValue(clientDialog.get_client());
                    Finder.get_allClients().add(clientDialog.get_client());
                    MainInterface.getActiveClients().add(clientDialog.get_client());
                }
            }
        });

        contextMenu.getItems().add(createClientMenuItem);
        _clientCombobox.setOnContextMenuRequested(event ->
        {
            contextMenu.show(_clientCombobox, event.getScreenX()+5, event.getScreenY()+5);
        });
        _clientTextField.setOnContextMenuRequested(event ->
        {
            contextMenu.show(_clientTextField, event.getScreenX()+5, event.getScreenY()+5);
        });

        centerGridPane.add(dateText, 0,0);
        centerGridPane.add(clientText, 1,0);
        centerGridPane.add(paymentText, 2,0);
        centerGridPane.add(amountText, 3,0);
        centerGridPane.add(managerText, 4,0);
        centerGridPane.add(designerText, 5, 0);
        centerGridPane.add(_datePicker, 0,1);
        centerGridPane.add(_clientHBox, 1,1);
        centerGridPane.add(_paymentCombobox, 2,1);
        centerGridPane.add(_amountTextField, 3,1);
        centerGridPane.add(_managerCombobox, 4,1);
        centerGridPane.add(_designerCombobox, 5,1);
        centerGridPane.setVgap(5);
        centerGridPane.setHgap(10);

        positionTextArea.setWrapText(true);
        positionTextArea.setPrefSize(_orderDialogScene.getWidth(),300);
        positionTextArea.setMinHeight(70);
        positionTextArea.setMaxHeight(250);

        headQuantityText.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 12));
        definitionPosText.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 12));
        quantityTextField.setPrefWidth(80);

        issueText.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 12));
        issueComboBox.setItems(FXCollections.observableArrayList(Issue.values()));

        issueVBox.setPadding(new Insets(2));
        issueVBox.setSpacing(3);
        issueVBox.getChildren().addAll(issueText, issueComboBox);

        positionGridPane.setPadding(new Insets(10,0,10,0));
        positionGridPane.add(definitionPosText, 0,0,2,1);
        positionGridPane.add(positionTextArea,0,1,2,2);
        positionGridPane.add(headQuantityText, 3,0);
        positionGridPane.add(quantityTextField,3,1);
        positionGridPane.add(issueVBox, 3,2,1,2);
        positionGridPane.setVgap(5);
        positionGridPane.setHgap(10);

        setPositionsTableView();

        positionsListTitledPane.setText("Позиции заказа");
        positionsListTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        positionsListTitledPane.setCollapsible(false);
        positionsListTitledPane.setPrefWidth(_orderDialogScene.getWidth());
        positionsListTitledPane.setMinHeight(170);
        positionsListTitledPane.setContent(_positionsTableView);

        addPositionButton.setOnAction(event ->
        {
            if(positionTextArea.getText().equals("") ||
                    positionTextArea.getText().equals(" ") ||
                    positionTextArea.getText().equals("\n") ||
                    positionTextArea.getText().equals("   ")) MainInterface.getAlertWarningDialog("Отсутствует описание позиции");
            else {
                if(
                        quantityTextField.getText().equals("") ||
                        quantityTextField.getText().equals(" ") ||
                        quantityTextField.getText().equals("  "))
                {
                    quantityTextField.setText("Не указано");
                }
                OrderPosition orderPosition = new OrderPosition();
                orderPosition.set_description(positionTextArea.getText());
                orderPosition.set_quantity(quantityTextField.getText());
                if(!issueComboBox.getSelectionModel().isEmpty()) orderPosition.set_issue(issueComboBox.getValue().text);
                _positionsTableView.getItems().add(orderPosition);
                _positionsTableView.scrollTo(orderPosition);
                positionTextArea.clear();
                quantityTextField.clear();
                issueComboBox.getSelectionModel().clearSelection();
            }
        });

        deletePositionButton.setOnAction(event ->
        {
            _positionsTableView.getItems().remove(_positionsTableView.getFocusModel().getFocusedItem());
        });

        buttonsHBox.setSpacing(5);
        buttonsHBox.getChildren().addAll(addPositionButton, deletePositionButton);

        _remarkTextArea.setWrapText(true);

        remarkTitledPane.setText("Примечание к заказу");
        remarkTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        remarkTitledPane.setCollapsible(false);
        remarkTitledPane.setExpanded(true);
        remarkTitledPane.setPrefWidth(_orderDialogScene.getWidth());
        remarkTitledPane.setPrefHeight(_orderDialogScene.getHeight()/4);
        remarkTitledPane.setMinHeight(_orderDialogScene.getHeight()/5);
        remarkTitledPane.setContent(_remarkTextArea);

        if(_editOrder)
        {
            _datePicker.setValue(_order.get_date().toLocalDate());
            _clientTextField.setText(Finder.getClient(_order.get_client()).get_name());
            _clientCombobox.setValue(Finder.getClient(_order.get_client()));
            _paymentCombobox.setValue(_order.get_payment());
            _amountTextField.setText(_order.get_amount());
            _managerCombobox.setValue(Finder.getStaff(_order.get_manager()));
            _designerCombobox.setValue(Finder.getStaff(_order.get_designer()));
            _positionsTableView.setItems(
                    FXCollections.observableArrayList(DataBase.getOrderPositions(_order.get_id())));
            _remarkTextArea.setText(_order.get_remark());
        }

        centerVBox.setPrefWidth(_orderDialogScene.getWidth());
        centerVBox.setSpacing(15);
        centerVBox.setPadding(new Insets(10));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(
                centerGridPane,
                positionGridPane,
                new Separator(),
                buttonsHBox,
                positionsListTitledPane,
                remarkTitledPane);
        return centerVBox;
    }


    private VBox getBottom()
    {
        HBox buttonsPane = new HBox();
        VBox bottomVBox = new VBox();
        Button createOrderButton = new Button("Создать заказ");
        Button saveChangesButton = new Button("Сохранить изменения");
        Button closeButton = new Button("Отмена");

        createOrderButton.setOnAction(event ->
        {
            if(set_order())
            {
                _ok = true;
                _orderDialogStage.close();
            }
        });

        saveChangesButton.setOnAction(event ->
        {
            if(set_order())
            {
                _ok = true;
                _orderDialogStage.close();
            }
        });

        closeButton.setOnAction(event ->
        {
            if(MainInterface.getAlertAskConfirmationDialog("Все несохранённые данные будут потеряны\n\nВы уверены?"))
            {
                _orderDialogStage.close();
            }
        });

        buttonsPane.setSpacing(15);
        buttonsPane.setPadding(new Insets(15));
        if(_editOrder) buttonsPane.getChildren().addAll(saveChangesButton, closeButton);
        else buttonsPane.getChildren().addAll(createOrderButton, closeButton);

        bottomVBox.getChildren().addAll(new Separator(), buttonsPane);
        return bottomVBox;
    }

    private void setClientCombobox()
    {
        _clientCombobox.getItems().addAll(MainInterface.getActiveClients());

        _clientCombobox.itemsProperty().getName();
        _clientCombobox.setPrefWidth(25);
        _clientCombobox.setMaxWidth(25);
        _clientCombobox.setMinWidth(25);

        _clientCombobox.setOnAction(event ->
        {
            if(_clientCombobox.getValue() != null)
            {
                _clientHBox.requestFocus();
                _clientTextField.setText(_clientCombobox.getSelectionModel().getSelectedItem().toString());
            }
        });

        /*
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
         */
    }

    private void setStaffsCombobox()
    {
        _managerCombobox.setPrefWidth(100);
        _designerCombobox.setPrefWidth(100);
        ArrayList<Staff> managerList = new ArrayList<>();
        List<Staff> designerList = new ArrayList<>();
        designerList.add(null);
        for(Staff staff : MainInterface.getActiveStaffs())
        {
            switch (staff.get_position())
            {
                case "менеджер":
                    managerList.add(staff);
                    System.out.println("staff id = " + staff.get_id() + "; currAcc id = " + MainInterface.get_currentAccount());
                    if (staff.get_id() == Finder.getAccount(MainInterface.get_currentAccount()).get_staffId())
                    {
                        System.out.println("add " + staff.get_name() + " to value");
                        _managerCombobox.setValue(staff);
                    }
                    break;
                case "дизайнер":
                    designerList.add(staff);
                    break;
            }
        }
        _managerCombobox.getItems().addAll(managerList);
        _designerCombobox.getItems().addAll(designerList);

    }

    private void setPositionsTableView()
    {
        _positionsTableView = new TableView<>();
        _positionsTableView.setPlaceholder(new Text("Список позиций пуст"));

        TableColumn<OrderPosition, String> descriptionCol = new TableColumn<>("Описание позиции");
        descriptionCol.prefWidthProperty().bind(_positionsTableView.widthProperty().multiply(0.738));
        descriptionCol.setOnEditStart((TableColumn.CellEditEvent<OrderPosition, String> event) ->
        {
            Tooltip tooltip = new Tooltip("Для внесения изменений нажать Shift+Enter");
            tooltip.setAnchorLocation(PopupWindow.AnchorLocation.WINDOW_BOTTOM_LEFT);
            Tooltip.install(_positionsTableView, tooltip);
        });
        descriptionCol.setCellFactory(TextAreaTableCell.<OrderPosition>forTableColumn());
        descriptionCol.setOnEditCommit((TableColumn.CellEditEvent<OrderPosition, String> event) ->
        {
            TablePosition<OrderPosition, String> pos = event.getTablePosition();
            String newDescription = event.getNewValue();
            int row = pos.getRow();
            OrderPosition orderPosition = event.getTableView().getItems().get(row);
            orderPosition.set_description(newDescription);
        });
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("_description"));

        TableColumn<OrderPosition, String> quantityCol = new TableColumn<>("Количество");
        quantityCol.prefWidthProperty().bind(_positionsTableView.widthProperty().multiply(0.14));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.setCellFactory(TextFieldTableCell.<OrderPosition> forTableColumn());
        quantityCol.setOnEditCommit((TableColumn.CellEditEvent<OrderPosition,String> event)->
        {
            TablePosition<OrderPosition, String> pos = event.getTablePosition();
            String newQuantity = event.getNewValue();
            int row = pos.getRow();
            OrderPosition orderPosition = event.getTableView().getItems().get(row);
            orderPosition.set_quantity(newQuantity);
        });


        TableColumn<OrderPosition, Issue> issueCol = new TableColumn<>("Выдача");
        //issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        issueCol.prefWidthProperty().bind(_positionsTableView.widthProperty().multiply(0.1));
        issueCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderPosition, Issue>, ObservableValue<Issue>>()
        {
            @Override
            public ObservableValue<Issue> call(TableColumn.CellDataFeatures<OrderPosition, Issue> param)
            {
                OrderPosition orderPosition = param.getValue();
                String issueCode = orderPosition.get_issue();
                Issue issue = Issue.getByCode(issueCode);
                return new SimpleObjectProperty<Issue>(issue);
            }
        });

        issueCol.setStyle("-fx-alignment: CENTER;");
        issueCol.setCellFactory(ComboBoxTableCell.forTableColumn(FXCollections.observableArrayList(Issue.values())));
        issueCol.setOnEditCommit((TableColumn.CellEditEvent<OrderPosition, Issue> event)->
        {
            TablePosition<OrderPosition, Issue> pos = event.getTablePosition();
            Issue newIssue = event.getNewValue();
            int row = pos.getRow();
            OrderPosition orderPosition = event.getTableView().getItems().get(row);
            orderPosition.set_issue(newIssue.getCode());
        });

        _positionsTableView.setEditable(true);
        _positionsTableView.getColumns().addAll(descriptionCol, quantityCol, issueCol);
    }

    ArrayList<Client>  searchClient(String searchClient)
    {

        if(!_foundClients.isEmpty())
        {
            _foundClients.clear();
        }

        String delimetr = " ";
        searchClient = searchClient.toLowerCase();
        char[] searchCharArray = searchClient.toCharArray();


        for(Client client : MainInterface.getActiveClients())
        {
            String name = client.get_name().toLowerCase();
            String[] nameWordsArray = name.split(delimetr);

            label1: for(String word: nameWordsArray)
            {
                char[] nameCharArray = word.toCharArray();

                for(int i = 0; i<searchCharArray.length; i++)
                {
                    if(i==nameCharArray.length) break;
                    if(searchCharArray[i] != nameCharArray[i]) break;
                    if(i == searchCharArray.length-1){
                        _foundClients.add(client);
                        break label1;
                    }
                }
            }
        }

        if(_foundClients.isEmpty())
        {
            for(Client client : MainInterface.getActiveClients())
            {
                String name = client.get_name().toLowerCase();
                char[] nameCharArray = name.toCharArray();
                for(int i = 0; i < searchCharArray.length; i++)
                {
                    if (i == nameCharArray.length) break;
                    if (searchCharArray[i] != nameCharArray[i]) break;
                    if (i == searchCharArray.length - 1)
                    {
                        _foundClients.add(client);
                    }
                }
            }
        }

        return new ArrayList<>(_foundClients);
    }

    private StringConverter<LocalDate> getConverter()
    {
        return new StringConverter<LocalDate>()
        {
            @Override
            public String toString(LocalDate date)
            {
                if(date!=null)
                {
                    return MainInterface._formatter.format(date);
                }else
                    return "";
            }
            @Override
            public LocalDate fromString(String string)
            {
                if(string !=null && !string.isEmpty())
                {
                    return LocalDate.parse(string, MainInterface._formatter);
                }else
                    {
                    return null;
                }
            }
        };
    }

    public enum Issue
    {
        OFFICE ("Офис", "Офис"), WORKSHOP("Цех","Цех");

        private String code;
        private String text;

        private Issue(String code, String text)
        {
            this.code = code;
            this.text = text;
        }

        public String getCode() {
            return code;
        }

        public String getText() {
            return text;
        }

        public static Issue getByCode(String issueCode)
        {
            for(Issue i: Issue.values())
            {
                if(i.code.equals(issueCode))
                {
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

    Order get_order() { return _order; }

    ArrayList<OrderPosition> getOrderPositionsList()
    {
        return new ArrayList<OrderPosition>(_positionsTableView.getItems());
    }

    private boolean set_order()
    {
        if(checkToMakeOrder())
        {
            if(!_editOrder) _order = new Order();
            _order.set_date(Date.valueOf(_datePicker.getValue()));
            _order.set_client(_clientCombobox.getValue().get_id());
            _order.set_amount(_amountTextField.getText());
            _order.set_payment(_paymentCombobox.getValue());
            _order.set_manager(_managerCombobox.getValue().get_id());
            if(_designerCombobox.getSelectionModel().isEmpty() || _designerCombobox.getValue() == null)
                _order.set_designer(-1);
            else
                _order.set_designer(_designerCombobox.getValue().get_id());
            _order.set_availability("В работе");
            _order.set_remark(_remarkTextArea.getText());
            if(_editOrder)
            {
                _order.set_accountEdit(MainInterface.get_currentAccount());
                _order.set_dateTimeEdit(LocalDateTime.now());
            } else
            {
                _order.set_accountCreate(MainInterface.get_currentAccount());
                _order.set_accountEdit(-1);
                _order.set_accountAvailability(-1);
                _order.set_dateTimeCreate(LocalDateTime.now());
                _order.set_dateTimeEdit(null);
                _order.set_dateTimeAvailability(null);
            }
            return true;
        }
        return false;
    }

    private boolean checkToMakeOrder()
    {
        if (_clientCombobox.getValue() == null && _clientTextField.getText().isEmpty())
        {
            MainInterface.getAlertWarningDialog("Не указан клиент");
        } else if (_clientCombobox.getValue() == null && !_clientTextField.getText().isEmpty())
        {
            MainInterface.getAlertWarningDialog("Такого клиента не существует.\nДобавьте этого клиента в базу данных либо выберите клиента из списка");
        } else if(_paymentCombobox.getValue() == null)
        {
            MainInterface.getAlertWarningDialog("Не указана оплата");
        } else if (_managerCombobox.getValue() == null)
        {
            MainInterface.getAlertWarningDialog("Не указан менеджер");
        } else if (_positionsTableView.getItems().isEmpty())
        {
            MainInterface.getAlertWarningDialog("Не внесено ни одной позиции заказа");
        } else
        {
            return true;
        }

        return false;
    }
}
