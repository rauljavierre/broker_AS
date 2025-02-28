import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * SyncClient is the class that implements the functionality to execute requests synchronously.
 * of an indirect stateful broker.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class SyncClient {

    private Broker broker;

    /**
     *
     * @param brokerIPPort broker ip port
     * @param brokerName broker name
     */
    public SyncClient(String brokerIPPort, String brokerName) {
        // Searching the broker
        try {
            broker = (Broker) Naming.lookup("//" + brokerIPPort + "/" + brokerName);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return list of the services offered to execute by the servers registered in the broker
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    public String getListOfServices() throws RemoteException {
        return broker.getListOfServices();
    }

    /**
     *
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @param parameters methods parameters
     * @return response of executed the service requested
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    private Object executeSyncService(final String serverName, final String serviceName, final List<Object> parameters) throws RemoteException {
        return broker.executeSyncService(serverName, serviceName, parameters);
    }

    /**
     *
     * @param parameters list of parameters to execute in the method in string
     * @return the list of object to excute like parameters in the method
     */
    private List<Object> parseParameters(String parameters) {
        return Arrays   .asList(Arrays.stream(parameters
                        .replaceAll(" ", "")
                        .split(","))
                        .filter(item -> !item.equals(""))
                        .toArray());
    }

    /**
     *
     * @return if the servicie is different of white return true else return false
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    public boolean entryServiceInput() throws RemoteException {
        String serverName, serviceName, parameters;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server name: ");
        serverName = scanner.nextLine();
        if(serverName.equals("")){
            return false;
        }
        System.out.print("Enter the service name: ");
        serviceName = scanner.nextLine();
        if(serverName.equals("Broker") && serviceName.equals("getListOfServices")) {
            System.out.println(getListOfServices());
            return true;
        }
        System.out.print("Enter the parameters (separated by commas): ");
        parameters = scanner.nextLine();
        List<Object> parametersList = parseParameters(parameters);
        System.out.println("Response: " + executeSyncService(serverName, serviceName, parametersList) + "\n");
        return true;
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
        SyncClient syncClient = new SyncClient("155.210.154.193:32001", "Broker_R_E");

        try {
            System.out.println(syncClient.getListOfServices());
            while(syncClient.entryServiceInput());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
