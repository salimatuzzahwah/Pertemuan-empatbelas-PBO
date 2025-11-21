/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pertemuan14;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Salimatuz Zahwah
 */
@Entity
@Table(name = "kendaraan")
@NamedQueries({
    @NamedQuery(name = "Kendaraan.findAll", query = "SELECT k FROM Kendaraan k"),
    @NamedQuery(name = "Kendaraan.findByPlatNomor", query = "SELECT k FROM Kendaraan k WHERE k.platNomor = :platNomor"),
    @NamedQuery(name = "Kendaraan.findByMerk", query = "SELECT k FROM Kendaraan k WHERE k.merk = :merk"),
    @NamedQuery(name = "Kendaraan.findByTahun", query = "SELECT k FROM Kendaraan k WHERE k.tahun = :tahun")})
public class Kendaraan implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "plat_nomor")
    private String platNomor;
    @Column(name = "merk")
    private String merk;
    @Column(name = "tahun")
    private Integer tahun;
    @JoinColumn(name = "id_pemilik", referencedColumnName = "id_pemilik")
    @ManyToOne
    private Pemilik idPemilik;

    public Kendaraan() {
    }

    public Kendaraan(String platNomor) {
        this.platNomor = platNomor;
    }

    public String getPlatNomor() {
        return platNomor;
    }

    public void setPlatNomor(String platNomor) {
        this.platNomor = platNomor;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public Integer getTahun() {
        return tahun;
    }

    public void setTahun(Integer tahun) {
        this.tahun = tahun;
    }

    public Pemilik getIdPemilik() {
        return idPemilik;
    }

    public void setIdPemilik(Pemilik idPemilik) {
        this.idPemilik = idPemilik;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (platNomor != null ? platNomor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Kendaraan)) {
            return false;
        }
        Kendaraan other = (Kendaraan) object;
        if ((this.platNomor == null && other.platNomor != null) || (this.platNomor != null && !this.platNomor.equals(other.platNomor))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pertemuan14.Kendaraan[ platNomor=" + platNomor + " ]";
    }
    
}
