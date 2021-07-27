import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;


public class MontagesForm
{
    private Stage _montagesFormStage;
    private Scene _montagesFormScene;
    private BorderPane _montagesFormBorderPane;
    private Button _cancelButton;
    private Button _addMontageButton;
    private DatePicker _datePicker;
    private DatePickerSkin _datePickerSkin;
    private TableView<Montage> _montagesInDayTableView = new TableView<>();
    private ArrayList<Montage> _allMontages;
    private TextField _objectTextField = new TextField();
    private TextArea _descriptionTextArea = new TextArea();
    private TextField _contactPersonTextField = new TextField();
    private ContextMenu _montagesContextMenu = new ContextMenu();
    private MenuItem _addMontageMenuItem;
    private MenuItem _editMontageMenuItem;
    private MenuItem _removeMontageMenuItem;
    private final Font titlePanesFont = Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11);

    MontagesForm()
    {
        _allMontages = DatabaseMontages.getMontagesList();
        _objectTextField.setEditable(false);
        _contactPersonTextField.setEditable(false);
        _descriptionTextArea.setEditable(false);
        _descriptionTextArea.setWrapText(true);
    }

    void show()
    {
        _montagesFormStage = new Stage();
        _montagesFormBorderPane = new BorderPane();
        _montagesFormScene = new Scene(_montagesFormBorderPane, 800, 600);

        _montagesFormBorderPane.setTop(getTop());
        _montagesFormBorderPane.setCenter(getCenter());
        _montagesFormBorderPane.setBottom(getBottom());

        set_montagesContextMenu();

        _montagesFormStage.setTitle("Монтажи");
        _montagesFormStage.getIcons().add(MainInterface.getIconLogo());
        _montagesFormStage.getIcons().add(MainInterface.getIconLogo());
        _montagesFormStage.setScene(_montagesFormScene);
        _montagesFormStage.show();
    }

    private VBox getTop()
    {
        VBox topVBox = new VBox();
        VBox datePickerVBox = new VBox();

        _datePicker = new DatePicker(LocalDate.now());
        _datePicker.setShowWeekNumbers(false);
        _datePickerSkin = new DatePickerSkin(_datePicker);
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>()
        {
            @Override
            public DateCell call(final DatePicker datePicker)
            {
                return new DateCell()
                {
                    @Override public void updateItem(LocalDate item, boolean empty)
                    {
                        super.updateItem(item, empty);

                        //if flag is true and date is within range, set style
                        for (Montage montage : _allMontages)
                        {
                            if(item.equals(montage.get_date().toLocalDate()))
                            {
                                if(item.isBefore(LocalDate.now()))
                                    this.setStyle("-fx-background-color: #ffc0cb;");
                                else
                                    this.setStyle("-fx-background-color: #3cb380");

                                if(montage.get_measure())
                                {
                                    //обозначить ячейку замером
                                    //setText((item.getDayOfMonth()) + " Замер");
                                }

                                break;
                            }

                        }

                        setOnMouseClicked(event ->
                        {
                            if(event.getButton() == MouseButton.PRIMARY)
                            {
                                if(super.getStyle().equals("-fx-background-color: #3cb380"))
                                {
                                    setStyle("-fx-background-color: #006400;" +
                                            "-fx-text-background-color: #ffffff");
                                } else if (super.getStyle().equals("-fx-background-color: #006400;" +
                                        "-fx-text-background-color: #ffffff"))
                                    setStyle("-fx-background-color: #006400;" +
                                            "-fx-text-background-color: #ffffff");
                                else
                                    setStyle(null);
                                clickOnDate(item);
                            }
                            if(event.getButton() == MouseButton.SECONDARY)
                            {
                                if(item.toString().equals(_datePicker.getValue().toString()))
                                {
                                    setOnContextMenuRequested(event1 ->
                                    {
                                        if(getItem() != null )
                                        {
                                            _montagesContextMenu.getItems().clear();
                                            _montagesContextMenu.getItems().addAll(_addMontageMenuItem);
                                            _montagesContextMenu.show(this, event1.getScreenX() + 10, event1.getScreenY() + 5);
                                        }
                                    });
                                }
                            }
                        });
                    }
                };
            }
        };
        _datePicker.setDayCellFactory(dayCellFactory);


        datePickerVBox.setPadding(new Insets(15));
        datePickerVBox.setSpacing(15);
        datePickerVBox.getChildren().addAll(_datePickerSkin.getPopupContent());

        topVBox.getChildren().addAll(datePickerVBox, new Separator());

        return topVBox;
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        HBox montagesHBox = new HBox();
        VBox montagesLeftVBox = new VBox();
        VBox montagesRightVBox = new VBox();
        Separator separator = new Separator();
        separator.setOrientation(Orientation.VERTICAL);
        TitledPane objectTitledPane = new TitledPane();
        TitledPane descriptionTitledPane = new TitledPane();
        TitledPane contactPersonTitledPane = new TitledPane();

        setMontagesTableView();

        objectTitledPane.setFont(titlePanesFont);
        objectTitledPane.setText("Объект монтажа");
        objectTitledPane.setCollapsible(false);
        objectTitledPane.setExpanded(true);
        objectTitledPane.setContent(_objectTextField);

        contactPersonTitledPane.setFont(titlePanesFont);
        contactPersonTitledPane.setText("Контактное лицо");
        contactPersonTitledPane.setCollapsible(false);
        contactPersonTitledPane.setExpanded(true);
        contactPersonTitledPane.setContent(_contactPersonTextField);

        descriptionTitledPane.setFont(titlePanesFont);
        descriptionTitledPane.setText("Описание");
        descriptionTitledPane.setCollapsible(false);
        descriptionTitledPane.setExpanded(true);
        descriptionTitledPane.setContent(_descriptionTextArea);

        montagesRightVBox.setSpacing(10);
        montagesRightVBox.getChildren().addAll(
                objectTitledPane,
                contactPersonTitledPane,
                descriptionTitledPane);

        montagesLeftVBox.getChildren().addAll(_montagesInDayTableView);

        montagesHBox.setSpacing(5);
        montagesHBox.getChildren().addAll(montagesLeftVBox, montagesRightVBox);

        centerVBox.getChildren().addAll(montagesHBox);
        centerVBox.setSpacing(15);
        centerVBox.setPadding(new Insets(10));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        return centerVBox;
    }

    private VBox getBottom()
    {
        VBox bottomVBox = new VBox();
        HBox buttonsHBox = new HBox();
        _cancelButton = new Button("Закрыть");
        _addMontageButton = new Button("Добавить монтаж");

        _cancelButton.setOnAction(event -> _montagesFormStage.close());
        _addMontageButton.setOnAction(event ->
        {

        });

        buttonsHBox.setPadding(new Insets(5));
        buttonsHBox.getChildren().addAll(_cancelButton);

        bottomVBox.getChildren().addAll(new Separator(), buttonsHBox);
        return bottomVBox;
    }

    private void setMontagesTableView()
    {
        TableColumn<Montage, LocalTime> timeCol = new TableColumn<>("Время");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("_time"));
        timeCol.setStyle("-fx-alignment: CENTER;");
        timeCol.prefWidthProperty().bind(_montagesFormScene.widthProperty().multiply(0.06));

        TableColumn<Montage, String> objectCol = new TableColumn<>("Объект");
        objectCol.prefWidthProperty().bind(_montagesFormScene.widthProperty().multiply(0.23));
        objectCol.setCellValueFactory(new PropertyValueFactory<>("_object"));

        TableColumn<Montage, Boolean> measureCol = new TableColumn<>("Замер");
        measureCol.prefWidthProperty().bind(_montagesFormScene.widthProperty().multiply(0.06));
        measureCol.setCellFactory(param ->
        {
            CheckBoxTableCell<Montage, Boolean> cell = new CheckBoxTableCell<>();
            cell.setAlignment(Pos.CENTER);
            return cell;
        });
        measureCol.setCellValueFactory(param ->
        {
            Montage montage = param.getValue();

            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(montage.get_measure());

            booleanProp.addListener((observable, oldValue, newValue) -> montage.set_measure(newValue));
            return booleanProp;
        });

        _montagesInDayTableView.setRowFactory(new Callback<TableView<Montage>, TableRow<Montage>>()
        {
            @Override
            public TableRow<Montage> call(TableView<Montage> tableView)
            {
                TableRow<Montage> row = new TableRow<Montage>()
                {
                    @Override
                    protected void updateItem(Montage montage, boolean empty)
                    {
                        super.updateItem(montage, empty);
                        this.setFocused(true);

                    }
                };

                row.setOnMouseClicked(event ->
                {
                    if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                            && event.getClickCount() == 1)
                    {
                        clickOnMontage();
                    }

                    if(!row.isEmpty() && event.getButton() == MouseButton.SECONDARY)
                    {
                        row.setOnContextMenuRequested(event1 ->
                        {
                            if(row.getItem()!=null)
                            {
                               _montagesContextMenu.getItems().clear();
                               _montagesContextMenu.getItems().addAll(_editMontageMenuItem, _removeMontageMenuItem);
                               _montagesContextMenu.show(row, event1.getScreenX() + 10, event1.getScreenY() + 5);
                            }
                        });
                    }
                });
                return row;
            }
        });
        _montagesInDayTableView.setPlaceholder(new Text("Монтажи отсутствуют"));
        _montagesInDayTableView.getColumns().addAll(timeCol, objectCol, measureCol);
    }

    private void set_montagesContextMenu()
    {

        _addMontageMenuItem = new MenuItem("Добавить монтаж");
        _editMontageMenuItem = new MenuItem("Редактировать монтаж");
        _removeMontageMenuItem = new MenuItem("Удалить монтаж");

        _addMontageMenuItem.setOnAction(event ->
        {
            if (_datePicker.getValue().isBefore(LocalDate.now()))
            {
                if (MainInterface.getAlertAskConfirmationDialog("Запланировать монтаж уже прошедшую дату?"))
                {
                    addMontage();
                }
            } else if(_datePicker.getValue().getDayOfWeek() == DayOfWeek.SUNDAY)
            {
                if (MainInterface.getAlertAskConfirmationDialog("Запланировать монтаж на воскресенье?"))
                {
                    addMontage();
                }
            } else
                addMontage();
        });

        _editMontageMenuItem.setOnAction(event ->
        {
            if(_montagesInDayTableView.getSelectionModel().getSelectedItem() != null)
                editMontage();
        });

        _removeMontageMenuItem.setOnAction(event ->
        {
            if(_montagesInDayTableView.getSelectionModel().getSelectedItem() != null)
                removeMontage();
        });


        //_montagesContextMenu.getItems().addAll(_addMontageMenuItem);
    }

    private void clickOnDate(LocalDate date)
    {
        clearTextEdits();
        _montagesFormBorderPane.requestFocus();
        _montagesInDayTableView.getItems().clear();
        //_montagesInDayTableView.getFocusModel().
        for (Montage montage : _allMontages)
        {
            if(montage.get_date().toLocalDate().equals(date))
                _montagesInDayTableView.getItems().add(montage);
        }
    }

    private void clickOnMontage()
    {
        if (_montagesInDayTableView.getSelectionModel().getSelectedItem() != null)
        {
            Montage montage = _montagesInDayTableView.getSelectionModel().getSelectedItem();
            _objectTextField.setText(montage.get_object());
            _contactPersonTextField.setText(montage.get_contactPerson());
            _descriptionTextArea.setText(montage.get_description());
        }
    }

    private void addMontage()
    {
        MontagesDialog montagesDialog = new MontagesDialog();
        montagesDialog.showAndWait(_montagesFormStage);
        if (montagesDialog._ok)
        {
            montagesDialog._montage.set_date(Date.valueOf(_datePicker.getValue()));
            if (DatabaseMontages.addMontage(montagesDialog._montage))
            {
                montagesDialog._montage.set_id(DatabaseMontages.getLastId(DatabaseMontages.MONTAGES_TABLE));
                _allMontages.add(montagesDialog._montage);
            }
            _datePickerSkin = new DatePickerSkin(_datePicker);
            clickOnDate(_datePicker.getValue());
        }
    }

    private void editMontage()
    {
        if(_montagesInDayTableView.getSelectionModel().getSelectedItem() != null)
        {
            Montage editMontage = _montagesInDayTableView.getSelectionModel().getSelectedItem();
            MontagesDialog montagesDialog = new MontagesDialog(editMontage);
            final int indexInArray = _allMontages.indexOf(editMontage);
            montagesDialog.showAndWait(_montagesFormStage);
            if (montagesDialog._ok)
            {
                montagesDialog._montage.set_date(Date.valueOf(_datePicker.getValue()));
                if (DatabaseMontages.editMontage(montagesDialog._montage))
                {
                    _allMontages.set(indexInArray, montagesDialog._montage);
                }
                _datePickerSkin = new DatePickerSkin(_datePicker);
                clickOnDate(_datePicker.getValue());
            }
        }
    }

    private void removeMontage()
    {
        Montage montage = _montagesInDayTableView.getSelectionModel().getSelectedItem();
        if(MainInterface.getAlertAskConfirmationDialog("Удалить выбранный монтаж?"))
        {
            if (DatabaseMontages.removeObject(montage.get_id(), DatabaseMontages.MONTAGES_TABLE))
            {
                _montagesInDayTableView.getItems().remove(montage);
                _allMontages.remove(montage);
                clearTextEdits();
                _datePickerSkin = new DatePickerSkin(_datePicker);
                clickOnDate(_datePicker.getValue());
            }
        }
    }

    private void clearTextEdits()
    {
        _objectTextField.clear();
        _contactPersonTextField.clear();
        _descriptionTextArea.clear();
    }
}
