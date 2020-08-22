import java.util.ArrayList;

public class GeneratePermutations {
    /**
     * Space - O(N!) ==> the number of possible permutations for a string of length N
     * Time - O(S * P! ) ==>P is possible permutations for string of length S
     * @param s string to be permuted
     * @return a list of permutations
     */
    static ArrayList<String> generatePermutations(String s) {
        ArrayList<String> perms = new ArrayList<>();

        if (s.length() <= 1) {
            perms.add(s);
            return perms;
        }

        String lastCh = s.substring(s.length() - 1);
        ArrayList<String> permutations = generatePermutations(s.substring(0, s.length() - 1));

        for (String perm: permutations) {
            for (int i = 0; i <= perm.length(); i++) {
                String newPerm = perm.substring(0, i) + lastCh + perm.substring(i);
                perms.add(newPerm);
            }
        }

        return perms;
    }

    public static void main(String[] args) {
        String a = "abcd";
        ArrayList<String> aPerms = generatePermutations(a);

        System.out.println(aPerms.toString());
    }
}
