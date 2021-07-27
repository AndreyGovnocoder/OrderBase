import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.time.LocalTime;

public class MontagesDialog
{
    private Stage _montagesDialogStage;
    private boolean _editMontage = false;
    boolean _ok = false;
    private TextField _hourTextField;
    private TextField _minTextField;
    private TextField _objectTextField;
    private TextArea _descriptionTextArea;
    private TextField _contactPersonTextField;
    private final CheckBox _measureCheckBox = new CheckBox("Замер");
    Montage _montage;

    MontagesDialog(Montage montage)
    {
        _editMontage = true;
        this._montage = montage;
        setTextEdits();
    }

    MontagesDialog()
    {
        setTextEdits();
    }

    void showAndWait(Stage primaryStage)
    {

        _montagesDialogStage = new Stage();
        BorderPane montagesDialogBorderPane = new BorderPane();
        Scene montagesDialogScene = new Scene(montagesDialogBorderPane, 500,500);

        montagesDialogBorderPane.setCenter(getCenter());
        montagesDialogBorderPane.setBottom(getBottom());

        if(_editMontage) _montagesDialogStage.setTitle("Редактирование монтажа");
        else _montagesDialogStage.setTitle("Создание нового монтажа");
        _montagesDialogStage.getIcons().add(MainInterface.getIconLogo());
        _montagesDialogStage.initModality(Modality.WINDOW_MODAL);
        _montagesDialogStage.initOwner(primaryStage);
        _montagesDialogStage.setScene(montagesDialogScene);
        _montagesDialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        VBox centerVBox = new VBox();
        TitledPane timeTitledPane = new TitledPane();
        TitledPane objectTitledPane = new TitledPane();
        TitledPane descriptionTitledPane = new TitledPane();
        TitledPane contactPersonTitledPane = new TitledPane();
        GridPane timeGridPane = new GridPane();
        Label hourLabel = new Label("Час");
        Label minLabel = new Label("Минуты");
        Label betweenLabel = new Label(":");

        _measureCheckBox.setPadding(new Insets(0,0,0,30));


        ColumnConstraints column1 = new ColumnConstraints(50, 50, Double.MAX_VALUE);
        ColumnConstraints column2 = new ColumnConstraints(10, 10, Double.MAX_VALUE);
        ColumnConstraints column3 = new ColumnConstraints(50, 50, Double.MAX_VALUE);
        timeGridPane.getColumnConstraints().addAll(column1, column2, column3);

        timeGridPane.add(hourLabel, 0,0);
        timeGridPane.add(minLabel, 2,0);
        timeGridPane.add(_hourTextField, 0,1);
        timeGridPane.add(betweenLabel,1,1);
        timeGridPane.add(_minTextField, 2,1);
        timeGridPane.add(_measureCheckBox, 3,1);
        GridPane.setHalignment(hourLabel, HPos.CENTER);
        GridPane.setValignment(hourLabel, VPos.CENTER);
        GridPane.setHalignment(minLabel, HPos.CENTER);
        GridPane.setValignment(minLabel, VPos.CENTER);
        GridPane.setHalignment(_hourTextField, HPos.CENTER);
        GridPane.setValignment(_hourTextField, VPos.CENTER);
        GridPane.setHalignment(_minTextField, HPos.CENTER);
        GridPane.setValignment(_minTextField, VPos.CENTER);
        GridPane.setHalignment(_measureCheckBox, HPos.CENTER);
        GridPane.setValignment(_measureCheckBox, VPos.CENTER);
        GridPane.setHalignment(betweenLabel, HPos.CENTER);
        GridPane.setValignment(betweenLabel, VPos.CENTER);
        timeGridPane.setVgap(5);

        timeTitledPane.setText("Время монтажа");
        timeTitledPane.setCollapsible(false);
        timeTitledPane.setExpanded(true);
        timeTitledPane.setContent(timeGridPane);

        objectTitledPane.setText("Объект монтажа");
        objectTitledPane.setCollapsible(false);
        objectTitledPane.setExpanded(true);
        objectTitledPane.setContent(_objectTextField);

        descriptionTitledPane.setText("Описание");
        descriptionTitledPane.setCollapsible(false);
        descriptionTitledPane.setExpanded(true);
        descriptionTitledPane.setContent(_descriptionTextArea);

        contactPersonTitledPane.setText("Контактное лицо");
        contactPersonTitledPane.setCollapsible(false);
        contactPersonTitledPane.setExpanded(true);
        contactPersonTitledPane.setContent(_contactPersonTextField);

        centerVBox.setSpacing(15);
        centerVBox.setPadding(new Insets(10));
        centerVBox.setStyle("-fx-background-color: #f0f8ff");
        centerVBox.getChildren().addAll(
                timeTitledPane,
                objectTitledPane,
                contactPersonTitledPane,
                descriptionTitledPane);
        return centerVBox;
    }

