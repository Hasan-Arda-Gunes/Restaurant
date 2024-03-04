import java.util.Queue;

public class Employees {
    private String name;
    private String role;
    private int promotionPoint;
    Employees(){this("","");}
    public Employees(String name, String role){
        this.name = name;
        this.role = role;
        promotionPoint = 0;
    }

    public int getPromotionPoint() {
        return promotionPoint;
    }

    public void setPromotionPoint(int promotionPoint) {
        this.promotionPoint = promotionPoint;
    }


    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
