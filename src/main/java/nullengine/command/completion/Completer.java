package nullengine.command.completion;

import nullengine.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@FunctionalInterface
public interface Completer {
    CompleteResult complete(CommandSender sender, String command, String[] args);

    final class CompleteResult{

        public static CompleteResult EMPTY = new CompleteResult();
        // key:tip,value:complete text

        private List<String> tips = new ArrayList<>();

        private List<String> completeList = new ArrayList<>();

        public CompleteResult() {}

        public CompleteResult(List<String> completeList) {
            this.completeList = completeList;
        }

        public CompleteResult(List<String> tips, List<String> completeList) {
            this.tips = tips;
            this.completeList = completeList;
        }

        public Collection<String> getComplete(){
            return this.tips;
        }

        public Collection<String> getTips(){
            return completeList;
        }

    }

}
