import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;

/**
 * Calculates mathematical expressions using the Shunting Yard algorithm. BEDMAS works.
 */
public class Calc {
    /**
     * Store precedence in a hashmap. Follows BEDMAS
     */
    private static final HashMap<String, Integer> precedence = new HashMap<>();

    static { //Static block to initialize precedences
        precedence.put("+",1); //LOWEST
        precedence.put("-",1);
        precedence.put("*",2);
        precedence.put("/",2);
        precedence.put("^",3); //HIGHEST
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String in = scanner.nextLine();
        long begin = System.nanoTime();
        System.out.println(calculate(in));
        System.out.println(System.nanoTime()-begin);
        scanner.close();
    }

    /**
     * Calculator with order of operations
     * @param in Input to evaluate
     * @return The answer
     */
    public static double calculate(String in){
        Stack<Double> value = new Stack<>();
        Stack<String> operator = new Stack<>();
        String[] tokens = in.split(" ");

        //1
        for (String token : tokens){
            if (isNumeric(token)) { //Push num onto value stack
                value.push(Double.parseDouble(token));
            } else if (token.equals("(")){ //Push left parenthesis onto value stack
                operator.push("(");
            } else if (token.equals(")")){ //If right parenthesis, do the following:
                while (!operator.peek().equals("(")){

                    //While the thing on top of the operator stack is not a left parenthesis

                    String op = operator.pop();
                    double y = value.pop();
                    double x = value.pop();
                    value.push(compute(x, y, op)); //Push result back into value stack
                }
                operator.pop(); //Pop the left parenthesis from the operator stack, and discard it
            } else if (isOperator(token)){ //If token is operator, do the following:
                while (!operator.isEmpty() && isOperator(operator.peek()) && precedence.get(operator.peek()) >= precedence.get(token)){

                    //While the operator stack is not empty, and the top thing on the operator stack has the same or
                    //greater precedence as the current operator
                    //Also check if the top of the operator stack is actually an operator. Could be parenthesis!

                    String op = operator.pop();
                    double y = value.pop();
                    double x = value.pop();
                    value.push(compute(x, y, op)); //Push result back into value stack
                }
                operator.push(token); //Push thisOp onto the operator stack
            } else
                throw new RuntimeException("INVALID TOKEN");
        }

        //2
        while (!operator.isEmpty()){ //While the operator stack is not empty, do the following:
            String op = operator.pop();
            double y = value.pop();
            double x = value.pop();
            value.push(compute(x, y, op)); //Push result back into value stack
        }
        //3
        return value.pop();

        //At this point the operator stack should be empty, and the value stack should have
        //only one value in it, which is the final result
    }

    /**
     * Compute the 2 numbers
     * @param x First num
     * @param y Second num
     * @param op Operator
     * @return Answer to expression
     */
    private static double compute(double x, double y, String op){
        return switch (op) {
            case "+" -> x + y;
            case "-" -> x - y;
            case "*" -> x * y;
            case "/" -> x / y;
            case "^" -> Math.pow(x, y);
            default -> 0;
        };
    }

    /**
     * Checks if token is a valid number. Checks one token at a time.
     * @param s Token to check
     * @return If the token is a valid number
     */
    private static boolean isNumeric(String s){
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
        // EX NUM: +5.35445
        // [-+]? means optional sign (+,- or none) Only 0 or 1 occurrence is allowed
        // \d* means optional digits before decimal. Zero or more digits before it
        // \.? means decimal point is optional. \ is used to detect ".", not use it as a metacharacter.
        // \d+ means at least one digit at the end

        // Double Backslash is used to double escape
        // EX: \\d becomes regex \d, b/c Java sees \\ and turns it into \
    }

    /**
     * Determines if the token is an operator. Checks one token at a time
     * @param s Token to check
     * @return If the token is a valid operator
     */
    private static boolean isOperator(String s){
        return precedence.containsKey(s); //Check for key in hashmap
    }
}