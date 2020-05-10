import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
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

    private static final long serialVersionUID = 4L;            //Default serial version uid

    Map<String, ServerImpl> servers;
    Map<String, String> pendingServices;   // <clientIP, serverName + serviceName>
    Map<String, Object> pendingResults;    // <clientIP, result>

    /**
     * Class constructor.
     */
    public BrokerImpl() throws RemoteException {
        super(); // Calls UnicastRemoteObject constructor
        this.servers = new HashMap<>();
        this.pendingServices = new HashMap<>();
        this.pendingResults = new HashMap<>();
    }

    /**
     * <p>Register a server in the broker</p>
     * @param serverName name of the server being registered
     * @param IPPort IP port of the server being registered
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    public void registerServer(final String serverName, final String IPPort) throws RemoteException {
        servers.put(serverName, new ServerImpl(serverName, IPPort));
        System.out.println("Server " + serverName + " has been registered");
    }


    /**
     * <p>register a service to be executed to clients by the server</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to register
     * @param parameters methods parameters
     * @param returnType data type returned by the method
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    public void registerService(final String serverName, final String serviceName,
                                final List<String> parameters, final String returnType){

        ServerImpl serverImpl = servers.getOrDefault(serverName, null);
        if (serverImpl == null) {
            System.out.println("Server " + serverName + " not registered");
        }
        else {
            serverImpl.addService(new Service(serviceName, parameters, returnType));
            System.out.println("Server " + serverName + " has registered " + serviceName);
        }
    }

    /**
     * <p>Delete a service to be executed to clients by the server</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to delete
     */
    public void deleteService(final String serverName, final String serviceName) {
        ServerImpl serverImpl = servers.getOrDefault(serverName, null);
        if (serverImpl == null) {
            System.out.println("Server " + serverName + " not registered");
        }
        else {
            serverImpl.deleteService(serviceName);
            System.out.println("Server " + serverName + " has deleted " + serviceName);
        }
    }

    /**
     * <p>Execute the service request by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @param parameters methods parameters
     * @return response to execute a service requested by the client
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    public Object executeSyncService(final String serverName, final String serviceName,
                                     final List<Object> parameters) throws RemoteException {
        ServerImpl serverImpl = servers.getOrDefault(serverName, null);
        Object response = 0;
        if (serverImpl == null) {
            response = "Server " + serverName + " not registered";
        }
        else {
            try {
                Server server = (Server) Naming.lookup("//" + serverImpl.getIPPort() + "/" + serverImpl.getName());
                if(serverImpl.hasThisService(serviceName)){
                    response = server.executeService(serviceName, parameters);
                }
                else {
                    response = "The server " + serverName + " hasn't the service " + serviceName;
                }
            }
            catch (NotBoundException | MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * <p>Execute the service request by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @param parameters methods parameters
     */
    public void executeAsyncService(final String serverName, final String serviceName,
                                    final List<Object> parameters)  {
        try {
            final String IPClient = getClientHost();

            Thread thread = new Thread() {
                public void run(){
                    ServerImpl serverImpl = servers.getOrDefault(serverName, null);
                    if (serverImpl == null) {
                        pendingResults.put(IPClient, "Server " + serverName + " not registered");
                    } else {
                        try {
                            Server server = (Server) Naming.lookup("//" + serverImpl.getIPPort() + "/" + serverImpl.getName());
                            if(serverImpl.hasThisService(serviceName)){
                                if(pendingServices.getOrDefault(IPClient, null) == null) {
                                    pendingResults.put(IPClient, server.executeService(serviceName, parameters));
                                    pendingServices.put(IPClient, serverName + serviceName);
                                }
                            }
                            else {
                                pendingResults.put(IPClient,"The server " + serverName + " hasn't the service " + serviceName);
                            }
                        }
                        catch (NotBoundException | MalformedURLException | RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };

            thread.start();
        } catch (ServerNotActiveException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Return the response of the method requested by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @return response to execute a service requested by the client
     */
    public Object obtainAsyncResponse(final String serverName, final String serviceName) {
        Object response = 0;
        try {
            if(pendingServices.getOrDefault(getClientHost(), null).equals(serverName + serviceName)) {
                response = pendingResults.get(getClientHost());
                pendingServices.remove(getClientHost());
                pendingResults.remove(getClientHost());
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
     *
     * @return list of the services offered to execute by the servers.
     */
    public String getListOfServices(){
        StringBuilder response = new StringBuilder("\n\nLIST OF SERVICES SUPPORTED BY THE OBJECT BROKER:");
        response.append("\n################################################\n\n");
        response.append("Server Name: Broker\nService Name: getListOfServices\n\n");
        for (Map.Entry<String, ServerImpl> thisEntry : servers.entrySet()) {
            ServerImpl serverImpl = thisEntry.getValue();
            response.append(serverImpl.getListOfServices());
        }
        return String.valueOf(response);
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
        String hostName = "155.210.154.193:32001";

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