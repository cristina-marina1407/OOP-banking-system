package org.poo.commands.helperMethods;

public class CompareTypesHelper {
    private CompareTypesHelper() {

    }

    /**
     * Compare two types
     *
     * @param type1 the first type
     * @param type2 the second type
     * @return true if the second type is an upgrade plan, false otherwise
     */

    public static boolean compareTypes(final String type1, final String type2) {
        if (type1.equals("student") && type2.equals("silver")) {
            return true;
        } else if (type1.equals("student") && type2.equals("gold")) {
            return true;
        } else if (type1.equals("standard") && type2.equals("silver")) {
            return true;
        } else if (type1.equals("standard") && type2.equals("gold")) {
            return true;
        } else if (type1.equals("silver") && type2.equals("gold")) {
            return true;
        }
        return false;
    }
}