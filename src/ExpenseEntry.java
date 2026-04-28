import java.io.Serializable;

public class ExpenseEntry implements Serializable {
    private String name;
    private double cost;
    private String category;

    public ExpenseEntry(String name, double cost, String category) {
        this.name = name;
        this.cost = cost;
        this.category = category;
    }

    public double getCost() { return cost; }
    public String toFileString() { return name + "," + cost + "," + category; }
}