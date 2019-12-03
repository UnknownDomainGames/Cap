package nullengine.command.util;

import java.util.List;
import java.util.stream.Collectors;

public class SuggesterHelper {

    public static List<String> filterStartWith(List<String> list,String start){
        return list.stream()
                .filter(s -> s.startsWith(start))
                .collect(Collectors.toList());
    }

}
