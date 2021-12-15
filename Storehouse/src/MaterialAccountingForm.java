import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

public class MaterialAccountingForm
{
    private Stage _accountingFormStage;
    private BorderPane _accountingFormBorderPane;
    private Scene _accountingFormScene;
    private VBox _centerVbox;
    private VBox _accountingsTableViewVBox;

    private FlowPane _materialInfoFlowPane = new FlowPane();
    private TextField _manufacturerTextField = new TextField();
    private TextField _widthTextField = new TextField();
    private TextField _heightTextField = new TextField();
    private TextField _colorTextField = new TextField();
    private TextField _propertyTextField = new TextField();
    private TextField _thicknessTextField = new TextField();
    private TextField _attributeTextField = new TextField();
    private ArrayList<MaterialAccounting> _accountings;
    private TableView<MaterialAccounting> _accountingsTableView;
    private boolean _showKinds = false;

    MaterialAccountingForm()
    {
        _accountings = DataBaseStorehouse.getMaterialAccountingList();
    }

    void show(Stage primaryStage)
    {
        _accountingFormStage = new Stage();
        _accountingFormBorderPane = new BorderPane();
        _accountingFormScene = new Scene(_accountingFormBorderPane, 900, 900);

        _accountingFormBorderPane.setCenter(getCenter());
        _accountingFormBorderPane.setBottom(getBottom());

        _accountingFormStage.initModality(Modality.WINDOW_MODAL);
        _accountingFormStage.initOwner(primaryStage);
        _accountingFormStage.setTitle("Учёт материалов");
        _accountingFormStage.getIcons().add(MainInterface.getIconLogo());
        _accountingFormStage.setScene(_accountingFormScene);
        _accountingFormStage.setOnCloseRequest(event ->
        {
            saveMaterialsTableAccountingsColsWidth();
            saveMatAccStageSize(_accountingFormStage);
        });
        loadMatAccStageSize(_accountingFormStage);
        _accountingFormStage.show();
    }

