package nl.iobyte.nodalcommunication.generic.namespace;

import java.util.Locale;

public class NamespaceComparator {

    private final String o2;

    public NamespaceComparator(String base) {
        o2 = base.toLowerCase(Locale.ROOT);
    }

    public int compare(String o1) {
        assert o1 != null;

        //Check if wildcard
        if("**".equals(o1))
            return 1;

        if(o1.length() > o2.length())
            return -1;

        //Lowercase
        o1 = o1.toLowerCase(Locale.ROOT);

        //Check if same
        if(o1.equals(o2))
            return 0;

        return o2.matches(o1.replaceAll("\\*+", "(.*)")) ? 1 : -1;
    }

}
