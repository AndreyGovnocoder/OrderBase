import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class OrderPane {
    private HBox client = new HBox();
    private HBox manager = new HBox();
    private VBox positions = new VBox();

    OrderPane(Order order){
        this.client.getChildren().addAll(new Text(order.getClient().getClient()));
        this.manager.getChildren().addAll(new Text(order.getManager()));
        this.client.setAlignment(Pos.CENTER);
        this.manager.setAlignment(Pos.CENTER);

        ObservableList<OrdersPosition> positionsList = FXCollections.observableArrayList(order.getPositions());
        TableView<OrdersPosition> tableView = new TableView();
        TableColumn<OrdersPosition, String> descriptionCol = new TableColumn<>("Позиция");
        TableColumn<OrdersPosition, String> issueCol = new TableColumn<>("Выдача");
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        issueCol.setCellValueFactory(new PropertyValueFactory<>("issue"));
        tableView.setItems(positionsList);
        tableView.getColumns().addAll(descriptionCol, issueCol);
        /*
        tableView.setFixedCellSize(25);
        tableView.prefHeightProperty().bind(Bindings.size(tableView.getItems()).multiply(tableView.getFixedCellSize()).add(30));
        */
        TitledPane titledPane = new TitledPane();
        titledPane.setText("Позиции заказа");
        titledPane.setContent(tableView);
        titledPane.setExpanded(false);
        this.positions.getChildren().add(titledPane);
    }

    public void setManager(Order order) {
        this.manager.getChildren().addAll(new Text(order.getManager()));
    }

    public HBox getManager() {
        return manager;
    }

    public void setClient(Order order) {
        this.client.getChildren().addAll(new Text(order.getClient().getClient()));
    }

    public HBox getClient() {
        return client;
    }

    public VBox getPositions(){return positions;}
}
