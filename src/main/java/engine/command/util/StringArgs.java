package engine.command.util;

import java.util.Arrays;

public class StringArgs {

    private final String[] args;

    private int index;

    public StringArgs(String[] args) {
        this.args = args;
    }

    public String next() {
        return args[index++];
    }

    public boolean hasNext() {
        return index < args.length;
    }

    public int getLength() {
        return args.length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "StringArgs{" +
                "args=" + Arrays.toString(args) +
                ", index=" + index +
                '}';
    }
}
