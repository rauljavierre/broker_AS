import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.*;

/**
 * ServerBlaBloImpl is the class that implements the API
 * of a server that holds mathematical operations. Stateless server.
 *
 * @author Raul Javierre, Eduardo Ruiz
 *
 */
public class ServerBlaBloImpl extends ServerImpl implements ServerBlaBlo {

    private static final long serialVersionUID = 4L;            //Default serial version uid


    private long numberOfInvocations;

    /**
     * Class constructor.
     */
    public ServerBlaBloImpl(String name, String IP_port) throws RemoteException {
        super(name, IP_port);
        numberOfInvocations = 0;
    }

    /**
     * <p>Counts the odd numbers of the array passed</p>
     * @return the number of odd numbers of the array passed
     */
    public String doSomething() {
        if (numberOfInvocations % 2 == 0) {
            return "Blo";
        }
        else {
            return "Bla";
        }
    }

    @Override
    public Object execute_service(String service_name, List<Object> parameters) {
        numberOfInvocations++;
        if ("doSomething".equals(service_name)) {
            return doSomething();
        }
        return -1;
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
        String hostName = "127.0.0.1:5002";

        try {
            // Creating remote object
            ServerImpl obj = new ServerBlaBloImpl("ServerBlaBlo", hostName);
            System.out.println(obj.getName() + " created!");

            // Registering remote object
            Naming.rebind("//" + obj.getIP_port() + "/" + obj.getName(), obj);
            System.out.println(obj.getName() + " registered at " + obj.getIP_port() + "!");

            // Searching the broker
            Broker broker = (Broker) Naming.lookup("//" + "127.0.0.1:5000" + "/" + "Broker_R_E");

            // Registering the server in the broker
            broker.register_server(obj.getName(), obj.getIP_port());

            // Registering some services in the broker
            broker.register_service(obj.getName(), "doSomething", Collections.singletonList(""), "long");
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}