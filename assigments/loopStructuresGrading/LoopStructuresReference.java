import java.util.Scanner;

public class LoopStructuresReference {

  public static void task0() {
    for(int i = 1; i <= 100; i++) {
        if(i % 3 == 0 && i % 5 == 0) {
            System.out.println("FizzBuzz");
        }
        else if(i % 3 == 0) {
            System.out.println("Fizz");
        }
        else if(i % 5 == 0) {
            System.out.println("Buzz");
        }
        else {
            System.out.println(i);
        }
    }
  }

  public static void task1() {
    Scanner input = new Scanner(System.in);

    int continued = 0;
    String line = "";
    while(!line.equals("no")) {
        System.out.print("Would you like to continue? ");
        line = input.nextLine();
        continued++;
    }
    input.close();

    System.out.printf("You continued %d times!\n",continued - 1);
  }

  public static void task2() {
    Scanner input = new Scanner(System.in);
    System.out.print("How tall of a pyramid? ");
    int n = input.nextInt();
    
    for(int i = 0; i < n; i++) {
        for(int j = 0; j <= i; j++) {
            System.out.print("*");
        }
        System.out.println();
    }

    input.close();
  }
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("You must provide a task to run!");
    }
    else {
        try {
        int taskn = Integer.parseInt(args[0]);
        switch(taskn) {
            case 0: task0(); break;
            case 1: task1(); break;
            case 2: task2(); break;
            default:
            System.out.println("Invalid task number "+args[0]);
            break;
        }
        } catch(NumberFormatException e) {
        System.out.println(args[0] + " is not a valid number.");
        }
    }
  }
}
