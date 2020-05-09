import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class SyncClient {

    private String name;
    private String IP_port;
    private Broker broker;

    public SyncClient(String name, String IP_port, String broker_IP_port, String broker_name) {
        this.name = name;
        this.IP_port = IP_port;

        // Searching the broker
        try {
            broker = (Broker) Naming.lookup("//" + broker_IP_port + "/" + broker_name);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getListOfServices() throws RemoteException {
        return broker.getListOfServices();
    }

    private Object execute_sync_service(final String server_name, final String service_name, final List<Object> parameters) throws RemoteException {
        return broker.execute_sync_service(server_name, service_name, parameters);
    }

    private List<Object> parseParameters(String parameters) {
        return Arrays   .asList(Arrays.asList(parameters
                        .replaceAll(" ", "")
                        .split(","))
                        .stream()
                        .filter(item -> !item.equals(""))
                        .toArray());
    }

    public boolean entryServiceInput() throws RemoteException, InterruptedException {
        String serverName, serviceName, parameters;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server name: ");
        serverName = scanner.nextLine();
        if(serverName.equals("")){
            return false;
        }
        System.out.print("Enter the service name: ");
        serviceName = scanner.nextLine();
        System.out.print("Enter the parameters (separated by commas): ");
        parameters = scanner.nextLine();
        List<Object> parametersList = parseParameters(parameters);
        System.out.println("Response: " + execute_sync_service(serverName, serviceName, parametersList));
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
        SyncClient syncClient = new SyncClient("SyncClient", "127.0.0.1:5003", "127.0.0.1:5000", "Broker_R_E");

        try {
            do {
                System.out.println(syncClient.getListOfServices());
            } while(syncClient.entryServiceInput());
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
