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
     *
     * @param serverName
     * @param IPPort
     * @throws RemoteException
     */
    void registerServer(final String serverName, final String IPPort) throws RemoteException;

    /**
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @param returnType
     * @throws RemoteException
     */
    void registerService(final String serverName, final String serviceName,
                         final List<String> parameters, final String returnType) throws RemoteException;

    /**
     *
     * @param serverName
     * @param serviceName
     * @throws RemoteException
     */
    void deleteService(final String serverName, final String serviceName) throws RemoteException;

    /**
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @return
     * @throws RemoteException
     */
    Object executeSyncService(final String serverName, final String serviceName,
                              final List<Object> parameters) throws RemoteException;

    /**
     *
     * @param serverName
     * @param serviceName
     * @param parameters
     * @throws RemoteException
     */
    void executeAsyncService(final String serverName, final String serviceName,
                             final List<Object> parameters) throws RemoteException;

    /**
     *
     * @param serverName
     * @param serviceName
     * @return
     * @throws RemoteException
     */
    Object obtainAsyncResponse(final String serverName, final String serviceName) throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    String getListOfServices() throws RemoteException;
}