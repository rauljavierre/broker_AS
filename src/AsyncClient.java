import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class AsyncClient {

    private String name;
    private String IP_port;
    private Broker broker;

    public AsyncClient(String name, String IP_port, String broker_IP_port, String broker_name) {
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

    private void execute_async_service(final String server_name, final String service_name, final List<Object> parameters) throws RemoteException {
        broker.execute_async_service(server_name, service_name, parameters);
    }

    private List<Object> parseParameters(String parameters) {
        return Arrays   .asList(Arrays.asList(parameters
                .replaceAll(" ", "")
                .split(","))
                .stream()
                .filter(item -> !item.equals(""))
                .toArray());
    }

    public List<Object> entryServiceInput() throws RemoteException, InterruptedException {
        String serverName, serviceName, parameters;
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the server name: ");
        serverName = scanner.nextLine();
        if(serverName.equals("")){
            return null;
        }
        System.out.print("Enter the service name: ");
        serviceName = scanner.nextLine();
        System.out.print("Enter the parameters (separated by commas): ");
        parameters = scanner.nextLine();
        List<Object> parametersList = parseParameters(parameters);
        List<Object> response = new ArrayList<>();
        response.add(serverName);
        response.add(serviceName);
        response.add(parametersList);
        return response;
    }

    private Object obtain_async_response(final String server_name, final String service_name) throws RemoteException {
        return broker.obtain_async_response(server_name, service_name);
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
        AsyncClient asyncClient = new AsyncClient("AsyncClient", "127.0.0.1:5004", "127.0.0.1:5000", "Broker_R_E");

        try {
            List<Object> list = new ArrayList<>();
            System.out.println(asyncClient.getListOfServices());
            while((list = asyncClient.entryServiceInput()) != null) {
                // Ejecutar
                asyncClient.execute_async_service((String) list.get(0), (String) list.get(1), (List<Object>) list.get(2));
                // Sleep
                sleep(5000);
                // Obtener respuesta
                System.out.println(asyncClient.obtain_async_response((String) list.get(0), (String) list.get(1)));
            }
        } catch (RemoteException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
