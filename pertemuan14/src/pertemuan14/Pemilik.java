/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pertemuan14;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author Salimatuz Zahwah
 */
@Entity
@Table(name = "pemilik")
@NamedQueries({
    @NamedQuery(name = "Pemilik.findAll", query = "SELECT p FROM Pemilik p"),
    @NamedQuery(name = "Pemilik.findByIdPemilik", query = "SELECT p FROM Pemilik p WHERE p.idPemilik = :idPemilik"),
    @NamedQuery(name = "Pemilik.findByNamaPemilik", query = "SELECT p FROM Pemilik p WHERE p.namaPemilik = :namaPemilik"),
    @NamedQuery(name = "Pemilik.findByAlamat", query = "SELECT p FROM Pemilik p WHERE p.alamat = :alamat"),
    @NamedQuery(name = "Pemilik.findByNoHp", query = "SELECT p FROM Pemilik p WHERE p.noHp = :noHp")})
public class Pemilik implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id_pemilik")
    private String idPemilik;
    @Basic(optional = false)
    @Column(name = "nama_pemilik")
    private String namaPemilik;
    @Column(name = "alamat")
    private String alamat;
    @Column(name = "no_hp")
    private String noHp;
    @OneToMany(mappedBy = "idPemilik")
    private Collection<Kendaraan> kendaraanCollection;

    public Pemilik() {
    }

    public Pemilik(String idPemilik) {
        this.idPemilik = idPemilik;
    }

    public Pemilik(String idPemilik, String namaPemilik) {
        this.idPemilik = idPemilik;
        this.namaPemilik = namaPemilik;
    }

    public String getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(String idPemilik) {
        this.idPemilik = idPemilik;
    }

    public String getNamaPemilik() {
        return namaPemilik;
    }

    public void setNamaPemilik(String namaPemilik) {
        this.namaPemilik = namaPemilik;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public Collection<Kendaraan> getKendaraanCollection() {
        return kendaraanCollection;
    }

    public void setKendaraanCollection(Collection<Kendaraan> kendaraanCollection) {
        this.kendaraanCollection = kendaraanCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idPemilik != null ? idPemilik.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pemilik)) {
            return false;
        }
        Pemilik other = (Pemilik) object;
        if ((this.idPemilik == null && other.idPemilik != null) || (this.idPemilik != null && !this.idPemilik.equals(other.idPemilik))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pertemuan14.Pemilik[ idPemilik=" + idPemilik + " ]";
    }
    
}
