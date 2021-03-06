/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.edu.um.mateo.inscripciones.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import mx.edu.um.mateo.general.model.Organizacion;

/**
 *
 * @author zorch
 */
@Entity
@Table(name="cat_descuentos")
public class Descuento implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Integer version;   
    @Column(nullable=false, length=2)
    private String status; 
    @Column(nullable=false, length=1)
    private String contabiliza; 
    @Column(nullable=false, length=120)
    private String descripcion; 
    @ManyToOne
    private Organizacion organizacion;
    
     public Descuento(){
    }
     public Descuento(String descripcion, String contabiliza, String status, Organizacion organizacion){
         this.descripcion=descripcion;
        this.contabiliza=contabiliza;
        this.status=status;
        this.organizacion=organizacion;
     }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getContabiliza() {
        return contabiliza;
    }

    public void setContabiliza(String contabiliza) {
        this.contabiliza = contabiliza;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }

    @Override
    public String toString() {
        return "Descuento{" + "id=" + id + ", version=" + version + ", status=" + status + ", contabiliza=" + contabiliza + ", descripcion=" + descripcion + ", organizacion=" + organizacion + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.id);
        hash = 67 * hash + Objects.hashCode(this.version);
        hash = 67 * hash + Objects.hashCode(this.descripcion);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Descuento other = (Descuento) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        if (!Objects.equals(this.descripcion, other.descripcion)) {
            return false;
        }
        return true;
    }
     
    
    
}
