import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

/**
 * ServerMathImpl is the class that implements the API
 * of a server that holds mathematical operations. Stateless server.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class ServerMathImpl extends ServerImpl implements ServerMath {


    /**
     * Class constructor.
     */
    public ServerMathImpl(String name, String IP_port) throws RemoteException {
        super(name, IP_port);
    }

    /**
     * <p>Counts the odd numbers of the array passed</p>
     * @param numbers an array of numbers
     * @return the number of odd numbers of the array passed
     */
    public long number_of_odd(List<Integer> numbers) {
        return numbers.stream()
                .mapToInt(Integer::valueOf)
                .filter(ServerMathImpl::isOdd)
                .count();
    }

    /**
     * <p>Returns if the integer passed is an odd number</p>
     * @param number the number that we want to know its parity
     * @return true if the number is odd. False otherwise
     */
    private static boolean isOdd(final int number) {
        return number % 2 != 0;
    }

    /**
     * <p>Calculates the fibonacci number of the integer passed</p>
     * @param number the number that we want to know fibonacci(number)
     * @return fibonacci(number) if 0 < number. 0 Otherwise
     */
    public int fibonacci(final int number) {
        int response = 0;
        if (number > 0) {
            response = recursive_fibonacci(number);
        }
        return response;
    }

    /**
     * <p>Calculates the fibonacci number of the integer passed in a recursive way</p>
     * @param number the number that we want to know fibonacci(number)
     * @return fibonacci(number)
     */
    private int recursive_fibonacci(final int number) {
        if (number > 1) {
            return recursive_fibonacci(number - 1) + recursive_fibonacci(number - 2);
        }
        else if (number == 1) {
            return 1;
        }
        else {
            return 0;
        }
    }

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @param number the number that we want to know its collatz sequence
     * @return the collatz sequence of the integer passed
     */
    public List<Integer> collatz_sequence(final int number) {
        int actual = number;
        List<Integer> response = new ArrayList<>();
        response.add(actual);
        while (actual != 1) {
            actual = nextCollatz(actual);
            response.add(actual);
        }
        return response;
    }

    /**
     * <p>Returns the next term of number in a collatz sequence</p>
     * @param number the number that we want to know its next collatz number
     * @return the next collatz number of the integer passed
     */
    private int nextCollatz(Integer number) {
        if(number == 1){
            return 1;
        }
        if(number % 2 == 0){
            return number / 2;
        }
        else{
            return 3 * number + 1;
        }
    }

    @Override
    public Object execute_sync_service(String service_name, List<Object> parameters) {
        switch (service_name) {
            case "fibonacci":
                return fibonacci(Integer.parseInt((String) parameters.get(0)));
            case "number_of_odd":
                return number_of_odd((List<Integer>) (Object) parameters);
            case "collatz_sequence":
                return collatz_sequence(Integer.parseInt((String) parameters.get(0)));
            default:
                return -1;
        }
    }

    @Override
    public Object execute_async_service(String service_name, List<Object> parameters) {
        if(service_name.equals("fibonacci")) {
            return fibonacci(Integer.parseInt((String) parameters.get(0)));
        }
        else {
            return 0;
        }
    }

    /**
     * <p>Executes a server that does mathematical operations on demand</p>
     * @param args arguments passed to main program (not used)
     */
    public static void main(String[] args){
        // Setting the directory of java.policy
        System.setProperty("java.security.policy", "java.policy");

        // Creating the security manager
        System.setSecurityManager(new SecurityManager());

        // Where we are... IP:PORT or NAME (with DNS). RMI uses 1099 by default
        String hostName = "127.0.0.1:5001";

        try {
            // Creating remote object
            ServerImpl obj = new ServerMathImpl("ServerMath", hostName);
            System.out.println(obj.getName() + " created!");

            // Registering remote object
            Naming.rebind("//" + obj.getIP_port() + "/" + obj.getName(), obj);
            System.out.println(obj.getName() + " registered at " + obj.getIP_port() + "!");

            // Searching the broker
            Broker broker = (Broker) Naming.lookup("//" + "127.0.0.1:5000" + "/" + "Broker_R_E");

            // Registering the server in the broker
            broker.register_server(obj.getName(), obj.getIP_port());

            // Registering some services in the broker
            broker.register_service(obj.getName(), "number_of_odd", Collections.singletonList("List<Integer>"), "long");
            broker.register_service(obj.getName(), "fibonacci", Collections.singletonList("int"), "int");
            broker.register_service(obj.getName(), "collatz_sequence", Collections.singletonList("int"), "List<Integer>");

            // Printing list of services available (just for debugging)
            System.out.println(broker.getListOfServices());

            // Testing our own services
            Long odd_numbers = (Long) broker.execute_sync_service("ServerMath", "number_of_odd", Arrays.asList(new Object[]{1, 4, 1, 5}));
            System.out.println("Result of odd_numbers: " + odd_numbers);

            int fib_of_3 = (int) broker.execute_sync_service("ServerMath", "fibonacci", Collections.singletonList("3"));
            System.out.println("Result of fibonacci(3): " + fib_of_3);

            List<Integer> collatz_sequence = (List<Integer>) broker.execute_sync_service("ServerMath", "collatz_sequence", Collections.singletonList("13"));
            System.out.println("Result of collatz_sequence(13): " + printOneList(collatz_sequence));

            // Deleting one service
            broker.delete_service(obj.getName(), "fibonacci");
            System.out.println(broker.getListOfServices());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String printOneList(List<Integer> list) {
        StringBuilder result = new StringBuilder();
        for (Integer element : list) {
            result.append(element).append(" ");
        }
        return result.toString();
    }
}