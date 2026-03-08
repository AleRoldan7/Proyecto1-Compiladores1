/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Service.FormularioService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

/**
 *
 * @author alejandro
 */
@Path("/formularios")
public class FormularioController {

    private FormularioService formularioService = new FormularioService();

    @POST
    @Path("/subir")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response subirFormulario(@FormDataParam("autor") String autor,
            @FormDataParam("archivo") InputStream archivoInputStream,
            @FormDataParam("archivo") FormDataContentDisposition detalle) {
        
        System.out.println("Si jalo el enpoitn ");
        try {

            String nombreArchivo = detalle.getFileName();

            formularioService.guardarFormulario(
                    autor,
                    nombreArchivo,
                    archivoInputStream
            );

            return Response.ok(Map.of("mensaje", "Formulario subido")).build();

        } catch (Exception e) {

            e.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                    Map.of("error", "Error al subir el formulario")).build();
        }

    }
    
    @GET
    @Path("/lista-formularios")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listarFormularios() {
        
        try {
            
            return Response.ok(formularioService.listarFormularios()).build();
            
        } catch (Exception e) {
            
            e.printStackTrace();
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error en la lista").build();
        }
    }
}
