package nl.iobyte.nodalcommunication.discovery.namespace;

import nl.iobyte.nodalcommunication.namespace.NamespaceComparator;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamespaceSet extends HashSet<String> {

    /**
     * {@inheritDoc}
     * @param s String
     * @return Boolean
     */
    public boolean add(String s) {
        return super.add(s.toLowerCase(Locale.ROOT));
    }

    /**
     * Get first entry matching namespace
     * @param namespace String
     * @return String
     */
    public String first(String namespace) {
        assert namespace != null;

        //Return first if wildcard
        if("**".equals(namespace))
            for(String str : this)
                return str;

        //Regex
        Pattern pattern = Pattern.compile(namespace.toLowerCase(Locale.ROOT).replaceAll("\\*+", "(.*)"));
        Matcher matcher = pattern.matcher("");

        //Find first match
        for(String str : this)
            if(matcher.reset(str).matches())
                return str;

        return null;
    }

    /**
     * Get entries matching namespace
     * @param namespace String
     * @return Set<String>
     */
    public Set<String> get(String namespace) {
        assert namespace != null;

        //Return all if wildcard
        if("**".equals(namespace))
            return new HashSet<>(this);

        //Regex
        Pattern pattern = Pattern.compile(namespace.toLowerCase(Locale.ROOT).replaceAll("\\*+", "(.*)"));
        Matcher matcher = pattern.matcher("");

        //Find matches
        Set<String> set = new HashSet<>();
        for(String str : this)
            if(matcher.reset(str).matches())
                set.add(str);

        return set;
    }

    /**
     * Remove entries matchin namespace
     * @param namespace String
     * @return Boolean
     */
    public boolean remove(String namespace) {
        boolean b = false;
        for(String str : get(namespace))
            if(super.remove(str))
                b = true;

        return b;
    }

}
