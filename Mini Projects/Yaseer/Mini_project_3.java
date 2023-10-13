import java.util.HashMap;

public class Mini_project_3 {
    static void checkAnagram(String s1, String s2) {
        if (s1.length() != s2.length()) {
            System.out.println("Length of two strings are not equal");
        }

        HashMap<String,Integer> map = new HashMap<String,Integer>();
        for(int i = 0 ;i<s1.length(); i++){
            String currentChar = String.valueOf(s1.charAt(i));
            if(map.containsKey(currentChar)){
                int currentCount = map.get(currentChar);
                map.put(currentChar, currentCount + 1);
            }
            else {
                map.put(currentChar, 1);
            }
        }
        System.out.println(map);
        for(int j = 0; j<s2.length(); j++){
            String currentChar2 = String.valueOf(s2.charAt(j));
            if(map.containsKey(currentChar2)){
                int currentCount2 = map.get(currentChar2);
                if(currentCount2 > 1){
                map.put(currentChar2, currentCount2 - 1);
                }else{ map.remove(currentChar2);}
           } 
        }
        System.out.println(map);
        System.out.println(map.isEmpty());
        if(map.isEmpty()) System.out.println("Given strings are Anaagram");
        else System.out.println("Given strings are not anaagram");
    }

    public static void main(String[] args) {
        String s1;
        String s2;
        s1 = "abcd";
        s2 = "acdd";
        Anaagram.checkAnagram(s1, s2);
            
    }

}
