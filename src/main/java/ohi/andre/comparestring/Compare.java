package ohi.andre.comparestring;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author francescoandreuzzi
 */
public class Compare {

    private static final int GREATER_RATE = 1000;
    private static final int LOWER_RATE = -100;
    private static final float COMPARE_BASE_VALUE = 1.5f;
    private static final float COMPARE_SUBTRACT_VALUE = 1.5f;
    private static final float LENGTH_SCALE = 0.2f;
    private static final String SPACE_REGEXP = "\\s";
    private static final String EMPTYSTRING = "";
    private static final String ACCENTS_PATTERN = "\\p{InCombiningDiacriticalMarks}+";
    
    /**
     *  Compare the {@code String} parameter to every element of the {@code String} Array, and
     *  returns the most similar {@code String}, or null if the greatest rate is lower
     *  than {@code minRate} 
     * 
     * @author Francesco Andreuzzi
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be returned
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return
     *  The most similar {@code String} object in the {@code String} set, or null if nothing was found
     */
    public static String similarString(String[] strings, String string, int minRate, boolean scrollCompare) {
        
        if(string == null || strings == null || strings.length == 0 || string.length() == 0) {
            return null;
        }
        
        int maxRate = LOWER_RATE, maxIndex = -1;
        for (int count = 0; count < strings.length; count++) {
            String currentString = strings[count];
            int rate = scrollCompare ? scrollComparison(currentString, string) : 
                    linearComparison(currentString, string);

            if (rate == GREATER_RATE) {
                return currentString;   
            }

            if (maxRate < rate) {
                maxRate = rate;
                maxIndex = count;
            }
        }

        if (maxRate < minRate || maxIndex == -1) {
            return null;
        }
        
        return strings[maxIndex];
    }
    
    /**
     *  Compare the {@code String} parameter with every element of the {@code String} Array, and
     *  returns the most similar {@code String}
     * 
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return
     *  The most similar {@code String} object in the {@code String} set, or null if nothing was found
     */
    public static String similarString(String[] strings, String string, boolean scrollCompare) {
        return similarString(strings, string, LOWER_RATE, scrollCompare);
    }
    
    /**
     *  Compare the {@code String} parameter with every element of the {@code String} Collection, and
     *  returns the most similar {@code String} object, or null if the greatest rate is lower
     *  than {@code minRate}
     * 
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be returned
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return
     *  The most similar {@code String} object in the {@code String} set, or null if nothing was found
     */
    public static String similarString(Collection<String> strings, String string, int minRate, boolean scrollCompare) {
        return similarString(strings.toArray(new String[strings.size()]), string, minRate, scrollCompare);
    }
    
    /**
     *  Compare the {@code String} parameter with every element of the {@code String} Collection, and
     *  returns the most similar {@code String} object
     * 
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return
     *  The most similar {@code String} object in the {@code String} set, or null if nothing was found
     */
    public static String similarString(Collection<String> strings, String string, boolean scrollCompare) {
        return similarString(strings.toArray(new String[strings.size()]), string, LOWER_RATE, scrollCompare);
    }
    
    /**
     *  Compare the {@code String} parameter with every element of the {@code String} Array, and
     *  returns every similar {@code String} object with a rate greater than {@code minRate}
     * 
     * @param similarStrings
     *  Similar {@code String} objects will be put here
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     */
    public static void similarStrings(List<String> similarStrings, String[] strings, String string, int minRate, 
            boolean scrollCompare) {
        
        if(similarStrings == null || strings == null || string == null || strings.length == 0 || string.length() == 0) {
            return;
        }
        
        for (String current : strings) {
            int result = scrollCompare ? scrollComparison(current, string) : linearComparison(current, string);
            if (result >= minRate)
                similarStrings.add(current);
        }
    }
    
