import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * BrokerImpl is the class that implements the API
 * of an indirect stateful broker.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class BrokerImpl extends UnicastRemoteObject implements Broker {

    Map<String, ServerImpl> servers;
    Map<String, String> pending_services;   // <client_IP, server_name + service_name>
    Map<String, Object> pending_results;    // <client_IP, result>

    /**
     * Class constructor.
     */
    public BrokerImpl() throws RemoteException {
        super(); // Calls UnicastRemoteObject constructor
        this.servers = new HashMap<>();
        this.pending_services = new HashMap<>();
        this.pending_results = new HashMap<>();
    }

    /**
     * <p>Counts the odd numbers of the array passed</p>
     * @return the number of odd numbers of the array passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public void register_server(final String server_name, final String IP_port) throws RemoteException {
        servers.put(server_name, new ServerImpl(server_name, IP_port));
        System.out.println("Server " + server_name + " has been registered");
    }

    /**
     * <p>Calculates the fibonacci number of the integer passed</p>
     * @return fibonacci(number)
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public void register_service(final String server_name, final String service_name,
                                 final List<String> parameters, final String return_type) throws RemoteException {

        ServerImpl serverImpl = servers.getOrDefault(server_name, null);
        if (serverImpl == null) {
            System.out.println("Server " + server_name + " not registered");
        }
        else {
            serverImpl.add_service(new Service(service_name, parameters, return_type));
            System.out.println("Server " + server_name + " has registered " + service_name);
        }
    }

    /**
     * <p>Calculates the fibonacci number of the integer passed</p>
     * @return fibonacci(number)
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public void delete_service(final String server_name, final String service_name) {
        ServerImpl serverImpl = servers.getOrDefault(server_name, null);
        if (serverImpl == null) {
            System.out.println("Server " + server_name + " not registered");
        }
        else {
            try {
                serverImpl.delete_service(service_name);
                System.out.println("Server " + server_name + " has deleted " + service_name);
            }
            catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @return the collatz sequence of the integer passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public Object execute_sync_service(final String server_name, final String service_name,
                                       final List<Object> parameters) throws RemoteException {
        ServerImpl serverImpl = servers.getOrDefault(server_name, null);
        Object response = 0;
        if (serverImpl == null) {
            System.out.println("Server " + server_name + " not registered");
        }
        else {
            try {
                Server server = (Server) Naming.lookup("//" + serverImpl.getIP_port() + "/" + serverImpl.getName());
                if(serverImpl.hasThisService(service_name)){
                    response = server.execute_service(service_name, parameters);
                }
                else {
                    System.out.println("The server " + server_name + " hasn't the service " + service_name);
                }
            }
            catch (NotBoundException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @return the collatz sequence of the integer passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public void execute_async_service(final String server_name, final String service_name,
                                        final List<Object> parameters) throws RemoteException {

        ServerImpl serverImpl = servers.getOrDefault(server_name, null);
        if (serverImpl == null) {
            System.out.println("Server " + server_name + " not registered");
        }
        else {
            try {
                Server server = (Server) Naming.lookup("//" + serverImpl.getIP_port() + "/" + serverImpl.getName());
                if(serverImpl.hasThisService(service_name)){
                    if(pending_services.getOrDefault(getClientHost(), null) == null) {
                        pending_results.put(getClientHost(), server.execute_service(service_name, parameters));
                        pending_services.put(getClientHost(), server_name + service_name);
                    }
                }
                else {
                    System.out.println("The server " + server_name + " hasn't the service " + service_name);
                }
            }
            catch (NotBoundException e) {
                e.printStackTrace();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @return the collatz sequence of the integer passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public Object obtain_async_response(final String server_name, final String service_name) throws RemoteException {
        Object response = 0;
        try {
            if(pending_services.getOrDefault(getClientHost(), null).equals(server_name + service_name)) {
                response = pending_results.get(getClientHost());
                pending_services.remove(getClientHost());
                pending_results.remove(getClientHost());
            }
            else {
                response = "Error obtaining async response";
            }
        }
        catch(NullPointerException ex) {
            response = "Error obtaining async response";
        }
        catch (ServerNotActiveException ex){
            ex.printStackTrace();
        }
        return response;
    }


    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @return the collatz sequence of the integer passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    public String getListOfServices() throws RemoteException {
        StringBuilder response = new StringBuilder("\n\nLIST OF SERVICES SUPPORTED BY THE OBJECT BROKER:\n################################################");
        Iterator entries = servers.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            ServerImpl serverImpl = (ServerImpl) thisEntry.getValue();
            response.append(serverImpl.getListOfServices());
        }
        return String.valueOf(response);
    }

    /**
     * <p>Executes a server that does mathematical operations on demand</p>
     * @param args arguments passed to main program (not used)
     */
    public static void main(String args[]){
        // Setting the directory of java.policy
        System.setProperty("java.security.policy", "java.policy");

        // Creating the security manager
        System.setSecurityManager(new SecurityManager());

        // Where we are... IP:PORT or NAME (with DNS). RMI uses 1099 by default
        String hostName = "127.0.0.1:5000";

        try {
            // Creating remote object
            Broker obj = new BrokerImpl();
            System.out.println("Broker_R_E created!");

            // Registering remote object
            Naming.rebind("//" + hostName + "/" + "Broker_R_E", obj);
            System.out.println("Broker_R_E registered!");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}