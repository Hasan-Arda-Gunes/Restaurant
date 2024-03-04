import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {

        File initial = new File(args[0]);
        Scanner scan = new Scanner(initial);
        HashTable<String> districtNames = new HashTable<>();
        HashTable<District> allDistricts = new HashTable<>();
        LinkedList<District> allDistricts2 = new LinkedList<>();
        FileWriter output = new FileWriter(args[2]);
        output.write("");
        // read the first input
        while (scan.hasNextLine()){
            String line = scan.nextLine();
            String[] info = line.split(", ");
            String city = info[0];
            String districtName = info[1];
            String employeeName = info[2];
            String role = info[3];
            // if it is a district that is not mentioned before create a new district object and add the employees
            if (!districtNames.contains(city+districtName)){
                District district = new District(city,districtName);
                district.addEmployee(new Employees(employeeName,role));
                allDistricts.insert(district);
                allDistricts2.add(district);
                districtNames.insert(city+districtName);
            }
            // find the district and add the employees
            else {
                District district = allDistricts.find(city,districtName);
                district.addEmployee(new Employees(employeeName,role));
            }
        }

        File input = new File(args[1]);
        scan = new Scanner(input);
        HashTable<String> months = new HashTable<>(23);
        months.insert("January:");
        months.insert("February:");
        months.insert("March:");
        months.insert("April:");
        months.insert("May:");
        months.insert("June:");
        months.insert("July:");
        months.insert("August:");
        months.insert("September:");
        months.insert("October:");
        months.insert("November:");
        months.insert("December:");

        // read the second input
        while (scan.hasNextLine()){
            String line = scan.nextLine();
            // if it is a new month reset the monthly bonuses
            if (months.contains(line)){
                for (District district : allDistricts2)
                    district.clearBonus();
                continue;
            }
            if (line.equals(""))
                continue;

            String[] info = line.split(" ");
            if (info[0].equals("ADD:")){

                String city = info[1].substring(0,info[1].length()-1);
                String districtName = info[2].substring(0,info[2].length()-1);
                String name = info[3] + " " + info[4].substring(0,info[4].length()-1);
                String role = info[5];
                District districtToInsert = allDistricts.find(city,districtName);
                Employees insert = new Employees(name,role);
                if(districtToInsert.find(name) != null){
                    output.append("Existing employee cannot be added again.\n");
                    continue;
                }
                if (!role.equals("CASHIER") && !role.equals("COOK")){
                    districtToInsert.addEmployee(insert);
                    continue;
                }

                if (role.equals("CASHIER")){
                    // if there is only one cashier prior to add operation check its promotion points
                    if (districtToInsert.getCashierSize() == 1){
                        Employees onlyCashier = districtToInsert.onlyCashier();
                        // if less than four dismissal
                        if (onlyCashier.getPromotionPoint()<-4){
                            districtToInsert.removeEmployee(onlyCashier);
                            output.append(onlyCashier.getName() + " is dismissed from branch: " + districtToInsert.getName() + ".\n");
                        }
                    }
                    districtToInsert.addEmployee(insert);
                    Employees potentialPromote = districtToInsert.promotableCashier();
                    // check if there is a cashier that can promote to a cook
                    if (potentialPromote != null){
                        districtToInsert.setPromotableCashier(null);
                        output.append(potentialPromote.getName()+" is promoted from Cashier to Cook.\n");
                        districtToInsert.removeEmployee(potentialPromote);
                        potentialPromote.setRole("COOK");
                        potentialPromote.setPromotionPoint(potentialPromote.getPromotionPoint()-3);
                        districtToInsert.addEmployee(potentialPromote);
                        // if the cook can also promote to a manager add it to the linked list
                        if (potentialPromote.getPromotionPoint() > 9)
                            districtToInsert.addPromotableCook(potentialPromote);
                    }
                }
                else if (role.equals("COOK")){
                    // if there is only one cook prior to add operation check its promotion points
                    if (districtToInsert.getCashierSize() == 1){
                        Employees onlyCook = districtToInsert.onlyCook();
                        // if less than 4 promotion point dismiss
                        if (onlyCook.getPromotionPoint()<-4){
                            districtToInsert.removeEmployee(onlyCook);
                            output.append(onlyCook.getName() + " is dismissed from branch: " + districtToInsert.getName() + ".\n");
                        }
                    }
                    districtToInsert.addEmployee(insert);
                    if (districtToInsert.getManager() == null || districtToInsert.getManager().getPromotionPoint() < -4){
                        Employees potentialPromote2 = districtToInsert.promotableCook();
                        // check if there exist a cook that can replace the manager
                        if (potentialPromote2 != null){
                            Employees oldManager = districtToInsert.getManager();
                            districtToInsert.removeEmployee(oldManager);
                            districtToInsert.removeEmployee(potentialPromote2);
                            potentialPromote2.setRole("MANAGER");
                            potentialPromote2.setPromotionPoint(potentialPromote2.getPromotionPoint()-10);
                            districtToInsert.setManager(potentialPromote2);
                            output.append(potentialPromote2.getName() + " is dismissed from branch: " + districtToInsert.getName() + ".\n");
                            output.append(potentialPromote2.getName() + " is promoted from Cook to Manager.\n");
                        }
                    }
                }
            }
            else if (info[0].equals("LEAVE:")){

                String city = info[1].substring(0,info[1].length()-1);
                String districtName = info[2].substring(0,info[2].length()-1);
                String name = info[3] + " " + info[4];
                District districtToRemove = allDistricts.find(city,districtName);
                Employees removedEmployee = districtToRemove.find(name);
                if (removedEmployee == null){
                    output.append("There is no such employee.\n");
                    continue;
                }
                if (removedEmployee.getRole().equals("MANAGER")){
                    // if there is no cook to replace manager, manager can't quit
                    if (!(districtToRemove.getCookSize()>1)){
                        if (removedEmployee.getPromotionPoint() >-5)
                            districtToRemove.increaseBonus(200);
                        continue;
                    }
                    Employees cookToPromote = districtToRemove.promotableCook();
                    // if there is no cook to replace manager, manager can't quit
                    if (cookToPromote == null){
                        if (removedEmployee.getPromotionPoint() >-5)
                            districtToRemove.increaseBonus(200);
                        continue;
                    }
                    // replace the manager with the cook
                    districtToRemove.removeEmployee(removedEmployee);
                    districtToRemove.removeEmployee(cookToPromote);
                    cookToPromote.setRole("MANAGER");
                    districtToRemove.setManager(cookToPromote);
                    cookToPromote.setPromotionPoint(cookToPromote.getPromotionPoint()-10);
                    output.append(removedEmployee.getName() + " is leaving from branch: " + districtToRemove.getName()+".\n");
                    output.append(cookToPromote.getName() + " is promoted from Cook to Manager.\n");
                }
                else if (removedEmployee.getRole().equals("COURIER")){
                    // if he/she is not the only courier, he/she can leave
                    if (districtToRemove.getCourierSize()>1){
                        output.append(removedEmployee.getName() + " is leaving from branch: " + districtToRemove.getName()+".\n");
                        districtToRemove.removeEmployee(removedEmployee);
                    }
                    // if he/she is the only courier, he/she can't leave so give bonus if promotion point bigger than -5
                    else if (removedEmployee.getPromotionPoint() > -5)
                        districtToRemove.increaseBonus(200);
                }
                else if (removedEmployee.getRole().equals("CASHIER")){
                    // if he/she is not the only cashier, he/she can leave
                    if (districtToRemove.getCashierSize()>1){
                        output.append(removedEmployee.getName() + " is leaving from branch: " + districtToRemove.getName()+".\n");
                        districtToRemove.removeEmployee(removedEmployee);

                    }
                    // if he/she is the only cashier, he/she can't leave so give bonus if promotion point bigger than -5
                    else if (removedEmployee.getPromotionPoint() > -5)
                        districtToRemove.increaseBonus(200);
                }
                else if (removedEmployee.getRole().equals("COOK")){
                    // if he/she is not the only cook, he/she can leave
                    if (districtToRemove.getCookSize()>1){
                        // if the cook is in promotable cooks linked list, remove the cook
                        if (districtToRemove.inPromotableCooks(removedEmployee))
                            districtToRemove.removePromotableCook(removedEmployee);
                        output.append(removedEmployee.getName() + " is leaving from branch: " + districtToRemove.getName()+".\n");
                        districtToRemove.removeEmployee(removedEmployee);
                        if (districtToRemove.inPromotableCooks(removedEmployee))
                            districtToRemove.removePromotableCook(removedEmployee);
                    }
                    // if he/she is the only cook, he/she can't leave so give bonus if promotion point bigger than -5
                    else if (removedEmployee.getPromotionPoint() > -5)
                        districtToRemove.increaseBonus(200);
                }
            }
            else if (info[0].equals("PERFORMANCE_UPDATE:")){

                String city = info[1].substring(0,info[1].length()-1);
                String districtName = info[2].substring(0,info[2].length()-1);
                String name = info[3] + " " + info[4].substring(0,info[4].length()-1);
                District updateDistrict = allDistricts.find(city,districtName);
                Employees updateEmployee = updateDistrict.find(name);
                if (updateEmployee == null){
                    output.append("There is no such employee.\n");
                    continue;
                }
                int point = Integer.parseInt(info[5]);
                int promotion = point/200;
                int bonus = point % 200;
                if (bonus > 0)
                    updateDistrict.increaseBonus(bonus);
                updateEmployee.setPromotionPoint(updateEmployee.getPromotionPoint() + promotion);
                if (updateEmployee.getRole().equals("COURIER")){
                    // if not the only courier and -5 or less promotion point, then dismiss
                    if (updateDistrict.getCourierSize()>1){
                        if (updateEmployee.getPromotionPoint() < -4){
                            output.append(updateEmployee.getName() + " is dismissed from branch: " + updateDistrict.getName() + ".\n");
                            updateDistrict.removeEmployee(updateEmployee);
                        }
                    }
                }
                else if (updateEmployee.getRole().equals("CASHIER")){
                    // if less than 3 promotion point, then cashier can no longer promote
                    if (updateDistrict.promotableCashier()!=null){
                        if (updateDistrict.promotableCashier().getPromotionPoint()<3)
                            updateDistrict.setPromotableCashier(null);
                    }
                    // if not the only cashier and -5 or less promotion point, then dismiss
                    if (updateDistrict.getCashierSize()>1){
                        if (updateEmployee.getPromotionPoint() < -4){
                            output.append(updateEmployee.getName() + " is dismissed from branch: " + updateDistrict.getName() + ".\n");
                            updateDistrict.removeEmployee(updateEmployee);
                        }
                        // more than 2 promotion point, promote to cook
                        else if (updateEmployee.getPromotionPoint() > 2){
                            updateDistrict.removeEmployee(updateEmployee);
                            updateEmployee.setRole("COOK");
                            updateDistrict.addEmployee(updateEmployee);
                            output.append(updateEmployee.getName() + " is promoted from Cashier to Cook.\n");
                            updateEmployee.setPromotionPoint(updateEmployee.getPromotionPoint()-3);
                            //if it still has more than 9 promotion point add it to promotable cooks linked list
                            if (updateEmployee.getPromotionPoint() > 9)
                                updateDistrict.addPromotableCook(updateEmployee);
                            if (updateDistrict.getManager() == null || updateDistrict.getManager().getPromotionPoint() < -4){
                                Employees potentialPromote = updateDistrict.promotableCook();
                                // if the manager has less than -4 promotion point and there is a cook to replace then replace
                                if (potentialPromote != null){
                                    Employees oldManager = updateDistrict.getManager();
                                    updateDistrict.removeEmployee(oldManager);
                                    updateDistrict.removeEmployee(potentialPromote);
                                    potentialPromote.setRole("MANAGER");
                                    updateDistrict.setManager(potentialPromote);
                                    if (oldManager != null)
                                        output.append(oldManager.getName() + " is dismissed from branch: " + updateDistrict.getName() + ".\n");
                                    output.append(potentialPromote.getName() + " is promoted from Cook to Manager.\n");
                                }
                            }
                        }
                    }
                    else if (updateDistrict.getCashierSize() == 1 && updateEmployee.getPromotionPoint()>2)
                        updateDistrict.setPromotableCashier(updateEmployee);
                }
                else if (updateEmployee.getRole().equals("COOK")){
                    // if not the only cook and -5 or less promotion point, then dismiss
                    if (updateDistrict.getCookSize()>1){
                        if (updateEmployee.getPromotionPoint() < -4){
                            output.append(updateEmployee.getName()+ " is dismissed from branch: " + updateDistrict.getName() + ".\n");
                            updateDistrict.removeEmployee(updateEmployee);
                        }
                    }
                    // if it can promote add it to linked list
                    if (updateEmployee.getPromotionPoint() > 9 && !updateDistrict.inPromotableCooks(updateEmployee))
                        updateDistrict.addPromotableCook(updateEmployee);
                    // if it can no longer promote remove it from the linked list
                    else if (updateDistrict.inPromotableCooks(updateEmployee) && updateEmployee.getPromotionPoint()<10)
                        updateDistrict.removePromotableCook(updateEmployee);
                    // if the manager has less than -4 promotion point and there is a cook to replace then replace
                    if (updateDistrict.getManager() == null || updateDistrict.getManager().getPromotionPoint() < -4){
                        Employees potentialPromote = updateDistrict.promotableCook();
                        if (potentialPromote != null){
                            Employees oldManager = updateDistrict.getManager();
                            updateDistrict.removeEmployee(oldManager);
                            updateDistrict.removeEmployee(potentialPromote);
                            potentialPromote.setRole("MANAGER");
                            updateDistrict.setManager(potentialPromote);
                            if (oldManager != null)
                                output.append(oldManager.getName() + " is dismissed from branch: " + updateDistrict.getName() + ".\n");
                            output.append(potentialPromote.getName() + " is promoted from Cook to Manager.\n");
                        }
                    }
                }
                else if (updateEmployee.getRole().equals("MANAGER")){
                    // if the manager has less than -4 promotion point and there is a cook to replace then replace
                    if (updateDistrict.getCookSize()>1){
                        if (updateEmployee.getPromotionPoint()<-4){
                            Employees promotableCook = updateDistrict.promotableCook();
                            if (promotableCook != null){
                                output.append(updateEmployee.getName()+ " is dismissed from branch: " + updateDistrict.getName()+".\n");
                                updateDistrict.removeEmployee(updateEmployee);
                                updateDistrict.removeEmployee(promotableCook);
                                promotableCook.setRole("MANAGER");
                                updateDistrict.setManager(promotableCook);
                                promotableCook.setPromotionPoint(promotableCook.getPromotionPoint()-10);
                                output.append(promotableCook.getName() + " is promoted from Cook to Manager.\n");
                            }
                        }
                    }
                }
            }
            else if (info[0].equals("PRINT_MONTHLY_BONUSES:")){
                // find the district and print the monthly bonus
                String city = info[1].substring(0,info[1].length()-1);
                String districtName = info[2];
                District printDistrict = allDistricts.find(city,districtName);
                output.append("Total bonuses for the " + printDistrict.getName() + " branch this month are: " + printDistrict.getMonthlyBonus()+"\n");
            }
            else if (info[0].equals("PRINT_OVERALL_BONUSES:")){
                // find the district and print the overall bonus
                String city = info[1].substring(0,info[1].length()-1);
                String districtName = info[2];
                District printDistrict = allDistricts.find(city,districtName);
                output.append("Total bonuses for the " + printDistrict.getName() + " branch are: " + printDistrict.getOverallBonus() + "\n");
            }
            else if (info[0].equals("PRINT_MANAGER:")){
                // find the district and print the manager of that branch
                String city = info[1].substring(0,info[1].length()-1);
                String districtName = info[2];
                District printDistrict = allDistricts.find(city,districtName);
                output.append("Manager of the " + printDistrict.getName() + " branch is " + printDistrict.getManager().getName()+".\n");
            }
        }
        output.close();
    }
}