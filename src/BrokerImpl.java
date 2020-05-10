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
     *
     * @param serverName
     * @param IPPort
     * @throws RemoteException
     */
    public void registerServer(final String serverName, final String IPPort) throws RemoteException {
        servers.put(serverName, new ServerImpl(serverName, IPPort));
        System.out.println("Server " + serverName + " has been registered");
    }


    /**
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @param returnType
     * @throws RemoteException
     */
    public void registerService(final String serverName, final String serviceName,
                                final List<String> parameters, final String returnType) throws RemoteException {

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
     *
     * @param serverName
     * @param serviceName
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
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @return
     * @throws RemoteException
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
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @throws RemoteException
     */
    public void executeAsyncService(final String serverName, final String serviceName,
                                    final List<Object> parameters) throws RemoteException {

        ServerImpl serverImpl = servers.getOrDefault(serverName, null);
        if (serverImpl == null) {
            try {
                pendingResults.put(getClientHost(), "Server " + serverName + " not registered");
            } catch (ServerNotActiveException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                Server server = (Server) Naming.lookup("//" + serverImpl.getIPPort() + "/" + serverImpl.getName());
                if(serverImpl.hasThisService(serviceName)){
                    if(pendingServices.getOrDefault(getClientHost(), null) == null) {
                        pendingResults.put(getClientHost(), server.executeService(serviceName, parameters));
                        pendingServices.put(getClientHost(), serverName + serviceName);
                    }
                }
                else {
                    pendingResults.put(getClientHost(),"The server " + serverName + " hasn't the service " + serviceName);
                }
            }
            catch (NotBoundException | MalformedURLException | ServerNotActiveException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param serverName
     * @param serviceName
     * @return
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
     * @return
     * @throws RemoteException
     */
    public String getListOfServices() throws RemoteException {
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