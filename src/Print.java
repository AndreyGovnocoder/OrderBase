import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
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
    static double A4_WIDTH = 560;
    static double A4_HALF_HEIGHT = 375;
    static TableView<OrderPosition> _positionsToPrintTableView;
    static TableView<OrderPosition> _orderPositionsTableView;
    static Stage _printStage;
    static Scene _printScene;
    static private boolean isPrint = false;
    static private String _manager = "";
    static private String _client = "";
    static private String _date = "";
    static private RadioButton formatA4RBtn = new RadioButton("A4");
    static private RadioButton formatA5RBtn = new RadioButton("A5");
    static private TextField nameClientTextField = new TextField();

    static boolean toPrint(Order order, Stage primaryStage)
    {
        _printStage = new Stage();
        BorderPane printBorderPane = new BorderPane();
        _printScene = new Scene(printBorderPane, 600, 600);
        _positionsToPrintTableView = new TableView<>();
        _orderPositionsTableView = new TableView<>();
        VBox centerVBox = new VBox();
        //HBox buttonsHBox = new HBox();
        AnchorPane buttonAnchorPane = new AnchorPane();
        VBox buttonsVBox = new VBox();
        TitledPane orderTitledPane = new TitledPane();
        GridPane orderGridPane = new GridPane();
        TitledPane toPrintTitledPane = new TitledPane();
        TitledPane paperFormatTitiledPane  = new TitledPane();
        HBox paperFormatHBox = new HBox();
        Label headDateLabel = new Label("Дата: ");
        Label headClientLabel = new Label("Заказчик: ");
        Label headManagerLabel = new Label("Менеджер: ");
        Label headGetDateLabel = new Label(order.get_date().toLocalDate().format(MainInterface._formatter));
        Label headGetClientLabel = new Label(Finder.getClient(order.get_client()).get_name());
        Label headGetManagerLabel = new Label(Finder.getStaff(order.get_manager()).get_name());
        Button toPrintButton = new Button("Распечатать накладную");
        Button cancelButton = new Button("Отмена");

        for(OrderPosition position : Finder.getOrdersPositionsList(order.get_id()))
            position.set_toPrint(false);

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

        orderGridPane.setHgap(10);
        orderGridPane.add(headDateLabel, 0,0);
        orderGridPane.add(headClientLabel, 0,1);
        if (Finder.getClient(order.get_client()).get_name().toLowerCase().equals("частное лицо"))
            orderGridPane.add(nameClientTextField, 2, 1);
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
        quantityCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.14));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrderPosition, String> issueCol = new TableColumn<>("Выдача");
        issueCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.13));
        issueCol.setCellValueFactory(new PropertyValueFactory<>("_issue"));
        issueCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrderPosition, Boolean> printCol = new TableColumn<>("Печать");
        printCol.prefWidthProperty().bind(_orderPositionsTableView.widthProperty().multiply(0.09));
        printCol.setCellValueFactory(param ->
        {
            OrderPosition position = param.getValue();

            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(position.get_toPrint());

            // Note: singleCol.setOnEditCommit(): Not work for
            // CheckBoxTableCell.

            // When "Single?" column change.
            booleanProp.addListener((observable, oldValue, newValue) ->
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
            });
            return booleanProp;
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

        _orderPositionsTableView.setItems(FXCollections.observableArrayList(Finder.getOrdersPositionsList(order.get_id())));
        _orderPositionsTableView.getColumns().addAll(descriptionCol, quantityCol, issueCol, printCol);

        for(OrderPosition position : Finder.getOrdersPositionsList(order.get_id()))
        {
            System.out.println(position.get_description() + " : " + position.get_toPrint());
        }

        TableColumn<OrderPosition, String> descriptionPrintCol = new TableColumn<>("Наименование работ, услуг");
        descriptionPrintCol.prefWidthProperty().bind(_positionsToPrintTableView.widthProperty().multiply(0.78));
        descriptionPrintCol.setCellValueFactory(new PropertyValueFactory<>("_description"));
        descriptionPrintCol.setCellFactory(tc ->
        {
            TableCell<OrderPosition, String> cell = new TableCell<>();
            Text text = new Text();
            cell.setGraphic(text);
            cell.setPrefHeight(Control.USE_COMPUTED_SIZE);
            text.wrappingWidthProperty().bind(descriptionPrintCol.widthProperty());
            text.textProperty().bind(cell.itemProperty());
            return cell ;
        });
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


        ToggleGroup formatRBtnsGroup = new ToggleGroup();
        formatA4RBtn.setToggleGroup(formatRBtnsGroup);
        formatA5RBtn.setToggleGroup(formatRBtnsGroup);
        formatA5RBtn.setSelected(!Finder.isFormatA4());
        formatA4RBtn.setSelected(Finder.isFormatA4());

        formatA4RBtn.setOnAction(event -> Finder.setFormat(Finder.A4, formatA4RBtn.isSelected()));
        formatA5RBtn.setOnAction(event -> Finder.setFormat(Finder.A5, formatA5RBtn.isSelected()));

        paperFormatHBox.setSpacing(20);
        //paperFormatHBox.getChildren().addAll(formatA4RBtn, formatA5RBtn);
        paperFormatHBox.getChildren().addAll(formatA4RBtn);

        //paperFormatTitiledPane.setPadding(new Insets(10));
        paperFormatTitiledPane.setText("Используемый формат бумаги");
        paperFormatTitiledPane.setContent(paperFormatHBox);
        paperFormatTitiledPane.setExpanded(true);
        paperFormatTitiledPane.setCollapsible(false);

        centerVBox.setSpacing(10);
        centerVBox.setPadding(new Insets(15));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(
                orderTitledPane,
                _orderPositionsTableView,
                new Separator(),
                paperFormatTitiledPane,
                toPrintTitledPane);

        buttonAnchorPane.getChildren().addAll(toPrintButton, cancelButton);
        AnchorPane.setTopAnchor(cancelButton, 5.0);
        AnchorPane.setRightAnchor(cancelButton, 5.0);
        AnchorPane.setBottomAnchor(cancelButton, 5.0);
        AnchorPane.setTopAnchor(toPrintButton, 5.0);
        AnchorPane.setLeftAnchor(toPrintButton, 5.0);
        AnchorPane.setBottomAnchor(toPrintButton, 5.0);

        buttonsVBox.getChildren().addAll(new Separator(), buttonAnchorPane);

        printBorderPane.setCenter(centerVBox);
        printBorderPane.setBottom(buttonsVBox);

        _printStage.setTitle("Печать накладной");
        _printStage.getIcons().add(MainInterface.getIconLogo());
        _printStage.initModality(Modality.WINDOW_MODAL);
        _printStage.initOwner(primaryStage);
        _printStage.setScene(_printScene);
        _printStage.showAndWait();
        return isPrint;
    }

    private static void print(Order order)
    {

        String positionsString = "";
        for(OrderPosition position : _positionsToPrintTableView.getItems())
            positionsString += position.get_description() + "\n";


        _manager = "";
        _client = "";
        _date = "";

        _manager = Finder.getStaff(order.get_manager()).get_name();
        _client = Finder.getClient(order.get_client()).get_name();
        if (Finder.getClient(order.get_client()).get_name().toLowerCase().equals("частное лицо") && !nameClientTextField.getText().isEmpty())
            _client += ": " + nameClientTextField.getText();
        _date = order.get_date().toLocalDate().format(MainInterface._formatter);

        BorderPane printBorderPane = new BorderPane();
        GridPane printGridPane = new GridPane();
        Text numberText = new Text("№");
        Text descriptionText = new Text("Наименование работ, услуг");
        Text quantityText = new Text("Количество");
        HBox headMainHBox = new HBox();
        VBox headTextVBox = new VBox();
        Label logoLabel = new Label();
        Label headSecondLabel = new Label("Заказчик: ");
        Label headSecondClientLabel = new Label(_client);
        Text manager = new Text("Менеджер: " + _manager + "\n\n");
        Text signature = new Text("Получатель: ________________________\n\n" +
                "    Подпись: _________________________");
        StackPane logoStackPane = new StackPane();
        StackPane bottom1StackPane = new StackPane();
        StackPane bottom2StackPane = new StackPane();
        //StackPane stackPane = new StackPane();
        VBox topVBox = new VBox();
        HBox bottomHBox = new HBox();
        HBox headSecondHBox = new HBox();
        HBox mainDateHBox = new HBox();

        VBox printVBox = new VBox();
        printVBox.setAlignment(Pos.TOP_LEFT);
        //printVBox.setStyle("-fx-border-color: yellow");

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Printer printer = printerJob.getPrinter();
        JobSettings jobSettings = printerJob.getJobSettings();
        PageLayout pageLayout;

        if (Finder._isFormatA4)
        {
            pageLayout = printer.createPageLayout(
                    Paper.A4,
                    PageOrientation.PORTRAIT,
                    0,0,0,0);
            //printBorderPane.setPrefWidth(pageLayout.getPrintableWidth()-10);
            //printBorderPane.setPrefHeight(pageLayout.getPrintableHeight()-10);
            //printBorderPane.setMinHeight(pageLayout.getPrintableHeight()/2 - 5);
            //printBorderPane.setMaxHeight(pageLayout.getPrintableHeight()/2 - 5);

        }
        else
        {

            pageLayout = printerJob.getPrinter().createPageLayout(
                    Paper.A5,
                    PageOrientation.LANDSCAPE,
                    0,0,0,0);
            //printBorderPane.setPrefSize(796.70020500003, 561.48395400002);
            /*
            printBorderPane.setPrefWidth(pageLayout.getPrintableWidth()-10);
            printBorderPane.setPrefHeight(pageLayout.getPrintableHeight()-10);
            printBorderPane.setMinHeight(pageLayout.getPrintableHeight() - 10);
            printBorderPane.setMaxHeight(pageLayout.getPrintableHeight() - 10);
            printBorderPane.setMinWidth(pageLayout.getPrintableHeight() - 10);
            printBorderPane.setMaxWidth(pageLayout.getPrintableHeight() - 10);
*/

        }
        //printBorderPane.setStyle("-fx-border-color: red");
        if (Finder.isFormatA5())
        {
            //stackPane.setMinSize(797, 561);
            //printBorderPane.setMinSize(797, 561);
            System.out.println("margins: " +
                    "top: " + pageLayout.getTopMargin() +
                    "left: " + pageLayout.getLeftMargin());
        }
        else
        {
            //printBorderPane.setPrefWidth(pageLayout.getPrintableWidth()-100);
            printVBox.setMaxWidth(A4_WIDTH);
            printVBox.setMinWidth(A4_WIDTH);
            printVBox.setPrefWidth(A4_WIDTH);
            printVBox.setMaxHeight(A4_HALF_HEIGHT);
            printVBox.setMinHeight(A4_HALF_HEIGHT);
            printVBox.setPrefHeight(A4_HALF_HEIGHT);
            printBorderPane.setMaxWidth(A4_WIDTH);
            printBorderPane.setMinWidth(A4_WIDTH);
            printBorderPane.setPrefWidth(A4_WIDTH);
            printBorderPane.setMaxHeight(A4_HALF_HEIGHT);
            printBorderPane.setMinHeight(A4_HALF_HEIGHT);
            printBorderPane.setPrefHeight(A4_HALF_HEIGHT);
        }



        ColumnConstraints numberCol = new ColumnConstraints();
        numberCol.setPercentWidth(4);
        ColumnConstraints descriptionCol = new ColumnConstraints();
        descriptionCol.setPercentWidth(81);
        ColumnConstraints quantityCol = new ColumnConstraints();
        quantityCol.setPercentWidth(15);
        RowConstraints rowHead = new RowConstraints(20);

        printGridPane.setGridLinesVisible(true);
        printGridPane.alignmentProperty().set(Pos.TOP_CENTER);

        printGridPane.setPadding(new Insets(10, 10,0,10));

        //printGridPane.setPrefWidth(pageLayout.getPrintableWidth());
        //printGridPane.setMinWidth(pageLayout.getPrintableWidth());
        //printGridPane.setMaxWidth(pageLayout.getPrintableWidth());
        //printGridPane.setStyle("-fx-border-color: blue");

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
                String.valueOf(DataBase.getLastId(DataBase.RECEIPTS_TABLE)+1));
        headMain.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        headMain.setPrefWidth(pageLayout.getPrintableWidth()/1.5);
        Label headMainDate1 = new Label("от ");
        headMainDate1.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        Label headMainDate2 = new Label(_date);
        headMainDate2.setFont(Font.font("Calibri",FontWeight.NORMAL, FontPosture.ITALIC, 18));

        mainDateHBox.getChildren().addAll( headMainDate1, headMainDate2);

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
        bottom1StackPane.setPrefWidth(printBorderPane.getPrefWidth()/2);
        bottom1StackPane.setMinWidth(printBorderPane.getPrefWidth()/2);
        bottom1StackPane.setMaxWidth(printBorderPane.getPrefWidth()/2);
        bottom2StackPane.getChildren().addAll(signature);
        bottom2StackPane.setAlignment(Pos.BOTTOM_RIGHT);
        bottom2StackPane.setPrefWidth(printBorderPane.getPrefWidth()/2);
        bottom2StackPane.setMinWidth(printBorderPane.getPrefWidth()/2);
        bottom2StackPane.setMaxWidth(printBorderPane.getPrefWidth()/2);

        bottomHBox.setPadding(new Insets(10,0,10,0));
        bottomHBox.setPrefWidth(printBorderPane.getPrefWidth());
        bottomHBox.getChildren().addAll(bottom1StackPane, bottom2StackPane);

        headSecondHBox.setAlignment(Pos.BOTTOM_LEFT);
        headSecondHBox.getChildren().addAll(headSecondLabel, headSecondClientLabel);

        headTextVBox.getChildren().addAll(headMain, mainDateHBox);

        headMainHBox.getChildren().addAll(headTextVBox, logoStackPane);

        topVBox.setPadding(new Insets(0,0,0,0));
        topVBox.getChildren().addAll(headMainHBox, headSecondHBox);

        printBorderPane.setTop(topVBox);
        printBorderPane.setCenter(printGridPane);
        printBorderPane.setBottom(bottomHBox);
        BorderPane.setAlignment(printGridPane, Pos.TOP_LEFT);


        //stackPane.setAlignment(Pos.TOP_LEFT);
        //stackPane.getChildren().addAll(printBorderPane);
        //stackPane.setStyle("-fx-border-color: yellow");

        double scaleX = pageLayout.getPrintableWidth();
        double scaleY = pageLayout.getPrintableHeight();
        System.out.println("width " + printBorderPane.getWidth());
        System.out.println("height " + printBorderPane.getHeight());
        System.out.println("a5rButt: " + formatA5RBtn.isDisable());

        jobSettings.setPageLayout(pageLayout);

        if(printerJob == null)
            return;

        boolean proceed = printerJob.showPrintDialog(_printStage);
        //boolean pageSetup = printerJob.showPageSetupDialog(_printStage);
        printVBox.getChildren().addAll(printBorderPane);
        if(proceed)
        {
            System.out.println("proceed");
            boolean printed = printerJob.printPage(printVBox);
            if(printed)
            {
                System.out.println("printed");
                printerJob.endJob();
                System.out.println("endJob");

                if(DataBase.setReceiptCount() && DataBase.setReceiptPrint(order.get_id()))
                {
                    isPrint = true;
                    Receipt receipt = new Receipt();
                    receipt.set_orderId(order.get_id());
                    receipt.set_positions(getPositionsToPrint());
                    receipt.set_manager(_manager);
                    receipt.set_date(_date);
                    receipt.set_client(_client);
                    if(DataBase.addReceipt(receipt))
                    {
                        receipt.set_id(DataBase.getLastId(DataBase.RECEIPTS_TABLE));
                        System.out.println("квитанция добавлена в бд");
                        Finder.get_allReceipts().add(receipt);
                    }
                }

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

    private static String getPositionsToPrint()
    {
        StringBuilder positionsString = new StringBuilder();

        for (OrderPosition position : _positionsToPrintTableView.getItems())
        {
            positionsString.append(String.valueOf(position.get_id())).append("^");
        }

        return positionsString.toString();
    }
}