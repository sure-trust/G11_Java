package SureTrust;
import java.lang.String;
public class Mini_project_1 {
    public static void main(String[] args) {

        String names[] = {"yaseer","rammohan","shaiksha","kavya","renu"};
        for(int i=0; i<names.length; i++){
            if(i%2 != 0){
                names[i] = "SureTrust";
            }
            System.out.print(names[i] + " ");   
        }
    }
}
