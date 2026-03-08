/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import Modelos.Formulario;
import Modelos.FormularioLista;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alejandro
 */
public class FormularioDBA {

    private static final String AGREGAR_FORMULARIO_QUERY = "INSERT INTO forms(autor, nombre_archivo, archivo_formulario) "
            + "VALUES (?, ?, ?)";

    private static final String SELECT_ALL
            = "SELECT id_formulario, autor, nombre_archivo FROM forms ORDER BY id_formulario DESC";

    private static final String SELECT_BY_ID
            = "SELECT autor, nombre_archivo, archivo_formulario FROM forms WHERE id_formulario = ?";

    public void guardarFormulario(Formulario formulario) {

        try (Connection connection = PoolConexion.getInstance().getConnect(); PreparedStatement insert = connection.prepareStatement(AGREGAR_FORMULARIO_QUERY)) {

            insert.setString(1, formulario.getAutor());
            insert.setString(2, formulario.getNombreArchivo());
            insert.setBytes(3, formulario.getArchivoFormulario());

            insert.executeUpdate();

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FormularioLista> obtenerFormularioListas() {

        List<FormularioLista> lista = new ArrayList<>();

        try (Connection connection = PoolConexion.getInstance().getConnect(); PreparedStatement query = connection.prepareStatement(SELECT_ALL); ResultSet resultSet = query.executeQuery()) {

            while (resultSet.next()) {

                FormularioLista formularioLista = new FormularioLista();
                
                formularioLista.setIdFormulario(resultSet.getInt("id_formulario"));
                formularioLista.setAutor(resultSet.getString("autor"));
                formularioLista.setNombreArchivo(resultSet.getString("nombre_archivo"));
                
                lista.add(formularioLista);
            }
            
            connection.close();
            
            return lista;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
