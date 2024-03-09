package PSP.SQLConnection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para establecer una conexión MySQL utilizando datos de configuración almacenados en un archivo XML.
 */
public class ConnectionMySQL {

    /**
     * Nombre del archivo XML que contiene los datos de conexión.
     */
    private String file = "conexion.xml";

    /**
     * Instancia única de la clase.
     */
    private static ConnectionMySQL _newInstance;

    /**
     * Objeto Connection para la conexión a la base de datos.
     */
    private static Connection con;

    /**
     * Constructor que carga los datos de conexión desde el archivo XML y establece la conexión MySQL.
     */
    public ConnectionMySQL() {
        ConnectionData dc = loadXML();

        try {
            con = DriverManager.getConnection(dc.getServer() + "/" + dc.getDatabase(), dc.getUsername(), dc.getPassword());
        } catch (SQLException e) {
            con = null;
            e.printStackTrace();
        }
    }

    /**
     * Método estático para obtener la conexión establecida.
     */
    public static Connection getConnect() {
        if (_newInstance == null) {
            _newInstance = new ConnectionMySQL();
        }
        return con;
    }

    /**
     * Método para cargar datos de conexión desde un archivo XML utilizando JAXB.
     */
    public ConnectionData loadXML() {
        ConnectionData con = new ConnectionData();
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(ConnectionData.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ConnectionData newR = (ConnectionData) jaxbUnmarshaller.unmarshal(new File(file));
            con = newR;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return con;
    }
}
