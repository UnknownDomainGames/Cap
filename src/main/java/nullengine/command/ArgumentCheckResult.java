package nullengine.command;

import java.util.List;

public class ArgumentCheckResult {

        private String helpMessage;

        private boolean right;

        private List<Integer> wrongLocation;

        private ArgumentCheckResult(String helpMessage, boolean right, List<Integer> wrongLocation) {
            this.helpMessage = helpMessage;
            this.right = right;
            this.wrongLocation = wrongLocation;
        }

        public static ArgumentCheckResult Right(){
            return new ArgumentCheckResult(null,true,null);
        }

        public static ArgumentCheckResult Error(String helpMessage,List<Integer> wrongLocation){
            return new ArgumentCheckResult(helpMessage,false,wrongLocation);
        }

    public String getHelpMessage() {
        return helpMessage;
    }

    public boolean isRight() {
        return right;
    }

    public List<Integer> getWrongLocation() {
        return wrongLocation;
    }
}