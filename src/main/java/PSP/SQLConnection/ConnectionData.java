package PSP.SQLConnection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

// Clase para almacenar información de conexión a una base de datos
@XmlRootElement(name = "conexion") // Define el nombre del elemento raíz en XML
@XmlAccessorType(XmlAccessType.FIELD) // Indica que se accederá a los campos directamente para la serialización XML
public class ConnectionData implements Serializable {

    private static final long serialVersionUID = 1L;
    private String server; // Dirección del servidor de la base de datos
    private String database; // Nombre de la base de datos
    private String username; // Nombre de usuario para la conexión
    private String password; // Contraseña para la conexión

    // Constructor sin argumentos
    public ConnectionData() {
        // Inicializa todos los atributos con cadenas vacías por defecto
        this.server = "";
        this.database = "";
        this.username = "";
        this.password = "";
    }

    // Métodos getter y setter para el atributo 'server'
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    // Métodos getter y setter para el atributo 'database'
    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    // Métodos getter y setter para el atributo 'username'
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Métodos getter y setter para el atributo 'password'
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Método toString() para obtener una representación de cadena de la clase
    @Override
    public String toString() {
        return "Conexion [server=" + server + ", database=" + database + ", username=" + username + ", password="
                + password + "]";
    }
}
