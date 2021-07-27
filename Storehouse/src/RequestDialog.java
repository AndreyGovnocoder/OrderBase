import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.Comparator;

public class RequestDialog
{
    private Stage _stage;
    private BorderPane _mainBorderPane;
    private VBox _centerVBox;
    private Button _addSaveButton = new Button("Добавить");
    private ComboBox<RequestsKind> _kindComboBox;
    private ComboBox<Material> _materialComboBox;
    private ComboBox<Ink> _inkComboBox;
    private ComboBox<Led> _ledComboBox;
    private ComboBox<PowerModule> _powerModuleComboBox;
    private ComboBox<Polygraphy> _polygraphyComboBox;
    private ComboBox<Construction> _constructionComboBox;
    private TextArea _descriptionTextArea = new TextArea();
    TitledPane _descriptionTitledPane;
    private VBox _cBoxesVBox;
    private boolean _edit = false;
    private Request _request;
    private boolean _ok = false;
    private int _kind = -1;
    private int _valueId = -1;

    RequestDialog()
    {}

    RequestDialog(Request request)
    {
        _request = request;
        _edit = true;
        _addSaveButton.setText("Сохранить");
        _kind = request.get_kind();
        _valueId = request.get_valueId();
    }

    RequestDialog(int kind, int valueId)
    {
        _kind = kind;
        _valueId = valueId;
    }

    public void showAndWait(Stage primaryStage)
    {
        _stage = new Stage();
        _mainBorderPane = new BorderPane();
        Scene scene = new Scene(_mainBorderPane);
        _mainBorderPane.setCenter(getCenter());
        _mainBorderPane.setBottom(getBottom());
        if (_edit)
        {
            _stage.setTitle("Редактирование заявки");
            setData();
        }
        else
            _stage.setTitle("Создание заявки");
        _stage.initModality(Modality.WINDOW_MODAL);
        _stage.initOwner(primaryStage);
        _stage.setScene(scene);
        _stage.getIcons().add(MainInterface.getIconLogo());
        System.out.println("in showandwait");
        //_descriptionTextArea.requestFocus();
        _descriptionTitledPane.getContent().requestFocus();
        _stage.showAndWait();
    }

    private VBox getCenter()
    {
        _centerVBox = new VBox();
        _cBoxesVBox = new VBox();
        _descriptionTitledPane = new TitledPane();

        _descriptionTitledPane.setText("Примечание");
        _descriptionTitledPane.setFont(Font.font("System", FontWeight.BOLD, FontPosture.REGULAR, 11));
        _descriptionTitledPane.setCollapsible(false);
        _descriptionTitledPane.setExpanded(true);
        _descriptionTitledPane.setContent(_descriptionTextArea);

        _cBoxesVBox.setAlignment(Pos.CENTER);
        _centerVBox.getChildren().addAll(_kindComboBox, _cBoxesVBox, _descriptionTextArea);

        if (_kind != -1)
            _kindComboBox.getSelectionModel().select(Finder.getRequestKind(_kind));

        _centerVBox.setPadding(new Insets(20));
        _centerVBox.setSpacing(10);
        _centerVBox.setStyle("-fx-background-color: #f0f8ff");
        _centerVBox.setAlignment(Pos.CENTER);
        return _centerVBox;
    }

    private VBox getBottom()
    {
        System.out.println("in getBottom");
        VBox bottomVBox = new VBox();
        AnchorPane buttonsPane = new AnchorPane();
        Button cancelButton = new Button("Отмена");

        _addSaveButton.setOnAction(event ->
        {
            if (check())
            {
                if (!_edit)
                    _request = new Request();
                _request.set_description(_descriptionTextArea.getText());
                _request.set_kind(_kindComboBox.getSelectionModel().getSelectedItem().get_id());
                switch (_request.get_kind())
                {
                    case 1:
                        _request.set_valueId(_materialComboBox.getSelectionModel().getSelectedItem().get_id());
                        break;
                    case 2:
                        _request.set_valueId(_inkComboBox.getSelectionModel().getSelectedItem().get_id());
                        break;
                    case 3:
                        _request.set_valueId(_ledComboBox.getSelectionModel().getSelectedItem().get_id());
                        break;
                    case 4:
                        _request.set_valueId(_powerModuleComboBox.getSelectionModel().getSelectedItem().get_id());
                        break;
                    case 5:
                        _request.set_valueId(_polygraphyComboBox.getSelectionModel().getSelectedItem().get_id());
                        break;
                    case 6:
                        _request.set_valueId(_constructionComboBox.getSelectionModel().getSelectedItem().get_id());
                        break;
                }

                if (_edit)
                {
                    if (save_requestInDB())
                        _stage.close();
                }
                else
                {
                    _request.set_status(1);
                    if (add_requestToDB())
                        _stage.close();
                }
            }
        });


        cancelButton.setOnAction(event -> { _stage.close(); });

        buttonsPane.getChildren().addAll(_addSaveButton, cancelButton);
        AnchorPane.setTopAnchor(_addSaveButton,5.0);
        AnchorPane.setLeftAnchor(_addSaveButton,5.0);
        AnchorPane.setBottomAnchor(_addSaveButton,5.0);
        AnchorPane.setTopAnchor(cancelButton,5.0);
        AnchorPane.setRightAnchor(cancelButton,5.0);
        AnchorPane.setBottomAnchor(cancelButton,5.0);

        bottomVBox.getChildren().addAll(new Separator(), buttonsPane);
        return bottomVBox;
    }

