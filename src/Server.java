import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {

    void add_service(Service service) throws RemoteException;

    void delete_service(String service) throws RemoteException;

    Object execute_sync_service(String service_name, List<Object> parameters) throws RemoteException;

    Object execute_async_service(String service_name, List<Object> parameters) throws RemoteException;

    String getListOfServices() throws RemoteException;

    boolean hasThisService(String name_of_service) throws RemoteException;
}
