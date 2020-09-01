
public class Client {
    private int id;
    private String client;
    private String mail;
    private String phone;

    Client(int id, String client,    String phone, String mail ){
        this.id = id;
        this.client = client;
        this.mail = mail;
        this.phone = phone;
    }

    Client(String client,  String phone, String mail){
        this.client = client;
        this.phone = phone;
        this.mail = mail;
    }

    Client(int id, String client ){
        this.client = client;
        this.id = id;
    }

    Client(String client ){
        this.client = client;
    }


    Client(){    }

    public int getId() {
        return id;
    }

    public String getClient() {
        return client;
    }

    public String getMail() {
        return mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return this.client;
    }
}
