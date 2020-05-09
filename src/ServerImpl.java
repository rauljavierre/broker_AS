import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServerImpl extends UnicastRemoteObject implements Server {

    protected String name;
    protected String IP_port;
    protected Map<String, Service> services;

    public ServerImpl(String name, String IP_port) throws RemoteException {
        super();
        this.name = name;
        this.IP_port = IP_port;
        services = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIP_port() {
        return IP_port;
    }

    public void setIP_port(String IP_port) {
        this.IP_port = IP_port;
    }

    public void add_service(Service service) throws RemoteException {
        services.put(service.getName(), service);
    }

    public void delete_service(String service) throws RemoteException {
        services.remove(service);
    }

    public Object execute_service(String service_name, List<Object> parameters) throws RemoteException {
        return null;
    }

    public String getListOfServices() throws RemoteException {
        StringBuilder toPrint = new StringBuilder();
        Iterator entries = services.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry thisEntry = (Map.Entry) entries.next();
            Service service = (Service) thisEntry.getValue();
            toPrint.append("\n\nServer Name:\t").append(name).append("\n");
            toPrint.append("Service Name:\t").append(service.getName()).append("\n");
            toPrint.append("Parameters:\t").append(printAllParameters(service.getParameters()));
            toPrint.append("Returning type:\t").append(service.getReturn_type()).append("\n\n");
        }
        return toPrint.toString();
    }

    private String printAllParameters(List<String> parameters) {
        String result = "";
        for(String parameter : parameters) {
            result += parameter + "\n";
        }
        return result;
    }

    public boolean hasThisService(String name_of_service) throws RemoteException {
        return services.getOrDefault(name_of_service, null) != null;
    }
}