    private VBox getCenter()
    {
        _centerVbox = new VBox();
        _accountingsTableViewVBox = new VBox();
        TitledPane kindTitledPane = new TitledPane();
        Accordion kindAccordion = new Accordion();
        HBox inScrollPaneHBox = new HBox();
        ScrollPane scrollPane = new ScrollPane(inScrollPaneHBox);
        kindAccordion.getPanes().addAll(getKindsTitledPanes());
        //ListView<TitledPane> kindsTitledPaneListView = new ListView<TitledPane>(FXCollections.observableArrayList(getKindsTitledPanes()));

        setTextFields();

        kindAccordion.setPadding(new Insets(15));
        inScrollPaneHBox.getChildren().addAll(kindAccordion);
        HBox.setHgrow(kindAccordion, Priority.ALWAYS);
        scrollPane.fitToWidthProperty().set(true);

        kindTitledPane.setText("Виды материала");
        kindTitledPane.setExpanded(false);
        kindTitledPane.setContent(scrollPane);
        kindTitledPane.expandedProperty().addListener((observable, oldValue, newValue) ->
        {
            if(oldValue)
            {
                if(!kindTitledPane.isExpanded())
                {
                    saveMaterialsTableAccountingsColsWidth();
                    _accountingsTableViewVBox.getChildren().remove(_accountingsTableView);
                    _accountingsTableViewVBox.getChildren().add(getAccountingTableView(_accountings));
                }
            }
        });

        _materialInfoFlowPane.setVgap(10);
        _materialInfoFlowPane.setHgap(10);
        _materialInfoFlowPane.setAlignment(Pos.TOP_CENTER);
        _materialInfoFlowPane.setPadding(new Insets(0,15,15,15));

        _accountingsTableViewVBox.getChildren().addAll(getAccountingTableView(_accountings));
        _accountingsTableViewVBox.setPadding(new Insets(15));

        _centerVbox.setSpacing(10);
        _centerVbox.setAlignment(Pos.TOP_CENTER);
        //_centerVbox.setPadding(new Insets(15));
        _centerVbox.setStyle("-fx-background-color: #f0f8ff");
        if(_showKinds) _centerVbox.getChildren().add(kindTitledPane);
        _centerVbox.getChildren().addAll(_accountingsTableViewVBox);
        _centerVbox.getChildren().addAll(new Separator(), _materialInfoFlowPane);

        return _centerVbox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button closeBtn = new Button("Закрыть");

        closeBtn.setOnAction(event ->
        {
            saveMaterialsTableAccountingsColsWidth();
            saveMatAccStageSize(_accountingFormStage);
            _accountingFormStage.close();
        });

        bottomAnchorPane.getChildren().addAll(closeBtn);
        AnchorPane.setTopAnchor(closeBtn, 5.0);
        AnchorPane.setRightAnchor(closeBtn, 5.0);
        AnchorPane.setBottomAnchor(closeBtn, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private ArrayList<TitledPane> getKindsTitledPanes()
    {
        ArrayList<TitledPane> kindsTitledPanesArray = new ArrayList<>();

        for(MaterialsKind kind : MaterialsForm._activeKinds)
        {
            TitledPane titledPane = new TitledPane();
            ListView<Material> materialsListView = new ListView<>();

            for(Material material : Finder.get_allMaterialsList())
                if(material.get_kind() == kind.get_id())
                    materialsListView.getItems().add(material);

            materialsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
            {
                ArrayList<MaterialAccounting> accountingsByMaterial = new ArrayList<>();
                for(MaterialAccounting accounting : _accountings)
                {
                    if(accounting.get_material() == newValue.get_id())
                        accountingsByMaterial.add(accounting);
                }
                clearTextFields();
                saveMaterialsTableAccountingsColsWidth();
                _accountingsTableViewVBox.getChildren().remove(_accountingsTableView);
                _accountingsTableViewVBox.getChildren().add(getAccountingTableView(accountingsByMaterial));
            });

            titledPane.setContent(materialsListView);
            titledPane.setExpanded(false);
            titledPane.setText(kind.get_name());

            kindsTitledPanesArray.add(titledPane);
        }

        return kindsTitledPanesArray;
    }

    private TableView<MaterialAccounting> getAccountingTableView(ArrayList<MaterialAccounting> accountingsList)
    {
        _accountingsTableView = new TableView<MaterialAccounting>();
        //_accountingsTableView.setPrefHeight(_accountingFormScene.getHeight()/2);
        TableColumn<MaterialAccounting, LocalDateTime> dateCol = new TableColumn<>("Дата");
        dateCol.setStyle("-fx-alignment: CENTER;");
        //dateCol.prefWidthProperty().bind(_accountingFormScene.widthProperty().multiply(0.076));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_dateTime"));
        dateCol.setCellFactory(tc -> new TableCell<MaterialAccounting, LocalDateTime>()
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

        TableColumn<MaterialAccounting, Integer> materialKindCol = new TableColumn<>("Материал");
        //materialKindCol.prefWidthProperty().bind(_accountingFormScene.widthProperty().multiply(0.42));
        materialKindCol.setStyle("-fx-alignment: CENTER;");
        materialKindCol.setCellValueFactory(new PropertyValueFactory<>("_material"));
        materialKindCol.setCellFactory(tc -> new TableCell<MaterialAccounting, Integer>()
        {
            @Override
            protected void updateItem(Integer materialId, boolean empty)
            {
                super.updateItem(materialId, empty);
                if(empty)
                {
                    setText(null);
                } else
                {
                    Material material = Finder.getMaterial(materialId);
                    String materialKind = Finder.getMaterialKind(
                            material.get_kind()).get_name();
                    setText(materialKind);
                }
            }
        });

        TableColumn<MaterialAccounting, Integer> accountCol = new TableColumn<>("Аккаунт");
        accountCol.setCellValueFactory(new PropertyValueFactory<>("_accountId"));
        accountCol.setStyle("-fx-alignment: CENTER;");
        //accountCol.prefWidthProperty().bind(_accountingFormScene.widthProperty().multiply(0.094));
        accountCol.setCellFactory(tc -> new TableCell<MaterialAccounting, Integer>()
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

        TableColumn<MaterialAccounting, String> procedureCol = new TableColumn<>("Процедура");
        procedureCol.setStyle("-fx-alignment: CENTER;");
        procedureCol.setCellValueFactory(new PropertyValueFactory<>("_procedure"));
        //procedureCol.prefWidthProperty().bind(_accountingFormScene.widthProperty().multiply(0.08));

        TableColumn<MaterialAccounting, Integer> quantityCol = new TableColumn<>("Количество");
        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));
        //quantityCol.prefWidthProperty().bind(_accountingFormScene.widthProperty().multiply(0.1));

        _accountingsTableView.setPrefHeight((_accountingFormScene.getHeight()*2)/3);
        //tableViewOrders.setPlaceholder(new Text("База заказов пуста"));
        _accountingsTableView.getColumns().addAll(
                dateCol,
                materialKindCol,
                accountCol,
                procedureCol,
                quantityCol);
        _accountingsTableView.setStyle("");
        _accountingsTableView.setRowFactory(new Callback<TableView<MaterialAccounting>, TableRow<MaterialAccounting>>()
        {
            @Override
            public TableRow<MaterialAccounting> call(TableView<MaterialAccounting> tableView)
            {
                TableRow<MaterialAccounting> row = new TableRow<MaterialAccounting>()
                {
                    @Override
                    protected void updateItem(MaterialAccounting accounting, boolean empty)
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

                row.setOnMouseClicked(event ->
                {
                    if(!row.isEmpty())
                    {
                        if(row.getItem().get_procedure().equals("Приход"))
                        {
                            row.setStyle("-fx-background-color: #4682B4;" +
                                    "-fx-text-background-color: #ffffff");
                        } else if(row.getItem().get_procedure().equals("Расход"))
                        {
                            row.setStyle("-fx-background-color: #8B0000; " +
                                    "-fx-text-background-color: #ffffff");
                        } else
                        {
                            row.setStyle("");
                        }
                    }
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1)
                    {
                        setMaterialInfo(Finder.getMaterial(row.getItem().get_material()));
                    } else if ((!row.isEmpty() && event.getButton() == MouseButton.SECONDARY))
                    {
                        row.setOnContextMenuRequested(event1 ->
                        {
                            if(row.getItem()!=null)
                            {
                                System.out.println("клик правой КМ");
                            }
                        });
                    }
                });
                return row;
            }
        });

        _accountingsTableView.setPrefHeight(_accountingFormScene.getHeight()/1.4);
        _accountingsTableView.setPlaceholder(new Text("Данные отсутствуют"));
        _accountingsTableView.setItems(FXCollections.observableArrayList(accountingsList));
        //_accountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        _accountingsTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _accountingsTableView.scrollTo(_accountingsTableView.getItems().size()-1);
        loadMaterialsTableAccountingsColsWidth();
        return _accountingsTableView;
    }

    private void setMaterialInfo(Material material)
    {
        clearTextFields();
        _materialInfoFlowPane.getChildren().clear();
        MaterialsKind kind = Finder.getMaterialKind(material.get_kind());

        TitledPane manufacturerTitledPane = new TitledPane();
        manufacturerTitledPane.setText("Производитель");
        manufacturerTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        manufacturerTitledPane.setCollapsible(false);
        manufacturerTitledPane.setExpanded(true);
        manufacturerTitledPane.setContent(_manufacturerTextField);

        TitledPane widthTitledPane = new TitledPane();
        widthTitledPane.setText("Ширина");
        widthTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        widthTitledPane.setCollapsible(false);
        widthTitledPane.setExpanded(true);
        widthTitledPane.setContent(_widthTextField);

        TitledPane heightTitledPane = new TitledPane();
        heightTitledPane.setText("Высота/метраж");
        heightTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        heightTitledPane.setCollapsible(false);
        heightTitledPane.setExpanded(true);
        heightTitledPane.setContent(_heightTextField);

        TitledPane colorTitledPane = new TitledPane();
        colorTitledPane.setText("Цвет");
        colorTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        colorTitledPane.setCollapsible(false);
        colorTitledPane.setExpanded(true);
        colorTitledPane.setContent(_colorTextField);

        TitledPane propertyTitledPane = new TitledPane();
        propertyTitledPane.setText("Свойства");
        propertyTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        propertyTitledPane.setCollapsible(false);
        propertyTitledPane.setExpanded(true);
        propertyTitledPane.setContent(_propertyTextField);

        TitledPane thicknessTitledPane = new TitledPane();
        thicknessTitledPane.setText("Толщина");
        thicknessTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        thicknessTitledPane.setCollapsible(false);
        thicknessTitledPane.setExpanded(true);
        thicknessTitledPane.setContent(_thicknessTextField);

        TitledPane attributeTitledPane = new TitledPane();
        attributeTitledPane.setText("Атрибут");
        attributeTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        attributeTitledPane.setCollapsible(false);
        attributeTitledPane.setExpanded(true);
        attributeTitledPane.setContent(_attributeTextField);

        if(kind.get_manufacturer() && Finder.getManufacturer(material.get_manufacturer()) != null)
        {
            _manufacturerTextField.setText(Finder.getManufacturer(material.get_manufacturer()).get_name());
            _materialInfoFlowPane.getChildren().add(manufacturerTitledPane);
        }

        if(kind.get_width())
        {
            _widthTextField.setText(String.valueOf(material.get_width()));
            _materialInfoFlowPane.getChildren().add(widthTitledPane);
        }

        if(kind.get_height())
        {
            _heightTextField.setText(String.valueOf(material.get_height()));
            _materialInfoFlowPane.getChildren().add(heightTitledPane);
        }

        if(kind.get_color() && Finder.getColor(material.get_color()) != null)
        {
            _colorTextField.setText(Finder.getColor(material.get_color()).get_name());
            _materialInfoFlowPane.getChildren().add(colorTitledPane);
        }

        if(kind.get_property() && Finder.getProperty(material.get_property()) != null)
        {
            _propertyTextField.setText(Finder.getProperty(material.get_property()).get_name());
            _materialInfoFlowPane.getChildren().add(propertyTitledPane);
        }

        if(kind.get_thickness())
        {
            _thicknessTextField.setText(String.valueOf(material.get_thickness()));
            _materialInfoFlowPane.getChildren().add(thicknessTitledPane);
        }

        if(kind.get_attribute() && Finder.getAttribute(material.get_attribute()) != null)
        {
            _attributeTextField.setText(Finder.getAttribute(material.get_attribute()).get_name());
            _materialInfoFlowPane.getChildren().add(attributeTitledPane);
        }
    }

    private void setTextFields()
    {
        _manufacturerTextField.setEditable(false);
        _widthTextField.setEditable(false);
        _heightTextField.setEditable(false);
        _colorTextField.setEditable(false);
        _propertyTextField.setEditable(false);
        _thicknessTextField.setEditable(false);
        _attributeTextField.setEditable(false);
    }

    public void set_accountings(ArrayList<MaterialAccounting> accountings)
    {
        this._accountings = accountings;
    }

    public void set_showKinds(boolean showKinds)
    {
        this._showKinds = showKinds;
    }

    private void clearTextFields()
    {
        _manufacturerTextField.clear();
        _widthTextField.clear();
        _heightTextField.clear();
        _colorTextField.clear();
        _propertyTextField.clear();
        _thicknessTextField.clear();
        _attributeTextField.clear();
    }

    private void saveMaterialsTableAccountingsColsWidth()
    {
        Properties tableColumnsWidthProp =
                Finder._settings.getPropertiesTableColumsWidth("_accountingsTableView");
        if (tableColumnsWidthProp == null)
        {
            tableColumnsWidthProp = new Properties();
            for (int i = 0; i < _accountingsTableView.getColumns().size(); ++i)
            {
                tableColumnsWidthProp.put(String.valueOf(i), _accountingsTableView.getColumns().get(i).getWidth());
            }
            Finder._settings.addPropertiesColWidths("_accountingsTableView", tableColumnsWidthProp);
        }
        else
        {
            for (int i = 0; i < _accountingsTableView.getColumns().size(); ++i)
            {
                tableColumnsWidthProp.put(String.valueOf(i), _accountingsTableView.getColumns().get(i).getWidth());
            }
        }
    }

    private void loadMaterialsTableAccountingsColsWidth()
    {
        try
        {
            Properties tableProperties = Finder._settings.getPropertiesTableColumsWidth("_accountingsTableView");
            if (tableProperties != null && tableProperties.size() > 0)
            {
                for (int i = 0; i < _accountingsTableView.getColumns().size(); ++i)
                {
                    _accountingsTableView.getColumns().get(i).setPrefWidth((double)tableProperties.get(String.valueOf(i)));
                }
            }
            else
                _accountingsTableView.columnResizePolicyProperty().set(TableView.CONSTRAINED_RESIZE_POLICY);
        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }

    private void saveMatAccStageSize(Stage matAccStage)
    {
        Properties propertiesStageSizes =
                Finder._settings.getPropertiesStageSizes("matAccStage");
        if (propertiesStageSizes == null)
        {
            propertiesStageSizes = new Properties();
            propertiesStageSizes.put("width", matAccStage.getWidth());
            propertiesStageSizes.put("height", matAccStage.getHeight());
            Finder._settings.addPropertiesStageSizes("matAccStage", propertiesStageSizes);
        } else
        {
            propertiesStageSizes.put("width", matAccStage.getWidth());
            propertiesStageSizes.put("height", matAccStage.getHeight());
        }
    }

    private void loadMatAccStageSize(Stage matAccStage)
    {
        try
        {
            Properties properties = Finder._settings.getPropertiesStageSizes("matAccStage");
            if (properties != null && properties.size() > 0)
            {
                matAccStage.setWidth((double)properties.get("width"));
                matAccStage.setHeight((double)properties.get("height"));
            }

        }catch (Exception ex)
        {
            System.out.println("Ошибка загрузки настроек\n" + ex.toString());
        }
    }
}
