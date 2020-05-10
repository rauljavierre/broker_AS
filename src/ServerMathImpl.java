import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

import static java.lang.Thread.sleep;

/**
 * ServerMathImpl is the class that implements the API
 * of a server that holds mathematical operations. Stateless server.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class ServerMathImpl extends ServerImpl implements ServerMath {

    private static final long serialVersionUID = 4L;            //Default serial version uid


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
    public Object execute_service(String service_name, List<Object> parameters) {
        switch (service_name) {
            case "fibonacci":
                try {
                    return fibonacci(Integer.parseInt((String) parameters.get(0)));
                }
                catch (NumberFormatException | ClassCastException ex) {
                    return "Bad call";
                }
            case "number_of_odd":
                try {
                    List<Integer> list = new ArrayList<>();
                    for (Object x : parameters) {
                        Integer parseInt = Integer.parseInt((String) x);
                        list.add(parseInt);
                    }
                    return number_of_odd(list);
                }
                catch (NumberFormatException | ClassCastException ex) {
                    return "Bad call";
                }
            case "collatz_sequence":
                try {
                    return collatz_sequence(Integer.parseInt((String) parameters.get(0)));
                }
                catch (NumberFormatException | ClassCastException ex) {
                    return "Bad call";
                }
            default:
                return -1;
        }
    }

    /**
     * <p>Executes a server that does mathematical operations on demand</p>
     * @param args arguments passed to main program (not used)
     */
    public static void main(String[] args) {
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

            // Waiting 60 seconds and updating our offer of services
            sleep(60000);
            broker.register_service(obj.getName(), "collatz_sequence", Collections.singletonList("int"), "List<Integer>");
            broker.delete_service(obj.getName(), "fibonacci");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}