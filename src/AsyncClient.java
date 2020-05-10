import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import static java.lang.Thread.sleep;

/**
 * AsyncClient is the class that implements the functionality to execute requests asynchronously
 * of an indirect stateful broker.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class AsyncClient {

    private Broker broker;

    /*
     * Constructor Class
     */
    public AsyncClient(String brokerIPPort, String brokerName) {
        // Searching the broker
        try {
            broker = (Broker) Naming.lookup("//" + brokerIPPort + "/" + brokerName);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return List of services offered by broker
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    public String getListOfServices() throws RemoteException {
        return broker.getListOfServices();
    }

    /**
     * Execute a service offered by the brokerage asynchronously
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @param parameters methods parameters
     */
    private void executeAsyncService(final String serverName, final String serviceName,
                                     final List<Object> parameters) throws RemoteException {
        broker.executeAsyncService(serverName, serviceName, parameters);
    }

    /**
     * @param parameters methods parameters
     * @return methods parameters in list
     */
    private List<Object> parseParameters(String parameters) {
        return Arrays   .asList(Arrays.stream(parameters
                        .replaceAll(" ", "")
                        .split(","))
                        .filter(item -> !item.equals(""))
                        .toArray());
    }

    /**
     *  <p>Get the server, method and parameters that the user want to run</p>
     * @return the response of the requested method to execute
     */
    public List<Object> entryServiceInput() {
        String serverName, serviceName, parameters;
        List<Object> response = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server name: ");
        serverName = scanner.nextLine();
        if(serverName.equals("")){
            return null;
        }
        response.add(serverName);
        System.out.print("Enter the service name: ");
        serviceName = scanner.nextLine();
        response.add(serviceName);
        System.out.print("Enter the parameters (separated by commas): ");
        parameters = scanner.nextLine();
        List<Object> parametersList = parseParameters(parameters);
        response.add(parametersList);
        return response;
    }

    /**
     * <p>Returns the response of the method requested by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @return response of the method executed
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    private Object obtainAsyncResponse(final String serverName, final String serviceName) throws RemoteException {
        return broker.obtainAsyncResponse(serverName, serviceName);
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

        // Creating the client
        AsyncClient asyncClient = new AsyncClient("127.0.0.1:5000", "Broker_R_E");

        try {
            List<Object> list;
            System.out.println(asyncClient.getListOfServices());
            while((list = asyncClient.entryServiceInput()) != null) {
                if(list.get(0).equals("Broker") && list.get(1).equals("getListOfServices")) {
                    System.out.println(asyncClient.getListOfServices());
                    continue;
                }
                System.out.println("Executing " + list.get(0) + "." + list.get(1) + " and waiting 5 seconds...");
                asyncClient.executeAsyncService((String) list.get(0), (String) list.get(1), (List<Object>) list.get(2));
                sleep(5000);
                System.out.println("Response: " + asyncClient.obtainAsyncResponse((String) list.get(0), (String) list.get(1)));
            }
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
