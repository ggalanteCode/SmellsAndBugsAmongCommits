package models;

public class MultiCodeSmell {
    private long id;
    private String methods, variables, idCommit = "";
    private int idp, idcl, idm, idv;

    public MultiCodeSmell(String methods, String variables, String idCommit, int idp, int idcl, int idm, int idv) {
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

    public int getIdp() { return idp; }

    public int getIdcl() { return idcl; }

    public int getIdm() { return idm; }

    public int getIdv() { return idv; }

    @Override
    public String toString() { return "DataClumps {" + "id=" + id + ", methods=" + methods + ",variables=" + variables + "}"; };

}
