import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Server extends Remote {

    void addService(Service service) throws RemoteException;

    void deleteService(String service) throws RemoteException;

    Object executeService(String serviceName, List<Object> parameters) throws RemoteException;

    String getListOfServices() throws RemoteException;

    boolean hasThisService(String nameOfService) throws RemoteException;
}