    /**
     *  Compare the String parameter with every element of the String Collection, and
     *  returns every similar String object with a rate greater than {@code minRate}
     * 
     * @param similarStrings
     *  Similar {@code String} objects will be put here
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     */
    public static void similarStrings(List<String> similarStrings, Collection<String> strings, String string, 
            int minRate, boolean scrollCompare) {
        similarStrings(similarStrings, strings.toArray(new String[strings.size()]), string, minRate, scrollCompare);
    }
    
    /**
     *  Compare the {@code String} parameter with every element of the {@code String} Collection, and
     *  returns every similar {@code String} object with a rate greater than {@code minRate}
     * 
     * @param strings   
     *  The set of String
     * @param string
     *  The String used as comparison
     * @param minRate
     *  The lowest compare rate that a String has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return
     *  A list containing every similar String
     */
    public static List<String> similarStrings(String[] strings, String string, int minRate, boolean scrollCompare) {
        List<String> list = new ArrayList<>();
        similarStrings(list, string, minRate, scrollCompare);
        return list;
    }
    
    /**
     *  Compare the {@code String} parameter with every element of the {@code String} Collection, and
     *  returns every similar {@code String} object with a rate greater than {@code minRate}
     * 
     * @param strings   
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return
     *  A list containing all of the similar {@code String} objects
     */
    public static List<String> similarStrings(Collection<String> strings, String string, int minRate, boolean scrollCompare) {
        return similarStrings(strings.toArray(new String[strings.size()]), string, minRate, scrollCompare);
    }
    
    /**
     *  Returns a compare rate for every {@code String} object in the {@code String} Array (if the rate
     *  is equal or greater to {@code minRate}
     *  This is not sorted!
     *
     * @param compareInfoList
     *  The method will add here the {@code CompareInfo} objects
     * @param strings
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     */
    public static void compareInfo(List<CompareInfo> compareInfoList, String[] strings, String string, 
            int minRate, boolean scrollCompare) {
        
        if(compareInfoList == null || strings == null || string == null || strings.length == 0 || string.length() == 0) {
            return;
        }
        
        for (String current : strings) {
            int result = scrollCompare ? scrollComparison(current, string) : linearComparison(current, string);

            if (minRate == -1 || result >= minRate)
                compareInfoList.add(new CompareInfo(current, result));
        }
    }
    
    /**
     *  Returns a compare rate for every {@code String} object in the {@code String} Collection (if the rate
     *  is equal or greater to {@code minRate}
     *  This is not sorted!
     *
     * @param compareInfoList
     *  The method will add here the {@code CompareInfo} objects
     * @param strings
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     */
    public static void compareInfo(List<CompareInfo> compareInfoList, Collection<String> strings, String string, 
            int minRate, boolean scrollCompare) {
        compareInfo(compareInfoList, strings.toArray(new String[strings.size()]), string, minRate, scrollCompare);
    }
    
    /**
     *  Returns a compare rate for every {@code String} object in the {@code String} Array (if the rate
     *  is equal or greater to {@code minRate}
     *  This is not sorted!
     * 
     * @param strings
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return 
     *  A list containing the {@code CompareInfo} objects for every
     *  {@code String} object element in {@code String} Set
     */
    public static List<CompareInfo> compareInfo(String[] strings, String string, int minRate, boolean scrollCompare) {
        List<CompareInfo> infos = new ArrayList<>();
        compareInfo(infos, strings, string, minRate, scrollCompare);
        return infos;
    }
    
    /**
     *  Returns a compare rate for every {@code String} object in the {@code String} Collection (if the rate
     *  is equal or greater to {@code minRate}
     *  This is not sorted!
     * 
     * @param strings
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param minRate
     *  The lowest compare rate that a {@code String} object has to get to be considered similar
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return 
     *  A list containing the rates for every {@code String} object in the {@code String} Set
     */
    public static List<CompareInfo> compareInfo(Collection<String> strings, String string, int minRate, boolean scrollCompare) {
        return compareInfo(strings.toArray(new String[strings.size()]), string, minRate, scrollCompare);
    }
    
