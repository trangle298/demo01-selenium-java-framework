package helpers;

import java.util.List;

public class SharedHelpers {

    public static String pickRandomFromList(List<String> list) {
        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }





}
