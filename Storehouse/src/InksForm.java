import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;

public class InksForm
{
    private Stage _inksFormStage;
    private Scene _inksFormScene;
    private BorderPane _mainBorderPane;
    private VBox _leftVBox;
    private VBox _centerVBox;
    private ToggleGroup _radioBtnsGroup;
    private RadioButton _consumptionRadioBtn;
    private RadioButton _arrivalRadioBtn;
    private RadioButton _allRadioBtn;
    private TableView<InkAccounting> _inkAccountingsTableView;
    static int _currAccount;
    //static ArrayList<Machine> _allMachinesArrayList;
    static ArrayList<Machine> _activeMachinesArrayList;
    //static ArrayList<Ink> _allInksArrayList;
    static ArrayList<Ink> _activeInksArrayList;
    static ArrayList<InkAccounting> _inkAccountingsArrayList;
    private ListView<Machine> _machinesListView;
    private ListView<Ink> _inksListView;
    private Label _inkNameLabel;
    private Label _inkColorLabel;
    private Label _inkMachineLabel;
    private Label _inkVolumeLabel;
    private Label _inkQuantityLabel;
    private Label _inkConsumptionLabel;

    InksForm()
    {
        //_allMachinesArrayList = new ArrayList<>(DataBaseStorehouse.getMachinesList());
        _activeMachinesArrayList = new ArrayList<>();
        for (final Machine machine : Finder.get_allMachinesArrayList())
            if (machine.is_active())
                _activeMachinesArrayList.add(machine);
        //_allInksArrayList = new ArrayList<>(DataBaseStorehouse.getInksList());
        _activeInksArrayList = new ArrayList<>();
        for (final Ink ink : Finder.get_allInksArrayList())
            if (ink.is_active())
                _activeInksArrayList.add(ink);
        _inkAccountingsArrayList = new ArrayList<>(DataBaseStorehouse.getInkAccountingList());
    }

    void showAndWait(Stage primaryStage)
    {
        _inksFormStage = new Stage();
        _mainBorderPane = new BorderPane();
        _inksFormScene = new Scene(_mainBorderPane, 675, 600);

        _mainBorderPane.setLeft(getLeft());
        _mainBorderPane.setCenter(getCenter());
        _mainBorderPane.setBottom(getBottom());

        //_inksFormStage.setOnCloseRequest(event -> DataBaseStorehouse.closeConnection());
        _inksFormStage.initModality(Modality.WINDOW_MODAL);
        _inksFormStage.initOwner(primaryStage);
        _inksFormStage.setScene(_inksFormScene);
        _inksFormStage.setTitle("Чернила");
        _inksFormStage.getIcons().add(MainInterface.getIconLogo());
        _inksFormStage.showAndWait();
    }

