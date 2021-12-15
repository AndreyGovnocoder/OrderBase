import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class RequestsForm
{
    private Stage _requestFormStage;
    private Scene _requestFormScene;
    private BorderPane _mainBorderPane;
    private TableView<Request> _requestTableView;
    private Menu _changeStatusMenu;
    private ContextMenu _contextMenu;
    private MenuItem _addRequesMenuItem ;
    private MenuItem _editRequestMenuItem;
    private MenuItem _removeRequestMenuItem;
    private MenuItem _setOnConsidered;
    private MenuItem _setOrderedMenuItem;
    private MenuItem _setDeliveredMenuItem;
    private MenuItem _openKindFormMenuItem;
    private CheckBox _notShowDeliveredCheckBox;
    private ComboBox<RequestsKind> _toSortComboBox;


    RequestsForm()
    {
        _requestTableView = new TableView<>();
        setContextMenu();
    }

    void showAndWait(Stage primaryStage)
    {
        _requestFormStage = new Stage();
        _mainBorderPane = new BorderPane();
        _requestFormScene = new Scene(_mainBorderPane, 890, 600);

        _mainBorderPane.setTop(getTop());
        _mainBorderPane.setCenter(getCenter());
        _mainBorderPane.setBottom(getBottom());

        _requestFormStage.initModality(Modality.WINDOW_MODAL);
        _requestFormStage.initOwner(primaryStage);
        _requestFormStage.setScene(_requestFormScene);
        _requestFormStage.setTitle("Заявки");
        _requestFormStage.getIcons().add(MainInterface.getIconLogo());
        _requestFormStage.setOnCloseRequest(event ->
        {
            saveRequestsTableColsWidth();
            saveRequestsStageSize(_requestFormStage);
        });
        loadRequestsStageSize(_requestFormStage);
        _requestFormStage.showAndWait();
    }

    VBox getTop()
    {
        VBox topVBox = new VBox();
        HBox sortHBox = new HBox();
        _notShowDeliveredCheckBox = new CheckBox("Не отображать доставленные");
        _toSortComboBox = new ComboBox<>();
        Label sortLabel = new Label("Сортировка:");
        RequestsKind reqKind = new RequestsKind();
        reqKind.set_kind("Все");
        reqKind.set_id(-1);
        _toSortComboBox.getItems().add(reqKind);
        for (RequestsKind kind : Finder.get_requestKindsList())
            _toSortComboBox.getItems().add(kind);
        _toSortComboBox.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->
        {
            if (newValue != oldValue)
                setRequestTableViewItems(newValue.get_id());
        });
        _toSortComboBox.setValue(reqKind);

        _notShowDeliveredCheckBox.setSelected(true);
        _notShowDeliveredCheckBox.setOnAction(event ->
        {
            setRequestTableViewItems(_toSortComboBox.getValue().get_id());
        });
        _notShowDeliveredCheckBox.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 14));
        sortLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 14));

        sortHBox.setSpacing(5);
        sortHBox.getChildren().addAll(sortLabel, _toSortComboBox);
        sortHBox.setPadding(new Insets(5, 0,5,0));
        sortHBox.setAlignment(Pos.CENTER);

        topVBox.getChildren().addAll(sortHBox, new Separator());
        //topVBox.setSpacing(15);
        topVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(sortHBox, Priority.ALWAYS);
        return topVBox;
    }

    VBox getCenter()
    {
        VBox centerVBox = new VBox();

        setRequestTableView();
        centerVBox.setPadding(new Insets(15));
        centerVBox.setSpacing(15);
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(_requestTableView);
        VBox.setVgrow(_requestTableView, Priority.ALWAYS);
        return centerVBox;
    }

    VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button closeButton = new Button("Закрыть");

        closeButton.setPrefWidth(80);
        closeButton.setOnAction(event ->
        {
            saveRequestsTableColsWidth();
            saveRequestsStageSize(_requestFormStage);
            _requestFormStage.close();
        });

        bottomAnchorPane.getChildren().addAll(closeButton, _notShowDeliveredCheckBox);
        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);
        AnchorPane.setTopAnchor(_notShowDeliveredCheckBox, 5.0);
        AnchorPane.setLeftAnchor(_notShowDeliveredCheckBox, 15.0);
        AnchorPane.setBottomAnchor(_notShowDeliveredCheckBox, 5.0);
        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    void setRequestTableView()
    {
        TableColumn<Request, LocalDateTime> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        //dateCol.prefWidthProperty().bind(_requestFormScene.widthProperty().multiply(0.086));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_dateTime"));
        dateCol.setCellFactory(tc -> new TableCell<Request, LocalDateTime>()
        {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty)
            {
                super.updateItem(date, empty);
                if (empty || date == null)
                {
                    setText(null);
                    setStyle("");
                }
                else
                {
                    if (getTableRow() != null && getTableRow().getItem() != null)
                    {
                        Request request = getTableRow().getItem();
                        if (MainInterface.get_currentAccount() == 9 && !request.is_viewed())
                        {
                            setStyle("-fx-background-color: #ccffcc");
                        }
                        else
                            setStyle("");
                    }

                    String dateTime = MainInterface._formatter.format(date);
                    setText(dateTime);
                    setAlignment(Pos.CENTER);
                }
            }
        });

        TableColumn<Request, Integer> kindCol = new TableColumn<>("Вид");
        kindCol.setCellValueFactory(new PropertyValueFactory<>("_kind"));
        //kindCol.prefWidthProperty().bind(_requestFormScene.widthProperty().multiply(0.1));
        kindCol.setStyle("-fx-alignment: CENTER;");
        kindCol.setCellFactory(tc -> new TableCell<Request, Integer>()
        {
            @Override
            protected void updateItem(Integer kindId, boolean empty)
            {
                super.updateItem(kindId, empty);
                if(empty)
                    setText(null);
                else
                {
                    RequestsKind kind = Finder.getRequestKind(kindId);
                    assert kind != null;
                    String name = kind.get_kind();
                    setText(name);
                }
            }
        });

        TableColumn<Request, Integer> nameCol = new TableColumn<>("Название");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("_valueId"));
        nameCol.setStyle("-fx-alignment: CENTER;");
        //nameCol.prefWidthProperty().bind(_requestFormScene.widthProperty().multiply(0.44));
        nameCol.setCellFactory(tc -> new TableCell<>()
        {
            int kindId = -1;
            private Text text;

            @Override
            protected void updateItem(Integer valueId, boolean empty)
            {
                super.updateItem(valueId, empty);

                if (text != null)
                {
                    text.textProperty().unbind();
                }

                if (empty || valueId == null)
                {
                    setGraphic(null);
                }
                else
                {
                    if (text == null)
                    {
                        text = new Text();
                        text.wrappingWidthProperty().bind(nameCol.widthProperty());
                    }

                    String name = "";
                    if (getTableRow() != null && getTableRow().getItem() != null)
                    {
                        kindId = getTableRow().getItem().get_kind();
                        switch (kindId)
                        {
                            case 1:
                                name = Finder.getMaterial(valueId).toString();
                                break;
                            case 2:
                                name = Finder.getInk(valueId).toString();
                                break;
                            case 3:
                                name = Finder.getLed(valueId).toString();
                                break;
                            case 4:
                                name = Finder.getPowerModule(valueId).toString();
                                break;
                            case 5:
                                name = Finder.getPolygraphy(valueId).toString();
                                break;
                            case 6:
                                name = Finder.getConstruction(valueId).toString();
                                break;
                        }
                        setGraphic(text);
                        setPrefHeight(Control.USE_COMPUTED_SIZE);
                        text.wrappingWidthProperty().bind(nameCol.widthProperty());
                        text.textProperty().setValue(name);
                    }
                }
            }
        });


        TableColumn<Request, String> descriptionCol = new TableColumn<>("Описание");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("_description"));
        descriptionCol.setStyle("-fx-alignment: CENTER;");
        //descriptionCol.prefWidthProperty().bind(_requestFormScene.widthProperty().multiply(0.2));
        descriptionCol.setCellFactory(tc -> new TableCell<>()
        {
            private Text text;

            @Override
            protected void updateItem(String description, boolean empty)
            {
                super.updateItem(description, empty);
                if (text != null)
                {
                    text.textProperty().unbind();
                }
                if (empty || description == null)
                {
                    setGraphic(null);
                }
                else
                {
                    if (text == null)
                    {
                        text = new Text();
                        text.wrappingWidthProperty().bind(getTableColumn().widthProperty());
                    }
                    text.textProperty().bind(itemProperty());
                    setGraphic(text);
                }
            }

        });

        TableColumn<Request, Integer> statusCol = new TableColumn<>("Статус");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("_status"));
        statusCol.setStyle("-fx-alignment: CENTER;");
        //statusCol.prefWidthProperty().bind(_requestFormScene.widthProperty().multiply(0.12));
        statusCol.setCellFactory(tc -> new TableCell<Request, Integer>()
        {
            @Override
            protected void updateItem(Integer statusId, boolean empty)
            {
                super.updateItem(statusId, empty);
                if(empty)
                    setText(null);
                else
                {
                    RequestStatus status = Finder.getRequestStatus(statusId);
                    assert status != null;
                    String name = status.get_status();
                    switch (statusId)
                    {
                        case 1:
                            setTextFill(Color.DARKRED);
                            break;
                        case 2:
                            setTextFill(Color.DARKBLUE);
                            break;
                        case 3:
                            setTextFill(Color.GREEN);
                            break;
                    }
                    setText(name);
                }
            }
        });

        _requestTableView.setRowFactory(new Callback<TableView<Request>, TableRow<Request>>()
        {
            @Override
            public TableRow<Request> call(TableView<Request> tableView)
            {
                TableRow<Request> row = new TableRow<Request>()
                {
                    @Override
                    protected void updateItem(Request request, boolean empty)
                    {
                        super.updateItem(request, empty);
                        this.setFocused(true);
                        if(!empty)
                        {
                            if (request != null && MainInterface.get_currentAccount() == 9 && !request.is_viewed())
                            {
                                //this.setStyle("-fx-background-color: #99ff99");
                            } else
                                this.setStyle("");
                        }
                        else
                        {
                            this.setStyle("");
                        }

                    }
                };

                row.setOnMouseClicked(event ->
                {
                    if (row.getItem() != null && MainInterface.get_currentAccount() == 9 &&!row.getItem().is_viewed())
                    {
                        Request request = row.getItem();
                        final int indexInArray = Finder.get_allRequests().indexOf(request);
                        final int indexInTable = _requestTableView.getItems().indexOf(request);
                        request.set_viewed(true);
                        if (DataBaseStorehouse.editRequest(request))
                        {
                            Finder.get_allRequests().set(indexInArray, request);
                            _requestTableView.getItems().set(indexInTable, request);
                        }
                    }

                    if (event.getButton() == MouseButton.SECONDARY)
                    {
                        if (row.isEmpty())
                        {
                            _contextMenu.getItems().clear();
                            _contextMenu.getItems().add(_addRequesMenuItem);
                            _contextMenu.show(_requestTableView, event.getScreenX() + 10, event.getScreenY() + 5);

                        }
                        else
                        {
                            Request selectedRequest = row.getItem();
                            _contextMenu.getItems().clear();
                            _contextMenu.getItems().addAll(
                                    _addRequesMenuItem,
                                    _editRequestMenuItem,
                                    _removeRequestMenuItem
                            );
                            if(!_requestTableView.getItems().isEmpty())
                            {
                                System.out.println("item is not empty");
                                _changeStatusMenu.getItems().clear();
                                _contextMenu.getItems().addAll(
                                        new SeparatorMenuItem(),
                                        _changeStatusMenu);
                                final int status = _requestTableView.getSelectionModel().getSelectedItem().get_status();
                                switch (status)
                                {
                                    case 1:
                                        _changeStatusMenu.getItems().addAll(_setOrderedMenuItem, _setDeliveredMenuItem);
                                        break;
                                    case 2:
                                        _changeStatusMenu.getItems().addAll(_setOnConsidered, _setDeliveredMenuItem);
                                        break;
                                    case 3:
                                        _changeStatusMenu.getItems().addAll(_setOnConsidered, _setOrderedMenuItem);
                                        break;
                                }

                                switch (selectedRequest.get_kind())
                                {
                                    case 1:
                                        _openKindFormMenuItem.setText("Материалы");
                                        break;
                                    case 2:
                                        _openKindFormMenuItem.setText("Чернила");
                                        break;
                                    case 3:
                                        _openKindFormMenuItem.setText("Светодиоды");
                                        break;
                                    case 4:
                                        _openKindFormMenuItem.setText("Блоки питания");
                                        break;
                                    case 5:
                                        _openKindFormMenuItem.setText("Полиграфия");
                                        break;
                                    case 6:
                                        _openKindFormMenuItem.setText("Конструкция");
                                        break;
                                }
                                _contextMenu.getItems().addAll(new SeparatorMenuItem(), _openKindFormMenuItem);
                                _contextMenu.show(row, event.getScreenX() + 10, event.getScreenY() + 5);
                            }
                        }
                    }
                });
                return row;
            }
        });

        _requestTableView.setOnMouseClicked(event ->
        {
            if(event.getButton() == MouseButton.SECONDARY)
            {
                if(_requestTableView.getItems().isEmpty())
                {
                    _contextMenu.getItems().clear();
                    _contextMenu.getItems().add(_addRequesMenuItem);
                    _contextMenu.show(_requestTableView, event.getScreenX() + 10, event.getScreenY() + 5);
                }
            }
            else
            {
                _contextMenu.hide();
            }
        });

        _requestTableView.getColumns().addAll(
                dateCol,
                kindCol,
                nameCol,
                descriptionCol,
                statusCol);

        _requestTableView.setPlaceholder(new Text("Заявки отсутствуют"));
        setRequestTableViewItems(_toSortComboBox.getValue().get_id());
        _requestTableView.scrollTo(_requestTableView.getItems().size()-1);
        loadRequestsTableColsWidth();
    }

    void setRequestTableViewItems(final int kind)
    {
        _requestTableView.getItems().clear();
        for (Request request : Finder.get_allRequests())
        {
            if (request.get_status() == 3 && _notShowDeliveredCheckBox.isSelected())
                continue;
            if (kind != -1 && request.get_kind() != kind)
                continue;

            _requestTableView.getItems().add(request);
        }
        //_requestTableView.setItems(FXCollections.observableArrayList(Finder.get_allRequests()));
    }

    void setContextMenu()
    {
        _contextMenu = new ContextMenu();
        _addRequesMenuItem = new MenuItem("Добавить");
        _editRequestMenuItem = new MenuItem("Редактировать");
        _removeRequestMenuItem = new MenuItem("Удалить");
        _changeStatusMenu = new Menu("Изменить статус");
        _setOnConsidered = new MenuItem("На рассмотрении");
        _setOrderedMenuItem = new MenuItem("Заказано");
        _setDeliveredMenuItem = new MenuItem("Доставлено");
        _openKindFormMenuItem = new MenuItem();

        _addRequesMenuItem.setOnAction(event ->
        {
            RequestDialog requestDialog = new RequestDialog();
            requestDialog.set_kindComboBox();
            requestDialog.showAndWait(_requestFormStage);
            if (requestDialog.isOk())
            {
                Request newRequest = Finder.getRequest(requestDialog.get_request().get_id());
                if (newRequest != null)
                {
                    final int sortKind = _toSortComboBox.getValue().get_id();
                    if ( sortKind == -1 || sortKind == newRequest.get_kind())
                    {
                        _requestTableView.getItems().add(newRequest);
                        _requestTableView.scrollTo(newRequest);
                    }
                }
            }
        });

        _editRequestMenuItem.setOnAction(event ->
        {
            if (_requestTableView.getSelectionModel().getSelectedItem() != null)
            {
                Request request = _requestTableView.getSelectionModel().getSelectedItem();
                final int indexInTable = _requestTableView.getItems().indexOf(request);
                RequestDialog requestDialog = new RequestDialog(request);
                requestDialog.set_kindComboBox();
                requestDialog.showAndWait(_requestFormStage);
                if (requestDialog.isOk())
                {
                    final int sortKind = _toSortComboBox.getValue().get_id();
                    if ( sortKind == -1 || sortKind == requestDialog.get_request().get_kind())
                    {
                        _requestTableView.getItems().set(indexInTable, requestDialog.get_request());
                    }
                }
            }
        });

        _removeRequestMenuItem.setOnAction(event ->
        {
            if (_requestTableView.getSelectionModel().getSelectedItem() != null)
            {
                if (MainInterface.getAlertAskConfirmationDialog("Вы уверены что хотите удалить заявку?"))
                {
                    Request request = _requestTableView.getSelectionModel().getSelectedItem();
                    final int indexInArray = Finder.getRequestIndexOf(request.get_id());
                    final int indexInTable = _requestTableView.getItems().indexOf(request);
                    if (DataBaseStorehouse.deleteRequest(request.get_id()))
                    {
                        Finder.get_allRequests().remove(indexInArray);
                        _requestTableView.getItems().remove(indexInTable);
                    }
                    else
                        MainInterface.getAlertErrorDialog("Не удалось удалить заявку - ошибка базы данных");
                }
            }
        });

        _setOnConsidered.setOnAction(event ->
        {
            setRequestStatus(1);
        });

        _setOrderedMenuItem.setOnAction(event ->
        {
            setRequestStatus(2);
        });

        _setDeliveredMenuItem.setOnAction(event ->
        {
            setRequestStatus(3);
        });

        _openKindFormMenuItem.setOnAction(event ->
        {
            Request request = _requestTableView.getSelectionModel().getSelectedItem();
            if (request != null)
            {
                switch (request.get_kind())
                {
                    case 1:
                        MaterialsForm materialsForm = new MaterialsForm();
                        materialsForm.showAndWait(_requestFormStage);
                        break;
                    case 2:
                        InksForm inksForm = new InksForm();
                        inksForm.set_currAccount(MainInterface.get_currentAccount());
                        inksForm.showAndWait(_requestFormStage);
                        break;
                    case 3:
                        LedsForm ledsForm = new LedsForm();
                        ledsForm.showAndWait(_requestFormStage);
                        break;
                    case 4:
                        PowerModulesForm powerModulesForm = new PowerModulesForm();
                        powerModulesForm.showAndWait(_requestFormStage);
                        break;
                    case 5:
                        PolygraphyForm polygraphyForm = new PolygraphyForm();
                        polygraphyForm.showAndWait(_requestFormStage);
                        break;
                    case 6:
                        ConstructionsForm constructionsForm = new ConstructionsForm();
                        constructionsForm.showAndWait(_requestFormStage);
                        break;
                }
            }
        });
    }

    private void setRequestStatus(final int status)
    {
        if (_requestTableView.getSelectionModel().getSelectedItem() != null)
        {
            Request request = _requestTableView.getSelectionModel().getSelectedItem();
            final int indexInTable = _requestTableView.getItems().indexOf(request);
            final int indexInArray = Finder.getRequestIndexOf(request.get_id());
            request.set_status(status);
            if (DataBaseStorehouse.editRequest(request))
            {
                if (status == 3 && _notShowDeliveredCheckBox.isSelected())
                    _requestTableView.getItems().remove(indexInTable);
                else
                    _requestTableView.getItems().set(indexInTable, request);
                Finder.get_allRequests().set(indexInArray, request);
            }
        }

    }

    private void saveRequestsTableColsWidth()
    {
        Properties tableColumnsWidthProp =
                Finder._settings.getPropertiesTableColumsWidth("_requestTableView");
        if (tableColumnsWidthProp == null)
        {
            tableColumnsWidthProp = new Properties();
            for (int i = 0; i < _requestTableView.getColumns().size(); ++i)
            {
                tableColumnsWidthProp.put(String.valueOf(i), _requestTableView.getColumns().get(i).getWidth());
            }
            Finder._settings.addPropertiesColWidths("_requestTableView", tableColumnsWidthProp);
        }
        else
        {
            for (int i = 0; i < _requestTableView.getColumns().size(); ++i)
                tableColumnsWidthProp.put(String.valueOf(i), _requestTableView.getColumns().get(i).getWidth());
        }
    }

    private void loadRequestsTableColsWidth()
    {
        try
        {
            Properties tableProperties = Finder._settings.getPropertiesTableColumsWidth("_requestTableView");
            if (tableProperties != null && tableProperties.size() > 0)
            {
                for (int i = 0; i < _requestTableView.getColumns().size(); ++i)
                {
                    //System.out.println("col " + i + ": " + (double)tableProperties.get(String.valueOf(i)));
                    _requestTableView.getColumns().get(i).setPrefWidth((double)tableProperties.get(String.valueOf(i)));
                }
            }
            else
                _requestTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }


    private void saveRequestsStageSize(Stage requestsStage)
    {
        Properties propertiesStageSizes =
                Finder._settings.getPropertiesStageSizes("requestsStage");
        if (propertiesStageSizes == null)
        {
            propertiesStageSizes = new Properties();
            propertiesStageSizes.put("width", requestsStage.getWidth());
            propertiesStageSizes.put("height", requestsStage.getHeight());
            Finder._settings.addPropertiesStageSizes("requestsStage", propertiesStageSizes);
        } else
        {
            propertiesStageSizes.put("width", requestsStage.getWidth());
            propertiesStageSizes.put("height", requestsStage.getHeight());
        }
    }

    private void loadRequestsStageSize(Stage requestsStage)
    {
        try
        {
            Properties properties = Finder._settings.getPropertiesStageSizes("requestsStage");
            if (properties != null && properties.size() > 0)
            {
                requestsStage.setWidth((double)properties.get("width"));
                requestsStage.setHeight((double)properties.get("height"));
            }

        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }
}
