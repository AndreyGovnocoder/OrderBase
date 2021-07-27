import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddAccountingDialog
{
    boolean ok = false;
    Stage dialogStage = new Stage();
    BorderPane dialogBorderPane = new BorderPane();
    Scene dialogScene = new Scene(dialogBorderPane, 300, 200);
    //VBox textAreaVBox = new VBox();
    VBox dialogCenterVBox = new VBox();
    TextField textField = new TextField();
    TextArea textArea = new TextArea();
    //Label textFieldLabel = new Label();
    //Label textAreaLabel = new Label();
    TitledPane textFieldTitledPane = new TitledPane();
    TitledPane textAreaTitledPane = new TitledPane();
    Button addButton = new Button("Добавить");
    Button saveButton = new Button("Сохранить изменения");
    Button closeButton = new Button("Закрыть");

    void showAndWait(Stage parentStage)
    {
        textField.setPrefWidth(100);
        textArea.setWrapText(true);
        AnchorPane buttonsPane = new AnchorPane();

        closeButton.setOnAction(event -> dialogStage.close());

        buttonsPane.getChildren().addAll(addButton, saveButton, closeButton);

        AnchorPane.setTopAnchor(addButton, 5.0);
        AnchorPane.setLeftAnchor(addButton, 5.0);
        AnchorPane.setBottomAnchor(addButton, 5.0);

        AnchorPane.setTopAnchor(saveButton, 5.0);
        AnchorPane.setLeftAnchor(saveButton, 5.0);
        AnchorPane.setBottomAnchor(saveButton, 5.0);

        AnchorPane.setTopAnchor(closeButton, 5.0);
        AnchorPane.setRightAnchor(closeButton, 5.0);
        AnchorPane.setBottomAnchor(closeButton, 5.0);

        textFieldTitledPane.setCollapsible(false);
        textFieldTitledPane.setExpanded(true);
        textFieldTitledPane.setContent(textField);
        textAreaTitledPane.setCollapsible(false);
        textAreaTitledPane.setExpanded(true);
        textAreaTitledPane.setContent(textArea);
        //dialogGrigPane.add(textFieldLabel, 0,0);
        //dialogGrigPane.add(textField, 1,0);
        //dialogGrigPane.add(textAreaLabel, 0,1);
        //dialogGrigPane.add(textArea, 1,1);
        //dialogGrigPane.setVgap(10);
        //dialogGrigPane.setHgap(15);
        //dialogGrigPane.setPadding(new Insets(15));

        //dialogGrigPane.alignmentProperty().set(Pos.CENTER);

        //textAreaVBox.setSpacing(10);
        //textAreaVBox.setPadding(new Insets(10));
        //textAreaLabel.setAlignment(Pos.CENTER);
        //textAreaVBox.getChildren().addAll(textAreaLabel, textArea);

        dialogCenterVBox.setSpacing(10);
        dialogCenterVBox.setPadding(new Insets(15));


        dialogBorderPane.setCenter(dialogCenterVBox);
        dialogBorderPane.setBottom(buttonsPane);

        //BorderPane.setAlignment(dialogGrigPane, Pos.CENTER);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.getIcons().add(MainInterface.getIconLogo());
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    void addTextField()
    {
        dialogCenterVBox.getChildren().addAll(textFieldTitledPane);
    }

    void addTextArea()
    {
        dialogCenterVBox.getChildren().addAll(textAreaTitledPane);
    }
}
