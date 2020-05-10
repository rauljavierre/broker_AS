import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *
     * @param service
     */
    public void addService(Service service) {
        services.put(service.getName(), service);
    }

    public void deleteService(String service) {
        services.remove(service);
    }

    public Object executeService(String serviceName, List<Object> parameters) {
        return null;
    }

    /**
     *
     * @return
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
     * @param parameters
     * @return
     */
    private String printAllParameters(List<String> parameters) {
        StringBuilder result = new StringBuilder();
        for(String parameter : parameters) {
            result.append(parameter).append("\n");
        }
        return result.toString();
    }

    public boolean hasThisService(String nameOfService) {
        return services.getOrDefault(nameOfService, null) != null;
    }
}
