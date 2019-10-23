package nullengine.command.completion;

import nullengine.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@FunctionalInterface
public interface Completer {
    CompleteResult complete(CommandSender sender, String command, String[] args);

    final class CompleteResult {

        public static CompleteResult EMPTY = new CompleteResult();

        private List<String> completeList = new ArrayList<>();
        private List<String> tips = new ArrayList<>();

        public CompleteResult() {
        }

        public CompleteResult(List<String> completeList) {
            this.completeList = completeList;
        }

        public CompleteResult(List<String> completeList, List<String> tips) {
            this.tips = tips;
            this.completeList = completeList;
        }

        public Collection<String> getComplete() {
            return this.completeList;
        }

        public Collection<String> getTips() {
            return tips;
        }

        public static CompleteResult completeResult(List<String> completeList) {
            return new CompleteResult(completeList);
        }

        @Override
        public String toString() {
            return "CompleteResult{" +
                    "completeList=" + completeList +
                    ", tips=" + tips +
                    '}';
        }
    }

}
