import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class SyncClient {

    private Broker broker;

    /**
     *
     * @param brokerIPPort
     * @param brokerName
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
     * @return
     * @throws RemoteException
     */
    public String getListOfServices() throws RemoteException {
        return broker.getListOfServices();
    }

    /**
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @return
     * @throws RemoteException
     */
    private Object executeSyncService(final String serverName, final String serviceName, final List<Object> parameters) throws RemoteException {
        return broker.executeSyncService(serverName, serviceName, parameters);
    }

    /**
     *
     * @param parameters
     * @return
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
     * @return
     * @throws RemoteException
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
        System.out.println("Response: " + executeSyncService(serverName, serviceName, parametersList));
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
        SyncClient syncClient = new SyncClient("127.0.0.1:5000", "Broker_R_E");

        try {
            do {
                System.out.println(syncClient.getListOfServices());
            } while(syncClient.entryServiceInput());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
