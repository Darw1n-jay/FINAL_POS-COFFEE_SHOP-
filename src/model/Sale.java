package pos.model;

public class Sale {
    public int id;
    public int userId;
    public String datetime;
    public double total;
    public double discount;
    public String paymentMode;
    public boolean paid;
    public String status;

    public Sale() {}

    public Sale(int id, int userId, String datetime, double total, double discount, String paymentMode, boolean paid, String status) {
        this.id = id;
        this.userId = userId;
        this.datetime = datetime;
        this.total = total;
        this.discount = discount;
        this.paymentMode = paymentMode;
        this.paid = paid;
        this.status = status;
    }
}
