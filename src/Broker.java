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
     * <p>Counts the odd numbers of the array passed</p>
     * @return the number of odd numbers of the array passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    void register_server(final String server_name, final String IP_port) throws RemoteException;

    /**
     * <p>Calculates the fibonacci number of the integer passed</p>
     * @return fibonacci(number)
     * @throws RemoteException may occur during the execution of a remote method call
     */
    void register_service(final String server_name, final String service_name,
                          final List<String> parameters, final String return_type) throws RemoteException;

    /**
     * <p>Calculates the fibonacci number of the integer passed</p>
     * @return fibonacci(number)
     * @throws RemoteException may occur during the execution of a remote method call
     */
    void delete_service(final String server_name, final String service_name) throws RemoteException;

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @return the collatz sequence of the integer passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    Object execute_sync_service(final String server_name, final String service_name,
                                final List<Object> parameters) throws RemoteException;

    /**
     * <p>Calculates the collatz sequence of the integer passed</p>
     * @return the collatz sequence of the integer passed
     * @throws RemoteException may occur during the execution of a remote method call
     */
    Object execute_async_service(final String server_name, final String service_name,
                                 final List<Object> parameters) throws RemoteException;

    String getListOfServices() throws RemoteException;
}