package PSP.SQLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Clase para establecer una conexión MySQL utilizando datos de configuración almacenados en un archivo XML
public class ConnectionMySQL {
    private String file = "conexion.xml"; // Nombre del archivo XML que contiene los datos de conexión
    private static ConnectionMySQL _newInstance; // Instancia única de la clase
    private static Connection con; // Objeto Connection para la conexión a la base de datos

    // Constructor que carga los datos de conexión desde el archivo XML y establece la conexión MySQL
    public ConnectionMySQL() {
        // Cargar datos de conexión desde el archivo XML
        ConnectionData dc = loadXML();

        try {
            // Establecer conexión MySQL utilizando los datos cargados
            con = DriverManager.getConnection(dc.getServer() + "/" + dc.getDatabase(), dc.getUsername(), dc.getPassword());
        } catch (SQLException e) {
            // En caso de error, establecer la conexión como nula y mostrar el error
            con = null;
            e.printStackTrace();
        }
    }

    // Método estático para obtener la conexión establecida
    public static Connection getConnect() {
        if (_newInstance == null) {
            _newInstance = new ConnectionMySQL();
        }
        return con;
    }

    // Método para cargar datos de conexión desde un archivo XML utilizando JAXB
    public ConnectionData loadXML() {
        ConnectionData con = new ConnectionData(); // Crear un objeto ConnectionData para almacenar los datos cargados
        JAXBContext jaxbContext;
        try {
            // Crear un contexto JAXB para la clase ConnectionData
            jaxbContext = JAXBContext.newInstance(ConnectionData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            // Deserializar el archivo XML y obtener un objeto ConnectionData
            ConnectionData newR = (ConnectionData) jaxbUnmarshaller.unmarshal(new File(file));
            con = newR; // Asignar los datos cargados al objeto ConnectionData creado
        } catch (JAXBException e) {
            // En caso de error en la deserialización, mostrar el error
            e.printStackTrace();
        }
        return con; // Devolver el objeto ConnectionData con los datos cargados
    }
}
