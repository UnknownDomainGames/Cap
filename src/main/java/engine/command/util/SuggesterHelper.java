package engine.command.util;

import java.util.List;
import java.util.stream.Collectors;

public class SuggesterHelper {

    public static List<String> filterStartWith(List<String> list, String start) {
        return list.stream()
                .filter(s -> s.startsWith(start))
                .collect(Collectors.toList());
    }

    public static List<String> filterStartWithLastString(List<String> list, String[] array) {
        if(array.length==0)
            return List.of();
        return list.stream()
                .filter(s -> s.startsWith(array[array.length - 1]))
                .collect(Collectors.toList());
    }


}
