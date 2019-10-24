package nullengine.command.completion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CompleteResult {

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

        public static CompleteResult onlyComplete(List<String> completeList) {
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