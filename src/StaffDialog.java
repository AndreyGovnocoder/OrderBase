import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

class StaffDialog
{
    private Staff _staff;
    boolean _ok = false;
    private boolean _editStaff = false;

    StaffDialog (Staff staff)
    {
        this._editStaff = true;
        this._staff = staff;
    }
    StaffDialog(){}

    void showAndWait(Stage parentStage)
    {
        Stage staffDialogStage = new Stage();
        BorderPane staffDialogBorderPane = new BorderPane();
        Scene staffDialogScene = new Scene(staffDialogBorderPane, 300,180);
        RadioButton managerRBtn = new RadioButton("менеджер");
        RadioButton designerRBtn = new RadioButton("дизайнер");
        TextField nameTextField = new TextField();
        Label nameLabel = new Label("Имя: ");
        GridPane staffDialogGridPane = new GridPane();
        AnchorPane buttonsPane = new AnchorPane();
        TitledPane positionPane = new TitledPane();
        HBox radioButtonHBox = new HBox();
        ToggleGroup positionGroup = new ToggleGroup();
        Button addStaffButton = new Button("Добавить");
        Button closeButton = new Button("Закрыть");
        Button saveChangesButton = new Button("Сохранить изменения");

        addStaffButton.setOnAction(event ->
        {
            _staff = new Staff();
            _staff.set_name(nameTextField.getText());
            if(managerRBtn.isSelected()) _staff.set_position(managerRBtn.getText());
            else _staff.set_position(designerRBtn.getText());
            _ok = true;
            staffDialogStage.close();
        });

        saveChangesButton.setOnAction(event ->
        {
            _staff.set_name(nameTextField.getText());
            if(managerRBtn.isSelected()) _staff.set_position(managerRBtn.getText());
            else _staff.set_position(designerRBtn.getText());
            _ok = true;
            staffDialogStage.close();
        });

        closeButton.setOnAction(event -> staffDialogStage.close());

        managerRBtn.setSelected(true);

        if(_editStaff)
        {
            nameTextField.setText(_staff.get_name());
            if(_staff.get_position().equals(managerRBtn.getText())) managerRBtn.setSelected(true);
            else if (_staff.get_position().equals(designerRBtn.getText())) designerRBtn.setSelected(true);
            buttonsPane.getChildren().addAll(saveChangesButton, closeButton);
        } else
        {
            buttonsPane.getChildren().addAll(addStaffButton, closeButton);
        }

        AnchorPane.setTopAnchor(addStaffButton, 5.0);
        AnchorPane.setLeftAnchor(addStaffButton, 5.0);
        AnchorPane.setBottomAnchor(addStaffButton, 5.0);

        AnchorPane.setTopAnchor(saveChangesButton, 5.0);
        AnchorPane.setLeftAnchor(saveChangesButton, 5.0);
        AnchorPane.setBottomAnchor(saveChangesButton, 5.0);

        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        staffDialogStage.initModality(Modality.WINDOW_MODAL);
        staffDialogStage.initOwner(parentStage);

        positionPane.setCollapsible(false);
        positionPane.setContent(radioButtonHBox);
        positionPane.setText("Должность");

        managerRBtn.setToggleGroup(positionGroup);
        designerRBtn.setToggleGroup(positionGroup);
        radioButtonHBox.getChildren().addAll(managerRBtn, designerRBtn);
        radioButtonHBox.setSpacing(30);
        radioButtonHBox.setPadding(new Insets(15));

        staffDialogGridPane.add(nameLabel, 0,0);
        staffDialogGridPane.add(nameTextField, 1,0);
        staffDialogGridPane.add(positionPane, 0,1,2,1);
        staffDialogGridPane.setVgap(10);
        staffDialogGridPane.setHgap(15);
        staffDialogGridPane.setPadding(new Insets(15));
        staffDialogGridPane.alignmentProperty().set(Pos.CENTER);

        staffDialogBorderPane.setCenter(staffDialogGridPane);
        staffDialogBorderPane.setBottom(buttonsPane);
        BorderPane.setAlignment(staffDialogGridPane, Pos.CENTER);

        if(_editStaff) staffDialogStage.setTitle("Редактирование сотрудника");
        else staffDialogStage.setTitle("Создание нового сотрудника");
        staffDialogStage.getIcons().add(MainInterface.getIconLogo());
        staffDialogStage.setMinHeight(180);
        staffDialogStage.setMinWidth(300);
        staffDialogStage.setScene(staffDialogScene);
        staffDialogStage.showAndWait();
    }

    Staff get_staff()
    {
        return _staff;
    }

}
