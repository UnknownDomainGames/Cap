package engine.command.impl;

import engine.command.CommandParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DefaultCommandParser implements CommandParser {
    @Override
    public Result parse(String command) {
        List<String> args = new ArrayList<>();
        boolean quotes = false;
        boolean escape = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < command.length(); i++) {
            char c = command.charAt(i);

            if ((c == ' ' || c == '　') && !quotes) {
                args.add(sb.toString());
                sb = new StringBuilder();
            } else if (c == '"') {
                if (escape)
                    sb.append(c);
                else
                    quotes = !quotes;
            } else if (c == '\\') {
                if (escape) {
                    escape = false;
                    sb.append(c);
                } else {
                    escape = true;
                }
            } else {
                sb.append(c);
                escape = false;
            }
        }
        args.add(sb.toString());
        String[] argsArray = args.toArray(new String[0]);
        return new CommandParser.Result(argsArray[0], Arrays.copyOfRange(argsArray, 1, argsArray.length));
    }
}