    /**
     *  Returns a compare rate for every {@code String} object in the {@code String} Collection
     *  This is not sorted!
     * 
     * @param strings
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return 
     *  A list containing the rates for every {@code String} object in the {@code String} Set
     */
    public static List<CompareInfo> compareInfo(String[] strings, String string, boolean scrollCompare) {
        return compareInfo(strings, string, LOWER_RATE, scrollCompare);
    }
    
    /**
     *  Returns a compare rate for every {@code String} object in the {@code String} Collection
     *  This is not sorted!
     * 
     * @param strings
     *  The set of {@code String} objects
     * @param string
     *  The {@code String} object used as comparison
     * @param scrollCompare
     *  Use scrolling compare mode
     * @return 
     *  A list containing the rates for every {@code String} object in the {@code String} Set
     */
    public static List<CompareInfo> compareInfo(Collection<String> strings, String string, boolean scrollCompare) {
        return compareInfo(strings, string, LOWER_RATE, scrollCompare);
    }
    
    /**
     *  Prepares and compares to {@code String} object in linear mode
     *
     * @param string1
     *  The first {@code String} object to be compared
     * @param string2
     *  The second {@code String} object to be compared
     * @return
     *  The compare rate of the two {@code String} objects
     */
    public static int linearComparison(String string1, String string2) {
        if(string1 == null || string2 == null || string1.length() == 0 || string2.length() == 0)
            return 0;
        
        string1 = prepareToCompare(string1);
        string2 = prepareToCompare(string2);
        
        String max = max(string1, string2);
        String min = min(string1, string2);

        float equalness = linearCompare(max, min);
        if (equalness == min.length() * COMPARE_BASE_VALUE)
            return GREATER_RATE;
        
        return (int) equalness;
    }
    
    /**
     *  Prepares and compares to {@code String} object in scrolling mode
     *
     * @param string1
     *  The first {@code String} object to be compared
     * @param string2
     *  The second {@code String} object to be compared
     * @return
     *  The compare rate of the two {@code String} objects
     */
    public static int scrollComparison(String string1, String string2) {
        if(string1 == null || string2 == null || string1.length() == 0 || string2.length() == 0)
            return 0;
        
        string1 = removeAccents(string1);
        string2 = removeAccents(string2);
        
        string1 = removeSpaces(string1);
        string1 = string1.toLowerCase();
        string2 = removeSpaces(string2);
        string2 = string2.toLowerCase();

        String max = max(string1, string2);
        String min = min(string1, string2);

        return scrollCompare(max, min);
    }
    
    /**
     *  Compares to {@code String} object in linear mode
     *
     * @param max
     *  The longer {@code String} object to be compared
     * @param min
     *  The shorter {@code String} object to be compared
     * @return
     *  The compare rate of two {@code String} objects
     */
    private static int linearCompare(String max, String min) {
        
        float n = 0;
        boolean[] steps = {false, false, false};

        for (int count = 0; count < min.length(); count++) {
            char c = min.charAt(count);

            if (!steps[0] && count > 0 && c == max.charAt(count - 1)) {
                n += COMPARE_BASE_VALUE;
                steps[0] = true;
            } else if (!steps[1] && c == max.charAt(count)) {
                n += COMPARE_BASE_VALUE;
                steps[1] = true;
            } else if (!steps[2] && count + 1 < max.length() && c == max.charAt(count + 1)) {
                n += COMPARE_BASE_VALUE;
                steps[2] = true;
            } else 
                n -= COMPARE_SUBTRACT_VALUE;
            
            steps[0] = steps[1];
            steps[1] = steps[2];
            steps[2] = false;
        }
        
        if(max.length() > min.length())
            n -= (float) (max.length() - min.length()) * LENGTH_SCALE;

        return (int) n;
    }
    
