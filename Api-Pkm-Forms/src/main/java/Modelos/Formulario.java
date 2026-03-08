/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelos;

import java.io.InputStream;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

/**
 *
 * @author alejandro
 */
public class Formulario {

    private Integer idFormulario;
    private String autor;
    private String nombreArchivo;
    private byte [] archivoFormulario;

    public Formulario(Integer idFormulario, String autor, String nombreArchivo, byte[] archivoFormulario) {
        this.idFormulario = idFormulario;
        this.autor = autor;
        this.nombreArchivo = nombreArchivo;
        this.archivoFormulario = archivoFormulario;
    }

    public Formulario() {
    }

    public Integer getIdFormulario() {
        return idFormulario;
    }

    public void setIdFormulario(Integer idFormulario) {
        this.idFormulario = idFormulario;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public byte[] getArchivoFormulario() {
        return archivoFormulario;
    }

    public void setArchivoFormulario(byte[] archivoFormulario) {
        this.archivoFormulario = archivoFormulario;
    }
    
    

}
