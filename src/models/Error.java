/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author mefi
 */
public class Error {
    private String errormsg,exceptionmsg;

    public Error() {
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public void setExceptionmsg(String exceptionmsg) {
        this.exceptionmsg = exceptionmsg;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public String getExceptionmsg() {
        return exceptionmsg;
    }

    @Override
    public String toString() {
        return "Error{" + "errormsg=" + errormsg + ", exceptionmsg=" + exceptionmsg + '}';
    }
    
}
