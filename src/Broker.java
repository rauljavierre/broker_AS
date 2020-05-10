import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Broker is the interface we'll be using to declare the API
 * of an indirect stateful broker.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public interface Broker extends Remote {

    /**
     * <p>Register a server in the broker</p>
     * @param serverName name of the server being registered
     * @param IPPort IP port of the server being registered
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    void registerServer(final String serverName, final String IPPort) throws RemoteException;

    /**
     * <p>register a service to be executed to clients by the server</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to register
     * @param parameters methods parameters
     * @param returnType data type returned by the method
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    void registerService(final String serverName, final String serviceName,
                         final List<String> parameters, final String returnType) throws RemoteException;

    /**
     * <p>Delete a service to be executed to clients by the server</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to delete
     */
    void deleteService(final String serverName, final String serviceName) throws RemoteException;

    /**
     * <p>Execute the service request by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @param parameters methods parameters
     * @return response to execute a service requested by the client
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    Object executeSyncService(final String serverName, final String serviceName,
                              final List<Object> parameters) throws RemoteException;

    /**
     * <p>Execute the service request by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @param parameters methods parameters
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    void executeAsyncService(final String serverName, final String serviceName,
                             final List<Object> parameters) throws RemoteException;

    /**
     * <p>Return the response of the method requested by the client</p>
     * @param serverName Name of the server that performs the service
     * @param serviceName Name of the service to run
     * @return response to execute a service requested by the client
     */
    Object obtainAsyncResponse(final String serverName, final String serviceName) throws RemoteException;

    /**
     *
     * @return list of the services offered to execute by the servers.
     * @throws RemoteException fails if connection to rmi doesn't work
     */
    String getListOfServices() throws RemoteException;
}