    /**
     *  Compares to {@code String} object in scrolling mode
     *
     * @param max
     *  The longer {@code String} object to be compared
     * @param min
     *  The shorter {@code String} object to be compared
     * @return
     *  The compare rate of two {@code String} objects
     */
    public static int scrollCompare(String max, String min) {
        if(max.length() < min.length())
            return 0;

        max = removeAccents(max);
        min = removeAccents(min);
        
        max = removeSpaces(max);
        max = max.toLowerCase();
        min = removeSpaces(min);
        min = min.toLowerCase();
        
        int n = LOWER_RATE;
        for(int indexOnMax = 0; indexOnMax < (max.length() - min.length()) + 1; indexOnMax++) {
            
            float x = linearCompare(max.substring(indexOnMax, min.length() + indexOnMax), min);
            x -= ((float) indexOnMax * 2f) / 3f;
            
            if((int) x > n)
                n = (int) x;
        }
        return n;
    }

    /**
     *  Utility method to get the first {@code String} in an alphabetical order
     *
     * @param s1
     * @param s2
     * @return
     *  -1 if s1 comes before s2, 1 if s2 comes before s1 and 0 if they're equal
     */
    public static int alphabeticCompare(String s1, String s2) {
        String cmd1 = removeSpaces(s1);
        cmd1 = cmd1.toLowerCase();
        String cmd2 = removeSpaces(s2);
        cmd2 = cmd2.toLowerCase();

        for (int count = 0; count < cmd1.length() && count < cmd2.length(); count++) {
            if (cmd1.charAt(count) < cmd2.charAt(count)) {
                return -1;
            } else if (cmd1.charAt(count) > cmd2.charAt(count)) {
                return 1;
            }
        }

        if (cmd1.length() > cmd2.length()) {
            return 1;
        } else if (cmd1.length() < cmd2.length()) {
            return -1;
        }
        return 0;
    }

    /**
     *  Remove all of the spaces from a {@code String} object
     *
     * @param string
     * @return
     *  A {@code String} object that does not contain spaces
     */
    public static String removeSpaces(String string) {
        return string.replaceAll(SPACE_REGEXP, EMPTYSTRING);
    }
    
    /**
     *  Replace accented characters in a {@code String} object with
     *  their no-accented representation
     *
     * @param s
     * @return
     *  A {@code String} object that does not contain accented characters
     */
    public static String removeAccents(String s) {
        Pattern pattern = Pattern.compile(ACCENTS_PATTERN);
        String decomposed = Normalizer.normalize(s, Form.NFD);
        return pattern.matcher(decomposed).replaceAll(EMPTYSTRING);
    }
    
    /**
     *  Get the longer {@code String} object 
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String max(String s1, String s2) {
        return s1.length() > s2.length() ? s1 : s2;
    }
    
    /**
     *  Get the shorter {@code String} object
     *
     * @param s1
     * @param s2
     * @return
     */
    public static String min(String s1, String s2) {
        return s1.length() <= s2.length() ? s1 : s2;
    }
    
    /**
     *  Adjust a {@code String} object to be compared.
     *
     * @param s
     * @return
     *  A lower case {@code String} object without spaces and
     *  accent
     */
    private static String prepareToCompare(String s) {
        s = removeAccents(s);
        s = removeSpaces(s);
        s = s.toLowerCase();
        return s;
    }

    /**
     * 
     *  Represents the result of a Comparison
     *
     * @author Francesco Andreuzzi
     */
    public static class CompareInfo implements Comparable<CompareInfo> {

        /**
         * The compared {@code String} object
         */
        public String s;

        /**
         * The rate that was given to the compared object
         */
        public int rate;
        
        /**
         *  Construct a new instance of {@code CompareInfo}
         *
         * @param st
         *  The compared {@code String} object
         * @param i
         *  The comparison rate
         */
        public CompareInfo(String st, int i) {
            this.s = st;
            this.rate = i;
        }

        @Override
        public int compareTo(CompareInfo o) {
            return this.rate > o.rate ? -1 : this.rate == o.rate ? 0 : 1;
        }
    }   
    
}