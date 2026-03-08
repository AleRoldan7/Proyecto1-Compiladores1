/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Service;

import Conexion.FormularioDBA;
import Excepcion.DatosInvalidos;
import Modelos.Formulario;
import Modelos.FormularioLista;
import Modelos.FormularioUploadDTO;
import java.io.InputStream;
import java.util.List;

/**
 *
 * @author alejandro
 */
public class FormularioService {

    private FormularioDBA formularioDBA = new FormularioDBA();

    public void guardarFormulario(String autor, String nombreArchivo, InputStream archivoStream) throws Exception {

        byte[] archivoBytes = archivoStream.readAllBytes();

        Formulario form = new Formulario();

        form.setAutor(autor);
        form.setNombreArchivo(nombreArchivo);
        form.setArchivoFormulario(archivoBytes);

        formularioDBA.guardarFormulario(form);
    }

    
    public List<FormularioLista> listarFormularios() throws Exception {
        
        return formularioDBA.obtenerFormularioListas();
    }
}