    private boolean add_requestToDB()
    {
        if (DataBaseStorehouse.addRequest(_request))
        {
            _ok = true;
            _request.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.REQUESTS_TABLE));
            MainInterface.getAlertInformationDialog("Заявка успешно добавлена");
            Finder.get_allRequests().add(_request);
            return _ok;
        }
        else
        {
            MainInterface.getAlertErrorDialog("Ошибка базы данных");
            return false;
        }
    }

    private boolean save_requestInDB()
    {
        final int indexInArray = Finder.getRequestIndexOf(_request.get_id());
        if (DataBaseStorehouse.editRequest(_request))
        {
            _ok = true;
            Finder.get_allRequests().set(indexInArray, _request);
            MainInterface.getAlertInformationDialog("Изменения успешно сохранены");
            return _ok;
        }
        else
        {
            MainInterface.getAlertErrorDialog("Ошибка базы данных");
            return false;
        }
    }

    private void setData()
    {
        final int kind = _request.get_kind();
        _kindComboBox.setValue(Finder.getRequestKind(kind));
        switch (kind)
        {
            case 1:
                _materialComboBox.setValue(Finder.getMaterial(_request.get_valueId()));
                break;
            case 2:
                _inkComboBox.setValue(Finder.getInk(_request.get_valueId()));
                break;
            case 3:
                _ledComboBox.setValue(Finder.getLed(_request.get_valueId()));
                break;
            case 4:
                _powerModuleComboBox.setValue(Finder.getPowerModule(_request.get_valueId()));
                break;
            case 5:
                _polygraphyComboBox.setValue(Finder.getPolygraphy(_request.get_valueId()));
                break;
            case 6:
                _constructionComboBox.setValue(Finder.getConstruction(_request.get_valueId()));
                break;
        }

        if (_request.get_description() != null)
            _descriptionTextArea.setText(_request.get_description());
    }

    void set_kindComboBox()
    {
        _kindComboBox = new ComboBox<RequestsKind>(
                FXCollections.observableArrayList(Finder.get_requestKindsList()));

        _kindComboBox.getSelectionModel().selectedItemProperty().addListener((observable,oldValue,newValue)->
        {
            if (oldValue == null || oldValue == newValue)
                set_valueComboBox(newValue.get_id(), _valueId);
            else
                set_valueComboBox(newValue.get_id(), -1);
        });
    }

    private boolean check()
    {
        if (_kindComboBox.getSelectionModel().getSelectedIndex() == -1)
        {
            MainInterface.getAlertWarningDialog("Необходимо выбрать вид и название объекта заявки");
            return false;
        }
        else
        {
            boolean check = true;
            switch (_kindComboBox.getSelectionModel().getSelectedItem().get_id())
            {
                case 1:
                    if (_materialComboBox.getSelectionModel().getSelectedIndex() == -1)
                        check = false;
                    break;
                case 2:
                    if (_inkComboBox.getSelectionModel().getSelectedIndex() == -1)
                        check = false;
                    break;
                case 3:
                    if (_ledComboBox.getSelectionModel().getSelectedIndex() == -1)
                        check = false;
                    break;
                case 4:
                    if (_powerModuleComboBox.getSelectionModel().getSelectedIndex() == -1)
                        check = false;
                    break;
                case 5:
                    if (_polygraphyComboBox.getSelectionModel().getSelectedIndex() == -1)
                        check = false;
                    break;
                case 6:
                    if (_constructionComboBox.getSelectionModel().getSelectedIndex() == -1)
                        check = false;
                    break;
            }
            if (!check)
            {
                MainInterface.getAlertWarningDialog("Необходимо выбрать вид и название объекта заявки");
                return false;
            }
        }
        return true;
    }


    private void set_valueComboBox(final int currKind, final int valueId)
    {
        switch (currKind)
        {
            case 1:
                System.out.println("case 1");
                _cBoxesVBox.getChildren().clear();
                ArrayList<Material> materials = new ArrayList<>();
                for (Material material : Finder.get_allMaterialsList())
                {
                    if (material.is_active())
                        materials.add(material);
                }
                materials.sort(Comparator.comparingInt(Material::get_kind));

                _materialComboBox = new ComboBox<Material>(FXCollections.observableArrayList(materials));
                for (Material material : materials)
                {
                    if (material.get_id() == valueId)
                    {
                        _materialComboBox.getSelectionModel().select(material);
                        break;
                    }
                }
                _cBoxesVBox.getChildren().addAll(_materialComboBox);
                break;

            case 2:
                System.out.println("case 2");
                _cBoxesVBox.getChildren().clear();
                ArrayList<Ink> inks = new ArrayList<>();
                for (Ink ink : Finder.get_allInksArrayList())
                {
                    if (ink.is_active())
                        inks.add(ink);
                }
                inks.sort(Comparator.comparingInt(Ink::get_machine));
                _inkComboBox = new ComboBox<Ink>(FXCollections.observableArrayList(inks));
                for (Ink ink : inks)
                {
                    if (ink.get_id() == valueId)
                    {
                        _inkComboBox.getSelectionModel().select(ink);
                        break;
                    }
                }
                _cBoxesVBox.getChildren().addAll(_inkComboBox);
                break;

            case 3:
                System.out.println("case 3");
                _cBoxesVBox.getChildren().clear();
                ArrayList<Led> leds = new ArrayList<>();
                for (Led led : Finder.get_ledArrayList())
                {
                    if (led.isActive())
                    {
                        leds.add(led);
                    }
                }
                leds.sort(Comparator.comparingInt(Led::get_kind));
                _ledComboBox = new ComboBox<Led>(FXCollections.observableArrayList(leds));
                for (Led led : leds)
                {
                    if (led.get_id() == valueId)
                    {
                        _ledComboBox.getSelectionModel().select(led);
                        break;
                    }
                }
                _cBoxesVBox.getChildren().addAll(_ledComboBox);
                break;

            case 4:
                System.out.println("case 4");
                _cBoxesVBox.getChildren().clear();
                ArrayList<PowerModule> powerModules = new ArrayList<>();
                for (PowerModule powerModule : Finder.get_powerModulesList())
                {
                    if (powerModule.is_active())
                    {
                        powerModules.add(powerModule);
                    }
                }
                powerModules.sort(Comparator.comparingInt(PowerModule::get_body));
                _powerModuleComboBox = new ComboBox<PowerModule>(FXCollections.observableArrayList(powerModules));
                for (PowerModule powerModule : powerModules)
                {
                    if (powerModule.get_id() == valueId)
                    {
                        _powerModuleComboBox.getSelectionModel().select(powerModule);
                        break;
                    }
                }
                _cBoxesVBox.getChildren().addAll(_powerModuleComboBox);
                break;

            case 5:
                System.out.println("case 5");
                _cBoxesVBox.getChildren().clear();
                ArrayList<Polygraphy> polygraphies = new ArrayList<>();
                for (Polygraphy polygraphy : Finder.get_polygraphyList())
                {
                    if (polygraphy.isActive())
                    {
                        polygraphies.add(polygraphy);
                    }
                }
                polygraphies.sort(Comparator.comparingInt(Polygraphy::get_quantity));
                _polygraphyComboBox = new ComboBox<Polygraphy>(FXCollections.observableArrayList(polygraphies));
                for (Polygraphy polygraphy : polygraphies)
                {
                    if (polygraphy.get_id() == valueId)
                    {
                        _polygraphyComboBox.getSelectionModel().select(polygraphy);
                        break;
                    }
                }
                _cBoxesVBox.getChildren().addAll(_polygraphyComboBox);
                break;

            case 6:
                System.out.println("case 6");
                _cBoxesVBox.getChildren().clear();
                ArrayList<Construction> constructions = new ArrayList<>();
                for (Construction construction : Finder.get_constructionsList())
                {
                    if (construction.isActive())
                    {
                        constructions.add(construction);
                    }
                }
                constructions.sort(Comparator.comparingInt(Construction::get_quantity));
                _constructionComboBox = new ComboBox<Construction>(FXCollections.observableArrayList(constructions));
                for (Construction construction : constructions)
                {
                    if (construction.get_id() == valueId)
                    {
                        _constructionComboBox.getSelectionModel().select(construction);
                        break;
                    }
                }
                _cBoxesVBox.getChildren().addAll(_constructionComboBox);
                break;
        }

    }

    boolean isOk() { return _ok; }

    Request get_request() { return _request; }
}
