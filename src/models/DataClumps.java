package models;

public class DataClumps {
    private long id;
    private String methods, variables, idCommit, idp, idcl, idm, idv = "";

    public DataClumps(String methods, String variables, String idCommit, String idp, String idcl, String idm, String idv) {
        this.methods = methods;
        this.variables = variables;
        this.idCommit = idCommit;
        this.idp = idp;
        this.idcl = idcl;
        this.idm = idm;
        this.idv = idv;
    }

    public void setId(long id) { this.id = id; }

    public long getId() {
        return id;
    }

    public String getMethods() { return methods; }

    public String getVariables() { return variables; }

    public String getIdCommit() { return idCommit; }

    public String getIdp() { return idp; }

    public String getIdcl() { return idcl; }

    public String getIdm() { return idm; }

    public String getIdv() { return idv; }

    @Override
    public String toString() { return "DataClumps {" + "id=" + id + ", methods=" + methods + ",variables=" + variables + "}"; };

}