    private VBox getLeft()
    {
        _leftVBox = new VBox();
        TitledPane machinesTitledPane = new TitledPane();
        TitledPane inksTitledPane = new TitledPane();
        _machinesListView = new ListView<>();
        _inksListView = new ListView<>();
        ContextMenu machinesContextMenu = new ContextMenu();
        ContextMenu inksContextMenu = new ContextMenu();
        MenuItem addMachineMenuItem = new MenuItem("Добавить");
        MenuItem editMachineMenuItem = new MenuItem("Редактировать");
        MenuItem deleteMachineMenuItem = new MenuItem("Удалить");
        MenuItem addInkMenuItem = new MenuItem("Добавить новые");
        MenuItem editInkMenuItem = new MenuItem("Редактировать");
        MenuItem deleteInkMenuItem = new MenuItem("Удалить");
        Menu inkAccountingMenu = new Menu("Учёт чернил");
        MenuItem inkConsumptionMenuItem = new MenuItem("Взять со склада");
        MenuItem inkIncomingMenuItem = new MenuItem("Внести приход");
        MenuItem addRequestItem = new MenuItem("Создать заявку");

        addMachineMenuItem.setOnAction(event ->
        {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Принтеры");
            dialog.setHeaderText("Введине название принтера");
            dialog.setContentText("Название: ");
            dialog.graphicProperty().set(null);
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().add(MainInterface.getIconLogo());
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent() && !result.get().isEmpty())
            {
                Machine machine = new Machine();
                machine.set_name(result.get());
                if (DataBaseStorehouse.addMachine(machine))
                {
                    machine.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.MACHINES_TABLE));
                    Finder.get_allMachinesArrayList().add(machine);
                    _activeMachinesArrayList.add(machine);
                    _machinesListView.getItems().add(machine);
                }
            }
        });

        editMachineMenuItem.setOnAction(event ->
        {
            if (_machinesListView.getSelectionModel().getSelectedItem() != null)
            {
                TextInputDialog dialog = new TextInputDialog();
                Machine machine = _machinesListView.getSelectionModel().getSelectedItem();
                final int indexInArrayAll = Finder.get_allMachinesArrayList().indexOf(machine);
                final int indexInArrayActive = _activeMachinesArrayList.indexOf(machine);
                final int indexInListView = _machinesListView.getItems().indexOf(machine);
                dialog.setTitle("Принтеры");
                dialog.setHeaderText("Введине название принтера");
                dialog.setContentText("Название: ");
                dialog.graphicProperty().set(null);
                Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
                dialogStage.getIcons().add(MainInterface.getIconLogo());
                dialog.getEditor().setText(machine.get_name());
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent() && !result.get().isEmpty())
                {
                    machine.set_name(result.get());
                    if (DataBaseStorehouse.editMachine(machine))
                    {
                        Finder.get_allMachinesArrayList().set(indexInArrayAll, machine);
                        _activeMachinesArrayList.set(indexInArrayActive, machine);
                        _machinesListView.getItems().set(indexInListView, machine);
                    }
                }
            }
        });

        deleteMachineMenuItem.setOnAction(event ->
        {
            if (_machinesListView.getSelectionModel().getSelectedItem() != null)
            {
                Machine machine = _machinesListView.getSelectionModel().getSelectedItem();
                final int indexInArrayAll = Finder.get_allMachinesArrayList().indexOf(machine);
                final int indexInArrayActive = _activeMachinesArrayList.indexOf(machine);
                final int indexInListView = _machinesListView.getItems().indexOf(machine);

                if (checkMachineInInks(machine.get_id()))
                {
                    System.out.println("принтер есть в списке ВСЕХ чернил");
                    if (checkMachineInActiveInks(machine.get_id()))
                    {
                        System.out.println("принтер есть в списке АКТИВНЫХ чернил");
                        MainInterface.getAlertWarningDialog(
                                "У принтера есть в наличии активные чернила.\n " +
                                        "Либо удалите все чернила у данного принтера, " +
                                        "либо установите у этих чернил другой принтер"
                        );
                    } else
                    {
                        System.out.println("принтера нету в списке АКТИВНЫХ чернил");
                        machine.set_active(false);
                        if (DataBaseStorehouse.editMachine(machine))
                        {
                            Finder.get_allMachinesArrayList().set(indexInArrayAll, machine);
                            _activeMachinesArrayList.remove(indexInArrayActive);
                            _machinesListView.getItems().remove(indexInListView);
                        }
                    }
                } else
                {
                    System.out.println("принтера нету в списке ВСЕХ чернил");
                    if (DataBaseStorehouse.deleteMachine(machine.get_id()))
                    {
                        Finder.get_allMachinesArrayList().remove(indexInArrayAll);
                        _activeMachinesArrayList.remove(indexInArrayActive);
                        _machinesListView.getItems().remove(indexInListView);
                    }
                }
            }
        });

        addInkMenuItem.setOnAction(event ->
        {
            InkDialogForm inkDialogForm = new InkDialogForm();
            if (_machinesListView.getSelectionModel().getSelectedItem() != null)
                inkDialogForm.set_currMachine(_machinesListView.getSelectionModel().getSelectedItem());
            inkDialogForm.showAndWait(_inksFormStage);
            if (inkDialogForm.is_ok())
            {
                Ink newInk = inkDialogForm.get_ink();
                if (DataBaseStorehouse.addInk(newInk))
                {
                    newInk.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.INKS_TABLE));
                    if (_machinesListView.getSelectionModel().getSelectedItem() != null)
                        if (_machinesListView.getSelectionModel().getSelectedItem().get_id() == newInk.get_machine())
                            _inksListView.getItems().add(newInk);
                    Finder.get_allInksArrayList().add(newInk);
                    _activeInksArrayList.add(newInk);
                    if (newInk.get_quantity() > 0)
                    {
                        InkAccounting accounting = new InkAccounting();
                        accounting.set_ink(newInk.get_id());
                        accounting.set_quantity(newInk.get_quantity());
                        accounting.set_procedure(MaterialsForm.INCOMING);
                        accounting.set_dateTime(LocalDateTime.now());
                        if (DataBaseStorehouse.addInkAccounting(accounting))
                        {
                            accounting.set_id(
                                    DataBaseStorehouse.getLastId(
                                            DataBaseStorehouse.INK_ACCOUNTING_TABLE));
                            _inkAccountingsArrayList.add(accounting);
                            _inksListView.getSelectionModel().select(newInk);
                            setItemsInkAccountingsTableView(newInk);
                            setInkData(newInk);
                        }
                    }
                }
            }
        });

        editInkMenuItem.setOnAction(event ->
        {
            if (_inksListView.getSelectionModel().getSelectedItem() != null)
            {
                Ink selectedInk = _inksListView.getSelectionModel().getSelectedItem();
                final int indexInArrayAll = Finder.get_allInksArrayList().indexOf(selectedInk);
                final int indexInArrayActive = _activeInksArrayList.indexOf(selectedInk);
                final int indexInListView = _inksListView.getItems().indexOf(selectedInk);
                InkDialogForm inkDialogForm = new InkDialogForm(selectedInk);
                inkDialogForm.showAndWait(_inksFormStage);
                if (inkDialogForm.is_ok())
                {
                    Ink editedInk = inkDialogForm.get_ink();
                    if (DataBaseStorehouse.editInk(editedInk))
                    {
                        if (_machinesListView.getSelectionModel().getSelectedItem().get_id() == editedInk.get_machine())
                            _inksListView.getItems().set(indexInListView, editedInk);
                        else
                            _inksListView.getItems().remove(indexInListView);
                        Finder.get_allInksArrayList().set(indexInArrayAll, editedInk);
                        _activeInksArrayList.set(indexInArrayActive, editedInk);
                    }
                }
            }
        });

        deleteInkMenuItem.setOnAction(event ->
        {
            if (_inksListView.getSelectionModel().getSelectedItem() != null)
            {
                if (MainInterface.getAlertAskConfirmationDialog("Уверены что хотите удалить?"))
                {
                    Ink selectedInk = _inksListView.getSelectionModel().getSelectedItem();
                    final int indexInArrayAll = Finder.get_allInksArrayList().indexOf(selectedInk);
                    final int indexInArrayActive = _activeInksArrayList.indexOf(selectedInk);
                    final int indexInListView = _inksListView.getItems().indexOf(selectedInk);
                    if (checkInkInAccountings(selectedInk.get_id()))
                    {
                        selectedInk.set_active(false);
                        if (DataBaseStorehouse.editInk(selectedInk))
                        {
                            Finder.get_allInksArrayList().set(indexInArrayAll, selectedInk);
                            _activeInksArrayList.remove(indexInArrayActive);
                            _inksListView.getItems().remove(indexInListView);
                        }
                    } else
                    {
                        if (DataBaseStorehouse.deleteInk(selectedInk.get_id()))
                        {
                            Finder.get_allInksArrayList().remove(indexInArrayAll);
                            _activeInksArrayList.remove(indexInArrayActive);
                            _inksListView.getItems().remove(indexInListView);
                        }
                    }
                }
            }
        });

        addRequestItem.setOnAction(event ->
        {
            Ink selectedInk = _inksListView.getSelectionModel().getSelectedItem();
            if (selectedInk == null)
                return;
            RequestDialog dialog = new RequestDialog(2, selectedInk.get_id());
            dialog.set_kindComboBox();
            dialog.showAndWait(_inksFormStage);
        });

        inkConsumptionMenuItem.setOnAction(event ->
        {
            Ink ink = _inksListView.getSelectionModel().getSelectedItem();
            final int indexInArrayAll = Finder.get_allInksArrayList().indexOf(ink);
            final int indexInArrayActive = _activeInksArrayList.indexOf(ink);
            final int indexInListView = _inksListView.getItems().indexOf(ink);
            if (ink.get_quantity() > 0)
            {
                int newQuantity = ink.get_quantity() - 1;
                if (DataBaseStorehouse.changeInkQuantity(ink.get_id(), newQuantity))
                {
                    ink.set_quantity(newQuantity);
                    InkAccounting accounting = new InkAccounting();

                    accounting.set_ink(ink.get_id());
                    accounting.set_quantity(1);
                    accounting.set_procedure(MaterialsForm.CONSUMPTION);
                    accounting.set_dateTime(LocalDateTime.now());
                    accounting.set_dateTimeOpen(LocalDateTime.now());

                    InkAccounting lastAccounting = getLastInkAccByColorAndMachine(ink.get_color(), ink.get_machine());
                    final int indexLastAccounting = _inkAccountingsArrayList.indexOf(lastAccounting);

                    if (DataBaseStorehouse.addInkAccounting(accounting))
                    {
                        accounting.set_id(
                                DataBaseStorehouse.getLastId(
                                        DataBaseStorehouse.INK_ACCOUNTING_TABLE));
                        _inkAccountingsArrayList.add(accounting);
                        setItemsInkAccountingsTableView(_inksListView.getSelectionModel().getSelectedItem());

                        if (lastAccounting != null && lastAccounting.get_id() != accounting.get_id())
                        {
                            lastAccounting.set_dateTimeClose(accounting.get_dateTimeOpen());
                            if (DataBaseStorehouse.editInkAccounting(lastAccounting))
                            {
                                _inkAccountingsArrayList.set(indexLastAccounting, lastAccounting);
                                ink.set_consumption(calculateInkConsumption(ink));
                                Ink lastInk = Finder.getInk(lastAccounting.get_ink());
                                final int indexLastInkInArrayAll = Finder.get_allInksArrayList().indexOf(lastInk);
                                final int indexLastInkInArrayActive = _activeInksArrayList.indexOf(lastInk);

                                if (ink.get_id() != lastInk.get_id())
                                {
                                    lastInk.set_consumption(calculateInkConsumption(lastInk));
                                    if (DataBaseStorehouse.changeInkConsumption(lastInk))
                                    {
                                        Finder.get_allInksArrayList().set(indexLastInkInArrayAll, lastInk);
                                        _activeInksArrayList.set(indexLastInkInArrayActive, lastInk);
                                    }
                                }

                                if (DataBaseStorehouse.changeInkConsumption(ink))
                                {
                                    Finder.get_allInksArrayList().set(indexInArrayAll, ink);
                                    _activeInksArrayList.set(indexInArrayActive, ink);
                                    _inksListView.getItems().set(indexInListView, ink);
                                    setInkData(ink);
                                }
                            }
                        } else
                        {
                            if (DataBaseStorehouse.changeInkConsumption(ink))
                            {
                                Finder.get_allInksArrayList().set(indexInArrayAll, ink);
                                _activeInksArrayList.set(indexInArrayActive, ink);
                                _inksListView.getItems().set(indexInListView, ink);
                                setInkData(ink);
                            }
                        }
                    }
                }
            } else
                MainInterface.getAlertWarningDialog("Данные чернила отсутствуют на складе!");
        });

        inkIncomingMenuItem.setOnAction(event ->
        {
            Ink ink = _inksListView.getSelectionModel().getSelectedItem();
            final int indexInArrayAll = Finder.get_allInksArrayList().indexOf(ink);
            final int indexInArrayActive = _activeInksArrayList.indexOf(ink);
            final int indexInListView = _inksListView.getItems().indexOf(ink);
            int newQuantity = ink.get_quantity();
            final int inputQuantity = addInkAmount();
            if (inputQuantity > 0)
            {
                newQuantity += inputQuantity;
                if (DataBaseStorehouse.changeInkQuantity(ink.get_id(), newQuantity))
                {
                    ink.set_quantity(newQuantity);
                    Finder.get_allInksArrayList().set(indexInArrayAll, ink);
                    _activeInksArrayList.set(indexInArrayActive, ink);
                    _inksListView.getItems().set(indexInListView, ink);
                    InkAccounting accounting = new InkAccounting();
                    accounting.set_ink(ink.get_id());
                    accounting.set_quantity(inputQuantity);
                    accounting.set_procedure(MaterialsForm.INCOMING);
                    accounting.set_dateTime(LocalDateTime.now());
                    if (DataBaseStorehouse.addInkAccounting(accounting))
                    {
                        accounting.set_id(
                                DataBaseStorehouse.getLastId(
                                        DataBaseStorehouse.INK_ACCOUNTING_TABLE));
                        _inkAccountingsArrayList.add(accounting);
                        setItemsInkAccountingsTableView(_inksListView.getSelectionModel().getSelectedItem());
                        setInkData(ink);
                    }
                }
            }
        });

        machinesContextMenu.getItems().addAll(
                addMachineMenuItem,
                editMachineMenuItem,
                deleteMachineMenuItem);

        _machinesListView.setContextMenu(machinesContextMenu);
        _machinesListView.getItems().addAll(_activeMachinesArrayList);
        _machinesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Machine>()
        {
            @Override
            public void changed(ObservableValue<? extends Machine> observable, Machine oldValue, Machine newValue)
            {
                setInksListView(newValue);
                _inkAccountingsTableView.setPlaceholder(new Text("Выберите чернила для отображения данных"));
            }
        });

        machinesTitledPane.setText("Принтеры");
        machinesTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        machinesTitledPane.setExpanded(true);
        machinesTitledPane.setCollapsible(false);
        machinesTitledPane.setContent(_machinesListView);

        inkAccountingMenu.getItems().addAll(inkConsumptionMenuItem, inkIncomingMenuItem);

        inksContextMenu.getItems().addAll(
                addInkMenuItem,
                editInkMenuItem,
                deleteInkMenuItem,
                new SeparatorMenuItem(),
                inkAccountingMenu,
                new SeparatorMenuItem(),
                addRequestItem);

        _inksListView.setContextMenu(inksContextMenu);
        _inksListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Ink>()
        {
            @Override
            public void changed(ObservableValue<? extends Ink> observable, Ink oldValue, Ink newValue)
            {
                if (newValue != null)
                {
                    setItemsInkAccountingsTableView(newValue);
                    setInkData(newValue);
                } else
                    clearInkData();
            }
        });

        inksTitledPane.setText("Чернила");
        inksTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        inksTitledPane.setExpanded(true);
        inksTitledPane.setCollapsible(false);
        inksTitledPane.setContent(_inksListView);

        _leftVBox.setSpacing(10);
        _leftVBox.setStyle("-fx-background-color: #f0f8ff");
        _leftVBox.setPadding(new Insets(15));
        _leftVBox.getChildren().addAll(machinesTitledPane, inksTitledPane);

        return _leftVBox;
    }

    private VBox getCenter()
    {
        _centerVBox = new VBox();
        TitledPane inkAccountingTitledPane = new TitledPane();
        VBox inkAccountingVBox = new VBox();
        VBox inksTableViewVBox = new VBox();
        TitledPane inkDataTitledPane = new TitledPane();
        GridPane inkDataGridPane = new GridPane();
        Label inkNameHeadLabel = new Label("Название: ");
        Label inkColorHeadLabel = new Label("Цвет: ");
        Label inkMachineHeadLabel = new Label("Принтер: ");
        Label inkVolumeHeadLabel = new Label("Объем тары: ");
        Label inkQuantityHeadLabel = new Label("Количество: ");
        Label inkConsumptionHeadLabel = new Label("Расход (литр/месяц): ");
        _inkNameLabel = new Label();
        _inkColorLabel = new Label();
        _inkMachineLabel = new Label();
        _inkVolumeLabel = new Label();
        _inkQuantityLabel = new Label();
        _inkConsumptionLabel = new Label();
        HBox radioBtnsHBox = new HBox();
        _consumptionRadioBtn = new RadioButton("Расход");
        _arrivalRadioBtn = new RadioButton("Приход");
        _allRadioBtn = new RadioButton("Расход и Приход");
        _radioBtnsGroup = new ToggleGroup();

        setInkAccountingsTableView();

        _consumptionRadioBtn.setToggleGroup(_radioBtnsGroup);
        _consumptionRadioBtn.setOnAction(event ->
        {
            if (_inksListView.getSelectionModel().getSelectedItem() != null)
            {
                Ink ink = _inksListView.getSelectionModel().getSelectedItem();
                setItemsInkAccountingsTableView(ink);
            }
        });

        _arrivalRadioBtn.setToggleGroup(_radioBtnsGroup);
        _arrivalRadioBtn.setOnAction(event ->
        {
            if (_inksListView.getSelectionModel().getSelectedItem() != null)
            {
                Ink ink = _inksListView.getSelectionModel().getSelectedItem();
                setItemsInkAccountingsTableView(ink);
            }
        });

        _allRadioBtn.setToggleGroup(_radioBtnsGroup);
        _allRadioBtn.setOnAction(event ->
        {
            if (_inksListView.getSelectionModel().getSelectedItem() != null)
            {
                Ink ink = _inksListView.getSelectionModel().getSelectedItem();
                setItemsInkAccountingsTableView(ink);
            }
        });
        _allRadioBtn.setSelected(true);

        radioBtnsHBox.setAlignment(Pos.CENTER);
        radioBtnsHBox.setSpacing(10);
        radioBtnsHBox.getChildren().addAll(
                _allRadioBtn,
                _consumptionRadioBtn,
                _arrivalRadioBtn);

        inksTableViewVBox.getChildren().addAll(_inkAccountingsTableView);

        inkAccountingVBox.setAlignment(Pos.CENTER);
        inkAccountingVBox.setPadding(new Insets(10));
        inkAccountingVBox.setSpacing(10);
        inkAccountingVBox.getChildren().addAll(radioBtnsHBox, inksTableViewVBox);

        inkAccountingTitledPane.setCollapsible(false);
        inkAccountingTitledPane.setExpanded(true);
        inkAccountingTitledPane.setText("Учёт чернил");
        inkAccountingTitledPane.setAlignment(Pos.TOP_CENTER);
        inkAccountingTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        inkAccountingTitledPane.setContent(inkAccountingVBox);

        inkDataGridPane.add(inkNameHeadLabel, 0, 0);
        inkDataGridPane.add(inkColorHeadLabel, 0, 1);
        inkDataGridPane.add(inkMachineHeadLabel, 0, 2);
        inkDataGridPane.add(inkVolumeHeadLabel, 0, 3);
        inkDataGridPane.add(inkQuantityHeadLabel, 0, 4);
        inkDataGridPane.add(inkConsumptionHeadLabel, 0, 5);
        inkDataGridPane.add(_inkNameLabel, 1, 0);
        inkDataGridPane.add(_inkColorLabel, 1, 1);
        inkDataGridPane.add(_inkMachineLabel, 1, 2);
        inkDataGridPane.add(_inkVolumeLabel, 1, 3);
        inkDataGridPane.add(_inkQuantityLabel, 1, 4);
        inkDataGridPane.add(_inkConsumptionLabel, 1, 5);
        inkDataGridPane.setVgap(10);
        inkDataGridPane.setHgap(5);
        inkDataGridPane.alignmentProperty().set(Pos.CENTER);
        GridPane.setHalignment(inkNameHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(inkColorHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(inkMachineHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(inkVolumeHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(inkQuantityHeadLabel, HPos.RIGHT);
        GridPane.setHalignment(inkConsumptionHeadLabel, HPos.RIGHT);
        GridPane.setValignment(_inkNameLabel, VPos.CENTER);
        GridPane.setValignment(_inkColorLabel, VPos.CENTER);
        GridPane.setValignment(_inkMachineLabel, VPos.CENTER);
        GridPane.setValignment(_inkVolumeLabel, VPos.CENTER);
        GridPane.setValignment(_inkQuantityLabel, VPos.CENTER);
        GridPane.setValignment(_inkConsumptionLabel, VPos.CENTER);
        GridPane.setHalignment(_inkNameLabel, HPos.LEFT);
        GridPane.setHalignment(_inkColorLabel, HPos.LEFT);
        GridPane.setHalignment(_inkMachineLabel, HPos.LEFT);
        GridPane.setHalignment(_inkVolumeLabel, HPos.LEFT);
        GridPane.setHalignment(_inkQuantityLabel, HPos.LEFT);
        GridPane.setHalignment(_inkConsumptionLabel, HPos.LEFT);
        GridPane.setValignment(inkNameHeadLabel, VPos.CENTER);
        GridPane.setValignment(inkColorHeadLabel, VPos.CENTER);
        GridPane.setValignment(inkMachineHeadLabel, VPos.CENTER);
        GridPane.setValignment(inkVolumeHeadLabel, VPos.CENTER);
        GridPane.setValignment(inkQuantityHeadLabel, VPos.CENTER);
        GridPane.setValignment(inkConsumptionHeadLabel, VPos.CENTER);

        inkDataTitledPane.setCollapsible(false);
        inkDataTitledPane.setExpanded(true);
        inkDataTitledPane.setText("Данные по чернилам");
        inkDataTitledPane.setAlignment(Pos.CENTER);
        inkDataTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        inkDataTitledPane.setContent(inkDataGridPane);

        _centerVBox.setPadding(new Insets(15));
        _centerVBox.setSpacing(15);
        _centerVBox.setStyle("-fx-background-color: #f0f8ff");
        _centerVBox.getChildren().addAll(inkAccountingTitledPane, inkDataTitledPane);

        return _centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button closeButton = new Button("Закрыть");
        Button calcConsumptionBtn = new Button("Вычесть расход");

        calcConsumptionBtn.setVisible(false);
        calcConsumptionBtn.setOnAction(event ->
        {
            if (_inksListView.getSelectionModel().getSelectedItem() != null)
            {
                Ink ink = _inksListView.getSelectionModel().getSelectedItem();
                calculateInkConsumption(ink);
                setInkData(ink);
            }
        });
        if (get_currAccount() == 7) calcConsumptionBtn.setVisible(true);

        closeButton.setPrefWidth(80);
        closeButton.setOnAction(event -> _inksFormStage.close());

        bottomAnchorPane.getChildren().addAll(calcConsumptionBtn, closeButton);
        AnchorPane.setTopAnchor(calcConsumptionBtn, 5.0);
        AnchorPane.setLeftAnchor(calcConsumptionBtn, 5.0);
        AnchorPane.setBottomAnchor(calcConsumptionBtn, 5.0);
        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomAnchorPane);

        return bottomVBox;
    }

    private InkAccounting getLastInkAccByColorAndMachine(String color, int machine)
    {
        InkAccounting lastAccounting = null;
        for (InkAccounting accounting : _inkAccountingsArrayList)
        {
            if (accounting.get_procedure().equals(MaterialsForm.CONSUMPTION))
            {
                Ink ink = Finder.getInk(accounting.get_ink());

                if (ink.get_color() != null && ink.get_color().equals(color) && ink.get_machine() == machine)
                    lastAccounting = accounting;
            }
        }

        if (lastAccounting != null)
            System.out.println("get last accounting: " + Finder.getInk(lastAccounting.get_ink()).get_name());

        return lastAccounting;
    }

    private Integer addInkAmount()
    {
        int amount = 0;
        TextInputDialog inputAmountDialog = new TextInputDialog();
        inputAmountDialog.setTitle("Количество чернил в таре");
        inputAmountDialog.setHeaderText("Введите количество чернил в таре (шт.)");
        inputAmountDialog.setContentText("Количество (шт.): ");
        inputAmountDialog.graphicProperty().set(null);
        inputAmountDialog.getEditor().textProperty().addListener(
                MaterialsForm.getChangeListener(inputAmountDialog.getEditor()));
        Optional<String> result = inputAmountDialog.showAndWait();
        if (result.isPresent())
            amount = Integer.parseInt(inputAmountDialog.getEditor().getText());

        return amount;
    }

    private void setInkData(Ink ink)
    {
        _inkNameLabel.setText(ink.get_name());
        _inkColorLabel.setText(ink.get_color());
        _inkMachineLabel.setText(Finder.getMachine(ink.get_machine()).get_name());
        _inkVolumeLabel.setText(String.valueOf(ink.get_volume()));
        _inkQuantityLabel.setText(String.valueOf(ink.get_quantity()));
        System.out.println(String.valueOf(ink.get_consumption()));
        if (String.valueOf(ink.get_consumption()).toLowerCase().equals("infinity") || ink.get_consumption() == 0.0)
            _inkConsumptionLabel.setText("Мало данных");
        else
            _inkConsumptionLabel.setText(String.format("%.2f", ink.get_consumption()));
    }

    private void clearInkData()
    {
        _inkNameLabel.setText("");
        _inkColorLabel.setText("");
        _inkMachineLabel.setText("");
        _inkVolumeLabel.setText("");
        _inkQuantityLabel.setText("");
        _inkConsumptionLabel.setText("");
    }

    private float calculateInkConsumption(Ink ink)
    {
        long dayQuantity = 0;
        float literQuantity = 0;
        float consumption = 0;
        int jars = 0;
        ArrayList<InkAccounting> accountingsCurrInkList = new ArrayList<>();


        for (InkAccounting accounting : DataBaseStorehouse.getInkAccountingList())
        {
            if (accounting.get_ink() == ink.get_id() && accounting.get_procedure().equals(MaterialsForm.CONSUMPTION))
                accountingsCurrInkList.add(accounting);
        }

        for (InkAccounting accountingCurrInk : accountingsCurrInkList)
        {
            if (accountingCurrInk.get_dateTimeClose() != null)
            {
                ++jars;
                dayQuantity += ChronoUnit.DAYS.between(accountingCurrInk.get_dateTimeOpen(), accountingCurrInk.get_dateTimeClose());
            }
        }

        //literQuantity = accountingsCurrInkList.size() * ink.get_volume();
        literQuantity = jars * ink.get_volume();
        consumption = literQuantity / dayQuantity;
        System.out.println("Количество дней: " + dayQuantity);
        System.out.println("Количество использованных банок: " + jars);
        System.out.println("Количество литров: " + literQuantity);
        System.out.println("Расход (литр/день): " + consumption);
        System.out.println("Расход(литр/месяц):" + consumption * 30);

        return consumption * 30;
    }

    private void setInkAccountingsTableView()
    {
        _inkAccountingsTableView = new TableView<>();
        String color = "Цвет: ";
        if (_inksListView.getSelectionModel().getSelectedItem() != null)
            color += _inksListView.getSelectionModel().getSelectedItem().get_color();
        TableColumn<InkAccounting, String> colorCol = new TableColumn<>(color);
        TableColumn<InkAccounting, LocalDateTime> dateCol = new TableColumn<>("Дата");
        TableColumn<InkAccounting, String> procedureCol = new TableColumn<>("Процедура");
        TableColumn<InkAccounting, Integer> quantityCol = new TableColumn<>("Количество");

        dateCol.setStyle("-fx-alignment: CENTER;");
        dateCol.prefWidthProperty().bind(_inksFormScene.widthProperty().multiply(0.202));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("_dateTime"));
        dateCol.setCellFactory(tc -> new TableCell<InkAccounting, LocalDateTime>()
        {
            @Override
            protected void updateItem(LocalDateTime date, boolean empty)
            {
                super.updateItem(date, empty);
                if (empty)
                    setText(null);
                else
                {
                    String dateToText = "";
                    if (date.isBefore(LocalDateTime.of(2020, Month.OCTOBER, 15, 0, 0)))
                        dateToText = MainInterface._formatter.format(date);
                    else
                        dateToText = MainInterface._formatter.format(date) + " " +
                                MainInterface._formatterTime.format(date);
                    setText(dateToText);
                }
            }
        });

        procedureCol.setStyle("-fx-alignment: CENTER;");
        procedureCol.prefWidthProperty().bind(_inksFormScene.widthProperty().multiply(0.18));
        procedureCol.setCellValueFactory(new PropertyValueFactory<>("_procedure"));

        quantityCol.setStyle("-fx-alignment: CENTER;");
        quantityCol.prefWidthProperty().bind(_inksFormScene.widthProperty().multiply(0.10));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("_quantity"));

        colorCol.getColumns().addAll(dateCol, procedureCol, quantityCol);

        _inkAccountingsTableView.getColumns().addAll(colorCol);
        _inkAccountingsTableView.setPlaceholder(new Text("Выберите принтер для отображения данных по чернилам"));
    }

    private void setItemsInkAccountingsTableView(Ink ink)
    {
        if (!_inkAccountingsTableView.getItems().isEmpty())
            _inkAccountingsTableView.getItems().clear();
        for (InkAccounting accounting : _inkAccountingsArrayList)
        {
            Ink accountingInk = Finder.getInk(accounting.get_ink());
            if (accountingInk.get_id() == ink.get_id())
            {
                if (_allRadioBtn.selectedProperty().get())
                    _inkAccountingsTableView.getItems().add(accounting);

                if (_arrivalRadioBtn.selectedProperty().get())
                    if (accounting.get_procedure().equals(MaterialsForm.INCOMING))
                        _inkAccountingsTableView.getItems().add(accounting);

                if (_consumptionRadioBtn.selectedProperty().get())
                    if (accounting.get_procedure().equals(MaterialsForm.CONSUMPTION))
                        _inkAccountingsTableView.getItems().add(accounting);
            }
        }
        _inkAccountingsTableView.getColumns().get(0).setText("Цвет: " + ink.get_color());
        if (_inkAccountingsTableView.getItems().isEmpty())
            if (_machinesListView.getSelectionModel().getSelectedItem() != null)
                if (_inksListView.getSelectionModel().getSelectedItem() != null)
                    _inkAccountingsTableView.setPlaceholder(new Text("Данные по выбранным чернилам отсутствуют"));
                else
                    _inkAccountingsTableView.setPlaceholder(new Text("Выберите чернила для отображения данных"));
            else
                _inkAccountingsTableView.setPlaceholder(
                        new Text("Выберите принтер для отображения данных по чернилам"));
    }

    private void setInksListView(Machine machine)
    {
        if (!_inkAccountingsTableView.getItems().isEmpty())
            _inkAccountingsTableView.getItems().clear();
        if (!_inksListView.getItems().isEmpty())
            _inksListView.getItems().clear();
        for (Ink ink : _activeInksArrayList)
            if (ink.get_machine() == machine.get_id())
                _inksListView.getItems().add(ink);
    }

    private boolean checkMachineInInks(final int machineId)
    {
        for (Ink ink : Finder.get_allInksArrayList())
            if (ink.get_machine() == machineId)
                return true;

        return false;
    }

    private boolean checkMachineInActiveInks(final int machineId)
    {
        for (Ink ink : Finder.get_allInksArrayList())
            if (ink.get_machine() == machineId && ink.is_active())
                return true;

        return false;
    }

    private boolean checkInkInAccountings(final int inkId)
    {
        for (InkAccounting accounting : _inkAccountingsArrayList)
            if (accounting.get_ink() == inkId)
                return true;

        return false;
    }

    void set_currAccount(int currAccount){ _currAccount = currAccount; }

    public int get_currAccount() { return _currAccount; }

    /*static Machine getMachine(int machineId)
    {
        for(Machine machine : _allMachinesArrayList)
        {
            System.out.println("machine =" + machine.get_name());
            System.out.println("id: " + machine.get_id() + " == machineId: " + machineId);
            if (machine.get_id() == machineId)
                return machine;
        }

        return new Machine();
    }

    */
}
