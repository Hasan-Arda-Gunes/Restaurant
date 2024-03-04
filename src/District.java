import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class District {
    private String name;
    private String city;
    private int overallBonus;
    private int monthlyBonus;
    private HashTable<Employees> COURIER;
    private HashTable<Employees> CASHIER;
    private HashTable<Employees> COOK;
    private Employees MANAGER;
    private LinkedList<Employees> promotableCooks;
    private Employees promotableCashier;

    District(){this("","");}
    District(String city, String name){
        this.city = city;
        this.name = name;
        overallBonus = 0;
        monthlyBonus = 0;
        COURIER = new HashTable<>();
        CASHIER = new HashTable<>();
        COOK = new HashTable<>();
        MANAGER = null;
        promotableCashier = null;
        promotableCooks = new LinkedList<>();
    }
    public String getName(){return name;}
    public String getCity(){return city;}
    public void addEmployee(Employees x){
        // add the employee according to roles
        if (x.getRole().equals("COURIER"))
            COURIER.insert(x);
        else if (x.getRole().equals("CASHIER"))
            CASHIER.insert(x);
        else if (x.getRole().equals("COOK"))
            COOK.insert(x);
        else if (x.getRole().equals("MANAGER"))
            MANAGER = x;
    }

    public void removeEmployee(Employees employee){
        // remove the employee from hash tables
        if (employee == null)
            return;

        if (employee.getRole().equals("COURIER"))
             COURIER.remove(employee);
        else if (employee.getRole().equals("CASHIER"))
            CASHIER.remove(employee);
        else if (employee.getRole().equals("COOK"))
            COOK.remove(employee);
        else if (employee.getRole().equals("MANAGER"))
            MANAGER = null;
    }
    public void increaseBonus(int n){
        overallBonus += n;
        monthlyBonus += n;
    }
    public void clearBonus(){monthlyBonus = 0;}
    public Employees find(String name){
        // find the employee from hash tables
        Employees courier =COURIER.find(name);
        if (courier != null)
            return courier;
        Employees cashier =CASHIER.find(name);
        if (cashier != null)
            return cashier;
        Employees cook =COOK.find(name);
        if (cook != null)
            return cook;
        if (MANAGER.getName().equals(name))
            return MANAGER;
        return null;
    }
    public int getCourierSize(){return COURIER.getSize();}
    public int getCashierSize(){return CASHIER.getSize();}
    public int getCookSize(){return COOK.getSize();}
    public Employees getManager(){return MANAGER;}
    public void setManager(Employees manager){MANAGER = manager;}
    public Employees promotableCook(){
        return promotableCooks.poll();
    }
    // return the first promotable cook
    public Employees promotableCashier(){
        return promotableCashier;
    }

    public int getMonthlyBonus(){return monthlyBonus;}

    public int getOverallBonus() {
        return overallBonus;
    }
    public void addPromotableCook(Employees x){promotableCooks.add(x);}
    public void setPromotableCashier(Employees x){promotableCashier = x;}
    public boolean inPromotableCooks(Employees x){return promotableCooks.contains(x);}
    public void removePromotableCook(Employees x){promotableCooks.remove(x);}
    public Employees onlyCashier(){return CASHIER.onlyEmployee();}
    // find the only cashier
    public Employees onlyCook(){return COOK.onlyEmployee();}
    //find the only cook
}