    private VBox getBottom()
    {
        HBox buttonsPane = new HBox();
        VBox bottomVBox = new VBox();
        Button createMontageButton = new Button("Создать монтаж");
        Button saveChangesButton = new Button("Сохранить изменения");
        Button closeButton = new Button("Отмена");

        closeButton.setOnAction(event -> _montagesDialogStage.close());

        createMontageButton.setOnAction(event ->
        {
            _montage = new Montage();

            if(checkTextEdits())
            {
                LocalTime localTime = LocalTime.of(
                        Integer.parseInt(_hourTextField.getText()),
                        Integer.parseInt(_minTextField.getText()));
                _montage.set_time(localTime);
                _montage.set_object(_objectTextField.getText());
                _montage.set_description(_descriptionTextArea.getText());
                _montage.set_contactPerson(_contactPersonTextField.getText());
                _montage.set_measure(_measureCheckBox.isSelected());
                _ok = true;
                _montagesDialogStage.close();
            }
        });

        saveChangesButton.setOnAction(event ->
        {
            if(checkTextEdits())
            {
                LocalTime localTime = LocalTime.of(
                        Integer.parseInt(_hourTextField.getText()),
                        Integer.parseInt(_minTextField.getText()));
                _montage.set_time(localTime);
                _montage.set_object(_objectTextField.getText());
                _montage.set_description(_descriptionTextArea.getText());
                _montage.set_contactPerson(_contactPersonTextField.getText());
                _montage.set_measure(_measureCheckBox.isSelected());
                _ok = true;
                _montagesDialogStage.close();
            }
        });

        buttonsPane.setSpacing(15);
        buttonsPane.setPadding(new Insets(15));
        if(_editMontage) buttonsPane.getChildren().addAll(saveChangesButton, closeButton);
        else buttonsPane.getChildren().addAll(createMontageButton, closeButton);

        bottomVBox.getChildren().addAll(new Separator(), buttonsPane);
        return bottomVBox;
    }

    private void setTextEdits()
    {
        _hourTextField = new TextField();
        _minTextField = new TextField();
        _objectTextField = new TextField();
        _descriptionTextArea = new TextArea();
        _contactPersonTextField = new TextField();
        final double width = 30;

        _descriptionTextArea.setWrapText(true);

        _hourTextField.textProperty().addListener(getChangeHoursListener(_hourTextField));
        _minTextField.textProperty().addListener(getChangeMinutesListener(_minTextField));
        _hourTextField.setMinWidth(width);
        _hourTextField.setPrefWidth(width);
        _hourTextField.setMaxWidth(width);
        _minTextField.setMinWidth(width);
        _minTextField.setPrefWidth(width);
        _minTextField.setMaxWidth(width);

        if(_editMontage)
        {
            _hourTextField.setText(String.valueOf(_montage.get_time().getHour()));
            if(_montage.get_time().getMinute()==0)
                _minTextField.setText("00");
            else
                _minTextField.setText(String.valueOf(_montage.get_time().getMinute()));
            if(_montage.get_object() != null)
                _objectTextField.setText(_montage.get_object());
            if(_montage.get_description() != null)
                _descriptionTextArea.setText(_montage.get_description());
            if(_montage.get_contactPerson() != null)
                _contactPersonTextField.setText(_montage.get_contactPerson());
            if(_montage.get_measure())
                _measureCheckBox.setSelected(true);
        }
    }

    private boolean checkTextEdits()
    {
        if(_hourTextField.getText().isEmpty())
        {
            MainInterface.getAlertErrorDialog("Не указан час");
            return false;
        }
        else if (_hourTextField.getText().length()>2
                || Integer.parseInt(_hourTextField.getText())>24)
        {
            MainInterface.getAlertErrorDialog("Неверно указаны часы");
            return false;
        }
        else if (_minTextField.getText().isEmpty())
        {
            MainInterface.getAlertErrorDialog("Не указаны минуты");
            return false;
        }
        else if (_minTextField.getText().length()>2
                || Integer.parseInt(_minTextField.getText())>59)
        {
            MainInterface.getAlertErrorDialog("Неверно указаны минуты");
            return false;
        }
        else if (_objectTextField.getText().isEmpty())
        {
            MainInterface.getAlertErrorDialog("Не указан объект");
            return false;
        }
        else if (_descriptionTextArea.getText().isEmpty())
        {
            MainInterface.getAlertErrorDialog("Отсутствует описание монтажа");
            return false;
        }
        else if (_contactPersonTextField.getText().isEmpty())
            _contactPersonTextField.setText("");

        return true;
    }

    static ChangeListener<String> getChangeHoursListener(TextField txtpoint)
    {
        return (observable, oldValue, newValue) ->
        {
            if (!newValue.isEmpty())
            {
                try
                {
                    long pointI = Long.parseLong(newValue);

                    if( txtpoint.getText().length() == 2)
                    {
                        if(Integer.parseInt(txtpoint.getText()) > 23)
                            txtpoint.deleteNextChar();
                        else
                            txtpoint.setText(String.valueOf(pointI));
                    }
                    else if (txtpoint.getText().length() > 2)
                        txtpoint.deleteNextChar();
                } catch (Exception e)
                {
                    txtpoint.clear();
                    txtpoint.setText(getNumber(oldValue));
                }
            }
        };
    }

    static ChangeListener<String> getChangeMinutesListener(TextField txtpoint)
    {
        return (observable, oldValue, newValue) ->
        {
            if (!newValue.isEmpty())
            {
                try
                {
                    long pointI = Long.parseLong(newValue);
                    if( txtpoint.getText().length() == 2)
                    {
                        if(!txtpoint.getText().equals("00"))
                        {
                            if(Integer.parseInt(txtpoint.getText()) > 59)
                                txtpoint.deleteNextChar();
                            else
                                txtpoint.setText(String.valueOf(pointI));
                        }
                        else
                            txtpoint.setText(newValue);
                    }
                    else if (txtpoint.getText().length() > 2)
                        txtpoint.deleteNextChar();

                } catch (Exception e)
                {
                    txtpoint.clear();
                    txtpoint.setText(getNumber(oldValue));
                }
            }
        };
    }

    static String getNumber(String value)
    {
        String n = "";
        try
        {
            return String.valueOf(Long.parseLong(value));
        } catch (Exception e)
        {
            String[] array = value.split("");
            for (String tab : array)
            {
                try
                {
                    n = n.concat(String.valueOf(Long.parseLong(String.valueOf(tab))));
                } catch (Exception ignored){}
            }
            return n;
        }
    }
}
