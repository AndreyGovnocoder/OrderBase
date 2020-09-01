public class OrdersPosition {
    private String description;
    private String quantity = "Не указано";
    private String issue = "Не указано";
    private boolean toPrint = false;
    private int number;

    OrdersPosition(String description, String issue, String quantity){
        this.description = description;
        this.issue = issue;
        this.quantity = quantity;
    }

    OrdersPosition(String description, String quantity){
        this.description = description;
        this.quantity = quantity;
    }

    OrdersPosition(String description){
        this.description = description;
    }
    OrdersPosition(){}

    public void setDescription(String description) {
        this.description = description;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public void setToPrint(boolean toPrint) {
        this.toPrint = toPrint;
    }

    public String getDescription() {
        return description;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getIssue() {
        return issue;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public boolean isToPrint() {return toPrint;}

    @Override
    public String toString() {
        String position = this.description + " (Выдача: "+ this.issue +")";
        return position;
    }
}
