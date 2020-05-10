import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {
    /**
     * <p>Add a service to delete the broker</p>
     * @param service Service to delete
     */
    void addService(Service service) throws RemoteException;

    /**
     * <p>Add a service to delete the broker</p>
     * @param service Service to delete
     */
    void deleteService(String service) throws RemoteException;

    /**
     * <p>Execute a method offer request by the client </p>
     * @param serviceName name of the method to execute
     * @param parameters list of parameters to execute the method
     * @return null
     */
    Object executeService(String serviceName, List<Object> parameters) throws RemoteException;

    /**
     * @return list of services that the client can ask to execute in String type
     */
    String getListOfServices() throws RemoteException;

    /**
     * <p>Verify that the server has the service</p>
     * @param nameOfService name of the method to check if it is from the server
     * @return true if the method is from the server or false if the method isn't from the server.
     */
    boolean hasThisService(String nameOfService) throws RemoteException;
}
