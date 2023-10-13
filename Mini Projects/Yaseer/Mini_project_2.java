import java.util.Collections;
import java.util.HashSet;
import java.util.*;
//! REMOVING REPEATING CHARACTERS IN AN STRING BY USING SET;

public class Mini_project_2{
    public static void main(String[] args) {
        String name = "yaseer";
        RemovingUsingSet obj = new RemovingUsingSet();
        String answer = obj.removeDup(name);
        System.out.println(answer);
        // answer.contains("arsey");
        // System.out.println(answer.CASE_INSENSITIVE_ORDER);

    }

    public String removeDup(String name) {
        HashSet<Character> set = new HashSet<Character>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            Character ch = name.charAt(i);
            if (!set.contains(ch)) {
                set.add(ch);
                sb.append(ch);
            }
        }
        // System.out.println(set);
        // System.out.println(c    );
        // System.out.println(sb);
        return String.valueOf(sb);
    }
}