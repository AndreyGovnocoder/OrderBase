import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MaterialsKindDialog
{
    Stage _materialsKindDialogStage;
    BorderPane _materialsKindDialogBorderPane;
    VBox _centerVBox;
    Scene _materialsKindDialogScene;
    TextField _nameTextField;
    CheckBox _manufacturerCheckBox;
    CheckBox _widthCheckBox;
    CheckBox _heightCheckBox;
    CheckBox _colorCheckBox;
    CheckBox _propertyCheckBox;
    CheckBox _thicknessCheckBox;
    CheckBox _attributeCheckBox;
    CheckBox _colorNumberCheckBox;
    MaterialsKind _kind;
    boolean _edit = false;
    boolean _ok = false;

    MaterialsKindDialog(MaterialsKind kind)
    {
        _kind = kind;
        _edit = true;
    }

    MaterialsKindDialog(){}

    void showAndWait(Stage primaryStage)
    {
        _materialsKindDialogStage = new Stage();
        _materialsKindDialogBorderPane = new BorderPane();
        //_materialsKindDialogScene = new Scene(_materialsKindDialogBorderPane, 320,250);
        _materialsKindDialogScene = new Scene(_materialsKindDialogBorderPane);
        _nameTextField = new TextField();
        _manufacturerCheckBox = new CheckBox("Производитель");
        _widthCheckBox = new CheckBox("Ширина");
        _heightCheckBox = new CheckBox("Высота/метраж");
        _colorCheckBox = new CheckBox("Цвет");
        _propertyCheckBox = new CheckBox("Свойства");
        _thicknessCheckBox = new CheckBox("Толщина");
        _attributeCheckBox = new CheckBox("Атрибут");
        _colorNumberCheckBox = new CheckBox("Номер цвета");

        _materialsKindDialogBorderPane.setCenter(getCenter());
        _materialsKindDialogBorderPane.setBottom(getBottom());
        BorderPane.setAlignment(_centerVBox, Pos.TOP_CENTER);

        if(is_edit()) setContent();
        else _kind = new MaterialsKind();

        _materialsKindDialogStage.initModality(Modality.WINDOW_MODAL);
        _materialsKindDialogStage.initOwner(primaryStage);
        if(_edit)
            _materialsKindDialogStage.setTitle("Редактирование: вид материала");
        else
            _materialsKindDialogStage.setTitle("Новый вид материала");
        _materialsKindDialogStage.setScene(_materialsKindDialogScene);
        _materialsKindDialogStage.getIcons().add(MainInterface.getIconLogo());
        _materialsKindDialogStage.showAndWait();
    }

    private VBox getCenter()
    {
        _centerVBox = new VBox();
        HBox headNameHBox = new HBox();
        Label _headNameLabel = new Label("Название");
        VBox itemsVBox = new VBox();

        headNameHBox.setSpacing(10);
        //headNameHBox.setAlignment(Pos.CENTER);
        headNameHBox.getChildren().addAll(_headNameLabel, _nameTextField);

        itemsVBox.setSpacing(3);
        itemsVBox.setPadding(new Insets(0,0,0,15));
        //itemsVBox.setAlignment(Pos.TOP_LEFT);
        itemsVBox.getChildren().addAll(
                _manufacturerCheckBox,
                _widthCheckBox,
                _heightCheckBox,
                _colorCheckBox,
                _propertyCheckBox,
                _thicknessCheckBox,
                _attributeCheckBox,
                _colorNumberCheckBox);

        _centerVBox.setSpacing(10);
        _centerVBox.setAlignment(Pos.TOP_CENTER);
        _centerVBox.setStyle("-fx-background-color: #f0f8ff");
        _centerVBox.setPadding(new Insets(10));
        _centerVBox.getChildren().addAll(headNameHBox,itemsVBox);
        return _centerVBox;
    }

    private VBox getBottom()
    {
        AnchorPane bottomBtnsAnchorPane = new AnchorPane();
        VBox bottomVBox = new VBox();
        Button okButton = new Button("Ок");
        Button cancelButton = new Button("Отмена");

        cancelButton.setPrefWidth(80);
        cancelButton.setOnAction(event -> _materialsKindDialogStage.close());

        okButton.setPrefWidth(80);
        okButton.setOnAction(event ->
        {
            if(_nameTextField.getText().equals(""))
                MainInterface.getAlertInformationDialog("Введите название материала");
            else
            {
                String columns = "+~-~Количество~";
                _kind.set_name(_nameTextField.getText());
                _kind.set_manufacturer(_manufacturerCheckBox.isSelected());
                _kind.set_width(_widthCheckBox.isSelected());
                _kind.set_height(_heightCheckBox.isSelected());
                _kind.set_color(_colorCheckBox.isSelected());
                _kind.set_property(_propertyCheckBox.isSelected());
                _kind.set_thickness(_thicknessCheckBox.isSelected());
                _kind.set_attribute(_attributeCheckBox.isSelected());
                _kind.set_colorNumber(_colorNumberCheckBox.isSelected());
                if(_manufacturerCheckBox.isSelected()) columns += "Производитель~";
                if(_widthCheckBox.isSelected()) columns += "Ширина~";
                if(_heightCheckBox.isSelected()) columns += "Высота/Метраж~";
                if(_colorCheckBox.isSelected()) columns += "Цвет~";
                if(_propertyCheckBox.isSelected()) columns += "Свойство~";
                if(_thicknessCheckBox.isSelected()) columns += "Толщина~";
                if(_attributeCheckBox.isSelected()) columns += "Атрибут~";
                if (_colorNumberCheckBox.isSelected()) columns += "Номер цвета~";
                columns += "Закуп. цена~Цена продажи~";

                if(!is_edit())
                {
                   if(checkName())
                   {
                       _kind.set_columns(columns);
                       _kind.set_active(true);
                       if (DataBaseStorehouse.addMaterialsKind(_kind))
                       {
                           _kind.set_id(DataBaseStorehouse.getLastId(DataBaseStorehouse.KINDS_TABLE));
                           _ok = true;
                           _materialsKindDialogStage.close();
                       }
                   }
                }
                else
                {
                    _kind.set_columns(columns);
                    if(checkName(_kind.get_id()))
                    {
                        _ok = true;
                        _materialsKindDialogStage.close();
                    }
                }
            }
        });

        bottomBtnsAnchorPane.getChildren().addAll(okButton, cancelButton);
        AnchorPane.setTopAnchor(okButton,5.0);
        AnchorPane.setLeftAnchor(okButton,5.0);
        AnchorPane.setBottomAnchor(okButton,5.0);
        AnchorPane.setTopAnchor(cancelButton,5.0);
        AnchorPane.setRightAnchor(cancelButton,5.0);
        AnchorPane.setBottomAnchor(cancelButton,5.0);

        bottomVBox.getChildren().addAll(new Separator(), bottomBtnsAnchorPane);

        return bottomVBox;
    }

    void setContent()
    {
        _nameTextField.setText(_kind.get_name());
        if (_kind.get_manufacturer()) _manufacturerCheckBox.setSelected(true);
        if (_kind.get_width()) _widthCheckBox.setSelected(true);
        if (_kind.get_height()) _heightCheckBox.setSelected(true);
        if (_kind.get_color()) _colorCheckBox.setSelected(true);
        if (_kind.get_property()) _propertyCheckBox.setSelected(true);
        if (_kind.get_thickness()) _thicknessCheckBox.setSelected(true);
        if (_kind.get_attribute()) _attributeCheckBox.setSelected(true);
        if (_kind.get_colorNumber()) _colorNumberCheckBox.setSelected(true);
    }

    MaterialsKind get_kind(){ return _kind; }
    boolean is_edit(){ return _edit;}
    boolean is_ok() { return _ok;}

    boolean checkName()
    {
        for(MaterialsKind kind : DataBaseStorehouse.getMaterialsKindsList())
        {
            if(kind.get_name().equals(_kind.get_name()))
            {
                MainInterface.getAlertWarningDialog("Такое название уже существует");
                return false;
            }
        }

        return true;
    }

    boolean checkName(int kindId)
    {
        for(MaterialsKind kind : DataBaseStorehouse.getMaterialsKindsList())
        {
            if(kind.get_name().equals(_kind.get_name()) && kind.get_id() != kindId)
            {
                MainInterface.getAlertWarningDialog("Такое название уже существует");
                return false;
            }
        }

        return true;
    }
}
