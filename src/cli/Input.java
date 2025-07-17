package cli;

import util.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import java.util.Date;

public class Input {
  public static int readInt(String prompt, Scanner sc) {
    while (true) {
      System.out.print(Color.BLUE + "▸ " + prompt + Color.RESET);
      try {
        return Integer.parseInt(sc.nextLine());
      } catch (NumberFormatException e) {
        System.out.println(Color.RED + "✗ " + "Enter a valid number." + Color.RESET);
      }
    }
  }

  public static double readDouble(String prompt, Scanner sc) {
    while (true) {
      System.out.print(Color.BLUE + "▸ " + prompt + Color.RESET);
      try {
        return Double.parseDouble(sc.nextLine());
      } catch (NumberFormatException e) {
        System.out.println(Color.RED + "✗ " + "Enter a valid decimal." + Color.RESET);
      }
    }
  }

  public static String readNonEmpty(String prompt, Scanner sc) {
    String inp;
    do {
      System.out.print(Color.BLUE + "▸ " + prompt + Color.RESET);
      inp = sc.nextLine().trim();
      if (inp.isEmpty())
        System.out.println(Color.RED + "✗ " + "Cannot be empty." + Color.RESET);
    } while (inp.isEmpty());
    return inp;
  }

  public static String read(String prompt, Scanner sc) {
    System.out.print(Color.BLUE + "▸ " + prompt + Color.RESET);
    return sc.nextLine().trim();
  }

  public static Date readDate(String prompt, Scanner sc) {
    while (true) {
      try {
        System.out.print(Color.BLUE + "▸ " + prompt + Color.RESET);
        String input = sc.nextLine();
        return new SimpleDateFormat("yyyy-MM-dd").parse(input);
      } catch (ParseException e) {
        System.out.println(Color.RED + "✗ " + "Invalid date. Use format yyyy-MM-dd." + Color.RESET);
      }
    }
  }

}
