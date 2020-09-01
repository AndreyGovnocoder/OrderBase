public class Staff {
    private int id;
    private String name;
    private String jobPosition;

    Staff(int id, String name, String jobPosition){
        this.id = id;
        this.name = name;
        this.jobPosition = jobPosition;
    }

    Staff(String name, String jobPosition){
        this.name = name;
        this.jobPosition = jobPosition;
    }

    Staff(String name){
        this.name = name;
    }

    Staff(){}


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getJobPosition() {
        return this.jobPosition;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setJobPosition(String jobPosition) {
        this.jobPosition = jobPosition;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
