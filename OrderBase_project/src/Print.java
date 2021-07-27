import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.print.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

class Print
{
    static TableView<OrderPosition> _positionsToPrintTableView;
    static TableView<OrderPosition> _orderPositionsTableView;
    static Stage _printStage;
    static Scene _printScene;

    static void toPrint(Order order, Stage primaryStage)
    {
        _printStage = new Stage();
        BorderPane printBorderPane = new BorderPane();
        _printScene = new Scene(printBorderPane, 600, 600);
        _positionsToPrintTableView = new TableView<>();
        _orderPositionsTableView = new TableView<>();
        VBox centerVBox = new VBox();
        HBox buttonsHBox = new HBox();
        TitledPane orderTitledPane = new TitledPane();
        GridPane orderGridPane = new GridPane();
        TitledPane toPrintTitledPane = new TitledPane();
        Label headDateLabel = new Label("Дата: ");
        Label headClientLabel = new Label("Заказчик: ");
        Label headManagerLabel = new Label("Менеджер: ");
        Label headGetDateLabel = new Label(order.get_date().toLocalDate().format(MainInterface._formatter));
        Label headGetClientLabel = new Label(DataBase.getClient(order.get_client()).get_name());
        Label headGetManagerLabel = new Label(DataBase.getStaff(order.get_manager()).get_name());
        Button toPrintButton = new Button("Распечатать накладную");
        Button cancelButton = new Button("Отмена");

        toPrintButton.setOnAction(event ->
        {
            if(_positionsToPrintTableView.getItems().isEmpty())
            {
                MainInterface.getAlertInformationDialog("Список позиций пуст.\n" +
                        "Для печати квитанции необходимо добавить позиции на печать");
            }
            else
            {
                print(order);
            }
        });
        cancelButton.setOnAction(event -> _printStage.close());

        headGetDateLabel.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));
        headGetClientLabel.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));
        headGetManagerLabel.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));

        orderGridPane.add(headDateLabel, 0,0);
        orderGridPane.add(headClientLabel, 0,1);
        orderGridPane.add(headManagerLabel,0,2);
        orderGridPane.add(headGetDateLabel,1,0);
        orderGridPane.add(headGetClientLabel, 1,1);
        orderGridPane.add(headGetManagerLabel,1,2);
        GridPane.setHalignment(headDateLabel, HPos.RIGHT);
        GridPane.setHalignment(headClientLabel, HPos.RIGHT);
        GridPane.setHalignment(headManagerLabel, HPos.RIGHT);

        orderTitledPane.setText("Заказ");
        orderTitledPane.setCollapsible(false);
        orderTitledPane.setExpanded(true);
        orderTitledPane.setContent(orderGridPane);

        _orderPositionsTableView.setEditable(true);
        TableColumn<OrderPosition, String> descriptionCol = new TableColumn<>("Позиция");
        descriptionCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.61));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("_description"));
        TableColumn<OrderPosition, String> quantityCol = new TableColumn<>("Количество");
        quantityCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.14));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrderPosition, String> issueCol = new TableColumn<>("Выдача");
        issueCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.13));
        issueCol.setCellValueFactory(new PropertyValueFactory<>("_issue"));
        issueCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrderPosition, Boolean> printCol = new TableColumn<>("Печать");
        printCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.09));
        printCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrderPosition, Boolean>, ObservableValue<Boolean>>()
        {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<OrderPosition, Boolean> param)
            {
                OrderPosition position = param.getValue();

                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(position.get_toPrint());

                // Note: singleCol.setOnEditCommit(): Not work for
                // CheckBoxTableCell.

                // When "Single?" column change.
                booleanProp.addListener(new ChangeListener<Boolean>()
                {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                        Boolean newValue)
                    {
                        position.set_toPrint(newValue);
                        if(newValue)
                        {
                            int x = _positionsToPrintTableView.getItems().size();
                            position.set_number(++x);
                            _positionsToPrintTableView.getItems().add(position);
                        } else
                        {
                            _positionsToPrintTableView.getItems().remove(position);
                        }
                    }
                });
                return booleanProp;
            }
        });
        printCol.setCellFactory(new Callback<TableColumn<OrderPosition, Boolean>, //
                TableCell<OrderPosition, Boolean>>()
        {
            @Override
            public TableCell<OrderPosition, Boolean> call(TableColumn<OrderPosition, Boolean> p)
            {
                CheckBoxTableCell<OrderPosition, Boolean> cell = new CheckBoxTableCell<OrderPosition, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        _orderPositionsTableView.setItems(FXCollections.observableArrayList(MainInterface.getOrdersPositionsList(order.get_id())));
        _orderPositionsTableView.getColumns().addAll(descriptionCol, quantityCol, issueCol, printCol);

        TableColumn<OrderPosition, String> descriptionPrintCol = new TableColumn<>("Наименование работ, услуг");
        descriptionPrintCol.prefWidthProperty().bind(_positionsToPrintTableView.widthProperty().multiply(0.78));
        descriptionPrintCol.setCellValueFactory(new PropertyValueFactory<>("_description"));
        TableColumn<OrderPosition, String> quantityPrintCol = new TableColumn<>("Количество");
        quantityPrintCol.prefWidthProperty().bind(_positionsToPrintTableView.widthProperty().multiply(0.14));
        quantityPrintCol.setStyle("-fx-alignment: CENTER;");
        quantityPrintCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        TableColumn<OrderPosition, Integer> numberPrintCol = new TableColumn<>("№");
        numberPrintCol.prefWidthProperty().bind(_positionsToPrintTableView.widthProperty().multiply(0.05));
        numberPrintCol.setStyle("-fx-alignment: CENTER;");
        numberPrintCol.setCellFactory(tc -> new TableCell<OrderPosition, Integer>()
        {
            @Override
            protected void updateItem(Integer number, boolean empty)
            {
                super.updateItem(number, empty);
                if (empty)
                {
                    setText(null);
                } else
                {
                    setText(String.valueOf(getTableRow().getIndex() + 1));
                }
            }
        });

        _positionsToPrintTableView.setPlaceholder(new Text("Список позиций пуст"));
        _positionsToPrintTableView.setEditable(false);
        _positionsToPrintTableView.getColumns().addAll(numberPrintCol, descriptionPrintCol, quantityPrintCol);

        toPrintTitledPane.setPadding(new Insets(10));
        toPrintTitledPane.setText("Печать");
        toPrintTitledPane.setContent(_positionsToPrintTableView);
        toPrintTitledPane.setExpanded(true);
        toPrintTitledPane.setCollapsible(false);

        centerVBox.setSpacing(10);
        centerVBox.setPadding(new Insets(15));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(orderTitledPane, _orderPositionsTableView, new Separator(), toPrintTitledPane);

        buttonsHBox.setSpacing(10);
        buttonsHBox.setPadding(new Insets(15));
        buttonsHBox.getChildren().addAll(toPrintButton, cancelButton);

        printBorderPane.setCenter(centerVBox);
        printBorderPane.setBottom(buttonsHBox);

        _printStage.setTitle("Печать накладной");
        _printStage.getIcons().add(MainInterface.getIconLogo());
        _printStage.initModality(Modality.WINDOW_MODAL);
        _printStage.initOwner(primaryStage);
        _printStage.setScene(_printScene);
        _printStage.showAndWait();
    }

    private static void print(Order order)
    {
        String positionsString = "";
        for(OrderPosition position : _positionsToPrintTableView.getItems())
        {
            positionsString += position.get_description() + "\n";
        }

        BorderPane printBorderPane = new BorderPane();
        GridPane printGridPane = new GridPane();
        Text numberText = new Text("№");
        Text descriptionText = new Text("Наименование работ, услуг");
        Text quantityText = new Text("Количество");
        HBox headMainHBox = new HBox();
        VBox headTextVBox = new VBox();
        Label logoLabel = new Label();
        Label headSecondLabel = new Label("Заказчик: ");
        Label headSecondClientLabel = new Label(MainInterface.getClient(order.get_client()).get_name());
        Text manager = new Text("Менеджер: " + MainInterface.getStaff(order.get_manager())+"\n\n");
        Text signature = new Text("Получатель: ________________________\n\n" +
                "    Подпись: ________________________");
        StackPane logoStackPane = new StackPane();
        StackPane bottom1StackPane = new StackPane();
        StackPane bottom2StackPane = new StackPane();
        StackPane stackPane = new StackPane();
        VBox topVBox = new VBox();
        HBox bottomHBox = new HBox();
        HBox headSecondHBox = new HBox();
        HBox mainDateHBox = new HBox();
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Printer printer = printerJob.getPrinter();
        JobSettings jobSettings = printerJob.getJobSettings();
        PageLayout pageLayout = jobSettings.getPageLayout();

        pageLayout = printer.createPageLayout(Paper.A5, PageOrientation.LANDSCAPE, Printer.MarginType.EQUAL);

        printBorderPane.setPrefWidth(pageLayout.getPrintableWidth()-20);
        printBorderPane.setPrefHeight(pageLayout.getPrintableHeight()-20);
        printBorderPane.setMinHeight(pageLayout.getPrintableHeight()-20);
        printBorderPane.setMaxHeight(pageLayout.getPrintableHeight()-20);

        ColumnConstraints numberCol = new ColumnConstraints();
        numberCol.setPercentWidth(4);
        ColumnConstraints descriptionCol = new ColumnConstraints();
        descriptionCol.setPercentWidth(81);
        ColumnConstraints quantityCol = new ColumnConstraints();
        quantityCol.setPercentWidth(15);
        RowConstraints rowHead = new RowConstraints(20);

        printGridPane.setGridLinesVisible(true);
        printGridPane.alignmentProperty().set(Pos.TOP_CENTER);
        printGridPane.setPrefWidth(printBorderPane.getPrefWidth()-20);
        printGridPane.setMinWidth(printBorderPane.getPrefWidth()-20);
        printGridPane.setMaxWidth(printBorderPane.getPrefWidth()-20);
        printGridPane.getColumnConstraints().addAll(numberCol, descriptionCol, quantityCol);
        printGridPane.getRowConstraints().addAll(rowHead);
        printGridPane.add(numberText, 0,0);
        printGridPane.add(descriptionText, 1,0);
        printGridPane.add(quantityText, 2,0);
        GridPane.setHalignment(numberText, HPos.CENTER);
        GridPane.setHalignment(quantityText, HPos.CENTER);
        GridPane.setValignment(numberText, VPos.CENTER);
        GridPane.setValignment(descriptionText, VPos.CENTER);
        GridPane.setValignment(quantityText, VPos.CENTER);
        GridPane.setMargin(quantityText, new Insets(2,0,2,2));
        GridPane.setMargin(descriptionText, new Insets(2,0,2,5));
        GridPane.setMargin(numberText, new Insets(2));

        for(int i = 0; i<_positionsToPrintTableView.getItems().size(); ++i)
        {
            String[] s = _positionsToPrintTableView.getItems().get(i).get_description().split("\n");
            VBox vBoxDescription = new VBox();
            RowConstraints row = new RowConstraints();
            Label number = new Label(String.valueOf(i+1));
            Text quantity = new Text(_positionsToPrintTableView.getItems().get(i).get_quantity());

            printGridPane.getRowConstraints().add(row);

            number.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
            number.setAlignment(Pos.TOP_CENTER);
            number.setPadding(new Insets(2,1,2,2));

            for (String value : s) {
                Label label = new Label();
                label.setText(value);
                label.setWrapText(true);
                label.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
                vBoxDescription.getChildren().addAll(label);
            }

            quantity.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 12));
            
            printGridPane.add(number, 0, i+1);
            printGridPane.add(vBoxDescription, 1,i+1 );
            printGridPane.add(quantity, 2, i+1);
            GridPane.setHalignment(number, HPos.CENTER);
            GridPane.setValignment(number, VPos.TOP);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setValignment(quantity, VPos.CENTER);
            GridPane.setMargin(vBoxDescription, new Insets(2,1,2,5));
        }

        Label headMain = new Label("Накладная на отпуск продукции № "+
                String.valueOf(DataBase.getReceiptCount()));
        headMain.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        headMain.setPrefWidth(pageLayout.getPrintableWidth()/1.5);
        Label headMainDate1 = new Label("от ");
        headMainDate1.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        Label headMainDate2 = new Label(order.get_date().toLocalDate().format(MainInterface._formatter));
        headMainDate2.setFont(Font.font("Calibri",FontWeight.NORMAL, FontPosture.ITALIC, 18));

        logoLabel.setGraphic(getLogo());

        logoStackPane.setPrefWidth(pageLayout.getPrintableWidth()-headMain.getPrefWidth());
        logoStackPane.setMinWidth(pageLayout.getPrintableWidth()-headMain.getPrefWidth());
        logoStackPane.setMaxWidth(pageLayout.getPrintableWidth()-headMain.getPrefWidth());
        logoStackPane.setAlignment(Pos.TOP_RIGHT);
        logoStackPane.getChildren().addAll(logoLabel);

        headSecondLabel.setAlignment(Pos.BOTTOM_LEFT);
        headSecondLabel.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 16));
        headSecondClientLabel.setAlignment(Pos.BOTTOM_LEFT);
        headSecondClientLabel.setFont(Font.font("Calibri",FontWeight.NORMAL, FontPosture.ITALIC, 12));

        manager.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
        signature.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));

        bottom1StackPane.getChildren().addAll(manager);
        bottom1StackPane.setAlignment(Pos.BOTTOM_LEFT);
        bottom1StackPane.setPrefWidth(printBorderPane.getPrefWidth()/2-20);
        bottom1StackPane.setMinWidth(printBorderPane.getPrefWidth()/2-20);
        bottom1StackPane.setMaxWidth(printBorderPane.getPrefWidth()/2-20);
        bottom2StackPane.getChildren().addAll(signature);
        bottom2StackPane.setAlignment(Pos.BOTTOM_RIGHT);
        bottom2StackPane.setPrefWidth(printBorderPane.getPrefWidth()/2-20);
        bottom2StackPane.setMinWidth(printBorderPane.getPrefWidth()/2-20);
        bottom2StackPane.setMaxWidth(printBorderPane.getPrefWidth()/2-20);

        bottomHBox.setPadding(new Insets(20));
        bottomHBox.setPrefWidth(printBorderPane.getPrefWidth());
        bottomHBox.getChildren().addAll(bottom1StackPane, bottom2StackPane);

        headSecondHBox.setAlignment(Pos.BOTTOM_LEFT);
        headSecondHBox.getChildren().addAll(headSecondLabel, headSecondClientLabel);

        headTextVBox.getChildren().addAll(headMain, mainDateHBox);

        headMainHBox.getChildren().addAll(headTextVBox, logoStackPane);

        topVBox.setPadding(new Insets(20));
        topVBox.getChildren().addAll(headMainHBox, headSecondHBox);

        printBorderPane.setTop(topVBox);
        printBorderPane.setCenter(printGridPane);
        printBorderPane.setBottom(bottomHBox);

        stackPane.setPrefWidth(pageLayout.getPrintableWidth());
        stackPane.setPrefHeight(pageLayout.getPrintableHeight()/2);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().addAll(printBorderPane);

        double scaleX = pageLayout.getPrintableWidth();
        double scaleY = pageLayout.getPrintableHeight();

        jobSettings.setPageLayout(pageLayout);

        if(printerJob == null){
            return;
        }

        boolean proceed = printerJob.showPrintDialog(_printStage);
        //boolean pageSetup = printerJob.showPageSetupDialog(stagePrint);

        if(proceed){
            boolean printed = printerJob.printPage(stackPane);
            if(printed){
                printerJob.endJob();
                DataBase.setReceiptCount();
                _printStage.close();
            }
        }
    }

    static ImageView getLogo(){
        Image logo = null;
        try {
            FileInputStream fs = new FileInputStream(DataBase.path + "\\src\\images\\logoPrnt.jpg");
            logo = new Image(fs);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ImageView imageView = new ImageView(logo);
        imageView.setFitHeight(37);
        imageView.setFitWidth(190);

        return imageView;
    }

}