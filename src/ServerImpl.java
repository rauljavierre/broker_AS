import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * ServerImpl is the class that implements the API
 * of a server. Stateful server.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class ServerImpl extends UnicastRemoteObject implements Server {

    private static final long serialVersionUID = 4L;            //Default serial version uid

    protected String name;
    protected String IPPort;
    protected Map<String, Service> services;

    public ServerImpl(String name, String IPPort) throws RemoteException {
        super();
        this.name = name;
        this.IPPort = IPPort;
        services = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIPPort() {
        return IPPort;
    }

    public void setIPPort(String IPPort) {
        this.IPPort = IPPort;
    }

    /**
     * <p>Add a service to offer the broker</p>
     * @param service Service to offer
     */
    public void addService(Service service) {
        services.put(service.getName(), service);
    }

    /**
     * <p>Add a service to delete the broker</p>
     * @param service Service to delete
     */
    public void deleteService(String service) {
        services.remove(service);
    }

    /**
     * <p>Execute a method offer request by the client </p>
     * @param serviceName name of the method to execute
     * @param parameters list of parameters to execute the method
     * @return null
     */
    public Object executeService(String serviceName, List<Object> parameters) {
        return null;
    }

    /**
     * @return list of services that the client can ask to execute in String type
     */
    public String getListOfServices() {
        StringBuilder toPrint = new StringBuilder();
        for (Map.Entry<String, Service> thisEntry : services.entrySet()) {
            Service service = thisEntry.getValue();
            toPrint.append("\nServer Name:\t").append(name).append("\n");
            toPrint.append("Service Name:\t").append(service.getName()).append("\n");
            toPrint.append("Parameters:\t").append(printAllParameters(service.getParameters()));
            toPrint.append("Returning type:\t").append(service.getReturnType()).append("\n\n");
        }
        return toPrint.toString();
    }

    /**
     *
     * @param parameters list of parameters to execute the method
     * @return String of the list of parameters to execute the method
     */
    private String printAllParameters(List<String> parameters) {
        StringBuilder result = new StringBuilder();
        for(String parameter : parameters) {
            result.append(parameter).append("\n");
        }
        return result.toString();
    }

    /**
     *<p>verify that the server has the service</p>
     * @param nameOfService name of the method to check if it is from the server
     * @return true if the method is from the server or false if the method isn't from the server.
     */
    public boolean hasThisService(String nameOfService) {
        return services.getOrDefault(nameOfService, null) != null;
    }
}
