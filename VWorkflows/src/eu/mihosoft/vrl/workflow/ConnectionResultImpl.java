/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.workflow;

/**
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */

class ConnectionResultImpl implements ConnectionResult {
    private CompatibilityResult status;
    private Connection connection;

    public ConnectionResultImpl(CompatibilityResult status, Connection connection) {
        this.status = status;
        this.connection = connection;
    }

    /**
     * @return the status
     */
    @Override
    public CompatibilityResult getStatus() {
        return status;
    }

    /**
     * @return the connection
     */
    @Override
    public Connection getConnection() {
        return connection;
    }


}
