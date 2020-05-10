import java.util.List;

public class Service {

    private String name;
    private List<String> parameters;
    private String return_type;

    /**
     *
     * @param name
     * @param parameters
     * @param return_type
     */
    public Service(String name, List<String> parameters, String return_type) {
        this.name = name;
        this.parameters = parameters;
        this.return_type = return_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    public String getReturnType() {
        return return_type;
    }

    public void setReturn_type(String return_type) {
        this.return_type = return_type;
    }
}
