package ohi.andre.comparestring;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class Compare {

    private static final int MAXRATE = 1000;
    
    public static String getOneSimilarString(String[] strings, String string, int minRate, boolean scroll) {
        int max = -100, index = -1;
        for (int count = 0; count < strings.length; count++) {
            String current = strings[count];
            int result = scroll ? scrollCompareTwoStrings(current, string) : linearCompareTwoStrings(current, string);

            if (result == Compare.MAXRATE) 
                return strings[count];

            if (max < result) {
                max = result;
                index = count;
            }
        }

        if (max < minRate || index == -1) 
            return null;
        return strings[index];
    }
    
    public static String getOneSimilarString(Collection<String> strings, String string, int minRate, boolean scroll) {
        String[] array = new String[strings.size()];
        return getOneSimilarString(strings.toArray(array), string, minRate, scroll);
    }
    
    public static void getCompareInfo(List<CompareInfo> addCompareInfosHere, String[] strings, String string, 
            int minRate, boolean scroll) {
        for (String current : strings) {
            int result = scroll ? scrollCompareTwoStrings(current, string) : linearCompareTwoStrings(current, string);

            if (minRate == -1 || result >= minRate)
                addCompareInfosHere.add(new CompareInfo(current, result));
        }
    }
    
    public static void getCompareInfo(List<CompareInfo> addCompareInfosHere, Collection<String> strings, String string, 
            int minRate, boolean scroll) {
        getCompareInfo(addCompareInfosHere, strings.toArray(new String[strings.size()]), string, minRate, scroll);
    }
    
    public static List<CompareInfo> getCompareInfos(String[] strings, String string, int minRate, boolean scroll) {
        List<CompareInfo> infos = new ArrayList<>();
        
        for (String current : strings) {
            int result = scroll ? scrollCompareTwoStrings(current, string) : linearCompareTwoStrings(current, string);

            if (minRate == -1 || result >= minRate)
                infos.add(new CompareInfo(current, result));
        }
        
        return infos;
    }
    
    public static List<CompareInfo> getCompareInfos(Collection<String> strings, String string, int minRate, boolean scroll) {
        return getCompareInfos(strings.toArray(new String[strings.size()]), string, minRate, scroll);
    }
    
    public static List<CompareInfo> getCompareInfos(String[] strings, String string, boolean scroll) {
        return getCompareInfos(strings, string, -1, scroll);
    }
    
    public static List<CompareInfo> getCompareInfos(Collection<String> strings, String string, boolean scroll) {
        return getCompareInfos(strings, string, -1, scroll);
    }
    
    public static List<String> getAllSimilarStrings(String[] strings, String string, int minRate, boolean scroll) {
        List<CompareInfo> infos = new ArrayList<>();
        
        for (String current : strings) {
            int result = scroll ? scrollCompareTwoStrings(current, string) : linearCompareTwoStrings(current, string);

            if (result >= minRate)
                infos.add(new CompareInfo(current, result));
        }
        
        List<String> list = new ArrayList<>();
        for(CompareInfo i : infos)
            list.add(i.s);
        
        return list;
    }
    
    public static List<String> getAllSimilarStrings(Collection<String> strings, String string, int minRate, boolean scroll) {
        return getAllSimilarStrings(strings.toArray(new String[strings.size()]), string, minRate, scroll);
    }
    
    public static void getAllSimilarStrings(List<String> addStringsHere, Collection<String> strings, String string, 
            int minRate, boolean scroll) {
        getAllSimilarStrings(addStringsHere, strings.toArray(new String[strings.size()]), string, minRate, scroll);
    }
    
    public static void getAllSimilarStrings(List<String> addStringsHere, String[] strings, String string, int minRate, 
            boolean scroll) {
        if(addStringsHere == null)
            return;
        
        List<CompareInfo> infos = new ArrayList<>();
        for (String current : strings) {
            int result = scroll ? scrollCompareTwoStrings(current, string) : linearCompareTwoStrings(current, string);

            if (result >= minRate)
                infos.add(new CompareInfo(current, result));
        }
        
        for(CompareInfo i : infos)
            addStringsHere.add(i.s);
    }
    
    public static int linearCompareTwoStrings(String string1, String string2) {
        if(string1 == null || string2 == null || string1.length() == 0 || string2.length() == 0)
            return 0;
        
        string1 = removeAccents(string1);
        string2 = removeAccents(string2);
        
        string1 = removeSpaces(string1);
        string1 = string1.toLowerCase();
        string2 = removeSpaces(string2); 
        string2 = string2.toLowerCase();

        String max = string1.length() > string2.length() ? string1 : string2;
        String min = string1.length() <= string2.length() ? string1 : string2;

        int equalness = linearCompare(max, min);
        // * 1.5 because add 1.5 in linearCompare 
        if (equalness == (int) (min.length() * 1.5))
            return Compare.MAXRATE;
        
        return equalness;
    }
    
    public static int scrollCompareTwoStrings(String string1, String string2) {
        if(string1 == null || string2 == null || string1.length() == 0 || string2.length() == 0)
            return 0;
        
        string1 = removeAccents(string1);
        string2 = removeAccents(string2);
        
        string1 = removeSpaces(string1);
        string1 = string1.toLowerCase();
        string2 = removeSpaces(string2);
        string2 = string2.toLowerCase();

        String max = string1.length() > string2.length() ? string1 : string2;
        String min = string1.length() <= string2.length() ? string1 : string2;

        return scrollCompare(max, min);
    }
    
    private static int linearCompare(String max, String min) {
        if(max.length() < min.length())
            return 0;
        
        float n = 0;
        boolean[] steps = {false, false, false};

        for (int count = 0; count < min.length(); count++) {
            char c = min.charAt(count);

            if (!steps[0] && count > 0 && c == max.charAt(count - 1)) {
                n += 1.5;
                steps[0] = true;
            } else if (!steps[1] && c == max.charAt(count)) {
                n += 1.5;
                steps[1] = true;
            } else if (!steps[2] && count + 1 < max.length() && c == max.charAt(count + 1)) {
                n += 1.5;
                steps[2] = true;
            } else 
                n -= 0.5;
            
            steps[0] = steps[1];
            steps[1] = steps[2];
            steps[2] = false;
        }
        
        if(max.length() > min.length())
            n -= (float) (max.length() - min.length()) * 0.2f;

        return (int) n;
    }
    
        
    public static int scrollCompare(String max, String min) {
        if(max.length() < min.length())
            return 0;

        max = removeAccents(max);
        min = removeAccents(min);
        
        max = removeSpaces(max);
        max = max.toLowerCase();
        min = removeSpaces(min);
        min = min.toLowerCase();
        
        int n = -100;
        for(int indexOnMax = 0; indexOnMax < (max.length() - min.length()) + 1; indexOnMax++) {
            
            int x = linearCompare(max.substring(indexOnMax, min.length() + indexOnMax), min);
            x -= indexOnMax * 2 / 3;
            
            if(x > n)
                n = x;
        }
        return n;
    }

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

    public static String removeSpaces(String string) {
        return string.replaceAll("\\d", "");
    }
    
    public static String removeAccents(String s) {
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String decomposed = Normalizer.normalize(s, Form.NFD);
        return pattern.matcher(decomposed).replaceAll("");
    }

    public static class CompareInfo implements Comparable<CompareInfo> {
        public String s;
        public int rate;
        
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

