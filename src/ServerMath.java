import java.util.List;

/**
* ServerMath is the interface we'll be using to declare the API
* of a server that holds mathematical operations. Stateless server.
* 
* @author Raul Javierre, Eduardo Ruiz
* 
*/
public interface ServerMath {

    /**
     * <p>Counts the odd numbers of the array passed</p>
     * @param numbers an array of numbers
     * @return the number of odd numbers of the array passed
     */
    long number_of_odd(List<Integer> numbers);

    /**
     * <p>Calculates the fibonacci number of the integer passed</p>
     * @param number the number that we want to know fibonacci(number)
     * @return fibonacci(number)
     */
    int fibonacci(final int number);

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @param number the number that we want to know its collatz sequence
     * @return the collatz sequence of the integer passed
     */
    List<Integer> collatz_sequence(final int number);
}