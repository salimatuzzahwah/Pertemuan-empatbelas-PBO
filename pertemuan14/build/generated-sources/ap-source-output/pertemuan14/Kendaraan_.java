package pertemuan14;

import javax.annotation.processing.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import pertemuan14.Pemilik;

@Generated(value="org.eclipse.persistence.internal.jpa.modelgen.CanonicalModelProcessor", date="2025-11-21T14:22:51", comments="EclipseLink-2.7.12.v20230209-rNA")
@StaticMetamodel(Kendaraan.class)
public class Kendaraan_ { 

    public static volatile SingularAttribute<Kendaraan, String> merk;
    public static volatile SingularAttribute<Kendaraan, Integer> tahun;
    public static volatile SingularAttribute<Kendaraan, Pemilik> idPemilik;
    public static volatile SingularAttribute<Kendaraan, String> platNomor;

}