import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.control.Button;
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


public class Print {
    static ListView<OrdersPosition> listView = new ListView<>();
    static TableView<OrdersPosition> tableViewPrintPositions = new TableView<>();
    static ObservableList<OrdersPosition> addedOrderPositions = FXCollections.observableArrayList();
    static TableView<OrdersPosition> tableViewOrdersPosition = new TableView<>();
    static Stage stagePrint;
    static Scene scenePrint;


    static void toPrint(Order order){
        addedOrderPositions.clear();
        tableViewOrdersPosition.getItems().clear();
        tableViewOrdersPosition.getColumns().clear();
        listView.getItems().clear();
        tableViewPrintPositions.getItems().clear();
        tableViewPrintPositions.getColumns().clear();

        stagePrint = new Stage();
        stagePrint.setTitle("Печать накладной");
        stagePrint.initModality(Modality.WINDOW_MODAL);
        stagePrint.initOwner(MainInterface.getMainStage());
        BorderPane borderPanePrint = new BorderPane();

        scenePrint = new Scene(borderPanePrint, 600,600);
        stagePrint.setScene(scenePrint);

        VBox vBoxOrder = new VBox();
        vBoxOrder.setSpacing(10);
        vBoxOrder.setPadding(new Insets(15));

        TitledPane titledPaneOrder = new TitledPane();
        titledPaneOrder.setText("Заказ");
        titledPaneOrder.setCollapsible(false);
        titledPaneOrder.setExpanded(true);

        GridPane gridPaneOrder = new GridPane();
        Label labelHeadDate = new Label("Дата: ");
        Label labelHeadClient = new Label("Заказчик: ");
        Label labelHeadManager = new Label("Менеджер: ");
        Label labelHeadGetDate = new Label(order.getDate().toLocalDate().format(CreateOrder.formatter));
        labelHeadGetDate.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));
        Label labelHeadGetClient = new Label(order.getClient().getClient());
        labelHeadGetClient.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));
        Label labelHeadGetManager = new Label(order.getManager());
        labelHeadGetManager.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 14));
        /*
        gridPaneOrder.add(new Text("Дата: "), 0,0);
        gridPaneOrder.add(new Text("Заказчик: "), 0,1);
        gridPaneOrder.add(new Text("Менеджер: "),0,2);
        gridPaneOrder.add(new Text(order.getDate().toLocalDate().format(CreateOrder.formatter)),1,0);
        gridPaneOrder.add(new Text(order.getClient().getClient()), 1,1);
        gridPaneOrder.add(new Text(order.getManager()),1,2);
        */
        gridPaneOrder.add(labelHeadDate, 0,0);
        gridPaneOrder.add(labelHeadClient, 0,1);
        gridPaneOrder.add(labelHeadManager,0,2);
        gridPaneOrder.add(labelHeadGetDate,1,0);
        gridPaneOrder.add(labelHeadGetClient, 1,1);
        gridPaneOrder.add(labelHeadGetManager,1,2);

        GridPane.setHalignment(labelHeadDate, HPos.RIGHT);
        GridPane.setHalignment(labelHeadClient, HPos.RIGHT);
        GridPane.setHalignment(labelHeadManager, HPos.RIGHT);
        titledPaneOrder.setContent(gridPaneOrder);


        tableViewOrdersPosition.setEditable(true);
        TableColumn<OrdersPosition, String> descriptionCol = new TableColumn<>("Позиция");
        descriptionCol.prefWidthProperty().bind(tableViewOrdersPosition.widthProperty().multiply(0.61));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<OrdersPosition, String> quantityCol = new TableColumn<>("Количество");
        quantityCol.prefWidthProperty().bind(tableViewOrdersPosition.widthProperty().multiply(0.14));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrdersPosition, String> issueCol = new TableColumn<>("Выдача");
        issueCol.prefWidthProperty().bind(tableViewOrdersPosition.widthProperty().multiply(0.13));
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        issueCol.setStyle("-fx-alignment: CENTER;");
        TableColumn<OrdersPosition, Boolean> printCol = new TableColumn<>("Печать");
        printCol.prefWidthProperty().bind(tableViewOrdersPosition.widthProperty().multiply(0.09));
        printCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OrdersPosition, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<OrdersPosition, Boolean> param) {
                OrdersPosition ordersPosition = param.getValue();

                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(ordersPosition.isToPrint());

                // Note: singleCol.setOnEditCommit(): Not work for
                // CheckBoxTableCell.

                // When "Single?" column change.
                booleanProp.addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                                        Boolean newValue) {
                        ordersPosition.setToPrint(newValue);
                        if(newValue){
                            addedOrderPositions.add(ordersPosition);
                        } else addedOrderPositions.remove(ordersPosition);
                        refreshAddedPositions();
                    }
                });
                return booleanProp;
            }
        });
        printCol.setCellFactory(new Callback<TableColumn<OrdersPosition, Boolean>, //
                TableCell<OrdersPosition, Boolean>>() {
            @Override
            public TableCell<OrdersPosition, Boolean> call(TableColumn<OrdersPosition, Boolean> p) {
                CheckBoxTableCell<OrdersPosition, Boolean> cell = new CheckBoxTableCell<OrdersPosition, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        ObservableList<OrdersPosition> positions = FXCollections.observableArrayList(DataBaseHelper.getPositionsFromOrder(order));
        tableViewOrdersPosition.setItems(positions);
        tableViewOrdersPosition.getColumns().addAll(descriptionCol, quantityCol, issueCol, printCol);


        HBox hBoxButtons = new HBox();
        hBoxButtons.setSpacing(10);
        hBoxButtons.setPadding(new Insets(15));
        Button buttonToPrint = new Button("Распечатать накладную");
        buttonToPrint.setOnAction(event -> {
            if(addedOrderPositions.isEmpty()){
                MainInterface.getAlertWarningDialog("Список позиций пуст");
            } else {
                print(order);
            }
        });
        buttonToPrint.setPrefSize(160, 50);
        Button buttonCancel = new Button("Отмена");
        buttonCancel.setPrefSize(80,50);
        buttonCancel.setOnAction(event -> stagePrint.close());
        hBoxButtons.getChildren().addAll(buttonToPrint, buttonCancel);

        TableColumn<OrdersPosition, String> descriptionPrintCol = new TableColumn<>("Наименование работ, услуг");
        descriptionPrintCol.prefWidthProperty().bind(tableViewPrintPositions.widthProperty().multiply(0.78));
        descriptionPrintCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        //descriptionPrintCol.setPrefWidth(scenePrint.getWidth()/1.35);
        TableColumn<OrdersPosition, String> quantityPrintCol = new TableColumn<>("Количество");
        quantityPrintCol.prefWidthProperty().bind(tableViewPrintPositions.widthProperty().multiply(0.14));
        //quantityPrintCol.setPrefWidth(80);
        quantityPrintCol.setStyle("-fx-alignment: CENTER;");
        quantityPrintCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<OrdersPosition, Integer> numberPrintCol = new TableColumn<>("№");
        numberPrintCol.prefWidthProperty().bind(tableViewPrintPositions.widthProperty().multiply(0.05));
        numberPrintCol.setStyle("-fx-alignment: CENTER;");
        //numberPrintCol.setPrefWidth(30);
        numberPrintCol.setCellValueFactory(new PropertyValueFactory<>("number"));


        tableViewPrintPositions.setPlaceholder(new Text("Список позиций пуст"));
        tableViewPrintPositions.setEditable(false);
        tableViewPrintPositions.getColumns().addAll(numberPrintCol, descriptionPrintCol, quantityPrintCol);

        refreshAddedPositions();

        TitledPane titledPaneToPrint = new TitledPane();
        titledPaneToPrint.setPadding(new Insets(10));
        titledPaneToPrint.setText("Печать");
        titledPaneToPrint.setContent(tableViewPrintPositions);
        titledPaneToPrint.setExpanded(true);
        titledPaneToPrint.setCollapsible(false);

        vBoxOrder.getChildren().addAll(titledPaneOrder, tableViewOrdersPosition, new Separator(), titledPaneToPrint);
        borderPanePrint.setCenter(vBoxOrder);
        borderPanePrint.setBottom(hBoxButtons);

        stagePrint.show();
    }

    static void refreshAddedPositions(){
        //listView.setItems(addedOrderPositions);
        int i = 1;
        for(OrdersPosition o:addedOrderPositions){
            o.setNumber(i);
            i++;
        }
        tableViewPrintPositions.setItems(addedOrderPositions);
    }

    static void print(Order order){

        String stringPositions = "";
        for(OrdersPosition ordersPosition: addedOrderPositions){
            stringPositions = stringPositions + ordersPosition.getDescription() +"\n";
        }

        HBox hBox = new HBox();
        hBox.setPadding(new Insets(20));
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Printer printer = printerJob.getPrinter();
        JobSettings jobSettings = printerJob.getJobSettings();
        PageLayout pageLayout = jobSettings.getPageLayout();
        pageLayout = printer.createPageLayout(Paper.A5, PageOrientation.LANDSCAPE, Printer.MarginType.EQUAL);

        BorderPane borderPanePrint = new BorderPane();
        borderPanePrint.setPrefWidth(pageLayout.getPrintableWidth()-20);
        borderPanePrint.setPrefHeight(pageLayout.getPrintableHeight()-20);
        borderPanePrint.setMinHeight(pageLayout.getPrintableHeight()-20);
        borderPanePrint.setMaxHeight(pageLayout.getPrintableHeight()-20);
        GridPane gridPanePrint = new GridPane();
        gridPanePrint.alignmentProperty().set(Pos.TOP_CENTER);
        gridPanePrint.setPrefWidth(borderPanePrint.getPrefWidth()-20);
        gridPanePrint.setMinWidth(borderPanePrint.getPrefWidth()-20);
        gridPanePrint.setMaxWidth(borderPanePrint.getPrefWidth()-20);

        ColumnConstraints numberCol = new ColumnConstraints();
        numberCol.setPercentWidth(4);
        ColumnConstraints descriptionCol = new ColumnConstraints();
        descriptionCol.setPercentWidth(81);
        ColumnConstraints quantityCol = new ColumnConstraints();
        quantityCol.setPercentWidth(15);
        RowConstraints rowHead = new RowConstraints(20);
        gridPanePrint.getColumnConstraints().addAll(numberCol, descriptionCol, quantityCol);
        gridPanePrint.getRowConstraints().addAll(rowHead);
        Text textNumber = new Text("№");
        Text textDescription = new Text("Наименование работ, услуг");
        Text textQuantity = new Text("Количество");

        gridPanePrint.add(textNumber, 0,0);
        gridPanePrint.add(textDescription, 1,0);
        gridPanePrint.add(textQuantity, 2,0);

        GridPane.setHalignment(textNumber, HPos.CENTER);
        GridPane.setHalignment(textQuantity, HPos.CENTER);
        GridPane.setValignment(textNumber, VPos.CENTER);
        GridPane.setValignment(textDescription, VPos.CENTER);
        GridPane.setValignment(textQuantity, VPos.CENTER);

        GridPane.setMargin(textQuantity, new Insets(2,0,2,2));
        GridPane.setMargin(textDescription, new Insets(2,0,2,5));
        GridPane.setMargin(textNumber, new Insets(2));

        for(int i = 0; i<addedOrderPositions.size(); i++){
            String[] s = addedOrderPositions.get(i).getDescription().split("\n");
            VBox vBoxDescription = new VBox();
            RowConstraints row = new RowConstraints();
            gridPanePrint.getRowConstraints().add(row);
            Label number = new Label(String.valueOf(addedOrderPositions.get(i).getNumber()));
            number.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
            number.setAlignment(Pos.TOP_CENTER);
            number.setPadding(new Insets(2,1,2,2));
            for(int n=0;n<s.length;n++){
                Label label = new Label();
                label.setText(s[n]);
                label.setWrapText(true);
                label.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
                vBoxDescription.getChildren().addAll(label);
            }
            /*
            //Label description = new Label(addedOrderPositions.get(i).getDescription());
            TextField description = new TextField();
            description.setEditable(false);
            description.setText(addedOrderPositions.get(i).getDescription());
            description.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
            //description.setWrapText(true);
            description.setPadding(new Insets(2,1,2,5));
            //description.setAlignment(Pos.CENTER_LEFT);
            //description.setPrefHeight(17*s.length);
             */

            Text quantity = new Text(addedOrderPositions.get(i).getQuantity());
            quantity.setFont(Font.font("Calibri", FontWeight.SEMI_BOLD, FontPosture.REGULAR, 12));
            gridPanePrint.add(number, 0, i+1);
            gridPanePrint.add(vBoxDescription, 1,i+1 );
            gridPanePrint.add(quantity, 2, i+1);
            GridPane.setHalignment(number, HPos.CENTER);
            GridPane.setValignment(number, VPos.TOP);
            GridPane.setHalignment(quantity, HPos.CENTER);
            GridPane.setValignment(quantity, VPos.CENTER);
            GridPane.setMargin(vBoxDescription, new Insets(2,1,2,5));
        }

        gridPanePrint.setGridLinesVisible(true);
        //borderPanePrint.setStyle("-fx-border-color: black");

        Label headMain = new Label("Накладная на отпуск продукции № "+
                String.valueOf(DataBaseHelper.getCount()));
        headMain.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        headMain.setPrefWidth(pageLayout.getPrintableWidth()/1.5);
        Label headMainDate1 = new Label("от ");
        headMainDate1.setFont(Font.font("Calibri", FontWeight.BOLD, FontPosture.REGULAR, 18));
        Label headMainDate2 = new Label(order.getDate().toLocalDate().format(CreateOrder.formatter));
        headMainDate2.setFont(Font.font("Calibri",FontWeight.NORMAL, FontPosture.ITALIC, 18));
        HBox hBoxMainDate = new HBox();
        hBoxMainDate.getChildren().addAll(headMainDate1, headMainDate2);

        HBox hBoxHeadMain = new HBox();
        VBox vBoxHeadText = new VBox();
        vBoxHeadText.getChildren().addAll(headMain, hBoxMainDate);

        Label labelLogo = new Label();
        labelLogo.setGraphic(getLogo());
        StackPane stackPaneLogo = new StackPane();
        stackPaneLogo.getChildren().addAll(labelLogo);
        stackPaneLogo.setAlignment(Pos.TOP_RIGHT);

        hBoxHeadMain.getChildren().addAll(vBoxHeadText, stackPaneLogo);

        stackPaneLogo.setPrefWidth(pageLayout.getPrintableWidth()-headMain.getPrefWidth());
        stackPaneLogo.setMinWidth(pageLayout.getPrintableWidth()-headMain.getPrefWidth());
        stackPaneLogo.setMaxWidth(pageLayout.getPrintableWidth()-headMain.getPrefWidth());

        Label headSecond = new Label("Заказчик: ");
        headSecond.setAlignment(Pos.BOTTOM_LEFT);
        headSecond.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 16));
        Label headSecondClient = new Label(order.getClient().getClient());
        headSecondClient.setAlignment(Pos.BOTTOM_LEFT);
        headSecondClient.setFont(Font.font("Calibri",FontWeight.NORMAL, FontPosture.ITALIC, 12));

        HBox hBoxheadSecond = new HBox();
        hBoxheadSecond.setAlignment(Pos.BOTTOM_LEFT);
        hBoxheadSecond.getChildren().addAll(headSecond, headSecondClient);

        Text manager = new Text("Менеджер: "+order.getManager()+"\n\n");
        manager.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));
        Text signature = new Text("Получатель: ________________________\n\n" +
                                  "    Подпись: ________________________");
        signature.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 12));

        StackPane stackPaneBottom1 = new StackPane();
        stackPaneBottom1.getChildren().addAll(manager);
        stackPaneBottom1.setAlignment(Pos.BOTTOM_LEFT);
        stackPaneBottom1.setPrefWidth(borderPanePrint.getPrefWidth()/2-20);
        stackPaneBottom1.setMinWidth(borderPanePrint.getPrefWidth()/2-20);
        stackPaneBottom1.setMaxWidth(borderPanePrint.getPrefWidth()/2-20);
        StackPane stackPaneBottom2 = new StackPane();
        stackPaneBottom2.getChildren().addAll(signature);
        stackPaneBottom2.setAlignment(Pos.BOTTOM_RIGHT);
        stackPaneBottom2.setPrefWidth(borderPanePrint.getPrefWidth()/2-20);
        stackPaneBottom2.setMinWidth(borderPanePrint.getPrefWidth()/2-20);
        stackPaneBottom2.setMaxWidth(borderPanePrint.getPrefWidth()/2-20);
        HBox hBoxBottom = new HBox();
        hBoxBottom.setPadding(new Insets(20));
        hBoxBottom.setPrefWidth(borderPanePrint.getPrefWidth());
        hBoxBottom.getChildren().addAll(stackPaneBottom1, stackPaneBottom2);

        VBox vBoxTop = new VBox();
        vBoxTop.setPadding(new Insets(20));
        vBoxTop.getChildren().addAll(hBoxHeadMain, hBoxheadSecond);

        borderPanePrint.setTop(vBoxTop);
        borderPanePrint.setCenter(gridPanePrint);
        borderPanePrint.setBottom(hBoxBottom);

        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(pageLayout.getPrintableWidth());
        stackPane.setPrefHeight(pageLayout.getPrintableHeight()/2);
        stackPane.setAlignment(Pos.CENTER);
        stackPane.getChildren().addAll(borderPanePrint);

        double scaleX = pageLayout.getPrintableWidth();
        double scaleY = pageLayout.getPrintableHeight();

        System.out.println(headMain.getPrefWidth());

        jobSettings.setPageLayout(pageLayout);

        if(printerJob == null){
            return;
        }

        boolean proceed = printerJob.showPrintDialog(stagePrint);
        //boolean pageSetup = printerJob.showPageSetupDialog(stagePrint);

        if(proceed){
            boolean printed = printerJob.printPage(stackPane);
            if(printed){
                printerJob.endJob();
                DataBaseHelper.setCount(DataBaseHelper.getCount());
                stagePrint.close();
            }
        }

        /*
        Stage testPrintStage = new Stage();
    Scene testPrintScene = new Scene(stackPane, 900,600);
    testPrintStage.setScene(testPrintScene);
    testPrintStage.show();
         */

    }

    static ImageView getLogo(){
        Image logo = null;
        try {
            FileInputStream fs = new FileInputStream(DataBaseHelper.path + "\\src\\images\\logoPrnt.jpg");
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
