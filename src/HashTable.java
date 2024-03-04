import java.util.LinkedList;

public class HashTable<Anytype> {
    private LinkedList<Anytype>[] list;
    private int size;

    HashTable() {
        this(101);
    }

    HashTable(int tableSize) {
        list = new LinkedList[tableSize];
        for (int i = 0; i < tableSize; i++) {
            list[i] = new LinkedList<>();
        }
        size = 0;
    }

    private int hash(Anytype x) {
        int hashIndex = x.hashCode();
        hashIndex = hashIndex % list.length;
        if (hashIndex < 0)
            hashIndex += list.length;
        return hashIndex;
    }

    private int hash(String x) {
        int hashIndex = x.hashCode();
        hashIndex = hashIndex % list.length;
        if (hashIndex < 0)
            hashIndex += list.length;
        return hashIndex;
    }

    public void insert(Anytype x) {
        // if the inserted object is of employee class then hash it with its name
        if (x instanceof Employees) {
            LinkedList<Anytype> insertedList = list[hash(((Employees) x).getName())];
            insertedList.add(x);
        } else if (x instanceof District) {
            // if the inserted object is of district class then hash it with its city and name
            LinkedList<Anytype> insertedList = list[hash(((District) x).getName() + ((District) x).getCity())];
            insertedList.add(x);
        } else {
            LinkedList<Anytype> insertedList = list[hash(x)];
            insertedList.add(x);
        }

        size++;
        if (size > list.length)
            rehash();
    }

    private static boolean isPrime(int n) {
        if (n == 2 || n == 3) {
            return true;
        }
        if (n == 1 || n % 2 == 0) {
            return false;
        }
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;

    }

    private static int nextPrime(int x) {
        if (x % 2 == 0) {
            x++;
        }
        while (!isPrime(x)) {
            x += 2;
        }
        return x;
    }

    private void rehash() {
        LinkedList<Anytype>[] oldLinkedLists = list;
        list = new LinkedList[nextPrime(2 * list.length)];
        for (int i = 0; i < list.length; i++)
            list[i] = new LinkedList<>();
        size = 0;
        for (LinkedList<Anytype> linkedList : oldLinkedLists) {
            for (Anytype x : linkedList)
                insert(x);
        }
    }

    public boolean contains(Anytype x) {
        // if the searched object is of employee class then find it with its name
        if (x instanceof Employees) {
            LinkedList<Anytype> check = list[hash(((Employees) x).getName())];
            return check.contains(x);
            // if the searched object is of district class then find it with its city and name
        } else if (x instanceof District) {
            LinkedList<Anytype> check = list[hash(((District) x).getName() + ((District) x).getCity())];
            return check.contains(x);
        } else {
            LinkedList<Anytype> check = list[hash(x)];
            return check.contains(x);
        }

    }

    public void remove(Anytype x) {
        // if the removed object is of employee class then find it with its name and remove it
        if (x instanceof Employees) {
            LinkedList<Anytype> removeFrom = list[hash(((Employees) x).getName())];
            removeFrom.remove(x);
            size--;
            // if the removed object is of district class then find it with its city and name and remove it
        } else if (x instanceof District) {
            LinkedList<Anytype> removeFrom = list[hash(((District) x).getName() + ((District) x).getCity())];
            removeFrom.remove(x);
            size--;
        } else {
            LinkedList<Anytype> removeFrom = list[hash(x)];
            removeFrom.remove(x);
            size--;
        }
    }

    public Employees find(String name) {
        LinkedList<Anytype> listToFind = list[hash(name)];
        for (int i = 0; i < listToFind.size(); i++) {
            // find it with its name
            if (listToFind.get(i) instanceof Employees) {
                if (((Employees) listToFind.get(i)).getName().equals(name))
                    return (Employees) listToFind.get(i);
            }
        }
        return null;
    }

    public District find(String city, String name) {
        LinkedList<Anytype> listToFind = list[hash(name + city)];
        for (int i = 0; i < listToFind.size(); i++) {
            // find it with its city and name
            if (listToFind.get(i) instanceof District) {
                if (((District) listToFind.get(i)).getName().equals(name) && ((District) listToFind.get(i)).getCity().equals(city))
                    return (District) listToFind.get(i);
            }
        }
        return null;
    }

    public int getSize() {
        return size;
    }

    public Employees onlyEmployee(){
        // if there is only one employee in hash table return it
        for (LinkedList<Anytype> districtList : list) {
            if (districtList.size() == 1){
                return (Employees) districtList.get(0);
            }
        }
        return null;

    }


}
