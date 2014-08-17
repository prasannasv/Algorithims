import java.util.*;

public class AllCaseCombinations {
  public static void main(String[] args) {
    System.out.println(new AllCaseCombinations().enlistAll(args[0]));
  }

  private List<String> enlistAll(String s) {
    char[] text = s.toCharArray();
    List<String> allCombinations = new LinkedList<String>();
    enlistAll(text, 0, allCombinations);
    return allCombinations;
  }

  private void enlistAll(char[] text, int pos, List<String> allCombinations) {
    if (pos == text.length) {
      allCombinations.add(new String(text));
      return;
    }
    enlistAll(text, pos + 1, allCombinations);
    text[pos] = toggleCase(text[pos]);
    enlistAll(text, pos + 1, allCombinations);
    text[pos] = toggleCase(text[pos]);
  }

  private char toggleCase(char c) {
    return Character.isUpperCase(c) ? Character.toLowerCase(c) : Character.toUpperCase(c);
  }
}
